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
 * @author Christof Dallermassl, Stefan Feitl
 * @version $Revision$
 */

public class GarminWaypointD103 implements GarminWaypoint
{
  protected int display_;
  protected String display_options_;
  protected int symbol_;
  protected int symbol_type_;
  protected String symbol_name_;
  protected double latitude_;
  protected double longitude_;
  protected String identification_ = "";
  protected String comment_ = "";

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
    identification_ = GarminDataConverter.getGarminString(buffer,2,6).trim();
    latitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer,8);
    longitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer,12);
        // unused = GarminDataConverter.getGarminLong(buffer,16);
    comment_ = GarminDataConverter.getGarminString(buffer,20,40).trim();  
    symbol_ = GarminDataConverter.getGarminByte(buffer,60);
    symbol_type_ = SYMBOL_TYPE[symbol_];
    symbol_name_ = GarminWaypointSymbols.getSymbolName(symbol_type_);
    display_ = GarminDataConverter.getGarminByte(buffer,61);
    display_options_ = DISPLAY_OPTIONS[GarminDataConverter.getGarminByte(buffer,61)];
  }

  public GarminWaypointD103(GarminPackage pack)
  {
    identification_ = pack.getNextAsString(6).trim();
    latitude_ = pack.getNextAsSemicircleDegrees();
    longitude_ = pack.getNextAsSemicircleDegrees();
    long unused = pack.getNextAsLong();
    comment_ = pack.getNextAsString(40).trim();
    symbol_ = pack.getNextAsByte();
    symbol_type_ = SYMBOL_TYPE[symbol_];
    symbol_name_ = GarminWaypointSymbols.getSymbolName(symbol_type_);
    display_ = pack.getNextAsByte();
    display_options_ = DISPLAY_OPTIONS[display_];
  }

  public GarminWaypointD103(GPSWaypoint waypoint)
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
  }

//----------------------------------------------------------------------
/**
 * Convert data type to {@link GarminPackage}
 * @return GarminPackage representing content of data type.
 */
  public GarminPackage toGarminPackage(int package_id)
  {
    int data_length = 6 + 4 + 4 + 4 + 40 + 1 + 1;

    GarminPackage pack = new GarminPackage(package_id,data_length);
    pack.setNextAsString(identification_,6,false);
    pack.setNextAsSemicircleDegrees(latitude_);
    pack.setNextAsSemicircleDegrees(longitude_);
    pack.setNextAsLong(0); // unused
    pack.setNextAsString(comment_,40,false);
    pack.setNextAsByte(symbol_);
    pack.setNextAsByte(display_);

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
 * Get the Waypoint Symbol Type
 *
 * @return Waypoint Symbol Type
 */
  public String getSymbolName()
  {
    return(symbol_name_);
  }
	
//----------------------------------------------------------------------
/**
 * Get the Waypoint Symbol Type
 *
 * @return Waypoint Symbol Type
 */
  public int getSymbolType()
  {
    return(symbol_type_);
  }

//----------------------------------------------------------------------
/**
 * Get the Longitude (degrees)
 *
 * @return Longitude (degrees)
 */
  public double getLongitude()
  {
    return(longitude_);
  }

//----------------------------------------------------------------------
/**
 * Set the Longitude (degrees)
 */
  public void setLongitude(double longitude)
  {
    longitude_ = longitude;
  }

//----------------------------------------------------------------------
/**
 * Get the Latitude (degrees)
 *
 * @return Latitude (degrees)
 */
  public double getLatitude()
  {
    return(latitude_);
  }

//----------------------------------------------------------------------
/**
 * Set the Latitude (degrees)
 */
  public void setLatitude(double latitude)
  {
    latitude_ = latitude;
  }

//----------------------------------------------------------------------
/**
 * Get the Identification String
 *
 * @return Identification String
 */
  public String getIdentification()
  {
    return(identification_);
  }
	
//----------------------------------------------------------------------
/**
 * Set the Identification String
 */
  public void setIdentification(String identification)
  {
    identification_ = identification;
  }

//----------------------------------------------------------------------
/**
 * Get the Comment String
 *
 * @return Comment String
 */
  public String getComment()
  {
    return(comment_);
  }
	
//----------------------------------------------------------------------
/**
 * Set the Comment String
 */
  public void setComment(String comment)
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
  }
	
//----------------------------------------------------------------------
/**
 * Set the Altitude (metres).  A value of 1.0e25 means the parameter is unsupported or unknown.
 *
 * @throws UnsupportedOperationException
 */
  public void setAltitude(float altitude) throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    throw new UnsupportedOperationException("Operation not supported by Waypoint D103");
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
    buffer.append("comment=").append(comment_);
    buffer.append("]");
    return(buffer.toString());
  }
}
