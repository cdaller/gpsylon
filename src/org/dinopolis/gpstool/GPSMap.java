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

package org.dinopolis.gpstool;

import com.bbn.openmap.BufferedMapBean;
import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.Layer;
import com.bbn.openmap.LayerHandler;
import com.bbn.openmap.MapBean;
import com.bbn.openmap.proj.GreatCircle;
import com.bbn.openmap.proj.Length;
import com.bbn.openmap.proj.ProjMath;
import com.bbn.openmap.proj.Projection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import org.dinopolis.gpstool.alarm.AlarmConditionManager;
import org.dinopolis.gpstool.gpsinput.GPSDataProcessor;
import org.dinopolis.gpstool.gpsinput.GPSDevice;
import org.dinopolis.gpstool.gpsinput.GPSException;
import org.dinopolis.gpstool.gpsinput.GPSFileDevice;
import org.dinopolis.gpstool.gpsinput.GPSNetworkGpsdDevice;
import org.dinopolis.gpstool.gpsinput.GPSNmeaDataProcessor;
import org.dinopolis.gpstool.gpsinput.GPSPosition;
import org.dinopolis.gpstool.gpsinput.GPSRawDataFileLogger;
import org.dinopolis.gpstool.gpsinput.GPSSerialDevice;
import org.dinopolis.gpstool.gpsinput.GPSSimulationDataProcessor;
import org.dinopolis.gpstool.gui.LatLongInputDialog;
import org.dinopolis.gpstool.gui.MapKeyHandler;
import org.dinopolis.gpstool.gui.MouseMode;
import org.dinopolis.gpstool.gui.MouseModeManager;
import org.dinopolis.gpstool.gui.NmeaDataTextFrame;
import org.dinopolis.gpstool.gui.StatusBar;
import org.dinopolis.gpstool.gui.layer.DestinationLayer;
import org.dinopolis.gpstool.gui.layer.GraticuleLayer;
import org.dinopolis.gpstool.gui.layer.LocationLayer;
import org.dinopolis.gpstool.gui.layer.MultiMapLayer;
import org.dinopolis.gpstool.gui.layer.PositionLayer;
import org.dinopolis.gpstool.gui.layer.ScaleLayer;
import org.dinopolis.gpstool.gui.layer.ShapeLayer;
import org.dinopolis.gpstool.gui.layer.TestLayer;
import org.dinopolis.gpstool.gui.layer.TrackLayer;
import org.dinopolis.gpstool.plugin.GuiPlugin;
import org.dinopolis.gpstool.plugin.LayerPlugin;
import org.dinopolis.gpstool.plugin.MouseModePlugin;
import org.dinopolis.gpstool.plugin.Plugin;
import org.dinopolis.gpstool.plugin.WriteImagePlugin;
import org.dinopolis.gpstool.projection.FlatProjection;
import org.dinopolis.gpstool.util.ExtensionFileFilter;
import org.dinopolis.gpstool.util.FileUtil;
import org.dinopolis.gpstool.util.GeoMath;
import org.dinopolis.util.Debug;
import org.dinopolis.util.ResourceManager;
import org.dinopolis.util.Resources;
import org.dinopolis.util.commandarguments.CommandArgumentException;
import org.dinopolis.util.commandarguments.CommandArguments;
import org.dinopolis.util.commandarguments.InvalidCommandArgumentException;
import org.dinopolis.util.gui.ActionStore;
import org.dinopolis.util.gui.MenuFactory;
import org.dinopolis.util.gui.ResourceEditorFrame;
import org.dinopolis.util.gui.SplashScreen;
import org.dinopolis.util.servicediscovery.RepositoryClassLoader;
import org.dinopolis.util.servicediscovery.ServiceDiscovery;

//----------------------------------------------------------------------
/**
 * This is an application that shows maps, tracks, gps position,
 * etc. It heavily depends on its resources, as all default values are
 * stored there. The information is organized and painted in layers.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GPSMap
  implements PropertyChangeListener, GPSMapKeyConstants, 
	MapNavigationHook, StatusHook, MapManagerHook,
	Positionable
{

  public final static String GPSMAP_VERSION = "0.4.14-pre3";
  private final static String GPSMAP_CVS_VERSION = "$Revision$";

  public final static String STD_PLUGINS_DIR_NAME = "plugins";

  HookManager hook_manager_;
  
      /** the layer to display the maps */
  protected MultiMapLayer map_layer_;
  protected PositionLayer position_layer_;
  protected DestinationLayer destination_layer_;
  protected TrackLayer track_layer_;
  protected ShapeLayer shape_layer_;
  protected LocationLayer location_layer_;
  protected TestLayer test_layer_;
  protected ScaleLayer scale_layer_;
  protected GraticuleLayer graticule_layer_;
    
  protected MapBean map_bean_;

  protected JMenuBar menu_bar_;

  protected JFrame main_frame_;

  protected MapKeyHandler map_key_handler_;
  
  protected StatusBar status_bar_;
  protected Tachometer tacho_meter_;

//  protected MouseDelegator mouse_delegator_;
  protected MouseModeManager mouse_mode_manager_;

      /** the default center point */
  protected LatLonPoint current_gps_position_ = new LatLonPoint(47.06005f,15.47314f);
  
  protected LatLonPoint current_map_position_ = current_gps_position_;
  
      /** destination for gps track */
  protected LatLonPoint destination_position_;

      /** a Vector containing the map infos for the used maps */
  protected Vector map_infos_;
  protected HashSet used_map_filenames_ = new HashSet();

      /** static variables for distance formatting */
  protected static DecimalFormat distance_formatter_;
  protected static String distance_unit_;
  
  protected PropertyChangeSupport property_change_support_;
  
      /** the resource bundle (configuration) */
  protected Resources resources_;

  /** the log entry popup menu */;
  private JPopupMenu map_entry_menu_;

      /** the Resource editor */
  private ResourceEditorFrame resource_editor_;

      /** GPS processor */
  private GPSDataProcessor gps_data_processor_;

      /** the identifier used in the action store */
  public final static String ACTION_STORE_ID = "GPSMap";

      /** the name of the resource file */
  private final static String RESOURCE_BOUNDLE_NAME = "GPSMap";

      /** the name of the directory containing the resources */
  private final static String USER_RESOURCE_DIR_NAME = ".gpsmap";

      /** the action store */
  private ActionStore action_store_;

  protected boolean simulation_mode_ = false;

      /** the service discoverer (for plugin functionality) */
  public static ServiceDiscovery service_discovery_;
  public static RepositoryClassLoader repository_class_loader_;

      // -- action constants //
  
      /** the name of the quit action */
  public final static String ACTION_QUIT = "quit";

      /** the name of the edit properties action */
  public final static String ACTION_EDIT_RESOURCES = "edit_resources";

      // the name of mouse mode properties action 
  public final static String ACTION_MOUSE_NAVIGATION_MODE = "mouse_navigation_mode";
  public final static String ACTION_MOUSE_POSITION_MODE = "mouse_position_mode";
  public final static String ACTION_MOUSE_DISTANCE_MODE = "mouse_distance_mode";

      // map actions:
  public final static String ACTION_ZOOM_IN = "zoom_in";
  public final static String ACTION_ZOOM_OUT = "zoom_out";
  public final static String ACTION_CENTER_MAP = "center_map";

      // gps mode actions 
  public final static String ACTION_FOLLOW_ME_MODE = "followme_mode";
  public final static String ACTION_SIMULATION_MODE = "simulation_mode";

      // import actions 
  public final static String ACTION_IMPORT_GPSDRIVE = "import_gpsdrive";

      // track actions 
  public final static String ACTION_DISPLAY_TRACK_MODE = "display_track_mode";
  public final static String ACTION_SAVE_TRACK_MODE = "save_track_mode";
  public final static String ACTION_CLEAR_TRACK = "clear_track";
  public final static String ACTION_LOAD_TRACK = "load_track";

      // layer actions 
  public final static String ACTION_TRACK_LAYER_ACTIVATE = "track_layer_activate";
  public final static String ACTION_POSITION_LAYER_ACTIVATE = "position_layer_activate";
  public final static String ACTION_MAP_LAYER_ACTIVATE = "map_layer_activate";
  public final static String ACTION_SHAPE_LAYER_ACTIVATE = "shape_layer_activate";
  public final static String ACTION_LOCATION_LAYER_ACTIVATE = "location_layer_activate";
  public final static String ACTION_GRATICULE_LAYER_ACTIVATE = "graticule_layer_activate";
  public final static String ACTION_TEST_LAYER_ACTIVATE = "test_layer_activate";
  public final static String ACTION_SCALE_LAYER_ACTIVATE = "scale_layer_activate";

      // location marker actions
  public final static String ACTION_LOAD_LOCATION_MARKER = "load_location_marker";
  public final static String ACTION_EXPORT_LOCATION_MARKER = "export_location_marker";
  public final static String ACTION_IMPORT_LOCATION_MARKER = "import_location_marker";
  public final static String ACTION_SHOW_MARKER_NAMES = "show_marker_names";
  public final static String ACTION_SET_MARKER_GPS_POS = "set_marker_gps_pos";
  public final static String ACTION_SELECT_MARKER_CATEGORIES = "select_marker_categories";
  public final static String ACTION_DATABASE_MANAGER = "database_manager";
  public final static String ACTION_LEVEL_OF_DETAIL_INCREASE = "level_of_detail_increase";
  public final static String ACTION_LEVEL_OF_DETAIL_DECREASE = "level_of_detail_decrease";
  public final static String ACTION_SEARCH_MARKER = "search_marker";

      // save as... actions
  public final static String ACTION_SAVE_AS_IMAGE_PLUGIN = "save_as_image_plugin";
  
      // other actions 
  public final static String ACTION_RESET_TACHOMETER = "reset_tachometer";
  public final static String ACTION_SET_SCALE = "set_scale";
  public final static String ACTION_DOWNLOAD_MAP_POSITION = "download_map_position";
  public final static String ACTION_DOWNLOAD_MAP_AREA = "download_map_area";
  public final static String ACTION_LOAD_SHAPE = "load_shape";
  public final static String ACTION_VIEW_GPS_DATA = "view_gps_data";

      // pan actions:
  public final static String ACTION_PAN_WEST = "pan_west";
  public final static String ACTION_PAN_EAST = "pan_east";
  public final static String ACTION_PAN_NORTH = "pan_north";
  public final static String ACTION_PAN_SOUTH = "pan_south";
  public final static String ACTION_PAN_QUICK_WEST = "pan_quick_west";
  public final static String ACTION_PAN_QUICK_EAST = "pan_quick_east";
  public final static String ACTION_PAN_QUICK_NORTH = "pan_quick_north";
  public final static String ACTION_PAN_QUICK_SOUTH = "pan_quick_south";

      // scale action
  public final static String ACTION_SCALE_1000 = "set_scale_1000";
  public final static String ACTION_SCALE_2000 = "set_scale_2000";
  public final static String ACTION_SCALE_5000 = "set_scale_5000";
  public final static String ACTION_SCALE_10000 = "set_scale_10000";
  public final static String ACTION_SCALE_25000 = "set_scale_25000";
  public final static String ACTION_SCALE_50000 = "set_scale_50000";
  public final static String ACTION_SCALE_100000 = "set_scale_100000";
  public final static String ACTION_SCALE_200000 = "set_scale_200000";
  public final static String ACTION_SCALE_500000 = "set_scale_500000";
  public final static String ACTION_SCALE_1000000 = "set_scale_1000000";
  public final static String ACTION_SCALE_2000000 = "set_scale_2000000";
  public final static String ACTION_SCALE_5000000 = "set_scale_5000000";
  
      // test action
  public final static String ACTION_TESTACTION = "testaction";

      // the keys for gps property change events
  
      /** the key for the current location from the gps device. The
       * value is a {@com.bbn.openmap.LatLonPoint} object. */
  public final static String PROPERTY_KEY_GPS_LOCATION = GPSDataProcessor.LOCATION;
      /** the key for the heading from the gps device. Usually, you want to use
       * <code>PROPERTY_KEY_CURRENT_HEADING</code> instead! The value
       * is a Float object. */
  public final static String PROPERTY_KEY_GPS_HEADING = GPSDataProcessor.HEADING;
      /** the key for the heading either from the gps device or
       * calculated from the last and the current location. This
       * property is always triggered, even if the gps device does not
       * send heading-information. The value is a Float object. */
  public final static String PROPERTY_KEY_CURRENT_HEADING = "current.heading";
      /** the key for the speed from the gps device. Usually, you want
       * to use <code>PROPERTY_KEY_CURRENT_SPEED</code> instead! The
       * value is a Float object and the speed is in kilometers per
       * hour. */
  public final static String PROPERTY_KEY_GPS_SPEED = GPSDataProcessor.SPEED;
      /** the key for the speed either from the gps device or
       * calculated from the last and the current location. This
       * property is always triggered, even if the gps device does not
       * send speed-information. The value is a Float object and the
       * speed is in kilometers per hour. */
  public final static String PROPERTY_KEY_CURRENT_SPEED = "current.speed";
      /** the key for the altitude from the gps device. The
       * value is a Float object and the altitude is given in meters. */
  public final static String PROPERTY_KEY_GPS_ALTITUDE = GPSDataProcessor.ALTITUDE;
      /** the key for the info of the gps satellites from the gps device. The
       * value is an array of {@link org.dinopolis.gpstool.gpsinput.SatelliteInfo} objects. */
  public final static String PROPERTY_KEY_GPS_SATELLITE_INFO = GPSDataProcessor.SATELLITE_INFO;
  public final static String PROPERTY_KEY_ROUTE_DESTINATION = "route.destination";
  public final static String PROPERTY_KEY_ROUTE_DESTINATION_DISTANCE = "route.destination.distance";
  public final static String PROPERTY_KEY_TOTAL_DISTANCE = "total.distance";


  public final static float KM2MILES  = 0.62137119f;
  public final static float KM2NAUTIC = 0.54f;
  public final static float METER2FEET = 3.280839895f;


