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

//----------------------------------------------------------------------
/**
 * Represents a Xfer_Complete package from a garmin device.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GarminXferComplete
{
//   public static final int ALMANAC_COMPLETE = 1;
//   public static final int PROXIMITY_WAYPOINTS__COMPLETE = 3;
//   public static final int ROUTES_COMPLETE = 4;
//   public static final int TRACKS_COMPLETE = 6;
//   public static final int WAYPOINTS_COMPLETE = 7;

  protected int num_packages_;
  
//----------------------------------------------------------------------
/**
 * Constructor using an int array of garmin data.
 * @param buffer the garmin package as int[].
 * @deprecated use the constructor with the GarminPackage instead
 */
  public GarminXferComplete(int[] buffer)
  {
    num_packages_ = buffer[3];
  }

//----------------------------------------------------------------------
/**
 * Constructor using an garmin package.
 * @param garmin_package the package from the gps device
 */
  public GarminXferComplete(GarminPackage garmin_package)
  {
    num_packages_ = garmin_package.get();
  }

  

//----------------------------------------------------------------------
/**
 * Returns the number of packages transfered.
 * @return the number of packages transfered.
 */
  public int getNumberPackages()
  {
    return(num_packages_);
  }

// //----------------------------------------------------------------------
// /**
//  * Returns the type of transfer complete.
//  * @return the type of transfer complete (Route, track, ...).
//  */
//   public int getCompleteType()
//   {
//     return(complete_type_);
//   }
}
