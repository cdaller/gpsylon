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


package org.dinopolis.gpstool.gpsinput;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

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
 * @author Sandra Brueckler, Stefan Feitl
 * @version $Revision$ */

public class GPSGarminDataProcessor extends GPSGeneralDataProcessor implements Runnable
{
/**
 * Declaration of required in-/output-streams and a communications thread
 * for firing position-change-events.
 */
  InputStream in_stream_ = null;
  OutputStream out_stream_ = null;
  Thread comm_thread_;

/**
 * Basic values for garmin devices
 */
  public int product_id_       = 0;
  public int product_sw_       = 0;
  public String product_name_  = "Not available";
  public String[] product_capabilities_;

  protected final static int MAX_GARMIN_MESSAGE_LENGTH = 260;

/** 
 * Variables for determining product capabilities.
 * These variables desribes the abilities of the GARMIN-device
 * connected to the computer.
 *
 * Array L is true for all supported link protocols
 * Array A is true for all supported application protocols
 * Array D is true for all supported data types
 */
  private static boolean[] L = new boolean[9];
  private static boolean[] A = new boolean[999];
  private static boolean[] D = new boolean[999];

      // Definitions for DLE, ETX, ACK and NAK
  private final static char DLE = (char)16;
  private final static char ETX = (char)3;
  private final static char ACK = (char)6;
  private final static char NAK = (char)21;
  
/**
 * Identifiers for L000 - Basic Link Protocol
 */
  public final static char Pid_Protocol_Array = (char)253;
  public final static char Pid_Product_Rqst   = (char)254;
  public final static char Pid_Product_Data   = (char)255;

/**
 * Identifiers for L001 - Link Protocol 1
 */
  public final static char Pid_Command_Data_L001   = (char)10;
  public final static char Pid_Xfer_Cmplt_L001     = (char)12;
  public final static char Pid_Date_Time_Data_L001 = (char)14;
  public final static char Pid_Position_Data_L001  = (char)17;
  public final static char Pid_Prx_Wpt_Data_L001   = (char)19;
  public final static char Pid_Records_L001        = (char)27;
  public final static char Pid_Rte_Hdr_L001        = (char)29;
  public final static char Pid_Rte_Wpt_Data_L001   = (char)30;
  public final static char Pid_Almanac_Data_L001   = (char)31;
  public final static char Pid_Trk_Data_L001       = (char)34;
  public final static char Pid_Wpt_Data_L001       = (char)35;
  public final static char Pid_Pvt_Data_L001       = (char)51;
  public final static char Pid_Rte_Link_Data_L001  = (char)98;
  public final static char Pid_Trk_Hdr_L001        = (char)99;

/**
 * Identifiers for L002 - Link Protocol 2
 */
  public final static char Pid_Almanac_Data_L002   = (char)4;
  public final static char Pid_Command_Data_L002   = (char)11;
  public final static char Pid_Xfer_Cmplt_L002     = (char)12;
  public final static char Pid_Date_Time_Data_L002 = (char)20;
  public final static char Pid_Position_Data_L002  = (char)24;
  public final static char Pid_Records_L002        = (char)35;
  public final static char Pid_Rte_Hdr_L002        = (char)37;
  public final static char Pid_Rte_Wpt_Data_L002   = (char)39;
  public final static char Pid_Wpt_Data_L002       = (char)43;

/**
 * Identifiers for A010 - Device Command Protocol 1
 */
  public final static char Cmnd_Abort_Transfer_A010 = (char)0;
  public final static char Cmnd_Transfer_Alm_A010   = (char)1;
  public final static char Cmnd_Transfer_Posn_A010  = (char)2;
  public final static char Cmnd_Transfer_Prx_A010   = (char)3;
  public final static char Cmnd_Transfer_Rte_A010   = (char)4;
  public final static char Cmnd_Transfer_Time_A010  = (char)5;
  public final static char Cmnd_Transfer_Trk_A010   = (char)6;
  public final static char Cmnd_Transfer_Wpt_A010   = (char)7;
  public final static char Cmnd_Turn_Off_Pwr_A010   = (char)8;
  public final static char Cmnd_Start_Pvt_Data_A010 = (char)49;
  public final static char Cmnd_Stop_Pvt_Data_A010  = (char)50;

/**
 * Identifiers for A011 - Device Command Protocol 2
 */
  public final static char Cmnd_Abort_Transfer_A011 = (char)0;
  public final static char Cmnd_Transfer_Alm_A011   = (char)4;
  public final static char Cmnd_Transfer_Rte_A011   = (char)8;
  public final static char Cmnd_Transfer_Time_A011  = (char)20;
  public final static char Cmnd_Transfer_Wpt_A011   = (char)21;
  public final static char Cmnd_Turn_Off_Pwr_A011   = (char)26;

//----------------------------------------------------------------------
/**
 * Default constructor.
 */
  public GPSGarminDataProcessor()
  {
  }
  
  
//----------------------------------------------------------------------
/**
 * Returns the product id.
 * @return the product id.
 */
  public int getProductId()
  {
    return(product_id_);
  }
  
//----------------------------------------------------------------------
/**
 * Returns the product software.
 * @return the product software.
 */
  public int getProductSoftware()
  {
    return(product_sw_);
  }
  
//----------------------------------------------------------------------
/**
 * Returns the product name.
 * @return the product name.
 */
  public String getProductName()
  {
    return(product_name_);
  }
  
//----------------------------------------------------------------------
/**
 * Returns the product capabilities.
 * @return the product capabilities.
 */
  public String[] getProductCapabilities()
  {
    return(product_capabilities_);
  }
  
  
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

