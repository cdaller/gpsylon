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


package org.dinopolis.gpstool.gpsinput;

public class GarminDataConverter
{
      /** Semicircle to Degrees conversion values */
	protected final static double	SEMICIRCLE_FACTOR					= (double)( 1L << 31 ) / 180.0d;


//----------------------------------------------------------------------
  /**
   * Helper functions for converting received binary data into datatypes:
   *
   * IntLEtoBE - coverts int little-endian to int big-endian
   * FloatLEtoBE - converts float little-endian to float big-endian
   * DoubleLEtoBE - converts double little-endian to double big-endian
   *
   * @param read chars to be converted into big-endian number format
   */
  public static int IntLEtoBE(char a, char b, char c, char d)
  {
      int accum = 0;
      char[] toConvert = new char[] {a,b,c,d};

      for (int shiftBy=0; shiftBy<32; shiftBy+=8)
      {
        accum |= ((toConvert[(int)shiftBy/8] & 0xff)) << shiftBy;
      }
    
      return accum;
  }

  public static float FloatLEtoBE(char a, char b, char c, char d)
  {
      int accum = 0;
      char[] toConvert = new char[] {a,b,c,d};

      for (int shiftBy=0; shiftBy<32; shiftBy+=8)
      {
        accum |= (toConvert[shiftBy/8] & 0xff) << shiftBy;
      }

      return Float.intBitsToFloat(accum);
  }

