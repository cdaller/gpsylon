/***********************************************************************
 * @(#)$RCSfile$   $Revision$$Date$
 *
 * Copyright (c) 2001-2003 Sandra Brueckler, Stefan Feitl
 * Written during an XPG-Project at the IICM of the TU-Graz, Austria
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


package org.dinopolis.gpstool.gpsinput;

//----------------------------------------------------------------------
/**
 * @author Sandra Brueckler, Stefan Feitl
 * @version $Revision$
 */

public class GarminTrackpointD300 implements GarminTrackpoint
{
  public double latitude_;
  public double longitude_;
  public int time_;
  public boolean new_track_;

  protected final static int TRACKPOINT_TYPE = 300;
  
  public GarminTrackpointD300()
  {
  }

  public GarminTrackpointD300(char[] buffer)
  {
    latitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer,2);
    longitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer,6);
    time_ = GarminDataConverter.getGarminInt(buffer,10);
    new_track_ = GarminDataConverter.getGarminBoolean(buffer,14);
  }

//----------------------------------------------------------------------
/**
 * Get the Trackpoint Type
 *
 * @return Trackpoint Type
 * @throws GarminUnsupportedMethodException
 */
  public int getType() throws GarminUnsupportedMethodException
  {
    return(TRACKPOINT_TYPE);
  }

//----------------------------------------------------------------------
/**
 * Get the Latitude (degrees)
 *
 * @return Latitude (degrees)
 * @throws GarminUnsupportedMethodException
 */
  public double getLatitude() throws GarminUnsupportedMethodException
  {
    return(latitude_);
  }

//----------------------------------------------------------------------
/**
 * Get the Longitude (degrees)
 *
 * @return Longitude (degrees)
 * @throws GarminUnsupportedMethodException
 */
  public double getLongitude() throws GarminUnsupportedMethodException
  {
    return(longitude_);
  }

//----------------------------------------------------------------------
/**
 * Get the time when the point was recorded. Is expressed as number of
 * seconds since UTC Dec 31, 1989 - 12:00 AM.
 *
 * @return Time (seconds)
 * @throws GarminUnsupportedMethodException
 */
 public float getTime() throws GarminUnsupportedMethodException
 {
   return(time_);
 }
	
//----------------------------------------------------------------------
/**
 * Get the Altitude (meters).  A value of 1.0e25 means the parameter is
 * unsupported or unknown.
 *
 * @return Altitude (meters)
 * @throws GarminUnsupportedMethodException
 */
  public float getAltitude() throws GarminUnsupportedMethodException
  {
    return(1.0E25F);
  }

//----------------------------------------------------------------------
/**
 * Get the Depth (meters). A value of 1.0e25 means the parameter is
 * unsupported or unknown.
 *
 * @return Depth (meters)
 * @throws GarminUnsupportedMethodException
 */
  public float getDepth() throws GarminUnsupportedMethodException
  {
    return(1.0E25F);
  }
  
//----------------------------------------------------------------------
/**
 * Is this the beginning of a new track segment? If true, this point
 * marks the beginning of a new track segment.
 *
 * @return Beginning of new track segment (boolean)
 * @throws GarminUnsupportedMethodException
 */
  public boolean isNewTrack() throws GarminUnsupportedMethodException
  {
    return(new_track_);
  }

  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("GarminTrackpoint[");
    buffer.append("lat=").append(latitude_).append(", ");
    buffer.append("lon=").append(longitude_).append(", ");
    buffer.append("time=").append(time_).append(", ");
    buffer.append("new_track=").append(new_track_);
    buffer.append("]");
    return(buffer.toString());
  }
}
