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


package org.dinopolis.gpstool.gpsinput;
import java.awt.Color;

//----------------------------------------------------------------------
/**
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
 * @throws GarminUnsupportedMethodException
 */
	
	public byte getType()
    throws GarminUnsupportedMethodException;
	

//----------------------------------------------------------------------
/**
 * Get the Waypoint Class Type
 *
 * @return Waypoint Class Type
 * @throws GarminUnsupportedMethodException
 */
	
	public int getClassType()
    throws GarminUnsupportedMethodException;

//----------------------------------------------------------------------
/**
 * Get the Waypoint Class Name
 *
 * @return Waypoint Class Name
 * @throws GarminUnsupportedMethodException
 */
	
	public String getClassName()
    throws GarminUnsupportedMethodException;

  
//----------------------------------------------------------------------
/**
 * Get the Waypoint Color
 *
 * @return Waypoint Color
 * @throws GarminUnsupportedMethodException
 */
	
	public Color getColor()
    throws GarminUnsupportedMethodException;
	

//----------------------------------------------------------------------
/**
 * Get the Waypoint Display Options
 *
 * @return		Waypoint Display Options
 * @throws GarminUnsupportedMethodException
 */
	
	public String getDisplayOptions()
    throws GarminUnsupportedMethodException;
	

//----------------------------------------------------------------------
/**
 * Get the Waypoint Attributes
 *
 * @return		Waypoint Attributes
 * @throws GarminUnsupportedMethodException
 */
	
	public short getAttributes()
    throws GarminUnsupportedMethodException;
	

//----------------------------------------------------------------------
/**
 * Get the Waypoint Symbol Type
 *
 * @return		Waypoint Symbol Type
 * @throws GarminUnsupportedMethodException
 */
	
	public int getSymbolType()
    throws GarminUnsupportedMethodException;

//----------------------------------------------------------------------
/**
 * Get the Waypoint Symbol Name
 *
 * @return		Waypoint Symbol Name or "unknown" if unknown.
 * @throws GarminUnsupportedMethodException
 */
	
	public String getSymbolName()
    throws GarminUnsupportedMethodException;
	

//----------------------------------------------------------------------
/**
 * Get the Waypoint Subclass
 *
 * @return		Waypoint Subclass
 * @throws GarminUnsupportedMethodException
 */
	
	public byte[] getSubclass()
    throws GarminUnsupportedMethodException;
	

//----------------------------------------------------------------------
/**
 * Get the Latitude (degrees)
 *
 * @return		Latitude (degrees)
 * @throws GarminUnsupportedMethodException
 */
	
	public double getLatitude()
    throws GarminUnsupportedMethodException;
	

//----------------------------------------------------------------------
/**
 * Get the Longitude (degrees)
 *
 * @return		Longitude (degrees)
 * @throws GarminUnsupportedMethodException
 */
	
	public double getLongitude()
    throws GarminUnsupportedMethodException;

//----------------------------------------------------------------------
/**
 * Get the Altitude (metres).  A value of 1.0e25 means the parameter is unsupported or unknown.
 *
 * @return		Altitude (metres)
 * @throws GarminUnsupportedMethodException
 */
	
	public float getAltitude()
    throws GarminUnsupportedMethodException;
	

//----------------------------------------------------------------------
/**
 * Get the Depth (metres). A value of 1.0e25 means the parameter is unsupported or unknown.
 *
 * @return		Depth (metres)
 * @throws GarminUnsupportedMethodException
 */
	
	public float getDepth()
    throws GarminUnsupportedMethodException;
	

//----------------------------------------------------------------------
/**
 * Get the Distance (metres). A value of 1.0e25 means the parameter is unsupported or unknown.
 *
 * @return		Distance (metres)
 * @throws GarminUnsupportedMethodException
 */
	
	public float getDistance()
    throws GarminUnsupportedMethodException;
	

//----------------------------------------------------------------------
/**
 * Get the State Code
 *
 * @return		State Code
 * @throws GarminUnsupportedMethodException
 */
	
	public String getStateCode()
    throws GarminUnsupportedMethodException;
	

//----------------------------------------------------------------------
/**
 * Get the Country Code
 *
 * @return		Country Code
 * @throws GarminUnsupportedMethodException
 */
	
	public String getCountryCode()
    throws GarminUnsupportedMethodException;

//----------------------------------------------------------------------
/**
 * Get the Estimated Time Enroute
 *
 * @return		ETE
 * @throws GarminUnsupportedMethodException
 */
	
	public int getEstimatedTimeEnroute()
    throws GarminUnsupportedMethodException;

//----------------------------------------------------------------------
/**
 * Get the Identification String
 *
 * @return		Identification String
 * @throws GarminUnsupportedMethodException
 */
	
	public String getIdentification()
    throws GarminUnsupportedMethodException;
	

//----------------------------------------------------------------------
/**
 * Get the Comment String
 *
 * @return		Comment String
 * @throws GarminUnsupportedMethodException
 */
	
	public String getComment()
    throws GarminUnsupportedMethodException;
	

//----------------------------------------------------------------------
/**
 * Get the Facility String
 *
 * @return		Facility String
 * @throws GarminUnsupportedMethodException
 */
	
	public String getFacility()
    throws GarminUnsupportedMethodException;
	

//----------------------------------------------------------------------
/**
 * Get the City String
 *
 * @return		City String
 * @throws GarminUnsupportedMethodException
 */
	
	public String getCity()
    throws GarminUnsupportedMethodException;
	

//----------------------------------------------------------------------
/**
 * Get the Address String
 *
 * @return		Address String
 * @throws GarminUnsupportedMethodException
 */
	
	public String getAddress()
    throws GarminUnsupportedMethodException;
	

//----------------------------------------------------------------------
/**
 * Get the Crossroad String
 *
 * @return		Crossroad String
 * @throws GarminUnsupportedMethodException
 */
	
	public String getCrossroad()
    throws GarminUnsupportedMethodException;

//----------------------------------------------------------------------
/**
 * Get the Link Identification String
 *
 * @return		Link Identification String
 * @throws GarminUnsupportedMethodException
 */
	
	public String getLinkIdentification()
    throws GarminUnsupportedMethodException;

}


