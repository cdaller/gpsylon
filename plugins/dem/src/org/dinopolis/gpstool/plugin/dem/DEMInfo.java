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

import javax.swing.ImageIcon;

import org.dinopolis.gpstool.map.MapInfo;
import org.dinopolis.gpstool.plugin.dem.mlt.MLT2LandSerf;

import com.bbn.openmap.LatLonPoint;

//----------------------------------------------------------------------
/**
 * Holds the information about dem map (image) 
 * 
 * @author Samuel Benz
 * @version $Revision$
 */

public class DEMInfo extends MapInfo {
	
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

	// ----------------------------------------------------------------------
	/**
	 * Loads the image
	 * 
	 * @return the image object
	 */
	protected Image loadImage() {

		String dem_image = MLT2LandSerf.tmpPath + "/" + MLT2LandSerf.createRasterID(filename_) + ".png";
		ImageIcon image_icon = new ImageIcon(dem_image);
		int status = image_icon.getImageLoadStatus();
		if (status == MediaTracker.ERRORED)
			System.err.println("ERROR: Could not load image '" + dem_image + "'");

//		Image image = image_icon.getImage();
		Image image = makeColorTransparent(image_icon.getImage(),Color.white);
		
		return (image);
	}
	
	protected static Image makeColorTransparent(Image im, final Color color) {

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
