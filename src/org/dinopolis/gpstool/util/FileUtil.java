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
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.IllegalArgumentException;
import java.text.ParseException;

//----------------------------------------------------------------------
/**
 * This class provides some help functionality for handling files and
 * filenames.
 * <p>
 * Contributions: Jun Li (fixed problem (Locale?) in getNextFileName)
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class FileUtil  
{

//----------------------------------------------------------------------
/**
 * Returns the next available filename for a given directory, prefix,
 * pattern and suffix. E.g. using the prefix "map_", the pattern
 * "0000" and the suffix ".txt" and there exist the files
 * "map_0001.txt" and "map_0002.txt" it returns "map_0003.txt". The
 * pattern must be a valid NumberFormat pattern.
 *
 * @param directory the directory the files are located in
 * @param prefix the prefix of the files
 * @param pattern the number pattern of the files
 * @param suffix the suffix of the files
 * @return the next available filename (including the given directory name!)
 */

  public static String getNextFileName(String directory, final String prefix,
                                       final String pattern, final String suffix)
  {
    NumberFormat format = new DecimalFormat(pattern);
    
    File dir = new File(directory);

        // filter only the matching files:
    FilenameFilter filter = new FilenameFilter()
      {
        public boolean accept(File dir, String name)
        {
          return(!(new File(dir,name).isDirectory())
                 && name.startsWith(prefix) && name.endsWith(suffix)
                 && (name.length() == prefix.length() + pattern.length() + suffix.length()));
        }
    };
    
    String[] children = dir.list(filter);
    if(children == null)
      throw(new IllegalArgumentException("Directory '"+directory
                                         +"' does not exist or cannot be read!"));
    int last_match = 0;
    int number;
    String filename;
    String number_string;
    for (int file_count = 0; file_count < children.length; file_count++)
    {
      filename = children[file_count];
      try
      {
        number_string = filename.substring(prefix.length(),prefix.length()+pattern.length());
        number = format.parse(number_string).intValue();   
        
        if(number > last_match)
          last_match = number;
      }
      catch(ParseException pe)
      {
            // do not care!
      }
    }
    filename = prefix + format.format(last_match + 1) + suffix;
    return(directory + File.separator + filename);
  }


//----------------------------------------------------------------------
/**
 * Returns an absolute path of the given path. If the path is already
 * absolute, it is returned. If is a relative path, it is concatenated
 * with the given base path.
 *
 * @param base_dir the base directory used in case the path is relative
 * @param path the path to use
 * @return an absolute path.
 */

  public static String getAbsolutePath(String base_dir, String path)
  {
    if(isAbsolutePath(path))
      return(path);
    return(base_dir + File.separator + path);
  }

//----------------------------------------------------------------------
/**
 * Returns if a given path is an absolute path (independent of
 * operating system).
 *
 * @return if a given path is an absolute path (independent of
 * operating system).
 */
  public static boolean isAbsolutePath(String path)
  {
    if(System.getProperty("os.name").toLowerCase().startsWith("windows"))
    {
      return(path.charAt(1) == ':');
    }
    else
    {
      return(path.charAt(0) == '/');
    }
  }


  public static void main(String[] args)
  {
    if(args.length < 4)
    {
      System.out.println("Returns next matching filename");
      System.out.println("Usage: FileUtil <dir> <prefix> <pattern> <suffix>");
      System.out.println("       e.g. FileUtil ~/.gpsmap/maps maps_ 00000 .gif");
      System.exit(1);
    }
    String dir = args[0];
    String prefix = args[1];
    String pattern = args[2];
    String suffix = args[3];
    System.out.println("new name: "+getNextFileName(dir,prefix,pattern,suffix));
  }
}


