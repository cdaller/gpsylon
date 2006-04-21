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

import java.awt.Color;
import org.dinopolis.gpstool.gpsinput.GPSWaypoint;

//----------------------------------------------------------------------
/**
 * This class represents packets in Garmin data format D102.
 *
 * @author Christof Dallermassl / Marc Rechte
 * @version $Revision$
 */

public class GarminWaypointD102 extends GarminWaypointBase
{
  protected long symbol_;
  protected float distance_;

  protected final static byte WAYPOINT_TYPE = 102;

  /**
   * Default Constructor 
   */
  public GarminWaypointD102()
  {
  }

  /**
   * Constructor from a byte buffer
   * @param buffer the buffer
   */
  public GarminWaypointD102(int[] buffer)
  {
    identification_ = GarminDataConverter.getGarminString(buffer,2,6).trim();
    latitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer,8);
    longitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer,12);
//    unused_ = GarminDataConverter.getGarminLong(buffer,16);
    comment_ = GarminDataConverter.getGarminString(buffer,20,40).trim();
    distance_ = GarminDataConverter.getGarminFloat(buffer,60);
    symbol_ = GarminDataConverter.getGarminLongWord(buffer,64);
    symbol_name_ = GarminWaypointSymbols.getSymbolName(symbol_);
  }

  /**
   * Constructor from a garmin packet
   * @param pack the packet
   */
  public GarminWaypointD102(GarminPacket pack)
  {
    identification_ = pack.getNextAsString(6).trim();
    latitude_ = pack.getNextAsSemicircleDegrees();
    longitude_ = pack.getNextAsSemicircleDegrees();
    pack.getNextAsLongWord();  // unused
    comment_ = pack.getNextAsString(40).trim();
    distance_ = pack.getNextAsFloat();
    symbol_ = pack.getNextAsLongWord();
    symbol_name_ = GarminWaypointSymbols.getSymbolName(symbol_);
  }

  /**
   * Copy Constructor
   * @param waypoint the other waypoint
   */
  public GarminWaypointD102(GPSWaypoint waypoint)
  {
    identification_ = waypoint.getIdentification();
    latitude_ = waypoint.getLatitude();
    longitude_ = waypoint.getLongitude();
    comment_ = waypoint.getComment();
    distance_ = 0;
    symbol_ = (short)GarminWaypointSymbols.getSymbolId(waypoint.getSymbolName());
    if(symbol_ < 0)
      symbol_ = 18; // default symbol (wpt_dot)
    symbol_name_ = GarminWaypointSymbols.getSymbolName(symbol_);
  }

//----------------------------------------------------------------------
/**
 * Convert data type to {@link org.dinopolis.gpstool.gpsinput.garmin.GarminPacket}
 * @param packet_id the packet id
 * @return GarminPacket representing content of data type.
 */
  public GarminPacket toGarminPacket(int packet_id)
  {
    int data_length = 6 + 4 + 4 + 4 + 40 + 4 + 4;
    GarminPacket pack = new GarminPacket(packet_id,data_length);
    pack.setNextAsString(identification_,6,false);
    pack.setNextAsSemicircleDegrees(latitude_);
    pack.setNextAsSemicircleDegrees(longitude_);
    pack.setNextAsLongWord(0);  // unused
    pack.setNextAsString(comment_,40,false);
    pack.setNextAsFloat(distance_);
    pack.setNextAsLongWord(symbol_);
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
