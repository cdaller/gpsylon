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

package org.dinopolis.gpstool.gui.layer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.dinopolis.gpstool.Gpsylon;
import org.dinopolis.gpstool.GpsylonKeyConstants;
import org.dinopolis.gpstool.gui.layer.location.FileLocationMarkerSource;
import org.dinopolis.gpstool.gui.layer.location.JDBCLocationMarkerSource;
import org.dinopolis.gpstool.gui.layer.location.LocationMarker;
import org.dinopolis.gpstool.gui.layer.location.LocationMarkerCategory;
import org.dinopolis.gpstool.gui.layer.location.LocationMarkerFilter;
import org.dinopolis.gpstool.gui.layer.location.LocationMarkerFrame;
import org.dinopolis.gpstool.gui.layer.location.LocationMarkerSource;
import org.dinopolis.gpstool.gui.layer.location.LocationMarkerSourceException;
import org.dinopolis.gpstool.gui.layer.location.SearchLocationMarkerFrame;
import org.dinopolis.gpstool.gui.layer.location.SelectCategoryFrame;
import org.dinopolis.gpstool.hook.MapNavigationHook;
import org.dinopolis.gpstool.util.ExtensionFileFilter;
import org.dinopolis.gpstool.util.FeedBack;
import org.dinopolis.gpstool.util.FileUtil;
import org.dinopolis.gpstool.util.GeoExtent;
import org.dinopolis.gpstool.util.GeonetDataConverter;
import org.dinopolis.gpstool.util.JDBCUtil;
import org.dinopolis.gpstool.util.Positionable;
import org.dinopolis.gpstool.util.geoscreen.GeoScreenList;
import org.dinopolis.gpstool.util.geoscreen.GeoScreenPoint;
import org.dinopolis.util.Debug;
import org.dinopolis.util.Resources;
import org.dinopolis.util.gui.ActionStore;
import org.dinopolis.util.gui.LoginDialog;
import org.dinopolis.util.gui.MenuFactory;
import org.dinopolis.util.gui.SwingWorker;
import org.hsqldb.util.DatabaseManagerSwing;

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.Layer;
import com.bbn.openmap.event.LayerStatusEvent;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.util.quadtree.QuadTree;

//----------------------------------------------------------------------
/**
 * A layer that is able to display location markers (points of
 * interest, ...). It uses LocationMarkerSource objects as sources for
 * the LocationMarker objects to paint. The painting itself is done in
 * this class.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class LocationLayer extends Layer
  implements GpsylonKeyConstants, PropertyChangeListener, MouseListener
{
  boolean layer_active_ = true;

  Resources resources_;
  
  JFileChooser load_file_chooser_;
  JFileChooser export_file_chooser_;
  FontMetrics font_metrics_;

  SwingWorker swing_worker_;
  
  ActionStore action_store_;

  Vector location_sources_;
  
      /** the location marker source used to store new markers. */
  LocationMarkerSource location_target_;
  
  Object location_markers_lock_ = new Object();
  GeoScreenList location_markers_;
  Object quad_tree_lock_ = new Object();
  QuadTree quad_tree_;

  boolean show_names_;
  Color name_color_text_;
  Color name_color_background_;
  int name_text_size_;
  Font name_text_font_;

  int name_offset_x_ = 2;

  final static int BORDER_SIZE = 2;

  Vector tooltip_markers_;
  int tooltip_limit_ = 10;
  boolean tooltip_registered_ = false;

  Positionable positionable_;

  MapNavigationHook map_navigation_hook_;

  JFrame dialog_owner_frame_;
  
  LocationMarkerFrame location_marker_frame_;
  SelectCategoryFrame select_category_frame_;

  LocationMarkerCategory[] categories_;
  
  GeoScreenPoint new_marker_pos_;
  Object new_marker_pos_lock_ = new Object();

  LocationMarker search_result_marker_;
  Object search_result_marker_lock_ = new Object();;

  LocationMarkerFilter location_marker_filter_;
  Object location_marker_filter_lock_ = new Object();

  boolean all_categories_visible_ = false;
  boolean no_categories_visible_ = false;

      /** level of detail currently used: 1 = least details, 10 = most details */
  int current_level_of_detail_ = -1;
  double[] level_of_detail_scales_;

  float old_scale_ = -1.0f;
  
  private static Logger logger_ = Logger.getLogger(LocationLayer.class);
  
//----------------------------------------------------------------------
/**
 * 
 */
  public LocationLayer()
  {
    super();
    setDoubleBuffered(true);
//    addLocationMarkerSource(new TestLocationMarkerSource());
  }

//----------------------------------------------------------------------
/**
 * Initializes this layer with the given resources.
 *
 * @param resources the resources to use.
 * @param positionable the object to ask for the current gps position.
 * @param frame the frame that should be the owner of any dialogs
 * opened.
 */
  public void initialize(Resources resources, Positionable positionable,
                         MapNavigationHook map_navigation_hook, JFrame frame)
  {
    resources_ = resources;
    positionable_ = positionable;
    map_navigation_hook_ = map_navigation_hook;
    dialog_owner_frame_ = frame;

    LocationMarkerCategory.setResources(resources_);
    
    layer_active_ = resources_.getBoolean(KEY_LOCATION_LAYER_ACTIVE);
    show_names_ = resources_.getBoolean(KEY_LOCATION_MARKER_SHOW_NAMES);
    name_color_text_ = resources_.getColor(KEY_LOCATION_MARKER_TEXT_COLOR);
    name_color_background_ = resources_.getColor(KEY_LOCATION_MARKER_TEXT_BACKGROUND_COLOR);
    name_text_size_ = resources_.getInt(KEY_LOCATION_MARKER_TEXT_FONT_SIZE);

    level_of_detail_scales_ = resources_.getDoubleArray(KEY_LOCATION_MARKER_CATEGORY_LEVEL_OF_DETAIL_SCALES);
    
        // bug! the tooltip manager prevents the layers (all!) to receive any
        // mouselistener events, so I removed it!
//         // only use tooltips, if the names are not shown:
//     if(!show_names_)
//     {
//       ToolTipManager tool_tip_manager = ToolTipManager.sharedInstance();
//       tool_tip_manager.registerComponent(this);
//       tooltip_registered_ = true;
//     }

//     System.out.println("LocationLayer: initialization...");
//     if(layer_active_)
//     {
//       layer_active_ = false;
//       Timer activator = new Timer();
//       activator.schedule(new TimerTask()
// 	{
// 	  public void run()
// 	  {
// 	    System.out.println("LocationLayer: delayed activation done...");
// 	    layer_active_ = true;
// 	    recalculateLocationMarkers();
// 	  }
// 	},10000);
//     }
    
    LocationMarkerSource default_source = getDefaultLocationMarkerSource();
    if(default_source != null)
    {
      addLocationMarkerSource(default_source);
      setTargetLocationMarkerSource(default_source);
    }
    
        /** the Actions */
    Action[] actions_ = {  new LocationLayerActivateAction(),
                           new LoadLocationAction(),
                           new SetMarkerGPSPositionAction(),
                           new SelectMarkerCategoriesAction(),
                           new ExportLocationAction(),
                           new ImportLocationAction(),
                           new DatabaseManagerAction(),
                           new LevelOfDetailChangeAction(Gpsylon.ACTION_LEVEL_OF_DETAIL_INCREASE,1),
                           new LevelOfDetailChangeAction(Gpsylon.ACTION_LEVEL_OF_DETAIL_DECREASE,-1),
                           new SearchMarkerAction(),
                           new ShowMarkerNamesAction()};
    action_store_ = ActionStore.getStore(Gpsylon.ACTION_STORE_ID);
    action_store_.addActions(actions_);

    categories_ = LocationMarkerCategory.getCategories();
    setLocationMarkerFilter(createCategoryFilter(categories_));
    
//      FileLocationMarkerSource source = new FileLocationMarkerSource(
//        "/filer/cdaller/texte/gps/cities.csv",
//        1,6,5,FileLocationMarkerSource.TYPE_CSV);
//      addLocationMarkerSource(source);
  }



