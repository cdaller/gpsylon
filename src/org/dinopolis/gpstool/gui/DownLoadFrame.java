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

package org.dinopolis.gpstool.gui;

import org.dinopolis.util.Debug;
import org.dinopolis.util.Resources;

import org.dinopolis.gpstool.util.angle.Latitude;
import org.dinopolis.gpstool.util.angle.Longitude;
import org.dinopolis.gpstool.util.angle.AngleFormat;
import org.dinopolis.gpstool.util.angle.Angle;

import org.dinopolis.gpstool.util.FileUtil;
import org.dinopolis.gpstool.util.MapRectangle;

import org.dinopolis.gpstool.gui.util.AngleJTextField;

import org.dinopolis.gpstool.GPSMapKeyConstants;
import org.dinopolis.gpstool.MapManagerHook;
import org.dinopolis.gpstool.PreviewHook;
import org.dinopolis.gpstool.MapInfo;

import org.dinopolis.gpstool.gui.layer.MultiMapLayer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


import java.io.IOException;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.Locale;
import org.dinopolis.gpstool.GPSMap;
import java.util.LinkedList;
import javax.swing.JProgressBar;
import java.awt.Dimension;
import java.util.Vector;
import java.util.Iterator;
import java.text.ParseException;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import javax.swing.SwingUtilities;


