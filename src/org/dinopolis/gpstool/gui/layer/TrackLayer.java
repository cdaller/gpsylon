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

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.Layer;
import com.bbn.openmap.event.LayerStatusEvent;
import com.bbn.openmap.event.MapMouseAdapter;
import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.SelectMouseMode;
import com.bbn.openmap.proj.Projection;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.beans.PropertyChangeEvent; 
import java.beans.PropertyChangeListener; 
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import org.dinopolis.gpstool.GPSMap;
import org.dinopolis.gpstool.GPSMapKeyConstants;
import org.dinopolis.gpstool.gui.layer.track.Track;
import org.dinopolis.gpstool.gui.layer.track.TrackPoint;
import org.dinopolis.gpstool.gui.layer.track.ReadGPSMapTrackPlugin;
import org.dinopolis.gpstool.util.ExtensionFileFilter;
import org.dinopolis.gpstool.util.FileUtil;
import org.dinopolis.util.Debug;
import org.dinopolis.util.Resources;
import org.dinopolis.util.gui.ActionStore;
import org.dinopolis.util.gui.MenuFactory;
import org.dinopolis.util.gui.SwingWorker;
import org.dinopolis.gpstool.plugin.ReadTrackPlugin;
import org.dinopolis.gpstool.plugin.PluginSupport;

//----------------------------------------------------------------------
/**
 * A layer that is able to display gps tracks and saves gps tracks
 * into files.
 *
 * @author Christof Dallermassl
 * @version $Revision$ */

