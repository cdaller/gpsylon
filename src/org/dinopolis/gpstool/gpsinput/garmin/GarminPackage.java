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
 * @author Christof Dallermassl, Stefan Feitl
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
  }

//----------------------------------------------------------------------
/**
 * Constructor
 *
 * @param package_id the package id of the garmin package
 * @param data_size the number of data bytes in this package
 * (excluding package id).
 */

  public GarminPackage(int package_id, int data_size)
  {
    setPackageId(package_id);
    initializeData(data_size);
  }

//----------------------------------------------------------------------
/**
 * Set the size of this garmin package (removes all data that was
 * formerly in this package!).
 *
 * @param data_size the number of data bytes in this package
 * (excluding package id).
 */
  public void initializeData(int data_size)
  {
    data_ = new int[data_size];
    package_size_ = data_size;
  }

//----------------------------------------------------------------------
/**
 * Append the data of the given package to the data of this package.
 *
 * @param garmin_package the package to append (the data of the package).
 */
  public void appendData(GarminPackage garmin_package)
  {
    appendData(garmin_package,0);
  }

//----------------------------------------------------------------------
/**
 * Append the data of the given package (starting at the given offset)
 * to the data of this package.
 *
 * @param garmin_package the package to append (the data of the package).
 * @param offset the offset to start copying the data.
 */
  public void appendData(GarminPackage garmin_package, int offset)
  {
    int[] new_data_ = new int[package_size_ + garmin_package.getPackageSize()-offset];
    System.arraycopy(data_,0,new_data_,0,package_size_); // copy old
    int[] add_data = garmin_package.getRawData();
    System.arraycopy(add_data,offset,new_data_,package_size_,add_data.length-offset);
    data_ = new_data_;
    package_size_ = new_data_.length;
    put_index_ = new_data_.length;
  }

