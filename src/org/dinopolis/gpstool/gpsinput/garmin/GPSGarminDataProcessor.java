/***********************************************************************
 * @(#)$RCSfile$   $Revision$ $Date$
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


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.imageio.ImageIO;
import org.dinopolis.gpstool.gpsinput.GPSDevice;
import org.dinopolis.gpstool.gpsinput.GPSException;
import org.dinopolis.gpstool.gpsinput.GPSGeneralDataProcessor;
import org.dinopolis.gpstool.gpsinput.GPSPosition;
import org.dinopolis.gpstool.gpsinput.GPSSerialDevice;
import org.dinopolis.util.Debug;

//----------------------------------------------------------------------
/**
 * This class implements a GARMIN-Processor that is able to connect
 * to a connected GARMIN-device, retrieve its capabilities, live
 * position-velocity-time-data and waypoint/route/track-information
 * stored in the device.
 * Waypoint/route/track-information may also be sent to the device
 * using the functions of this class.
 * <P>
 *
 * @author Christof Dallermassl
 * @version $Revision$ */

public class GPSGarminDataProcessor extends GPSGeneralDataProcessor// implements Runnable
{
/**
 * Declaration of required in-/output-streams and a communications thread
 * for firing position-change-events.
 */
  protected InputStream in_stream_ = null;
  protected OutputStream out_stream_ = null;

  protected WatchDogThread watch_dog_;
  protected ReaderThread read_thread_;

      /** lock used to synchronize ACK/NAK of packages from device with
       * reader thread */
  protected Object acknowledge_lock_ = new Object();
      /** helper variable to pass result (ACK/NAK) from reader thread to
       * writer thread */
  protected boolean send_success_ = false;
      /** helper variable to pass result package id from reader thread to
       * writer thread */
  protected int send_package_id_ = 0;

      // lock objects and result objects for synchronous calls:
  protected Object route_sync_request_lock_ = new Object();
  protected List result_routes_;
  protected Object track_sync_request_lock_ = new Object();
  protected List result_tracks_;
  protected Object waypoint_sync_request_lock_ = new Object();
  protected List result_waypoints_;
  protected Object pvt_sync_request_lock_ = new Object();
  protected GarminPVT result_pvt_;
  protected Object product_info_lock_ = new Object();
  protected Object screenshot_sync_request_lock_ = new Object();
  protected BufferedImage result_screenshot_;

      /** Listeners for the Result Packages */
  protected Vector result_listeners_;
      /** Listeners for the Route Packages */
  protected Vector route_listeners_;
      /** Listeners for the Track Packages */
  protected Vector track_listeners_;
      /** Listeners for the Waypoint Packages */
  protected Vector waypoint_listeners_;
  
/**
 * Basic values for garmin devices
 */
  public GarminCapabilities capabilities_;
  public GarminProduct product_info_;

      /** timeout in milliseconds to wait for ACK/NAK from device (0 waits forever) */
  protected final static long ACK_TIMEOUT = 2000L;

  protected final static int MAX_TRIES = 5;
  
      /* constants to indicate which type of packages were received */
  protected final static int RECEIVED_WAYPOINTS = 1;
  protected final static int RECEIVED_TRACKS = 2;
  protected final static int RECEIVED_ROUTES = 4;

