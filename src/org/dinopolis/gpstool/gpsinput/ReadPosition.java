/***********************************************************************
 * @(#)$RCSfile$   $Revision$ $Date$
 *
 * Copyright (c) 2001 IICM, Graz University of Technology
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

import org.dinopolis.gpstool.gpsinput.nmea.GPSNmeaDataProcessor;

public class ReadPosition
{
  public static void main(String[] args)
    throws Exception
  {
    GPSDataProcessor gps_data_processor = new GPSNmeaDataProcessor();
    GPSDevice device = new GPSSerialDevice();
//      Hashtable environment = new HashTable();
//      environment.put(GPSSerialDevice.PORT_NAME_KEY,serial_port_name);
//      environment.put(GPSSerialDevice.PORT_SPEED_KEY,new Integer(serial_port_speed));
//      device.init(environment);
    gps_data_processor.setGPSDevice(device);
    gps_data_processor.open();
    GPSPosition position = gps_data_processor.getGPSPosition();
    double heading = gps_data_processor.getHeading();
    gps_data_processor.close();
  }
  
}

