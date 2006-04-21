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
import java.util.Map;
import java.util.TreeMap;
import org.dinopolis.gpstool.gpsinput.GPSWaypoint;

//----------------------------------------------------------------------
/**
 * This class represents packets in Garmin data format D108.
 *
 * @author Christof Dallermassl, Stefan Feitl
 * @version $Revision$
 */

public class GarminWaypointD108 extends GarminWaypointBase
{
  public int class_type_;
  public String class_name_;
  public Color color_;
  public String display_options_;
  public short attributes_;
  public int symbol_type_;
  public byte[] subclass_ = new byte[18];
  public float depth_;
  public float distance_;
  public String state_code_ = "";
  public String country_code_ = "";
  public String facility_ = "";
  public String city_ = "";
  public String address_ = "";
  public String cross_road_ = "";

  public int color_index_;
  public int display_index_;

  protected final static byte WAYPOINT_TYPE = 108;
  
  protected final static String[] DISPLAY_OPTIONS =
    new String[] {"symbol+name","symbol","symbol+comment"};

  protected static Map class_name_map_;

//----------------------------------------------------------------------
/**
 * Static initializer.
 */
  static
  {
    class_name_map_ = new TreeMap();
    class_name_map_.put(new Integer(0x00),"user");
    class_name_map_.put(new Integer(0x40),"aviation_airport");
    class_name_map_.put(new Integer(0x41),"aviation_intersection");
    class_name_map_.put(new Integer(0x42),"aviation_NDB");
    class_name_map_.put(new Integer(0x43),"aviation_VOR");
    class_name_map_.put(new Integer(0x44),"aviation_airport_runway");
    class_name_map_.put(new Integer(0x45),"aviation_airport_intersection");
    class_name_map_.put(new Integer(0x46),"aviation_airport_NDB");
    class_name_map_.put(new Integer(0x80),"map_point");
    class_name_map_.put(new Integer(0x81),"map_area");
    class_name_map_.put(new Integer(0x82),"map_intersection");
    class_name_map_.put(new Integer(0x83),"map_address");
    class_name_map_.put(new Integer(0x84),"map_label");
    class_name_map_.put(new Integer(0x85),"map_line");
  }
  
//----------------------------------------------------------------------
/**
 * Default constructor
 */
  public GarminWaypointD108()
  {
  }

//----------------------------------------------------------------------
/**
 * Constructor using an int array buffer.
 * @param buffer the buffer holding the information
 */
  public GarminWaypointD108(int[] buffer)
  {
    class_type_ = GarminDataConverter.getGarminByte(buffer,2);
    class_name_ = (String)class_name_map_.get(new Integer(class_type_));
    if(class_name_ == null)
      class_name_ = "unknown";
    color_index_ = GarminDataConverter.getGarminByte(buffer,3);
    if(color_index_ == 0xff)
      color_index_ = DEFAULT_COLOR_INDEX;
    color_ = COLORS[color_index_];
    display_index_ = GarminDataConverter.getGarminByte(buffer,4);
    if(display_index_ < DISPLAY_OPTIONS.length)
       display_options_ = DISPLAY_OPTIONS[display_index_];
    else
      display_options_ = "unknown";
    attributes_ = GarminDataConverter.getGarminByte(buffer,5);
    symbol_type_ = GarminDataConverter.getGarminWord(buffer,6);
    symbol_name_ = GarminWaypointSymbols.getSymbolName(symbol_type_);
    subclass_ = GarminDataConverter.getGarminByteArray(buffer,7,18);
    latitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer,26);
    longitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer,30);
    altitude_ = GarminDataConverter.getGarminFloat(buffer,34);
    depth_ = GarminDataConverter.getGarminFloat(buffer,38);
    distance_ = GarminDataConverter.getGarminFloat(buffer,42);
    state_code_ = GarminDataConverter.getGarminString(buffer,46,2).trim();
    country_code_ = GarminDataConverter.getGarminString(buffer,48,2).trim();

    // read strings
    identification_ = GarminDataConverter.getGarminString(buffer,50,51);
    int offset = 50 + identification_.length() + 1;
    comment_ = GarminDataConverter.getGarminString(buffer,offset,51);
    offset = offset + comment_.length() + 1;
    facility_ = GarminDataConverter.getGarminString(buffer,offset,51);
    offset = offset + facility_.length() + 1;
    city_ = GarminDataConverter.getGarminString(buffer,offset,51);
    offset = offset + city_.length() + 1;
    address_ = GarminDataConverter.getGarminString(buffer,offset,51);
    offset = offset + address_.length() + 1;
    cross_road_ = GarminDataConverter.getGarminString(buffer,offset,51);
    offset = offset + cross_road_.length() + 1;
  }