//----------------------------------------------------------------------
/**
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class DownLoadFrame extends JFrame implements ActionListener, GPSMapKeyConstants, FocusListener
{

  Resources resources_;
  String[] available_scales_ = {"1000","2000","5000","10000","25000","50000","100000","200000",
                                "500000","1000000","2000000","5000000","10000000"};

  AngleJTextField latitude_text_;
  AngleJTextField longitude_text_;
  JComboBox scale_box_;
  JLabel info_label_;
  JProgressBar progress_bar_bytes_;
  JProgressBar progress_bar_images_;
  
  MapManagerHook map_manager_;
  PreviewHook preview_hook_;

  AngleFormat latlon_formatter_;

  double absolute_width_ = -1;
  double absolute_height_ = -1;

  double latitude_;
  double longitude_;

  DownLoadThread download_thread_;
  DownloadInfoQueue download_queue_;
  int download_mode_ = DOWNLOAD_MODE_SINGLE_MAP;

  public static final int DOWNLOAD_MODE_SINGLE_MAP = 1;
  public static final int DOWNLOAD_MODE_AREA_MAP = 2;
 
  static final int DOWNLOAD_SUCCESS = 0;
  static final int DOWNLOAD_ERROR = 1;
  
//  static final String DEFAULT_SCALE = "2000";
  static final String DEFAULT_SCALE = "50000";
  static final int DEFAULT_IMAGE_WIDTH = 1280;
  static final int DEFAULT_IMAGE_HEIGHT = 1024;

  public static final float EXPEDIA_FACTOR = 1378.6f;

  public static final String COMMAND_DOWNLOAD_SCALE = "scale";
  public static final String COMMAND_DOWNLOAD_PREVIEW = "preview";
  public static final String COMMAND_DOWNLOAD_DOWNLOAD = "download";
  public static final String COMMAND_DOWNLOAD_CLOSE = "close";

  public static final float EARTH_EQUATORIAL_RADIUS_M = 6378137f;
  public static final float EARTH_POLAR_RADIUS_M = 6356752.3f;
  public static final float VERTICAL_METER_PER_DEGREE = (float)(EARTH_POLAR_RADIUS_M * 2 * Math.PI / 360.0);

  public static final String MAP_PREFIX = "map_";
  public static final String MAP_PATTERN = "00000";
  
//----------------------------------------------------------------------
/**
 * Constructor using the current map position as download position
 *
 * @param resources the resources
 * @param map_manager the hook to add new maps to
 * @param preview_hook the PreviewHook able to display a preview for
 * the map to download.
 */

  public DownLoadFrame(Resources resources, MapManagerHook map_manager, 
                       PreviewHook preview_hook)
  {
    super();
    resources_ = resources;
    map_manager_ = map_manager;
    preview_hook_ = preview_hook;
    
    setTitle(resources_.getString(KEY_LOCALIZE_DOWNLOADFRAME_TITLE));

    try
    {
      latlon_formatter_ = new AngleFormat(resources_.getString(KEY_ANGLE_FORMAT_LATLON));
    }
    catch(IllegalArgumentException e)
    {
      System.err.println("Illegal format for latitude/longitude: "+e.getMessage());
    }

    
    Container content_pane = getContentPane();

    JPanel center_panel = new JPanel();
    content_pane.add(center_panel,BorderLayout.CENTER);
    JPanel south_panel = new JPanel();
    content_pane.add(south_panel,BorderLayout.SOUTH);
    
    center_panel.setLayout(new GridLayout(7,2));

    center_panel.add(new JLabel(resources_.getString(KEY_LOCALIZE_LATITUDE)));
    center_panel.add(latitude_text_ = new AngleJTextField());
    center_panel.add(new JLabel(resources_.getString(KEY_LOCALIZE_LONGITUDE)));
    center_panel.add(longitude_text_ = new AngleJTextField());
    center_panel.add(new JLabel(resources_.getString(KEY_LOCALIZE_SCALE)));
    center_panel.add(scale_box_ = new JComboBox(available_scales_));
    scale_box_.setSelectedItem(DEFAULT_SCALE);

    center_panel.add(new JLabel(resources_.getString(KEY_LOCALIZE_LOAD_PROGRESS)
                       + " " + resources_.getString(KEY_LOCALIZE_MAPS)));
    center_panel.add(progress_bar_images_ = new JProgressBar());

    center_panel.add(new JLabel(resources_.getString(KEY_LOCALIZE_LOAD_PROGRESS)
                       + " " + resources_.getString(KEY_LOCALIZE_BYTES)));
    center_panel.add(progress_bar_bytes_ = new JProgressBar());

    center_panel.add(new JLabel(resources_.getString(KEY_LOCALIZE_INFO)));
    center_panel.add(info_label_ = new JLabel());

        // set valid angle formats for latitude and longitude:
    latitude_text_.clearValidAngleFormats();
    latitude_text_.addValidAngleFormats(resources_.getStringArray(KEY_ANGLE_FORMAT_VALID_FORMATS));
    longitude_text_.clearValidAngleFormats();
    longitude_text_.addValidAngleFormats(resources_.getStringArray(KEY_ANGLE_FORMAT_VALID_FORMATS));

        // add focus listener to the lat/lon fields:
    latitude_text_.addFocusListener(this);
    longitude_text_.addFocusListener(this);
    
    scale_box_.setActionCommand(COMMAND_DOWNLOAD_SCALE);
    scale_box_.addActionListener(this);

    setDownloadCoordinates(resources.getDouble(KEY_CURRENT_MAP_POSITION_LATITUDE),
                           resources.getDouble(KEY_CURRENT_MAP_POSITION_LONGITUDE));
    
    JButton download_button = new JButton(resources_.getString(KEY_LOCALIZE_DOWNLOAD_BUTTON));
    download_button.setActionCommand(COMMAND_DOWNLOAD_DOWNLOAD);
    download_button.addActionListener(this);
    
//      JButton preview_button = new JButton(resources_.getString(KEY_LOCALIZE_PREVIEW_BUTTON));
//      preview_button.setActionCommand(COMMAND_DOWNLOAD_PREVIEW);
//      preview_button.addActionListener(this);
    
    JButton close_button = new JButton(resources_.getString(KEY_LOCALIZE_CLOSE_BUTTON));
    close_button.setActionCommand(COMMAND_DOWNLOAD_CLOSE);
    close_button.addActionListener(this);
    
    south_panel.add(download_button);
//    south_panel.add(preview_button);
    south_panel.add(close_button);
    
    pack();

    updatePreviewRectangle();
    
    addWindowListener(new WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
          preview_hook_.hidePreviewMaps();
          setVisible(false);
        }
      });
  }


//----------------------------------------------------------------------
/**
 * Sets the string shown in the info field.
 * 
 * @param info the info to be shown.
 */
  public void setInfo(String info)
  {
    info_label_.setText(info);
  }
  
//----------------------------------------------------------------------
/**
 * Sets the download mode (single map, area maps). See
 * DOWNLOAD_MODE_SINGLE_MAP, DOWNLOAD_MODE_AREA_MAP.
 * 
 * @param mode the mode.
 */
  public void setDownloadMode(int mode)
  {
    preview_hook_.hidePreviewMaps();
    download_mode_ = mode;
    updatePreviewRectangle();
  }
  
