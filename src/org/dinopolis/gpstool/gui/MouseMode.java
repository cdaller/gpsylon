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

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.Icon;
import javax.swing.Action;

//----------------------------------------------------------------------
/**
 * A mouse mode is a mouse listener (click or motion), that may be
 * switched on or off by the user. To give some examples of mouse
 * modes: A ZoomMouseMode could react on clicks to zoom in/out the
 * map, a CalculatorMouseMode may sum the distances of the clicks and
 * show the result in a window.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public interface MouseMode extends MouseListener, MouseMotionListener
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

  public void setActive(boolean active);

//----------------------------------------------------------------------
/**
 * Returns if the mouse mode is active or not.
 *
 * @return <code>true</code> if the mouse mode is active and reacts on
 * mouse events.
 */
  public boolean isActive();

//----------------------------------------------------------------------
/**
 * The name returned here is used in the menu and/or the toolbar of
 * the application to switch the mouse mode on or off. It should be
 * localized.
 *
 * @return the name of the mouse mode.
 */
  public String getMouseModeName();

//----------------------------------------------------------------------
/**
 * The icon returned here is used in the menu and/or the toolbar of
 * the application to switch the mouse mode on or off. 
 *
 * @return the icon of the mouse mode.
 */
  public Icon getMouseModeIcon();


//----------------------------------------------------------------------
/**
 * The description returned here is used in the menu and/or the toolbar of
 * the application to switch the mouse mode on or off. 
 *
 * @return the description of the mouse mode.
 */
  public String getMouseModeDescription();


// //----------------------------------------------------------------------
// /**
//  * The action returned here is used in the menu and/or the toolbar of
//  * the application to switch the mouse mode on or off. 
//  *
//  * @return the action of the mouse mode.
//  */
//   public Action getMouseModeAction();
}


