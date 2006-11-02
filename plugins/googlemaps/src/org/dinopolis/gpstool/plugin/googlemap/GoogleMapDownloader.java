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


package org.dinopolis.gpstool.plugin.googlemap;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.dinopolis.gpstool.GpsylonKeyConstants;
import org.dinopolis.gpstool.map.MapInfo;
import org.dinopolis.gpstool.plugin.MapRetrievalPlugin;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.plugin.downloadmousemode.DownloadMouseModeLayer;
import org.dinopolis.gpstool.plugin.googlemap.util.SimplePoint;
import org.dinopolis.gpstool.plugin.googlemap.util.Tile;
import org.dinopolis.gpstool.util.HttpRequester;
import org.dinopolis.util.Debug;
import org.dinopolis.util.ProgressListener;
import org.dinopolis.util.Resources;
import org.dinopolis.util.gui.HTMLViewerFrame;



//----------------------------------------------------------------------
/**
 * A downloader download a map from the googlemap server.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GoogleMapDownloader implements MapRetrievalPlugin
{
  protected Resources resources_;
  public static final int BUFFER_SIZE = 4096;
  public static final String PROGRESS_LISTENER_ID = "download map";

  public static final String HOST_NAME = "mt3.google.com";
  public static final String GOOGLE_MAPS_URL = "http://mt3.google.com/mt?n=404&v=w2.25&x={0,number,#}&y={1,number,#}&zoom={2,number,#}";
  protected static final int[] POSSIBLE_GOOGLE_SCALES = {
    5708800, 2854400, 1427200, 713600, 356800, 178400, 89200, 44600, 22300, 11200, 5600, 2825, 1412};
  protected static final int[] GOOGLE_ZOOM_LEVELS = {7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 71, 18, 19};

  protected HttpRequester web_request_;

  protected String session_key_;
  protected String cookie_;
  protected String session_id_;
  protected String session_pn_;

  public GoogleMapDownloader()
  {
  }

  public void initializePlugin(PluginSupport support)
  {
    resources_ = support.getResources();
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
    return("Google Maps Downloader");
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
    return(0.01f);
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
    return("GoogleMaps Downloader");
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
    return("Downloads maps from Google Maps server.");
  }

//----------------------------------------------------------------------
/**
 * Returns the scale the plugin would use for the given parameters.
 *
 * @param latitude the latitude of the center of the map to retrieve.
 * @param longitude the longitude of the center of the map to retrieve.
 * @param wanted_mapblast_scale the scale to download. The base for
 * this value is the scale used by the mapblast server. The plugin has
 * to calculate this value to match the scale used by its map source!
 * @param image_width the width of the map to download.
 * @param image_height the height of the map to download.
 * @return the scale the plugin uses for the given parameters.
 */
  public double getMapScale(double latitude, double longitude,
                             double wanted_mapblast_scale, int image_height,
                             int image_width)
  {
    return POSSIBLE_GOOGLE_SCALES[getZoomLevelIndex(wanted_mapblast_scale)];
  }

  /**
   * Returns the index of the array of the possible google zoom levels.
   * @param mapblast_scale the mapblast scale
   * @return the index.
   */
  private int getZoomLevelIndex(double mapblast_scale)
  {
    for(int index = 0; index < POSSIBLE_GOOGLE_SCALES.length; index++)
    {
      if(mapblast_scale >= POSSIBLE_GOOGLE_SCALES[index])
      {
        return index;
      }
    }
    return 0;

  }