//----------------------------------------------------------------------
/**
 * Calculates the level of detail to use. The result depends on the
 * current scale and on user settings.
 *
 * @param scale the scale to use to find the level in the resources.
 * @return the level of detail to use (value between 1 and 10, 1 means
 * least details, 10 means most details).
 */
  protected int getLevelOfDetailForScale(float scale)
  {
        // find out, which level of detail should be used (dep. on scale):
    int count = 0;
    while((count < level_of_detail_scales_.length)
          && (scale < level_of_detail_scales_[count]))
      count++;

    return(count);
  }
  

//----------------------------------------------------------------------
/**
 * Creates and returns the location marker filter used to retrieve the
 * the location markers from the sources. It uses the information in
 * the categories to create the filter. The filter returned represents
 * all categories that are visible resp. not visible and apply to the
 * chosen level of detail (dependent on current scale). If all or no
 * categories are visible, this method sets the
 * <code>all_categories_visible_</code>
 * resp. <code>no_categories_visible_</code> flags.
 *
 * @param categories the categories (visible or invisible).
 * @return a filter that represents the visible categories.
 */
  protected LocationMarkerFilter createCategoryFilter(LocationMarkerCategory[] categories)
  {
    if(getProjection() == null)
    {
      if(Debug.DEBUG)
        Debug.println("levelofdetail","createCategoryFilter: no projection set");
      no_categories_visible_ = true;
      return(null);
    }

    Vector visible_categories = new Vector();
    Vector not_visible_categories = new Vector();

    if(current_level_of_detail_ < 0)
    {
      current_level_of_detail_ = getLevelOfDetailForScale(getProjection().getScale());
      if(Debug.DEBUG)
        Debug.println("levelofdetail","current level < 0, new level = "+current_level_of_detail_);
    }

    if(Debug.DEBUG)
      Debug.println("levelofdetail","for scale "+getProjection().getScale()
                    +" using level of detail "+current_level_of_detail_);
    
    LocationMarkerCategory category;
    for(int cat_count = 0; cat_count < categories.length; cat_count++)
    {
      category = categories[cat_count];
      if(category.isVisible() && (category.getLevelOfDetail() <= current_level_of_detail_))
      {
        visible_categories.add(category.getId());
        if(Debug.DEBUG)
          Debug.println("LocationLayer_filter","Adding to visible filter: "+category.getId());
      }
      else
      {
        not_visible_categories.add(category.getId());
        if(Debug.DEBUG)
          Debug.println("LocationLayer_filter","Adding to not visible filter: "+category.getId());
      }
    }
    LocationMarkerFilter visible_filter = null;
    LocationMarkerFilter not_visible_filter = null;
    LocationMarkerFilter filter = null;

    if(visible_categories.size() > 0)
      visible_filter = new LocationMarkerFilter(LocationMarkerFilter.KEY_CATEGORY,
                                                visible_categories.toArray(),
                                                LocationMarkerFilter.OR_TYPE,
                                                LocationMarkerFilter.EQUALS_OPERATION);
    if(not_visible_categories.size() > 0)
      not_visible_filter = new LocationMarkerFilter(LocationMarkerFilter.KEY_CATEGORY,
                                                    not_visible_categories.toArray(),
                                                    LocationMarkerFilter.AND_TYPE,
                                                    LocationMarkerFilter.NOT_EQUALS_OPERATION);

        // if we have visible and invisible categories, create a filter that handles both
    if((visible_filter != null) && (not_visible_filter != null))
    {
      all_categories_visible_ = false;
      no_categories_visible_ = false;
      return(new LocationMarkerFilter(LocationMarkerFilter.KEY_FILTER,
                                      new LocationMarkerFilter[]{visible_filter, not_visible_filter},
                                      LocationMarkerFilter.AND_TYPE,
                                      LocationMarkerFilter.FILTER_OPERATION));
    }
        // all categories are visible
    if(visible_filter != null)
    {
      all_categories_visible_ = true;
      no_categories_visible_ = false;
      return(visible_filter);
    }

        // no categories are invisible:
    all_categories_visible_ = false;
    no_categories_visible_ = true;
    return(not_visible_filter);
  }


//----------------------------------------------------------------------
/**
 * Sets (threadsafe) the filter to be used to retrieve the location markers.
 *
 * @param filter the new filter to use
 */
  public void setLocationMarkerFilter(LocationMarkerFilter filter)
  {
    synchronized(location_marker_filter_lock_)
    {
      location_marker_filter_ = filter;
    }
  }

//----------------------------------------------------------------------
/**
 * Returns the filter to be used to retrieve the location markers.
 *
 * @return the new filter to use.
 */
  public LocationMarkerFilter getLocationMarkerFilter()
  {
    return(location_marker_filter_);
  }

