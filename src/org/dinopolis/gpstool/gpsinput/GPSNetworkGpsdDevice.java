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
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.util.Hashtable;
import java.util.Enumeration;

import java.net.Socket;
import java.net.UnknownHostException;

import org.dinopolis.util.Debug;


//----------------------------------------------------------------------
/**
 * This class connects to a gps-receiver by the serial connection.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GPSNetworkGpsdDevice implements GPSDevice
{
  public final static String GPSD_HOST_KEY = "gps_host";
  public final static String GPSD_PORT_KEY = "gpsd_port";
  public final static String RAW_DATA_LOG_FILENAME_KEY = "data_logfile";
  
  protected final static String DEFAULT_GPSD_HOST = "localhost";
  protected final static int DEFAULT_GPSD_PORT = 2947;

  protected String gpsd_host_;
  protected int gpsd_port_;

  protected Socket gpsd_socket_;

  protected InputStream in_stream_;  
  protected OutputStream out_stream_;  
  
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

    try
    {
      if ((gpsd_host_ = (String)environment.get(GPSD_HOST_KEY)) == null)
        gpsd_host_ = DEFAULT_GPSD_HOST;;

      if(environment.containsKey(GPSD_PORT_KEY))
        gpsd_port_ = ((Integer)environment.get(GPSD_PORT_KEY)).intValue();
      else
        gpsd_port_ = DEFAULT_GPSD_PORT;
    }
    catch(Exception e)
    {
      throw new GPSException("Invalid environment set for network gpsd connection: "+e.getMessage());
    }
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

    try
    {
      if (Debug.DEBUG)
        Debug.println("gpstool_gpsd","try to connect to host "
                      + gpsd_host_ + " port "+gpsd_port_);
      gpsd_socket_ = new Socket(gpsd_host_,gpsd_port_);
      in_stream_ = new BufferedInputStream(gpsd_socket_.getInputStream());
      out_stream_ = new BufferedOutputStream(gpsd_socket_.getOutputStream());

          // start logging mode in gpsd with key 'R':
      OutputStreamWriter out = new OutputStreamWriter(out_stream_);
      out.write("R\n\r");
      out.flush();
          // read answer from gpsd:
      InputStreamReader in = new InputStreamReader(in_stream_);
      int data;
      while((data = in.read()) != 13)
      {
//         System.out.println("GPSNetworkGpsdDrive: "+data);
      }
    }
    catch(UnknownHostException uhe)
    {
      throw new GPSException(uhe.getMessage());
    }
    catch(IOException ioe)
    {
      throw new GPSException(ioe.getMessage());
    }
    catch(SecurityException se)
    {
      throw new GPSException(se.getMessage());
    }
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
      if (in_stream_ != null)
        in_stream_.close();
      if(out_stream_ != null)
        out_stream_.close();
      if (gpsd_socket_ != null)
        gpsd_socket_.close();
    }
    catch(IOException ioe)
    {
      throw new GPSException(ioe.getMessage());
    }
    in_stream_ = null;
    out_stream_ = null;
    gpsd_socket_ = null;
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
    return(in_stream_);
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
    return(out_stream_);
  }
}




