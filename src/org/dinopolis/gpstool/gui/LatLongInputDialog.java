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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.dinopolis.gpstool.GPSMapKeyConstants;
import org.dinopolis.gpstool.gui.layer.location.CategoryCellRenderer;
import org.dinopolis.gpstool.gui.layer.location.LocationMarker;
import org.dinopolis.gpstool.gui.layer.location.LocationMarkerCategory;
import org.dinopolis.gpstool.gui.util.AngleJTextField;
import org.dinopolis.gpstool.util.angle.Angle;
import org.dinopolis.gpstool.util.angle.AngleFormat;
import org.dinopolis.gpstool.util.angle.Latitude;
import org.dinopolis.gpstool.util.angle.Longitude;
import org.dinopolis.util.Debug;
import org.dinopolis.util.Resources;

//----------------------------------------------------------------------
/**
 * A dialog that allows to enter latitude, longitude and has an OK and
 * a cancel button.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class LatLongInputDialog extends JDialog implements GPSMapKeyConstants
{

  Resources resources_;

  AngleJTextField latitude_text_;
  AngleJTextField longitude_text_;
  JLabel info_text_;
  AngleFormat latlon_formatter_;

  public static final String COMMAND_OK = "ok";
  public static final String COMMAND_CANCEL = "cancel";

//----------------------------------------------------------------------
/**
 * Constructor. This dialog has no title set. So the title has to be
 * set from external.
 *
 * @param resources the resources to user for titles, button titles,
 * etc.
 * @param action_listener the action listener to inform on a button
 * press. The commands used for the buttons are COMMAND_OK or
 * COMMAND_Cancel. All button presses must be handled from outside. No
 * default behaviour is implemented (closing the frame, etc.).
 * @param owner the owner of the frame.
 */

  public LatLongInputDialog(Resources resources, ActionListener action_listener, Frame owner)
  {
    super(owner,false);
    resources_ = resources;
    
    try
    {
      latlon_formatter_ = new AngleFormat(resources_.getString(KEY_ANGLE_FORMAT_LATLON));
    }
    catch(IllegalArgumentException e)
    {
      System.err.println("Illegal format for latitude/longitude: "+e.getMessage());
    }

    
    Container content_pane = getContentPane();

        // the panel with the input fields and the buttons
    JPanel input_panel = new JPanel(new BorderLayout());
    
    JPanel center_panel = new JPanel();
    input_panel.add(center_panel,BorderLayout.CENTER);
    JPanel south_panel = new JPanel();
    input_panel.add(south_panel,BorderLayout.SOUTH);
    
    center_panel.setLayout(new GridLayout(2,2));

    center_panel.add(new JLabel(resources_.getString(KEY_LOCALIZE_LATITUDE)));
    center_panel.add(latitude_text_ = new AngleJTextField());
    center_panel.add(new JLabel(resources_.getString(KEY_LOCALIZE_LONGITUDE)));
    center_panel.add(longitude_text_ = new AngleJTextField());

        // set valid angle formats for latitude and longitude:
    latitude_text_.clearValidAngleFormats();
    latitude_text_.addValidAngleFormats(resources_.getStringArray(KEY_ANGLE_FORMAT_VALID_FORMATS));
    longitude_text_.clearValidAngleFormats();
    longitude_text_.addValidAngleFormats(resources_.getStringArray(KEY_ANGLE_FORMAT_VALID_FORMATS));

    JButton ok_button = new JButton(resources_.getString(KEY_LOCALIZE_OK_BUTTON));
    ok_button.setActionCommand(COMMAND_OK);
    ok_button.addActionListener(action_listener);
    
    JButton cancel_button = new JButton(resources_.getString(KEY_LOCALIZE_CANCEL_BUTTON));
    cancel_button.setActionCommand(COMMAND_CANCEL);
    cancel_button.addActionListener(action_listener);
    
    south_panel.add(ok_button);
    south_panel.add(cancel_button);

    content_pane.add(input_panel,BorderLayout.CENTER);
        // info label:
    content_pane.add(info_text_ = new JLabel(),BorderLayout.SOUTH);

    pack();

    setSize(300,(int)getSize().getHeight());

//    updatePreviewRectangle();
    
    addWindowListener(new WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
//          preview_hook_.hidePreviewMaps();
          setVisible(false);
        }
      });
  }


//----------------------------------------------------------------------
/**
 * Sets the string shown in the info field.
 * 
 * @param info the info to be shown.
 */
  public void setInfo(String info)
  {
    info_text_.setText(info);
  }


//----------------------------------------------------------------------
/**
 * Checks the validity of the user input. If any of the user's input
 * is not valid, a Dialog is opened to inform the user about the
 * incorrect data and <code>false</code> is returned.
 * 
 * @return false if any of the users input is invalid.
 */
  public boolean checkValidity()
  {
    if((latitude_text_.getAngle() == null) || (longitude_text_.getAngle() == null))
    {
      JOptionPane.showMessageDialog(this,
                                    resources_.getString(KEY_LOCALIZE_MESSAGE_LATITUDE_OR_LONGITUDE_WRONG_FORMAT),
                                    resources_.getString(KEY_LOCALIZE_MESSAGE_ERROR_TITLE),
                                    JOptionPane.ERROR_MESSAGE);
      return(false);
    }
    return(true);
  }
  
//----------------------------------------------------------------------
/**
 * Sets new coordinates
 * 
 * @param latitude
 * @param longitude
 */
  public void setCoordinates(double latitude, double longitude)
  {
    latitude_text_.setText(latlon_formatter_.format(new Latitude(latitude)));
    longitude_text_.setText(latlon_formatter_.format(new Longitude(longitude)));
  }

//----------------------------------------------------------------------
/**
 * Returns the latitude setup in the dialog or Double.NaN if no valid
 * latitude was entered by the user.
 *
 * @return the latitude setup in the dialog or Double.NaN if no valid
 * latitude was entered by the user.
 */
  public double getLatitude()
  {
    Angle angle = latitude_text_.getAngle();
    if(angle == null)
      return(Double.NaN);
    else
      return(angle.degrees());
  }

//----------------------------------------------------------------------
/**
 * Returns the longitude setup in the dialog or Double.NaN if no valid
 * latitude was entered by the user.
 *
 * @return the longitude setup in the dialog or Double.NaN if no valid
 * latitude was entered by the user.
 */
  public double getLongitude()
  {
    Angle angle = longitude_text_.getAngle();
    if(angle == null)
      return(Double.NaN);
    else
      return(angle.degrees());
  }
}