//----------------------------------------------------------------------
/**
 * Creates and returns the location marker source that is used to
 * store and to retrieve location markers.
 *
 * @return the default location marker source to be used or null if an
 * error occured.
 */
  protected LocationMarkerSource getDefaultLocationMarkerSource()
  {
    LocationMarkerSource default_source = null;
    
    try
    {
      if(resources_.getBoolean(KEY_LOCATION_MARKER_USE_DB))
      {
            // use a jdbc database for location markers:
        String jdbc_driver = resources_.getString(KEY_LOCATION_MARKER_DB_JDBCDRIVER);
        String jdbc_url =  resources_.getString(KEY_LOCATION_MARKER_DB_URL);
        String jdbc_username =  resources_.getString(KEY_LOCATION_MARKER_DB_USER);
        String jdbc_password =  resources_.getString(KEY_LOCATION_MARKER_DB_PASSWORD,"");

        JDBCUtil jdbc_util = new JDBCUtil(jdbc_driver,jdbc_url,jdbc_username,jdbc_password);

        try
        {
          jdbc_util.setDebug(false);
          boolean could_open = false;
          try
          {
            jdbc_util.open();
            could_open = true;
          }
          catch(SQLException e)
          {
            logger_.warn("WARNING: could not open the database: "+e.getMessage());
          }
          if(!could_open || !jdbc_util.tableExists("MARKERS"))
          {
                // TODO: ask user, if database should be created!
            int result = JOptionPane.showConfirmDialog(null,
                                                       resources_.getString(KEY_LOCALIZE_MESSAGE_CREATE_DATABASE_MESSAGE),
                                                       resources_.getString(KEY_LOCALIZE_MESSAGE_INFO_TITLE),
                                                       JOptionPane.YES_NO_OPTION,
                                                       JOptionPane.QUESTION_MESSAGE);
            if(result ==  JOptionPane.YES_OPTION)
            {
              java.net.URL url = resources_.getURL(KEY_LOCATION_MARKER_DB_CREATE_DB_SCRIPT_URL);
              logger_.info("Database does not exist, creating it...");
              logger_.info("using the script at URL "+url);

              Reader reader = new InputStreamReader(url.openStream());
              if(!jdbc_driver.equals("org.hsqldb.jdbcDriver"))
              {
                  // TODO: ask for admin username and password, reconnect and create db!
                String[] messages =
                  new String[] {"Database Administrator Username/Password for Database:",
                               jdbc_url};
                could_open = false;
                while(!could_open)
                {
                  LoginDialog login_dialog =
                    new LoginDialog(dialog_owner_frame_, "Username/Password", messages);
                  result = login_dialog.getValue();
                  
                  if(result == LoginDialog.OK_OPTION)
                  {
                    jdbc_util = new JDBCUtil(jdbc_driver,jdbc_url,
                                             login_dialog.getUsername(),
                                             login_dialog.getPassword());
                    try
                    {
                      jdbc_util.open();
                      jdbc_util.executeSQL(reader);
                      could_open = true;
                    }
                    catch(SQLException e)
                    {
                      JOptionPane.showMessageDialog(dialog_owner_frame_,
                                                    "SQL Error occured: "+e.getMessage(),
                                                    resources_.getString(KEY_LOCALIZE_MESSAGE_ERROR_TITLE),
                                                    JOptionPane.ERROR_MESSAGE);
                      e.printStackTrace();
                    }
                  }
                  else
                  {
                    could_open = true;  // user gave up!
                  }
                }
              }
              else
              {
                jdbc_util.executeSQL(reader);
              }
            }
          }
          else
          {
            if(resources_.getBoolean(KEY_LOCATION_MARKER_DB_OPTIMIZE_DB_ON_START))
            {
              logger_.info("try to optimize database...");
              JDialog dialog = new JDialog(dialog_owner_frame_,resources_.getString(KEY_LOCALIZE_MESSAGE_INFO_TITLE),false);
              dialog.getContentPane().add(new JLabel(resources_.getString(KEY_LOCALIZE_MESSAGE_OPTIMIZE_DATABASE_MESSAGE)));
              dialog.pack();
              dialog.setVisible(true);
              Dimension screen_dim = Toolkit.getDefaultToolkit().getScreenSize();
              int x = (int) ((screen_dim.getWidth() - dialog.getWidth()) / 2);
              int y = (int) ((screen_dim.getHeight() - dialog.getHeight()) / 2);
              dialog.setLocation(x, y);
              
              jdbc_util.optimizeDatabase();
              
              dialog.setVisible(false);
              dialog.dispose();
              dialog=null;
              resources_.setBoolean(KEY_LOCATION_MARKER_DB_OPTIMIZE_DB_ON_START,false);
            }
          }
          jdbc_util.close();
        }
        catch(Exception e)
        {
          logger_.error("ERROR: orruced during testing/creating the location marker database:");
          e.printStackTrace();
        }
        default_source = new JDBCLocationMarkerSource(jdbc_driver,jdbc_url,
                                                      jdbc_username,jdbc_password, resources_);
        ((JDBCLocationMarkerSource)default_source).open();
      }
      else
      {
            // use a file to retrieve and store location markers (csv format)

            // first check and create the directory for the location markers:
        String dirname = FileUtil.getAbsolutePath(resources_.getString(KEY_FILE_MAINDIR),
                                                  resources_.getString(KEY_FILE_LOCATION_DIR));
        File dir = new File(dirname);
        if(!dir.isDirectory())
        {
          logger_.error("Directory '"+dirname+"' does not exist, creating it.");
          dir.mkdirs();
        }

	String marker_file = dirname + File.separator + resources_.getString(KEY_FILE_LOCATION_FILENAME);
	default_source = new FileLocationMarkerSource(resources_,marker_file);
        ((FileLocationMarkerSource)default_source).setNameColumn(0);
        ((FileLocationMarkerSource)default_source).setLatitudeColumn(1);
        ((FileLocationMarkerSource)default_source).setLongitudeColumn(2);
        ((FileLocationMarkerSource)default_source).setCategoryColumn(3);
        ((FileLocationMarkerSource)default_source).setDelimiter(',');
        ((FileLocationMarkerSource)default_source).initialize();
      }
    }
    catch(LocationMarkerSourceException lmse)
    {
      if(lmse.getCause() instanceof FileNotFoundException)
      {
        logger_.warn("WARNING: Location Marker file could not be found: "
                           +lmse.getCause().getMessage() +
                     " - create a location marker to create the file.");
      }
      else
            // TODO: open window and inform about error!
        lmse.printStackTrace();
    }
    return(default_source);
  }
  
//----------------------------------------------------------------------
/**
 * Add a source of locations to this marker
 */
  public void addLocationMarkerSource(LocationMarkerSource source)
  {
    if(location_sources_ == null)
      location_sources_ = new Vector();

    location_sources_.add(source);
  }

//----------------------------------------------------------------------
/**
 * Returns the LocationMarkerSource that is able to store new
 * LocationMarker objects (make them persistent).
 *
 * @return the LocationMarkerSource that will be used to store
 * new LocationMarkers.
 */
  public LocationMarkerSource getTargetLocationMarkerSource()
  {
    return(location_target_);
  }

//----------------------------------------------------------------------
/**
 * Sets a LocationMarkerSource that is able to store new
 * LocationMarker objects (make them persistent).
 *
 * @param target the LocationMarkerSource that will be used to store
 * new LocationMarkers.
 */
  public void setTargetLocationMarkerSource(LocationMarkerSource target)
  {
    location_target_ = target;
  }

  
//----------------------------------------------------------------------
/**
 * Callback method for property change events (ResourceBundle,
 * GPSDevice, etc.). Performes updates according to the values
 * of the PropertyChangeEvent.
 * 
 * @param event the property change event.
 */

  public void propertyChange(PropertyChangeEvent event)
  {
    if(!layer_active_)
      return;
    
    if(event.getPropertyName().equals(KEY_LOCATION_MARKER_TEXT_FONT_SIZE))
    {
      name_text_size_ = resources_.getInt(KEY_LOCATION_MARKER_TEXT_FONT_SIZE);
      name_text_font_ = null;  // will be created in the next paintComponent call!
      repaint();
      return;
    }

    if(event.getPropertyName().equals(KEY_LOCATION_MARKER_TEXT_COLOR))
    {
      name_color_text_ = resources_.getColor(KEY_LOCATION_MARKER_TEXT_COLOR);
      repaint();
      return;
    }

    if(event.getPropertyName().equals(KEY_LOCATION_MARKER_TEXT_BACKGROUND_COLOR))
    {
      name_color_background_ = resources_.getColor(KEY_LOCATION_MARKER_TEXT_BACKGROUND_COLOR);
      repaint();
      return;
    }

    if(event.getPropertyName().equals(KEY_LOCATION_MARKER_SHOW_NAMES))
    {
      show_names_ = resources_.getBoolean(KEY_LOCATION_MARKER_SHOW_NAMES);
      repaint(); // no recalculation needed!
//       System.out.println("show names changed to: "+show_names_);
//       System.out.println("registered: "+tooltip_registered_);

          // bug: the tooltip manager prevents the layers (all!) to receive
          // mouselistener events, so I removed it.
//       if(show_names_ && tooltip_registered_)
//       {
//         ToolTipManager tool_tip_manager = ToolTipManager.sharedInstance();
//         tool_tip_manager.unregisterComponent(this);
//         tooltip_registered_ = false;
// //         System.out.println("unregistered tooltips");
//       }
//       else
//         if(!show_names_ && !tooltip_registered_)
//         {
//           ToolTipManager tool_tip_manager = ToolTipManager.sharedInstance();
//           tool_tip_manager.registerComponent(this);
//           tooltip_registered_ = true;
// //           System.out.println("registered tooltips");
//         }
      return;
    }
  }

  
//----------------------------------------------------------------------
/**
 * If this layer is enabled, calls paint from its superclass.
 */
  public void paintComponent(Graphics g)
  {
    if(!layer_active_)
      return;

    if(location_markers_ == null)
      return;
    
    Graphics2D g2 = (Graphics2D)g;

    if(name_text_font_ == null)
      name_text_font_ = g.getFont().deriveFont((float)name_text_size_);

    if(location_markers_ != null)
    {
      LocationMarker marker;
      synchronized(location_markers_lock_)
      {
        Iterator iterator = location_markers_.iterator();
            //      System.out.println("markers.size: "+location_markers_.size());
        try
        {
          while(iterator.hasNext())
          {
            marker = (LocationMarker)iterator.next();
  
            paintLocationMarkerSymbol(g2,marker);
  
            if(show_names_)
              paintLocationMarkerName(g2,marker);
          }
        }
        catch(ClassCastException cce)
        {
          cce.printStackTrace();
        }
      }
    }
        // draw flag that indicates the position of a new location marker:
    if(new_marker_pos_ != null)
    {
      synchronized(new_marker_pos_lock_)
      {
        paintNewLocationMarker(g2,new_marker_pos_);
      }
    }
    
        // draw search result marker:
    if(search_result_marker_ != null)
    {
      synchronized(search_result_marker_lock_)
      {
        logger_.debug("Drawing search result: "+search_result_marker_);
        paintLocationMarkerSymbol(g2,search_result_marker_);
        paintLocationMarkerName(g2,search_result_marker_);
      }
    }
  }


