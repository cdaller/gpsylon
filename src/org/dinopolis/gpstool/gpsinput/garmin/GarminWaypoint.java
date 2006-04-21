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

//----------------------------------------------------------------------
/**
 * Interface representing the basic functions of Garmin waypoints.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public interface GarminWaypoint 
{
//----------------------------------------------------------------------
/**
 * Get the Waypoint Type
 *
 * @return Waypoint Type
 * @throws UnsupportedOperationException
 */
  public byte getType()
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Get the Waypoint Class Type
 *
 * @return Waypoint Class Type
 * @throws UnsupportedOperationException
 */
  public int getClassType()
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Get the Waypoint Class Name
 *
 * @return Waypoint Class Name
 * @throws UnsupportedOperationException
 */
  public String getClassName()
    throws UnsupportedOperationException;
  
//----------------------------------------------------------------------
/**
 * Get the Waypoint Color
 *
 * @return Waypoint Color
 * @throws UnsupportedOperationException
 */
  public Color getColor()
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Get the Waypoint Display Options
 *
 * @return Waypoint Display Options
 * @throws UnsupportedOperationException
 */
  public String getDisplayOptions()
    throws UnsupportedOperationException;
	
//----------------------------------------------------------------------
/**
 * Get the Waypoint Attributes
 *
 * @return Waypoint Attributes
 * @throws UnsupportedOperationException
 */
  public short getAttributes()
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Get the Waypoint Symbol Type
 *
 * @return Waypoint Symbol Type
 * @throws UnsupportedOperationException
 */
//  public int getSymbolType()
//    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Get the Waypoint Symbol Name
 *
 * @return Waypoint Symbol Name or "unknown" if unknown.
 * @throws UnsupportedOperationException
 */
  public String getSymbolName()
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Get the Waypoint Subclass
 *
 * @return Waypoint Subclass
 * @throws UnsupportedOperationException
 */
  public byte[] getSubclass()
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Get the Latitude (degrees)
 *
 * @return Latitude (degrees)
 * @throws UnsupportedOperationException
 */
  public double getLatitude()
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Set the Latitude (degrees)
 *
 * @param latitude the latitude in degrees.
 * @throws UnsupportedOperationException
 */
  public void setLatitude(double latitude)
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Get the Longitude (degrees)
 *
 * @return Longitude (degrees)
 * @throws UnsupportedOperationException
 */
  public double getLongitude()
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Set the Longitude (degrees)
 *
 * @param longitude the longitude in degrees.
 * @throws UnsupportedOperationException
 */
  public void setLongitude(double longitude)
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Get the Altitude (metres).  A value of 1.0e25 means the parameter
 * is unsupported or unknown.
 *
 * @return Altitude (metres)
 * @throws UnsupportedOperationException
 */
  public double getAltitude()
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Set the Altitude (metres).  A value of 1.0e25 means the parameter
 * is unsupported or unknown.
 *
 * @param altitude the altitude in meters. 
 * @throws UnsupportedOperationException
 */
  public void setAltitude(double altitude)
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Get the Depth (metres). A value of 1.0e25 means the parameter is
 * unsupported or unknown.
 *
 * @return Depth (metres)
 * @throws UnsupportedOperationException
 */
  public float getDepth()
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Get the Distance (metres). A value of 1.0e25 means the parameter is
 * unsupported or unknown.
 *
 * @return Distance (metres)
 * @throws UnsupportedOperationException
 */
  public float getDistance()
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Get the State Code
 *
 * @return State Code
 * @throws UnsupportedOperationException
 */
  public String getStateCode()
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Get the Country Code
 *
 * @return Country Code
 * @throws UnsupportedOperationException
 */
  public String getCountryCode()
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Get the Estimated Time Enroute
 *
 * @return ETE
 * @throws UnsupportedOperationException
 */
  public long getEstimatedTimeEnroute()
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Get the Identification String
 *
 * @return Identification String
 * @throws UnsupportedOperationException
 */
  public String getIdentification()
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Set the Identification String
 *
 * @param identification the idenfitication of the waypoint
 * @throws UnsupportedOperationException
 */
  public void setIdentification(String identification)
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Get the Comment String
 *
 * @return Comment String
 * @throws UnsupportedOperationException
 */
  public String getComment()
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Set the Comment String
 *
 * @param comment the comment
 * @throws UnsupportedOperationException
 */
  public void setComment(String comment)
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Get the Facility String
 *
 * @return Facility String
 * @throws UnsupportedOperationException
 */
  public String getFacility()
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Get the City String
 *
 * @return City String
 * @throws UnsupportedOperationException
 */
  public String getCity()
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Get the Address String
 *
 * @return Address String
 * @throws UnsupportedOperationException
 */
  public String getAddress()
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Get the Crossroad String
 *
 * @return Crossroad String
 * @throws UnsupportedOperationException
 */
  public String getCrossroad()
    throws UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Get the Link Identification String
 *
 * @return Link Identification String
 * @throws UnsupportedOperationException
 */
  public String getLinkIdentification()
    throws UnsupportedOperationException;
//----------------------------------------------------------------------
  /**
   * Set the Waypoint Symbol Name
   *
   * @param name Waypoint Symbol Name or "unknown" if unknown.
   * @throws UnsupportedOperationException
   */
    public void setSymbolName(String name)
      throws UnsupportedOperationException;

}


