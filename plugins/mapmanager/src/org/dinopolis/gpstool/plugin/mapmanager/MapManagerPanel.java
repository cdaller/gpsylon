/***********************************************************************
 * @(#)$RCSfile$   $Revision$$Date$
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


package org.dinopolis.gpstool.plugin.mapmanager;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.dinopolis.gpstool.GpsylonKeyConstants;
import org.dinopolis.gpstool.hook.MapManagerHook;
import org.dinopolis.util.Resources;



//----------------------------------------------------------------------
/**
 * This panel shows a table with all maps available from the map manager.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class MapManagerPanel extends JPanel implements GpsylonKeyConstants
{

  MapInfoTable map_info_table_;
  
//----------------------------------------------------------------------
/**
 * Default constructor
 *
 */
  public MapManagerPanel()
  {
    super(new BorderLayout());
  }


//----------------------------------------------------------------------
/**
 * Initialize the table with the map manager to retrieve the infos from.
 *
 * @param map_manager the map manager
 */
  public void initialize(MapManagerHook map_manager, Resources app_resources)
  {
    map_info_table_ = new MapInfoTable();
    String[] column_names = new String[]
                            {
                            app_resources.getString(KEY_LOCALIZE_SCALE),
                            app_resources.getString(KEY_LOCALIZE_LATITUDE),
                            app_resources.getString(KEY_LOCALIZE_LONGITUDE),
                            app_resources.getString(KEY_LOCALIZE_FILENAME),
                            app_resources.getString(KEY_LOCALIZE_WIDTH),
                            app_resources.getString(KEY_LOCALIZE_HEIGHT)
                            };
    String angle_format = app_resources.getString(KEY_ANGLE_FORMAT_LATLON);
    map_info_table_.initialize(map_manager,column_names,angle_format);

    add(map_info_table_);
  }
  
}


