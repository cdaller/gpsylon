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

//import java.awt.*;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;

public class StringChooser extends JPanel 
  implements ItemListener 
{
  /** the editing component */
  private JComboBox selector_;

  private PropertyEditor editor_;    

  private String[] values_;

  public StringChooser(PropertyEditor pe) 
  {
    this(pe, pe.getTags(), null);
  }

  public StringChooser(PropertyEditor pe, String[] values, String[] titles) 
  {
    super();
    if (titles == null)
      titles = new String[0];
    setLayout(new BorderLayout());
    editor_ = pe;
    selector_ = new JComboBox();
    values_ = values;
    add(selector_, BorderLayout.WEST);

    for (int i = 0; i < values.length; i++) 
    {
      try
      {
        if (titles[i] == null)
          titles[i] = values[i];
        selector_.addItem(titles[i]);
      }
      catch(ArrayIndexOutOfBoundsException exc)
      {
        selector_.addItem(values[i]);
      }
    }
    // This is a noop if the getAsText is not a tag.
    String to_select = editor_.getAsText();
    select(to_select);
    if (to_select == null)
      editor_.setAsText(values_[0]);
    selector_.addItemListener(this);
  }
  
  //----------------------------------------------------------------------
  /**
   * @param value the value to select
   */

  public void select(String value)
  {
    if (value == null)
      selector_.setSelectedIndex(0);
    else
    {
      for (int count = 0; count < values_.length; count++)
      {
        if (values_[count].equals(value))
        {
          selector_.setSelectedIndex(count);
          break;
        }
      }
    }
  }

  public void itemStateChanged(ItemEvent evt) 
  {
    editor_.setAsText(values_[selector_.getSelectedIndex()]);
  }

  public void repaint() 
  {
    if ((selector_ != null) && (editor_ != null))
      select(editor_.getAsText());
  }
}
