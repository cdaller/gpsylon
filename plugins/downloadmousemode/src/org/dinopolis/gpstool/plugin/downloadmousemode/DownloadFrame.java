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

package org.dinopolis.gpstool.plugin.downloadmousemode;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import org.dinopolis.gpstool.GpsylonKeyConstants;
import org.dinopolis.gpstool.gui.util.AngleJTextField;
import org.dinopolis.gpstool.gui.util.PluginCellRenderer;
import org.dinopolis.gpstool.plugin.MapRetrievalPlugin;
import org.dinopolis.gpstool.util.angle.Angle;
import org.dinopolis.util.Resources;


//----------------------------------------------------------------------
/**
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class DownloadFrame extends JFrame implements GpsylonKeyConstants
{

  Resources resources_;
  String[] available_scales_ = {"1000","2000","5000","10000","25000","50000","100000","200000",
                                "500000","1000000","2000000","5000000","10000000"};

  AngleJTextField latitude_text_;
  AngleJTextField longitude_text_;
  JComboBox mapserver_box_;
  JComboBox scale_box_;
  JLabel info_label_;
  public JProgressBar progress_bar_bytes_;
  public JProgressBar progress_bar_images_;
  
//  static final String DEFAULT_SCALE = "2000";
  static final String DEFAULT_SCALE = "500000";
  static final int DEFAULT_IMAGE_WIDTH = 1280;
  static final int DEFAULT_IMAGE_HEIGHT = 1024;


  public static final String COMMAND_DOWNLOAD_SCALE = "scale";
//  public static final String COMMAND_DOWNLOAD_PREVIEW = "preview";
  public static final String COMMAND_DOWNLOAD_DOWNLOAD = "download";
  public static final String COMMAND_DOWNLOAD_MAPSERVER = "mapserver";
  public static final String COMMAND_DOWNLOAD_CLOSE = "close";

  public static final String MAP_PREFIX = "map_";
  public static final String MAP_PATTERN = "00000";
  
//----------------------------------------------------------------------
/**
 * Constructor using the current map position as download position
 *
 * @param resources the resources
 * @param map_manager the hook to add new maps to
 * @param preview_hook the PreviewHook able to display a preview for
 * the map to download.
 */

  public DownloadFrame(Resources resources, ActionListener action_listener,
                       FocusListener focus_listener,
                       MapRetrievalPlugin[] available_mapservers,
                       MapRetrievalPlugin default_mapserver)
  {
    super();
    resources_ = resources;
    
    setTitle(resources_.getString(DownloadMouseModeLayer.KEY_LOCALIZE_DOWNLOADFRAME_TITLE));

    Container content_pane = getContentPane();

    JPanel center_panel = new JPanel();
    content_pane.add(center_panel,BorderLayout.CENTER);
    JPanel south_panel = new JPanel();
    content_pane.add(south_panel,BorderLayout.SOUTH);
    
    center_panel.setLayout(new GridLayout(8,2));

    center_panel.add(new JLabel(resources_.getString(GpsylonKeyConstants.KEY_LOCALIZE_LATITUDE)));
    center_panel.add(latitude_text_ = new AngleJTextField());
    center_panel.add(new JLabel(resources_.getString(GpsylonKeyConstants.KEY_LOCALIZE_LONGITUDE)));
    center_panel.add(longitude_text_ = new AngleJTextField());
    center_panel.add(new JLabel(resources_.getString(DownloadMouseModeLayer.KEY_LOCALIZE_MAP_SERVER)));
    center_panel.add(mapserver_box_ = new JComboBox(available_mapservers));
    mapserver_box_.setSelectedItem(default_mapserver);
    mapserver_box_.setRenderer(new PluginCellRenderer());
    center_panel.add(new JLabel(resources_.getString(GpsylonKeyConstants.KEY_LOCALIZE_SCALE)));
    center_panel.add(scale_box_ = new JComboBox(available_scales_));
    scale_box_.setSelectedItem(DEFAULT_SCALE);

    center_panel.add(new JLabel(resources_.getString(GpsylonKeyConstants.KEY_LOCALIZE_LOAD_PROGRESS)
                       + " " + resources_.getString(GpsylonKeyConstants.KEY_LOCALIZE_MAPS)));
    center_panel.add(progress_bar_images_ = new JProgressBar());

    center_panel.add(new JLabel(resources_.getString(GpsylonKeyConstants.KEY_LOCALIZE_LOAD_PROGRESS)
                       + " " + resources_.getString(GpsylonKeyConstants.KEY_LOCALIZE_BYTES)));
    center_panel.add(progress_bar_bytes_ = new JProgressBar());

    center_panel.add(new JLabel(resources_.getString(GpsylonKeyConstants.KEY_LOCALIZE_INFO)));
    center_panel.add(info_label_ = new JLabel());

        // set valid angle formats for latitude and longitude:
    latitude_text_.clearValidAngleFormats();
    latitude_text_.addValidAngleFormats(
      resources_.getStringArray(GpsylonKeyConstants.KEY_ANGLE_FORMAT_VALID_FORMATS));
    longitude_text_.clearValidAngleFormats();
    longitude_text_.addValidAngleFormats(
      resources_.getStringArray(GpsylonKeyConstants.KEY_ANGLE_FORMAT_VALID_FORMATS));
        // set angle format for textfields:
    try
    {
      latitude_text_.setPrintFormat(resources_.getString(GpsylonKeyConstants.KEY_ANGLE_FORMAT_LATLON));
      longitude_text_.setPrintFormat(resources_.getString(GpsylonKeyConstants.KEY_ANGLE_FORMAT_LATLON));
    }
    catch(IllegalArgumentException e)
    {
      System.err.println("Illegal format for latitude/longitude: "+e.getMessage());
    }


        // add focus listener to the lat/lon fields:
    latitude_text_.addFocusListener(focus_listener);
    longitude_text_.addFocusListener(focus_listener);
    
    scale_box_.setActionCommand(COMMAND_DOWNLOAD_SCALE);
    scale_box_.addActionListener(action_listener);

    mapserver_box_.setActionCommand(COMMAND_DOWNLOAD_MAPSERVER);
    mapserver_box_.addActionListener(action_listener);

    setDownloadCoordinates(resources.getDouble(GpsylonKeyConstants.KEY_CURRENT_MAP_POSITION_LATITUDE),
                           resources.getDouble(GpsylonKeyConstants.KEY_CURRENT_MAP_POSITION_LONGITUDE));
    
    JButton download_button = new JButton(resources_.getString(DownloadMouseModeLayer.KEY_LOCALIZE_DOWNLOAD_BUTTON));
    download_button.setActionCommand(COMMAND_DOWNLOAD_DOWNLOAD);
    download_button.addActionListener(action_listener);
    
//      JButton preview_button = new JButton(resources_.getString(KEY_LOCALIZE_PREVIEW_BUTTON));
//      preview_button.setActionCommand(COMMAND_DOWNLOAD_PREVIEW);
//      preview_button.addActionListener(this);
    
    final JButton close_button = new JButton(resources_.getString(GpsylonKeyConstants.KEY_LOCALIZE_CLOSE_BUTTON));
    close_button.setActionCommand(COMMAND_DOWNLOAD_CLOSE);
    close_button.addActionListener(action_listener);
    
    south_panel.add(download_button);
//    south_panel.add(preview_button);
    south_panel.add(close_button);
    
    pack();

    addWindowListener(new WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
              // simulate close click
          close_button.doClick();
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
    info_label_.setText(info);
  }
  
//----------------------------------------------------------------------
/**
 * Sets new coordinates
 * 
 * @param latitude
 * @param longitude
 */
  public void setDownloadCoordinates(double latitude, double longitude)
  {
    latitude_text_.setAngleAsLatitude(latitude);
    longitude_text_.setAngleAsLongitude(longitude);
//    System.out.println("Frame: Setting coordinates: "+latitude+"/"+longitude);
  }

//----------------------------------------------------------------------
/**
 * Returns the scale setup in the dialog.
 *
 * @return the scale setup in the dialog.
 */
  public float getScale()
  {
    String scale = (String)scale_box_.getSelectedItem();
    return(Float.parseFloat(scale));
  }

//----------------------------------------------------------------------
/**
 * Returns the image width setup in the dialog.
 *
 * @return the image width setup in the dialog.
 */
  public int getImageWidth()
  {
    return(DEFAULT_IMAGE_WIDTH);
  }
  
//----------------------------------------------------------------------
/**
 * Returns the image height setup in the dialog.
 *
 * @return the image height setup in the dialog.
 */
  public int getImageHeight()
  {
    return(DEFAULT_IMAGE_HEIGHT);
  }

//----------------------------------------------------------------------
/**
 * Returns the latitude setup in the dialog.
 *
 * @return the latitude setup in the dialog.
 * @exception ParseException if the input text is not conform to any
 * of the valid format definitions.
 */
  public double getLatitude()
    throws ParseException
  {
    Angle angle = latitude_text_.getAngle();
    if(angle == null)
      throw new ParseException(
        resources_.getString(GpsylonKeyConstants.KEY_LOCALIZE_MESSAGE_LATITUDE_OR_LONGITUDE_WRONG_FORMAT),0);

    return(angle.degrees());
  }

//----------------------------------------------------------------------
/**
 * Returns the longitude setup in the dialog.
 *
 * @return the longitude setup in the dialog.
 * @exception ParseException if the input text is not conform to any
 * of the valid format definitions.
 */
  public double getLongitude()
    throws ParseException
  {
    Angle angle = longitude_text_.getAngle();
    if(angle == null)
      throw new ParseException(
        resources_.getString(GpsylonKeyConstants.KEY_LOCALIZE_MESSAGE_LONGITUDE_WRONG_FORMAT),0);

    return(angle.degrees());
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
        resources_.getString(GpsylonKeyConstants.KEY_LOCALIZE_MESSAGE_LATITUDE_OR_LONGITUDE_WRONG_FORMAT),
        resources_.getString(GpsylonKeyConstants.KEY_LOCALIZE_MESSAGE_ERROR_TITLE),
        JOptionPane.ERROR_MESSAGE);
      return(false);
    }
    return(true);
  }

//----------------------------------------------------------------------
/**
 * Returns the chosen map retrieval plugin.
 * 
 * @return the chosen map retrieval plugin.
 */
  public MapRetrievalPlugin getMapRetrievalPlugin()
  {
    return((MapRetrievalPlugin)mapserver_box_.getSelectedItem());
  }
}




