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

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.dinopolis.gpstool.gpsinput.GPSTrackpoint;

//----------------------------------------------------------------------
/**
 * Adapter to map the interface of a GPSTrackpoint to the interface of
 * the GarminWaypoint (altitude is a float, not a double in the garmin
 * protocol, identification and comment are empty, date is translated
 * from garmin format). At the moment, only get methods are supported.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GarminTrackpointAdapter implements GPSTrackpoint
{

  protected GarminTrackpoint trackpoint_;

  public static long garmin_zero_date_seconds_;


  static
  {
    TimeZone timezone = TimeZone.getTimeZone("UTC");
    Calendar garmin_zero = Calendar.getInstance(timezone);
    garmin_zero.set(Calendar.DAY_OF_MONTH,0);
    garmin_zero.set(Calendar.MONTH,0);
    garmin_zero.set(Calendar.YEAR,1990);
    garmin_zero.set(Calendar.HOUR_OF_DAY,0);
    garmin_zero.set(Calendar.MINUTE,0);
    garmin_zero.set(Calendar.SECOND,0);
    garmin_zero.set(Calendar.MILLISECOND,0);
//     System.out.println("garmin garmin_zero_: "+garmin_zero.getTime()+" "+garmin_zero);
    garmin_zero_date_seconds_ = garmin_zero.getTime().getTime() / 1000;
        // alternative is to set the value directly (taken from gpspoint2):
//    garmin_zero_date_seconds_ = 631065600L;
  }
  
  public GarminTrackpointAdapter(GarminTrackpoint trackpoint)
  {
    trackpoint_ = trackpoint;
  }

//----------------------------------------------------------------------
/**
 * Get the identification value. Always an empty string for a garmin
 * trackpoint.
 * @return the identification value.
 */
  public String getIdentification()
  {
    return("");
  }

//----------------------------------------------------------------------
/**
 * Set the identification.
 * @param identification The new identification.
 */
  public void setIdentification(String identification)
  {
    // not supported by garmin trackpoints
  }

//----------------------------------------------------------------------
/**
 * Get the comment. Always an empty string for a garmin trackpoint.
 * @return the comment or an empty string, if no comment was set.
 */
  public String getComment()
  {
    return("");
  }

//----------------------------------------------------------------------
/**
 * Set the comment.
 * @param comment The comment.
 */
  public void setComment(String comment)
  {
    // not supported by garmin trackpoints
  }

//----------------------------------------------------------------------
/**
 * Get the latitude (in degrees, positive means North).
 * @return the latitude value.
 */
  public double getLatitude()
  {
    return(trackpoint_.getLatitude());
  }

//----------------------------------------------------------------------
/**
 * Set the latitude.
 * @param latitude The new latitude value.
 */
  public void setLatitude(double latitude)
  {
//    trackpoint_.setLatitude(latitude);
  }

//----------------------------------------------------------------------
/**
 * Get the longitude (in degrees - positive degrees mean East).
 * @return the longitude value.
 */
  public double getLongitude()
  {
    return(trackpoint_.getLongitude());
  }

//----------------------------------------------------------------------
/**
 * Set the longitude.
 * @param longitude The new longitude value.
 */
  public void setLongitude(double longitude)
  {
//    trackpoint_.setLongitude(longitude);
  }

//----------------------------------------------------------------------
/**
 * Get the altitude (in meters above sea level). Returns
 * <code>Double.NaN</code>, if no altitude was set.
 * @return the altitude value.
 */
  public double getAltitude()
  {
    float alt = trackpoint_.getAltitude();
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
//    trackpoint_.setAltitude((float)altitude);
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


//----------------------------------------------------------------------
/**
 * Returns the date of the given trackpoint or null, if no date was set.
 *
 * @return the date of the given trackpoint or null, if no date was set.
 */
  public Date getDate()
  {
//     long unixtime = (trackpoint_.getTime() + garmin_zero_date_seconds_) * 1000;
//     System.out.println("garmin time: "+trackpoint_.getTime()+" unixtime: "+unixtime+" date:"+new Date(unixtime));
//     System.out.println("Garmin zero: "+garmin_zero_date_seconds_);
//     System.out.println("calc: "+getDateFromGarminTime(trackpoint_.getTime()));
    return(getDateFromGarminTime(trackpoint_.getTime()));
  }

//----------------------------------------------------------------------
/**
 * Sets the date of the given trackpoit.
 *
 * @param date the date of the trackpoint.
 */
  public void setDate(Date date)
  {
//    trackpoint_.setTime(getGarminTimeFromDate(date));
  }

  
//----------------------------------------------------------------------
/**
 * Is this the beginning of a new track segment? If true, this point
 * marks the beginning of a new track segment.
 *
 * @return Beginning of new track segment (boolean)
 * @throws GarminUnsupportedMethodException
 */
  public boolean isNewTrack()
  {
    return(trackpoint_.isNewTrack());
  }

//----------------------------------------------------------------------
/**
 * Set the beginning of a new track segment? If true, this point
 * marks the beginning of a new track segment.
 *
 * @param new_segment beginning of new track segment
 */
  public void  setNewTrack(boolean new_segment)
  {
        //trackpoint_.setNewTrack(new_segment);
  }


//----------------------------------------------------------------------
/**
 * Returns null, as garmin trackpoints do not support symbols of
 * trackpoints.
 * @return null
 */
  public String getSymbolName()
  {
    return(null);
  }


//----------------------------------------------------------------------
/**
 * Returns the seconds from 1.1.1990 from the given date.
 *
 * @param the date.
 * @return the seconds from 1.1.1990 from the given date.
 */
  protected long getGarminTimeFromDate(Date date)
  {
    return(date.getTime()/1000 - garmin_zero_date_seconds_);
  }
  
//----------------------------------------------------------------------
/**
 * Returns the date from the seconds since 1.1.1990 or null, if
 * <code>garmin_time</code> is <0.
 *
 * @param the seconds.
 * @return the date from the seconds since 1.1.1990 or null.
 */
  protected Date getDateFromGarminTime(long garmin_time)
  {
//     Calendar new_cal = (Calendar)garmin_zero_.clone();
//     new_cal.add(Calendar.SECOND,(int)garmin_time);
//     return(new_cal.getTime());
    if(garmin_time < 0)
      return(null);
    
    return(new Date((garmin_zero_date_seconds_ + garmin_time) * 1000));
  }
  
}


