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


package org.dinopolis.gpstool.gui.layer.location;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.dinopolis.gpstool.gui.layer.location.LocationMarker;

import org.dinopolis.gpstool.util.geoscreen.GeoScreenList;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import org.dinopolis.util.Resources;
import org.dinopolis.util.io.Tokenizer;

//----------------------------------------------------------------------
/**
 * This class reads creates Location Marker object from comma
 * separated, tab separated or space separated files.
 *
 * @author Christof Dallermassl
 * @version $Revision$ */

public class FileLocationMarkerSource extends AbstractLocationMarkerSource
{

  protected int name_column_ = 0;
  protected int latitude_column_ = 1;
  protected int longitude_column_ = 2;
  protected int category_column_ = 3;
  protected String filename_;

  protected char delimiter_ = ',';
  
  protected int column_number_ = 3; // default is the minimum number of fields in a marker
  
  protected PrintWriter out_; 
    /** if set to true, the file is reopened everytime a marker
        is written, and closed afterwards again. If set to false, the
        file is left opened. */
  protected boolean always_close_ = false;

  protected Resources resources_;

  protected boolean remove_zero_length_elements_ = false;

//----------------------------------------------------------------------
/**
 * Constructs a location marker source that reads from a comma
 * separated file. The column indices are numbered starting from 1!!
 * <p> After having created this LocationMarkerSource, call
 * initialize() which will create all internal datastructures and
 * calls readLocationMarkers().
 *
 * @param resources the resources (needed to read the category names, icons, ...)
 * @param filename the file to read
 */
  public FileLocationMarkerSource(Resources resources, String filename)
  {
    resources_ = resources;
    filename_ = filename;
  }


//----------------------------------------------------------------------
/**
 * Get the column of the name element. The first column is 0!!
 *
 * @return the column of the name element.
 */
  public int getNameColumn() 
  {
    return (name_column_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the column for the name element. The first column is 0!!!!
 *
 * @param name_column the column for the name element.
 */
  public void setNameColumn(int name_column) 
  {
    name_column_ = name_column;
  }
  
//----------------------------------------------------------------------
/**
 * Get the column of the latitude element. The first column is 0!!
 *
 * @return the column of the latitude element.
 */
  public int getLatitudeColumn() 
  {
    return (latitude_column_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the column for the latitude element. The first column is 0!!!!
 *
 * @param latitude_column the column for the latitude element.
 */
  public void setLatitudeColumn(int latitude_column) 
  {
    latitude_column_ = latitude_column;
  }
  
  
//----------------------------------------------------------------------
/**
 * Get the column of the longitude element. The first column is 0!!
 *
 * @return the column of the longitude element.
 */
  public int getLongitudeColumn() 
  {
    return (longitude_column_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the column for the longitude element. The first column is 0!!!!
 *
 * @param longitude_column the column for the longitude element.
 */
  public void setLongitudeColumn(int longitude_column) 
  {
    longitude_column_ = longitude_column;
  }
  
  
//----------------------------------------------------------------------
/**
 * Get the column of the category element. The first column is 0!!
 *
 * @return the column of the category element.
 */
  public int getCategoryColumn() 
  {
    return (category_column_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the column for the category element. The first column is 0!!!!
 *
 * @param category_column the column for the category element.
 */
  public void setCategoryColumn(int category_column) 
  {
    category_column_ = category_column;
  }
  
//----------------------------------------------------------------------
/**
 * Get the delimiter character to use.
 *
 * @return the delimiter.
 */
  public char getDelimiter() 
  {
    return (delimiter_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the delimiter character to use (e.g. comma, tab, space (',', '\t', ' ')).
 *
 * @param delimiter the delimiter.
 */
  public void setDelimiter(char delimiter) 
  {
    delimiter_ = delimiter;
  }


//----------------------------------------------------------------------
/**
 * Returns true, if empty elements should be removed between the
 * columns (e.g. gpsdrive writes its waypoints with multiple spaces
 * between the values). This may be dangerous!
 *
 * @return true, if empty elements should be removed between the
 * columns.
 */
  public boolean isRemoveEmptyElements() 
  {
    return (remove_zero_length_elements_);
  }
  
//----------------------------------------------------------------------
/**
 * If set to true,  empty elements should be removed between the
 * columns (e.g. gpsdrive writes its waypoints with multiple spaces
 * between the values). This may be dangerous!
 *
 * @param remove_empty_elements
 */
  public void setRemoveEmptyElements(boolean remove_empty_elements) 
  {
    remove_zero_length_elements_ = remove_empty_elements;
  }
  
//----------------------------------------------------------------------
/**
 * Called from the initialize method to fill the location markers.
 *
 * @return a GeoScreenList filled with LocationMarker objects or an
 * empty list, if no markers are found.
 * @exception LocationMarkerSourceException if the location marker
 * source throws an exception on making the location marker
 * persistent, it is wrapped into a LocationMarkerSourceException
 */
  protected GeoScreenList readLocationMarkers()
    throws LocationMarkerSourceException
  {
    GeoScreenList marker_list = new GeoScreenList();
    try
    {
      Tokenizer tokenizer = new Tokenizer(new FileReader(filename_));
      tokenizer.setDelimiter((int)delimiter_);

      while(tokenizer.hasNextLine())
      {
        List list = null;
        try
        {
          list = tokenizer.nextLine();
//          System.out.println(list);
          if((list != null) && (list.size() > 0))
          {
            if(remove_zero_length_elements_)
              list = Tokenizer.removeZeroLengthElements(list);
            
            if(list.size() > column_number_)
              column_number_ = list.size();
	    
            LocationMarker marker = new LocationMarker();
            
                // extract name, latitude and longitude from list:
            marker.setName((String)list.get(name_column_));
            float coord = Float.parseFloat((String)list.get(latitude_column_));
            marker.setLatitude(coord);
            coord = Float.parseFloat((String)list.get(longitude_column_));
            marker.setLongitude(coord);

            if((category_column_ >= 0) && (category_column_ < column_number_))
            {
              String category_id = (String)list.get(category_column_);
              marker.setCategory(LocationMarkerCategory.getCategory(category_id,resources_));
            }
            marker_list.add(marker);
          }
        }
        catch(NumberFormatException nfe)
        {
          System.err.println("ERROR: on import data from file '"+filename_
                             +"' in line "+tokenizer.getLineNumber()+": "+list
                             +" message: "+nfe.getMessage());
        }
        catch(NoSuchElementException nsee)
        {
          System.err.println("ERROR: on import data from file '"+filename_
                             +"' in line "+tokenizer.getLineNumber()+": "+list
                             +" message: "+nsee.getMessage());
        }
        catch(ArrayIndexOutOfBoundsException aiobe)
        {
          System.err.println("ERROR: on import data from file '"+filename_
                             +"' in line "+tokenizer.getLineNumber()+": "+list
                             +" message: "+aiobe.getMessage());
        }
      }
      tokenizer.close();
    }
//      catch(FileNotFoundException fnfe)
//      {
//        System.err.println("ERROR: File not found: '"+filename_+"'");
//        System.err.println(" - ignoring file location markers.");
//      }
    catch(IOException e)
    {
      throw new LocationMarkerSourceException("FileLocationMarkerSource: "
                                              +e.getMessage(),e);
    }
    return(marker_list);
  }

//----------------------------------------------------------------------
/**
 * Makes a location marker persistent. Depending on the file type set,
 * it writes the data comma, tab, or space separated into the given
 * file.
 *
 * @param marker the new location marker to ge added.
 * @exception LocationMarkerSourceException if the location marker
 * source throws an exception on making the location marker
 * persistent, it is wrapped into a LocationMarkerSourceException
 */
  protected void writeLocationMarker(LocationMarker marker)
    throws LocationMarkerSourceException
  {
    try
    {
      if(always_close_ || out_ == null)
        out_ = new PrintWriter(new FileWriter(filename_,true)); //append data to file
      StringBuffer line = new StringBuffer();
          // TODO: separator might not be set!!!!!!!!!!!!!!!!!!
      for(int column_count = 0; column_count < column_number_; column_count++)
      {
        if(column_count == latitude_column_)
        {
          line.append(formatCoordinate(marker.getLatitude()));
          line.append(delimiter_);
        }
        else
          if(column_count == longitude_column_)
          {
            line.append(formatCoordinate(marker.getLongitude()));
            line.append(delimiter_);
          }
          else
            if(column_count == name_column_)
            {
              line.append("\"").append(marker.getName()).append("\"");
              line.append(delimiter_);
            }
            else
            {
                  // append empty column:
              line.append("\"\"").append(delimiter_);
            }
      }
          // cut off last column separator:
      line.setLength(line.length()-1);
      System.out.println("write line to waypointfile: "+line);
      out_.println(line.toString());
      out_.flush();
      if(always_close_)
        out_.close();
    }
    catch(IOException ioe)
    {
      throw new LocationMarkerSourceException(ioe);
    }
    
  }


  protected String formatCoordinate(float coordinate)
  {
    return(String.valueOf((coordinate*100000f+0.5f)/100000f));
  }
}