//----------------------------------------------------------------------
// The GPSMap class
//----------------------------------------------------------------------
//----------------------------------------------------------------------
/**
 * Empty Constructor
 */
  public GPSMap()
  {
    this(new String[]{});
  }


//----------------------------------------------------------------------
/**
 * Constructor using the command line arguments to override the resources.
 *
 * @param args command line argument (see printHelp for details)
 */
  public GPSMap(String[] args)
  {

    System.out.println("GPSMap V"+GPSMAP_VERSION);
    System.out.println("by Christof Dallermassl (christof@dallermassl.at)");
    System.out.println("latest version at: http://gpsmap.sourceforge.net");
    System.out.println("using");

    SplashScreen splash_screen = new SplashScreen("GPSMap V"+GPSMAP_VERSION,10000);


        // setup the GUI
    loadResources();
    setLocale();
    initFilenames();
    processCommandLineArguments(args);

            // initialize for plugins:
    initializePluginArchitecture();

        /** the Actions */
    Action[] actions_ = { new QuitAction(),
                          new EditResourcesAction(),
                          new MouseDistanceModeAction(),
                          new MouseNavigationModeAction(),
                          new MousePositionModeAction(),
                          new ZoomInAction(),
                          new ZoomOutAction(),
                          new PanAction(ACTION_PAN_WEST,-0.2f,0f),
                          new PanAction(ACTION_PAN_EAST,0.2f,0f),
                          new PanAction(ACTION_PAN_NORTH,0f,-0.2f),
                          new PanAction(ACTION_PAN_SOUTH,0f,0.2f),
                          new PanAction(ACTION_PAN_QUICK_WEST,-0.8f,0f),
                          new PanAction(ACTION_PAN_QUICK_EAST,0.8f,0f),
                          new PanAction(ACTION_PAN_QUICK_NORTH,0f,-0.8f),
                          new PanAction(ACTION_PAN_QUICK_SOUTH,0f,0.8f),
                          new ScaleAction(ACTION_SCALE_1000,1000f),
                          new ScaleAction(ACTION_SCALE_2000,2000f),
                          new ScaleAction(ACTION_SCALE_5000,5000f),
                          new ScaleAction(ACTION_SCALE_10000,10000f),
                          new ScaleAction(ACTION_SCALE_25000,25000f),
                          new ScaleAction(ACTION_SCALE_50000,50000f),
                          new ScaleAction(ACTION_SCALE_100000,100000f),
                          new ScaleAction(ACTION_SCALE_200000,200000f),
                          new ScaleAction(ACTION_SCALE_500000,500000f),
                          new ScaleAction(ACTION_SCALE_1000000,1000000f),
                          new ScaleAction(ACTION_SCALE_2000000,2000000f),
                          new ScaleAction(ACTION_SCALE_5000000,5000000f),
                          new ResetTachometerAction(),
                          new SimulationModeAction(),
                          new SetScaleAction(),
                          new ViewGPSDataAction(),
                          new CenterMapAction(),
                          new SaveAsImagePluginAction(),
//                                new TestAction(),
                          new ImportGpsDriveAction()
    };
  

    action_store_ = ActionStore.getStore(ACTION_STORE_ID);
    action_store_.addActions(actions_);

    property_change_support_ = new PropertyChangeSupport(this);

        // Create a Swing frame.  The OpenMapFrame knows how to use
        // the MapHandler to locate and place certain objects.
//    main_frame_ = new OpenMapFrame("GPS Map");
    main_frame_ = new JFrame("GPS Map V"+GPSMAP_VERSION);
        // Size the frame appropriately
    main_frame_.setSize(640, 480);
    
    main_frame_.addWindowListener(new WindowAdapter() 
      {
        public void windowClosing(WindowEvent e) 
        {
              // TODO QuitAction!!!! FIXXME????
          try
          {
            if(gps_data_processor_ != null)
              gps_data_processor_.close();
          }
          catch(GPSException gpse)
          {
            System.err.println("WARNING: could not close connection to gps device: "
                               +gpse.getMessage());
          }
          
          System.exit(0);
        }
      }
      );
    
    checkLockFiles(resources_.getStringArray(KEY_LOCKFILES));

    updateResources(null);
    updateWindowLocation();

    
        // Create a MapBean
    map_bean_ = new MapBean();
    map_bean_.setBackgroundColor(new Color(0,0,0));
    map_bean_.setDoubleBuffered(true);

        // initialize data for plugins (PluginSupport):
    hook_manager_ = new HookManager();
    hook_manager_.setMapManagerHook(this);
    hook_manager_.setMapNavigationHook(this);
    hook_manager_.setStatusHook(this); 
    hook_manager_.setMainFrame(main_frame_);
    hook_manager_.setMapComponent(map_bean_);
    hook_manager_.setPropertyChangeSupport(property_change_support_);
    hook_manager_.setResources(resources_);

        // create MouseModeManager
    mouse_mode_manager_ = new MouseModeManager();

     // Set the default gps position
    double latitude = resources_.getDouble(KEY_CURRENT_GPS_POSITION_LATITUDE);
    double longitude = resources_.getDouble(KEY_CURRENT_GPS_POSITION_LONGITUDE);
    current_gps_position_ = new LatLonPoint(latitude,longitude);
    property_change_support_.firePropertyChange(PROPERTY_KEY_GPS_LOCATION,null,current_gps_position_);

        // Set the default map position
    latitude = resources_.getDouble(KEY_CURRENT_MAP_POSITION_LATITUDE);
    longitude = resources_.getDouble(KEY_CURRENT_MAP_POSITION_LONGITUDE);
    current_map_position_ = new LatLonPoint(latitude,longitude);

        // set projection (including position and scale)
    Projection projection = new FlatProjection(current_map_position_,
                                               (float)resources_.getDouble(KEY_MAP_SCALE),0,0);
    map_bean_.setProjection(projection);

    main_frame_.getContentPane().add(map_bean_,BorderLayout.CENTER);

    status_bar_ = new StatusBar(resources_,this);
    main_frame_.getContentPane().add(status_bar_,BorderLayout.SOUTH);
    
    
        // instantiate, initialize and add plugins (layers, gui and mousemdoes)
    initializePlugins();
    
    graticule_layer_ = new GraticuleLayer();
    graticule_layer_.initialize(resources_);
    map_bean_.add(graticule_layer_);
    
    test_layer_ = new TestLayer(resources_);
    map_bean_.add(test_layer_);

    scale_layer_ = new ScaleLayer(resources_);
    map_bean_.add(scale_layer_);

    shape_layer_ = new ShapeLayer(resources_);
    map_bean_.add(shape_layer_);
    
    track_layer_ = new TrackLayer();
    track_layer_.initialize(resources_);
    map_bean_.add(track_layer_);
    
    destination_layer_ = new DestinationLayer(resources_);
    map_bean_.add(destination_layer_);
    map_bean_.addMouseListener(destination_layer_);
    map_bean_.addMouseMotionListener(destination_layer_);

    position_layer_ = new PositionLayer(resources_);
    map_bean_.add(position_layer_);
    map_bean_.addMouseListener(position_layer_);
    map_bean_.addMouseMotionListener(position_layer_);

    position_layer_.setNewCurrentPosition(current_gps_position_);

        // the map key handler must directly follow the position
        // layer, otherwise no key events are received!
//     map_key_handler_ = new MapKeyHandler(this);
//     position_layer_.addKeyListener(map_key_handler_);
    
    location_layer_ = new LocationLayer();
    location_layer_.initialize(resources_,this,this,main_frame_);
    map_bean_.add(location_layer_);
    map_bean_.addMouseListener(location_layer_);

    map_layer_ = new MultiMapLayer();
    map_layer_.initialize(resources_);
    map_bean_.add(map_layer_);


    tacho_meter_ = new Tachometer();

    // connect some modules by the use of their eventhandler:
    addPropertyChangeListener(PROPERTY_KEY_GPS_LOCATION, position_layer_);
    addPropertyChangeListener(PROPERTY_KEY_CURRENT_HEADING, position_layer_);
    resources_.addPropertyChangeListener(KEY_POSITION_USE_ICON,position_layer_);
    resources_.addPropertyChangeListener(KEY_POSITION_FOLLOW_ME_RELATIVE_BORDER,position_layer_);
    
    addPropertyChangeListener(PROPERTY_KEY_GPS_LOCATION, destination_layer_);

    addPropertyChangeListener(PROPERTY_KEY_GPS_LOCATION, track_layer_);
//    addPropertyChangeListener(PROPERTY_KEY_GPS_SPEED, track_layer_);
    addPropertyChangeListener(PROPERTY_KEY_GPS_ALTITUDE, track_layer_);
    addPropertyChangeListener(PROPERTY_KEY_CURRENT_SPEED, track_layer_);
    
    addPropertyChangeListener(PROPERTY_KEY_GPS_LOCATION, status_bar_);
    addPropertyChangeListener(PROPERTY_KEY_CURRENT_HEADING, status_bar_);
    resources_.addPropertyChangeListener(KEY_ANGLE_FORMAT_LATLON, status_bar_);
    resources_.addPropertyChangeListener(KEY_ANGLE_FORMAT_HEADING, status_bar_);
//    addPropertyChangeListener(PROPERTY_KEY_GPS_SPEED, status_bar_);
    addPropertyChangeListener(PROPERTY_KEY_CURRENT_SPEED, status_bar_);
    addPropertyChangeListener(PROPERTY_KEY_TOTAL_DISTANCE, status_bar_);
    addPropertyChangeListener(PROPERTY_KEY_ROUTE_DESTINATION_DISTANCE, status_bar_);
    addPropertyChangeListener(PROPERTY_KEY_GPS_SATELLITE_INFO, status_bar_);

    addPropertyChangeListener(PROPERTY_KEY_GPS_LOCATION, tacho_meter_);
    addPropertyChangeListener(PROPERTY_KEY_GPS_SPEED, tacho_meter_);

    resources_.addPropertyChangeListener(KEY_UNIT_DISTANCE, scale_layer_);
    resources_.addPropertyChangeListener(KEY_GRATICULE_DRAW_TEXT,graticule_layer_);

    resources_.addPropertyChangeListener(KEY_LOCATION_MARKER_TEXT_COLOR,location_layer_);
    resources_.addPropertyChangeListener(KEY_LOCATION_MARKER_TEXT_BACKGROUND_COLOR,location_layer_);
    resources_.addPropertyChangeListener(KEY_LOCATION_MARKER_TEXT_FONT_SIZE,location_layer_);
    resources_.addPropertyChangeListener(KEY_LOCATION_MARKER_SHOW_NAMES,location_layer_);
    
    position_layer_.addPropertyChangeListener(this);
    position_layer_.setMapNavigationHook(this);

    destination_layer_.addPropertyChangeListener(this);

        // add the status bar as StatusLayerListener for all layers:
    map_layer_.addLayerStatusListener(status_bar_);
    track_layer_.addLayerStatusListener(status_bar_);
    shape_layer_.addLayerStatusListener(status_bar_);
    graticule_layer_.addLayerStatusListener(status_bar_);
    location_layer_.addLayerStatusListener(status_bar_);

        // load maps:
    map_infos_ = loadMapInfo();
    map_layer_.addMaps(map_infos_);

        // create popup menu
//    map_popup_menu_ = createPopupMenu();

        // add menu bar at the end (maybe some modules add actions to it!)
    menu_bar_ = MenuFactory.createMenuBar(resources_, action_store_);
    
    addMouseModesToMenu();
    
    main_frame_.setJMenuBar(menu_bar_);

//      map_bean_.add(graticule_layer_);
//      map_bean_.add(test_layer_);
//      map_bean_.add(scale_layer_);
//      map_bean_.add(location_layer_);
//      map_bean_.add(shape_layer_);
//  //    map_bean_.add(track_layer_);
//      map_bean_.add(destination_layer_);
//  //      map_bean_.add(position_layer_);
//  //    map_bean_.add(map_layer_);

    main_frame_.setVisible(true);
//     main_frame_.getContentPane().requestFocus();
//     main_frame_.getContentPane().addKeyListener(new GPSMapKeyListener());

    System.out.println("Plugins in the following locations are used:");
    List plugin_dirs = repository_class_loader_.getRepositories();
    Iterator dir_iterator = plugin_dirs.iterator();
    if(!dir_iterator.hasNext())
      System.out.println("no directories given");
    else
    {
      while(dir_iterator.hasNext())
      {
        System.out.println(dir_iterator.next());
      }
    }

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
        ResourceManager.getResources(GPSMap.class,
                                     RESOURCE_BOUNDLE_NAME,
                                     USER_RESOURCE_DIR_NAME,
                                     Locale.getDefault());
      resources_.addPropertyChangeListener(this);
    }
    catch (MissingResourceException mre) 
    {
      if (Debug.DEBUG)
        Debug.println("GPSMap", mre.toString()+'\n'+
                      Debug.getStackTrace(mre));
      System.err.println("resource file "+RESOURCE_BOUNDLE_NAME+" not found");
      System.err.println("please make sure that this file is within the classpath !");
      System.exit(1);
    }
  }

