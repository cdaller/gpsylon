/***********************************************************************
 * @(#)$RCSfile$   $Revision$$Date$
 *
 * Copyright (c) 2003 IICM, Graz University of Technology
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


package org.dinopolis.gpstool.gpsinput.garmin;

//----------------------------------------------------------------------
/**
 * This class represents a package for communication with a garmin
 * device. It allows to set/get the data in various formats (int,
 * boolean, string, etc.). This class is NOT thread safe! Do NOT
 * read/write the data concurrently as unforeseen behaviour may (and
 * will!) result.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

class GarminPackage  
{
  public static final int GARMIN_MAX_PACKAGE_SIZE = 255;
  protected int[] data_;
  protected int package_id_;
  protected int package_size_;
  protected int put_index_;
  protected int get_index_;
  
//----------------------------------------------------------------------
/**
 * Constructor
 */

  public GarminPackage()
  {
    data_ = new int[GARMIN_MAX_PACKAGE_SIZE];
  }

//----------------------------------------------------------------------
/**
 * Constructor
 */

  public GarminPackage(int package_id, int data_size)
  {
    data_ = new int[data_size];
    package_id_ = package_id;
    package_size_ = data_size;
  }

//----------------------------------------------------------------------
/**
 * Get the package id.
 * @return the package id.
 */
  public int getPackageId()
  {
    return (package_id_);
  }

//----------------------------------------------------------------------
/**
 * Set the package id.
 * @param package_id The new package id.
 */
  public void setPackageId(int package_id)
  {
    package_id_ = package_id;
  }

//----------------------------------------------------------------------
/**
 * Get the package size.
 * @return the package size.
 */
  public int getPackageSize()
  {
    return (package_size_);
  }

//----------------------------------------------------------------------
/**
 * Set the package size value.
 * @param package_size The new package size.
 */
  public void setPackage_size(int package_size)
  {
    package_size_ = package_size;
  }
  
//----------------------------------------------------------------------
/**
 * Put another data-value to the package.
 * @param value the byte to add
 */
  public void put(int value)
  {
    data_[put_index_++] = value;
  }

//----------------------------------------------------------------------
/**
 * Get the next data value as byte.
 * @return the next byte
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 */
  public int get()
    throws IllegalStateException
  {
    if(get_index_ >= put_index_)
      throw new IllegalStateException("Not enough data available in package");
    return(data_[get_index_++]);
  }

  
// //----------------------------------------------------------------------
// /**
//  * Get the next data value as byte.
//  * @return the next byte
//  * @throws IllegalStateException on a try to read more bytes than were
//  * added before.
//  */
//   public byte getNextAsByte()
//     throws IllegalStateException
//   {
//     return(get());
//   }

// //----------------------------------------------------------------------
// /**
//  * Get the next data value as int.
//  * @return the next value as int
//  * @throws IllegalStateException on a try to read more bytes than were
//  * added before.
//  */
//   public int getNextAsInteger()
//     throws IllegalStateException
//   {
//     int value = GarminDataConverter.getGarminInt(data_,get_index_);
//     get_index_ += 2;
//     return(value);
//   }

// //----------------------------------------------------------------------
// /**
//  * Get the next data value as String.
//  * @return the next value as String
//  * @throws IllegalStateException on a try to read more bytes than were
//  * added before.
//  */
//   public String getNextAsString()
//     throws IllegalStateException
//   {
//     String value = GarminDataConverter.getGarminString(data_,get_index_);
//     get_index_ += value.length;
//     return(value);
//   }

//----------------------------------------------------------------------
/**
 * Return the checksum of the package
 * @return the checksum byte
 */
  public byte calcChecksum()
  {
    int checksum = (package_id_ & 0xff) + package_size_; 
    for (int index = 0; index < package_size_; index++) { 
      checksum += (data_[index] & 0xff);
    }
    checksum = -checksum;
    return (byte) checksum;
  }

  public char[] getCompatibilityBuffer()
  {
    char[] buffer = new char[package_size_ + 2];
      
    buffer[0] = (char)package_id_;
    buffer[1] = (char)package_size_;
    for(int index = 0; index < package_size_; index++)
    {
      buffer[index+2] = (char)data_[index];
    }
    return(buffer);
  }

  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("Garminpackage[id=").append(package_id_);
    buffer.append(",size=").append(package_size_);
    buffer.append(",data=[");
    for(int index = 0; index < package_size_; index++)
      buffer.append(data_[index]).append(" ");
    buffer.append("]]");
    return(buffer.toString());
  }
  
}


