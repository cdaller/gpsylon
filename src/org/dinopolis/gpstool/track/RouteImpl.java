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

import com.bbn.openmap.proj.Projection;
import java.util.Iterator;
import org.dinopolis.gpstool.gpsinput.GPSRoute;
import org.dinopolis.gpstool.gpsinput.GPSRouteImpl;
import org.dinopolis.gpstool.gpsinput.GPSWaypoint;
import org.dinopolis.gpstool.util.geoscreen.GeoScreen;

//----------------------------------------------------------------------
/**
 * An vector based implementation of {@link Route}. Only {@link
 * Waypoints} are allowed!
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class RouteImpl extends GPSRouteImpl implements Route
{

  protected int min_x_;
  protected int max_x_;
  protected int min_y_;
  protected int max_y_;
  
//----------------------------------------------------------------------
/**
 * Empty constructor
 */
  public RouteImpl()
  {
    super();
  }

//----------------------------------------------------------------------
/**
 * Copy constructor for {@link org.dinopolis.gpstool.gpsinput.GPSRoute}.
 * Creates for all GPSWaypoint a corresponding Waypoint object.
 *
 * @param route the route from a gps device.
 */
  public RouteImpl(GPSRoute route)
  {
    this();
    setIdentification(route.getIdentification());
    setComment(route.getComment());
    Iterator iterator = route.getWaypoints().iterator();
    GPSWaypoint point;
    while(iterator.hasNext())
    {
      point = (GPSWaypoint)iterator.next();
      addWaypoint(new WaypointImpl(point));
    }
  }

//----------------------------------------------------------------------
/**
 * Return the projected routepoint at the given position. This method
 * returns the same object as the {@link #getWaypoint(int)} method but
 * allows convenient access (without cast to {@link
 * Waypoint}) to the returned waypoint.
 *
 * @return the routepoint.
 *
 * @throws IndexOutofBoundsException if the index is out of range
 * (index < 0 || index >= size()).
 */
  public Waypoint getProjectedWaypoint(int position)
    throws IndexOutOfBoundsException
  {
    return((Waypoint)getWaypoint(position));
  }

//----------------------------------------------------------------------
/**
 * Returns the minimal horizontal screen coordinates for the
 * projection used. This value is only valid after the projection was
 * done.
 *
 * @return minimal x value.
 * @see #forward(Projection)
 */

  public int getMinX()
  {
    if(!minmax_valid_)
      calculateMinMax();
    return(min_x_);
  }

//----------------------------------------------------------------------
/**
 * Returns the maximal horizontal screen coordinates for the
 * projection used. This value is only valid after the projection was
 * done.
 *
 * @return maximal x value.
 * @see #forward(Projection)
 */

  public int getMaxX()
  {
    if(!minmax_valid_)
      calculateMinMax();
    return(max_x_);
  }

//----------------------------------------------------------------------
/**
 * Returns the minimal horizontal screen coordinates for the
 * projection used. This value is only valid after the projection was
 * done.
 *
 * @return minimal y value.
 * @see #forward(Projection)
 */

  public int getMinY()
  {
    if(!minmax_valid_)
      calculateMinMax();
    return(min_y_);
  }

//----------------------------------------------------------------------
/**
 * Returns the mayimal horizontal screen coordinates for the
 * projection used. This value is only valid after the projection was
 * done.
 *
 * @return mayimal y value.
 * @see #forward(Projection)
 */

  public int getMaxY()
  {
    if(!minmax_valid_)
      calculateMinMax();
    return(max_y_);
  }
  
//----------------------------------------------------------------------
// Implementaion of GeoScreen Interface:
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
    Iterator iterator = route_points_.iterator();
    Waypoint point;
    while(iterator.hasNext())
    {
      point = (Waypoint)iterator.next();
      point.forward(projection);
      min_x_ = Math.min(min_x_,point.getX());
      min_y_ = Math.min(min_y_,point.getY());
      max_x_ = Math.max(max_x_,point.getX());
      max_y_ = Math.max(max_y_,point.getY());
    }
    minmax_valid_ = true;
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
    Iterator iterator = route_points_.iterator();
    while(iterator.hasNext())
    {
      ((GeoScreen)iterator.next()).inverse(projection);
    }
  }

//----------------------------------------------------------------------
/**
 * Finds the min/max lat/long/alt/x/y of all registered waypoints. 
 */
  protected void calculateMinMax()
  {
    min_latitude_ = 90.0;
    max_latitude_ = -90.0;
    min_longitude_ = 180.0;
    max_longitude_ = -180.0;
    min_altitude_ = Double.MAX_VALUE;
    max_altitude_ = Double.MIN_VALUE;
    min_x_ = Integer.MAX_VALUE;
    min_y_ = Integer.MAX_VALUE;
    max_x_ = Integer.MIN_VALUE;
    max_y_ = Integer.MIN_VALUE;

    Waypoint waypoint;
    for(int index=0; index < route_points_.size(); index++)
    {
      waypoint = (Waypoint)route_points_.get(index);

      min_latitude_ = Math.min(waypoint.getLatitude(),min_latitude_);
      max_latitude_ = Math.max(waypoint.getLatitude(),max_latitude_);

      min_longitude_ = Math.min(waypoint.getLongitude(),min_longitude_);
      max_longitude_ = Math.max(waypoint.getLongitude(),max_longitude_);

      min_x_ = Math.min(waypoint.getX(),min_x_);
      max_x_ = Math.max(waypoint.getX(),max_x_);

      min_y_ = Math.min(waypoint.getY(),min_y_);
      max_y_ = Math.max(waypoint.getY(),max_y_);

      if(waypoint.getAltitude() != Double.NaN)
      {
        min_altitude_ = Math.min(waypoint.getAltitude(),min_altitude_);
        max_altitude_ = Math.max(waypoint.getAltitude(),max_altitude_);
      }
    }
    minmax_valid_ = true;
  }


  public String toString()
  {
    StringBuffer buf = new StringBuffer();
    buf.append("RouteImpl[identification=").append(identification_).append(",");
    buf.append("route points=").append(route_points_.toString()).append("]");
    return(buf.toString());
  }
}