//----------------------------------------------------------------------
/**
 * Sets the locales given in the resource file, if any.
 */

  void setLocale()
  {
    String language = resources_.getString("locale.language", null);
    String country = resources_.getString("locale.country", null);
    if ((language != null) && (country != null))
      Locale.setDefault(new Locale(language, country));
  }

//----------------------------------------------------------------------
/**
 * Initializes the file- and directorynames, if they are unset.
 */

  void initFilenames()
  {
    String main_dir = resources_.getString(KEY_FILE_MAINDIR,null);
    if((main_dir == null) || (main_dir.length() == 0))
    {
          // maindir:
      String maindir = System.getProperty("user.home")+ File.separator + USER_RESOURCE_DIR_NAME;
//      System.out.println("maindir: "+maindir);
      resources_.setString(KEY_FILE_MAINDIR,maindir);
//      System.out.println("maindir from resources: "+resources_.getString(KEY_FILE_MAINDIR));
    }
  }
  
  
//----------------------------------------------------------------------
/**
 * Loads the mapinfo data from the map description file and sends it
 * to the map layer.
 *
 * @return a Vector containing MapInfo objects.
 */

  protected Vector loadMapInfo()
  {
    String main_dir = resources_.getString(KEY_FILE_MAINDIR);

    String description_filename = resources_.getString(KEY_FILE_MAP_DESCRIPTION_FILE);

    
    String full_name = FileUtil.getAbsolutePath(main_dir,description_filename);

    Vector map_infos = new Vector();

    BufferedReader map_reader;
    try
    {
      map_reader= new BufferedReader(new FileReader(full_name));
    }
    catch(FileNotFoundException fnfe)
    {
      System.err.println("ERROR: Could not open map description file '"+full_name+"'");
      JOptionPane.showMessageDialog(main_frame_,
                                    resources_.getString(KEY_LOCALIZE_MESSAGE_FILE_NOT_FOUND_MESSAGE)
                                    +": '"+full_name+"'",
                                    resources_.getString(KEY_LOCALIZE_MESSAGE_ERROR_TITLE),
                                    JOptionPane.ERROR_MESSAGE);
      return(map_infos);
    }

    try
    {
      int linenumber = 0;
      String line;
      String latitude_string;
      String longitude_string;
      String map_filename;
      String scale_string;
      String image_height_string;
      String image_width_string;
      MapInfo map_info;
      StringTokenizer tokenizer;
      
      while ((line = map_reader.readLine()) != null)
      {
        linenumber++;
        if ((!line.startsWith("#")) && (line.length() > 0))
        {
          try
          {
            tokenizer = new StringTokenizer(line);
            map_filename = tokenizer.nextToken();
            latitude_string = tokenizer.nextToken();
            longitude_string = tokenizer.nextToken();
            scale_string = tokenizer.nextToken();
            image_width_string = tokenizer.nextToken();
            image_height_string = tokenizer.nextToken();

                // check for absolute or relative pathnames:
            File map_file = new File(map_filename);
            if(!map_file.isAbsolute())
              map_filename = new File(main_dir,map_filename).getCanonicalPath();

            used_map_filenames_.add(map_filename);
            
            try
            {
              map_info = new MapInfo(map_filename,
                                     Double.parseDouble(latitude_string),
                                     Double.parseDouble(longitude_string),
                                     Float.parseFloat(scale_string),
                                     Integer.parseInt(image_width_string),
                                     Integer.parseInt(image_height_string));
//              System.out.println("MapInfo loaded: "+map_info);
              map_infos.add(map_info);
            }
            catch(NumberFormatException nfe)
            {
              System.err.println("ERROR: Wrong format in line : "
                               +linenumber+" in file '"+full_name+"':"
                                 +nfe.getMessage());
              System.err.println("Ignoring line '"+line+"'");
            }
          }
          catch(NoSuchElementException nsee)
          {
            System.err.println("ERROR: reading map description in line "
                               +linenumber+" in file '"+full_name+"'");
            System.err.println("The correct format of the map description file is:");
            System.err.println("<mapfilename> <latitude of center> <longitude of center> <scale> <width> <height>");
            System.err.println("Ignoring line '"+line+"'");
          }
        }
      }      
      map_reader.close();
    }
    catch(IOException ioe)
    {
      ioe.printStackTrace();
    }
    finally
    {
      return(map_infos);
    }
  }


//----------------------------------------------------------------------
/**
 * Looks for a menu or a menu item with the given name in the given
 * menu bar.
 *
 * @param menu_bar the menu bar to search.
 * @param name the name to search for.
 * @return the menu/menuitem or <ocde>null</code> if no menu was found.
 */
  protected JMenuItem findMenuItem(JMenuBar menu_bar, String name)
  {
    int menu_index = 0;
    JMenuItem menu_item;
    while(menu_index < menu_bar.getMenuCount())
    {
      menu_item = menu_bar.getMenu(menu_index);
      if(menu_item.getText().equals(name))
      {
        return(menu_item);
      }
      menu_index++;
    }
        // nothing found:
    return(null);
  }

//----------------------------------------------------------------------
/**
 * Looks for a menu or a menu item with the given name in the given menu.
 *
 * @param menu the menu to search.
 * @param name the name to search for.
 * @return the menu/menuitem or <ocde>null</code> if no menu was found.
 */
  protected JMenuItem findMenuItem(JMenu menu, String name)
  {
    int menu_index = 0;
    JMenuItem menu_item;
    while(menu_index < menu.getItemCount())
    {
      menu_item = menu.getItem(menu_index);
      if(menu_item.getText().equals(name))
      {
        return(menu_item);
      }
      menu_index++;
    }
        // nothing found:
    return(null);
  }

//----------------------------------------------------------------------
/**
 * Adds the mouse modes of the MouseModeManager to the menu bar (Menu
 * Mouse Mode).
 *
 */
  protected void addMouseModesToMenu()
  {
        // find the menu that should contain the mouse modes (control/mouse mode):

    JMenu mouse_mode_menu = (JMenu)findMenuItem(menu_bar_,
                                                resources_.getString(KEY_MENU_MOUSE_MODE_LABEL));
    if(mouse_mode_menu != null)
    {
      JMenuItem[] mouse_mode_items = mouse_mode_manager_.getMenuItems();
      for(int item_count = 0; item_count < mouse_mode_items.length; item_count++)
      {
//          System.out.println("Adding Mouse Mode "+mouse_mode_items[item_count] +" to menu.");
        mouse_mode_menu.add(mouse_mode_items[item_count]);
      }
    }
    else
    {
      System.err.println("ERROR: Could not find 'Mouse Mode' menu, no mouse modes added to menu!");
    }
  }

//----------------------------------------------------------------------
/**
 * Adds the sub menus of plugins to the  menu bar (Menu Plugin).
 */
  protected void addToPluginsMenu(JMenuItem plugin_sub_menu)
  {
        // find the menu that should contain the mouse modes (control/mouse mode):

    JMenu plugin_menu = (JMenu)findMenuItem(menu_bar_,
                                            resources_.getString(KEY_MENU_PLUGIN_LABEL));
    if(plugin_menu != null)
    {
      JMenuItem[] mouse_mode_items = mouse_mode_manager_.getMenuItems();
      for(int item_count = 0; item_count < mouse_mode_items.length; item_count++)
      {
//          System.out.println("Adding Mouse Mode "+mouse_mode_items[item_count] +" to menu.");
        plugin_menu.add(mouse_mode_items[item_count]);
      }
    }
    else
    {
      System.err.println("ERROR: Could not find 'Plugins' menu, no plugin menus added to menu!");
    }
  }

//----------------------------------------------------------------------
/**
 * Processes the command line arguments and sets resource values if
 * needed.
 *
 * @param args The command line arguments
 */

  protected void processCommandLineArguments(String[] arguments)
  {
    String[] valid_args =
      new String[] {"device*","d*","help","h","speed#","s#","gpsfile*","f*",
                    "nmealogfile*","l*","gpsdhost*","g*","gpsdport#","p#",
                    "file","gpsd","serial","alarmfile*"};

    CommandArguments args = null;
    try
    {
      args = new CommandArguments(arguments,valid_args);
    }
    catch(CommandArgumentException cae) 
    {
      cae.printStackTrace();
    }
    if (args != null)
    {
      if(args.isSet("help") || (args.isSet("h")))
      {
        printHelp();
        System.exit(0);
      }

      if (args.isSet("device"))
      {
        resources_.setString(KEY_GPS_DEVICE_SERIAL_PORT,args.getStringValue("device"));
        resources_.setString(KEY_GPS_DEVICE_MODE,VALUE_KEY_DEVICE_MODE_SERIAL);
      }
      else 
      if (args.isSet("d"))
      {
        resources_.setString(KEY_GPS_DEVICE_SERIAL_PORT,args.getStringValue("d"));
        resources_.setString(KEY_GPS_DEVICE_MODE,VALUE_KEY_DEVICE_MODE_SERIAL);
      }

      if (args.isSet("speed"))
      {
        resources_.setString(KEY_GPS_DEVICE_SERIAL_SPEED,args.getStringValue("speed"));
        resources_.setString(KEY_GPS_DEVICE_MODE,VALUE_KEY_DEVICE_MODE_SERIAL);
      }
      else 
      if (args.isSet("s"))
      {
        resources_.setString(KEY_GPS_DEVICE_SERIAL_SPEED,args.getStringValue("s"));
        resources_.setString(KEY_GPS_DEVICE_MODE,VALUE_KEY_DEVICE_MODE_SERIAL);
      }

      if (args.isSet("gpsfile") || args.isSet("f"))
      {
        String filename;
        if(args.isSet("f"))
          filename = args.getStringValue("f");
        else
          filename = args.getStringValue("gpsfile");

        if(filename.equals("none"))
        {
          resources_.setString(KEY_GPS_DEVICE_DATA_FILENAME,"");
          resources_.setString(KEY_GPS_DEVICE_MODE,VALUE_KEY_DEVICE_MODE_NONE);
        }
        else
        {
          resources_.setString(KEY_GPS_DEVICE_DATA_FILENAME,filename);
          resources_.setString(KEY_GPS_DEVICE_MODE,VALUE_KEY_DEVICE_MODE_FILE);
        }
           
      }
      else 
      if (args.isSet("f"))
      {
        resources_.setString(KEY_GPS_DEVICE_DATA_FILENAME,args.getStringValue("f"));
        resources_.setString(KEY_GPS_DEVICE_MODE,VALUE_KEY_DEVICE_MODE_FILE);
      }

      if (args.isSet("nmealogfile") || (args.isSet("l")))
      {
        String nmea_log_file;
        if(args.isSet("nmealogfile"))
          nmea_log_file = args.getStringValue("nmealogfile");
        else
          nmea_log_file = args.getStringValue("l");
          
        if(nmea_log_file.equals("none"))
          resources_.setString(KEY_GPS_DEVICE_NMEALOGFILE,"");
        else
          resources_.setString(KEY_GPS_DEVICE_NMEALOGFILE,nmea_log_file);
      }

      if (args.isSet("gpsdhost"))
      {
        resources_.setString(KEY_GPS_DEVICE_GPSD_HOST,args.getStringValue("gpsdhost"));
        resources_.setString(KEY_GPS_DEVICE_MODE,VALUE_KEY_DEVICE_MODE_FILE);
      }
      else 
      if (args.isSet("g"))
      {
        resources_.setString(KEY_GPS_DEVICE_GPSD_HOST,args.getStringValue("g"));
        resources_.setString(KEY_GPS_DEVICE_MODE,VALUE_KEY_DEVICE_MODE_FILE);
      }

      if (args.isSet("gpsdport"))
      {
        resources_.setString(KEY_GPS_DEVICE_GPSD_PORT,args.getStringValue("gpsdport"));
        resources_.setString(KEY_GPS_DEVICE_MODE,VALUE_KEY_DEVICE_MODE_FILE);
      }
      else 
      if (args.isSet("p"))
      {
        resources_.setString(KEY_GPS_DEVICE_GPSD_PORT,args.getStringValue("p"));
        resources_.setString(KEY_GPS_DEVICE_MODE,VALUE_KEY_DEVICE_MODE_FILE);
      }

      if (args.isSet("file"))
      {
        resources_.setString(KEY_GPS_DEVICE_MODE,VALUE_KEY_DEVICE_MODE_FILE);
      }
      if (args.isSet("gpsd"))
      {
        resources_.setString(KEY_GPS_DEVICE_MODE,VALUE_KEY_DEVICE_MODE_GPSD);
      }
      if (args.isSet("serial"))
      {
        resources_.setString(KEY_GPS_DEVICE_MODE,VALUE_KEY_DEVICE_MODE_SERIAL);
      }
      if (args.isSet("alarmfile"))
      {
	String filename = args.getStringValue("alarmfile");
	if(filename.equals("none"))
	   filename = "";
        resources_.setString(KEY_ALARM_FILE,filename);
      }
    }
  }

