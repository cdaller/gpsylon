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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;
import java.beans.PropertyEditorSupport;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JColorChooser;

import org.dinopolis.util.gui.ResourceEditorPanel;


public class ColorEditor extends PropertyEditorSupport
  implements ActionListener
{
  /** the editing component */
  private JPanel editing_component_;
  
  private JButton selected_color_button_;

  private Color selected_color_;

  public final static Color[] COLORS = {Color.white, Color.lightGray,
                                        Color.gray, Color.darkGray,
                                        Color.black, Color.red,
                                        Color.pink, Color.orange,
                                        Color.yellow, Color.green,
                                        Color.magenta, Color.cyan,
                                        Color.blue}; 

  public final static String[] COLOR_NAMES = {"white", "lightGray",
                                              "gray", "darkGray",
                                              "black", "red", "pink",
                                              "orange", "yellow",
                                              "green", "magenta",
                                              "cyan", "blue" }; 

  public final static String DEFAULT_KEY = "resource_editor.button.default";

  //----------------------------------------------------------------------
  /**
   */

  public ColorEditor()
  {
    super();    
  }

//----------------------------------------------------------------------
/**
 * Returns the String value.
 *
 * @return the String value.
 */
  
  public String getAsText() 
  {
    if (selected_color_ == null)
      return(null);
    return(selected_color_.getRed()+","+selected_color_.getGreen()+
           ","+selected_color_.getBlue()); 
  }
  
//----------------------------------------------------------------------
/**
 * Sets the given value.
 *
 * @param value the value to set.
 * @param IllegalArgumentException if value does not represent a valid
 * color.
 */

  public void setAsText(String value)
    throws IllegalArgumentException
  {
    StringTokenizer tok = new StringTokenizer(value, ",");
    int tokens = tok.countTokens();
    if (tokens == 1)
    {
      selected_color_ = getColorFromName(value);
      if ((selected_color_button_ != null) && (selected_color_ != null))
        selected_color_button_.setForeground(selected_color_);
      else
        throw(new IllegalArgumentException("'"+value+"' is not a "+ 
                                           "valid color!"));
      return;
    }
    if (tokens >= 3)
    {
      try
      {
        int a = 255;
        int r = Integer.parseInt(tok.nextToken());
        int g = Integer.parseInt(tok.nextToken());
        int b = Integer.parseInt(tok.nextToken());
        if (tokens > 3)
          a = Integer.parseInt(tok.nextToken());
        selected_color_ = new Color(r,g,b,a);
        if (selected_color_button_ != null)
          selected_color_button_.setForeground(selected_color_);
        return;
      }
      catch (NumberFormatException exc)
      {
      }
    }
    throw(new NumberFormatException("'"+value+"' is not a "+
                                    "valid color!"));
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
    editing_component_.setLayout(new BorderLayout());

    selected_color_button_ = new JButton(ResourceEditorPanel.getResources().
                                         getString("color_editor.color_button_text"));
    if (selected_color_ != null)
      selected_color_button_.setForeground(selected_color_);

    JButton reset = new JButton(ResourceEditorPanel.getResources().getString(DEFAULT_KEY));
    reset.setActionCommand("reset");
    reset.addActionListener(this);
    editing_component_.add(reset, BorderLayout.EAST);
    

    selected_color_button_.addActionListener(this);
    selected_color_button_.setOpaque(false);
    editing_component_.add(selected_color_button_, BorderLayout.CENTER);
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
    selected_color_ = (Color)value;
    if (selected_color_button_ != null) 
    {
      if (selected_color_ == null)
        selected_color_ = (Color)getValue();
      selected_color_button_.setForeground(selected_color_);
    }
  }

//----------------------------------------------------------------------
/**
 * Invoked when the a text field changed its value.
 *
 * @param event the action event
 */

  public void actionPerformed(ActionEvent event)
  {
    if ("reset".equals(event.getActionCommand()))
    {
      setValue(null);
      return;
    }
    Color new_color = JColorChooser.showDialog(editing_component_, 
                                               "Choose a Color",
                                               selected_color_);
    if ((new_color != null) && (!new_color.equals(selected_color_)))
      setValue(new_color);
  }

  //----------------------------------------------------------------------
  /**
   * @param name the name of the collor
   * @return the corresponding color
   * @exception IllegalArgumentException if name is not valid.
 */

  public static Color getColorFromName(String name)
    throws IllegalArgumentException
  {
    if ((name == null) || (name.length() <= 0))
      return(null);
    for (int count = 0; count < COLOR_NAMES.length; count++)
      if (COLOR_NAMES[count].equalsIgnoreCase(name))
        return(COLORS[count]);
    throw(new IllegalArgumentException("'"+name+"' is not a valid "+
                                       "Color!")); 
  }
}