//----------------------------------------------------------------------
/**
 * Sets new coordinates
 * 
 * @param latitude
 * @param longitude
 */
  public void setDownloadCoordinates(double latitude, double longitude)
  {
    latitude_text_.setText(latlon_formatter_.format(new Latitude(latitude)));
    longitude_text_.setText(latlon_formatter_.format(new Longitude(longitude)));

    updatePreviewRectangle();
  }

//----------------------------------------------------------------------
/**
 * Sets size of download area
 * 
 * @param abs_width the width of the area multiplied with the
 * current scale.
 * @param abs_height the height of the area multiplied with the
 * current scale.
 */
  public void setDownloadAreaSize(int abs_width, int abs_height)
  {
    absolute_width_ = abs_width;
    absolute_height_ = abs_height;
    updatePreviewRectangle();
  }


//----------------------------------------------------------------------
/**
 * Returns the scale setup in the dialog.
 *
 * @return the scale setup in the dialog.
 */
  protected float getScale()
  {
    String scale = (String)scale_box_.getSelectedItem();
    return(Float.parseFloat(scale));
  }

//----------------------------------------------------------------------
/**
 * Returns the image width setup in the dialog.
 *
 * @return the image width setup in the dialog.
 */
  protected int getImageWidth()
  {
    return(DEFAULT_IMAGE_WIDTH);
  }
  
//----------------------------------------------------------------------
/**
 * Returns the image height setup in the dialog.
 *
 * @return the image height setup in the dialog.
 */
  protected int getImageHeight()
  {
    return(DEFAULT_IMAGE_HEIGHT);
  }

//----------------------------------------------------------------------
/**
 * Returns the latitude setup in the dialog.
 *
 * @return the latitude setup in the dialog.
 * @exception ParseException if the input text is not conform to any
 * of the valid format definitions.
 */
  public double getLatitude()
    throws ParseException
  {
    Angle angle = latitude_text_.getAngle();
    if(angle == null)
      throw new ParseException(resources_.getString(KEY_LOCALIZE_MESSAGE_LATITUDE_OR_LONGITUDE_WRONG_FORMAT),0);

    return(angle.degrees());
  }

//----------------------------------------------------------------------
/**
 * Returns the longitude setup in the dialog.
 *
 * @return the longitude setup in the dialog.
 * @exception ParseException if the input text is not conform to any
 * of the valid format definitions.
 */
  public double getLongitude()
    throws ParseException
  {
    Angle angle = longitude_text_.getAngle();
    if(angle == null)
      throw new ParseException(resources_.getString(KEY_LOCALIZE_MESSAGE_LONGITUDE_WRONG_FORMAT),0);

    return(angle.degrees());
  }

//----------------------------------------------------------------------
/**
 * Checks the validity of the user input. If any of the user's input
 * is not valid, a Dialog is opened to inform the user about the
 * incorrect data and <code>false</code> is returned.
 * 
 * @return false if any of the users input is invalid.
 */
  public boolean checkValidity()
  {
    if((latitude_text_.getAngle() == null) || (longitude_text_.getAngle() == null))
    {
      JOptionPane.showMessageDialog(this,
                                    resources_.getString(KEY_LOCALIZE_MESSAGE_LATITUDE_OR_LONGITUDE_WRONG_FORMAT),
                                    resources_.getString(KEY_LOCALIZE_MESSAGE_ERROR_TITLE),
                                    JOptionPane.ERROR_MESSAGE);
      return(false);
    }
    return(true);
  }
  
//----------------------------------------------------------------------
/**
 * Updates the preview rectangle in the map
 */
  protected void updatePreviewRectangle()
  {
    try
    {
      switch(download_mode_)
      {
      case DOWNLOAD_MODE_SINGLE_MAP:
        preview_hook_.setPreviewMap(getLatitude(), getLongitude(),
                                    getScale(),
                                    getImageWidth(), getImageHeight());
        break;
      case DOWNLOAD_MODE_AREA_MAP:
            // no preview as the rectangle is already painted!
        Vector maps = calculateDownloadMaps(getLatitude(),
                                            getLongitude(),
                                            getScale(),
                                            getImageWidth(),
                                            getImageHeight(),
                                            absolute_width_,
                                            absolute_height_);
        info_label_.setText(maps.size() + " " + resources_.getString(KEY_LOCALIZE_MAPS));
        preview_hook_.setPreviewMaps(maps);
        break;
      default:
        System.err.println("ERROR: Unknown download mode");
        break;
      }
    }
    catch(ParseException pe)
    {
      checkValidity();
    }
  }


