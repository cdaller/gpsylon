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


package org.dinopolis.gpstool.track;

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.proj.Projection;
import java.awt.Color;
import java.awt.Point;
import org.dinopolis.gpstool.gpsinput.GPSWaypoint;
import org.dinopolis.gpstool.util.geoscreen.GeoScreen;



//----------------------------------------------------------------------
/**
 * This interface represents a waypoint that may be projected from
 * geographical coordinates to screen coordinates and vice versa.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class WaypointImpl implements Waypoint
{

  protected Color color_;
  protected String display_options_;
  protected String symbol_name_;
  protected double latitude_;
  protected double longitude_;
//  protected Point screen_point_;
  protected int x_;
  protected int y_;
  protected double altitude_ = Double.NaN;
  protected double depth_ = Double.NaN;
  protected String identification_;
  protected String comment_;

//----------------------------------------------------------------------
/**
 * Empty Constructor
 */
  public WaypointImpl()
  {
  }
  
//----------------------------------------------------------------------
/**
 * Copy Constructor
 *
 * @param waypoint the waypoint to copy
 */
  public WaypointImpl(GPSWaypoint waypoint)
  {
//    color_ = waypoint.getColor();
//    display_options_ = waypoint.getDisplayOptions();
    symbol_name_ = waypoint.getSymbolName();
    latitude_ = waypoint.getLatitude();
    longitude_ = waypoint.getLongitude();
    altitude_ = waypoint.getAltitude();
//    depth_ = waypoint.getDepth();
    identification_ = waypoint.getIdentification();
    comment_ = waypoint.getComment();
  }

//----------------------------------------------------------------------
/**
 * Copy Constructor for Waypoints (copy the screen coordinates as well).
 *
 * @param waypoint the waypoint to copy
 */
  public WaypointImpl(Waypoint waypoint)
  {
    this((GPSWaypoint)waypoint);
    System.out.println("copy constr. waypointimpl");
    x_ = waypoint.getX();
    y_ = waypoint.getY();
  }
  
//----------------------------------------------------------------------
// Implementaion of GPSWaypoint Interace:
//----------------------------------------------------------------------

//----------------------------------------------------------------------
/**
 * Get the identification value.
 * @return the identification value.
 */
  public String getIdentification()
  {
    return(identification_);
  }

//----------------------------------------------------------------------
/**
 * Set the identification.
 * @param identification The new identification.
 */
  public void setIdentification(String identification)
  {
    identification_ = identification;
  }

//----------------------------------------------------------------------
/**
 * Get the comment.
 * @return the comment or an empty string, if no comment was set.
 */
  public String getComment()
  {
    return(comment_);
  }

//----------------------------------------------------------------------
/**
 * Set the comment.
 * @param comment The comment.
 */
  public void setComment(String comment)
  {
    comment_ = comment;
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
 * equal to the expression <code>getAltitude() == Double.NaN</code>.
 * @param true if waypoint has valid altitude.
 */
  public boolean hasValidAltitude()
  {
    return(!Double.isNaN(getAltitude()));
  }

//----------------------------------------------------------------------
/**
 * Returns the name of the symbol of the waypoint or null, if no
 * symbol was set. The names are taken from the garmin specification
 * and are listed in {@link
 * org.dinopolis.gpstool.gpsinput.garmin.GarminWaypointSymbols} at the
 * moment.
 * @return the name of the symbol of the waypoint or null, if no
 * symbol was set or is not supported.
 */
  public String getSymbolName()
  {
    return(symbol_name_);
  }

//----------------------------------------------------------------------
// Implementaion of Waypoint Interace:
//----------------------------------------------------------------------

//----------------------------------------------------------------------
/**
 * Get the x coordinate on screen.
 *
 * @return the x coordinate on screen.
 */
  public int getX()
  {
    return(x_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the x coordinate on screen.
 *
 * @param x the x coordinate on screen.
 */
  public void setX(int x)
  {
    x_ = x;
  }
  
//----------------------------------------------------------------------
/**
 * Get the y coordinate on screen.
 *
 * @return the y coordinate on screen.
 */
  public int getY()
  {
    return(y_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the y coordinate on screen.
 *
 * @param y the y coordinate on screen.
 */
  public void setY(int y)
  {
    y_ = y;
  }

//----------------------------------------------------------------------
// Implementaion of GeoScreen Interace:
//----------------------------------------------------------------------
  
//----------------------------------------------------------------------
/**
 * Use the geographical coordinates (have to be set prior to calling
 * this method) and the given projection to calculate the screen
 * coordinates of this GeoScreenPoint.
 *
 * @param projection the projection to use.
 */
  public void forward(Projection projection)
  {
    Point screen_point = projection.forward((float)getLatitude(),(float)getLongitude());
    x_ = (int)screen_point.getX();
    y_ = (int)screen_point.getY();
  }

//----------------------------------------------------------------------
/**
 * Use the screen coordinates (have to be set prior to calling this
 * method) and the given projection to calculate the geographical
 * coordinates of this GeoScreenPoint.
 *
 * @param projection the projection to use.
 */
  public void inverse(Projection projection)
  {
    LatLonPoint point = projection.inverse(x_,y_);
    latitude_ = point.getLatitude();
    longitude_ = point.getLongitude();
  }
  
}

