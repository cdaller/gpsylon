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


package org.dinopolis.gpstool.plugin.navigationmousemode;

import com.bbn.openmap.LatLonPoint;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.dinopolis.gpstool.GPSMapKeyConstants;
import org.dinopolis.gpstool.MapNavigationHook;
import org.dinopolis.gpstool.plugin.MouseModePlugin;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.util.Resources;

//----------------------------------------------------------------------
/**
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class NavigationMouseMode implements MouseModePlugin, AWTEventListener
{
  boolean mode_active_;
  MapNavigationHook map_navigation_hook_;
  Cursor zoom_in_cursor_;
  Cursor zoom_out_cursor_;
  int zoom_mode_ = ZOOM_IN_MODE;
  Component component_;
  
  public static final int ZOOM_IN_MODE = 0;
  public static final int ZOOM_OUT_MODE = 1;
  

//----------------------------------------------------------------------
/**
 * Empty constructor
 */
  public NavigationMouseMode()
  {
  }

//----------------------------------------------------------------------
/**
 * Initialize the plugin and pass a PluginSupport that provides
 * objects, the plugin may use.
 *
 * @param support the PluginSupport object
 */
  public void initializePlugin(PluginSupport support)
  {
    map_navigation_hook_ = support.getMapNavigationHook();
    component_ = support.getMapComponent();
    Resources resources = support.getResources();
    ImageIcon zoom_in = (ImageIcon)resources.getIcon(GPSMapKeyConstants.KEY_CURSOR_ZOOM_IN_ICON);
    ImageIcon zoom_out = (ImageIcon)resources.getIcon(GPSMapKeyConstants.KEY_CURSOR_ZOOM_OUT_ICON);
    Toolkit toolkit = component_.getToolkit();
    zoom_out_cursor_ = toolkit.createCustomCursor(zoom_out.getImage(),new Point(5,4),"zoom out");
    zoom_in_cursor_ = toolkit.createCustomCursor(zoom_in.getImage(),new Point(5,4),"zoom in");
  }

  protected void updateZoomCursor(int mode)
  {
    if(!mode_active_)
      return;
    if(mode == ZOOM_IN_MODE)
      component_.setCursor(zoom_in_cursor_);
    else
      component_.setCursor(zoom_out_cursor_);
  }

  // AWTEventListener 
  //----------------------------------------------------------------------
  /**
   * Invoked when a key event occures.
   */

  public void eventDispatched(AWTEvent event)
  {
    if (event instanceof KeyEvent)
      dispatchKeyEvent((KeyEvent)event);
  }

//----------------------------------------------------------------------
/**
 * Invoked when a key has been typed.
 * This event occurs when a key press is followed by a key release.
 *
 * @param event the key event.
 */
  
  public void dispatchKeyEvent(KeyEvent event)
  {
    if (event.isShiftDown())
      updateZoomCursor(ZOOM_OUT_MODE);
    else
      updateZoomCursor(ZOOM_IN_MODE);
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
// MouseMapPlugin methods
//----------------------------------------------------------------------
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
    if(!mode_active_)
    {
      component_.setCursor(Cursor.getDefaultCursor());
      component_.getToolkit().removeAWTEventListener(this);
    }
    else
    {
        // otherwise, no key events come through (ugly, but could not
        // find another solution):
      component_.getToolkit().addAWTEventListener(this,AWTEvent.KEY_EVENT_MASK);
    }
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
    return("Navigation Mode");
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
    return("Zoom In/Out");
  }

//----------------------------------------------------------------------
// Plugin methods
//----------------------------------------------------------------------
//----------------------------------------------------------------------
/**
 * Returns the unique id of the plugin. The id is used to identify
 * the plugin and to distinguish it from other plugins.
 *
 * @return The id of the plugin.
 */

  public String getPluginIdentifier()
  {
    return("NavigationMouseMode");
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
    return(getMouseModeName());
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
    return(getMouseModeDescription());
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
      LatLonPoint point = map_navigation_hook_.getMapProjection().inverse(event.getX(),event.getY());

      if(event.isShiftDown())
      {
        map_navigation_hook_.setMapCenter(point.getLatitude(),point.getLongitude());
        map_navigation_hook_.reScale(2.0f);
      }

      if(event.isControlDown())
      {

      }

      if(event.isAltDown())
      {
      }

          // no modifiers pressed:
      if(!event.isAltDown() && !event.isShiftDown() && !event.isControlDown())
      {
        map_navigation_hook_.setMapCenter(point.getLatitude(),point.getLongitude());
        map_navigation_hook_.reScale(0.5f);
      }
    } // end of if(Button1)
  }

  public void mouseEntered(MouseEvent event)
  {
    if(!mode_active_)
      return;
    Component source = (Component)event.getSource();
    if(event.isShiftDown())
      source.setCursor(zoom_out_cursor_);
    else
      source.setCursor(zoom_in_cursor_);
    
//    System.out.println("mouseEntered: "+event.getSource());
  }

  public void mouseExited(MouseEvent event)
  {
    if(!mode_active_)
      return;
    Component source = (Component)event.getSource();
    source.setCursor(Cursor.getDefaultCursor());
//    System.out.println("mouseExited: "+event.getSource());
  }

  public void mousePressed(MouseEvent event)
  {
    if(!mode_active_)
      return;
    if(event.getButton() == MouseEvent.BUTTON1)
    {
    }
//    System.out.println("mousePressed: "+event.getSource());
  }

  public void mouseReleased(MouseEvent event)
  {
    if(!mode_active_)
      return;
//    System.out.println("mouseReleased: "+event.getSource());
  }


//----------------------------------------------------------------------
// MouseMotionListener Adapter
//----------------------------------------------------------------------

  public void mouseDragged(MouseEvent event)
  {
    if(!mode_active_)
      return;
//     drag_current_x_ = event.getX();
//     drag_current_y_ = event.getY();
//     drag_mode_ = true;
//     download_area_action_.setEnabled(drag_mode_);
//     if((download_frame_ != null) && (download_frame_.isVisible()))
//     {
//       setDownloadFrameParametersForAreaDownload();
//     }
//     mouse_layer_.repaint();
// //    System.out.println("mouseDragged: "+event.getSource());
  }

  public void mouseMoved(MouseEvent event)
  {
    if(!mode_active_)
      return;
//    System.out.println("mouseMoved: "+event.getSource());
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



