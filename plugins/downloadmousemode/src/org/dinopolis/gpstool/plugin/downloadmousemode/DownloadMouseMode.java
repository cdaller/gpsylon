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

import org.dinopolis.gpstool.util.geoscreen.GeoScreenPoint;
import org.dinopolis.gpstool.gui.MouseMode;
import java.awt.event.MouseEvent;
import com.bbn.openmap.LatLonPoint;
import javax.swing.Icon;




//----------------------------------------------------------------------
/**
 * The mouse mode for the download layer. This mouse mode gets the
 * mouse events and tells the layer to draw one or more rectangles as
 * a preview.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

class DownloadMouseMode implements MouseMode
{
  DownloadMouseModeLayer download_layer_;
  boolean mode_active_;


//----------------------------------------------------------------------
/**
 * Creates a new <code>DownloadMouseMode</code> instance.
 *
 * @param download_layer the <code>DownloadMouseModeLayer</code> this
 * mouse mode belongs to.
 */
  public DownloadMouseMode(DownloadMouseModeLayer download_layer)
  {
    download_layer_ = download_layer;
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
