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

import org.dinopolis.gpstool.plugin.WriteImagePlugin;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.Component;
import java.awt.Graphics2D;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import org.dinopolis.gpstool.plugin.PluginSupport;

//----------------------------------------------------------------------
/**
 * This plugin writes the content of a component as a raster image
 * format to a stream. It therefore may be used to store a screenshot
 * in a file or to view the content of the window in a dynamic
 * webpage. It uses the {@link javax.imageio.ImageIO} class.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public abstract class WriteRasterImagePlugin implements WriteImagePlugin
{

//----------------------------------------------------------------------
/**
 * Initialize the plugin and pass a PluginSupport that provides
 * objects, the plugin may use.
 *
 * @param support the PluginSupport object
 */
  public void initializePlugin(PluginSupport support)
  {
  }

//----------------------------------------------------------------------
/**
 * The application calls this method to indicate that the plugin is
 * activated and will be used from now on. The Plugin should
 * initialize any needed resources (files, etc.) in this method.
 *
 * @throws Exception if an error occurs. If this method throws an
 * exception, the plugin will not be used by the application.
 */

  public void startPlugin()
    throws Exception
  {
  }

//----------------------------------------------------------------------
/**
 * The application calls this method to indicate that the plugin is
 * deactivated and will not be used any more. The Plugin should
 * release all resources (close files, etc.) in this method.
 *
 * @throws Exception if an error occurs.
 */

  public void stopPlugin()
    throws Exception
  {
  }
//----------------------------------------------------------------------
/**
 * Returns the unique id of the plugin. The id is used to identify
 * the plugin and to distinguish it from other plugins.
 *
 * @return The id of the plugin.
 */

  public String getPluginIdentifier()
  {
    return("WriteRasterImage"+getImageFormat());
  }

//----------------------------------------------------------------------
/**
 * Returns the version of the plugin. The version may be used to
 * choose between different version of the same plugin. 
 *
 * @return The version of the plugin.
 */

  public float getPluginVersion()
  {
    return(1.0f);
  }

//----------------------------------------------------------------------
/**
 * Returns the name of the Plugin. The name should be a human
 * readable and understandable name like "Save Image as JPEG". It is
 * prefereable but not necessary that the name is localized. 
 *
 * @return The name of the plugin.
 */

  public String getPluginName()
  {
    return("Save Image as "+getImageFormat());
  }
  

//----------------------------------------------------------------------
/**
 * Returns a description of the Plugin. The description should be
 * human readable and understandable like "This plugin saves the
 * content of the main window as an image in jpeg format". It is
 * prefereable but not necessary that the description is localized. 
 *
 * @return The description of the plugin.
 */

  public String getPluginDescription()
  {
    return("Saves an image in "+getImageFormat()+" format.");
  }

//----------------------------------------------------------------------
/**
 * Returns the format of the images in the form of the
 * <code>javax.imageio.ImageIO.write()</code> method.
 *
 * @return The format of the image.
 */
  protected abstract String getImageFormat();
  
//----------------------------------------------------------------------
/**
 * Returns the content type of the data the plugin produces
 * (e.g. image/jpeg, ...).
 *
 * @return The content type of the data.
 */

  public String getContentType()
  {
    return("image/"+getImageFormat());
  }
  
  
//----------------------------------------------------------------------
/**
 * Returns a short description of the content that may be used e.g. in
 * a file chooser. If possible, the description should be localized.
 *
 * @return The short description of the content.
 */

  public String getContentDescription()
  {
    return(getImageFormat().toUpperCase() + " Image");
  }
  
//----------------------------------------------------------------------
/**
 * Returns possible file extensions the the content. This information
 * may be used in a file chooser as a filter (e.g. ["jpg","jpeg"]).
 *
 * @return The file extensions to use for this data.
 */

  public String[] getContentFileExtensions()
  {
    return(new String[] {getImageFormat()});
  }

//----------------------------------------------------------------------
/**
 * Returns an image of the component.
 *
 * @return an image of the component.
 */
  public static BufferedImage getComponentScreenShot(Component component)
  {
    BufferedImage image = (BufferedImage)component.createImage(component.getWidth(),component.getHeight());
    Graphics2D g2 = image.createGraphics();
    component.paint(g2);
    return (image);
  }


//----------------------------------------------------------------------
/**
 * Writes the data to the given output stream.
 *
 * @param component the component to write.
 * @param out the outputstream to write the data to.
 * @throws IOException if an error occurs during writing.
 */
  public void write(Component component, OutputStream out)
    throws IOException
  {
    BufferedImage image = getComponentScreenShot(component);
    ImageIO.write(image,getImageFormat(),out);
  }
}


