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

import org.dinopolis.gpstool.gpsinput.GPSRoute;

//----------------------------------------------------------------------
/**
 * This class represents packets in Garmin data format D200.
 *
 * @author Sandra Brueckler, Stefan Feitl
 * @version $Revision$
 */

public class GarminRouteD200 extends GarminRoute  
{
  protected static byte route_id_ = 1;

  public GarminRouteD200(int[] buffer)
  {
    setIdentification(Integer.toString(GarminDataConverter.getGarminByte(buffer,2)));
  }


  public GarminRouteD200(GarminPacket pack)
  {
    setIdentification(Integer.toString(pack.getNextAsByte()));
  }

  public GarminRouteD200(GPSRoute route)
  {
    setIdentification(route.getIdentification());
  }

//----------------------------------------------------------------------
/**
 * Convert data type to {@link org.dinopolis.gpstool.gpsinput.garmin.GarminPacket}
 * @return GarminPacket representing content of data type.
 */
  public GarminPacket toGarminPacket(int packet_id)
  {
    byte id;
    int data_length = 1;
    GarminPacket pack = new GarminPacket(packet_id,data_length);

    // Try to parse route identification and get valid route id
    // If parse fails, a default value is generated to avoid errors
    try
    {
      id = java.lang.Byte.parseByte(getIdentification());
    }
    catch (NumberFormatException nfe)
    {
      id = route_id_++;
    }

    pack.setNextAsByte(id);
    return (pack);
  }
}