//----------------------------------------------------------------------
/**
 * Downloads a single map with the given parameters.
 *
 * @param latitude latititude of center of map.
 * @param longitude longitude of center of map.
 * @param scale the scale of the map to download (might slightly
 * modified due to different support of different mapservers)
 * @param image_width the width of the map to download (pixels)
 * @param image_height the height of the map to download (pixels)
 */
  protected void downloadSingleMap(MapRectangle map)
  {
    downloadSingleMap(map.getLatitude(),map.getLongitude(),
                      map.getScale(),(int)map.getWidth(),(int)map.getHeight());
  }
  
//----------------------------------------------------------------------
/**
 * Downloads a single map with the given parameters.
 *
 * @param latitude latititude of center of map.
 * @param longitude longitude of center of map.
 * @param scale the scale of the map to download (might slightly
 * modified due to different support of different mapservers)
 * @param image_width the width of the map to download (pixels)
 * @param image_height the height of the map to download (pixels)
 */
  protected void downloadSingleMap(double latitude, double longitude,
				   float scale, int image_width, int image_height)
  {
    if(Debug.DEBUG)
      Debug.println("GPSMap_downloadmap","try to download map lat:"+latitude
		    +" long:"+longitude
		    +" scale="+scale);

    String dirname = FileUtil.getAbsolutePath(resources_.getString(KEY_FILE_MAINDIR),
                                              resources_.getString(KEY_FILE_MAP_DIR));
    
    File dir = new File(dirname);
    if(!dir.isDirectory())
    {
      System.err.println("Directory '"+dirname+"' does not exist, creating it.");
      dir.mkdirs();
    }

        // create a new MapInfo object that describes the map to
        // download.  The target directory is set as filename, as the
        // final filename cannot be determined at this moment. It will
        // be created from the download thread.
    MapInfo map_info = new MapInfo(dirname,latitude,longitude,
                                   scale,image_width,image_height);
    try
    {
      downloadMap(map_info);
    }
    catch(MalformedURLException mfue)
    {
      JOptionPane.showMessageDialog(this,
				    resources_.getString(KEY_LOCALIZE_MESSAGE_DOWNLOAD_ERROR_MESSAGE)
				    +"\n"+mfue.getMessage(),
				    resources_.getString(KEY_LOCALIZE_MESSAGE_ERROR_TITLE),
				    JOptionPane.ERROR_MESSAGE);
    }
  }

//----------------------------------------------------------------------
/**
 * Downloads maps of an area with the given parameters. The area is
 * specified by <code>absolute_width</code> and
 * <code>absolute_height</code>.
 *
 * @param latitude latititude of center of the area
 * @param longitude longitude of center of area
 * @param scale the scale of the maps to download (might slightly
 * modified due to different support of different mapservers)
 * @param image_width the width of the maps to download (pixels)
 * @param image_height the height of the maps to download (pixels)
 * @param absolute_width the absolute width of the area to
 * download. The absolute width is calculated as follows: width in
 * pixels multiplied with the scale used to show the area (rectangle).
 * @param absolute_height the absolute height of the area to download.
 */
  protected void downloadAreaMap(double latitude, double longitude,
				 float scale, int image_width, int image_height,
				 double absolute_width, double absolute_height)
  {
    Vector maps = calculateDownloadMaps(latitude,longitude,scale,
                                        image_width,image_height,
                                        absolute_width,absolute_height);
    progress_bar_images_.setMaximum(maps.size());
    Iterator map_iterator = maps.iterator();
    MapRectangle map;
    while(map_iterator.hasNext())
    {
      map = (MapRectangle)map_iterator.next();
      downloadSingleMap(map);
    }
  }

