/***********************************************************************
 * @(#)$RCSfile$   $Revision$ $Date$
 *
 * Copyright (c) 2001 IICM, Graz University of Technology
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

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.event.LayerStatusEvent;
import com.bbn.openmap.event.LayerStatusListener;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent; 
import java.beans.PropertyChangeListener; 
import java.text.DecimalFormat;
import java.util.HashSet;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JToolBar;
import org.dinopolis.gpstool.GPSMap;
import org.dinopolis.gpstool.GPSMapKeyConstants;
import org.dinopolis.gpstool.MapNavigationHook;
import org.dinopolis.gpstool.gpsinput.GPSPositionError;
import org.dinopolis.gpstool.gpsinput.SatelliteInfo;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.util.UnitHelper;
import org.dinopolis.util.Resources;

//----------------------------------------------------------------------
/**
 * This Panel represents the status bar
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class StatusBar extends JToolBar implements PropertyChangeListener, ActionListener,
                   GPSMapKeyConstants, LayerStatusListener
{
  protected JButton latitude_;
  protected JButton longitude_;
  protected JButton distance_;
  protected JButton speed_;
  protected JLabel status_;
  protected JLabel heading_;
//  protected JComboBox scale_box_;
  protected SatelliteActivity satellite_activity_;
  
  protected Resources resources_;
  protected MapNavigationHook map_navigation_hook_;
  protected UnitHelper unit_helper_;
  double latitude_value_;
  double longitude_value_;
  float heading_value_;
  float distance_value_;
  float speed_value_;
  JProgressBar paint_progress_;
  HashSet layers_ = new HashSet();

//   String[] available_scales_ = {"1000","2000","5000","10000","25000","50000","100000","200000",
//                                 "500000","1000000","2000000","5000000","10000000","50000000"};
//   static final String DEFAULT_SCALE = "50000";

//   static final String COMMAND_SCALE = "scale";

  
  public StatusBar(PluginSupport plugin_support)
  {
    super();
    
    resources_ = plugin_support.getResources();
    map_navigation_hook_ = plugin_support.getMapNavigationHook();
    unit_helper_ = plugin_support.getUnitHelper();
    
    latitude_ = new JButton("latitude");
    longitude_ = new JButton("longitude");
    heading_ = new JLabel("heading");
    speed_ = new JButton("speed");
    distance_ = new JButton("distance");
    status_ = new JLabel("status");
//     scale_box_ = new JComboBox(available_scales_);
//     scale_box_.setSelectedItem(DEFAULT_SCALE);
//     scale_box_.setActionCommand(COMMAND_SCALE);
//     scale_box_.addActionListener(this);
    paint_progress_ = new JProgressBar();
//     paint_progress_.setString("calc");
//     paint_progress_.setStringPainted(true);
    paint_progress_.setMinimum(0);
    paint_progress_.setMaximum(0);  //number of layers is set automagically in updateLayerStatus()
    
        // set preferred width to 100:
    Dimension dim = paint_progress_.getPreferredSize();
    dim.setSize(100f,dim.getHeight());
    paint_progress_.setPreferredSize(dim);

    satellite_activity_ = new SatelliteActivity(6,2);  // 6 columns, 2 rows

    add(satellite_activity_);
    add(latitude_);
    add(longitude_);
    add(heading_);
    add(speed_);
    add(distance_);
    add(status_);
//     add(scale_box_);
    add(paint_progress_);
  }

  public void setLatitude(double latitude)
  {
    latitude_value_ = latitude;
    latitude_.setText("lat: "+unit_helper_.formatLatitude(latitude));
  }
    
  public void setLongitude(double longitude)
  {
    longitude_value_ = longitude;
    longitude_.setText("lon: "+ unit_helper_.formatLongitude(longitude));
  }
    
  public void setDistance(float distance)
  {
    distance_value_ = distance;
    distance_.setText(unit_helper_.formatDistance(distance));
  }
    
  public void setSpeed(float speed)
  {
    speed_value_ = speed;
    speed_.setText(unit_helper_.formatSpeed(speed));
  }
    
  public void setStatus(String status_message)
  {
    status_.setText(status_message);
  }

  public void setHeading(float heading)
  {
    heading_value_ = heading;
    heading_.setText("Dir: "+unit_helper_.formatHeading(heading));
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
    if(event.getPropertyName().equals(GPSMap.PROPERTY_KEY_GPS_LOCATION))
    {
      LatLonPoint tmp_point = (LatLonPoint)event.getNewValue();
      if(tmp_point != null)
      {
        setLatitude(tmp_point.getLatitude());
        setLongitude(tmp_point.getLongitude());
      }
      return;
    }
    
    if(event.getPropertyName().equals(GPSMap.PROPERTY_KEY_CURRENT_HEADING))
    {
      Float tmp_heading = (Float)event.getNewValue();
      if(tmp_heading != null)
      {
        setHeading(tmp_heading.floatValue());
      }
      return;
    }

    if(event.getPropertyName().equals(GPSMap.PROPERTY_KEY_CURRENT_SPEED))
    {
      Float tmp_speed = (Float)event.getNewValue();
      if(tmp_speed != null)
      {
        setSpeed(tmp_speed.floatValue());
      }
      return;
    }

    if(event.getPropertyName().equals(GPSMap.PROPERTY_KEY_TOTAL_DISTANCE))
    {
      Float tmp_distance = (Float)event.getNewValue();
      if(tmp_distance != null)
      {
        setDistance(tmp_distance.floatValue());
      }
      return;
    }

    if(event.getPropertyName().equals(GPSMap.PROPERTY_KEY_ROUTE_DESTINATION_DISTANCE))
    {
      Float tmp_distance = (Float)event.getNewValue();
      if(tmp_distance != null)
      {
        setDistance(tmp_distance.floatValue());
      }
      return;
    }

    if(event.getPropertyName().equals(GPSMap.PROPERTY_KEY_GPS_SATELLITE_INFO))
    {
      satellite_activity_.setSatelliteInfos((SatelliteInfo[])event.getNewValue());
      return;
    }

    if(event.getPropertyName().equals(GPSMap.PROPERTY_KEY_GPS_POS_ERROR))
    {
      satellite_activity_.setPositionError((GPSPositionError)event.getNewValue());
      return;
    }

  }


//----------------------------------------------------------------------
/**
 * Callback method for action performed events.
 * 
 * @param event the event.
 */
  public void actionPerformed(ActionEvent event)
  {
//     if(event.getActionCommand().equals(COMMAND_SCALE))
//     {
//       try
//       {
//         String scale_string = (String)scale_box_.getSelectedItem();
//         float scale = Float.parseFloat(scale_string);
//         map_navigation_hook_.setScale(scale);
//       }
//       catch(NumberFormatException nfe)
//       {
//             // shouldnt happen!
//         nfe.printStackTrace();
//       }
//     }
  }


