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

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.util.*;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.dinopolis.gpstool.*;
import org.dinopolis.gpstool.gui.util.AngleJTextField;
import org.dinopolis.gpstool.gui.util.BasicLayer;
import org.dinopolis.gpstool.hook.MapManagerHook;
import org.dinopolis.gpstool.hook.MapNavigationHook;
import org.dinopolis.gpstool.plugin.MapRetrievalPlugin;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.util.geoscreen.GeoScreenPoint;
import org.dinopolis.util.*;
import org.dinopolis.util.gui.ActionStore;
import org.dinopolis.util.gui.MenuFactory;

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;

//----------------------------------------------------------------------
/**
 * This class provides a {@link org.dinopolis.gpstool.gui.MouseMode} and draws
 * some things in a layer. Therefore it is a LayerPlugin.  <p> The
 * following describes the collaboration between the different classes
 * used (Layer, MouseMode, Frame):<br> the mouse mode tells the layer
 * about mouse clicks and mouse drags. The layer calculates the geo
 * coordinates of the mouse clicks and sets them into the
 * frame. Whenever the user changes anything in the frame (scale,
 * coordinates, etc.), the preview rectangles are recalculated (in the
 * background) and repainted. When the projection changes, the preview
 * rectangles are recalculated and repainted as well.
 * The map download methods in this class have been move to DownloadMap 
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class DownloadMouseModeLayer extends BasicLayer
  implements ActionListener, FocusListener, GpsylonKeyConstants, ProgressListener, ProjectionListener
{

  GeoScreenPoint mouse_drag_start_;
  GeoScreenPoint mouse_drag_end_;
  MapManagerHook map_manager_;
  MapNavigationHook map_navigation_;
  Resources application_resources_;
  Resources resources_;
  DownloadMouseMode download_mouse_mode_;
  DownloadMapCalculator download_calculator_;
  MapRectangle[] map_rectangles_;
  Rectangle[] preview_rectangles_;
  Object preview_rectangles_lock_ = new Object();
  DownloadFrame download_frame_;
  boolean mouse_mode_active_ = false;
  MapRetrievalPlugin map_retrieval_plugin_;
  MapRetrievalPlugin[] map_retrieval_plugins_;
  MapRetrievalPlugin last_map_retrieval_plugin_;

  // MH
  DownloadMap download_map_;
  ActionStore action_store_;
 
  boolean automatic_map_download_mode_;  

  public static final int DOWNLOAD_MODE_SINGLE_MAP = 1;
  public static final int DOWNLOAD_MODE_AREA_MAP = 2;
  int download_mode_ = DOWNLOAD_MODE_SINGLE_MAP;

  public static final float EXPEDIA_FACTOR = 1378.6f;


      // keys for resources:
  public static final String KEY_DOWNLOADMOUSEMODE_PLUGIN_IDENTIFIER =
  "downloadmousemode.plugin.identifier";
  public static final String KEY_DOWNLOADMOUSEMODE_PLUGIN_VERSION =
  "downloadmousemode.plugin.version";
  public static final String KEY_DOWNLOADMOUSEMODE_PLUGIN_NAME =
  "downloadmousemode.plugin.name";
  public static final String KEY_DOWNLOADMOUSEMODE_PLUGIN_DESCRIPTION =
  "downloadmousemode.plugin.description";
        /** the name of the resource file */
  private final static String RESOURCE_BUNDLE_NAME = "DownloadMouseMode";

      /** the name of the directory containing the resources */
  private final static String USER_RESOURCE_DIR_NAME = Gpsylon.USER_RESOURCE_DIR_NAME;

      // properties keys:
  public static final String KEY_LOCALIZE_DOWNLOADFRAME_TITLE = "localize.downloadframe_title";
  public static final String KEY_LOCALIZE_DOWNLOAD_BUTTON = "localize.download_button";
  public static final String KEY_LOCALIZE_MESSAGE_DOWNLOAD_ERROR_MESSAGE = "localize.message.download_error_message";
  public static final String KEY_LOCALIZE_MESSAGE_DOWNLOAD_ERROR_NO_ROUTE_TO_HOST_MESSAGE = "localize.message.download_error.no_route_to_host_message";
  public static final String KEY_LOCALIZE_MESSAGE_DOWNLOAD_ERROR_FILE_NOT_FOUND_MESSAGE = "localize.message.download_error.file_not_found_message";
      /** map server urls */
  public static final String KEY_DOWNLOAD_MAP_URL_PREFIX = "download.map.url";
  public static final String KEY_DOWNLOAD_MAP_SCALE_FACTOR_PREFIX = "download.map.scale_factor";