//----------------------------------------------------------------------
/**
 * Calculates the maps of an area with the given parameters. The area is
 * specified by <code>absolute_width</code> and
 * <code>absolute_height</code>.
 *
 * @param latitude latititude of center of the area
 * @param longitude longitude of center of area
 * @param scale the scale of the maps to download (might slightly
 * modified due to different support of different mapservers)
 * @param image_width the width of the maps to download (pixels)
 * @param image_height the height of the maps to download (pixels)
 * @param absolute_width the absolute width of the area to
 * download. The absolute width is calculated as follows: width in
 * pixels multiplied with the scale used to show the area (rectangle).
 * @param absolute_height the absolute height of the area to download.
 * @return a vector holding MapRectangle objects
 */

  protected Vector calculateDownloadMaps(double latitude, double longitude,
                                         float scale, int image_width, int image_height,
                                         double absolute_width, double absolute_height)
  {
        // calculate circumference of small circle at latitude:
    double horiz_meter_per_degree = Math.cos(Math.toRadians(latitude))
                                    *EARTH_EQUATORIAL_RADIUS_M*2*Math.PI / 360.0;
    
    double image_width_degree = image_width * scale / MultiMapLayer.PIXELFACT / horiz_meter_per_degree;
    double image_height_degree = image_height * scale / MultiMapLayer.PIXELFACT / VERTICAL_METER_PER_DEGREE;

    double long_offset = absolute_width / MultiMapLayer.PIXELFACT / horiz_meter_per_degree / 2;
    double lat_offset = absolute_height / MultiMapLayer.PIXELFACT / VERTICAL_METER_PER_DEGREE / 2; 

        // bottom left (latitude origin is in bottom) of rectangle:
    double start_lat = latitude - lat_offset;
    double start_long = longitude - long_offset;

        // top right of rectangle:
    double end_lat = latitude + lat_offset;
    double end_long = longitude + long_offset;

        // center of image are not at corners of rectangle:
    start_lat += image_height_degree/2;
    start_long += image_width_degree/2;

        // end is reached if the complete image would be outside the
        // rectangle.  therefore we stop if the coordinates are one
        // image size beyond the rectangle:
    end_lat += image_height_degree/2;
    end_long += image_width_degree/2;

    double current_lat = start_lat;
    double current_long = start_long;

    Vector maps = new Vector();
    while(current_lat < end_lat)
    {
      while(current_long < end_long)
      {
        maps.add(new MapRectangle(current_lat, current_long, image_width, image_height, scale));
        current_long += image_width_degree;
      }
      current_long = start_long;
      current_lat += image_height_degree;
    }
    return(maps);
  }

//----------------------------------------------------------------------
/**
 * Focus Listener Method
 * 
 * @param event the action event
 */

  public void focusGained(FocusEvent event)
  {
  }


//----------------------------------------------------------------------
/**
 * Focus Listener Method for focus lost. This method checks the
 * validity of the latitude/longitude input fields for valid inputs
 * 
 * @param event the action event
 */

  public void focusLost(FocusEvent event)
  {
    Object source = event.getSource();
    if(source instanceof AngleJTextField)
    {
          // opening a modal dialog in the focusLost method is not a
          // good idea, so we do it in an extra thread:
      SwingUtilities.invokeLater(new Runnable()
        {
          public void run()
          {
            updatePreviewRectangle();
          }
        }
        );
    }
  }



  
//----------------------------------------------------------------------
/**
 * Action Listener Method
 * 
 * @param event the action event
 */

  public void actionPerformed(ActionEvent event)
  {
    if(event.getActionCommand().equals(COMMAND_DOWNLOAD_CLOSE))
    {
      preview_hook_.hidePreviewMaps();
      setVisible(false);
      return;
    }

    if(event.getActionCommand().equals(COMMAND_DOWNLOAD_SCALE))
    {
      updatePreviewRectangle();
      return;
    }

    if(event.getActionCommand().equals(COMMAND_DOWNLOAD_DOWNLOAD))
    {
      try
      {
        switch(download_mode_)
        {
        case DOWNLOAD_MODE_SINGLE_MAP:
          downloadSingleMap(getLatitude(),getLongitude(),
                            getScale(),getImageWidth(),getImageHeight());
          break;
        case DOWNLOAD_MODE_AREA_MAP:
          downloadAreaMap(getLatitude(),getLongitude(),
                          getScale(),getImageWidth(),getImageHeight(),
                          absolute_width_, absolute_height_);
          break;
        default:
          System.err.println("ERROR: Unknown download mode");
          break;
        }
      }
      catch(ParseException pe)
      {
        checkValidity();
      }
    }
  }


