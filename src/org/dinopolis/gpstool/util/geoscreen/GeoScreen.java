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

import com.bbn.openmap.proj.Projection;

//----------------------------------------------------------------------
/**
 * This interface represents a geographical location (point) and its
 * projection to screen coordinates. One can set either the
 * geographical coordinates (latitude, longitude) and use the forward
 * method to calculate the screen coordinates or set the screen
 * coordinates and call the inverse method to calculate the
 * geographical coordinates.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public interface GeoScreen
{
  
// //----------------------------------------------------------------------
// /**
//  * Get the latitude.
//  *
//  * @return the latitude.
//  */
//   public float getLatitude();
  
// //----------------------------------------------------------------------
// /**
//  * Set the latitude.
//  *
//  * @param latitude the latitude.
//  */
//   public void setLatitude(float latitude);

  
// //----------------------------------------------------------------------
// /**
//  * Get the longitude.
//  *
//  * @return the longitude.
//  */
//   public float getLongitude();
  
// //----------------------------------------------------------------------
// /**
//  * Set the longitude.
//  *
//  * @param longitude the longitude.
//  */
//   public void setLongitude(float longitude);

// //----------------------------------------------------------------------
// /**
//  * Get the x coordinate on screen.
//  *
//  * @return the x coordinate on screen.
//  */
//   public int getX();
  
// //----------------------------------------------------------------------
// /**
//  * Set the x coordinate on screen.
//  *
//  * @param x the x coordinate on screen.
//  */
//   public void setX(int x);

  
// //----------------------------------------------------------------------
// /**
//  * Get the y coordinate on screen.
//  *
//  * @return the y coordinate on screen.
//  */
//   public int getY();
  
// //----------------------------------------------------------------------
// /**
//  * Set the y coordinate on screen.
//  *
//  * @param y the y coordinate on screen.
//  */
//   public void setY(int y);

// //----------------------------------------------------------------------
// /**
//  * Get the location on screen.
//  *
//  * @return the screen location.
//  */
//   public Point getScreenLocation();
  
// //----------------------------------------------------------------------
// /**
//  * Set the location on screen.
//  *
//  * @param location the screen location.
//  */
//   public void setScreenLocation(Point location);


// //----------------------------------------------------------------------
// /**
//  * Get the geographical location.
//  *
//  * @return the geographical location.
//  */
//   public LatLonPoint getGeoLocation();
  
// //----------------------------------------------------------------------
// /**
//  * Set the geographical location.
//  *
//  * @param geolocation the geographical location.
//  */
//   public void setGeoLocation(LatLonPoint geolocation);

  
//----------------------------------------------------------------------
/**
 * Use the geographical coordinates (have to be set prior to calling
 * this method) and the given projection to calculate the screen
 * coordinates of this GeoScreenPoint.
 *
 * @param projection the projection to use.
 */
  public void forward(Projection projection);

//----------------------------------------------------------------------
/**
 * Use the screen coordinates (have to be set prior to calling this
 * method) and the given projection to calculate the geographical
 * coordinates of this GeoScreenPoint.
 *
 * @param projection the projection to use.
 */
  public void inverse(Projection projection);


  
}


