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

import org.dinopolis.gpstool.plugin.BasicLayerPlugin;
import org.dinopolis.gpstool.util.geoscreen.GeoScreenPoint;
import org.dinopolis.gpstool.plugin.PluginSupport;
import javax.swing.JMenuItem;
import org.dinopolis.gpstool.gui.MouseMode;
import java.awt.Graphics;
import com.bbn.openmap.proj.Projection;
import org.dinopolis.gpstool.MapManagerHook;
import org.dinopolis.gpstool.MapNavigationHook;
import javax.swing.JMenu;
import org.dinopolis.util.Resources;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.text.ParseException;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.util.LinkedList;
import org.dinopolis.gpstool.MapInfo;
import java.net.URL;
import org.dinopolis.gpstool.gui.util.AngleJTextField;
import org.dinopolis.gpstool.GPSMapKeyConstants;
import javax.swing.JOptionPane;
import java.io.FileOutputStream;
import java.net.URLConnection;
import java.io.BufferedInputStream;
import java.io.IOException;
import org.dinopolis.gpstool.util.FileUtil;
import org.dinopolis.util.Debug;
import java.io.File;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.net.MalformedURLException;
import java.util.Locale;


//----------------------------------------------------------------------
/**
 * This class provides a plugin that allows the user to download maps
 * from servers from the internet (mapblast, expedia, etc.). It
 * provides a {@link org.dinopolis.gpstool.gui.MouseMode} and draws
 * some things in a layer. Therefore it is a LayerPlugin.  <p> The
 * following describes the collaboration between the different classes
 * used (Layer, MouseMode, Frame):<br> the mouse mode tells the layer
 * about mouse clicks and mouse drags. The layer calculates the geo
 * coordinates of the mouse clicks and sets them into the
 * frame. Whenever the user changes anything in the frame (scale,
 * coordinates, etc.), the preview rectangles are recalculated (in the
 * background) and repainted. When the projection changes, the preview
 * rectangles are recalculated and repainted as well.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class DownloadMouseModeLayer extends BasicLayerPlugin
  implements ActionListener, FocusListener, GPSMapKeyConstants
{

  GeoScreenPoint mouse_drag_start_;
  GeoScreenPoint mouse_drag_end_;
  MapManagerHook map_manager_;
  MapNavigationHook man_navigation_;
  Resources resources_;
  DownloadMouseMode download_mouse_mode_;
  DownloadMapCalculator download_calculator_;
  MapRectangle[] map_rectangles_;
  Rectangle[] preview_rectangles_;
  Object preview_rectangles_lock_ = new Object();
  DownloadFrame download_frame_;
  DownloadThread download_thread_;
  DownloadInfoQueue download_queue_;
  boolean mouse_mode_active_ = false;

  public static final int DOWNLOAD_MODE_SINGLE_MAP = 1;
  public static final int DOWNLOAD_MODE_AREA_MAP = 2;
  int download_mode_ = DOWNLOAD_MODE_SINGLE_MAP;

  static final int DOWNLOAD_SUCCESS = 0;
  static final int DOWNLOAD_ERROR = 1;

  public static final float EXPEDIA_FACTOR = 1378.6f;
  
//----------------------------------------------------------------------
/**
 * Default Constructor
 */
  public DownloadMouseModeLayer()
  {
  }


//----------------------------------------------------------------------
// Plugin Methods
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
    map_manager_ = support.getMapManagerHook();
    man_navigation_ = support.getMapNavigationHook();
    resources_ = support.getResources();
    download_calculator_ = new DownloadMapCalculator();
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
    return("DownloadMouseModeLayer");
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
    return(1.0f);
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
    return("Download Maps");
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
    return("Download maps from internet servers");
  }


//----------------------------------------------------------------------
// GuiPlugin Methods
// ----------------------------------------------------------------------

//----------------------------------------------------------------------
/**
 * The plugin may return a JMenu object to be used in the main menu of
 * the application and may (should) contain other menu items. The
 * menuitems returned should provide an icon, a mnemonic key, and a
 * localized name (and a accelerator key). This implementation does
 * not use any menus but is activated only through its mouse mode.
 *
 * @return A menu that is used in the main menu in the
 * application or <code>null</code>, if no main menu is needed.
 *
 */
  public JMenu getMainMenu()
  {
    return(null);
  }

