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

import javax.swing.JTable;
import org.dinopolis.gpstool.MapManagerHook;
import java.util.Collection;
import org.dinopolis.gpstool.gui.util.AngleCellRenderer;
import org.dinopolis.gpstool.MapInfo;
import org.dinopolis.gpstool.util.angle.Latitude;
import org.dinopolis.gpstool.util.angle.Longitude;

//----------------------------------------------------------------------
/**
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class MapInfoTable extends JTable
{
  MapManagerHook map_manager_;
  
//----------------------------------------------------------------------
/**
 * Default Constructor
 */
  public MapInfoTable()
  {
    super();
  }

//----------------------------------------------------------------------
/**
 * Initialize the table with the map manager to retrieve the infos from.
 *
 * @param map_manager the map manager
 */
  public void initialize(MapManagerHook map_manager, String[] column_names,
                         String angle_format)
  {
    map_manager_ = map_manager;
    Collection map_infos = map_manager.getMapInfos();
    MapInfo[] infos = new MapInfo[map_infos.size()];
    infos = (MapInfo[])map_infos.toArray(infos);
        // set table model for MapInfo objects
    MapInfoTableModel model = new MapInfoTableModel(column_names,infos);
    setModel(model);

        // set renderer for latitude/longitude
    setDefaultRenderer(Latitude.class, new AngleCellRenderer(angle_format));
    setDefaultRenderer(Longitude.class, new AngleCellRenderer(angle_format));
  }
  
}


