package org.dinopolis.gpstool.gpsinput.garmin;

import java.awt.Color;
import org.dinopolis.gpstool.gpsinput.GPSWaypoint;

public class GarminWaypointD109 extends GarminWaypointD108
{
  protected short color_value_;
  protected short dtype_;
  protected long ete_;

  protected final static byte WAYPOINT_TYPE = 109;
  
  protected final static Color[] COLORS =
  new Color[] {Color.black,            // black
               new Color(0x80,0,0),    // dark red
               new Color(0,0x80,0),    // dark green
               new Color(0x80,0x80,0), // dark yellow
               new Color(0,0,0x80),    // dark blue
               new Color(0x80,0x80,0), // dark magenta
               new Color(0,0x80,0x80), // dark cyan
               Color.lightGray,        // light gray
               Color.darkGray,         // dark gray
               Color.red,              // red
               Color.green,            // green
               Color.yellow,           // yellow
               Color.blue,             // blue
               Color.magenta,          // magenta
               Color.cyan,             // cyan
               Color.white};           // white
  protected final static int DEFAULT_COLOR_INDEX = 0;

  protected final static String[] DISPLAY_OPTIONS =
    new String[] {"symbol+name","symbol","symbol+comment"};

  protected final static String[] CLASS_NAMES =
  new String[] {"user","aviation_airport","aviation_intersection", "aviation_NDB",
                "aviation_VOR","aviation_airport_runway","aviation_airport_intersection",
                "aviation_airport_NDB","map_point","map_area","map_intersection","map_address",
                "map_label","map_line"};
  
  public GarminWaypointD109()
  {
  }

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
    ete_ = GarminDataConverter.getGarminLong(buffer,50);

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

  public GarminWaypointD109(GarminPackage pack)
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
    ete_ = pack.getNextAsLong(); // 4b

    // read strings
    identification_ = pack.getNextAsString(51);
    comment_ = pack.getNextAsString(51);
    facility_ = pack.getNextAsString(31);
    city_ = pack.getNextAsString(25);
    address_ = pack.getNextAsString(51);
    cross_road_ = pack.getNextAsString(51);
  }

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
 * Convert data type to {@link GarminPackage}
 * @return GarminPackage representing content of data type.
 */
  public GarminPackage toGarminPackage(int package_id)
  {
    int data_length = 52 + Math.min(identification_.length()+1,51)
                      + Math.min(comment_.length()+1,51)
                      + Math.min(facility_.length()+1,31)
                      + Math.min(city_.length()+1,25)
                      + Math.min(address_.length()+1,51)
                      + Math.min(cross_road_.length()+1,51);
    GarminPackage pack = new GarminPackage(package_id,data_length);

    pack.setNextAsByte(dtype_);
    pack.setNextAsByte(class_type_);
    pack.setNextAsByte(color_value_);
    pack.setNextAsByte(attributes_);
    pack.setNextAsWord(symbol_type_);
    pack.setNextAsByteArray(subclass_);
    pack.setNextAsSemicircleDegrees(latitude_);
    pack.setNextAsSemicircleDegrees(longitude_);
    pack.setNextAsFloat(altitude_);
    pack.setNextAsFloat(depth_);
    pack.setNextAsFloat(distance_);
    pack.setNextAsString(state_code_,2,false);
    pack.setNextAsString(country_code_,2,false);
    pack.setNextAsLong(ete_);
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
 * @throws UnsupportedOperationException
 */
  public byte getType() throws UnsupportedOperationException
  {
    return(WAYPOINT_TYPE);
  }

//----------------------------------------------------------------------
/**
 * Get the Waypoint Class Name
 *
 * @return Waypoint Class Name
 * @throws UnsupportedOperationException
 */
  public String getClassName() throws UnsupportedOperationException
  {
    return(class_name_);
  }
	
//----------------------------------------------------------------------
/**
 * Get the Waypoint Class Type
 *
 * @return Waypoint Class Type
 * @throws UnsupportedOperationException
 */
  public int getClassType() throws UnsupportedOperationException
  {
    return(class_type_);
  }

//----------------------------------------------------------------------
/**
 * Get the Waypoint Color
 *
 * @return Waypoint Color
 * @throws UnsupportedOperationException
 */
  public Color getColor() throws UnsupportedOperationException
  {
    return(color_);
  }

//----------------------------------------------------------------------
/**
 * Get the Waypoint Display Options
 *
 * @return Waypoint Display Options
 * @throws UnsupportedOperationException
 */
  public String getDisplayOptions() throws UnsupportedOperationException
  {
    return(display_options_);
  }

//----------------------------------------------------------------------
/**
 * Get the Waypoint Attributes
 *
 * @return Waypoint Attributes
 * @throws UnsupportedOperationException
 */
  public short getAttributes() throws UnsupportedOperationException
  {
    return(attributes_);
  }

//----------------------------------------------------------------------
/**
 * Get the Waypoint Symbol Type
 *
 * @return Waypoint Symbol Type
 * @throws UnsupportedOperationException
 */
  public String getSymbolName() throws UnsupportedOperationException
  {
    return(symbol_name_);
  }
	
//----------------------------------------------------------------------
/**
 * Get the Waypoint Symbol Type
 *
 * @return Waypoint Symbol Type
 * @throws UnsupportedOperationException
 */
  public int getSymbolType() throws UnsupportedOperationException
  {
    return(symbol_type_);
  }

//----------------------------------------------------------------------
/**
 * Get the Waypoint Subclass
 *
 * @return Waypoint Subclass
 * @throws UnsupportedOperationException
 */
  public byte[] getSubclass() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("Subclass not supported");
  }

