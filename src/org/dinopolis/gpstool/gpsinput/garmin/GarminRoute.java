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
import java.util.List;
import java.util.Vector;

//----------------------------------------------------------------------
/**
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GarminRoute  
{
  Vector route_points_ = new Vector();
  String identification_;
  String comment_;

  public GarminRoute()
  {
  }

//----------------------------------------------------------------------
/**
 * Get the identification.
 *
 * @return the identification.
 */
  public String getIdentification() 
  {
    return (identification_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the identification.
 *
 * @param identification the identification.
 */
  protected void setIdentification(String identification) 
  {
    identification_ = identification;
  }

//----------------------------------------------------------------------
/**
 * Get the comment.
 *
 * @return the comment.
 * @throws GarminUnsupportedMethodException
 */
  public String getComment()
    throws GarminUnsupportedMethodException
  {
    if(comment_ == null)
      throw new GarminUnsupportedMethodException("Comment not supported");
    else
      return(comment_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the comment.
 *
 * @param comment the comment.
 */
  protected void setComment(String comment) 
  {
    comment_ = comment;
  }
  
  
//----------------------------------------------------------------------
/**
 * Get a list of route_points (GarminWaypoint objects) and Link objects.
 *
 * @return a list of  route_points.
 */
  public List getRoutePoints() 
  {
    return (route_points_);
  }
  
// //----------------------------------------------------------------------
// /**
//  * Set the route_points.
//  *
//  * @param route_points the route_points.
//  */
//   public void setRoutePoints(GarminWaypoint[] route_points) 
//   {
//     for(int index = 0; index < route_points.length; index++)
//       addRoutePoint(route_points[index]);
//   }
  
//----------------------------------------------------------------------
/**
 * Add a route point.
 *
 * @param route_point the route point to add.
 */
  public void addRoutePoint(GarminWaypoint route_point) 
  {
    route_points_.add(route_point);
  }

  public void addRouteLinkData(GarminRouteLinkD210 link_type)
  {
    route_points_.add(link_type); // FIXXME: maybe not clever to mix route points and links!
  }

  public String toString()
  {
    StringBuffer buf = new StringBuffer();
    buf.append("GarminRoute[identification=").append(identification_).append(",");
    buf.append("route points/links=").append(route_points_.toString()).append("]");
    return(buf.toString());
  }
  
}
