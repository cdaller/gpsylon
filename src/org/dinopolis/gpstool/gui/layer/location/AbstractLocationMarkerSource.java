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


package org.dinopolis.gpstool.gui.layer.location;

import com.bbn.openmap.util.quadtree.QuadTree;

import java.util.Iterator;

import org.dinopolis.gpstool.util.geoscreen.GeoScreenList;
import org.dinopolis.gpstool.gui.layer.location.LocationMarker;

//----------------------------------------------------------------------
/**
 * This class holds the location markers in memory. Extending classes
 * provide the persistent backup for the data.  Extending classes may
 * read (and optionally write) their location marker from some file
 * (or any other place). The extending class must only read the data
 * from the file and the abstract super class does the rest (store it
 * in a quadtree, ...).
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public abstract class AbstractLocationMarkerSource
  implements LocationMarkerSource
{

  QuadTree quad_tree_;


//----------------------------------------------------------------------
/**
 * Call this method as soon as all parameters needed to read the data
 * are set. It will create a QuadTree and fill it with the
 * LocationMarkers returned from the readLocationMarkers method.  If
 * this method is not called, not data is read from the persistent
 * storage!
 * */
  public void initialize()
    throws LocationMarkerSourceException
  {
    if(quad_tree_ == null)
      quad_tree_ = new QuadTree(90.0f, -180.0f, -90.0f, 180.0f, 100, 50f);

    GeoScreenList list = readLocationMarkers();
    if(list != null)
    {
      Iterator iterator = list.iterator();
      LocationMarker marker;
      while(iterator.hasNext())
      {
        marker = (LocationMarker)iterator.next();
        quad_tree_.put(marker.getLatitude(),marker.getLongitude(),marker);
      }
    }
  }
  

//----------------------------------------------------------------------
/**
 * Returns a list of LocationMarker objects in a GeoScreenList that
 * match the given parameters.
 *
 * @param north Northern limit for the locations.
 * @param south Southern limit for the locations to retrieve.
 * @param west Western limit for locations to retrieve.
 * @param east Eastern limit for locations to retrieve.
 * @return a list of LocationMarker objects.
 */
  public GeoScreenList getLocationMarkers(float north,float south,
                                          float west,float east)
  {
    return(getLocationMarkers(north,south,west,east,(GeoScreenList)null));
  }

//----------------------------------------------------------------------
/**
 * Returns a list of LocationMarker objects in a GeoScreenList that
 * match the given parameters. This implementation does not use the
 * GeoScreenList passed as parameter!
 *
 * @param north Northern limit for the locations.
 * @param south Southern limit for the locations to retrieve.
 * @param west Western limit for locations to retrieve.
 * @param east Eastern limit for locations to retrieve.
 * @param location_list List to add the new locationmarkers to. If null, the
 *   LocationMarkerSource creates a new GeoScreenList object.
 * @return a list of LocationMarker objects.
 */
  public GeoScreenList getLocationMarkers(float north,float south,
                                          float west,float east,
                                          GeoScreenList location_list)
  {
    if(location_list == null)
      location_list = new GeoScreenList();

    location_list.addAll(quad_tree_.get(north,west,south,east));
    return(location_list);
  }

//----------------------------------------------------------------------
/**
 * Adds a location marker to the source. The source is responsible to
 * make the location marker persistent (@see
 * #writeLocationMarker(LocationMarker)).
 *
 * @param marker the new location marker to ge added.
 * @exception UnsupportedOperationException if the location marker
 * source is read only.
 * @exception LocationMarkerSourceException if the location marker
 * source throws an exception on making the location marker
 * persistent, it is wrapped into a LocationMarkerSourceException
 */
  public void putLocationMarker(LocationMarker marker)
    throws UnsupportedOperationException, LocationMarkerSourceException
  {
    writeLocationMarker(marker);
    quad_tree_.put(marker.getLatitude(),marker.getLongitude(),marker);
  }

//----------------------------------------------------------------------
/**
 * Returns a list of LocationMarker objects in a GeoScreenList that
 * match the given parameters and filter.
 *
 * @param north Northern limit for the locations.
 * @param south Southern limit for the locations to retrieve.
 * @param west Western limit for locations to retrieve.
 * @param east Eastern limit for locations to retrieve.
 * @param filter the filter to use to retrieve the location markers.
 * @param location_list List to add the new locationmarkers to. If null, the
 *   LocationMarkerSource creates a new GeoScreenList object.
 * @return a list of LocationMarker objects.
 * @exception LocationMarkerSourceException if the location marker
 * source throws an exception on making the location marker
 * persistent, it is wrapped into a LocationMarkerSourceException
 * @exception UnsupportedOperationException if the location marker
 * source is read only.
 */
  public GeoScreenList getLocationMarkers(float north,float south,
                                          float west,float east,
                                          LocationMarkerFilter filter,
                                          GeoScreenList location_list)
    throws LocationMarkerSourceException, UnsupportedOperationException
  {
    throw new UnsupportedOperationException("Filter is not supported!");
  }

//----------------------------------------------------------------------
/**
 * Returns a list of LocationMarker objects in a GeoScreenList that
 * match the given parameters and filter.
 *
 * @param north Northern limit for the locations.
 * @param south Southern limit for the locations to retrieve.
 * @param west Western limit for locations to retrieve.
 * @param east Eastern limit for locations to retrieve.
 * @param filter the filter to use to retrieve the location markers.
 * @return a list of LocationMarker objects.
 * @exception LocationMarkerSourceException if the location marker
 * source throws an exception on making the location marker
 * persistent, it is wrapped into a LocationMarkerSourceException
 * @exception UnsupportedOperationException if the location marker
 * source is read only.
 */
  public GeoScreenList getLocationMarkers(float north,float south,
                                          float west,float east,
                                          LocationMarkerFilter filter)
    throws LocationMarkerSourceException, UnsupportedOperationException
  {
    return(getLocationMarkers(north,south,west,east,filter,null));
  }


//----------------------------------------------------------------------
/**
 * If the source supports filters in requests this method must return
 * true, false otherwise. This implementation does not support
 * filters, so it always returns false.
 *
 * @return true, if the source supports filters in requests.
 */
  public boolean supportsFilters()
  {
    return(false);
  }


//----------------------------------------------------------------------
/**
 * Called from the initialize method to fill the location markers.
 *
 * @return a GeoScreenList filled with LocationMarker objects or an
 * empty list, if no markers are found.
 * @exception LocationMarkerSourceException if the location marker
 * source throws an exception on making the location marker
 * persistent, it is wrapped into a LocationMarkerSourceException

*/
  protected abstract GeoScreenList readLocationMarkers()
    throws LocationMarkerSourceException;

//----------------------------------------------------------------------
/**
 * Tells the persisten backend to add a new location marker. This
 * implementation throws an UnsupportedOperationException, so
 * subclasses that do not overwrite this method are read-only!
 *
 * @param marker the new location marker to ge added.
 * @exception UnsupportedOperationException if the location marker
 * source is read only.
 * @exception LocationMarkerSourceException if the location marker
 * source throws an exception on making the location marker
 * persistent, it is wrapped into a LocationMarkerSourceException
 */
  protected void writeLocationMarker(LocationMarker marker)
    throws UnsupportedOperationException, LocationMarkerSourceException
  {
    throw new UnsupportedOperationException("Write operation is not supported by this LocationMarkerSource");
  }
}


