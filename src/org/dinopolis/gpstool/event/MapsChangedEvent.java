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
 */import org.dinopolis.gpstool.MapInfo;

public class MapsChangedEvent
{

  public static int MAP_REMOVED = 0;
  public static int MAP_ADDED = 1;

  protected int action_;
  protected MapInfo map_info_;
  protected Object source_;
  
//----------------------------------------------------------------------
/**
 * Constructor
 */
  public MapsChangedEvent(Object source, MapInfo info, int action)
  {
  	source_ = source;
    map_info_ = info;
    action_ = action;
  }

//----------------------------------------------------------------------
/**
 * Returns information about the map.
 *
 * @return information about the map added or removed.
 */
  public MapInfo getMapInfo()
  {
    return(map_info_);
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


