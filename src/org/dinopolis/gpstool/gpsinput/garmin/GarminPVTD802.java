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
 * D802 is an extension to D800 and has some additional (unknown) fields.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GarminPVTD802 extends GarminPVTD800
{
  public GarminPVTD802(int[] buffer)
  {
		super(buffer);
  }

  public GarminPVTD802(GarminPackage pack)
  {
		super(pack);
  }

// //----------------------------------------------------------------------
// /**
//  * Convert data type to {@link GarminPackage}
//  * @return GarminPackage representing content of data type.
//  */
//   public GarminPackage toGarminPackage(int package_id)
//   {
//     int data_length = 4 + 4 + 4 + 4 + 2 + 8 + 8 + 8 + 4 + 4 + 4 + 4 + 2 + 4;
//     GarminPackage pack = new GarminPackage(package_id,data_length);
//     int[] data = new int[data_length];

//     data = GarminDataConverter.setGarminFloat(alt_,data,0);
//     data = GarminDataConverter.setGarminFloat(epe_,data,4);
//     data = GarminDataConverter.setGarminFloat(eph_,data,8);
//     data = GarminDataConverter.setGarminFloat(epv_,data,12);
//     data = GarminDataConverter.setGarminWord(fix_,data,16);
//     data = GarminDataConverter.setGarminDouble(tow_,data,18);
//     data = GarminDataConverter.setGarminRadiantDegrees(lat_,data,26);
//     data = GarminDataConverter.setGarminRadiantDegrees(lon_,data,34);
//     data = GarminDataConverter.setGarminFloat(east_,data,42);
//     data = GarminDataConverter.setGarminFloat(north_,data,46);
//     data = GarminDataConverter.setGarminFloat(up_,data,50);
//     data = GarminDataConverter.setGarminFloat(msl_height_,data,54);
//     data = GarminDataConverter.setGarminWord(leap_seconds_,data,58);
//     data = GarminDataConverter.setGarminLong(wn_days_,data,60);
//     pack.put(data);

//     return (pack);
//   }

//----------------------------------------------------------------------
/**
 * Print PVT data in human readable form.
 * @return string representation of this object.
 */
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("GarminPVTD802[");
    buffer.append("alt=").append(alt_).append(", ");
    buffer.append("epe=").append(epe_).append(", ");
    buffer.append("eph=").append(eph_).append(", ");
    buffer.append("epv=").append(epv_).append(", ");
    buffer.append("fix=").append(fix_).append(", ");
    buffer.append("tow=").append(tow_).append(", ");
    buffer.append("lat=").append(lat_).append(", ");
    buffer.append("lon=").append(lon_).append(", ");
    buffer.append("east=").append(east_).append(", ");
    buffer.append("north=").append(north_).append(", ");
    buffer.append("up=").append(up_).append(", ");
    buffer.append("msl_height=").append(msl_height_).append(", ");
    buffer.append("leap_seconds=").append(leap_seconds_).append(", ");
    buffer.append("wn_days=").append(wn_days_);
    buffer.append("]");

    return(buffer.toString());
  }
}
