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


package org.dinopolis.gpstool.gui.layer.location;

import javax.swing.JDialog;
import org.dinopolis.util.Resources;
import java.awt.event.ActionListener;
import java.awt.Frame;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.WindowAdapter;

import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTable;

import org.dinopolis.gpstool.GPSMapKeyConstants;
import javax.swing.table.TableModel;
import javax.swing.JFrame;
import java.util.Set;
import java.util.TreeSet;
import java.util.Arrays;
import java.awt.event.ActionEvent;

//----------------------------------------------------------------------
/**
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class SelectCategoryFrame
  extends JFrame implements GPSMapKeyConstants, ActionListener
{
  Resources resources_;

  public static final String COMMAND_OK = "ok";
  public static final String COMMAND_APPLY = "apply";
  public static final String COMMAND_CANCEL = "cancel";

  protected int category_name_column_index_;
  protected int category_visible_column_index_; 
  protected int category_level_of_detail_column_index_; 
  
  protected int category_name_column_width_;
  protected int category_visible_column_width_;
  protected int category_level_of_detail_column_width_; 

  JTable category_table_;

  protected boolean edit_level_of_detail_mode_ = true;

//----------------------------------------------------------------------
/**
 * Constructor
 *
 * @param resources the resources to user for titles, button titles,
 * etc.
 * @param action_listener the action listener to inform on a button
 * press. The commands used for the buttons are COMMAND_OK,
 * COMMAND_APPLY or COMMAND_CANCEL. All button presses must be handled
 * from outside. No default behaviour is implemented (closing the
 * frame, etc.).
 * @param owner the owner of the frame.
 */

  public SelectCategoryFrame(Resources resources, ActionListener action_listener, Frame owner)
  {
//    super(owner,resources.getString(KEY_LOCALIZE_SELECT_LOCATION_MARKER_CATEGORIES_TITLE),false);
    super(resources.getString(KEY_LOCALIZE_SELECT_LOCATION_MARKER_CATEGORIES_TITLE));
    resources_ = resources;

    category_name_column_index_ =
      resources_.getInt(KEY_LOCATION_MARKER_SELECT_CATEGORY_NAME_COLUMN_INDEX);
    category_visible_column_index_ =
      resources_.getInt(KEY_LOCATION_MARKER_SELECT_CATEGORY_VISIBLE_COLUMN_INDEX); 
    category_level_of_detail_column_index_ =
      resources_.getInt(KEY_LOCATION_MARKER_SELECT_CATEGORY_LEVEL_OF_DETAIL_COLUMN_INDEX); 
  
    category_name_column_width_ =
      resources_.getInt(KEY_LOCATION_MARKER_SELECT_CATEGORY_NAME_COLUMN_WIDTH);
    category_visible_column_width_ =
      resources_.getInt(KEY_LOCATION_MARKER_SELECT_CATEGORY_VISIBLE_COLUMN_WIDTH);
    category_level_of_detail_column_width_ =
      resources_.getInt(KEY_LOCATION_MARKER_SELECT_CATEGORY_LEVEL_OF_DETAIL_COLUMN_WIDTH);

    edit_level_of_detail_mode_ =
      resources_.getBoolean(KEY_LOCATION_MARKER_SELECT_CATEGORY_ALLOW_EDIT_LEVEL_OF_DETAIL);
    
    Container content_pane = getContentPane();

    JPanel south_panel = new JPanel();

    category_table_ = new CategoryTable();
    setLevelOfDetailEditable(edit_level_of_detail_mode_);

    JScrollPane scroll_pane = new JScrollPane(category_table_);
    content_pane.add(scroll_pane,BorderLayout.NORTH);
    
    JButton ok_button = new JButton(resources_.getString(KEY_LOCALIZE_OK_BUTTON));
    ok_button.setActionCommand(COMMAND_OK);
    ok_button.addActionListener(action_listener);
    
    JButton apply_button = new JButton(resources_.getString(KEY_LOCALIZE_APPLY_BUTTON));
    apply_button.setActionCommand(COMMAND_APPLY);
    apply_button.addActionListener(action_listener);
    
    JButton close_button = new JButton(resources_.getString(KEY_LOCALIZE_CANCEL_BUTTON));
    close_button.setActionCommand(COMMAND_CANCEL);
    close_button.addActionListener(action_listener);
    
    south_panel.add(ok_button);
    south_panel.add(apply_button);
    south_panel.add(close_button);

    content_pane.add(south_panel,BorderLayout.SOUTH);

    JPanel panel = new JPanel();
    
    JButton all_button = new JButton(resources_.getString(KEY_LOCALIZE_ALL));
    all_button.setActionCommand("select_all");
    all_button.addActionListener(this);

    JButton none_button = new JButton(resources_.getString(KEY_LOCALIZE_NONE));
    none_button.setActionCommand("select_none");
    none_button.addActionListener(this);

    panel.add(all_button);
    panel.add(none_button);
    content_pane.add(panel,BorderLayout.CENTER);

    pack();

//    setSize(300,(int)getSize().getHeight());

    addWindowListener(new WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
          setVisible(false);
        }
      });
  }


  public void actionPerformed(ActionEvent event)
  {
    String command = event.getActionCommand();
    boolean value_to_set = false;
    if(command.equals("select_all"))
      value_to_set = true;

    if(command.equals("select_none"))
      value_to_set = false;

    if(command.equals("select_all") || (command.equals("select_none")))
    {
      LocationMarkerCategory[] categories = getCategories();
      for(int count = 0; count < categories.length; count++)
      {
        categories[count].setVisible(value_to_set);
      }
      setCategories(categories);
      repaint();
    }
    
  }