      comm_thread_ = new Thread(this,"GPSGarminDataProcessorIn");
      comm_thread_.setDaemon(true); // so thread is finished after exit of application
      comm_thread_.start();

          // read product data
      readProductData();
    }
    catch(IOException e)
    {
      throw new GPSException(e.getMessage());
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
    gps_device_.close();
  }

  
//----------------------------------------------------------------------
/**
 * Start reading of position-time-velocity-data from the connected
 * device using the communicaions thread.
 */
  public void run()
  {
    readMessages();
  }


//----------------------------------------------------------------------
/**
 * Send request/command to GARMIN-Device.
 *
 * @param rqst Request to be sent to the device.
 * @param cmnd Command to be sent to the device.
 */
  protected void sendCommand(char rqst, char cmnd)
  {
    char check = (char)0;
    char icheck = (char)0;

    char[] helper;
    boolean success = false;

    try
    {
      do
      {
            // Start sending request to device
            // If any byte in size, data or checksum fields is equal to DLE
            // a second DLE is sent immediately following this byte
        out_stream_.write(DLE);
        out_stream_.write(rqst);

            // Write length of packet data to output stream
            // Length is always 2 because we are transmitting a 16 bit command id
        out_stream_.write((char)2);

            // Write packet data to device
            // Highbyte of command data is always 0 because all command id's are <= 255
        out_stream_.write(cmnd);
        out_stream_.write((char)(0));
        if (cmnd == DLE) {out_stream_.write(DLE);}

            // Create checksum and write to device
        check += (rqst + 2);
        if ((int)check > 255) check -= 256;
        check += cmnd;
        if ((int)check > 255) check -= 256;

        icheck = (char)(256-check);
        out_stream_.write(icheck);
        if (icheck == DLE) {out_stream_.write(DLE);}

            // Finish request and flush output stream
        out_stream_.write(DLE);
        out_stream_.write(ETX);
        out_stream_.flush();

            // Wait for answer of device and handle if necessary
        if ((rqst != ACK) && (rqst != NAK))
        {
              // Delay loop
          int count = 0;

          while(in_stream_.available()==0)
          {
            count++;
            if (count == 1000000)
            {
                  // The power off command may not be acknowledged so this is
                  // no reason to throw an exception
              if (cmnd == Cmnd_Turn_Off_Pwr_A010 || cmnd == Cmnd_Turn_Off_Pwr_A011)
                return;
              throw new IOException("Device not ready / powered!");
            }
          }

              // Read package from device and check if sent package has arrived correctly
          helper=getPackage();
          if (helper[0] == NAK) {success = false;}
          if (helper[0] == ACK) {success = true;}
        }
        else
          success = true;
      }
      while (success == false);
    }
    catch(IOException ioe)
    {
      ioe.printStackTrace();
    }
  }