//----------------------------------------------------------------------
/**
 * Get the Latitude (degrees)
 *
 * @return Latitude (degrees)
 * @throws UnsupportedOperationException
 */
  public double getLatitude() throws UnsupportedOperationException
  {
    return(latitude_);
  }
	
//----------------------------------------------------------------------
/**
 * Set the Latitude (degrees)
 *
 * @throws UnsupportedOperationException
 */
  public void setLatitude(double latitude) throws UnsupportedOperationException
  {
    latitude_ = latitude;
  }

//----------------------------------------------------------------------
/**
 * Get the Longitude (degrees)
 *
 * @return Longitude (degrees)
 * @throws UnsupportedOperationException
 */
  public double getLongitude() throws UnsupportedOperationException
  {
    return(longitude_);
  }

//----------------------------------------------------------------------
/**
 * Set the Longitude (degrees)
 *
 * @throws UnsupportedOperationException
 */
  public void setLongitude(double longitude) throws UnsupportedOperationException
  {
    longitude_ = longitude;
  }

//----------------------------------------------------------------------
/**
 * Get the Altitude (metres).  A value of 1.0e25 means the parameter is unsupported or unknown.
 *
 * @return Altitude (metres)
 * @throws UnsupportedOperationException
 */
  public float getAltitude() throws UnsupportedOperationException
  {
    return(altitude_);
  }
	
//----------------------------------------------------------------------
/**
 * Set the Altitude (metres).  A value of 1.0e25 means the parameter is unsupported or unknown.
 *
 * @throws UnsupportedOperationException
 */
  public void setAltitude(float altitude) throws UnsupportedOperationException
  {
    altitude_ = altitude;
  }

//----------------------------------------------------------------------
/**
 * Get the Depth (metres). A value of 1.0e25 means the parameter is unsupported or unknown.
 *
 * @return Depth (metres)
 * @throws UnsupportedOperationException
 */
  public float getDepth() throws UnsupportedOperationException
  {
    return(depth_);
  }

//----------------------------------------------------------------------
/**
 * Get the Distance (metres). A value of 1.0e25 means the parameter is
 * unsupported or unknown.
 *
 * @return Distance (metres)
 * @throws UnsupportedOperationException
 */
  public float getDistance() throws UnsupportedOperationException
  {
    return(distance_);
  }

