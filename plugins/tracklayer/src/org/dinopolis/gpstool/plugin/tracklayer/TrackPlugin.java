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

package org.dinopolis.gpstool.plugin.tracklayer;

import com.bbn.openmap.Layer;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.dinopolis.gpstool.GPSMap;
import org.dinopolis.gpstool.TrackManager;
import org.dinopolis.gpstool.gui.MouseMode;
import org.dinopolis.gpstool.plugin.GuiPlugin;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.plugin.ReadTrackPlugin;
import org.dinopolis.gpstool.track.Track;
import org.dinopolis.gpstool.track.TrackImpl;
import org.dinopolis.gpstool.track.Trackpoint;
import org.dinopolis.gpstool.track.TrackpointImpl;
import org.dinopolis.gpstool.util.ExtensionFileFilter;
import org.dinopolis.util.Debug;
import org.dinopolis.util.ResourceManager;
import org.dinopolis.util.Resources;
import org.dinopolis.util.gui.ActionStore;
import org.dinopolis.util.gui.MenuFactory;

//----------------------------------------------------------------------
/**
 * This plugin allows to manage the available maps. It shows all
 * available maps on a layer it provides, it provides a table that
 * lists all available maps and allows to edit them, and it provides a
 * mouse mode, that lets the user interact with the map manager by
 * clicking on the map component.
 * 
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class TrackPlugin implements GuiPlugin
{
	protected TrackLayer layer_;
  protected TrackManager track_manager_;
	protected JMenuItem load_tracks_menu_item_;
	/** the resources of the track plugin */
	protected Resources resources_;
	/** the resources of the GPSMap application */
	protected Resources application_resources_;
	/** the action store */
	protected ActionStore action_store_;

  PluginSupport plugin_support_;

  protected ActiveTrackLogger track_logger_;

	// keys for resources:
	public static final String KEY_TRACK_PLUGIN_IDENTIFIER =
		"track.plugin.identifier";
	public static final String KEY_TRACK_PLUGIN_VERSION =
		"track.plugin.version";
	public static final String KEY_TRACK_PLUGIN_NAME =
		"track.plugin.name";
	public static final String KEY_TRACK_PLUGIN_DESCRIPTION =
		"track.plugin.description";
// 	public static final String KEY_TRACK_WINDOW_REMEMBER_SETTINGS =
// 		"track.window.remember_settings";
// 	public static final String KEY_TRACK_WINDOW_DIMENSION_WIDTH =
// 		"track.window.dimension.width";
// 	public static final String KEY_TRACK_WINDOW_DIMENSION_HEIGHT =
// 		"track.window.dimension.height";
// 	public static final String KEY_TRACK_WINDOW_LOCATION_X =
// 		"track.window.location.x";
// 	public static final String KEY_TRACK_WINDOW_LOCATION_Y =
// 		"track.window.location.y";
// 	public static final String KEY_TRACK_TABLE_COLUMN_NAMES =
// 		"track.table.column_names";

	public static final String KEY_LOAD_TRACK_MENU_NAME = "load_track";

	/** the name of the resource file */
	private final static String RESOURCE_BUNDLE_NAME = "TrackPlugin";

	/** the name of the directory containing the resources */
	private final static String USER_RESOURCE_DIR_NAME = GPSMap.USER_RESOURCE_DIR_NAME;

	public static final String TRACK_ACTION_STORE_ID = RESOURCE_BUNDLE_NAME;

      // track actions 
  public final static String ACTION_LOAD_TRACK = "load_track";

  
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
		track_manager_ = support.getTrackManager();
		application_resources_ = support.getResources();

    try
    {
          // prevent any "old" values in the gpsmap resources to confuse
          // this plugin:
      application_resources_.unset(TrackLayer.KEY_TRACK_LAYER_ACTIVE);
    }
    catch(MissingResourceException ignored) {}
    
		// load map manager resources:
		if (Debug.DEBUG)
			Debug.println("trackplugin_init", "loading resources");
		loadResources();
        // attach the plugin resources to the global application
        // resources:
    application_resources_.attachResources(resources_);
 
    track_logger_ = new ActiveTrackLogger();
    track_logger_.initialize(track_manager_);
    support.getPropertyChangeSupport().addPropertyChangeListener(track_logger_);
    
		// prepare the actionstore for the menu:
