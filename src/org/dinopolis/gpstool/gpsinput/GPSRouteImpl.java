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

import java.util.List;
import java.util.Vector;

//----------------------------------------------------------------------
/**
 * An vector based implementation of the {@link GPSRoute}.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GPSRouteImpl implements GPSRoute
{
  Vector route_points_ = new Vector();
  String identification_;
  String comment_;

  public GPSRouteImpl()
  {
  }

//--------------------------------------------------------------------------------
/**
 * Get the identification.
 * @return the identification.
 */
  public String getIdentification()
  {
    return (identification_);
  }

//--------------------------------------------------------------------------------
/**
 * Set the identification.
 * @param identification The new identification.
 */
  public void setIdentification(String identification)
  {
    identification_ = identification;
  }
  
//--------------------------------------------------------------------------------
/**
 * Get the comment.
 * @return the comment or an empty string, if no comment was set.
 */
  public String getComment()
  {
    return(comment_);
  }

//--------------------------------------------------------------------------------
/**
 * Set the comment.
 * @param comment The comment.
 */
  public void setComment(String comment)
  {
    comment_ = comment;
  }

//--------------------------------------------------------------------------------
/**
 * Get the list of waypoints this route is made of.
 * @return the routepoints.
 */
  public List getWaypoints()
  {
    return((List)route_points_.clone());
  }

//--------------------------------------------------------------------------------
/**
 * Set the routepoints.
 * @param routepoints The routepoints.
 */
  public void setWaypoints(List routepoints)
  {
    route_points_ = new Vector(routepoints);
  }

//--------------------------------------------------------------------------------
/**
 * Add a route point at the end of the list.
 * @param routepoint The routepoint to add.
 */
  public void addWaypoint(GPSWaypoint routepoint)
  {
    route_points_.add(routepoint);
  }

//--------------------------------------------------------------------------------
/**
 * Add a route point at the end of the list.
 * @param position the new position of the routepoint at (0 = add as first point). 
 * @param routepoint The routepoint to add.
 */
  public void addWaypoint(int position, GPSWaypoint routepoint)
  {
    route_points_.add(position,routepoint);
  }

//--------------------------------------------------------------------------------
/**
 * Get the routepoint at the given position.
 * @return the routepoint.
 *
 * @throws IndexOutofBoundsException if the index is out of range
 * (index < 0 || index >= size()).
 */
  public GPSWaypoint getWaypoint(int position)
    throws IndexOutOfBoundsException
  {
    return((GPSWaypoint)route_points_.get(position));
  }

//--------------------------------------------------------------------------------
/**
 * Remove the routepoint from the given position.
 *
 * @throws IndexOutofBoundsException if the index is out of range
 * (index < 0 || index >= size()).
 */
  public void removeWaypoint(int position)
    throws IndexOutOfBoundsException
  {
    route_points_.remove(position);
  }

//--------------------------------------------------------------------------------
/**
 * Clears the route (routepoints, identification, comment, etc.).
 */
  public void clear()
  {
    route_points_.clear();
    identification_ = "";
    comment_ = "";
  }


//   public void addRouteLinkData(GarminRouteLinkD210 link_type)
//   {
//     route_points_.add(link_type); // FIXXME: maybe not clever to mix route points and links!
//   }

  public String toString()
  {
    StringBuffer buf = new StringBuffer();
    buf.append("GPSRouteImpl[identification=").append(identification_).append(",");
    buf.append("route points=").append(route_points_.toString()).append("]");
    return(buf.toString());
  }
  
}
