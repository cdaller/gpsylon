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

import org.dinopolis.gpstool.gpsinput.GPSRouteImpl;


//----------------------------------------------------------------------
/**
 * At the moment, the same as the GPSRouteImpl. The only exception is
 * that it accepts GarminWaypoints as well and converts them to
 * GPSWaypoints by using the {@link GarminWaypointAdapter}.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GarminRoute extends GPSRouteImpl
{
//--------------------------------------------------------------------------------
/**
 * Add a garmin waypoint at the end of the list.
 * @param routepoint The routepoint to add.
 */
  public void addWaypoint(GarminWaypoint routepoint)
  {
        // adopts the GarminWaypoint to a GPSWaypoint
    super.addWaypoint(new GarminWaypointAdapter(routepoint));
  }
  
}