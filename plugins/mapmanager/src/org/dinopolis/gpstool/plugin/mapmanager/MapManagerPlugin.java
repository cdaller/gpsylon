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


package org.dinopolis.gpstool.plugin.mapmanager;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.MissingResourceException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.bbn.openmap.Layer;
import org.dinopolis.gpstool.MapManagerHook;
import org.dinopolis.gpstool.gui.MouseMode;
import org.dinopolis.gpstool.plugin.GuiPlugin;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.util.Debug;
import org.dinopolis.util.ResourceManager;
import org.dinopolis.util.Resources;
import org.dinopolis.util.gui.ActionStore;
import org.dinopolis.util.gui.MenuFactory;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import java.util.Set;
import java.util.TreeSet;
import org.dinopolis.gpstool.util.MapInfoScaleComparator;
import javax.swing.event.ListSelectionEvent;



//----------------------------------------------------------------------
/**
 * This plugin shows allows to manager the available maps.
 * 
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class MapManagerPlugin implements GuiPlugin, ListSelectionListener
{
      /** the map manager to retrieve the information about maps from */
  protected MapManagerHook map_manager_;
      /** the layer to draw */
  protected MapManagerLayer layer_;
  protected JMenuItem frame_menu_item_;
  /** the resources of the map manager plugin */
  protected Resources resources_;
  /** the resources of the GPSMap application */
  protected Resources application_resources_;
      /** the action store */
  protected ActionStore action_store_;
      /** the map manager frame */
  protected MapManagerFrame main_frame_;

      // keys for resources:
  public static final String KEY_MAPMANAGER_PLUGIN_IDENTIFIER = "mapmanager.plugin.identifier";
  public static final String KEY_MAPMANAGER_PLUGIN_VERSION = "mapmanager.plugin.version";
  public static final String KEY_MAPMANAGER_PLUGIN_NAME = "mapmanager.plugin.name";
  public static final String KEY_MAPMANAGER_PLUGIN_DESCRIPTION = "mapmanager.plugin.description";
  public static final String KEY_MAPMANAGER_WINDOW_REMEMBER_SETTINGS = "mapmanager.window.remember_settings";
  public static final String KEY_MAPMANAGER_WINDOW_DIMENSION_WIDTH = "mapmanager.window.dimension.width";
  public static final String KEY_MAPMANAGER_WINDOW_DIMENSION_HEIGHT = "mapmanager.window.dimension.height";
  public static final String KEY_MAPMANAGER_WINDOW_LOCATION_X = "mapmanager.window.location.x";
  public static final String KEY_MAPMANAGER_WINDOW_LOCATION_Y = "mapmanager.window.location.y";
  public static final String KEY_MAPMANAGER_TABLE_COLUMN_NAMES = "mapmanager.table.column_names";
  
  public static final String KEY_MAPMANAGER_MENU_NAME = "mapmanager";
  
  
	/** the name of the resource file */
	private final static String RESOURCE_BUNDLE_NAME = "MapManagerPlugin";

	/** the name of the directory containing the resources */
	private final static String USER_RESOURCE_DIR_NAME = ".mapmanagerplugin";

  public static final String MAPMANAGER_ACTION_STORE_ID = RESOURCE_BUNDLE_NAME;
  public static final String ACTION_MAPMANAGER_MAPMANAGER_FRAME = "map_manager";


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
    map_manager_ = support.getMapManagerHook();
    application_resources_ = support.getResources();
        // load map manager resources:
    if(Debug.DEBUG)
      Debug.println("MapManager_init","loading resources");
    loadResources();

        // prepare the actionstore for the menu:
    action_store_ = ActionStore.getStore(MAPMANAGER_ACTION_STORE_ID);
    action_store_.addActions(new Action[] {new MapManagerFrameAction()});
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
    boolean store_resources = false;
        // save window locaton and dimensions:
    if (resources_.getBoolean(KEY_MAPMANAGER_WINDOW_REMEMBER_SETTINGS) && (main_frame_ != null))
    {
      Point location = main_frame_.getLocationOnScreen();
      Dimension dimension = main_frame_.getSize();
      resources_.setInt(KEY_MAPMANAGER_WINDOW_LOCATION_X, location.x);
      resources_.setInt(KEY_MAPMANAGER_WINDOW_LOCATION_Y, location.y);
      resources_.setInt(KEY_MAPMANAGER_WINDOW_DIMENSION_WIDTH, dimension.width);
      resources_.setInt(KEY_MAPMANAGER_WINDOW_DIMENSION_HEIGHT, dimension.height);
      store_resources = true;
    }

    if(store_resources)
      resources_.store();

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
    return("Map Manager Plugin");
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
    return((float)resources_.getDouble(KEY_MAPMANAGER_PLUGIN_VERSION));
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
    return(resources_.getString(KEY_MAPMANAGER_PLUGIN_NAME));
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
    return(resources_.getString(KEY_MAPMANAGER_PLUGIN_DESCRIPTION));
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
  	if(frame_menu_item_ == null)
  	{
      frame_menu_item_ = (JMenuItem)MenuFactory.createMenuComponent(MenuFactory.KEY_MENUE_PREFIX,
                                                                    KEY_MAPMANAGER_MENU_NAME,
                                                                    resources_,
                                                                    action_store_);
  	}
    return(frame_menu_item_);
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
    if(layer_ == null)
    {
      layer_ = new MapManagerLayer(map_manager_);
    }
    return(layer_);
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
    layer_.setActive(active);
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
    return(layer_.isActive());
  }

