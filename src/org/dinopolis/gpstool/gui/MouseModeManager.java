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


package org.dinopolis.gpstool.gui;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import org.dinopolis.util.gui.SelectedButtonActionSynchronizer;
import javax.swing.KeyStroke;

//----------------------------------------------------------------------
/**
 * The MouseModeManager manages the different MouseModes.
 *
 * @see MouseMode
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class MouseModeManager  
{
  protected Vector mouse_modes_;
  protected ButtonGroup button_group_;

  public MouseModeManager()
  {
    mouse_modes_ = new Vector();
    button_group_ = new ButtonGroup();
  }

  public void addMouseMode(MouseMode mode)
  {
    mouse_modes_.add(mode);
  }

  public void addMouseModes(MouseMode[] modes)
  {
    for(int index = 0; index < modes.length; index++)
      addMouseMode(modes[index]);
  }

  public void removeMouseMode(MouseMode mode)
  {
    mouse_modes_.remove(mode);
  }

  public boolean containsMouseMode(MouseMode mode)
  {
    return(mouse_modes_.contains(mode));
  }

//----------------------------------------------------------------------
/**
 * Returns an action for the given mouse mode (using its name, icon
 * and description). When the action is executed (actionPerformed
 * method called), it calls the {@link
 * org.dinopolis.gpstool.gui.MouseMode#setActive(boolean)} method.
 *
 * @param mode the mouse mode.
 * @return menu items for all mouse modes.
 */
  public static Action createMouseModeAction(final MouseMode mode)
  {
    Action action = new AbstractAction()
      {
        MouseMode mouse_mode_ = mode;
        public void actionPerformed(ActionEvent event)
        {
          Object selected = getValue(SelectedButtonActionSynchronizer.SELECTED);
          if((selected == null) || !(selected instanceof Boolean))
          {
            System.err.println("WARNING: MapMouseMode is not synchronized with Button!");
          }
          else
          {
            boolean mode_active = ((Boolean)selected).booleanValue();
            mouse_mode_.setActive(mode_active);
//            System.out.println("MouseMode '"+mouse_mode_.getMouseModeName()+"' activated: "+mode_active);
          }
        }
      };
    action.putValue(Action.NAME,mode.getMouseModeName());
    action.putValue(Action.SHORT_DESCRIPTION,mode.getMouseModeDescription());
    action.putValue(Action.SMALL_ICON,mode.getMouseModeIcon());

        // add property change listener to react on change of the
        // "selected" property and therefore to de-/activate the mouse mode:
    action.addPropertyChangeListener(new PropertyChangeListener()
      {
        public void propertyChange(PropertyChangeEvent event)
        {
          String property_name = event.getPropertyName();
          if(SelectedButtonActionSynchronizer.SELECTED.equals(property_name))
          {
            Object selected = event.getNewValue();
            if((selected != null) && (selected instanceof Boolean))
              mode.setActive(((Boolean)selected).booleanValue());
          }
        }
      });
    
    return(action);
  }

//----------------------------------------------------------------------
/**
 * Returns the menu items of all mouse modes. The menu items are
 * JRadioButtonMenuItem objects and grouped in a ButtonGroup, so only
 * one of them may be chosen at one time. Additionally, the actions
 * and the menus are synchronized, so changes of the state in one is
 * reflected in the other (selected state).
 *
 * @return menu items for all mouse modes.
 */

  public JMenuItem[] getMenuItems()
  {
    JMenuItem[] menu_items = new JRadioButtonMenuItem[mouse_modes_.size()];
    JRadioButtonMenuItem item = null;
    MouseMode mode;
    Action action;
    JMenuItem active_item = null;
    for(int mode_index = 0; mode_index < mouse_modes_.size(); mode_index++)
    {
//       menu_items[mode_index] = new JRadioButtonMenuItem(mouse_modes_.getMouseModeName(),
//                                                         mouse_modes_.getMouseModeIcon());
      mode = (MouseMode)mouse_modes_.elementAt(mode_index);
      action = createMouseModeAction(mode);
      item = new JRadioButtonMenuItem(action);
      String accelerator_key = mode.getMouseModeAcceleratorKey();
      if((accelerator_key != null) && (accelerator_key.length() > 0))
        item.setAccelerator(KeyStroke.getKeyStroke(accelerator_key));
      char mnemonic = mode.getMouseModeMnemonic();
      if(mnemonic == 0)
        mnemonic = mode.getMouseModeName().charAt(0);
      item.setMnemonic(mnemonic);
          // create a synchronizer, that keeps the selected state of
          // action and item synchronized. there is no need to keep
          // this reference, as the synchronizer is added as listener
          // to action and item.
      new SelectedButtonActionSynchronizer(item,action);
      menu_items[mode_index] = item;
      button_group_.add(item);
      if(mode.isActive())
        active_item = item;
    }
    if(active_item != null)
      active_item.setSelected(true);
    else
      item.setSelected(true); // activate the last one
    return(menu_items);
  }
}


