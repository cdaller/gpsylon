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

package org.dinopolis.gpstool.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import org.dinopolis.util.io.Tokenizer;

//----------------------------------------------------------------------
/**
 * This class allows to read tab-separated streams, files,
 * readers. The tokenizer respects quoted entries, so a line of <code>a b "c
 * d"</code> (a space here should be interpreted as a tab!) results in three
 * entries ("a","b","c d").
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */
public class GeonetStreamTokenizer extends Tokenizer
{
  public static final String[] geonet_keys_ =
    new String[] {"Region Code (RC)","Unique Feature Id (UFI)",
		  "Unique Name Id (UNI)","Degree Latitude (DD_LAT)",
		  "Degree Lotitude (DD_LONG)",
		  "Deg Min Sec Latitude (DMS_LAT)","Deg Min Sec Longiutde (DMS_LONG)",
		  "Universal Transmercator (UTM)","Joint Operation Graphic (JOG)",
		  "Feature Classification (FC)", "Feature Designation Code (DSG)",
		  "Populated Place Classifictation (PC)","Primary Country Code (CC1)",
		  "First Order Admin Divistion (ADM1)","Second Order Admin Division (ADM2)",
		  "Dimension (DIM)", "Secondary Country Code (CC2)","Name Type (NT)",
		  "Language Code (LC)","SHORT_FORM","GENERIC",
		  "SORT_NAME","FULL_NAME","FULL_NAME_ND",
		  "MODIFY_DATE"};

  
// //----------------------------------------------------------------------
// /**
//  * Creates and initializes a StreamTokenizer from the given reader.
//  *
//  * @param reader the reader to use.
//  */
//   public GeonetStreamTokenizer(Reader reader)
//   {
//     super(reader);
//   }
  
//----------------------------------------------------------------------
/**
 * Creates and initializes a StreamTokenizer from the given
 * inputstream.
 *
 * @param input_stream the stream to use
 */
  public GeonetStreamTokenizer(InputStream input_stream)
    throws UnsupportedEncodingException
  {
    super(new InputStreamReader(input_stream,"UTF-8"));
  }


//----------------------------------------------------------------------
/**
 * Demonstrates the usage of a GeonetStreamTokenizer
 *
 * @param args the applications arguments (using the first argument as
 * Filename to read).
 */
  public static void main(String[] args)
  {
    if(args.length < 1)
    {
      System.out.println("Usage: the first argument is used as filename!");
      return;
    }
    
    try
    {
      GeonetStreamTokenizer tokenizer = new GeonetStreamTokenizer(new FileInputStream(args[0]));
      tokenizer.setDelimiter('\t');
      tokenizer.respectEscapedCharacters(false);
      
      int column_count;
      while(tokenizer.hasNextLine())
      {
        List list = tokenizer.nextLine();

//         System.out.println(list.get(1) + " " +list.get(22) + "("+list.get(17)+")"); // name  + name type
        
        Iterator iterator = list.iterator();
        column_count = 0;
        while(iterator.hasNext())
        {
          System.out.println(geonet_keys_[column_count]+": '"+(String)iterator.next()+"'");
          column_count++;
        }
        System.out.println("----------------------------------------------------------------------");
      }
      tokenizer.close();
    }
    catch(IOException ioe)
    {
      ioe.printStackTrace();
    }
  }

}
