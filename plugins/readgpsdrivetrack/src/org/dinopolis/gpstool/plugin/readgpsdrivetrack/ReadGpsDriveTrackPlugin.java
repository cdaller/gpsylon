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


package org.dinopolis.gpstool.plugin.readgpsdrivetrack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import org.dinopolis.gpstool.GPSMapKeyConstants;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.plugin.ReadTrackPlugin;
import org.dinopolis.gpstool.track.Track;
import org.dinopolis.gpstool.track.TrackImpl;
import org.dinopolis.gpstool.track.Trackpoint;
import org.dinopolis.gpstool.track.TrackpointImpl;
import org.dinopolis.util.Debug;
import org.dinopolis.util.Resources;

//----------------------------------------------------------------------
/**
 * This plugin reads track data from a stream (ususally from a file)
 * and provides one or more tracks (lists of {@link
 * org.dinopolis.gpstool.util.geoscreen.GeoScreenTrackPoint}
 * objects). The track format is like gpsdrive writes its tracks
 * (latitude longitude altitude date) - separated with spaces.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class ReadGpsDriveTrackPlugin implements ReadTrackPlugin, GPSMapKeyConstants
{

  public ReadGpsDriveTrackPlugin()
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
    return("GPSDrive Tracks");
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
    return(new String[] {"sav"});
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

      Track track = new TrackImpl();
      StringTokenizer tokenizer;
      SimpleDateFormat date_format = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy",Locale.US);
      String line;
      boolean new_segment = true;
      while((line = track_in.readLine()) != null)
      {
        linenumber++;
        if(!line.startsWith("#"))
        {
          tokenizer = new StringTokenizer(line," ");
          
          float latitude = Float.parseFloat(tokenizer.nextToken());
          if(latitude == 1001.0f)  // 1001 means invalid position
          {
            new_segment = true;
          }
          else
          {
            float longitude = Float.parseFloat(tokenizer.nextToken()); 
            float altitude = Integer.parseInt(tokenizer.nextToken());
                // hack:
            String first_date_token = tokenizer.nextToken();
            Date date = date_format.parse(line,new ParsePosition(line.indexOf(first_date_token)));
            
            Trackpoint point = new TrackpointImpl();
            point.setDate(date);
            point.setLongitude(longitude);
            point.setLatitude(latitude);
            point.setAltitude(altitude);
            point.setNewTrack(new_segment);
            track.addWaypoint(point);
            new_segment = false;
          }
        }
      }

      if(Debug.DEBUG)
        Debug.println("read_track","finished loading gpsdrive track");
      
      track_in.close();
      Date first_date = ((Trackpoint)track.getWaypoint(0)).getDate();
      SimpleDateFormat track_date_format = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
      track.setIdentification(track_date_format.format(first_date));
      track.setComment("track created by gpsdrive");
      return(new Track[] {track});
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
    return("ReadTrackGpsDrive");
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
    return("Read Track Data from gpsdrive");
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
    return("This plugin reads track data that was saved by gpsdrive.");
  }
}
