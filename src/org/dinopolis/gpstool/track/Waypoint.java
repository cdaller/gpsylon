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

import org.dinopolis.gpstool.gpsinput.GPSWaypoint;
import org.dinopolis.gpstool.util.geoscreen.GeoScreen;



//----------------------------------------------------------------------
/**
 * This interface represents a waypoint that may be projected from
 * geographical coordinates to screen coordinates and vice versa.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public interface Waypoint extends GPSWaypoint, GeoScreen
{
  //----------------------------------------------------------------------
/**
 * Get the x coordinate on screen.
 *
 * @return the x coordinate on screen.
 */
  public int getX();
  
//----------------------------------------------------------------------
/**
 * Set the x coordinate on screen.
 *
 * @param x the x coordinate on screen.
 */
  public void setX(int x);
  
//----------------------------------------------------------------------
/**
 * Get the y coordinate on screen.
 *
 * @return the y coordinate on screen.
 */
  public int getY();
  
//----------------------------------------------------------------------
/**
 * Set the y coordinate on screen.
 *
 * @param y the y coordinate on screen.
 */
  public void setY(int y);

}