//----------------------------------------------------------------------
/**
 * Send request/command to GARMIN-Device.
 *
 * @param rqst Request to be sent to the device.
 * @param data Data to be sent to the device.
 */
  protected void sendCommand(char rqst, String data)
  {
    char check = (char)0;
    char icheck = (char)0;

    int packet = 0;
    int length = data.length();
    char[] buffer = data.toCharArray();

    char[] helper;
    boolean success = false;

    try
    {
      do
      {
            // Start sending request to device
            // If any byte in size, data or checksum fields is equal to DLE
            // a second DLE is sent immediately following this byte
        out_stream_.write(DLE);
        out_stream_.write(rqst);

            // Write length of packet data to output stream
        out_stream_.write((char)length);
        if (length == DLE) {out_stream_.write(DLE);}

            // Write packet data to device
        for (int i=0;i<length;i++)
        {
          System.out.println(i+" "+(int)buffer[i]);
          out_stream_.write(buffer[i]);
          if (buffer[i] == DLE) {out_stream_.write(DLE);}
          packet += buffer[i];
          if ((int)packet > 255) packet -= 256;
        }

            // Create checksum and write to device
        check += (rqst + length);
        if ((int)check > 255) check -= 256;
        check += packet;
        if ((int)check > 255) check -= 256;

        icheck = (char)(256-check);
        out_stream_.write(icheck);
        if (icheck == DLE) {out_stream_.write(DLE);}

            // Finish request and flush output stream
        out_stream_.write(DLE);
        out_stream_.write(ETX);
        out_stream_.flush();

            // Wait for answer of device and handle if necessary
        if ((rqst != ACK) && (rqst != NAK))
        {
              // Delay loop
          int count = 0;

          while(in_stream_.available()==0)
          {
            count++;
            if (count==100000) throw new IOException("Device not ready / powered!");
          }

              // Read package from device and check if sent package has arrived correctly
          helper=getPackage();
          if (helper[0] == NAK) {success = false;}
          if (helper[0] == ACK) {success = true;}
        }
        else
          success = true;
      }
      while (success == false);
    }
    catch(IOException ioe)
    {
      ioe.printStackTrace();
    }
  }


//----------------------------------------------------------------------
/**
 * Receive a package transmitted by the GARMIN-Device.
 */
  protected char[] getPackage()
  {
    char check = (char)0;
    char icheck = (char)0;

    int count = 0;
    int data;
    char[] buffer;

    char[] pid = new char[1];
    boolean success = false;

        // Initialize receive buffer and check whether device is ready
    buffer = new char[MAX_GARMIN_MESSAGE_LENGTH];

    try
    {
      do
      {
            // Wait for beginning of next incoming message
        while ((data = in_stream_.read()) != DLE) {}

        if (in_stream_.available() == 0)
          throw new IOException("Device not ready!");

        while (count<MAX_GARMIN_MESSAGE_LENGTH)
        {
              // Read data from instream
          data = in_stream_.read();
          if (count > 0)
          {
            if ((buffer[count-1] == DLE) && (data == ETX))
              break;
          }

              // Write received data into buffer
//            if (count == 0) System.out.print("Incoming package: "+(int)data+", ");
//            if (count == 1) System.out.println("Package size: "+(int)data);
          buffer[count] = (char)data;

              // If two subsequent DLE's have been received, one is ignored due to
              // transfer protocol specifiactions
          if (data == DLE && buffer[count-1] == DLE)
          {
          }
          else
            count++;

              // Delay loop - necessary because computer may try to receive data
              // faster than it can be (is) sent by the device
          while (in_stream_.available() == 0)
          {
          }
        }

            // Get packet id out of instream
        pid[0] = buffer[0];

            // Reduce counter by one because last byte (ETX) is not stored at all
            // Last char is DLE signalling end of packet
        count--;

            // Calculate checksum for received package
        for (int i=0;i<count-1;i++)
        {
          check += buffer[i];
          if ((int)check > 255)
            check -= 256;
        }

            // Check if calculated checksum is equal to received checksum
            // Acknowledge received packet if it was correctly trasmitted
            // Otherwise ask for new transmission of packet
        icheck=(char)(256-check);

        if (icheck == buffer[count-1])
        {
          success = true;
          if ((pid[0] != ACK) && (pid[0]!= NAK))
            sendCommand(ACK,(char)pid[0]);
        }
        else
        {
          count = 0;
          if ((pid[0] != ACK) && (pid[0]!= NAK))
            sendCommand(NAK,(char)pid[0]);
          System.out.println("Package not received correctly: "+(int)pid[0]);
        }
      }
      while (success == false);
    }
    catch(IOException ioe)
    {
      ioe.printStackTrace();
    }

    return buffer;
  }


