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

import javax.swing.table.AbstractTableModel;
import org.dinopolis.gpstool.MapInfo;
import org.dinopolis.gpstool.util.angle.Latitude;
import org.dinopolis.gpstool.util.angle.Longitude;



//----------------------------------------------------------------------
/**
 * A table model to show the map info.
 */
public class MapInfoTableModel extends AbstractTableModel
{
  String[] column_names_;
  MapInfo[] map_infos_;
    
  public MapInfoTableModel(String[] column_names,
                           MapInfo[] map_infos)
  {
    column_names_ = column_names;
    map_infos_ = map_infos;
  }

  public MapInfo getMapInfo(int row)
  {
    return(map_infos_[row]);
  }

  public String getColumnName(int column)
  {
    return(column_names_[column]);
  }

  public int getRowCount()
  {
    return(map_infos_.length);
  }

  public int getColumnCount()
  {
    return(column_names_.length);
  }

  public Object getValueAt(int row, int column)
  {
    switch(column)
    {
    case 0: // scale
      return(new Float(map_infos_[row].getScale()));
    case 1: // latitiude
      return(new Latitude(map_infos_[row].getLatitude()));
    case 2: // longitude
      return(new Longitude(map_infos_[row].getLongitude()));
    case 3: // filename
      return(map_infos_[row].getFilename());
    case 4: // image width
      return(new Integer(map_infos_[row].getWidth()));
    case 5: // image height
      return(new Integer(map_infos_[row].getHeight()));
    default:
      return("unknown column: "+column);
    }
  }

  public boolean isCellEditable(int row, int column)
  {
    return(false);
  }

  public void setValueAt(Object value, int row, int column)
  {
  }

  public Class getColumnClass(int column)
  {
    return(getValueAt(0, column).getClass());
  }

}


