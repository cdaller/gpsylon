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

package org.dinopolis.gpstool;

//----------------------------------------------------------------------
/**
 * This hook is used for hooks concerning the management of maps.
 * 
 * @author Christof Dallermassl
 * @version $Revision$
 */

public interface MapManagerHook
{

//----------------------------------------------------------------------
/**
 * Adds new maps to the system. This method is responsible to make
 * this information permantent and to add this map to the running
 * system. If the filename in map_info is already used, the map is not
 * added.
 *
 * @param map_infos the new maps
 */

//  public void addNewMaps(MapInfo[] map_infos)

//----------------------------------------------------------------------
/**
 * Adds a new map to the system. This method is responsible to make
 * this information permantent and to add this map to the running
 * system. If the filename in map_info is already used, the map is not
 * added.
 *
 * @param map_info the new map
 */

  public void addNewMap(MapInfo map_info);
  
}