//----------------------------------------------------------------------
/**
 * Forwards the request to a formerly set target location marker
 * source (see #setTargetLocationMarkerSource(LocationMarker). If no
 * target was set, nothing is done.
 *
 * @param new_marker the new marker to be added
 */
  public void addNewLocationMarker(LocationMarker new_marker)
  {
    if(location_target_ == null)
      return;
    
    try
    {
          // first, make marker persistent
      location_target_.putLocationMarker(new_marker);
          // second project marker and add it to list:
      new_marker.forward(getProjection());
      synchronized(location_markers_lock_)
      {
        location_markers_.add(new_marker);
      }
      synchronized(quad_tree_lock_)
      {
        if(quad_tree_ == null)
          quad_tree_ = new QuadTree(getProjection().getHeight(),0,
                                    0,getProjection().getWidth(),100);
        
        quad_tree_.put(new_marker.getY(),new_marker.getX(),new_marker);
      }
    }
    catch(UnsupportedOperationException uoe)
    {
      logger_.error("ERROR: the LocationMarkerSource set for writing does not support write-operations! Will not try it again!");
      location_target_ = null;
    }
    catch(LocationMarkerSourceException lmse)
    {
      logger_.error("ERROR: the LocationMarkerSource set for writing has thrown an exception: "+lmse.getMessage());
      lmse.getCause().printStackTrace();
    }
  }

  
  
//----------------------------------------------------------------------
/**
 * Called from the paintComponent method to draw the name of the
 * location markers. This is a factory method, so classes extending
 * this class may overwrite this method if they want to change the
 * way, the name of the location markers are drawn.
 *
 * @param g2 the graphics context to draw to
 * @param marker the marker to draw
 */
  protected void paintLocationMarkerName(Graphics2D g2, LocationMarker marker)
  {
    g2.setFont(name_text_font_);
    g2.setColor(name_color_background_);
    font_metrics_ = g2.getFontMetrics();
    String name = marker.getName();
    if(name.length() == 0)
      name = resources_.getString(KEY_LOCALIZE_EMPTY_MARKER_NAME);
//    System.out.println("painting name: '"+name+"'");
    int text_width = font_metrics_.stringWidth(name);

    int x = marker.getX() + name_offset_x_;
    int y = marker.getY();
    LocationMarkerCategory category = marker.getCategory();
    if(category != null)
    {
      ImageIcon icon = category.getIcon();
      if(icon != null)
      {
        x += icon.getIconWidth()/2;
      }
    }
    
    g2.setColor(name_color_background_);
    g2.fillRect(x, y - name_text_size_ - BORDER_SIZE,
                text_width + 2*BORDER_SIZE, name_text_size_ + 2*BORDER_SIZE);
    g2.setColor(name_color_text_);
    g2.drawString(name,x+BORDER_SIZE, y);
  }
  

//----------------------------------------------------------------------
/**
 * Called from the paintComponent method to draw the symbol of the
 * location markers. This is a factory method, so classes extending
 * this class may overwrite this method if they want to change the
 * way, the symbol of the location markers are drawn.
 *
 * @param g2 the graphics context to draw to
 * @param marker the marker to draw
 */
  protected void paintLocationMarkerSymbol(Graphics2D g2, LocationMarker marker)
  {
    LocationMarkerCategory category = marker.getCategory();
    if(category != null)
    {
      ImageIcon icon = marker.getCategory().getIcon();
      icon.paintIcon(this,g2,
                     marker.getX()-icon.getIconWidth()/2,
                     marker.getY()-icon.getIconHeight()/2);
    }
    else
    {
      g2.setColor(Color.red);
      g2.drawRect(marker.getX()-2,marker.getY()-2,4,4);
    }
  }


//----------------------------------------------------------------------
/**
 * Called from the paintComponent method to draw the new location
 * marker that is just about to be set (marks the selected position as
 * long as the "Set Location Marker" dialog is opened). This is a
 * factory method, so classes extending this class may overwrite this
 * method if they want to change the way, the symbol of the location
 * markers are drawn.
 *
 * @param g2 the graphics context to draw to.
 * @param new_marker the symbol for the new marker.
 */
  protected void paintNewLocationMarker(Graphics2D g2, GeoScreenPoint new_marker)
  {
        // draw kind of a flag (pole and flag with border):
    g2.setColor(Color.black);
    int x = new_marker.getX();
    int y = new_marker.getY();
    g2.drawLine(x,y-15,x,y);
    x++;
    g2.drawRect(x,y-15,8,6);
    x++;
    g2.setColor(Color.red);
    g2.fillRect(x,y-14,7,5);
  }
  
//----------------------------------------------------------------------
/**
 * Sets the location markers to be drawn (in a synchronized way).
 */
  protected void setLocationMarkers(GeoScreenList markers)
  {
    synchronized(location_markers_lock_)
    {
      location_markers_ = markers;
    }
  }

//----------------------------------------------------------------------
/**
 * Sets the quad tree (used for tool tip display) (in a synchronized way).
 */
  protected void setQuadTree(QuadTree quad_tree)
  {
    synchronized(quad_tree_lock_)
    {
      quad_tree_ = quad_tree;
    }
  }


//----------------------------------------------------------------------
/**
 * Returns the tooltip for the given location.
 *
 * @return the tooltip to display.
 */
  public String getToolTipText(MouseEvent event)
  {
        // WORKAROUND as unregistering does not help!
    if((!tooltip_registered_) || (quad_tree_ == null))
      return(null);
    synchronized(quad_tree_lock_)
    {
          // TODO: test, if the quad_tree method using a max dinstance is faster/slower!
      tooltip_markers_ = quad_tree_.get(event.getY()+tooltip_limit_,event.getX()-tooltip_limit_,
                                        event.getY()-tooltip_limit_,event.getX()+tooltip_limit_);
      if((tooltip_markers_ != null) && (tooltip_markers_.size() > 0))
      {
        String name = ((LocationMarker)tooltip_markers_.elementAt(0)).getName();
        if(name.length() == 0)
          name = resources_.getString(KEY_LOCALIZE_EMPTY_MARKER_NAME);
        return(name);
      }
      else
        return(null);
    }
  }


//----------------------------------------------------------------------
/**
 * Retrive all location markers for the given parameters (north,
 * south, west, east) from all location markers sources available. If
 * the filter creation found, that all categories are visible/not
 * visible, the result is respected here (ignoring the filter
 * passed!).
 *
 * @param north the north limit
 * @param south the south limit
 * @param west the west limit
 * @param east the east limit
 * @param filter the filter to use.
 * @param list if not null, this list is filled, otherwise a new is
 * created.
 * @return a list of locationmarkers, that is NOT projected! For
 * drawing on screen, you have to call the forward method before
 * reading screen coordinates of the markers.
 */
  protected GeoScreenList getAllLocationMarkers(float north, float south,
                                                float west, float east,
                                                LocationMarkerFilter filter,
                                                GeoScreenList list)
    throws LocationMarkerSourceException
  {
    if(location_sources_ == null)
      return(null);

    if(list == null)
      list = new GeoScreenList();
        // retrieve all location markers from their sources:
    LocationMarkerSource source;
    Iterator source_iterator = location_sources_.iterator();
	    
    while(source_iterator.hasNext())
    {
      source = (LocationMarkerSource)source_iterator.next();

          // check, if any categories are shown at all:
      if(!no_categories_visible_)
      {
            // show all? or no filters supported? then show all:
        if(all_categories_visible_ || !source.supportsFilters())
        {
          list = source.getLocationMarkers(north,south,west,east,list);
        }
        else
        {
              // use the filter:
          synchronized(location_marker_filter_lock_)
          {
            list = source.getLocationMarkers(north,south,west,east,
                                             filter, list);
          }
        }
      }
    }
    return(list);
  }

  
