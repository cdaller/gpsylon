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

package org.dinopolis.gpstool.plugin.trackchart;

import com.bbn.openmap.Layer;
import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.MissingResourceException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.dinopolis.gpstool.GPSMap;
import org.dinopolis.gpstool.TrackManager;
import org.dinopolis.gpstool.gui.MouseMode;
import org.dinopolis.gpstool.plugin.GuiPlugin;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.track.Track;
import org.dinopolis.gpstool.track.TrackImpl;
import org.dinopolis.gpstool.track.Trackpoint;
import org.dinopolis.gpstool.track.TrackpointImpl;
import org.dinopolis.util.Debug;
import org.dinopolis.util.ResourceManager;
import org.dinopolis.util.Resources;
import org.dinopolis.util.gui.ActionStore;
import org.dinopolis.util.gui.MenuFactory;




//----------------------------------------------------------------------
/**
 * 
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class TrackChartPlugin implements GuiPlugin
{
  protected TrackManager track_manager_;
	protected JMenuItem plugin_menu_item_;
	/** the resources of the track plugin */
	protected Resources resources_;
	/** the resources of the GPSMap application */
	protected Resources application_resources_;
	/** the action store */
	protected ActionStore action_store_;

  protected PluginSupport plugin_support_;

	// keys for resources:
	public static final String KEY_TRACKCHART_PLUGIN_IDENTIFIER =
		"trackchart.plugin.identifier";
	public static final String KEY_TRACKCHART_PLUGIN_VERSION =
		"trackchart.plugin.version";
	public static final String KEY_TRACKCHART_PLUGIN_NAME =
		"trackchart.plugin.name";
	public static final String KEY_TRACKCHART_PLUGIN_DESCRIPTION =
		"trackchart.plugin.description";

	public static final String KEY_TRACKCHART_SHOW_MENU_NAME = "trackchart_show";

	/** the name of the resource file */
	private final static String RESOURCE_BUNDLE_NAME = "TrackChartPlugin";

	/** the name of the directory containing the resources */
	private final static String USER_RESOURCE_DIR_NAME = GPSMap.USER_RESOURCE_DIR_NAME;

	public static final String TRACKCHART_ACTION_STORE_ID = RESOURCE_BUNDLE_NAME;

      // trackchart actions 
  public final static String ACTION_SHOW_TRACKCHART = "trackchart_show";

  
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

		// load plugin resources:
		if (Debug.DEBUG)
			Debug.println("trackchartplugin_init", "loading resources");
		loadResources();
        // attach the plugin resources to the global application
        // resources:
//    application_resources_.attachResources(resources_);
 
		// prepare the actionstore for the menu:
		action_store_ = ActionStore.getStore(TRACKCHART_ACTION_STORE_ID);
		action_store_.addActions(new Action[] { new ShowTrackChartAction()});
    
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
		return(resources_.getString(KEY_TRACKCHART_PLUGIN_IDENTIFIER));
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
		return ((float) resources_.getDouble(KEY_TRACKCHART_PLUGIN_VERSION));
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
		return (resources_.getString(KEY_TRACKCHART_PLUGIN_NAME));
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
		return (resources_.getString(KEY_TRACKCHART_PLUGIN_DESCRIPTION));
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
		if (plugin_menu_item_ == null)
		{
			plugin_menu_item_ =
				(JMenuItem) MenuFactory.createMenuComponent(
					MenuFactory.KEY_MENUE_PREFIX,
					KEY_TRACKCHART_SHOW_MENU_NAME,
					resources_,
					action_store_);
		}
		return (plugin_menu_item_);
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
					TrackChartPlugin.class,
					RESOURCE_BUNDLE_NAME,
					USER_RESOURCE_DIR_NAME,
					Locale.getDefault());
		}
		catch (MissingResourceException mre)
		{
			if (Debug.DEBUG)
				Debug.println(
					"TrackChartPlugin",
					mre.toString() + '\n' + Debug.getStackTrace(mre));
			System.err.println(
				"TrackChartPlugin: resource file '"
					+ RESOURCE_BUNDLE_NAME
					+ "' not found");
			System.err.println(
				"please make sure that this file is within the classpath !");
			System.exit(1);
		}
	}



