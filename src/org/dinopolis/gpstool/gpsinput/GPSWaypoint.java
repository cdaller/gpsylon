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

import java.lang.UnsupportedOperationException;

//----------------------------------------------------------------------
/**
 * Describes a waypoint of a gps device.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public interface GPSWaypoint 
{

//----------------------------------------------------------------------
/**
 * Get the identification value.
 * @return the identification value.
 */
  public String getIdentification();

//----------------------------------------------------------------------
/**
 * Set the identification.
 * @param identification The new identification.
 */
  public void setIdentification(String identification);

//----------------------------------------------------------------------
/**
 * Get the comment.
 * @return the comment or an empty string, if no comment was set.
 */
  public String getComment();

//----------------------------------------------------------------------
/**
 * Set the comment.
 * @param comment The comment.
 */
  public void setComment(String comment);

//----------------------------------------------------------------------
/**
 * Get the latitude (in degrees, positive means North).
 * @return the latitude value.
 */
  public double getLatitude();

//----------------------------------------------------------------------
/**
 * Set the latitude.
 * @param latitude The new latitude value.
 */
  public void setLatitude(double latitude);

//----------------------------------------------------------------------
/**
 * Get the longitude (in degrees - positive degrees mean East).
 * @return the longitude value.
 */
  public double getLongitude();

//----------------------------------------------------------------------
/**
 * Set the longitude.
 * @param longitude The new longitude value.
 */
  public void setLongitude(double longitude);

//----------------------------------------------------------------------
/**
 * Get the altitude (in meters above sea level). Returns
 * <code>Double.NaN</code>, if no altitude was set.
 * @return the altitude value.
 */
  public double getAltitude();

//----------------------------------------------------------------------
/**
 * Set the altitude (in meters).
 * @param altitude The new altitude value.
 */
  public void setAltitude(double altitude);


//----------------------------------------------------------------------
/**
 * Returns true if the altitude of this waypoint is valid. This is
 * equal to the expression <code>getAltitude() == Double.NaN</code>.
 * @param true if waypoint has valid altitude.
 */
  public boolean hasValidAltitude();
}


