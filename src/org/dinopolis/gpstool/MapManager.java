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

package org.dinopolis.gpstool;

import java.util.Vector;
import java.util.HashSet;
import org.dinopolis.util.Resources;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.io.IOException;
import java.util.Iterator;
import org.dinopolis.gpstool.event.MapsChangedListener;
import org.dinopolis.gpstool.event.MapsChangedEvent;
import java.util.Locale;
import org.dinopolis.gpstool.util.FileUtil;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import javax.swing.JOptionPane;
import java.util.StringTokenizer;
import java.awt.Frame;
import java.lang.NumberFormatException;
import java.util.NoSuchElementException;
import com.bbn.openmap.proj.Projection;
import java.awt.Point;
import org.dinopolis.gpstool.gui.util.ImageInfo;
import java.util.Collection;
import java.util.TreeSet;
import org.dinopolis.gpstool.util.MapInfoScaleComparator;
import org.dinopolis.gpstool.gui.util.ImageInfoScaleComparator;


//----------------------------------------------------------------------
/**
 * This is an application that shows maps, tracks, gps position,
 * etc. It heavily depends on its resources, as all default values are
 * stored there. The information is organized and painted in layers.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class MapManager implements MapManagerHook, GPSMapKeyConstants
{

      /** a Vector containing the map infos for the used maps */
  protected Collection map_infos_;
  protected HashSet used_map_filenames_ = new HashSet();
  protected Resources resources_;
  protected Vector maps_changed_listeners_;
  protected Frame main_frame_;
  
//----------------------------------------------------------------------
/**
 * Empty Constructor
 */
  public MapManager()
  {
    map_infos_ = new TreeSet(new MapInfoScaleComparator());
  }

  public void initialize(Resources resources, Frame main_frame)
  {
    resources_ = resources;
    main_frame_ = main_frame;
    loadMapInfos();
  }

//----------------------------------------------------------------------
/**
 * Adds a new map to the system. This method is responsible to make
 * this information persistent and to add this map to the running
 * system. If the filename in map_info is already used, the map is not
 * added.
 *
 * @param map_info the new map
 */

  public void addNewMap(MapInfo map_info)
  {
    String new_map_filename = map_info.getFilename(); // full filename
    if(used_map_filenames_.contains(new_map_filename))
    {
      System.err.println("Filename '"+new_map_filename+"' already used, map ignored!");
      return;
    }
    
    String main_dir = resources_.getString(KEY_FILE_MAINDIR);
    String description_filename = resources_.getString(KEY_FILE_MAP_DESCRIPTION_FILE);
    
    String maps_filename = main_dir + File.separator + description_filename;

    BufferedWriter map_writer;

    if(new_map_filename.startsWith(main_dir))
      new_map_filename = new_map_filename.substring(main_dir.length()+1);
    try
    {
      map_writer= new BufferedWriter(new FileWriter(maps_filename,true)); // append mode

      StringBuffer new_line = new StringBuffer();

          // Locale.US is used to get the decimal point as a point!
      NumberFormat latlon_formatter = NumberFormat.getInstance(Locale.US);
      if(latlon_formatter instanceof DecimalFormat)
        ((DecimalFormat)latlon_formatter).applyPattern("#.#####");
      
      NumberFormat scale_formatter = NumberFormat.getInstance(Locale.US);
      if(scale_formatter instanceof DecimalFormat)
        ((DecimalFormat)scale_formatter).applyPattern("#.#");
      
      new_line.append(new_map_filename);
      new_line.append(" ");
      new_line.append(latlon_formatter.format(map_info.getLatitude()));
      new_line.append(" ");
      new_line.append(latlon_formatter.format(map_info.getLongitude()));
      new_line.append(" ");
      new_line.append(scale_formatter.format(map_info.getScale()));
      new_line.append(" ");
      new_line.append(map_info.getWidth()); 
      new_line.append(" ");
      new_line.append(map_info.getHeight()); 
      map_writer.write(new_line.toString());
      map_writer.newLine();
    
      map_writer.close();

      fireMapsChanged(new MapsChangedEvent(map_info,MapsChangedEvent.MAP_ADDED));
    }
    catch(IOException ioe)
    {
      System.err.println("ERROR: On writing to map description file '"+maps_filename+"': "+ioe.getMessage());
      return;
    }
  }


