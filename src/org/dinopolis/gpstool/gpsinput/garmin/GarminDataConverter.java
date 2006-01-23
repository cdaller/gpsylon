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

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;



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

  /** garmin zero date (1.1.1990, 00:00) */
  public static long garmin_zero_date_seconds_;

  static
  {
		 TimeZone timezone = TimeZone.getTimeZone("Etc/UTC");
		 Calendar garmin_zero = Calendar.getInstance(timezone);
		 garmin_zero.set(Calendar.DAY_OF_MONTH,0);
		 garmin_zero.set(Calendar.MONTH,0);
		 garmin_zero.set(Calendar.YEAR,1990);
		 garmin_zero.set(Calendar.HOUR_OF_DAY,0);
		 garmin_zero.set(Calendar.MINUTE,0);
		 garmin_zero.set(Calendar.SECOND,0);
		 garmin_zero.set(Calendar.MILLISECOND,0);
 //     System.out.println("garmin garmin_zero_: "+garmin_zero.getTime()+" "+garmin_zero);
		 garmin_zero_date_seconds_ = garmin_zero.getTime().getTime() / 1000;
				 // alternative is to set the value directly (taken from gpspoint2):
 //    garmin_zero_date_seconds_ = 631065600L;
	 }

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
 * @param byteArray the byte array to be converted to garmin data.
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
    if(!zero_terminate)
    {
        // pad the rest with spaces:
      while(index < max_length)
      {
        buffer[offset+index] = ' ';  // pad with spaces
        index++;
      }
    }
    else
    {
      buffer[offset+index] = 0;
    }

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
 * Extracts an 16bit int (unsigned, java int) from the given character
 * buffer and returns it.
 *
 * @param buffer the character buffer to extract the integer from.
 * @param offset the offset to start reading the buffer.
 * @return the value extracted from the buffer.
 */
  public static int getGarminUnsignedInt(int[] buffer, int offset)
  {
    int value = (buffer[offset] & 0xff)
                | ((buffer[offset+1] & 0xff) << 8);
    return(value);
  }

//----------------------------------------------------------------------
/**
 * Convert a given int (16bit) to garmin data array.
 *
 * @param word the word to be converted to garmin data array.
 * @param buffer the buffer to write the value(s) to.
 * @param offset the offset to write the value(s) into the buffer.
 * @return the buffer holding the given value at the given position
 * (the rest is unchanged).
 * @throws ArrayIndexOutOfBoundsException if the buffer size is too
 * small to hold the given data.
 */
  public static int[] setGarminUnsignedInt(int word, int[] buffer, int offset)
  {
    buffer[offset] =  word & 0xff; 
    buffer[offset+1] = (word & 0xff00) >> 8;
    return(buffer);
  }


//----------------------------------------------------------------------
/**
 * Extracts an 16bit int (signed, java int) from the given character
 * buffer and returns it.
 *
 * @param buffer the character buffer to extract the integer from.
 * @param offset the offset to start reading the buffer.
 * @return the value extracted from the buffer.
 */
  public static int getGarminSignedInt(int[] buffer, int offset)
  {
		int value = getGarminUnsignedInt(buffer,offset);
		if((value & 0x8000) == 0)
			return(value);
		else
					// two's complement! (as java int  has 4 bytes, the 2 leading zero
					// bytes have to be filled by 0xff first, before the inverse two's
					// complement may be calculated!:
			return(-(~((value | 0xffff0000)-1)));  
  }

//----------------------------------------------------------------------
/**
 * Convert a given int (16bit, signed) to garmin data array.
 *
 * @param word the word to be converted to garmin data array.
 * @param buffer the buffer to write the value(s) to.
 * @param offset the offset to write the value(s) into the buffer.
 * @return the buffer holding the given value at the given position
 * (the rest is unchanged).
 * @throws ArrayIndexOutOfBoundsException if the buffer size is too
 * small to hold the given data.
 */
  public static int[] setGarminSignedInt(int word, int[] buffer, int offset)
  {
		if(word < 0)
		{
			word = ~(-word) + 1; // two's complement
			buffer[offset+1] = (word & 0x7f00 | 0x8000) >> 8; // copy data, set
																												// signed bit
		}
		else
		{
			buffer[offset+1] = (word & 0x7f00) >> 8;
		}
    buffer[offset] =  word & 0xff; 
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
		return(getGarminUnsignedInt(buffer,offset));
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
		return(setGarminUnsignedInt(word,buffer,offset));
  }
  
