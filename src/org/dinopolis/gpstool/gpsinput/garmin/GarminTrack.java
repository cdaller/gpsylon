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


package org.dinopolis.gpstool.gpsinput.garmin;

import java.awt.Color;
import java.util.List;
import java.util.Vector;
import org.dinopolis.gpstool.gpsinput.GPSTrack;

//----------------------------------------------------------------------
/**
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GarminTrack extends GarminRoute implements GPSTrack
{
  boolean display_;

  short color_;
  
//--------------------------------------------------------------------------------
/**
 * Add a garmin trackpoint at the end of the list.
 * @param routepoint The routepoint to add.
 */
  public void addWaypoint(GarminTrackpoint trackpoint)
  {
        // adopts the GarminTrackpoint to a GPSTrackpoint
    super.addWaypoint(new GarminTrackpointAdapter(trackpoint));
  }

  public String toString()
  {
    StringBuffer buf = new StringBuffer();
    buf.append("GarminTrack[identification=").append(getIdentification()).append(",");
    buf.append("track points/links=").append(getWaypoints().toString()).append("]");
    return(buf.toString());
  }
}
