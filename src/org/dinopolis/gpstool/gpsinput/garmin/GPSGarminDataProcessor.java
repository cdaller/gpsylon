/***********************************************************************
 * @(#)$RCSfile$   $Revision$ $Date$
 *
 * Copyright (c) 2001-2003 Sandra Brueckler, Stefan Feitl, Christof Dallermassl
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.dinopolis.gpstool.gpsinput.GPSDevice;
import org.dinopolis.gpstool.gpsinput.GPSException;
import org.dinopolis.gpstool.gpsinput.GPSGeneralDataProcessor;
import org.dinopolis.gpstool.gpsinput.GPSPosition;
import org.dinopolis.gpstool.gpsinput.GPSPositionError;
import org.dinopolis.gpstool.gpsinput.GPSRoute;
import org.dinopolis.gpstool.gpsinput.GPSSerialDevice;
import org.dinopolis.gpstool.gpsinput.GPSTrack;
import org.dinopolis.gpstool.gpsinput.GPSTrackpoint;
import org.dinopolis.gpstool.gpsinput.GPSWaypoint;
import org.dinopolis.util.Debug;

//----------------------------------------------------------------------
/**
 * This class implements a GARMIN-Processor that is able to connect to a
 * connected GARMIN-device, retrieve its capabilities, live
 * position-velocity-time-data and waypoint/route/track-information stored in
 * the device.  Waypoint/route/track-information may also be sent to the device
 * using the functions of this class.  <P> This class works in two different
 * threads: one that sends the commands (mostly this will be the caller's
 * thread) and another, that reads packets from the garmin device. This is
 * necessary as the garmin device may send packets without request and because
 * sometimes the caller should not be locked if the device does not send
 * anything at all (and java only provides blockint read). So the strategy is
 * to send the request and then sychronize on a lock object (if we want to wait
 * for the result). The receiving thread reads the data and calls the
 * <code>fireXXXReceived</code> methods
 * (e.g. <code>fireRoutesREceived(...)</code>). In there the lock object is
 * notified and the waiting thread (if any) is woken up and reads the result
 * from a member variable (this is kind of ugly, but I did not find another
 * simple way to pass the result between the two threads (suggestions
 * welcome!)). The woken up thread returns this value then to the caller. 
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
  protected SimpleDateFormat track_date_format = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");

      /** lock used to synchronize ACK/NAK of packets from device with
       * reader thread */
  protected Object acknowledge_lock_ = new Object();
      /** helper variable to pass result (ACK/NAK) from reader thread to
       * writer thread */
  protected boolean send_success_ = false;
      /** helper variable to pass result packet id from reader thread to
       * writer thread */
  protected int send_packet_id_ = 0;

      // lock objects and result objects for synchronous calls:
  protected List result_routes_;
  protected List result_tracks_;
  protected List result_waypoints_;
  protected GarminPVT result_pvt_;
  protected GarminFlashInfo result_flash_info_;
  protected BufferedImage result_screenshot_;
  protected long result_serial_number_;
  protected GarminFile result_file_;
  protected Object waypoint_sync_request_lock_ = new Object();
  protected Object track_sync_request_lock_ = new Object();
  protected Object route_sync_request_lock_ = new Object();
  protected Object pvt_sync_request_lock_ = new Object();
  protected Object product_info_lock_ = new Object();
  protected Object screenshot_sync_request_lock_ = new Object();
  protected Object serial_number_sync_request_lock_ = new Object();
  protected Object flash_info_sync_request_lock_ = new Object();
  protected Object file_sync_request_lock_ = new Object();

      /** Listeners for the Result Packets */
  protected Vector result_listeners_;
      /** Listeners for the Route Packets */
  protected Vector route_listeners_;
      /** Listeners for the Track Packets */
  protected Vector track_listeners_;
      /** Listeners for the Waypoint Packets */
  protected Vector waypoint_listeners_;

      /** marker if it was requested to send pvt periodically */
  protected boolean send_pvt_periodically_ = false;
  
/**
 * Basic values for garmin devices
 */
  public GarminCapabilities capabilities_;
  public GarminProduct product_info_;

      /** timeout in milliseconds to wait for ACK/NAK from device (0 waits forever) */
  protected final static long ACK_TIMEOUT = 2000L;

  protected final static int MAX_TRIES = 5;
  
      /* constants to indicate which type of packets were received */
  protected final static int RECEIVED_WAYPOINTS = 1;
  protected final static int RECEIVED_TRACKS = 2;
  protected final static int RECEIVED_ROUTES = 4;

      // Definitions for DLE, ETX, ACK and NAK
  public final static int DLE = 16; // 0x10
  public final static int ETX = 3;  // 0x03
  public final static int ACK = 6;  // 0x06
  public final static int NAK = 21; // 0x15
  
/**
 * Identifiers for L000 - Basic Link Protocol
 */
  public final static int Pid_Protocol_Array = 253;  // 0xfd
  public final static int Pid_Product_Rqst   = 254;  // 0xfe
  public final static int Pid_Product_Data   = 255;  // 0xff

/**
 * Identifiers for L001 - Link Protocol 1
 */
  public final static int Pid_Unit_Id_Info        = 9;  // 0x09 // from gpsexplorer
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
  public final static int Pid_Version_Info        = 32;  // 0x20 // from gpsexplorer
  public final static int Pid_Trk_Data_L001       = 34;  // 0x22
  public final static int Pid_Wpt_Data_L001       = 35;  // 0x23
	public final static int Pid_Serial_Number       = 38;  // 0x26  // from experiment
  public final static int Pid_Communication_Speed = 49;  // 0x31  // from gpsexplorer
  public final static int Pid_Pvt_Data_L001       = 51;  // 0x33
  public final static int Pid_Display_Data_L001   = 69;  // 0x45  // from garble
  public final static int Pid_Flash_Erase_Response = 74; // 0x4a  // from gpsexplorer
  public final static int Pid_Flash_Erase_Request = 75;  // 0x4b  // from gpsexplorer
  public final static int Pid_File_Data           = 90;  // 0x5a  // from gpsexplorer
  public final static int Pid_File_Header         = 91;  // 0x5b  // from gpsexplorer
  public final static int Pid_File_Not_Exist      = 92;  // 0x5c  // from gpsexplorer
  public final static int Pid_Flash_Info          = 95;  // 0x5f  // from gpsexplorer
  public final static int Pid_Rte_Link_Data_L001  = 98;  // 0x62
  public final static int Pid_Trk_Hdr_L001        = 99;  // 0x63
  public final static int Pid_Unlock_Code_Send    = 108;  // 0x6c  // from gpsexplorer
  public final static int Pid_Unlock_Code_Response = 109;  // 0x6d  // from gpsexplorer

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
	public final static int Cmnd_Transfer_SerialNr   = 14; // 0x0e // from experiment ??????
			// voltage is untested and reportedly works on models GPS V and 48
      // does NOT work on eTrex Legend
	public final static int Cmnd_Transfer_Voltage_A010 = 17; // 0x11  // untested
  public final static int Cmnd_Transfer_Screenbitmap_A010 = 32; // 0x20
  public final static int Cmnd_Start_Pvt_Data_A010 = 49;  // 0x31
  public final static int Cmnd_Stop_Pvt_Data_A010  = 50;  // 0x32
  public final static int Cmnd_Get_Map_Flash_Info  = 63;  // 0x3f  // from gpsexplorer

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
  public final static int Pid_Request_File         = 89; // 0x59 // from gpsexplorer
	public final static int Pid_Voltage_Response     = 40; // 0x28 // from Newsgroup

	public final static int Cmnd_Transfer_Temp_Correction = 15; // 0x0f
	public final static int Pid_Temp_Correction_L001 = 39; // 0x27
      /**
       * Other Commands
       */

