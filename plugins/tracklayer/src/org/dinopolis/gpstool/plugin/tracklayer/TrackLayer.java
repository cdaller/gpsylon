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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.dinopolis.gpstool.GPSMapKeyConstants;
import org.dinopolis.gpstool.TrackManager;
import org.dinopolis.gpstool.event.TrackChangedEvent;
import org.dinopolis.gpstool.event.TrackChangedListener;
import org.dinopolis.gpstool.gui.util.BasicLayer;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.track.Track;
import org.dinopolis.gpstool.track.Trackpoint;
import org.dinopolis.util.Debug;
import org.dinopolis.util.Resources;



//----------------------------------------------------------------------
/**
 * This layer displays tracks stored in the trackmanager.
 * 
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class TrackLayer extends BasicLayer implements TrackChangedListener
{
  TrackManager track_manager_;
  List tracks_;
  Object tracks_lock_ = new Object();

  Resources resources_;
  BasicStroke loaded_track_line_stroke_;
  BasicStroke current_track_line_stroke_;
  Color loaded_track_color_;
  Color current_track_color_;


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
    loaded_track_color_ = resources_.getColor(GPSMapKeyConstants.KEY_TRACK_LOADED_TRACK_COLOR);
    loaded_track_line_stroke_ =
      new BasicStroke((float)resources_.getDouble(GPSMapKeyConstants.KEY_TRACK_LOADED_TRACK_LINE_WIDTH));
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

    g2.setStroke(loaded_track_line_stroke_);
    g2.setColor(loaded_track_color_);

    Iterator track_iterator = tracks.iterator();
    Track track;
    while(track_iterator.hasNext())
    {
      track = (Track)track_iterator.next();
      if(Debug.DEBUG)
        Debug.println("trackplugin_paint","painting track "+track.getIdentification());
      List trackpoints = track.getWaypoints();
      Iterator point_iterator = trackpoints.iterator();
      int start_x,start_y, end_x, end_y;
      Trackpoint trackpoint;
      
          // goto first trackpoint:
      if(point_iterator.hasNext())
      {
        trackpoint = (Trackpoint)point_iterator.next();
        start_x = trackpoint.getX();
        start_y = trackpoint.getY();

            // draw the rest:
        while(point_iterator.hasNext())
        {
          trackpoint = (Trackpoint)point_iterator.next();
          end_x = trackpoint.getX();
          end_y = trackpoint.getY();
          
              // finally draw the line:
          g2.drawLine(start_x,start_y,end_x,end_y);
          if(Debug.DEBUG)
            Debug.println("trackplugin_paint","painting line: "+start_x+","+start_y+"/"+end_x+"."+end_y);
          
              // last end is new start
          start_x = end_x;
          start_y = end_y;
        }
      }
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
    List tracks = track_manager_.getVisibleProjectedTracks(getProjection());
    if(Debug.DEBUG)
      Debug.println("trackplugin","doCalculation of tracks: "+tracks);
    setVisibleTracks(tracks);
  }


//----------------------------------------------------------------------
/**
 * Sets the tracks to draw.
 *
 * @param tracks a list of Track objects.
 */
  protected void setVisibleTracks(List tracks)
  {
    synchronized(tracks_lock_)
    {
      tracks_ = tracks;
      if(Debug.DEBUG)
        Debug.println("trackplugin","visible tracks are "+tracks_);
    }
  }

//----------------------------------------------------------------------
/**
 * Called when a track is added or removed.
 *
 * @param event the event
 */
  public void trackChanged(TrackChangedEvent event)
  {
    
  }
}


