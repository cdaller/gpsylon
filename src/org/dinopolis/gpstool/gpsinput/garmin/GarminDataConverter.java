/***********************************************************************
 * @(#)$RCSfile$   $Revision$ $Date$
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
 * Helper class for converting garmin data arrays to java datatypes
 * and vice versa.
 *
 * @author Christof Dallermassl, Stefan Feitl
 * @version $Revision$
 */

public class GarminDataConverter
{
  /** Semicircle to Degrees conversion values */
  protected final static double SEMICIRCLE_FACTOR = (double)( 1L << 31 ) / 180.0d;

//----------------------------------------------------------------------
/**
 * Extracts a byte array from the given character buffer and returns
 * it.
 *
 * @param buffer the character buffer to extract the string from.
 * @param offset the offset to start reading the buffer.
 * @param length the length of the array.
 * @return the value extracted from the buffer.
 */
  public static byte[] getGarminByteArray(int[] buffer, int offset, int length)
  {
    byte[] value = new byte[length];
    int index = length - 1;
    while(index >= 0)
    {
      value[index] = (byte)(buffer[offset+index] & 0xff);
      index--;
    }
        // does not work due to different types:
        //System.arraycopy(buffer,offset,value,0,length);
    return(value);
  }

//----------------------------------------------------------------------
/**
 * Generate a garmin data array from the given byte array.
 *
 * @param array the byte array to be converted to garmin data.
 * @param buffer the buffer to write the value(s) to.
 * @param offset the offset to write the value(s) into the buffer.
 * @return the buffer holding the given value at the given position
 * (the rest is unchanged).
 * @throws ArrayIndexOutOfBoundsException if the buffer size is too
 * small to hold the given data.
 */
  public static int[] setGarminByteArray(byte[] byteArray, int[] buffer, int offset)
  {
    for(int index = 0; index < byteArray.length; index++)
    {
      buffer = setGarminByte(byteArray[index],buffer,offset+index);
    }
    return(buffer);
  }

//----------------------------------------------------------------------
/**
 * Extracts a zero-terminated string from the given character buffer
 * and returns it.
 *
 * @param buffer the character buffer to extract the string from.
 * @param offset the offset to start reading the buffer.
 * @return the value extracted from the buffer.
 */
  public static String getGarminString(int[] buffer, int offset)
  {
    return(getGarminString(buffer,offset,buffer.length));
  }
  
  
//----------------------------------------------------------------------
/**
 * Extracts a zero-terminated string from the given character buffer
 * and returns it.
 *
 * @param buffer the character buffer to extract the string from.
 * @param offset the offset to start reading the buffer.
 * @param max_length max length of the string.
 * @return the value extracted from the buffer.
 */
  public static String getGarminString(int[] buffer, int offset, int max_length)
  {
    int ch;
    StringBuffer result = new StringBuffer();
    int max_index = Math.min(offset + max_length,buffer.length);

    for(int index = offset; index < max_index; index++)
    {
      ch = buffer[index];
      if(ch != 0)
        result.append((char)ch);
      else
      {
        return(result.toString());
      }
    }
    return(result.toString());
  }

//----------------------------------------------------------------------
/**
 * Convert a given string to garmin data array.
 *
 * @param string the string to write to the buffer
 * @param buffer the buffer to write the value(s) to.
 * @param offset the offset to write the value(s) into the buffer.
 * @param max_length the maximum length the string may have (if the
 * string should be zero terminated, this length includes the yero
 * termination).
 * @param zero_terminate zero terminate the string.
 * @return the buffer holding the given value at the given position
 * (the rest is unchanged).
 * @throws ArrayIndexOutOfBoundsException if the buffer size is too
 * small to hold the given data.
 */
  public static int[] setGarminString(String string, int [] buffer, int offset,
                                      int max_length, boolean zero_terminate)
  {
    if(zero_terminate)
      max_length--;
    int index = 0;
    while((index < string.length()) && (index < max_length))
    {
      buffer[offset+index] = (int)string.charAt(index);
      index++;
    }
    if(zero_terminate)
      buffer[offset+index] = 0;

    return(buffer);
  }

//----------------------------------------------------------------------
/**
 * Extracts an boolean from the given character buffer and returns
 * it.
 *
 * @param buffer the character buffer to extract the boolean from.
 * @param offset the offset to start reading the buffer.
 * @return the value extracted from the buffer.
 */
  public static boolean getGarminBoolean(int[] buffer, int offset)
  {
    return(buffer[offset] != 0);
  }

//----------------------------------------------------------------------
/**
 * Convert a given boolean to garmin data array.
 *
 * @param bool the boolean to be converted to garmin data array.
 * @param buffer the buffer to write the value(s) to.
 * @param offset the offset to write the value(s) into the buffer.
 * @return the buffer holding the given value at the given position
 * (the rest is unchanged).
 * @throws ArrayIndexOutOfBoundsException if the buffer size is too
 * small to hold the given data.
 */
  public static int[] setGarminBoolean(boolean bool, int[] buffer, int offset)
  {
    if (bool)
      buffer[offset] = 1;
    else
      buffer[offset] = 0;
      
    return(buffer);
  }

//----------------------------------------------------------------------
/**
 * Extracts an byte (unsigned java short) from the given character buffer and
 * returns it.
 *
 * @param buffer the character buffer to extract the byte from.
 * @param offset the offset to start reading the buffer.
 * @return the value extracted from the buffer.
 */
  public static short getGarminByte(int[] buffer, int offset)
  {
    return((short)(buffer[offset] & 0xff));
  }

//----------------------------------------------------------------------
/**
 * Convert a given java short to garmin data array.
 *
 * @param byt the short to be converted to garmin data array.
 * @param buffer the buffer to write the value(s) to.
 * @param offset the offset to write the value(s) into the buffer.
 * @return the buffer holding the given value at the given position
 * (the rest is unchanged).
 * @throws ArrayIndexOutOfBoundsException if the buffer size is too
 * small to hold the given data.
 */
  public static int[]  setGarminByte(int byt, int[] buffer, int offset)
  {
    buffer[offset] = (byt & 0xff);
    return(buffer);
  }
  
//----------------------------------------------------------------------
/**
 * Extracts an word (unsigned, java int) from the given character
 * buffer and returns it.
 *
 * @param buffer the character buffer to extract the integer from.
 * @param offset the offset to start reading the buffer.
 * @return the value extracted from the buffer.
 */
  public static int getGarminWord(int[] buffer, int offset)
  {
    int value = (buffer[offset] & 0xff)
                | ((buffer[offset+1] & 0xff) << 8);
    return(value);
  }

//----------------------------------------------------------------------
/**
 * Convert a given word (16bit integer) to garmin data array.
 *
 * @param word the word to be converted to garmin data array.
 * @param buffer the buffer to write the value(s) to.
 * @param offset the offset to write the value(s) into the buffer.
 * @return the buffer holding the given value at the given position
 * (the rest is unchanged).
 * @throws ArrayIndexOutOfBoundsException if the buffer size is too
 * small to hold the given data.
 */
  public static int[] setGarminWord(int word, int[] buffer, int offset)
  {
    buffer[offset] =  word & 0xff; 
    buffer[offset+1] = (word & 0xff00) >> 8;
    return(buffer);
  }
  
//----------------------------------------------------------------------
/**
 * Extracts an integer from the given character buffer and returns
 * it.
 *
 * @param buffer the character buffer to extract the integer from.
 * @param offset the offset to start reading the buffer.
 * @return the value extracted from the buffer.
 */
  public static int getGarminInt(int[] buffer, int offset)
  {
    int value = (buffer[offset] & 0xff)
                | ((buffer[offset+1] & 0xff) << 8)
                | ((buffer[offset+2] & 0xff) << 16)
                | ((buffer[offset+3] & 0xff) << 24);
    return(value);
  }

//----------------------------------------------------------------------
/**
 * Convert a given integer to garmin data array.
 *
 * @param integer the integer to be converted to garmin data array.
 * @param buffer the buffer to write the value(s) to.
 * @param offset the offset to write the value(s) into the buffer.
 * @return the buffer holding the given value at the given position
 * (the rest is unchanged).
 * @throws ArrayIndexOutOfBoundsException if the buffer size is too
 * small to hold the given data.
 */
  public static int[] setGarminInt(int integer, int[] buffer, int offset)
  {
    buffer[offset] = integer & 0xff;
    buffer[offset+1] = (integer & (int)(0xff << 8)) >> 8;   
    buffer[offset+2] = (integer & (int)(0xff << 16)) >> 16; 
    buffer[offset+3] = (integer & (int)(0xff << 24)) >> 24; 
    return(buffer);
  }

//----------------------------------------------------------------------
/**
 * Extracts an longword (unsinged 32bit) from the given buffer and
 * returns it.
 *
 * @param buffer the buffer to extract the integer from.
 * @param offset the offset to start reading the buffer.
 * @return the value extracted from the buffer.
 */
  public static long getGarminLong(int[] buffer, int offset)
  {
    long value = (buffer[offset] & 0xff)
                | ((buffer[offset+1] & 0xff) << 8)
                | ((buffer[offset+2] & 0xff) << 16)
                | ((buffer[offset+3] & 0xff) << 24);
    return(value);
  }

//----------------------------------------------------------------------
/**
 * Convert a given long (unsigned, only 32bit are used) to garmin data
 * array.
 *
 * @param longword the long value to be converted to garmin data
 * array.
 * @param buffer the buffer to write the value(s) to.
 * @param offset the offset to write the value(s) into the buffer.
 * @return the buffer holding the given value at the given position
 * (the rest is unchanged).
 * @throws ArrayIndexOutOfBoundsException if the buffer size is too
 * small to hold the given data.
 */
  public static int[] setGarminLong(long longword, int[] buffer, int offset)
  {
    buffer[offset] = (int)longword & 0xff;
    buffer[offset+1] = (int)(longword & (int)(0xff << 8)) >> 8;   
    buffer[offset+2] = (int)(longword & (int)(0xff << 16)) >> 16; 
    buffer[offset+3] = (int)(longword & (int)(0xff << 24)) >> 24; 
    return(buffer);
  }

//----------------------------------------------------------------------
/**
 * Extracts an float from the given character buffer and returns
 * it.
 *
 * @param buffer the character buffer to extract the float from.
 * @param offset the offset to start reading the buffer.
 * @return the value extracted from the buffer.
 */
  public static float getGarminFloat(int[] buffer, int offset)
  {
    int value = (buffer[offset] & 0xff)
                | ((buffer[offset+1] & 0xff) << 8)
                | ((buffer[offset+2] & 0xff) << 16)
                | ((buffer[offset+3] & 0xff) << 24);
    return(Float.intBitsToFloat(value));
  }

//----------------------------------------------------------------------
/**
 * Convert a given float to garmin data array.
 *
 * @param flo the float to be converted to garmin data array.
 * @param buffer the buffer to write the value(s) to.
 * @param offset the offset to write the value(s) into the buffer.
 * @return the buffer holding the given value at the given position
 * (the rest is unchanged).
 * @throws ArrayIndexOutOfBoundsException if the buffer size is too
 * small to hold the given data.
 */
  public static int[] setGarminFloat(float flo,int[] buffer, int offset)
  {
    int integer = Float.floatToRawIntBits(flo);
    return(setGarminInt(integer,buffer,offset));
  }
  
//----------------------------------------------------------------------
/**
 * Extracts an double from the given character buffer and returns
 * it.
 *
 * @param buffer the character buffer to extract the double from.
 * @param offset the offset to start reading the buffer.
 * @return the value extracted from the buffer.
 */
  public static double getGarminDouble(int[] buffer, int offset)
  {
    long value =  ((long)(buffer[offset] & 0xff))
                  | ((long)(buffer[offset+1] & 0xff)) << 8
                  | ((long)(buffer[offset+2] & 0xff)) << 16
                  | ((long)(buffer[offset+3] & 0xff)) << 24
                  | ((long)(buffer[offset+4] & 0xff)) << 32
                  | ((long)(buffer[offset+5] & 0xff)) << 40
                  | ((long)(buffer[offset+6] & 0xff)) << 48
                  | ((long)(buffer[offset+7] & 0xff)) << 56;
    
    return Double.longBitsToDouble(value);
  }
  
//----------------------------------------------------------------------
/**
 * Convert a given double to garmin data array.
 *
 * @param double the double to be converted to garmin data array.
 * @param buffer the buffer to write the value(s) to.
 * @param offset the offset to write the value(s) into the buffer.
 * @return the buffer holding the given value at the given position
 * (the rest is unchanged).
 * @throws ArrayIndexOutOfBoundsException if the buffer size is too
 * small to hold the given data.
 */
  public static int[] setGarminDouble(double doub, int[] buffer, int offset)
  {
    long value = Double.doubleToRawLongBits(doub);
    
    buffer[offset+0]=(int)((value & (0xff << 0)) >> 0);
    buffer[offset+1]=(int)((value & (0xff << 8)) >> 8);
    buffer[offset+2]=(int)((value & (0xff << 16)) >> 16);
    buffer[offset+3]=(int)((value & (0xff << 24)) >> 24);
    buffer[offset+4]=(int)(((value >> 32) & (0xff << 0)) >> 0);
    buffer[offset+5]=(int)(((value >> 32) & (0xff << 8)) >> 8);
    buffer[offset+6]=(int)(((value >> 32) & (0xff << 16)) >> 16);
    buffer[offset+7]=(int)(((value >> 32) & (0xff << 24)) >> 24);
    
    return(buffer);
  }

//----------------------------------------------------------------------
/**
 * Extracts a degree value from the given character buffer and
 * returns it. It extracs a semicircle value and converts it to
 * degrees afterwards.
 *
 * @param buffer the character buffer to extract the boolean from.
 * @param offset the offset to start reading the buffer.
 * @return the value extracted from the buffer.
 */
  public static double getGarminSemicircleDegrees(int[] buffer, int offset)
  {
      return(convertSemicirclesToDegrees(getGarminInt(buffer,offset)));
  }

//----------------------------------------------------------------------
/**
 * Convert given degrees (java double) to garmin data array (semicircles).
 *
 * @param degrees the degree value to be converted to garmin data array.
 * @param buffer the buffer to write the value(s) to.
 * @param offset the offset to write the value(s) into the buffer.
 * @return the buffer holding the given value at the given position
 * (the rest is unchanged).
 * @throws ArrayIndexOutOfBoundsException if the buffer size is too
 * small to hold the given data.
 */
  public static int[] setGarminSemicircleDegrees(double degrees,int[] buffer, int offset)
  {
      return setGarminInt(convertDegreesToSemicircles(degrees),buffer,offset);
  }

//----------------------------------------------------------------------
/**
 * Extracts a degree value from the given character buffer and
 * returns it. It extracs a radiant value and converts it to
 * degrees afterwards.
 *
 * @param buffer the character buffer to extract the boolean from.
 * @param offset the offset to start reading the buffer.
 * @return the value extracted from the buffer.
 */
  public static double getGarminRadiantDegrees(int[] buffer, int offset)
  {
    return Math.toDegrees(getGarminDouble(buffer,offset));
  }

//----------------------------------------------------------------------
/**
 * Convert given degrees (java double) to garmin data array (radiant).
 *
 * @param degrees the degree value to be converted to garmin data array.
 * @param buffer the buffer to write the value(s) to.
 * @param offset the offset to write the value(s) into the buffer.
 * @return the buffer holding the given value at the given position
 * (the rest is unchanged).
 * @throws ArrayIndexOutOfBoundsException if the buffer size is too
 * small to hold the given data.
 */
  public static int[] setGarminRadiantDegrees(double degrees,int[] buffer, int offset)
  {
    return setGarminDouble(Math.toRadians(degrees),buffer,offset);
  }

//----------------------------------------------------------------------
/**
 * Converts Semicircles to Degrees.
 *
 * @param semicircles
 * @return degrees
 */
  public static double convertSemicirclesToDegrees(int semicircle)
  {
    return((double)semicircle / SEMICIRCLE_FACTOR);
  }

//----------------------------------------------------------------------
/**
 * Converts Degrees to Semicircles.
 *
 * @param degrees
 * @return semicircles
 */
  public static int convertDegreesToSemicircles(double degrees)
  {
    return (int)(degrees * SEMICIRCLE_FACTOR);
  }
}

