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
  protected Vector mouse_mode_infos_;
  protected ButtonGroup menu_button_group_;

//----------------------------------------------------------------------
/**
 * Default Constructor
 *
 */
  public MouseModeManager()
  {
    mouse_mode_infos_ = new Vector();
    menu_button_group_ = new ButtonGroup();
  }

//----------------------------------------------------------------------
/**
 * Add a mouse mode
 * @param mode the mouse mode to add
 */
  public void addMouseMode(MouseMode mode)
  {
    Action action = createMouseModeAction(mode);
    MouseModeInfo info = new MouseModeInfo(mode,action);
    mouse_mode_infos_.add(info);
  }

//----------------------------------------------------------------------
/**
 * Add mouse modes
 * @param modes the mouse modes to add
 */
  public void addMouseModes(MouseMode[] modes)
  {
    for(int index = 0; index < modes.length; index++)
      addMouseMode(modes[index]);
  }

//----------------------------------------------------------------------
/**
 * Remove a mouse mode
 * @param mode the mouse mode to remove
 */
//    public void removeMouseMode(MouseMode mode)
//    {
//      mouse_modes_.remove(mode);
//    }

//----------------------------------------------------------------------
/**
 * Returns true if the manager holds the given mouse mode
 * @param mode the mouse mode to check
 */
//    public boolean containsMouseMode(MouseMode mode)
//    {
//      return(mouse_modes_.contains(mode));
//    }


//----------------------------------------------------------------------
/**
 * Activate the mouse mode with the given name
 *
 * @param mode_name the mouse mode to activate
 * @return true if a mouse mode was found with the given name, false
 * otherwise.
 */
  public boolean activateMouseMode(String mode_name)
  {
    Iterator iterator = mouse_mode_infos_.iterator();
    MouseModeInfo info;
    MouseMode mode;
    while(iterator.hasNext())
    {
      info = (MouseModeInfo)iterator.next();
      mode = info.getMouseMode();
      if(mode_name.equals(mode.getMouseModeName()))
      {
	info.getAction().putValue(SelectedButtonActionSynchronizer.SELECTED,
				  new Boolean(true));
	return(true);
      }
    }
    return(false);
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
    action.putValue(Action.ACCELERATOR_KEY,
		    KeyStroke.getKeyStroke(mode.getMouseModeAcceleratorKey()));
    char mnemonic = mode.getMouseModeMnemonic();
    if(mnemonic == 0)
      mnemonic = mode.getMouseModeName().charAt(0);
    action.putValue(Action.MNEMONIC_KEY,new Integer((int)mnemonic));

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
    JMenuItem[] menu_items = new JRadioButtonMenuItem[mouse_mode_infos_.size()];
    JRadioButtonMenuItem item = null;
    MouseMode mode;
    Action action;
    JMenuItem active_item = null;
    MouseModeInfo info;
    for(int mode_index = 0; mode_index < mouse_mode_infos_.size(); mode_index++)
    {
      info = (MouseModeInfo)mouse_mode_infos_.elementAt(mode_index);
      mode = info.getMouseMode();
      action = info.getAction();
      item = new JRadioButtonMenuItem(action);
          // create a synchronizer, that keeps the selected state of
          // action and item synchronized. There is no need to keep
          // this reference, as the synchronizer is added as listener
          // to action and item.
      new SelectedButtonActionSynchronizer(item,action);
      menu_items[mode_index] = item;
      menu_button_group_.add(item);
      if(mode.isActive())
        active_item = item;
    }
//      if(active_item != null)
//        active_item.setSelected(true);
//      else
//        if(item != null)
//          item.setSelected(true); // activate the last one
    return(menu_items);
  }

//----------------------------------------------------------------------
// Inner Classes
//----------------------------------------------------------------------

//----------------------------------------------------------------------
/**
 * Holds a mouse mode and the action that is used in a menu or
 * toolbar to activate/deactivate it.
 */
  class MouseModeInfo
  {
    MouseMode mode_;
    Action action_;
    
//----------------------------------------------------------------------
/**
 * Creates a new <code>MouseModeInfo</code> instance.
 *
 * @param mode a <code>MouseMode</code> 
 * @param action the <code>Action</code> that is used in a menu or
 * toolbar to activate/deactivate the mouse mode.
 */
    public MouseModeInfo(MouseMode mode, Action action)
    {
      mode_ = mode;
      action_ = action;
    }

//----------------------------------------------------------------------
/**
 * Returns the mouse mode.
 *
 * @return the mouse mode.
 */
    public MouseMode getMouseMode()
    {
      return(mode_);
    }
//----------------------------------------------------------------------
/**
 * Returns the action
 *
 * @return the action.
 */
    public Action getAction()
    {
      return(action_);
    }
  }
  
}