public class TrackLayer extends Layer implements PropertyChangeListener,
GPSMapKeyConstants
{

  LatLonPoint current_position_;
  float current_speed_;
  float current_altitude_;

      /** a vector that holds vectors of trackpoints */
  Vector loaded_tracks_ = new Vector();
      /** holding trackpoints for the current track (gps position) */
  Track current_track_ = new Track("active log");

      /** holds GeneralPath objects */
  Vector loaded_tracks_paths_;
      /** the current track (gps positions) in screen coordinates */
  GeneralPath current_track_path_;
  
  Object loaded_tracks_paths_lock_ = new Object();
  Object current_track_path_lock_ = new Object();

  BasicStroke current_track_line_stroke_;
  BasicStroke loaded_track_line_stroke_;

  Color current_track_color_;
  Color loaded_track_color_;
  
  boolean display_track_;
  boolean log_track_ = false;
  
  boolean start_track_logging_ = true;
//  Point tmp_point_ = new Point();


  boolean layer_active_ = true;
  
  PrintWriter track_out_;
  boolean always_close_track_file_ = false;
  String track_log_filename_;
  
  Resources resources_;

  MessageFormat track_format_;

  JFileChooser track_file_chooser_;

  SwingWorker swing_worker_;
  
  PropertyChangeSupport property_change_support_;

  ActionStore action_store_;

  final static BasicStroke line_stroke_ = new BasicStroke(3.0f);

  public final int GPSMAP_TYPE = 1;
  public final int GPSDRIVE_TYPE = 2;
  public final static String TRACK_FORMAT_DEFINITION_IN_FILE_PREFIX = "# Format: ";

  PluginSupport plugin_support_;

//----------------------------------------------------------------------
/**
 * Construct a track layer.  
 */
  public TrackLayer()
  {
  }


//----------------------------------------------------------------------
/**
 * Initializes this layer with the given resources.
 */
  public void initialize(PluginSupport support)
  {
    plugin_support_ = support;
    resources_ = support.getResources();
    String track_format = resources_.getString(KEY_TRACK_FILE_FORMAT);
    track_format_ = new MessageFormat(track_format,Locale.US); // for decimal points

    current_track_color_ = resources_.getColor(KEY_TRACK_CURRENT_TRACK_COLOR);
    loaded_track_color_ = resources_.getColor(KEY_TRACK_LOADED_TRACK_COLOR);
    current_track_line_stroke_ =
      new BasicStroke((float)resources_.getDouble(KEY_TRACK_CURRENT_TRACK_LINE_WIDTH));
    loaded_track_line_stroke_ =
      new BasicStroke((float)resources_.getDouble(KEY_TRACK_LOADED_TRACK_LINE_WIDTH));
    
    display_track_ = resources_.getBoolean(KEY_TRACK_DISPLAY_MODE);

    layer_active_ = resources_.getBoolean(KEY_TRACK_LAYER_ACTIVE);

        /** the Actions */
    Action[] actions = { new DisplayTrackModeAction(),
                         new SaveTrackModeAction(),
                         new LoadTrackAction(),
                         new ClearTrackModeAction(),
                         new TrackLayerActivateAction()
    };
    action_store_ = ActionStore.getStore(GPSMap.ACTION_STORE_ID);
    action_store_.addActions(actions);
    
    setDoubleBuffered(true);
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
    String name = event.getPropertyName();
    if(name.equals(GPSMap.PROPERTY_KEY_CURRENT_SPEED)
       || name.equals(GPSMap.PROPERTY_KEY_GPS_SPEED))
    {
      Float speed = (Float)event.getNewValue();
      if(speed != null)
        current_speed_ = speed.floatValue();
    }
    if(name.equals(GPSMap.PROPERTY_KEY_GPS_ALTITUDE))
    {
      Float altitude = (Float)event.getNewValue();
      if(altitude != null)
        current_altitude_ = altitude.floatValue();
    }
    if(name.equals(GPSMap.PROPERTY_KEY_GPS_LOCATION))
    {
      LatLonPoint tmp_point = (LatLonPoint)event.getNewValue();
//       System.out.println("TrackLayer, propertyChange: old: "+event.getOldValue()+" new:"+
// 			 event.getNewValue());
      if(tmp_point != null)
      {
        if(Debug.DEBUG)
          Debug.println("GPSMap_TrackLayer","new position in tracklayer: "+tmp_point);
        addTrackPosition(tmp_point,current_speed_, current_altitude_);
      }
      return;
    }
    if(name.equals(KEY_TRACK_FILE_FORMAT))
    {
      String track_format = (String)event.getNewValue();
      if(track_format != null)
        track_format_ = new MessageFormat(track_format,Locale.US); // for decimal points
      return;
    }
  }

//----------------------------------------------------------------------
/**
 * Add a new position for the track.
 * 
 * @param position the new position to add.
 * @param speed the speed at the position
 * @param altitude the altitude at the position
 */
  protected void addTrackPosition(LatLonPoint position, float speed,
                                  float altitude)
  {
    if((position == null)) // || (!display_track_ && !log_track_))
      return;

    TrackPoint track_point = new TrackPoint(position,altitude,speed,new Date());
    synchronized(current_track_)
    {
      current_track_.add(track_point);
    }
    track_point.forward(getProjection());
    synchronized(current_track_path_lock_)
    {
      if(current_track_path_ == null)
      {
        current_track_path_ = new GeneralPath();
        current_track_path_.moveTo((float)track_point.getX(),(float)track_point.getY());
      }
      else
      {
        current_track_path_.lineTo((float)track_point.getX(),(float)track_point.getY());
      }
    }
    
    if(log_track_)
    {
      if(start_track_logging_)
      {
        logTrackLine("# Format: "+track_format_.toPattern());
        start_track_logging_ = false;
      }
//       Object[] params = new Object[] {new Double(position.getLatitude()), 
//                                       new Double(position.getLongitude()),
//                                       new Float(GPSMap.getAltitude(altitude)),
//                                       GPSMap.getAltitudeUnit(),
//                                       new Float(GPSMap.getDistanceOrSpeed(speed)),
//                                       GPSMap.getSpeedUnit(),
//                                       new Date(System.currentTimeMillis())};
      
          // track logs use always meters, km/h ..., no customized data!
      Object[] params = new Object[] {new Double(position.getLatitude()), 
                                      new Double(position.getLongitude()),
                                      new Float(altitude),
                                      "m",
                                      new Float(speed),
                                      "km/h",
                                      new Date()}; 
      String line = track_format_.format(params,new StringBuffer(), null).toString();
      logTrackLine(line);
    }
    
    if(display_track_)
    {
//       Rectangle bounds = track_screen_path_.getBounds();
//       repaint((int)bounds.getX(),(int)bounds.getY(),
//               (int)bounds.getWidth(),(int)bounds.getHeight());
      repaint();
    }
  }


//----------------------------------------------------------------------
/**
 * Log a line to the track log file.
 *
 */
  protected void logTrackLine(String message)
  {
    if(!log_track_)
      return;
    try
    {
      if(always_close_track_file_ || track_out_ == null)
        track_out_ = new PrintWriter(
          new BufferedWriter(new FileWriter(track_log_filename_,true))); //append data to file
      track_out_.println(message);
      track_out_.flush();
      if(always_close_track_file_)
        track_out_.close();
    }
    catch(IOException ioe)
    {
      System.err.println("Cannot open/write to logfile '"+track_log_filename_
                         +"' - stop logging!");
      ioe.printStackTrace();
      log_track_ = false;
    }
  }

//----------------------------------------------------------------------
/**
 * Recalculate the path (screen coordinates) to draw from the tracks
 * (loaded tracks and current track). This is done in a separate
 * thread using a SwingWorker.
 */
  protected void recalculatePath()
  {
    if((!layer_active_) || (!display_track_))
      return;
        // stop old thread
    if(swing_worker_ != null)
      swing_worker_.interrupt();

    swing_worker_ = new SwingWorker()
      {
        Vector loaded_paths_ = new Vector();
        boolean interrupted_ = false;
        GeneralPath track_path_;
        
        public Object construct()
        {
          fireStatusUpdate(LayerStatusEvent.START_WORKING);
          
          if(loaded_tracks_ != null)
          {
            Vector loaded_tracks;
            synchronized(loaded_tracks_)
            {
              loaded_tracks = (Vector)loaded_tracks_.clone();
            }
            Iterator loaded_track_iterator = loaded_tracks.iterator();
            Track track;
            while(loaded_track_iterator.hasNext())
            {
              track = (Track)loaded_track_iterator.next();
              track.forward(getProjection());
              track_path_ = createPathFromTrack(track);
              if(track_path_ != null)
                loaded_paths_.addElement(track_path_);
              if(Thread.interrupted())
              {
                track_path_ = null;
                interrupted_ = true;
                return(null);
              }
            }
          }
          if(current_track_ != null)
          {
            Track current;
            synchronized(current_track_)
            {
                  // clone the current list, as otherwise concurrent access could occur
              current = (Track)current_track_.clone();
            }
            current.forward(getProjection());
            track_path_ = createPathFromTrack(current);
          }
          if(Thread.interrupted())
          {
            track_path_ = null;
            interrupted_ = true;
            return(null);
          }
          return(null);
        }

        public void finished()
        {
          if(!interrupted_)
          {
            fireStatusUpdate(LayerStatusEvent.FINISH_WORKING);
            setLoadedTrackPaths(loaded_paths_);
            setCurrentTrackPath(track_path_);
            repaint();
          }
        }
      };
    swing_worker_.start();
  }

//----------------------------------------------------------------------
/**
 * Add a track. Calculate the screen coordinates and adds the track
 * and the a GeneralPath object from the track (in the background
 * using a SwingWorker) to the loaded tracks/paths. Does NOT repaint
 * the screen!
 *
 * @param new_track the track to add.
 */
  public void addTrack(final Track new_track)
  {
    SwingWorker worker = new SwingWorker()
      {
        GeneralPath new_path_;
        public Object construct()
        {
          fireStatusUpdate(LayerStatusEvent.START_WORKING);
          new_track.forward(getProjection());
          new_path_ = createPathFromTrack(new_track);
          synchronized(loaded_tracks_)
          {
            loaded_tracks_.addElement(new_track);
          }
          addLoadedTrackPath(new_path_);
          return(null);
        }

        public void finished()
        {
          fireStatusUpdate(LayerStatusEvent.FINISH_WORKING);
        }
      };
    worker.start();
  }

//----------------------------------------------------------------------
/**
 * Creates a GeneralPath from the given track that holds
 * TrackPointInfo objects. The first point is added to the path with
 * a <code>moveTo</code> method, the others by the use of
 * <code>lineTo</code>.
 *
 * @param track the track to create a path from it.
 * @return the new path with the data added or null, if no data was
 * contained in the new track.
 */
  protected GeneralPath createPathFromTrack(Track track)
  {
    Point tmp_point = new Point();
    GeneralPath path = new GeneralPath();
    Iterator trackpoint_iterator = track.iterator();
    TrackPoint trackpoint;
        // add the first point with moveTo method:
    if(trackpoint_iterator.hasNext())
    {
      trackpoint = (TrackPoint)trackpoint_iterator.next();
      path.moveTo((float)trackpoint.getX(),(float)trackpoint.getY());
    }
    else
    {
      return(null); // nothing in the new track!
    }
    while(trackpoint_iterator.hasNext())
    {
      trackpoint = (TrackPoint)trackpoint_iterator.next();
      path.lineTo((float)trackpoint.getX(),(float)trackpoint.getY());
    }
    return(path);
  }
  
// //----------------------------------------------------------------------
// /**
//  * Add a track from a file.
//  *
//  * @param file the file that contains the track information.
//  * @param type the type of the track file
//  */
//   public void addTrack(File file, int type)
//   {
//     int linenumber = 0;
//     try
//     {
//       BufferedReader track_in = new BufferedReader(new FileReader(file));
//       String line;
//       MessageFormat line_format;
//       Track track = new Track(file.getPath());
//       switch(type)
//       {
// 	    case GPSMAP_TYPE:
//         if(Debug.DEBUG)
//           Debug.println("GPSMap_tracklayer_load","loading GPSMap track");
// 	      line_format = track_format_;  // default format
// 	      while((line = track_in.readLine()) != null)
// 	      {
//           linenumber++;
//           if(line.startsWith(TRACK_FORMAT_DEFINITION_IN_FILE_PREFIX))
//           {
//             line_format =
//               new MessageFormat(line.substring(TRACK_FORMAT_DEFINITION_IN_FILE_PREFIX.length()),
//                                 Locale.US);  // for . as decimal point!
            
// //            System.out.println("using format: '"+line.substring(format_def.length())+"'");
//           }
//           if(!line.startsWith("#"))
//           {
//             Object[] objs = line_format.parse(line);

//             float latitude = 0.0f;
//             float longitude = 0.0f;
//             float altitude = 0.0f;
//             String altitude_unit = "";
//             float speed = 0.0f;
//             String speed_unit = "";
//             Date date = null;

//             if(objs.length > 0)
//             {
//               latitude = ((Number)objs[0]).floatValue();
//               if(objs.length > 1)
//               {
//                 longitude = ((Number)objs[1]).floatValue();
//                 if(objs.length > 2)
//                 {
//                   altitude = ((Number)objs[2]).floatValue();
//                   if(objs.length > 3)
//                   {
//                     altitude_unit = (String)objs[3];
//                     if(objs.length > 4)
//                     {
//                       speed = ((Number)objs[4]).floatValue();
//                       if(objs.length > 5)
//                       {
//                         speed_unit = (String)objs[5];
//                         if(objs.length > 6)
//                         {
//                           if(objs[6] instanceof Date)
//                             date = (Date)objs[6];
//                           else
//                             date = null;
//                         }
//                       }
//                     }
//                   }
//                 }
//               }
//             }
            
//             LatLonPoint location = new LatLonPoint(latitude,longitude);
//             TrackPoint info = new TrackPoint(location,altitude,speed,date);
//             track.add(info);
//           }
// 	      }
//         if(Debug.DEBUG)
//           Debug.println("GPSMap_tracklayer_load","finished loading GPSMap track");

// 	      break;
// 	    case GPSDRIVE_TYPE:
//         if(Debug.DEBUG)
//           Debug.println("GPSMap_tracklayer_load","loading gpsdrive track");
//         StringTokenizer tokenizer;
//         SimpleDateFormat date_format = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy",Locale.US);
// 	      while((line = track_in.readLine()) != null)
// 	      {
//           linenumber++;
//           if(!line.startsWith("#"))
//           {
//             tokenizer = new StringTokenizer(line," ");
            
//             float latitude = Float.parseFloat(tokenizer.nextToken());
//             if(latitude != 1001.0f)  // 1001 means invalid position
//             {
//               float longitude = Float.parseFloat(tokenizer.nextToken()); 
//               float altitude = Integer.parseInt(tokenizer.nextToken());
//                   // hack:
//               String first_date_token = tokenizer.nextToken();
//               Date date = date_format.parse(line,new ParsePosition(line.indexOf(first_date_token)));

//                   // TODO: calculate speed
//               TrackPoint info = new TrackPoint(latitude,longitude,altitude,0.0f,date);
//               track.add(info);
//             }
//           }
//  	      }
//         if(Debug.DEBUG)
//           Debug.println("GPSMap_tracklayer_load","finished loading gpsdrive track");
// 	      break;
// 	    default:
// 	      break;
//       }
//       addTrack(track);
//     }
//     catch(IOException ioe)
//     {
//       ioe.printStackTrace();
//     }
//     catch(ParseException pe)
//     {
//       System.err.println("ERROR: ParseError in line "+linenumber+" of file '"+file+"'");
//       pe.printStackTrace();
//     }
//     catch(ClassCastException cce)
//     {
//       System.err.println("ERROR: Error in line "+linenumber+" of file '"+file+"': "+cce.getMessage());
//       cce.printStackTrace();
//     }
//     catch(NumberFormatException nfe)
//     {
//       System.out.println("ERROR: ParseError in line "+linenumber+" of file '"+file+"'");
//       nfe.printStackTrace();
//     }
//     catch(NoSuchElementException nsee)
//     {
//       System.out.println("ERROR: Invalid track line format in line "+linenumber+" of file '"+file+"'");
//       nsee.printStackTrace();
//     }
//   }


//----------------------------------------------------------------------
/**
 * Sets the paths for the loaded tracks (screen coordinates).
 *
 * @param paths a vector holding GeneralPath objects.
 */
  protected void setLoadedTrackPaths(Vector paths)
  {
    synchronized(loaded_tracks_paths_lock_)
    {
      loaded_tracks_paths_ = paths;
    }
  }

//----------------------------------------------------------------------
/**
 * Sets the paths for the loaded tracks (screen coordinates).
 *
 * @param paths a vector holding GeneralPath objects.
 */
  protected void addLoadedTrackPath(GeneralPath path)
  {
    synchronized(loaded_tracks_paths_lock_)
    {
      if(loaded_tracks_paths_ == null)
        loaded_tracks_paths_ = new Vector();
      loaded_tracks_paths_.addElement(path);
    }
  }

//----------------------------------------------------------------------
/**
 * Sets the path for the current track (screen coordinates).
 *
 * @param path the path.
 */
  protected void setCurrentTrackPath(GeneralPath path)
  {
    synchronized(current_track_path_lock_)
    {
      current_track_path_ = path;
    }
  }

  
//----------------------------------------------------------------------
// Layer overrides
//----------------------------------------------------------------------

//----------------------------------------------------------------------
/**
 * Renders the graphics list.  It is important to make this
 * routine as fast as possible since it is called frequently
 * by Swing, and the User Interface blocks while painting is
 * done.
 */
  public void paintComponent(Graphics g)
  {
    if(!layer_active_)
      return;
    if(!display_track_)
      return;
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g2.setStroke(loaded_track_line_stroke_);
    g2.setColor(loaded_track_color_);
    GeneralPath path;
    if(loaded_tracks_paths_ != null)
    {
      synchronized(loaded_tracks_paths_lock_)
      {
        Iterator loaded_track_iterator = loaded_tracks_paths_.iterator();
        while(loaded_track_iterator.hasNext())
        {
          path = (GeneralPath)loaded_track_iterator.next();
          g2.draw(path);
        }
      }
    }
    if(current_track_path_ != null)
    {
      g2.setStroke(current_track_line_stroke_);
      g2.setColor(current_track_color_);
      synchronized(current_track_path_lock_)
      {
        g2.draw(current_track_path_);
      }
    }
  }


//       /** 
//        * Implementing the ProjectionPainter interface.
//        */
//   public void renderDataForProjection(Projection proj, java.awt.Graphics g)
//   {
//     System.out.println("RenderDataForPojection callback");
//     if (proj == null)
//     {
// 	    System.err.println("ERROR: MultiImageLayer.renderDataForProjection: null projection!");
// 	    return;
//     }
//     else
//     {
// 	    setProjection(proj.makeClone());
//       omgraphic_list_.generate(proj);
//     }
//     paint(g);
//   }

  
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
    
    if (!proj.equals(getProjection()))
    {
      setProjection(proj.makeClone());
      
          // recalculate the track screen coordinates
      setCurrentTrackPath(null);
      setLoadedTrackPaths(null);
      recalculatePath();
    }
    if(Debug.DEBUG)
      Debug.println("MapLayer_projection","new projection: "+proj);

    if(display_track_)
    {
//      Rectangle bounds = track_screen_path_.getBounds();
//      repaint((int)bounds.getX(),(int)bounds.getY(),
//              (int)bounds.getWidth(),(int)bounds.getHeight());
//      repaint();
    }
  }


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

    if (property_change_support_ == null)
      property_change_support_ = new PropertyChangeSupport(this);
    property_change_support_.addPropertyChangeListener(key,listener);
