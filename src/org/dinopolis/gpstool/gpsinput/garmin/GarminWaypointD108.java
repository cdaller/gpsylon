package org.dinopolis.gpstool.gpsinput.garmin;
import java.awt.Color;

public class GarminWaypointD108 implements GarminWaypoint
{
  public int class_type_;
  public String class_name_;
  public Color color_;
  public String display_options_;
  public short attributes_;
  public int symbol_type_;
  public String symbol_name_;
  public double latitude_;
  public double longitude_;
  public float altitude_;
  public float depth_;
  public float distance_;
  public String state_code_;
  public String country_code_;
  public String identification_;
  public String comment_;
  public String facility_;
  public String city_;
  public String address_;
  public String cross_road_;

  protected final static byte WAYPOINT_TYPE = 108;
  
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
  protected final static int DEFAULT_COLOR_INDEX = 15;

  protected final static String[] DISPLAY_OPTIONS =
    new String[] {"symbol+name","symbol","symbol+comment"};

  protected final static String[] CLASS_NAMES =
  new String[] {"user","aviation_airport","aviation_intersection", "aviation_NDB",
                "aviation_VOR","aviation_airport_runway","aviation_airport_intersection",
                "aviation_airport_NDB","map_point","map_area","map_intersection","map_address",
                "map_label","map_line"};
  
  public GarminWaypointD108()
  {
  }

  public GarminWaypointD108(char[] buffer)
  {
    class_type_ = GarminDataConverter.getGarminByte(buffer,2);
    if(class_type_ < CLASS_NAMES.length)
      class_name_ = CLASS_NAMES[class_type_];
    else
      class_name_ = "unknown";
    short color_index = GarminDataConverter.getGarminByte(buffer,3);
    if(color_index == 0xff)
      color_index = DEFAULT_COLOR_INDEX;
    color_ = COLORS[color_index];
    int display_index = GarminDataConverter.getGarminByte(buffer,4);
    if(display_index < DISPLAY_OPTIONS.length)
       display_options_ = DISPLAY_OPTIONS[display_index];
    else
      display_options_ = "unknown";
    attributes_ = GarminDataConverter.getGarminByte(buffer,5);
    symbol_type_ = GarminDataConverter.getGarminWord(buffer,6);
    symbol_name_ = GarminWaypointSymbols.getSymbolName(symbol_type_);
    latitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer,26);
    longitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer,30);
    altitude_ = GarminDataConverter.getGarminFloat(buffer,34);
    depth_ = GarminDataConverter.getGarminFloat(buffer,38);
    distance_ = GarminDataConverter.getGarminFloat(buffer,42);
    state_code_ = GarminDataConverter.getGarminString(buffer,46,2).trim();
    country_code_ = GarminDataConverter.getGarminString(buffer,48,2).trim();
        // read strings:
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
 * Get the Waypoint Type
 *
 * @return Waypoint Type
 * @throws UnsupportedOperationException
 */
	
	public byte getType()
    throws UnsupportedOperationException
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
	
	public String getClassName()
    throws UnsupportedOperationException
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
	
	public int getClassType()
    throws UnsupportedOperationException
  {
    return(class_type_);
  }
	

//----------------------------------------------------------------------
/**
 * Get the Waypoint Colour
 *
 * @return Waypoint Colour
 * @throws UnsupportedOperationException
 */
	
	public Color getColor()
    throws UnsupportedOperationException
  {
    return(color_);
  }
	

//----------------------------------------------------------------------
/**
 * Get the Waypoint Display Options
 *
 * @return		Waypoint Display Options
 * @throws UnsupportedOperationException
 */
	
	public String getDisplayOptions()
    throws UnsupportedOperationException
  {
    return(display_options_);
  }
	

//----------------------------------------------------------------------
/**
 * Get the Waypoint Attributes
 *
 * @return		Waypoint Attributes
 * @throws UnsupportedOperationException
 */
	
	public short getAttributes()
    throws UnsupportedOperationException
  {
    return(attributes_);
  }
	

//----------------------------------------------------------------------
/**
 * Get the Waypoint Symbol Type
 *
 * @return		Waypoint Symbol Type
 * @throws UnsupportedOperationException
 */
	
	public String getSymbolName()
    throws UnsupportedOperationException
  {
    return(symbol_name_);
  }
	
//----------------------------------------------------------------------
/**
 * Get the Waypoint Symbol Type
 *
 * @return		Waypoint Symbol Type
 * @throws UnsupportedOperationException
 */
	
	public int getSymbolType()
    throws UnsupportedOperationException
  {
    return(symbol_type_);
  }
	

