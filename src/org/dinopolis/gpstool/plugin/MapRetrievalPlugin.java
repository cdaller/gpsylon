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


package org.dinopolis.gpstool.plugin;

import java.io.IOException;
import org.dinopolis.gpstool.MapInfo;
import org.dinopolis.gpstool.util.ProgressListener;



//----------------------------------------------------------------------
/**
 * This is the interface of all plugins that are able to retrieve
 * maps.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public interface MapRetrievalPlugin extends Plugin
{
//----------------------------------------------------------------------
/**
 * Retrieve a map with the given parameters and store it in a file.
 *
 * @param latitude the latitude of the center of the map to retrieve.
 * @param longitude the longitude of the center of the map to retrieve.
 * @param wanted_mapblast_scale the scale to download. The base for
 * this value is the scale used by the mapblast server. The plugin has
 * to calculate this value to match the scale used by its map source!
 * @param image_width the width of the map to download.
 * @param image_height the height of the map to download.
 * @param file_path_wo_extension the path of the file, the map should
 * be store to. This path contains the directory and the filename, but
 * not the extension of the file, as the extension may depend on the
 * fileformat the plugin retrieves.
 * @param progress_listener a progress listener to be informed about
 * the progress on retrieval.
 * @return a MapInfo object that holds the complete information about
 * the downloaded map. This includes the complete filename (with
 * extension) and the scale of the image in mapblast format!
 * @throws IOException if an error on reading or writing of the map
 * occured.
 */

  public MapInfo getMap(double latitude, double longitude, double wanted_mapblast_scale,
                        int image_width, int image_height, String file_path_wo_extension,
                        ProgressListener progress_listener)
    throws IOException;

//----------------------------------------------------------------------
/**
 * Returns the scale the plugin would use for the given parameters.
 *
 * @param latitude the latitude of the center of the map to retrieve.
 * @param longitude the longitude of the center of the map to retrieve.
 * @param wanted_mapblast_scale the scale to download. The base for
 * this value is the scale used by the mapblast server. The plugin has
 * to calculate this value to match the scale used by its map source!
 * @param image_width the width of the map to download.
 * @param image_height the height of the map to download.
 * @return the scale the plugin uses for the given parameters.
 */
  public double getMapScale(double latitude, double longitude,
                             double wanted_mapblast_scale, int image_height,
                             int image_width);

}


