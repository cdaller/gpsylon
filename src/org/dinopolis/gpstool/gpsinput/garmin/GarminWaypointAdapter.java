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

import org.dinopolis.gpstool.gpsinput.GPSWaypoint;

//----------------------------------------------------------------------
/**
 * Adapter to map the interface of a GPSWaypoint to the interface of
 * the GarminWaypoint (altitude is a float, not a double in the garmin
 * protocol). At the moment, only get methods are supported.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GarminWaypointAdapter implements GPSWaypoint
{

  protected GarminWaypoint waypoint_;
  
  public GarminWaypointAdapter(GarminWaypoint waypoint)
  {
    waypoint_ = waypoint;
  }

//----------------------------------------------------------------------
/**
 * Get the identification value.
 * @return the identification value.
 */
  public String getIdentification()
  {
    return(waypoint_.getIdentification());
  }

//----------------------------------------------------------------------
/**
 * Set the identification.
 * @param identification The new identification.
 */
  public void setIdentification(String identification)
  {
//    waypoint_.setIdentification(identification);
  }

//----------------------------------------------------------------------
/**
 * Get the comment.
 * @return the comment or an empty string, if no comment was set.
 */
  public String getComment()
  {
    return(waypoint_.getComment());
  }

//----------------------------------------------------------------------
/**
 * Set the comment.
 * @param comment The comment.
 */
  public void setComment(String comment)
  {
//    waypoint_.setComment(comment);
  }

//----------------------------------------------------------------------
/**
 * Get the latitude (in degrees, positive means North).
 * @return the latitude value.
 */
  public double getLatitude()
  {
    return(waypoint_.getLatitude());
  }

//----------------------------------------------------------------------
/**
 * Set the latitude.
 * @param latitude The new latitude value.
 */
  public void setLatitude(double latitude)
  {
//    waypoint_.setLatitude(latitude);
  }

//----------------------------------------------------------------------
/**
 * Get the longitude (in degrees - positive degrees mean East).
 * @return the longitude value.
 */
  public double getLongitude()
  {
    return(waypoint_.getLongitude());
  }

//----------------------------------------------------------------------
/**
 * Set the longitude.
 * @param longitude The new longitude value.
 */
  public void setLongitude(double longitude)
  {
//    waypoint_.setLongitude(longitude);
  }

//----------------------------------------------------------------------
/**
 * Get the altitude (in meters above sea level). Returns
 * <code>Double.NaN</code>, if no altitude was set.
 * @return the altitude value.
 */
  public double getAltitude()
  {
    float alt = waypoint_.getAltitude();
    if(Math.abs(alt - 1.0E25) < 0.0001E25)
      return(Double.NaN);
    return((double)alt);
  }

//----------------------------------------------------------------------
/**
 * Set the altitude (in meters).
 * @param altitude The new altitude value.
 */
  public void setAltitude(double altitude)
  {
//    waypoint_.setAltitude((float)altitude);
  }


//----------------------------------------------------------------------
/**
 * Returns true if the altitude of this waypoint is valid. This is
 * equal to the expression <code>!Double.isNaN(getAltitude())</code>.
 * @param true if waypoint has valid altitude.
 */
  public boolean hasValidAltitude()
  {
    return(!Double.isNaN(getAltitude()));
  }
  
}


