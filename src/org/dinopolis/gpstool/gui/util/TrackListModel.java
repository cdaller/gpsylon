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


package org.dinopolis.gpstool.gui.util;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.dinopolis.gpstool.TrackManager;
import org.dinopolis.gpstool.event.TrackChangedEvent;
import org.dinopolis.gpstool.event.TrackChangedListener;
import org.dinopolis.gpstool.event.TrackChangedListener;
import java.util.Iterator;

//----------------------------------------------------------------------
/**
 * A list model to be used when tracks are displayed.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class TrackListModel implements ListModel, TrackChangedListener
{
  TrackManager track_manager_;
  List track_identifiers_;
  Vector data_listeners_;
  
//----------------------------------------------------------------------
/**
 * Constructor taking a track manager to read the track information from.
 *
 * @param track_manager the track manager to get the track information from.
 */
  public TrackListModel(TrackManager track_manager)
  {
    super();
    track_manager_ = track_manager;
    track_identifiers_ = new Vector();

        // add tracks from trackmanager:
    String[] track_ids = track_manager_.getTrackIdentifiers();
    for(int index = 0; index < track_ids.length; index++)
    {
      track_identifiers_.add(track_ids[index]);
    }
  }

  
// Implementation of javax.swing.ListModel

//----------------------------------------------------------------------
/**
 * Returns the size of the list.
 *
 * @return the size of the list
 */
  public int getSize()
  {
    return(track_identifiers_.size());
  }

//----------------------------------------------------------------------
/**
 * Add a ListDataListener to be informd about changes in the list.
 *
 * @param listener the listener to be added 
 */
  public void addListDataListener(ListDataListener list_data_listener)
  {
    if(data_listeners_ == null)
      data_listeners_ = new Vector();
    if(data_listeners_ != null)
      data_listeners_.add(list_data_listener);
  }

//----------------------------------------------------------------------
/**
 * Returns the element at the given position.
 *
 * @param pos the position
 */
  public Object getElementAt(int pos)
  {
    return(track_identifiers_.get(pos));
  }

//----------------------------------------------------------------------
/**
 * Remove a ListDataListener to be informd about changes in the list.
 *
 * @param listener the listener to be removed
 */
  public void removeListDataListener(ListDataListener listener)
  {
    if(data_listeners_ == null)
      return;
    data_listeners_.remove(listener);
  }


//----------------------------------------------------------------------
/**
 * Informs all listeners about changes in the list.
 *
 * @param track_id the id of the track
 * @param index the index of the track in the list
 * @param type indicates if the track is added or removed
 */
  protected void fireTrackChanged(String track_id, int index, int type)
  {
    if(data_listeners_ == null)
      return;

    Vector data_listeners;
    synchronized(data_listeners_)
    {
      data_listeners = new Vector(data_listeners_);
    }
    Iterator listener_iterator = data_listeners.iterator();
    ListDataListener listener;
    ListDataEvent data_event = new ListDataEvent(track_manager_,type,index,index);
    while(listener_iterator.hasNext())
    {
      listener = (ListDataListener)listener_iterator.next();
      if(type == ListDataEvent.INTERVAL_REMOVED)
        listener.intervalRemoved(data_event);
      else
        listener.intervalAdded(data_event);
    }
  }


// Implementation of org.dinopolis.gpstool.event.TrackChangedListener

//----------------------------------------------------------------------
/**
 * Called when a track is added or removed in the TrackManager
 *
 * @param event the event 
 */
  public void trackChanged(TrackChangedEvent event)
  {
    String id = event.getTrackIdentification();
    if(event.getAction() == TrackChangedEvent.TRACK_REMOVED)
    {
      int pos = track_identifiers_.indexOf(id);
      track_identifiers_.remove(id);
      fireTrackChanged(id,pos,ListDataEvent.INTERVAL_REMOVED);
    }
    else if(event.getAction() == TrackChangedEvent.TRACK_ADDED)
    {
      track_identifiers_.add(id);
      fireTrackChanged(id,track_identifiers_.indexOf(id),ListDataEvent.INTERVAL_ADDED);
    }
  }

  
}



