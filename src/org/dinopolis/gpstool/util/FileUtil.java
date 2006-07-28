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
   * Returns the next available number for a filename for a given directory, prefix,
   * pattern and suffix. E.g. using the prefix "map_", the pattern
   * "0000" and the suffix ".txt" and there exist the files
   * "map_0001.txt" and "map_0002.txt" it returns "map_0003.txt". The
   * pattern must be a valid NumberFormat pattern. If wildcards are used
   * for the prefix or the suffix, they are contained in the returned
   * filename!
   *
   * @param directory the directory the files are located in
   * @param prefix the prefix of the files (may contain '?'s as wildcard).
   * @param pattern the number pattern of the files
   * @param suffix the suffix of the files (may contain '?'s as wildcard).
   * @return the next available filename (including the given directory name!)
   */

    public static int getNextFileNameNumber(String directory, final String prefix,
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
                   && wildcardStartsWith(name,prefix) && wildcardEndsWith(name,suffix)
//                   && name.startsWith(prefix) && name.endsWith(suffix)
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
      return last_match + 1;
    }

//----------------------------------------------------------------------
/**
 * Returns the next available filename for a given directory, prefix,
 * pattern and suffix. E.g. using the prefix "map_", the pattern
 * "0000" and the suffix ".txt" and there exist the files
 * "map_0001.txt" and "map_0002.txt" it returns "map_0003.txt". The
 * pattern must be a valid NumberFormat pattern. If wildcards are used
 * for the prefix or the suffix, they are contained in the returned
 * filename!
 *
 * @param directory the directory the files are located in
 * @param prefix the prefix of the files (may contain '?'s as wildcard).
 * @param pattern the number pattern of the files
 * @param suffix the suffix of the files (may contain '?'s as wildcard).
 * @return the next available filename (including the given directory name!)
 */

  public static String getNextFileName(String directory, final String prefix,
                                       final String pattern, final String suffix)
  {
    int freeNumber = getNextFileNameNumber(directory, prefix, pattern, suffix);
    NumberFormat format = new DecimalFormat(pattern);
    String filename = prefix + format.format(freeNumber) + suffix;
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


//----------------------------------------------------------------------
/**
 * Returns true, if s1 starts with s2 respecting the wildcards (* =
 * 0 or more characters; ? = exactly 1 character) in s2.
 * Example:
 * <xmp>
 * wildcardStartsWith("abc.txt","ab?") returns true.
 * </xmp>
 *
 * @param s1 the first string (may not contain wildcards)
 * @param s2 the second string (may contain wildcards!)
 * @return true if s1 starts with s2 respecting the wildcards in s2,
 * false otherwise.
 */
  public static boolean wildcardStartsWith(String s1, String s2)
  {
    int length = Math.min(s1.length(),s2.length());
    return(wildcardEqual(s2,s1.substring(0,length)));
  }

//----------------------------------------------------------------------
/**
 * Returns true, if s1 ends with s2 respecting the wildcards (* =
 * 0 or more characters; ? = exactly 1 character) in s2.
 * Example:
 * <xmp>
 * wildcardEndsWith("abc.txt","t?t") returns true.
 * </xmp>
 *
 * @param s1 the first string (may not contain wildcards)
 * @param s2 the second string (may contain wildcards!)
 * @return true if s1 ends with s2 respecting the wildcards in s2,
 * false otherwise.
 */
  public static boolean wildcardEndsWith(String s1, String s2)
  {
    int length = s2.length();
    return(wildcardEqual(s2,s1.substring(s1.length()-length)));
  }


//----------------------------------------------------------------------
/**
 * Compare s1 with s2; s1 can use wildcards.
 * * = 0 or more characters; ? = exactly 1 character
 * returns true for same, false for different
 *
 * Ripped out of org.javalobby.util.StringUtils resp.
 * from http://www.ulfdittmer.com/old/java/TextUtils.java
 *
 * This function works by moving both Strings forward when they are the same
 * if either s1 or s2 has any elements left at the end, when
 * the other has none, the compare is invalid.
 *
 * @param s1 the first string (may contain wildcards)
 * @param s2 the second string (may not contain wildcards!)
 * @return true if the comparison was successful, false otherwise.
 */
  public static boolean wildcardEqual (String s1, String s2) {
    while (s2.length() != 0)
    {
          //make sure we have atleast 1 character in s1
      if (s1.length() == 0)
        return false;    //s1 ended, but s2 has not ended yet
      if (s1.charAt(0) == s2.charAt(0))
      {     //character the same?
            //move both strings forward
        s1 = s1.substring(1);
        s2 = s2.substring(1);
      }
      else if (s1.charAt(0) == '?')
      {         //exactly 1 char
            //move both strings forward
        s1 = s1.substring(1);
        s2 = s2.substring(1);
      }
      else if (s1.charAt(0) == '*')
      {         //0 or many chars
            //try carrying on as if the * ended on the current character in s2
        if (wildcardEqual(s1.substring(1), s2))
          return true;
        else
          s2 = s2.substring(1);    //only move s2 forward
            //next iteration we will be back here, to try matching against the new s2
      }
      else
        return false;    //not the same
    }
        //s2.length()==0, as it is the only exit condition from the loop
        //if s1 has finished, then they are the same
    if (s1.length()==0)
      return true;
        //s1 could be a *, which can be 0 characters
    if (s1.charAt(0)=='*' && s1.length()==1)
      return true;
    return false;//not the same
  }

  public static void main(String[] args)
  {
//     if(args.length < 4)
//     {
//       System.out.println("Returns next matching filename");
//       System.out.println("Usage: FileUtil <dir> <prefix> <pattern> <suffix>");
//       System.out.println("       e.g. FileUtil ~/.gpsmap/maps maps_ 00000 .gif");
//       System.exit(1);
//     }
//     String dir = args[0];
//     String prefix = args[1];
//     String pattern = args[2];
//     String suffix = args[3];
//     System.out.println("new name: "+getNextFileName(dir,prefix,pattern,suffix));
    String s1 = args[0];
    String s2 = args[1];
    System.out.println("WildcardEqual of "+s1+" and "+ s2 +" = "+wildcardEqual(s1,s2));
    System.out.println("WildcardStartWith of "+s1+" and "+ s2 +" = "+wildcardStartsWith(s1,s2));
    System.out.println("WildcardEndsWith of "+s1+" and "+ s2 +" = "+wildcardEndsWith(s1,s2));
  }
}