  public static double DoubleLEtoBE(char a, char b, char c, char d, char e, char f, char g, char h)
  {
      long accum = 0;
      char[] toConvert = new char[] {a,b,c,d,e,f,g,h};

      for (int shiftBy=0; shiftBy<64; shiftBy+=8)
      {
            // Cast to long or shift done modulo 32 required to work properly
        accum |= ((long)(toConvert[(int)shiftBy/8] & 0xff)) << shiftBy;
      }
    
      return Double.longBitsToDouble(accum);
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
  public static byte[] getGarminByteArray(char[] buffer, int offset, int length)
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
   * Extracts a zero-terminated string from the given character buffer
   * and returns it.
   *
   * @param buffer the character buffer to extract the string from.
   * @param offset the offset to start reading the buffer.
   * @return the value extracted from the buffer.
   */
  public static String getGarminString(char[] buffer, int offset)
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
  public static String getGarminString(char[] buffer, int offset, int max_length)
  {
    char ch;
    StringBuffer result = new StringBuffer();
    int max_index = Math.min(offset + max_length,buffer.length);
    for(int index = offset; index < max_index; index++)
    {
      ch = buffer[index];
      if(ch != 0)
        result.append(ch);
      else
      {
        return(result.toString());
      }
    }
    return(result.toString());
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
  public static int getGarminInt(char[] buffer, int offset)
  {
    int value = (buffer[offset] & 0xff)
                | ((buffer[offset+1] & 0xff) << 8)
                | ((buffer[offset+2] & 0xff) << 16)
                | ((buffer[offset+3] & 0xff) << 24);


//     System.out.println("garminint called buffer=: "
//                        +(int)buffer[offset]+","
//                        +(int)buffer[offset+1]+","
//                        +(int)buffer[offset+2]+","
//                        +(int)buffer[offset+3]);
    return(value);
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
  public static short getGarminByte(char[] buffer, int offset)
  {
    return((short)(buffer[offset] & 0xff));
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
  public static int getGarminWord(char[] buffer, int offset)
  {
    int value = (buffer[offset] & 0xff)
                | ((buffer[offset+1] & 0xff) << 8);
    return(value);
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
  public static float getGarminFloat(char[] buffer, int offset)
  {
    int value = (buffer[offset] & 0xff)
                | ((buffer[offset+1] & 0xff) << 8)
                | ((buffer[offset+2] & 0xff) << 16)
                | ((buffer[offset+3] & 0xff) << 24);
    return(Float.intBitsToFloat(value));
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
  public static double getGarminDouble(char[] buffer, int offset)
  {
    long value = (buffer[offset] & 0xff)
                | ((buffer[offset+1] & 0xff) << 8)
                | ((buffer[offset+2] & 0xff) << 16)
                | ((buffer[offset+3] & 0xff) << 24)
                | ((buffer[offset+4] & 0xff) << 32)
                | ((buffer[offset+5] & 0xff) << 40)
                | ((buffer[offset+6] & 0xff) << 48)
                | ((buffer[offset+7] & 0xff) << 56);
    return(Double.longBitsToDouble(value));
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
  public static boolean getGarminBoolean(char[] buffer, int offset)
  {
    return(buffer[offset] != 0);
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
  public static double getGarminDegrees(char[] buffer, int offset)
  {
    return(convertSemicirclesToDegrees(getGarminInt(buffer,offset)));
  }

  //----------------------------------------------------------------------
  /**
   * Converts Semicircles to Degrees.
   *
   * @param semicircles
   @ @return degrees
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
   @ @return semicircles
   */
  public static double convertDegreesToSemicircles(double degrees)
  {
    return((int)degrees * SEMICIRCLE_FACTOR);
  }






  
  public static String[] D202toData(char[] buffer)
  {
      char[] rte_ident = new char[51];

      for (int i=0;i<(int)buffer[1];i++)
      {
	  rte_ident[i] = buffer[i+2];
      }

      return new String[] {"Header",new String(rte_ident)};
    }

  public static String[] D210toData(char[] buffer)
  {
      char rte_class;
      char[] rte_ident = new char[51];

      rte_class = (char)(buffer[2]+256*buffer[3]);
      for (int i=0;i<(int)buffer[1]-21;i++)
      {
	  rte_ident[i] = buffer[22+i];
      }

      return new String[] {""+rte_class,new String(rte_ident)};
  }

  public static String[] D300toData(char[] buffer)
  {
      double lat;
      double lon;
      float time;
      char new_track;

      lat = IntLEtoBE(buffer[2],buffer[3],buffer[4],buffer[5])*(180/Math.pow(2,31));
      lon = IntLEtoBE(buffer[6],buffer[7],buffer[8],buffer[9])*(180/Math.pow(2,31));
      time = FloatLEtoBE(buffer[10],buffer[11],buffer[12],buffer[13]);
      new_track = buffer[22];

      return new String[] {""+lat,""+lon,""+time,""+new_track};
  }

  public static String[] D301toData(char[] buffer)
  {
      double lat;
      double lon;
      float time;
      float alt;
      float depth;
      char new_track;

      lat = IntLEtoBE(buffer[2],buffer[3],buffer[4],buffer[5])*(180/Math.pow(2,31));
      lon = IntLEtoBE(buffer[6],buffer[7],buffer[8],buffer[9])*(180/Math.pow(2,31));
      time = FloatLEtoBE(buffer[10],buffer[11],buffer[12],buffer[13]);
      alt = FloatLEtoBE(buffer[14],buffer[15],buffer[16],buffer[17]);
      depth = FloatLEtoBE(buffer[18],buffer[19],buffer[20],buffer[21]);
      new_track = buffer[22];

      return new String[] {""+lat,""+lon,""+time,""+alt,""+depth,""+new_track};
  }

  public static String[] D310toData(char[] buffer)
  {
      char color;
      char display;
      char[] trk_ident = new char[] {};

      display = buffer[2];
      color = buffer[3];
      for (int i=0;i<(int)buffer[1]-4;i++)
      {
	  trk_ident[i] = buffer[4+i];
      }

      return new String[] {"Header",""+display,""+color,new String(trk_ident)};
  }

  public static String[] D800toData(char[] buffer)
  {
      float alt = 0;
      float epe = 0;
      float eph = 0;
      float epv = 0;
      int fix = -1;
      double tow = 0;
      double lat = 0;
      double lon = 0;
      float east = 0;
      float north = 0;
      float up = 0;
      float msl_height = 0;
      int leap_seconds = 0;
      int wn_days = 0;

      // Altitude above WGS84-Ellipsoid [meters]
      alt = FloatLEtoBE(buffer[2],buffer[3],buffer[4],buffer[5]);

      // Estimated position error
      // epe - 2sigma [meters]
      // eph - horizontal only [meters]
      // epv - vertical only [meters]
      // fix - type of position fix
      epe = FloatLEtoBE(buffer[6],buffer[7],buffer[8],buffer[9]);
      eph = FloatLEtoBE(buffer[10],buffer[11],buffer[12],buffer[13]);
      epv = FloatLEtoBE(buffer[14],buffer[15],buffer[16],buffer[17]);
      fix = buffer[18]+256*buffer[19];

      // Time of week [seconds]
      tow = DoubleLEtoBE(buffer[20],buffer[21],buffer[22],buffer[23],
			 buffer[24],buffer[25],buffer[26],buffer[27]);

      // Latitude and longitude is reported in radiant, so it has
      // to be converted into degree
      lat = DoubleLEtoBE(buffer[28],buffer[29],buffer[30],buffer[31],
			 buffer[32],buffer[33],buffer[34],buffer[35])*(180/Math.PI);
      lon = DoubleLEtoBE(buffer[36],buffer[37],buffer[38],buffer[39],
			 buffer[40],buffer[41],buffer[42],buffer[43])*(180/Math.PI);

      // Movement speeds in east, north, up-direction. Opposite directions
      // are reported by negative speeds [meters/second]
      east = FloatLEtoBE(buffer[44],buffer[45],buffer[46],buffer[47]);
      north = FloatLEtoBE(buffer[48],buffer[49],buffer[50],buffer[51]);
      up = FloatLEtoBE(buffer[52],buffer[53],buffer[54],buffer[55]);

      // Height of WGS84-Ellipsoid above MSL [meters]
      msl_height = FloatLEtoBE(buffer[56],buffer[57],buffer[58],buffer[59]);

      // Difference between GPS and UTS [seconds]
      leap_seconds = buffer[60]+256*buffer[61];

      // Week number days
      wn_days = IntLEtoBE(buffer[62],buffer[63],buffer[64],buffer[65]);
		
      return new String[] {""+lat,""+lon,""+(alt+msl_height),""+fix,""+epe,""+eph,""+epv,
			       ""+east,""+north,""+up,""+tow,""+leap_seconds,""+wn_days};
  }
}

