/***********************************************************************
 * @(#)$RCSfile$   $Revision$$Date$
 *
 * Copyright (c) 2002 IICM, Graz University of Technology
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


package org.dinopolis.gpstool.plugin.readgpx;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import org.dinopolis.gpstool.Gpsylon;
import org.dinopolis.gpstool.GpsylonKeyConstants;
import org.dinopolis.gpstool.gpx.ReadGPX;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.plugin.ReadTrackPlugin;
import org.dinopolis.gpstool.track.Track;
import org.dinopolis.util.Debug;
import org.dinopolis.util.ResourceManager;
import org.dinopolis.util.Resources;
import java.util.List;

//----------------------------------------------------------------------
/**
 * This plugin reads track data from a stream (ususally from a file)
 * and provides one or more tracks (lists of {@link
 * org.dinopolis.gpstool.util.geoscreen.GeoScreenTrackPoint}
 * objects). The track format is like gpsdrive writes its tracks
 * (latitude longitude altitude date) - separated with spaces.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class ReadGPXPlugin extends ReadGPX implements ReadTrackPlugin, GpsylonKeyConstants 
{

  Resources resources_;

  	/** the name of the resource file */
	private final static String RESOURCE_BUNDLE_NAME = "ReadGPXPlugin";

	/** the name of the directory containing the resources */
	private final static String USER_RESOURCE_DIR_NAME = Gpsylon.USER_RESOURCE_DIR_NAME;

      // resource keys:
  public static final String KEY_READGPX_PLUGIN_IDENTIFIER = "readgpx.plugin.identifier";
  public static final String KEY_READGPX_PLUGIN_VERSION = "readgpx.plugin.version";
  public static final String KEY_READGPX_PLUGIN_NAME = "readgpx.plugin.name";
  public static final String KEY_READGPX_PLUGIN_DESCRIPTION = "readgpx.plugin.description";
  public static final String KEY_READGPX_FILE_EXTENSION  = "readgpx.file.extension";
  public static final String KEY_READGPX_FILE_DESCRIPTIVE_NAME  = "readgpx.file.descriptive_name";

  public ReadGPXPlugin()
  {
  }


//----------------------------------------------------------------------
/**
 * Initialize the plugin and pass a PluginSupport that provides
 * objects, the plugin may use.
 *
 * @param support the PluginSupport object
 */
  public void initializePlugin(PluginSupport support)
  {
    loadResources();
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
 * Returns a short description of the track data that may be used e.g. in
 * a file chooser. If possible, the description should be localized.
 *
 * @return The short description of the content.
 */

  public String getContentDescription()
  {
    return(resources_.getString(KEY_READGPX_FILE_DESCRIPTIVE_NAME));
  }
  
//----------------------------------------------------------------------
/**
 * Returns possible file extensions the content. This information
 * may be used in a file chooser as a filter (e.g. ["jpg","jpeg"]).
 *
 * @return The file extensions to use for this kind of data.
 */

  public String[] getContentFileExtensions()
  {
    return(resources_.getStringArray(KEY_READGPX_FILE_EXTENSION));
  }
  

//----------------------------------------------------------------------
/**
 * Parse the given input stream and return tracks. If no tracks could
 * be read, an empty array (length of 0) is returned (not null!).
 *
 * @param in the inputstream to read the data from.
 * @return an array of {@link
 * org.dinopolis.gpstool.gui.layer.track.Track} objects.
 * @throws IOException if an error occurs during reading.  */
  public Track[] getTracks(InputStream in)
    throws IOException
  {
    super.parseInputStream(in);
    List tracks = getTracks();
    Track[] track_array = (Track[])tracks.toArray(new Track[0]);
//     for(int index = 0; index < track_array.length; index++)
//     {
//       System.out.println("ReadGPXPlugin.getTracks: "+track_array[index].getIdentification());
//       List trackpoints = track_array[index].getWaypoints();
//       for(int index2 = 0; index2 < trackpoints.size(); index2++)
//       {
//         System.out.println("  Waypoint: "+trackpoints.get(index2));
//       }
//     }
    return(track_array);
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
    return(resources_.getString(KEY_READGPX_PLUGIN_IDENTIFIER));
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
    return((float)resources_.getDouble(KEY_READGPX_PLUGIN_VERSION));
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
    return(resources_.getString(KEY_READGPX_PLUGIN_NAME));
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
    return(resources_.getString(KEY_READGPX_PLUGIN_DESCRIPTION));
  }

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
					ReadGPXPlugin.class,
					RESOURCE_BUNDLE_NAME,
					USER_RESOURCE_DIR_NAME,
					Locale.getDefault());
		}
		catch (MissingResourceException mre)
		{
			if (Debug.DEBUG)
				Debug.println(
					"ReadgpxPlugin",
					mre.toString() + '\n' + Debug.getStackTrace(mre));
			System.err.println(
				"ReadgpxPlugin: resource file '"
					+ RESOURCE_BUNDLE_NAME
					+ ".properties' not found");
			System.err.println(
				"please make sure that this file is within the classpath !");
			System.exit(1);
		}
	}
}
