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
import org.dinopolis.gpstool.gpsinput.GPSTrack;

//----------------------------------------------------------------------
/**
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GarminTrack extends GarminRoute implements GPSTrack
{
  protected boolean display_;

  protected Color color_;
  
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

//--------------------------------------------------------------------------------
/**
 * Returns true if this track is displayed on the gps device, false otherwise.
 *
 * @return true if this track is displayed on the gps device, false otherwise.
 */
  public boolean isDisplayed()
  {
    return(display_);
  }

//--------------------------------------------------------------------------------
/**
 * Define whether to display the current track or not.
 *
 * @param display if true, display current track.
 */
  public void setDisplayed(boolean display)
  {
    display_ = display;
  }

//--------------------------------------------------------------------------------
/**
 * Get the color of the current track.
 */
  public Color getColor()
  {
    return(color_);
  }

//--------------------------------------------------------------------------------
/**
 * Define the color of the current track. See {@link GarminWaypointD108} for colors.
 *
 * @param color Color of the current track.
 */
  public void setColor(Color color)
  {
    color_ = color;
  }
}
