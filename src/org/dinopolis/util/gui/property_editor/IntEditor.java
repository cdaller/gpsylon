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
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document; 
import javax.swing.text.PlainDocument; 

//----------------------------------------------------------------------
/**
 * This Class implements a Int editor.
 *
 * @author Dieter Freismuth
 * @version $Revision$
 */

public class IntEditor extends PropertyEditorSupport 
  implements ActionListener, FocusListener
{
  /** the editing component */
  private JPanel editing_component_;

  /** the text field */
  private JTextField text_field_;

//----------------------------------------------------------------------
/**
 * Returns the Int value.
 *
 * @return the Int value.
 */

  public String getAsText() 
  {
    Object value = getValue();
    if (value == null)
      return(null);
    return(((Integer)value).toString());
  }
  
//----------------------------------------------------------------------
/**
 * Sets the given value.
 *
 * @param value the value to set.
 */

  public void setAsText(String value)
  {
    setValue(Integer.valueOf(value));
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
    text_field_ = new IntField();
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
    text_field_.setText(getAsText());
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
    if (text.length() <= 0)
    {
      setValue(null);
      return;
    }
    setValue(Integer.valueOf(text));
  }
}


class IntField extends JTextField 
{
  private NumberFormat integer_formatter_;
  
  public IntField()
  {
    super();
    integer_formatter_ = NumberFormat.getNumberInstance();
    integer_formatter_.setParseIntegerOnly(true);
  }

  public int getValue() 
  {
    int ret_val = 0;
    try 
    {
      ret_val = integer_formatter_.parse(getText()).intValue();
    } 
    catch (ParseException e) 
    {
      // This should never happen because insertString allows
      // only properly formatted data to get in the field.
    }
    return(ret_val);
  }

  public void setValue(int value) 
  {
    setText(integer_formatter_.format(value));
  }
  
  protected Document createDefaultModel() 
  {
    return new IntDocument();
  }

  protected class IntDocument extends PlainDocument 
  {
        // cdaller: taken from batik DoubleDocument: now it accepts negative numbers as well!
    public void insertString(int offs, 
                             String str,
                             AttributeSet a) 
      throws BadLocationException 
    {
//       if (str == null)
//         return;
//       char[] source = str.toCharArray();
//       char[] result = new char[source.length];
//       int j = 0;

//       for (int i = 0; i < result.length; i++)
//         if (Character.isDigit(source[i]))
//           result[j++] = source[i];
//       super.insertString(offs, new String(result, 0, j), a);
      if (str == null)
      {
        return;
      }

          // Get current value
      String curVal = getText(0, getLength());

          // Strip non digit characters
      char[] buffer = str.toCharArray();
      char[] digit = new char[buffer.length];
      int j = 0;

      if(offs==0 && buffer!=null && buffer.length>0 && buffer[0]=='-')
        digit[j++] = buffer[0];

      for (int i = 0; i < buffer.length; i++)
      {
        if(Character.isDigit(buffer[i]))
          digit[j++] = buffer[i];
      }

          // Now, test that new value is within range.
      String added = new String(digit, 0, j);
      try
      {
        StringBuffer val = new StringBuffer(curVal);
        val.insert(offs, added);
        if(val.toString().equals("-"))
          super.insertString(offs, added, a);
        else
        {
          Integer.valueOf(val.toString());
          super.insertString(offs, added, a);
        }
      }
      catch(NumberFormatException e)
      {
            // Ignore insertion, as it results in an out of range value
      }
      
    } 
  }
}









