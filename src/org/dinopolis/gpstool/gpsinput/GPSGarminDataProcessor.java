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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Hashtable;
import org.dinopolis.util.Debug;

//----------------------------------------------------------------------
/**
 * All classes extending this class are interpreting data from
 * a GPSDevice (serial gps-receivier, file containing gps data, ...)
 * and provide this information in a uniform way. So an NMEA-processor
 * interprets NMEA sentences, while a Garmin-Processor understands the
 * garmin protocol.
 * <P>
 * This abstract class adds some basic functionality all
 * GSPDataProcessors might use.
 *
 * @author Sandra Brueckler, Stefan Feitl
 * @version $Revision$ */

public class GPSGarminDataProcessor extends GPSGeneralDataProcessor implements Runnable
{

  public final static String GARMIN_OPTIONS_KEY = "garmin_options";

      /** the inputstream from the GPSDevice */
  InputStream in_stream_ = null;
      /** the outputstream to the GPSDevice */
  OutputStream out_stream_ = null;
      /** the communications thread */
  Thread comm_thread_;

      // Basic values for garmin devices
  public static int PROD_ID       = 0;
  public static int PROD_SW       = 0;
  public static String PROD_NAME  = "Not available";
  public static String[] PROD_CAP;
  private static String options_ = null;
  private static boolean running = false;
  public final static int MAX_GARMIN_MESSAGE_LENGTH = 260;

      // Variables for determining product capabilities
  private static boolean[] L = new boolean[9];
  private static boolean[] A = new boolean[999];
  private static boolean[] D = new boolean[999];

  public final static String GARMIN_LOCATION = "GLL";
  public final static String GARMIN_HEADING = "HDG";

      // Definitions for DLE and ETX
  private final static char DLE = (char)16;
  private final static char ETX = (char)3;
  private final static char ACK = (char)6;
  private final static char NAK = (char)21;
  
      // Identifiers for L000 - Basic Link Protocol
  public final static char Pid_Protocol_Array = (char)253;
  public final static char Pid_Product_Rqst   = (char)254;
  public final static char Pid_Product_Data   = (char)255;

      // Identifiers for L001 - Link Protocol 1
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

      // Identifiers for L002 - Link Protocol 2
  public final static char Pid_Almanac_Data_L002   = (char)4;
  public final static char Pid_Command_Data_L002   = (char)11;
  public final static char Pid_Xfer_Cmplt_L002     = (char)12;
  public final static char Pid_Date_Time_Data_L002 = (char)20;
  public final static char Pid_Posotion_Data_L002  = (char)24;
  public final static char Pid_Records_L002        = (char)35;
  public final static char Pid_Rte_Hdr_L002        = (char)37;
  public final static char Pid_Rte_Wpt_Data_L002   = (char)39;
  public final static char Pid_Wpt_Data_L002       = (char)43;

      // Identifiers for A010 - Device Command Protocol 1
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