      // Definitions for DLE, ETX, ACK and NAK
  public final static int DLE = 16;
  public final static int ETX = 3;
  public final static int ACK = 6;
  public final static int NAK = 21;
  
/**
 * Identifiers for L000 - Basic Link Protocol
 */
  public final static int Pid_Protocol_Array = 253;  // 0xfd
  public final static int Pid_Product_Rqst   = 254;  // 0xfe
  public final static int Pid_Product_Data   = 255;  // 0xff

/**
 * Identifiers for L001 - Link Protocol 1
 */
  public final static int Pid_Command_Data_L001   = 10;  // 0x0a
  public final static int Pid_Xfer_Cmplt_L001     = 12;  // 0x0c
  public final static int Pid_Date_Time_Data_L001 = 14;  // 0x0e
  public final static int Pid_Position_Data_L001  = 17;  // 0x11
  public final static int Pid_Prx_Wpt_Data_L001   = 19;  // 0x13
  public final static int Pid_Satellite_Info      = 26;  // 0x1a // undocumented async
  public final static int Pid_Records_L001        = 27;  // 0x1b
  public final static int Pid_Enable_Async_Events = 28;  // 0x1c // from http://playground.sun.com/pub/soley/garmin.txt
  public final static int Pid_Rte_Hdr_L001        = 29;  // 0x1d
  public final static int Pid_Rte_Wpt_Data_L001   = 30;  // 0x1e
  public final static int Pid_Almanac_Data_L001   = 31;  // 0x1f
  public final static int Pid_Trk_Data_L001       = 34;  // 0x22
  public final static int Pid_Wpt_Data_L001       = 35;  // 0x23
  public final static int Pid_Pvt_Data_L001       = 51;  // 0x33
  public final static int Pid_Display_Data_L001   = 69;  // 0x45
  public final static int Pid_Rte_Link_Data_L001  = 98;  // 0x62
  public final static int Pid_Trk_Hdr_L001        = 99;  // 0x63

/**
 * Identifiers for L002 - Link Protocol 2
 */
  public final static int Pid_Almanac_Data_L002   = 4;   // 0x04
  public final static int Pid_Command_Data_L002   = 11;  // 0x0b
  public final static int Pid_Xfer_Cmplt_L002     = 12;  // 0x0c
  public final static int Pid_Date_Time_Data_L002 = 20;  // 0x14
  public final static int Pid_Position_Data_L002  = 24;  // 0x18
  public final static int Pid_Records_L002        = 35;  // 0x23
  public final static int Pid_Rte_Hdr_L002        = 37;  // 0x25
  public final static int Pid_Rte_Wpt_Data_L002   = 39;  // 0x27
  public final static int Pid_Wpt_Data_L002       = 43;  // 0x2b
  
/**
 * Identifiers for A010 - Device Command Protocol 1
 */
  public final static int Cmnd_Abort_Transfer_A010 = 0;  // 0x00
  public final static int Cmnd_Transfer_Alm_A010   = 1;  // 0x01
  public final static int Cmnd_Transfer_Posn_A010  = 2;  // 0x02
  public final static int Cmnd_Transfer_Prx_A010   = 3;  // 0x03
  public final static int Cmnd_Transfer_Rte_A010   = 4;  // 0x04
  public final static int Cmnd_Transfer_Time_A010  = 5;  // 0x05
  public final static int Cmnd_Transfer_Trk_A010   = 6;  // 0x06
  public final static int Cmnd_Transfer_Wpt_A010   = 7;  // 0x07
  public final static int Cmnd_Turn_Off_Pwr_A010   = 8;  // 0x08
  public final static int Cmnd_Transfer_Screenbitmap_A010 = 32; // 0x20
  public final static int Cmnd_Start_Pvt_Data_A010 = 49;  // 0x31
  public final static int Cmnd_Stop_Pvt_Data_A010  = 50;  // 0x32

/**
 * Identifiers for A011 - Device Command Protocol 2
 */
  public final static int Cmnd_Abort_Transfer_A011 = 0;  //0x00
  public final static int Cmnd_Transfer_Alm_A011   = 4;  // 0x04
  public final static int Cmnd_Transfer_Rte_A011   = 8;  // 0x08
  public final static int Cmnd_Transfer_Time_A011  = 20;  // 0x14
  public final static int Cmnd_Transfer_Wpt_A011   = 21;  // 0x15
  public final static int Cmnd_Turn_Off_Pwr_A011   = 26;  // 0x1a

/**
 * Inofficial Commands
 */
  public final static int Cmnd_Set_Serial_Speed    = 48; // 0x30 // from gpsexplorer
  public final static int Pid_Change_Serial_Speed  = 49; // 0x31 // from gpsexplorer
      /**
       * Other Commands
       */
  
//----------------------------------------------------------------------
/**
 * Default constructor.
 */
  public GPSGarminDataProcessor()
  {
  }
  
  
//--------------------------------------------------------------------------------
// GPSDataProcessor interface
//--------------------------------------------------------------------------------
  
  
//----------------------------------------------------------------------
/**
 * Initialize the GPS-Processor.
 *
 * @param environment Environment the processor should use
 * @exception if an error occured on initializing.
 */
  public void init(Hashtable environment) throws GPSException
  {
    if (gps_device_ == null)
      throw new GPSException("No GPSDevice set!");
  }

//----------------------------------------------------------------------
/**
 * Starts the data processing. The Data Processor connects to the
 * GPSDevice and starts sending/retrieving information. Additionally
 * basic product capabilities of the connected device are determined.
 *
 * @exception if an error occured on connecting.
 */
  public void open() throws GPSException
  {
    if (gps_device_ == null)
      throw new GPSException("no GPSDevice set!");

    try
    {
      gps_device_.open();
      in_stream_ = gps_device_.getInputStream();
      out_stream_ = gps_device_.getOutputStream();

//       comm_thread_ = new Thread(this,"GPSGarminDataProcessorIn");
//       comm_thread_.setDaemon(true); // so thread is finished after exit of application
//       comm_thread_.start();

      watch_dog_ = new WatchDogThread();
      watch_dog_.setDaemon(true);
	
      read_thread_ = new ReaderThread();
      read_thread_.setName("Garmin Reader");
      read_thread_.setDaemon(true);
      read_thread_.start();

      GarminProduct info = getGarminProductInfo(5000L); // needed to know the capabilities of the device
      if(info == null)
        throw new GPSException("Garmin device does not respond!");
	
//       write_thread_ = new WriterThread(out_stream_);
//       write_thread_.setName("Garmin Writer");
//       write_thread_.setDaemon(true);
//       write_thread_.start();

          // read product data      readProductData();
    }
    catch(IOException e)
    {
      throw new GPSException(e);
    }
    
  }
  
//----------------------------------------------------------------------
/**
 * Stopps the data processing. The Data Processor disconnects from the
 * GPSDevice.
 *
 * @exception if an error occured on disconnecting.
 */
  public void close() throws GPSException
  {
    if (gps_device_ == null)
      throw new GPSException("no GPSDevice set!");
    if(read_thread_ != null)
      read_thread_.stopThread();
    if(watch_dog_ != null)
      watch_dog_.stopWatching();
    gps_device_.close();
  }


//----------------------------------------------------------------------
/**
 * Returns information about the gps connected (name of device, type
 * of connection, etc.) This information is for display to the user,
 * not for further processing (may change without notice).
 *
 * @return information about the gps connected.
 */
  public String[] getGPSInfo()
  {
    String name = product_info_.getProductName()
                  +" ("+product_info_.getProductId()
                  +") V"+product_info_.getProductSoftware();
    Vector capabilities = capabilities_.getProductCapabilities();
    StringBuffer capabilities_string = new StringBuffer();
    for(int index=0; index < capabilities.size()-1; index++)
    {
      capabilities_string.append(capabilities.get(index)).append(", ");
    }
        // add last:
    capabilities_string.append(capabilities.get(capabilities.size()-1));
    String[] info = new String[] {name, capabilities_string.toString()};
    return(info);
  }

  
//----------------------------------------------------------------------
/**
 * Requests the gps device to send the current
 * position/heading/etc. periodically. This implemenation ignores the
 * period and returns 1000 always as this seems to be the value set
 * for garmin devices.
 *
 * @param period time in milliseconds between periodically sending
 * position/heading/etc. This value may be changed by the gps device,
 * so do not rely on the value given!
 * @return the period chosen by the gps device or 0 if the gps device
 * is unable to send periodically. Do not rely on this value as some
 * drivers just do not know!
 * @throws GPSException if the operation threw an exception
 * (e.g. communication problem).
 */
  public long startSendPositionPeriodically(long period)
    throws GPSException
  {
    try
    {
      requestStartPvtData();
    }
    catch(IOException ioe)
    {
      throw new GPSException(ioe);
    }
    return(1000);
  }
  

//----------------------------------------------------------------------
/**
 * Requests the gps device to stop to send the current
 * position/heading/etc. periodically. Do not rely on this, as some
 * gps devices may not stop it (e.g. NMEA).
 * @throws GPSException if the operation threw an exception
 * (e.g. communication problem).
 */
  public void stopSendPositionPeriodically()
    throws GPSException
  {
    try
    {
      requestStopPvtData();
    }
    catch(IOException ioe)
    {
      throw new GPSException(ioe);
    }
  }

//--------------------------------------------------------------------------------
/**
 * Get a list of waypoints from the gps device. This call blocks until
 * something is received!
 * @return a list of <code>GPSWaypoint</code> objects.
 *
 * @throws UnsupportedOperationException if the operation is not
 * supported by the gps device or by the protocol used.
 * @throws GPSException if the operation threw an exception
 * (e.g. communication problem).
 * @see GPSWaypoint
 */
  public List getWaypoints()
    throws UnsupportedOperationException, GPSException
  {
    try
    {
      return(getWaypoints(0L));
    }
    catch(IOException ioe)
    {
      throw new GPSException(ioe);
    }
  }

//--------------------------------------------------------------------------------
/**
 * Get a list of routes from the gps device. This call blocks until
 * something is received!
 * @return a list of <code>GPSRoute</code> objects.
 *
 * @throws UnsupportedOperationException if the operation is not
 * supported by the gps device or by the protocol used.
 * @throws GPSException if the operation threw an exception
 * (e.g. communication problem).
 * @see GPSRoute
 */
  public List getRoutes()
    throws UnsupportedOperationException, GPSException
  {
    try
    {
//      System.out.println("GPSGarminDataProcessor.getRoutes");
      return(getRoutes(0L));
    }
    catch(IOException ioe)
    {
      throw new GPSException(ioe);
    }
  }

//--------------------------------------------------------------------------------
/**
 * Get a list of tracks from the gps device. This call blocks until
 * something is received!
 * @return a list of <code>GPSRoute</code> objects.
 *
 * @throws UnsupportedOperationException if the operation is not
 * supported by the gps device or by the protocol used.
 * @throws GPSException if the operation threw an exception
 * (e.g. communication problem).
 * @see GPSTrack
 */
  public List getTracks()
    throws UnsupportedOperationException, GPSException
  {
    try
    {
      return(getTracks(0L));
    }
    catch(IOException ioe)
    {
      throw new GPSException(ioe);
    }
  }

//--------------------------------------------------------------------------------
/**
 * Get a screenshot of the gpsdevice. This call blocks until
 * something is received!
 * @return an image of the screenshot
 *
 * @throws UnsupportedOperationException if the operation is not
 * supported by the gps device or by the protocol used.
 * @throws GPSException if the operation threw an exception
 * (e.g. communication problem).
 */
  public BufferedImage getScreenShot()
    throws UnsupportedOperationException, GPSException
  {
    try
    {
      return(getScreenShot(0L));
    }
    catch(IOException ioe)
    {
      throw new GPSException(ioe);
    }
  }


//----------------------------------------------------------------------
// Other methods
//----------------------------------------------------------------------

  
// //----------------------------------------------------------------------
// /**
//  * Contignous reading of position-time-velocity-data from the connected
//  * device using the communicaions thread.
//  */
//   public void run()
//     {
//       GarminPVT pvt;
//       while(continue_pvt_thread_)
//       {
// 	try
// 	{
// 	  requestPVT();
// 	}
// 	catch(IOException ioe)
// 	{
// 	  ioe.printStackTrace();
// 	  return;
// 	}
// 	try
// 	{
// 	  Thread.sleep(pvt_thread_sleep_time_);
// 	}
// 	catch(InterruptedException ignored) {}
//       }
//     }


//----------------------------------------------------------------------
/**
 * Returns the heading
 * 
 * @param speed_north speed in direction north (m/s)
 * @param speed_east speed in direction east (m/s)
 * @return the heading [0,360] degrees
 */
  protected static float calcHeading(float speed_north, float speed_east)
  {
    double heading = Math.toDegrees(Math.atan2(speed_north,speed_east));
        // conversion from mathematical model to geographical (0 is North, 90 is East)
    heading = 90.0 - heading;
    if(heading < 0)
      heading = 360.0 + heading;
    return((float)heading);
  }

//----------------------------------------------------------------------
/**
 * Returns the current speed in km/h.
 *
 * @param speed_north speed in direction north (m/s)
 * @param speed_east speed in direction east (m/s)
 */
  protected static float calcSpeed(float speed_north, float speed_east)
  {
    return((float)(Math.sqrt(speed_north*speed_north + speed_east*speed_east)*3.6));
  }


//----------------------------------------------------------------------
/**
 * Send request/command to GARMIN-Device.
 *
 * @param rqst Request to be sent to the device.
 * @param cmd Command to be sent to the device.
 * @param timeout milliseconds to wait at maximum until the package
 * must be acknowledged by the device.
 */
  protected boolean sendCommand(int request, int cmd, long timeout)
    throws IOException
  {
    if(Debug.DEBUG)
      Debug.println("gps_garmin_package","Sending request "+request+"/"+cmd);
    GarminPackage garmin_package = new GarminPackage(request,2);
    garmin_package.put(cmd);
    garmin_package.put(0);
    putPackage(garmin_package,timeout);
    return(send_success_);
  }

//----------------------------------------------------------------------
/**
 * Send request to GARMIN-Device.
 *
 * @param rqst Request to be sent to the device.
 * @param timeout milliseconds to wait at maximum until the package
 * must be acknowledged by the device.
 */
  protected boolean sendCommand(int request, long timeout)
    throws IOException
  {
    if(Debug.DEBUG)
      Debug.println("gps_garmin_package","Sending request "+request);
    GarminPackage garmin_package = new GarminPackage(request,0);
    putPackage(garmin_package,timeout);
    return(send_success_);
  }

//----------------------------------------------------------------------
/**
 * Send request/command to GARMIN-Device. The command is sent to the
 * device, no answer is read.
 *
 * @param rqst Request to be sent to the device.
 * @param cmd Command to be sent to the device.
 */
  protected void sendCommandAsync(int request, int cmd)
    throws IOException
  {
    if(Debug.DEBUG)
      Debug.println("gps_garmin_package","Sending request async "+request+"/"+cmd);
    GarminPackage garmin_package = new GarminPackage(request,2);
    garmin_package.put(cmd);
    garmin_package.put(0);
    putPackageAsync(garmin_package);
  }

//----------------------------------------------------------------------
/**
 * Send request/command to GARMIN-Device. The command is sent to the
 * device, no answer is read.
 *
 * @param rqst Request to be sent to the device.
 */
  protected void sendCommandAsync(int request)
    throws IOException
  {
    if(Debug.DEBUG)
      Debug.println("gps_garmin_package","Sending request async "+request);
    GarminPackage garmin_package = new GarminPackage(request,0);
    putPackageAsync(garmin_package);
  }

//----------------------------------------------------------------------
/**
 * Writes a package to the garmin device and returns
 * immediately. Better use {@link #putPackage(GarminPackage)}.
 * @param garmin_package the package to send.
 */
  protected void putPackageAsync(GarminPackage garmin_package)
  {
    try
    {
      if(Debug.DEBUG)
        Debug.println("gps_garmin_package","Sending package async "+garmin_package.getPackageId());
          // package header
      out_stream_.write(DLE);
      out_stream_.write(garmin_package.getPackageId());
      int package_size = garmin_package.getPackageSize();
      if(package_size == DLE)
        out_stream_.write(DLE);
      out_stream_.write(package_size);

          // package data
      int data;
      for(int index=0; index < package_size; index++)
      {
        data = garmin_package.get();
        if(data == DLE)
          out_stream_.write(DLE);
        out_stream_.write(data);
      }

          // checksum and end markers
      byte checksum = garmin_package.calcChecksum();
      if(checksum == DLE)
        out_stream_.write(DLE);
      out_stream_.write(checksum);

      out_stream_.write(DLE);
      out_stream_.write(ETX);
      out_stream_.flush();
      
          // inform listeners:
      String buffer_string = "sent: "+garmin_package.toString();
      fireRawDataReceived(buffer_string.toCharArray(),0,buffer_string.length());
    }
    catch(IOException ioe)
    {
      ioe.printStackTrace();
    }
  }

//----------------------------------------------------------------------
/**
 * Writes a package to the garmin device and waits for the result (ACK
 * or NAK). If sending was not successfull, resend package.
 * @param garmin_package the package to send.
 */
  protected void putPackage(GarminPackage garmin_package)
  {
    putPackage(garmin_package,0L);
  }

//----------------------------------------------------------------------
/**
 * Writes a package to the garmin device and waits for the result (ACK
 * or NAK). If sending was not successfull, resend package.
 * @param garmin_package the package to send.
 * @param timeout time in milliseconds to wait maximum (0 = forever).
 */
  protected void putPackage(GarminPackage garmin_package, long timeout)
  {
    do
    {
      synchronized(acknowledge_lock_)
      {
        send_success_ = false;
        send_package_id_ = 0;
//        System.err.println("Sending package in putPackage()");
        putPackageAsync(garmin_package);
        try
        {
//          System.err.println("waiting for ACK");
          acknowledge_lock_.wait(timeout);
        }
        catch(InterruptedException ignore){}
//        System.err.println("after waiting for ACK");
      }
    }
    while(send_success_ && (send_package_id_ == garmin_package.getPackageId()));
  }
  
//----------------------------------------------------------------------
/**
 * Read a package transmitted by the GARMIN-Device.
 * This method is partly taken from the garble project (thanks to ???)
 * @return a GarminPackage or null, if an error occured.
 */
  protected GarminPackage getPackage()
  {
    try
    {
      int bytes_scanned = 0;
      int package_id;

      while(true)
      {
        package_id = in_stream_.read();
        watch_dog_.startWatching();   // start watchdog after first byte arrived
        watch_dog_.reset();
        bytes_scanned++;
        if(package_id == DLE)
        {
              // could be it, but could be dle in chunk of leftover packet...
          package_id = in_stream_.read();
          watch_dog_.reset();
          bytes_scanned++;
              // valid packet start dle is never followed by dle or etx
          if (package_id == DLE)
          {
                // dle stuffing in packet data, ignore...
            continue;
          }
          if (package_id == ETX)
          {
                // end of old packet frame, ignore...
            continue;
          } 
          break;
        }
      }
      
      watch_dog_.setPackageId(package_id);
      
          // now, the package starts:
      int data = in_stream_.read();
      watch_dog_.reset();
      if (data == DLE)
      {
        data = in_stream_.read();
        watch_dog_.reset();
        if (data != DLE)
        {
          if (Debug.DEBUG) 
            Debug.println("gps_garmin_package","missing DLE stuffing in packet size");
          sendCommandAsync(NAK,package_id);
          watch_dog_.stopWatching();
          return(null);
        }
      }
      int package_size = (data & 0xff);
      GarminPackage garmin_package = new GarminPackage(package_id, package_size);
      if (Debug.DEBUG) 
        Debug.println("gps_garmin_package","receiving package id: "
                      +package_id+" size: "+package_size);
      
//      System.out.println("Reading data: ");
      for (int data_index = 0; data_index < package_size; data_index++)
      {
	
        data = in_stream_.read();
        watch_dog_.reset();
//	System.out.print(data_index+":"+data+ " ");
        garmin_package.put(data);
        if (data == DLE)
        {
              // check for and ignore correct DLE stuffing byte
          data = in_stream_.read();
          watch_dog_.reset();
          if (data != DLE)
          {
            if (Debug.DEBUG) 
              Debug.println("gps_garmin_package","missing DLE stuffing in packet data");
            sendCommandAsync(NAK,package_id);
            watch_dog_.stopWatching();
            return(null);
          }
        }
      }
//      System.out.println("\ndata read.");
      
      byte package_checksum = (byte)in_stream_.read();
      watch_dog_.reset();
      if (package_checksum == DLE)
      {
        package_checksum = (byte)in_stream_.read();
        watch_dog_.reset();
        if (package_checksum != DLE)
        {
          if (Debug.DEBUG)
            Debug.println("gps_garmin_package","missing DLE stuffing in packet checksum");
          sendCommandAsync(NAK,package_id);
          watch_dog_.stopWatching();
          return(null);
        }
      }
//      System.out.println("checksum : "+package_checksum);

      int calc_checksum = garmin_package.calcChecksum();
      if (calc_checksum != package_checksum)
      {
        if (Debug.DEBUG)
          Debug.println("gps_garmin_package","bad checksum (is "+calc_checksum
                        +" should be "+package_checksum);
        sendCommandAsync(NAK,package_id);
        watch_dog_.stopWatching();
        return(null);
      }
	
      int dle, etx;
      dle = in_stream_.read();
      etx = in_stream_.read();
      watch_dog_.reset();
      if (dle != DLE || etx != ETX)
      {
        if (Debug.DEBUG)
        {
          if(Debug.isEnabled("gps_garmin_package"))
          {
            Debug.println("gps_garmin_package","bad packet framing");
            Debug.println("gps_garmin_package","id is " + package_id);
            Debug.println("gps_garmin_package","size is " + package_size);
            Debug.println("gps_garmin_package","data is: ");
            for (int i = 0; i < package_size; i++) {
              Debug.print("gps_garmin_package",garmin_package.get() + " ");
            }
            Debug.println("gps_garmin_package","\nchecksum is " + package_checksum);
            Debug.println("gps_garmin_package","DLE byte is " + dle);
            Debug.println("gps_garmin_package","ETX byte is " + etx);
          }
        }
        sendCommandAsync(NAK,package_id);
        watch_dog_.stopWatching();
        return(null);
      }
	
          // if we got this far, we got the packet ok, so send ACK
          // (not for ACK/NAK packages)
      if((package_id != ACK) && (package_id != NAK))
        sendCommandAsync(ACK,package_id);
      watch_dog_.stopWatching();

          // inform raw data listeners:
      String buffer_string = "received: "+garmin_package.toString();
      fireRawDataReceived(buffer_string.toCharArray(),0,buffer_string.length());
      
      return (garmin_package); 
    }
    catch(IOException ioe)
    {
      ioe.printStackTrace();
    }
    return(null);
  }    


//----------------------------------------------------------------------
/**
 * Sleep until capabilities are available.
 */
  protected void waitTillReady()
  {
    while(capabilities_ == null)
    {
      try
      {
        Thread.sleep(100);
      }
      catch(InterruptedException ignore) {}
    }
  }


//----------------------------------------------------------------------
/**
 * Returns information about the garmin product or null, if the
 * timeout was exceeded.
 *
 * @return information about the garmin product.
 */
  protected GarminProduct getGarminProductInfo(long timeout)
    throws IOException
  {
    if(product_info_ != null)
      return(product_info_);

    synchronized(product_info_lock_)
    {
      requestProductInfo();
      try
      {
        product_info_lock_.wait(timeout);
      }
      catch(InterruptedException ignore){}
    }
    return(product_info_);
  }

//----------------------------------------------------------------------
/**
 * Returns the capablitilites of the garmin product.
 *
 * @return the capablitilites of the garmin product.
 */
  protected GarminCapabilities getGarminCapabilities(long timeout)
    throws IOException
  {
    if(product_info_ == null)
      getGarminProductInfo(timeout);
    return(capabilities_);
  }

  
//----------------------------------------------------------------------
/**
 * Returns all the current PVT (position, velocity, etc.) from the gps
 * device. This method blocks until the routes are read or the timeout
 * (in milliseconds) is reached. If the device has no fix (not enough
 * satellites are visible, this method blocks forever, so be careful
 * to use a timeout of zero!)
 *
 * @param timeout in milliseconds or 0 to wait forever.
 * @return a list of route objects or null, if the timeout was reached. 
 */
  public GarminPVT getPVT(long timeout)
    throws IOException
  {
    synchronized(pvt_sync_request_lock_)
    {
      result_pvt_ = null;
      requestPVT();
      try
      {
        pvt_sync_request_lock_.wait(timeout);
      }
      catch(InterruptedException ignore){}
    }
    return(result_pvt_);
  }

  
//----------------------------------------------------------------------
/**
 * Returns all available routes from the gps device. This method
 * blocks until the routes are read or the timeout (in milliseconds)
 * is reached.
 *
 * @param timeout in milliseconds or 0 to wait forever.
 * @return a list of route objects or null, if the timeout was reached. 
 */
  public List getRoutes(long timeout)
    throws IOException
  {
    synchronized(route_sync_request_lock_)
    {
      result_routes_ = null;
      requestRoutes();
      try
      {
        route_sync_request_lock_.wait(timeout);
      }
      catch(InterruptedException ignore){}
    }
    return(result_routes_);
  }

//----------------------------------------------------------------------
/**
 * Returns all available tracks from the gps device. This method
 * blocks until the tracks are read or the timeout (in milliseconds)
 * is reached.
 *
 * @param timeout in milliseconds or 0 to wait forever.
 * @return a list of track objects or null, if the timeout was reached. 
 */
  public List getTracks(long timeout)
    throws IOException
  {
    synchronized(track_sync_request_lock_)
    {
      result_tracks_ = null;
      requestTracks();
      try
      {
        track_sync_request_lock_.wait(timeout);
      }
      catch(InterruptedException ignore){}
    }
    return(result_tracks_);
  }

//----------------------------------------------------------------------
/**
 * Returns a screenshot of the gps device. This method
 * blocks until the tracks are read or the timeout (in milliseconds)
 * is reached.
 *
 * @param timeout in milliseconds or 0 to wait forever.
 * @return the screenshot or null if the timeout was reached.
 */
  public BufferedImage getScreenShot(long timeout)
    throws IOException
  {
    synchronized(screenshot_sync_request_lock_)
    {
      result_screenshot_ = null;
      requestScreenShot();
      try
      {
        screenshot_sync_request_lock_.wait(timeout);
      }
      catch(InterruptedException ignore){}
    }
    return(result_screenshot_);
  }

//----------------------------------------------------------------------
/**
 * Returns all available waypoints from the gps device. This method
 * blocks until the waipoints are read or the timeout (in milliseconds)
 * is reached.
 *
 * @param timeout in milliseconds or 0 to wait forever.
 * @return a list of waypoint objects or null, if the timeout was reached. 
 */
  public List getWaypoints(long timeout)
    throws IOException
  {
    synchronized(waypoint_sync_request_lock_)
    {
      result_waypoints_ = null;
      requestWaypoints();
      try
      {
        waypoint_sync_request_lock_.wait(timeout);
      }
      catch(InterruptedException ignore){}
    }
    return(result_waypoints_);
  }

//----------------------------------------------------------------------
/**
 * Switches off the gps device. This method is non blocking and returns
 * immediately.
 */
  public void requestPowerOff()
    throws IOException
  {
    waitTillReady();
        // Turn off power using link protocol L001 and command protocol A010
    if (capabilities_.hasCapability("L1") &&
        capabilities_.hasCapability("A10"))
    {
      sendCommandAsync(Pid_Command_Data_L001, Cmnd_Turn_Off_Pwr_A010);
    }

        // Turn off power using link protocol L002 and command protocol A011
    if (capabilities_.hasCapability("L2") &&
        capabilities_.hasCapability("A11"))
    {
      sendCommandAsync(Pid_Command_Data_L002, Cmnd_Turn_Off_Pwr_A011);
    }

  }

//----------------------------------------------------------------------
/**
 * Method to request screenshot from device.
 */
  public void requestScreenShot()
    throws IOException
  {
    waitTillReady();
    sendCommandAsync(Pid_Command_Data_L001, Cmnd_Transfer_Screenbitmap_A010);
  }

//----------------------------------------------------------------------
/**
 * Request to send async packages from the gps device. As the packages
 * sent are not known at this time, this method should not be used!!! 
 */
  protected void requestAsyncEvents()
  {
        // experimental code (from http://playground.sun.com/pub/soley/garmin.txt):
        // more information about these packages can be found at:
        // http://artico.lma.fi.upm.es/numerico/miembros/antonio/async/report.txt
    waitTillReady();
    GarminPackage garmin_package = new GarminPackage(Pid_Enable_Async_Events,2);
        // 00 00 = 0x0= disable all (no bits set)
        // 01 00 = 0x1= enables RecordType=00,01,02 // etrex summit: nothing sent
        // 02 00 = 0x2= enables RecordType=0d // etrex summit: nothing sent
        // 04 00 = 0x4= enables RecordType=14,27,28 // etrex summit: packages id 39/0x27 (2 bytes) are sent
        // 08 00 = 0x8= enables RecordType=16 // etrex summit: nothing sent
        // 10 00 = 0x10= enables RecordType=17 // etrex summit: nothing sent
        // 20 00 = 0x20= enables RecordType=07,12,19 // etrex summit: packages id 55/0x37(36bytes),56/0x38(40bytes) are sent
        // 40 00 = 0x40= enables RecordType=07,12 // etrex summit: packages 55,56 are sent
        // 80 00 = 0x80= enables RecordType=1a // etrex summit: package 26/0x1a (96bytes) every second
        // 00 01 = 0x100= enables RecordType=29,2a // etrex summit: nothing sent
        // 00 02 = 0x200= enables RecordType=?? // etrex summit: packages 102/0x66 (20bytes), 104/0x68(20 bytes) sent
        // 00 04 = 0x400= enables RecordType=?? // etrex summit: nothing sent
        // 00 08 = 0x800= enables RecordType=?? // etrex summit: nothing sent
        // 00 10 = 0x1000= enables RecordType=?? // etrex summit: nothing sent
        // 00 20 = 0x20100= enables RecordType=?? // etrex summit: nothing sent
        // 00 40 = 0x20100= enables RecordType=?? // etrex summit: nothing sent
        // 00 80 = 0x20100= enables RecordType=?? // etrex summit: nothing sent
        // ff ff = 0xffff= enables all (all bits set)
    garmin_package.put(0x80);
    garmin_package.put(0);
    putPackageAsync(garmin_package);
  }

//----------------------------------------------------------------------
/**
 * Requests to send a PVT package every second.
 */
  protected void requestStartPvtData()
    throws IOException
  {
    waitTillReady();
        // Turn off power using link protocol L001 and command protocol A010
    if (capabilities_.hasCapability("L1") &&
        capabilities_.hasCapability("A10"))
    {
      sendCommandAsync(Pid_Command_Data_L001, Cmnd_Start_Pvt_Data_A010);
    }
        // not supported in L2/A11
  }

//----------------------------------------------------------------------
/**
 * Requests to stop to send a PVT package every second.
 */
  protected void requestStopPvtData()
    throws IOException
  {
    waitTillReady();
        // Turn off power using link protocol L001 and command protocol A010
    if (capabilities_.hasCapability("L1") &&
        capabilities_.hasCapability("A10"))
    {
      sendCommandAsync(Pid_Command_Data_L001, Cmnd_Stop_Pvt_Data_A010);
    }
        // not supported in L2/A11
  }

//----------------------------------------------------------------------
/**
 * Request the routes from the gps device. This method is non blocking
 * and returns immediately after the acknowledge was received or the
 * number of tries was exceeded.
 * @return true if the acknowledge was sent, false otherwise (the
 * device did not receive the package then).
 */
  protected boolean requestRoutes()
    throws IOException
  {
    waitTillReady();
      
    boolean success = false;
    int num_tries = 0;
    while(!success && (num_tries < MAX_TRIES))
    {
          // Does device support route transfer protocol
      if(capabilities_.hasCapability("A200") || capabilities_.hasCapability("A201"))
      {
        if(capabilities_.hasCapability("L1"))
        {
          if(capabilities_.hasCapability("A10"))
          {
            success = sendCommand(Pid_Command_Data_L001, Cmnd_Transfer_Rte_A010,ACK_TIMEOUT);
          }
          else // A011
          {
            success = sendCommand(Pid_Command_Data_L001, Cmnd_Transfer_Rte_A011,ACK_TIMEOUT);
          }
        }
        else // L002
        {
          if(capabilities_.hasCapability("A10"))
          {
            success = sendCommand(Pid_Command_Data_L002, Cmnd_Transfer_Rte_A010,ACK_TIMEOUT);
          }
          else // A011
          {
            success = sendCommand(Pid_Command_Data_L002, Cmnd_Transfer_Rte_A011,ACK_TIMEOUT);
          }
        }
      }
      num_tries++;
    }
    return(success);
  }

//----------------------------------------------------------------------
/**
 * Request the waypoints from the gps device. This method is non blocking
 * and returns immediately after the acknowledge was received or the
 * number of tries was exceeded.
 * @return true if the acknowledge was sent, false otherwise (the
 * device did not receive the package then).
 */
  protected boolean requestWaypoints()
    throws IOException
  {
    waitTillReady();
      
    boolean success = false;
    int num_tries = 0;
    while(!success && (num_tries < MAX_TRIES))
    {
          // Does device support route transfer protocol
      if(capabilities_.hasCapability("A100"))
      {
        if(capabilities_.hasCapability("L1"))
        {
          if(capabilities_.hasCapability("A10"))
          {
            success = sendCommand(Pid_Command_Data_L001, Cmnd_Transfer_Wpt_A010,ACK_TIMEOUT);
          }
          else // A011
          {
            success = sendCommand(Pid_Command_Data_L001, Cmnd_Transfer_Wpt_A011,ACK_TIMEOUT);
          }
        }
        else // L002
        {
          if(capabilities_.hasCapability("A10"))
          {
            success = sendCommand(Pid_Command_Data_L002, Cmnd_Transfer_Wpt_A010,ACK_TIMEOUT);
          }
          else // A011
          {
            success = sendCommand(Pid_Command_Data_L002, Cmnd_Transfer_Wpt_A011,ACK_TIMEOUT);
          }
        }
      }
      num_tries++;
    }
    return(success);
  }

//----------------------------------------------------------------------
/**
 * Request the tracks from the gps device. This method is non blocking
 * and returns immediately after the acknowledge was received or the
 * number of tries was exceeded.
 * @return true if the acknowledge was sent, false otherwise (the
 * device did not receive the package then).
 */
  protected boolean requestTracks()
    throws IOException
  {
    waitTillReady();
      
    boolean success = false;
    int num_tries = 0;
    while(!success && (num_tries < MAX_TRIES))
    {
          // Does device support route transfer protocol
      if(capabilities_.hasCapability("A300") || capabilities_.hasCapability("A301"))
      {
        if(capabilities_.hasCapability("L1"))
        {
          if(capabilities_.hasCapability("A10"))
          {
            success = sendCommand(Pid_Command_Data_L001, Cmnd_Transfer_Trk_A010,ACK_TIMEOUT);
          }
        }
      }
      num_tries++;
    }
    return(success);
  }

//----------------------------------------------------------------------
/**
 * Request the product info from the gps device. This method is non blocking
 * and returns immediately after the acknowledge was received or the
 * number of tries was exceeded.
 * @return true if the acknowledge was sent, false otherwise (the
 * device did not receive the package then).
 */
  protected boolean requestProductInfo()
    throws IOException
  {
    boolean success = false;
    int num_tries = 0;
    while(!success && (num_tries < MAX_TRIES))
    {
      success = sendCommand(Pid_Product_Rqst,ACK_TIMEOUT);
      num_tries++;
    }
    return(success);
  }

//----------------------------------------------------------------------
/**
 * Request the pvt info (position, velocity, ...)  from the gps
 * device. This method is non blocking
 * and returns immediately after the acknowledge was received or the
 * number of tries was exceeded.
 * @return true if the acknowledge was sent, false otherwise (the
 * device did not receive the package then).
 */
  protected boolean requestPVT()
    throws IOException
  {
    waitTillReady();

    if(capabilities_.hasCapability("A800") &&
       capabilities_.hasCapability("L1") &&
       capabilities_.hasCapability("A10"))
    {
      boolean success = false;
      int num_tries = 0;
      while(!success && (num_tries < MAX_TRIES))
      {
        success = sendCommand(Pid_Command_Data_L001, Cmnd_Start_Pvt_Data_A010,ACK_TIMEOUT);
        num_tries++;
      }
      return(success);
    }
    return(false);
  }

//----------------------------------------------------------------------
/**
 * Method called out of the thread that reads the information from the
 * gps device when route packages were sent.
 */
  protected void fireRoutesReceived(Vector routes)
  {
        // if a snychronous call was made, notify the thread for the results
    synchronized(route_sync_request_lock_)
    {
      result_routes_ = routes;
      route_sync_request_lock_.notify();
    }
    if(Debug.DEBUG)
      Debug.println("gps_garmin","Routes received: "+routes);
  }
  
//----------------------------------------------------------------------
/**
 * Method called out of the thread that reads the information from the
 * gps device when track packages were sent.
 */
  protected void fireTracksReceived(Vector tracks)
  {
        // if a snychronous call was made, notify the thread for the results
    synchronized(track_sync_request_lock_)
    {
      result_tracks_ = tracks;
      track_sync_request_lock_.notify();
    }
    if(Debug.DEBUG)
      Debug.println("gps_garmin","Tracks received: "+tracks);
  }

//----------------------------------------------------------------------
/**
 * Method called out of the thread that reads the information from the
 * gps device when waypoint packages were sent.
 */
  protected void fireWaypointsReceived(List waypoints)
  {
        // if a snychronous call was made, notify the thread for the results
    synchronized(waypoint_sync_request_lock_)
    {
      result_waypoints_ = waypoints;
      waypoint_sync_request_lock_.notify();
    }
    if(Debug.DEBUG)
      Debug.println("gps_garmin","Waypoints received: "+waypoints);
  }

//----------------------------------------------------------------------
/**
 * Method called out of the thread that reads the information from the
 * gps device when transfer complete packages were sent.
 */
  protected void fireTransferCompleteReceived()
  {
    if(Debug.DEBUG)
      Debug.println("gps_garmin","TransferComplete received");
  }
  
//----------------------------------------------------------------------
/**
 * Method called out of the thread that reads the information from the
 * gps device when product data package was sent.
 */
  protected void fireProductDataReceived(GarminProduct product)
  {
    product_info_ = product;
    if(Debug.DEBUG)
      Debug.println("gps_garmin","product data received: "+product);
  }

//----------------------------------------------------------------------
/**
 * Method called out of the thread that reads the information from the
 * gps device when protocol array (capabilities) package was sent.
 */
  protected void fireProtocolArrayReceived(GarminCapabilities capabilities)
  {
    synchronized(product_info_lock_)
    {
      capabilities_ = capabilities;
      product_info_lock_.notify();
    }
      if(Debug.DEBUG)
        Debug.println("gps_garmin","product capabilities received: "+capabilities_);
  }

//----------------------------------------------------------------------
/**
 * Method called out of the thread that reads the information from the
 * gps device when PVT (position, velocity, ...) package was sent.
 */
  protected void firePVTDataReceived(GarminPVT pvt)
  {
    synchronized(pvt_sync_request_lock_)
    {
      if((pvt != null) && (pvt.getFix() > 1))
      {
        changeGPSData(LOCATION,new GPSPosition(pvt.getLat(),pvt.getLon()));
        changeGPSData(SPEED,new Float(calcSpeed(pvt.getNorth(),pvt.getEast())));
        double altitude = pvt.getAlt() + pvt.getMslHeight();
        changeGPSData(ALTITUDE,new Float(altitude));
        changeGPSData(HEADING,new Float(calcHeading(pvt.getNorth(),pvt.getEast())));
      }
      pvt_sync_request_lock_.notify();
    }
    if(Debug.DEBUG)
      Debug.println("gps_garmin","pvt received: "+pvt);
  }

  
//----------------------------------------------------------------------
/**
 * Method called out of the thread that reads the information from the
 * gps device when display data was sent.
 */
  protected void fireDisplayDataReceived(GarminDisplayData display_data)
  {
    if(Debug.DEBUG)
      Debug.println("gps_garmin","display data received: "+display_data);

    synchronized(screenshot_sync_request_lock_)
    {
      result_screenshot_ = display_data.getImage();
      screenshot_sync_request_lock_.notify();
    }
  }

  

//----------------------------------------------------------------------
/**
 * Method called out of the thread that reads the information from the
 * gps device when a acknowledge (ACK or NAK) was sent.
 */
  protected void fireResult(boolean result, int package_id)
  {
    synchronized(acknowledge_lock_)
    {
      send_success_ = result;
      send_package_id_ = package_id;
      acknowledge_lock_.notify();
    }
        // inform listeners
    if(result_listeners_ != null)
    {
      Iterator listeners = result_listeners_.iterator();
      ResultReceivedListener listener;
      while(listeners.hasNext())
      {
        listener = (ResultReceivedListener)listeners.next();
        listener.receivedResult(result,package_id);
      }
    }
    if(Debug.DEBUG)
      Debug.println("gps_garmin","Result received: "+result);
  }
  
//----------------------------------------------------------------------
/**
 * Method called out of the thread that reads the information from the
 * gps device when data package (any package) was sent. This method
 * detects the type of the package, creates the objects for routes,
 * tracks, waypoints, etc. and informs the fireXXX methods about
 * it. It therefore is the main parser method for received garmin
 * packages.
 */
  protected void firePackageReceived(GarminPackage garmin_package)
  {
    GarminPackage next_garmin_package;
    int packages_type_received = 0;

        // create int[] buffer (intermediate solution, as the
        // Garmin** classes do not work with the GarminPackage class
        // yet:
    int[] buffer = garmin_package.getCompatibilityBuffer();
      
    switch((int)buffer[0])
    {
	  case NAK:
	    fireResult(false,buffer[1]);
	    break;
	  case ACK:
	    fireResult(true,buffer[1]);
	    break;
          // product info:
	  case Pid_Product_Data:
	    fireProductDataReceived(new GarminProduct(buffer));
	    break;
          // capabilities:
	  case Pid_Protocol_Array:
	    fireProtocolArrayReceived(new GarminCapabilities(buffer));
	    break;
          // PVT package
	  case Pid_Pvt_Data_L001:
	    if(capabilities_.hasCapability("D800"))
	    {
	      firePVTDataReceived(new GarminPVTD800(buffer));
	    }
	    break;
          // larger amount of packages belong together:

    case Pid_Display_Data_L001:
      GarminDisplayData display_data = new GarminDisplayData(garmin_package);
      int height = display_data.getHeight();
      fireProgressActionStart(GETSCREENSHOT,1,height);
      for(int linenum = 0; linenum < height; linenum++)
      {
        do
        {
          next_garmin_package = getPackage();
        }
        while(next_garmin_package == null);
        if(next_garmin_package.getPackageId() != Pid_Display_Data_L001)
        {
          System.err.println("WARNING: Expected Display Data, received: "+next_garmin_package);
          return;
        }
        if(linenum % 10 == 0)
          fireProgressActionProgress(GETSCREENSHOT,linenum);
            // add line to display data:
        display_data.addLine(next_garmin_package);
      }
      fireProgressActionProgress(GETSCREENSHOT,height);
      fireProgressActionEnd(GETSCREENSHOT);
      fireDisplayDataReceived(display_data);
      break;
	  case Pid_Records_L001:
	  case Pid_Records_L002:
	    int package_num = buffer[2]+256*buffer[3];
	    int package_count = 0;
	    if(Debug.DEBUG)
	      Debug.println("gps_garmin_package","Receiving "+package_num+" packets from device.");
          // var to store the resulting route/track/etc.
          // I hope that packets may not be mixed (route and tracks)!
	    Vector items = new Vector();
	    Object item = null;
	    
          // Receive routes/tracks/... from device
	    boolean transfer_complete = false;
	    while(!transfer_complete)
	    {
	      do
	      {
          next_garmin_package = getPackage();
	      }
	      while(next_garmin_package == null);
	      buffer = next_garmin_package.getCompatibilityBuffer();
	      package_count++;
	      if(Debug.DEBUG)
          Debug.println("gps_garmin_package","read package "
                        +(package_count+1)+" of "+package_num);
	      switch((int)buffer[0])
	      {
              // route header:
        case Pid_Rte_Hdr_L001:
        case Pid_Rte_Hdr_L002:
          if(packages_type_received == 0) // only true for first package
            fireProgressActionStart(GETROUTES,1,package_num);
          packages_type_received = RECEIVED_ROUTES;
              // save previous item
          if(item != null)
            items.add(item);

              // create route header depending on used format:
          if(capabilities_.hasCapability("D200"))
            item = new GarminRouteD200(buffer);
          if(capabilities_.hasCapability("D201"))
            item = new GarminRouteD201(buffer);
          if(capabilities_.hasCapability("D202"))
            item = new GarminRouteD202(buffer);
          break;
		    
              // route point:
        case Pid_Rte_Wpt_Data_L001:
        case Pid_Rte_Wpt_Data_L002:
          if(package_count % 10 == 0)
            fireProgressActionProgress(GETROUTES,package_count);
          if(capabilities_.hasCapability("D103"))
            ((GarminRoute)item).addWaypoint(new GarminWaypointD103(buffer));
          else
            if(capabilities_.hasCapability("D107"))
              ((GarminRoute)item).addWaypoint(new GarminWaypointD107(buffer));
            else
              if(capabilities_.hasCapability("D108"))
                ((GarminRoute)item).addWaypoint(new GarminWaypointD108(buffer));
              else
                if(capabilities_.hasCapability("D109"))
                  ((GarminRoute)item).addWaypoint(new GarminWaypointD109(buffer));
                else
                  System.err.println("WARNING: unsupported garmin waypoint type!");
          if(Debug.DEBUG)
            Debug.println("gps_garmin","Received Waypoint");
          break;
              // route link:
        case Pid_Rte_Link_Data_L001:
          if(package_count % 10 == 0)
            fireProgressActionProgress(GETROUTES,package_count);
              // temporarily ignored
//           if(capabilities_.hasCapability("D210"))
//             ((GarminRoute)item).addRouteLinkData(new GarminRouteLinkD210(buffer));
          break;
		    
              // track header
        case Pid_Trk_Hdr_L001:
          if(packages_type_received == 0) // only true for first package
            fireProgressActionStart(GETTRACKS,1,package_num);
          packages_type_received = RECEIVED_TRACKS;
              // save previous item
          if(item != null)
            items.add(item);

              // create route header depending on used format:
          if(capabilities_.hasCapability("D310"))
            item = new GarminTrackD310(buffer);
          if(Debug.DEBUG)
            Debug.println("gps_garmin","Received Track Header: "+item);
          break;

              // trackpoints
        case Pid_Trk_Data_L001:
          if(Debug.DEBUG)
            Debug.println("gps_garmin","Received Track Data");
          if(package_count % 10 == 0)
            fireProgressActionProgress(GETTRACKS,package_count);
          if(capabilities_.hasCapability("D300"))
            ((GarminTrack)item).addWaypoint(new GarminTrackpointD300(buffer));
          if(capabilities_.hasCapability("D301"))
            ((GarminTrack)item).addWaypoint(new GarminTrackpointD301(buffer));
          break;

              // waypoint:
        case Pid_Wpt_Data_L001:
        case Pid_Wpt_Data_L002:
          if(packages_type_received == 0) // only true for first package
            fireProgressActionStart(GETWAYPOINTS,1,package_num);
          packages_type_received = RECEIVED_WAYPOINTS;
          if(items == null)
          {
            items = new Vector();
          }
          if(package_count % 10 == 0)
            fireProgressActionProgress(GETWAYPOINTS,package_count);
          if(capabilities_.hasCapability("D103"))
            items.add(new GarminWaypointAdapter(new GarminWaypointD103(buffer)));
          else
            if(capabilities_.hasCapability("D107"))
              items.add(new GarminWaypointAdapter(new GarminWaypointD107(buffer)));
            else
              if(capabilities_.hasCapability("D108"))
                items.add(new GarminWaypointAdapter(new GarminWaypointD108(buffer)));
              else
                if(capabilities_.hasCapability("D109"))
                  items.add(new GarminWaypointAdapter(new GarminWaypointD109(buffer)));
                else
                  System.err.println("WARNING: unsupported garmin waypoint type!");
          if(Debug.DEBUG)
            Debug.println("gps_garmin","Received Waypoint");
          break;
              // transfer complete
        case Pid_Xfer_Cmplt_L001:
//		  case Pid_Xfer_Cmplt_L002: // same number as Pid_Xfer_Cmplt_L001
//          GarminXferComplete xfer_complete = new GarminXferComplete(garmin_package);
          if(Debug.DEBUG)
            Debug.println("gps_garmin","transfer complete");
          transfer_complete = true;
          break;
        default:
          System.err.println("WARNING GPSGarminDataProcessor: unknown package id: "
                             +(int)buffer[0]);
          if(Debug.DEBUG)
            Debug.println("gps_garmin","unknown package: "+garmin_package);
	      }
	    }
	    
          // add last item vector:
	    if(item != null)
	      items.add(item);
	    if(items.size() > 0)
	    {
	      switch(packages_type_received)
	      {
        case(RECEIVED_ROUTES):
          fireProgressActionProgress(GETROUTES,package_num);
          fireProgressActionEnd(GETROUTES);
          fireRoutesReceived(items);
          break;
        case(RECEIVED_TRACKS):
          fireProgressActionProgress(GETTRACKS,package_num);
          fireProgressActionEnd(GETTRACKS);
          fireTracksReceived(items);
          break;
        case(RECEIVED_WAYPOINTS):
          fireProgressActionProgress(GETWAYPOINTS,package_num);
          fireProgressActionEnd(GETWAYPOINTS);
          fireWaypointsReceived(items);
          break;
        default:
          System.err.println("WARNING: GPSGarminDataProcessor: unknown package list (ignored)");
	      }
	      packages_type_received = 0; // reset package type
	    }
	    break;
	  case Pid_Xfer_Cmplt_L001:
	    fireTransferCompleteReceived();
	    break;
//     case Pid_Satellite_Info:
//       GarminSatelliteInfo info = new GarminSatelliteInfo(garmin_package);
//       break;
	  default:
      System.err.println("WARNING GPSGarminDataProcessor: unknown package id: "
                         +(int)buffer[0]);
      if(Debug.DEBUG)
        Debug.println("gps_garmin","unknown package: "+garmin_package);
    }
  }

//----------------------------------------------------------------------
/**
 * Returns the last received position from the GPSDevice or
 * <code>null</code> if no position was retrieved until now.
 * @return the position from the GPSDevice.
 */

