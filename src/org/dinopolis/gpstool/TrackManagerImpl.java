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


package org.dinopolis.gpstool;

import com.bbn.openmap.proj.Projection;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import org.dinopolis.gpstool.event.TrackChangedEvent;
import org.dinopolis.gpstool.event.TrackChangedListener;
import org.dinopolis.gpstool.gpsinput.GPSDataProcessor;
import org.dinopolis.gpstool.gpsinput.GPSException;
import org.dinopolis.gpstool.gpsinput.GPSTrack;
import org.dinopolis.gpstool.gpsinput.GPSWaypoint;
import org.dinopolis.gpstool.track.RouteIdentificationComparator;
import org.dinopolis.gpstool.track.Track;
import org.dinopolis.gpstool.track.TrackImpl;
import org.dinopolis.gpstool.util.GeoExtent;
import org.dinopolis.util.Debug;

//----------------------------------------------------------------------
/**
 * Manages tracks and provides these to all modules. Internally, all
 * {@link org.dinopolis.gpstool.gpsinput.GPSTrack} objects that are
 * added or downloaded are converted to {@link
 * org.dinopolis.gpstool.track.Track} objects, so they may be
 * projected and printed.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class TrackManagerImpl implements TrackManager  
{
//  List tracks_ = new Vector();
//  Map track_map_ = new TreeMap(new RouteIdentificationComparator());
  Map track_map_ = new TreeMap();
  GPSDataProcessor gps_data_processor_;
  Vector track_listeners_;

//----------------------------------------------------------------------
/**
 * Empty constructor
 */
  public TrackManagerImpl()
  {
    
  }

//----------------------------------------------------------------------
/**
 * Set the gps data processor to use to down/upload tracks from the
 * gps device.
 *
 * @param processor a gps data processor.
 */
  public void setGPSDataProcessor(GPSDataProcessor processor)
  {
    gps_data_processor_ = processor;
  }
  
//----------------------------------------------------------------------
/**
 * Returns a copy of the list of tracks ({@link
 * org.dinopolis.gpstool.track.Track} objects).
 *
 * @return a list of tracks.
 */
  public List getTracks()
  {
    synchronized(track_map_)
    {
      Collection tracks = track_map_.values();
      return(new Vector(tracks));
    }
  }

//----------------------------------------------------------------------
/**
 * Set the tracks.
 *
 * @param tracks a list of tracks.
 */
  public void setTracks(List tracks)
  {
    synchronized(track_map_)
    {
      track_map_.clear();
      Iterator iterator = tracks.iterator();
      while(iterator.hasNext())
      {
        addTrack(new TrackImpl((GPSTrack)iterator.next()));
      }
    }
  }

//----------------------------------------------------------------------
/**
 * Add a track. 
 * TODO: what happens, when the track has the same id as an already existing track?
 * @param track the track to add.
 */
  public void addTrack(GPSTrack track)
  {
    synchronized(track_map_)
    {
      if(Debug.DEBUG)
        Debug.println("trackmanager","adding track "+track.getIdentification());
      track_map_.put(track.getIdentification(),new TrackImpl(track));
      fireTrackChangedEvent(new TrackChangedEvent(this,
                                                  track.getIdentification(),
                                                  TrackChangedEvent.TRACK_ADDED));
    }
  }

//----------------------------------------------------------------------
/**
 * Returns the track identifiers.
 *
 * @return the tracks identifiers.
 */
  public String[] getTrackIdentifiers()
  {
    synchronized(track_map_)
    {
      Set identifier_set = track_map_.keySet();
      String[] identifiers = new String[identifier_set.size()];
      identifiers = (String[])identifier_set.toArray(identifiers);
      return(identifiers);
    }
  }

//----------------------------------------------------------------------
/**
 * Returns the track with the given identifier or <code>null</code> if
 * no track was found with the given identifier.
 *
 * @param identifier the identifier of the track to return.
 * @return the track with the given identifier or <code>null</code> if
 * no track was found with the given identifier.
 */
  public Track getTrack(String identifier)
  {
    synchronized(track_map_)
    {
      return((Track)track_map_.get(identifier));
    }
  }

//----------------------------------------------------------------------
/**
 * Removes the track with the given identifier.
 *
 * @param identifier the identifier of the track to remove.
 */
  public void removeTrack(String identifier)
  {
    synchronized(track_map_)
    {
      track_map_.remove(identifier);
      fireTrackChangedEvent(new TrackChangedEvent(this,
                                                   identifier,
                                                   TrackChangedEvent.TRACK_REMOVED));
    }
  }


//----------------------------------------------------------------------
/**
 * Download all tracks from the connected gps device and adds them to
 * the list of tracks.
 *
 * @throws UnsupportedOperationException if the gps device does not
 * support this operation.
 * @throws GPSException if the operation threw an exception
 * (e.g. communication problem).
 */
  public void addTracksFromGPSDevice()
    throws UnsupportedOperationException, GPSException
  {
    if(gps_data_processor_ == null)
      throw new UnsupportedOperationException("No gps data processor available (set).");

    List gps_tracks = gps_data_processor_.getTracks();
    Iterator iterator = gps_tracks.iterator();
    GPSTrack gps_track;
    Track track;
    while(iterator.hasNext())
    {
      gps_track = (GPSTrack)iterator.next();
      track = new TrackImpl(gps_track);
      addTrack(track);
    }
  }

