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


package org.dinopolis.gpstool.gui.util;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.dinopolis.gpstool.plugin.Plugin;

//----------------------------------------------------------------------
/**
 * Cell renderer for plugins (for choosing a plugin in comboboxes,
 * lists, etc.).
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class PluginCellRenderer extends JLabel
  implements ListCellRenderer, TableCellRenderer
{

  public PluginCellRenderer()
  {
    setOpaque(true);
    setHorizontalAlignment(RIGHT);
    setVerticalAlignment(CENTER);
  }

  public Component getListCellRendererComponent(JList list,
                                                Object value,
                                                int index,
                                                boolean isSelected,
                                                boolean cellHasFocus)
  {
    if (isSelected)
    {
      setBackground(list.getSelectionBackground());
      setForeground(list.getSelectionForeground());
    }
    else
    {
      setBackground(list.getBackground());
      setForeground(list.getForeground());
    }
    
    setText(((Plugin)value).getPluginName());

    return (this);
  }


  public Component getTableCellRendererComponent(JTable table,
						 Object value,
						 boolean isSelected,
						 boolean hasFocus,
						 int row,int column)
  {
    if (isSelected)
    {
      setBackground(table.getSelectionBackground());
      setForeground(table.getSelectionForeground());
    }
    else
    {
      setBackground(table.getBackground());
      setForeground(table.getForeground());
    }
    setText(((Plugin)value).getPluginName());
    return(this);
  }
}