//----------------------------------------------------------------------
/**
 * Extracts a signed integer from the given character buffer and returns
 * it.
 *
 * @param buffer the character buffer to extract the integer from.
 * @param offset the offset to start reading the buffer.
 * @return the value extracted from the buffer.
 * @deprecated better use getGarminSignedInt, getGarminUnsignedInt (equals
 * to getGarminWord)
 */
  public static int getGarminInt(int[] buffer, int offset)
  {
		return(getGarminSignedInt(buffer,offset));
  }

//----------------------------------------------------------------------
/**
 * Convert a given signed integer to garmin data array.
 *
 * @param integer the integer to be converted to garmin data array.
 * @param buffer the buffer to write the value(s) to.
 * @param offset the offset to write the value(s) into the buffer.
 * @return the buffer holding the given value at the given position
 * (the rest is unchanged).
 * @throws ArrayIndexOutOfBoundsException if the buffer size is too
 * small to hold the given data.
 * @deprecated better use getGarminSignedInt, getGarminUnsignedInt (equals
 * to getGarminWord)
 */
  public static int[] setGarminInt(int integer, int[] buffer, int offset)
  {
		return(setGarminSignedInt(integer,buffer,offset));
  }


//----------------------------------------------------------------------
/**
 * Extracts an unsingned long (32bit) from the given buffer and
 * returns it.
 *
 * @param buffer the buffer to extract the integer from.
 * @param offset the offset to start reading the buffer.
 * @return the value extracted from the buffer.
 */
  public static long getGarminUnsignedLong(int[] buffer, int offset)
  {
    long value = (buffer[offset] & 0xff)
                | ((buffer[offset+1] & 0xff) << 8)
                | ((long)(buffer[offset+2] & 0xff) << 16)
                | ((long)(buffer[offset+3] & 0xff) << 24);
    return(value);
  }

//----------------------------------------------------------------------
/**
 * Convert a given unsigned long (only 32bit are used) to garmin data
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
  public static int[] setGarminUnsignedLong(long longword, int[] buffer, int offset)
  {
    buffer[offset] = (int)(longword & 0xffL);
    buffer[offset+1] = (int)(longword & 0xff00L) >> 8;   
    buffer[offset+2] = (int)(longword & 0xff0000L) >> 16; 
    buffer[offset+3] = (int)(longword & (int)(0xff000000L)) >> 24; 
    return(buffer);
  }

//----------------------------------------------------------------------
/**
 * Extracts an singned long (32bit) from the given buffer and
 * returns it.
 *
 * @param buffer the buffer to extract the integer from.
 * @param offset the offset to start reading the buffer.
 * @return the value extracted from the buffer.
 */
  public static long getGarminSignedLong(int[] buffer, int offset)
  {
		long value = getGarminUnsignedLong(buffer,offset);
		if((value & 0x80000000L) == 0)
			return(value);
		else
					// two's complement! (as java long  has 8 bytes, the 4 leading zero
					// bytes have to be filled by 0xff first, before the inverse two's
					// complement may be calculated!:
			return(-(~((value | 0xffffffff00000000L)-1)));  
  }

