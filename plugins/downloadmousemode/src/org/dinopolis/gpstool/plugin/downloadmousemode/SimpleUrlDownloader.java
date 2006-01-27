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


package org.dinopolis.gpstool.plugin.downloadmousemode;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Locale;
import org.dinopolis.gpstool.GpsylonKeyConstants;
import org.dinopolis.gpstool.map.MapInfo;
import org.dinopolis.gpstool.plugin.MapRetrievalPlugin;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.util.ProgressListener;
import org.dinopolis.util.Debug;
import org.dinopolis.util.Resources;
import org.dinopolis.util.gui.HTMLViewerFrame;



//----------------------------------------------------------------------
/**
 * A downloader that uses an URL to download a map. The url to use is
 * stored in the resources. The scale factor to use is also stored in
 * the resources. Derived classes only have to implement the
 * getMapServerName() method. This name is used to create the resource
 * key to find the url and the scale factor.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public abstract class SimpleUrlDownloader implements MapRetrievalPlugin
{
  Resources resources_;
  public static final int BUFFER_SIZE = 4096;
  public static final String PROGRESS_LISTENER_ID = "download map";
  
  public SimpleUrlDownloader()
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
    return(getMapServerName()+"Downloader");
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
    return(getMapServerName()+" Downloader");
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
    return("Downloads maps from "+getMapServerName()+" server.");
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
 * @return the mapblast scale the plugin uses for the given parameters.
 */
  public double getMapScale(double latitude, double longitude,
                            double wanted_mapblast_scale, int image_height,
                            int image_width)
  {
    return(getCorrectedMapblastScale(wanted_mapblast_scale));
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
          // calculate the best scale for this mapserver:
      double mapserver_scale = getDownloadScale(wanted_mapblast_scale);
          // create the url to fetch the image:
      URL url = new URL(getUrl(latitude,longitude,mapserver_scale,image_width, image_height));
      
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

          // set some request properties
      connection = setRequestProperties(connection);
      
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
        throw new IOException("Invalid mime type (expected 'image/*'): received "
                              +mime_type);
      }

      int content_length = connection.getContentLength();
      if(content_length < 0)
        progress_listener.actionStart(PROGRESS_LISTENER_ID,0,Integer.MIN_VALUE);
      else
        progress_listener.actionStart(PROGRESS_LISTENER_ID,0,content_length);

          // create MapInfo object to return:
      String extension = mime_type.substring(mime_type.indexOf('/')+1);
      String filename = file_path_wo_extension+extension;
      MapInfo map_info = new MapInfo();
      map_info.setLatitude(latitude);
      map_info.setLongitude(longitude);
      map_info.setScale((float)getCorrectedMapblastScale(wanted_mapblast_scale));
      map_info.setWidth(image_width);
      map_info.setHeight(image_height);
      map_info.setFilename(filename);
          
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



//----------------------------------------------------------------------
/**
 * Set some request properties for the url connection. This method may
 * be overwritten to set some additional http header fields. This
 * implementation does nothing.
 *
 * @param connection the connection that is used to request the url
 * for the map.
 * @return the same (or new connection) with some request properties
 * set.
 */
  protected URLConnection setRequestProperties(URLConnection connection)
  {
    return(connection);
  }



//----------------------------------------------------------------------
/**
 * Return the server name. This information is used to create the name
 * of the plugin and the download dialog. 
 *
 * @return the name of the server.
 */
  protected abstract String getMapServerName();

//----------------------------------------------------------------------
/**
 * Return the scale of the maps downloaded with this downloader. By
 * default, the scale factor is looked up in the resources with the
 * following key:
 * <code>DownloadMouseModeLayer.KEY_DOWNLOAD_MAP_SCALE_FACTOR_PREFIX
 *                                         + "."+getMapServerName())</code>
 *
 * @return the scale factor to get the appropriate mapblast scale.
 */
  protected double getScaleFactor()
  {
    return(resources_.getDouble(DownloadMouseModeLayer.KEY_DOWNLOAD_MAP_SCALE_FACTOR_PREFIX
                                              + "."+getMapServerName()));
  }
  
//----------------------------------------------------------------------
/**
 * Return the scale of the maps downloaded with this downloader. This
 * value is dependent on the map server.
 * @param wanted_mapblast_scale the scale the user selected
 *
 * @return the scale the map server is able to return.
 */
  protected double getDownloadScale(double wanted_mapblast_scale)
  {
//    System.out.println("map scale: "+Math.round(wanted_mapblast_scale / getScaleFactor()));
        // for map servers with different scale system than mapblast
    return(Math.round(wanted_mapblast_scale / getScaleFactor()));
  }
  
//----------------------------------------------------------------------
/**
 * Return the scale of the maps downloaded with this downloader.
 * @param wanted_mapblast_scale the scale the user selected
 *
 * @return the mapblast scale the map server is able to return.
 */
  protected double getCorrectedMapblastScale(double wanted_mapblast_scale)
  {
        // for map servers with different scale system than mapblast
    return(getDownloadScale(wanted_mapblast_scale) * getScaleFactor());
  }
  
//----------------------------------------------------------------------
/**
 * Return the url to use to download a map for the given
 * parameters. This implementation gets the basic url from the
 * resources with the key:
 * <code>DownloadMouseModeLayer.KEY_DOWNLOAD_MAP_URL_PREFIX+"."+getMapServerName()</code>
 * and uses this string as a <code>MessageFormat<code> and replaces
 * the first param of the MessageFormat with the latitude, the second
 * with the longitude, the third with the scale, the fourth with the
 * width and the fifth with the height of the
 * image. E.g. <code>http\://localhost/getmap.cgi?latitude={0,number,#.########}&longitude={1,number,#.########}&scale={2,number,#}&width={3,number,#}&height={4,number,#}</code>.
 *
 * @param latitude the latitude of the center of the map.
 * @param longitude the longitude of the center of the map.
 * @param scale the scale used by the map server
 * @param width the width of the image in pixels
 * @param height the height of the image in pixels
 *
 * @return the url to use to request the image from the map server.
 */
  protected String getUrl(double latitude, double longitude, double scale, int width, int height)
  {
    String url_message = resources_.getString(
      DownloadMouseModeLayer.KEY_DOWNLOAD_MAP_URL_PREFIX+"."+getMapServerName());
    Object[] params = new Object[] {new Double(latitude), 
                                    new Double(longitude),
                                    new Float(scale),
                                    new Integer(width), 
                                    new Integer(height)};
    
    MessageFormat message_format = new MessageFormat(url_message,Locale.US); // 'US' for the decimal points!
    return(message_format.format(params));
  }
  
}