//   private void loadTestTrack()
//   {
//     if(Debug.DEBUG)
//       Debug.println("trackplugin","loading test track");
//     Track track = new TrackImpl();
//     track.setIdentification("test track");
//     Trackpoint point;

//     point = new TrackpointImpl();
//     point.setLatitude(47.05489);
//     point.setLongitude(15.4736);
//     point.setAltitude(369);
//     point.setNewTrack(true);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.05378);
//     point.setLongitude(15.47469);
//     point.setAltitude(370);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.05015);
//     point.setLongitude(15.47997);
//     point.setAltitude(372);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.04831);
//     point.setLongitude(15.48298);
//     point.setAltitude(365);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.04537);
//     point.setLongitude(15.48615);
//     point.setAltitude(365);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.04324);
//     point.setLongitude(15.48787);
//     point.setAltitude(363);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.04303);
//     point.setLongitude(15.48759);
//     point.setAltitude(362);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.04039);
//     point.setLongitude(15.48826);
//     point.setAltitude(364);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.03846);
//     point.setLongitude(15.48692);
//     point.setAltitude(362);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.03597);
//     point.setLongitude(15.48272);
//     point.setAltitude(355);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.03423);
//     point.setLongitude(15.47929);
//     point.setAltitude(342);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.03275);
//     point.setLongitude(15.47639);
//     point.setAltitude(341);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.03138);
//     point.setLongitude(15.47909);
//     point.setAltitude(351);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.02835);
//     point.setLongitude(15.48162);
//     point.setAltitude(362);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.02292);
//     point.setLongitude(15.48514);
//     point.setAltitude(359);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.02136);
//     point.setLongitude(15.48536);
//     point.setAltitude(355);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.019);
//     point.setLongitude(15.48411);
//     point.setAltitude(356);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.01766);
//     point.setLongitude(15.48068);
//     point.setAltitude(358);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.01653);
//     point.setLongitude(15.47594);
//     point.setAltitude(359);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.01533);
//     point.setLongitude(15.46972);
//     point.setAltitude(356);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.01492);
//     point.setLongitude(15.46017);
//     point.setAltitude(355);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.01434);
//     point.setLongitude(15.45658);
//     point.setAltitude(356);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.00453);
//     point.setLongitude(15.42465);
//     point.setAltitude(353);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.00005);
//     point.setLongitude(15.413);
//     point.setAltitude(356);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.99973);
//     point.setLongitude(15.41238);
//     point.setAltitude(357);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.99501);
//     point.setLongitude(15.405);
//     point.setAltitude(358);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.99389);
//     point.setLongitude(15.40367);
//     point.setAltitude(352);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.99003);
//     point.setLongitude(15.39991);
//     point.setAltitude(359);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.9822);
//     point.setLongitude(15.39491);
//     point.setAltitude(365);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98003);
//     point.setLongitude(15.39376);
//     point.setAltitude(370);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97254);
//     point.setLongitude(15.38957);
//     point.setAltitude(374);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.96522);
//     point.setLongitude(15.38476);
//     point.setAltitude(364);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.96265);
//     point.setLongitude(15.38279);
//     point.setAltitude(366);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.959);
//     point.setLongitude(15.37968);
//     point.setAltitude(363);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95617);
//     point.setLongitude(15.37635);
//     point.setAltitude(358);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95467);
//     point.setLongitude(15.37354);
//     point.setAltitude(356);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95321);
//     point.setLongitude(15.36732);
//     point.setAltitude(351);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95325);
//     point.setLongitude(15.36425);
//     point.setAltitude(349);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95475);
//     point.setLongitude(15.35811);
//     point.setAltitude(345);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.9664);
//     point.setLongitude(15.33966);
//     point.setAltitude(352);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97007);
//     point.setLongitude(15.33286);
//     point.setAltitude(361);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97486);
//     point.setLongitude(15.32258);
//     point.setAltitude(351);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97951);
//     point.setLongitude(15.31022);
//     point.setAltitude(356);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98129);
//     point.setLongitude(15.30226);
//     point.setAltitude(360);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98177);
//     point.setLongitude(15.29698);
//     point.setAltitude(361);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98168);
//     point.setLongitude(15.29069);
//     point.setAltitude(357);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98029);
//     point.setLongitude(15.27928);
//     point.setAltitude(362);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97878);
//     point.setLongitude(15.27091);
//     point.setAltitude(372);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97741);
//     point.setLongitude(15.26563);
//     point.setAltitude(388);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97632);
//     point.setLongitude(15.26346);
//     point.setAltitude(391);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97194);
//     point.setLongitude(15.25761);
//     point.setAltitude(415);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.9711);
//     point.setLongitude(15.25583);
//     point.setAltitude(417);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97074);
//     point.setLongitude(15.2502);
//     point.setAltitude(423);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.96996);
//     point.setLongitude(15.24714);
//     point.setAltitude(426);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.96853);
//     point.setLongitude(15.24514);
//     point.setAltitude(429);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.96741);
//     point.setLongitude(15.24428);
//     point.setAltitude(431);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.96406);
//     point.setLongitude(15.24295);
//     point.setAltitude(439);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95999);
//     point.setLongitude(15.23971);
//     point.setAltitude(459);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95711);
//     point.setLongitude(15.23945);
//     point.setAltitude(469);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95413);
//     point.setLongitude(15.23855);
//     point.setAltitude(483);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95293);
//     point.setLongitude(15.23748);
//     point.setAltitude(488);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.9514);
//     point.setLongitude(15.23484);
//     point.setAltitude(497);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95063);
//     point.setLongitude(15.23203);
//     point.setAltitude(503);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95009);
//     point.setLongitude(15.22975);
//     point.setAltitude(512);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95117);
//     point.setLongitude(15.22772);
//     point.setAltitude(516);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95308);
//     point.setLongitude(15.22154);
//     point.setAltitude(537);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95482);
//     point.setLongitude(15.2196);
//     point.setAltitude(544);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95756);
//     point.setLongitude(15.21898);
//     point.setAltitude(555);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.96327);
//     point.setLongitude(15.21864);
//     point.setAltitude(565);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.96831);
//     point.setLongitude(15.21649);
//     point.setAltitude(573);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.96975);
//     point.setLongitude(15.21259);
//     point.setAltitude(579);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.9682);
//     point.setLongitude(15.20643);
//     point.setAltitude(594);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.96836);
//     point.setLongitude(15.20553);
//     point.setAltitude(599);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97138);
//     point.setLongitude(15.19937);
//     point.setAltitude(620);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97436);
//     point.setLongitude(15.19712);
//     point.setAltitude(637);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97524);
//     point.setLongitude(15.19435);
//     point.setAltitude(644);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97464);
//     point.setLongitude(15.18916);
//     point.setAltitude(657);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97554);
//     point.setLongitude(15.18559);
//     point.setAltitude(667);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.9764);
//     point.setLongitude(15.18296);
//     point.setAltitude(672);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97683);
//     point.setLongitude(15.17877);
//     point.setAltitude(677);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97707);
//     point.setLongitude(15.17514);
//     point.setAltitude(678);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.9787);
//     point.setLongitude(15.16959);
//     point.setAltitude(687);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97863);
//     point.setLongitude(15.16497);
//     point.setAltitude(690);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97988);
//     point.setLongitude(15.16279);
//     point.setAltitude(693);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98286);
//     point.setLongitude(15.15974);
//     point.setAltitude(708);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98516);
//     point.setLongitude(15.15577);
//     point.setAltitude(725);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98773);
//     point.setLongitude(15.15111);
//     point.setAltitude(738);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.99013);
//     point.setLongitude(15.14841);
//     point.setAltitude(751);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.99314);
//     point.setLongitude(15.14094);
//     point.setAltitude(778);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.99378);
//     point.setLongitude(15.13819);
//     point.setAltitude(786);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.99372);
//     point.setLongitude(15.13628);
//     point.setAltitude(791);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.99282);
//     point.setLongitude(15.13332);
//     point.setAltitude(795);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.99164);
//     point.setLongitude(15.13135);
//     point.setAltitude(798);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.99086);
//     point.setLongitude(15.13019);
//     point.setAltitude(803);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98938);
//     point.setLongitude(15.12729);
//     point.setAltitude(802);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98801);
//     point.setLongitude(15.12388);
//     point.setAltitude(811);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98685);
//     point.setLongitude(15.1224);
//     point.setAltitude(816);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98295);
//     point.setLongitude(15.11927);
//     point.setAltitude(831);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98071);
//     point.setLongitude(15.11699);
//     point.setAltitude(837);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97659);
//     point.setLongitude(15.11554);
//     point.setAltitude(857);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97453);
//     point.setLongitude(15.1126);
//     point.setAltitude(872);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97318);
//     point.setLongitude(15.11144);
//     point.setAltitude(876);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.96975);
//     point.setLongitude(15.11187);
//     point.setAltitude(884);
//     track.addWaypoint(point);

