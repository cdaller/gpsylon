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

package org.dinopolis.gpstool.gui;

import org.dinopolis.util.Debug;
import org.dinopolis.util.Resources;

import org.dinopolis.gpstool.GPSMapKeyConstants;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import org.dinopolis.gpstool.GPSMap;

import org.dinopolis.gpstool.gpsinput.GPSDataProcessor;
import org.dinopolis.gpstool.gpsinput.GPSRawDataListener;

//----------------------------------------------------------------------
/**
 * This class can be used as a viewer for raw gps data. It is a frame
 * that holds a textarea that shows all incoming raw gps data events
 * from the given gps data processor. It has an "clear" and a "close"
 * button, nothing else (TODO: save the data to a file, give a message
 * if no data is received within a couple of seconds, ...)
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class NmeaDataTextFrame extends JFrame
  implements ActionListener, GPSMapKeyConstants, GPSRawDataListener
{

  Resources resources_;
  JTextArea text_area_;
  GPSDataProcessor gps_data_processor_;
  
  public static final String COMMAND_CLOSE = "close";
  public static final String COMMAND_CLEAR = "clear";

  
//----------------------------------------------------------------------
/**
 * Constructor using the current map position as download position
 *
 * @param resources the resources
 * @param map_manager the hook to add new maps to
 * @param preview_hook the PreviewHook able to display a preview for
 * the map to download.
 */

  public NmeaDataTextFrame(Resources resources, GPSDataProcessor gps_data_processor)
  {
    super();
    resources_ = resources;
    gps_data_processor_ = gps_data_processor;
    
    setTitle(resources_.getString(KEY_LOCALIZE_RAWGPSDATAFRAME_TITLE));

    Container content_pane = getContentPane();

    JPanel south_panel = new JPanel();

    text_area_ = new JTextArea(25,50);
    JScrollPane scroll_pane = new JScrollPane(text_area_);
    content_pane.add(scroll_pane,BorderLayout.CENTER);
    
    JButton close_button = new JButton(resources_.getString(KEY_LOCALIZE_CLOSE_BUTTON));
    close_button.setActionCommand(COMMAND_CLOSE);
    close_button.addActionListener(this);
    south_panel.add(close_button);
    
    JButton clear_button = new JButton(resources_.getString(KEY_LOCALIZE_CLEAR_BUTTON));
    clear_button.setActionCommand(COMMAND_CLEAR);
    clear_button.addActionListener(this);
    south_panel.add(clear_button);

    content_pane.add(south_panel,BorderLayout.SOUTH);

    text_area_.setEditable(false);

    gps_data_processor_.addGPSRawDataListener(this);

    pack();

    addWindowListener(new WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
          gps_data_processor_.removeGPSRawDataListener(NmeaDataTextFrame.this);
          setVisible(false);
          dispose();
        }
      });
  }


//----------------------------------------------------------------------
/**
 * Informs a GPSRawDataListener about received raw data from a
 * gps device.
 *
 * @param raw_data the raw data received from the gps device.
 * @param offset the start of the relevant data
 * @param length the length of the data
 */
  public void gpsRawDataReceived(char[] raw_data, int offset, int length)
  {
    text_area_.append(new String(raw_data,offset,length));
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
      gps_data_processor_.removeGPSRawDataListener(this);
      dispose();
      setVisible(false);
      return;
    }

    if(event.getActionCommand().equals(COMMAND_CLEAR))
    {
      text_area_.setText("");
      return;
    }
  }
}




