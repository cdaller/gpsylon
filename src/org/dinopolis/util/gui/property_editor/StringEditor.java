/***********************************************************************
 * @(#)$RCSfile$   $Revision$ $Date$
 *
 * Copyright (c) 2001 IICM, Graz University of Technology
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

package org.dinopolis.util.gui.property_editor;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.beans.PropertyEditorSupport;
import javax.swing.JPanel;
import javax.swing.JTextField;

//----------------------------------------------------------------------
/**
 * This Class implements a String editor.
 *
 * @author Dieter Freismuth
 * @version $Revision$
 */

public class StringEditor extends PropertyEditorSupport 
  implements ActionListener, FocusListener
{
  /** the editing component */
  private JPanel editing_component_;

  /** the text field */
  private JTextField text_field_;

//----------------------------------------------------------------------
/**
 * Returns the String value.
 *
 * @return the String value.
 */

  public String getAsText() 
  {
    return((String)getValue());
  }
  
//----------------------------------------------------------------------
/**
 * Sets the given value.
 *
 * @param value the value to set.
 */

  public void setAsText(String value)
  {
    setValue(value);
  }

  //----------------------------------------------------------------------
  /**
   * Returns the custom editing component.
   *
   * @return the editing component.
   */

  public Component getCustomEditor()
  {
    if (editing_component_ == null)
      createEditingComponent();
    return(editing_component_);
  }

//----------------------------------------------------------------------
/**
 * Creates and sets the editing component.
 */

  protected void createEditingComponent()
  {
    editing_component_ = new JPanel();
    editing_component_.setLayout(new GridLayout(0,1));
    text_field_ = new JTextField();
    text_field_.addActionListener(this);
    text_field_.addFocusListener(this);
    editing_component_.add(text_field_);
    updateValues();
  }


//----------------------------------------------------------------------
/**
 * Sets the given value.
 *
 * @param value the value to be set
 */

  public void setValue(Object value)
  {
    super.setValue(value);
    updateValues();
  }

//----------------------------------------------------------------------
/**
 * Updates the text field to stored value.
 */

  protected void updateValues()
  {
    if (text_field_ == null)
      return;
    text_field_.setText((String)getValue());
  }

//----------------------------------------------------------------------
/**
 * Invoked when the a text field loses its focus.
 * Sets the value of the textfield.
 *
 * @param event the focus event.
 */

  public void focusLost(FocusEvent event)
  {
    setValue();
  }

//----------------------------------------------------------------------
/**
 * Invoked when the a text field gained its focus.
 * Selects the textfield.
 *
 * @param event the focus event
 */

  public void focusGained(FocusEvent event)
  {
    text_field_.selectAll();
  }

//----------------------------------------------------------------------
/**
 * Invoked when the a text field changed its value.
 *
 * @param event the action event
 */

  public void actionPerformed(ActionEvent event)
  {
    setValue();
    text_field_.transferFocus();
  }

//----------------------------------------------------------------------
/**
 * Sets the value according to the strings given within the text
 * fields.
 */

  protected void setValue()
  {
    String text = text_field_.getText();
//     if (text.length() <= 0)
//     {
//       setValue(null);
//       return;
//     }
    setValue(text);
  }
}