// 		action_store_ = ActionStore.getStore(TRACK_ACTION_STORE_ID);
// 		action_store_.addActions(new Action[] { new LoadTrackAction()});
    
//    loadTestTrack();
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
    track_logger_.enable(true);
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
    track_logger_.enable(false);
    resources_.setBoolean(TrackLayer.KEY_TRACK_LAYER_ACTIVE,layer_.isActive());
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
		return(resources_.getString(KEY_TRACK_PLUGIN_IDENTIFIER));
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
		return ((float) resources_.getDouble(KEY_TRACK_PLUGIN_VERSION));
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
		return (resources_.getString(KEY_TRACK_PLUGIN_NAME));
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
		return (resources_.getString(KEY_TRACK_PLUGIN_DESCRIPTION));
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
// 		if (load_tracks_menu_item_ == null)
// 		{
// 			load_tracks_menu_item_ =
// 				(JMenuItem) MenuFactory.createMenuComponent(
// 					MenuFactory.KEY_MENUE_PREFIX,
// 					KEY_LOAD_TRACK_MENU_NAME,
// 					resources_,
// 					action_store_);
// 		}
// 		return (load_tracks_menu_item_);
    return(null);
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
		if (layer_ == null)
		{
			layer_ = new TrackLayer();
      layer_.initializePlugin(plugin_support_);
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
					TrackPlugin.class,
					RESOURCE_BUNDLE_NAME,
					USER_RESOURCE_DIR_NAME,
					Locale.getDefault());
		}
		catch (MissingResourceException mre)
		{
			if (Debug.DEBUG)
				Debug.println(
					"TrackPlugin",
					mre.toString() + '\n' + Debug.getStackTrace(mre));
			System.err.println(
				"TrackPlugin: resource file '"
					+ RESOURCE_BUNDLE_NAME
					+ "' not found");
			System.err.println(
				"please make sure that this file is within the classpath !");
			System.exit(1);
		}
	}



  private void loadTestTrack()
  {
    if(Debug.DEBUG)
      Debug.println("trackplugin","loading test track");
    Track track = new TrackImpl();
    track.setIdentification("test Track");
    Trackpoint point;

    point = new TrackpointImpl();
    point.setLatitude(47.05489);
    point.setLongitude(15.4736);
    point.setAltitude(369);
    point.setNewTrack(true);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(47.05378);
    point.setLongitude(15.47469);
    point.setAltitude(370);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(47.05015);
    point.setLongitude(15.47997);
    point.setAltitude(372);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(47.04831);
    point.setLongitude(15.48298);
    point.setAltitude(365);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(47.04537);
    point.setLongitude(15.48615);
    point.setAltitude(365);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(47.04324);
    point.setLongitude(15.48787);
    point.setAltitude(363);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(47.04303);
    point.setLongitude(15.48759);
    point.setAltitude(362);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(47.04039);
    point.setLongitude(15.48826);
    point.setAltitude(364);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(47.03846);
    point.setLongitude(15.48692);
    point.setAltitude(362);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(47.03597);
    point.setLongitude(15.48272);
    point.setAltitude(355);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(47.03423);
    point.setLongitude(15.47929);
    point.setAltitude(342);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(47.03275);
    point.setLongitude(15.47639);
    point.setAltitude(341);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(47.03138);
    point.setLongitude(15.47909);
    point.setAltitude(351);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(47.02835);
    point.setLongitude(15.48162);
    point.setAltitude(362);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(47.02292);
    point.setLongitude(15.48514);
    point.setAltitude(359);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(47.02136);
    point.setLongitude(15.48536);
    point.setAltitude(355);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(47.019);
    point.setLongitude(15.48411);
    point.setAltitude(356);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(47.01766);
    point.setLongitude(15.48068);
    point.setAltitude(358);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(47.01653);
    point.setLongitude(15.47594);
    point.setAltitude(359);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(47.01533);
    point.setLongitude(15.46972);
    point.setAltitude(356);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(47.01492);
    point.setLongitude(15.46017);
    point.setAltitude(355);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(47.01434);
    point.setLongitude(15.45658);
    point.setAltitude(356);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(47.00453);
    point.setLongitude(15.42465);
    point.setAltitude(353);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(47.00005);
    point.setLongitude(15.413);
    point.setAltitude(356);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.99973);
    point.setLongitude(15.41238);
    point.setAltitude(357);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.99501);
    point.setLongitude(15.405);
    point.setAltitude(358);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.99389);
    point.setLongitude(15.40367);
    point.setAltitude(352);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.99003);
    point.setLongitude(15.39991);
    point.setAltitude(359);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.9822);
    point.setLongitude(15.39491);
    point.setAltitude(365);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.98003);
    point.setLongitude(15.39376);
    point.setAltitude(370);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.97254);
    point.setLongitude(15.38957);
    point.setAltitude(374);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.96522);
    point.setLongitude(15.38476);
    point.setAltitude(364);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.96265);
    point.setLongitude(15.38279);
    point.setAltitude(366);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.959);
    point.setLongitude(15.37968);
    point.setAltitude(363);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.95617);
    point.setLongitude(15.37635);
    point.setAltitude(358);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.95467);
    point.setLongitude(15.37354);
    point.setAltitude(356);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.95321);
    point.setLongitude(15.36732);
    point.setAltitude(351);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.95325);
    point.setLongitude(15.36425);
    point.setAltitude(349);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.95475);
    point.setLongitude(15.35811);
    point.setAltitude(345);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.9664);
    point.setLongitude(15.33966);
    point.setAltitude(352);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.97007);
    point.setLongitude(15.33286);
    point.setAltitude(361);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.97486);
    point.setLongitude(15.32258);
    point.setAltitude(351);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.97951);
    point.setLongitude(15.31022);
    point.setAltitude(356);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.98129);
    point.setLongitude(15.30226);
    point.setAltitude(360);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.98177);
    point.setLongitude(15.29698);
    point.setAltitude(361);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.98168);
    point.setLongitude(15.29069);
    point.setAltitude(357);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.98029);
    point.setLongitude(15.27928);
    point.setAltitude(362);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.97878);
    point.setLongitude(15.27091);
    point.setAltitude(372);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.97741);
    point.setLongitude(15.26563);
    point.setAltitude(388);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.97632);
    point.setLongitude(15.26346);
    point.setAltitude(391);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.97194);
    point.setLongitude(15.25761);
    point.setAltitude(415);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.9711);
    point.setLongitude(15.25583);
    point.setAltitude(417);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.97074);
    point.setLongitude(15.2502);
    point.setAltitude(423);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.96996);
    point.setLongitude(15.24714);
    point.setAltitude(426);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.96853);
    point.setLongitude(15.24514);
    point.setAltitude(429);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.96741);
    point.setLongitude(15.24428);
    point.setAltitude(431);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.96406);
    point.setLongitude(15.24295);
    point.setAltitude(439);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.95999);
    point.setLongitude(15.23971);
    point.setAltitude(459);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.95711);
    point.setLongitude(15.23945);
    point.setAltitude(469);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.95413);
    point.setLongitude(15.23855);
    point.setAltitude(483);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.95293);
    point.setLongitude(15.23748);
    point.setAltitude(488);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.9514);
    point.setLongitude(15.23484);
    point.setAltitude(497);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.95063);
    point.setLongitude(15.23203);
    point.setAltitude(503);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.95009);
    point.setLongitude(15.22975);
    point.setAltitude(512);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.95117);
    point.setLongitude(15.22772);
    point.setAltitude(516);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.95308);
    point.setLongitude(15.22154);
    point.setAltitude(537);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.95482);
    point.setLongitude(15.2196);
    point.setAltitude(544);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.95756);
    point.setLongitude(15.21898);
    point.setAltitude(555);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.96327);
    point.setLongitude(15.21864);
    point.setAltitude(565);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.96831);
    point.setLongitude(15.21649);
    point.setAltitude(573);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.96975);
    point.setLongitude(15.21259);
    point.setAltitude(579);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.9682);
    point.setLongitude(15.20643);
    point.setAltitude(594);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.96836);
    point.setLongitude(15.20553);
    point.setAltitude(599);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.97138);
    point.setLongitude(15.19937);
    point.setAltitude(620);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.97436);
    point.setLongitude(15.19712);
    point.setAltitude(637);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.97524);
    point.setLongitude(15.19435);
    point.setAltitude(644);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.97464);
    point.setLongitude(15.18916);
    point.setAltitude(657);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.97554);
    point.setLongitude(15.18559);
    point.setAltitude(667);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.9764);
    point.setLongitude(15.18296);
    point.setAltitude(672);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.97683);
    point.setLongitude(15.17877);
    point.setAltitude(677);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.97707);
    point.setLongitude(15.17514);
    point.setAltitude(678);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.9787);
    point.setLongitude(15.16959);
    point.setAltitude(687);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.97863);
    point.setLongitude(15.16497);
    point.setAltitude(690);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.97988);
    point.setLongitude(15.16279);
    point.setAltitude(693);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.98286);
    point.setLongitude(15.15974);
    point.setAltitude(708);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.98516);
    point.setLongitude(15.15577);
    point.setAltitude(725);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.98773);
    point.setLongitude(15.15111);
    point.setAltitude(738);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.99013);
    point.setLongitude(15.14841);
    point.setAltitude(751);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.99314);
    point.setLongitude(15.14094);
    point.setAltitude(778);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.99378);
    point.setLongitude(15.13819);
    point.setAltitude(786);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.99372);
    point.setLongitude(15.13628);
    point.setAltitude(791);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.99282);
    point.setLongitude(15.13332);
    point.setAltitude(795);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.99164);
    point.setLongitude(15.13135);
    point.setAltitude(798);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.99086);
    point.setLongitude(15.13019);
    point.setAltitude(803);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.98938);
    point.setLongitude(15.12729);
    point.setAltitude(802);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.98801);
    point.setLongitude(15.12388);
    point.setAltitude(811);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.98685);
    point.setLongitude(15.1224);
    point.setAltitude(816);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.98295);
    point.setLongitude(15.11927);
    point.setAltitude(831);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.98071);
    point.setLongitude(15.11699);
    point.setAltitude(837);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.97659);
    point.setLongitude(15.11554);
    point.setAltitude(857);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.97453);
    point.setLongitude(15.1126);
    point.setAltitude(872);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.97318);
    point.setLongitude(15.11144);
    point.setAltitude(876);
    track.addWaypoint(point);

    point = new TrackpointImpl();
    point.setLatitude(46.96975);
    point.setLongitude(15.11187);
    point.setAltitude(884);
    track.addWaypoint(point);

    track_manager_.addTrack(track);
  }

