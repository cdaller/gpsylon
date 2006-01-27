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


package org.dinopolis.gpstool.gui.util;

import java.awt.Rectangle;
import java.awt.Image;

import org.dinopolis.gpstool.map.MapInfo;

//----------------------------------------------------------------------
/**
 * ImageInfo class holds information about images and its
 * visible part.
 */

public class ImageInfo  
{
  int x_;
  int y_;
  Rectangle visible_rectangle_;
  float scale_factor_;
  MapInfo map_info_;

//----------------------------------------------------------------------
/**
 * Constructor DO NOT USE, ONLY FOR TESTING PURPOSES. WILL NOT WORK!!!!!
 */
  protected ImageInfo(String name, int x, int y, int width, int height, float scale_factor)
  {
    this(new MapInfo(name,0.0,0.0,0.0f,width,height),x,y,scale_factor);
  }

  
//----------------------------------------------------------------------
/**
 * Constructor
 */
  public ImageInfo(MapInfo map_info,int x,int y, float scale_factor)
  {
    map_info_ = map_info;
    x_ = x;
    y_ = y;
    scale_factor_ = scale_factor;
  }
    
//----------------------------------------------------------------------
/**
 * Constructor
 */
  public ImageInfo(ImageInfo info)
  {
    map_info_ = info.map_info_;
    x_ = info.x_;
    y_ = info.y_;
    scale_factor_ = info.scale_factor_;
    map_info_ = info.map_info_;
    visible_rectangle_ = info.visible_rectangle_;
  }
    
//----------------------------------------------------------------------
/**
 * Get the map_info.
 *
 * @return the map_info.
 */
  public MapInfo getMapInfo() 
  {
    return (map_info_);
  }
    
//----------------------------------------------------------------------
/**
 * Set the map_info.
 *
 * @param map_info the map_info.
 */
  public void setMapInfo(MapInfo map_info) 
  {
    map_info_ = map_info;
  }
    
//----------------------------------------------------------------------
/**
 * Get the image.
 *
 * @return the image.
 */
  public Image getImage() 
  {
    return (map_info_.getImage());
  }
    
//----------------------------------------------------------------------
/**
 * Get the x coordinate of the image (top/left).
 *
 * @return the x coordinate of the image (top/left).
 */
  public int getX() 
  {
    return (x_);
  }
    
//----------------------------------------------------------------------
/**
 * Set the x coordinate of the image (top left).
 *
 * @param x the x coordinate of the image (top left).
 */
  public void setX(int x) 
  {
    x_ = x;
  }

//----------------------------------------------------------------------
/**
 * Get the y coordinate of the image (top/left).
 *
 * @return the y coordinate of the image (top/left).
 */
  public int getY() 
  {
    return (y_);
  }
    
//----------------------------------------------------------------------
/**
 * Set the y coordinate of the image (top left).
 *
 * @param y the y coordinate of the image (top left).
 */
  public void setY(int y) 
  {
    y_ = y;
  }

//----------------------------------------------------------------------
/**
 * Get the unscaled width of the image. 
 *
 * @return the unscaled width of the image.
 */
  public int getUnscaledWidth() 
  {
    return (map_info_.getWidth());
  }
    

//----------------------------------------------------------------------
/**
 * Get the unscaled height of the image. 
 *
 * @return the unscaled height of the image.
 */
  public int getUnscaledHeight() 
  {
    return (map_info_.getHeight());
  }
    
//----------------------------------------------------------------------
/**
 * Get the scaled width of the image. This is the width, the image
 * will be drawn on screen (so it is relativ to the scale factor).
 *
 * @return the scaled width of the image.
 */
  public int getWidth() 
  {
    return ((int)(map_info_.getWidth() * scale_factor_));
  }
    

//----------------------------------------------------------------------
/**
 * Get the scaled height of the image. This is the height, the image
 * will be drawn on screen (so it is relativ to the scale factor).
 *
 * @return the scaled height of the image.
 */
  public int getHeight() 
  {
    return ((int)(map_info_.getHeight() * scale_factor_));
  }
    
//----------------------------------------------------------------------
/**
 * Get the visible_rectangle.
 *
 * @return the visible_rectangle.
 */
  public Rectangle getVisibleRectangle() 
  {
    return (visible_rectangle_);
  }
    
//----------------------------------------------------------------------
/**
 * Set the visible_rectangle.
 *
 * @param visible_rectangle the visible_rectangle.
 */
  public void setVisibleRectangle(Rectangle visible_rectangle) 
  {
    visible_rectangle_ = visible_rectangle;
  }
    
//----------------------------------------------------------------------
/**
 * Set the visible_rectangle.
 *
 * @param x left coordinate
 * @param y top coordinate
 * @param width width of visible rectangle
 * @param height height of visible rectangle
 */
  public void setVisibleRectangle(int x, int y, int width, int height) 
  {
    visible_rectangle_ = new Rectangle(x,y,width,height);
  }
    
//----------------------------------------------------------------------
/**
 * Get the x coordinate of the visible rectangle of the image (top/left).
 *
 * @return the x coordinate of the visible rectangle of the image (top/left).
 */
  public int getVisibleRectangleX() 
  {
    return ((int)visible_rectangle_.getX());
  }
    
//----------------------------------------------------------------------
/**
 * Get the y coordinate of the visible rectangle of the image (top/left).
 *
 * @return the y coordinate of the visible rectangle of the image (top/left).
 */
  public int getVisibleRectangleY() 
  {
    return ((int)visible_rectangle_.getY());
  }
    
//----------------------------------------------------------------------
/**
 * Get the width of the visible rectangle of the image (top/left).
 *
 * @return the width of the visible rectangle of the image (top/left).
 */
  public int getVisibleRectangleWidth() 
  {
    return ((int)visible_rectangle_.getWidth());
  }
    
//----------------------------------------------------------------------
/**
 * Get the height of the visible rectangle of the image (top/left).
 *
 * @return the height of the visible rectangle of the image (top/left).
 */
  public int getVisibleRectangleHeight() 
  {
    return ((int)visible_rectangle_.getHeight());
  }
    
//----------------------------------------------------------------------
/**
 * Get the scale_factor.
 *
 * @return the scale_factor.
 */
  public float getScaleFactor() 
  {
    return (scale_factor_);
  }
    
//----------------------------------------------------------------------
/**
 * Set the scale_factor.
 *
 * @param scale_factor the scale_factor.
 */
  public void setScaleFactor(float scale_factor) 
  {
    scale_factor_ = scale_factor;
  }


//----------------------------------------------------------------------
/**
 * Return a string representation of this object.
 *
 * @return a string representation of this object.
 */
  public String toString()
  {
    return("ImageInfo [x=" + x_ + " y=" + y_+ " width=" + getWidth() + " height="
           + getHeight() +", map=" + map_info_
           + ", visible=" + visible_rectangle_ +"]");
  }
  
    
}


