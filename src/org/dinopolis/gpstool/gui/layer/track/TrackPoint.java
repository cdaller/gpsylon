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


package org.dinopolis.gpstool.gui.layer.track;

import com.bbn.openmap.LatLonPoint;
import java.awt.Point;
import java.util.Date;
import org.dinopolis.gpstool.util.geoscreen.GeoScreenPoint;

//----------------------------------------------------------------------
/**
 * This class extends a {@link
 * org.dinopolis.gpstool.util.geoscreen.GeoScreenPoint} with
 * additional data (speed, data, altitude) and may be used to store
 * track data.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class TrackPoint extends GeoScreenPoint
{

  protected float speed_;
  protected Date date_;
  protected float altitude_;
  
//----------------------------------------------------------------------
/**
 * Empty Constructor
 */
  public TrackPoint()
  {
  }

  
//----------------------------------------------------------------------
/**
 * Copy Constructor.
 *
 * @param geoscreenpoint the GeoScreenTrackPoint to copy.
 */
  public TrackPoint(TrackPoint track_point)
  {
    x_ = track_point.x_;
    y_ = track_point.y_;
    latitude_ = track_point.latitude_;
    longitude_ = track_point.longitude_;
    speed_ = track_point.speed_;
    date_ = track_point.date_;
    altitude_ = track_point.altitude_;
  }

  
//----------------------------------------------------------------------
/**
 * Constructor using the screen location.
 *
 * @param screenlocation the location on screen.
 */
  public TrackPoint(Point screenlocation)
  {
    super(screenlocation);
  }

//----------------------------------------------------------------------
/**
 * Constructor using the screen location.
 *
 * @param x the x coordinate on screen.
 * @param y the y coordinate on screen.
 */
  public TrackPoint(int x, int y)
  {
    super(x,y);
  }

  
//----------------------------------------------------------------------
/**
 * Constructor using the geographical location.
 *
 * @param geolocation the geographical location.
 */
  public TrackPoint(LatLonPoint geolocation)
  {
    super(geolocation);
  }

//----------------------------------------------------------------------
/**
 * Constructor using the geographical location.
 *
 * @param latitude the latitude
 * @param longitude the longitude
 */
  public TrackPoint(float latitude, float longitude)
  {
    super(latitude,longitude);
  }

//----------------------------------------------------------------------
/**
 * Constructor using the geographical location, altiude, speed and date.
 *
 * @param latitude the latitude
 * @param longitude the longitude
 * @param altitude the altitude
 * @param speed the speed
 * @param date the date
 */
  public TrackPoint(float latitude, float longitude, float altitude,
		    float speed, Date date)
  {
    super(latitude,longitude);
    altitude_ = altitude;
    speed_ = speed;
    date_ = date;
  }


//----------------------------------------------------------------------
/**
 * Constructor using the geographical location, altiude, speed and date.
 *
 * @param latitude the latitude
 * @param longitude the longitude
 * @param altitude the altitude
 * @param speed the speed
 * @param date the date
 */
  public TrackPoint(LatLonPoint geo_location, float altitude,
		    float speed, Date date)
  {
    super(geo_location);
    altitude_ = altitude;
    speed_ = speed;
    date_ = date;
  }


//----------------------------------------------------------------------
/**
 * Get the altitude.
 *
 * @return the altitude.
 */
    public float getAltitude()
    {
      return (altitude_);
    }
    
//----------------------------------------------------------------------
/**
 * Set the altitude.
 *
 * @param altitude the altitude.
 */
    public void setAltitude(float altitude)
    {
      altitude_ = altitude;
    }

//----------------------------------------------------------------------
/**
 * Get the speed.
 *
 * @return the speed.
 */
    public float getSpeed()
    {
      return (speed_);
    }
  
//----------------------------------------------------------------------
/**
 * Set the speed.
 *
 * @param speed the speed.
 */
    public void setSpeed(float speed)
    {
      speed_ = speed;
    }

//----------------------------------------------------------------------
/**
 * Get the date.
 *
 * @return the date.
 */
    public Date getDate()
    {
      return (date_);
    }
    
//----------------------------------------------------------------------
/**
 * Set the date.
 *
 * @param date the date.
 */
    public void setDate(Date date)
    {
      date_ = date;
    }

//----------------------------------------------------------------------
/**
 * Returns a String representation of this object.
 *
 * @return a String representation of this object.
 */
  public String toString()
  {
    return("GeoScreenTrackPoint ["+super.toString()+",speed="+speed_+", alt="+altitude_
	   +", date="+date_+"]");
  }  
}


