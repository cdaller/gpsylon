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

import org.dinopolis.gpstool.gpsinput.GPSTrack;
import org.dinopolis.gpstool.gpsinput.GPSTrackpoint;
import java.util.Iterator;

//----------------------------------------------------------------------
/**
 * An vector based implementation of {@link Track}. Only {@link
 * org.dinopolis.gpstool.track.Waypoint} objects are allowed!
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class TrackImpl extends RouteImpl implements Track
{

//----------------------------------------------------------------------
/**
 * Empty constructor
 */
  public TrackImpl()
  {
    super();
  }

//----------------------------------------------------------------------
/**
 * Copy constructor for {@link org.dinopolis.gpstool.gpsinput.GPSTrack}.
 * Creates for all GPSTrackpoints a corresponding Trackpoint object.
 *
 * @param track the track from a gps device.
 */
  public TrackImpl(GPSTrack track)
  {
    this();
    setIdentification(track.getIdentification());
    setComment(track.getComment());
    Iterator iterator = track.getWaypoints().iterator();
    GPSTrackpoint point;
    while(iterator.hasNext())
    {
      point = (GPSTrackpoint)iterator.next();
      addWaypoint(new TrackpointImpl(point));
    }

  }
//----------------------------------------------------------------------
/**
 * Copy constructor for {@link org.dinopolis.gpstool.gpsinput.Track}.
 * Creates for all Trackpoints a corresponding Trackpoint object. This
 * constructor is needed, as otherwise the trackpoints are copied as
 * GPSTrackpoints and the screen coordinates are not copied.
 *
 * @param track the track from a gps device.
 */
  public TrackImpl(Track track)
  {
    this();
    setIdentification(track.getIdentification());
    setComment(track.getComment());
    Iterator iterator = track.getWaypoints().iterator();
    Trackpoint point;
    while(iterator.hasNext())
    {
      point = (Trackpoint)iterator.next();
      addWaypoint(new TrackpointImpl(point));
    }

  }


  public String toString()
  {
    StringBuffer buf = new StringBuffer();
    buf.append("TrackImpl[identification=").append(identification_).append(",");
    buf.append("track points=").append(route_points_.toString()).append("]");
    return(buf.toString());
  }

}