//----------------------------------------------------------------------
/**
 * Get product information from attached device to determine which
 * communication functions may be used.
 */
  protected void readProductData()
  {
    char[] buffer;
    int count = 0;

    try
    {
          // Send device information request
      sendCommand(Pid_Product_Rqst,"");

          // Retrieve, check and store device information
      buffer=getPackage();
      if ((int)buffer[0]==255)
      {
        product_id_=(int)(buffer[2]+256*buffer[3]);
        product_sw_=(int)(buffer[4]+256*buffer[5]);

        char helper[] = new char[255];

        for (int i=0;i<249;i++)
        {
          helper[i]=buffer[i+6];
        }
        product_name_ = new String(helper);
      }
      else
      {
        throw new IOException("Invalid version string received.");
      }

          // Delay loop
      while(in_stream_.available()==0)
      {
        count++;
        if (count==100000)
        {
          System.out.println("No capability data available");
          break;
        }
      }

          // Retrieve device capabilities if devices transmits them
      if (in_stream_.available()>0) buffer=getPackage();

          // Store capabilities into global variable
      if ((int)buffer[0] == 253)
      {
        product_capabilities_ = new String[(buffer[1]/3)];
        for (int i=0;i<(buffer[1]/3);i++)
        {
              // Add received capability to global capability variable
          product_capabilities_[i] = buffer[2+3*i]+""+(int)(buffer[3+3*i]+256*buffer[4+3*i]);

              // Set capabilities due to received tag values
          if ((char)buffer[2+3*i] == 'L') L[(int)(buffer[3+3*i]+256*buffer[4+3*i])]=true;
          if ((char)buffer[2+3*i] == 'A') A[(int)(buffer[3+3*i]+256*buffer[4+3*i])]=true;
          if ((char)buffer[2+3*i] == 'D') D[(int)(buffer[3+3*i]+256*buffer[4+3*i])]=true;
        }
      }
    }
    catch(IOException ioe)
    {
      ioe.printStackTrace();
    }
  }

//----------------------------------------------------------------------
/**
 * Read actual position, velocity and time from connected device
 *
 * @exception if an error occured during access to the device
 * @return current position, velocity and time
 */
  public GarminPVT readPVTData(boolean events) throws GPSException
  {
    char[] buffer;
    GarminPVT pvt = new GarminPVT();

        // If communications thread is alive and not using
        // this function let it sleep until work is done.
        // Otherwise data transfer is highly likely to fail
    if (!events && comm_thread_.isAlive()) comm_thread_.interrupt();

        // Does device support PVT protocol
    if (A[800])
    {
      try
      {
            // Request PVT data due to link and command protocols available for device
        if (L[1] && A[10]) sendCommand(Pid_Command_Data_L001, Cmnd_Start_Pvt_Data_A010);

            // Wait if device transmits data or is not ready to transmit data
            // If device does not send PVT data return to calling function
        Thread.sleep(1500);
        if (in_stream_.available()==0)
        {
          pvt.setFix(1);
          return(pvt);
        }

            // Read PVT data as available from device
        buffer=getPackage();

            // Check if a PVT data packet has been received
        if ((int)buffer[0] == Pid_Pvt_Data_L001)
        {
              // Data is in data format D800
          if (D[800]) pvt = new GarminPVTD800(buffer);
        }

            // No more PVT data required
        sendCommand(Pid_Command_Data_L001, Cmnd_Stop_Pvt_Data_A010);
      }
      catch(IOException ioe)
      {
        ioe.printStackTrace();
        throw new GPSException(ioe.getMessage());
      }
      catch(InterruptedException inte)
      {}
    }

        // If communications thread is alive awake it again
    if (!events && comm_thread_.isAlive()) comm_thread_.interrupt();

    return(pvt);
  }

