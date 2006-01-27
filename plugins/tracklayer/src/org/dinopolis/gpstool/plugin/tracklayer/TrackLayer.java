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


package org.dinopolis.gpstool.plugin.tracklayer;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

import org.dinopolis.gpstool.GpsylonKeyConstants;
import org.dinopolis.gpstool.event.TrackChangedEvent;
import org.dinopolis.gpstool.event.TrackChangedListener;
import org.dinopolis.gpstool.gui.util.BasicLayer;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.track.*;
import org.dinopolis.util.Debug;
import org.dinopolis.util.Resources;

import com.bbn.openmap.proj.Projection;



//----------------------------------------------------------------------
/**
 * This layer displays tracks stored in the trackmanager.
 * 
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class TrackLayer extends BasicLayer implements TrackChangedListener, PropertyChangeListener
{
  TrackManager track_manager_;
  List tracks_;
  Object tracks_lock_ = new Object();

  Resources resources_;
  BasicStroke loaded_track_line_stroke_;
  BasicStroke active_track_line_stroke_;
  Color loaded_track_color_;
  Color active_track_color_;
  boolean draw_trackpoints_;

      /** the minimum distance between two adjacent trackpoints. If
       * two trackpoints are closer than this value (x,y coordinates),
       * they are not painted */
  protected int min_distance_between_trackpoints_;

      // resource keys:
  public static final String KEY_TRACK_LOADED_TRACK_LINE_WIDTH = "track.loaded_track.line.width";
  public static final String KEY_TRACK_LOADED_TRACK_COLOR = "track.loaded_track.color";
  public static final String KEY_TRACK_ACTIVE_TRACK_LINE_WIDTH = "track.active_track.line.width";
  public static final String KEY_TRACK_ACTIVE_TRACK_COLOR = "track.active_track.color";
  public static final String KEY_TRACK_DISPLAY_MODE = "track.display_mode";
  public static final String KEY_TRACK_LAYER_ACTIVE = "track.layer_active";
  public static final String KEY_TRACK_MIN_DISTANCE_BETWEEN_TRACKPOINTS = "track.min_distance_between_trackpoints";
  public static final String KEY_TRACK_DRAW_MODE = "track.track_draw_mode";

  public static final String VALUE_TRACK_DRAW_MODE_LINEONLY = "line_only";
  public static final String VALUE_TRACK_DRAW_MODE_TRACKPOINT_LINE = "trackpoint_line";
  
//----------------------------------------------------------------------
/**
 * Constructor
 *
 * @param track_manager the track manager to ask for tracks.
 */
  public TrackLayer()
  {
    super();
  }

//----------------------------------------------------------------------
/**
 * Initialize with all it needs.
 * TODO: use my own resources
 *
 * @param support a plugin support object
 */
  public void initializePlugin(PluginSupport support)
  {
    track_manager_ = support.getTrackManager();
    resources_ = support.getResources();
    track_manager_.addTrackListener(this);
    resources_.addPropertyChangeListener(this);
        // set attributes for loaded tracks:
    loaded_track_color_ = resources_.getColor(KEY_TRACK_LOADED_TRACK_COLOR);
    loaded_track_line_stroke_ =
      new BasicStroke((float)resources_.getDouble(KEY_TRACK_LOADED_TRACK_LINE_WIDTH));
        // set attributes for active track:
    active_track_color_ = resources_.getColor(KEY_TRACK_ACTIVE_TRACK_COLOR);
    active_track_line_stroke_ =
      new BasicStroke((float)resources_.getDouble(KEY_TRACK_ACTIVE_TRACK_LINE_WIDTH));
    boolean active = resources_.getBoolean(KEY_TRACK_LAYER_ACTIVE);
    setActive(active);

    min_distance_between_trackpoints_ = resources_.getInt(KEY_TRACK_MIN_DISTANCE_BETWEEN_TRACKPOINTS);
    draw_trackpoints_ = !resources_.getString(KEY_TRACK_DRAW_MODE).equals(VALUE_TRACK_DRAW_MODE_LINEONLY);
  }

      //----------------------------------------------------------------------
/**
 * Paints the objects for this layer.
 *
 * @param g the graphics context.
 */
  public void paintComponent(Graphics g)
  {
    if(!isActive())
      return;

    if(Debug.DEBUG)
      Debug.println("trackplugin","in paintComponent");

    if(tracks_ == null)
      return;
    
    Vector tracks = null;
    synchronized(tracks_lock_)
    {
      tracks = new Vector(tracks_);
    }

    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    Iterator track_iterator = tracks.iterator();
    Track track;
    while(track_iterator.hasNext())
    {
      track = (Track)track_iterator.next();
      if(Debug.DEBUG)
        Debug.println("trackplugin_paint","painting track "+track.getIdentification());
      if(track.getIdentification().equals(resources_.getString(GpsylonKeyConstants.KEY_TRACK_ACTIVE_TRACK_IDENTIFIER)))
      {
        g2.setStroke(active_track_line_stroke_);
        g2.setColor(active_track_color_);
      }
      else
      {
        g2.setStroke(loaded_track_line_stroke_);
        g2.setColor(loaded_track_color_);
      }
      List trackpoints = track.getWaypoints();
      Iterator point_iterator = trackpoints.iterator();
      int start_x,start_y, end_x, end_y;
      Trackpoint trackpoint;

      int screen_width = getWidth();
      int screen_height = getHeight();

//      long start_time = System.currentTimeMillis();
          // goto first trackpoint:
      if(point_iterator.hasNext())
      {
        trackpoint = (Trackpoint)point_iterator.next();
        start_x = trackpoint.getX();
        start_y = trackpoint.getY();
        if(draw_trackpoints_)
          g2.drawRect(start_x-1,start_y-1,3,3);

            // draw the rest:
        while(point_iterator.hasNext())
        {
          trackpoint = (Trackpoint)point_iterator.next();
          end_x = trackpoint.getX();
          end_y = trackpoint.getY();

          if((end_x == 0) && (end_y == 0))
          {
            System.out.println("XXXXXXXXXXXXXXX TrackLayer: end point == 0/0");
            System.out.println("XXXXXXXXXXXXXXX Please inform the GPSylon developers!");
//            System.exit(1);
          }

              // only draw line, if the coordinates are different:
          if((Math.abs(end_x - start_x) > min_distance_between_trackpoints_)
             || (Math.abs(end_y - start_y) > min_distance_between_trackpoints_))
          {
                // finally draw the line:
            if(!trackpoint.isNewTrack())
            {
              g2.drawLine(start_x,start_y,end_x,end_y);
            }
//           g2.setColor(Color.green);
            if(draw_trackpoints_)
              g2.drawRect(end_x-1,end_y-1,3,3);
            
            if(Debug.DEBUG)
              Debug.println("trackplugin_paint","painting line: from"+start_x+"/"
                            +start_y+" to "+end_x+"/"+end_y);

                // last end is new start
            start_x = end_x;
            start_y = end_y;
          }
        }
      }
//      long end_time = System.currentTimeMillis();
//       System.out.println("painting track '"+track.getIdentification()
//                          +"' ("+track.size()+"  points): "+(end_time-start_time));
    }
  }

