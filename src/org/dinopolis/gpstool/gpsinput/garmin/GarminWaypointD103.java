package org.dinopolis.gpstool.gpsinput.garmin;
import java.awt.Color;

public class GarminWaypointD103 implements GarminWaypoint
{
  public String display_options_;
  public int symbol_type_;
  public String symbol_name_;
  public double latitude_;
  public double longitude_;
  public String identification_;
  public String comment_;

  protected final static byte WAYPOINT_TYPE = 103;
  

  protected final static String[] DISPLAY_OPTIONS =
    new String[] {"symbol+name","symbol","symbol+comment"};

//  protected final static String[] SYMBOL_NAMES =
//  new String[] {"wpt_dot","house","gas","car","fish","boat","anchor","wreck","exit",
//                "skull","flag","camp","circle","deer","first_aid","back_track"};

      /** mapping from D103 symbol names to GarminWaypointSymbol constants */
  protected final static int[] SYMBOL_TYPE =
  new int[] {18,10,8220,170,7,150,0,19,177,14,178,151,171,156,8196};
  
  public GarminWaypointD103()
  {
  }

  public GarminWaypointD103(int[] buffer)
  {
    for(int index = 0; index < buffer.length; index++)
    {
      System.out.println(index+":"+buffer[index] + " / " + (char)buffer[index]);
    }
    identification_ = GarminDataConverter.getGarminString(buffer,2,6).trim();
    latitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer,8);
    longitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer,12);
        // unused = GarminDataConverter.getGarminLong(buffer,16);
    comment_ = GarminDataConverter.getGarminString(buffer,20,40).trim();  
    symbol_type_ = SYMBOL_TYPE[GarminDataConverter.getGarminByte(buffer,60)];
    symbol_name_ = GarminWaypointSymbols.getSymbolName(symbol_type_);
    display_options_ = DISPLAY_OPTIONS[GarminDataConverter.getGarminByte(buffer,61)];
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
 * Get the Waypoint Class Type
 *
 * @return Waypoint Class Type
 * @throws UnsupportedOperationException
 */
	
	public int getClassType()
    throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
  }

  
//----------------------------------------------------------------------
/**
 * Get the Waypoint Color
 *
 * @return Waypoint Color
 * @throws UnsupportedOperationException
 */
	
	public Color getColor()
    throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
  }
	

//----------------------------------------------------------------------
/**
 * Get the Distance (metres). A value of 1.0e25 means the parameter is unsupported or unknown.
 *
 * @return		Distance (metres)
 * @throws UnsupportedOperationException
 */
	
	public float getDistance()
    throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
  }
 
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("GarminWaypoint[");
    buffer.append("type=").append(WAYPOINT_TYPE).append(", ");
    buffer.append("display_options=").append(display_options_).append(", ");
    buffer.append("symbol_type=").append(symbol_type_).append(", ");
    buffer.append("symbol_name=").append(symbol_name_).append(", ");
    buffer.append("lat=").append(latitude_).append(", ");
    buffer.append("lon=").append(longitude_).append(", ");
    buffer.append("identification=").append(identification_).append(", ");
    buffer.append("comment=").append(comment_);
    buffer.append("]");
    return(buffer.toString());
  }
}