//    System.out.println("TrackLayer: property change listener added for key "+key);
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


// //----------------------------------------------------------------------
// /**
//  * Returns whether this Component can become the focus owner. This
//  * method is deprecated since Java 1.4. But for the sake of backward
//  * compatibility it is used however (and calls the new isFocusable()
//  * method).
//  *
//  * @return true if this Component is focusable; false otherwise.
//  */

//   public boolean isFocusTraversable()
//   {
//     return(isFocusable());
//   }

//----------------------------------------------------------------------
/**
 * Returns whether this Component can become the focus owner.
 *
 * @return true if this Component is focusable; false otherwise.
 */

  public boolean isFocusable()
  {
    return(true);
  }


// ----------------------------------------------------------------------
// inner classes
// ----------------------------------------------------------------------

  
// ----------------------------------------------------------------------
// action classes

//----------------------------------------------------------------------
/**
 * The Action that triggers display track mode.
 */

  class DisplayTrackModeAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public DisplayTrackModeAction()
    {
      super(GPSMap.ACTION_DISPLAY_TRACK_MODE);
      putValue(MenuFactory.SELECTED, new Boolean(display_track_));
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
      display_track_  = !display_track_;
      resources_.setBoolean(KEY_TRACK_DISPLAY_MODE,display_track_);
      putValue(MenuFactory.SELECTED, new Boolean(display_track_));

      recalculatePath();
      repaint();
      
      if(Debug.DEBUG)
        Debug.println("GPSMap_TrackLayer","display_track mode: "+display_track_);
    }
  }

