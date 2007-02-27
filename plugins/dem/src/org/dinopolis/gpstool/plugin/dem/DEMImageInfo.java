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

import org.dinopolis.gpstool.gui.util.ImageInfo;
import org.dinopolis.gpstool.map.MapInfo;

//----------------------------------------------------------------------
/**
 * DEM Image Info 
 * 
 * @author Samuel Benz
 * @version $Revision$
 */

public class DEMImageInfo extends ImageInfo {

	DEMInfo map_info_;
    
	//	----------------------------------------------------------------------
	/**
	 * Constructor
	*/
	public DEMImageInfo(MapInfo map_info,int x,int y, float scale_factor)
	{
		super(map_info,x,y,scale_factor);
		map_info_ = (DEMInfo) super.getMapInfo();
	}
	    
    //	----------------------------------------------------------------------
	/**
	 * Constructor
	*/
	public DEMImageInfo(ImageInfo info)
	{
		super(info);
		map_info_ = (DEMInfo) super.getMapInfo();
	}

    //	----------------------------------------------------------------------
	/**
	 * Get the map_info.
	 *
	 * @return the map_info.
	*/
	public DEMInfo getDEMInfo() 
	{
	    return (map_info_);
	}
	       
	public Image getImage() 
	{
	    return getImage(map_info_.getFilename(),null);
	}
	      
	//----------------------------------------------------------------------
	/**
	 * Get the image by filename.
	 *
	 * @return the image.
	 * @param filename_ the filename
	*/
	public Image getImage(String filename_) 
	{
	    return (map_info_.getImage(filename_,null));
	}
	   
	//----------------------------------------------------------------------
	/**
	 * Get the image by filename and set one color transparent
	 *
	 * @return the image.
	 * @param filename_ the filename
	 * @param color the color to set transparent in the image
	*/
	public Image getImage(String filename_,Color color) 
	{
	    return (map_info_.getImage(filename_,color));
	}
	
}