//  public static final String KEY_DOWNLOAD_MAP_URL_CHOICE = "download.map.url.choice";
//   public static final String KEY_DOWNLOAD_MAP_URL_MAPBLAST = "download.map.url.mapblast";
//   public static final String KEY_DOWNLOAD_MAP_URL_EXPEDIA_EAST = "download.map.url.expedia_east";
//   public static final String KEY_DOWNLOAD_MAP_URL_EXPEDIA_WEST = "download.map.url.expedia_west";
  public static final String KEY_LOCALIZE_BYTES_READ = "localize.bytes_read";
  public static final String KEY_LOCALIZE_MAP_SERVER = "localize.map_server";
  public static final String KEY_DOWNLOAD_MAP_LAST_MAP_RETRIEVAL_PLUGIN_USED = "download.map.last_map_retrieval_plugin_used";
  
//----------------------------------------------------------------------
/**
 * Default Constructor
 */
  public DownloadMouseModeLayer()
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
    map_manager_ = support.getMapManagerHook();
    map_navigation_ = support.getMapNavigationHook();
    application_resources_ = support.getResources();
    loadResources();
        // attach my resources to the main resources, so the property
        // editor sees them:
    application_resources_.attachResources(resources_);
    try
    {
          // prevent any "old" values in the gpssylon resources to confuse
          // this plugin:
      application_resources_.unset("download.map.url.choice");
    }
    catch(MissingResourceException ignored) {}


    download_calculator_ = new DownloadMapCalculator();
        // find all available map retrieval plugins:
        // (do not use a string here, so the compiler checks for typos)
    Object[] plugins = support.getServiceDiscovery().getServices(
      org.dinopolis.gpstool.plugin.MapRetrievalPlugin.class);

    if(plugins.length == 0)
      setEnabled(false);
    else
    {
      map_retrieval_plugins_ = new MapRetrievalPlugin[plugins.length];
      MapRetrievalPlugin plugin;
      for(int index = 0; index < map_retrieval_plugins_.length; index++)
      {
        plugin = ((MapRetrievalPlugin)plugins[index]);
        plugin.initializePlugin(support);
        map_retrieval_plugins_[index] = plugin;
      }
    }
    
    	//  MH
    	//  initialization of download_frame_ & download_map_ & the automatic download button
    download_frame_ = new DownloadFrame(application_resources_,this,this,
            map_retrieval_plugins_,
            getDefaultMapRetrievalPlugin());  

    download_map_ = new DownloadMap();
    download_map_.initializePlugin(support);
    download_map_.setProgressListener(this);
    download_map_.setDownloadFrame(download_frame_);
    
    Action[] actions_ = { new AutomaticMapDownloadModeAction()};
    action_store_ = ActionStore.getStore(Gpsylon.ACTION_STORE_ID);
    action_store_.addActions(actions_);
   }


//----------------------------------------------------------------------
// BasicLayer methods
// ----------------------------------------------------------------------

