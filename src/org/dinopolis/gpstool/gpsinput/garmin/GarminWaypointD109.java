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

import org.dinopolis.gpstool.gpsinput.GPSWaypoint;

//----------------------------------------------------------------------
/**
 * This class represents packets in Garmin data format D109.
 *
 * @author Christof Dallermassl, Stefan Feitl
 * @version $Revision$
 */

public class GarminWaypointD109 extends GarminWaypointD108
{
  protected short color_value_;
  protected short dtype_;
  protected long ete_;

  protected final static byte WAYPOINT_TYPE = 109;
  
  protected final static String[] DISPLAY_OPTIONS =
    new String[] {"symbol+name","symbol","symbol+comment"};

  protected final static String[] CLASS_NAMES =
  new String[] {"user","aviation_airport","aviation_intersection", "aviation_NDB",
                "aviation_VOR","aviation_airport_runway","aviation_airport_intersection",
                "aviation_airport_NDB","map_point","map_area","map_intersection","map_address",
                "map_label","map_line"};
  
  /**
   * Default constructor
   */
  public GarminWaypointD109()
  {
  }

  /**
   * Constructor using an int array buffer.
   * @param buffer the buffer holding the information
   */
  public GarminWaypointD109(int[] buffer)
  {
//     for(int index = 0; index < buffer.length; index++)
//     {
//       System.out.println(index+":"+buffer[index] + " / " + (char)buffer[index]);
//     }
    dtype_ = GarminDataConverter.getGarminByte(buffer,2); // always 0x01
    class_type_ = GarminDataConverter.getGarminByte(buffer,3);
    if(class_type_ < CLASS_NAMES.length)
      class_name_ = CLASS_NAMES[class_type_];
    else
      class_name_ = "unknown";
    color_value_ = GarminDataConverter.getGarminByte(buffer,4);
    color_index_ = color_value_ & 0x1f;
    if(color_index_ == 0x1f)
      color_index_ = DEFAULT_COLOR_INDEX;
    color_ = COLORS[color_index_];
    display_index_ = (color_value_ & 0x70) << 5;
    if(display_index_ < DISPLAY_OPTIONS.length)
       display_options_ = DISPLAY_OPTIONS[display_index_];
    else
      display_options_ = "unknown";

    attributes_ = GarminDataConverter.getGarminByte(buffer,5);
    symbol_type_ = GarminDataConverter.getGarminWord(buffer,6);
    symbol_name_ = GarminWaypointSymbols.getSymbolName(symbol_type_);
    subclass_ = GarminDataConverter.getGarminByteArray(buffer,8,18);
    latitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer,26);
    longitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer,30);
    altitude_ = GarminDataConverter.getGarminFloat(buffer,34);
    depth_ = GarminDataConverter.getGarminFloat(buffer,38);
    distance_ = GarminDataConverter.getGarminFloat(buffer,42);
    state_code_ = GarminDataConverter.getGarminString(buffer,46,2).trim();
    country_code_ = GarminDataConverter.getGarminString(buffer,48,2).trim();
    ete_ = GarminDataConverter.getGarminLongWord(buffer,50);

    // read strings
    identification_ = GarminDataConverter.getGarminString(buffer,54,51).trim();
    int offset = 54 + identification_.length() + 1;
    comment_ = GarminDataConverter.getGarminString(buffer,offset,51).trim();
    offset = offset + comment_.length() + 1;
    facility_ = GarminDataConverter.getGarminString(buffer,offset,31).trim();
    offset = offset + facility_.length() + 1;
    city_ = GarminDataConverter.getGarminString(buffer,offset,25).trim();
    offset = offset + city_.length() + 1;
    address_ = GarminDataConverter.getGarminString(buffer,offset,51).trim();
    offset = offset + address_.length() + 1;
    cross_road_ = GarminDataConverter.getGarminString(buffer,offset,51).trim();
    offset = offset + cross_road_.length() + 1;
  }

  /**
   * Constructor using a GarminPacket.
   * @param pack the packet to use the data from.
   */
  public GarminWaypointD109(GarminPacket pack)
  {
    dtype_ = pack.getNextAsByte(); // 1b
    class_type_ = pack.getNextAsByte(); // 1 b
    if(class_type_ < CLASS_NAMES.length)
      class_name_ = CLASS_NAMES[class_type_];
    else
      class_name_ = "unknown";
    color_value_ = pack.getNextAsByte(); // 1b
    color_index_ = color_value_ & 0x1f;
    if(color_index_ == 0x1f)
      color_index_ = DEFAULT_COLOR_INDEX;
    color_ = COLORS[color_index_];
    display_index_ = (color_value_ & 0x70) << 5;
    if(display_index_ < DISPLAY_OPTIONS.length)
       display_options_ = DISPLAY_OPTIONS[display_index_];
    else
      display_options_ = "unknown";

    attributes_ = pack.getNextAsByte(); // 1b
    symbol_type_ = pack.getNextAsWord(); // 2b
    symbol_name_ = GarminWaypointSymbols.getSymbolName(symbol_type_);
    subclass_ = pack.getNextAsByteArray(18); // 18b
    latitude_ = pack.getNextAsSemicircleDegrees(); // 4b
    longitude_ = pack.getNextAsSemicircleDegrees(); // 4b
    altitude_ = pack.getNextAsFloat(); // 4b
    depth_ = pack.getNextAsFloat(); // 4b
    distance_ = pack.getNextAsFloat(); // 4b
    state_code_ = pack.getNextAsString(2).trim(); // 2b
    country_code_ = pack.getNextAsString(2).trim(); // 2b
    ete_ = pack.getNextAsLongWord(); // 4b

    // read strings
    identification_ = pack.getNextAsString();
    comment_ = pack.getNextAsString();
    facility_ = pack.getNextAsString();
    city_ = pack.getNextAsString();
    address_ = pack.getNextAsString();
    cross_road_ = pack.getNextAsString();
  }

  /**
   * Constructor using another waypoint.
   * @param waypoint the waypoint to take the information from.
   */
  GarminWaypointD109(GPSWaypoint waypoint)
  {
    dtype_ = 0x01;
    class_type_ = 0x00;
    if(class_type_ < CLASS_NAMES.length)
      class_name_ = CLASS_NAMES[class_type_];
    else
      class_name_ = "unknown";
    color_value_ = 0x00;
    color_index_ = color_value_ & 0x1f;
    if(color_index_ == 0x1f)
      color_index_ = DEFAULT_COLOR_INDEX;
    color_ = COLORS[color_index_];
    display_index_ = (color_value_ & 0x70) << 5;
    if(display_index_ < DISPLAY_OPTIONS.length)
       display_options_ = DISPLAY_OPTIONS[display_index_];
    else
      display_options_ = "unknown";

    attributes_ = 0x60;
    symbol_type_ = GarminWaypointSymbols.getSymbolId(waypoint.getSymbolName());
    if(symbol_type_ < 0)
      symbol_type_ = 18; // default symbol (wpt_dot)
    symbol_name_ = GarminWaypointSymbols.getSymbolName(symbol_type_);

    for (int i=0;i<18;i++)
      if (i<6)
        subclass_[i]=(byte)0x00;
      else
        subclass_[i]=(byte)0xff;
    latitude_ = waypoint.getLatitude();
    longitude_ = waypoint.getLongitude();

    if (waypoint.hasValidAltitude())
      altitude_ = (float)waypoint.getAltitude();
    else
      altitude_ = 1.0E25f;

    depth_ = 1.0e25f;
    distance_ = 0;
    state_code_ = "";
    country_code_ = "";
    ete_ = 0;

    String tmp;
    tmp = waypoint.getIdentification();
    identification_ = tmp == null ? "" : tmp;
    tmp = waypoint.getComment();
    comment_ = tmp == null ? "" : tmp;
    facility_ = "";
    city_ = "";
    address_ = "";
    cross_road_ = "";
  }

