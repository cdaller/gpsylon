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

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.dinopolis.gpstool.MapInfo;
import org.dinopolis.gpstool.event.MapsChangedEvent;
import org.dinopolis.gpstool.event.MapsChangedListener;
import org.dinopolis.gpstool.util.angle.Latitude;
import org.dinopolis.gpstool.util.angle.Longitude;

//----------------------------------------------------------------------
/**
 * A table model to show the map info.
 */
public class MapInfoTableModel
	extends AbstractTableModel
	implements MapsChangedListener, MapInfoHoldingTable
{
	String[] column_names_;
	List map_infos_;

	public MapInfoTableModel(String[] column_names, Collection map_infos)
	{
		column_names_ = column_names;
		map_infos_ = new Vector(map_infos);
	}

	public MapInfo getMapInfo(int row)
	{
		return ((MapInfo) map_infos_.get(row));
	}
	
	public int getRow(MapInfo map_info)
	{
		return(map_infos_.indexOf(map_info));
	}

	public String getColumnName(int column)
	{
		return (column_names_[column]);
	}

	public int getRowCount()
	{
		return (map_infos_.size());
	}

	public int getColumnCount()
	{
		return (column_names_.length);
	}

	public Object getValueAt(int row, int column)
	{
		switch (column)
		{
			case 0 : // scale
				return (new Float(((MapInfo) map_infos_.get(row)).getScale()));
			case 1 : // latitiude
				return (new Latitude(((MapInfo) map_infos_.get(row)).getLatitude()));
			case 2 : // longitude
				return (new Longitude(((MapInfo) map_infos_.get(row)).getLongitude()));
			case 3 : // filename
				return (((MapInfo) map_infos_.get(row)).getFilename());
			case 4 : // image width
				return (new Integer(((MapInfo) map_infos_.get(row)).getWidth()));
			case 5 : // image height
				return (new Integer(((MapInfo) map_infos_.get(row)).getHeight()));
			default :
				return ("unknown column: " + column);
		}
	}

	public boolean isCellEditable(int row, int column)
	{
		return (false);
	}

	public void setValueAt(Object value, int row, int column)
	{
	}

	public Class getColumnClass(int column)
	{
		return (getValueAt(0, column).getClass());
	}

	/**
	 * Called from the map manager whenever maps are added or removed.
	 * @see org.dinopolis.gpstool.event.MapsChangedListener#mapsChanged(org.dinopolis.gpstool.event.MapsChangedEvent)
	 */
	public void mapsChanged(MapsChangedEvent event)
	{
		if (event.getAction() == MapsChangedEvent.MAP_ADDED)
		{
			int pos = map_infos_.size();
			map_infos_.add(event.getMapInfo());
			fireTableRowsInserted(pos, pos);
		}
		else // map removed
			{
			int pos = map_infos_.indexOf(event.getMapInfo());
			if (pos < 0)
				return;
			map_infos_.remove(event.getMapInfo());
			fireTableRowsDeleted(pos, pos);
		}
	}

}