//----------------------------------------------------------------------
/**
 * Read waypoint-data from connected device
 *
 * @exception if an error occured during access to the device
 * @return waypoints stored in device
 */
  public GarminWaypoint[] readWaypointData() throws GPSException
  {
    char[] buffer;
    GarminWaypoint[] waypoints = null;
    
        // If communications thread is alive let it sleep until work is done.
        // Otherwise data transfer is highly likely to fail
    if (comm_thread_.isAlive()) comm_thread_.interrupt();

        // Does device support waypoint transfer protocol
    if (A[100])
    {
      try
      {
            // Request waypoint data due to link and command protocols available for device
        if (L[1] && A[10]) sendCommand(Pid_Command_Data_L001, Cmnd_Transfer_Wpt_A010);
        if (L[1] && A[11]) sendCommand(Pid_Command_Data_L001, Cmnd_Transfer_Wpt_A011);
        if (L[2] && A[10]) sendCommand(Pid_Command_Data_L002, Cmnd_Transfer_Wpt_A010);
        if (L[2] && A[11]) sendCommand(Pid_Command_Data_L002, Cmnd_Transfer_Wpt_A011);

            // Wait if device transmits data or is not ready to transmit data
            // If device does not send waypoints return to calling function
        Thread.sleep(1500);
        if (in_stream_.available()==0)
        {
          return(waypoints);
        }

            // Read waypoint data as available from device
        buffer = getPackage();

            // Has the beginning package been received from device?
        if ((buffer[0] == Pid_Records_L001) || (buffer[0] == Pid_Records_L002))
        {
              // How many data packets will follow after beginning packet
          int length = buffer[2]+256*buffer[3];

          System.out.println("Receiving "+length+" waypoints from device");

              // Initialize waypoint buffer
          waypoints = new GarminWaypoint[length];

              // Receive waypoints from device
          for (int wpt_count = 0; wpt_count < length; wpt_count++)
          {
            buffer = getPackage();

                // Data is in data format D100

                // Data is in data format D101

                // Data is in data format D102

                // Data is in data format D103

                // Data is in data format D104

                // Data is in data format D105

                // Data is in data format D106

                // Data is in data format D107

                // Data is in data format D108
            if (D[108]) waypoints[wpt_count] = new GarminWaypointD108(buffer);

                // Data is in data format D150

                // Data is in data format D151

                // Data is in data format D152

                // Data is in data format D154

                // Data is in data format D155
          }

              // Receive signal that transfer is completed
          buffer = getPackage();
        }
      }
      catch(IOException ioe)
      {
        ioe.printStackTrace();
        throw new GPSException(ioe.getMessage());
      }
      catch(InterruptedException inte)
      {}
    }

        // If communications thread is alive awake it again
    if (comm_thread_.isAlive()) comm_thread_.interrupt();

    return(waypoints);
  }

