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

//----------------------------------------------------------------------
/**
 * All classes implementing this interface are interpreting data from
 * a GPSDevice (serial gps-receivier, file containing gps data, ...)
 * and provide this information in a uniform way. So an NMEA-processor
 * interprets NMEA sentences, while a Garmin-Processor understands the
 * garmin protocol.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public interface GPSRawDataListener
{

//----------------------------------------------------------------------
/**
 * Informs a GPSRawDataListener about received raw data from a
 * gps device.
 *
 * @param raw_data the raw data received from the gps device.
 */
  public void gpsRawDataReceived(char[] raw_data, int offset, int length);
}