//     track_manager_.addTrack(track);


//     track = new TrackImpl();
//     track.setIdentification("higher test track");
    
//     point = new TrackpointImpl();
//     point.setLatitude(47.05489);
//     point.setLongitude(15.4736);
//     point.setAltitude(1369);
//     point.setNewTrack(true);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.05378);
//     point.setLongitude(15.47469);
//     point.setAltitude(1370);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.05015);
//     point.setLongitude(15.47997);
//     point.setAltitude(1372);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.04831);
//     point.setLongitude(15.48298);
//     point.setAltitude(1365);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.04537);
//     point.setLongitude(15.48615);
//     point.setAltitude(1365);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.04324);
//     point.setLongitude(15.48787);
//     point.setAltitude(1363);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.04303);
//     point.setLongitude(15.48759);
//     point.setAltitude(1362);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.04039);
//     point.setLongitude(15.48826);
//     point.setAltitude(1364);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.03846);
//     point.setLongitude(15.48692);
//     point.setAltitude(1362);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.03597);
//     point.setLongitude(15.48272);
//     point.setAltitude(1355);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.03423);
//     point.setLongitude(15.47929);
//     point.setAltitude(1342);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.03275);
//     point.setLongitude(15.47639);
//     point.setAltitude(1341);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.03138);
//     point.setLongitude(15.47909);
//     point.setAltitude(1351);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.02835);
//     point.setLongitude(15.48162);
//     point.setAltitude(1362);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.02292);
//     point.setLongitude(15.48514);
//     point.setAltitude(1359);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.02136);
//     point.setLongitude(15.48536);
//     point.setAltitude(1355);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.019);
//     point.setLongitude(15.48411);
//     point.setAltitude(1356);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.01766);
//     point.setLongitude(15.48068);
//     point.setAltitude(1358);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.01653);
//     point.setLongitude(15.47594);
//     point.setAltitude(1359);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.01533);
//     point.setLongitude(15.46972);
//     point.setAltitude(1356);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.01492);
//     point.setLongitude(15.46017);
//     point.setAltitude(1355);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.01434);
//     point.setLongitude(15.45658);
//     point.setAltitude(1356);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.00453);
//     point.setLongitude(15.42465);
//     point.setAltitude(1353);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(47.00005);
//     point.setLongitude(15.413);
//     point.setAltitude(1356);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.99973);
//     point.setLongitude(15.41238);
//     point.setAltitude(1357);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.99501);
//     point.setLongitude(15.405);
//     point.setAltitude(1358);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.99389);
//     point.setLongitude(15.40367);
//     point.setAltitude(1352);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.99003);
//     point.setLongitude(15.39991);
//     point.setAltitude(1359);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.9822);
//     point.setLongitude(15.39491);
//     point.setAltitude(1365);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98003);
//     point.setLongitude(15.39376);
//     point.setAltitude(1370);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97254);
//     point.setLongitude(15.38957);
//     point.setAltitude(1374);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.96522);
//     point.setLongitude(15.38476);
//     point.setAltitude(1364);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.96265);
//     point.setLongitude(15.38279);
//     point.setAltitude(1366);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.959);
//     point.setLongitude(15.37968);
//     point.setAltitude(1363);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95617);
//     point.setLongitude(15.37635);
//     point.setAltitude(1358);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95467);
//     point.setLongitude(15.37354);
//     point.setAltitude(1356);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95321);
//     point.setLongitude(15.36732);
//     point.setAltitude(1351);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95325);
//     point.setLongitude(15.36425);
//     point.setAltitude(1349);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95475);
//     point.setLongitude(15.35811);
//     point.setAltitude(1345);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.9664);
//     point.setLongitude(15.33966);
//     point.setAltitude(1352);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97007);
//     point.setLongitude(15.33286);
//     point.setAltitude(1361);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97486);
//     point.setLongitude(15.32258);
//     point.setAltitude(1351);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97951);
//     point.setLongitude(15.31022);
//     point.setAltitude(1356);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98129);
//     point.setLongitude(15.30226);
//     point.setAltitude(1360);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98177);
//     point.setLongitude(15.29698);
//     point.setAltitude(1361);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98168);
//     point.setLongitude(15.29069);
//     point.setAltitude(1357);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98029);
//     point.setLongitude(15.27928);
//     point.setAltitude(1362);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97878);
//     point.setLongitude(15.27091);
//     point.setAltitude(1372);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97741);
//     point.setLongitude(15.26563);
//     point.setAltitude(1388);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97632);
//     point.setLongitude(15.26346);
//     point.setAltitude(1391);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97194);
//     point.setLongitude(15.25761);
//     point.setAltitude(1415);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.9711);
//     point.setLongitude(15.25583);
//     point.setAltitude(1417);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97074);
//     point.setLongitude(15.2502);
//     point.setAltitude(1423);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.96996);
//     point.setLongitude(15.24714);
//     point.setAltitude(1426);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.96853);
//     point.setLongitude(15.24514);
//     point.setAltitude(1429);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.96741);
//     point.setLongitude(15.24428);
//     point.setAltitude(1431);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.96406);
//     point.setLongitude(15.24295);
//     point.setAltitude(1439);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95999);
//     point.setLongitude(15.23971);
//     point.setAltitude(1459);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95711);
//     point.setLongitude(15.23945);
//     point.setAltitude(1469);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95413);
//     point.setLongitude(15.23855);
//     point.setAltitude(1483);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95293);
//     point.setLongitude(15.23748);
//     point.setAltitude(1488);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.9514);
//     point.setLongitude(15.23484);
//     point.setAltitude(1497);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95063);
//     point.setLongitude(15.23203);
//     point.setAltitude(1503);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95009);
//     point.setLongitude(15.22975);
//     point.setAltitude(1512);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95117);
//     point.setLongitude(15.22772);
//     point.setAltitude(1516);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95308);
//     point.setLongitude(15.22154);
//     point.setAltitude(1537);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95482);
//     point.setLongitude(15.2196);
//     point.setAltitude(1544);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.95756);
//     point.setLongitude(15.21898);
//     point.setAltitude(1555);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.96327);
//     point.setLongitude(15.21864);
//     point.setAltitude(1565);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.96831);
//     point.setLongitude(15.21649);
//     point.setAltitude(1573);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.96975);
//     point.setLongitude(15.21259);
//     point.setAltitude(1579);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.9682);
//     point.setLongitude(15.20643);
//     point.setAltitude(1594);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.96836);
//     point.setLongitude(15.20553);
//     point.setAltitude(1599);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97138);
//     point.setLongitude(15.19937);
//     point.setAltitude(1620);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97436);
//     point.setLongitude(15.19712);
//     point.setAltitude(1637);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97524);
//     point.setLongitude(15.19435);
//     point.setAltitude(1644);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97464);
//     point.setLongitude(15.18916);
//     point.setAltitude(1657);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97554);
//     point.setLongitude(15.18559);
//     point.setAltitude(1667);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.9764);
//     point.setLongitude(15.18296);
//     point.setAltitude(1672);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97683);
//     point.setLongitude(15.17877);
//     point.setAltitude(1677);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97707);
//     point.setLongitude(15.17514);
//     point.setAltitude(1678);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.9787);
//     point.setLongitude(15.16959);
//     point.setAltitude(1687);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97863);
//     point.setLongitude(15.16497);
//     point.setAltitude(1690);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97988);
//     point.setLongitude(15.16279);
//     point.setAltitude(1693);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98286);
//     point.setLongitude(15.15974);
//     point.setAltitude(1708);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98516);
//     point.setLongitude(15.15577);
//     point.setAltitude(1725);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98773);
//     point.setLongitude(15.15111);
//     point.setAltitude(1738);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.99013);
//     point.setLongitude(15.14841);
//     point.setAltitude(1751);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.99314);
//     point.setLongitude(15.14094);
//     point.setAltitude(1778);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.99378);
//     point.setLongitude(15.13819);
//     point.setAltitude(1786);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.99372);
//     point.setLongitude(15.13628);
//     point.setAltitude(1791);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.99282);
//     point.setLongitude(15.13332);
//     point.setAltitude(1795);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.99164);
//     point.setLongitude(15.13135);
//     point.setAltitude(1798);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.99086);
//     point.setLongitude(15.13019);
//     point.setAltitude(1803);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98938);
//     point.setLongitude(15.12729);
//     point.setAltitude(1802);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98801);
//     point.setLongitude(15.12388);
//     point.setAltitude(1811);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98685);
//     point.setLongitude(15.1224);
//     point.setAltitude(1816);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98295);
//     point.setLongitude(15.11927);
//     point.setAltitude(1831);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.98071);
//     point.setLongitude(15.11699);
//     point.setAltitude(1837);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97659);
//     point.setLongitude(15.11554);
//     point.setAltitude(1857);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97453);
//     point.setLongitude(15.1126);
//     point.setAltitude(1872);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.97318);
//     point.setLongitude(15.11144);
//     point.setAltitude(1876);
//     track.addWaypoint(point);

//     point = new TrackpointImpl();
//     point.setLatitude(46.96975);
//     point.setLongitude(15.11187);
//     point.setAltitude(1884);
//     track.addWaypoint(point);

//     track_manager_.addTrack(track);

//   }

//----------------------------------------------------------------------
/**
 * The Action that triggers the track chart window.
 */

  class ShowTrackChartAction extends AbstractAction 
  {
        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public ShowTrackChartAction()
    {
      super(ACTION_SHOW_TRACKCHART);
    }

        //----------------------------------------------------------------------
        /**
         * Show the track chart
         * 
         * @param event the action event
         */

    public void actionPerformed(ActionEvent event)
    {
      TrackChartFrame frame_ = new TrackChartFrame(resources_.getString(KEY_TRACKCHART_PLUGIN_NAME));
      frame_.initialize(application_resources_,resources_,
                        plugin_support_.getTrackManager(),
                        plugin_support_.getUnitHelper());
      frame_.setVisible(true);
    }
  }
}
