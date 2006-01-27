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

import java.io.*;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.util.*;

import org.dinopolis.gpstool.GpsylonKeyConstants;
import org.dinopolis.gpstool.map.MapInfo;
import org.dinopolis.gpstool.plugin.MapRetrievalPlugin;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.util.HttpRequester;
import org.dinopolis.util.*;



//----------------------------------------------------------------------
/**
 * A downloader download a map from the mappoint server. As the
 * mappoint server does not allow simple url requests, a couple of
 * steps are necessary. Thanks to Alex Koralewski for providing the
 * way to do it!
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class MappointDownloader implements MapRetrievalPlugin
{
  protected Resources resources_;
  public static final String PROGRESS_LISTENER_ID = "download map";

//  public static final float MAPBLAST2MAPPOINT_SCALE_FACTOR = 1.0f;

  public static final String HOST_NAME = "mappoint.msn.com";
  protected static final int[] POSSIBLE_MAPPOINT_SCALES = {1,3,6,12,25,50,150,800,2000,7000,12000};
      // 500 gives the same result as 800
      // 300 gives the same result as 800
      // 10000 gives the same result as 12000 ....

  protected HttpRequester web_request_;

  protected String session_key_;
  protected String cookie_;
  protected String session_id_;
  protected String session_pn_;
  
  public MappointDownloader()
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

  protected String getMapServerName()
  {
    return("Mappoint "+getLocation());
  }

  protected String getLocation()
  {
    return("EUR");
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
    double scale_factor = 1109.5; // works for 1280x1024
    double min_diff = wanted_mapblast_scale;
    int mappoint_scale = POSSIBLE_MAPPOINT_SCALES[0];
    double tmp_mapblast_scale;
    double diff;
        // find closest mappoint scale to wanted mapblast scale:
    for(int index = 0; index < POSSIBLE_MAPPOINT_SCALES.length; index++)
    {
      tmp_mapblast_scale = POSSIBLE_MAPPOINT_SCALES[index]*scale_factor;
      diff = Math.abs(wanted_mapblast_scale - tmp_mapblast_scale);
      if(diff < min_diff)
      {
        mappoint_scale = POSSIBLE_MAPPOINT_SCALES[index];
        min_diff = diff;
      }
    }
    return(mappoint_scale * scale_factor);
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

          // TODO do not get session id, key, cookie every
          // time. Enough to do it the first time!

          // TODO calculate scale!

          // this works for 50000 maps! for 500000 it does not!
//       double scale_factor1 =  1529.6575;
//       double scale_factor2 =  1.10134;
//       int mappoint_scale = (int)Math.round(wanted_mapblast_scale / scale_factor1);
//       float real_mapblast_scale = (float)Math.round(wanted_mapblast_scale * scale_factor2);


      double scale_factor = 1109.5; // works for 1280x1024
      double min_diff = wanted_mapblast_scale;
      int mappoint_scale = POSSIBLE_MAPPOINT_SCALES[0];
      double tmp_mapblast_scale;
      double diff;
          // find closest mappoint scale to wanted mapblast scale:
      for(int index = 0; index < POSSIBLE_MAPPOINT_SCALES.length; index++)
      {
        tmp_mapblast_scale = POSSIBLE_MAPPOINT_SCALES[index]*scale_factor;
        diff = Math.abs(wanted_mapblast_scale - tmp_mapblast_scale);
        if(diff < min_diff)
        {
          mappoint_scale = POSSIBLE_MAPPOINT_SCALES[index];
          min_diff = diff;
        }
      }
      double real_mapblast_scale = mappoint_scale * scale_factor;
      
      MapInfo map_info = new MapInfo();
      map_info.setLatitude(latitude);
      map_info.setLongitude(longitude);
      map_info.setScale((float)real_mapblast_scale);
      map_info.setWidth(image_width);
      map_info.setHeight(image_height);
      map_info.setFilename(file_path_wo_extension+"gif");
      
      Map request_header = new TreeMap();
      request_header.put("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 4.0)");
      request_header.put("Keep-Alive","300");
      request_header.put("Connection","keep-alive");
      request_header.put("Host",HOST_NAME);
      
          // set proxy authentication if necessary:
      if(resources_.getBoolean(GpsylonKeyConstants.KEY_HTTP_PROXY_AUTHENTICATION_USE))
      {
        String proxy_userid =
          resources_.getString(GpsylonKeyConstants.KEY_HTTP_PROXY_AUTHENTICATION_USERNAME);
        String proxy_password =
          resources_.getString(GpsylonKeyConstants.KEY_HTTP_PROXY_AUTHENTICATION_PASSWORD);
        String auth_string = proxy_userid +":" + proxy_password;

        auth_string = "Basic " + new sun.misc.BASE64Encoder().encode(auth_string.getBytes());
        request_header.put("Proxy-Authorization", auth_string);
      }

          // start url:
      URL url = new URL("http://"+HOST_NAME+"/");
      if(Debug.DEBUG)
        Debug.println("map_download","URL: "+url);
      Map response = web_request_.requestUrl(url,request_header);

          // find session key from redirected url:
      String session_key = getSessionKey(response);
      if(Debug.DEBUG)
        Debug.println("map_download","Session key = "+session_key);

          // re-request the map page with the session key
      url = new URL ("http://"+HOST_NAME+"/"+session_key+"/map.aspx?"
                     +"C="+latitude+","+longitude
                     +"&A="+mappoint_scale
                     +"&L="+getLocation()
                     +"&P=");

      if(Debug.DEBUG)
        Debug.println("map_download","URL: "+url);
      response = web_request_.requestUrl(url,request_header);
    
          // get and concatinate cookie:
      String cookie = getCookie(response);
      if(Debug.DEBUG)
        Debug.println("map_download","Cookie: '"+cookie+"'");

          // set the cookie for next requests:
      request_header.put("Cookie",cookie);

          // finding session_PN and session_ID from hidden fields in html page:
      String [] session_id_and_pn = getSessionIDandPN(response);
      String session_id = session_id_and_pn[0];
      String session_pn = session_id_and_pn[1];
      if(Debug.DEBUG)
        Debug.println("map_download","Session id: '"+session_id+"', "
                      +"Session pn: '"+session_pn+"'");


          // reload the page with session_key, cookie, session_id and session_pn set:
      request_header.put("Referer",url.toString());
    
      url = new URL("http://"+HOST_NAME+"/"+session_key+"/map.aspx?"
                    +"ID="+session_id
                    +"&C="+latitude+","+longitude
                    +"&L="+getLocation()
                    +"&A="+mappoint_scale
                    +"&PN="+session_pn
                    +"&S="+image_width+","+image_height
                    +"&P=");
      if(Debug.DEBUG)
        Debug.println("map_download","URL: "+url);
      response = web_request_.requestUrl(url,request_header);

      request_header.put("Referer",url.toString());
          // finally request the image:
      url = new URL("http://"+HOST_NAME+"/"+session_key+"/MPSvc.aspx?MPMtd=M"
                    +"&ID="+session_id
                    +"&L="+getLocation()
                    +"&C="+latitude+","+longitude
                    +"&A="+mappoint_scale
                    +"&S="+image_width+","+image_height
                    +"&PN="+session_pn
                    +"&P=");
      if(Debug.DEBUG)
        Debug.println("map_download","URL: "+url);
      response = web_request_.requestUrl(url,request_header);

      int bytes_written = saveContentToFile(response,file_path_wo_extension+"gif");
      if(Debug.DEBUG)
        Debug.println("map_download", bytes_written + " bytes written to file '"
                      +file_path_wo_extension+"gif'.");
      return(map_info);
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



  protected String getSessionKey(Map web_response)
  {
    String url_file = (String)web_response.get("Location");
//    System.out.println("redirected to: "+connection.getURL());
    String session_key = url_file.substring(1,url_file.indexOf(')')+1);
    return(session_key);
  }


  protected double getScaleFactor(int mappoint_scale, int image_width, int image_height)
  {
    return(1365.333);
  }

  protected String getCookie(Map web_response)
  {

        // concatinate the multiple returned cookies:
    String cookie = null;
    String value;
    Object cookies = web_response.get("Set-Cookie");
    if(cookies == null)
      return("");
    
    if(cookies instanceof List)
    {
      for (int index = 0; index < ((List)cookies).size(); index++)
      {
        value = (String)((List)cookies).get(index);
        if(cookie == null)
          cookie = value.substring(0,value.indexOf(';'));
        else
          cookie = cookie + "; " + value.substring(0,value.indexOf(';'));
      }
    }
    else
    {
      cookie = ((String)cookies).substring(0,((String)cookies).indexOf(';'));
    }
    return(cookie);
  }

  protected String[] getSessionIDandPN(Map web_response)
    throws IOException
  {
        // finding session_PN and session_ID from hidden fields in html page:
    String content = (String)web_response.get("Content");
    String search_str = "value=\"ID=";
    int start_pos = content.indexOf(search_str) + search_str.length();
    int end_pos = content.indexOf("\"",start_pos);
    String session_id = content.substring(start_pos,end_pos);
    start_pos = content.indexOf("PN=");
    end_pos = content.indexOf("\"",start_pos);
    String session_pn = content.substring(start_pos+3,end_pos);
    return(new String[] {session_id, session_pn});
  }

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
