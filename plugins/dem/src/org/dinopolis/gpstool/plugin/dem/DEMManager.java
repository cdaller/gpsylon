/***********************************************************************
 * @(#)$RCSfile$ $Revision$
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

package org.dinopolis.gpstool.plugin.dem;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import org.dinopolis.gpstool.map.MapInfo;
import org.dinopolis.gpstool.map.MapManager;
import org.dinopolis.gpstool.util.FileUtil;
import org.dinopolis.gpstool.util.MapInfoScaleComparator;
import org.dinopolis.util.Resources;
import org.dinopolis.util.io.FileChangeDetection;

//----------------------------------------------------------------------
/**
 * DEMManager extends the MapManager basically for setting the mlt.txt
 * 
 * TODO: Check MapManager to include possibility fot setting an other map description file
 * 
 * @author Samuel Benz
 * @version $Revision$
 */

public class DEMManager extends MapManager {

	public DEMManager() {
		super();
	}

public void initialize(Resources resources, Frame main_frame)
{
	resources_ = resources;
	main_frame_ = main_frame;

	// load the map infos from the file.
	String main_dir = resources_.getString(KEY_FILE_MAINDIR);
	String description_filename = resources_.getString("file.dem.description_file");
	maps_filename_ = FileUtil.getAbsolutePath(main_dir, description_filename);

    detect_map_file_changes_ = resources.getBoolean(KEY_FILE_MAP_DESCRIPTION_FILE_DETECT_CHANGES);

		synchronized (map_info_lock_)
		{
			try
			{
				map_infos_ = loadMapInfos(maps_filename_);
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}

    if(detect_map_file_changes_)
    {
      FileChangeDetection change_detection = new FileChangeDetection(maps_filename_);
      change_detection.addFileChangeListener(this);
      change_detection.startChangeDetection();
    }
}

	
//----------------------------------------------------------------------
/**
 * Loads the mapinfo data from the map description file and returns them
 * (sorted by scale).
 *
 * @param filename the file to read from.
 * @return a TreeSet containing MapInfo objects.
 */

protected TreeSet loadMapInfos(String filename) throws IOException
{
	TreeSet map_infos = new TreeSet(new MapInfoScaleComparator());
	//File main_dir = new File(filename).getParentFile();

	BufferedReader map_reader;
	try
	{
		map_reader = new BufferedReader(new FileReader(filename));
	}
	catch (FileNotFoundException fnfe)
	{
		System.err.println(
			"ERROR: Could not open map description file '" + filename + "'");
		JOptionPane.showMessageDialog(
			main_frame_,
			resources_.getString(KEY_LOCALIZE_MESSAGE_FILE_NOT_FOUND_MESSAGE)
				+ ": '"
				+ filename
				+ "'",
			resources_.getString(KEY_LOCALIZE_MESSAGE_ERROR_TITLE),
			JOptionPane.ERROR_MESSAGE);
		return (map_infos);
	}

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
				String map_dir = resources_.getString("file.dem.dir");
				
				if (!map_file.isAbsolute())
					map_filename = new File(map_dir, map_filename).getCanonicalPath();

				used_map_filenames_.add(map_filename);

				try
				{
					map_info =
						new DEMInfo(
							map_filename,
							Double.parseDouble(latitude_string),
							Double.parseDouble(longitude_string),
							Float.parseFloat(scale_string),
							Integer.parseInt(image_width_string),
							Integer.parseInt(image_height_string));
					        // System.out.println("MapInfo loaded: "+map_info);
					map_infos.add(map_info);
				}
				catch (NumberFormatException nfe)
				{
					System.err.println(
						"ERROR: Wrong format in line : "
							+ linenumber
							+ " in file '"
							+ filename
							+ "':"
							+ nfe.getMessage());
					System.err.println("Ignoring line '" + line + "'");
				}
			}
			catch (NoSuchElementException nsee)
			{
				System.err.println(
					"ERROR: reading map description in line "
						+ linenumber
						+ " in file '"
						+ filename
						+ "'");
				System.err.println(
					"The correct format of the map description file is:");
				System.err.println(
					"<mapfilename> <latitude of center> <longitude of center> <scale> <width> <height>");
				System.err.println("Ignoring line '" + line + "'");
			}
		}
	}
	map_reader.close();
	return (map_infos);
}
}