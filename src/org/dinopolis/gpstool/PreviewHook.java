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
import java.util.Vector;

//----------------------------------------------------------------------
/**
 * This hook is used for hooks offering previews for maps.
 * 
 * @author Christof Dallermassl
 * @version $Revision$
 */

public interface PreviewHook
{

//----------------------------------------------------------------------
/**
 * Sets the rectangle to indicate the area of the map to download.
 *
 * @param download_latitude the latitude of the center of the map to
 * download.
 * @param download_longitude the longitude of the center of the map to
 * download.
 * @param download_scale the scale of the map to download.
 * @param download_image_width the width of the map to download.
 * @param download_image_height the height of the map to download.
 */
  public void setPreviewMap(double download_latitude,
                            double download_longitude,
                            float download_scale,
                            int download_image_width,
                            int download_image_height);

//----------------------------------------------------------------------
/**
 * Hides the preview rectangle.
 *
 */
  public void hidePreviewMaps();

//----------------------------------------------------------------------
/**
 * Sets the rectangles to indicate the area of the maps (multiple!) to
 * download. The maps are held in a vector that contains MapRectangle
 * objects which in turn indicate the nort/east corner, width, height,
 * and scale of the map.
 *
 * @param maps a vector containing MapRectangle objects.
 */
  public void setPreviewMaps(Vector maps);

}

