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

package org.dinopolis.gpstool.plugin.gpstool;

import com.bbn.openmap.Layer;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
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
import javax.swing.JOptionPane;
import org.dinopolis.gpstool.GPSMap;
import org.dinopolis.gpstool.TrackManager;
import org.dinopolis.gpstool.gpsinput.GPSDataProcessor;
import org.dinopolis.gpstool.gpsinput.GPSException;
import org.dinopolis.gpstool.gui.ImageFrame;
import org.dinopolis.gpstool.gui.MouseMode;
import org.dinopolis.gpstool.gui.NmeaDataTextFrame;
import org.dinopolis.util.gui.ProgressFrame;
import org.dinopolis.gpstool.plugin.GuiPlugin;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.util.ExtensionFileFilter;
import org.dinopolis.util.Debug;
import org.dinopolis.util.ResourceManager;
import org.dinopolis.util.Resources;
import org.dinopolis.util.gui.ActionStore;
import org.dinopolis.util.gui.MenuFactory;
import org.dinopolis.util.servicediscovery.ServiceDiscovery;

//----------------------------------------------------------------------
/**
 * This plugin provides functionality to communicate with the gps
 * device at a high level: upload/download tracks, routes, and
 * waypoints, show screenshot, show raw data window....
 * 
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GPSToolPlugin implements GuiPlugin
{
  JMenu gpstool_main_menu_;
	/** the resources of the gpstool plugin */
	protected Resources resources_;
	/** the resources of the GPSMap application */
	protected Resources application_resources_;
	/** the action store */
	protected ActionStore action_store_;

  protected PluginSupport plugin_support_;

  TrackManager track_manager_;
  GPSDataProcessor gps_data_processor_;
  ServiceDiscovery service_discovery_;

	// keys for resources:
	public static final String KEY_GPSTOOL_PLUGIN_IDENTIFIER =
		"gpstool.plugin.identifier";
	public static final String KEY_GPSTOOL_PLUGIN_VERSION =
		"gpstool.plugin.version";
	public static final String KEY_GPSTOOL_PLUGIN_NAME =
		"gpstool.plugin.name";
	public static final String KEY_GPSTOOL_PLUGIN_DESCRIPTION =
		"gpstool.plugin.description";
  public static final String KEY_LOCALIZE_GPS_SCREENSHOT_FRAME_TITLE = "localize.gps_screenshot_frame_title";
  public static final String KEY_LOCALIZE_DOWNLOAD_SCREENSHOT = "localize.download_screenshot";
  public static final String KEY_LOCALIZE_DOWNLOAD_TRACK = "localize.download_track";
  public static final String KEY_LOCALIZE_UPLOAD_TRACK = "localize.upload_track";
  public static final String KEY_LOCALIZE_DOWNLOAD_ROUTE = "localize.download_route";
  public static final String KEY_LOCALIZE_UPLOAD_ROUTE = "localize.upload_route";
  public static final String KEY_LOCALIZE_DOWNLOAD_WAYPOINT = "localize.download_waypoint";
  public static final String KEY_LOCALIZE_UPLOAD_WAYPOINT = "localize.upload_waypoint";
  public static final String KEY_LOCALIZE_DISPLAY_DEVICE_INFO = "localize.display_device_info";

	public static final String KEY_GPSTOOL_MAIN_MENU_NAME = "gpstoolplugin";

	/** the name of the resource file */
	private final static String RESOURCE_BUNDLE_NAME = "GPSToolPlugin";

	/** the name of the directory containing the resources */
	private final static String USER_RESOURCE_DIR_NAME = GPSMap.USER_RESOURCE_DIR_NAME;

	public static final String GPSTOOL_ACTION_STORE_ID = RESOURCE_BUNDLE_NAME;

      // actions 
  public final static String ACTION_VIEW_GPS_RAW_DATA = "view_gps_raw_data";
  public final static String ACTION_UPLOAD_TO_GPS = "upload_to_gps";
  public final static String ACTION_DOWNLOAD_FROM_GPS = "download_from_gps";
  public final static String ACTION_GPS_SCREENSHOT = "gps_screenshot";
  public final static String ACTION_DISPLAY_DEVICE_INFO = "display_device_info";
  
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
    gps_data_processor_ = support.getGPSDataProcessor();
		application_resources_ = support.getResources();
    service_discovery_ = support.getServiceDiscovery();
    
		// load map manager resources:
		if (Debug.DEBUG)
			Debug.println("gpstoolplugin_init", "loading resources");
		loadResources();

		// prepare the actionstore for the menu:
		action_store_ = ActionStore.getStore(GPSTOOL_ACTION_STORE_ID);
		action_store_.addActions(new Action[] {new ViewGPSDataAction(),
                                           new UploadToGPSAction(),
                                           new DownloadFromGPSAction(),
                                           new ScreenShotAction(),
																					 new DisplayDeviceInfoAction()
    });

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
		return(resources_.getString(KEY_GPSTOOL_PLUGIN_IDENTIFIER));
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
		return ((float) resources_.getDouble(KEY_GPSTOOL_PLUGIN_VERSION));
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
		return (resources_.getString(KEY_GPSTOOL_PLUGIN_NAME));
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
		return (resources_.getString(KEY_GPSTOOL_PLUGIN_DESCRIPTION));
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
		if (gpstool_main_menu_ == null)
		{
			gpstool_main_menu_ = MenuFactory.createMenu(
        MenuFactory.KEY_MENUE_PREFIX,
        KEY_GPSTOOL_MAIN_MENU_NAME,
        resources_,
        action_store_);
		}
		return (gpstool_main_menu_);
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
	 * Returns null, as this plugin does not provide any mouse modes.
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
	 * Returns null as this plugin does not paint any information in the map.
	 *
	 * @return null
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
					GPSToolPlugin.class,
					RESOURCE_BUNDLE_NAME,
					USER_RESOURCE_DIR_NAME,
					Locale.getDefault());
		}
		catch (MissingResourceException mre)
		{
			if (Debug.DEBUG)
				Debug.println(
					"GPSToolPlugin",
					mre.toString() + '\n' + Debug.getStackTrace(mre));
			System.err.println(
				"GPSToolPlugin: resource file '"
					+ RESOURCE_BUNDLE_NAME
					+ "' not found");
			System.err.println(
				"please make sure that this file is within the classpath !");
			System.exit(1);
		}
	}

