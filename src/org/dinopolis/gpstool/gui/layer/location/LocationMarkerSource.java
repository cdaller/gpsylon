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

import org.dinopolis.gpstool.util.geoscreen.GeoScreenList;

//----------------------------------------------------------------------
/**
 * Implementations of this interface serve as sources for
 * LocationMarkers (e.g. a file, a database, etc.).
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public interface LocationMarkerSource 
{

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
 * @exception LocationMarkerSourceException if the location marker
 * source throws an exception on making the location marker
 * persistent, it is wrapped into a LocationMarkerSourceException
 */
  public GeoScreenList getLocationMarkers(float north,float south,
                                          float west,float east)
    throws LocationMarkerSourceException;

//----------------------------------------------------------------------
/**
 * Returns a list of LocationMarker objects in a GeoScreenList that
 * match the given parameters.
 *
 * @param north Northern limit for the locations.
 * @param south Southern limit for the locations to retrieve.
 * @param west Western limit for locations to retrieve.
 * @param east Eastern limit for locations to retrieve.
 * @param location_list List to add the new locationmarkers to. If null, the
 *   LocationMarkerSource creates a new GeoScreenList object.
 * @return a list of LocationMarker objects.
 * @exception LocationMarkerSourceException if the location marker
 * source throws an exception on making the location marker
 * persistent, it is wrapped into a LocationMarkerSourceException
 */
  public GeoScreenList getLocationMarkers(float north,float south,
                                          float west,float east,
                                          GeoScreenList location_list)
    throws LocationMarkerSourceException;

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
    throws LocationMarkerSourceException, UnsupportedOperationException;

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
    throws LocationMarkerSourceException, UnsupportedOperationException;


//----------------------------------------------------------------------
/**
 * Adds a location marker to the source. The source is responsible to
 * make the location marker persistent.
 *
 * @param marker the new location marker to ge added.
 * @exception UnsupportedOperationException if the location marker
 * source is read only.
 * @exception LocationMarkerSourceException if the location marker
 * source throws an exception on making the location marker
 * persistent, it is wrapped into a LocationMarkerSourceException
 */
  public void putLocationMarker(LocationMarker marker)
    throws UnsupportedOperationException, LocationMarkerSourceException;


//----------------------------------------------------------------------
/**
 * If the source supports filters in requests this method must return
 * true, false otherwise.
 *
 * @return true, if the source supports filters in requests.
 */
  public boolean supportsFilters();


}