//----------------------------------------------------------------------
/**
 * Recalculate the screen coordinates of the available location
 * markers. This is done in a separate thread using a SwingWorker.
 */
  protected void recalculateLocationMarkers()
  {
    if(!layer_active_)
      return;
        // stop old thread
    if(swing_worker_ != null)
      swing_worker_.interrupt();

    swing_worker_ = new SwingWorker()
      {
        GeoScreenList worker_markers_ = new GeoScreenList();
        QuadTree worker_quad_tree_;
        
        public Object construct()
        {
          fireStatusUpdate(LayerStatusEvent.START_WORKING);

          if(location_sources_ == null)
            return(null);

              // recalculate the filter by the use of the categories
              // info (shown and level of detail:)
          setLocationMarkerFilter(createCategoryFilter(categories_));

          try
          {
                // retrieve all location markers for the current projection:
            GeoExtent extent = new GeoExtent(getProjection());
            if(Debug.DEBUG)
              Debug.println("LocationLayer","retrieve markers for "+extent);
            synchronized(location_marker_filter_lock_)
            {
              worker_markers_ = getAllLocationMarkers(extent.getNorth(),extent.getSouth(),
                                                      extent.getWest(),extent.getEast(),
                                                      location_marker_filter_,
                                                      worker_markers_);
            }
            if(Debug.DEBUG)
              Debug.println("LocationLayer","returned from retrieve markers for "+extent);
            
            if(Thread.interrupted())
            {
              if(Debug.DEBUG)
                Debug.println("LocationLayer","interrupted: retrieve markers for "+extent);
              worker_markers_ = null;
              return(null);
            }
                // calculate the screen coordinates of the location markers
            worker_markers_.forward(getProjection());


//             Iterator marker_iterator = worker_markers_.iterator();
//             LocationMarker marker;
//             worker_quad_tree_ = new QuadTree(getProjection().getHeight(),0,
//                                              0,getProjection().getWidth(),100);
//             while(marker_iterator.hasNext())
//             {
//               marker = (LocationMarker)marker_iterator.next();
// //             System.out.println("put marker into quadtree: "+marker);
//               worker_quad_tree_.put(marker.getY(),marker.getX(),marker);
//             }

            
                // if there is a new location marker set, calculate its projection as well
            if(new_marker_pos_ != null)
            {
              synchronized(new_marker_pos_lock_)
              {
                new_marker_pos_.forward(getProjection());
              }
            }

                // if there is a search result location marker, calculate:
            if(search_result_marker_ != null)
            {
              synchronized(search_result_marker_lock_)
              {
                search_result_marker_.forward(getProjection());
              }
            }
          }
          catch(LocationMarkerSourceException lmse)
          {
            logger_.error("ERROR: LocationMarkerSource threw an exception: "
                               +lmse.getMessage(),lmse.getCause());
          }
          
          return(null);
        }

        public void finished()
        {
          fireStatusUpdate(LayerStatusEvent.FINISH_WORKING);
          if(worker_markers_ != null)
          {
            setLocationMarkers(worker_markers_);
            setQuadTree(worker_quad_tree_);
            repaint();
          }
        }
      };
    swing_worker_.start();
  }



//----------------------------------------------------------------------
/**
 * Writes all location markers (of all location marker sources) into a
 * file. The fileformat is comma separated containing the following
 * columns: name, latitude, longitude, category_id.  <p> All means:
 * the location markers of the whole world, but still respecting the
 * current selection of categories! So if you want really all, select
 * all categories first!
 *
 * @param out the file to write to.
 * @exception IOException if an error occurs during writing.
 */
  public void exportLocationMarkers(File out)
    throws IOException, LocationMarkerSourceException
  {
    GeoScreenList markers = getAllLocationMarkers(90,-90,-180,180,location_marker_filter_,null);

    PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(out)));

    writer.println("name,latitude,longitude,category_id");
    
    Iterator iterator = markers.iterator();
    LocationMarker marker;
    LocationMarkerCategory category;
    while(iterator.hasNext())
    {
      marker = (LocationMarker)iterator.next();
      writer.print("\""+marker.getName()+"\"");
      writer.print(",");
      writer.print(marker.getLatitude());
      writer.print(",");
      writer.print(marker.getLongitude());
      writer.print(",");
      category = marker.getCategory();
      if(category == null)
        writer.println("");
      else
        writer.println(category.getId());
    }
    writer.close();
  }


//----------------------------------------------------------------------
/**
 * Reads all location markers from a given source and adds them.
 *
 * @param source the source to read.
 * @exception LocationMarkerSourceException if an error on reading
 * occurs.
 */
  public void importLocationMarkerSource(LocationMarkerSource source)
    throws LocationMarkerSourceException
  {
    GeoScreenList markers = source.getLocationMarkers(90,-90,-180,180);
    LocationMarker marker;
    Iterator iterator = markers.iterator();
    while(iterator.hasNext())
    {
      marker = (LocationMarker)iterator.next();
      addNewLocationMarker(marker);
    }
  }
  
//----------------------------------------------------------------------
/**
 * Creates and initializes the file chooser for loading and importing
 * location markers.
 */
  protected void createLoadLocationMarkerFileChooser()
  {
        load_file_chooser_ = new JFileChooser();
        load_file_chooser_.setDialogTitle(resources_.getString(KEY_LOCALIZE_LOAD_LOCATION_DIALOG_TITLE));
        ExtensionFileFilter filter;
        
            // add filter for csv location files:
        filter = new ExtensionFileFilter();
        filter.addExtension(resources_.getString(KEY_LOCATION_FILE_CSV_EXTENSION));
        filter.setDescription(resources_.getString(KEY_LOCATION_FILE_CSV_DESCRIPTIVE_NAME));
        load_file_chooser_.addChoosableFileFilter(filter);

            // add filter for gpsdrive location files:
        filter = new ExtensionFileFilter();
        filter.addExtension(resources_.getString(KEY_LOCATION_FILE_GPSDRIVE_EXTENSION));
        filter.setDescription(resources_.getString(KEY_LOCATION_FILE_GPSDRIVE_DESCRIPTIVE_NAME));
        load_file_chooser_.addChoosableFileFilter(filter);

            // add filter for geonet files:
        filter = new ExtensionFileFilter();
        filter.addExtension(resources_.getString(KEY_LOCATION_FILE_GEONET_EXTENSION));
//        filter.addExtension(resources_.getString(KEY_LOCATION_FILE_GEONET_COMPRESSED_EXTENSION));
        filter.addExtension(resources_.getString(KEY_LOCATION_FILE_ZIP_EXTENSION));
        filter.setDescription(resources_.getString(KEY_LOCATION_FILE_GEONET_DESCRIPTIVE_NAME));
        load_file_chooser_.addChoosableFileFilter(filter);

        load_file_chooser_.setAcceptAllFileFilterUsed(false);
        
        load_file_chooser_.setMultiSelectionEnabled(true);
        load_file_chooser_.setFileHidingEnabled(false);
//         String tracks_dirname = FileUtil.getAbsolutePath(resources_.getString(KEY_FILE_MAINDIR),
//                                                           resources_.getString(KEY_FILE_TRACK_DIR));
//          load_file_chooser_.setCurrentDirectory(new File(tracks_dirname));
  }

