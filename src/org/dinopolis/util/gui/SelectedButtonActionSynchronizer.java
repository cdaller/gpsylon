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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;

import javax.swing.AbstractButton;
import javax.swing.Action;

//----------------------------------------------------------------------
/**
 * This class helps to synchronize actions and toggle buttons (like
 * CheckButtonMenuItems, RadioButtonMenuItems, ...). Whenever the
 * button's state changes, the selected state is set into the action
 * (key <code>SELECTED</code>, value Boolean object), or vice versa
 * (if the value SELECTED is set into the action, the button changes
 * its state accordingly).
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class SelectedButtonActionSynchronizer
  implements ItemListener, PropertyChangeListener
{
  WeakReference weak_action_;
  WeakReference weak_button_;
  
  public static final String SELECTED = "selected";
        
//----------------------------------------------------------------------
/**
 * Constructor using the AbstractButton and the Action to be synchronized.
 *
 * @param button the abstract button (also JMenuItems are
 * AbstractButtons!)
 * @param action the action
 */
  public SelectedButtonActionSynchronizer(AbstractButton button, Action action)
  {
    super();
    setAction(action);
    setButton(button);
    button.addItemListener(this);
    action.addPropertyChangeListener(this);
  }


//----------------------------------------------------------------------
/**
 * ItemListener interface
 *
 * @param event the ActionEvent
 */
  public void itemStateChanged(ItemEvent event)
  {
//    System.out.println("XXXXX Button '"+getButton().getText()+"' ItemStateChanged: "+event);
    AbstractButton button = (AbstractButton)event.getSource();
    Action action = getAction();
    if(action != null)
    {
      action.putValue(SELECTED,new Boolean(event.getStateChange() == ItemEvent.SELECTED));
    }
    else
    {
          // action was garbage collected
      button.removeItemListener(this);
    }
  }
  
//----------------------------------------------------------------------
/**
 * ChangeListener interface
 *
 * @param event the ChangeEvent
 */
  public void propertyChange(PropertyChangeEvent event)
  {
//    System.out.println("XXXXX Action '"+getAction().getValue(Action.NAME)+"' PropertyChanged: "+event);
    String property_name = event.getPropertyName();
    if(SELECTED.equals(property_name))
    {
      AbstractButton button = getButton();
      Action action = (Action)event.getSource();
      if(button != null)
      {
        Object selected = action.getValue(SELECTED);
//        System.out.println("changed "+event.getPropertyName()+" to " +selected);
        if((selected != null) && (selected instanceof Boolean))
          button.setSelected(((Boolean)selected).booleanValue());
      }
      else
      {
            // action was garbage collected
        action.removePropertyChangeListener(this);
      }
    }
  }
  
 
//----------------------------------------------------------------------
/**
 * Sets the weak reference for the action.
 *
 * @param action the action
 */
	public void setAction(Action action)
  {
    weak_action_ = new WeakReference(action);
	}

//----------------------------------------------------------------------
/**
 * Resolves the weak reference for the action.
 *
 * @return the action or <null> if the reference was garbage
 * collected.
 */
  public Action getAction()
  {
    return (Action)weak_action_.get();
  }
//----------------------------------------------------------------------
/**
 * Sets the weak reference for the button.
 *
 * @param button the button
 */
	public void setButton(AbstractButton button)
  {
    weak_button_ = new WeakReference(button);
	}

//----------------------------------------------------------------------
/**
 * Resolves the weak reference for the button.
 *
 * @return the button or <null> if the reference was garbage
 * collected.
 */
  public AbstractButton getButton()
  {
    return (AbstractButton)weak_button_.get();
  }
}
  


