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


package org.dinopolis.gpstool.gpsinput.nmea;

import java.util.Vector;
import java.util.StringTokenizer;

import org.dinopolis.util.Debug;

//----------------------------------------------------------------------
/**
 * This class represents a NMEA 0183 sentence as it is sent from a gps
 * receiver or similar devices. See
 * http://www.poly-electronic.ch/dok-gps/nmea-faq.txt for details
 * about this standard.
 *
 * @author Christof Dallermassl
 * @version $Revision$ */

public class NMEA0183Sentence  
{
  protected String raw_data_;
  protected String talker_id_ = null;
  protected String sentence_id_ = null;
  protected byte checksum_ = -1;
  protected byte calculated_checksum_ = -1;
  protected Vector data_fields_ = null;
  
//----------------------------------------------------------------------
/**
 * Initialize a new NMEA 0183 sentence with the given raw data.
 * The raw data looks like "$HCHDG,219.5,,,2.5,E*21".
 * @param raw_data the raw data to be parsed.
 */

  public NMEA0183Sentence(String raw_data)
  {
    if (Debug.DEBUG)
      Debug.print("gpstool_nmeasentence","raw data='"+raw_data+"'");
    int star_pos = raw_data.indexOf('*');
    if(star_pos <= 6)
      throw (new IllegalArgumentException("Invalid NMEA Sentence (no '*'): "
                                          +new String(raw_data)));
    raw_data_ = raw_data.substring(0,star_pos+3);
  }
  

//----------------------------------------------------------------------
/**
 * Initialize a new NMEA 0183 sentence with the given raw data.
 * @param raw_data the raw data to be parsed.
 */

  public NMEA0183Sentence(byte[] raw_data)
  {
    this(new String(raw_data));
  }


//----------------------------------------------------------------------
/**
 * Initialize a new NMEA 0183 sentence with the given raw data.
 * @param raw_data the raw data to be parsed.
 */

  public NMEA0183Sentence(char[] raw_data)
  {
    this(new String(raw_data));
  }


//----------------------------------------------------------------------
/**
 * Returns the talker id of this NMEA sentence.
 * @return the talker id of this NMEA sentence.
 */

  public String getTalkerId()
  {
    if (talker_id_ == null)
      talker_id_ = raw_data_.substring(1,3);
    return(talker_id_);
  }


//----------------------------------------------------------------------
/**
 * Returns the sentence id of this NMEA sentence.
 * @return the sentence id of this NMEA sentence.
 */

public String getSentenceId()
  {
    if (sentence_id_ == null)
      sentence_id_ = raw_data_.substring(3,6); 
    return(sentence_id_);
  }

//----------------------------------------------------------------------
/**
 * Returns the data fields of this NMEA sentence.
 * @return the data fields of this NMEA sentence.
 */

  public Vector getDataFields()
  {
    if (data_fields_ == null)
      retrieveDataFieldsAndChecksum();
    return(data_fields_);
  }

//----------------------------------------------------------------------
/**
 * Returns the checksum of this NMEA sentence.
 * @return the checksum of this NMEA sentence.
 */

  public byte getChecksum()
  {
    if (checksum_ < 0)
      retrieveDataFieldsAndChecksum();
    return(checksum_);
  }


//----------------------------------------------------------------------
/**
 * Returns the calculated checksum of this NMEA sentence.
 * @return the calculated checksum of this NMEA sentence.
 */

  public byte getCalculatedChecksum()
  {
    if (calculated_checksum_ < 0)
    {
      if(checksum_ < 0)
        retrieveDataFieldsAndChecksum();
      calculated_checksum_ = calcChecksum();
    }
    return(calculated_checksum_);
  }


//----------------------------------------------------------------------
/**
 * Returns true if the sentence is valid (by using the checksum).
 *
 * @return true if the sentence is valid (by using the checksum).
 */
  public boolean isValid()
  {
    return(getChecksum() == getCalculatedChecksum());
  }
  
//----------------------------------------------------------------------
/**
 * Parses the raw data and extracts the data fields and the checksum.
 */

