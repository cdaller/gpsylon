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


package org.dinopolis.gpstool.track;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.NoSuchElementException;
import org.dinopolis.gpstool.GPSMapKeyConstants;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.plugin.ReadTrackPlugin;
import org.dinopolis.util.Debug;
import org.dinopolis.util.Resources;

//----------------------------------------------------------------------
/**
 * This plugin reads track data from a stream (ususally from a file)
 * and provides one or more tracks (lists of {@link TrackPoint}
 * objects). This plugin reads the tracks GPSMap saves itself. That's
 * why it is not a real plugin, but always available.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class ReadGPSMapTrackPlugin implements ReadTrackPlugin, GPSMapKeyConstants
{

  Resources resources_;
  public final static String TRACK_FORMAT_DEFINITION_IN_FILE_PREFIX = "# Format: ";
  
  public ReadGPSMapTrackPlugin()
  {
  }


//----------------------------------------------------------------------
/**
 * Initialize the plugin and pass a PluginSupport that provides
 * objects, the plugin may use.
 *
 * @param support the PluginSupport object
 */
  public void initializePlugin(PluginSupport support)
  {
    resources_ = support.getResources();
  }
  
//----------------------------------------------------------------------
/**
 * The application calls this method to indicate that the plugin is
 * activated and will be used from now on. The Plugin should
 * initialize any needed resources (files, etc.) in this method.
 *
 * @throws Exception if an error occurs. If this method throws an
 * exception, the plugin will not be used by the application.
 */

  public void startPlugin()
    throws Exception
  {
  }

//----------------------------------------------------------------------
/**
 * The application calls this method to indicate that the plugin is
 * deactivated and will not be used any more. The Plugin should
 * release all resources (close files, etc.) in this method.
 *
 * @throws Exception if an error occurs.
 */

  public void stopPlugin()
    throws Exception
  {
  }

//----------------------------------------------------------------------
/**
 * Returns a short description of the track data that may be used e.g. in
 * a file chooser. If possible, the description should be localized.
 *
 * @return The short description of the content.
 */

  public String getContentDescription()
  {
    return(resources_.getString(KEY_TRACK_FILE_DESCRIPTIVE_NAME));
  }
  
//----------------------------------------------------------------------
/**
 * Returns possible file extensions the content. This information
 * may be used in a file chooser as a filter (e.g. ["jpg","jpeg"]).
 *
 * @return The file extensions to use for this kind of data.
 */

  public String[] getContentFileExtensions()
  {
    return(new String[] {resources_.getString(KEY_TRACK_FILE_EXTENSION)});
  }
  

//----------------------------------------------------------------------
/**
 * Parse the given input stream and return tracks. If no tracks could
 * be read, an empty array (length of 0) is returned (not null!).
 *
 * @param in the inputstream to read the data from.
 * @return an array of {@link
 * org.dinopolis.gpstool.gui.layer.track.Track} objects.
 * @throws IOException if an error occurs during reading.  */
  public Track[] getTracks(InputStream in)
    throws IOException
  {
    int linenumber = 0;
    try
    {
      BufferedReader track_in = new BufferedReader(new InputStreamReader(in));
      String line;
      MessageFormat line_format;
      Track track = new TrackImpl();
      if(Debug.DEBUG)
        Debug.println("read_track","loading GPSMap track");
      String track_format = resources_.getString(KEY_TRACK_FILE_FORMAT);
      line_format = new MessageFormat(track_format,Locale.US); // for decimal points

      while((line = track_in.readLine()) != null)
      {
        linenumber++;
        if(line.startsWith(TRACK_FORMAT_DEFINITION_IN_FILE_PREFIX))
        {
          line_format =
            new MessageFormat(line.substring(TRACK_FORMAT_DEFINITION_IN_FILE_PREFIX.length()),
                              Locale.US);  // for . as decimal point!
	  
//            System.out.println("using format: '"+line.substring(format_def.length())+"'");
        }
        if(!line.startsWith("#"))
        {
          Object[] objs = line_format.parse(line);
	  
          float latitude = 0.0f;
          float longitude = 0.0f;
          float altitude = 0.0f;
          String altitude_unit = "";
          float speed = 0.0f;
          String speed_unit = "";
          Date date = null;
	  
          if(objs.length > 0)
          {
            latitude = ((Number)objs[0]).floatValue();
            if(objs.length > 1)
            {
              longitude = ((Number)objs[1]).floatValue();
              if(objs.length > 2)
              {
                altitude = ((Number)objs[2]).floatValue();
                if(objs.length > 3)
                {
                  altitude_unit = (String)objs[3];
                  if(objs.length > 4)
                  {
                    speed = ((Number)objs[4]).floatValue();
                    if(objs.length > 5)
                    {
                      speed_unit = (String)objs[5];
                      if(objs.length > 6)
                      {
                        if(objs[6] instanceof Date)
                          date = (Date)objs[6];
                        else
                          date = null;
                      }
                    }
                  }
                }
              }
            }
          }
          Trackpoint point = new TrackpointImpl();
          point.setDate(date);
          point.setLongitude(longitude);
          point.setLatitude(latitude);
          point.setAltitude(altitude);
          track.addWaypoint(point);
        }
      }
      if(Debug.DEBUG)
        Debug.println("read_track_plugin","finished loading GPSMap track");

      track_in.close();
      
      Date first_date = ((Trackpoint)track.getWaypoint(0)).getDate();
      if(first_date == null)
        first_date = new Date(); // use now!
      SimpleDateFormat track_date_format = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
      track.setIdentification(track_date_format.format(first_date));
      track.setComment("track created by GPSMap");
      System.out.println("read track:"+track);
      return(new Track[] {track});
    }
    catch(ParseException pe)
    {
      System.err.println("ERROR: ParseError in line "+linenumber);
      pe.printStackTrace();
    }
    catch(ClassCastException cce)
    {
      System.err.println("ERROR: Error in line "+linenumber+": "+cce.getMessage());
      cce.printStackTrace();
    }
    catch(NumberFormatException nfe)
    {
      System.out.println("ERROR: ParseError in line "+linenumber);
      nfe.printStackTrace();
    }
    catch(NoSuchElementException nsee)
    {
      System.out.println("ERROR: Invalid track line format in line "+linenumber);
      nsee.printStackTrace();
    }
    return(new Track[0]); // in case of error, return empty array
  }


//----------------------------------------------------------------------
/**
 * Returns the unique id of the plugin. The id is used to identify
 * the plugin and to distinguish it from other plugins.
 *
 * @return The id of the plugin.
 */

  public String getPluginIdentifier()
  {
    return("ReadTrackGPSMap");
  }

//----------------------------------------------------------------------
/**
 * Returns the version of the plugin. The version may be used to
 * choose between different version of the same plugin. 
 *
 * @return The version of the plugin.
 */

  public float getPluginVersion()
  {
    return(1.0f);
  }

//----------------------------------------------------------------------
/**
 * Returns the name of the Plugin. The name should be a human
 * readable and understandable name like "Save Image as JPEG". It is
 * prefereable but not necessary that the name is localized. 
 *
 * @return The name of the plugin.
 */

  public String getPluginName()
  {
    return("Read Track Data from GPSMap");
  }

//----------------------------------------------------------------------
/**
 * Returns a description of the Plugin. The description should be
 * human readable and understandable like "This plugin saves the
 * content of the main window as an image in jpeg format". It is
 * prefereable but not necessary that the description is localized. 
 *
 * @return The description of the plugin.
 */

  public String getPluginDescription()
  {
    return("This plugin reads track data that was saved by GPSMap.");
  }


}