//----------------------------------------------------------------------
/**
 * Prints help for commandline options.
 */

  public void printHelp()
  {
    System.out.println("GPSMap "+GPSMAP_VERSION);
    System.out.println("Written by Christof Dallermassl in 2002");
    System.out.println("email: cdaller@iicm.edu");
    System.out.println("This Programm is licenced under the GPL");
    System.out.println("Comments are welcomed!");
    System.out.println("");
    System.out.println("Usage:");
    System.out.println("GPSMap [options]");
    System.out.println();
    System.out.println("Available Options:");
    System.out.println("--help, -h: this screen");
    System.out.println("--device, -d <device>: serial device for NMEA device (e.g. /dev/ttyS0 or COM1)");
    System.out.println("--speed, -s <speed>: speed for serial device (NMEA uses 4800 (default))");
    System.out.println("--gpsfile, -f <filename>: the file is used as input for NMEA data (if set to 'none', no file is used)");
    System.out.println("--gpsdhost, -g <hostname> : NMEA data is fetched from gpsd (gps data server)");
    System.out.println("--gpsdport, -p <port>: the port of the gpsd server");
    System.out.println("--nmealogfile, -l <filename>: NMEA data is logged to this file (if set to 'none', no file is used)");
    System.out.println("--file: NMEA data is read from a file (given on commandline or set in properties)");
    System.out.println("--serial: NMEA data is read from serial device (given on commandline or set in properties)");
    System.out.println("--gpsd: NMEA data is read from gpsd server (given on commandline or set in properties)");
    System.out.println();
    System.out.println("Giving a filename switches automatically to NMEA input from file.");
    System.out.println("Similar for giving a gpsd host/port or a serial device");
    System.out.println();
    System.out.println("If you want to switch between previously setup serial/file/gpsd settings,");
    System.out.println("you can do this by the use of the --file, --serial, --gpsd switches.");
    System.out.println("");
    System.out.println("The given commandline options are overwriting the settings in the property file (~/.gpsmap/GPSMap.properties) and are stored there at termination of the program.");
    System.out.println();
    System.out.println("Have fun!");
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
    if(Debug.DEBUG)
      Debug.println("GPSMap_propertychange","property change event: "
		    +event.getPropertyName()+"="+event.getNewValue()
		    +" from "+event.getSource());
    
    if (event.getSource() == resources_)
    {
      updateResources(event.getPropertyName());
//       invalidate();
//       validate();

//        if(main_frame_ != null)
//  	main_frame_.repaint();
      return;
    }

    if(event.getSource() instanceof GPSDataProcessor)
    {
//      GPSDataProcessor source = (GPSDataProcessor)event.getSource ();
      PropertyChangeEvent new_event = null;

//       System.out.println("GPSMap: property location detected!");
//       System.out.println(event.getPropertyName()+".equals("+GPSDataProcessor.LOCATION
// 			 +")="+event.getPropertyName().equals(GPSDataProcessor.LOCATION));
      
      if(event.getPropertyName().equals(GPSDataProcessor.LOCATION))
      {
        GPSPosition pos = (GPSPosition)event.getNewValue();
            // conversion from GPSPosition to LatLonPoint:
        LatLonPoint new_gps_pos = null;

        if(pos != null)
        {
          new_gps_pos = new LatLonPoint(pos.getLatitude(),pos.getLongitude());
          current_gps_position_ = new LatLonPoint(new_gps_pos);
        }

        pos = (GPSPosition)event.getOldValue();

            // conversion from GPSPosition to LatLonPoint:
        LatLonPoint old_gps_pos = null;
        if(pos != null)
          old_gps_pos = new LatLonPoint(pos.getLatitude(),pos.getLongitude());
        new_event = new PropertyChangeEvent(this,PROPERTY_KEY_GPS_LOCATION,
                                            old_gps_pos,new_gps_pos);

        if(Debug.DEBUG)
          Debug.println("GPSMap_GPSdata","gps event: position old="
                        +old_gps_pos+", new="+new_gps_pos);
      }
      else
      {  // all other events are forwarded (they use the same property names!!!)
        new_event = new PropertyChangeEvent(this,event.getPropertyName(),
                                            event.getOldValue(),event.getNewValue());
        Debug.println("GPSMap_GPSdata","gps event: '"+event.getPropertyName()+"': old="
                      +event.getOldValue()+" new="+event.getNewValue());
      }
          // tell my listeners about this event:
      if(new_event != null)
      {
// 	  System.out.println("GPSMap: firing event: "+new_event.getSource()+" "
// 			     +new_event.getPropertyName()+": old="+new_event.getOldValue()
// 				 +" new="+new_event.getNewValue());
// 	  System.out.println("old.equals(new)="
// 			     +new_event.getNewValue().equals(new_event.getOldValue()));
        property_change_support_.firePropertyChange(new_event);
      }
      return;
    }

    if(event.getPropertyName().equals(PROPERTY_KEY_ROUTE_DESTINATION))
    {
      LatLonPoint destination = (LatLonPoint)event.getNewValue();
      if(destination != null)
      {
        destination_position_ = new LatLonPoint(destination);

        if(Debug.DEBUG)
          Debug.println("GPSMap_Destination","new destination: "+event.getNewValue());
        
        if(simulation_mode_)
        {
          startSimulation(current_gps_position_,destination_position_);
        }
      }
    }
  }
  

//----------------------------------------------------------------------
/**
 * Sets the simulation parameters (start end destination point) and
 * starts the simulation. It converts from LatLonPoints to
 * GPSPositions.  It does nothing, if the gps_data_processor_ is
 * not a Simulation processor.
 *
 * @param start the start point
 * @param destination the destination point
 */

  protected void startSimulation(LatLonPoint start, LatLonPoint destination)
  {
    if(!(gps_data_processor_ instanceof GPSSimulationDataProcessor))
      return;

    GPSSimulationDataProcessor gps_processor = 
          (GPSSimulationDataProcessor)gps_data_processor_;

    GPSPosition gps_position = new GPSPosition();
    gps_position.setLatitude(start.getLatitude());
    gps_position.setLongitude(start.getLongitude());
    gps_processor.setStartPosition(gps_position);

    gps_position = new GPSPosition();
    gps_position.setLatitude(destination.getLatitude());
    gps_position.setLongitude(destination.getLongitude());
    gps_processor.setDestinationPosition(gps_position);

    gps_processor.open();
  }

//----------------------------------------------------------------------
/**
 * Initializes the plugin architecture (create class loader, add
 * plugin directories, create service provider, etc.). The plugins
 * itself are not created here!
 */

  protected void initializePluginArchitecture()
  {
    
    if(resources_.getBoolean(KEY_DEVELOPMENT_PLUGINS_CLASSLOADER_USE_DEFAULT_CLASSLOADER))
          // use default classloader as well, so plugins are searched
          // in classpath (useful for development of plugins)
      service_discovery_ = new ServiceDiscovery(true); 
    else
      // do not use the system loader (is used automatically by the RepositoryClassLoader)
      service_discovery_ = new ServiceDiscovery(false);
    repository_class_loader_ = new RepositoryClassLoader();
    
        // find paths that should be searched for plugin/service jars:
    String[] plugin_repositories = resources_.getStringArray(KEY_FILE_PLUGIN_DIRS);
    String repository;
    TreeSet repository_set = new TreeSet();
    
        // add default system plugin directory ("plugins" in the same directory as my jar)
        // or parallel to "classes" directory:

        // find location of my jar:
    String class_name_path = this.getClass().getName().replace('.','/') + ".class"; // TODO check under windows!
    URL url = GPSMap.class.getClassLoader().getResource(class_name_path);
        // the resulting url looks like
        // "jar:file:/home/cdaller/gpstool/gpstool.jar!/org/dinopolis/gpstool/GPSMap.class"
    if(url.getProtocol().startsWith("jar"))
    {
      String url_path = url.getPath();
      int exclamation_pos = url_path.indexOf('!');
      int dir_name_end = url_path.lastIndexOf('/',exclamation_pos);
      int protocol_end = url_path.indexOf(':');
      String dir_name = url_path.substring(protocol_end+1,dir_name_end);
      if(Debug.DEBUG)
        Debug.println("plugin","URL of my class :"+url+", dir of jar is "+dir_name);
      repository = FileUtil.getAbsolutePath(dir_name,STD_PLUGINS_DIR_NAME);
    }
    else
    {
      String url_path = url.getPath();
      String dir_name = url_path.substring(0,url_path.length() - class_name_path.length());
      dir_name = new File(dir_name).getParent(); // use parent of "classes" directory
      repository = FileUtil.getAbsolutePath(dir_name,STD_PLUGINS_DIR_NAME);
    }
          // adding default plugin dir:
    repository_class_loader_.addRepository(repository);
    if(Debug.DEBUG)
      Debug.println("plugin","Adding default plugin dir: "+repository);
    repository_set.add(repository);
    

        // add plugin paths from resources:
    for(int count = 0; count < plugin_repositories.length; count++)
    {
      repository = plugin_repositories[count];
      repository = FileUtil.getAbsolutePath(resources_.getString(KEY_FILE_MAINDIR),repository);
      if(repository_set.contains(repository))
      {
        if(Debug.DEBUG)
          Debug.println("plugin","Ignoring duplicate plugin/service repository: "+repository);
      }
      else
      {
        if(Debug.DEBUG)
          Debug.println("plugin","Adding repository for plugin/service jars: "+repository);
        repository_set.add(repository);
        repository_class_loader_.addRepository(repository);
      }
    }
    repository_set = null;
    service_discovery_.addClassLoader(repository_class_loader_);
  }