  protected void retrieveDataFieldsAndChecksum()
  {
    if (data_fields_ == null)
      data_fields_ = new Vector();
    
    StringTokenizer tokenizer = new StringTokenizer(raw_data_,",*",true);
    tokenizer.nextElement();  // skip first element (NMEA messageid)
    if (tokenizer.hasMoreElements())
      tokenizer.nextElement();  // skip first delimiter
    String token;
    String element = "";
    
    while(tokenizer.hasMoreElements())
    {
      token = tokenizer.nextToken();
      if(token.equals(","))
      {
        data_fields_.addElement(element);
        element = "";
      }
      else
      {
        if(token.equals("*"))
        {
          data_fields_.addElement(element);
          element = "";  // mostly useless, as checksum is the last
          checksum_ = decodeChecksum(tokenizer.nextToken());
          if(tokenizer.hasMoreElements())
            System.err.println("WARNING: too long NMEA sentence, elements after checksum found: "
                               +raw_data_);
        }
        else
        {
          element = token;  // normal token
        }
      }
    }
  }

  public boolean equals(NMEA0183Sentence object)
  {
    return(object.raw_data_.equals(raw_data_));
  }
  
  public boolean equals(Object object)
  {
    if (!(object instanceof NMEA0183Sentence))
      return(false);
    else
      return(equals((NMEA0183Sentence)object));
  }
  
//----------------------------------------------------------------------
/**
 * Decodes the checksum string of a nmea sentence.
 *
 * @param	checksum_string the string representing the checksum (two characters long)
 * @return the checksum as a byte value
 */
	
	protected static byte decodeChecksum(String checksum_string)
	{
    byte checksum;
    
    checksum = (byte)((hexCharToByte(checksum_string.charAt(0)) & 0xF ) << 4 );
    checksum = (byte)(checksum | hexCharToByte(checksum_string.charAt(1)) & 0xF );
    return(checksum);
	}
	
	
//----------------------------------------------------------------------
/**
 * Calculate the checksum of this NMEA sentence
 *
 * @return the calculated checksum
 */
	
	protected byte calcChecksum()
	{
		int		calc = 0;
		int		count;
		int		len;
		char	chr;
		
		len = raw_data_.length();
		
		for(count = 1; count < len - 2; count++)  // ignore '$' at beginning and checksum at the end
    {
			chr = raw_data_.charAt(count);
			
			if(chr == '*') // just to be sure
        break;

				if(count == 1)
					calc = (chr + 256) & 0xFF;
				else
					calc ^= (chr + 256) & 0xFF;
		}
		return((byte)calc);
	}
	
//----------------------------------------------------------------------
/**
 * Get the byte value for a hex character
 *
 * @param	hex_char hex character
 * @return byte value
 */
	
	protected static byte hexCharToByte(char hex_char)
	{
		if( hex_char > 57 )
			return((byte)(hex_char - 55));
    else
			return((byte)(hex_char - 48)); 
	}
	
//----------------------------------------------------------------------
/**
 * Returns the string representation of this NMEA sentence.
 * @return the string representation of this NMEA sentence.
 */

  public String toString()
  {
    return(raw_data_);
  }


  public static void main(String[] args)
  {
    NMEA0183Sentence s;
    if(args.length > 0)
    {
      s = new NMEA0183Sentence(args[0]);
      System.out.println("Sentence: "+s);
      System.out.println("talkId: "+s.getTalkerId());
      System.out.println("sentId: "+s.getSentenceId());
      System.out.println("datafields: "+s.getDataFields());
      System.out.println("checksum: "+s.getChecksum());
      System.out.println("calculated checksum: "+s.getCalculatedChecksum());
      System.out.println("valid: "+s.isValid());
    }
    else
    {
      s = new NMEA0183Sentence("$HCHDG,219.5,,,2.5,E*21");
      System.out.println("Sentence: "+s);
      System.out.println("talkId: "+s.getTalkerId());
      System.out.println("sentId: "+s.getSentenceId());
      System.out.println("datafields: "+s.getDataFields());
      System.out.println("checksum: "+s.getChecksum());
      System.out.println("calculated checksum: "+s.getCalculatedChecksum());
      System.out.println("valid: "+s.isValid());
    }
  }
}