      // Identifiers for A011 - Device Command Protocol 2
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
 * Initializes the GPS-Processor. Retrieves information on possible
 * commands and instruction sets.
 *
 * @exception if an error occured on initializing.
 */
  public void init(Hashtable environment)
    throws GPSException
  {
    if (gps_device_ == null)
      throw new GPSException("No GPSDevice set!");

//      try
//      {
//        gps_device_.open();
//        in_stream_ = gps_device_.getInputStream();
//        out_stream_ = gps_device_.getOutputStream();

//            // Receive product data for information purposes
//        readProductData();

//        gps_device_.close();
//      }
//      catch(IOException e)
//      {
//        throw new GPSException(e.getMessage());
//      }

//      if (environment.containsKey(GARMIN_OPTIONS_KEY))
//        options_ = (String)environment.get(GARMIN_OPTIONS_KEY);

    if (Debug.DEBUG)
      Debug.print("gpstool_message","options: "+options_);
  }

//----------------------------------------------------------------------
/**
 * Starts the data processing. The Data Processor connects to the
 * GPSDevice and starts sending/retrieving information.
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
      comm_thread_ = new Thread(this,"GPSGarminDataProcessorIn");
      comm_thread_.setDaemon(true); // so thread is finished after exit of application
      comm_thread_.start();

      out_stream_ = gps_device_.getOutputStream();
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
  public void run()
  {
//          // Only one thread may perform operations at the same time
//      if (!running)
//      {
//        running = true;

//            // Perform operation due to parameter(s) passed to the program
//        if (options_.startsWith("p"))
//        {
//  	System.out.println("\nPVT mode...");
//  	printPVTData();
//        }
//        if (options_.startsWith("dw"))
//        {
//  	System.out.println("\nDownload waypoints...");
//  	printWaypointData();
//        }
//        if (options_.startsWith("dr"))
//        {
//  	System.out.println("\nDownlaod routes...");
//  	printRouteData();
//        }
//        if (options_.startsWith("dt"))
//        {
//  	System.out.println("\nDownload tracks...");
//  	printTrackData();
//        }
//      }
//      running = false;
  }


//----------------------------------------------------------------------
/**
 * Send request/command to GARMIN-Device.
 *  Function is overloaded to support multiple communication
 *  methods with the device connected. May be extended if necessary.
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
            // Highbyte of command data is always 0 because all command id's
            // are <= 255
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
 * communication functions may bes used.
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
        PROD_ID=(int)(buffer[2]+256*buffer[3]);
        PROD_SW=(int)(buffer[4]+256*buffer[5]);

        char helper[] = new char[255];

        for (int i=0;i<249;i++)
        {
          helper[i]=buffer[i+6];
        }
        PROD_NAME = new String(helper);
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
        PROD_CAP = new String[(buffer[1]/3)];
        for (int i=0;i<(buffer[1]/3);i++)
        {
              // Add received capability to global capability variable
          PROD_CAP[i] = buffer[2+3*i]+""+(int)(buffer[3+3*i]+256*buffer[4+3*i]);

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
  public GarminPVT readPVTData()
    throws GPSException
  {
    char[] buffer;
    int count = 0;

    GarminPVT pvt = new GarminPVT();

        // Does device support PVT protocol
    if (A[800])
    {
      try
      {
            // Request PVT data due to link and command protocols available for device
        if (L[1] && A[10]) sendCommand(Pid_Command_Data_L001, Cmnd_Start_Pvt_Data_A010);

            // Wait if device transmits data or is not ready to transmit data
            // If device does not send PVT data return to calling function
        while(in_stream_.available()==0)
        {
          count++;
          if (count==1000000)
          {
            pvt.fix_ = -1;
            return(pvt);
          }
        }

            // Read PVT data if available from device
        buffer=getPackage();

            // Check if a PVT data packet has been received
        if ((int)buffer[0] == Pid_Pvt_Data_L001)
        {
              // Altitude above WGS84-Ellipsoid [meters]
          pvt.alt_ = GarminDataConverter.FloatLEtoBE(buffer[2],buffer[3],buffer[4],buffer[5]);

              // Estimated position error
              // epe - 2sigma [meters]
              // eph - horizontal only [meters]
              // epv - vertical only [meters]
              // fix - type of position fix
          pvt.epe_ = GarminDataConverter.FloatLEtoBE(buffer[6],buffer[7],buffer[8],buffer[9]);
          pvt.eph_ = GarminDataConverter.FloatLEtoBE(buffer[10],buffer[11],buffer[12],buffer[13]);
          pvt.epv_ = GarminDataConverter.FloatLEtoBE(buffer[14],buffer[15],buffer[16],buffer[17]);
          pvt.fix_ = buffer[18]+256*buffer[19];

              // Time of week [seconds]
          pvt.tow_ = GarminDataConverter.DoubleLEtoBE(buffer[20],buffer[21],buffer[22],buffer[23],
                                  buffer[24],buffer[25],buffer[26],buffer[27]);

              // Latitude and longitude is reported in radiant, so it has
              // to be converted into degree
          pvt.lat_ = GarminDataConverter.DoubleLEtoBE(buffer[28],buffer[29],buffer[30],buffer[31],
                                  buffer[32],buffer[33],buffer[34],buffer[35])*(180/Math.PI);
          pvt.lon_ = GarminDataConverter.DoubleLEtoBE(buffer[36],buffer[37],buffer[38],buffer[39],
                                  buffer[40],buffer[41],buffer[42],buffer[43])*(180/Math.PI);

              // Movement speeds in east, north, up-direction. Opposite directions
              // are reported by negative speeds [meters/second]
          pvt.east_ = GarminDataConverter.FloatLEtoBE(buffer[44],buffer[45],buffer[46],buffer[47]);
          pvt.north_ = GarminDataConverter.FloatLEtoBE(buffer[48],buffer[49],buffer[50],buffer[51]);
          pvt.up_ = GarminDataConverter.FloatLEtoBE(buffer[52],buffer[53],buffer[54],buffer[55]);

              // Height of WGS84-Ellipsoid above MSL [meters]
          pvt.msl_height_ = GarminDataConverter.FloatLEtoBE(buffer[56],buffer[57],buffer[58],buffer[59]);

              // Difference between GPS and UTS [seconds]
          pvt.leap_seconds_ = buffer[60]+256*buffer[61];

              // Week number days
          pvt.wn_days_ = GarminDataConverter.IntLEtoBE(buffer[62],buffer[63],buffer[64],buffer[65]);
        }

            // No more PVT data required
        sendCommand(Pid_Command_Data_L001, Cmnd_Stop_Pvt_Data_A010);
      }
      catch(IOException ioe)
      {
        ioe.printStackTrace();
        throw new GPSException(ioe.getMessage());
      }
    }
        // PVT data is not supported by other protocols so nothing else to do
        // except returning received data
    return(pvt);
  }

//----------------------------------------------------------------------
/**
 * Read waypoint-data from connected device
 *
 * @exception if an error occured during access to the device
 * @return waypoints stored in device
 */
  public GarminWaypoint[] readWaypointData()
    throws GPSException
  {
    char[] buffer;
    int count = 0;

    char wpt_class;
    char color;
    char display;
    char attributes;
    int symbol;
    double lat;
    double lon;
    float alt;
    float depth;
    float dist;
    char[] state = new char[2];
    char[] country = new char[2];
    char[] desc = new char[51];

    String[][] wptdata = new String[][] {};

    GarminWaypoint[] waypoints = null;
    double latlon_factor = 180.0/Math.pow(2,31);
    
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
        while(in_stream_.available()==0)
        {
          count++;
          if (count==1000000)
            break;
        }

            // Reset delay counter and read waypoint data as available from device
        count = 0;
        buffer = getPackage();

            // Has the beginning package been received from device?
        if ((buffer[0] == Pid_Records_L001) || (buffer[0] == Pid_Records_L002))
        {
              // How many data packets will follow after beginning packet
          int length = buffer[2]+256*buffer[3];

          System.out.println("Receiving "+length+" waypoints from device");

              // Initialize waypoint buffer
          if (D[108])
            waypoints = new GarminWaypoint[length];

              // Receive waypoints from device
          for (int wpt_count = 0; wpt_count < length; wpt_count++)
          {
            buffer = getPackage();

                // Data is in data format D108
            
                // FIXXME who tells that is is in format D108?????
                // what if a device is able to send differnt waypoint
                // formats???
            if (D[108])
            {
              waypoints[wpt_count] = new GarminWaypointD108(buffer);
            }
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
    }

    return(waypoints);
  }

//----------------------------------------------------------------------
/**
 * Read route-data from connected device
 *
 * @exception if an error occured during access to the device
 * @return routes stored in device
 */
  public GarminRoute readRouteData()
  {
    char[] buffer;
    int count = 0;

    char rte_class;
    byte[] rte_subclass;
    String rte_ident;

    GarminRoute route = new GarminRoute();
    
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
        while(in_stream_.available()==0)
        {
          count++;
          if (count==1000000) break;
        }

            // Reset delay counter and read route data as available from device
        count = 0;
        buffer=getPackage();

            // Has the beginning package been received from device?
        if ((buffer[0] == Pid_Records_L001) || (buffer[0] == Pid_Records_L002))
        {
              // How many data packets will follow after beginning packet
          int length = buffer[2]+256*buffer[3];

          System.out.println("Receiving "+length+" route informations from device");

              // Initialize routedata buffer
//          if (D[108]) rtedata = new String[1+length][14];

              // Receive routes from device
          for (int i=0;i<length;i++)
          {
            buffer=getPackage();
            System.out.println("Route: "+(i+1));

                // Process received data using application protocol A201
            if (A[201])
            {
                  // Has a route header been received
              if ((buffer[0] == Pid_Rte_Hdr_L001) || (buffer[0] == Pid_Rte_Hdr_L002))
              {
                    // Data is in data format D200

                    // Data is in data format D201

                    // Data is in data format D202
                if (D[202])
                {
                  route = new GarminRouteD202(buffer);
                }
              }

                  // Has route waypoint data been received
              if ((buffer[0] == Pid_Rte_Wpt_Data_L001) || (buffer[0] == Pid_Rte_Wpt_Data_L002))
              {
                    // Data is in data format D108
                if (D[108])
                {
                  route.addRoutePoint(new GarminWaypointD108(buffer));
                }
              }

                  // Has route link data been received
              if (buffer[0] == Pid_Rte_Link_Data_L001)
              {
                    // Data is in format D210
                if (D[210])
                {
                  route.addRouteLinkData(new GarminRouteLinkTypeD210(buffer));
                }
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
      }
    }

    return(route);
  }

//----------------------------------------------------------------------
/**
 * Read track-data from connected device
 *
 * @exception if an error occured during access to the device
 * @return tracks stored in device
 */
  public String[][] readTrackData()
  {
    char[] buffer;
    int count = 0;

    char color;
    char display;
    char[] trk_ident = new char[] {};
    double lat;
    double lon;
    float time;
    float alt;
    float depth;
    char new_track;

    String[][] trkdata = new String[][] {};
    
        // Does device support waypoint transfer protocol
    if (A[300] || A[301])
    {
      try
      {
            // Request waypoint data due to link and command protocols available for device
        if (L[1] && A[10]) sendCommand(Pid_Command_Data_L001, Cmnd_Transfer_Trk_A010);

            // Wait if device transmits data or is not ready to transmit data
            // If device does not send waypoints return to calling function
        while(in_stream_.available()==0)
        {
          count++;
          if (count==1000000) break;
        }

            // Reset delay counter and read waypoint data as available from device
        count = 0;
        buffer=getPackage();

            // Has the beginning package been received from device?
        if (buffer[0] == Pid_Records_L001)
        {
              // How many data packets will follow after beginning packet
          int length = buffer[2]+256*buffer[3];

          System.out.println("Receiving "+length+" track informations from device");

              // Initialize trackdata buffer
          if (D[300]) trkdata = new String[1+length][4];
          if (D[301]) trkdata = new String[1+length][6];
          trkdata[0] = new String[] {""+length};

              // Receive tracks from device
          for (int i=0;i<length;i++)
          {
            buffer=getPackage();
            System.out.println("Track: "+(i+1));

                // Initialize character buffers
            trk_ident = new char[] {};

                // Process received data using application protocol A301
            if (A[301])
            {
                  // Has a track header been received
              if (buffer[0] == Pid_Trk_Hdr_L001)
              {
                    // Data is in data format D310
                if (D[310])
                {
                  display = buffer[2];
                  color = buffer[3];
                  for (int j=0;j<(int)buffer[1]-4;j++)
                  {
                    trk_ident[j] = buffer[4+j];
                  }

                  trkdata[i+1] = new String[] {"Header",""+display,""+color,new String(trk_ident)};
                }
              }

                  // Has track data been received
              if (buffer[0] == Pid_Trk_Data_L001)
              {
                    // Data is in format D300
                if (D[300])
                {
                  lat = GarminDataConverter.IntLEtoBE(buffer[2],buffer[3],buffer[4],buffer[5])*(180/Math.pow(2,31));
                  lon = GarminDataConverter.IntLEtoBE(buffer[6],buffer[7],buffer[8],buffer[9])*(180/Math.pow(2,31));
                  time = GarminDataConverter.FloatLEtoBE(buffer[10],buffer[11],buffer[12],buffer[13]);
                  new_track = buffer[22];

                  trkdata[i+1] = new String[] {""+lat,""+lon,""+time,""+new_track};
                }

                    // Data is in format D301
                if (D[301])
                {
                  lat = GarminDataConverter.IntLEtoBE(buffer[2],buffer[3],buffer[4],buffer[5])*(180/Math.pow(2,31));
                  lon = GarminDataConverter.IntLEtoBE(buffer[6],buffer[7],buffer[8],buffer[9])*(180/Math.pow(2,31));
                  time = GarminDataConverter.FloatLEtoBE(buffer[10],buffer[11],buffer[12],buffer[13]);
                  alt = GarminDataConverter.FloatLEtoBE(buffer[14],buffer[15],buffer[16],buffer[17]);
                  depth = GarminDataConverter.FloatLEtoBE(buffer[18],buffer[19],buffer[20],buffer[21]);
                  new_track = buffer[22];

                  trkdata[i+1] = new String[] {""+lat,""+lon,""+time,""+alt,""+depth,""+new_track};
                }
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
      }
    }

    return trkdata;
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

//----------------------------------------------------------------------
/**
 * Reads the GARMIN position-velocity-time-data from the device
 * Protected function for reading and handling data without GUI
 */
  public void printPVTData()
    throws GPSException
  {
    int loopcount = 0;
    GarminPVT pvtdata;

    if (Debug.DEBUG)
    {
      Debug.println("gpstool","start reading pvt-data from GPSDevice...");
      Debug.print("gpstool_readmessage","inputstream: "+in_stream_);
    }

    while(true && D[800])
    {
      loopcount++;
      pvtdata = readPVTData();

      System.out.println(pvtdata);

//           // Handle received data
//       if (Integer.parseInt(pvtdata[3])>=0)
//       {
//         System.out.println("Position information received:");
//         System.out.println("Latitude:  "+pvtdata[0]);
//         System.out.println("Longitude: "+pvtdata[1]);
//         System.out.println("Altitude:  "+pvtdata[2]);
//         System.out.println("Validity:  "+pvtdata[3]+"\n");
//         System.out.println("Pos.error - 2sigma:     "+pvtdata[4]);
//         System.out.println("Pos.error - horizontal: "+pvtdata[5]);
//         System.out.println("Pos.error - vertical:   "+pvtdata[6]+"\n");
//         System.out.println("Velocity - east:  "+pvtdata[7]);
//         System.out.println("Velocity - north: "+pvtdata[8]);
//         System.out.println("Velocity - up:    "+pvtdata[9]+"\n");
//         System.out.println("Time of week (s):   "+pvtdata[10]);
//         System.out.println("Difference GPS-UTC: "+pvtdata[11]);
//         System.out.println("Week number days:   "+pvtdata[12]);
//       }
//       else
//       {
//         System.out.println("No PVT data received!");
//       }
    }
  }

//----------------------------------------------------------------------
/**
 * Reads waypoints from the connected device
 * Protected function for reading and handling data without GUI
 */
  public void printWaypointData()
    throws GPSException
  {
    int length;

    if (Debug.DEBUG)
    {
      Debug.println("gpstool","start reading waypoint-data from GPSDevice...");
      Debug.print("gpstool_readmessage","inputstream: "+in_stream_);
    }

    GarminWaypoint[] waypoints = readWaypointData();
    System.out.println(waypoints.length + " Waypoints received.");

        // Handling for packets in data format D108
    if (D[108])
    {
      for (int i = 0; i < waypoints.length; i++)
      {
        System.out.println("Waypoint: "+i);
        System .out.println(waypoints[i]);
      }
    }
  }

//----------------------------------------------------------------------
/**
 * Reads routes from the connected device
 * Protected function for reading and handling data without GUI
 */
  public void printRouteData()
  {
    int length;

    if (Debug.DEBUG)
    {
      Debug.println("gpstool","start reading route-data from GPSDevice...");
      Debug.print("gpstool_readmessage","inputstream: "+in_stream_);
    }

    GarminRoute route = readRouteData();

    System.out.println(route);
  }

//----------------------------------------------------------------------
/**
 * Reads tracks from the connected device
 * Protected function for reading and handling data without GUI
 */
  public void printTrackData()
  {
    String[][] trkdata;
    int length;

    if (Debug.DEBUG)
    {
      Debug.println("gpstool","start reading track-data from GPSDevice...");
      Debug.print("gpstool_readmessage","inputstream: "+in_stream_);
    }

    trkdata = readTrackData();
    length = Integer.parseInt(trkdata[0][0]);

        // Handle received data
    if (length > 0)
    {
	    for (int i=0;i<length;i++)
	    {
            // Handle track headers
        if (trkdata[i+1][0] == "Header")
        {
          System.out.println("Track: "+trkdata[i+1][3]);
          System.out.println("Display: "+trkdata[i+1][1]+", Color: "+trkdata[i+1][2]+"\n");
          System.out.println("Trackdata:");
        }
        else
        {
          System.out.println("Trackpoint: ");

              // Handling for packets in data format D300
          if (D[300])
          {
            System.out.println("Latitude:  "+trkdata[i+1][0]);
            System.out.println("Longitude: "+trkdata[i+1][1]);
            System.out.print("Time: "+trkdata[i+1][2]);
            System.out.println(", New Track: "+trkdata[i+1][3]);
          }

              // Handling for packets in data format D301
          if (D[301])
          {
            System.out.println("Latitude:  "+trkdata[i+1][0]);
            System.out.println("Longitude: "+trkdata[i+1][1]);
            System.out.println("Altitude: "+trkdata[i+1][3]);
            System.out.print("Depth: "+trkdata[i+1][4]);
            System.out.print(", Time: "+trkdata[i+1][2]);
            System.out.println(", New Track: "+trkdata[i+1][5]);
          }
        }
	    }
    }
    else
    {
      System.out.println("No tracks received.");
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
    return(null);
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
