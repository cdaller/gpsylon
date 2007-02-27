/***********************************************************************
 * @(#)$RCSfile$ $Revision$
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

package org.dinopolis.gpstool.plugin.dem;


import java.awt.Color;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.lang.ref.SoftReference;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.dinopolis.gpstool.map.MapInfo;
import com.bbn.openmap.LatLonPoint;

//----------------------------------------------------------------------
/**
 * Holds the information about dem map (image) 
 * 
 * @author Samuel Benz
 * @version $Revision$
 */

public class DEMInfo extends MapInfo {
	
	private static Logger logger_ = Logger.getLogger(DEMInfo.class);
	
	public DEMInfo() {
		super();
	}

	public DEMInfo(String filename, double latitude, double longitude,
			float scale, int image_width, int image_height) {
		super(filename, latitude, longitude, scale, image_width, image_height);
	}

	public DEMInfo(String filename, LatLonPoint center, float scale,
			int image_width, int image_height) {
		super(filename, center, scale, image_width, image_height);
	}

	//----------------------------------------------------------------------
	/**
	 * Clear the soft reference to reload the image
	 *
	 */
	public void clearReference() {
	      image_ref_ = null;  // reload image
	} 
	  
//	----------------------------------------------------------------------
	/**
	 * Get the image.
	 *
	 * @return the image or null if the image is not found.
	 */
	  public Image getImage(String filename) 
	  {
	    return getImage(filename,null);
	  }
	    
//	----------------------------------------------------------------------
	/**
	 * Get the image.
	 *
	 * @return the image or null if the image is not found.
	 */
	  public Image getImage(String filename,Color color) 
	  {
	    if(image_ref_ == null)  // first call to this method
	    {
	      image_ref_ = new SoftReference(loadImage(filename,color));
	    }
	    
	    Image image = (Image)image_ref_.get();  // image_ref_ is a soft reference!
	    
	    if(image == null) // image was garbage collected, so reload the image:
	    {
	      if(logger_.isDebugEnabled())
	        logger_.debug("image "+ filename + " will be loaded (weak reference worked).");
	      image_ref_ = new SoftReference(loadImage(filename,color));
	      image = (Image)image_ref_.get();  
	    }
	    return (image);
	  }
	   
//	----------------------------------------------------------------------
	/**
	 * Loads the image and set one color transparent
	 *
	 * @return the image object
	 * @param filename the filename
	 * @param color the color to set transparent in the image
	 */
	  protected Image loadImage(String filename,Color color)
	  {
//	    image_ = Toolkit.getDefaultToolkit().getImage(filename_);
	    ImageIcon image_icon;
	    Image image;
	    if(image_url_ != null)
	      image_icon = new ImageIcon(image_url_);
	    else
	      image_icon = new ImageIcon(filename);
	    int status = image_icon.getImageLoadStatus();
	    if (status == MediaTracker.ERRORED)
	      logger_.error("ERROR: Could not load image '"+filename+"'");
	    
	    if(color == null){
	    	image = image_icon.getImage();
	    }else{
	    	image = makeColorTransparent(image_icon.getImage(),color);
	    }
	    	
	    if(logger_.isDebugEnabled())
	        logger_.debug("Image '"+filename+"' loaded.");
	    return(image);
	  }
	   
//	----------------------------------------------------------------------
	/**
	 * Set one color in an image transparent
	 *
	 * @return the transparent image object
	 * @param the source image
	 * @param color the color to set transparent
	 */
	  protected static Image makeColorTransparent(Image im, final Color color)
	  {

		ImageFilter filter = new RGBImageFilter() {
			// the color we are looking for... Alpha bits are set to opaque
			public int markerRGB = color.getRGB() | 0xFF000000;

			public final int filterRGB(int x, int y, int rgb) {
				if ((rgb | 0xFF000000) == markerRGB) {
					// Mark the alpha bits as zero - transparent
					return 0x00FFFFFF & rgb;
				} else {
					// nothing to do
					return rgb;
				}
			}
		};

		ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
		return Toolkit.getDefaultToolkit().createImage(ip);
	  }

}