//----------------------------------------------------------------------
/**
 * Get the Waypoint Subclass
 *
 * @return		Waypoint Subclass
 * @throws UnsupportedOperationException
 */
	
	public byte[] getSubclass()
    throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("Subclass not supported");
  }
	

//----------------------------------------------------------------------
/**
 * Get the Latitude (degrees)
 *
 * @return		Latitude (degrees)
 * @throws UnsupportedOperationException
 */
	
	public double getLatitude()
    throws UnsupportedOperationException
  {
    return(latitude_);
  }
	

//----------------------------------------------------------------------
/**
 * Get the Longitude (degrees)
 *
 * @return		Longitude (degrees)
 * @throws UnsupportedOperationException
 */
	
	public double getLongitude()
    throws UnsupportedOperationException
  {
    return(longitude_);
  }

//----------------------------------------------------------------------
/**
 * Get the Altitude (metres).  A value of 1.0e25 means the parameter is unsupported or unknown.
 *
 * @return		Altitude (metres)
 * @throws UnsupportedOperationException
 */
	
	public float getAltitude()
    throws UnsupportedOperationException
  {
    return(altitude_);
  }
	

//----------------------------------------------------------------------
/**
 * Get the Depth (metres). A value of 1.0e25 means the parameter is unsupported or unknown.
 *
 * @return		Depth (metres)
 * @throws UnsupportedOperationException
 */
	
	public float getDepth()
    throws UnsupportedOperationException
  {
    return(depth_);
  }
	

//----------------------------------------------------------------------
/**
 * Get the Distance (metres). A value of 1.0e25 means the parameter is
 * unsupported or unknown.
 *
 * @return		Distance (metres)
 * @throws UnsupportedOperationException
 */
	
	public float getDistance()
    throws UnsupportedOperationException
  {
    return(distance_);
  }
	

//----------------------------------------------------------------------
/**
 * Get the State Code
 *
 * @return		State Code
 * @throws UnsupportedOperationException
 */
	
	public String getStateCode()
    throws UnsupportedOperationException
  {
    return(state_code_);
  }
	

//----------------------------------------------------------------------
/**
 * Get the Country Code
 *
 * @return		Country Code
 * @throws UnsupportedOperationException
 */
	
	public String getCountryCode()
    throws UnsupportedOperationException
  {
    return(country_code_);
  }

//----------------------------------------------------------------------
/**
 * Get the Estimated Time Enroute
 *
 * @return		ETE
 * @throws UnsupportedOperationException
 */
	
	public int getEstimatedTimeEnroute()
    throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("Estimated Time En Route not supported");
  }

//----------------------------------------------------------------------
/**
 * Get the Identification String
 *
 * @return		Identification String
 * @throws UnsupportedOperationException
 */
	
	public String getIdentification()
    throws UnsupportedOperationException
  {
    return(identification_);
  }
	

//----------------------------------------------------------------------
/**
 * Get the Comment String
 *
 * @return		Comment String
 * @throws UnsupportedOperationException
 */
	
	public String getComment()
    throws UnsupportedOperationException
  {
    return(comment_);
  }
	

//----------------------------------------------------------------------
/**
 * Get the Facility String
 *
 * @return		Facility String
 * @throws UnsupportedOperationException
 */
	
	public String getFacility()
    throws UnsupportedOperationException
  {
    return(facility_);
  }
	

//----------------------------------------------------------------------
/**
 * Get the City String
 *
 * @return		City String
 * @throws UnsupportedOperationException
 */
	
	public String getCity()
    throws UnsupportedOperationException
  {
    return(city_);
  }
	

//----------------------------------------------------------------------
/**
 * Get the Address String
 *
 * @return		Address String
 * @throws UnsupportedOperationException
 */
	
	public String getAddress()
    throws UnsupportedOperationException
  {
    return(address_);
  }
	

//----------------------------------------------------------------------
/**
 * Get the Crossroad String
 *
 * @return		Crossroad String
 * @throws UnsupportedOperationException
 */
	
	public String getCrossroad()
    throws UnsupportedOperationException
  {
    return(cross_road_);
  }

//----------------------------------------------------------------------
/**
 * Get the Link Identification String
 *
 * @return		Link Identification String
 * @throws UnsupportedOperationException
 */
	
	public String getLinkIdentification()
    throws UnsupportedOperationException
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