//----------------------------------------------------------------------
/**
 * This method is called from a background thread to recalulate the
 * screen coordinates of any geographical objects. This method must
 * store its objects and paint them in the paintComponent() method.
 */
  protected void doCalculation()
  {
    Projection projection = getProjection();
    if(projection == null)
      return;
    try
    {
      List tracks = track_manager_.getVisibleProjectedTracks(projection);
      if(Debug.DEBUG)
        Debug.println("trackplugin","doCalculation of tracks: "+tracks);
      setVisibleTracks(tracks);
    }
    catch(InterruptedException ignored){}
  }


//----------------------------------------------------------------------
/**
 * Sets the tracks to draw.
 *
 * @param tracks a list of Track objects.
 */
  protected void setVisibleTracks(List tracks)
  {
        // deep copy of tracks, as otherwise someone else could
        // modify my tracks without being able to notice:
    Iterator iterator = tracks.iterator();
    Vector new_tracks = new Vector();
    Track track = null;
    TrackImpl simplified_track;
    while(iterator.hasNext())
    {
//       track = (Track)iterator.next();
//       simplified_track = new TrackImpl();
//       simplified_track.setIdentification(track.getIdentification());
//       simplified_track.setComment(track.getComment());
//       Iterator point_iterator = track.getWaypoints().iterator();
//       Trackpoint point;
//       Trackpoint old_point = null;
//       while(point_iterator.hasNext())
//       {
//         point = (Trackpoint)point_iterator.next();
//             // only use next point, if the screen coordinates are
//             // different: BEWARE: this simplified track MUST NEVER be
//             // set in the TrackManager as then lots of information is
//             // LOST!
//         if((old_point == null)
//            || ((old_point.getX() != point.getX()) && (old_point.getY() != point.getY())))
//         {
//           simplified_track.addWaypoint(new TrackpointImpl(point));
//           old_point = point;
//         }
//       }
      track = (Track)iterator.next();
//      System.out.println("Cloning track "+track.getIdentification()+" size="+track.size());
      new_tracks.add(new TrackImpl(track));
//      System.out.println("End of Cloning tracks");
//      new_tracks.add(simplified_track);
//       System.out.println("simplified track '"+track.getIdentification()
//                          +"' from "+track.size()+" to "+simplified_track.size()+" points.");
    }
    synchronized(tracks_lock_)
    {
      tracks_ = new_tracks;
    }
    if(Debug.DEBUG)
      Debug.println("trackplugin","visible tracks are "+tracks_);
  }

//----------------------------------------------------------------------
/**
 * Called when a track is added or removed.
 *
 * @param event the event
 */
  public void trackChanged(TrackChangedEvent event)
  {
    recalculateCoordinates();
  }


//----------------------------------------------------------------------
/**
 * Callback method for property change events (Postion, altitude, ...)
 * 
 * @param event the property change event.
 */

  public void propertyChange(PropertyChangeEvent event)
  {
    String name = event.getPropertyName();
    if(Debug.DEBUG)
      Debug.println("tracklayer_properties","PropertyChangeEvent "+name);
    if(name.equals(KEY_TRACK_LOADED_TRACK_COLOR))
    {
      loaded_track_color_ = resources_.getColor(KEY_TRACK_LOADED_TRACK_COLOR);
      repaint();
      return;
    }
    if(name.equals(KEY_TRACK_ACTIVE_TRACK_COLOR))
    {
      active_track_color_ = resources_.getColor(KEY_TRACK_ACTIVE_TRACK_COLOR);
      repaint();
      return;
    }
    if(name.equals(KEY_TRACK_MIN_DISTANCE_BETWEEN_TRACKPOINTS))
    {
      min_distance_between_trackpoints_ = resources_.getInt(KEY_TRACK_MIN_DISTANCE_BETWEEN_TRACKPOINTS);
      repaint();
      return;
    }
    if(name.equals(KEY_TRACK_DRAW_MODE))
    {
      draw_trackpoints_ = !resources_.getString(KEY_TRACK_DRAW_MODE).equals(VALUE_TRACK_DRAW_MODE_LINEONLY);
      repaint();
      return;
    }
  }

}