//----------------------------------------------------------------------
// MouseListener Adapter
//----------------------------------------------------------------------

  public void mouseClicked(MouseEvent event)
  {
//    System.out.println("LocationLayer mouseClicked: "+event.getSource());

    if(event.getButton() == MouseEvent.BUTTON1)
    {
      LatLonPoint point = getProjection().inverse(event.getX(),event.getY());

      if(event.isShiftDown())
      {
      }

      if(event.isControlDown())
      {
      }

      if(event.isAltDown())
      {
      }

          // no modifiers pressed:
      if(!event.isAltDown() && !event.isShiftDown() && !event.isControlDown())
      { 
        if((location_marker_frame_ != null) && location_marker_frame_.isVisible())
        {
          synchronized(new_marker_pos_lock_)
          {
            new_marker_pos_ = new GeoScreenPoint(point);
            new_marker_pos_.forward(getProjection());
          }
          location_marker_frame_.setCoordinates(point.getLatitude(),point.getLongitude());
          repaint();
        }
        
//        repaint();
      }
    } // end of if(Button1)
    
    if(event.isPopupTrigger())
    {
//      System.out.println("PopUp");
    }
  }

  public void mouseEntered(MouseEvent event)
  {
//    System.out.println("mouseEntered: "+event.getSource());
  }

  public void mouseExited(MouseEvent event)
  {
//    System.out.println("mouseExited: "+event.getSource());
  }

  public void mousePressed(MouseEvent event)
  {
        // on solaris, mousepressed sets popuptrigger
    if(event.isPopupTrigger())
    {
    }
//    System.out.println("mousePressed: "+event.getSource());
  }

  public void mouseReleased(MouseEvent event)
  {
        // on windows, mousereleased sets popuptrigger
    if(event.isPopupTrigger())
    {
    }
//    System.out.println("mouseReleased: "+event.getSource());
  }


//----------------------------------------------------------------------
// ProjectionListener interface implementation
//----------------------------------------------------------------------


//----------------------------------------------------------------------
/**
 * Handler for <code>ProjectionEvent</code>s.  This function is
 * invoked when the <code>MapBean</code> projection changes.  The
 * graphics are reprojected and then the Layer is repainted.
 * <p>
 * @param event the projection event
 */
  public void projectionChanged(ProjectionEvent event)
  {
    Projection proj = event.getProjection();

    if(proj == null)
      return;

    Projection old_proj = getProjection();
    if (!proj.equals(old_proj))
    {
      if(old_proj != null)
      {
        old_scale_ = old_proj.getScale();

          // adapt level of detail respecting the user's choice - only
          // change level of detail, if level of detail configured in
          // the resources file (depening on the scale) is more
          // appropriate than the user's choice:
        int level_for_scale = getLevelOfDetailForScale(proj.getScale());
        if(proj.getScale() < old_scale_) // zoom in
        {
          if(level_for_scale > current_level_of_detail_)
          {
            current_level_of_detail_ = level_for_scale;
          }
        }
        else
        {
          if(proj.getScale() > old_scale_) // zoom out
          {
            if(level_for_scale < current_level_of_detail_)
            {
              current_level_of_detail_ = level_for_scale;
            }
          }
        }
        if(Debug.DEBUG)
          Debug.println("levelofdetail","current level of detail: "+current_level_of_detail_);
      }
      setProjection(proj.makeClone());
      
      recalculateLocationMarkers();
    }
//     if(Debug.DEBUG)
//       Debug.println("LocationLayer_projection","new projection: "+proj);
  }



// ----------------------------------------------------------------------
// inner classes
// ----------------------------------------------------------------------

// ----------------------------------------------------------------------
// action classes

//----------------------------------------------------------------------
/**
 * The Action that triggers the de-/activation of this layer.
 */

  class LocationLayerActivateAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public LocationLayerActivateAction()
    {
      super(Gpsylon.ACTION_LOCATION_LAYER_ACTIVATE);
      putValue(MenuFactory.SELECTED, new Boolean(layer_active_));
    }

        //----------------------------------------------------------------------
        /**
         * Switches this layer on or off.
         * 
         * @param event the action event
         */

    public void actionPerformed(ActionEvent event)
    {
      if(Debug.DEBUG)
        Debug.println("LocationLayer","activate event: command: "+event.getActionCommand()+" "
                         +event.paramString());
      layer_active_ = !layer_active_;
      putValue(MenuFactory.SELECTED, new Boolean(layer_active_));
      resources_.setBoolean(KEY_LOCATION_LAYER_ACTIVE,layer_active_);
      if(!layer_active_)
        repaint();
      else
        recalculateLocationMarkers();
    }
  }

//----------------------------------------------------------------------
/**
 * The Action that sets a new marker at the current gps position.
 */

  class SetMarkerGPSPositionAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public SetMarkerGPSPositionAction()
    {
      super(Gpsylon.ACTION_SET_MARKER_GPS_POS);
    }

        //----------------------------------------------------------------------
        /**
         * Sets a Marker at the current gps position.
         * 
         * @param event the action event
         */

    public void actionPerformed(ActionEvent event)
    {
      if(positionable_ == null)
        return;

      if (location_marker_frame_ == null)
      {
        ActionListener action_listener = new ActionListener()
          {
            public void actionPerformed(ActionEvent event)
            {
              if(event.getActionCommand().equals(LocationMarkerFrame.COMMAND_OK))
              {
                if(location_marker_frame_.checkValidity())
                {
                  LocationMarker marker = location_marker_frame_.getLocationMarker();
//                System.out.println("set marker "+marker);                
                  addNewLocationMarker(marker);
                  repaint();
                }
                return;
              }

              if(event.getActionCommand().equals(LocationMarkerFrame.COMMAND_CLOSE))
              {
                new_marker_pos_ = null;
                repaint();
                location_marker_frame_.setVisible(false);
                return;
              }
            }
          };
        location_marker_frame_ = new LocationMarkerFrame(resources_,action_listener, dialog_owner_frame_);
        location_marker_frame_.setInfo(resources_.getString(KEY_LOCALIZE_CLICK_TO_CHANGE_POS_MESSAGE));
      }
      LatLonPoint pos = positionable_.getCurrentGPSPosition();
      location_marker_frame_.setCoordinates(pos.getLatitude(),pos.getLongitude());
//       Date now = new Date();
//       String default_name = DateFormat.getDateInstance(DateFormat.SHORT).format(now) + " "
//                             + DateFormat.getTimeInstance(DateFormat.SHORT).format(now);
//       location_marker_frame_.setMarkerName(default_name);
      synchronized(new_marker_pos_lock_)
      {
        new_marker_pos_ = new GeoScreenPoint(pos);
        new_marker_pos_.forward(getProjection());
      }
      repaint();
      location_marker_frame_.setVisible(true);
    }
  }
  
//----------------------------------------------------------------------
/**
 * The Action that triggers load location mode.
 */

  class LoadLocationAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public LoadLocationAction()
    {
      super(Gpsylon.ACTION_LOAD_LOCATION_MARKER);
    }

