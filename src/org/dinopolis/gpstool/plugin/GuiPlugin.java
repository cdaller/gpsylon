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


package org.dinopolis.gpstool.plugin;

import javax.swing.Action;
import org.dinopolis.gpstool.gui.MouseMode;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import com.bbn.openmap.Layer;

//----------------------------------------------------------------------
/**
 * This interface is used for all plugins that want to provide any
 * kind of gui-interface for the application. The plugins may provide
 * menu items that are placed into a sub menu (in the "plugins" menu)
 * or if they provide a larger quantity of actions, the plugin may
 * provide actions for a main menu item.
 * <p>
 * The plugins are informed
 * about start and stop, so they may initialize and free their
 * resources on startup and on closing of the application.
 * </p>
 * <p>
 * If the plugin wants to draw anything on the map, it may return a
 * layer in the {@link #getLayer()} method.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public interface GuiPlugin extends Plugin
{


//----------------------------------------------------------------------
/**
 * The plugin may return a JMenu object to be used in the main menu of
 * the application and may (should) contain other menu items. The
 * menuitems returned should provide an icon, a mnemonic key, and a
 * localized name (and a accelerator key).
 *
 * @return A menu that is used in the main menu in the
 * application or <code>null</code>, if no main menu is needed.
 *
 */
  public JMenu getMainMenu();

//----------------------------------------------------------------------
/**
 * The application provides a sub menu for every plugin that may be
 * used. The JMenuItem (or JMenu) returned is added to a submenu in
 * the "plugins" menu item.  The menuitems returned should provide an
 * icon, a mnemonic key, and a localized name (and a accelerator key).
 *
 * @return A menuitem (or a JMenu) that are used in a sub menu in the
 * application or <code>null</code>, if no submenus are needed.
 *
 */
  public JMenuItem getSubMenu();

 
// //----------------------------------------------------------------------
// /**
//  * The application provides a sub menu for every plugin that may be
//  * used. The actions returned are used in the order provided and added
//  * to a submenu in the "plugins" menu item. The name of the plugin is
//  * used as the name of the submenu (see {@link
//  * Plugin#getPluginName()}). The actions returned should provide an
//  * icon and a localized name.
//  *
//  * @return actions that are used in a sub menu in the application or
//  * <code>null</code>, if no submenus are needed.
//  *
//  */
//   public Action[] getSubMenuActions();

// //----------------------------------------------------------------------
// /**
//  * The application provides a menu for every plugin that may be used
//  * by the plugin. The actions returned are used in the order provided
//  * and added to a menu. The name of the plugin is used as the name of
//  * the submenu (see {@link Plugin#getPluginName()}). The actions
//  * returned should provide an icon and a localized name.
//  *
//  * @return actions that are used in a menu in the application or
//  * <code>null</code>, if no menus are needed.
//  *
//  */
//   public Action[] getMainMenuActions();


//----------------------------------------------------------------------
/**
 * Every plugin may provide one or more mouse modes. These mouse modes
 * may react on mouse clicks, drags, etc.
 *
 * @return mouse modes that are used by this plugin in the application or
 * <code>null</code>, if no mouse modes are used.
 *
 */
  public MouseMode[] getMouseModes();

//----------------------------------------------------------------------
/**
 * If the plugin wants to draw anything on the map it may
 * return a layer here or <code>null</code> if not.
 *
 * @return the layer the plugin wants to paint into.
 * @see com.bbn.openmap.Layer
 */

  public Layer getLayer();

//----------------------------------------------------------------------
/**
 * Called by the application to switch the layer on or off. If the
 * layer is switched off, it must not paint anything and should not
 * consume any calculational power.
 *
 * @param active if <code>true</code> the layer is switched on and
 * should react on changes of the projection and draw anything in the
 * paintComponent method.
 */
  public void setActive(boolean active);
  
//----------------------------------------------------------------------
/**
 * Returns if the plugin is active or not.
 *
 * @return <code>true</code> if the plugin is active and paints
 * something.
 */
  public boolean isActive();
}


