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


package org.dinopolis.gpstool.gui.layer.location;

import org.dinopolis.gpstool.util.geoscreen.GeoScreenPoint;
import com.bbn.openmap.LatLonPoint;

//----------------------------------------------------------------------
/**
 * This class serve as geographical markers with a
 * location and a name to be displayed.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class LocationMarker extends GeoScreenPoint
{

  protected String name_;
  LocationMarkerCategory category_;
  

//----------------------------------------------------------------------
/**
 * Empty Constructor.
 *
 */
  public LocationMarker()
  {
  }
  
//----------------------------------------------------------------------
/**
 * Copy Constructor.
 *
 * @param marker the marker to copy.
 */
  public LocationMarker(LocationMarker marker)
  {
    name_ = marker.name_;
    category_ = marker.category_;
        // set variables of superclass (GeoScreenPoint):
    x_ = marker.x_;
    y_ = marker.y_;
    latitude_ = marker.latitude_;
    longitude_ = marker.longitude_;
  }
  
//----------------------------------------------------------------------
/**
 * Constructor using the geographical location and the name.
 *
 * @param geolocation the geographical location.
 * @param name the name of the location marker.
 */
  public LocationMarker(String name, LatLonPoint geolocation)
  {
    this(name,geolocation.getLatitude(),geolocation.getLongitude());
  }

//----------------------------------------------------------------------
/**
 * Constructor using the geographical location and the name.
 *
 * @param name the name of the location marker.
 * @param geolocation the geographical location.
 * @param category the category of the marker.
 */
  public LocationMarker(String name, LatLonPoint geolocation,
                        LocationMarkerCategory category)
  {
    this(name,geolocation.getLatitude(),geolocation.getLongitude(),category);
  }

//----------------------------------------------------------------------
/**
 * Constructor using the geographical location and the name and a
 * default category..
 *
 * @param latitude the latitude
 * @param longitude the longitude
 * @param name the name of the location marker.
 */
  public LocationMarker(String name,float latitude, float longitude)
  {
    this(name,latitude,longitude,null);
  }

  
//----------------------------------------------------------------------
/**
 * Constructor using the geographical location and the name.
 *
 * @param name the name of the location marker.
 * @param latitude the latitude
 * @param longitude the longitude
 * @param category the category of the marker.
 */
  public LocationMarker(String name,float latitude, float longitude,
                        LocationMarkerCategory category)
  {
    super(latitude,longitude);
    name_ = name;
    if(category != null)
      category_ = category;
    else
      category_ = new LocationMarkerCategory("none","none");
  }

  
//----------------------------------------------------------------------
/**
 * Get the name of the Location.
 *
 * @return the name.
 */
  public String getName()
  {
    return(name_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the name of the Location.
 *
 * @param name the name.
 */
  public void setName(String name)
  {
    name_ = name;
  }

//----------------------------------------------------------------------
/**
 * Get the category.
 *
 * @return the category.
 */
  public LocationMarkerCategory getCategory() 
  {
    return (category_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the category.
 *
 * @param category the category.
 */
  public void setCategory(LocationMarkerCategory category) 
  {
    category_ = category;
  }
  
//----------------------------------------------------------------------
/**
 * Returns a String representation of this object.
 *
 * @return a String representation of this object.
 */
  public String toString()
  {
    return("LocationMarker [name='"+name_+"', pos="+super.toString()+"]");
  }
  
}


