package org.dinopolis.gpstool.gpsinput.garmin;

import java.awt.Color;
import org.dinopolis.gpstool.gpsinput.GPSWaypoint;

public class GarminWaypointD100 implements GarminWaypoint
{
  protected double latitude_;
  protected double longitude_;
  protected String identification_ = "";
  protected String comment_ = "";

  protected final static byte WAYPOINT_TYPE = 100;

  public GarminWaypointD100()
  {
  }

  public GarminWaypointD100(int[] buffer)
  {
    identification_ = GarminDataConverter.getGarminString(buffer,2,6).trim();
    latitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer,8);
    longitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer,12);
//    unused_ = GarminDataConverter.getGarminLong(buffer,16);
    comment_ = GarminDataConverter.getGarminString(buffer,20,40).trim();
  }

  public GarminWaypointD100(GarminPackage pack)
  {
    identification_ = pack.getNextAsString(6).trim();
    latitude_ = pack.getNextAsSemicircleDegrees();
    longitude_ = pack.getNextAsSemicircleDegrees();
    pack.getNextAsLong();  // unused
    comment_ = pack.getNextAsString(40).trim();
  }

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
 * Convert data type to {@link GarminPackage}
 * @return GarminPackage representing content of data type.
 */
  public GarminPackage toGarminPackage(int package_id)
  {
    int data_length = 6 + 4 + 4 + 4 + 40;
    GarminPackage pack = new GarminPackage(package_id,data_length);

    pack.setNextAsString(identification_,6,false);
    pack.setNextAsSemicircleDegrees(latitude_);
    pack.setNextAsSemicircleDegrees(longitude_);
    pack.setNextAsLong(0);  // unused
    pack.setNextAsString(comment_,40,false);

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
 * Get the Waypoint Display Options
 *
 * @return Waypoint Display Options
 * @throws UnsupportedOperationException
 */
  public String getDisplayOptions() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D100");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D100");
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
 * Get the Waypoint Class Type
 *
 * @return Waypoint Class Type
 * @throws UnsupportedOperationException
 */
  public int getClassType() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("Operation not supported by Waypoint D100");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D100");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D100");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D100");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D100");
  }
	
//----------------------------------------------------------------------
/**
 * Set the Altitude (metres).  A value of 1.0e25 means the parameter is unsupported or unknown.
 *
 * @return Altitude (metres)
 * @throws UnsupportedOperationException
 */
  public void setAltitude(float altitude) throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("Operation not supported by Waypoint D100");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D100");
  }

//----------------------------------------------------------------------
/**
 * Get the Distance (metres). A value of 1.0e25 means the parameter is unsupported or unknown.
 *
 * @return Distance (metres)
 * @throws UnsupportedOperationException
 */
  public float getDistance() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("Operation not supported by Waypoint D100");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D100");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D100");
  }

//----------------------------------------------------------------------
/**
 * Get the Estimated Time Enroute
 *
 * @return ETE
 * @throws UnsupportedOperationException
 */
  public long getEstimatedTimeEnroute() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("Operation not supported by Waypoint D100");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D100");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D100");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D100");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D100");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D100");
  }
 
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("GarminWaypoint[");
    buffer.append("lat=").append(latitude_).append(", ");
    buffer.append("lon=").append(longitude_).append(", ");
    buffer.append("identification=").append(identification_).append(", ");
    buffer.append("comment=").append(comment_);
    buffer.append("]");
    return(buffer.toString());
  }
}