//----------------------------------------------------------------------
/**
 * Load a location
 * 
 * @param event the action event
 */
    public void actionPerformed(ActionEvent event)
    {
      File[] chosen_files = null;
      if(load_file_chooser_ == null)
      {
        createLoadLocationMarkerFileChooser();
      }
      
      int result = load_file_chooser_.showOpenDialog(LocationLayer.this);
      if(result == JFileChooser.APPROVE_OPTION)
      {
        chosen_files = load_file_chooser_.getSelectedFiles();
        String filter_description = ((ExtensionFileFilter)load_file_chooser_.getFileFilter()).getDescription();
        String type = "csv";
//         if(filter_description.startsWith(resources_.getString(KEY_LOCATION_FILE_CSV_DESCRIPTIVE_NAME)))
//           type = "csv";
//         else
        if(filter_description.startsWith(resources_.getString(KEY_LOCATION_FILE_GPSDRIVE_DESCRIPTIVE_NAME)))
            type = "gpsdrive";
	  
        for(int count = 0; count < chosen_files.length; count++)
        {
          try
          {
            FileLocationMarkerSource source =
              new FileLocationMarkerSource(resources_,chosen_files[count].getAbsolutePath());
            source.setNameColumn(0);
            source.setLatitudeColumn(1);
            source.setLongitudeColumn(2);
            source.setCategoryColumn(3);
            if(type.equals("gpsdrive"))
            {
              source.setRemoveEmptyElements(true);
              source.setDelimiter(' ');
            }
            else // csv:
            {
              source.setDelimiter(',');
            }

            source.initialize();
            addLocationMarkerSource(source);
          }
          catch(LocationMarkerSourceException e)
          {
                // TODO: open window and tell user!
            e.printStackTrace();
          }
        }
        recalculateLocationMarkers();
      }

    }
  }

//----------------------------------------------------------------------
/**
 * The Action that triggers load location mode.
 */

  class ImportLocationAction extends AbstractAction implements FeedBack
  {
    JDialog feedback_dialog_;
    JLabel feedback_label_;
    
        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public ImportLocationAction()
    {
      super(Gpsylon.ACTION_IMPORT_LOCATION_MARKER);
    }

//----------------------------------------------------------------------
/**
 * Import a location
 * 
 * @param event the action event
 */
    public void actionPerformed(ActionEvent event)
    {
      File[] chosen_files = null;
      if(load_file_chooser_ == null)
      {
        createLoadLocationMarkerFileChooser();
      }
      
      int result = load_file_chooser_.showOpenDialog(LocationLayer.this);
      if(result == JFileChooser.APPROVE_OPTION)
      {
        chosen_files = load_file_chooser_.getSelectedFiles();
        String filter_description = ((ExtensionFileFilter)load_file_chooser_.getFileFilter()).getDescription();
        String type = "csv";
//         if(filter_description.startsWith(resources_.getString(KEY_LOCATION_FILE_CSV_DESCRIPTIVE_NAME)))
//           type = "csv";
//         else
        if(filter_description.startsWith(resources_.getString(KEY_LOCATION_FILE_GPSDRIVE_DESCRIPTIVE_NAME)))
            type = "gpsdrive";
	  
        if(filter_description.startsWith(resources_.getString(KEY_LOCATION_FILE_GEONET_DESCRIPTIVE_NAME)))
            type = "geonet";

        String filename;
        for(int count = 0; count < chosen_files.length; count++)
        {
          try
          {
            if(Debug.DEBUG)
              Debug.println("LocationLayer","Import-type: "+type);
                // special treatment for geonet data is needed:
            if(type.equals("geonet"))
            {
              InputStream in_stream;
              filename = chosen_files[count].getName();
              if(filename.endsWith(".gz"))
                in_stream = new GZIPInputStream(new FileInputStream(chosen_files[count]));
              else
                if(filename.endsWith(".zip"))
                {
                  in_stream = new ZipInputStream(new FileInputStream(chosen_files[count]));
                  ZipEntry entry = ((ZipInputStream)in_stream).getNextEntry();
                  logger_.info("Reading zip entry: "+entry.getName());
                }
                else
                  in_stream = new FileInputStream(chosen_files[count]);

              final GeonetDataConverter converter = new GeonetDataConverter(in_stream);
              converter.setFeedback(this);

                  // start import in background:
              SwingWorker worker = new SwingWorker()
                {
                  public Object construct()
                  {
                    try
                    {
                      converter.insertIntoLocationMarkerSource(getTargetLocationMarkerSource());
                    }
                    catch(Exception e)
                    {
                      e.printStackTrace();
                    }
                    recalculateLocationMarkers();
                    resources_.setBoolean(KEY_LOCATION_MARKER_DB_OPTIMIZE_DB_ON_START,true);
//                     feedback_dialog_.setVisible(false);
//                     feedback_dialog_.dispose();
//                     feedback_dialog_ = null;
//                     feedback_label_ = null;
                    return(null);
                  }
                };
              worker.start();
            }
            else
            {
              FileLocationMarkerSource source =
                new FileLocationMarkerSource(resources_,chosen_files[count].getAbsolutePath());
              source.setNameColumn(0);
              source.setLatitudeColumn(1);
              source.setLongitudeColumn(2);
              source.setCategoryColumn(3);
              if(type.equals("gpsdrive"))
              {
                source.setRemoveEmptyElements(true);
                source.setDelimiter(' ');
              }
              else // csv:
              {
                source.setDelimiter(',');
              }

              source.initialize();
              importLocationMarkerSource(source);
              source = null;
              recalculateLocationMarkers();
              resources_.setBoolean(KEY_LOCATION_MARKER_DB_OPTIMIZE_DB_ON_START,true);
            }
          }
          catch(Exception e)
          {
                // TODO: open window and tell user!
            e.printStackTrace();
          }
        }
      }
    }

    public void feedBack(Object message)
    {
      if(feedback_dialog_ == null)
      {
        feedback_dialog_ =  new JDialog(dialog_owner_frame_,
                                        resources_.getString(KEY_LOCALIZE_MESSAGE_INFO_TITLE),false);
        feedback_dialog_.getContentPane().add(feedback_label_ = new JLabel("zero "+resources_.getString(KEY_LOCALIZE_MESSAGE_LINES_READ_MESSAGE)));
        feedback_dialog_.pack();
        feedback_dialog_.setVisible(true);
        Dimension screen_dim = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((screen_dim.getWidth() - feedback_dialog_.getWidth()) / 2);
        int y = (int) ((screen_dim.getHeight() - feedback_dialog_.getHeight()) / 2);
        feedback_dialog_.setLocation(x, y);
        feedback_dialog_.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      }
      feedback_label_.setText(message+" " + resources_.getString(KEY_LOCALIZE_MESSAGE_LINES_READ_MESSAGE));
      logger_.info(message+" " + resources_.getString(KEY_LOCALIZE_MESSAGE_LINES_READ_MESSAGE));
    }
  }

//----------------------------------------------------------------------
/**
 * The Action that triggers export location markers
 */

  class ExportLocationAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public ExportLocationAction()
    {
      super(Gpsylon.ACTION_EXPORT_LOCATION_MARKER);
    }

//----------------------------------------------------------------------
/**
 * Export all location markers!
 * 
 * @param event the action event
 */
    public void actionPerformed(ActionEvent event)
    {
      File chosen_file = null;
      if(export_file_chooser_ == null)
      {
        export_file_chooser_ = new JFileChooser();
        export_file_chooser_.setDialogTitle(resources_.getString(KEY_LOCALIZE_EXPORT_LOCATION_DIALOG_TITLE));
        ExtensionFileFilter filter;
        
            // add filter for csv location files:
        filter = new ExtensionFileFilter();
        filter.addExtension(resources_.getString(KEY_LOCATION_FILE_CSV_EXTENSION));
        filter.setDescription(resources_.getString(KEY_LOCATION_FILE_CSV_DESCRIPTIVE_NAME));
        export_file_chooser_.addChoosableFileFilter(filter);

//             // add filter for gpsdrive location files:
//         filter = new ExtensionFileFilter();
//         filter.addExtension(resources_.getString(KEY_LOCATION_FILE_GPSDRIVE_EXTENSION));
//         filter.setDescription(resources_.getString(KEY_LOCATION_FILE_GPSDRIVE_DESCRIPTIVE_NAME));
//         export_file_chooser_.addChoosableFileFilter(filter);

        export_file_chooser_.setAcceptAllFileFilterUsed(false);
        
        export_file_chooser_.setMultiSelectionEnabled(false);
        export_file_chooser_.setFileHidingEnabled(false);
//         String tracks_dirname = FileUtil.getAbsolutePath(resources_.getString(KEY_FILE_MAINDIR),
//                                                           resources_.getString(KEY_FILE_TRACK_DIR));
//          export_file_chooser_.setCurrentDirectory(new File(tracks_dirname));
      }
      
      int result = export_file_chooser_.showSaveDialog(LocationLayer.this);
      if(result == JFileChooser.APPROVE_OPTION)
      {
        chosen_file = export_file_chooser_.getSelectedFile();
        try
        {
          exportLocationMarkers(chosen_file);
        }
        catch(Exception ioe)
        {
          ioe.printStackTrace();
          JOptionPane.showMessageDialog(dialog_owner_frame_,
                                    resources_.getString(KEY_LOCALIZE_MESSAGE_EXPORT_ERROR_MESSAGE)
                                        +": "+ioe.getMessage(),
                                    resources_.getString(KEY_LOCALIZE_MESSAGE_ERROR_TITLE),
                                    JOptionPane.ERROR_MESSAGE);
        }
      }

    }
  }