//----------------------------------------------------------------------
/**
 * Read route-data from connected device
 *
 * @exception if an error occured during access to the device
 * @return routes stored in device
 */
  public GarminRoute[] readRouteData() throws GPSException
  {
    char[] buffer;
    int rte_count = 0;
    GarminRoute[] routes = new GarminRoute[99];
    
        // If communications thread is alive let it sleep until work is done.
        // Otherwise data transfer is highly likely to fail
    if (comm_thread_.isAlive()) comm_thread_.interrupt();

        // Does device support route transfer protocol
    if (A[200] || A[201])
    {
      try
      {
            // Request route data due to link and command protocols available for device
        if (L[1] && A[10]) sendCommand(Pid_Command_Data_L001, Cmnd_Transfer_Rte_A010);
        if (L[1] && A[11]) sendCommand(Pid_Command_Data_L001, Cmnd_Transfer_Rte_A011);
        if (L[2] && A[10]) sendCommand(Pid_Command_Data_L002, Cmnd_Transfer_Rte_A010);
        if (L[2] && A[11]) sendCommand(Pid_Command_Data_L002, Cmnd_Transfer_Rte_A011);

            // Wait if device transmits data or is not ready to transmit data
            // If device does not send routes return to calling function
        Thread.sleep(1500);
        if (in_stream_.available()==0)
        {
          return(routes);
        }

            // Read route data as available from device
        buffer=getPackage();

            // Has the beginning package been received from device?
        if ((buffer[0] == Pid_Records_L001) || (buffer[0] == Pid_Records_L002))
        {
              // How many data packets will follow after beginning packet
          int length = buffer[2]+256*buffer[3];

          System.out.println("Receiving "+length+" route informations from device");

              // Receive routes from device
          for (int i=0;i<length;i++)
          {
            buffer=getPackage();
            System.out.println("Route: "+(i+1));

                // Process received data using application protocol A200
            if (A[200])
            {
            }

                // Process received data using application protocol A201
            if (A[201])
            {
                  // Has a route header been received
              if ((buffer[0] == Pid_Rte_Hdr_L001) || (buffer[0] == Pid_Rte_Hdr_L002))
              {
                rte_count++;

                    // Data is in supported data formats D200, D201, D202
                if (D[200]) routes[rte_count] = new GarminRouteD200(buffer);
                if (D[201]) routes[rte_count] = new GarminRouteD201(buffer);
                if (D[202]) routes[rte_count] = new GarminRouteD202(buffer);
              }

                  // Has route waypoint data been received
              if ((buffer[0] == Pid_Rte_Wpt_Data_L001) || (buffer[0] == Pid_Rte_Wpt_Data_L002))
              {
                    // Data is in data format D108
                if (D[108]) routes[rte_count].addRoutePoint(new GarminWaypointD108(buffer));
              }

                  // Has route link data been received
              if (buffer[0] == Pid_Rte_Link_Data_L001)
              {
                    // Data is in supported data format D210
                if (D[210]) routes[rte_count].addRouteLinkData(new GarminRouteLinkD210(buffer));
              }
            }
          }

              // Receive signal that transfer is completed
          buffer=getPackage();
        }
      }
      catch(IOException ioe)
      {
        ioe.printStackTrace();
        throw new GPSException(ioe.getMessage());
      }
      catch(InterruptedException inte)
      {}
    }

        // If communications thread is alive awake it again
    if (comm_thread_.isAlive()) comm_thread_.notify();

    return(routes);
  }

//----------------------------------------------------------------------
/**
 * Read track-data from connected device.
 * Supported data protocols:<br>
 * <b>Command protocols</b> A300, A301<br>
 * <b>Data protocols</b> D300, D301, D310<br>
 *
 * @exception if an error occured during access to the device
 * @return tracks stored in device
 */
  public GarminTrack[] readTrackData() throws GPSException
  {
    char[] buffer;
    int trk_count = 0;
    GarminTrack[] tracks = new GarminTrack[99];
    
        // Does device use supported track transfer protocol
    if (A[300] || A[301])
    {
      try
      {
            // Request waypoint data due to link and command protocols available for device
        if (L[1] && A[10]) sendCommand(Pid_Command_Data_L001, Cmnd_Transfer_Trk_A010);

            // Wait if device transmits data or is not ready to transmit data
            // If device does not send tracks return to calling function
        Thread.sleep(1500);
        if (in_stream_.available()==0)
        {
          return(tracks);
        }

            // Read waypoint data as available from device
        buffer=getPackage();

            // Has the beginning package been received from device?
        if (buffer[0] == Pid_Records_L001)
        {
              // How many data packets will follow after beginning packet
          int length = buffer[2]+256*buffer[3];

          System.out.println("Receiving "+length+" track informations from device");

              // Receive tracks from device using protocols A300, A301
          for (int i=0;i<length;i++)
          {
            buffer=getPackage();
            System.out.println("Track: "+(i+1));

                // Protocol A301 supports track headers, so check for them
            if (A[301] && (buffer[0] == Pid_Trk_Hdr_L001))
            {
              trk_count++;

                  // Data is in supported data format D310
              if (D[310]) tracks[trk_count] = new GarminTrackD310(buffer);
            }

                // Read and process received track data
            if (buffer[0] == Pid_Trk_Data_L001)
            {
                  // Data is in supported data formats D300, D301
              if (D[300]) tracks[trk_count].addTrackPoint(new GarminTrackpointD300(buffer));
              if (D[301]) tracks[trk_count].addTrackPoint(new GarminTrackpointD301(buffer));
            }
          }

              // Receive signal that transfer is completed
          buffer=getPackage();
        }
      }
      catch(IOException ioe)
      {
        ioe.printStackTrace();
        throw new GPSException(ioe.getMessage());
      }
      catch(InterruptedException inte)
      {}
    }

    return(tracks);
  }

