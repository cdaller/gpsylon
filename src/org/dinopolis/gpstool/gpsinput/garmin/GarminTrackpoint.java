/***********************************************************************
 * @(#)$RCSfile$   $Revision$$Date$
 *
 * Copyright (c) 2001-2003 Sandra Brueckler, Stefan Feitl
 * Written during an XPG-Project at the IICM of the TU-Graz, Austria
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

//----------------------------------------------------------------------
/**
 * @author Sandra Brueckler, Stefan Feitl
 * @version $Revision$
 */

public interface GarminTrackpoint
{
//----------------------------------------------------------------------
/**
 * Get the Trackpoint Type
 *
 * @return Trackpoint Type
 * @throws GarminUnsupportedMethodException
 */
 public int getType() throws GarminUnsupportedMethodException;

//----------------------------------------------------------------------
/**
 * Get the Latitude (degrees)
 *
 * @return Latitude (degrees)
 * @throws GarminUnsupportedMethodException
 */
 public double getLatitude() throws GarminUnsupportedMethodException;
	
//----------------------------------------------------------------------
/**
 * Get the Longitude (degrees)
 *
 * @return Longitude (degrees)
 * @throws GarminUnsupportedMethodException
 */
 public double getLongitude() throws GarminUnsupportedMethodException;

//----------------------------------------------------------------------
/**
 * Get the time when the point was recorded. Is expressed as number of
 * seconds since UTC Dec 31, 1989 - 12:00 AM.
 *
 * @return Time (seconds)
 * @throws GarminUnsupportedMethodException
 */
 public long getTime() throws GarminUnsupportedMethodException;
	
//----------------------------------------------------------------------
/**
 * Get the Altitude (metres). A value of 1.0e25 means the parameter
 * is unsupported or unknown.
 *
 * @return Altitude (meters)
 * @throws GarminUnsupportedMethodException
 */
 public float getAltitude() throws GarminUnsupportedMethodException;
	
//----------------------------------------------------------------------
/**
 * Get the Depth (metres). A value of 1.0e25 means the parameter is
 * unsupported or unknown.
 *
 * @return Depth (meters)
 * @throws GarminUnsupportedMethodException
 */
 public float getDepth() throws GarminUnsupportedMethodException;
	
//----------------------------------------------------------------------
/**
 * Is this the beginning of a new track segment? If true, this point
 * marks the beginning of a new track segment.
 *
 * @return Beginning of new track segment (boolean)
 * @throws GarminUnsupportedMethodException
 */
 public boolean isNewTrack() throws GarminUnsupportedMethodException;
}
