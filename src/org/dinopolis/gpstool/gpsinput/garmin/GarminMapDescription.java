/***********************************************************************
 * @(#)$RCSfile$   $Revision$$Date$
 *
 * Copyright (c) 2001-2003 Sandra Brueckler, Stefan Feitl
 * Written during an XPG-Project at the IICM of the TU-Graz, Austria
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


package org.dinopolis.gpstool.gpsinput.garmin;

//----------------------------------------------------------------------
/**
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GarminMapDescription
{
  int image_number_;
  int map_number_;
  String map_type_;
  String map_name_;
  String map_area_;
  int product_number_;
  int map_length_;

  public GarminMapDescription()
  {
  }


//----------------------------------------------------------------------
/**
 * Get the image_number value.
 * @return the image_number value.
 */
  public int getImageNumber()
  {
    return (image_number_);
  }

//----------------------------------------------------------------------
/**
 * Set the image_number value.
 * @param image_number The new image_number value.
 */
  public void setImageNumber(int image_number)
  {
    image_number_ = image_number;
  }


//----------------------------------------------------------------------
/**
 * Get the map_type value.
 * @return the map_type value.
 */
  public String getMapType()
  {
    return (map_type_);
  }

//----------------------------------------------------------------------
/**
 * Set the map_type value.
 * @param map_type The new map_type value.
 */
  public void setMapType(String map_type)
  {
    map_type_ = map_type;
  }


//----------------------------------------------------------------------
/**
 * Get the map_name value.
 * @return the map_name value.
 */
  public String getMapName()
  {
    return (map_name_);
  }

//----------------------------------------------------------------------
/**
 * Set the map_name value.
 * @param map_name The new map_name value.
 */
  public void setMapName(String map_name)
  {
    map_name_ = map_name;
  }

//----------------------------------------------------------------------
/**
 * Get the map_area value.
 * @return the map_area value.
 */
  public String getMapArea()
  {
    return (map_area_);
  }

//----------------------------------------------------------------------
/**
 * Set the map_area value.
 * @param map_area The new map_area value.
 */
  public void setMapArea(String map_area)
  {
    map_area_ = map_area;
  }


//----------------------------------------------------------------------
/**
 * Get the map_number value.
 * @return the map_number value.
 */
  public int getMapNumber()
  {
    return (map_number_);
  }

//----------------------------------------------------------------------
/**
 * Set the map_number value.
 * @param map_number The new map_number value.
 */
  public void setMapNumber(int map_number)
  {
    map_number_ = map_number;
  }


//----------------------------------------------------------------------
/**
 * Returns the filename used on the gps device for this map.
 *
 * @return  the filename used on the gps device for this map.
 */
  public String getImageFileName()
  {
    String name = Integer.toString(image_number_);
        // pad name with leading 0s:
    while (name.length() < 8)
    {
	    name = "0" + name;
    }
    return(name.toUpperCase());
  }

  public String getMapNumberFileName()
  {
    String name = Integer.toHexString(map_number_);
        // pad name with leading 0s:
    while (name.length() < 7)
    {
	    name = "0" + name;
    }
    name = "I"+name;
    return(name.toUpperCase());

  }


//----------------------------------------------------------------------
/**
 * Get the product_number value.
 * @return the product_number value.
 */
  public int getMapProductNumber()
  {
    return (product_number_);
  }

//----------------------------------------------------------------------
/**
 * Set the product_number value.
 * @param product_number The new product_number value.
 */
  public void setMapProductNumber(int product_number)
  {
    product_number_ = product_number;
  }

  
//----------------------------------------------------------------------
/**
 * Get the map_length value.
 * @return the map_length value.
 */
  public int getMapLength()
  {
    return (map_length_);
  }

//----------------------------------------------------------------------
/**
 * Set the map_length value.
 * @param map_length The new map_length value.
 */
  public void setMapLength(int map_length)
  {
    map_length_ = map_length;
  }

  
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("GarminMapDescription[");
    buffer.append("length=").append(map_length_).append(",");
    buffer.append("product_number=").append(product_number_).append(",");
    buffer.append("image_number=").append(image_number_).append(",");
    buffer.append("map_number=").append(map_number_).append(",");
    buffer.append("map_type='").append(map_type_).append("',");
    buffer.append("map_name='").append(map_name_).append("',");
    buffer.append("map_area='").append(map_area_).append("'");
    buffer.append("]");
    return(buffer.toString());
  }
}
