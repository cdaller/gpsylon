/***********************************************************************
 * @(#)$RCSfile$ $Revision$
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

package org.dinopolis.gpstool.plugin.googlemap;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Locale;
import java.util.MissingResourceException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.dinopolis.gpstool.Gpsylon;
import org.dinopolis.gpstool.gui.LatLongInputDialog;
import org.dinopolis.gpstool.gui.MouseMode;
import org.dinopolis.gpstool.gui.MouseModeManager;
import org.dinopolis.gpstool.hook.MapManagerHook;
import org.dinopolis.gpstool.hook.MapNavigationHook;
import org.dinopolis.gpstool.plugin.GuiPlugin;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.util.Debug;
import org.dinopolis.util.ResourceManager;
import org.dinopolis.util.Resources;
import org.dinopolis.util.gui.ActionStore;
import org.dinopolis.util.gui.MenuFactory;

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.Layer;
import com.bbn.openmap.proj.Projection;


/**
 * This plugin implements the plugin to show/use google maps.
 * References:
 * http://mapki.com/index.php?title=Lat/Lon_To_Tile
 * http://www.quakr.net/java/gmaps/Tile.java
 * http://www.ponies.me.uk/maps/GoogleTileUtils.java
 * http://www.codeproject.com/useritems/googlemap.asp
 * http://cfis.savagexi.com/articles/2006/05/03/google-maps-deconstructed
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GoogleMapPlugin implements GuiPlugin, ListSelectionListener
{

	/** the map manager to retrieve the information about maps from */
	protected MapManagerHook map_manager_;
	/** the map navigation to set the projection */
	protected MapNavigationHook map_navigation_;
	/** the mouse mode manger from the application */
	protected MouseModeManager mouse_mode_manager_;
	/** the main frame of the application */
	protected JFrame main_frame_;
	/** the Label for the status frame */
	protected JLabel statusMsg = new JLabel(" ");
	/** plugin support fomr the application */
	protected PluginSupport support_;
	/** the plugin menu item */
	protected JMenu googlemap_main_menu_ ;
	/** the plugin menu item */
	protected JMenuItem frame_menu_item_;
	/** the resources of the googlemap plugin */
	protected Resources resources_;
	/** the resources of the GPSMap application */
	protected Resources application_resources_;
	/** the action store */
	protected ActionStore action_store_;
	/** the layer to draw */
	protected GoogleMapLayer layer_;
	/** the mouse mode */
	protected GoogleMapMouseMode mouse_mode_;


	// keys for resources:
	public static final String KEY_GOOGLEMAP_PLUGIN_IDENTIFIER = "googlemap.plugin.identifier";
	public static final String KEY_GOOGLEMAP_PLUGIN_VERSION = "googlemap.plugin.version";
	public static final String KEY_GOOGLEMAP_PLUGIN_NAME = "googlemap.plugin.name";
	public static final String KEY_GOOGLEMAP_PLUGIN_DESCRIPTION = "googlemap.plugin.description";

	public static final String KEY_LOCALIZE_CENTER_MAP_DIALOG_TITLE = "localize.center_map_dialog_title";

	/** the name of the resource file */
	private final static String RESOURCE_BUNDLE_NAME = "GoogleMapPlugin";

	/** the name of the directory containing the resources */
	private final static String USER_RESOURCE_DIR_NAME = Gpsylon.USER_RESOURCE_DIR_NAME;

	public static final String GOOGLEMAP_ACTION_STORE_ID = RESOURCE_BUNDLE_NAME;

	public static final String KEY_GOOGLEMAP_MAIN_MENU_NAME = "GoogleMap";
	public static final String ACTION_GOOGLEMAP_CENTER_MAP = "center_map";

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
		support_ = support;
		map_manager_ = support.getMapManagerHook();
		map_navigation_ = support.getMapNavigationHook();
		mouse_mode_manager_ = support.getMouseModeManager();
		main_frame_ = (JFrame) support.getMainFrame();
		application_resources_ = support.getResources();
		// load resources:
		if (Debug.DEBUG)
			Debug.println("GoogleMap_init", "loading resources");
		loadResources();

		// prepare the actionstore for the menu:
		action_store_ = ActionStore.getStore(GOOGLEMAP_ACTION_STORE_ID);
		action_store_.addActions(new Action[] { new GoogleMapCenterMap()});

	    // set the GoogleMap Projection to map_bean_
		Projection newprojection = new GoogleMapProjection(map_navigation_.getMapProjection().getCenter(),map_navigation_.getMapProjection().getScale(), 0, 0);
		map_navigation_.setMapProjection(newprojection);

		Container main_container_ = main_frame_.getContentPane();

		JPanel NewStatusBar = new JPanel(new  BorderLayout());

		//Component[] componetns = main_frame_.getContentPane().getComponents();
		Component StatusBar = main_frame_.getContentPane().getComponent(1);

		NewStatusBar.add(StatusBar,BorderLayout.NORTH);
		NewStatusBar.add(statusMsg,BorderLayout.SOUTH);

		main_container_.add(NewStatusBar,BorderLayout.SOUTH);
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

	public void startPlugin() throws Exception
	{
		//mouse_mode_manager_.activateMouseMode(resources_.getString(KEY_GOOGLEMAP_PLUGIN_NAME));
	}

	//----------------------------------------------------------------------
	/**
	 * The application calls this method to indicate that the plugin is
	 * deactivated and will not be used any more. The Plugin should
	 * release all resources (close files, etc.) in this method.
	 *
	 * @throws Exception if an error occurs.
	 */

	public void stopPlugin() throws Exception
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
		return resources_.getString(KEY_GOOGLEMAP_PLUGIN_IDENTIFIER);
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
		return ((float) resources_.getDouble(KEY_GOOGLEMAP_PLUGIN_VERSION));
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
		return (resources_.getString(KEY_GOOGLEMAP_PLUGIN_NAME));
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
		return (resources_.getString(KEY_GOOGLEMAP_PLUGIN_DESCRIPTION));
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
//		if (googlemap_main_menu_ == null)
//		{
//			googlemap_main_menu_ = MenuFactory.createMenu(
//        MenuFactory.KEY_MENUE_PREFIX,
//        KEY_GOOGLEMAP_MAIN_MENU_NAME,
//        resources_,
//        action_store_);
//		}
//		return (googlemap_main_menu_);
    return null;
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
		return (null);
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
//		if (mouse_mode_ == null)
//		{
//			mouse_mode_ = new GoogleMapMouseMode();
//			mouse_mode_.initialize(support_, resources_, (GoogleMapLayer)getLayer(),this,map_manager_,statusMsg);
//		}
//		return (new MouseMode[] { mouse_mode_ });
    return null;
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
		if (layer_ == null)
		{
			layer_ = new GoogleMapLayer();
		}
		return (layer_);
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
		return (layer_.isActive());
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
					GoogleMapPlugin.class,
					RESOURCE_BUNDLE_NAME,
					USER_RESOURCE_DIR_NAME,
					Locale.getDefault());
		}
		catch (MissingResourceException mre)
		{
			if (Debug.DEBUG)
				Debug.println("GoogleMapPlugin", mre.toString() + '\n' + Debug.getStackTrace(mre));
			System.err.println("SwissGridPlugin: resource file '" + RESOURCE_BUNDLE_NAME + "' not found");
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

	}

	//----------------------------------------------------------------------
	/**
	 * Called when the user selects or deselects a map in the table.
	 *
	 * @param event the event
	 */
	public void valueChanged(ListSelectionEvent event)
	{
		if (Debug.DEBUG)
			Debug.println("GoogleMap_selection", "selection changed " + event);

		if (event.getValueIsAdjusting())
			return; // user not finished yet!

		//		ListSelectionModel selection_model = (ListSelectionModel) event.getSource();
		//		int firstIndex = event.getFirstIndex();
		//		int lastIndex = event.getLastIndex();

		//		Set selected_map_infos = new TreeSet();
		//		if (!selection_model.isSelectionEmpty())
		//		{
		//			// Find out which indexes are selected.
		//			int minIndex = selection_model.getMinSelectionIndex();
		//			int maxIndex = selection_model.getMaxSelectionIndex();
		//			for (int index = minIndex; index <= maxIndex; index++)
		//			{
		//				if (selection_model.isSelectedIndex(index))
		//				{

		//				}
		//			}
		//		}
		//		layer_.setSelectedMaps(selected_map_infos);
		//		layer_.repaint()'


	}




	//----------------------------------------------------------------------
	// inner classes
	//----------------------------------------------------------------------

	//----------------------------------------------------------------------
	/**
	 * The Action for switching a layer on or off
	 */

	class GoogleMapCenterMap extends AbstractAction
	{
		/**
		 *
		 */
		private static final long serialVersionUID = -558784001480428444L;

		GoogleMapInputDialog input_dialog_;

		//----------------------------------------------------------------------
		/**
		 * The Default Constructor.
		 */

		public GoogleMapCenterMap()
		{
			super(ACTION_GOOGLEMAP_CENTER_MAP);
		}

		//----------------------------------------------------------------------
		/**
		 * Ouputs some test message
		 *
		 * @param event the action event
		 */

		public void actionPerformed(ActionEvent event)
		{
		     if (input_dialog_ == null)
		      {
		        ActionListener action_listener = new ActionListener()
		          {
		            public void actionPerformed(ActionEvent event)
		            {
		              if(event.getActionCommand().equals(GoogleMapInputDialog.COMMAND_OK))
		              {
		                if(input_dialog_.checkValidity())
		                {
		                  int latitude = input_dialog_.getLatitude();
		                  int longitude = input_dialog_.getLongitude();
		                  LatLonPoint newCenter = GoogleMapProjection.lv032ll(latitude,longitude);
		                  map_navigation_.setMapCenter(newCenter.getLatitude(),newCenter.getLongitude());
		                  input_dialog_.setVisible(false);
		                }
		                return;
		              }

		              if(event.getActionCommand().equals(LatLongInputDialog.COMMAND_CANCEL))
		              {
		                input_dialog_.setVisible(false);
		                return;
		              }
		            }
		          };
		        input_dialog_ = new GoogleMapInputDialog(application_resources_,action_listener, main_frame_);
		        input_dialog_.setTitle(application_resources_.getString(KEY_LOCALIZE_CENTER_MAP_DIALOG_TITLE));
		      }
		      LatLonPoint pos = map_navigation_.getMapProjection().getCenter();
		      Point2D oldCenter = GoogleMapProjection.ll2lv03(pos.getLatitude(),pos.getLongitude());
		      input_dialog_.setCoordinates((int)oldCenter.getX(),(int)oldCenter.getY());
		      input_dialog_.setVisible(true);
		}

	}

}