//----------------------------------------------------------------------
/**
 * Convert a given signed long (only 32bit are used) to garmin data
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
  public static int[] setGarminSignedLong(long longword, int[] buffer, int offset)
  {
		if(longword < 0)
		{
			longword = ~(-longword) + 1; // two's complement
			buffer[offset+3] = (int)(longword & 0x7f000000L | 0x80000000L) >> 24; // copy data, set signed bit
		}
		else
		{
			buffer[offset+3] = (int)(longword & 0x7f000000L) >> 24;
		}
    buffer[offset] = (int)longword & 0xff;
    buffer[offset+1] = (int)(longword & 0xff00) >> 8;   
    buffer[offset+2] = (int)(longword & 0xff0000L) >> 16; 
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
  public static long getGarminLongWord(int[] buffer, int offset)
  {
		return(getGarminUnsignedLong(buffer,offset));
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
  public static int[] setGarminLongWord(long longword, int[] buffer, int offset)
  {
		return(setGarminUnsignedLong(longword,buffer,offset));
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
    return(setGarminLongWord(integer,buffer,offset));
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
 * @param doub the double to be converted to garmin data array.
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
      return(convertSemicirclesToDegrees(getGarminSignedLong(buffer,offset)));
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
      return setGarminSignedLong(convertDegreesToSemicircles(degrees),buffer,offset);
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
 * @param semicircle
 * @return degrees
 */
  public static double convertSemicirclesToDegrees(long semicircle)
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
  public static long convertDegreesToSemicircles(double degrees)
  {
    return (long)(degrees * SEMICIRCLE_FACTOR);
  }

//----------------------------------------------------------------------
/**
 * Returns the seconds from 1.1.1990 from the given date or 0 if the given date
 * is null.
 *
 * @param date the date.
 * @return the seconds from 1.1.1990 from the given date.
 */
  public static long convertDateToGarminTime(Date date)
  {
    if(date == null)
      return(0);
    else
      return(date.getTime()/1000 - garmin_zero_date_seconds_);
  }
  
//----------------------------------------------------------------------
/**
 * Returns the date from the seconds since 1.1.1990 or null, if
 * <code>garmin_time</code> is <0.
 *
 * @param garmin_time the seconds.
 * @return the date from the seconds since 1.1.1990 or null.
 */
  public static Date convertGarminTimeToDate(long garmin_time)
  {
//     Calendar new_cal = (Calendar)garmin_zero_.clone();
//     new_cal.add(Calendar.SECOND,(int)garmin_time);
//     return(new_cal.getTime());
    if((garmin_time < 0) || (garmin_time == 0xffffffffL))
      return(null);
    
    return(new Date((garmin_zero_date_seconds_ + garmin_time) * 1000));
  }



	public static void main(String[] args)
	{
		int[] buffer = {90, 17, 52, 173};
		long value = getGarminLongWord(buffer,0);
		if(value != 2905870682L)
			System.out.println("Wrong: "+value);
		else
			System.out.println("Right!");

		int[] buffer1 = {255, 255};
		int value1 = getGarminSignedInt(buffer1,0);
		if(value1 != -1)
			System.out.println("Wrong: "+value1);
		else
			System.out.println("Right!");

		buffer1 = setGarminSignedInt(-1,buffer1,0);
		if((buffer1[0] != 255) && (buffer1[1] != 255))
			System.out.println("Wrong! " +Integer.toHexString(buffer1[0])+","+Integer.toHexString(buffer1[1]));
		else
			System.out.println("Right!");

		int[] buffer2 = {255,255,255,255};
		long value2 = getGarminSignedLong(buffer2,0);
		if(value2 != -1)
			System.out.println("Wrong: "+value2);
		else
			System.out.println("Right!");

		buffer2 = setGarminSignedInt(-1,buffer2,0);
		if((buffer2[0] != 255) && (buffer2[1] != 255) && (buffer2[2] != 255) && (buffer2[3] != 255))
			System.out.println("Wrong! " 
												 +Integer.toHexString(buffer2[0])+","
												 +Integer.toHexString(buffer2[1])+","
												 +Integer.toHexString(buffer2[2])+","
												 +Integer.toHexString(buffer2[3]));
		else
			System.out.println("Right!");

		Date now = new Date();
		long garmin_now = convertDateToGarminTime(now);
		System.out.println(now+":"+Long.toHexString(garmin_now));
		System.out.println(now+":"+garmin_now);
		System.out.println(convertGarminTimeToDate(garmin_now));
		buffer2 = setGarminLongWord(garmin_now,buffer2,0);
		long garmin_now2 = getGarminLongWord(buffer2,0);
		System.out.println(convertGarminTimeToDate(garmin_now2));
		System.out.println("zero:"+convertGarminTimeToDate(0));

	}
}

