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

import java.awt.Image;
import java.awt.MediaTracker;
import java.lang.ref.SoftReference;

import javax.swing.ImageIcon;

import org.dinopolis.util.Debug;

import com.bbn.openmap.LatLonPoint;

//----------------------------------------------------------------------
/**
 * Holds information about maps to be used (like coordinates of the
 * center of the map, its size, etc.).
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class MapInfo implements Comparable
{
  protected LatLonPoint center_;
  protected float scale_;
  protected String filename_;
  protected int width_;
  protected int height_;
  protected SoftReference image_ref_;
  

//----------------------------------------------------------------------
/**
 * Constructor
 */

  public MapInfo()
  {
  }

//----------------------------------------------------------------------
/**
 * Constructor
 */

  public MapInfo(String filename, double latitude, double longitude, float scale,
                 int image_width, int image_height)
  {
    this(filename,new LatLonPoint((float)latitude,(float)longitude),scale,image_width,image_height);
  }

//----------------------------------------------------------------------
/**
 * Constructor
 */

  public MapInfo(String filename, LatLonPoint center, float scale,
                 int image_width, int image_height)
  {
    filename_ = filename;
    center_ = center;
    scale_ = scale;
    width_ = image_width;
    height_ = image_height;
  }

//----------------------------------------------------------------------
/**
 * Returns a string represenation of this point.
 * @return a string represenation of this point.
 */

  public String toString()
  {
    return(filename_+"(" + "center: "+center_ + ", 1:"+scale_+" w/h: "+width_+"/"+height_);
  }

//----------------------------------------------------------------------
/**
 * Get the value of scale.
 *
 * @return value of scale.
 */
  public float getScale()
  {
    return (scale_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the value of scale.
 *
 * @param the scale.
 */
  public void setScale(float scale)
  {
    scale_ = scale;
  }

//----------------------------------------------------------------------
/**
 * Get the filename.
 *
 * @return the filename.
 */
  public String getFilename() 
  {
    return (filename_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the filename.
 *
 * @param filename the filename.
 */
  public void setFilename(String filename) 
  {
    if(filename_ != filename)
      image_ref_ = null;  // new file
    filename_ = filename;
  }
  
  
//----------------------------------------------------------------------
/**
 * Get the center.
 *
 * @return the center.
 */
  public LatLonPoint getCenter() 
  {
    return (center_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the center.
 *
 * @param center the center.
 */
  public void setCenter(LatLonPoint center) 
  {
    center_ = center;
  }

//----------------------------------------------------------------------
/**
 * Get the latitude.
 *
 * @return the latitude.
 */
  public double getLatitude() 
  {
    return (center_.getLatitude());
  }
  
//----------------------------------------------------------------------
/**
 * Set the latitude.
 *
 * @param latitude the latitude.
 */
  public void setLatitude(double latitude) 
  {
    if(center_ == null)
      center_ = new LatLonPoint();
    center_.setLatitude((float)latitude);
  }
  
  
//----------------------------------------------------------------------
/**
 * Get the longitude.
 *
 * @return the longitude.
 */
  public double getLongitude() 
  {
    return (center_.getLongitude());
  }
  
//----------------------------------------------------------------------
/**
 * Set the longitude.
 *
 * @param longitude the longitude.
 */
  public void setLongitude(double longitude) 
  {
    if(center_ == null)
      center_ = new LatLonPoint();
    center_.setLongitude((float)longitude);
  }
  

//----------------------------------------------------------------------
/**
 * Get the height.
 *
 * @return the height.
 */
  public int getHeight() 
  {
    return (height_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the height.
 *
 * @param height the height.
 */
  public void setHeight(int height) 
  {
    height_ = height;
  }

  
//----------------------------------------------------------------------
/**
 * Get the width.
 *
 * @return the width.
 */
  public int getWidth() 
  {
    return (width_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the width.
 *
 * @param width the width.
 */
  public void setWidth(int width) 
  {
    width_ = width;
  }

//----------------------------------------------------------------------
/**
 * Get the image.
 *
 * @return the image.
 */
  public Image getImage() 
  {
    if(image_ref_ == null)  // first call to this method
    {
      image_ref_ = new SoftReference(loadImage());
    }
    
    Image image = (Image)image_ref_.get();  // image_ref_ is a soft reference!
    
    if(image == null) // image was garbage collected, so reload the image:
    {
      if(Debug.DEBUG)
        Debug.println("GPSMap_MapInfo","image "+getFilename()
                      +" will be loaded (weak reference worked).");
      image_ref_ = new SoftReference(loadImage());
      image = (Image)image_ref_.get();  
    }
    return (image);
  }

//----------------------------------------------------------------------
/**
 * Loads the image
 *
 * @return the image object
 */
  protected Image loadImage()
  {
//    image_ = Toolkit.getDefaultToolkit().getImage(filename_);

    ImageIcon image_icon = new ImageIcon(filename_);
    int status = image_icon.getImageLoadStatus();
    if (status == MediaTracker.ERRORED)
      System.err.println("ERROR: Could not load image '"+filename_+"'");
    
    Image image = image_icon.getImage();
    
    if(Debug.DEBUG && Debug.isEnabled("MultipleImagePanel_loadimage"))
    {
      Debug.println("Image '"+filename_+"' loaded.");
    }
    return(image);
  }

  
	/**
	 * Compares the filesnames of the given map info object to the filename of
	 * this map info.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object other)
	{
	  return(((MapInfo)other).getFilename().compareTo(getFilename()));
	}

}