//----------------------------------------------------------------------
/**
 * Instantiates and Initializes the plugins that are found.
 */

  protected void initializePlugins()
  {
        // TODO FIXXME check for duplicate plugins (old and new version)
        // and remove the old one

    Vector mouse_modes = new Vector();
    
        // GuiPlugins
    Object[] plugins = service_discovery_.getServices(org.dinopolis.gpstool.plugin.GuiPlugin.class);
    GuiPlugin gui_plugin;
    JMenuItem plugin_menu;
    for(int plugins_index = 0; plugins_index < plugins.length; plugins_index++)
    {
      gui_plugin = (GuiPlugin)plugins[plugins_index];
      System.out.println("Adding Gui Plugin: " + gui_plugin.getPluginName());
      gui_plugin.initializePlugin(hook_manager_);
      addMouseModes(gui_plugin.getMouseModes());
          // add sub menus of plugin:
      plugin_menu = gui_plugin.getSubMenu();
      if(plugin_menu != null)
        addToPluginsMenu(plugin_menu);

          // add main menu of plugin:
      plugin_menu = gui_plugin.getMainMenu();
      menu_bar_.add(plugin_menu);
    }

        // LayerPlugins
    plugins = service_discovery_.getServices(org.dinopolis.gpstool.plugin.LayerPlugin.class);
    LayerPlugin layer_plugin;
    for(int plugins_index = 0; plugins_index < plugins.length; plugins_index++)
    {
      layer_plugin = (LayerPlugin)plugins[plugins_index];
      System.out.println("Adding Layer Plugin: " + layer_plugin.getPluginName());
      layer_plugin.initializePlugin(hook_manager_);
      addMouseModes(layer_plugin.getMouseModes());
      map_bean_.add(layer_plugin);
          // add sub menus of plugin:
      plugin_menu = layer_plugin.getSubMenu();
      if(plugin_menu != null)
        addToPluginsMenu(plugin_menu);

          // add main menu of plugin:
      plugin_menu = layer_plugin.getMainMenu();
      menu_bar_.add(plugin_menu);
    }

        // MouseMode Plugins:
    plugins = service_discovery_.getServices(org.dinopolis.gpstool.plugin.MouseModePlugin.class);
        // initialize all mouse modes and add them as mouselisteners:
    MouseModePlugin mouse_mode_plugin;
    Layer layer;
    for(int plugins_index = 0; plugins_index < plugins.length; plugins_index++)
    {
      mouse_mode_plugin = (MouseModePlugin)plugins[plugins_index];
      System.out.println("Adding Mouse Mode Plugin: " + mouse_mode_plugin.getMouseModeName());
      mouse_mode_plugin.initializePlugin(hook_manager_);
      layer = mouse_mode_plugin.getLayer();
      if(layer != null)
        map_bean_.add(layer);
      addMouseMode(mouse_mode_plugin);
    }
  }

//----------------------------------------------------------------------
/**
 * Adds the given mouse mode to the mouse mode manager and
 * registers it as a mouse listener and as as a mouse motion
 * listener.
 *
 * @param mouse_mode the mouse mode
 */
  protected void addMouseMode(MouseMode mouse_mode)
  {
    mouse_mode_manager_.addMouseMode(mouse_mode);
    mouse_mode.setActive(false);
    map_bean_.addMouseListener(mouse_mode);
    map_bean_.addMouseMotionListener(mouse_mode);
  }


//----------------------------------------------------------------------
/**
 * Adds the given mouse modes to the mouse mode manager and
 * registers them as a mouse listener and as as a mouse motion
 * listener.
 *
 * @param mouse_mode the mouse mode
 */
  protected void addMouseModes(MouseMode[] mouse_modes)
  {
    for(int index = 0; index < mouse_modes.length; index++)
      addMouseMode(mouse_modes[index]);
  }


  
//----------------------------------------------------------------------
/**
 * Updates the resource for the given key, or all resources if key
 * is 'null'.
 */

  protected void updateResources(String key)
  {
    try
    {
      if((key == null) || (key.startsWith(KEY_HTTP_PROXY_PREFIX)))
        updateProxySettings();

      if((key == null) || (key.startsWith(KEY_GPS_DEVICE_PREFIX)))
        updateGPSConnection();

      if((key == null) || key.equals(KEY_NUMBER_FORMAT_DISTANCE))
      {
        distance_formatter_ = new DecimalFormat(resources_.getString(KEY_NUMBER_FORMAT_DISTANCE));
      }

      if((key == null) || key.equals(KEY_UNIT_DISTANCE))
      {
        distance_unit_ = resources_.getString(KEY_UNIT_DISTANCE);
      }

    }
    catch (MissingResourceException mre) 
    {
      System.err.println(mre.getMessage());
      System.exit(1);
    }
  }

//----------------------------------------------------------------------
/**
 * Updates the proxy settings.
 */

  protected void updateProxySettings()
  {
    boolean use_proxy = resources_.getBoolean(KEY_HTTP_PROXY_USE, false);
    String proxy_host = resources_.getString(KEY_HTTP_PROXY_HOST, null);
    int proxy_port = resources_.getInt(KEY_HTTP_PROXY_PORT, -1);

    Properties props = System.getProperties();
    if (use_proxy)
    {
      props.put("http.proxyHost", proxy_host);
      props.put("http.proxyPort", Integer.toString(proxy_port));
    }
    else
    {
      props.remove("http.proxyHost");
      props.remove("http.proxyPort");
    }
  }

//----------------------------------------------------------------------
/**
 * Updates the locations according to the values set within the
 * resource file. 
 */

  protected void updateWindowLocation()
  {
    try
    {
      main_frame_.setLocation(resources_.getInt(KEY_WINDOW_LOCATION_X),
                              resources_.getInt(KEY_WINDOW_LOCATION_Y));
      main_frame_.setSize(resources_.getInt(KEY_WINDOW_DIMENSION_WIDTH),
                          resources_.getInt(KEY_WINDOW_DIMENSION_HEIGHT));
    }
    catch (MissingResourceException exc)
    {
    }
    catch (NumberFormatException exc)
    {
    }
  }


//----------------------------------------------------------------------
/**
 * Updates the locations according to the values set within the
 * resource file. 
 */

  public void updateGPSConnection()
  {
    try
    {
      String gps_mode = resources_.getString(KEY_GPS_DEVICE_MODE);
//      System.out.println("GPSMode: "+gps_mode);
      
      if(!gps_mode.equals(VALUE_KEY_DEVICE_MODE_NONE))
      {
        try
        {
          if(gps_data_processor_ != null)
          {
            gps_data_processor_.close();
          }
        }
        catch(Exception e)
        {
          System.err.println("ERROR: Could not close the old gps data processor!");
          e.printStackTrace();
        }
      }
      
      if(simulation_mode_)
      {
        if(Debug.DEBUG)
          Debug.println("GPSMap_GPSConnection","create new simulation processor");
        gps_data_processor_ = new GPSSimulationDataProcessor();
      }
      else
        if(gps_mode.equals(VALUE_KEY_DEVICE_MODE_FILE))
        {
          gps_data_processor_ = new GPSNmeaDataProcessor();
          GPSDevice gps_device;
          Hashtable environment = new Hashtable();
          String filename = resources_.getString(KEY_GPS_DEVICE_DATA_FILENAME);
          if((filename == null) || (filename.length() <= 0))
            return;

          environment.put(GPSFileDevice.PATH_NAME_KEY,filename);
          gps_device = new GPSFileDevice();
          gps_device.init(environment);
          int delay = resources_.getInt(KEY_GPS_DEVICE_NMEA_DELAY);
          ((GPSNmeaDataProcessor)gps_data_processor_).setDelayTime(delay);
          boolean ignore_checksum = resources_.getBoolean(KEY_GPS_DEVICE_NMEA_IGNORE_CHECKSUM);
          ((GPSNmeaDataProcessor)gps_data_processor_).setIgnoreInvalidChecksum(ignore_checksum);
          gps_data_processor_.setGPSDevice(gps_device);
          if(Debug.DEBUG)
            Debug.println("GPSMap_GPSConnection","connecting to gpsfile: "
                          +filename);
          gps_data_processor_.open();
        }
        else
          if(gps_mode.equals(VALUE_KEY_DEVICE_MODE_SERIAL))
          {
            String serial_port_name = resources_.getString(KEY_GPS_DEVICE_SERIAL_PORT);
            int serial_port_speed = resources_.getInt(KEY_GPS_DEVICE_SERIAL_SPEED);
    	  
            gps_data_processor_ = new GPSNmeaDataProcessor();
            GPSDevice gps_device;
            Hashtable environment = new Hashtable();
            environment.put(GPSSerialDevice.PORT_NAME_KEY,serial_port_name);
            environment.put(GPSSerialDevice.PORT_SPEED_KEY,new Integer(serial_port_speed));
            gps_device = new GPSSerialDevice();


              // NMEA LOGFILE:
            String nmea_logfile = resources_.getString(KEY_GPS_DEVICE_NMEALOGFILE);
            if((nmea_logfile != null) && (nmea_logfile.length() > 0))
              gps_data_processor_.addGPSRawDataListener(new GPSRawDataFileLogger(nmea_logfile));
            
            gps_device.init(environment);
            gps_data_processor_.setGPSDevice(gps_device);
            if(Debug.DEBUG)
              Debug.println("GPSMap_GPSConnection","connecting to gpsdevice on port "
                            +serial_port_name);
            gps_data_processor_.open();

          }
          else
            if(gps_mode.equals(VALUE_KEY_DEVICE_MODE_GPSD))
            {
              String gpsd_hostname = resources_.getString(KEY_GPS_DEVICE_GPSD_HOST,"localhost");
              int gpsd_port = resources_.getInt(KEY_GPS_DEVICE_GPSD_PORT,2947);
              
              gps_data_processor_ = new GPSNmeaDataProcessor();
              GPSDevice gps_device;
              Hashtable environment = new Hashtable();
              environment.put(GPSNetworkGpsdDevice.GPSD_HOST_KEY,gpsd_hostname);
              environment.put(GPSNetworkGpsdDevice.GPSD_PORT_KEY,new Integer(gpsd_port));
              gps_device = new GPSNetworkGpsdDevice();
              
              // NMEA LOGFILE:
              String nmea_logfile = resources_.getString(KEY_GPS_DEVICE_NMEALOGFILE);
              if((nmea_logfile != null) && (nmea_logfile.length() > 0))
                gps_data_processor_.addGPSRawDataListener(new GPSRawDataFileLogger(nmea_logfile));
            
              gps_device.init(environment);
              gps_data_processor_.setGPSDevice(gps_device);
              if(Debug.DEBUG)
                Debug.println("GPSMap_GPSConnection","connecting to gpsdevice on host "
                              +gpsd_hostname+":"+gpsd_port);
              gps_data_processor_.open();
              
            }
            else
            if(gps_mode.equals(VALUE_KEY_DEVICE_MODE_NONE))
            {
              gps_data_processor_ = null;
            }
      
          // set me as gps data listener into the data processor:
      if(gps_data_processor_ != null)
      {
        gps_data_processor_.addGPSDataChangeListener(this);
        String alarm_file = resources_.getString(KEY_ALARM_FILE,"");
        if((alarm_file != null) && (alarm_file.length() > 0))
        {
              // add alarm settings:
          AlarmConditionManager alarm_manager = new AlarmConditionManager();
          try
          {
            alarm_manager.addAlarms(alarm_file,gps_data_processor_);
          }
          catch(IOException ioe)
          {
            ioe.printStackTrace();
          }
        }
      }
    }
    catch(GPSException e)
    {
      e.printStackTrace();
      System.err.println("An error occured (e.g. could not connect to gps device)\n"
                         +"switching to simulation mode...");
          // TODO FIXXME: switch to simulation mode using the SimulationModeAction
    }
    catch (MissingResourceException exc)
    {
      exc.printStackTrace();
    }
  }
  
//----------------------------------------------------------------------
/**
 * Checks for the existence of any lock files (for serial devices,
 * etc.). If there are any found, it opens a dialog box and asks the
 * user what to do.
 *
 */
  public void checkLockFiles(String[] lockfilenames)
  {
    for(int count = 0; count < lockfilenames.length; count++)
    {
      File lockfile = new File(lockfilenames[count]);
      if(lockfile.exists())
      {
            // show option dialog to delete the log file or not!
        Object[] options = {resources_.getString(KEY_LOCALIZE_DELETELOCK_BUTTON),
                            resources_.getString(KEY_LOCALIZE_IGNORE_BUTTON)};

        int option = JOptionPane.showOptionDialog(main_frame_,
                                                  resources_.getString(KEY_LOCALIZE_MESSAGE_LOCK_EXISTS_TEXT)
                                                  +" "+lockfilenames[count],
                                                  resources_.getString(KEY_LOCALIZE_MESSAGE_LOCK_EXISTS_TITLE),
                                                  JOptionPane.OK_CANCEL_OPTION,
                                                  JOptionPane.QUESTION_MESSAGE,
                                                  null,     //don't use a custom Icon
                                                  options,  //the titles of buttons
                                                  options[0]); //default button title
        if (option == JOptionPane.OK_OPTION) // delete lock file
        {
          lockfile.delete();
        }
      }
    }
  }

