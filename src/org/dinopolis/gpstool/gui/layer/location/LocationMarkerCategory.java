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

import javax.swing.ImageIcon;

import org.dinopolis.util.Resources;
import org.dinopolis.gpstool.GPSMapKeyConstants;
import java.lang.Comparable;

//----------------------------------------------------------------------
/**
 * This class represents a category for a location marker.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class LocationMarkerCategory implements GPSMapKeyConstants, Comparable
{

  protected String name_;
  protected String id_;
  protected ImageIcon icon_;
  protected boolean visible_;

      /** level of detail currently used: 1 = least details, 10 = most details */
  protected int level_of_detail_;
  
  protected static Resources resources_;

//----------------------------------------------------------------------
/**
 * Empty Constructor.
 *
 */
  public LocationMarkerCategory()
  {
  }
  
//----------------------------------------------------------------------
/**
 * Copy Constructor.
 *
 * @param marker the marker to copy.
 */
  public LocationMarkerCategory(LocationMarkerCategory category)
  {
    this(category.name_, category.id_, category.icon_);
  }

//----------------------------------------------------------------------
/**
 * Constructor using the name and the id.
 *
 * @param name the name of the location marker.
 * @param id the id of the category.
 */
  public LocationMarkerCategory(String name, String id)
  {
    this(name, id, null);
  }

//----------------------------------------------------------------------
/**
 * Constructor using the name and the id.
 *
 * @param name the name of the location marker.
 * @param id the id of the category.
 * @param icon the icon for the category.
 */
  public LocationMarkerCategory(String name, String id, ImageIcon icon)
  {
    this(name,id,icon,true);
  }

//----------------------------------------------------------------------
/**
 * Constructor using the name and the id.
 *
 * @param name the name of the location marker.
 * @param id the id of the category.
 * @param icon the icon for the category.
 * @param visible if true, this category is visible
 */
  public LocationMarkerCategory(String name, String id, ImageIcon icon, boolean visible)
  {
    this(name,id,icon,visible,1);
  }

//----------------------------------------------------------------------
/**
 * Constructor using the name and the id.
 *
 * @param name the name of the location marker.
 * @param id the id of the category.
 * @param icon the icon for the category.
 * @param visible if true, this category is visible
 * @param level_of_detail the level of detail for this category. 1
 * means least details (only big cities), 10 means most details (even
 * small things).
 */
  public LocationMarkerCategory(String name, String id, ImageIcon icon, boolean visible, int level_of_detail)
  {
    name_ = name;
    id_ = id;
    icon_ = icon;
    visible_ = visible;
    level_of_detail_ = level_of_detail;
  }

//----------------------------------------------------------------------
/**
 * Sets the static resources that are valid for all Category
 * objects. These resources can be used to retrieve the level of
 * detail of the name of a given category_id without the need to pass
 * the resources as a parameter.
 *
 * @param resources the resources to read the information from.
 */
  public static void setResources(Resources resources)
  {
    resources_ = resources;
  }
  
