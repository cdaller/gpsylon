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

//----------------------------------------------------------------------
/**
 * Holds information about the estimated position error of the gps.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GPSPositionError
{
  protected double spherical_error_;
  protected double horizontal_error_;
  protected double vertical_error_;
  
//----------------------------------------------------------------------
/**
 * Constructor using horizontal and vertical error (in meters).
 *
 * @param horizontal_error the estimated horizontal error (in meters).
 * @param vertical_error the estimated vertical error (in meters).
 */
  public GPSPositionError(double spherical_error,
                          double horizontal_error,
                          double vertical_error)
  {
    spherical_error_ = spherical_error;
    horizontal_error_ = horizontal_error;
    vertical_error_ = vertical_error;
  }

//----------------------------------------------------------------------
/**
 * Returns the estimated vertical error of the gps position (in meters).
 *
 * @return the estimated vertical error (in meters).
 */
  public double getVerticalError()
  {
    return(vertical_error_);
  }

//----------------------------------------------------------------------
/**
 * Returns the estimated horizontal error of the gps position (in meters).
 *
 * @return the estimated horizontal error (in meters).
 */
  public double getHorizontalError()
  {
    return(horizontal_error_);
  }

//----------------------------------------------------------------------
/**
 * Returns the estimated overall spherical error of the gps position
 * (in meters).
 *
 * @return the estimated overall spherical error (in meters).
 */
  public double getSphericalError()
  {
    return(spherical_error_);
  }

//----------------------------------------------------------------------
/**
 * Returns true if the other objecs is a GPSPositionError object and
 * the vertical and horizontal errors are equal, false otherwise.
 *
 * @return true if the other objecs is a GPSPositionError object and
 * the vertical and horizontal errors are equal, false otherwise.
 */
  public boolean equals(Object other)
  {
    return((horizontal_error_ == ((GPSPositionError)other).horizontal_error_)
           & (vertical_error_ == ((GPSPositionError)other).vertical_error_)
           & (spherical_error_ == ((GPSPositionError)other).spherical_error_));
  }
}