//----------------------------------------------------------------------
/**
 * The Action that triggers save track mode.
 */

  class SaveTrackModeAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public SaveTrackModeAction()
    {
      super(GPSMap.ACTION_SAVE_TRACK_MODE);
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
      log_track_  = !log_track_;
      putValue(MenuFactory.SELECTED, new Boolean(log_track_));

      if(log_track_)
      {
        start_track_logging_ = true;
        String dirname = FileUtil.getAbsolutePath(resources_.getString(KEY_FILE_MAINDIR),
                                                  resources_.getString(KEY_FILE_TRACK_DIR));
        File dir = new File(dirname);
        if(!dir.isDirectory())
        {
          System.err.println("Directory '"+dirname+"' does not exist, creating it.");
          dir.mkdirs();
        }
          
        if(track_out_ != null)
        {
          track_out_.close();
          track_out_ = null;
        }
            // create new filename:
        track_log_filename_ = FileUtil.getNextFileName(dirname,
                                                       resources_.getString(KEY_TRACK_FILE_PREFIX),
                                                       resources_.getString(KEY_TRACK_FILE_PATTERN),
                                                       "." + resources_.getString(KEY_TRACK_FILE_EXTENSION));

        
      }
      
      if(Debug.DEBUG)
        Debug.println("GPSMap_TrackLayer","save_track mode: "+log_track_);
    }
  }

