/***********************************************************************
 *
 * Copyright (c) 2001 IICM, Graz University of Technology
 * Inffeldgasse 16c, A-8010 Graz, Austria.
 *
 * Copyright (c) 2003 Sven Boeckelmann
 * Langendreerstrasse 30, 44892 Bochum, Germany
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


package org.dinopolis.gpstool.plugin.importexport;

import com.bbn.openmap.Layer;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.dinopolis.gpstool.GPSMap;
import org.dinopolis.gpstool.GPSMapKeyConstants;
import org.dinopolis.gpstool.TrackManager;
import org.dinopolis.gpstool.gui.MouseMode;
import org.dinopolis.gpstool.track.ReadGPSMapTrackPlugin;
import org.dinopolis.gpstool.plugin.GuiPlugin;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.plugin.ReadTrackPlugin;
import org.dinopolis.gpstool.track.Track;
import org.dinopolis.gpstool.util.ExtensionFileFilter;
import org.dinopolis.gpstool.util.FileUtil;
import org.dinopolis.util.Debug;
import org.dinopolis.util.ResourceManager;
import org.dinopolis.util.Resources;
import org.dinopolis.util.gui.ActionStore;
import org.dinopolis.util.gui.MenuFactory;
import org.dinopolis.util.servicediscovery.ServiceDiscovery;




/**
 * Plugin to import/export tracks, routes, waypoints, etc. Uses
 * plugins itself to import export the different file formats.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 * @see org.dinopolis.gpstool.plugin.ReadTrackPlugin
 */

public class ImportExportPlugin implements GuiPlugin
{
  protected PluginSupport plugin_support_;
  protected Resources resources_;
  protected Resources application_resources_;
  protected ActionStore action_store_;
  protected TrackManager track_manager_;
  protected ServiceDiscovery service_discovery_;
  JFileChooser track_file_chooser_;
  JMenu plugin_menu_;

  
	/** the name of the resource file */
	private final static String RESOURCE_BUNDLE_NAME = "ImportExportPlugin";

	/** the name of the directory containing the resources */
	private final static String USER_RESOURCE_DIR_NAME = GPSMap.USER_RESOURCE_DIR_NAME;

	public static final String TRACK_ACTION_STORE_ID = RESOURCE_BUNDLE_NAME;

      // track actions 
  public final static String ACTION_IMPORT_TRACK = "import_track";