//----------------------------------------------------------------------
/**
 * Retrieve a map with the given parameters and store it in a file.
 *
 * @param latitude the latitude of the center of the map to retrieve.
 * @param longitude the longitude of the center of the map to retrieve.
 * @param wanted_mapblast_scale the scale to download. The base for
 * this value is the scale used by the mapblast server. The plugin has
 * to calculate this value to match the scale used by its map source!
 * @param image_width the width of the map to download.
 * @param image_height the height of the map to download.
 * @param file_path_wo_extension the path of the file, the map should
 * be store to. This path contains the directory and the filename, but
 * not the extension of the file, as the extension may depend on the
 * fileformat the plugin retrieves.
 * @param progress_listener a progress listener to be informed about
 * the progress on retrieval.
 * @return a MapInfo object that holds the complete information about
 * the downloaded map. This includes the complete filename (with
 * extension) and the scale of the image in mapblast format!
 * @throws IOException if an error on reading or writing of the map
 * occured.
 */
  public MapInfo getMap(double latitude, double longitude, double wanted_mapblast_scale,
                        int image_width, int image_height, String file_path_wo_extension,
                        ProgressListener progress_listener)
    throws IOException
  {
    try
    {
      if(web_request_ == null)
      {
        web_request_ = new HttpRequester(HOST_NAME);
      }


      int zoom_index = getZoomLevelIndex(wanted_mapblast_scale);
      int google_zoom_level = GOOGLE_ZOOM_LEVELS[zoom_index];
      double mapblast_scale = POSSIBLE_GOOGLE_SCALES[zoom_index];

      Tile tile = new Tile(latitude, longitude, google_zoom_level);
      SimplePoint coords = tile.getTileLatLong();
      SimplePoint google_xy = tile.getTileCoord();

      MapInfo map_info = new MapInfo();
      map_info.setLatitude(coords.getX());
      map_info.setLongitude(coords.getY());
      map_info.setScale((float)mapblast_scale);
      map_info.setWidth(256);
      map_info.setHeight(256);
      map_info.setFilename(file_path_wo_extension+"png");

      // start url:
      Object[] params = new Object[] {new Integer(google_xy.getX()),new Integer(google_xy.getY()),new Integer(google_zoom_level)};
      MessageFormat message_format = new MessageFormat(GOOGLE_MAPS_URL,Locale.US); // 'US' for the decimal points!
      String url_string = message_format.format(params);
      URL url = new URL(url_string);

      if(Debug.DEBUG)
        Debug.println("map_download","loading map from url: "+url);
          // create connection to mapserver:
      URLConnection connection = url.openConnection();

          // set proxy authentication if necessary:
      if(resources_.getBoolean(GpsylonKeyConstants.KEY_HTTP_PROXY_AUTHENTICATION_USE))
      {
        String proxy_userid =
          resources_.getString(GpsylonKeyConstants.KEY_HTTP_PROXY_AUTHENTICATION_USERNAME);
        String proxy_password =
          resources_.getString(GpsylonKeyConstants.KEY_HTTP_PROXY_AUTHENTICATION_PASSWORD);
        String auth_string = proxy_userid +":" + proxy_password;

        auth_string = "Basic " + new sun.misc.BASE64Encoder().encode(auth_string.getBytes());
        connection.setRequestProperty("Proxy-Authorization", auth_string);
      }

      connection.connect();
          // is the image an image?:
      String mime_type = connection.getContentType().toLowerCase();
      if(!mime_type.startsWith("image"))
      {
            // handle wrong mime type. the most probable error is a
            // 404-not found or an invalid proxy settings:
//           for (int i =1; connection.getHeaderFieldKey(i) != null; i++)
//           {
//             System.out.println("header" + connection.getHeaderFieldKey(i)
//                                +"="+connection.getHeaderField(connection.getHeaderFieldKey(i)));
//           }

        if(mime_type.startsWith("text"))
        {
          HTMLViewerFrame viewer = new HTMLViewerFrame(url);
          viewer.setSize(640,480);
          viewer.setTitle("ERROR on loading url: "+url);
          viewer.setVisible(true);
          throw new IOException("Invalid mime type (expected 'image/*'): received "
                                +mime_type+"\nPage is displayed in HTML frame.");
        }
        throw new IOException("Invalid mime type (expected 'image/*'): received " + mime_type);
      }

      int content_length = connection.getContentLength();
      if(content_length < 0)
        progress_listener.actionStart(PROGRESS_LISTENER_ID,0,Integer.MIN_VALUE);
      else
        progress_listener.actionStart(PROGRESS_LISTENER_ID,0,content_length);

          // create MapInfo object to return:
      String extension = mime_type.substring(mime_type.indexOf('/')+1);
      String filename = file_path_wo_extension+extension;

      FileOutputStream out = new FileOutputStream(filename);

      byte[] buffer = new byte[BUFFER_SIZE];
      BufferedInputStream in = new BufferedInputStream(connection.getInputStream(), BUFFER_SIZE);

      int sum_bytes = 0;
      int num_bytes = 0;
          // Read (and print) till end of file
      while ((num_bytes = in.read(buffer)) != -1)
      {
        out.write(buffer, 0, num_bytes);
//          System.out.println(getName()+": read and wrote "+num_bytes+" bytes");
        sum_bytes += num_bytes;
        progress_listener.actionProgress(PROGRESS_LISTENER_ID,sum_bytes);
      }

      progress_listener.actionEnd(PROGRESS_LISTENER_ID);

      in.close();
      out.close();
      return(map_info);
//        downloadTerminated(map_info,DOWNLOAD_SUCCESS,
//                           sum_bytes+" "+resources_.getString(KEY_LOCALIZE_BYTES_READ));
    }
    catch(NoRouteToHostException nrhe)
    {
      nrhe.printStackTrace();
      progress_listener.actionEnd(PROGRESS_LISTENER_ID);
      String message = nrhe.getMessage() + ":\n"
                       + resources_.getString(DownloadMouseModeLayer.KEY_LOCALIZE_MESSAGE_DOWNLOAD_ERROR_NO_ROUTE_TO_HOST_MESSAGE);
//      System.out.println("NoRouteToHostException message: "+nrhe.getMessage());
      throw new IOException(message);
    }
    catch(FileNotFoundException fnfe)
    {
      fnfe.printStackTrace();
      progress_listener.actionEnd(PROGRESS_LISTENER_ID);
      String message = fnfe.getMessage() + ":\n"
                       + resources_.getString(DownloadMouseModeLayer.KEY_LOCALIZE_MESSAGE_DOWNLOAD_ERROR_FILE_NOT_FOUND_MESSAGE);
//      System.out.println("FileNotFoundException message: "+message);
      throw new IOException(message);
    }
    catch(Exception e)
    {
      progress_listener.actionEnd(PROGRESS_LISTENER_ID);
      e.printStackTrace();
      String message = e.getMessage();
      if(message == null)
      {
        Throwable cause = e.getCause();
        if(cause != null)
          message = cause.getMessage();
      }
//      System.out.println("other exception message: "+message);
      throw new IOException(message);
    }
  }

  /**
   * Saves the content of the web response to the given file.
   * @param web_response the response.
   * @param filename the filename.
   * @return the number of bytes written.
   * @throws IOException if an error occurs.
   */
  protected int saveContentToFile(Map web_response, String filename)
    throws IOException
  {
    OutputStreamWriter out_stream = new OutputStreamWriter(new FileOutputStream(filename));
    String content = (String)web_response.get("Content");
    out_stream.write(content);
    out_stream.close();
    return(content.length());
  }
}