//----------------------------------------------------------------------
/**
 * Returns if the level of detail can be edited.
 *
 * @return if the level of detail can be edited.
 */
protected boolean isLevelOfDetailEditable() 
{
  return (edit_level_of_detail_mode_);
}

//----------------------------------------------------------------------
/**
 * Sets if the level of detail of the categories may be edited resp if
 * they are shown at all.
 *
 * @param edit_level_of_detail
 */
protected void setLevelOfDetailEditable(boolean edit_level_of_detail) 
{
  edit_level_of_detail_mode_ = edit_level_of_detail;
  int table_width;
  if(edit_level_of_detail_mode_)
    table_width =category_name_column_width_
                 + category_visible_column_width_
                 + category_level_of_detail_column_width_;
  else
    table_width =category_name_column_width_
                 + category_visible_column_width_;
  category_table_.setPreferredScrollableViewportSize(new Dimension(table_width + 10,200));
}


//----------------------------------------------------------------------
/**
 * Sets the categories (holding its visibility state).
 *
 * @param categories the categories (holding its visibility state).
 */
  public void setCategories(LocationMarkerCategory[] categories)
  {
    ((CategoryTableModel)category_table_.getModel()).setCategories(categories);
  }

//----------------------------------------------------------------------
/**
 * Returns the categories (holding its visibility state) chosen in the
 * dialog.
 *
 * @return the categories (holding its visibility state) chosen in the
 * dialog.
 */
  public LocationMarkerCategory[] getCategories()
  {
    return(((CategoryTableModel)category_table_.getModel()).getCategories());
  }

//----------------------------------------------------------------------
/**
 * A table that displays in a row the categories and a checkbox for
 * their visibilities.
 *
 */
  class CategoryTable extends JTable
  {
        //CategoryCellRenderer category_renderer_ = new CategoryCellRenderer();

    public CategoryTable()
    {
      super();
      LocationMarkerCategory[] categories = LocationMarkerCategory.getCategories(resources_);
      String[] column_names;
      if(edit_level_of_detail_mode_)
        column_names = new String[] {resources_.getString(KEY_LOCALIZE_CATEGORY),
                                            resources_.getString(KEY_LOCALIZE_DISPLAY),
                                            resources_.getString(KEY_LOCALIZE_LEVEL_OF_DETAIL)};
      else
        column_names = new String[] {resources_.getString(KEY_LOCALIZE_CATEGORY),
                                     resources_.getString(KEY_LOCALIZE_DISPLAY)};
        
      CategoryTableModel model = new CategoryTableModel(column_names,categories);
      setModel(model);

      getColumnModel().getColumn(category_name_column_index_).setPreferredWidth(category_name_column_width_);
      getColumnModel().getColumn(category_visible_column_index_).setPreferredWidth(category_visible_column_width_);
      if(edit_level_of_detail_mode_)
        getColumnModel().getColumn(category_level_of_detail_column_index_).setPreferredWidth(category_level_of_detail_column_width_);

      setDefaultRenderer(LocationMarkerCategory.class, new CategoryCellRenderer());
    }

    public CategoryTable(TableModel model)
    {
      super(model);
    }

//     public TableCellRenderer getCellRenderer(int row, int column)
//     {
//       if (column == category_name_column_index_)
//       {
//         return(category_renderer_);
//       }
// // else...
//       return super.getCellRenderer(row, column);
//     }
  }
  

//----------------------------------------------------------------------
/**
 * A table model to show the categories and the checkbox for visuality
 * or not. Additionally one may request the table model for the
 * categories.
 */
  class CategoryTableModel extends AbstractTableModel
  {
    LocationMarkerCategory[] categories_;
    String[] column_names_;
    
    public CategoryTableModel(String[] column_names,
			      LocationMarkerCategory[] categories)
    {
      column_names_ = column_names;
      setCategories(categories);
    }

    public LocationMarkerCategory[] getCategories()
    {
      return(categories_);
    }
    
    public void setCategories(LocationMarkerCategory[] categories)
    {
      categories_ = categories;
      Arrays.sort(categories_);
    }
    
    public String getColumnName(int column)
    {
      return(column_names_[column]);
    }

    public int getRowCount()
    {
      return(categories_.length);
    }

    public int getColumnCount()
    {
      return(column_names_.length);
    }

    public Object getValueAt(int row, int column)
    {
//      System.out.println("getValueAt row: "+row+" column: "+column);
      if(column == category_name_column_index_)  // Category
      {
//        System.out.println("category");
        return(categories_[row]);
      }
      else
      {
        if(column == category_visible_column_index_)
        {
//          System.out.println("visible");
          return(new Boolean(categories_[row].isVisible()));
        }
        else
        {
//          System.out.println("level");
          return(new Integer(categories_[row].getLevelOfDetail()));
        }
      }
    }

    public boolean isCellEditable(int row, int column)
    {
      return((column == category_visible_column_index_)
             || (column == category_level_of_detail_column_index_));
    }

    public void setValueAt(Object value, int row, int column)
    {
      if(column == category_visible_column_index_)
      {
        categories_[row].setVisible(((Boolean)value).booleanValue());
        fireTableCellUpdated(row, column);
      }
      else
      {
        if(column == category_visible_column_index_)
        {
          categories_[row].setLevelOfDetail(Math.min(((Integer)value).intValue(),10));
          fireTableCellUpdated(row, column);
        }
      }
    }

    public Class getColumnClass(int column)
    {
      return(getValueAt(0, column).getClass());
    }

  }
}



