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
 * This class represents a packet for communication with a garmin
 * device. It allows to set/get the data in various formats (int,
 * boolean, string, etc.). This class is NOT thread safe! Do NOT
 * read/write the data concurrently as unforeseen behaviour may (and
 * will!) result.
 *
 * @author Christof Dallermassl, Stefan Feitl
 * @version $Revision$
 */

class GarminPacket  
{
  public static final int GARMIN_MAX_PACKET_SIZE = 255;
  protected int[] data_;
  protected int packet_id_;
  protected int packet_size_;
  protected int put_index_;
  protected int get_index_;
  
//----------------------------------------------------------------------
/**
 * Constructor
 */

  public GarminPacket()
  {
  }

//----------------------------------------------------------------------
/**
 * Constructor
 *
 * @param packet_id the packet id of the garmin packet
 * @param data_size the number of data bytes in this packet
 * (excluding packet id).
 */

  public GarminPacket(int packet_id, int data_size)
  {
    setPacketId(packet_id);
    initializeData(data_size);
  }

//----------------------------------------------------------------------
/**
 * Set the size of this garmin packet (removes all data that was
 * formerly in this packet!).
 *
 * @param data_size the number of data bytes in this packet
 * (excluding packet id).
 */
  public void initializeData(int data_size)
  {
    data_ = new int[data_size];
    packet_size_ = data_size;
		reset();
  }

//----------------------------------------------------------------------
/**
 * Append the data of the given packet to the data of this packet.
 *
 * @param garmin_packet the packet to append (the data of the packet).
 */
  public void appendData(GarminPacket garmin_packet)
  {
    appendData(garmin_packet,0);
  }

//----------------------------------------------------------------------
/**
 * Append the data of the given packet (starting at the given offset)
 * to the data of this packet.
 *
 * @param garmin_packet the packet to append (the data of the packet).
 * @param offset the offset to start copying the data.
 */
  public void appendData(GarminPacket garmin_packet, int offset)
  {
    int[] new_data_ = new int[packet_size_ + garmin_packet.getPacketSize()-offset];
    System.arraycopy(data_,0,new_data_,0,packet_size_); // copy old
    int[] add_data = garmin_packet.getRawData();
    System.arraycopy(add_data,offset,new_data_,packet_size_,add_data.length-offset);
    data_ = new_data_;
    packet_size_ = new_data_.length;
    put_index_ = new_data_.length;
  }

//----------------------------------------------------------------------
/**
 * Return the raw data of the packet.
 * @return the raw data of the packet.
 */
  public int[] getRawData()
  {
    return(data_);
  }
  
//----------------------------------------------------------------------
/**
 * Get the packet id.
 * @return the packet id.
 */
  public int getPacketId()
  {
    return (packet_id_);
  }

//----------------------------------------------------------------------
/**
 * Set the packet id.
 * @param packet_id The new packet id.
 */
  public void setPacketId(int packet_id)
  {
    packet_id_ = packet_id;
  }

//----------------------------------------------------------------------
/**
 * Get the packet size.
 * @return the packet size.
 */
  public int getPacketSize()
  {
    return (packet_size_);
  }

//----------------------------------------------------------------------
/**
 * Set the packet size value.
 * @param packet_size The new packet size.
 */
  public void setPacket_size(int packet_size)
  {
    packet_size_ = packet_size;
  }
  
//----------------------------------------------------------------------
/**
 * Put another data-value to the packet.
 * @param value the byte to add
 */
  public void put(int value)
  {
    data_[put_index_++] = value;
  }

//----------------------------------------------------------------------
/**
 * Put an array of data-values to the packet.
 * @param value the bytes to add
 */
  public void put(int[] value)
  {
      for (int i=0; i < value.length; i++)
	  data_[put_index_++] = value[i];
  }
  
//----------------------------------------------------------------------
  /**
   * Checks there are more bytes to read.
   * @throws IllegalStateException on a try to read more bytes than were
   * added before.
   * Added by MR
   */
  public void checkGet() 
  throws IllegalStateException
  {
    if(get_index_ >= put_index_)
      throw new IllegalStateException("Not enough data available in packet");
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
    checkGet();
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
 * Get the word (16bit, unsigned) on the given offset.
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
 * Get the (signed) int (16bit) on the given offset.
 *
 * @param offset the position to return.
 * @return the integer at the given position
 */
  public int getInt(int offset)
  {
    return(getSignedInt(offset));
  }

//----------------------------------------------------------------------
/**
 * Get the (signed) int (16bit) on the given offset.
 *
 * @param offset the position to return.
 * @return the integer at the given position
 */
  public int getSignedInt(int offset)
  {
    return(GarminDataConverter.getGarminSignedInt(data_,offset));
  }

//----------------------------------------------------------------------
/**
 * Get the unsigned int (16bit) on the given offset.
 *
 * @param offset the position to return.
 * @return the integer at the given position
 */
  public int getUnsignedInt(int offset)
  {
    return(GarminDataConverter.getGarminSignedInt(data_,offset));
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
 * Get the (unsigned) long (32 bit) on the given offset.
 *
 * @param offset the position to return.
 * @return the long at the given position
 * @deprecated use getLongWord or getSignedLong instead
 */
  public long getLong(int offset)
  {
    return(getSignedInt(offset));
  }

//----------------------------------------------------------------------
/**
 * Get the (unsigned) long (32 bit) on the given offset.
 *
 * @param offset the position to return.
 * @return the long at the given position
 */
  public long getLongWord(int offset)
  {
    return(getUnsignedInt(offset));
  }

//----------------------------------------------------------------------
/**
 * Get the (signed) long (32 bit) on the given offset.
 *
 * @param offset the position to return.
 * @return the long at the given position
 */
  public long getSignedLong(int offset)
  {
    return(GarminDataConverter.getGarminSignedLong(data_,offset));
  }

//----------------------------------------------------------------------
/**
 * Get the (unsigned) long (32 bit) on the given offset.
 *
 * @param offset the position to return.
 * @return the long at the given position
 */
  public long getUnsignedLong(int offset)
  {
    return(GarminDataConverter.getGarminUnsignedLong(data_,offset));
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
    checkGet();
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
    checkGet();
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
 * Get the next data value as int (32bit, signed).
 * @return the next value as int
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 * @deprecated Use getNextAsSigendInt, getNextAsUnsignedInt or getNextAsWord
 * (same as getNextAsUnsignedInt) instead.
 */
  public int getNextAsInt()
    throws IllegalStateException
  {
    checkGet();
    return(getNextAsSignedInt());
  }


//----------------------------------------------------------------------
/**
 * Set the next data value as int (32bit, signed)
 * @param value the next value as int
 * @deprecated Use setNextAsSigendInt, setNextAsUnsignedInt or setNextAsWord
 * (same as setNextAsUnsignedInt) instead.
 */
  public void setNextAsInt(int value)
  {
		setNextAsSignedInt(value);
  }

//----------------------------------------------------------------------
/**
 * Get the next data value as int (32bit, signed).
 * @return the next value as int
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 */
  public int getNextAsSignedInt()
    throws IllegalStateException
  {
    checkGet();
    int value = GarminDataConverter.getGarminSignedInt(data_,get_index_);
    get_index_ += 2;
    return(value);
  }


//----------------------------------------------------------------------
/**
 * Set the next data value as int (32bit, signed)
 * @param value the next value as int
 */
  public void setNextAsSignedInt(int value)
  {
    data_ = GarminDataConverter.setGarminSignedInt(value,data_,put_index_);
    put_index_ += 2;
  }

//----------------------------------------------------------------------
/**
 * Get the next data value as int (32bit, unsigned).
 * @return the next value as int
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 */
  public int getNextAsUnsignedInt()
    throws IllegalStateException
  {
    checkGet();
    int value = GarminDataConverter.getGarminUnsignedInt(data_,get_index_);
    get_index_ += 2;
    return(value);
  }


//----------------------------------------------------------------------
/**
 * Set the next data value as int (32bit, unsigned)
 * @param value the next value as int
 */
  public void setNextAsUnsignedInt(int value)
  {
    data_ = GarminDataConverter.setGarminUnsignedInt(value,data_,put_index_);
    put_index_ += 2;
  }

//----------------------------------------------------------------------
/**
 * Get the next data value as word (16 bit, unsigned).
 * @return the next value as word
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 */
  public int getNextAsWord()
    throws IllegalStateException
  {
    checkGet();
    return(getNextAsUnsignedInt());
  }


//----------------------------------------------------------------------
/**
 * Set the next data value as word (16 bit, unsigned).
 * @param value the next value as word
 */
  public void setNextAsWord(int value)
  {
		setNextAsUnsignedInt(value);
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
    checkGet();
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
 * Get the next data value as long (32bit, unsigned).
 * @return the next value as long.
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 * @deprecated Use getNextAsSigendLong, getNextAsUnsignedLong or getNextAsLongWord
 * (same as getNextAsUnsignedLong) instead.
 */
  public long getNextAsLong()
    throws IllegalStateException
  {
    checkGet();
    return(getNextAsUnsignedLong());
  }

//----------------------------------------------------------------------
/**
 * Set the next data value as long (32bit, unsigned).
 * @param value the next value as long
 * @deprecated Use setNextAsSigendLong, setNextAsUnsignedLong or setNextAsLongWord
 * (same as setNextAsUnsignedLong) instead.
 */
  public void setNextAsLong(long value)
  {
		setNextAsUnsignedLong(value);
  }

//----------------------------------------------------------------------
/**
 * Get the next data value as long (32bit, unsigned).
 * @return the next value as long.
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 */
  public long getNextAsLongWord()
    throws IllegalStateException
  {
    checkGet();
    return(getNextAsUnsignedLong());
  }

//----------------------------------------------------------------------
/**
 * Set the next data value as long (32bit, unsigned).
 * @param value the next value as long
 */
  public void setNextAsLongWord(long value)
  {
		setNextAsUnsignedLong(value);
  }

//----------------------------------------------------------------------
/**
 * Get the next data value as long (32bit, unsigned).
 * @return the next value as long.
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 */
  public long getNextAsUnsignedLong()
    throws IllegalStateException
  {
    checkGet();
    long value = GarminDataConverter.getGarminUnsignedLong(data_,get_index_);
    get_index_ += 4;
    return(value);
  }

//----------------------------------------------------------------------
/**
 * Set the next data value as long (32bit, unsigned).
 * @param value the next value as long
 */
  public void setNextAsUnsignedLong(long value)
  {
    data_ = GarminDataConverter.setGarminUnsignedLong(value,data_,put_index_);
    put_index_ += 4;
  }

//----------------------------------------------------------------------
/**
 * Get the next data value as long (32bit, signed).
 * @return the next value as long.
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 */
  public long getNextAsSignedLong()
    throws IllegalStateException
  {
    checkGet();
    long value = GarminDataConverter.getGarminSignedLong(data_,get_index_);
    get_index_ += 4;
    return(value);
  }

//----------------------------------------------------------------------
/**
 * Set the next data value as long (32bit, signed).
 * @param value the next value as long
 */
  public void setNextAsSignedLong(long value)
  {
    data_ = GarminDataConverter.setGarminSignedLong(value,data_,put_index_);
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
    checkGet();
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
 * @param length the length of the array
 * @return the next values as byte array
 * @throws IllegalStateException on a try to read more bytes than were
 * added before.
 */
  public byte[] getNextAsByteArray(int length)
    throws IllegalStateException
  {
    checkGet();
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
    checkGet();
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
    checkGet();
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
    checkGet();
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
    checkGet();
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
 * Return the checksum of the packet
 * @return the checksum byte
 */
  public byte calcChecksum()
  {
    int checksum = (packet_id_ & 0xff) + packet_size_; 
    for (int index = 0; index < packet_size_; index++) { 
      checksum += (data_[index] & 0xff);
    }
    checksum = -checksum;
    return((byte)checksum);
  }

//----------------------------------------------------------------------
/**
 * Return the data buffer as int[].
 * @return the data buffer as int[].
 * @deprecated Use the varisous methods to read int, byte, word etc. instead.
 */
  public int[] getCompatibilityBuffer()
  {
    int[] buffer = new int[packet_size_ + 2];
      
    buffer[0] = packet_id_;
    buffer[1] = packet_size_;
    for(int index = 0; index < packet_size_; index++)
    {
      buffer[index+2] = data_[index];
    }
    return(buffer);
  }


//----------------------------------------------------------------------
/**
 * Reset the get index, so the next call to getNextAsXXX method will start
 * from the beginning of the buffer.
 */
  public void reset()
  {
		get_index_ = 0;
  }



  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("Garminpacket[id=").append(packet_id_);
    buffer.append(",size=").append(packet_size_);
    buffer.append(",data=[");
    for(int index = 0; index < packet_size_; index++)
		{
      buffer.append(data_[index]);
//			buffer.append("/").append((char)data_[index]);
			buffer.append(" ");
		}
		buffer.append("]]");
    return(buffer.toString());
  }


  public static void main(String[] args)
  {
//     String teststring = "hallo";
//     GarminPacket gp = new GarminPacket(1,4+2+teststring.length()+1);
//     gp.setNextAsInt(123456);
//     gp.setNextAsWord(1245);
//     gp.setNextAsString(teststring);

//     int word = 1234;
//     System.out.println("1234 as byte:");
//     System.out.println(word & 0xff); 
//     System.out.println((word & 0xff00) >> 8);
    
//     System.out.println(gp.getNextAsInt());
//     System.out.println(gp.getNextAsWord());
//     System.out.println(gp.getNextAsString());

		GarminPacket gp = new GarminPacket(69,84);
    for(int index = 0; index < gp.getPacketSize(); index++)
    {
      gp.put(index);
    }

    for(int index = 0; index < gp.getPacketSize(); index++)
    {
      System.out.println("index: "+index +" value: "+gp.getNextAsByte());
    }

    System.out.println(gp);
  }
}
