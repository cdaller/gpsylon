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


package org.dinopolis.gpstool.plugin.memorymonitor;

import org.dinopolis.gpstool.plugin.GuiPlugin;
import org.dinopolis.gpstool.plugin.Plugin;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.MapManagerHook;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import com.bbn.openmap.Layer;
import javax.swing.KeyStroke;
import javax.swing.Action;
import javax.swing.AbstractAction;
import org.apache.batik.util.gui.MemoryMonitor;
import org.dinopolis.gpstool.gui.MouseMode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//----------------------------------------------------------------------
/**
 * This plugin opens a frame that shows the memory usage (it uses the
 * MemoryMonitor class from Batik).
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class MemoryMonitorPlugin implements GuiPlugin 
{
  Action menu_action_;
  
// ----------------------------------------------------------------------  
// Implementation of org.dinopolis.gpstool.plugin.Plugin
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
    return("Memory Monitor Plugin");
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
    return(0.1f);
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
    return("Memory Monitor");
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
    return("Shows memory consumption");
  }
  

//----------------------------------------------------------------------
// GuiPlugin methods
//----------------------------------------------------------------------

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
  public JMenu getMainMenu()
  {
    return(null);
  }

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
  public JMenuItem getSubMenu()
  {
    if(menu_action_ == null)
    {
      menu_action_ = new MemoryMonitorAction(this);
      menu_action_.putValue(Action.NAME,getPluginName());
      menu_action_.putValue(Action.SHORT_DESCRIPTION,getPluginDescription());
      menu_action_.putValue(Action.MNEMONIC_KEY,new Integer(getPluginName().charAt(0)));
      menu_action_.putValue(Action.ACCELERATOR_KEY,
                            KeyStroke.getKeyStroke("alt M"));
    }
    return(new JMenuItem(menu_action_));

  }

//----------------------------------------------------------------------
/**
 * Every plugin may provide one or more mouse modes. These mouse modes
 * may react on mouse clicks, drags, etc.
 *
 * @return mouse modes that are used by this plugin in the application or
 * <code>null</code>, if no mouse modes are used.
 *
 */
  public MouseMode[] getMouseModes()
  {
    return(null);
  }

//----------------------------------------------------------------------
/**
 * If the plugin wants to draw anything on the map it may
 * return a layer here or <code>null</code> if not.
 *
 * @return the layer the plugin wants to paint into.
 * @see com.bbn.openmap.Layer
 */

  public Layer getLayer()
  {
    return(null);
  }


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
  public void setActive(boolean active)
  {
  }

//----------------------------------------------------------------------
/**
 * Returns if the plugin is active or not.
 *
 * @return <code>true</code> if the plugin is active and paints
 * something.
 */
  public boolean isActive()
  {
    return(false);
  }


     //----------------------------------------------------------------------
     /**
      * The Action for switching a layer on or off
      */

 class MemoryMonitorAction extends AbstractAction
 {

       //----------------------------------------------------------------------
       /**
        * The Default Constructor.
        */

   public MemoryMonitorAction(GuiPlugin plugin)
   {
     super(plugin.getPluginName());
   }

       //----------------------------------------------------------------------
       /**
        * Ouputs some test message
        * 
        * @param event the action event
        */

   public void actionPerformed(ActionEvent event)
   {
     MemoryMonitor monitor = new MemoryMonitor();
     monitor.setVisible(true);
   }
 }
  
}