  public static final String KEY_IMPORTEXPORT_MENU_NAME = "importexport";
  public static final String KEY_LOCALIZE_IMPORT_TRACK_DIALOG_TITLE = "localize.import_track.dialog_title";
  public static final String KEY_IMPORTEXPORT_PLUGIN_IDENTIFIER = "importexport.plugin.identifier";
  public static final String KEY_IMPORTEXPORT_PLUGIN_VERSION = "importexport.plugin.version";
  public static final String KEY_IMPORTEXPORT_PLUGIN_NAME = "importexport.plugin.name";
  public static final String KEY_IMPORTEXPORT_PLUGIN_DESCRIPTION = "importexport.plugin.description";

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
    track_manager_ = support.getTrackManager();
    service_discovery_ = support.getServiceDiscovery();
		// load plugin resources:
		loadResources();

    
		// prepare the actionstore for the menu:
		action_store_ = ActionStore.getStore(TRACK_ACTION_STORE_ID);
		action_store_.addActions(new Action[] { new ImportTrackAction()});
    
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
		return(resources_.getString(KEY_IMPORTEXPORT_PLUGIN_IDENTIFIER));
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
		return ((float) resources_.getDouble(KEY_IMPORTEXPORT_PLUGIN_VERSION));
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
		return (resources_.getString(KEY_IMPORTEXPORT_PLUGIN_NAME));
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
		return (resources_.getString(KEY_IMPORTEXPORT_PLUGIN_DESCRIPTION));
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
		if (plugin_menu_ == null)
		{
			plugin_menu_ =
				(JMenu) MenuFactory.createMenuComponent(
					MenuFactory.KEY_MENUE_PREFIX,
					KEY_IMPORTEXPORT_MENU_NAME,
					resources_,
					action_store_);
		}
		return (plugin_menu_);
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
	}

	//----------------------------------------------------------------------
	/**
	 * returns null as this plugin does not provide any mouse modes.
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
	 * Returns null, as this plugin does not provide any layers.
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
//		layer_.setActive(active);
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
		return(true);
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
					ImportExportPlugin.class,
					RESOURCE_BUNDLE_NAME,
					USER_RESOURCE_DIR_NAME,
					Locale.getDefault());
		}
		catch (MissingResourceException mre)
		{
			if (Debug.DEBUG)
				Debug.println(
					"ImportExportPlugin",
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

//----------------------------------------------------------------------
/**
 * The Action that triggers import track mode. It uses plugins that
 * provide the functionality.
 *
 * @see org.dinopolis.gpstool.plugin.ReadTrackPlugin.
 */

  class ImportTrackAction extends AbstractAction 
  {
    Object[] plugins_;

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public ImportTrackAction()
    {
      super(ACTION_IMPORT_TRACK);
          // find all available track reader plugins:
          // (do not use a string here, so the compiler checks for typos)
      Object[] plugins = service_discovery_.getServices(
        org.dinopolis.gpstool.plugin.ReadTrackPlugin.class);
          // add ReadGPSMapTrackPlugin by hand:
      plugins_ = new Object[plugins.length + 1];
      plugins_[0] = new ReadGPSMapTrackPlugin();
      System.arraycopy(plugins,0,plugins_,1,plugins.length);
   
      if(Debug.DEBUG)
        Debug.println("plugin","plugins for reading tracks detected: "+Debug.objectToString(plugins_));

          // disable action, if no plugins found:
      if(plugins_.length == 0)
      {
        setEnabled(false);
      }
    }

        //----------------------------------------------------------------------
        /**
         * Import a track
         * 
         * @param event the action event
         */

    public void actionPerformed(ActionEvent event)
    {
      File[] chosen_files = null;
      if(track_file_chooser_ == null)
      {
        track_file_chooser_ = new JFileChooser();
        track_file_chooser_.setDialogTitle(resources_.getString(KEY_LOCALIZE_IMPORT_TRACK_DIALOG_TITLE));
        track_file_chooser_.setAcceptAllFileFilterUsed(false);
     
        track_file_chooser_.setMultiSelectionEnabled(true);
        track_file_chooser_.setFileHidingEnabled(false);
        String tracks_dirname = FileUtil.getAbsolutePath(
          application_resources_.getString(GPSMapKeyConstants.KEY_FILE_MAINDIR),
          application_resources_.getString(GPSMapKeyConstants.KEY_FILE_TRACK_DIR));
        track_file_chooser_.setCurrentDirectory(new File(tracks_dirname));

            // use all ReadTrackPlugin plugins to build extension file filters:
        ExtensionFileFilter filter;
        ReadTrackPlugin plugin;
        String[] extensions;
        boolean plugin_found = false;
        for(int plugin_count = 0; plugin_count < plugins_.length; plugin_count++)
        {
          plugin = (ReadTrackPlugin)plugins_[plugin_count];
          if(plugin != null)
          {
            plugin.initializePlugin(plugin_support_);
            filter = new ExtensionFileFilter();
            extensions = plugin.getContentFileExtensions();
            for(int extension_count = 0; extension_count < extensions.length; extension_count++)
              filter.addExtension(extensions[extension_count]);
            filter.setDescription(plugin.getContentDescription());
            filter.setAuxiliaryObject(plugin);
            track_file_chooser_.addChoosableFileFilter(filter);
            plugin_found = true;
          }
        }
        if(!plugin_found)
        {
              // TODO: open dialog for error:
          System.err.println("ERROR: no plugin found!");
          return;
        }
      }

      int result = track_file_chooser_.showOpenDialog(plugin_support_.getMainFrame());
      if(result == JFileChooser.APPROVE_OPTION)
      {
        ExtensionFileFilter filter = (ExtensionFileFilter)track_file_chooser_.getFileFilter();
        ReadTrackPlugin plugin = (ReadTrackPlugin)filter.getAuxiliaryObject();
        File file = track_file_chooser_.getSelectedFile();
        chosen_files = track_file_chooser_.getSelectedFiles();
            // add all tracks from all files:
        for(int file_count = 0; file_count < chosen_files.length; file_count++)
        {
          try
          {
                // create inputstream from file for plugin:
            Track[] tracks = plugin.getTracks(new BufferedInputStream(
                                                new FileInputStream(chosen_files[file_count])));
            for(int track_count = 0; track_count < tracks.length; track_count++)
            {
              track_manager_.addTrack(tracks[track_count]);
            }
          }
          catch(IOException ioe)
          {
            ioe.printStackTrace();
          }
        }
      }
    }
  }
}
