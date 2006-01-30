/***********************************************************************
 * @(#)$RCSfile$   $Revision$$Date$
*
 * Copyright (c) 2002 IICM, Graz University of Technology
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


package org.dinopolis.gpstool.util;
import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.proj.Projection;
import java.awt.geom.Line2D;

//----------------------------------------------------------------------
/**
 * This class represents an geographical extent which is represented
 * by the north, south, west and east coordinates.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GeoExtent  
{
  protected float north_;
  protected float south_;
  protected float west_;
  protected float east_;


//----------------------------------------------------------------------
/**
 * Creates an extent that represents the projection given. Some
 * projections have a different east-west length on top compared to
 * the bottom (same with north south on the left/right edge), so the
 * getUpperLeft and getLowerRight methods of the projection is not
 * really a help to find the maximum north,south,west, or east
 * coordinates. .
 *
 * @param projection the projection to calculate the extent from.
 */
  public GeoExtent(Projection projection)
  {
        // uper left:
    LatLonPoint point = projection.getUpperLeft();
    north_ = point.getLatitude();
    west_ = point.getLongitude();

    float tmp;

        // lower left:
    point = projection.inverse(0,projection.getHeight(),point);
    tmp = point.getLongitude();
    if(tmp < west_)
      west_ = tmp;

    south_ = point.getLatitude();

        // lower right:
    point = projection.getLowerRight();
    tmp = point.getLatitude();
    if(tmp < south_)
      south_ = tmp;

    east_ = point.getLongitude();
        // upper right:
    point = projection.inverse(projection.getWidth(),0,point);
    tmp = point.getLongitude();
    if(tmp > east_)
      east_ = tmp;

    tmp = point.getLatitude();
    if(tmp > north_)
      north_ = tmp;
  }

//----------------------------------------------------------------------
/**
 * Get the most northern coordinate of this extent.
 *
 * @return the most northern coordinate.
 */
  public float getNorth() 
  {
    return (north_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the most northern coordinate of this extent.
 *
 * @param north most northern coordinate.
 */
  public void setNorth(float north) 
  {
    north_ = north;
  }
  
//----------------------------------------------------------------------
/**
 * Get the most southern coordinate of this extent.
 *
 * @return the most southern coordinate.
 */
  public float getSouth() 
  {
    return (south_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the most southern coordinate of this extent.
 *
 * @param south most southern coordinate.
 */
  public void setSouth(float south) 
  {
    south_ = south;
  }
  
//----------------------------------------------------------------------
/**
 * Get the most western coordinate of this extent.
 *
 * @return the most western coordinate.
 */
  public float getWest() 
  {
    return (west_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the most western coordinate of this extent.
 *
 * @param west most western coordinate.
 */
  public void setWest(float west) 
  {
    west_ = west;
  }
  
//----------------------------------------------------------------------
/**
 * Get the most eastern coordinate of this extent.
 *
 * @return the most eastern coordinate.
 */
  public float getEast() 
  {
    return (east_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the most eastern coordinate of this extent.
 *
 * @param east most eastern coordinate.
 */
  public void setEast(float east) 
  {
    east_ = east;
  }

//----------------------------------------------------------------------
/**
 * Returns true, if the given latitude/longitude is inside the given geo extent.
 * The calculation does not take the curvature of the earth into account!
 *
 * @param latitude the latitude of the point.
 * @param longitude the longitude of the point.
 * @return true, if the given coordinates are inside the rectangle created
 * by north, east, south, west.
 */
  public boolean isInside(double latitude, double longitude)
  {
    return((latitude > south_) && (latitude < north_)
           && (longitude > west_) && (longitude < east_));
  }

//----------------------------------------------------------------------
/**
 * Returns true, if the line (lat1, lon1) to (lat2,lon2) intersects the
 * interior of this geo extent. The calculation does not take the curvature
 * of the earth into account!
 *
 * @param lat1 the latitude of the start point of the line.
 * @param lon1 the longitude of the start point of the line.
 * @param lat2 the latitude of the end point of the line.
 * @param lon2 the longitude of the end point of the line.
 * @return <code>true</code>, if the given line intersects one of the lines (N,W) to (N,E),
 * (N,E) to (S,E), (S,E) to (S,W) or (S,W) to (N,W).
 */
  public boolean intersectsLine(double lat1, double lon1, double lat2, double lon2)
  {
    return(isInside(lat1,lon1)
           || isInside(lat2,lon2)
           || Line2D.linesIntersect(north_,west_,north_,east_,lat1,lon1,lat2,lon2)
           || Line2D.linesIntersect(north_,east_,south_,east_,lat1,lon1,lat2,lon2)
           || Line2D.linesIntersect(south_,east_,south_,west_,lat1,lon1,lat2,lon2)
           || Line2D.linesIntersect(south_,west_,north_,east_,lat1,lon1,lat2,lon2));
  }

  public String toString()
  {
    return("GeoExtent[N:"+north_ + ",S:"+south_ +",W:"+west_+",E:"+east_+"]");
  }
  
}


