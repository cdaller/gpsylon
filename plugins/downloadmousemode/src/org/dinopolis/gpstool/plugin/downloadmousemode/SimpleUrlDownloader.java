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
import org.dinopolis.gpstool.GPSMapKeyConstants;
import org.dinopolis.gpstool.MapInfo;
import org.dinopolis.gpstool.plugin.MapRetrievalPlugin;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.util.ProgressListener;
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
      if(resources_.getBoolean(GPSMapKeyConstants.KEY_HTTP_PROXY_AUTHENTICATION_USE))
      {
        String proxy_userid =
          resources_.getString(GPSMapKeyConstants.KEY_HTTP_PROXY_AUTHENTICATION_USERNAME);
        String proxy_password =
          resources_.getString(GPSMapKeyConstants.KEY_HTTP_PROXY_AUTHENTICATION_PASSWORD);
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
          throw new IOException("Invalid mime type (expected 'image/*'): "
                                +mime_type+"\nPage is displayed in HTML frame.");
        }
        throw new IOException("Invalid mime type (expected 'image/*'): "
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



  protected abstract String getMapServerName();

  protected double getScaleFactor()
  {
    return(resources_.getDouble(DownloadMouseModeLayer.KEY_DOWNLOAD_MAP_SCALE_FACTOR_PREFIX
                                              + "."+getMapServerName()));
  }
  
  protected double getDownloadScale(double wanted_mapblast_scale)
  {
        // for map servers with different scale system than mapblast
    return(Math.round(wanted_mapblast_scale / getScaleFactor()));
  }
  
  protected double getCorrectedMapblastScale(double wanted_mapblast_scale)
  {
        // for map servers with different scale system than mapblast
    return(getDownloadScale(wanted_mapblast_scale) * getScaleFactor());
  }
  
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
