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


package org.dinopolis.gpstool.plugin.modifytracks;

import java.awt.Container;

import javax.swing.JFrame;

import org.dinopolis.gpstool.GPSMapKeyConstants;



//----------------------------------------------------------------------
/**
 * This panel shows a table with all maps available from the map manager.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class NewTrackFrame extends JFrame implements GPSMapKeyConstants
{


//----------------------------------------------------------------------
/**
 * Default constructor
 *
 */
  public NewTrackFrame()
  {
    super();
  }


//----------------------------------------------------------------------
/**
 * Initialize the table with the map manager to retrieve the infos from.
 *
 * @param map_manager the map manager
 */
  public void initialize()
  {

  	Container content_pane = getContentPane();






		//content_pane.add(optionPane);
		//setContentPane(content_pane);


    //pack();
    //setSize(200,200);
    //setVisible(true);
  }



}

