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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.dinopolis.gpstool.GPSMapKeyConstants;
import org.dinopolis.util.Resources;

//----------------------------------------------------------------------
/**
 * A layer that is able to display location markers (points of
 * interest, ...). It uses LocationMarkerSource objects as sources for
 * the LocationMarker objects to paint. The painting itself is done in
 * this class.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class SearchLocationMarkerFrame extends JFrame
  implements GPSMapKeyConstants, MouseListener
{
  LocationMarkerSearchPanel search_panel_;
  Resources resources_;

  ActionListener action_listener_;

  protected String double_click_command = COMMAND_GOTO;

  public static final String COMMAND_GOTO = "goto";
  public static final String COMMAND_CLOSE = "close";

//----------------------------------------------------------------------
/**
 * Constructor
 *
 * @param resources the resources to user for titles, button titles,
 * etc.
 * @param action_listener the action listener to inform on a button
 * press. The commands used for the buttons are COMMAND_GOTO,
 * or COMMAND_CLOSE. All button presses must be handled
 * from outside. No default behaviour is implemented (closing the
 * frame, etc.).
 * @param owner the owner of the frame.
 */
  public SearchLocationMarkerFrame(Resources resources, ActionListener action_listener, Frame owner)
  {
    super(resources.getString(KEY_LOCALIZE_SEARCH_LOCATION_MARKER_TITLE));
    resources_ = resources;
    action_listener_ = action_listener;
    
    search_panel_ = new LocationMarkerSearchPanel();
    search_panel_.addMouseListenerForList(this);
    Container content_pane = getContentPane();
    content_pane.add(search_panel_,BorderLayout.CENTER);

    JButton goto_button = new JButton(resources_.getString(KEY_LOCALIZE_GOTO_BUTTON));
    goto_button.setActionCommand(COMMAND_GOTO);
    goto_button.addActionListener(action_listener);

    JButton close_button = new JButton(resources_.getString(KEY_LOCALIZE_CLOSE_BUTTON));
    close_button.setActionCommand(COMMAND_CLOSE);
    close_button.addActionListener(action_listener);

    JPanel south_panel = new JPanel();
    south_panel.add(goto_button);
    south_panel.add(close_button);

    content_pane.add(south_panel,BorderLayout.SOUTH);
    pack();
  }

//----------------------------------------------------------------------
/**
 * Returns the selected location marker or null, if no entry in the
 * list is selected.
 * 
 * @return the selected location marker or null, if no entry in the
 * list is selected.
 */
  public LocationMarker getSelectedLocationMarker()
  {
    return(search_panel_.getSelectedLocationMarker());
  }

//----------------------------------------------------------------------
/**
 * Add a location marker source to search.
 * 
 * @param source the source to add.
 */
  public void addLocationMarkerSource(LocationMarkerSource source)
  {
    search_panel_.addLocationMarkerSource(source);
  }
  
//----------------------------------------------------------------------
// MouseListener Adapter
//----------------------------------------------------------------------

  public void mouseClicked(MouseEvent event)
  {
    if (event.getClickCount() == 2)
    {
//      int index = result_list_.locationToIndex(event.getPoint());
//        System.out.println("Double clicked on Item " + index);
//        System.out.println("Double clicked on marker " + getSelectedLocationMarker());
      action_listener_.actionPerformed(new ActionEvent(search_panel_,0,double_click_command));
    }
  }
   
  public void mouseEntered(MouseEvent event)
  {
//    System.out.println("mouseEntered: "+event.getSource());
  }

  public void mouseExited(MouseEvent event)
  {
//    System.out.println("mouseExited: "+event.getSource());
  }

  public void mousePressed(MouseEvent event)
  {
        // on solaris, mousepressed sets popuptrigger
    if(event.isPopupTrigger())
    {
//      showPopup(event);
    }
//    System.out.println("mousePressed: "+event.getSource());
  }

  public void mouseReleased(MouseEvent event)
  {
        // on windows, mousereleased sets popuptrigger
    if(event.isPopupTrigger())
    {
//      showPopup(event);
    }
//    System.out.println("mouseReleased: "+event.getSource());
  }



}



