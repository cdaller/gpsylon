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

package org.dinopolis.gpstool.gpsinput;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import org.dinopolis.util.Debug;

//----------------------------------------------------------------------
/**
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GPSInfoPanel extends JPanel implements PropertyChangeListener
{
  JLabel latitude_label_;
  JLabel longitude_label_;
  JLabel speed_label_;
  JLabel altitude_label_;
  JLabel heading_label_;
  
  
//----------------------------------------------------------------------
/**
 * Constructor
 *
 */

  public GPSInfoPanel()
  {
    super();
    
    setLayout(new GridLayout(5,2));

    add(new JLabel("Latitude"));
    add(latitude_label_ = new JLabel());
    add(new JLabel("Longitude"));
    add(longitude_label_ = new JLabel());
    add(new JLabel("Altitude"));
    add(altitude_label_ = new JLabel());
    add(new JLabel("Heading"));
    add(heading_label_ = new JLabel());
    add(new JLabel("Speed"));
    add(speed_label_ = new JLabel());
  }

  public void propertyChange(PropertyChangeEvent event)
  {
    String name = event.getPropertyName();
    Object value = event.getNewValue();
    if(name.equals(GPSDataProcessor.SATELLITE_INFO))
    {
      SatelliteInfo[] infos = (SatelliteInfo[])value;
      SatelliteInfo info;
      for(int count=0; count < infos.length; count++)
      {
	info = infos[count];
	System.out.println("sat "+info.getPRN()+": elev="+info.getElevation()
			   + " azim="+info.getAzimuth()+" dB="+info.getSNR());
      }
    }
    else
    {
      if(name.equals(GPSDataProcessor.LOCATION))
      {
	GPSPosition pos = (GPSPosition)value;
	latitude_label_.setText(String.valueOf(pos.getLatitude()));
	longitude_label_.setText(String.valueOf(pos.getLongitude()));
      }
      else
      if(name.equals(GPSDataProcessor.ALTITUDE))
        altitude_label_.setText(value.toString());
      else
      if(name.equals(GPSDataProcessor.SPEED))
	speed_label_.setText(value.toString());
      else
      if(name.equals(GPSDataProcessor.HEADING))
	heading_label_.setText(value.toString());
    }
  }
}




