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


package org.dinopolis.gpstool.gui.util;

import javax.swing.ComboBoxModel;
import org.dinopolis.gpstool.TrackManager;



//----------------------------------------------------------------------
/**
 * A list model to be used when tracks are displayed.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class TrackListComboBoxModel extends TrackListModel implements ComboBoxModel
{
  Object selected_;
  
//----------------------------------------------------------------------
/**
 * Constructor taking a track manager to read the track information from.
 *
 * @param track_manager the track manager to get the track information from.
 */
  public TrackListComboBoxModel(TrackManager track_manager)
  {
    super(track_manager);
  }

//----------------------------------------------------------------------
/**
 * Return the selected item
 *
 * @return the selected item
 */
  public Object getSelectedItem()
  {
    return(selected_);
  }

//----------------------------------------------------------------------
/**
 * Set the selected item
 *
 * @param item the selected item
 */
  public void setSelectedItem(Object item)
  {
    selected_ = item;
  }

  
}