//----------------------------------------------------------------------
/**
 * The application provides a sub menu for every plugin that may be
 * used. The JMenuItem (or JMenu) returned is added to a submenu in
 * the "plugins" menu item.  The menuitems returned should provide an
 * icon, a mnemonic key, and a localized name (and a accelerator
 * key). This implementation does not use any menus but is activated
 * only through its mouse mode.
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
 * Every plugin may provide one or more mouse modes. These mouse modes
 * may react on mouse clicks, drags, etc.
 *
 * @return mouse modes that are used by this plugin in the application or
 * <code>null</code>, if no mouse modes are used.
 *
 */
  public MouseMode[] getMouseModes()
  {
    if(download_mouse_mode_ == null)
      download_mouse_mode_ = new DownloadMouseMode(this);

    return(new MouseMode[] {download_mouse_mode_});
  }

//----------------------------------------------------------------------
// BasicLayer methods
// ----------------------------------------------------------------------

//----------------------------------------------------------------------
/**
 * This method is called from a background thread to recalulate the
 * screen coordinates of any geographical objects. This method must
 * store its objects and paint them in the paintComponent() method.
 */
  protected void doCalculation()
  {
    if(!mouse_mode_active_)
      return;
    Projection projection = getProjection();

        // calculate preview rectangles:
    map_rectangles_ = download_calculator_.calculateMapRectangles();
//    System.out.println("Map Rectangles to calculate: "+Debug.objectToString(map_rectangles_));
    Rectangle[] preview_rectangles = new Rectangle[map_rectangles_.length];
    Rectangle rectangle;
    MapRectangle map;
    float scale_factor;
    for(int index = 0; index < map_rectangles_.length; index++)
    {
      map = map_rectangles_[index];
      map.forward(projection);
      scale_factor = map.getScale() / projection.getScale();
      
      rectangle = new Rectangle(map.getScreenLocation());
      rectangle.setSize((int)(map.getWidth()*scale_factor),
                        (int)(map.getHeight()*scale_factor));
          // the coordinates in the map are of the center, so move the
          // rectangle to draw:
      rectangle.translate(-(int)rectangle.getWidth()/2,-(int)rectangle.getHeight()/2);
      preview_rectangles[index] = rectangle;
    }
    setPreviewRectangles(preview_rectangles);
  }

//----------------------------------------------------------------------
/**
 * Paint layer objects.
 */
  public void paintComponent(Graphics g)
  {
    if(!mouse_mode_active_)
      return;

          // draw rectangle of dragged mouse:
    if((mouse_drag_start_ != null) && (mouse_drag_end_ != null))
    {
      int x,y,width,height;
      x = Math.min(mouse_drag_start_.getX(),mouse_drag_end_.getX());
      y = Math.min(mouse_drag_start_.getY(),mouse_drag_end_.getY());
      width = Math.abs(mouse_drag_start_.getX() - mouse_drag_end_.getX());
      height = Math.abs(mouse_drag_start_.getY() - mouse_drag_end_.getY());

      g.setColor(Color.BLACK);
      g.drawRect(x, y, width, height);
    }

        // draw preview rectangles:
    Rectangle[] rectangles;
    synchronized(preview_rectangles_lock_)
    {
      rectangles = new Rectangle[map_rectangles_.length];
      System.arraycopy(preview_rectangles_,0,rectangles,0,preview_rectangles_.length);
    }
    
    g.setColor(Color.RED);
    int x,y,width,height;
    Rectangle rectangle;
    for(int index = 0; index < rectangles.length; index++)
    {
      rectangle = rectangles[index];
      x = (int)rectangle.getX();
      y = (int)rectangle.getY();
      width = (int)rectangle.getWidth();
      height = (int)rectangle.getHeight();
      g.drawRect(x, y, width, height);
          // draw cross inside rectangle
      g.drawLine(x, y, x+width, y+height);
      g.drawLine(x+width, y, x, y+height);
    }
  }

//----------------------------------------------------------------------
// Class methods
// ----------------------------------------------------------------------