//----------------------------------------------------------------------
/**
 * Calculates the distance in km of two given points.
 *
 * @param p1 point1
 * @param p2 point2
 * @return the distance in km.
 */
  public static float calculateDistance(LatLonPoint p1, LatLonPoint p2)
  {
    float distance;

    distance = (float)GeoMath.distance(p1.getLatitude(),
                                       p1.getLongitude(),
                                       p2.getLatitude(),
                                       p2.getLongitude());
    return(distance / 1000.0f);
  }

//----------------------------------------------------------------------
/**
 * Returns the distance or speed converted from km to whatever unit that is
 * currently chosen (miles, nautical miles).
 *
 * @param distance_or_speed in km
 * @return the converted distance or speed.
 */
  public static float getDistanceOrSpeed(float distance_or_speed)
  {
    if(distance_unit_.equals("metric"))
      return(distance_or_speed);
    if(distance_unit_.equals("miles"))
      return(distance_or_speed * KM2MILES);
    if(distance_unit_.equals("nautic"))
      return(distance_or_speed * KM2NAUTIC);
    
        // should never happen:
    throw new IllegalArgumentException("Unknown distance unit in resources: "+distance_unit_);
  }

  
//----------------------------------------------------------------------
/**
 * Returns a String that contains the distance formatted corresponding
 * to the settings in the resource file. This includes the formatting
 * of the number itself, the unit (metric, nautic or miles) and a
 * suffix that correponds to the unit (km, mi, nmi).
 *
 * @param distance
 * @return a String containing the formatted distance including the unit.
 */
  public static String getDistanceOrSpeedString(float distance)
  {
    if(distance_unit_.equals("metric"))
      return(distance_formatter_.format(distance));
    if(distance_unit_.equals("miles"))
      return(distance_formatter_.format(distance * KM2MILES));
    if(distance_unit_.equals("nautic"))
      return(distance_formatter_.format(distance * KM2NAUTIC));
    
        // should never happen:
    throw new IllegalArgumentException("Unknown distance unit in resources: "+distance_unit_);
  }

//----------------------------------------------------------------------
/**
 * Returns the factor between kilometers and the chosen unit for
 * distances or speed (miles, nautic).
 *
 * @param distance
 * @return a String containing the formatted distance including the unit.
 */
  public static float getDistanceOrSpeedFactor()
  {
    if(distance_unit_.equals("metric"))
      return(1.0f);
    if(distance_unit_.equals("miles"))
      return(KM2MILES);
    if(distance_unit_.equals("nautic"))
      return(KM2NAUTIC);
    
        // should never happen:
    throw new IllegalArgumentException("Unknown distance unit in resources: "+distance_unit_);
  }

//----------------------------------------------------------------------
/**
 * Returns a String that contains the unit of distances corresponding
 * to the settings in the resource file. 
 *
 * @return a String that contains the unit of distances corresponding
 * to the settings in the resource file.
 */

  public static String getDistanceUnit()
  {
    if(distance_unit_.equals("metric"))
      return("km");
    if(distance_unit_.equals("miles"))
      return("mi");
    if(distance_unit_.equals("nautic"))
      return("nmi");

        // should never happen:
    throw new IllegalArgumentException("Unknown distance unit in resources: "+distance_unit_);
  }
  
//----------------------------------------------------------------------
/**
 * Returns a String that contains the unit for speed corresponding
 * to the settings in the resource file. 
 *
 * @return a String that contains the unit for speed corresponding
 * to the settings in the resource file.
 */

  public static String getSpeedUnit()
  {
    if(distance_unit_.equals("metric"))
      return("km/h");
    if(distance_unit_.equals("miles"))
      return("mph");
    if(distance_unit_.equals("nautic"))
      return("knots");

        // should never happen:
    throw new IllegalArgumentException("Unknown distance unit in resources: "+distance_unit_);
  }


//----------------------------------------------------------------------
/**
 * Returns the altitude converted from meter to whatever unit that is
 * currently chosen (feet).
 *
 * @param altitude in meter
 * @return the converted altitude
 */
  public static float getAltitude(float altitude)
  {
    if(distance_unit_.equals("metric"))
      return(altitude);
    if(distance_unit_.equals("miles") || distance_unit_.equals("nautic"))
      return(altitude * METER2FEET);

        // should never happen:
    throw new IllegalArgumentException("Unknown distance unit in resources: "+distance_unit_);
  }

//----------------------------------------------------------------------
/**
 * Returns a String that contains the unit for altitude corresponding
 * to the settings in the resource file. 
 *
 * @return a String that contains the unit for altitude corresponding
 * to the settings in the resource file.
 */

  public static String getAltitudeUnit()
  {
    if(distance_unit_.equals("metric"))
      return("m");
    if(distance_unit_.equals("miles"))
      return("ft");
    if(distance_unit_.equals("nautic"))
      return("ft");

        // should never happen:
    throw new IllegalArgumentException("Unknown distance unit in resources: "+distance_unit_);
  }


  
  
//----------------------------------------------------------------------
// The GPS Property Change Methods
//----------------------------------------------------------------------

//----------------------------------------------------------------------
/**
 * Adds a listener for GPS data change events.
 *
 * @param listener the listener to be added.
 * @param key the key of the GPSdata to be observed.
 * @exception IllegalArgumentException if <code>key</code> or
 * <code>listener</code> is <code>null</code>. 
 */
  public void addPropertyChangeListener(String key, PropertyChangeListener listener)
    throws IllegalArgumentException
  {
    if (key == null)
      throw new IllegalArgumentException("The key must not be <null>.");
    if (listener == null)
      throw new IllegalArgumentException("The listener must not be <null>.");

    property_change_support_.addPropertyChangeListener(key,listener);
  }

  
//----------------------------------------------------------------------
/**
 * Adds a listener for GPS data change events.
 *
 * @param listener the listener to be added.
 * @exception IllegalArgumentException if <code>listener</code> is
 * <code>null</code>.  
 */
  public void addPropertyChangeListener(PropertyChangeListener listener)
    throws IllegalArgumentException
  {
    if (listener == null)
      throw new IllegalArgumentException("The listener must not be <null>.");

    if (property_change_support_ == null)
      property_change_support_ = new PropertyChangeSupport(this);
    property_change_support_.addPropertyChangeListener(listener);
  }
  
//----------------------------------------------------------------------
/**
 * Removes a listener for GPS data change events.
 *
 * @param listener the listener to be removed.
 * @param key the key of the GPSdata to be observed.
 * @exception IllegalArgumentException if <code>key<code> or
 * <code>listener</code> is <code>null</code>.  
 */
  public void removePropertyChangeListener(String key, PropertyChangeListener listener)
    throws IllegalArgumentException
  {
    if (key == null)
      throw new IllegalArgumentException("The key must not be <null>.");
    if (listener == null)
      throw new IllegalArgumentException("The listener must not be <null>.");

    if (property_change_support_ != null)
      property_change_support_.removePropertyChangeListener(key,listener);
  }


//----------------------------------------------------------------------
/**
 * Removes a listener for GPS data change events.
 *
 * @param listener the listener to be removed.
 * @exception IllegalArgumentException if <code>listener</code> is
 * <code>null</code>.  
 */
  public void removePropertyChangeListener(PropertyChangeListener listener)
    throws IllegalArgumentException
  {
    if (listener == null)
      throw new IllegalArgumentException("The listener must not be <null>.");

    if (property_change_support_ != null)
      property_change_support_.removePropertyChangeListener(listener);
  }

//----------------------------------------------------------------------
/**
 * Returns the current gps position
 *
 * @return the current gps position
 */

  public LatLonPoint getCurrentGPSPosition()
  {
    return(current_gps_position_);
  }

  
//----------------------------------------------------------------------
// The MapNavigation Hooks
//----------------------------------------------------------------------

//----------------------------------------------------------------------
/**
 * Sets the new center of the map.
 *
 * @param latitude The latitude of the new center of the map
 * @param longitude The longitude of the new center of the map
 */

  public void setMapCenter(double latitude, double longitude)
  {
    map_bean_.setCenter((float)latitude,(float)longitude);
    resources_.setDouble(KEY_CURRENT_MAP_POSITION_LATITUDE,latitude);
    resources_.setDouble(KEY_CURRENT_MAP_POSITION_LONGITUDE,longitude);
  }

  
//----------------------------------------------------------------------
/**
 * Rescales the map by a given factor. A factor of 1.0 leaves the map
 * unchanged. A factor greater 1.0 zooms in, a factor less than 1.0
 * zooms out.
 *
 * @param scale_factor the scale factor.
 */

  public void reScale(float scale_factor)
  {
    float new_scale = scale_factor * map_bean_.getScale();
    map_bean_.setScale(new_scale);
    resources_.setDouble(KEY_MAP_SCALE,new_scale);
  }
  
//----------------------------------------------------------------------
/**
 * Rescales the map by to a given scale.
 *
 * @param scale the scale
 */

  public void setScale(float scale)
  {
    map_bean_.setScale(scale);
    resources_.setDouble(KEY_MAP_SCALE,scale);
  }

//----------------------------------------------------------------------
/**
 * Sets a new center for the map. A negative factor moves the center
 * up or left, a postive factor down or right. A factor of 1.0
 * translates the center a complete height/width down or right.
 *
 * @param factor_x the horizontal factor to recenter the map.
 * @param factor_y the vertical factor to recenter the map.
 */

  public void translateMapCenterRelative(float factor_x, float factor_y)
  {
    Projection proj = map_bean_.getProjection();
    Point center_xy = new Point((int)(proj.getWidth() * (0.5f + factor_x)),
                                (int)(proj.getHeight() * (0.5f + factor_y)));
//    System.out.println("new Center:"+center_xy);
    LatLonPoint new_center = proj.inverse(center_xy);
    setMapCenter((double)new_center.getLatitude(),(double)new_center.getLongitude());
  }

//----------------------------------------------------------------------
/**
 * Returns the currently used projection of the map. This projection
 * may be used to calculate the latititude/longitude from screen
 * coordinates and vice versa.
 *
 * @return the projection currently used.
 * @see com.bbn.openmap.proj.Projection
 */
  public Projection getMapProjection()
  {
    return(map_bean_.getProjection());
  }

//----------------------------------------------------------------------
// The MapManager Hooks
//----------------------------------------------------------------------

//----------------------------------------------------------------------
/**
 * Adds new maps to the system. This method is responsible to make
 * this information permantent and to add this map to the running
 * system. If the filename in map_info is already used, the map is not
 * added.
 *
 * @param map_infos the new maps
 */

//   public void addNewMaps(MapInfo[] map_infos)
//   {
//   }

//----------------------------------------------------------------------
/**
 * Adds a new map to the system. This method is responsible to make
 * this information permantent and to add this map to the running
 * system. If the filename in map_info is already used, the map is not
 * added.
 *
 * @param map_info the new map
 */

  public synchronized void addNewMap(MapInfo map_info)
  {
    String new_map_filename = map_info.getFilename(); // full filename
    if(used_map_filenames_.contains(new_map_filename))
    {
      System.err.println("Filename '"+new_map_filename+"' already used, map ignored!");
      return;
    }
    
    String main_dir = resources_.getString(KEY_FILE_MAINDIR);
    String description_filename = resources_.getString(KEY_FILE_MAP_DESCRIPTION_FILE);
    
    String maps_filename = main_dir + File.separator + description_filename;

    BufferedWriter map_writer;

    if(new_map_filename.startsWith(main_dir))
      new_map_filename = new_map_filename.substring(main_dir.length()+1);
    try
    {
      map_writer= new BufferedWriter(new FileWriter(maps_filename,true)); // append mode

      StringBuffer new_line = new StringBuffer();

          // Locale.US is used to get the decimal point as a point!
      NumberFormat latlon_formatter = NumberFormat.getInstance(Locale.US);
      if(latlon_formatter instanceof DecimalFormat)
        ((DecimalFormat)latlon_formatter).applyPattern("#.#####");
      
      NumberFormat scale_formatter = NumberFormat.getInstance(Locale.US);
      if(scale_formatter instanceof DecimalFormat)
        ((DecimalFormat)scale_formatter).applyPattern("#.#");
      
      new_line.append(new_map_filename);
      new_line.append(" ");
      new_line.append(latlon_formatter.format(map_info.getLatitude()));
      new_line.append(" ");
      new_line.append(latlon_formatter.format(map_info.getLongitude()));
      new_line.append(" ");
      new_line.append(scale_formatter.format(map_info.getScale()));
      new_line.append(" ");
      new_line.append(map_info.getWidth()); 
      new_line.append(" ");
      new_line.append(map_info.getHeight()); 
      map_writer.write(new_line.toString());
      map_writer.newLine();
    
      map_writer.close();
    }
    catch(IOException ioe)
    {
      System.err.println("ERROR: On writing to map description file '"+maps_filename+"': "+ioe.getMessage());
      return;
    }
    
        // add the map to the running map layer:
    map_layer_.addMap(map_info);
  }

