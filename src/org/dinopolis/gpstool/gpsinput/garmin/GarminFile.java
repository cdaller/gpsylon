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

public class GarminFile
{
  int num_data_packages_;
  GarminPackage data_;
  
  public GarminFile(GarminPackage garmin_package)
  {
    if(garmin_package.getPackageSize() != 4)
      throw new IllegalArgumentException("File Header has size != 4");
    num_data_packages_ = garmin_package.getNextAsLongWord();
    data_ = new GarminPackage(0,0);
  }

  public int getDataPackageCount()
  {
    return(num_data_packages_);
  }

  public void addDataPackage(GarminPackage garmin_package)
  {
    int package_num = garmin_package.get();
    data_.appendData(garmin_package,1); // append all except first byte
  }

  //----------------------------------------------------------------------
/**
 * Get the next data value as byte.
 * @return the next byte
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 */
  public short getNextAsByte()
    throws IllegalStateException
  {
    return(data_.getNextAsByte());
  }

//----------------------------------------------------------------------
/**
 * Get the next data value as int (16bit, signed).
 * @return the next value as int
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 */
  public int getNextAsSignedInt()
    throws IllegalStateException
  {
    return(data_.getNextAsSignedInt());
  }

//----------------------------------------------------------------------
/**
 * Get the next data value as word (16bit, unsigned).
 * @return the next value as word
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 */
  public int getNextAsWord()
    throws IllegalStateException
  {
    return(data_.getNextAsWord());
  }
  

//----------------------------------------------------------------------
/**
 * Get the next data value as float.
 * @return the next value as float
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 */
  public float getNextAsFloat()
    throws IllegalStateException
  {
    return(data_.getNextAsFloat());
  }

  //----------------------------------------------------------------------
/**
 * Get the next data value as long.
 * @return the next value as long.
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 */
  public long getNextAsLong()
    throws IllegalStateException
  {
    return(data_.getNextAsLong());
  }


//----------------------------------------------------------------------
/**
 * Get the next data value as String.
 * @return the next value as String
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 */
  public String getNextAsString()
    throws IllegalStateException
  {
    return(data_.getNextAsString());
  }

//----------------------------------------------------------------------
/**
 * Get the next data value as String.
 * @param max_length the maximum length allowed for the string.
 * @return the next value as String
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 */
  public String getNextAsString(int max_length)
    throws IllegalStateException
  {
    return(data_.getNextAsString(max_length));
  }


  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("GarminFile[");
    buffer.append("num_packages=").append(num_data_packages_).append(",");
    if(data_.getPackageSize() > 1024)
      buffer.append("data (limited to 1024 bytes)=").append(data_.toString().substring(0,1024));
    else
      buffer.append("data=").append(data_);
    buffer.append("]");
    return(buffer.toString());
  }
}