//----------------------------------------------------------------------
/**
 * Constructor using a GarminPacket.
 * @param pack the packet to use the data from.
 */
  public GarminWaypointD108(GarminPacket pack)
  {
      //      System.out.println("Receiving D108: "+pack.getPacketSize());

    class_type_ = pack.getNextAsByte(); // 1 b
    class_name_ = (String)class_name_map_.get(new Integer(class_type_));
    if(class_name_ == null)
      class_name_ = "unknown";
    color_index_ = pack.getNextAsByte(); // 1 b
    if(color_index_ == 0xff)
      color_index_ = DEFAULT_COLOR_INDEX;
    color_ = COLORS[color_index_];
    display_index_ = pack.getNextAsByte(); // 1 b
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

    // read strings
    identification_ = pack.getNextAsString(51);
    comment_ = pack.getNextAsString(51);
    facility_ = pack.getNextAsString(31);
    city_ = pack.getNextAsString(25);
    address_ = pack.getNextAsString(51);
    cross_road_ = pack.getNextAsString(51);
  }

//----------------------------------------------------------------------
/**
 * Constructor using another waypoint.
 * @param waypoint the waypoint to take the information from.
 */
  GarminWaypointD108(GPSWaypoint waypoint)
  {
    class_type_ = 0;
    class_name_ = (String)class_name_map_.get(new Integer(class_type_));
    if(class_name_ == null)
      class_name_ = "unknown";
    color_index_ = 0xff;
    if(color_index_ == 0xff)
      color_index_ = DEFAULT_COLOR_INDEX;
    color_ = COLORS[color_index_];
    display_index_ = 0;
   if(display_index_ < DISPLAY_OPTIONS.length)
      display_options_ = DISPLAY_OPTIONS[display_index_];
    else
      display_options_ = "unknown";
    attributes_ = 0x60; // see garmin specification, page 39
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
 *
 * @param packet_id the id to use in the packet.
 * @return GarminPacket representing content of data type.
 */
  public GarminPacket toGarminPacket(int packet_id)
  {
    int data_length = 48 + Math.min(identification_.length()+1,51)
                      + Math.min(comment_.length()+1,51)
                      + Math.min(facility_.length()+1,31)
                      + Math.min(city_.length()+1,25)
                      + Math.min(address_.length()+1,51)
                      + Math.min(cross_road_.length()+1,51);
    GarminPacket pack = new GarminPacket(packet_id,data_length);

    pack.setNextAsByte(class_type_);
    pack.setNextAsByte(color_index_);
    pack.setNextAsByte(display_index_);
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
    pack.setNextAsString(identification_,51,true);
    pack.setNextAsString(comment_,51,true);
    pack.setNextAsString(facility_,31,true);
    pack.setNextAsString(city_,25,true);
    pack.setNextAsString(address_,51,true);
    pack.setNextAsString(cross_road_,51,true);

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
 * Get the Waypoint Class Name
 *
 * @return Waypoint Class Name
 */
  public String getClassName()
  {
    return(class_name_);
  }
	
//----------------------------------------------------------------------
/**
 * Get the Waypoint Class Type
 *
 * @return Waypoint Class Type
 */
  public int getClassType()
  {
    return(class_type_);
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

//----------------------------------------------------------------------
/**
 * Get the Waypoint Display Options
 *
 * @return Waypoint Display Options
 */
  public String getDisplayOptions()
  {
    return(display_options_);
  }

//----------------------------------------------------------------------
/**
 * Get the Waypoint Attributes
 *
 * @return Waypoint Attributes
 */
  public short getAttributes()
  {
    return(attributes_);
  }
	

//----------------------------------------------------------------------
/**
 * Get the Depth (metres). A value of 1.0e25 means the parameter is
 * unsupported or unknown.
 *
 * @return Depth (metres)
 */
  public float getDepth()
  {
    return(depth_);
  }
	
//----------------------------------------------------------------------
/**
 * Get the Distance (metres). A value of 1.0e25 means the parameter is
 * unsupported or unknown.
 *
 * @return Distance (metres)
 */
  public float getDistance()
  {
    return(distance_);
  }
	
//----------------------------------------------------------------------
/**
 * Get the State Code
 *
 * @return State Code
 */
  public String getStateCode()
  {
    return(state_code_);
  }

//----------------------------------------------------------------------
/**
 * Get the Country Code
 *
 * @return Country Code
 */
  public String getCountryCode()
  {
    return(country_code_);
  }

//----------------------------------------------------------------------
/**
 * Get the Facility String
 *
 * @return Facility String
 */
  public String getFacility()
  {
    return(facility_);
  }

//----------------------------------------------------------------------
/**
 * Get the City String
 *
 * @return City String
 */
  public String getCity()
  {
    return(city_);
  }

//----------------------------------------------------------------------
/**
 * Get the Address String
 *
 * @return Address String
 */
  public String getAddress()
  {
    return(address_);
  }

//----------------------------------------------------------------------
/**
 * Get the Crossroad String
 *
 * @return Crossroad String
 */
  public String getCrossroad()
  {
    return(cross_road_);
  }
}
