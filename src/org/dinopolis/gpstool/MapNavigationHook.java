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
import com.bbn.openmap.proj.Projection;

//----------------------------------------------------------------------
/**
 * This hook is used for hooks concerning navigation in the map. 
 * 
 * @author Christof Dallermassl
 * @version $Revision$
 */

public interface MapNavigationHook 
{

//----------------------------------------------------------------------
/**
 * Sets the new center of the map.
 *
 * @param latitude The latitude of the new center of the map
 * @param longitude The longitude of the new center of the map
 */

  public void setMapCenter(double latitude, double longitude);

//----------------------------------------------------------------------
/**
 * Rescales the map by a given factor. A factor of 1.0 leaves the map
 * unchanged. A factor greater 1.0 zooms in, a factor less than 1.0
 * zooms out.
 *
 * @param scale_factor the scale factor.
 */

  public void reScale(float scale_factor);

//----------------------------------------------------------------------
/**
 * Rescales the map by to a given scale.
 *
 * @param scale the scale
 */

  public void setScale(float scale);
  
//----------------------------------------------------------------------
/**
 * Sets a new center for the map. A negative factor moves the center
 * up or left, a postive factor down or right. A factor of 1.0
 * translates the center a complete height/width down or right.
 *
 * @param factor_x the horizontal factor to recenter the map.
 * @param factor_y the vertical factor to recenter the map.
 */

  public void translateMapCenterRelative(float factor_x, float factor_y);

//----------------------------------------------------------------------
/**
 * Returns the currently used projection of the map. This projection
 * may be used to calculate the latititude/longitude from screen
 * coordinates and vice versa.
 *
 * @return the projection currently used.
 * @see com.bbn.openmap.proj.Projection
 */
  public Projection getMapProjection();

}