//----------------------------------------------------------------------
/**
 * Get the State Code
 *
 * @return State Code
 * @throws UnsupportedOperationException
 */
  public String getStateCode() throws UnsupportedOperationException
  {
    return(state_code_);
  }

//----------------------------------------------------------------------
/**
 * Get the Country Code
 *
 * @return Country Code
 * @throws UnsupportedOperationException
 */
  public String getCountryCode() throws UnsupportedOperationException
  {
    return(country_code_);
  }

//----------------------------------------------------------------------
/**
 * Get the Estimated Time Enroute in seconds.
 *
 * @return ETE
 * @throws UnsupportedOperationException
 */
  public long getEstimatedTimeEnroute() throws UnsupportedOperationException
  {
    return(ete_);
  }

//----------------------------------------------------------------------
/**
 * Get the Identification String
 *
 * @return Identification String
 * @throws UnsupportedOperationException
 */
  public String getIdentification() throws UnsupportedOperationException
  {
    return(identification_);
  }
	
//----------------------------------------------------------------------
/**
 * Set the Identification String
 *
 * @throws UnsupportedOperationException
 */
  public void setIdentification(String identification) throws UnsupportedOperationException
  {
    identification_ = identification;
  }

//----------------------------------------------------------------------
/**
 * Get the Comment String
 *
 * @return Comment String
 * @throws UnsupportedOperationException
 */
  public String getComment() throws UnsupportedOperationException
  {
    return(comment_);
  }
	
//----------------------------------------------------------------------
/**
 * Set the Comment String
 *
 * @throws UnsupportedOperationException
 */
  public void setComment(String comment) throws UnsupportedOperationException
  {
    comment_ = comment;
  }

//----------------------------------------------------------------------
/**
 * Get the Facility String
 *
 * @return Facility String
 * @throws UnsupportedOperationException
 */
  public String getFacility() throws UnsupportedOperationException
  {
    return(facility_);
  }

//----------------------------------------------------------------------
/**
 * Get the City String
 *
 * @return City String
 * @throws UnsupportedOperationException
 */
  public String getCity() throws UnsupportedOperationException
  {
    return(city_);
  }

//----------------------------------------------------------------------
/**
 * Get the Address String
 *
 * @return Address String
 * @throws UnsupportedOperationException
 */
  public String getAddress() throws UnsupportedOperationException
  {
    return(address_);
  }

//----------------------------------------------------------------------
/**
 * Get the Crossroad String
 *
 * @return Crossroad String
 * @throws UnsupportedOperationException
 */
  public String getCrossroad() throws UnsupportedOperationException
  {
    return(cross_road_);
  }

//----------------------------------------------------------------------
/**
 * Get the Link Identification String
 *
 * @return Link Identification String
 * @throws UnsupportedOperationException
 */
  public String getLinkIdentification() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("Link Identification not supported");
  }
 
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("GarminWaypoint[");
    buffer.append("type=").append(WAYPOINT_TYPE).append(", ");
    buffer.append("class_name=").append(class_name_).append(", ");
    buffer.append("color=").append(color_).append(", ");
    buffer.append("display_options=").append(display_options_).append(", ");
    buffer.append("attributes=").append(attributes_).append(", ");
    buffer.append("symbol_type=").append(symbol_type_).append(", ");
    buffer.append("symbol_name=").append(symbol_name_).append(", ");
    buffer.append("lat=").append(latitude_).append(", ");
    buffer.append("lon=").append(longitude_).append(", ");
    buffer.append("alt=").append(altitude_).append(", ");
    buffer.append("depth=").append(depth_).append(", ");
    buffer.append("distance=").append(distance_).append(", ");
    buffer.append("state_code=").append(state_code_).append(", ");
    buffer.append("country_code=").append(country_code_).append(", ");
    buffer.append("identification=").append(identification_).append(", ");
    buffer.append("facility=").append(facility_).append(", ");
    buffer.append("city=").append(city_).append(", ");
    buffer.append("address=").append(address_).append(", ");
    buffer.append("cross_road=").append(cross_road_);
    buffer.append("]");
    return(buffer.toString());
  }
}
