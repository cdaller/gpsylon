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


package org.dinopolis.gpstool.gui;
import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;

//----------------------------------------------------------------------
/**
 * A mini progress bar, that changes the color of a single pixel for
 * the change of a status. The pixels are situated in columns and rows.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class MiniProgressBar extends JComponent
{

  Color active_color_;
  Color inactive_color_;
  int rows_;
  int columns_;
  boolean[][] active_array_;

//----------------------------------------------------------------------
/**
 * Constructor
 */
  public MiniProgressBar(int columns, int rows)
  {
    super();
    active_color_ = Color.red;
    inactive_color_ = Color.green;
    columns_ = columns;
    rows_ = rows;
    setPreferredSize(new Dimension(columns,rows));
    active_array_ = new boolean[columns][rows];
  }
  
  
// //----------------------------------------------------------------------
// /**
//  * Get the number of columns.
//  *
//  * @return the number of columns.
//  */
//   public int getColumns()
//   {
//     return (columns_);
//   }
  
// //----------------------------------------------------------------------
// /**
//  * Set the number of columns.
//  *
//  * @param columns the number of columns.
//  */
//   public void setColumns(int columns)
//   {
//     columns_ = columns;
//     setPreferredSize(new Dimension(columns_,rows_));
//   }
  
  
// //----------------------------------------------------------------------
// /**
//  * Get the rows.
//  *
//  * @return the rows.
//  */
//   public int getRows()
//   {
//     return (rows_);
//   }
  
// //----------------------------------------------------------------------
// /**
//  * Set the rows.
//  *
//  * @param rows the rows.
//  */
//   public void setRows(int rows)
//   {
//     rows_ = rows;
//     setPreferredSize(new Dimension(columns_,rows_));
//   }

  
//----------------------------------------------------------------------
/**
 * Get the color for active pixels.
 *
 * @return the color for active pixels.
 */
  public Color getActiveColor()
  {
    return (active_color_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the color for active pixels.
 *
 * @param color the color for active pixels.
 */
  public void setActiveColor(Color color)
  {
    active_color_ = color;
    repaint();
  }
  
//----------------------------------------------------------------------
/**
 * Get the color for inactive pixels.
 *
 * @return the color for inactive pixels.
 */
  public Color getInactiveColor()
  {
    return (inactive_color_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the color for inactive pixels.
 *
 * @param color the color for inactive pixels.
 */
  public void setInactiveColor(Color color)
  {
    inactive_color_ = color;
    repaint();
  }

//----------------------------------------------------------------------
/**
 * Set the the given row/column active (true) or inactive (false);
 *
 * @param column the column of the pixel
 * @param row the row of the pixel
 * @param value if true, the given pixel is set to active.
 */
  public void setActive(int column, int row, boolean value)
  {
    active_array_[column][row] = value;
  }


  boolean active_;
  
//----------------------------------------------------------------------
/**
 * Get the state of the given pixel.
 *
 * @return true, if active.
 */
  public boolean isActive(int column, int row)
  {
    return (active_array_[column][row]);
  }
  
  

//----------------------------------------------------------------------
/**
 * Paint the component
 *
 */
  public void paintComponent(Graphics g)
  {
    for(int row_count = 0; row_count < rows_; row_count++)
    {
      for(int column_count = 0; column_count < columns_; column_count++)
      {
        if(active_array_[row_count][column_count])
          g.setColor(active_color_);
        else
          g.setColor(inactive_color_);
        g.drawLine(column_count,row_count,column_count,row_count);
      }
    }
  }

  
  
}


