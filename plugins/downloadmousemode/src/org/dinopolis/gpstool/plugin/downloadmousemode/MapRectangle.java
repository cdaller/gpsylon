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


package org.dinopolis.gpstool.plugin.downloadmousemode;

import java.awt.Dimension;
import org.dinopolis.gpstool.util.geoscreen.GeoScreenPoint;

//----------------------------------------------------------------------
/**
 * This class is simliar to java.awt.Rectangle with the exception that
 * the center of the map is given (in latitude/longitude).
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class MapRectangle extends GeoScreenPoint
{
  Dimension dimension_;
  float scale_;
  

  public MapRectangle(float latitude, float longitude, int width, int height, float scale)
  {
    super(latitude,longitude);
    dimension_ = new Dimension(width, height);
    scale_ = scale;
  }
  
//----------------------------------------------------------------------
/**
 * Get the dimension (in pixels).
 *
 * @return the dimension (in pixels).
 */
  public Dimension getDimension() 
  {
    return (dimension_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the dimension (in pixels).
 *
 * @param dimension the dimension (in pixels).
 */
  public void setDimension(Dimension dimension) 
  {
    dimension_ = dimension;
  }
  
//----------------------------------------------------------------------
/**
 * Get the width (in pixels).
 *
 * @return the width (in pixels).
 */
  public double getWidth() 
  {
    return (dimension_.getWidth());
  }
  
//----------------------------------------------------------------------
/**
 * Get the height (in pixels).
 *
 * @return the height (in pixels).
 */
  public double getHeight() 
  {
    return (dimension_.getHeight());
  }
  
//----------------------------------------------------------------------
/**
 * Get the scale.
 *
 * @return the scale.
 */
  public float getScale() 
  {
    return (scale_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the scale.
 *
 * @param scale the scale.
 */
  public void setScale(float scale) 
  {
    scale_ = scale;
  }
  
}


