/***********************************************************************
 * @(#)$RCSfile$   $Revision$$Date$
 *
 * Copyright (c) 2003 IICM, Graz University of Technology
 * Inffeldgasse 16c, A-8010 Graz, Austria.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL)
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program; if not, write to the
 * Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 ***********************************************************************/


package org.dinopolis.gpstool.gpsinput.garmin;

import org.dinopolis.gpstool.gpsinput.GPSTrack;

//----------------------------------------------------------------------
/**
 * This class represents packages in Garmin data format D310.
 *
 * @author Christof Dallermassl, Stefan Feitl
 * @version $Revision$
 */

public class GarminTrackD310 extends GarminTrack
{

//----------------------------------------------------------------------
/*
 * Constructor using an int[].
 *
 * @param buffer the buffer to read the data from.
 */
  public GarminTrackD310(int[] buffer)
  {
    setDisplayed(GarminDataConverter.getGarminBoolean(buffer,2));
    int color_index = GarminDataConverter.getGarminByte(buffer,3);
      if(color_index == 0xff)
      color_index = GarminWaypointD108.DEFAULT_COLOR_INDEX;
    setColor(GarminWaypointD108.COLORS[color_index]);
    setIdentification(GarminDataConverter.getGarminString(buffer,4));
  }

//----------------------------------------------------------------------
/*
 * Constructor using an garmin package.
 *
 * @param pack the package to read the data from.
 */
  public GarminTrackD310(GarminPackage pack)
  {
    setDisplayed(pack.getNextAsBoolean());
    int color_index = pack.getNextAsByte();
      if(color_index == 0xff)
      color_index = GarminWaypointD108.DEFAULT_COLOR_INDEX;
    setColor(GarminWaypointD108.COLORS[color_index]);
    setIdentification(pack.getNextAsString(pack.getPackageSize()-2));
  }

//----------------------------------------------------------------------
/*
 * Copy constructor using another track.
 *
 * @param track the track to read the data from.
 */
  public GarminTrackD310(GPSTrack track)
  {
    setDisplayed(track.isDisplayed());
    setColor(track.getColor());
    setIdentification(track.getIdentification());
  }

//----------------------------------------------------------------------
/**
 * Convert data type to {@link org.dinopolis.gpstool.gpsinput.garmin.GarminPackage}.
 *
 * @param package_id the id to put in the garmin package.
 * @return GarminPackage representing content of data type.
 */
  public GarminPackage toGarminPackage(int package_id)
  {
        // display (boolean)
        // color (byte)
        // identification + NULL (max 51 char incl. NULL)
    int data_length = 1 + 1 + Math.min(getIdentification().length()+1,51);
    GarminPackage pack = new GarminPackage(package_id,data_length);

    pack.setNextAsBoolean(isDisplayed());
//    pack.setNextAsByte(getColor());
    boolean color_found = false;
    int color_index = 0;
    while((color_index < GarminWaypointD108.COLORS.length) && (!color_found))
    {
      if(GarminWaypointD108.COLORS[color_index].equals(color_))
        color_found = true;
      else
        color_index++;
    }
    if((color_ == null) || (!color_found))
      pack.setNextAsByte(GarminWaypointD108.DEFAULT_COLOR_INDEX);
    else
      pack.setNextAsByte(color_index);
    pack.setNextAsString(getIdentification(),51,true);
    return (pack);
  }
}