//----------------------------------------------------------------------
/**
 * The Action that triggers clear track mode.
 */

  class ClearTrackModeAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public ClearTrackModeAction()
    {
      super(GPSMap.ACTION_CLEAR_TRACK);
    }

        //----------------------------------------------------------------------
        /**
         * Clears all tracks (current and loaded).
         * 
         * @param event the action event
         */

    public void actionPerformed(ActionEvent event)
    {
      synchronized(current_track_)
      {
        current_track_.clear();
      }
      synchronized(loaded_tracks_)
      {
        loaded_tracks_.clear();
      }
      synchronized(loaded_tracks_paths_lock_)
      {
        loaded_tracks_paths_ = null;
      }
      synchronized(current_track_path_lock_)
      {
        current_track_path_ = null;
      }
      repaint();

      if(track_out_ != null)
      {
        track_out_.close();
        track_out_ = null;
      }

          // stop saving the track if it is cleared
      Action action = action_store_.getAction(GPSMap.ACTION_SAVE_TRACK_MODE);
      if(action != null)
        action.putValue(MenuFactory.SELECTED, new Boolean(false));


      if(Debug.DEBUG)
        Debug.println("GPSMap_TrackLayer","clear_track mode: "+log_track_);
    }
  }


//----------------------------------------------------------------------
/**
 * The Action that triggers load track mode. It uses plugins that
 * provide the functionality.
 *
 * @see org.dinopolis.gpstool.plugin.ReadTrackPlugin.
 */

  class LoadTrackAction extends AbstractAction 
  {
    Object[] plugins_;

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public LoadTrackAction()
    {
      super(GPSMap.ACTION_LOAD_TRACK);
            // find all available track reader plugins:
            // (do not use a string here, so the compiler checks for typos)
      Object[] plugins = GPSMap.service_discovery_.getServices(
        org.dinopolis.gpstool.plugin.ReadTrackPlugin.class);
          // add ReadGPSMapTrackPlugin by hand:
      plugins_ = new Object[plugins.length + 1];
      plugins_[0] = new ReadGPSMapTrackPlugin();
      System.arraycopy(plugins,0,plugins_,1,plugins.length);
      
      if(Debug.DEBUG)
        Debug.println("plugin","plugins for reading tracks detected: "+Debug.objectToString(plugins_));

          // disable action, if no plugins found:
      if(plugins_.length == 0)
      {
        setEnabled(false);
      }
    }

        //----------------------------------------------------------------------
        /**
         * Load a track
         * 
         * @param event the action event
         */

    public void actionPerformed(ActionEvent event)
    {
      File[] chosen_files = null;
      if(track_file_chooser_ == null)
      {
        track_file_chooser_ = new JFileChooser();
        track_file_chooser_.setDialogTitle(resources_.getString(KEY_LOCALIZE_LOAD_TRACK_DIALOG_TITLE));
        track_file_chooser_.setAcceptAllFileFilterUsed(false);
        
        track_file_chooser_.setMultiSelectionEnabled(true);
        track_file_chooser_.setFileHidingEnabled(false);
        String tracks_dirname = FileUtil.getAbsolutePath(resources_.getString(KEY_FILE_MAINDIR),
                                                         resources_.getString(KEY_FILE_TRACK_DIR));
        track_file_chooser_.setCurrentDirectory(new File(tracks_dirname));

            // use all ReadTrackPlugin plugins to build extension file filters:
        ExtensionFileFilter filter;
        ReadTrackPlugin plugin;
        String[] extensions;
        boolean plugin_found = false;
        for(int plugin_count = 0; plugin_count < plugins_.length; plugin_count++)
        {
          plugin = (ReadTrackPlugin)plugins_[plugin_count];
          if(plugin != null)
          {
            plugin.initializePlugin(plugin_support_);
            filter = new ExtensionFileFilter();
            extensions = plugin.getContentFileExtensions();
            for(int extension_count = 0; extension_count < extensions.length; extension_count++)
              filter.addExtension(extensions[extension_count]);
            filter.setDescription(plugin.getContentDescription());//"JPG "+resources_.getString(KEY_LOCALIZE_IMAGES));
            filter.setAuxiliaryObject(plugin);
            track_file_chooser_.addChoosableFileFilter(filter);
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

      int result = track_file_chooser_.showOpenDialog(TrackLayer.this);
      if(result == JFileChooser.APPROVE_OPTION)
      {
        ExtensionFileFilter filter = (ExtensionFileFilter)track_file_chooser_.getFileFilter();
        ReadTrackPlugin plugin = (ReadTrackPlugin)filter.getAuxiliaryObject();
        File file = track_file_chooser_.getSelectedFile();
        chosen_files = track_file_chooser_.getSelectedFiles();
            // add all tracks from all files:
        for(int file_count = 0; file_count < chosen_files.length; file_count++)
        {
          try
          {
                // create inputstream from file for plugin:
            Track[] tracks = plugin.getTracks(new BufferedInputStream(
                                                 new FileInputStream(chosen_files[file_count])));
            for(int track_count = 0; track_count < tracks.length; track_count++)
            {
              addTrack(tracks[track_count]);
            }
          }
          catch(IOException ioe)
          {
            ioe.printStackTrace();
          }
        }
        if(!display_track_)
        {
              // enable "display track", otherwise we do not see the new loaded tracks!
          Action action = action_store_.getAction(GPSMap.ACTION_DISPLAY_TRACK_MODE);
          if(action != null)
            action.putValue(MenuFactory.SELECTED, new Boolean(true));
          display_track_ = true;
	
          recalculatePath();
        }
        else
          repaint(); // everything already calculated before!
      }
    }
  }

//  //----------------------------------------------------------------------
//  /**
//   * The Action that triggers load track mode.
//   */

//    class LoadTrackAction extends AbstractAction 
//    {

//          //----------------------------------------------------------------------
//          /**
//           * The Default Constructor.
//           */

//      public LoadTrackAction()
//      {
//        super(GPSMap.ACTION_LOAD_TRACK);
//      }

//          //----------------------------------------------------------------------
//          /**
//           * Load a track
//           * 
//           * @param event the action event
//           */

//      public void actionPerformed(ActionEvent event)
//      {
//        File[] chosen_files = null;
//        if(track_file_chooser_ == null)
//        {
//          track_file_chooser_ = new JFileChooser();
//          track_file_chooser_.setDialogTitle(resources_.getString(KEY_LOCALIZE_LOAD_TRACK_DIALOG_TITLE));
//          ExtensionFileFilter filter;

//              // add filter for gpsdrive tracks:
//          filter = new ExtensionFileFilter();
//          filter.addExtension(resources_.getString(KEY_TRACK_GPSDRIVE_FILE_EXTENSION));
//          filter.setDescription(resources_.getString(KEY_TRACK_GPSDRIVE_FILE_DESCRIPTIVE_NAME));
//          track_file_chooser_.addChoosableFileFilter(filter);
        
//              // add filter for GPSMap tracks:
//          filter = new ExtensionFileFilter();
//          filter.addExtension(resources_.getString(KEY_TRACK_FILE_EXTENSION));
//          filter.setDescription(resources_.getString(KEY_TRACK_FILE_DESCRIPTIVE_NAME));
//          track_file_chooser_.addChoosableFileFilter(filter);
        
//          track_file_chooser_.setAcceptAllFileFilterUsed(false);
        
//          track_file_chooser_.setMultiSelectionEnabled(true);
//          track_file_chooser_.setFileHidingEnabled(false);
//          String tracks_dirname = FileUtil.getAbsolutePath(resources_.getString(KEY_FILE_MAINDIR),
//                                                           resources_.getString(KEY_FILE_TRACK_DIR));
//          track_file_chooser_.setCurrentDirectory(new File(tracks_dirname));
//        }
      
//        int result = track_file_chooser_.showOpenDialog(TrackLayer.this);
//        if(result == JFileChooser.APPROVE_OPTION)
//        {
//          chosen_files = track_file_chooser_.getSelectedFiles();
//          String filter_description = ((ExtensionFileFilter)track_file_chooser_.getFileFilter()).getDescription();
//          int track_type = GPSMAP_TYPE;
//          if(filter_description.startsWith(resources_.getString(KEY_TRACK_GPSDRIVE_FILE_DESCRIPTIVE_NAME)))
//            track_type = GPSDRIVE_TYPE;
//          for(int count = 0; count < chosen_files.length; count++)
//          {
//            addTrack(chosen_files[count],track_type);
//          }

//              // enable "display track", otherwise we do not see the new loaded tracks!
//          Action action = action_store_.getAction(GPSMap.ACTION_DISPLAY_TRACK_MODE);
//          if(action != null)
//            action.putValue(MenuFactory.SELECTED, new Boolean(true));
//          display_track_ = true;

//          recalculatePath();
//          repaint();
//        }

      
//      }
//    }

//----------------------------------------------------------------------
/**
 * The Action that triggers the de-/activation of this layer.
 */

  class TrackLayerActivateAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public TrackLayerActivateAction()
    {
      super(GPSMap.ACTION_TRACK_LAYER_ACTIVATE);
      putValue(MenuFactory.SELECTED, new Boolean(layer_active_));
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
      layer_active_ = !layer_active_;
      putValue(MenuFactory.SELECTED, new Boolean(layer_active_));
      resources_.setBoolean(KEY_TRACK_LAYER_ACTIVE,layer_active_);
      recalculatePath();
      repaint();
    }
  }


}