//----------------------------------------------------------------------
/**
 * Uploads all available tracks to the gps device.
 *
 * @throws UnsupportedOperationException if the gps device does not
 * support this operation.
 * @throws GPSException if the operation threw an exception
 * (e.g. communication problem).
 */
  public void uploadTracksToGPSDevice()
    throws UnsupportedOperationException, GPSException
  {
    if(gps_data_processor_ == null)
      throw new UnsupportedOperationException("No gps data processor available (set).");

    gps_data_processor_.setTracks(getTracks());
  }

//----------------------------------------------------------------------
/**
 * Uploads the tracks with the given identifiers to the gps device.
 *
 * @param identifiers the identifiers of the tracks to be uploaded.
 * @throws UnsupportedOperationException if the gps device does not
 * support this operation.
 * @throws GPSException if the operation threw an exception
 * (e.g. communication problem).
 */
  public void uploadTracksToGPSDevice(String[] identifiers)
    throws UnsupportedOperationException, GPSException
  {
    if(gps_data_processor_ == null)
      throw new UnsupportedOperationException("No gps data processor available (set).");

    Vector tracks = new Vector();
    for(int index = 0; index < identifiers.length; index++)
    {
      tracks.add(getTrack(identifiers[index]));
    }
    if(tracks.size() > 0)
      gps_data_processor_.setTracks(tracks);
  }

//----------------------------------------------------------------------
/**
 * Adds a track listener.
 *
 * @param listener the tracklistener to be added.
 */
  public void addTrackListener(TrackChangedListener listener)
  {
    if(track_listeners_ == null)
      track_listeners_ = new Vector();

    synchronized(track_listeners_)
    {
      track_listeners_.add(listener);
    }
  }

//----------------------------------------------------------------------
/**
 * Removes a track listener.
 *
 * @param listener the tracklistener to be added.
 */
  public void removeTrackListener(TrackChangedListener listener)
  {
    if(track_listeners_ == null)
      return;

    synchronized(track_listeners_)
    {
      track_listeners_.remove(listener);
    }
  }


//----------------------------------------------------------------------
/**
 * Returns all visible projected tracks (holding the screen
 * coordinates as well as the geographic coordinates) in a List. If no
 * tracks are found (or visible), an empty list is returned. Visiblity
 * is determined for the given projection.
 *
 * @param projection the projection to use.
 * @return a list containing {@link org.dinopolis.gpstool.track.Track} objects.
 */
  public List getVisibleProjectedTracks(Projection projection)
  {
    Vector tracks = new Vector();
    GeoExtent visible_area = new GeoExtent(projection);
    
    synchronized(track_map_)
    {
      Iterator iterator = track_map_.keySet().iterator();
      Track track;
      String id;
      while(iterator.hasNext())
      {
        id = (String)iterator.next();
        if(Debug.DEBUG)
          Debug.println("trackmanager","getVisibleProjectedTracks. checking track "+id);
        track = (Track)getTrack(id);
        if(isTrackVisible(track,visible_area))
        {
          track.forward(projection);
          tracks.add(track);
        }
      }
    }
    return(tracks);
  }
  
//----------------------------------------------------------------------
/**
 * Returns a projected track (holding the screen coordinates as well
 * as the geographic coordinates). At the moment, no caching is done,
 * so the calculation is always executed when this method is called.
 * If no track was found with the given identifier, <code>null</code>
 * is returned.
 * TODO: cache calls with same projection
 *
 * @param identifier the identifier of the track to return.
 * @param projection the projection to use.
 * @return the projected track.
 */
  public Track getProjectedTrack(String identifier, Projection projection)
  {
    Track track = (Track)getTrack(identifier);
    if(track == null)
      return(null);
    track.forward(projection);
    return(track);
  }


//----------------------------------------------------------------------
/**
 * Returns true if the given track is starts in, ends in or crosses
 * the given area, false otherwise.
 *
 * @return true if the track is visible in the given area.
 */
  public static boolean isTrackVisible(GPSTrack track, GeoExtent visible_area)
  {
    if(Debug.DEBUG && Debug.isEnabled("trackmanager"))
      Debug.println("trackmanager","isTrackVisible: track="+track.getIdentification()
                    +" visible area="+visible_area
                    +" track minlat="+track.getMinLatitude()
                    +" track maxlat="+track.getMaxLatitude()
                    +" track minlon="+track.getMinLongitude()
                    +" track maxlon="+track.getMaxLongitude());

    int track_size = track.size();
    GPSWaypoint point;
    for(int index = 0; index < track_size; index++)
    {
      point = track.getWaypoint(index);
      if((point.getLatitude() > visible_area.getSouth())
         && (point.getLatitude() < visible_area.getNorth())
         && (point.getLongitude() > visible_area.getWest())
         && (point.getLongitude() < visible_area.getEast()))
        return(true);
    }
        // no point inside, return false:
    return(false);
  }


//----------------------------------------------------------------------
/**
 * Inform all registered listeners about track changes.
 *
 * @param event the event.
 */
  protected void fireTrackChangedEvent(TrackChangedEvent event)
  {
    if(track_listeners_ == null)
      return;
    synchronized(track_listeners_)
    {
      Iterator iterator = track_listeners_.iterator();
      while(iterator.hasNext())
      {
        ((TrackChangedListener)iterator.next()).trackChanged(event);
      }
    }
  }
}


