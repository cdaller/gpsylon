
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

import org.dinopolis.gpstool.MapInfo;
import org.dinopolis.gpstool.gui.util.TableSorter;

/**
 * @author cdaller
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class MapInfoTableSorter extends TableSorter implements MapInfoHoldingTable
{
	
	/**
	 * Constructor for MapInfoTableSorter.
	 */
	public MapInfoTableSorter()
	{
		super();
	}

	/**
	 * Constructor for MapInfoTableSorter.
	 * @param model
	 */
	public MapInfoTableSorter(MapInfoTableModel model)
	{
		super(model);
	}

	public MapInfo getMapInfo(int row)
	{
		checkModel();
		return(((MapInfoTableModel)model).getMapInfo(sorted_indices_[row]));
	}

	/**
	 * Return the row of the given map info object.
	 * @param map_info
	 */
	public int getRow(MapInfo map_info)
	{
		checkModel();
		int model_row = ((MapInfoTableModel)model).getRow(map_info);
		for(int index = 0; index < sorted_indices_.length; index++)
		{
			if(sorted_indices_[index] == model_row)
			  return(index);
		}
		// should never get to this line:
		return(-1);
	}
}