//----------------------------------------------------------------------
/**
 * The Action that triggers to show the names of the markers or not.
 */

  class ShowMarkerNamesAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public ShowMarkerNamesAction()
    {
      super(Gpsylon.ACTION_SHOW_MARKER_NAMES);
      putValue(MenuFactory.SELECTED, new Boolean(show_names_));
    }

//----------------------------------------------------------------------
/**
 * Switch to show names of locations on/off
 * 
 * @param event the action event
 */
    public void actionPerformed(ActionEvent event)
    {
      show_names_  = !show_names_;
      resources_.setBoolean(KEY_LOCATION_MARKER_SHOW_NAMES,show_names_);
      putValue(MenuFactory.SELECTED, new Boolean(show_names_));

      repaint();
    }
  }


//----------------------------------------------------------------------
/**
 * The Action that triggers the dialog to choose the catagories that
 * are displayed.
 */

  class SelectMarkerCategoriesAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public SelectMarkerCategoriesAction()
    {
      super(Gpsylon.ACTION_SELECT_MARKER_CATEGORIES);
    }

//----------------------------------------------------------------------
/**
 * Show dialog that lets the user choose the categories that should be
 * displayed, and those that should not be displayed.
 * 
 * @param event the action event
 */
    public void actionPerformed(ActionEvent event)
    {
      if (select_category_frame_ == null)
      {
        ActionListener action_listener = new ActionListener()
          {
            public void actionPerformed(ActionEvent event)
            {
              if(event.getActionCommand().equals(SelectCategoryFrame.COMMAND_OK)
                || event.getActionCommand().equals(SelectCategoryFrame.COMMAND_APPLY))
              {
//                System.out.println("OK or APPLY");
                categories_ = select_category_frame_.getCategories();
                    // write them back to the resources:
                LocationMarkerCategory.setCategories(categories_);
                    // set the visible categories as a filter:
                setLocationMarkerFilter(createCategoryFilter(categories_));
                recalculateLocationMarkers();
              }

              if(event.getActionCommand().equals(SelectCategoryFrame.COMMAND_OK))
              {
//                System.out.println("OK");
                select_category_frame_.setVisible(false);
                return;
              }

              if(event.getActionCommand().equals(SelectCategoryFrame.COMMAND_CANCEL))
              {
//                System.out.println("CANCEL");
                    // reset to old categories:
                select_category_frame_.setCategories(LocationMarkerCategory.getCategories());
                select_category_frame_.setVisible(false);
                return;
              }
            }
          };
        select_category_frame_ = new SelectCategoryFrame(resources_,action_listener, dialog_owner_frame_);
      }
      select_category_frame_.setVisible(true);
    }
  }

//----------------------------------------------------------------------
/**
 * The Action that triggers the dialog to search a location marker.
 */

  class SearchMarkerAction extends AbstractAction 
  {

    SearchLocationMarkerFrame search_frame_;
    
        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public SearchMarkerAction()
    {
      super(Gpsylon.ACTION_SEARCH_MARKER);
    }

//----------------------------------------------------------------------
/**
 * Show dialog that lets the user choose the categories that should be
 * displayed, and those that should not be displayed.
 * 
 * @param event the action event
 */
    public void actionPerformed(ActionEvent event)
    {
      if (search_frame_ == null)
      {
        ActionListener action_listener = new ActionListener()
          {
            public void actionPerformed(ActionEvent event)
            {
              if(event.getActionCommand().equals(SearchLocationMarkerFrame.COMMAND_GOTO))
              {
//                System.out.println("OK or APPLY");
                logger_.debug("selected marker: " + search_frame_.getSelectedLocationMarker());
                synchronized(search_result_marker_lock_)
                {
                  search_result_marker_ = search_frame_.getSelectedLocationMarker();
                  if(search_result_marker_ != null)
                  {
                    search_result_marker_.forward(getProjection());
                  }
                }
                if((map_navigation_hook_ != null) && (search_result_marker_ != null))
                  map_navigation_hook_.setMapCenter(search_result_marker_.getLatitude(),
                                                    search_result_marker_.getLongitude());
              }
              
              if(event.getActionCommand().equals(SearchLocationMarkerFrame.COMMAND_CLOSE))
              {
//                System.out.println("CANCEL");
                    // reset to old categories:
                search_frame_.setVisible(false);
                search_frame_.dispose();
                search_frame_ = null;
                synchronized(search_result_marker_lock_)
                {
                  search_result_marker_ = null;
                }
                repaint();
                return;
              }
            }
          };
        search_frame_ = new SearchLocationMarkerFrame(resources_,action_listener, dialog_owner_frame_);
        Iterator source_iterator = location_sources_.iterator();
        LocationMarkerSource source;
        while(source_iterator.hasNext())
        {
          source = (LocationMarkerSource)source_iterator.next();
          search_frame_.addLocationMarkerSource(source);
        }
        
      }
      search_frame_.setVisible(true);
    }
  }

//----------------------------------------------------------------------
/**
 * The Action that triggers the DatabaseManager from HSQLDB
 */

  class DatabaseManagerAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public DatabaseManagerAction()
    {
      super(Gpsylon.ACTION_DATABASE_MANAGER);
    }

//----------------------------------------------------------------------
/**
 * Show dialog that lets the user choose the categories that should be
 * displayed, and those that should not be displayed.
 * 
 * @param event the action event
 */
    public void actionPerformed(ActionEvent event)
    {
      String[] arguments = new String[] {"-driver", resources_.getString(KEY_LOCATION_MARKER_DB_JDBCDRIVER),
                                         "-url", resources_.getString(KEY_LOCATION_MARKER_DB_URL),
                                         "-user",resources_.getString(KEY_LOCATION_MARKER_DB_USER),
                                         "-password",resources_.getString(KEY_LOCATION_MARKER_DB_PASSWORD)};
      DatabaseManagerSwing.main(arguments);
    }
  }

//----------------------------------------------------------------------
/**
 * The Action that triggers the change of the level of detail.
 */

  class LevelOfDetailChangeAction extends AbstractAction 
  {

    int change_value_;
    
        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public LevelOfDetailChangeAction(String id, int value)
    {
      super(id);
      change_value_ = value;
    }

//----------------------------------------------------------------------
/**
 * Show dialog that lets the user choose the categories that should be
 * displayed, and those that should not be displayed.
 * 
 * @param event the action event
 */
    public void actionPerformed(ActionEvent event)
    {
      current_level_of_detail_ += change_value_;
          // only values between 0 and 10 (inclusive) are valid:
      if((current_level_of_detail_ > 10) || (current_level_of_detail_ < 0))
      {
        current_level_of_detail_ -= change_value_;
      }
      else
        recalculateLocationMarkers();
      if(Debug.DEBUG)
        Debug.println("levelofdetail","current level of detail: "+current_level_of_detail_);
    }
  }

}



