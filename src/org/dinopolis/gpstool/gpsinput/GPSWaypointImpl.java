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

/**
 * Simple Implementation of the interface.
 *
 * @author cdaller
 */
public class GPSWaypointImpl implements GPSWaypoint
{
  protected String identification_;
  protected String comment_;
  protected double latitude_;
  protected double longitude_;
  protected double altitude_;
  protected String symbol_name_;

  /**
   * @see org.dinopolis.gpstool.gpsinput.GPSWaypoint#getIdentification()
   */
  public String getIdentification()
  {
    return identification_;
  }

  /**
   * @see org.dinopolis.gpstool.gpsinput.GPSWaypoint#setIdentification(java.lang.String)
   */
  public void setIdentification(String identification)
  {
    identification_ = identification;
  }

  /**
   * @see org.dinopolis.gpstool.gpsinput.GPSWaypoint#getComment()
   */
  public String getComment()
  {
    return comment_;
  }

  /**
   * @see org.dinopolis.gpstool.gpsinput.GPSWaypoint#setComment(java.lang.String)
   */
  public void setComment(String comment)
  {
    comment_ = comment;
  }

  /**
   * @see org.dinopolis.gpstool.gpsinput.GPSWaypoint#getLatitude()
   */
  public double getLatitude()
  {
    return latitude_;
  }

  /**
   * @see org.dinopolis.gpstool.gpsinput.GPSWaypoint#setLatitude(double)
   */
  public void setLatitude(double latitude)
  {
    latitude_ = latitude;
  }

  /**
   * @see org.dinopolis.gpstool.gpsinput.GPSWaypoint#getLongitude()
   */
  public double getLongitude()
  {
    return  longitude_;
  }

  /**
   * @see org.dinopolis.gpstool.gpsinput.GPSWaypoint#setLongitude(double)
   */
  public void setLongitude(double longitude)
  {
    longitude_ = longitude;
  }

  /**
   * @see org.dinopolis.gpstool.gpsinput.GPSWaypoint#getAltitude()
   */
  public double getAltitude()
  {
    return altitude_;
  }

  /**
   * @see org.dinopolis.gpstool.gpsinput.GPSWaypoint#setAltitude(double)
   */
  public void setAltitude(double altitude)
  {
    altitude_ = altitude;
  }

  /**
   * @see org.dinopolis.gpstool.gpsinput.GPSWaypoint#hasValidAltitude()
   */
  public boolean hasValidAltitude()
  {
    return Double.isNaN(getAltitude());
  }

  /**
   * @see org.dinopolis.gpstool.gpsinput.GPSWaypoint#getSymbolName()
   */
  public String getSymbolName()
  {
    return symbol_name_;
  }

  /**
   * @see org.dinopolis.gpstool.gpsinput.GPSWaypoint#setSymbolName(java.lang.String)
   */
  public void setSymbolName(String name)
  {
    symbol_name_ = name;
  }

}