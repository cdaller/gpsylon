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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

//----------------------------------------------------------------------
/**
 * All classes implementing this interface are sources of gps-data,
 * like a gps-receiver, a file containing logging information of
 * gps-receivers, ....
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public interface GPSDevice 
{

//----------------------------------------------------------------------
/**
 * Initialize the GPSDevice and hand over all information needed for
 * the specific GPSDevice to opens the connection.
 *
 * @param environment contains all informations needed to initialize
 * the gps device.  
 * @exception GPSException if the initialization was not successfull,
 * e.g. some information in the environment is missing.
 */
  
  public void init(Hashtable environment)
    throws GPSException;
    
  
//----------------------------------------------------------------------
/**
 * Opens the gps device (e.g. serial connection to gps-receiver or
 * file containing logging information from a gps-receiver). 
 * @exception GPSException if the opening of the device was not successfull.
 */

  public void open()
    throws GPSException;

//----------------------------------------------------------------------
/**
 * Closes the connection to the GPSDevice.
 * @exception GPSException if closing the device was not successfull.
 */

  public void close()
    throws GPSException;
    
//----------------------------------------------------------------------
/**
 * Returns an input stream from the gps device.
 * @return an input stream from the gps device.
 */

  public InputStream getInputStream()
    throws IOException;
  
//----------------------------------------------------------------------
/**
 * Returns an output stream from the gps device.
 * @return an output stream from the gps device.
 */

  public OutputStream getOutputStream()
    throws IOException;
  
}