//----------------------------------------------------------------------
/**
 * Activate/Deactivate the layer (called from
 * {@DownloadMouseMode}). If activated, the download dialog box is
 * opened.
 *
 * @param active if <code>true</code> the layer is switched on.
 * 
 */

  public void setMouseModeActive(boolean active)
  {
        // display downloadframe on activation
    if((download_frame_ == null) && active)
    {
      download_frame_ = new DownloadFrame(resources_,this,this);
    }
    if(download_frame_ != null)
      download_frame_.setVisible(active);
    mouse_mode_active_ = active;
    if(active)
      updatePreviewRectangle();
    else
      repaint();
        // TODO else remove everything
  }

//----------------------------------------------------------------------
/**
 * Action Listener Method
 * 
 * @param event the action event
 */

  public void actionPerformed(ActionEvent event)
  {
    if(event.getActionCommand().equals(DownloadFrame.COMMAND_DOWNLOAD_CLOSE))
    {
      mouse_mode_active_ = false;
//       setMouseDragStart(null);
//       setMouseDragEnd(null);
//       setPreviewRectangles(new Rectangle[0]);
      repaint();
      download_frame_.setVisible(false);
//      setActive(false);
      return;
    }

    if(event.getActionCommand().equals(DownloadFrame.COMMAND_DOWNLOAD_SCALE))
    {
      download_calculator_.setImageScale(download_frame_.getScale());
      recalculateCoordinates();
      return;
    }

    if(event.getActionCommand().equals(DownloadFrame.COMMAND_DOWNLOAD_DOWNLOAD))
    {
      downloadMaps(map_rectangles_);
    }
  }


  public void downloadMaps(MapRectangle[] map_rectangles)
  {
    download_frame_.progress_bar_images_.setMaximum(map_rectangles.length);
    for(int index = 0; index < map_rectangles.length; index++)
    {
      downloadMap(map_rectangles[index]);
    }
  }


  public void downloadMap(MapRectangle map_rectangle)
  {
    if(Debug.DEBUG)
      Debug.println("GPSMap_downloadmap","try to download map lat:"+map_rectangle.getLatitude()
                    +" long:"+map_rectangle.getLongitude()
                    +" scale="+map_rectangle.getScale());

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
    MapInfo map_info = new MapInfo(dirname,
                                   (double)map_rectangle.getLatitude(),
                                   (double)map_rectangle.getLongitude(),
                                   map_rectangle.getScale(),
                                   (int)map_rectangle.getWidth(),
                                   (int)map_rectangle.getHeight());
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
        download_thread_ = new DownloadThread(download_queue_);
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
      AngleJTextField angle = (AngleJTextField)source;
//           // opening a modal dialog in the focusLost method is not a
//           // good idea, so we do it in an extra thread:
//       SwingUtilities.invokeLater(new Runnable()
//         {
//           public void run()
//           {
            updatePreviewRectangle();
//           }
//         }
//         );
    }
  }

//----------------------------------------------------------------------
/**
 * Updates the download calculator with the settings from the download
 * frame.
 */
  protected void updateDownloadCalculator()
  {
    try
    {
      download_calculator_.setImageScale(download_frame_.getScale());
      download_calculator_.setImageDimension(download_frame_.getImageWidth(),
                                             download_frame_.getImageHeight());
      switch(download_mode_)
      {
      case DOWNLOAD_MODE_SINGLE_MAP:
        download_calculator_.setDownloadCenter((float)download_frame_.getLatitude(),
                                               (float)download_frame_.getLongitude());
        break;
      case DOWNLOAD_MODE_AREA_MAP:
        float north;
        float south;
        float east;
        float west;
//        System.out.println("updateCalculator: start: "+mouse_drag_start_ +"/"+mouse_drag_end_);
        if(mouse_drag_start_.getLatitude() < mouse_drag_end_.getLatitude())
        {
          north = mouse_drag_end_.getLatitude();
          south = mouse_drag_start_.getLatitude();
        }
        else
        {
          north = mouse_drag_start_.getLatitude();
          south = mouse_drag_end_.getLatitude();
        }
        if(mouse_drag_start_.getLongitude() < mouse_drag_end_.getLongitude())
        {
          west = mouse_drag_start_.getLongitude();
          east = mouse_drag_end_.getLongitude();
        }
        else
        {
          west = mouse_drag_end_.getLongitude();
          east = mouse_drag_start_.getLongitude();
        }
        download_calculator_.setDownloadArea(north,west,south,east);
        break;
      default:
        System.err.println("ERROR: Unknown download mode");
        break;
      }
    }
    catch(ParseException pe)
    {
      download_frame_.checkValidity();
    }
  }

  
