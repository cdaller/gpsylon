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


package org.dinopolis.gpstool.track;

import java.awt.Point;
import java.util.Date;
import org.dinopolis.gpstool.gpsinput.GPSTrackpoint;
import org.dinopolis.gpstool.gpsinput.GPSWaypoint;
import org.dinopolis.gpstool.util.geoscreen.GeoScreen;



//----------------------------------------------------------------------
/**
 * This class represents a trackpoint that may be projected from
 * geographical coordinates to screen coordinates and vice versa.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class TrackpointImpl extends WaypointImpl implements Trackpoint
{
  protected Date date_;
  protected boolean new_track_;

//----------------------------------------------------------------------
/**
 * Empty Constructor
 */
  public TrackpointImpl()
  {
    super();
  }
  
//----------------------------------------------------------------------
/**
 * Empty Constructor
 */
  public TrackpointImpl(GPSTrackpoint trackpoint)
  {
    super(trackpoint);
    date_ = trackpoint.getDate();
    new_track_ = trackpoint.isNewTrack();
  }
  

//----------------------------------------------------------------------
// Implementaion of GPSTrackpoint Interace:
//----------------------------------------------------------------------

//----------------------------------------------------------------------
/**
 * Returns the date of the given trackpoint or null, if no date was set.
 *
 * @return the date of the given trackpoint or null, if no date was set.
 */
  public Date getDate()
  {
    return(date_);
  }

//----------------------------------------------------------------------
/**
 * Sets the date of the given trackpoit.
 *
 * @param date the date of the trackpoint.
 */
  public void setDate(Date date)
  {
    date_ = date;
  }
  
//----------------------------------------------------------------------
/**
 * Is this the beginning of a new track segment? If true, this point
 * marks the beginning of a new track segment.
 *
 * @return Beginning of new track segment (boolean)
 */
  public boolean isNewTrack()
  {
    return(new_track_);
  }

//----------------------------------------------------------------------
/**
 * Set the beginning of a new track segment? If true, this point
 * marks the beginning of a new track segment.
 *
 * @param new_segment beginning of new track segment
 */
  public void setNewTrack(boolean new_segment)
  {
    new_track_ = new_segment;
  }  
}

