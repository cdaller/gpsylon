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


package org.dinopolis.gpstool.plugin.writerasterimage;


//----------------------------------------------------------------------
/**
 * This plugin writes the content of a component as a png image
 * 
 * @author Christof Dallermassl
 * @version $Revision$
 * @see org.dinopolis.gpstool.plugin.writerasterimage.WriteRasterImagePlugin
 */

public class WritePngImagePlugin extends WriteRasterImagePlugin
{

//----------------------------------------------------------------------
/**
 * Default Constructor.
 *
 */
  public WritePngImagePlugin()
  {
  }

//----------------------------------------------------------------------
/**
 * Returns "png".
 *
 * @return The format of the image.
 * @see org.dinopolis.gpstool.plugin.writerasterimage.WriteRasterImagePlugin#getImageFormat()
 */
  protected String getImageFormat()
  {
    return("png");
  }
  
}


