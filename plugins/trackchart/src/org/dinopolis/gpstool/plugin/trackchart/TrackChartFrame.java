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


package org.dinopolis.gpstool.plugin.trackchart;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.dinopolis.gpstool.GPSMapKeyConstants;
import org.dinopolis.gpstool.TrackManager;
import org.dinopolis.gpstool.gui.util.TrackListComboBoxModel;
import org.dinopolis.gpstool.track.Track;
import org.dinopolis.gpstool.track.Trackpoint;
import org.dinopolis.gpstool.util.GeoMath;
import org.dinopolis.util.Resources;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;



//----------------------------------------------------------------------
/**
 * 
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class TrackChartFrame extends JFrame implements ActionListener
{
  protected TrackManager track_manager_;
  protected ChartPanel chart_panel_;
  protected Resources application_resources_ ;
  protected Resources plugin_resources_ ;
  protected JComboBox track_box_;

  public TrackChartFrame(String title)
  {
    super(title);
  }

  public void initialize(Resources application_resources,
                         Resources plugin_resources,
                         TrackManager track_manager)
  {
    track_manager_ = track_manager;
    application_resources_ = application_resources;
    plugin_resources_ = plugin_resources;

    List tracks = track_manager_.getTracks();
    if(tracks.size() > 0)
    {
      Track default_track = (Track)track_manager_.getTracks().get(0);
      chart_panel_ = new ChartPanel(getTrackChart(default_track));
      chart_panel_.setPreferredSize(new Dimension(500,270));
      chart_panel_.setMouseZoomable(true,false);

      JPanel north_panel = new JPanel(new FlowLayout());
      north_panel.add(new JLabel(application_resources_.getString(GPSMapKeyConstants.KEY_LOCALIZE_TRACK)),
                      BorderLayout.NORTH);
      track_box_ = new JComboBox(new TrackListComboBoxModel(track_manager));
      track_box_.addActionListener(this);
      track_box_.setSelectedItem(default_track.getIdentification());
      north_panel.add(track_box_);
      getContentPane().add(north_panel,BorderLayout.NORTH);
      getContentPane().add(chart_panel_,BorderLayout.CENTER);
    }
    else
    {
      getContentPane().add(new JLabel("Sorry, no tracks available"));
    }
    pack();
  }


  protected JFreeChart getTrackChart(Track track)
  {
        //    System.out.println("create chart for track "+track.getIdentification());
    XYSeries xy_series = new XYSeries(track.getIdentification());
    List waypoints = track.getWaypoints();
    Iterator waypoint_iterator = waypoints.iterator();
    Trackpoint last_trackpoint = null;
    Trackpoint trackpoint;
    if(waypoint_iterator.hasNext())
    {
      last_trackpoint = (Trackpoint)waypoint_iterator.next(); // start trackpoint
      xy_series.add(0,last_trackpoint.getAltitude());
    
      while(waypoint_iterator.hasNext())
      {
        trackpoint = (Trackpoint)waypoint_iterator.next();
            // calculate distance from last trackpoint (in km)
        double distance = GeoMath.distance(last_trackpoint.getLatitude(), last_trackpoint.getLongitude(),
                                           trackpoint.getLatitude(),trackpoint.getLongitude())/1000.0;
        xy_series.add(distance,trackpoint.getAltitude());
      }
    }
      
    XYSeriesCollection data = new XYSeriesCollection(xy_series);
    String chart_title = application_resources_.getString(GPSMapKeyConstants.KEY_LOCALIZE_TRACK)
                         + " '" + track.getIdentification() +"' - "+
                         application_resources_.getString(GPSMapKeyConstants.KEY_LOCALIZE_ALTITUDE);
    JFreeChart chart =
      ChartFactory.createLineXYChart(chart_title,
                                     application_resources_.getString(GPSMapKeyConstants.KEY_LOCALIZE_DISTANCE),
                                     application_resources_.getString(GPSMapKeyConstants.KEY_LOCALIZE_ALTITUDE),
                                     data, 
                                     true,
                                     true,
                                     false);
    return(chart);
  }

//----------------------------------------------------------------------
/**
 * Called on changes in the checkbox or other gui events.
 *
 * @param event the event
 */
  public void actionPerformed(ActionEvent event)
  {
    if(event.getSource() == track_box_)
    {
      String track_id = (String)track_box_.getSelectedItem();
      Track track = track_manager_.getTrack(track_id);
      JFreeChart chart = getTrackChart(track);
      chart_panel_.setChart(chart);
    }
  }
}

