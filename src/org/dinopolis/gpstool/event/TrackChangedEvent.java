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

package org.dinopolis.gpstool.event;

//----------------------------------------------------------------------
/**
 * 
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class TrackChangedEvent
{

  public static int TRACK_REMOVED = 1;
  public static int TRACK_ADDED = 2;
  public static int TRACK_CHANGED = 4;

  protected int action_;
  protected String track_id_;
  protected Object source_;
  
//----------------------------------------------------------------------
/**
 * Constructor
 */
  public TrackChangedEvent(Object source, String track_id, int action)
  {
  	source_ = source;
    track_id_ = track_id;
    action_ = action;
  }

//----------------------------------------------------------------------
/**
 * Returns the track identification.
 *
 * @return the track identification added or removed.
 */
  public String getTrackIdentification()
  {
    return(track_id_);
  }

//----------------------------------------------------------------------
/**
 * Returns the action.
 *
 * @return the action
 */
  public int getAction()
  {
    return(action_);
  } 
  
//----------------------------------------------------------------------
/**
 * Returns the source of the event.
 *
 * @return the source
 */
  public Object getSource()
  {
  	return(source_);
  }
}


