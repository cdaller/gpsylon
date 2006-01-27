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
 * Free Software Foundation, Inc., a
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 ***********************************************************************/

package org.dinopolis.gpstool.plugin.imageserver;

import java.util.Locale;
import java.util.MissingResourceException;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.dinopolis.gpstool.Gpsylon;
import org.dinopolis.gpstool.gui.MouseMode;
import org.dinopolis.gpstool.plugin.GuiPlugin;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.util.*;
import org.dinopolis.util.gui.ActionStore;

import com.bbn.openmap.Layer;


//----------------------------------------------------------------------
/**
 * This plugin uses a very simple webserver to listent to HTTP
 * requests and answers them with a simple default page and a
 * screenshot of the GPSylon window.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class ImageServerPlugin implements GuiPlugin
{
	protected JMenuItem plugin_menu_item_;
	/** the resources of the track plugin */
	protected Resources resources_;
	/** the resources of the GPSylon application */
	protected Resources application_resources_;
	/** the action store */
	protected ActionStore action_store_;

  protected PluginSupport plugin_support_;

	// keys for resources:
	public static final String KEY_IMAGESERVER_PLUGIN_IDENTIFIER =
		"imageserver.plugin.identifier";
	public static final String KEY_IMAGESERVER_PLUGIN_VERSION =
		"imageserver.plugin.version";
	public static final String KEY_IMAGESERVER_PLUGIN_NAME =
		"imageserver.plugin.name";
	public static final String KEY_IMAGESERVER_PLUGIN_DESCRIPTION =
		"imageserver.plugin.description";

//	public static final String KEY_IMAGESERVER_SHOW_MENU_NAME = "imageserver_show";

	/** the name of the resource file */
	private final static String RESOURCE_BUNDLE_NAME = "ImageServerPlugin";

	/** the name of the directory containing the resources */
	private final static String USER_RESOURCE_DIR_NAME = Gpsylon.USER_RESOURCE_DIR_NAME;

	public static final String IMAGESERVER_ACTION_STORE_ID = RESOURCE_BUNDLE_NAME;

      // imageserver actions 
//  public final static String ACTION_SHOW_IMAGESERVER = "imageserver_show";

  public static final String KEY_IMAGESERVER_WEB_SERVER_PORT = "imageserver.web_server.port";
  public static final String KEY_IMAGESERVER_WEB_SERVER_DEFAULT_PAGE = "imageserver.web_server.default_page";
  public static final String KEY_IMAGESERVER_WEB_SERVER_SCREENSHOT_URL = "imageserver.web_server.screenshot_url";

  protected WebServer web_server_;

  
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
    plugin_support_ = support;
		application_resources_ = support.getResources();

		// load plugin resources:
		if (Debug.DEBUG)
			Debug.println("imageserverplugin_init", "loading resources");
		loadResources();
        // attach the plugin resources to the global application
        // resources:
    application_resources_.attachResources(resources_);
 
		// prepare the actionstore for the menu:
// 		action_store_ = ActionStore.getStore(IMAGESERVER_ACTION_STORE_ID);
// 		action_store_.addActions(new Action[] { new ShowImageserverAction()});
    
//     loadTestTrack();
	}

	//----------------------------------------------------------------------
	/**
	 * The application calls this method to indicate that the plugin is
	 * activated and will be used from now on. The Plugin should
	 * initialize any needed resources (files, etc.) in this method. This
	 * implementation starts the web server thread.
	 *
	 * @throws Exception if an error occurs. If this method throws an
	 * exception, the plugin will not be used by the application.
	 */

	public void startPlugin() throws Exception
	{
    web_server_ = new WebServer(resources_.getInt(KEY_IMAGESERVER_WEB_SERVER_PORT),
				resources_.getString(KEY_IMAGESERVER_WEB_SERVER_DEFAULT_PAGE),
				resources_.getString(KEY_IMAGESERVER_WEB_SERVER_SCREENSHOT_URL));
    web_server_.setComponent(plugin_support_.getMapComponent());
	}

	//----------------------------------------------------------------------
	/**
	 * The application calls this method to indicate that the plugin is
	 * deactivated and will not be used any more. The Plugin should
	 * release all resources (close files, etc.) in this method. This
	 * implementation stops the webserver thread.
	 *
	 * @throws Exception if an error occurs.
	 */

	public void stopPlugin() throws Exception
	{
    web_server_.stopServer();
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
		return(resources_.getString(KEY_IMAGESERVER_PLUGIN_IDENTIFIER));
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
		return ((float) resources_.getDouble(KEY_IMAGESERVER_PLUGIN_VERSION));
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
		return (resources_.getString(KEY_IMAGESERVER_PLUGIN_NAME));
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
		return (resources_.getString(KEY_IMAGESERVER_PLUGIN_DESCRIPTION));
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
		return (null);
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
    return(null);
// 		if (plugin_menu_item_ == null)
// 		{
// 			plugin_menu_item_ =
// 				(JMenuItem) MenuFactory.createMenuComponent(
// 					MenuFactory.KEY_MENUE_PREFIX,
// 					KEY_IMAGESERVER_SHOW_MENU_NAME,
// 					resources_,
// 					action_store_);
// 		}
// 		return (plugin_menu_item_);
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
// 		if (mouse_mode_ == null)
// 		{
// 			mouse_mode_ = new MapManagerMouseMode();
// 			mouse_mode_.initialize(resources_, (MapManagerLayer)getLayer(),this,map_manager_);
// 		}
// 		return (new MouseMode[] { mouse_mode_ });
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
		return (null);
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
		return (true);
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
			resources_ =
				ResourceManager.getResources(
					ImageServerPlugin.class,
					RESOURCE_BUNDLE_NAME,
					USER_RESOURCE_DIR_NAME,
					Locale.getDefault());
		}
		catch (MissingResourceException mre)
		{
			if (Debug.DEBUG)
				Debug.println(
					"ImageserverPlugin",
					mre.toString() + '\n' + Debug.getStackTrace(mre));
			System.err.println(
				"ImageserverPlugin: resource file '"
					+ RESOURCE_BUNDLE_NAME
					+ "' not found");
			System.err.println(
				"please make sure that this file is within the classpath !");
			System.exit(1);
		}
	}


// //----------------------------------------------------------------------
// /**
//  * The Action that triggers the track chart window.
//  */

//   class ShowImageserverAction extends AbstractAction 
//   {
//         //----------------------------------------------------------------------
//         /**
//          * The Default Constructor.
//          */

//     public ShowImageserverAction()
//     {
//       super(ACTION_SHOW_IMAGESERVER);
//     }

//         //----------------------------------------------------------------------
//         /**
//          * Show the track chart
//          * 
//          * @param event the action event
//          */

//     public void actionPerformed(ActionEvent event)
//     {
//     }
//   }
}
