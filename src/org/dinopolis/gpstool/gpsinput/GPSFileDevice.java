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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.util.Hashtable;
import java.util.Enumeration;


import org.dinopolis.util.Debug;


//----------------------------------------------------------------------
/**
 * This class 'connects' to a file containing gps-informations.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GPSFileDevice implements GPSDevice
{
  public final static String PATH_NAME_KEY = "path_name";
  
  protected File file_ = null;
  protected FileInputStream inputstream_ = null;
  protected FileOutputStream outputstream_ = null;

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
    throws GPSException
  {
    String file_name;

    if ((file_name = (String)environment.get(PATH_NAME_KEY)) == null)
      throw new GPSException("File name not found in environment!");

    if (Debug.DEBUG)
      Debug.println("gpstool_file","connect to file '" + file_name + "'.");
    file_ = new File(file_name);
  }

//----------------------------------------------------------------------
/**
 * Opens the gps device (e.g. serial connection to gps-receiver or
 * file containing logging information from a gps-receiver). 
 * @exception GPSException if the opening of the device was not successfull.
 */

  public void open()
    throws GPSException
  {
  }
  
//----------------------------------------------------------------------
/**
 * Closes the connection to the GPSDevice.
 * @exception GPSException if closing the device was not successfull.
 */

  public void close()
    throws GPSException
  {
    try
    {
      if (inputstream_ != null)
        inputstream_.close();
      if (outputstream_ != null)
        outputstream_.close();
    }
    catch(IOException e)
    {
      throw new GPSException(e.getMessage());
    }
  }
    
//----------------------------------------------------------------------
/**
 * Returns an input stream from the gps device. If the port is
 * unidirectional and doesn't support receiving data or
 * <code>open</code> was not called before, then getInputStream
 * returns <code>null</code>.
 * @return an input stream from the gps device.
 */

  public InputStream getInputStream()
    throws IOException
  {
    if (file_ == null)
      return(null);
    if (inputstream_ == null)
      inputstream_ = new FileInputStream(file_);
    return(inputstream_);
  }
  
//----------------------------------------------------------------------
/**
 * Returns an output stream from the gps device. If the port is
 * unidirectional and doesn't support receiving data or
 * <code>open</code> was not called before, then getOutputStream
 * returns <code>null</code>.
 * @return an output stream from the gps device.
 */

  public OutputStream getOutputStream()
    throws IOException
  {
    if (file_ == null)
      return(null);
    if (outputstream_ == null)
      outputstream_ = new FileOutputStream(file_);
    return(outputstream_);
  }
}













