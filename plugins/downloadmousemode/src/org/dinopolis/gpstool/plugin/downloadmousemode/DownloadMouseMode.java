/***********************************************************************
 * @(#)$RCSfile$   $Revision$$Date$
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


package org.dinopolis.gpstool.plugin.downloadmousemode;

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.Layer;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import org.dinopolis.gpstool.plugin.MouseModePlugin;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.util.geoscreen.GeoScreenPoint;
import org.dinopolis.util.Resources;

//----------------------------------------------------------------------
/**
 * The mouse mode for the download layer. This mouse mode gets the
 * mouse events and tells the layer to draw one or more rectangles as
 * a preview.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class DownloadMouseMode implements MouseModePlugin
{
  DownloadMouseModeLayer download_layer_;
  boolean mode_active_;
  Resources resources_;


//----------------------------------------------------------------------
/**
 * Creates a new <code>DownloadMouseMode</code> instance.
 */
  public DownloadMouseMode()
  {
  }

//----------------------------------------------------------------------
// Plugin Methods
// ----------------------------------------------------------------------
  
//----------------------------------------------------------------------
/**
 * Initialize the plugin and pass a PluginSupport that provides
 * objects, the plugin may use.
 *
 * @param support the PluginSupport object
 */
  public void initializePlugin(PluginSupport support)
  {
    download_layer_ = new DownloadMouseModeLayer();
    download_layer_.initializePlugin(support);
    resources_ = support.getResources();
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
    return(resources_.getString(DownloadMouseModeLayer.KEY_DOWNLOADMOUSEMODE_PLUGIN_IDENTIFIER));
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
    return((float)resources_.getDouble(DownloadMouseModeLayer.KEY_DOWNLOADMOUSEMODE_PLUGIN_VERSION));
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
    return(resources_.getString(DownloadMouseModeLayer.KEY_DOWNLOADMOUSEMODE_PLUGIN_NAME));
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
    return(resources_.getString(DownloadMouseModeLayer.KEY_DOWNLOADMOUSEMODE_PLUGIN_DESCRIPTION));
  }

//----------------------------------------------------------------------
/**
 * If the mouse mode plugin wants to draw anything on the map it may
 * return a layer here or <code>null</code> if not.
 *
 * @return the layer the plugin wants to paint into.
 * @see com.bbn.openmap.Layer
 */

  public Layer getLayer()
  {
    return(download_layer_);
  }

  
//----------------------------------------------------------------------
// MouseMode methods
// ----------------------------------------------------------------------
//----------------------------------------------------------------------
/**
 * Called by the application to switch the mouse mode on or off. If
 * the mouse mode is switched off, it must not react on mouse events
 * (although it might register them). This method may be used to
 * change the mouse cursor, ...
 *
 * @param active if <code>true</code> the mouse mode is switched on
 * and should react on mouse events.
 */

  public void setActive(boolean active)
  {
    mode_active_ = active;
    download_layer_.setMouseModeActive(active);
  }

//----------------------------------------------------------------------
/**
 * Returns if the mouse mode is active or not.
 *
 * @return <code>true</code> if the mouse mode is active and reacts on
 * mouse events.
 */
  public boolean isActive()
  {
    return(mode_active_);
  }

//----------------------------------------------------------------------
/**
 * The name returned here is used in the menu and/or the toolbar of
 * the application to switch the mouse mode on or off. It should be
 * localized.
 *
 * @return the name of the mouse mode.
 */
  public String getMouseModeName()
  {
    return("Download Maps");
  }

//----------------------------------------------------------------------
/**
 * The icon returned here is used in the menu and/or the toolbar of
 * the application to switch the mouse mode on or off. 
 *
 * @return the icon of the mouse mode.
 */
  public Icon getMouseModeIcon()
  {
    return(null);
  }


//----------------------------------------------------------------------
/**
 * The description returned here is used in the menu and/or the toolbar of
 * the application to switch the mouse mode on or off. 
 *
 * @return the description of the mouse mode.
 */
  public String getMouseModeDescription()
  {
    return("Download Maps from Internet Servers");
  }

//----------------------------------------------------------------------
/**
 * Returns the mnemonic character that is used for manual (keyboard)
 * selection in a menu. If possible, it should be the first letter of
 * the name (default).
 *
 * @return a string describing the mnemonic character for this mouse
 * mode when used in a menu.
 */
  public char getMouseModeMnemonic()
  {
    return('D');
  }


//----------------------------------------------------------------------
/**
 * Returns the accelerator key that is used for the mouse mode in the
 * menu or toolbar. The format of the key strings is described in
 * {@link javax.swing.Keystroke#getKeyStroke(java.lang.String)}. Some
 * examples are given: <code>INSERT</code>,<code>controle
 * DELETE</code>,<code>alt shift X</code>,<code>shift
 * F</code>.
 *
 * @return a string describing the accelerator key.
 */
  public String getMouseModeAcceleratorKey()
  {
    return("control D");
  }

//----------------------------------------------------------------------
// MouseListener Adapter
//----------------------------------------------------------------------

  public void mouseClicked(MouseEvent event)
  {
    if(!mode_active_)
      return;
//    System.out.println("mouseClicked: "+event.getSource());

    if(event.getButton() == MouseEvent.BUTTON1)
    {
          // no modifiers pressed:
      if(!event.isAltDown() && !event.isShiftDown() && !event.isControlDown())
      {
        download_layer_.setMouseDragStart(event.getPoint());
        download_layer_.setMouseDragEnd(null);
      }
    } // end of if(Button1)
  }

  public void mouseEntered(MouseEvent event)
  {
  }

  public void mouseExited(MouseEvent event)
  {
  }

  public void mousePressed(MouseEvent event)
  {
    if(!mode_active_)
      return;

    if(event.getButton() == MouseEvent.BUTTON1)
    {
          // no modifiers pressed:
      if(!event.isAltDown() && !event.isShiftDown() && !event.isControlDown())
      {
        download_layer_.setMouseDragStart(event.getPoint());
        download_layer_.setMouseDragEnd(null);
      }
    } // end of if(Button1)
  }

  public void mouseReleased(MouseEvent event)
  {
  }


//----------------------------------------------------------------------
// MouseMotionListener Adapter
//----------------------------------------------------------------------

  public void mouseDragged(MouseEvent event)
  {
    if(!mode_active_)
      return;
    download_layer_.setMouseDragEnd(event.getPoint());
  }

  public void mouseMoved(MouseEvent event)
  {
  }

//----------------------------------------------------------------------
// MouseWheelListener
// available only in jdk 1.4, so not used at the moment
//----------------------------------------------------------------------

//    void mouseWheelMoved(MouseWheelEvent event)
//     {
//       System.out.println("mouseWheelMoved: "+event.getSource());
//     }


}
