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
 * This class handles PVT-information received from GPS-devices. It
 * stores the following values:<br>
 *
 * alt - altitude above wgs84-ellipsoid [m]<br>
 * epe - estimated position error 2sigma [m]<br>
 * eph - estimated position error horizontal only [m]<br>
 * epv - estimated position error vertical only [m]<br>
 * fix - type of position fix (0:unusable, 1:invalid, 2:2D, 3:3D,
 *                             4:2D-differential, 5:3D-differential)<br>
 * tow - time of week [s]: number of seconds since the beginning of the current week<br>
 * lat - latitude [degrees]: reported in rad, has to be converted to degrees<br>
 * lon - longitude [degreees]: reported in rad, has to be converted to degrees<br>
 * east - movement speed to the east [m/s],   movements to the west are
 *                                            reported by negative numbers<br>
 * north - movement speed to the north [m/s], movements to the south are
 *                                            reported by negative numbers<br>
 * up - movement speed upwards [m/s], movement downwards is reported by
 *                                    negative numbers<br>
 * msl_height - height of the wgs84-ellipsoid above/below MSL [m]
 * leap_seconds - difference between GPS and UTC [s]
 * wn_days - number of days since UTC Dec 31, 1989 to the beginning of the current week
 *
 * @author Sandra Brueckler, Stefan Feitl
 * @version $Revision$
 */

public class GarminPVTD800 extends GarminPVT
{
  public GarminPVTD800(int[] buffer)
  {
    // Altitude above WGS84-Ellipsoid [meters]
    alt_ = GarminDataConverter.getGarminFloat(buffer,2);

    // Estimated position errors and position fix
    epe_ = GarminDataConverter.getGarminFloat(buffer,6);
    eph_ = GarminDataConverter.getGarminFloat(buffer,10);
    epv_ = GarminDataConverter.getGarminFloat(buffer,14);
    fix_ = GarminDataConverter.getGarminWord(buffer,18);
    
    // Time of week [seconds]
    tow_ = GarminDataConverter.getGarminDouble(buffer,20);
    
    // Latitude and longitude
    lat_ = GarminDataConverter.getGarminRadiantDegrees(buffer,28);
    lon_ = GarminDataConverter.getGarminRadiantDegrees(buffer,36);
    
    // Movement speeds in east, north, up direction
    east_ = GarminDataConverter.getGarminFloat(buffer,44);
    north_ = GarminDataConverter.getGarminFloat(buffer,48);
    up_ = GarminDataConverter.getGarminFloat(buffer,52);
    
    // Height of WGS84-Ellipsoid above MSL [meters]
    msl_height_ = GarminDataConverter.getGarminFloat(buffer,56);
    
    // Difference between GPS and UTS [seconds]
    leap_seconds_ = GarminDataConverter.getGarminWord(buffer,60);
    
    // Week number days
    wn_days_ = GarminDataConverter.getGarminSignedLong(buffer,62);
  }

  public GarminPVTD800(GarminPacket pack)
  {
    // Altitude above WGS84-Ellipsoid [meters]
    alt_ = pack.getNextAsFloat();

    // Estimated position errors and position fix
    epe_ = pack.getNextAsFloat();
    eph_ = pack.getNextAsFloat();
    epv_ = pack.getNextAsFloat();
    fix_ = pack.getNextAsWord();
    
    // Time of week [seconds]
    tow_ = pack.getNextAsDouble();
    
    // Latitude and longitude
    lat_ = pack.getNextAsRadiantDegrees();
    lon_ = pack.getNextAsRadiantDegrees();
    
    // Movement speeds in east, north, up direction
    east_ = pack.getNextAsFloat();
    north_ = pack.getNextAsFloat();
    up_ = pack.getNextAsFloat();
    
    // Height of WGS84-Ellipsoid above MSL [meters]
    msl_height_ = pack.getNextAsFloat();
    
    // Difference between GPS and UTS [seconds]
    leap_seconds_ = pack.getNextAsWord();
    
    // Week number days
    wn_days_ = pack.getNextAsSignedLong();
  }

// //----------------------------------------------------------------------
// /**
//  * Convert data type to {@link GarminPacket}
//  * @return GarminPacket representing content of data type.
//  */
//   public GarminPacket toGarminPacket(int packet_id)
//   {
//     int data_length = 4 + 4 + 4 + 4 + 2 + 8 + 8 + 8 + 4 + 4 + 4 + 4 + 2 + 4;
//     GarminPacket pack = new GarminPacket(packet_id,data_length);
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
//     data = GarminDataConverter.setGarminSignedLong(wn_days_,data,60);
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
    buffer.append("GarminPVT[");
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