//----------------------------------------------------------------------
// Action Classes
//----------------------------------------------------------------------
  
      //----------------------------------------------------------------------
      /**
       * The Action that triggers the display for the device info
       */

  class DisplayDeviceInfoAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public DisplayDeviceInfoAction()
    {
      super(ACTION_DISPLAY_DEVICE_INFO);
    }

        //----------------------------------------------------------------------
        /**
         * Open the frame to show gps data
         * 
         * @param event the action event
         */

    public void actionPerformed(ActionEvent event)
    {
      if(gps_data_processor_ != null)
      {
				try
				{
          String title = resources_.getString(KEY_LOCALIZE_DISPLAY_DEVICE_INFO);
					String[] info = gps_data_processor_.getGPSInfo();
					StringBuffer buffer = new StringBuffer();
					for(int count = 0; count < info.length; count++)
					{
						buffer.append(info[count]).append("\n");
					}
          JOptionPane.showMessageDialog(plugin_support_.getMainFrame(), 
																				buffer.toString(), title, 
																				JOptionPane.INFORMATION_MESSAGE);
					
				}
				catch(GPSException ge)
				{
					ge.printStackTrace();
				}
      }
    }
  }

      //----------------------------------------------------------------------
      /**
       * The Action that triggers the display for the gps data frame
       */

  class ViewGPSDataAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public ViewGPSDataAction()
    {
      super(ACTION_VIEW_GPS_RAW_DATA);
    }

        //----------------------------------------------------------------------
        /**
         * Open the frame to show gps data
         * 
         * @param event the action event
         */

    public void actionPerformed(ActionEvent event)
    {
      if(gps_data_processor_ != null)
      {
        NmeaDataTextFrame frame = new NmeaDataTextFrame(application_resources_,gps_data_processor_);
        frame.setVisible(true);
      }
    }
  }


      //----------------------------------------------------------------------
      /**
       * The Action that triggers the download of tracks, routes,
       * waypoints from the gps device.
       */

  class DownloadFromGPSAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public DownloadFromGPSAction()
    {
      super(ACTION_DOWNLOAD_FROM_GPS);
    }

        //----------------------------------------------------------------------
        /**
         * Open the frame to select what should be downloaded.
         * 
         * @param event the action event
         */

    public void actionPerformed(ActionEvent event)
    {
      if(track_manager_ != null)
      {
        Thread background_thread = new Thread()
          {
            public void run()
            {
              try
              {
                ProgressFrame progress_frame = new ProgressFrame(
                  resources_.getString(KEY_LOCALIZE_DOWNLOAD_TRACK),
                  plugin_support_.getMainFrame());
                gps_data_processor_.addProgressListener(progress_frame);
                track_manager_.addTracksFromGPSDevice();
                gps_data_processor_.removeProgressListener(progress_frame);
              }
              catch(GPSException ge)
              {
                ge.printStackTrace();
              }
            }
          };
        background_thread.start();
      }
    }
  }
      //----------------------------------------------------------------------
      /**
       * The Action that triggers the upload of tracks, routes,
       * waypoints to the gps device.
       */

  class UploadToGPSAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public UploadToGPSAction()
    {
      super(ACTION_UPLOAD_TO_GPS);
    }

        //----------------------------------------------------------------------
        /**
         * Open the frame to select what should be uploaded.
         * 
         * @param event the action event
         */

    public void actionPerformed(ActionEvent event)
    {
      if(track_manager_ != null)
      {
        Thread background_thread = new Thread()
          {
            public void run()
            {
              try
              {
                ProgressFrame progress_frame = new ProgressFrame(
                  resources_.getString(KEY_LOCALIZE_UPLOAD_TRACK),
                  plugin_support_.getMainFrame());
                gps_data_processor_.addProgressListener(progress_frame);
                track_manager_.uploadTracksToGPSDevice();
                gps_data_processor_.removeProgressListener(progress_frame);
              }
              catch(GPSException ge)
              {
                ge.printStackTrace();
              }
            }
          };
        background_thread.start();
      }
    }
  }

      //----------------------------------------------------------------------
      /**
       * The Action that triggers the upload of tracks, routes,
       * waypoints to the gps device.
       */

  class ScreenShotAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public ScreenShotAction()
    {
      super(ACTION_GPS_SCREENSHOT);
    }

        //----------------------------------------------------------------------
        /**
         * Open the frame to select what should be uploaded.
         * 
         * @param event the action event
         */

    public void actionPerformed(ActionEvent event)
    {
      if(gps_data_processor_ != null)
      {
        Thread background_thread = new Thread()
          {
            public void run()
            {
              try
              {
                ProgressFrame progress_frame = new ProgressFrame(
                  resources_.getString(KEY_LOCALIZE_DOWNLOAD_SCREENSHOT),
                  plugin_support_.getMainFrame());
                gps_data_processor_.addProgressListener(progress_frame);
                BufferedImage image = gps_data_processor_.getScreenShot();
                gps_data_processor_.removeProgressListener(progress_frame);
                ImageFrame image_frame = new ImageFrame(application_resources_,
                                                        resources_.getString(KEY_LOCALIZE_GPS_SCREENSHOT_FRAME_TITLE),
                                                        image,service_discovery_);
                image_frame.setImage(image);
                image_frame.setVisible(true);
              }
              catch(GPSException ge)
              {
                ge.printStackTrace();
              }
            }
          };
        background_thread.start();
      }
    }
  }

}