  public GPSPosition getGPSPosition()
  {
    try
    {
      GarminPVT pvtdata = getPVT(0L);
      if(pvtdata != null)
        return(new GPSPosition(pvtdata.getLat(),pvtdata.getLon()));
      else
        return(null);
    }
    catch(Exception e)
    {
      e.printStackTrace();
      return(null);
    }
  }

//----------------------------------------------------------------------
/**
 * Returns the last received heading (direction) from the GPSDevice or
 * <code>-1.0</code> if no heading was retrieved until now.
 * @return the heading from the GPSDevice.
 */
  public float getHeading()
  {
    return(-1.0f);
  }


//----------------------------------------------------------------------
/**
 * Add a result-received listener.
 * @param listener the result-received listener to add.
 */
  public void addResultReceivedListener(ResultReceivedListener listener)
  {
    if(result_listeners_ == null)
      result_listeners_ = new Vector();
    result_listeners_.add(listener);
  }

//----------------------------------------------------------------------
/**
 * Remove a result-received listener.
 * @param listener the result-received listener to remove.
 */
  public void removeResultReceivedListener(ResultReceivedListener listener)
  {
    if(result_listeners_ == null)
      return;
    result_listeners_.remove(listener);
  }
  
  public static void main(String[] args)
  {
    try
    {
      GPSGarminDataProcessor gps_processor = new GPSGarminDataProcessor();
      GPSDevice gps_device;
      Hashtable environment = new Hashtable();
      environment.put(GPSSerialDevice.PORT_NAME_KEY,"/dev/ttyS1");
      environment.put(GPSSerialDevice.PORT_SPEED_KEY,new Integer(9600));
      gps_device = new GPSSerialDevice();
      gps_device.init(environment);
      gps_processor.setGPSDevice(gps_device);
      gps_processor.open();

//	System.out.println("REQ: requesting produce info");
//	gps_processor.requestProductInfo();
//       System.out.println("REQ: requesting PVT");
//       gps_processor.requestPVT();
//       System.out.println("REQ: requesting waypoints");
//       gps_processor.requestWaypoints();
//       System.out.println("REQ: requesting routes");
//       gps_processor.requestRoutes();
//      System.out.println("REQ: requesting tracks");
//      gps_processor.requestTracks();

//      System.out.println("requesting async events");
//      gps_processor.requestAsyncEvents();

//       System.out.println("Requesting PVT");
//       GarminPVT pvt = gps_processor.getPVT(1000L);
//       System.out.println("Sync PVT: "+pvt);
	
//      List routes = gps_processor.getRoutes(0L);
//      System.out.println("Sync Routes: "+routes);

      gps_processor.requestScreenShot();
      
      System.in.read();
//       List waypoints = gps_processor.getWaypoints();
//       System.out.println("Sync Waypoints: "+waypoints);
//       List tracks = gps_processor.getTracks(0L);
//       System.out.println("Sync Tracks: "+tracks);
      
//       System.in.read(); // wait for keypress
//      gps_processor.requestPowerOff();
      
      gps_processor.close();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    
  }


//----------------------------------------------------------------------
/**
 * The ReaderThread reads packages from the gps device and passes them
 * to the firePackageReceived method. It therefore does not block the
 * application's thread when the gps device does not answer.
 */
  class ReaderThread extends Thread
  {
    boolean running_ = true;

    public ReaderThread()
    {
      super();
    }

    public void run()
    { 
      while(running_)
      {
        if(Debug.DEBUG)
          Debug.println("gps_garmin_package","waiting for package...");
        GarminPackage garmin_package = getPackage();
        if(garmin_package == null)
        {
          if(Debug.DEBUG)
            Debug.println("gps_garmin_package","invalid package received");
        }
        else
        {
          if(Debug.DEBUG)
            Debug.println("gps_garmin_package","package received: "+garmin_package.getPackageId());
          firePackageReceived(garmin_package);
        }
      }
    }

    public void stopThread()
    {
      running_ = false;
    }
  }

//----------------------------------------------------------------------
/**
 * The WatchDogThread is used to prevent the data processor from
 * beeing locked. It happens sometimes that the gps device stops
 * sending in the middle of a package and the reader thread then is
 * blocked in the read() method. Therefore the reader thread uses this
 * watchdog. It sends a NAK package after 5 seconds, if not reset
 * before. The readerthread resets the watchdog on every character it
 * reads. So if the reader thread is blocked for more than 5 seconds,
 * a NAK package is sent to the garmin device. This usually helps to
 * wake up the device again. The reader thread tells the watchdog when
 * to start/stop watching (on start/end of the package reading
 * method), so the watchdog does not send NAKs when no packages are
 * expected.
 */
  class WatchDogThread extends Thread
  {
    boolean running_ = true;
    boolean reset_ = false;
    int package_id_ = 0;
    
    public WatchDogThread()
    {
      super("GarminWatchDog");
    }

    public void startWatching()
    {
      running_ = true;
      package_id_ = 0;
      try
      {
        start();
      }
      catch(IllegalThreadStateException ignore) {} // already started
	  
    }
    
    public void setPackageId(int package_id)
    {
      package_id_ = package_id;
    }

    public void reset()
    {
      reset_ = true;
    }
    
    public void run()
    { 
      while(running_)
      {
        reset_ = false;
        try
        {
          Thread.sleep(5000);
        }
        catch(InterruptedException ignore) {}
            // wait 5seconds, if we did not get any data during this
            // time, send a NAK:
        if(!reset_)
        {
          if(Debug.DEBUG)
            Debug.println("gps_garmin_package","WATCHDOG sending NAK");
          try
          {
            sendCommandAsync(NAK,package_id_);
          }
          catch(IOException ioe)
          {
            ioe.printStackTrace();
          }
        }
      }
    }

    public void stopWatching()
    {
      running_ = false;
    }
  }

}