//----------------------------------------------------------------------
/**
 * Returns an image of the current map bean.
 *
 * @return an image of the current map bean. 
 */
  public BufferedImage getMapScreenShot()
  {
    BufferedImage image = (BufferedImage)map_bean_.createImage(map_bean_.getWidth(),map_bean_.getHeight());
    Graphics2D g2 = image.createGraphics();
    map_bean_.paint(g2);
    return (image);
  }
  
//----------------------------------------------------------------------
// The Status Hooks
//----------------------------------------------------------------------
//----------------------------------------------------------------------
/**
 * Sets the status message.
 *
 * @param message the new status message.
 */

  public void setStatusInfo(String message)
  {
    status_bar_.setStatus(message);
  }
  
//----------------------------------------------------------------------
/**
 * Main Method
 */
  public static void main(String[] args)
  {
        //com.bbn.openmap.util.Debug.init(); // enable openmap debug info
    new GPSMap(args);
  }

      //----------------------------------------------------------------------
      // The Key Listener Subclass
      //----------------------------------------------------------------------

//   class GPSMapKeyListener extends KeyAdapter
//   {
//     public GPSMapKeyListener()
//     {
//     }

//     public void keyPressed(KeyEvent event)
//     {
//       System.out.println("Key pressed: "+KeyEvent.getKeyText(event.getKeyCode()));
//     }
    
