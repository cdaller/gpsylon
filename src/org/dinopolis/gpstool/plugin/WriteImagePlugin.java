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


package org.dinopolis.gpstool.plugin;

import java.io.IOException;
import java.awt.Component;
import java.io.OutputStream;

//----------------------------------------------------------------------
/**
 * This plugin writes the content of a java AWT component in a
 * specific format (e.g. jpeg, svg, ...) to a stream. It therefore may
 * be used to store a screenshot in a file or to view the content of
 * the window in a dynamic webpage.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public interface WriteImagePlugin extends Plugin
{

//----------------------------------------------------------------------
/**
 * Returns the content type of the data the plugin produces
 * (e.g. image/jpeg, ...).
 *
 * @return The content type of the data.
 */

  public String getContentType();
  
//----------------------------------------------------------------------
/**
 * Returns a short description of the content that may be used e.g. in
 * a file chooser. If possible, the description should be localized.
 *
 * @return The short description of the content.
 */

  public String getContentDescription();
  
//----------------------------------------------------------------------
/**
 * Returns possible file extensions the the content. This information
 * may be used in a file chooser as a filter (e.g. ["jpg","jpeg"]).
 *
 * @return The file extensions to use for this data.
 */

  public String[] getContentFileExtensions();


// //----------------------------------------------------------------------
// /**
//  * Initializes the plugin with the component to
//  *
//  * @param out the outputstream to write the data to.
//  * @throws IOException if an error occurs during writing.
//  */
//   public void initialize();

//----------------------------------------------------------------------
/**
 * Writes the data to the given output stream.
 *
 * @param component the component to write.
 * @param out the outputstream to write the data to.
 * @throws IOException if an error occurs during writing.
 */
  public void write(Component component, OutputStream out)
    throws IOException;
}