//----------------------------------------------------------------------
/**
 * Create an url for downloading the map and start a background thread
 * that downloads the map. As soon as the download is terminated
 * (successfully or not), the method <code>downloadTerminated</code>
 * method is called. During this process, additional information might
 * be added to the map_info object, so that the map_info passed as a
 * parameter to this method is not guaranteed to be exactly the same
 * that is returned to the downladTerminated method. E.g. the scale
 * might be slightly changed, as e.g. expedia does not support exactly
 * the same scales as mapblast does.
 *
 * @param map_info the info that describes the map to be downloaded.  
 */

  protected void downloadMap(MapInfo map_info)
    throws MalformedURLException
  {
    String map_server = "";
    try
    {
      map_server = resources_.getString(KEY_DOWNLOAD_MAP_URL_CHOICE);
      float scale;
          // expedia uses a differen scaling system in its urls:
      if(map_server.toLowerCase().startsWith("expedia"))
      {
        scale = Math.round(map_info.getScale() / EXPEDIA_FACTOR);
        map_info.setScale(scale * EXPEDIA_FACTOR);
      }
      else
        scale = map_info.getScale();

//      System.out.println("Map info after: "+map_info);
      
      Object[] params = new Object[] {new Double(map_info.getLatitude()), 
                                      new Double(map_info.getLongitude()),
                                      new Float(scale),
                                      new Integer(map_info.getWidth()), 
                                      new Integer(map_info.getHeight())};
      String message = resources_.getString(KEY_DOWNLOAD_MAP_URL_PREFIX + "." + map_server);

      MessageFormat message_format = new MessageFormat(message,Locale.US); // for the decimal points!
      String url = message_format.format(params,new StringBuffer(), null).toString();

      if(Debug.DEBUG)
          Debug.println("GPSMap_downloadmap","using url"+url);
      
      if(download_thread_ == null)
      {
        download_queue_ = new DownloadInfoQueue();
        download_thread_ = new DownLoadThread(download_queue_);
        download_thread_.start();
      }
            
      download_queue_.put(new DownloadInfo(new URL(url),map_info));
    
    }
    catch(MissingResourceException mre)
    {
      System.err.println("ERROR: Server "+map_server+" not specified correctly in the resource file.");
      JOptionPane.showMessageDialog(this,
                resources_.getString(KEY_LOCALIZE_MESSAGE_DOWNLOAD_ERROR_MESSAGE)
                +"\nServer "+map_server+" not specified correctly in the resource file.",
                resources_.getString(KEY_LOCALIZE_MESSAGE_ERROR_TITLE),
                JOptionPane.ERROR_MESSAGE);

    }
  }

//----------------------------------------------------------------------
/**
 * Callback for the DownloadThread to inform about the termination of
 * the download.
 *
 * @param map_info The map info object of the downloaded map.
 * @param status The status of the download
 * @param message the message (success or error)
 */

  protected void downloadTerminated(MapInfo map_info,int status,String message)
  {
    if(status == DOWNLOAD_ERROR)
    {
      JOptionPane.showMessageDialog(this,
                                    resources_.getString(KEY_LOCALIZE_MESSAGE_DOWNLOAD_ERROR_MESSAGE)
                                    +"\n"+message,
                                    resources_.getString(KEY_LOCALIZE_MESSAGE_ERROR_TITLE),
                                    JOptionPane.ERROR_MESSAGE);
      info_label_.setText("");
    }
    else
    {
      info_label_.setText(message);
      System.out.println("Image '"+map_info.getFilename()+"' successfully downloaded.");
      progress_bar_images_.setValue(progress_bar_images_.getValue()+1);
      if(map_manager_ != null)
        map_manager_.addNewMap(map_info);
    }
  }


// ----------------------------------------------------------------------
// Inner Classes
// ----------------------------------------------------------------------

