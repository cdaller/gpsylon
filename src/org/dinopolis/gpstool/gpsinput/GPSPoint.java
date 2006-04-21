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

//----------------------------------------------------------------------
/**
 * Describes a point of a gps device.
 *
 * @author Christof Dallermassl / Marc Rechté
 * @version $Revision$
 */

public class GPSPoint implements Cloneable
{
  protected double latitude_ = 0;
  protected double longitude_ = 0;
  protected double altitude_ = Double.NaN;


//----------------------------------------------------------------------
/**
 * Clone itself.
 * @return the clone.
 */
  protected Object clone() throws CloneNotSupportedException
  {
    Object o = null;
			o = super.clone();
    return(o);
  }



//----------------------------------------------------------------------
/**
 * Get the latitude (in degrees, positive means North).
 * @return the latitude value.
 */
  public double getLatitude()
  {
    return(latitude_);
  }

//----------------------------------------------------------------------
/**
 * Set the latitude.
 * @param latitude The new latitude value.
 */
  public void setLatitude(double latitude)
  {
    if (latitude < -90.0 || latitude > 90.0)
      throw new IllegalArgumentException("out of range latitude");
    latitude_ = latitude;
  }

//----------------------------------------------------------------------
/**
 * Get the longitude (in degrees - positive degrees mean East).
 * @return the longitude value.
 */
  public double getLongitude()
  {
    return(longitude_);
  }

//----------------------------------------------------------------------
/**
 * Set the longitude.
 * @param longitude The new longitude value.
 */
  public void setLongitude(double longitude)
  {
    if (longitude < -180.0 || longitude > 180.0)
      throw new IllegalArgumentException("out of range longitude");
    longitude_ = longitude;
  }

//----------------------------------------------------------------------
/**
 * Get the altitude (in meters above sea level). Returns
 * <code>Double.NaN</code>, if no altitude was set.
 * @return the altitude value.
 */
  public double getAltitude()
  {
    return(altitude_);
  }

//----------------------------------------------------------------------
/**
 * Set the altitude (in meters).
 * @param altitude The new altitude value.
 */
  public void setAltitude(double altitude)
	{
		altitude_ = altitude;
	}

//----------------------------------------------------------------------
/**
 * Returns true if the altitude of this waypoint is valid. This is
 * equal to the expression <code>Double.isNaN(getAltitude())</code>.
 * @return true if waypoint has valid altitude.
 */
  public boolean hasValidAltitude()
	{
		return !Double.isNaN(altitude_);
  }

  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("GPSPoint[");
    buffer.append("lat=").append(latitude_).append(", ");
    buffer.append("lon=").append(longitude_);
    if (hasValidAltitude()) buffer.append(", alt=").append(altitude_);
    buffer.append("]");
    return(buffer.toString());
  }

}
