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
import org.dinopolis.gpstool.gpsinput.GPSTrackpoint;
import org.dinopolis.gpstool.gpsinput.GPSWaypoint;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.track.Track;
import org.dinopolis.gpstool.track.TrackImpl;
import org.dinopolis.gpstool.util.GeoExtent;
import org.dinopolis.util.Debug;
import org.dinopolis.util.Resources;

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

public class TrackManagerImpl implements TrackManager, GPSMapKeyConstants 
{
//  List tracks_ = new Vector();
//  Map track_map_ = new TreeMap(new RouteIdentificationComparator());
  Map track_map_ = new TreeMap();
  GPSDataProcessor gps_data_processor_;
  Vector track_listeners_;
  String active_track_identifier_;
  Resources resources_;

//----------------------------------------------------------------------
/**
 * Empty constructor
 */
  public TrackManagerImpl()
  {
  }


//----------------------------------------------------------------------
/**
 * Initialize with all the track manager needs (resources and gps data
 * processor).
 *
 * @param support 
 */
  public void initialize(PluginSupport support)
  {
    gps_data_processor_ = support.getGPSDataProcessor();
    resources_ = support.getResources();
    active_track_identifier_ = resources_.getString(KEY_TRACK_ACTIVE_TRACK_IDENTIFIER);
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
 * Set the tracks. All previously stored tracks are removed.
 *
 * @param tracks a list of tracks.
 */
  public void setTracks(List tracks)
  {
        // first remove the old tracks one by one (so the
        // TrackListeners are informed):
    List old_tracks = getTracks();
    Iterator iterator = old_tracks.iterator();
    while(iterator.hasNext())
    {
      removeTrack(((GPSTrack)iterator.next()).getIdentification());
    }
        // add the new tracks:
    iterator = tracks.iterator();
    while(iterator.hasNext())
    {
      addTrack(new TrackImpl((GPSTrack)iterator.next()));
    }
  }

//----------------------------------------------------------------------
/**
 * Add a track. If a track with the same identifier already exists,
 * the old track is overwritten.
 *
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
 * Defines the track with the given identifier as the active track
 * (the track used by the {@link
 * #addToActiveTrack(GPSTrackpoint)} method).
 *
 * @param identifier the identifier of the track to use as active track.
 * @throws IllegalArgumentException if not track exists with the given
 * name or the identifier is null
 */
  public void setActiveTrackIdentifier(String identifier)
    throws IllegalArgumentException
  {
    if(identifier == null)
      throw new IllegalArgumentException("Identifier must not be null!");
    Track track = getTrack(identifier);
    if(track == null)
      throw new IllegalArgumentException("Track with identifier '"
                                         +identifier+"' does not exist.");
    
    active_track_identifier_ = identifier;
  }


//----------------------------------------------------------------------
/**
 * Returns the name of the active track
 * (the track used by the {@link
 * #addToActiveTrack(GPSTrackpoint)} method).
 *
 * @return the identifier of the track to use as active track.
 */
  public String getActiveTrackIdentifier()
  {
    return(active_track_identifier_);
  }

//----------------------------------------------------------------------
/**
 * Adds the given trackpoint to the track with the given identifier.
 *
 * @param identifier the identifier of the track.
 * @param trackpoint the trackpoint to add to the track.
 * @throws IllegalArgumentException if the track does not exist or any
 * of the arguments are null.
 */
  public void addToTrack(String identifier, GPSTrackpoint trackpoint)
    throws IllegalArgumentException
  {
    Track track = getTrack(identifier);
    if(track == null)
      throw new IllegalArgumentException("Track with identifier '"
                                         +identifier+"' does not exist.");
    if(trackpoint == null)
      throw new IllegalArgumentException("Trackpoint must not be null");
    
    track.addWaypoint(trackpoint);
//    System.out.println("added trackpoint to track");
    fireTrackChangedEvent(new TrackChangedEvent(this,
                                                identifier,
                                                TrackChangedEvent.TRACK_CHANGED));
  }

//----------------------------------------------------------------------
/**
 * Adds the given trackpoint to the active track.
 *
 * @param trackpoint the trackpoint to add to the active track.
 * @throws IllegalArgumentException if the track does not exist or is
 * null.
 */
  public void addToActiveTrack(GPSTrackpoint trackpoint)
    throws IllegalArgumentException
  {
//    System.out.println("adding trackpoint to active track"+trackpoint);
    addToTrack(active_track_identifier_,trackpoint);
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
 * is determined for the given projection. The tracks returned are
 * deep cloned, so any changes in these tracks do not influence the
 * tracks stored in the track manager (and vice versa).
 *
 * @param projection the projection to use.
 * @throws InterruptedException if the thread was interrupted while
 * calculating the projection. This happens, if the projection changes
 * again, before the first calculations are finished and the second
 * SwingWorker interrupts the first one (the result of the first
 * worker is not valid any more anyway. This is done in the {@link
 * org.dinopolis.gpstool.gui.util.BasicLayer#recalculateCoordinates()}
 * method.
 * @return a list containing {@link org.dinopolis.gpstool.track.Track} objects.
 */
  public List getVisibleProjectedTracks(Projection projection)
    throws InterruptedException
  {
    Vector tracks = new Vector();
    GeoExtent visible_area = new GeoExtent(projection);
    
    synchronized(track_map_)
    {
      Iterator iterator = track_map_.keySet().iterator();
      Track track;
      TrackImpl projected_track;
      String id;
      while(iterator.hasNext())
      {
        id = (String)iterator.next();
        if(Debug.DEBUG)
          Debug.println("trackmanager","getVisibleProjectedTracks. checking track "+id);
        track = (Track)getTrack(id);
        if(isTrackVisible(track,visible_area))
        {
//          System.out.println("projection of track "+track.getIdentification());
              // deep clone of the track, so no changes may happen
              // after the projection was done:
          projected_track = new TrackImpl(track);
          projected_track.forward(projection);
//          System.out.println("end of projection of track "+track.getIdentification());
          tracks.add(projected_track);
        }
        if(Thread.interrupted())
        {
          throw new InterruptedException("Track Projection was interrupted");
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
 * is returned. The track returned is deep cloned, so any changes in
 * this tracks does not influence the tracks stored in the track
 * manager (and vice versa).
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
    TrackImpl projected_track = new TrackImpl(track);
    projected_track.forward(projection);
    return(projected_track);
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
    if(track_size == 0)
      return(false);
    
    GPSWaypoint start_point;
    GPSWaypoint next_point;
    int index = 1;
    start_point = track.getWaypoint(0);

    if(track_size == 1)
      return(visible_area.isInside(start_point.getLatitude(), start_point.getLongitude()));
    
    while (index < track_size)
    {
      next_point = track.getWaypoint(index);
      if(visible_area.intersectsLine(start_point.getLatitude(), start_point.getLongitude(),
                                     next_point.getLatitude(), next_point.getLongitude()))
        return(true);
      start_point = next_point;
      index++;
    }
    return(false);
    
//     GPSWaypoint point;
//     for(int index = 0; index < track_size; index++)
//     {
//       point = track.getWaypoint(index);
//       if((point.getLatitude() > visible_area.getSouth())
//          && (point.getLatitude() < visible_area.getNorth())
//          && (point.getLongitude() > visible_area.getWest())
//          && (point.getLongitude() < visible_area.getEast()))
//         return(true);
//     }
        // no point inside, return false:
//    return(false);
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


