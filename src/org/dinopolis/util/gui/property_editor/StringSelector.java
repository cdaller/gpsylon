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
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyEditor;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.swing.JCheckBox;

import javax.swing.JPanel;

public class StringSelector extends JPanel 
  implements ItemListener 
{
  public final static String STRING_ARRAY_DELIMITER = ","; 

  private JCheckBox[] check_box_;

  private PropertyEditor editor_;    

  private String[] values_;

  public StringSelector(PropertyEditor pe) 
  {
    this(pe, pe.getTags(), null);
  }

  public StringSelector(PropertyEditor pe, String[] values, 
                        String[] titles) 
  {
    this (pe, values, titles, 3);
  }

  public StringSelector(PropertyEditor pe, String[] values, 
                        String[] titles, int vis_columns) 
  {
    super();
    if (titles == null)
      titles = new String[0];

    setLayout(new BorderLayout());
        
    editor_ = pe;
    values_ = values;

    JPanel panel = new JPanel();
    int raws = values.length/vis_columns;
    if ((values.length % vis_columns) != 0)
      raws++;
    panel.setLayout(new GridLayout(raws,vis_columns));
    add(panel, BorderLayout.WEST);

    check_box_ = new JCheckBox[values.length];
    for (int i = 0; i < values.length; i++) 
    {
      try
      {
        if (titles[i] == null)
          titles[i] = values[i];
        check_box_[i] = new JCheckBox(titles[i]);
      }
      catch(ArrayIndexOutOfBoundsException exc)
      {
        check_box_[i] = new JCheckBox(values[i]);
      }
      
      panel.add(check_box_[i]);

      check_box_[i].addItemListener(this);
    }

    select((String[])editor_.getValue());
  }

  //----------------------------------------------------------------------
  /**
   * @param value the value to select
   * @param select wheather to select or unselect the value.
   */

  private void select(String[] values)
  {
    if (values == null)
      values = new String[0];

    for (int i = 0; i < values_.length; i++) 
    {
      check_box_[i].removeItemListener(this);
      check_box_[i].setSelected(false);
      check_box_[i].addItemListener(this);
    }

    for (int count = 0; count < values.length; count++) 
    {
      String token = values[count];
      for (int i = 0; i < values_.length; i++) 
      {
        if (values[count].equals(values_[i]))
        {
          check_box_[i].removeItemListener(this);
          check_box_[i].setSelected(true);
          check_box_[i].addItemListener(this);
        }
      }
    }
  }

  public void itemStateChanged(ItemEvent evt) 
  {
    changed();
  }

  //----------------------------------------------------------------------
  /**
   * @return 
   */

  private void changed()
  {
    String[] ret = new String[check_box_.length];
    
    int length = 0;
    for (int count = 0; count < check_box_.length; count++)
      if (check_box_[count].isSelected())
        ret[length++] = values_[count];
    String[] values = new String[length];
    System.arraycopy(ret, 0, values, 0, length);
    editor_.setValue(values);
  }

  public void repaint() 
  {
    if ((check_box_ != null) && (editor_ != null))
      select((String[])editor_.getValue());
  }
}
