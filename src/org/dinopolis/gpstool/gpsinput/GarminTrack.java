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


package org.dinopolis.gpstool.gpsinput;
import java.util.List;
import java.util.Vector;

//----------------------------------------------------------------------
/**
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GarminTrack
{
  Vector track_points_ = new Vector();
  boolean display_;
  short color_;
  String identification_;

  public GarminTrack()
  {
  }

//----------------------------------------------------------------------
/**
 * Get the identification.
 *
 * @return the identification.
 */
  public String getIdentification() 
  {
    return (identification_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the identification.
 *
 * @param identification the identification.
 */
  protected void setIdentification(String identification) 
  {
    identification_ = identification;
  }

//----------------------------------------------------------------------
/**
 * Get a list of track_points (GarminTrackpoint objects).
 *
 * @return a list of track_points.
 */
  public List getTrackPoints() 
  {
    return (track_points_);
  }
  
//----------------------------------------------------------------------
/**
 * Add a track_point.
 *
 * @param track_point the route point to add.
 */
  public void addTrackPoint(GarminTrackpoint track_point) 
  {
    track_points_.add(track_point);
  }

  public String toString()
  {
    StringBuffer buf = new StringBuffer();
    buf.append("GarminTrack[identification=").append(identification_).append(",");
    buf.append("track points/links=").append(track_points_.toString()).append("]");
    return(buf.toString());
  }
}