//----------------------------------------------------------------------
/**
 * Get the name of the Category.
 *
 * @return the name.
 */
  public String getName()
  {
    return(name_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the name of the Category.
 *
 * @param name the name.
 */
  public void setName(String name)
  {
    name_ = name;
  }

//----------------------------------------------------------------------
/**
 * Get the id.
 *
 * @return the id.
 */
  public String getId() 
  {
    return (id_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the id.
 *
 * @param id the id.
 */
  public void setId(String id) 
  {
    id_ = id;
  }


//----------------------------------------------------------------------
/**
 * Get the level of detail of this category. 1 means least details
 * (only big cities), 10 means most details (even small things).
 *
 * @return the level of detail.
 */
  public int getLevelOfDetail() 
  {
    return (level_of_detail_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the level of detail of this category. 1 means least details
 * (only big cities), 10 means most details (even small things).
 *
 * @param level_of_detail the level of detail.
 */
  public void setLevelOfDetail(int level_of_detail) 
  {
    level_of_detail_ = level_of_detail;
  }
  
//----------------------------------------------------------------------
/**
 * Get the icon.
 *
 * @return the icon.
 */
  public ImageIcon getIcon() 
  {
    return (icon_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the icon.
 *
 * @param icon the icon.
 */
  public void setIcon(ImageIcon icon) 
  {
    icon_ = icon;
  }

//----------------------------------------------------------------------
/**
 * Return true if this category is visible.
 *
 * @return true if this category is visible.
 */
  public boolean isVisible() 
  {
    return (visible_);
  }
  
//----------------------------------------------------------------------
/**
 * Sets the visibility of this category.
 *
 * @param visible if true, the category is visible.
 */
  public void setVisible(boolean visible) 
  {
    visible_ = visible;
  }

//----------------------------------------------------------------------
/**
 * Returns an Array that holds localized categories. These resources
 * are created from the data in resources set via the static method
 * setResources().
 *
 * @return a Array that holds localized categories.
 * @exception IllegalStateException if the resources were not set before.
 */
  public static LocationMarkerCategory[] getCategories()
    throws IllegalStateException
  {
    if(resources_ == null)
      throw new IllegalStateException("No Resources set in LocationMarkerCategory");
    return(getCategories(resources_));
  }
  
//----------------------------------------------------------------------
/**
 * Returns an Array that holds localized categories.
 *
 * @param resources the resources to read the information from.
 * @return a Array that holds localized categories.
 */
  public static LocationMarkerCategory[] getCategories(Resources resources)
  {
    String[] category_ids = resources.getStringArray(KEY_LOCATION_MARKER_CATEGORY_AVAILABLE_CATEGORIES);
    LocationMarkerCategory[] categories = new LocationMarkerCategory[category_ids.length];
    for(int count = 0; count < category_ids.length; count++)
    {
      categories[count] = getCategory(category_ids[count],resources);
    }
    return(categories);
  }


//----------------------------------------------------------------------
/**
 * Returns a Category that matches the given id. The informaton is
 * read from the previously set Resources.
 *
 * @param category_id the id of the category to return.
 * @return a Category that matches the given id.
 * @exception IllegalStateException if the resources were not set before.
 */
  public static LocationMarkerCategory getCategory(String category_id)
    throws IllegalStateException
  {
    if(resources_ == null)
      throw new IllegalStateException("No Resources set in LocationMarkerCategory");
    return(getCategory(category_id,resources_));
  }
  
//----------------------------------------------------------------------
/**
 * Returns a Category that matches the given id.
 *
 * @param category_id the id of the category to return.
 * @param resources the resources to read the information from.
 * @return a Category that matches the given id.
 */
  public static LocationMarkerCategory getCategory(String category_id, Resources resources)
  {
        // TODO: cache the categories in a map

    String name;
    ImageIcon icon;
    name = resources.getString(KEY_LOCALIZE_LOCATION_MARKER_CATEGORY_ID_PREFIX+category_id);
    icon = (ImageIcon)resources.getIcon(KEY_LOCALIZE_LOCATION_MARKER_CATEGORY_ID_PREFIX
					+category_id
					+KEY_LOCATION_MARKER_CATEGORY_ICON_SUFFIX);
    boolean visible = resources.getBoolean(KEY_LOCALIZE_LOCATION_MARKER_CATEGORY_ID_PREFIX
					   +category_id
					   +KEY_LOCATION_MARKER_CATEGORY_VISIBLE_SUFFIX);
    int level_of_detail = resources.getInt(KEY_LOCALIZE_LOCATION_MARKER_CATEGORY_ID_PREFIX
                                           +category_id
                                           +KEY_LOCATION_MARKER_CATEGORY_LEVEL_OF_DETAIL_SUFFIX);
    if(icon != null)
      icon.setDescription(name);
    return(new LocationMarkerCategory(name,category_id,icon,visible,level_of_detail));
  }
  
//----------------------------------------------------------------------
/**
 * Writes the information in the categories back to the resource that
 * were set via the static method setResources();
 *
 * @param categories the categories to write.
 * @param resources the resources to write to.
 * @exception IllegalStateException if the resources were not set before.
 */
  public static void setCategories(LocationMarkerCategory[] categories)
    throws IllegalStateException
  {
    if(resources_ == null)
      throw new IllegalStateException("No Resources set in LocationMarkerCategory");
    setCategories(categories,resources_);
  }
//----------------------------------------------------------------------
/**
 * Writes the information in the categories back to the resources.
 *
 * @param categories the categories to write.
 * @param resources the resources to write to.
 */
  public static void setCategories(LocationMarkerCategory[] categories, Resources resources)
  {
    for(int count = 0; count < categories.length; count++)
    {
      setCategory(categories[count],resources);
    }
  }

//----------------------------------------------------------------------
/**
 * Writes the information in the category back to the resources that
 * were statically set before.
 *
 * @param category the category
 * @exception IllegalStateException if the resources were not set before.
 */
  public static void setCategory(LocationMarkerCategory category)
    throws IllegalStateException
  {
    if(resources_ == null)
      throw new IllegalStateException("No Resources set in LocationMarkerCategory");
    setCategory(category,resources_);
  }
//----------------------------------------------------------------------
/**
 * Writes the information in the category back to the resources.
 *
 * @param category the category
 * @param resources the resources to write the info to.
 */
  public static void setCategory(LocationMarkerCategory category, Resources resources)
  {
    String name;
    ImageIcon icon;
    String category_id = category.getId();

        // TODO set icon!
    
    resources.setString(KEY_LOCALIZE_LOCATION_MARKER_CATEGORY_ID_PREFIX+category_id,category.getName());
//     icon = (ImageIcon)resources.getIcon(KEY_LOCALIZE_LOCATION_MARKER_CATEGORY_ID_PREFIX
// 					+category_id
// 					+KEY_LOCATION_MARKER_CATEGORY_ICON_SUFFIX);
    resources.setBoolean(KEY_LOCALIZE_LOCATION_MARKER_CATEGORY_ID_PREFIX
					   +category_id
					   +KEY_LOCATION_MARKER_CATEGORY_VISIBLE_SUFFIX,category.isVisible());
    resources.setInt(KEY_LOCALIZE_LOCATION_MARKER_CATEGORY_ID_PREFIX
					   +category_id
					   +KEY_LOCATION_MARKER_CATEGORY_LEVEL_OF_DETAIL_SUFFIX,category.getLevelOfDetail());
  }


//----------------------------------------------------------------------
/**
 * Compares the current category with a given one by its name.
 *
 * @return the result of the comparison of the names of the
 * categories.
 */
  public int compareTo(Object category)
  {
    return(getName().compareTo(((LocationMarkerCategory)category).getName()));
  }

//----------------------------------------------------------------------
/**
 * Returns a String representation of this object.
 *
 * @return a String representation of this object.
 */
  public String toString()
  {
    return("LocationMarkerCategory [name='"+name_+"', id="+id_+"]");
  }
  
}