//----------------------------------------------------------------------
/**
 * Returns a collection that holds information about all available maps
 * (MapInfo objects).
 *
 * @return information about all available maps.
 */
  public Collection getMapInfos()
  {
    synchronized(map_infos_)
    {
      TreeSet clone = new TreeSet(new MapInfoScaleComparator());
      clone.addAll(map_infos_);
      return(clone); // return copy of infos
    }
  }


//----------------------------------------------------------------------
/**
 * Loads the mapinfo data from the map description file and sends it
 * to the map layer.
 *
 * @return a Vector containing MapInfo objects.
 */

  protected void loadMapInfos()
  {
    String main_dir = resources_.getString(KEY_FILE_MAINDIR);

    String description_filename = resources_.getString(KEY_FILE_MAP_DESCRIPTION_FILE);

    
    String full_name = FileUtil.getAbsolutePath(main_dir,description_filename);

    BufferedReader map_reader;
    try
    {
      map_reader= new BufferedReader(new FileReader(full_name));
    }
    catch(FileNotFoundException fnfe)
    {
      System.err.println("ERROR: Could not open map description file '"+full_name+"'");
      JOptionPane.showMessageDialog(main_frame_,
                                    resources_.getString(KEY_LOCALIZE_MESSAGE_FILE_NOT_FOUND_MESSAGE)
                                    +": '"+full_name+"'",
                                    resources_.getString(KEY_LOCALIZE_MESSAGE_ERROR_TITLE),
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    try
    {
      int linenumber = 0;
      String line;
      String latitude_string;
      String longitude_string;
      String map_filename;
      String scale_string;
      String image_height_string;
      String image_width_string;
      MapInfo map_info;
      StringTokenizer tokenizer;
      synchronized(map_infos_)
      {
	while ((line = map_reader.readLine()) != null)
	{
	  linenumber++;
	  if ((!line.startsWith("#")) && (line.length() > 0))
	  {
	    try
	    {
	      tokenizer = new StringTokenizer(line);
	      map_filename = tokenizer.nextToken();
	      latitude_string = tokenizer.nextToken();
	      longitude_string = tokenizer.nextToken();
	      scale_string = tokenizer.nextToken();
	      image_width_string = tokenizer.nextToken();
	      image_height_string = tokenizer.nextToken();
	      
	      // check for absolute or relative pathnames:
	      File map_file = new File(map_filename);
	      if(!map_file.isAbsolute())
		map_filename = new File(main_dir,map_filename).getCanonicalPath();
	      
	      used_map_filenames_.add(map_filename);
	      
	      try
	      {
		map_info = new MapInfo(map_filename,
				       Double.parseDouble(latitude_string),
				       Double.parseDouble(longitude_string),
				       Float.parseFloat(scale_string),
				       Integer.parseInt(image_width_string),
				       Integer.parseInt(image_height_string));
//              System.out.println("MapInfo loaded: "+map_info);
		map_infos_.add(map_info);
	      }
	      catch(NumberFormatException nfe)
	      {
		System.err.println("ERROR: Wrong format in line : "
				   +linenumber+" in file '"+full_name+"':"
				   +nfe.getMessage());
		System.err.println("Ignoring line '"+line+"'");
	      }
	    }
	    catch(NoSuchElementException nsee)
	    {
	      System.err.println("ERROR: reading map description in line "
				 +linenumber+" in file '"+full_name+"'");
	      System.err.println("The correct format of the map description file is:");
	      System.err.println("<mapfilename> <latitude of center> <longitude of center> <scale> <width> <height>");
	      System.err.println("Ignoring line '"+line+"'");
	    }
	  }
	}
      }      
      map_reader.close();
    }
    catch(IOException ioe)
    {
      ioe.printStackTrace();
    }
  }

//----------------------------------------------------------------------
/**
 * Returns a collection of ImageInfo objects that describe all maps and
 * their position that are visible in the given projection. This collection
 * holds also maps that are covered by other maps (on top). The
 * following algorithm is used to to determine the visibility of the
 * maps: if the distance between the center of the image and the
 * center of the viewport (the projection) is less than (image.width +
 * viewport.width)/2 (same with height), the image is visible. The
 * "visible_rectangle" info is not used in the returned ImageInfo
 * objects!
 *
 * @param projection the projection to find the images for.
 */
  public synchronized Collection getAllVisibleImages(Projection projection)
  {
    return(getAllVisibleImages(projection,0.0f));
  }

//----------------------------------------------------------------------
/**
 * Returns a collection of ImageInfo objects that describe all maps and
 * their position that are visible in the given projection. This collection
 * holds also maps that are covered by other maps (on top). The
 * following algorithm is used to to determine the visibility of the
 * maps: if the distance between the center of the image and the
 * center of the viewport (the projection) is less than (image.width +
 * viewport.width)/2 (same with height), the image is visible. The
 * "visible_rectangle" info is not used in the returned ImageInfo
 * objects!
 *
 * @param projection the projection to find the images for.
 * @param min_scale_factor if the scale of the map divided by
 * the scale of the projection is less than this value, the map is sorted
 * out. So if the scale of the projection is 100000 and one map is of
 * scale 25000, the factor would be 1/4, so if a min_scale_factor of 0.5
 * is given, the map would not be taken (as one could not read
 * anything on it anyway).
 */
  public synchronized Collection getAllVisibleImages(Projection projection,
						     double min_scale_factor)
  {
    Point position = new Point();
    Collection map_infos = getMapInfos(); // get clone of map infos
    Vector visible_images = new Vector();
    MapInfo map_info;
    double scaled_image_width;
    double scaled_image_height;
    int projection_width = projection.getWidth();
    int projection_height = projection.getHeight();
    double projection_width_2 = projection_width / 2.0;
    double projection_height_2 = projection_height / 2.0;
    float scale_factor;
    double delta_x;
    double delta_y;
    
        // create projected position of all mapimages:
    Iterator map_iterator = map_infos.iterator();
    while(map_iterator.hasNext())
    {
      map_info = (MapInfo)map_iterator.next();
      scale_factor = map_info.getScale() / projection.getScale();
      if(scale_factor >= min_scale_factor)
      {
	scaled_image_width = map_info.getWidth() * scale_factor;
	scaled_image_height = map_info.getHeight() * scale_factor;
	  
	position = projection.forward(map_info.getCenter(),position);
	  
	// check, if image is visible at all:
	delta_x = Math.abs(projection_width_2 - position.getX());
	delta_y = Math.abs(projection_height_2 - position.getY());
	
	// visible in the given viewport area: if the distance
	// between the center of the image and the center of the
	// viewport is greater than (image.width + viewport.width)/2
	// (same with height), the image is not visible
	
	if((delta_x > (scaled_image_width + projection_width)/2)
	   && (delta_y > (scaled_image_height  + projection_height)/2))
	{
//        System.out.println("image not visible: "+info);
	}
	else
	{
	  visible_images.addElement(new ImageInfo(map_info,
						  (int)(position.getX() - scaled_image_width/2),
						  (int)(position.getY() - scaled_image_height/2),
						  scale_factor));
	}
      }
    }
    return(visible_images);
  }

//----------------------------------------------------------------------
/**
 * Adds as a listener that is informed about changes of the maps
 * (adding, removal).
 *
 * @param listener the listener to add
 */
  public void addMapsChangedListener(MapsChangedListener listener)
  {
    if(maps_changed_listeners_ == null)
      maps_changed_listeners_ = new Vector();
    synchronized(maps_changed_listeners_)
    {
      if(!maps_changed_listeners_.contains(listener))
	maps_changed_listeners_.add(listener);
    }
  }
//----------------------------------------------------------------------
/**
 * Remove a listener that is informed about changes of the maps
 * (adding, removal).
 *
 * @param listener the listener to be removed.
 */
  public void removeMapsChangedListener(MapsChangedListener listener)
  {
    if(maps_changed_listeners_ == null)
      return;
    synchronized(maps_changed_listeners_)
    {
      maps_changed_listeners_.remove(listener);
    }
  }

//----------------------------------------------------------------------
/**
 * Informs all maps changed listeners about a change event.
 *
 * @param event the event to inform the listeners.
 */
  protected void fireMapsChanged(MapsChangedEvent event)
  {
    if(maps_changed_listeners_ == null)
      return;

    Vector listeners;
    synchronized(maps_changed_listeners_)
    {
      listeners = (Vector)maps_changed_listeners_.clone();
    }
    Iterator iterator = listeners.iterator();
    MapsChangedListener listener;
    while(iterator.hasNext())
    {
      listener = (MapsChangedListener)iterator.next();
      listener.mapsChanged(event);
    }
  }
}