// //----------------------------------------------------------------------
// /**
//  * The Action that triggers load track mode. It uses plugins that
//  * provide the functionality.
//  *
//  * @see org.dinopolis.gpstool.plugin.ReadTrackPlugin.
//  */

//   class LoadTrackAction extends AbstractAction 
//   {
//     Object[] plugins_;

//         //----------------------------------------------------------------------
//         /**
//          * The Default Constructor.
//          */

//     public LoadTrackAction()
//     {
//       super(ACTION_LOAD_TRACK);
//             // find all available track reader plugins:
//             // (do not use a string here, so the compiler checks for typos)
//       Object[] plugins = GPSMap.service_discovery_.getServices(
//         org.dinopolis.gpstool.plugin.ReadTrackPlugin.class);
//           // add ReadGPSMapTrackPlugin by hand:
//       plugins_ = new Object[plugins.length + 1];
//       plugins_[0] = new ReadGPSMapTrackPlugin();
//       System.arraycopy(plugins,0,plugins_,1,plugins.length);
      
//       if(Debug.DEBUG)
//         Debug.println("plugin","plugins for reading tracks detected: "+Debug.objectToString(plugins_));

//           // disable action, if no plugins found:
//       if(plugins_.length == 0)
//       {
//         setEnabled(false);
//       }
//     }