// //----------------------------------------------------------------------
// /**
//  * Returns the currently used projection.
//  *
//  * @return the currently used projection.
//  * 
//  */
//   protected Projection getMapProjection()
//   {
//     return(getProjection());
//   }

//----------------------------------------------------------------------
/**
 * Sets the drag point of the mouse.
 *
 * @param start the start of the mouse drag.
 */
  protected void setMouseDragStart(Point start)
  {
    mouse_drag_start_ = new GeoScreenPoint(start);
    mouse_drag_start_.inverse(getProjection());
    download_mode_ = DOWNLOAD_MODE_SINGLE_MAP;
    download_frame_.setDownloadCoordinates(mouse_drag_start_.getLatitude(),
                                           mouse_drag_start_.getLongitude());
    updatePreviewRectangle();
  }
  
//----------------------------------------------------------------------
/**
 * Sets the drag points of the mouse.
 *
 * @param end the start of the mouse drag. If end is null, it is
 * assumed that no drag, but only a click was performed.
 */
  protected void setMouseDragEnd(Point end)
  {
    if(end == null)
    {
      mouse_drag_end_ = null;
      return;
    }
    mouse_drag_end_ = new GeoScreenPoint(end);
    mouse_drag_end_.inverse(getProjection());
    download_mode_ = DOWNLOAD_MODE_AREA_MAP;
    download_frame_.setDownloadCoordinates((mouse_drag_start_.getLatitude() +
                                            mouse_drag_end_.getLatitude())/2.0f,
                                           (mouse_drag_start_.getLongitude() +
                                            mouse_drag_end_.getLongitude())/2.0f);
    updatePreviewRectangle();
  }
  
//----------------------------------------------------------------------
/**
 * Sets the preview rectangles in a threadsafe way.
 */
  
  protected void setPreviewRectangles(Rectangle[] rectangles)
  {
    synchronized(preview_rectangles_lock_)
    {
      preview_rectangles_ = rectangles;
    }
    download_frame_.setInfo(rectangles.length + " " +  resources_.getString(KEY_LOCALIZE_MAPS));
  }

//----------------------------------------------------------------------
/**
 * Updates the download calculator, recalculates the screen
 * coordinates (and repaints).
 *
 */
  protected void updatePreviewRectangle()
  {
    updateDownloadCalculator();
    recalculateCoordinates();
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
      download_frame_.setInfo("");
    }
    else
    {
      download_frame_.setInfo(message);
      System.out.println("Image '"+map_info.getFilename()+"' successfully downloaded.");
      download_frame_.progress_bar_images_.setValue(download_frame_.progress_bar_images_.getValue()+1);
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
  
  class DownloadThread extends Thread
  {

    public static final int BUFFER_SIZE = 4096;
    boolean run_ = true; // while true, keep on running
    DownloadInfoQueue queue_;
    boolean simulate_only = resources_.getBoolean(KEY_DEVELOPMENT_DOWNLOAD_SIMULATE_ONLY);
    
//----------------------------------------------------------------------
/**
 * Constructor
 */

    public DownloadThread(DownloadInfoQueue queue)
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
        if(simulate_only)
          System.out.println("Downloading URL: "+info.getUrl());
        else
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
          download_frame_.progress_bar_bytes_.setIndeterminate(true);
        else
          download_frame_.progress_bar_bytes_.setMaximum(content_length);
        
        String extension = mime_type.substring(mime_type.indexOf('/')+1);

        String dirname  = map_info.getFilename();

        String filename = FileUtil.getNextFileName(dirname,
                                                   resources_.getString(KEY_FILE_MAP_FILENAME_PREFIX),
                                                   resources_.getString(KEY_FILE_MAP_FILENAME_PATTERN),
                                                   "." + extension);
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
          download_frame_.progress_bar_bytes_.setValue(sum_bytes);
        }

        download_frame_.progress_bar_bytes_.setIndeterminate(false);
        
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