//----------------------------------------------------------------------
/**
 * This method is called from a background thread to recalulate the
 * screen coordinates of any geographical objects. This method must
 * store its objects and paint them in the paintComponent() method.
 * It does not include maps in automatic map download mode.
 */
  
  protected void doCalculation()
  {
    if(!mouse_mode_active_)
      return;
    Projection projection = map_navigation_.getMapProjection();

        // calculate preview rectangles:
    map_rectangles_ = download_calculator_.calculateMapRectangles(DownloadMap.DOWNLOAD_MAP_MANUAL_MODE);
    
    if (map_rectangles_[0] == null)
    	return;
    
//     System.out.println("Map Rectangles to calculate: "+Debug.objectToString(map_rectangles_));
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

  // MH
//----------------------------------------------------------------------
  /** Looks for the default map retrieval plugin
   * 
   * @return default plugin
   */
  protected MapRetrievalPlugin getDefaultMapRetrievalPlugin() {
	  MapRetrievalPlugin default_plugin = null;
	  String default_plugin_name = resources_.getString(KEY_DOWNLOAD_MAP_LAST_MAP_RETRIEVAL_PLUGIN_USED);
	  int index = 0;
	  while((index < map_retrieval_plugins_.length) && (default_plugin == null))
	  {
		  if(map_retrieval_plugins_[index].getPluginIdentifier().equals(default_plugin_name))
			  return map_retrieval_plugins_[index];

		  index++;
	  }
	  // default plugin not found
	  return map_retrieval_plugins_[0];
  }

  
//----------------------------------------------------------------------
/**
 * Paint layer objects.
 * @param g the graphics object
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
          // if not preview rectangles were set, just return:
      if(preview_rectangles_ == null)
        return;
      
      rectangles = new Rectangle[preview_rectangles_.length];
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
 * {@link DownloadMouseMode}). If activated, the download dialog box is
 * opened.
 *
 * @param active if <code>true</code> the layer is switched on.
 * 
 */

  public void setMouseModeActive(boolean active)
  {
    setActive(active);
    
    // MH
    // moved initialization of download_frame_ to initializePlugin
    // also moved detection of default mapRetrievalPlugin to separate method
    //if(download_frame_ != null)
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

    if(event.getActionCommand().equals(DownloadFrame.COMMAND_DOWNLOAD_SCALE)
       || event.getActionCommand().equals(DownloadFrame.COMMAND_DOWNLOAD_MAPSERVER))
    {
      updateDownloadCalculator();
      recalculateCoordinates();
      return;
    }

    if(event.getActionCommand().equals(DownloadFrame.COMMAND_DOWNLOAD_DOWNLOAD))
    {
      map_retrieval_plugin_ = download_frame_.getMapRetrievalPlugin();
          // save the plugin used, so it is reused the next time!
      resources_.setString(KEY_DOWNLOAD_MAP_LAST_MAP_RETRIEVAL_PLUGIN_USED,
                           map_retrieval_plugin_.getPluginIdentifier());
      // MH
      download_map_.setDownloadFrame(download_frame_);
      download_map_.setDownloadMode(DownloadMap.DOWNLOAD_MAP_MANUAL_MODE);
      download_map_.downloadMaps(map_rectangles_);
      
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
//      AngleJTextField angle = (AngleJTextField)source;
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
      MapRetrievalPlugin plugin = download_frame_.getMapRetrievalPlugin();
      double plugin_scale =  plugin.getMapScale(download_frame_.getLatitude(),
                                                download_frame_.getLongitude(),
                                                download_frame_.getScale(),
                                                download_frame_.getImageWidth(),
                                                download_frame_.getImageHeight());
      download_calculator_.setImageScale((float)plugin_scale);
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
//     return(map_navigation_.getMapProjection());
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
    mouse_drag_start_.inverse(map_navigation_.getMapProjection());
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
        // ignore very small drags:
    double distance_to_start = Math.abs(end.getX() - mouse_drag_start_.getX())
                            + Math.abs(end.getY() - mouse_drag_start_.getY());
    if(distance_to_start < 10.0)
    {
      mouse_drag_end_ = null;
      return;
    }

    mouse_drag_end_ = new GeoScreenPoint(end);
    mouse_drag_end_.inverse(map_navigation_.getMapProjection());
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
    download_frame_.setInfo(rectangles.length + " " +  application_resources_.getString(KEY_LOCALIZE_MAPS));
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
 * Loads the resource file, or exits the application on a
 * MissingResourceException.
 */
	void loadResources()
	{
	  try
	  {
	    resources_ =
	      ResourceManager.getResources(
          DownloadMouseMode.class,
          RESOURCE_BUNDLE_NAME,
          USER_RESOURCE_DIR_NAME,
          Locale.getDefault());
	  }
	  catch (MissingResourceException mre)
	  {
	    if (Debug.DEBUG)
	      Debug.println(
          "DownloadMouseModePlugin",
          mre.toString() + '\n' + Debug.getStackTrace(mre));
	    System.err.println(
	      "DownloadMouseModePlugin: resource file '"
	      + RESOURCE_BUNDLE_NAME
	      + "' not found");
	    System.err.println(
	      "please make sure that this file is within the classpath !");
	    System.exit(1);
	  }
	}

//	 MH
//	 ----------------------------------------------------------------------
//	 ProjectionListener Implementation
//	 ----------------------------------------------------------------------
	
//	----------------------------------------------------------------------
	  /**
	   * The Action that triggers the de-/activation of the automatic map download button.
	   */  
	  public void projectionChanged(ProjectionEvent event)
	  {
			if (automatic_map_download_mode_) {
				
				  download_map_.deleteDownloadQueue();			
				
					// Calculates the coordinates of the maps that should be displayed in
					// automatic_map_download_mode and downloads them.	
				    
				Projection projection = map_navigation_.getMapProjection();
							
				LatLonPoint ul = projection.getUpperLeft();
				LatLonPoint lr = projection.getLowerRight();
						
				DownloadMapCalculator calc = new DownloadMapCalculator();
				
				calc.setDownloadArea(ul.getLatitude(), ul.getLongitude(), lr.getLatitude(), lr.getLongitude());
				calc.setImageScale(projection.getScale());
				
				// Defines the image dimension of any automatically downloaded map. 
				// Width/Height should be written in a properties-file perhaps 
				calc.setImageDimension(500,400);
				
				MapRectangle[] rectangles_ = calc.calculateMapRectangles(DownloadMap.DOWNLOAD_MAP_AUTOMATIC_MODE);
				
					// MH: This following code is the reason why I needed a separate DownloadMap class
				download_map_.setDownloadMode(DownloadMap.DOWNLOAD_MAP_AUTOMATIC_MODE);
				download_map_.downloadMaps(rectangles_);
			}

	  }
	
	
// ----------------------------------------------------------------------
// ProgressListener Implementation
// ----------------------------------------------------------------------

//----------------------------------------------------------------------
/**
 * Callback to inform listeners about an action to start.
 *
 * @param action_id the id of the action that is started. This id may
 * be used to display a message for the user.
 * @param min_value the minimum value of the progress counter.
 * @param max_value the maximum value of the progress counter. If the
 * max value is unknown, max_value is set to <code>Integer.NaN</code>.
 */
  public void actionStart(String action_id, int min_value, int max_value)
  {
    download_frame_.progress_bar_bytes_.setMinimum(min_value);
    if(max_value == Integer.MIN_VALUE)
      download_frame_.progress_bar_bytes_.setIndeterminate(true);
    else
      download_frame_.progress_bar_bytes_.setMaximum(max_value);
  }
  
//----------------------------------------------------------------------
/**
 * Callback to inform listeners about progress going on. It is not
 * guaranteed that this method is called on every change of current
 * value (e.g. only call this method on every 10th change).
 *
 * @param action_id the id of the action that is started. This id may
 * be used to display a message for the user.
 * @param current_value the current value
 */
  public void actionProgress(String action_id, int current_value)
  {
	// System.out.print(current_value+" ");
    download_frame_.progress_bar_bytes_.setValue(current_value);
  }

//----------------------------------------------------------------------
/**
 * Callback to inform listeners about the end of the action.
 *
 * @param action_id the id of the action that is started. This id may
 * be used to display a message for the user.
 */
  public void actionEnd(String action_id)
  {
	if(download_frame_.progress_bar_bytes_.isIndeterminate())
      download_frame_.progress_bar_bytes_.setIndeterminate(false);
    else
      download_frame_.progress_bar_bytes_.setValue(download_frame_.progress_bar_bytes_.getMaximum());
  }

  
  
//----------------------------------------------------------------------
//Inner Classes
//----------------------------------------------------------------------
  
  
//MH
//----------------------------------------------------------------------
/**
* The Action that triggers the de-/activation of the automatic map download button
*/
class AutomaticMapDownloadModeAction extends AbstractAction 
{
     //----------------------------------------------------------------------
     /**
      * The Default Constructor.
      */

 public AutomaticMapDownloadModeAction()
 {
   super(Gpsylon.ACTION_AUTOMATIC_MAP_DOWNLOAD_MODE);
	putValue(MenuFactory.SELECTED, new Boolean(automatic_map_download_mode_));
 }

     //----------------------------------------------------------------------
     /**
      * Stores bounds and locations if this option was enabled and
      * exits.
      * 
      * @param event the action event
      */

 public void actionPerformed(ActionEvent event)
 {
   automatic_map_download_mode_ = !automatic_map_download_mode_;
   
   projectionChanged(null);
   
   resources_.setBoolean(KEY_POSITION_AUTOMATIC_MAP_DOWNLOAD_MODE, automatic_map_download_mode_); // fixes bug #626309
   
   Action action = action_store_.getAction(Gpsylon.ACTION_AUTOMATIC_MAP_DOWNLOAD_MODE);
   if(action != null)
     action.putValue(MenuFactory.SELECTED, new Boolean(automatic_map_download_mode_));
   
   if(Debug.DEBUG)
     Debug.println("GPSMap_DownloadMouseModeLayer","automatic map download mode : "+automatic_map_download_mode_);
 }
}

  
  
////----------------------------------------------------------------------
///**
// * Download the given map info with the use of the given url. This
// * method creates the final filename for the map by the use of the
// * directory name in the MapInfo object.
// *
// * @param url the url to download
// * @param map_info the MapInfo object describing the map.
// */

//    public void download(URL url, MapInfo map_info)
//    {
//      try
//      {
//        URLConnection connection = url.openConnection();

//        if(application_resources_.getBoolean(KEY_HTTP_PROXY_AUTHENTICATION_USE))
//        {
//          String proxy_userid = application_resources_.getString(KEY_HTTP_PROXY_AUTHENTICATION_USERNAME);
//          String proxy_password = application_resources_.getString(KEY_HTTP_PROXY_AUTHENTICATION_PASSWORD);
//          String auth_string = proxy_userid +":" + proxy_password;

//          auth_string = "Basic " + new sun.misc.BASE64Encoder().encode(auth_string.getBytes());
//          connection.setRequestProperty("Proxy-Authorization", auth_string);
//        }

//        connection.connect();
//        String mime_type = connection.getContentType().toLowerCase();
//        if(!mime_type.startsWith("image"))
//        {
//              // handle wrong mime type. the most probable error is a
//              // 404-not found or an invalid proxy settings:
////           for (int i =1; connection.getHeaderFieldKey(i) != null; i++)
////           {
////             System.out.println("header" + connection.getHeaderFieldKey(i)
////                                +"="+connection.getHeaderField(connection.getHeaderFieldKey(i)));
////           }
         
//          if(mime_type.startsWith("text"))
//          {
//            HTMLViewerFrame viewer = new HTMLViewerFrame(url);
//            viewer.setSize(640,480);
//            viewer.setTitle("ERROR on loading url: "+url);
//            viewer.setVisible(true);
//            throw new IOException("Invalid mime type (expected 'image/*'): "
//                                  +mime_type+"\nPage is displayed in HTML frame.");
//          }
//          throw new IOException("Invalid mime type (expected 'image/*'): "
//                                +mime_type);
//        }

//        int content_length = connection.getContentLength();
//        if(content_length < 0)
//          download_frame_.progress_bar_bytes_.setIndeterminate(true);
//        else
//          download_frame_.progress_bar_bytes_.setMaximum(content_length);
       
//        String extension = mime_type.substring(mime_type.indexOf('/')+1);
//        String dirname  = map_info.getFilename();
//        String filename = FileUtil.getNextFileName(dirname,
//                                                   application_resources_.getString(KEY_FILE_MAP_FILENAME_PREFIX),
//                                                   application_resources_.getString(KEY_FILE_MAP_FILENAME_PATTERN),
//                                                   "." + extension);
//        map_info.setFilename(filename);
         
//        FileOutputStream out = new FileOutputStream(filename);

//        byte[] buffer = new byte[BUFFER_SIZE];
//        BufferedInputStream in = new BufferedInputStream(connection.getInputStream(), BUFFER_SIZE);

//        int sum_bytes = 0;
//        int num_bytes = 0;
//            // Read (and print) till end of file
//        while ((num_bytes = in.read(buffer)) != -1)
//        {
//          out.write(buffer, 0, num_bytes);
////          System.out.println(getName()+": read and wrote "+num_bytes+" bytes");
//          sum_bytes += num_bytes;
//          download_frame_.progress_bar_bytes_.setValue(sum_bytes);
//        }

//        download_frame_.progress_bar_bytes_.setIndeterminate(false);
       
//        in.close();
//        out.close();
//        downloadTerminated(map_info,DOWNLOAD_SUCCESS,
//                           sum_bytes+" "+resources_.getString(KEY_LOCALIZE_BYTES_READ));
//      }
//      catch(NoRouteToHostException nrhe)
//      {
//        String message = nrhe.getMessage() + ":\n"
//                    + resources_.getString(KEY_LOCALIZE_MESSAGE_DOWNLOAD_ERROR_NO_ROUTE_TO_HOST_MESSAGE);
//        downloadTerminated(map_info,DOWNLOAD_ERROR,message);
//      }
//      catch(FileNotFoundException fnfe)
//      {
//        String message = fnfe.getMessage() + ":\n"
//                    + resources_.getString(KEY_LOCALIZE_MESSAGE_DOWNLOAD_ERROR_FILE_NOT_FOUND_MESSAGE);
//        downloadTerminated(map_info,DOWNLOAD_ERROR,message);
//      }
//      catch(Exception e)
//      {
////        e.printStackTrace();
//        downloadTerminated(map_info,DOWNLOAD_ERROR,e.getMessage());
//      }
//    }
//  }

 
////-----------------------------------------------------------------------
////-----------------------------------------------------------------------
///**
// * Download Info
// */

//  class DownloadInfo
//  {
//    URL url_;
//    MapInfo info_;
////    MapDownloaderInstance instance_;
   
////-----------------------------------------------------------------------
///**
// * Constructor
// */
//    public DownloadInfo()
//    {
//    }

////-----------------------------------------------------------------------
///**
// * Constructor
// */
//    public DownloadInfo(URL url, MapInfo info)
//    {
//      url_ = url;
//      info_ = info;
//    }


////----------------------------------------------------------------------
///**
// * Get the url.
// *
// * @return the url.
// */
//    public URL getUrl()
//    {
//      return (url_);
//    }
   
////----------------------------------------------------------------------
///**
// * Set the url.
// *
// * @param url the url.
// */
//    public void setUrl(URL url)
//    {
//      url_ = url;
//    }


////----------------------------------------------------------------------
///**
// * Get the info.
// *
// * @return the info.
// */
//    public MapInfo getMapInfo()
//    {
//      return (info_);
//    }
   
////----------------------------------------------------------------------
///**
// * Set the info.
// *
// * @param info the info.
// */
//    public void setMapInfo(MapInfo info)
//    {
//      info_ = info;
//    }
//  }

}