//         //----------------------------------------------------------------------
//         /**
//          * Load a track
//          * 
//          * @param event the action event
//          */

//     public void actionPerformed(ActionEvent event)
//     {
//       File[] chosen_files = null;
//       if(track_file_chooser_ == null)
//       {
//         track_file_chooser_ = new JFileChooser();
//         track_file_chooser_.setDialogTitle(resources_.getString(KEY_LOCALIZE_LOAD_TRACK_DIALOG_TITLE));
//         track_file_chooser_.setAcceptAllFileFilterUsed(false);
        
//         track_file_chooser_.setMultiSelectionEnabled(true);
//         track_file_chooser_.setFileHidingEnabled(false);
//         String tracks_dirname = FileUtil.getAbsolutePath(resources_.getString(KEY_FILE_MAINDIR),
//                                                          resources_.getString(KEY_FILE_TRACK_DIR));
//         track_file_chooser_.setCurrentDirectory(new File(tracks_dirname));

//             // use all ReadTrackPlugin plugins to build extension file filters:
//         ExtensionFileFilter filter;
//         ReadTrackPlugin plugin;
//         String[] extensions;
//         boolean plugin_found = false;
//         for(int plugin_count = 0; plugin_count < plugins_.length; plugin_count++)
//         {
//           plugin = (ReadTrackPlugin)plugins_[plugin_count];
//           if(plugin != null)
//           {
//             plugin.initializePlugin(plugin_support_);
//             filter = new ExtensionFileFilter();
//             extensions = plugin.getContentFileExtensions();
//             for(int extension_count = 0; extension_count < extensions.length; extension_count++)
//               filter.addExtension(extensions[extension_count]);
//             filter.setDescription(plugin.getContentDescription());//"JPG "+resources_.getString(KEY_LOCALIZE_IMAGES));
//             filter.setAuxiliaryObject(plugin);
//             track_file_chooser_.addChoosableFileFilter(filter);
//             plugin_found = true;
//           }
//         }
//         if(!plugin_found)
//         {
//               // TODO: open dialog for error:
//           System.err.println("ERROR: no plugin found!");
//           return;
//         }
//       }

