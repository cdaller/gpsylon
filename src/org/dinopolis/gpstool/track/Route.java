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

import org.dinopolis.gpstool.gpsinput.GPSRoute;
import org.dinopolis.gpstool.util.geoscreen.GeoScreen;



//----------------------------------------------------------------------
/**
 * This interface represents a route that may be projected from
 * geographical coordinates to screen coordinates and vice versa.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public interface Route extends GPSRoute, GeoScreen
{

//----------------------------------------------------------------------
/**
 * Return the projected routepoint at the given position. This method
 * returns the same object as the {@link #getWaypoint(int)} method but
 * allows convenient access (without cast to {@link
 * Waypoint}) to the returned waypoint.
 *
 * @return the routepoint.
 *
 * @throws IndexOutofBoundsException if the index is out of range
 * (index < 0 || index >= size()).
 */
  public Waypoint getProjectedWaypoint(int position)
    throws IndexOutOfBoundsException;

//----------------------------------------------------------------------
/**
 * Returns the minimal horizontal screen coordinates for the
 * projection used. This value is only valid after the projection was
 * done.
 *
 * @return minimal x value.
 * @see GeoScreen#forward(com.bbn.openmap.Projection)
 */

  public int getMinX();

//----------------------------------------------------------------------
/**
 * Returns the maximal horizontal screen coordinates for the
 * projection used. This value is only valid after the projection was
 * done.
 *
 * @return maximal x value.
 * @see GeoScreen#forward(com.bbn.openmap.Projection)
 */

  public int getMaxX();

//----------------------------------------------------------------------
/**
 * Returns the minimal horizontal screen coordinates for the
 * projection used. This value is only valid after the projection was
 * done.
 *
 * @return minimal y value.
 * @see GeoScreen#forward(com.bbn.openmap.Projection)
 */

  public int getMinY();

//----------------------------------------------------------------------
/**
 * Returns the mayimal horizontal screen coordinates for the
 * projection used. This value is only valid after the projection was
 * done.
 *
 * @return mayimal y value.
 * @see GeoScreen#forward(com.bbn.openmap.Projection)
 */

  public int getMaxY();

}


