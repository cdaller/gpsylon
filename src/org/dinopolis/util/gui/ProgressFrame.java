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


package org.dinopolis.util.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import org.dinopolis.gpstool.util.ProgressListener;



//----------------------------------------------------------------------
/**
 * This frame may be used to display the information from the {@link
 * org.dinopolis.gpstool.util.ProgressListener}.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 * @see org.dinopolis.gpstool.util.ProgressListener
 */

public class ProgressFrame extends JFrame implements ProgressListener
{
  JProgressBar progress_bar_;

//----------------------------------------------------------------------
/**
 * Constructor using a frame (and progress bar) title and a parent
 * component to center the frame on.
 *
 * @param title the title of the frame and the progress bar
 * @param parent_component the component to center the frame one.
 */
  public ProgressFrame(String title, Component parent_component)
  {
    this(title);
    Point parent_location = parent_component.getLocation();
    setLocation((int)(parent_location.getX() + (parent_component.getWidth() - getWidth()) / 2),
                (int)(parent_location.getY() + (parent_component.getHeight() - getHeight()) /2));
  }
  
//----------------------------------------------------------------------
/**
 * Constructor using a frame (and progress bar) title.
 *
 * @param title the title of the frame and the progress bar
 */
  public ProgressFrame(String title)
  {
    super(title);

    Container content_pane = getContentPane();
    progress_bar_ = new JProgressBar();
    progress_bar_.setSize(200,15);
    progress_bar_.setStringPainted(true);
    progress_bar_.setString(title);
    content_pane.add(progress_bar_);
    pack();
  }

  public void setTitle(String title)
  {
    setTitle(title);
  }


//----------------------------------------------------------------------
/**
 * Callback to inform listeners about an action to start.
 *
 * @param action_id the id of the action that is started. This id may
 * be used to display a message for the user.
 * @param min_value the minimum value of the progress counter.
 * @param max_value the maximum value of the progress counter. If the
 * max value is unknown, max_value is set to <code>Integer.NaN</code>.
 */
  public void actionStart(String action_id, int min_value, int max_value)
  {
    progress_bar_.setMinimum(min_value);
    progress_bar_.setMaximum(max_value);
    setVisible(true);
    
  }
  
//----------------------------------------------------------------------
/**
 * Callback to inform listeners about progress going on. It is not
 * guaranteed that this method is called on every change of current
 * value (e.g. only call this method on every 10th change).
 *
 * @param action_id the id of the action that is started. This id may
 * be used to display a message for the user.
 * @param current_value the current value
 */
  public void actionProgress(String action_id, int current_value)
  {
    progress_bar_.setValue(current_value);
  }

//----------------------------------------------------------------------
/**
 * Callback to inform listeners about the end of the action.
 *
 * @param action_id the id of the action that is started. This id may
 * be used to display a message for the user.
 */
  public void actionEnd(String action_id)
  {
    setVisible(false);
  }

}