//   }


  class Tachometer implements PropertyChangeListener
  {
    protected float average_speed_;
    protected float current_speed_;
    protected float current_heading_;
    protected float distance_;
    protected float total_distance_;
    protected float test_total_dist_;
    protected float start_time_;
    protected LatLonPoint old_point_;
    protected long old_time_;
    protected long test_old_time_;
    
        /** every TIME_DIFF milliseonds a new value is calculated */
    protected int TIME_DIFF = 3000;  

    /** if true, use the speed from the gps receiver, if false, calculate speed from
	distance and time. */
    protected boolean use_gps_speed_ = false;
    
    /** if true, use the heading from the gps receiver, if false, calculate heading from
	location. */
    protected boolean use_gps_heading_ = false;
    
    public Tachometer()
    {
      reset();
    }

    public void reset()
    {
      start_time_ = System.currentTimeMillis();
      average_speed_ = 0.0f;
      current_speed_ = 0.0f;
      distance_ = 0.0f;
      total_distance_ = 0.0f;
      test_total_dist_ = 0.0f;
      test_old_time_ = System.currentTimeMillis();
      old_time_ = System.currentTimeMillis();
      old_point_ = null;
      property_change_support_.firePropertyChange(PROPERTY_KEY_TOTAL_DISTANCE,
                                                  null,
                                                  new Float(total_distance_));
      if(!use_gps_speed_)
        property_change_support_.firePropertyChange(PROPERTY_KEY_CURRENT_SPEED,
                                                    null,
                                                    new Float(current_speed_));
    }

  //----------------------------------------------------------------------
  /**
   * Callback method for property change events
   *
   * @param event the property change event.
   */
  
    public void propertyChange(PropertyChangeEvent event)
    {
      if(event.getPropertyName().equals(GPSMap.PROPERTY_KEY_GPS_HEADING))
      {
        use_gps_heading_ = true; // gps receiver sends heading, so use it from now on!
//	System.out.println("GPS device provides heading, using it.");
        property_change_support_.firePropertyChange(PROPERTY_KEY_CURRENT_HEADING,
                                                    event.getOldValue(),
                                                    event.getNewValue());
        return;
      }
      if(event.getPropertyName().equals(GPSMap.PROPERTY_KEY_GPS_SPEED))
      {
	// alternative method to calculate distance, little less
	// accurate than using the location!:
//          long new_time = System.currentTimeMillis();
//          long time_diff = new_time - test_old_time_;
//          test_old_time_ = new_time;

//          float speed = ((Float)event.getNewValue()).floatValue();
//          float dist = speed * (float)time_diff / 3600000f;
//          test_total_dist_ = test_total_dist_ + dist;
//          if(Debug.DEBUG)
//            Debug.println("GPSMap_Tacho",
//                          "Testdist: dist:"+dist+" total:"+test_total_dist_+"km (timediff="+time_diff+")");

        use_gps_speed_ = true; // gps receiver sends speed, so use it from now on!
//	System.out.println("GPS device provides speed, using it.");
        property_change_support_.firePropertyChange(PROPERTY_KEY_CURRENT_SPEED,
                                                    event.getOldValue(),
                                                    event.getNewValue());
        return;
      }
      if(event.getPropertyName().equals(GPSMap.PROPERTY_KEY_GPS_LOCATION))
      {
        long new_time = System.currentTimeMillis();
        long time_diff = new_time - old_time_;
            // only react, if last measure was at last 5 secs ago:
        if((time_diff) < TIME_DIFF)
          return;

        LatLonPoint new_point_ = (LatLonPoint)event.getNewValue();
        if(new_point_ == null)
          return;

            // first call, so no old point stored yet
        if(old_point_ == null)
        {
          old_point_ = new_point_;
          return;
        }

        distance_ = calculateDistance(old_point_,new_point_);
        total_distance_ += distance_;

//  	System.out.println("current_speed: "+current_speed_);
//  	System.out.println("distance: "+distance_);
//  	System.out.println("timediff: "+time_diff);
//  	System.out.println("total distance: "+total_distance_);
//	System.out.println();
        property_change_support_.firePropertyChange(PROPERTY_KEY_TOTAL_DISTANCE,
                                                    null,
                                                    new Float(total_distance_));
        if(!use_gps_speed_)
	{
	  // speed in km/h:
	  current_speed_ = distance_ * 3600000f/time_diff;
          property_change_support_.firePropertyChange(PROPERTY_KEY_CURRENT_SPEED,
                                                      null,
                                                      new Float(current_speed_));
	}
        if(!use_gps_heading_)
	{
	  current_heading_ = (float)GeoMath.courseDegrees(old_point_.getLatitude(),
							  old_point_.getLongitude(),
							  new_point_.getLatitude(),
							  new_point_.getLongitude());

          property_change_support_.firePropertyChange(PROPERTY_KEY_CURRENT_HEADING,
                                                      null,
                                                      new Float(current_heading_));
	}
        old_point_ = new_point_;
        old_time_ = new_time;

        return;
      }
    }
    
  }


      //----------------------------------------------------------------------
      // The action classes
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      /**
       * The Action that triggers for "edit_resources" events.
       */

  class EditResourcesAction extends AbstractAction 
  {
        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public EditResourcesAction()
    {
      super(ACTION_EDIT_RESOURCES);
    }

        //----------------------------------------------------------------------
        /**
         * Visualizes the resource editor.
         *
         * @param event the action event
         */

    public void actionPerformed(ActionEvent event)
    {
      if (resource_editor_ == null)
      {
        resource_editor_ = new ResourceEditorFrame(resources_,USER_RESOURCE_DIR_NAME,
                                                   resources_.getString(KEY_RESOURCE_EDITOR_TITLE));
        resource_editor_.rememberSizeAndPosition(true);
      }
      else
      {
        resource_editor_.reset();
      }
      resource_editor_.setVisible(true);
    }
  }

      //----------------------------------------------------------------------
      /**
       * The Action that triggers for "quit" events.
       */

  class QuitAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public QuitAction()
    {
      super(ACTION_QUIT);
      setEnabled(true);
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
      Point location;
      Dimension dimension;
      if (resources_.getBoolean(KEY_REMEMBER_FRAME_SETTINGS))
      {
        location = main_frame_.getLocationOnScreen();
        dimension = main_frame_.getSize();
        resources_.setInt(KEY_WINDOW_LOCATION_X, location.x);
        resources_.setInt(KEY_WINDOW_LOCATION_Y, location.y);
        resources_.setInt(KEY_WINDOW_DIMENSION_WIDTH, dimension.width);
        resources_.setInt(KEY_WINDOW_DIMENSION_HEIGHT, dimension.height);
      }

      if (resources_.getBoolean(KEY_CURRENT_MAP_POSITION_REMEMBER_CURRENT_MAP_POSITION))
      {
        LatLonPoint center = map_bean_.getCenter();
        resources_.setDouble(KEY_CURRENT_MAP_POSITION_LATITUDE, center.getLatitude());
        resources_.setDouble(KEY_CURRENT_MAP_POSITION_LONGITUDE, center.getLongitude());
      }

      if(resources_.getBoolean(KEY_CURRENT_GPS_POSITION_REMEMBER_CURRENT_GPS_POSITION))
      {
        resources_.setDouble(KEY_CURRENT_GPS_POSITION_LATITUDE,current_gps_position_.getLatitude());
        resources_.setDouble(KEY_CURRENT_GPS_POSITION_LONGITUDE,current_gps_position_.getLongitude());
      }

      if (resources_.getBoolean(KEY_MAP_SCALE_REMEMBER_MAP_SCALE))
      {
        resources_.setDouble(KEY_MAP_SCALE, map_bean_.getScale());
      }

      try
      {
        resources_.store();
      }
      catch (IOException exc)
      {
        System.err.println("ERROR: could not save resources: "+exc.getMessage());
      }

      try
      {
        if(gps_data_processor_ != null)
          gps_data_processor_.close();
      }
      catch(GPSException gpse)
      {
        System.err.println("WARNING: could not close connection to gps device: "
                           +gpse.getMessage());
      }
      
      System.exit(0);
    }
  }

      //----------------------------------------------------------------------
      /**
       * The Action that triggers a change for the mousemode events.
       */

  class MousePositionModeAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public MousePositionModeAction()
    {
      super(ACTION_MOUSE_POSITION_MODE);
      putValue(MenuFactory.SELECTED, new Boolean(false));
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
          // unselect the other mouse modes:
// TODO FIXXME Does only work once!
      Action action = action_store_.getAction(ACTION_MOUSE_NAVIGATION_MODE);
      if(action != null)
        action.putValue(MenuFactory.SELECTED, new Boolean(false));
      action = action_store_.getAction(ACTION_MOUSE_DISTANCE_MODE);
      if(action != null)
        action.putValue(MenuFactory.SELECTED, new Boolean(false));

//      mouse_delegator_.setActiveMouseModeWithID(SelectMouseMode.modeID);
    }
  }

      //----------------------------------------------------------------------
      /**
       * The Action that triggers a change for the mousemode events.
       */

  class MouseDistanceModeAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public MouseDistanceModeAction()
    {
      super(ACTION_MOUSE_DISTANCE_MODE);
      putValue(MenuFactory.SELECTED, new Boolean(false));
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
          // unselect the other mouse modes:
      Action action = action_store_.getAction(ACTION_MOUSE_NAVIGATION_MODE);
      if(action != null)
        action.putValue(MenuFactory.SELECTED, new Boolean(false));
      action = action_store_.getAction(ACTION_MOUSE_POSITION_MODE);
      if(action != null)
        action.putValue(MenuFactory.SELECTED, new Boolean(false));

//      mouse_delegator_.setActiveMouseModeWithID(DistanceMouseMode.modeID);
    }
  }

      //----------------------------------------------------------------------
      /**
       * The Action that triggers a change for the mousemode events.
       */

  class MouseNavigationModeAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public MouseNavigationModeAction()
    {
      super(ACTION_MOUSE_NAVIGATION_MODE);
      putValue(MenuFactory.SELECTED, new Boolean(true)); // default mode
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
          // unselect the other mouse modes:
      Action action = action_store_.getAction(ACTION_MOUSE_DISTANCE_MODE);
      if(action != null)
        action.putValue(MenuFactory.SELECTED, new Boolean(false));
      action = action_store_.getAction(ACTION_MOUSE_POSITION_MODE);
      if(action != null)
        action.putValue(MenuFactory.SELECTED, new Boolean(false));
       
//      mouse_delegator_.setActiveMouseModeWithID(NavMouseMode.modeID);
    }
  }

      //----------------------------------------------------------------------
      /**
       * The Action that triggers zoom in event.
       */

  class ZoomInAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public ZoomInAction()
    {
      super(ACTION_ZOOM_IN);
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
      reScale(0.5f);
    }
  }

      //----------------------------------------------------------------------
      /**
       * The Action that triggers zoom out event.
       */

  class ZoomOutAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public ZoomOutAction()
    {
      super(ACTION_ZOOM_OUT);
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
      reScale(2.0f);
    }
  }

      //----------------------------------------------------------------------
      /**
       * The Action that triggers center map event
       */

  class CenterMapAction extends AbstractAction 
  {

    LatLongInputDialog input_dialog_;

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public CenterMapAction()
    {
      super(ACTION_CENTER_MAP);
    }

        //----------------------------------------------------------------------
        /**
         * Opens a dialog to enter coordinates for the new center of the map.
         * 
         * @param event the action event
         */

    public void actionPerformed(ActionEvent event)
    {
      if (input_dialog_ == null)
      {
        ActionListener action_listener = new ActionListener()
          {
            public void actionPerformed(ActionEvent event)
            {
              if(event.getActionCommand().equals(LatLongInputDialog.COMMAND_OK))
              {
                if(input_dialog_.checkValidity())
                {
                  double latitude = input_dialog_.getLatitude();
                  double longitude = input_dialog_.getLongitude();
                  setMapCenter(latitude,longitude);
                  input_dialog_.setVisible(false);
                }
                return;
              }

              if(event.getActionCommand().equals(LatLongInputDialog.COMMAND_CANCEL))
              {
                input_dialog_.setVisible(false);
                return;
              }
            }
          };
        input_dialog_ = new LatLongInputDialog(resources_,action_listener, main_frame_);
        input_dialog_.setTitle(resources_.getString(KEY_LOCALIZE_CENTER_MAP_DIALOG_TITLE));
      }
      LatLonPoint pos = getCurrentGPSPosition();
      input_dialog_.setCoordinates(pos.getLatitude(),pos.getLongitude());
      input_dialog_.setVisible(true);
    }
  }
      //----------------------------------------------------------------------
      /**
       * The Action that triggers follow me mode.
       */

  class ResetTachometerAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public ResetTachometerAction()
    {
      super(ACTION_RESET_TACHOMETER);
    }

        //----------------------------------------------------------------------
        /**
         * Resets the values from the tachometer.
         * 
         * @param event the action event
         */

    public void actionPerformed(ActionEvent event)
    {
	tacho_meter_.reset();
    }
  }

      //----------------------------------------------------------------------
      /**
       * The Action that triggers simulation mode.
       */

  class SimulationModeAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public SimulationModeAction()
    {
      super(ACTION_SIMULATION_MODE);
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
//      System.out.println("simulation mode action called");
      simulation_mode_ = !simulation_mode_;
      Action action = action_store_.getAction(GPSMap.ACTION_SIMULATION_MODE);
      if(action != null)
        action.putValue(MenuFactory.SELECTED, new Boolean(simulation_mode_));

      updateGPSConnection();
      if(simulation_mode_ && (destination_position_ != null))
        startSimulation(current_gps_position_,destination_position_);
    }
  }

      //----------------------------------------------------------------------
      /**
       * The Action that imports data from gpsdrive.
       */

  class ImportGpsDriveAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public ImportGpsDriveAction()
    {
      super(ACTION_IMPORT_GPSDRIVE);
    }

  //----------------------------------------------------------------------
  /**
   * Reads the map_koord.txt file from gpsdrive and copies its
   * entries to the maps description file (adding height and
   * width).
   * 
   * @param event the action event
   */
  
    public void actionPerformed(ActionEvent event)
    {
      System.out.println("import_gpsdrive action called");
      String home_dir = System.getProperty("user.home");
      String gpsdrive_dir = home_dir + File.separator + ".gpsdrive";
      
      String import_filename = gpsdrive_dir + File.separator + "map_koord.txt";

      BufferedReader map_reader;
    
      try
      {
        map_reader= new BufferedReader(new FileReader(import_filename));
      }
      catch(FileNotFoundException fnfe)
      {
        System.err.println("ERROR: Could not open map description file from gpsdrive '"+import_filename+"'");
        return;
      }


      try
      {
        int linenumber = 0;
        String line;
        String latitude_string;
        String longitude_string;
        String map_filename;
        String scale_string;
        StringTokenizer tokenizer;
        String full_filename;
        MapInfo info;
        
        while ((line = map_reader.readLine()) != null)
        {
          linenumber++;
          if ((!line.startsWith("#")) && (line.length() > 0)) // comments allowed????
          {
            try
            {
              tokenizer = new StringTokenizer(line);
              map_filename = tokenizer.nextToken();
              latitude_string = tokenizer.nextToken();
              longitude_string = tokenizer.nextToken();
              scale_string = tokenizer.nextToken();


                  // only add files that are not already available!
              full_filename = gpsdrive_dir + File.separator + map_filename;

                    // replace comma delimiter, just in case!
              latitude_string = latitude_string.replace(',','.');
              longitude_string = longitude_string.replace(',','.');
              scale_string = scale_string.replace(',','.');
              
              info = new MapInfo(full_filename,
                       new Double(latitude_string).doubleValue(),
                       new Double(longitude_string).doubleValue(),
                       new Float(scale_string).floatValue(),
                       1280,1024);
                addNewMap(info);
            }
            catch(NoSuchElementException nsee)
            {
              System.err.println("ERROR: reading map description in line "
                                 +linenumber+" in file '"+import_filename+"'");
              System.err.println("The correct format of the map description file is:");
              System.err.println("<mapfilename> <latitude of center> <longitude of center> <scale>");
              System.err.println("Ignoring line '"+line+"'");
            }
            catch(NumberFormatException nfe)
            {
              System.err.println("ERROR: reading map description in line "
                                 +linenumber+" in file '"+import_filename+"'");
              System.err.println("The correct format of the map description file is:");
              System.err.println("<mapfilename> <latitude of center> <longitude of center> <scale>");
              System.err.println("Ignoring line '"+line+"'");
            }
          }
        }      
        map_reader.close();
      }
      catch(IOException ioe)
      {
        ioe.printStackTrace();
      }
    }
  }

      //----------------------------------------------------------------------
      /**
       * The Action that triggers the setting of the scale.
       */

  class SetScaleAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public SetScaleAction()
    {
      super(ACTION_SET_SCALE);
    }

        //----------------------------------------------------------------------
        /**
         * Sets the scale of the map.
         * 
         * @param event the action event
         */

    public void actionPerformed(ActionEvent event)
    {
//      System.out.println("simulation mode action called");
      Action action = action_store_.getAction(GPSMap.ACTION_SET_SCALE);
      if(action != null)
        action.putValue(MenuFactory.SELECTED, new Boolean(simulation_mode_));

      float scale = 50000f;
      map_bean_.setScale(scale);
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
      super(ACTION_VIEW_GPS_DATA);
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
        NmeaDataTextFrame frame = new NmeaDataTextFrame(resources_,gps_data_processor_);
        frame.setVisible(true);
      }
    }
  }


      //----------------------------------------------------------------------
      /**
       * The Action for saving the image. It uses plugins that
       * implement the functionality for e.g. saving the image as
       * jpg,png, svg,...)
       */

  class SaveAsImagePluginAction extends AbstractAction 
  {
    JFileChooser file_chooser_;
    Object[] plugins_;

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public SaveAsImagePluginAction()
    {
      super(ACTION_SAVE_AS_IMAGE_PLUGIN);

            // find all available write image plugins:
            // (do not use a string here, so the compiler checks for typos)
      plugins_ = service_discovery_.getServices(
        org.dinopolis.gpstool.plugin.WriteImagePlugin.class);
      
      if(Debug.DEBUG)
        Debug.println("plugin","plugins for writing image detected: "+Debug.objectToString(plugins_));
          // disable action, if no plugins found:
      if(plugins_.length == 0)
      {
        setEnabled(false);
      }
    }

        //----------------------------------------------------------------------
        /**
         * Saves the image as the chosen file format
         * 
         * @param event the action event
         */

    public void actionPerformed(ActionEvent event)
    {
      if(file_chooser_ == null)
      {
        file_chooser_ = new JFileChooser();
//        file_chooser_.setDialogTitle(resources_.getString(KEY_LOCALIZE_SAVE_AS_DIALOG_TITLE));
        file_chooser_.setAcceptAllFileFilterUsed(false);
        file_chooser_.setMultiSelectionEnabled(false);
        file_chooser_.setFileHidingEnabled(false);


            // use plugins as extension file filters:
        ExtensionFileFilter filter;
        WriteImagePlugin plugin;
        String[] extensions;
        boolean plugin_found = false;
        for(int plugin_count = 0; plugin_count < plugins_.length; plugin_count++)
        {
          plugin = (WriteImagePlugin)plugins_[plugin_count];
          if(plugin !=null)
          {
            filter = new ExtensionFileFilter();
            extensions = plugin.getContentFileExtensions();
            for(int extension_count = 0; extension_count < extensions.length; extension_count++)
              filter.addExtension(extensions[extension_count]);
            filter.setDescription(plugin.getContentDescription());//"JPG "+resources_.getString(KEY_LOCALIZE_IMAGES));
            filter.setAuxiliaryObject(plugin);
            file_chooser_.addChoosableFileFilter(filter);
            plugin_found = true;
          }
        }
        if(!plugin_found)
        {
              // TODO: open dialog for error:
          System.err.println("ERROR: no plugin found!");
          return;
        }
      }

      int result = file_chooser_.showSaveDialog(map_bean_);
      if(result == JFileChooser.APPROVE_OPTION)
      {
        try
        {
          ExtensionFileFilter filter = (ExtensionFileFilter)file_chooser_.getFileFilter();
          WriteImagePlugin plugin = (WriteImagePlugin)filter.getAuxiliaryObject();
          File file = file_chooser_.getSelectedFile();
          String format = ((ExtensionFileFilter)file_chooser_.getFileFilter()).getExtension(file);
          if (format == null)
          {
            format = plugin.getContentFileExtensions()[0];
            file = new File(file.getPath()+"."+format);
          }
//          System.out.println("using file "+file+" to save in format: "+format);
          plugin.write(map_bean_,new FileOutputStream(file));
        }
        catch(IOException ioe)
        {
          ioe.printStackTrace();
        }
      }
    }
  }

      //----------------------------------------------------------------------
      /**
       * The Action that pans left/right/up/down for the given factors.
       */

  class PanAction extends AbstractAction 
  {
    float pan_horiz_;
    float pan_vertical_;
    
        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public PanAction(String action_id,float horizontal, float vertical)
    {
      super(action_id);
      pan_horiz_ = horizontal;
      pan_vertical_ = vertical;
    }

        //----------------------------------------------------------------------
        /**
         * Open the frame to show gps data
         * 
         * @param event the action event
         */

    public void actionPerformed(ActionEvent event)
    {
      translateMapCenterRelative(pan_horiz_,pan_vertical_);
    }
  }

      //----------------------------------------------------------------------
      /**
       * The Action that sets the scale to the given factor.
       */

  class ScaleAction extends AbstractAction 
  {
    float scale_;
    
        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public ScaleAction(String action_id,float scale)
    {
      super(action_id);
      scale_ = scale;
    }

        //----------------------------------------------------------------------
        /**
         * Open the frame to show gps data
         * 
         * @param event the action event
         */

    public void actionPerformed(ActionEvent event)
    {
      setScale(scale_);
    }
  }

//       //----------------------------------------------------------------------
//       /**
//        * The Action for testing
//        */

//   class TestAction extends AbstractAction 
//   {

//         //----------------------------------------------------------------------
//         /**
//          * The Default Constructor.
//          */

//     public TestAction()
//     {
//       super(ACTION_TESTACTION);
//     }

//         //----------------------------------------------------------------------
//         /**
//          * Ouputs some test message
//          * 
//          * @param event the action event
//          */

//     public void actionPerformed(ActionEvent event)
//     {
//       System.out.println("test action called");
//     }
//   }


}

