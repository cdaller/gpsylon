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


package org.dinopolis.gpstool.track;

import com.bbn.openmap.proj.Projection;
import java.util.List;
import org.dinopolis.gpstool.event.TrackChangedListener;
import org.dinopolis.gpstool.gpsinput.GPSException;
import org.dinopolis.gpstool.gpsinput.GPSTrack;
import org.dinopolis.gpstool.gpsinput.GPSTrackpoint;

//----------------------------------------------------------------------
/**
 * Manages tracks and provides these to all modules.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public interface TrackManager  
{
//----------------------------------------------------------------------
/**
 * Returns a list of tracks.
 *
 * @return a list of tracks.
 */
  public List getTracks();

//----------------------------------------------------------------------
/**
 * Set the tracks. All previously stored tracks are removed. 
 *
 * @param tracks a list of tracks.
 */
  public void setTracks(List tracks);

//----------------------------------------------------------------------
/**
 * Add a track. If a track with the same identifier already exists,
 * the old track is overwritten.
 *
 * @param track the track to add.
 */
  public void addTrack(GPSTrack track);

//----------------------------------------------------------------------
/**
 * Returns the track identifiers.
 *
 * @return the tracks identifiers.
 */
  public String[] getTrackIdentifiers();

//----------------------------------------------------------------------
/**
 * Returns the track with the given identifier or <code>null</code> if
 * no track was found with the given identifier.
 *
 * @param identifier the identifier of the track to return.
 * @return the track with the given identifier or <code>null</code> if
 * no track was found with the given identifier.
 */
  public Track getTrack(String identifier);

//----------------------------------------------------------------------
/**
 * Removes the track with the given identifier.
 *
 * @param identifier the identifier of the track to remove.
 */
  public void removeTrack(String identifier);

//----------------------------------------------------------------------
/**
 * Defines the track with the given identifier as the active track
 * (the track used by the {@link #addToActiveTrack(GPSTrackpoint)} method).
 *
 * @param identifier the identifier of the track to use as active track.
 * @throws IllegalArgumentException if not track exists with the given
 * name or the identifier is null
 */
  public void setActiveTrackIdentifier(String identifier)
    throws IllegalArgumentException;


//----------------------------------------------------------------------
/**
 * Returns the name of the active track (the track used by the 
 * {@link #addToActiveTrack(GPSTrackpoint)} 
 * method).
 *
 * @return the identifier of the track to use as active track.
 */
  public String getActiveTrackIdentifier();

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
    throws IllegalArgumentException;

//----------------------------------------------------------------------
/**
 * Adds the given trackpoint to the active track.
 *
 * @param trackpoint the trackpoint to add to the active track.
 * @throws IllegalArgumentException if the track does not exist or is
 * null.
 */
  public void addToActiveTrack(GPSTrackpoint trackpoint)
    throws IllegalArgumentException;

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
    throws UnsupportedOperationException, GPSException;

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
    throws UnsupportedOperationException, GPSException;

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
    throws UnsupportedOperationException, GPSException;

//----------------------------------------------------------------------
/**
 * Adds a track listener.
 *
 * @param listener the tracklistener to be added.
 */
  public void addTrackListener(TrackChangedListener listener);

//----------------------------------------------------------------------
/**
 * Removes a track listener.
 *
 * @param listener the tracklistener to be added.
 */
  public void removeTrackListener(TrackChangedListener listener);


//----------------------------------------------------------------------
/**
 * Returns all visible projected tracks (holding the screen
 * coordinates as well as the geographic coordinates) in a List. If no
 * tracks are found (or visible), an empty list is returned. Visiblity
 * is determined for the given projection.
 *
 * @param projection the projection to use.
 * @throws InterruptedException if the thread was interrupted while
 * calculating the projection. This happens, if the projection changes
 * again, before the first calculations are finished and the second
 * SwingWorker interrupts the first one (the result of the first
 * worker is not valid any more anyway. This is done in the {@link
 * org.dinopolis.gpstool.gui.util.BasicLayer#recalculateCoordinates()}
 * method.
 * @return a list containing {@link org.dinopolis.gpstool.track.Track}
 * objects or an empty list.
 */
  public List getVisibleProjectedTracks(Projection projection)
    throws InterruptedException;
  
//----------------------------------------------------------------------
/**
 * Returns a projected track (holding the screen coordinates as well
 * as the geographic coordinates). At the moment, no caching is done,
 * so the calculation is always executed when this method is called.
 * If no track was found with the given identifier, <code>null</code>
 * is returned.
 *
 * @param identifier the identifier of the track to return.
 * @param projection the projection to use.
 * @return the projected track or null if no track was found.
 */
  public Track getProjectedTrack(String identifier, Projection projection);
  
}


