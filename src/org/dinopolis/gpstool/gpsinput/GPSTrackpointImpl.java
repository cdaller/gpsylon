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

import java.util.Date;

/**
 * Simple implementation of the interface.
 * @author cdaller
 */
public class GPSTrackpointImpl extends GPSWaypointImpl implements GPSTrackpoint
{
  protected Date date_;
  boolean new_track_;
  
  /**
   * @see org.dinopolis.gpstool.gpsinput.GPSTrackpoint#getDate()
   */
  public Date getDate()
  {
    return date_;
  }

  /**
   * @see org.dinopolis.gpstool.gpsinput.GPSTrackpoint#setDate(java.util.Date)
   */
  public void setDate(Date date)
  {
    date_ = date;
  }

  /**
   * @see org.dinopolis.gpstool.gpsinput.GPSTrackpoint#isNewTrack()
   */
  public boolean isNewTrack()
  {
    return new_track_;
  }

  /**
   * @see org.dinopolis.gpstool.gpsinput.GPSTrackpoint#setNewTrack(boolean)
   */
  public void setNewTrack(boolean new_segment)
  {
    new_track_ = new_segment;
  }

}
