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


package org.dinopolis.gpstool.util.geoscreen;
import java.awt.Point;
import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.proj.Projection;

//----------------------------------------------------------------------
/**
 * This class represents a geographical location (point) and its
 * projection to screen coordinates. One can set either the
 * geographical coordinates (latitude, longitude) and use the forward
 * method to calculate the screen coordinates or set the screen
 * coordinates and call the inverse method to calculate the
 * geographical coordinates.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GeoScreenPoint implements GeoScreen
{

  protected int x_;
  protected int y_;
  protected float latitude_;
  protected float longitude_;

//----------------------------------------------------------------------
/**
 * Empty Constructor
 */
  public GeoScreenPoint()
  {
  }

  
//----------------------------------------------------------------------
/**
 * Copy Constructor.
 *
 * @param geoscreenpoint the GeoScreenPoint to copy.
 */
  public GeoScreenPoint(GeoScreenPoint geoscreenpoint)
  {
    x_ = geoscreenpoint.x_;
    y_ = geoscreenpoint.y_;
    latitude_ = geoscreenpoint.latitude_;
    longitude_ = geoscreenpoint.longitude_;
  }

  
//----------------------------------------------------------------------
/**
 * Constructor using the screen location.
 *
 * @param screenlocation the location on screen.
 */
  public GeoScreenPoint(Point screenlocation)
  {
    this((int)screenlocation.getX(),(int)screenlocation.getY());
  }

//----------------------------------------------------------------------
/**
 * Constructor using the screen location.
 *
 * @param x the x coordinate on screen.
 * @param y the y coordinate on screen.
 */
  public GeoScreenPoint(int x, int y)
  {
    x_ = x;
    y_ = y;
  }

  
//----------------------------------------------------------------------
/**
 * Constructor using the geographical location.
 *
 * @param geolocation the geographical location.
 */
  public GeoScreenPoint(LatLonPoint geolocation)
  {
    this(geolocation.getLatitude(),geolocation.getLongitude());
  }

//----------------------------------------------------------------------
/**
 * Constructor using the geographical location.
 *
 * @param latitude the latitude
 * @param longitude the longitude
 */
  public GeoScreenPoint(float latitude, float longitude)
  {
    latitude_ = latitude;
    longitude_ = longitude;
  }

  
//----------------------------------------------------------------------
/**
 * Get the latitude.
 *
 * @return the latitude.
 */
  public float getLatitude()
  {
    return(latitude_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the latitude.
 *
 * @param latitude the latitude.
 */
  public void setLatitude(float latitude)
  {
    latitude_ = latitude;
  }

  
//----------------------------------------------------------------------
/**
 * Get the longitude.
 *
 * @return the longitude.
 */
  public float getLongitude()
  {
    return(longitude_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the longitude.
 *
 * @param longitude the longitude.
 */
  public void setLongitude(float longitude)
  {
    longitude_ = longitude;
  }

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
/**
 * Get the location on screen.
 *
 * @return the screen location.
 */
  public Point getScreenLocation()
  {
    return(new Point(x_,y_));
  }
  
//----------------------------------------------------------------------
/**
 * Set the location on screen.
 *
 * @param location the screen location.
 */
  public void setScreenLocation(Point location)
  {
    x_ = (int)location.getX();
    y_ = (int)location.getY();
  }


//----------------------------------------------------------------------
/**
 * Get the geographical location.
 *
 * @return the geographical location.
 */
  public LatLonPoint getGeoLocation()
  {
    return(new LatLonPoint(latitude_,longitude_));
  }
  
//----------------------------------------------------------------------
/**
 * Set the geographical location.
 *
 * @param geolocation the geographical location.
 */
  public void setGeoLocation(LatLonPoint geolocation)
  {
    latitude_ = geolocation.getLatitude();
    longitude_ = geolocation.getLongitude();
  }

  
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
    setScreenLocation(projection.forward(latitude_,longitude_));
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
    setGeoLocation(projection.inverse(x_,y_));
  }


//----------------------------------------------------------------------
/**
 * Returns a String representation of this object.
 *
 * @return a String representation of this object.
 */
  public String toString()
  {
    return("GeoScreenPoint [xy="+getScreenLocation()+", lat/lon="+getGeoLocation()+"]");
  }  
}


