/***********************************************************************
 * @(#)$RCSfile$   $Revision$$Date$
 *
 * Copyright (c) 2001-2003 Sandra Brueckler, Stefan Feitl
 * Written during an XPG-Project at the IICM of the TU-Graz, Austria
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
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GarminFlashInfo
{
  int map_area_;
  
  public GarminFlashInfo(GarminPackage garmin_package)
  {
    map_area_ = garmin_package.getNextAsWord();
  }


  public void addData(GarminPackage garmin_package)
  {
  }

  public int getMapArea()
  {
    return(map_area_);
  }

  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("GarminFlashInfo[");
    buffer.append("map_area=").append(map_area_);
    buffer.append("]");
    return(buffer.toString());
  }
}
