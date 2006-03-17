/***********************************************************************
 * @(#)$RCSfile$  $Revision$
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

package org.dinopolis.gpstool.plugin.dufourmap;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.MissingResourceException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.dinopolis.gpstool.Gpsylon;
import org.dinopolis.gpstool.gui.MouseMode;
import org.dinopolis.gpstool.plugin.GuiPlugin;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.util.Debug;
import org.dinopolis.util.ResourceManager;
import org.dinopolis.util.Resources;
import org.dinopolis.util.gui.ActionStore;

import com.bbn.openmap.Layer;

//----------------------------------------------------------------------
/**
 * This plugin implements the firts Map of Switzerland. The Dufourmap. 
 * see: http://de.wikipedia.org/wiki/Dufourkarte
 * 
 * The DufourMapLayer will be able to crossfade the map with the standard MultiMapLayer
 * 
 * Exctract the maps from the Dufourmap CD with the makemap script (located 
 * in the swissgrid plugin folder)
 * Dufourmap CD see: http://www.swisstopo.ch/de/products/digital/multimedia/dm/
 * 
 * @author Samuel Benz
 * @version $Revision$
 */

public class Dufourmap implements GuiPlugin, ListSelectionListener, PropertyChangeListener
{
	
	/** the plugin support of the application */
	protected PluginSupport support_;
	/** the Plugin Menu Item from GPSMap */
	protected JMenuItem frame_menu_item_;
	/** the resources of the swiss grid plugin */
	protected Resources resources_;
	/** the resources of the GPSMap application */
	protected Resources application_resources_;
	/** the action store */
	protected ActionStore action_store_;
	/** the main frame of the GPSMap application used for MapManager Error Messages */
	protected JFrame main_frame_;
	
	protected JFrame SliderWindow;
	/** the layer to draw */
	protected DufourmapLayer layer_;
	
	// keys for resources:
	public static final String KEY_DUFOURMAP_PLUGIN_IDENTIFIER = "dufourmap.plugin.identifier";
	public static final String KEY_DUFOURMAP_PLUGIN_VERSION = "dufourmap.plugin.version";
	public static final String KEY_DUFOURMAP_PLUGIN_NAME = "dufourmap.plugin.name";
	public static final String KEY_DUFOURMAP_PLUGIN_DESCRIPTION = "dufourmap.plugin.description";
	
	public static final String KEY_DUFOURMAP_MENU_NAME = "dufourmap";

	/** the name of the resource file */
	private final static String RESOURCE_BUNDLE_NAME = "Dufourmap";

	/** the name of the directory containing the resources */
	private final static String USER_RESOURCE_DIR_NAME = Gpsylon.USER_RESOURCE_DIR_NAME;

	public static final String DUFOURMAP_ACTION_STORE_ID = RESOURCE_BUNDLE_NAME;
	public static final String ACTION_DUFOURMAP_DUFOURMAP_FRAME = "dufourmap";

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
		
		application_resources_ = support.getResources();
		main_frame_ = (JFrame) support.getMainFrame();
		
		// load dufourmap resources:
		if (Debug.DEBUG)
			Debug.println("Dufourmap init", "loading resources");
		loadResources();
		application_resources_.attachResources(resources_);

		// prepare the actionstore for the menu:
		action_store_ = ActionStore.getStore(DUFOURMAP_ACTION_STORE_ID);
		action_store_.addActions(new Action[] { new DufourmapFrameAction()});
				
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
		return ("Dufourmap Plugin");
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
		return ((float) resources_.getDouble(KEY_DUFOURMAP_PLUGIN_VERSION));
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
		return (resources_.getString(KEY_DUFOURMAP_PLUGIN_NAME));
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
		return (resources_.getString(KEY_DUFOURMAP_PLUGIN_DESCRIPTION));
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
		/*if (frame_menu_item_ == null)
		{
			frame_menu_item_ =
				(JMenuItem) MenuFactory.createMenuComponent(
					MenuFactory.KEY_MENUE_PREFIX,
					KEY_DUFOURMAP_MENU_NAME,
					resources_,
					action_store_);
		}
		return (frame_menu_item_);*/
		return null;
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
		return (null);
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
			layer_ = new DufourmapLayer();
			DufourMapManager dm_manager_ = new DufourMapManager();
			dm_manager_.initialize(application_resources_, main_frame_);
			layer_.initialize(dm_manager_);
			
		    SliderWindow = new JFrame(resources_.getString(KEY_DUFOURMAP_PLUGIN_NAME));
		    SliderWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		    //Create and set up the content pane.
		    SliderFrame slider = new SliderFrame(layer_);
		    slider.setOpaque(true); //content panes must be opaque
		    SliderWindow.setContentPane(slider);

		    //Display the window.
		    SliderWindow.pack();
		    SliderWindow.setVisible(false);
			
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
		if(active == true){
			SliderWindow.setVisible(true);
		}else{
			SliderWindow.setVisible(false);
		}
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
			resources_ = ResourceManager.getResources(Dufourmap.class,RESOURCE_BUNDLE_NAME,USER_RESOURCE_DIR_NAME,Locale.getDefault());
			resources_.addPropertyChangeListener(this);
		}
		catch (MissingResourceException mre)
		{
			if (Debug.DEBUG)
				Debug.println(
					"DufourmapPlugin",
					mre.toString() + '\n' + Debug.getStackTrace(mre));
			System.err.println(
				"DufourmapPlugin: resource file '"
					+ RESOURCE_BUNDLE_NAME
					+ "' not found");
			System.err.println(
				"please make sure that this file is within the classpath !");
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
			Debug.println("Dufourmap selection", "selection changed " + event);

		if (event.getValueIsAdjusting())
			return;		
	}
	
	
	
	
	//----------------------------------------------------------------------
	// inner classes
	//----------------------------------------------------------------------

	//----------------------------------------------------------------------
	/**
	 * The Action for switching a layer on or off
	 */

	class DufourmapFrameAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 8615283568768208964L;

		//----------------------------------------------------------------------
		/**
		 * The Default Constructor.
		 */

		public DufourmapFrameAction()
		{
			super(ACTION_DUFOURMAP_DUFOURMAP_FRAME);
		}

		//----------------------------------------------------------------------
		/**
		 * Ouputs some test message
		 * 
		 * @param event the action event
		 */

		public void actionPerformed(ActionEvent event)
		{
		}
	}


	public void propertyChange(PropertyChangeEvent event) {
	    if (event.getSource() == resources_)
	    {
	      //updateResources(event.getPropertyName());
	      //System.out.println(event.getPropertyName());
	      return;
	    }
	}

}
