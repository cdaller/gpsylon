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
 * This class represents packets in Garmin data format D107.
 *
 * @author Christof Dallermassl,  Stefan Feitl
 * @version $Revision$
 */

public class GarminWaypointD107 extends GarminWaypointD103
{
  protected Color color_;
  protected short color_index_;
  protected float distance_;
  
  protected final static byte WAYPOINT_TYPE = 107;
  
  protected final static Color[] COLORS =
  new Color[] {Color.black,            // black
               Color.red,            // red
               Color.green,            // green
               Color.blue};             // blue
               
  protected final static int DEFAULT_COLOR_INDEX = 0;


  public GarminWaypointD107()
  {
  }

  public GarminWaypointD107(int[] buffer)
  {
    identification_ = GarminDataConverter.getGarminString(buffer,2,6).trim();
    latitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer,8);
    longitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer,12);
    // GarminDataConverter.getGarminLongWord(buffer,16);  //unused
    comment_ = GarminDataConverter.getGarminString(buffer,20,40).trim();  
    symbol_ = GarminDataConverter.getGarminByte(buffer,60);
    symbol_type_ = SYMBOL_TYPE[symbol_];
    symbol_name_ = GarminWaypointSymbols.getSymbolName(symbol_type_);
    display_ = GarminDataConverter.getGarminByte(buffer,61);
    display_options_ = DISPLAY_OPTIONS[display_];
    distance_ = GarminDataConverter.getGarminFloat(buffer,62);
    color_index_ = GarminDataConverter.getGarminByte(buffer,63);
    if(color_index_ == 0xff)
      color_index_ = DEFAULT_COLOR_INDEX;
    color_ = COLORS[color_index_];
  }

  public GarminWaypointD107(GarminPacket pack)
  {
    identification_ = pack.getNextAsString(6).trim();
    latitude_ = pack.getNextAsSemicircleDegrees();
    longitude_ = pack.getNextAsSemicircleDegrees();
    pack.getNextAsLongWord(); // unused
    comment_ = pack.getNextAsString(40).trim();  
    symbol_ = pack.getNextAsByte();
    symbol_type_ = SYMBOL_TYPE[symbol_];
    symbol_name_ = GarminWaypointSymbols.getSymbolName(symbol_type_);
    display_ = pack.getNextAsByte();
    display_options_ = DISPLAY_OPTIONS[display_];
    distance_ = pack.getNextAsFloat();
    color_index_ = pack.getNextAsByte();
    if(color_index_ == 0xff)
      color_index_ = DEFAULT_COLOR_INDEX;
    color_ = COLORS[color_index_];
  }

  public GarminWaypointD107(GPSWaypoint waypoint)
  {
    String tmp;
    int val = -1;

    tmp = waypoint.getIdentification();
    identification_ = tmp == null ? "" : tmp;
    latitude_ = waypoint.getLatitude();
    longitude_ = waypoint.getLongitude();
    tmp = waypoint.getComment();
    comment_ = tmp == null ? "" : tmp;
    symbol_type_ = (short)GarminWaypointSymbols.getSymbolId(waypoint.getSymbolName());
    if(symbol_type_ < 0)
      symbol_type_ = 18; // default symbol (wpt_dot)

    // Convert garmin standard symbol types to symbol type used by D103
    for (int i=0;i<SYMBOL_TYPE.length;i++)
      if (SYMBOL_TYPE[i]==symbol_type_)
	val = i;
    if (val == -1)
      val = 0;
    symbol_ = val;

    symbol_name_ = GarminWaypointSymbols.getSymbolName(symbol_type_);
    display_ = 0;
    display_options_ = DISPLAY_OPTIONS[display_];
    distance_ = 0f;
    color_index_ = 0xff;
    if(color_index_ == 0xff)
      color_index_ = DEFAULT_COLOR_INDEX;
    color_ = COLORS[color_index_];
  }

//----------------------------------------------------------------------
/**
 * Convert data type to {@link org.dinopolis.gpstool.gpsinput.garmin.GarminPacket}
 * @return GarminPacket representing content of data type.
 */
  public GarminPacket toGarminPacket(int packet_id)
  {
    int data_length = 6 + 4 + 4 + 4 + 40 + 1 + 1 + 4 + 1;
    GarminPacket pack = new GarminPacket(packet_id,data_length);

    pack.setNextAsString(identification_,6,false);
    pack.setNextAsSemicircleDegrees(latitude_);
    pack.setNextAsSemicircleDegrees(longitude_);
    pack.setNextAsLongWord(0); // unused
    pack.setNextAsString(comment_,40,false);
    pack.setNextAsByte(symbol_);
    pack.setNextAsByte(display_);
    pack.setNextAsFloat(distance_);
    pack.setNextAsByte(color_index_);

    return (pack);
  }
  
//----------------------------------------------------------------------
/**
 * Get the Waypoint Color
 *
 * @return Waypoint Color
 */
  public Color getColor()
  {
    return(color_);
  }
 
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("GarminWaypoint[");
    buffer.append("identification=").append(identification_).append(", ");
    buffer.append("type=").append(WAYPOINT_TYPE).append(", ");
    buffer.append("display_options=").append(display_options_).append(", ");
    buffer.append("symbol_type=").append(symbol_type_).append(", ");
    buffer.append("symbol_name=").append(symbol_name_).append(", ");
    buffer.append("lat=").append(latitude_).append(", ");
    buffer.append("lon=").append(longitude_).append(", ");
    buffer.append("comment=").append(comment_).append(", ");
    buffer.append("color=").append(color_);
    buffer.append("]");
    return(buffer.toString());
  }
}