//----------------------------------------------------------------------
/**
 * Return the raw data of the package.
 * @return the raw data of the package.
 */
  public int[] getRawData()
  {
    return(data_);
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
 * Put an array of data-values to the package.
 * @param value the bytes to add
 */
  public void put(int[] value)
  {
      for (int i=0; i < value.length; i++)
	  data_[put_index_++] = value[i];
  }

//----------------------------------------------------------------------
/**
 * Get the next data value as byte.
 * @return the next byte
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 */
  public short get()
    throws IllegalStateException
  {
    if(get_index_ >= put_index_)
      throw new IllegalStateException("Not enough data available in package");
    return((short)data_[get_index_++]);
  }

//----------------------------------------------------------------------
/**
 * Get the byte on the given offset.
 *
 * @param offset the position to return.
 * @return the byte at the given position
 */
  public short get(int offset)
  {
    return((short)data_[offset]);
  }


//----------------------------------------------------------------------
/**
 * Get the boolean on the given offset.
 *
 * @param offset the position to return.
 * @return the boolean at the given position
 */
  public boolean getBoolean(int offset)
  {
    return(GarminDataConverter.getGarminBoolean(data_,offset));
  }

//----------------------------------------------------------------------
/**
 * Get the byte on the given offset.
 *
 * @param offset the position to return.
 * @return the byte at the given position
 */
  public int getByte(int offset)
  {
    return(get(offset));
  }

//----------------------------------------------------------------------
/**
 * Get the word on the given offset.
 *
 * @param offset the position to return.
 * @return the word at the given position
 */
  public int getWord(int offset)
  {
    return(GarminDataConverter.getGarminWord(data_,offset));
  }

//----------------------------------------------------------------------
/**
 * Get the int on the given offset.
 *
 * @param offset the position to return.
 * @return the integer at the given position
 */
  public int getInt(int offset)
  {
    return(GarminDataConverter.getGarminInt(data_,offset));
  }

//----------------------------------------------------------------------
/**
 * Get the float on the given offset.
 *
 * @param offset the position to return.
 * @return the float at the given position
 */
  public float getFloat(int offset)
  {
    return(GarminDataConverter.getGarminFloat(data_,offset));
  }

//----------------------------------------------------------------------
/**
 * Get the long on the given offset.
 *
 * @param offset the position to return.
 * @return the long at the given position
 */
  public long getLong(int offset)
  {
    return(GarminDataConverter.getGarminLong(data_,offset));
  }

//----------------------------------------------------------------------
/**
 * Get the double on the given offset.
 *
 * @param offset the position to return.
 * @return the double at the given position
 */
  public double getDouble(int offset)
  {
    return(GarminDataConverter.getGarminDouble(data_,offset));
  }

//----------------------------------------------------------------------
/**
 * Get the byte array on the given offset with the given length.
 *
 * @param offset the position to return.
 * @return the byte array at the given position
 */
  public byte[] getNextAsByteArray(int offset, int length)
  {
    return(GarminDataConverter.getGarminByteArray(data_,offset,length));
  }

//----------------------------------------------------------------------
/**
 * Get the string on the given offset.
 *
 * @param offset the position to return.
 * @return the string at the given position
 */
  public String getString(int offset)
  {
    return(GarminDataConverter.getGarminString(data_,offset));
  }

//----------------------------------------------------------------------
/**
 * Get the string on the given offset.
 *
 * @param offset the position to return.
 * @param max_length the maximum length of the string
 * @return the string at the given position
 */
  public String getString(int offset, int max_length)
  {
    return(GarminDataConverter.getGarminString(data_,offset,max_length));
  }

//----------------------------------------------------------------------
/**
 * Get the semicircle degrees on the given offset.
 *
 * @param offset the position to return.
 * @return the semicircle degrees at the given position
 */
  public double getSemicircleDegrees(int offset)
  {
    return(GarminDataConverter.getGarminSemicircleDegrees(data_,offset));
  }

//----------------------------------------------------------------------
/**
 * Get the radiant degrees on the given offset.
 *
 * @param offset the position to return.
 * @return the radiant degrees at the given position
 */
  public double getRadiantDegrees(int offset)
  {
    return(GarminDataConverter.getGarminRadiantDegrees(data_,offset));
  }

  
//----------------------------------------------------------------------
/**
 * Get the next data value as boolean.
 * @return the next boolean
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 */
  public boolean getNextAsBoolean()
    throws IllegalStateException
  {
    boolean value = GarminDataConverter.getGarminBoolean(data_,get_index_);
    get_index_++;
    return(value);
  }

//----------------------------------------------------------------------
/**
 * Set the next data value as boolean.
 * @param value the next value as boolean
 */
  public void setNextAsBoolean(boolean value)
  {
    data_ = GarminDataConverter.setGarminBoolean(value,data_,put_index_);
    put_index_ += 1;
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
    int value = GarminDataConverter.getGarminByte(data_,get_index_);
    get_index_++;
    return((short)value);
  }

//----------------------------------------------------------------------
/**
 * Set the next data value as byte.
 * @param value the next value as byte
 */
  public void setNextAsByte(int value)
  {
    data_ = GarminDataConverter.setGarminByte(value,data_,put_index_);
    put_index_ += 1;
  }

//----------------------------------------------------------------------
/**
 * Get the next data value as int.
 * @return the next value as int
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 */
  public int getNextAsInt()
    throws IllegalStateException
  {
    int value = GarminDataConverter.getGarminInt(data_,get_index_);
    get_index_ += 4;
    return(value);
  }

//----------------------------------------------------------------------
/**
 * Set the next data value as int.
 * @param value the next value as int
 */
  public void setNextAsInt(int value)
  {
    data_ = GarminDataConverter.setGarminInt(value,data_,put_index_);
    put_index_ += 4;
  }

//----------------------------------------------------------------------
/**
 * Get the next data value as word.
 * @return the next value as word
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 */
  public int getNextAsWord()
    throws IllegalStateException
  {
    int value = GarminDataConverter.getGarminWord(data_,get_index_);
    get_index_ += 2;
    return(value);
  }


//----------------------------------------------------------------------
/**
 * Set the next data value as word.
 * @param value the next value as word
 */
  public void setNextAsWord(int value)
  {
    data_ = GarminDataConverter.setGarminWord(value,data_,put_index_);
    put_index_ += 2;
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
    float value = GarminDataConverter.getGarminFloat(data_,get_index_);
    get_index_ += 4;
    return(value);
  }

//----------------------------------------------------------------------
/**
 * Set the next data value as float.
 * @param value the next value as float
 */
  public void setNextAsFloat(float value)
  {
    data_ = GarminDataConverter.setGarminFloat(value,data_,put_index_);
    put_index_ += 4;
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
    long value = GarminDataConverter.getGarminLong(data_,get_index_);
    get_index_ += 4;
    return(value);
  }

//----------------------------------------------------------------------
/**
 * Set the next data value as long.
 * @param value the next value as long
 */
  public void setNextAsLong(long value)
  {
    data_ = GarminDataConverter.setGarminLong(value,data_,put_index_);
    put_index_ += 4;
  }

//----------------------------------------------------------------------
/**
 * Get the next data value as double.
 * @return the next value as double.
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 */
  public double getNextAsDouble()
    throws IllegalStateException
  {
    double value = GarminDataConverter.getGarminDouble(data_,get_index_);
    get_index_ += 8;
    return(value);
  }

//----------------------------------------------------------------------
/**
 * Set the next data value as double.
 * @param value the next value as double
 */
  public void setNextAsDouble(double value)
  {
    data_ = GarminDataConverter.setGarminDouble(value,data_,put_index_);
    put_index_ += 8;
  }

//----------------------------------------------------------------------
/**
 * Get the next data values as byte array.
 * @return the next values as byte array
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 */
  public byte[] getNextAsByteArray(int length)
    throws IllegalStateException
  {
    byte[] value=new byte[length];
    value = GarminDataConverter.getGarminByteArray(data_,get_index_,length);
    get_index_ += length;
    return(value);
  }

//----------------------------------------------------------------------
/**
 * Set the next data values as byte array.
 * @param value the next values as byte array
 */
  public void setNextAsByteArray(byte[] value)
  {
    data_ = GarminDataConverter.setGarminByteArray(value,data_,put_index_);
    put_index_ += value.length;
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
    String value = GarminDataConverter.getGarminString(data_,get_index_);
    get_index_ += value.length()+1; // length + zero termination
    return(value);
  }

//----------------------------------------------------------------------
/**
 * Set the next data value as zero terminated string.
 * @param value the next value as long
 */
  public void setNextAsString(String value)
  {
    int length = value.length() + 1;
    data_ = GarminDataConverter.setGarminString(value,data_,put_index_,
						length, true);
    put_index_ += length;
  }

//----------------------------------------------------------------------
/**
 * Set the next data value as long.
 * @param value the next value as long
 * @param max_length the maximum length the string may have (if the
 * string should be zero terminated, this length includes the zero
 * termination).
 * @param zero_terminate zero terminate the string.
 */
  public void setNextAsString(String value, int max_length, boolean zero_terminate)
  {
    data_ = GarminDataConverter.setGarminString(value,data_,put_index_,
						max_length, zero_terminate);
    if(zero_terminate)
      put_index_ += Math.min(value.length()+1,max_length);
    else
      put_index_ += max_length;
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
    String value = GarminDataConverter.getGarminString(data_,get_index_,max_length);
    get_index_ += value.length();
    return(value);
  }

//----------------------------------------------------------------------
/**
 * Get the next data value as semicircle degrees.
 * @return the next value as semicircle degrees
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 */
  public double getNextAsSemicircleDegrees()
    throws IllegalStateException
  {
    double value = GarminDataConverter.getGarminSemicircleDegrees(data_,get_index_);
    get_index_ += 4;
    return(value);
  }

//----------------------------------------------------------------------
/**
 * Set the next data value as semicircle degrees.
 * @param value the next value as semicircle degrees
 */
  public void setNextAsSemicircleDegrees(double value)
  {
    data_ = GarminDataConverter.setGarminSemicircleDegrees(value,data_,put_index_);
    put_index_ += 4;
  }

//----------------------------------------------------------------------
/**
 * Get the next data value as radiant degrees.
 * @return the next value as radiant degrees
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 */
  public double getNextAsRadiantDegrees()
    throws IllegalStateException
  {
    double value = GarminDataConverter.getGarminRadiantDegrees(data_,get_index_);
    get_index_ += 8;
    return(value);
  }

//----------------------------------------------------------------------
/**
 * Set the next data value as radiant degrees.
 * @param value the next value as radiant degrees
 */
  public void setNextAsRadiantDegrees(double value)
  {
    data_ = GarminDataConverter.setGarminRadiantDegrees(value,data_,put_index_);
    put_index_ += 8;
  }

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
    return((byte)checksum);
  }

  public int[] getCompatibilityBuffer()
  {
    int[] buffer = new int[package_size_ + 2];
      
    buffer[0] = package_id_;
    buffer[1] = package_size_;
    for(int index = 0; index < package_size_; index++)
    {
      buffer[index+2] = data_[index];
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
      buffer.append(data_[index]).append("/").append((char)data_[index]).append(" ");
    buffer.append("]]");
    return(buffer.toString());
  }


  public static void main(String[] args)
  {
    String teststring = "hallo";
    GarminPackage gp = new GarminPackage(1,4+2+teststring.length()+1);
    gp.setNextAsInt(123456);
    gp.setNextAsWord(1245);
    gp.setNextAsString(teststring);

    int word = 1234;
    System.out.println("1234 as byte:");
    System.out.println(word & 0xff); 
    System.out.println((word & 0xff00) >> 8);
    
    System.out.println(gp.getNextAsInt());
    System.out.println(gp.getNextAsWord());
    System.out.println(gp.getNextAsString());

//     int value;
//     for(int index = 0; index < gp.getPackageSize(); index++)
//     {
//       value = gp.get();
//       System.out.println("byte: "+index+":"+value+":"+(char)value);
//     }
    System.out.println(gp);
  }
}