//----------------------------------------------------------------------
/**
 * Callback method for layer status events
 * 
 * @param event the event.
 */
  public void updateLayerStatus(LayerStatusEvent event)
  {
        // adopt value of progress bar automatically to number of layers sending events:
    if(!layers_.contains(event.getLayer()))
    {
      layers_.add(event.getLayer());
      paint_progress_.setMaximum(paint_progress_.getMaximum() + 1);
    }
    
    if(event.getStatus() == LayerStatusEvent.START_WORKING)
    {
      paint_progress_.setValue(paint_progress_.getValue()-1);
      return;
    }
    if(event.getStatus() == LayerStatusEvent.FINISH_WORKING)
    {
      paint_progress_.setValue(paint_progress_.getValue()+1);
      return;
    }
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
    return(false);
  }



//----------------------------------------------------------------------
// Inner Classes:
//----------------------------------------------------------------------



//----------------------------------------------------------------------
/**
 * Info button is used to display information. Therefore it might not
 * get the focus. Otherwise it acts like a normal JButton.
 *
 */
  
  class InfoButton extends JButton 
  {
    public InfoButton()
    {
      super();
    }


    public InfoButton(Action a)
    {
      super(a);
    }

    public InfoButton(Icon icon)
    {
      super(icon);
    }

    public InfoButton(String text)
    {
      super(text);
    }

    public InfoButton(String text, Icon icon)
    {
      super(text,icon);
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
//     public boolean isFocusTraversable()
//     {
//       return(isFocusable());
//     }


//----------------------------------------------------------------------
/**
 * Returns whether this Component can become the focus owner.
 *
 * @return true if this Component is focusable; false otherwise.
 */
    public boolean isFocusable()
    {
      return(false);
    }
  }


}

