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

import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.dinopolis.gpstool.util.angle.AngleFormat;

//----------------------------------------------------------------------
/**
 * Cell renderer for angles (latitude and longitude)
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class AngleCellRenderer extends JLabel
  implements ListCellRenderer, TableCellRenderer
{
  AngleFormat print_format_;

  public static final String DEFAULT_FORMAT = "DD°MM'SS\"";

  public AngleCellRenderer()
  {
    this(DEFAULT_FORMAT);
  }

  public AngleCellRenderer(String angle_format)
  {
    setOpaque(true);
    setHorizontalAlignment(RIGHT);
    setVerticalAlignment(CENTER);
    print_format_ = new AngleFormat(angle_format);
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
    
    setText(print_format_.format(value));

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
    setText(print_format_.format(value));
    return this;
  }
}







