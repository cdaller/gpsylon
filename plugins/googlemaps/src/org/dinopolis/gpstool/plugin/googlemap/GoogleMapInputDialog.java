/***********************************************************************
 * @(#)$RCSfile$ $Revision$
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

package org.dinopolis.gpstool.plugin.googlemap;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.dinopolis.gpstool.GpsylonKeyConstants;
import org.dinopolis.util.Resources;

//----------------------------------------------------------------------
/**
 * A dialog that allows to enter latitude, longitude in SwissGrid and has an OK and
 * a cancel button.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GoogleMapInputDialog extends JDialog implements GpsylonKeyConstants
{

  private static final long serialVersionUID = -4268690927239597804L;

  Resources resources_;

  JTextField latitude_text_;
  JTextField longitude_text_;
  JLabel info_text_;

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

  public GoogleMapInputDialog(Resources resources, ActionListener action_listener, Frame owner)
  {
    super(owner,false);
    resources_ = resources;

    Container content_pane = getContentPane();

    // the panel with the input fields and the buttons
    JPanel input_panel = new JPanel(new BorderLayout());

    JPanel center_panel = new JPanel();
    input_panel.add(center_panel,BorderLayout.CENTER);
    JPanel south_panel = new JPanel();
    input_panel.add(south_panel,BorderLayout.SOUTH);

    center_panel.setLayout(new GridLayout(2,2));

    center_panel.add(new JLabel(resources_.getString(KEY_LOCALIZE_LATITUDE)));
    center_panel.add(latitude_text_ = new JTextField());
    center_panel.add(new JLabel(resources_.getString(KEY_LOCALIZE_LONGITUDE)));
    center_panel.add(longitude_text_ = new JTextField());

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

    addWindowListener(new WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
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
    if((latitude_text_.getText() == null) || (longitude_text_.getText() == null))
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
  public void setCoordinates(int latitude, int longitude)
  {
    latitude_text_.setText(new Integer(latitude).toString());
    longitude_text_.setText(new Integer(longitude).toString());
  }

//----------------------------------------------------------------------
/**
 * Returns the latitude setup in the dialog or 0 if no valid
 * latitude was entered by the user.
 *
 * @return the latitude setup in the dialog or Double.NaN if no valid
 * latitude was entered by the user.
 */
  public int getLatitude()
  {
    Integer lat = new  Integer(latitude_text_.getText());
    return(lat.intValue());
  }

//----------------------------------------------------------------------
/**
 * Returns the longitude setup in the dialog or 0 if no valid
 * latitude was entered by the user.
 *
 * @return the longitude setup in the dialog or Double.NaN if no valid
 * latitude was entered by the user.
 */
  public int getLongitude()
  {
    Integer lon = new  Integer(longitude_text_.getText());
	return(lon.intValue());
  }
}




