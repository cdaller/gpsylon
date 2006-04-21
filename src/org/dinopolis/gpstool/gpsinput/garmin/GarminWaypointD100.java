/***********************************************************************
 * @(#)$RCSfile$   $Revision$$Date$
 *
 * Copyright (c) Christof Dallermassl, Austria.
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

import org.dinopolis.gpstool.gpsinput.GPSWaypoint;

//----------------------------------------------------------------------
/**
 * This class represents packets in Garmin data format D100.
 *
 * @author Christof Dallermassl / Marc Rechte
 * @version $Revision$
 */

public class GarminWaypointD100 extends GarminWaypointBase
{
  protected final static byte WAYPOINT_TYPE = 100;

  /**
   * Default Constructor
   */
  public GarminWaypointD100()
  {
  }

  /**
   * Constructor using a garmin packet as int array.
   * 
   * @param buffer the int array holding the information
   */
  public GarminWaypointD100(int[] buffer)
  {
    identification_ = GarminDataConverter.getGarminString(buffer,2,6).trim();
    latitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer,8);
    longitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer,12);
//    unused_ = GarminDataConverter.getGarminLongWord(buffer,16);
    comment_ = GarminDataConverter.getGarminString(buffer,20,40).trim();
  }

  /**
   * Constructor using a garmin packet.
   * @param pack the packet holding the information.
   */
  public GarminWaypointD100(GarminPacket pack)
  {
    identification_ = pack.getNextAsString(6).trim();
    latitude_ = pack.getNextAsSemicircleDegrees();
    longitude_ = pack.getNextAsSemicircleDegrees();
    pack.getNextAsLongWord();  // unused
    comment_ = pack.getNextAsString(40).trim();
  }

  /**
   * Copy Constructor using a waypoint.
   * @param waypoint the waypoint.
   */
  public GarminWaypointD100(GPSWaypoint waypoint)
  {
    String tmp;
    tmp = waypoint.getIdentification();
    identification_ = tmp == null ? "" : tmp;
    latitude_ = waypoint.getLatitude();
    longitude_ = waypoint.getLongitude();
    tmp = waypoint.getComment();
    comment_ = tmp == null ? "" : tmp;
  }

//----------------------------------------------------------------------
/**
 * Convert data type to {@link org.dinopolis.gpstool.gpsinput.garmin.GarminPacket}
 * @return GarminPacket representing content of data type.
 */
  public GarminPacket toGarminPacket(int packet_id)
  {
    int data_length = 6 + 4 + 4 + 4 + 40;
    GarminPacket pack = new GarminPacket(packet_id,data_length);

    pack.setNextAsString(identification_,6,false);
    pack.setNextAsSemicircleDegrees(latitude_);
    pack.setNextAsSemicircleDegrees(longitude_);
    pack.setNextAsLongWord(0);  // unused
    pack.setNextAsString(comment_,40,false);

    return (pack);
  }

//----------------------------------------------------------------------
/**
 * Get the Waypoint Type
 *
 * @return Waypoint Type
 */
  public byte getType()
  {
    return(WAYPOINT_TYPE);
  }
}
