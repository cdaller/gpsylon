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


package org.dinopolis.gpstool.gpsinput.sirf;

import java.io.IOException;
import java.io.OutputStream;
import org.dinopolis.gpstool.gpsinput.GPSException;
import org.dinopolis.gpstool.gpsinput.GPSSerialDevice;
import org.dinopolis.gpstool.gpsinput.nmea.GPSNmeaDataProcessor;


//----------------------------------------------------------------------
/**
 * This data processor switches a sirf device from sirf mode to nmea
 * mode. The rest is normal nmea behaviour. So no real sirf protocol
 * is used (only the change from sirf to nmea).  As a reference the
 * sirf manual from http://www.falcom.de/pub/sirf/SiRFmessages.pdf is
 * used.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 *
 */

public class GPSSirfDataProcessor extends GPSNmeaDataProcessor
{

  protected OutputStream out_stream_;
  protected static int[] SWITCH_SIRF_TO_NMEA_PAYLOAD = new int[]
  {0x81,  // message id
   0x02,  // mode
   0x01,  // gga message
   0x01,  // checksum
   0x00,  // gll message
   0x01,  // checksum
   0x05,  // gsa message
   0x01,  // checksum
   0x05,  // gsv message
   0x01,  // checksum
   0x00,  // mss message
   0x01,  // checksum
   0x00,  // rmc message
   0x01,  // checksum
   0x00,  // vtg messag
   0x01,  // checksum
   0x00,  // unused field
   0x01,  // unused field
   0x00,  // unused field
   0x01,  // unused field
   0x00,  // unused field
   0x01,  // unused field
   0x12, 0xc0  // baud rate (38400,19200,9600,4800,2400)
  };

  public static final int CHECKSUM_15BIT_LIMIT = (int)Math.pow(2,15)-1;
  
//----------------------------------------------------------------------
/**
 * Default constructor.
 */
  public GPSSirfDataProcessor()
  {
    super();
  }
  
  
//----------------------------------------------------------------------
/**
 * Starts the data processing. The Data Processor connects to the
 * GPSDevice and starts retrieving information. 
 *
 * @exception if an error occured on connecting.
 */
  public void open()
    throws GPSException
  {
    super.open();
    try
    {
          // switch device from sirf to nmea mode:
      out_stream_ = gps_device_.getOutputStream();
      int[] message = createSirfMessage(SWITCH_SIRF_TO_NMEA_PAYLOAD);
      for(int index = 0; index < message.length; index++)
      {
        out_stream_.write(message[index]);
      }
      out_stream_.flush();

          // change serial port speed:
      if(gps_device_ instanceof GPSSerialDevice)
      {
            // find speed set in sirf message:
        int speed = SWITCH_SIRF_TO_NMEA_PAYLOAD[SWITCH_SIRF_TO_NMEA_PAYLOAD.length -1]
                    + SWITCH_SIRF_TO_NMEA_PAYLOAD[SWITCH_SIRF_TO_NMEA_PAYLOAD.length-2] << 8;
        
        System.err.println("setting speed to "+speed);
        ((GPSSerialDevice)gps_device_).setSerialPortSpeed(speed);
      }
    }
    catch(IOException e)
    {
      throw new GPSException(e.getMessage());
    }
    
  }


//----------------------------------------------------------------------
 /**
  * Calculate the checksum of a sirf message
  *
  * @param message the sirf message
  * @return the checksum
  */
  protected static int calculateSirfChecksum(int[] message)
  {
    int index = 0;
    int checksum = 0;
    while (index < message.length)
    {
      checksum += message[index];
      index++;
    }
    checksum = checksum & CHECKSUM_15BIT_LIMIT;
    return(checksum);
  }

//----------------------------------------------------------------------
 /**
  * Create a sirf message (sequence start, payload, checksum, sequence
  * end) from a given payload.
  *
  * @param payload the payload
  * @return the sirf message (int array)
  */
  protected static int[] createSirfMessage(int [] payload)
  {
    // start sequence = 2 bytes, end sequence = 2 bytes,
    // payload length = 2 bytes, message checksum = 2 bytes
    int message_length = payload.length + 8;
  
    int[] message = new int[message_length]; 
    message[0] = 0xa0;
    message[1] = 0xa2;
    message[2] = (payload.length & 0xff00) >> 8;
    message[3] = payload.length & 0x00ff;
    System.arraycopy(payload,0,message,4,payload.length);
    int checksum = calculateSirfChecksum(payload);
    message[message_length - 4] = (checksum & 0xff00) >> 8 ;
    message[message_length - 3] = checksum & 0x00ff;
    message[message_length - 2] = 0xb0;
    message[message_length - 1] = 0xb3;
    return(message);
  }


//   public static void main(String[] args)
//   {
//     int[] payload = SWITCH_SIRF_TO_NMEA_PAYLOAD;
//     int[] message = createSirfMessage(payload);
//     for(int index = 0; index < message.length; index++)
//     {
//       System.out.println(index +": 0x"+ Integer.toHexString(message[index]));
//     }
//   }
}