//----------------------------------------------------------------------
/**
 * Turn off the power of the device connected to the computer
 */
  public void powerOff()
  {
        // Turn off power using link protocol L001 and command protocol A010
    if (L[1] && A[10])
    {
      sendCommand(Pid_Command_Data_L001, Cmnd_Turn_Off_Pwr_A010);
    }

        // Turn off power using link protocol L002 and command protocol A011
    if (L[2] && A[11])
    {
      sendCommand(Pid_Command_Data_L002, Cmnd_Turn_Off_Pwr_A011);
    }

        // Power of devices with other capabilities can't be turned off so nothing else to do
  }

/**
 * Reads the GARMIN position-velocity-time-data from the device when
 * Processor is used in thread mode.
 */
  protected void readMessages()
  {
    int loopcount = 0;
    GarminPVT pvtdata;

    Object lock_ = new Object();

    try
    {
      while(true)
      {
        loopcount++;
        pvtdata = readPVTData(true);

            // Has valid PVT-data been received
        if (pvtdata.fix_ > 1)
        {
              /** Fire data change event for location (degrees) */
          GPSPosition pos = new GPSPosition(pvtdata.lat_,pvtdata.lon_);
          changeGPSData(LOCATION, pos);

              /** Fire data change event for height / depth above msl (meters) */
          if (pvtdata.alt_ >= 0)
            changeGPSData(ALTITUDE, new Float(pvtdata.alt_));
          else
            changeGPSData(DEPTH, new Float(pvtdata.alt_));
        }

        Thread.sleep(1000);
      }
    }
    catch (GPSException gpse)
    {
      gpse.printStackTrace();
    }
    catch (InterruptedException inte)
    {}
  }

//----------------------------------------------------------------------
/**
 * Reads the GARMIN position-velocity-time-data from the device
 * Protected function for reading and handling data without GUI
 */
  public void printPVTData() throws GPSException
  {
    GarminPVT pvtdata;

    if (Debug.DEBUG)
    {
      Debug.println("gpstool","start reading pvt-data from GPSDevice...");
      Debug.print("gpstool_readmessage","inputstream: "+in_stream_);
    }

    while(true)
    {
      pvtdata = readPVTData(false);
      System.out.println(pvtdata);
    }
  }

//----------------------------------------------------------------------
/**
 * Reads waypoints from the connected device
 * Protected function for reading and handling data without GUI
 */
  public void printWaypointData() throws GPSException
  {
    GarminWaypoint[] waypoints;
    
    if (Debug.DEBUG)
    {
      Debug.println("gpstool","start reading waypoint-data from GPSDevice...");
      Debug.print("gpstool_readmessage","inputstream: "+in_stream_);
    }

    waypoints = readWaypointData();
    System.out.println(waypoints.length + " Waypoints received.");

    for (int i=1; i<(waypoints.length-1); i++)
    {
      if (waypoints[i] != null) System.out.println(waypoints[i]);
    }
  }

//----------------------------------------------------------------------
/**
 * Reads routes from the connected device
 * Protected function for reading and handling data without GUI
 */
  public void printRouteData() throws GPSException
  {
    GarminRoute[] routes;

    if (Debug.DEBUG)
    {
      Debug.println("gpstool","start reading route-data from GPSDevice...");
      Debug.print("gpstool_readmessage","inputstream: "+in_stream_);
    }

    routes = readRouteData();

    for (int i=1; i<(routes.length-1); i++)
    {
      if (routes[i] != null) System.out.println(routes[i]);
    }
  }

//----------------------------------------------------------------------
/**
 * Reads tracks from the connected device
 * Protected function for reading and handling data without GUI
 */
  public void printTrackData() throws GPSException
  {
    GarminTrack[] tracks;

    if (Debug.DEBUG)
    {
      Debug.println("gpstool","start reading track-data from GPSDevice...");
      Debug.print("gpstool_readmessage","inputstream: "+in_stream_);
    }

    tracks = readTrackData();

        // Loop has to start with 0 because device supprots only single tracks
        // it is stored at index 0. If device supports multiple tracks, these
        // tracks start at index 1.
    for (int i=0; i<(tracks.length-1); i++)
    {
      if (tracks[i] != null) System.out.println(tracks[i]);
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
      GarminPVT pvtdata = readPVTData(false);
      return(new GPSPosition(pvtdata.lat_,pvtdata.lon_));
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
}
