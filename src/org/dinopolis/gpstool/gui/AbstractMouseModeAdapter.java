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


package org.dinopolis.gpstool.gui;
import javax.swing.Icon;
import java.awt.event.MouseEvent;

//----------------------------------------------------------------------
/**
 * This
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public abstract class AbstractMouseModeAdapter implements MouseMode
{

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

  public abstract void setActive(boolean active);

//----------------------------------------------------------------------
/**
 * The name returned here is used in the menu and/or the toolbar of
 * the application to switch the mouse mode on or off. It should be
 * localized.
 *
 * @return the name of the mouse mode.
 */
  public abstract String getMouseModeName();

//----------------------------------------------------------------------
/**
 * The icon returned here is used in the menu and/or the toolbar of
 * the application to switch the mouse mode on or off. 
 *
 * @return the icon of the mouse mode.
 */
  public abstract Icon getMouseModeIcon();

//----------------------------------------------------------------------
/**
 * Invoked when the mouse has been clicked on a component.
 *
 * @param event the mouse event.
 */
  public void mouseClicked(MouseEvent event)
  {
  }

//----------------------------------------------------------------------
/**
 * Invoked when a mouse button has been pressed on a component.
 *
 * @param event the mouse event.
 */
  public void mousePressed(MouseEvent event)
  {
  }

//----------------------------------------------------------------------
/**
 * Invoked when a mouse button has been released on a component.
 *
 * @param event the mouse event.
 */
  public void mouseReleased(MouseEvent event)
  {
  }

//----------------------------------------------------------------------
/**
 * Invoked when the mouse enters a component.
 *
 * @param event the mouse event.
 */
  public void mouseEntered(MouseEvent event)
  {
  }

//----------------------------------------------------------------------
/**
 * Invoked when the mouse exits a component.
 *
 * @param event the mouse event.
 */
  public void mouseExited(MouseEvent event)
  {
  }

//----------------------------------------------------------------------
/**
 * Invoked when a mouse button is pressed on a component and then 
 * dragged.  Mouse drag events will continue to be delivered to
 * the component where the first originated until the mouse button is
 * released (regardless of whether the mouse position is within the
 * bounds of the component).
 *
 * @param event the mouse event.
 */
  public void mouseDragged(MouseEvent event)
  {
  }

//----------------------------------------------------------------------
/**
 * Invoked when the mouse button has been moved on a component
 * (with no buttons no down).
 *
 * @param event the mouse event.
 */
  public void mouseMoved(MouseEvent event)
  {
  }
}

