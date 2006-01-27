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

package org.dinopolis.gpstool.plugin.averagepos;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import org.dinopolis.gpstool.Gpsylon;
import org.dinopolis.gpstool.GpsylonKeyConstants;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.util.UnitHelper;
import org.dinopolis.util.Debug;
import org.dinopolis.util.Resources;

import com.bbn.openmap.LatLonPoint;



//----------------------------------------------------------------------
/**
 * This plugin allows to manage the available maps. It shows all
 * available maps on a layer it provides, it provides a table that
 * lists all available maps and allows to edit them, and it provides a
 * mouse mode, that lets the user interact with the map manager by
 * clicking on the map component.
 * 
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class AveragePosFrame extends JFrame implements PropertyChangeListener, ActionListener
{

  public static final String COMMAND_CLOSE = "close";
  public static final String COMMAND_CLEAR = "clear";

  Resources resources_;
  AveragePosComponent average_pos_component_;
  PluginSupport support_;
  JLabel latitude_label_;
  JLabel longitude_label_;
  UnitHelper unit_helper_;

  public AveragePosFrame()
  {
  }


  public void initialize(PluginSupport support)
  {
    support_ = support;
    support_.getPropertyChangeSupport().addPropertyChangeListener(this);
    resources_ = support_.getResources();
    unit_helper_ = support_.getUnitHelper();
    
    Container content_pane = getContentPane();

    JPanel south_panel = new JPanel();

    average_pos_component_ = new AveragePosComponent();
    content_pane.add(average_pos_component_,BorderLayout.CENTER);
    
    JButton close_button = new JButton(resources_.getString(GpsylonKeyConstants.KEY_LOCALIZE_CLOSE_BUTTON));
    close_button.setActionCommand(COMMAND_CLOSE);
    close_button.addActionListener(this);
    south_panel.add(close_button);
    
    JButton clear_button = new JButton(resources_.getString(GpsylonKeyConstants.KEY_LOCALIZE_CLEAR_BUTTON));
    clear_button.setActionCommand(COMMAND_CLEAR);
    clear_button.addActionListener(this);
    south_panel.add(clear_button);

    content_pane.add(south_panel,BorderLayout.SOUTH);

    JPanel north_panel = new JPanel();
    latitude_label_ = new JLabel();
    longitude_label_ = new JLabel();
    north_panel.add(latitude_label_);
    north_panel.add(longitude_label_);
    content_pane.add(north_panel,BorderLayout.NORTH);
    
    pack();

    addWindowListener(new WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
          close();
        }
      });
  }


//----------------------------------------------------------------------
/**
 * Close window and remove the property listener.
 * 
 */
  protected void close()
  {
    support_.getPropertyChangeSupport().removePropertyChangeListener(this);
    setVisible(false);
    dispose();
  }

//----------------------------------------------------------------------
/**
 * Action Listener Method
 * 
 * @param event the action event
 */

  public void actionPerformed(ActionEvent event)
  {
    if(event.getActionCommand().equals(COMMAND_CLOSE))
    {
      close();
      return;
    }

    if(event.getActionCommand().equals(COMMAND_CLEAR))
    {
      average_pos_component_.clear();
      return;
    }
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
    if(name.equals(Gpsylon.PROPERTY_KEY_GPS_ALTITUDE))
    {
      Float altitude = (Float)event.getNewValue();
      if(altitude != null)
        average_pos_component_.addAltitude(altitude.floatValue());
//       if(altitude != null)
//         current_altitude_ = altitude.floatValue();
    }
    if(name.equals(Gpsylon.PROPERTY_KEY_GPS_LOCATION))
    {
      LatLonPoint tmp_point = (LatLonPoint)event.getNewValue();
//       System.out.println("TrackLayer, propertyChange: old: "+event.getOldValue()+" new:"+
// 			 event.getNewValue());
      if(tmp_point != null)
      {
        if(Debug.DEBUG)
          Debug.println("activeposframe","new position in tracklayer: "+tmp_point);
        average_pos_component_.addPosition(tmp_point);
	LatLonPoint avg_pos = average_pos_component_.getAveragePosition();
	latitude_label_.setText("lat: "+unit_helper_.formatLatitude(avg_pos.getLatitude()));
	longitude_label_.setText("lon: "+unit_helper_.formatLongitude(avg_pos.getLongitude()));
      }
      return;
    }
  }
}
