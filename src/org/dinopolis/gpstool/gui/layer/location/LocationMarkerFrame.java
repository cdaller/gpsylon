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

package org.dinopolis.gpstool.gui.layer.location;

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
 * A dialog that allows to enter a name, coordinates and a category
 * for a location marker.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class LocationMarkerFrame extends JDialog implements GPSMapKeyConstants
{

  Resources resources_;

  AngleJTextField latitude_text_;
  AngleJTextField longitude_text_;
  JLabel info_text_;
  JTextField name_textfield_;

  JComboBox category_box_;
  
  AngleFormat latlon_formatter_;

  public static final String COMMAND_OK = "ok";
  public static final String COMMAND_CLOSE = "close";

//----------------------------------------------------------------------
/**
 * Constructor.
 *
 * @param resources the resources to user for titles, button titles,
 * etc.
 * @param action_listener the action listener to inform on a button
 * press. The commands used for the buttons are COMMAND_OK or
 * COMMAND_CLOSE. All button presses must be handled from outside. No
 * default behaviour is implemented (closing the frame, etc.).
 * @param owner the owner of the frame.
 */

  public LocationMarkerFrame(Resources resources, ActionListener action_listener, Frame owner)
  {
    super(owner,resources.getString(KEY_LOCALIZE_NEW_LOCATION_MARKER_TITLE),false);
    resources_ = resources;
    
//    setTitle(resources_.getString(KEY_LOCALIZE_NEW_LOCATION_MARKER_TITLE));

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
    
    center_panel.setLayout(new GridLayout(4,2));

    center_panel.add(new JLabel(resources_.getString(KEY_LOCALIZE_NAME)));
    center_panel.add(name_textfield_ = new JTextField());
    center_panel.add(new JLabel(resources_.getString(KEY_LOCALIZE_CATEGORY)));
    category_box_ = new JComboBox(LocationMarkerCategory.getCategories(resources_));
    center_panel.add(category_box_);
    category_box_.setRenderer(new CategoryCellRenderer());
    center_panel.add(new JLabel(resources_.getString(KEY_LOCALIZE_LATITUDE)));
//     center_panel.add(latitude_text_ = new JLabel());
    center_panel.add(latitude_text_ = new AngleJTextField());
    center_panel.add(new JLabel(resources_.getString(KEY_LOCALIZE_LONGITUDE)));
//    center_panel.add(longitude_text_ = new JLabel());
    center_panel.add(longitude_text_ = new AngleJTextField());

        // set valid angle formats for latitude and longitude:
    latitude_text_.clearValidAngleFormats();
    latitude_text_.addValidAngleFormats(resources_.getStringArray(KEY_ANGLE_FORMAT_VALID_FORMATS));
    longitude_text_.clearValidAngleFormats();
    longitude_text_.addValidAngleFormats(resources_.getStringArray(KEY_ANGLE_FORMAT_VALID_FORMATS));

    JButton ok_button = new JButton(resources_.getString(KEY_LOCALIZE_OK_BUTTON));
    ok_button.setActionCommand(COMMAND_OK);
    ok_button.addActionListener(action_listener);
    
    JButton close_button = new JButton(resources_.getString(KEY_LOCALIZE_CLOSE_BUTTON));
    close_button.setActionCommand(COMMAND_CLOSE);
    close_button.addActionListener(action_listener);
    
    south_panel.add(ok_button);
    south_panel.add(close_button);

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
 * Returns the location marker setup in the dialog.
 * 
 * @return the location marker setup in the dialog.
 */
  public LocationMarker getLocationMarker()
  {
    return(new LocationMarker(getMarkerName(),(float)getLatitude(),(float)getLongitude(),getCategory()));
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

//----------------------------------------------------------------------
/**
 * Returns the name setup in the dialog.
 *
 * @return the longitude setup in the dialog.
 */
  public String getMarkerName()
  {
    return(name_textfield_.getText());
  }

//----------------------------------------------------------------------
/**
 * Sets the name of the location marker.
 *
 * @return name the name of the location marker.
 */
  public void setMarkerName(String name)
  {
    name_textfield_.setText(name);
//     name_textfield_.setSelectionStart(0);
//     name_textfield_.setSelectionEnd(name.length());
  }

  public LocationMarkerCategory getCategory()
  {
    return((LocationMarkerCategory)category_box_.getSelectedItem());
  }

  
// //----------------------------------------------------------------------
// /**
//  * Action Listener Method
//  * 
//  * @param event the action event
//  */

//   public void actionPerformed(ActionEvent event)
//   {
// //     if(event.getActionCommand().equals(COMMAND_OK))
// //     {
// //       System.out.println("set marker on "+getName()+":"+getLatitude()+"/"+getLongitude());
// //       return;
// //     }

//     if(event.getActionCommand().equals(COMMAND_CLOSE))
//     {
//       System.out.println("close, do nothing");
//       setVisible(false);
//       result_button_ = CLOSE_OPTION;
//       return;
//     }

//   }

  
// ----------------------------------------------------------------------
// Inner Classes
// ----------------------------------------------------------------------

}