//-----------------------------------------------------------------------
/**
 * Download thread
 */
  
  class DownLoadThread extends Thread
  {

    public static final int BUFFER_SIZE = 4096;
    boolean run_ = true; // while true, keep on running
    DownloadInfoQueue queue_;
    
//----------------------------------------------------------------------
/**
 * Constructor
 */

    public DownLoadThread(DownloadInfoQueue queue)
    {
      super("gpsmap download thread");
      setDaemon(true);
      queue_ = queue;
    }



//----------------------------------------------------------------------
/**
 *
 */
    public void run()
    {
      while(run_)
      {
        DownloadInfo info = (DownloadInfo)queue_.get();
        download(info.getUrl(),info.getMapInfo());
      }
    }
    

//----------------------------------------------------------------------
/**
 * Download the given map info with the use of the given url. This
 * method creates the final filename for the map by the use of the
 * directory name in the MapInfo object.
 *
 * @param url the url to download
 * @param map_info the MapInfo object describing the map.
 */

    public void download(URL url, MapInfo map_info)
    {
      try
      {
        URLConnection connection = url.openConnection();

        if(resources_.getBoolean(KEY_HTTP_PROXY_AUTHENTICATION_USE))
        {
          String proxy_userid = resources_.getString(KEY_HTTP_PROXY_AUTHENTICATION_USERNAME);
          String proxy_password = resources_.getString(KEY_HTTP_PROXY_AUTHENTICATION_PASSWORD);
          String auth_string = proxy_userid +":" + proxy_password;

          auth_string = "Basic " + new sun.misc.BASE64Encoder().encode(auth_string.getBytes());
          connection.setRequestProperty("Proxy-Authorization", auth_string);
        }

        connection.connect();
        String mime_type = connection.getContentType().toLowerCase();
        if(!mime_type.startsWith("image"))
        {
          throw new IOException("Invalid mime type (expected 'image/*'): "
                                +connection.getContentType());
        }

        int content_length = connection.getContentLength();
        if(content_length < 0)
          progress_bar_bytes_.setIndeterminate(true);
        else
          progress_bar_bytes_.setMaximum(content_length);
        
        String extension = mime_type.substring(mime_type.indexOf('/')+1);

        String dirname  = map_info.getFilename();

        String filename = FileUtil.getNextFileName(dirname,
                                                   MAP_PREFIX ,
                                                   MAP_PATTERN, "." + extension);
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
          progress_bar_bytes_.setValue(sum_bytes);
        }

        progress_bar_bytes_.setIndeterminate(false);
        
        in.close();
        out.close();
        downloadTerminated(map_info,DOWNLOAD_SUCCESS,
                           sum_bytes+" "+resources_.getString(KEY_LOCALIZE_BYTES_READ));
      }
      catch(Exception e)
      {
        downloadTerminated(map_info,DOWNLOAD_ERROR,e.getMessage());
      }
    }
  }

//-----------------------------------------------------------------------
//-----------------------------------------------------------------------
/**
 * Download Info
 */

  class DownloadInfo
  {
    URL url_;
    MapInfo info_;
    
//-----------------------------------------------------------------------
/**
 * Constructor
 */
    public DownloadInfo()
    {
    }

//-----------------------------------------------------------------------
/**
 * Constructor
 */
    public DownloadInfo(URL url, MapInfo info)
    {
      url_ = url;
      info_ = info;
    }


//----------------------------------------------------------------------
/**
 * Get the url.
 *
 * @return the url.
 */
    public URL getUrl()
    {
      return (url_);
    }
    
//----------------------------------------------------------------------
/**
 * Set the url.
 *
 * @param url the url.
 */
    public void setUrl(URL url)
    {
      url_ = url;
    }


//----------------------------------------------------------------------
/**
 * Get the info.
 *
 * @return the info.
 */
    public MapInfo getMapInfo()
    {
      return (info_);
    }
    
//----------------------------------------------------------------------
/**
 * Set the info.
 *
 * @param info the info.
 */
    public void setMapInfo(MapInfo info)
    {
      info_ = info;
    }
  }

//-----------------------------------------------------------------------
//-----------------------------------------------------------------------
/**
 * DownloadInfoQueue. This Queue notifies any waiting threads if an
 * object is put into it and it puts a requesting thread to sleep
 * (wait) if nothing is in the queue.
 */

  class DownloadInfoQueue
  {
    LinkedList queue_;
    
//-----------------------------------------------------------------------
/**
 * Constructor
 */
    public DownloadInfoQueue()
    {
    }


//-----------------------------------------------------------------------
/**
 * Adds an Object to the Queue.
 */
    public synchronized void put(Object obj)
    {
      if(queue_ == null)
        queue_ = new LinkedList();
      queue_.add(obj);
      notify();
    }


//-----------------------------------------------------------------------
/**
 * Constructor
 */
    public synchronized Object get()
    {
      while((queue_ == null) || (queue_.size() == 0))
      {
        try
        {
          wait();
        }
        catch(InterruptedException ie)
        {
        }
      }
      return(queue_.removeFirst());
    }
  }    
}