//----------------------------------------------------------------------
/**
 * Convert data type to {@link org.dinopolis.gpstool.gpsinput.garmin.GarminPacket}
 * @param packet_id the packet id
 *
 * @return GarminPacket representing content of data type.
 */
  public GarminPacket toGarminPacket(int packet_id)
  {
    int data_length = 52 + Math.min(identification_.length()+1,51)
                      + Math.min(comment_.length()+1,51)
                      + Math.min(facility_.length()+1,31)
                      + Math.min(city_.length()+1,25)
                      + Math.min(address_.length()+1,51)
                      + Math.min(cross_road_.length()+1,51);
    GarminPacket pack = new GarminPacket(packet_id,data_length);

    pack.setNextAsByte(dtype_);
    pack.setNextAsByte(class_type_);
    pack.setNextAsByte(color_value_);
    pack.setNextAsByte(attributes_);
    pack.setNextAsWord(symbol_type_);
    pack.setNextAsByteArray(subclass_);
    pack.setNextAsSemicircleDegrees(latitude_);
    pack.setNextAsSemicircleDegrees(longitude_);
    pack.setNextAsFloat((float)altitude_);
    pack.setNextAsFloat(depth_);
    pack.setNextAsFloat(distance_);
    pack.setNextAsString(state_code_,2,false);
    pack.setNextAsString(country_code_,2,false);
    pack.setNextAsLongWord(ete_);
    pack.setNextAsString(identification_);
    pack.setNextAsString(comment_);
    pack.setNextAsString(facility_);
    pack.setNextAsString(city_);
    pack.setNextAsString(address_);
    pack.setNextAsString(cross_road_);

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



//----------------------------------------------------------------------
/**
 * Get the Estimated Time Enroute in seconds.
 *
 * @return ETE
 */
  public long getEstimatedTimeEnroute()
  {
    return(ete_);
  }
}
