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

package org.dinopolis.gpstool.plugin.tracklayer;

import com.bbn.openmap.LatLonPoint;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import org.dinopolis.gpstool.GPSMap;
import org.dinopolis.gpstool.TrackManager;
import org.dinopolis.gpstool.track.Track;
import org.dinopolis.gpstool.track.TrackImpl;
import org.dinopolis.gpstool.track.Trackpoint;
import org.dinopolis.gpstool.track.TrackpointImpl;
import org.dinopolis.util.Debug;




//----------------------------------------------------------------------
/**
 * This class is informed about the current position and passes this
 * information to the trackmanager.
 * 
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class ActiveTrackLogger implements PropertyChangeListener
{
  protected TrackManager track_manager_;
  boolean logging_active_ = false;
  float current_altitude_;
  LatLonPoint current_position_;
  String active_track_identifier_;
  
//----------------------------------------------------------------------
/**
 * Empty constructor.
 */
  public ActiveTrackLogger()
  {
  }

//----------------------------------------------------------------------
/**
 * Initialzie the logger with the track manager.
 *
 * @param track_manager the track manager to store the trackpoints.
 */
  public void initialize(TrackManager track_manager)
  {
    track_manager_ = track_manager;
    active_track_identifier_ = track_manager_.getActiveTrackIdentifier();
    checkForActiveTrack();
  }


//----------------------------------------------------------------------
/**
 * Checks, if the active track exists, if not, create a new track.
 */
  protected void checkForActiveTrack()
  {
        // does the active track exist? if not, create it!
    Track track = track_manager_.getTrack(active_track_identifier_);
    if(track == null)
    {
      track = new TrackImpl();
      track.setIdentification(active_track_identifier_);
      track.setComment("crated by GPSylon");
      track_manager_.addTrack(track);
    }
  }


//----------------------------------------------------------------------
/**
 * Enable (or disable) logging of the current information.
 *
 * @param enable_log if true, the current position is logged.
 */
  public void enable(boolean enable_log)
  {
    logging_active_ = enable_log;
  }

//----------------------------------------------------------------------
/**
 * Returns true, if the logger is logging.
 *
 * @return true, if the logger is logging.
 */
  public boolean isEnabled()
  {
    return(logging_active_);
  }

//----------------------------------------------------------------------
/**
 * Callback method for property change events (Postion, altitude, ...)
 * 
 * @param event the property change event.
 */

  public void propertyChange(PropertyChangeEvent event)
  {
    if(!logging_active_)
      return;
    
    String name = event.getPropertyName();
    if(name.equals(GPSMap.PROPERTY_KEY_GPS_ALTITUDE))
    {
      Float altitude = (Float)event.getNewValue();
      if(altitude != null)
        current_altitude_ = altitude.floatValue();
    }
    if(name.equals(GPSMap.PROPERTY_KEY_GPS_LOCATION))
    {
      LatLonPoint tmp_point = (LatLonPoint)event.getNewValue();
//       System.out.println("TrackLayer, propertyChange: old: "+event.getOldValue()+" new:"+
// 			 event.getNewValue());
      if(tmp_point != null)
      {
        if(Debug.DEBUG)
          Debug.println("track_logger","new position in tracklayer: "+tmp_point);
        Trackpoint trackpoint = new TrackpointImpl();
        trackpoint.setDate(new Date());
        trackpoint.setLatitude(tmp_point.getLatitude());
        trackpoint.setLongitude(tmp_point.getLongitude());
        trackpoint.setAltitude(current_altitude_);
        checkForActiveTrack();
        track_manager_.addToActiveTrack(trackpoint);
      }
      return;
    }
  }
  
}