/**
 * Unknown Commands (all eTrex Legend):
 * sent command id 14: 
 * response: id=38,size=4,data=[90 17 52 173 ]
 *
 * sent command id 15: 
 * response: 
 * id=27 (record), holding 121 packets:
 * id=39,size=24,data=[222 255 0 0 90 242 251 105 66 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[223 255 0 0 176 134 194 117 68 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[224 255 0 0 63 58 137 129 70 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[225 255 0 0 205 237 79 141 72 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[226 255 0 0 36 130 22 153 74 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[227 255 0 0 93 178 114 224 75 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[228 255 0 0 150 226 206 39 77 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[229 255 0 0 151 243 42 111 78 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[230 255 0 0 208 35 135 182 79 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[231 255 0 0 208 52 227 253 80 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[232 255 0 0 66 132 63 69 82 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[233 255 0 0 67 149 155 140 83 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[234 255 0 0 180 228 247 211 84 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[235 255 0 0 181 245 83 27 86 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[236 255 0 0 182 6 176 98 87 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[237 255 0 0 67 169 26 39 88 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[238 255 0 0 96 13 133 235 88 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[239 255 0 0 68 186 118 110 89 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[240 255 0 0 210 92 225 50 90 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[241 255 0 0 239 192 75 247 90 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[242 255 0 0 211 109 61 122 91 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[243 255 0 0 240 209 167 62 92 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[244 255 0 0 126 116 18 3 93 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[245 255 0 0 97 33 4 134 93 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[246 255 0 0 126 133 110 74 94 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[247 255 0 0 40 123 231 139 94 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[248 255 0 0 98 50 96 205 94 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[249 255 0 0 12 40 217 14 95 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[250 255 0 0 240 212 202 145 95 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[251 255 0 0 41 140 67 211 95 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[252 255 0 0 41 140 67 211 95 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[253 255 0 0 211 129 188 20 96 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[254 255 0 0 211 129 188 20 96 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[255 255 0 0 211 129 188 20 96 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[0 0 0 0 13 57 53 86 96 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[1 0 0 0 211 129 188 20 96 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[2 0 0 0 211 129 188 20 96 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[3 0 0 0 211 129 188 20 96 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[4 0 0 0 211 129 188 20 96 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[5 0 0 0 41 140 67 211 95 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[6 0 0 0 41 140 67 211 95 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[7 0 0 0 240 212 202 145 95 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[8 0 0 0 70 223 81 80 95 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[9 0 0 0 12 40 217 14 95 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[10 0 0 0 98 50 96 205 94 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[11 0 0 0 40 123 231 139 94 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[12 0 0 0 126 133 110 74 94 56 111 65 12 37 182 187 91 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[13 0 0 0 69 206 245 8 94 56 111 65 211 109 61 122 91 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[14 0 0 0 97 33 4 134 93 56 111 65 153 182 196 56 91 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[15 0 0 0 183 43 139 68 93 56 111 65 239 192 75 247 90 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[16 0 0 0 126 116 18 3 93 56 111 65 239 192 75 247 90 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[17 0 0 0 154 199 32 128 92 56 111 65 181 9 211 181 90 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[18 0 0 0 240 209 167 62 92 56 111 65 210 92 225 50 90 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[19 0 0 0 12 37 182 187 91 56 111 65 210 92 225 50 90 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[20 0 0 0 211 109 61 122 91 56 111 65 238 175 239 175 89 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[21 0 0 0 239 192 75 247 90 56 111 65 10 3 254 44 89 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[22 0 0 0 181 9 211 181 90 56 111 65 39 86 12 170 88 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[23 0 0 0 210 92 225 50 90 56 111 65 39 86 12 170 88 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[24 0 0 0 238 175 239 175 89 56 111 65 39 86 12 170 88 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[25 0 0 0 68 186 118 110 89 56 111 65 153 179 161 229 87 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[26 0 0 0 96 13 133 235 88 56 111 65 182 6 176 98 87 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[27 0 0 0 125 96 147 104 88 56 111 65 124 79 55 33 87 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[28 0 0 0 67 169 26 39 88 56 111 65 152 162 69 158 86 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[29 0 0 0 96 252 40 164 87 56 111 65 238 172 204 92 86 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[30 0 0 0 124 79 55 33 87 56 111 65 181 245 83 27 86 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[31 0 0 0 210 89 190 223 86 56 111 65 123 62 219 217 85 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[32 0 0 0 238 172 204 92 86 56 111 65 209 72 98 152 85 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[33 0 0 0 181 245 83 27 86 56 111 65 152 145 233 86 85 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[34 0 0 0 209 72 98 152 85 56 111 65 238 155 112 21 85 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[35 0 0 0 152 145 233 86 85 56 111 65 180 228 247 211 84 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[36 0 0 0 238 155 112 21 85 56 111 65 180 228 247 211 84 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[37 0 0 0 10 239 126 146 84 56 111 65 10 239 126 146 84 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[38 0 0 0 208 55 6 81 84 56 111 65 208 55 6 81 84 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[39 0 0 0 38 66 141 15 84 56 111 65 208 55 6 81 84 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[40 0 0 0 237 138 20 206 83 56 111 65 38 66 141 15 84 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[41 0 0 0 67 149 155 140 83 56 111 65 38 66 141 15 84 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[42 0 0 0 9 222 34 75 83 56 111 65 208 55 6 81 84 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[43 0 0 0 95 232 169 9 83 56 111 65 38 66 141 15 84 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[44 0 0 0 37 49 49 200 82 56 111 65 38 66 141 15 84 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[45 0 0 0 123 59 184 134 82 56 111 65 38 66 141 15 84 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[46 0 0 0 123 59 184 134 82 56 111 65 38 66 141 15 84 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[47 0 0 0 66 132 63 69 82 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[48 0 0 0 66 132 63 69 82 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[49 0 0 0 66 132 63 69 82 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[50 0 0 0 152 142 198 3 82 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[51 0 0 0 66 132 63 69 82 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[52 0 0 0 66 132 63 69 82 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[53 0 0 0 66 132 63 69 82 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[54 0 0 0 123 59 184 134 82 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[55 0 0 0 123 59 184 134 82 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[56 0 0 0 37 49 49 200 82 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[57 0 0 0 95 232 169 9 83 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[58 0 0 0 9 222 34 75 83 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[59 0 0 0 67 149 155 140 83 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[60 0 0 0 237 138 20 206 83 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[61 0 0 0 208 55 6 81 84 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[62 0 0 0 180 228 247 211 84 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[63 0 0 0 152 145 233 86 85 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[64 0 0 0 123 62 219 217 85 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[65 0 0 0 238 172 204 92 86 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[66 0 0 0 124 79 55 33 87 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[67 0 0 0 153 179 161 229 87 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[68 0 0 0 39 86 12 170 88 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[69 0 0 0 68 186 118 110 89 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[70 0 0 0 210 92 225 50 90 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[71 0 0 0 211 109 61 122 91 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[72 0 0 0 212 126 153 193 92 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[73 0 0 0 69 206 245 8 94 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[74 0 0 0 70 223 81 80 95 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[75 0 0 0 13 57 53 86 96 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[76 0 0 0 184 63 10 223 97 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[77 0 0 0 185 80 102 38 99 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[78 0 0 0 42 160 194 109 100 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[79 0 0 0 213 166 151 246 101 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[80 0 0 0 214 183 243 61 103 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[81 0 0 0 100 107 186 73 105 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[82 0 0 0 243 30 129 85 107 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[83 0 0 0 17 148 71 97 109 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[84 0 0 0 159 71 14 109 111 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * id=39,size=24,data=[85 0 0 0 46 251 212 120 113 56 111 65 102 159 26 47 56 56 111 65 0 0 0 0 ]
 * ends with transfer_complete (id 12)
 * result is not a screenshot, as changing display does yield exactly the
 * same data!
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
                  +" (id="+product_info_.getProductId()
                  +") V"+(product_info_.getProductSoftware()/100.0);
    Vector capabilities = capabilities_.getProductCapabilities();
    StringBuffer capabilities_string = new StringBuffer("Supported Formats: ");
    for(int index=0; index < capabilities.size()-1; index++)
    {
      capabilities_string.append(capabilities.get(index)).append(", ");
    }
        // add last:
    capabilities_string.append(capabilities.get(capabilities.size()-1));
		long serial_number = -1;
		try
		{
			serial_number = getSerialNumber(1000);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		String[] info;
		if(serial_number != -1)
		{
			String ser_num_str = "Serial Nr. "+serial_number;
			info = new String[] {name, capabilities_string.toString(),ser_num_str};
		}
		else
			info = new String[] {name, capabilities_string.toString()};

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
    throws GPSException
  {
    if(!capabilities_.hasCapability("A100"))
      throw new UnsupportedOperationException("Garmin Device does not support waypoint transfer");
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
 * Write a list of waypoints to the gps device. This call blocks until
 * all waypoints were sent!
 * @throws UnsupportedOperationException if the operation is not
 * supported by the gps device or by the protocol used.
 * @throws GPSException if the operation threw an exception
 * (e.g. communication problem).
 *
 * @see GPSWaypoint
 */
  public void setWaypoints(List waypoints) 
    throws GPSException, UnsupportedOperationException
  {
    waitTillReady();

    if(!capabilities_.hasCapability("A100"))
      throw new UnsupportedOperationException("Garmin Device does not support waypoint transfer");

    int num_packets=waypoints.size();

    int packet_count = 1;
    fireProgressActionStart(SETWAYPOINTS,1,num_packets+1);

    GarminPacket records=new GarminPacket();
    if (capabilities_.hasCapability("L1"))
      records.setPacketId(Pid_Records_L001);
    else
      records.setPacketId(Pid_Records_L002);
    records.initializeData(2);
    records.setNextAsWord(num_packets);
//    System.out.println("RECORDS " + records);

    GarminPacket xfer_cmplt=new GarminPacket();
    if (capabilities_.hasCapability("L1"))
      xfer_cmplt.setPacketId(Pid_Xfer_Cmplt_L001);
    else
      xfer_cmplt.setPacketId(Pid_Xfer_Cmplt_L002);
    xfer_cmplt.initializeData(2);
    if (capabilities_.hasCapability("A10"))
      xfer_cmplt.setNextAsWord(Cmnd_Transfer_Wpt_A010);
    if (capabilities_.hasCapability("A11"))
      xfer_cmplt.setNextAsWord(Cmnd_Transfer_Wpt_A011);

    putPacket(records);

    for (int i=0;i<waypoints.size();i++)
    {
      GarminPacket pack=new GarminPacket();
      if(Debug.DEBUG)
        Debug.println("gps_garmin","Sending waypoint "+waypoints.get(i));
//       GarminWaypointD108 wpt = new GarminWaypointD108((GPSWaypoint)waypoints.get(i));
//       System.out.println("D108 waypoint:"+wpt);
//       System.out.println("GarminPacket from waypoint: "+wpt.toGarminPacket(12));

      if (capabilities_.hasCapability("L1"))
      {
        if (capabilities_.hasCapability("D100"))
          pack=new GarminWaypointD100((GPSWaypoint)waypoints.get(i)).toGarminPacket(Pid_Wpt_Data_L001);
        else if (capabilities_.hasCapability("D101"))
          pack=new GarminWaypointD101((GPSWaypoint)waypoints.get(i)).toGarminPacket(Pid_Wpt_Data_L001);
        else if (capabilities_.hasCapability("D102"))
          pack=new GarminWaypointD102((GPSWaypoint)waypoints.get(i)).toGarminPacket(Pid_Wpt_Data_L001);
        else if (capabilities_.hasCapability("D103"))
          pack=new GarminWaypointD103((GPSWaypoint)waypoints.get(i)).toGarminPacket(Pid_Wpt_Data_L001);
        else if (capabilities_.hasCapability("D107"))
          pack=new GarminWaypointD107((GPSWaypoint)waypoints.get(i)).toGarminPacket(Pid_Wpt_Data_L001);
        else if (capabilities_.hasCapability("D108"))
          pack=new GarminWaypointD108((GPSWaypoint)waypoints.get(i)).toGarminPacket(Pid_Wpt_Data_L001);
        else if (capabilities_.hasCapability("D109"))
          pack=new GarminWaypointD109((GPSWaypoint)waypoints.get(i)).toGarminPacket(Pid_Wpt_Data_L001);
        else
          System.err.println("ERROR: no possible waypoint packet found!");
      }
      else if (capabilities_.hasCapability("L2"))
      {
        if (capabilities_.hasCapability("D100"))
          pack=new GarminWaypointD100((GPSWaypoint)waypoints.get(i)).toGarminPacket(Pid_Wpt_Data_L002);
        else if (capabilities_.hasCapability("D101"))
          pack=new GarminWaypointD101((GPSWaypoint)waypoints.get(i)).toGarminPacket(Pid_Wpt_Data_L002);
        else if (capabilities_.hasCapability("D102"))
          pack=new GarminWaypointD102((GPSWaypoint)waypoints.get(i)).toGarminPacket(Pid_Wpt_Data_L002);
        else if (capabilities_.hasCapability("D103"))
          pack=new GarminWaypointD103((GPSWaypoint)waypoints.get(i)).toGarminPacket(Pid_Wpt_Data_L002);
        else if (capabilities_.hasCapability("D107"))
          pack=new GarminWaypointD107((GPSWaypoint)waypoints.get(i)).toGarminPacket(Pid_Wpt_Data_L002);
        else if (capabilities_.hasCapability("D108"))
          pack=new GarminWaypointD108((GPSWaypoint)waypoints.get(i)).toGarminPacket(Pid_Wpt_Data_L002);
        else if (capabilities_.hasCapability("D109"))
          pack=new GarminWaypointD109((GPSWaypoint)waypoints.get(i)).toGarminPacket(Pid_Wpt_Data_L002);
        else
          System.err.println("ERROR: no possible waypoint packet found!");
      }
      else
        System.err.println("ERROR: no possible waypoint protocol found!");

      if(packet_count % 10 == 0)
        fireProgressActionProgress(SETWAYPOINTS,packet_count);
      putPacket(pack);
      packet_count++;
    }

    fireProgressActionProgress(SETWAYPOINTS,num_packets);
    putPacket(xfer_cmplt);
    fireProgressActionEnd(SETWAYPOINTS);
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
    if(!capabilities_.hasCapability("A200") && !capabilities_.hasCapability("A201"))
      throw new UnsupportedOperationException("Garmin Device does not support route transfer");
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
 * Write a list of tracks to the gps device. This call blocks until
 * all tracks were sent!
 *
 * @param routes a list of route objects
 * @throws UnsupportedOperationException if the operation is not
 * supported by the gps device or by the protocol used.
 * @throws GPSException if the operation threw an exception
 * (e.g. communication problem).
 * @see GPSRoute
 */
  public void setRoutes(List routes)
    throws GPSException, UnsupportedOperationException
  {
    waitTillReady();

    if(!capabilities_.hasCapability("A200") && !capabilities_.hasCapability("A201"))
      throw new UnsupportedOperationException("Garmin Device does not support route transfer");

    int num_packets=routes.size(); // count route headers

		boolean add_link_packets = capabilities_.hasCapability("D210");
		int num_points;
    for (int route_count=0; route_count < routes.size(); route_count++)
    {
			num_points = ((GPSRoute)routes.get(route_count)).getWaypoints().size();
      num_packets += num_points;
			if(add_link_packets)
				num_packets += num_points-1;
    }

		System.out.println("Sending "+num_packets+" packets.");

    int packet_count = 1;
    fireProgressActionStart(SETROUTES,1,num_packets+1);

    GarminPacket records=new GarminPacket();
    if (capabilities_.hasCapability("L1"))
      records.setPacketId(Pid_Records_L001);
    else
      records.setPacketId(Pid_Records_L002);
    records.initializeData(2);
    records.setNextAsWord(num_packets);
//    System.out.println("RECORDS " + records);

    GarminPacket xfer_cmplt=new GarminPacket();
    if (capabilities_.hasCapability("L1"))
      xfer_cmplt.setPacketId(Pid_Xfer_Cmplt_L001);
    else
      xfer_cmplt.setPacketId(Pid_Xfer_Cmplt_L002);
    xfer_cmplt.initializeData(2);
    if (capabilities_.hasCapability("A10"))
      xfer_cmplt.setNextAsWord(Cmnd_Transfer_Trk_A010);

    putPacket(records);

		GarminRouteLinkD210 link_packet_d210 = new GarminRouteLinkD210();
		link_packet_d210.setClassId(3);

		link_packet_d210.setSubclass(new byte[] {0,0,0,0,0,0,
																							-1,-1,-1,-1,-1,-1,
																							-1,-1,-1,-1,-1,-1});
//		System.out.println("link packet to send: "+link_packet_d210);
		GarminPacket link_packet = link_packet_d210.toGarminPacket(Pid_Rte_Link_Data_L001);

    for (int route_count=0; route_count < routes.size(); route_count++)
    {
      GarminPacket pack=new GarminPacket();
      if(Debug.DEBUG)
        Debug.println("gps_garmin","Sending route "+routes.get(route_count));

          // Route header(s)
      if (capabilities_.hasCapability("L1"))
      {
        if (capabilities_.hasCapability("D200"))
          pack=new GarminRouteD200((GPSRoute)routes.get(route_count)).toGarminPacket(Pid_Rte_Hdr_L001);
        else if (capabilities_.hasCapability("D201"))
          pack=new GarminRouteD201((GPSRoute)routes.get(route_count)).toGarminPacket(Pid_Rte_Hdr_L001);
        else if (capabilities_.hasCapability("D202"))
          pack=new GarminRouteD202((GPSRoute)routes.get(route_count)).toGarminPacket(Pid_Rte_Hdr_L001);
        else if (!capabilities_.hasCapability("A300"))
          System.err.println("ERROR: no possible route header packet found!");
      }
      else if (capabilities_.hasCapability("L2"))
      {
        if (capabilities_.hasCapability("D200"))
          pack=new GarminRouteD200((GPSRoute)routes.get(route_count)).toGarminPacket(Pid_Rte_Hdr_L002);
        else if (capabilities_.hasCapability("D201"))
          pack=new GarminRouteD201((GPSRoute)routes.get(route_count)).toGarminPacket(Pid_Rte_Hdr_L002);
        else if (capabilities_.hasCapability("D202"))
          pack=new GarminRouteD202((GPSRoute)routes.get(route_count)).toGarminPacket(Pid_Rte_Hdr_L002);
        else if (!capabilities_.hasCapability("A300"))
          System.err.println("ERROR: no possible route header packet found!");
      }
      else
        System.err.println("ERROR: no possible route header protocol found!");

      if(packet_count % 10 == 0)
        fireProgressActionProgress(SETROUTES,packet_count);
      putPacket(pack);
//			System.out.println("sending packet: "+pack);
      packet_count++;

			GPSRoute route = (GPSRoute)routes.get(route_count);
			List waypoints = route.getWaypoints();
			int waypoint_number = waypoints.size();
			GPSWaypoint waypoint;
          // Route points
      for (int waypoint_count=0; waypoint_count < waypoint_number;waypoint_count++)
      {
        waypoint = (GPSWaypoint)waypoints.get(waypoint_count);

				if (capabilities_.hasCapability("L1"))
				{
					if (capabilities_.hasCapability("D100"))
						pack=new GarminWaypointD100(waypoint).toGarminPacket(Pid_Rte_Wpt_Data_L001);
					else if (capabilities_.hasCapability("D101"))
						pack=new GarminWaypointD101(waypoint).toGarminPacket(Pid_Rte_Wpt_Data_L001);
					else if (capabilities_.hasCapability("D102"))
						pack=new GarminWaypointD102(waypoint).toGarminPacket(Pid_Rte_Wpt_Data_L001);
					else if (capabilities_.hasCapability("D103"))
						pack=new GarminWaypointD103(waypoint).toGarminPacket(Pid_Rte_Wpt_Data_L001);
					else if (capabilities_.hasCapability("D107"))
						pack=new GarminWaypointD107(waypoint).toGarminPacket(Pid_Rte_Wpt_Data_L001);
					else if (capabilities_.hasCapability("D108"))
						pack=new GarminWaypointD108(waypoint).toGarminPacket(Pid_Rte_Wpt_Data_L001);
					else if (capabilities_.hasCapability("D109"))
						pack=new GarminWaypointD109(waypoint).toGarminPacket(Pid_Rte_Wpt_Data_L001);
					else
						System.err.println("ERROR: no possible waypoint packet found!");
				}
				else if (capabilities_.hasCapability("L2"))
				{
					if (capabilities_.hasCapability("D100"))
						pack=new GarminWaypointD100(waypoint).toGarminPacket(Pid_Rte_Wpt_Data_L002);
					else if (capabilities_.hasCapability("D101"))
						pack=new GarminWaypointD101(waypoint).toGarminPacket(Pid_Rte_Wpt_Data_L002);
					else if (capabilities_.hasCapability("D102"))
						pack=new GarminWaypointD102(waypoint).toGarminPacket(Pid_Rte_Wpt_Data_L002);
					else if (capabilities_.hasCapability("D103"))
						pack=new GarminWaypointD103(waypoint).toGarminPacket(Pid_Rte_Wpt_Data_L002);
					else if (capabilities_.hasCapability("D107"))
						pack=new GarminWaypointD107(waypoint).toGarminPacket(Pid_Rte_Wpt_Data_L002);
					else if (capabilities_.hasCapability("D108"))
						pack=new GarminWaypointD108(waypoint).toGarminPacket(Pid_Rte_Wpt_Data_L002);
					else if (capabilities_.hasCapability("D109"))
						pack=new GarminWaypointD109(waypoint).toGarminPacket(Pid_Rte_Wpt_Data_L002);
					else
						System.err.println("ERROR: no possible waypoint packet found!");
        }
        else
          System.err.println("ERROR: no possible route point protocol found!");

        if(packet_count % 10 == 0)
          fireProgressActionProgress(SETROUTES,packet_count);
        putPacket(pack);
//				System.out.println("sending packet: "+pack);

				if((waypoint_count < waypoint_number-1) && (capabilities_.hasCapability("D210")))
				{
//					System.out.println("XXXX sending link:");
          putPacket(link_packet);  // send always the same link packet
					link_packet.reset(); // reset put/get index inside packet
				}

        packet_count++;
      }
    }

    fireProgressActionProgress(SETROUTES,num_packets);
    putPacket(xfer_cmplt);
//		System.out.println("sending packet: "+xfer_cmplt);
    fireProgressActionEnd(SETROUTES);
  }

//--------------------------------------------------------------------------------
/**
 * Get a list of tracks from the gps device. This call blocks until
 * something is received!
 * @return a list of <code>GPSTrack</code> objects.
 *
 * @throws UnsupportedOperationException if the operation is not
 * supported by the gps device or by the protocol used.
 * @throws GPSException if the operation threw an exception
 * (e.g. communication problem).
 * @see GPSTrack
 */
  public List getTracks()
    throws GPSException, UnsupportedOperationException
  {
    if(!capabilities_.hasCapability("A300") && !capabilities_.hasCapability("A301"))
      throw new UnsupportedOperationException("Garmin Device does not support track transfer");
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
 * Write a list of tracks to the gps device. This call blocks until
 * all tracks were sent!
 *
 * @param tracks a list of tracks to transfer.
 * @throws UnsupportedOperationException if the operation is not
 * supported by the gps device or by the protocol used.
 * @throws GPSException if the operation threw an exception
 * (e.g. communication problem).
 * @see GPSRoute
 */
  public void setTracks(List tracks)
    throws GPSException, UnsupportedOperationException
  {
    waitTillReady();

    if(!capabilities_.hasCapability("A300") && !capabilities_.hasCapability("A301"))
      throw new UnsupportedOperationException("Garmin Device does not support track transfer");

    int num_packets=tracks.size();
    for (int track_count=0 ; track_count < tracks.size(); track_count++)
    {
      num_packets+=((GPSTrack)tracks.get(track_count)).getWaypoints().size();
    }

    int packet_count = 1;
    fireProgressActionStart(SETTRACKS,1,num_packets+1);

    GarminPacket records=new GarminPacket();
    if (capabilities_.hasCapability("L1"))
      records.setPacketId(Pid_Records_L001);
    else
      records.setPacketId(Pid_Records_L002);
    records.initializeData(2);
    records.setNextAsWord(num_packets);
//    System.out.println("RECORDS " + records);

    GarminPacket xfer_cmplt=new GarminPacket();
    if (capabilities_.hasCapability("L1"))
      xfer_cmplt.setPacketId(Pid_Xfer_Cmplt_L001);
    else
      xfer_cmplt.setPacketId(Pid_Xfer_Cmplt_L002);
    xfer_cmplt.initializeData(2);
    if (capabilities_.hasCapability("A10"))
      xfer_cmplt.setNextAsWord(Cmnd_Transfer_Trk_A010);

    putPacket(records);

    for (int track_count=0; track_count<tracks.size(); track_count++)
    {
      GarminPacket pack=new GarminPacket();
      if(Debug.DEBUG)
        Debug.println("gps_garmin","Sending track "+tracks.get(track_count));

          // Track header(s)
      if (capabilities_.hasCapability("L1"))
      {
        if (capabilities_.hasCapability("D310"))
          pack=new GarminTrackD310((GPSTrack)tracks.get(track_count)).toGarminPacket(Pid_Trk_Hdr_L001);
        else if (!capabilities_.hasCapability("A300"))
          System.err.println("ERROR: no possible track header packet found!");

      }
      else
        System.err.println("ERROR: no possible track header protocol found!");

      if(packet_count % 10 == 0)
        fireProgressActionProgress(SETTRACKS,packet_count);
      putPacket(pack);
      packet_count++;

          // Track points
			GPSTrack current_track = (GPSTrack)tracks.get(track_count);
			System.out.println(current_track.getIdentification());
			List waypoints = current_track.getWaypoints();
			int waypoint_number = waypoints.size();
			GPSTrackpoint trackpoint;
      for (int j=0; j < waypoint_number; j++)
      {
        trackpoint = (GPSTrackpoint)waypoints.get(j);

        if (capabilities_.hasCapability("L1"))
        {
          if (capabilities_.hasCapability("D300"))
            pack=new GarminTrackpointD300(trackpoint).toGarminPacket(Pid_Trk_Data_L001);
          else if (capabilities_.hasCapability("D301"))
					{
            pack=new GarminTrackpointD301(trackpoint).toGarminPacket(Pid_Trk_Data_L001);
					}
          else
            System.err.println("ERROR: no possible track point packet found!");
        }
        else
          System.err.println("ERROR: no possible track point protocol found!");

        if(packet_count % 10 == 0)
          fireProgressActionProgress(SETTRACKS,packet_count);
        putPacket(pack);
        packet_count++;
      }
    }

    fireProgressActionProgress(SETTRACKS,num_packets);
    putPacket(xfer_cmplt);
    fireProgressActionEnd(SETTRACKS);
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


//--------------------------------------------------------------------------------
/**
 * Get the serial number of the gpsdevice. This call blocks until
 * something is received!
 * @return serial number of the gps device
 *
 * @throws UnsupportedOperationException if the operation is not
 * supported by the gps device or by the protocol used.
 * @throws GPSException if the operation threw an exception
 * (e.g. communication problem).
 */
	public long getSerialNumber()
		throws GPSException
	{
    try
    {
      return(getSerialNumber(0L));
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
 * @param request Request to be sent to the device.
 * @param cmd Command to be sent to the device.
 * @param timeout milliseconds to wait at maximum until the packet
 * must be acknowledged by the device.
 */
  protected boolean sendCommand(int request, int cmd, long timeout)
    throws IOException
  {
    if(Debug.DEBUG)
      Debug.println("gps_garmin_packet","Sending request "+request+"/"+cmd);
    GarminPacket garmin_packet = new GarminPacket(request,2);
    garmin_packet.put(cmd);
    garmin_packet.put(0);
    putPacket(garmin_packet,timeout);
    return(send_success_);
  }

//----------------------------------------------------------------------
/**
 * Send request to GARMIN-Device.
 *
 * @param request Request to be sent to the device.
 * @param timeout milliseconds to wait at maximum until the packet
 * must be acknowledged by the device.
 */
  protected boolean sendCommand(int request, long timeout)
    throws IOException
  {
    if(Debug.DEBUG)
      Debug.println("gps_garmin_packet","Sending request "+request);
    GarminPacket garmin_packet = new GarminPacket(request,0);
    putPacket(garmin_packet,timeout);
    return(send_success_);
  }

//----------------------------------------------------------------------
/**
 * Send request/command to GARMIN-Device. The command is sent to the
 * device, no answer is read.
 *
 * @param request Request to be sent to the device.
 * @param cmd Command to be sent to the device.
 */
  protected void sendCommandAsync(int request, int cmd)
    throws IOException
  {
    if(Debug.DEBUG)
      Debug.println("gps_garmin_packet","Sending request async "+request+"/"+cmd);
    GarminPacket garmin_packet = new GarminPacket(request,2);
    garmin_packet.put(cmd);
    garmin_packet.put(0);
    putPacketAsync(garmin_packet);
  }

//----------------------------------------------------------------------
/**
 * Send request/command to GARMIN-Device. The command is sent to the
 * device, no answer is read.
 *
 * @param request Request to be sent to the device.
 */
  protected void sendCommandAsync(int request)
    throws IOException
  {
    if(Debug.DEBUG)
      Debug.println("gps_garmin_packet","Sending request async "+request);
    GarminPacket garmin_packet = new GarminPacket(request,0);
    putPacketAsync(garmin_packet);
  }

//----------------------------------------------------------------------
/**
 * Writes a packet to the garmin device and returns
 * immediately. Better use {@link #putPacket(GarminPacket)}.
 * @param garmin_packet the packet to send.
 */
  protected void putPacketAsync(GarminPacket garmin_packet)
  {
    try
    {
      if(Debug.DEBUG)
			{
        Debug.println("gps_garmin_packet","Sending packet async "+garmin_packet.getPacketId());
				if(Debug.isEnabled("gps_garmin_packet_detail"))
					Debug.println("gps_garmin_packet_detail","send packet details: "+garmin_packet);
			}
          // packet header
      out_stream_.write(DLE);
      out_stream_.write(garmin_packet.getPacketId());
      int packet_size = garmin_packet.getPacketSize();
      if(packet_size == DLE)
        out_stream_.write(DLE);
      out_stream_.write(packet_size);

          // packet data
      int data;
      for(int index=0; index < packet_size; index++)
      {
        data = garmin_packet.get();
        if(data == DLE)
          out_stream_.write(DLE);
        out_stream_.write(data);
      }

          // checksum and end markers
      byte checksum = garmin_packet.calcChecksum();
      if(checksum == DLE)
        out_stream_.write(DLE);
      out_stream_.write(checksum);

      out_stream_.write(DLE);
      out_stream_.write(ETX);
      out_stream_.flush();
      
          // inform listeners:
      String buffer_string = "sent: "+garmin_packet.toString()+"\n";
      fireRawDataReceived(buffer_string.toCharArray(),0,buffer_string.length());
    }
    catch(IOException ioe)
    {
      ioe.printStackTrace();
    }
  }

//----------------------------------------------------------------------
/**
 * Writes a packet to the garmin device and waits for the result (ACK
 * or NAK). If sending was not successfull, resend packet.
 * @param garmin_packet the packet to send.
 */
  protected void putPacket(GarminPacket garmin_packet)
  {
    putPacket(garmin_packet,0L);
  }

//----------------------------------------------------------------------
/**
 * Writes a packet to the garmin device and waits for the result (ACK
 * or NAK). If sending was not successfull, resend packet.
 * @param garmin_packet the packet to send.
 * @param timeout time in milliseconds to wait maximum (0 = forever).
 */
  protected void putPacket(GarminPacket garmin_packet, long timeout)
  {
    do
    {
      synchronized(acknowledge_lock_)
      {
        send_success_ = false;
        send_packet_id_ = 0;
//        System.err.println("Sending packet in putPacket()");
        putPacketAsync(garmin_packet);
        try
        {
//          System.err.println("waiting for ACK");
          acknowledge_lock_.wait(timeout);
        }
        catch(InterruptedException ignore){}
//        System.err.println("after waiting for ACK");
      }
    }
    while(send_success_ && (send_packet_id_ == garmin_packet.getPacketId()));
  }
  
//----------------------------------------------------------------------
/**
 * Read a packet transmitted by the GARMIN-Device.
 * This method is partly taken from the garble project (thanks to ???)
 * @return a GarminPacket or null, if an error occured.
 */
  protected GarminPacket getPacket()
  {
    try
    {
      int bytes_scanned = 0;
      int packet_id;

      while(true)
      {
        packet_id = in_stream_.read();
        watch_dog_.startWatching();   // start watchdog after first byte arrived
        watch_dog_.reset();
        bytes_scanned++;
        if(packet_id == DLE)
        {
              // could be it, but could be dle in chunk of leftover packet...
          packet_id = in_stream_.read();
          watch_dog_.reset();
          bytes_scanned++;
              // valid packet start dle is never followed by dle or etx
          if (packet_id == DLE)
          {
                // dle stuffing in packet data, ignore...
            continue;
          }
          if (packet_id == ETX)
          {
                // end of old packet frame, ignore...
            continue;
          } 
          break;
        }
      }
      
      watch_dog_.setPacketId(packet_id);
      
          // now, the packet starts:
      int data = in_stream_.read();
      watch_dog_.reset();
      if (data == DLE)
      {
        data = in_stream_.read();
        watch_dog_.reset();
        if (data != DLE)
        {
          if (Debug.DEBUG) 
            Debug.println("gps_garmin_packet","missing DLE stuffing in packet size");
          sendCommandAsync(NAK,packet_id);
          watch_dog_.stopWatching();
          return(null);
        }
      }
      int packet_size = (data & 0xff);
      GarminPacket garmin_packet = new GarminPacket(packet_id, packet_size);
      if (Debug.DEBUG) 
        Debug.println("gps_garmin_packet","receiving packet id: "
                      +packet_id+" size: "+packet_size);
      
//      System.out.println("Reading data: ");
      for (int data_index = 0; data_index < packet_size; data_index++)
      {
	
        data = in_stream_.read();
        watch_dog_.reset();
//	System.out.print(data_index+":"+data+ " ");
        garmin_packet.put(data);
        if (data == DLE)
        {
              // check for and ignore correct DLE stuffing byte
          data = in_stream_.read();
          watch_dog_.reset();
          if (data != DLE)
          {
            if (Debug.DEBUG) 
              Debug.println("gps_garmin_packet","missing DLE stuffing in packet data");
            sendCommandAsync(NAK,packet_id);
            watch_dog_.stopWatching();
            return(null);
          }
        }
      }
//      System.out.println("\ndata read.");
      
      byte packet_checksum = (byte)in_stream_.read();
      watch_dog_.reset();
      if (packet_checksum == DLE)
      {
        packet_checksum = (byte)in_stream_.read();
        watch_dog_.reset();
        if (packet_checksum != DLE)
        {
          if (Debug.DEBUG)
            Debug.println("gps_garmin_packet","missing DLE stuffing in packet checksum");
          sendCommandAsync(NAK,packet_id);
          watch_dog_.stopWatching();
          return(null);
        }
      }
//      System.out.println("checksum : "+packet_checksum);

      int calc_checksum = garmin_packet.calcChecksum();
      if (calc_checksum != packet_checksum)
      {
        if (Debug.DEBUG)
          Debug.println("gps_garmin_packet","bad checksum (is "+calc_checksum
                        +" should be "+packet_checksum);
        sendCommandAsync(NAK,packet_id);
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
          if(Debug.isEnabled("gps_garmin_packet"))
          {
            Debug.println("gps_garmin_packet","bad packet framing");
            Debug.println("gps_garmin_packet","id is " + packet_id);
            Debug.println("gps_garmin_packet","size is " + packet_size);
            Debug.println("gps_garmin_packet","data is: ");
            for (int i = 0; i < packet_size; i++) {
              Debug.print("gps_garmin_packet",garmin_packet.get() + " ");
            }
            Debug.println("gps_garmin_packet","\nchecksum is " + packet_checksum);
            Debug.println("gps_garmin_packet","DLE byte is " + dle);
            Debug.println("gps_garmin_packet","ETX byte is " + etx);
          }
        }
        sendCommandAsync(NAK,packet_id);
        watch_dog_.stopWatching();
        return(null);
      }
	
          // if we got this far, we got the packet ok, so send ACK
          // (not for ACK/NAK packets)
      if((packet_id != ACK) && (packet_id != NAK))
        sendCommandAsync(ACK,packet_id);
      watch_dog_.stopWatching();

          // inform raw data listeners:
      String buffer_string = "received: "+garmin_packet.toString()+"\n";
      fireRawDataReceived(buffer_string.toCharArray(),0,buffer_string.length());
      
      return (garmin_packet); 
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
    throws IOException, GPSException
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
				// added by
				// Marc Rechte: begin 
				// If not gotten within time, the device may not be able to report its
				// capabilities, lets use the product id to find out:
			if (product_info_ != null && capabilities_ == null) 
				capabilities_ = new GarminCapabilities(product_info_);
				// Marc Rechte: end
    return(product_info_);
  }

//----------------------------------------------------------------------
/**
 * Returns the capablitilites of the garmin product.
 *
 * @return the capablitilites of the garmin product.
 */
  protected GarminCapabilities getGarminCapabilities(long timeout)
    throws IOException, GPSException
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
 * Returns the serial number of the gps device. This method
 * blocks until the tracks are read or the timeout (in milliseconds)
 * is reached.
 *
 * @param timeout in milliseconds or 0 to wait forever.
 * @return the serial number of the device or -1  if the timeout was reached.
 */
  public long getSerialNumber(long timeout)
    throws IOException
  {
    synchronized(serial_number_sync_request_lock_)
    {
      result_serial_number_ = -1;
      requestSerialNumber();
      try
      {
        serial_number_sync_request_lock_.wait(timeout);
      }
      catch(InterruptedException ignore){}
    }
    return(result_serial_number_);
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

  private void getMap(GarminMapDescription map_description, long timeout)
    throws IOException
  {
    String[] suffices = new String[] { "TRE", "LBL" ,  "RGN", "NET", "NOD", "SRT" };
    for(int suffix_index = 0; suffix_index < suffices.length; suffix_index++)
    {
      GarminFile map = getMapFile(map_description,suffices[suffix_index],timeout);
    }
  }

  private GarminFile getMapFile(GarminMapDescription map_description,String suffix, long timeout)
    throws IOException, FileNotFoundException
  {
    GarminFile map = null;
    try
    {
      map = getFile(map_description.getImageFileName()+".TRE", timeout);
      System.out.println("Successfully downloaded map by using the ImageFileName: "
                         +map_description.getMapName());
    }
    catch(FileNotFoundException fnfe)
    {
      map = getFile(map_description.getMapNumberFileName()+".TRE", timeout);
      System.out.println("Successfully downloaded map by using mapnumberfilename: "
                         +map_description.getMapName());
    }
    return(map);
  }
  
  private List getMaps(long timeout)
    throws IOException
  {
    GarminFile map_dir = getFile("MAPSOURC.MPS",timeout);
    System.out.println("getMaps:" + map_dir);
    Vector maps_in_unit = new Vector();
    int has_next = map_dir.getNextAsByte();
    while(has_next == 76) // 0x4c
    {
      int length = map_dir.getNextAsWord();  
      int product_number = (int)map_dir.getNextAsLongWord(); // software id
      int img_number = (int)map_dir.getNextAsLongWord();
      String map_type = map_dir.getNextAsString();
      String map_name = map_dir.getNextAsString();
      String map_area = map_dir.getNextAsString();
      int map_number = (int)map_dir.getNextAsLongWord();
      map_dir.getNextAsLongWord(); // ???

      GarminMapDescription map_description = new GarminMapDescription();
      map_description.setMapLength(length);
      map_description.setMapProductNumber(product_number);
      map_description.setImageNumber(img_number);
      map_description.setMapType(map_type);
      map_description.setMapName(map_name);
      map_description.setMapArea(map_area);
      map_description.setMapNumber(map_number);

      maps_in_unit.add(map_description);

      has_next = map_dir.getNextAsByte();
    }
    return(maps_in_unit);
  }

  private GarminFile getFile(String filename, long timeout)
    throws IOException, FileNotFoundException
  {
    synchronized(flash_info_sync_request_lock_)
    {
      result_flash_info_ = null;
      requestMapFlashInfo();
      try
      {
        flash_info_sync_request_lock_.wait(timeout);
      }
      catch(InterruptedException ignore) {}
    }
        // no result or FileNotExists:
    if(result_flash_info_ == null)
      return(null);
    
    int map_area = result_flash_info_.getMapArea();
    GarminPacket request_packet = new GarminPacket(Pid_Request_File,4+2+filename.length()+1);
    request_packet.setNextAsLongWord(0);
    request_packet.setNextAsWord(map_area);
    request_packet.setNextAsString(filename);
    if(Debug.DEBUG)
      Debug.println("gps_garmin_map","Requesting file '"+filename+"' on map area "
                    +map_area+" with packet: "
                    +request_packet);
    synchronized(file_sync_request_lock_)
    {
      result_file_ = null;
      putPacketAsync(request_packet);
      try
      {
        file_sync_request_lock_.wait(timeout);
      }
      catch(InterruptedException ignore) {}
      if(result_file_ == null)
        throw new FileNotFoundException("File '"+filename+"' was not found on gps device.");
      return(result_file_); // TODO clone before return, so next file does not overwrite this var!
    }
  }


  private void setMaps()
  {
        // requestEraseFlash();
        // if first short in response is 0, send unlock
        // else error (message in data?)
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
 * Request the voltage from a garmin device. This method is untested and the
 * response is unknown at the moment!
 * Excerpt from newsgroup (taken from g7to):
 * extern BYTE	VoltsM[]; '  "\x0a\x02\x11\x00";	'  SEND voltages
 * 
 * extern DOUBLE  InternalVolts;			'  Internal (AA) voltage returned by Garmin
 * extern DOUBLE  ExternalVolts;			'  External voltage returned by Garmin
 * 
 * PROCX void doGPSVolts(void)
 * {
 * 	INT i;
 * 	i=' ((INT' )(SerialMessage+3));
 * 	InternalVolts=(float)i/100.0;
 * 	i=' ((INT' )(SerialMessage+5));
 * 	ExternalVolts=(float)i/100.0;
 * } '  doGPSVolts ' 
 * 
 * '  -------------------------------------------------------------------------- ' 
 * PROCX void getGPSVolts(void)
 * {
 * 	SendGarminMessage(VoltsM,4,"Send Voltages");	'  ask FOR volts
 * 	getGarminMessage(1); 						'  GET & PARSE ack
 * 	getGarminMessage(1);						'  GET & PARSE volts
 * 	printf("Int: %5.2lf,  Ext: %5.2lf\n",InternalVolts,ExternalVolts);
 * } '  getGPSVolts ' 
 * 
 * '  -------------------------------------------------------------------------- ' 
 * 
 * This is what I get:
 * TX - 10 0A 02 11 00 E3 10 03
 * RX - 10 06 02 0A 00 EE 10 03
 * TX - 10 06 02 23 00 D5 10 03
 * RX - 10 0C 02 03 00 EF 10 03
 */
	public void requestVoltage()
		throws IOException
	{
		waitTillReady();
		sendCommandAsync(Pid_Command_Data_L001, Cmnd_Transfer_Voltage_A010);
	}

//----------------------------------------------------------------------
/**
 * Request the serial number from the gps device. This method is non blocking
 * and returns immediately after the acknowledge was received.
 */
	public void requestSerialNumber()
		throws IOException
	{
		waitTillReady();
		if(capabilities_.hasCapability("L1"))
			sendCommandAsync(Pid_Command_Data_L001,Cmnd_Transfer_SerialNr);
		else
			sendCommandAsync(Pid_Command_Data_L002,Cmnd_Transfer_SerialNr);
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
 * Request to send async packets from the gps device. As the packets
 * sent are not known at this time, this method should not be used!!! 
 */
  protected void requestAsyncEvents()
  {
        // experimental code (from http://playground.sun.com/pub/soley/garmin.txt):
        // more information about these packets can be found at:
        // http://artico.lma.fi.upm.es/numerico/miembros/antonio/async/report.txt
    waitTillReady();
    GarminPacket garmin_packet = new GarminPacket(Pid_Enable_Async_Events,2);
        // 00 00 = 0x0= disable all (no bits set)
        // 01 00 = 0x1= enables RecordType=00,01,02 // etrex summit: nothing sent
        // 02 00 = 0x2= enables RecordType=0d // etrex summit: nothing sent
        // 04 00 = 0x4= enables RecordType=14,27,28 // etrex summit: packets id 39/0x27 (2 bytes) are sent
        // 08 00 = 0x8= enables RecordType=16 // etrex summit: nothing sent
        // 10 00 = 0x10= enables RecordType=17 // etrex summit: nothing sent
        // 20 00 = 0x20= enables RecordType=07,12,19 // etrex summit: packets id 55/0x37(36bytes),56/0x38(40bytes) are sent
        // 40 00 = 0x40= enables RecordType=07,12 // etrex summit: packets 55,56 are sent
        // 80 00 = 0x80= enables RecordType=1a // etrex summit: packet 26/0x1a (96bytes) every second
        // 00 01 = 0x100= enables RecordType=29,2a // etrex summit: nothing sent
        // 00 02 = 0x200= enables RecordType=?? // etrex summit: packets 102/0x66 (20bytes), 104/0x68(20 bytes) sent
        // 00 04 = 0x400= enables RecordType=?? // etrex summit: nothing sent
        // 00 08 = 0x800= enables RecordType=?? // etrex summit: nothing sent
        // 00 10 = 0x1000= enables RecordType=?? // etrex summit: nothing sent
        // 00 20 = 0x20100= enables RecordType=?? // etrex summit: nothing sent
        // 00 40 = 0x20100= enables RecordType=?? // etrex summit: nothing sent
        // 00 80 = 0x20100= enables RecordType=?? // etrex summit: nothing sent
        // ff ff = 0xffff= enables all (all bits set)
    garmin_packet.put(0x80);
    garmin_packet.put(0);
    putPacketAsync(garmin_packet);
  }


//----------------------------------------------------------------------
/**
 * Experimental method to request information about the flash
 * memory. This information is taken from the gpsexplorer application.
 */
  protected void requestMapFlashInfo()
    throws IOException
  {
    waitTillReady();
    if (capabilities_.hasCapability("L1") &&
        capabilities_.hasCapability("A10"))
    {
      sendCommandAsync(Pid_Command_Data_L001, Cmnd_Get_Map_Flash_Info);
    }
  }

//----------------------------------------------------------------------
/**
 * Experimental method to request eraseure of the flash memory!!! This
 * information is taken from the gpsexplorer application. Completely untested! 
 */
  private void requestEraseFlash(int map_area)
  {
    waitTillReady();
    GarminPacket garmin_packet = new GarminPacket(Pid_Flash_Erase_Request,2);
    garmin_packet.setNextAsWord(map_area);
    putPacketAsync(garmin_packet);
  }


//----------------------------------------------------------------------
/**
 * Requests to send a PVT packet every second.
 */
  protected void requestStartPvtData()
    throws IOException
  {
    waitTillReady();
    if (capabilities_.hasCapability("L1") &&
        capabilities_.hasCapability("A10"))
    {
      sendCommandAsync(Pid_Command_Data_L001, Cmnd_Start_Pvt_Data_A010);
      send_pvt_periodically_ = true;
    }
        // not supported in L2/A11
  }

//----------------------------------------------------------------------
/**
 * Requests to stop to send a PVT packet every second.
 */
  protected void requestStopPvtData()
    throws IOException
  {
    waitTillReady();
    if (capabilities_.hasCapability("L1") &&
        capabilities_.hasCapability("A10"))
    {
      sendCommandAsync(Pid_Command_Data_L001, Cmnd_Stop_Pvt_Data_A010);
      send_pvt_periodically_ = false;
    }
        // not supported in L2/A11
  }

//----------------------------------------------------------------------
/**
 * Request the routes from the gps device. This method is non blocking
 * and returns immediately after the acknowledge was received or the
 * number of tries was exceeded.
 * @return true if the acknowledge was sent, false otherwise (the
 * device did not receive the packet then).
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
 * device did not receive the packet then).
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
 * device did not receive the packet then).
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
 * device did not receive the packet then).
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
 * device did not receive the packet then).
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
 * gps device when route packets were sent. 
 * @param routes list of route packets
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
 * gps device when track packets were sent.
 * @param tracks list of waypoint packets
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
 * gps device when waypoint packets were sent.
 * @param waypoints list of waypoint packets
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
 * gps device when transfer complete packets were sent.
 */
  protected void fireTransferCompleteReceived()
  {
    if(Debug.DEBUG)
      Debug.println("gps_garmin","TransferComplete received");
  }
  
//----------------------------------------------------------------------
/**
 * Method called out of the thread that reads the information from the
 * gps device when product data packet was sent.
 * @param product packet
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
 * gps device when protocol array (capabilities) packet was sent.
 * @param capabilities capabilities packet
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
 * gps device when PVT (position, velocity, ...) packet was sent.
 * @param pvt pvt packet
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
        GPSPositionError pos_error = new GPSPositionError(pvt.getEpe(),pvt.getEph(),pvt.getEpv());
        changeGPSData(EPE,pos_error);
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
 * @param display_data the display data packet
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
 * gps device when serial number data was sent.
 * @param serial_number the serial number
 */
  protected void fireSerialNumberReceived(long serial_number)
  {
    if(Debug.DEBUG)
      Debug.println("gps_garmin","serial number received: "+serial_number);

    synchronized(serial_number_sync_request_lock_)
    {
      result_serial_number_ = serial_number;
      serial_number_sync_request_lock_.notify();
    }
  }

//----------------------------------------------------------------------
/**
 * Method called out of the thread that reads the information from the
 * gps device when display data was sent.
 * @param flash_info the flash info packet
 */
  protected void fireFlashInfoReceived(GarminFlashInfo flash_info)
  {
    if(Debug.DEBUG)
      Debug.println("gps_garmin","flash info received: "+flash_info);

    synchronized(flash_info_sync_request_lock_)
    {
      result_flash_info_ = flash_info;
      flash_info_sync_request_lock_.notify();
    }
  }

  
//----------------------------------------------------------------------
/**
 * Method called out of the thread that reads the information from the gps
 * device when file not exist packet was sent. Sets the
 * <code>result_file</code> to null.
 * @param file_not_found a garmin packet indicating file not found.
 */
  protected void fireFileNotFoundReceived(GarminPacket file_not_found)
  {
    if(Debug.DEBUG)
      Debug.println("gps_garmin","file not found received: "+file_not_found);

    synchronized(file_sync_request_lock_)
    {
      result_file_ = null;
      file_sync_request_lock_.notify();
    }
  }

//----------------------------------------------------------------------
/**
 * Method called out of the thread that reads the information from the
 * gps device when file was sent.
 */
  protected void fireFileReceived(GarminFile garmin_file)
  {
    if(Debug.DEBUG)
      Debug.println("gps_garmin","file received (maybe shortened): "
                    +garmin_file);

    synchronized(file_sync_request_lock_)
    {
      result_file_ = garmin_file;
      file_sync_request_lock_.notify();
    }
  }

  

//----------------------------------------------------------------------
/**
 * Method called out of the thread that reads the information from the
 * gps device when a acknowledge (ACK or NAK) was sent.
 */
  protected void fireResult(boolean result, int packet_id)
  {
    synchronized(acknowledge_lock_)
    {
      send_success_ = result;
      send_packet_id_ = packet_id;
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
        listener.receivedResult(result,packet_id);
      }
    }
    if(Debug.DEBUG)
      Debug.println("gps_garmin","Result received: "+result+" for packet id "+packet_id
);
  }
  
//----------------------------------------------------------------------
/**
 * Method called out of the thread that reads the information from the
 * gps device when data packet (any packet) was sent. This method
 * detects the type of the packet, creates the objects for routes,
 * tracks, waypoints, etc. and informs the fireXXX methods about
 * it. It therefore is the main parser method for received garmin
 * packets.
 */
  protected void firePacketReceived(GarminPacket garmin_packet)
  {
    int packet_id = garmin_packet.getPacketId();
    GarminPacket next_garmin_packet;
    int packets_type_received = 0;

        // create int[] buffer (intermediate solution, as the
        // Garmin** classes do not work with the GarminPacket class
        // yet:
//    int[] buffer = garmin_packet.getCompatibilityBuffer();
      
    switch(packet_id)
    {
    case NAK:
      fireResult(false,packet_id);
      break;
    case ACK:
      fireResult(true,packet_id);
      break;
          // product info:
    case Pid_Product_Data:
      fireProductDataReceived(new GarminProduct(garmin_packet));
      break;
          // capabilities:
    case Pid_Protocol_Array:
      fireProtocolArrayReceived(new GarminCapabilities(garmin_packet));
      break;
          // PVT packet
    case Pid_Pvt_Data_L001:
      if(capabilities_ == null)
        return;
      if(capabilities_.hasCapability("D800"))
      {
        firePVTDataReceived(new GarminPVTD800(garmin_packet));
      }
      if(capabilities_.hasCapability("D802"))
      {
        firePVTDataReceived(new GarminPVTD802(garmin_packet));
      }
      break;
	  
    case Pid_Display_Data_L001:
      if(capabilities_ == null)
        return;
      try
      {
            // if any packets of the display data are corrupt (wrong checksum,
            // etc.), the garmin device will not resend them, even if NAK is
            // answered! So if this happens, we'll fetch a second image and put it
            // in the same display data as before, so the two images are
            // overlaid. It should happen very rarely that exactly the same
            // lines of the two corrupt images are equal. So at the end, we
            // will get one good image! This is kind of dirty, but it works! (cdaller)
        int corrupt_images_retries = 1;
        int num_corrupt_packets = 0;
        GarminDisplayData display_data = new GarminDisplayData(garmin_packet);
        int height = display_data.getHeight();
        if(Debug.DEBUG)
          Debug.println("garmin_display_header","Reading Display Data with "+height+" lines.");
        do
        {
          num_corrupt_packets = 0;
          fireProgressActionStart(GETSCREENSHOT,1,height);
							// TODO: height != number of packets for color devices!!!!!
          for(int linenum = 0; linenum < height-num_corrupt_packets; linenum++)
          {
            do
            {
              next_garmin_packet = getPacket();
//		  System.out.println("packet "+ (linenum+1) + " received");
              if(next_garmin_packet == null)
              {
                num_corrupt_packets++;
//		    System.out.println("XXXXXXXXx packet was null");
              }
            }
            while(next_garmin_packet == null);
            if(next_garmin_packet.getPacketId() != Pid_Display_Data_L001)
            {
              System.err.println("WARNING: Expected Display Data, received: "+next_garmin_packet);
// 	    fireDisplayDataReceived(display_data);
// 	    return;
            }
            else // display data packet:
            {
              if(linenum % 10 == 0)
                fireProgressActionProgress(GETSCREENSHOT,linenum);
                  // add line to display data:
              display_data.addData(next_garmin_packet);
            }
          }
//	      System.out.println("finished one run");
          if((num_corrupt_packets > 0) && (corrupt_images_retries > 0))
          {
            System.err.println("WARNING: "+num_corrupt_packets
                               +" corrupt packets detected, retry reading display data");
            requestScreenShot();
		
            GarminPacket ignore;
            do
            {
              ignore = getPacket(); // ignore the next header (assume the first header was not corrupt!)
//		  System.out.println("ignoring packet = "+ignore);
            }
            while((ignore != null) && (ignore.getPacketId() != Pid_Display_Data_L001));
          }
        }
        while((num_corrupt_packets > 0) && (corrupt_images_retries-- > 0));
        fireProgressActionProgress(GETSCREENSHOT,height);
        fireProgressActionEnd(GETSCREENSHOT);
        fireDisplayDataReceived(display_data);
      }
      catch(IOException ioe)
      {
        ioe.printStackTrace();
      }
      break;
          // larger amount of packets belong together:
    case Pid_Records_L001:
    case Pid_Records_L002:
      if(capabilities_ == null)
        return;
      int packet_num = garmin_packet.getWord(0); // buffer[2]+256*buffer[3];
      int packet_count = 0;
      if(Debug.DEBUG)
        Debug.println("gps_garmin_packet","Receiving "+packet_num+" packets from device.");
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
          next_garmin_packet = getPacket();
        }
        while(next_garmin_packet == null);
//        buffer = next_garmin_packet.getCompatibilityBuffer();
        packet_count++;
        if(Debug.DEBUG)
				{
          Debug.println("gps_garmin_packet","read packet "+packet_count+" of "+packet_num);
					if(Debug.isEnabled("gps_garmin_packet_detail"))
						Debug.println("gps_garmin_packet_detail","packet details: "+next_garmin_packet.toString());
				}
				packet_id = next_garmin_packet.getPacketId();
        switch(packet_id)
        {
              // route header:
        case Pid_Rte_Hdr_L001:
        case Pid_Rte_Hdr_L002:
          if(packets_type_received == 0) // only true for first packet
            fireProgressActionStart(GETROUTES,1,packet_num);
          packets_type_received = RECEIVED_ROUTES;
              // save previous item
          if(item != null)
            items.add(item);

              // create route header depending on used format:
          if(capabilities_.hasCapability("D200"))
            item = new GarminRouteD200(next_garmin_packet);
          if(capabilities_.hasCapability("D201"))
            item = new GarminRouteD201(next_garmin_packet);
          if(capabilities_.hasCapability("D202"))
            item = new GarminRouteD202(next_garmin_packet);
          break;
		    
              // route point:
        case Pid_Rte_Wpt_Data_L001:
        case Pid_Rte_Wpt_Data_L002:

							// check, if id and link version match (only necessary when using
							// undocumented features :-) : 
     // case Pid_Unknwon1_L001: // same as Pid_Rte_Wpt_Data_L002, but different meaning!
					if((packet_id == Pid_Rte_Wpt_Data_L002) && (capabilities_.hasCapability("L1")))
					{
						System.out.println("Unknown packet1: "+next_garmin_packet);
						break;
					}

          if(packet_count % 10 == 0)
            fireProgressActionProgress(GETROUTES,packet_count);
          if(capabilities_.hasCapability("D100"))
            ((GarminRoute)item).addWaypoint(new GarminWaypointD100(next_garmin_packet));
          else
            if(capabilities_.hasCapability("D101"))
              ((GarminRoute)item).addWaypoint(new GarminWaypointD101(next_garmin_packet));
            else
              if(capabilities_.hasCapability("D102"))
                ((GarminRoute)item).addWaypoint(new GarminWaypointD102(next_garmin_packet));
              else
                if(capabilities_.hasCapability("D103"))
                  ((GarminRoute)item).addWaypoint(new GarminWaypointD103(next_garmin_packet));
                else
                  if(capabilities_.hasCapability("D107"))
                    ((GarminRoute)item).addWaypoint(new GarminWaypointD107(next_garmin_packet));
                  else
                    if(capabilities_.hasCapability("D108"))
                      ((GarminRoute)item).addWaypoint(new GarminWaypointD108(next_garmin_packet));
                    else
                      if(capabilities_.hasCapability("D109"))
                        ((GarminRoute)item).addWaypoint(new GarminWaypointD109(next_garmin_packet));
                      else
                        System.err.println("WARNING: unsupported garmin waypoint type!");
          if(Debug.DEBUG)
            Debug.println("gps_garmin","Received Waypoint");
          break;
              // route link:
        case Pid_Rte_Link_Data_L001:
          if(capabilities_ == null)
            return;
          if(packet_count % 10 == 0)
            fireProgressActionProgress(GETROUTES,packet_count);
// 					GarminRouteLinkD210 link = new GarminRouteLinkD210(next_garmin_packet);
// 					System.out.println("LINK: "+link.toString());
	  // temporarily ignored
	  //           if(capabilities_.hasCapability("D210"))
	  //             ((GarminRoute)item).addRouteLinkData(new GarminRouteLinkD210(next_garmin_packet));
          break;
		    
              // track header
        case Pid_Trk_Hdr_L001:
          if(packets_type_received == 0) // only true for first packet
            fireProgressActionStart(GETTRACKS,1,packet_num);
          packets_type_received = RECEIVED_TRACKS;
              // save previous item
          if(item != null)
            items.add(item);

              // create route header depending on used format:
          if(capabilities_.hasCapability("D310"))
            item = new GarminTrackD310(next_garmin_packet);
          if(Debug.DEBUG)
            Debug.println("gps_garmin","Received Track Header: "+item);
          break;

              // trackpoints
        case Pid_Trk_Data_L001:
          if(Debug.DEBUG)
            Debug.println("gps_garmin","Received Track Data");
          if(packet_count % 10 == 0)
            fireProgressActionProgress(GETTRACKS,packet_count);

          if(item == null) // device is incapable of sending track header
          {
            item = new GarminTrack();
            ((GarminTrack)item).setIdentification(track_date_format.format(new Date()));
            fireProgressActionStart(GETTRACKS,1,packet_num);
            packets_type_received = RECEIVED_TRACKS;
          }
          
          if(capabilities_.hasCapability("D300"))
            ((GarminTrack)item).addWaypoint(new GarminTrackpointD300(next_garmin_packet));
          if(capabilities_.hasCapability("D301"))
            ((GarminTrack)item).addWaypoint(new GarminTrackpointD301(next_garmin_packet));
          break;

              // waypoint:
        case Pid_Wpt_Data_L001:
        case Pid_Wpt_Data_L002:
//           if(Debug.DEBUG)
//             Debug.println("gps_garmin","Received Waypoint Data");
          if(capabilities_ == null)
            return;
          if(packets_type_received == 0) // only true for first packet
            fireProgressActionStart(GETWAYPOINTS,1,packet_num);
          packets_type_received = RECEIVED_WAYPOINTS;
          if(items == null)
          {
            items = new Vector();
          }
          if(packet_count % 10 == 0)
            fireProgressActionProgress(GETWAYPOINTS,packet_count);
          if(capabilities_.hasCapability("D100"))
            items.add(new GarminWaypointAdapter(new GarminWaypointD100(next_garmin_packet)));
          else if(capabilities_.hasCapability("D101"))
            items.add(new GarminWaypointAdapter(new GarminWaypointD101(next_garmin_packet)));
          else if(capabilities_.hasCapability("D102"))
            items.add(new GarminWaypointAdapter(new GarminWaypointD102(next_garmin_packet)));
          else if(capabilities_.hasCapability("D103"))
            items.add(new GarminWaypointAdapter(new GarminWaypointD103(next_garmin_packet)));
          else if(capabilities_.hasCapability("D107"))
            items.add(new GarminWaypointAdapter(new GarminWaypointD107(next_garmin_packet)));
          else if(capabilities_.hasCapability("D108"))
            items.add(new GarminWaypointAdapter(new GarminWaypointD108(next_garmin_packet)));
          else if(capabilities_.hasCapability("D109"))
            items.add(new GarminWaypointAdapter(new GarminWaypointD109(next_garmin_packet)));
          else
            System.err.println("WARNING: unsupported garmin waypoint type!");
          if(Debug.DEBUG)
            Debug.println("gps_garmin","Received Waypoint");
          break;
              // transfer complete
        case Pid_Xfer_Cmplt_L001:
//		  case Pid_Xfer_Cmplt_L002: // same number as Pid_Xfer_Cmplt_L001
//          GarminXferComplete xfer_complete = new GarminXferComplete(next_garmin_packet);
          if(Debug.DEBUG)
            Debug.println("gps_garmin","transfer complete");
          transfer_complete = true;
          break;
        default:
          System.err.println("WARNING GPSGarminDataProcessor: unknown packet id: "
                             +packet_id);
          if(Debug.DEBUG)
            Debug.println("gps_garmin","unknown packet: "+next_garmin_packet);
        }
      }
	    
          // add last item vector:
      if(item != null)
        items.add(item);
			switch(packets_type_received)
			{
			case(RECEIVED_ROUTES):
				fireProgressActionProgress(GETROUTES,packet_num);
				fireProgressActionEnd(GETROUTES);
				fireRoutesReceived(items);
				break;
			case(RECEIVED_TRACKS):
				fireProgressActionProgress(GETTRACKS,packet_num);
				fireProgressActionEnd(GETTRACKS);
				fireTracksReceived(items);
				break;
			case(RECEIVED_WAYPOINTS):
				fireProgressActionProgress(GETWAYPOINTS,packet_num);
				fireProgressActionEnd(GETWAYPOINTS);
				fireWaypointsReceived(items);
				break;
			default:
				if(items.size() == 0)
				{
							// as I do not know, what kind of information we were waiting for,
							// I'll inform all that NOTHING (empty list) came:
					fireProgressActionProgress(GETWAYPOINTS,packet_num);
					fireProgressActionEnd(GETWAYPOINTS);
					fireProgressActionProgress(GETROUTES,packet_num);
					fireProgressActionEnd(GETROUTES);
					fireProgressActionProgress(GETTRACKS,packet_num);
					fireProgressActionEnd(GETTRACKS);
							// send an empty list:
					fireWaypointsReceived(items);
					fireRoutesReceived(items);
					fireTracksReceived(items);
				}
//				System.err.println("WARNING: GPSGarminDataProcessor: unknown packet list (ignored)");
			}
			packets_type_received = 0; // reset packet type
      break;  // end of Pid_Records_L001/L002
    case Pid_Xfer_Cmplt_L001:
      fireTransferCompleteReceived();
      break;
//     case Pid_Satellite_Info:
//       GarminSatelliteInfo info = new GarminSatelliteInfo(garmin_packet);
//       break;
    case Pid_File_Data:
      System.out.println("WARNING: unexpected File_Data: "+garmin_packet);
      break;
    case Pid_File_Header:
      GarminFile garmin_file = new GarminFile(garmin_packet);
      if(Debug.DEBUG)
        Debug.println("gps_garmin","Pid_File_Header: "+garmin_file);
//      System.out.println("Garmin file header :"+garmin_file);
      packet_count = 0;
      while(packet_count < garmin_file.getDataPacketCount())
      {
        do
        {
          next_garmin_packet = getPacket();
        }
        while(next_garmin_packet == null);
//        System.out.println("File Data: "+next_garmin_packet);
        packet_count++;
        if(next_garmin_packet.getPacketId() != Pid_File_Data)
        {
          System.err.println("WARNING GPSGarminDataProcessor: unknown packet id: "
                             +packet_id+" while waiting for File Data!");
          if(Debug.DEBUG)
            Debug.println("gps_garmin","unknown packet: "+garmin_packet);
        }
        garmin_file.addDataPacket(next_garmin_packet);
      } 
      fireFileReceived(garmin_file);
      break;
    case Pid_File_Not_Exist:
      System.out.println("Pid_File_Not_Exist: "+garmin_packet);
      fireFileNotFoundReceived(garmin_packet);
      break;
    case Pid_Flash_Info:
      System.out.println("Pid_File_Flash_Info: "+garmin_packet);
      GarminFlashInfo flash_info = new GarminFlashInfo(garmin_packet);
      fireFlashInfoReceived(flash_info);
      break;
    case Pid_Flash_Erase_Response:
      System.out.println("Pid_Flash_Erase_Response: "+garmin_packet);
      break;
    case Pid_Unlock_Code_Response:
      System.out.println("Pid_Unlock_Code_Response: "+garmin_packet);
      if(garmin_packet.getPacketSize() != 2)
      {
        System.err.println("Wrong response for unlock code");
        return;
      }
      int success = garmin_packet.getNextAsByte();
      if(success == 0)
      {
        System.err.println("Illegal unlock code");
        return;
      }
      if(success == 1)
      {
            // unlock codes correct
      }
      break;
		case Pid_Voltage_Response:
			System.out.println("Voltage packet received - not handled yet!");
			break;
		case Pid_Serial_Number:
			fireSerialNumberReceived(garmin_packet.getNextAsLongWord());
// 			System.out.println("Serial Number packet received - not really handled yet!");
// 			System.out.println("Serial Number: "+garmin_packet.getLong(0));
			break;
    default:
      System.err.println("WARNING GPSGarminDataProcessor: unknown packet id: "
                         +packet_id);
      if(Debug.DEBUG)
        Debug.println("gps_garmin","unknown packet: "+garmin_packet);
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
      String port_name = "/dev/ttyS0";
      if(args.length > 0)
        port_name = args[0];
      GPSGarminDataProcessor gps_processor = new GPSGarminDataProcessor();
      GPSDevice gps_device;
      Hashtable environment = new Hashtable();
      environment.put(GPSSerialDevice.PORT_NAME_KEY,port_name);
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

//       List maps_in_unit = gps_processor.getMaps(0L);
//       System.out.println("Maps in Unit:");
//       GarminMapDescription map_description = null;
//       Iterator iterator = maps_in_unit.iterator();
//       while(iterator.hasNext())
//       {
//         map_description = (GarminMapDescription)iterator.next();
//         System.out.println(map_description);
//       }
//       System.out.println("Download last map:");
//       if(map_description != null)
//         gps_processor.getMap(map_description,0L);


// ======================================================================
//       Vector waypoints = new Vector();
//       WaypointImpl wpt;
//       wpt = new WaypointImpl("Hart bei Straden",46.783300,15.866700);
//       wpt.setSymbolName("car");
//       wpt.setComment("Hart bei Straden");
//       waypoints.add(wpt);
//       wpt = new WaypointImpl("Stephansdom",48.208773,16.372496);
//       wpt.setSymbolName("flag");
//       wpt.setComment("Stephansdom");
//       waypoints.add(wpt);
//       wpt = new WaypointImpl("Dallermassl_VBruck",47.998845, 13.647118);
//       wpt.setSymbolName("wpt-dot");
//       wpt.setComment("Dallermassl_VBruck");
//       waypoints.add(wpt);
//       wpt = new WaypointImpl("Voralpenkreuz",48.061010, 14.039118);
//       wpt.setSymbolName("gas_plus");
//       wpt.setComment("Voralpenkreuz");
//       waypoints.add(wpt);
//       wpt = new WaypointImpl("Schmaranz_Home",47.020032, 15.508126);
//       wpt.setSymbolName("camp");
//       wpt.setComment("Schmaranz_Home");
//       waypoints.add(wpt);
//       wpt = new WaypointImpl("Dallermassl Graz",47.060099, 15.473327,483);
//       wpt.setSymbolName("house");
//       wpt.setComment("Dallermassl Graz");
//       waypoints.add(wpt);
//       wpt = new WaypointImpl("landhaus",47.070416, 15.439635);
//       wpt.setSymbolName("trcbck");
//       wpt.setComment("landhaus");
//       waypoints.add(wpt);
//       wpt = new WaypointImpl("test",46.766694, 14.359430);
//       wpt.setSymbolName("1st_aid");
//       wpt.setComment("test");
//       waypoints.add(wpt);
//       gps_processor.setWaypoints(waypoints);

// ======================================================================

// 			System.out.println("Requesting Voltage:");
// 			gps_processor.requestVoltage();

// ======================================================================

			System.out.println("press key");
      System.in.read();
//       System.out.println("testing unkown protocols...");
// 			gps_processor.sendCommandAsync(Pid_Command_Data_L001, 15);
// 			System.in.read();
      System.out.println("testing unkown protocols...");
			gps_processor.sendCommandAsync(Pid_Command_Data_L001, 14);
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
      System.exit(1);
    }
    
  }


//----------------------------------------------------------------------
/**
 * The ReaderThread reads packets from the gps device and passes them
 * to the firePacketReceived method. It therefore does not block the
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
          Debug.println("gps_garmin_packet","waiting for packet...");
        GarminPacket garmin_packet = getPacket();
        if(garmin_packet == null)
        {
          if(Debug.DEBUG)
            Debug.println("gps_garmin_packet","invalid packet received");
        }
        else
        {
          if(Debug.DEBUG)
					{
            Debug.println("gps_garmin_packet","packet received: "+garmin_packet.getPacketId());
						if(Debug.isEnabled("gps_garmin_packet_detail"))
							Debug.println("gps_garmin_packet_detail","packet details: "+garmin_packet.toString());
					}
          firePacketReceived(garmin_packet);
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
 * sending in the middle of a packet and the reader thread then is
 * blocked in the read() method. Therefore the reader thread uses this
 * watchdog. It sends a NAK packet after 5 seconds, if not reset
 * before. The readerthread resets the watchdog on every character it
 * reads. So if the reader thread is blocked for more than 5 seconds,
 * a NAK packet is sent to the garmin device. This usually helps to
 * wake up the device again. The reader thread tells the watchdog when
 * to start/stop watching (on start/end of the packet reading
 * method), so the watchdog does not send NAKs when no packets are
 * expected.
 */
  class WatchDogThread extends Thread
  {
    boolean running_ = true;
    boolean reset_ = false;
    int packet_id_ = 0;
    
    public WatchDogThread()
    {
      super("GarminWatchDog");
    }

    public void startWatching()
    {
      running_ = true;
      packet_id_ = 0;
      try
      {
        start();
      }
      catch(IllegalThreadStateException ignore) {} // already started
	  
    }
    
    public void setPacketId(int packet_id)
    {
      packet_id_ = packet_id;
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
            Debug.println("gps_garmin_packet","WATCHDOG sending NAK");
          try
          {
            sendCommandAsync(NAK,packet_id_);
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