//----------------------------------------------------------------------
// other methods
//----------------------------------------------------------------------

//----------------------------------------------------------------------
/**
 * Loads the resource file, or exits on a MissingResourceException.
 */

  void loadResources()
  {
    try 
    {
      resources_ = ResourceManager.getResources(MapManagerPlugin.class,
                                                RESOURCE_BUNDLE_NAME,
                                                USER_RESOURCE_DIR_NAME,
                                                Locale.getDefault());
    }
    catch (MissingResourceException mre) 
    {
      if (Debug.DEBUG)
        Debug.println("MapManagerPlugin", mre.toString()+'\n'+
                      Debug.getStackTrace(mre));
      System.err.println("MapManagerPlugin: resource file '"+RESOURCE_BUNDLE_NAME+"' not found");
      System.err.println("please make sure that this file is within the classpath !");
      System.exit(1);
    }
  }

//----------------------------------------------------------------------
/**
 * Updates the locations according to the values set within the
 * resource file. 
 */

  protected void updateWindowLocation()
  {
    try
    {
      main_frame_.setLocation(resources_.getInt(KEY_MAPMANAGER_WINDOW_LOCATION_X),
                              resources_.getInt(KEY_MAPMANAGER_WINDOW_LOCATION_Y));
      main_frame_.setSize(resources_.getInt(KEY_MAPMANAGER_WINDOW_DIMENSION_WIDTH),
                          resources_.getInt(KEY_MAPMANAGER_WINDOW_DIMENSION_HEIGHT));
    }
    catch (MissingResourceException exc)
    {
    }
    catch (NumberFormatException exc)
    {
    }
  }

//----------------------------------------------------------------------
/**
 * Called when the user selects or deselects a map in the table.
 *
 * @param event the event
 */
  public void valueChanged(ListSelectionEvent event)
  {
    if(Debug.DEBUG)
      Debug.println("MapManager_selection","selection changed "+event);
    MapInfoTable table = main_frame_.getMapInfoTable();
    MapInfoTableModel table_model = (MapInfoTableModel)table.getModel();

        // find and store all selected maps:
    int[] selected_rows = table.getSelectedRows();
    Set selected_map_infos = new TreeSet();; 
    for(int sel_row_index=0; sel_row_index < selected_rows.length; sel_row_index++)
    {
      selected_map_infos.add(table_model.getMapInfo(selected_rows[sel_row_index]).getFilename());
    }
    layer_.setSelectedMaps(selected_map_infos);
    layer_.repaint();
  }
  
//----------------------------------------------------------------------
// inner classes
//----------------------------------------------------------------------

      //----------------------------------------------------------------------
     /**
      * The Action for switching a layer on or off
      */

 class MapManagerFrameAction extends AbstractAction
 {

       //----------------------------------------------------------------------
       /**
        * The Default Constructor.
        */

   public MapManagerFrameAction()
   {
     super(ACTION_MAPMANAGER_MAPMANAGER_FRAME);
   }

       //----------------------------------------------------------------------
       /**
        * Ouputs some test message
        * 
        * @param event the action event
        */

   public void actionPerformed(ActionEvent event)
   {
     if(main_frame_ == null)
     {
       main_frame_ = new MapManagerFrame();
       main_frame_.initialize(map_manager_,application_resources_,resources_);
       JTable table = main_frame_.getMapInfoTable();
       ListSelectionModel selection_model = table.getSelectionModel();
           // add the layer as selection listener, so it may paint the selected maps
       selection_model.addListSelectionListener(MapManagerPlugin.this);
     }
     updateWindowLocation();
     main_frame_.setVisible(true);
   }
 }

}


