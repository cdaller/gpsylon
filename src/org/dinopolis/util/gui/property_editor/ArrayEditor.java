/***********************************************************************
 * @(#)$RCSfile$   $Revision$ $Date$
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

package org.dinopolis.util.gui.property_editor;

import java.awt.Component;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.Array;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JPanel;

//----------------------------------------------------------------------
/**
 * @author Dieter Freismuth
 * @version $Revision$
 */

public class ArrayEditor extends PropertyEditorSupport 
  implements PropertyChangeListener
{
  /** the editing component */
  private JPanel editing_component_;

  /** the base type */
  private Class base_type_;

  /** the base editor */
  private PropertyEditor base_editor_;

  /** the base editor */
  private PropertyEditor[] base_editors_;

  /** the array field delimiter */
  public final static String DELIMITER = ",";

  public final static String ESCAPE = "\\";
  
  //----------------------------------------------------------------------
  /**
   * @param base_editor the base editor capable to edit one single value of the array.
   */

  public ArrayEditor(Class base_type, PropertyEditor base_editor)
  {
    super();
    base_type_ = base_type;
    base_editor_ = base_editor;
    base_editors_ = new PropertyEditor[0];
  }

//----------------------------------------------------------------------
/**
 * Returns the String value.
 *
 * @return the String value.
 */

  public String getAsText() 
  {
    Object value = getValue();
    int value_length = 0;
    if (value != null)
      value_length = Array.getLength(value);

    StringBuffer buffer = new StringBuffer();
    for (int count = 0; count < value_length; count++)
    {
      if (count > 0)
        buffer.append(DELIMITER);
      base_editor_.setValue(Array.get(value, count));
      buffer.append(escape(base_editor_.getAsText()));
//      buffer.append(base_editor_.getAsText());
    }
    return(buffer.toString());
  }
  
//----------------------------------------------------------------------
/**
 * Sets the given value.
 *
 * @param value the value to set.
 */

  public void setAsText(String value)
  {
    int delim_start_pos = 0;
    int delim_pos = value.indexOf(DELIMITER, delim_start_pos);
    Vector elements = new Vector();
    StringBuffer buffer = new StringBuffer();
    while (delim_pos > 0)
    {
      int escape_pos = delim_pos;
      while ((escape_pos > 0) &&
             (ESCAPE.equals(value.substring(escape_pos-ESCAPE.length(),escape_pos))))
        escape_pos -= ESCAPE.length();
      if ((delim_pos-(escape_pos/ESCAPE.length())) % 2 == 0) // is real delimiter
      {
        elements.add(value.substring(delim_start_pos, delim_pos));
        delim_start_pos = delim_pos+DELIMITER.length();
      }
      delim_pos = value.indexOf(DELIMITER, delim_pos+DELIMITER.length());
    }
    elements.add(value.substring(delim_start_pos));
    

    int elements_count = elements.size();

    Object array = Array.newInstance(base_type_, elements_count);
    for (int count = 0; count < elements_count; count++)
    {
      base_editor_.setAsText(unescape((String)elements.elementAt(count)));
//      base_editor_.setAsText((String)elements.elementAt(count));
      Array.set(array, count, base_editor_.getValue());
    }
    setValue(array);
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
    editing_component_.setLayout(new GridLayout(1,0));
    editing_component_.removeAll();

    // clean up
    for (int count = 0; count < base_editors_.length; count++)
      base_editors_[count].removePropertyChangeListener(this);

    Object value = getValue();

    int value_length = 0;
    if (value != null)
      value_length = Array.getLength(value);
    base_editors_ = new PropertyEditor[value_length];

    try
    {
      for (int count = 0; count < base_editors_.length; count++)
      {
        base_editors_[count] = (PropertyEditor)base_editor_.getClass().newInstance();
        base_editors_[count].addPropertyChangeListener(this);
        Component comp = base_editors_[count].getCustomEditor();
        
        if (comp != null)
          editing_component_.add(comp);
        else
          System.err.println("NULL EDITOR !!!!! : "+base_editors_[count]);
      }
      updateValues();
    }
    catch (InstantiationException exc)
    {
      // should never happen
      exc.printStackTrace();
      editing_component_ = null;
    }
    catch (IllegalAccessException exc)
    {
      // should never happen
      exc.printStackTrace();
      editing_component_ = null;
    }
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
    if (editing_component_ == null)
      return;
    Object value = getValue();
    int value_length = 0;
    if (value != null)
      value_length = Array.getLength(value);

    for (int count = 0; count < Math.min(value_length,
                                         base_editors_.length);
         count++)
    {
      base_editors_[count].removePropertyChangeListener(this);
      base_editors_[count].setValue(Array.get(value, count));
      base_editors_[count].addPropertyChangeListener(this);
    }
  }

 //----------------------------------------------------------------------
  /**
   * This method gets called when a underlaying property editor
   * thanged its value.
   * @param evt A PropertyChangeEvent object describing the event source 
   * and the property that has changed.
   */

  public void propertyChange(PropertyChangeEvent evt)
  {
    Object[] values = new Object[base_editors_.length];
    boolean[] null_values = new boolean[base_editors_.length];
    
    for (int count = 0; count < values.length; count++)
    {
      values[count] = base_editors_[count].getValue();
      null_values[count] = (values[count] == null);
    }
    int real_values_length = values.length;
//      if (!fixed_size_)
//      {
//        real_values_length = 0;
//        for (int count = 0; count < values.length; count++)
//        {
//          if (null_values[count]) // was removed !
//            removePropertyEditor(real_values_length);
//          else
//            real_values_length++;
//        }
//      }

    Object array = Array.newInstance(base_type_, real_values_length);
    for (int count = 0; count < real_values_length; count++)
      Array.set(array, count, base_editors_[count].getValue());
    setValue(array);
  }

  //----------------------------------------------------------------------
  /**
   * @param to_escape 
   * @return the escaped string
   */

  public static String escape(String to_escape)
  {
//    to_escape = replace(to_escape,ESCAPE,ESCAPE+ESCAPE);
    to_escape = replace(to_escape,DELIMITER,ESCAPE+DELIMITER);
    return(to_escape);
  }


  //----------------------------------------------------------------------
  /**
   * @param to_unescape 
   * @return the unescaped string
   */

  public static String unescape(String to_unescape)
  {
    to_unescape = replace(to_unescape,ESCAPE+DELIMITER,DELIMITER);
//    to_unescape = replace(to_unescape,ESCAPE+ESCAPE,ESCAPE);
    return(to_unescape);
  }


  //----------------------------------------------------------------------
  /**
   * Replace each substring that matches the given string (old_str)
   * with the new_str.
   *
   * @param str the string to use
   * @param old_str the string to replace
   * @param new_str the new strint that replaces the old_str
   * @return the result
   */

  protected static String replace(String str, String old_str, String new_str)
  {
    int old_index = 0;
    int index = 0;
    StringBuffer result = new StringBuffer(str.length());
    while((index >= 0) && (old_index < str.length()))
    {
      index = str.indexOf(old_str,old_index);
      if(index >= 0)
      {
        result.append(str.substring(old_index,index)).append(new_str);
        old_index = index + old_str.length();
      }
    }
    result.append(str.substring(old_index,str.length())); // append rest
    return(result.toString());
  }

  public static void main(String[] args)
  {
    String s = "c:\\und\\no,ch\\was\\\\und noch weiter";
    System.out.println("original: "+s);
    String s2 = escape(s);
    System.out.println("escaped: "+s2);
    System.out.println("unescaped again: "+unescape(s2));
    
  }
}