//       int result = track_file_chooser_.showOpenDialog(TrackLayer.this);
//       if(result == JFileChooser.APPROVE_OPTION)
//       {
//         ExtensionFileFilter filter = (ExtensionFileFilter)track_file_chooser_.getFileFilter();
//         ReadTrackPlugin plugin = (ReadTrackPlugin)filter.getAuxiliaryObject();
//         File file = track_file_chooser_.getSelectedFile();
//         chosen_files = track_file_chooser_.getSelectedFiles();
//             // add all tracks from all files:
//         for(int file_count = 0; file_count < chosen_files.length; file_count++)
//         {
//           try
//           {
//                 // create inputstream from file for plugin:
//             Track[] tracks = plugin.getTracks(new BufferedInputStream(
//                                                  new FileInputStream(chosen_files[file_count])));
//             for(int track_count = 0; track_count < tracks.length; track_count++)
//             {
//               addTrack(tracks[track_count]);
//             }
//           }
//           catch(IOException ioe)
//           {
//             ioe.printStackTrace();
//           }
//         }
//         if(!display_track_)
//         {
//               // enable "display track", otherwise we do not see the new loaded tracks!
//           Action action = action_store_.getAction(GPSMap.ACTION_DISPLAY_TRACK_MODE);
//           if(action != null)
//             action.putValue(MenuFactory.SELECTED, new Boolean(true));
//           display_track_ = true;
	
//           recalculatePath();
//         }
//         else
//           repaint(); // everything already calculated before!
//       }
//     }
//   }
}
