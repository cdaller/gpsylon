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

import java.io.IOException;
import java.util.Collection;

import org.dinopolis.gpstool.event.MapsChangedListener;

import com.bbn.openmap.proj.Projection;

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
  
  //----------------------------------------------------------------------
  /**
   * Removes the given map from the map manager. This method does not store
   * the information in the file.
   * 
   * @param info the map info
   */
  public void removeMap(MapInfo info);

  //----------------------------------------------------------------------
  /**
   * Stores the map informations in (in the file, in the database, ...)
   *
   * @throws IOException if an error occured.
   */
  public void storeMapInfos() throws IOException;



//----------------------------------------------------------------------
/**
 * Returns a list that holds information about all available maps
 * (MapInfo objects).
 *
 * @return information about all available maps.
 */
  public Collection getMapInfos();

  //----------------------------------------------------------------------
/**
 * Returns a list of ImageInfo objects that describe all maps and
 * their position that are visible in the given projection. This list
 * holds also maps that are below other maps (on top). The
 * following algorithm is used to to determine the visibility of the
 * maps: if the distance between the center of the image and the
 * center of the viewport (the projection) is less than (image.width +
 * viewport.width)/2 (same with height), the image is visible. The
 * "visible_rectangle" info is not used in the returned ImageInfo
 * objects!
 *
 * @param projection the projection to find the images for.
 */
  public Collection getAllVisibleImages(Projection projection);

//----------------------------------------------------------------------
/**
 * Returns a collection of ImageInfo objects that describe all maps and
 * their position that are visible in the given projection. This collection
 * holds also maps that are covered by other maps (on top). The
 * following algorithm is used to to determine the visibility of the
 * maps: if the distance between the center of the image and the
 * center of the viewport (the projection) is less than (image.width +
 * viewport.width)/2 (same with height), the image is visible. The
 * "visible_rectangle" info is not used in the returned ImageInfo
 * objects!
 *
 * @param projection the projection to find the images for.
 * @param min_scale_factor if the scale of the image divided by the
 * scale of the projection is less than this value, the image is not
 * used. So if the scale of the projection is 100000 and one map is
 * of scale 1000, the factor would be 0.01, so if a min_scale_factor
 * of 0.5 is given, the map would not be taken.
 */
  public Collection getAllVisibleImages(Projection projection,
					double min_scale_factor);

//----------------------------------------------------------------------
/**
 * Adds as a listener that is informed about changes of the maps
 * (adding, removal).
 *
 * @param listener the listener to add
 */
  public void addMapsChangedListener(MapsChangedListener listener);


  //----------------------------------------------------------------------
/**
 * Remove a listener that is informed about changes of the maps
 * (adding, removal).
 *
 * @param listener the listener to be removed.
 */
  public void removeMapsChangedListener(MapsChangedListener listener);
}






