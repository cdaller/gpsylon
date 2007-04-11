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

package org.dinopolis.gpstool.gpsinput.nmea;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dinopolis.gpstool.gpsinput.GPSDataProcessor;
import org.dinopolis.gpstool.gpsinput.GPSException;
import org.dinopolis.gpstool.gpsinput.GPSGeneralDataProcessor;
import org.dinopolis.gpstool.gpsinput.GPSPosition;
import org.dinopolis.gpstool.gpsinput.GPSPositionError;
import org.dinopolis.gpstool.gpsinput.SatelliteInfo;

// ----------------------------------------------------------------------
/**
 * This class is interpreting NMEA data from a GPSDevice (serial gps-receivier, file containing gps
 * data, ...) and provides this information (heading and location, etc.).
 *
 * @author Christof Dallermassl
 * @version $Revision$
 *
 * Contributions:
 * <ul>
 * <li>Didier Donsez <didier.donsez@imag.fr> added hanlding of VTG and HTD nmea sentences</li>
 * </ul>
 */

public class GPSNmeaDataProcessor extends GPSGeneralDataProcessor implements Runnable
{
  private static Logger logger_ = Logger.getLogger(GPSNmeaDataProcessor.class);
  private static Logger logger_message_ = Logger.getLogger(GPSNmeaDataProcessor.class.getName() + ".message");
  private static Logger logger_nmea_ = Logger.getLogger(GPSNmeaDataProcessor.class.getName() + ".nmea");
  /** the inputstream from the GPSDevice */
  protected InputStream in_stream_ = null;

  /** the reader thread */
  protected Thread read_thread_;

  public final static int MAX_NMEA_MESSAGE_LENGTH = 90;
  /** if an end of stream is reached, wait some milliseconds and continue */
  private static final long EOFREACHED_WAIT_MS = 50;
  /** the milliseconds to wait for device info on nmea devices */
  private static final long NMEA_DEVICE_INFO_WAIT_MS = 1000;

  SatelliteInfo[] satellite_infos_;
  int satellite_info_count_;

  boolean open_ = false;

  int delay_time_ = -1;

  int last_gsv_message_number_ = 0;

  boolean ignore_invalid_checksum_ = false;
  boolean print_ignore_warning_ = true;

  List gps_infos_;

  /**
   * initialization strings that some nmea devices understand
   * taken from http://www.gpspassion.com/forumsen/topic.asp?TOPIC_ID=60637
   * tested with GlobalSat bt338
   */
  public static final String INIT_COLD_START = "$PSRF101,0,0,0,000,0,0,12,6*12";
  public static final String INIT_WARM_START = "$PSRF101,0,0,0,000,0,0,12,2*16";
  public static final String INIT_HOT_START = "$PSRF101,0,0,0,000,0,0,12,1*15";

  // ----------------------------------------------------------------------
  /**
   * Default constructor.
   */
  public GPSNmeaDataProcessor()
  {
  }

  // ----------------------------------------------------------------------
  /**
   * Default constructor.
   *
   * @param delay_time the time between two NMEA messages are read (may be used for reading NMEA
   *          files slower) in milliseconds.
   */
  public GPSNmeaDataProcessor(int delay_time)
  {
    this();
    setDelayTime(delay_time);
  }

  // ----------------------------------------------------------------------
  /**
   * Sets the deley time between reading two NMEA messages.
   *
   * @param delay_time the time between two NMEA messages are read (may be used for reading NMEA
   *          files slower) in milliseconds.
   */
  public void setDelayTime(int delay_time)
  {
    delay_time_ = delay_time;
  }

  // ----------------------------------------------------------------------
  /**
   * Returns true if invalid NMEA checksums should be ignored.
   *
   * @return true if invalid NMEA checksums should be ignored.
   */
  public boolean isIgnoreInvalidChecksum()
  {
    return (ignore_invalid_checksum_);
  }

  // ----------------------------------------------------------------------
  /**
   * Set if invalid nmea checksums should be ignored. If set to true, invalid checksums are ignored
   * (may be used for broken nmea devices).
   *
   * @param ignore_invalid_checksum if set to true, invalid checksums are ignored.
   */
  public void setIgnoreInvalidChecksum(boolean ignore_invalid_checksum)
  {
    ignore_invalid_checksum_ = ignore_invalid_checksum;
  }

  // ----------------------------------------------------------------------
  /**
   * Starts the data processing. The Data Processor connects to the GPSDevice and starts retrieving
   * information.
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
      open_ = true;
      in_stream_ = gps_device_.getInputStream();
      // start this runnable as thread:
      read_thread_ = new Thread(this, "GPSNmeaDataProcessor");
      read_thread_.setDaemon(true); // so thread is finished after exit of application
      read_thread_.start();

      sendInitNmea(gps_device_.getOutputStream());
    }
    catch (IOException e)
    {
      throw new GPSException(e.getMessage());
    }

  }

  /**
   * Initializes the nmea device. This is not really necessary, but tells the device
   * to send some information about itself.
   * @param outStream the stream to write to
   * @throws IOException if an error occurs.
   */
  private void sendInitNmea(OutputStream outStream) throws IOException
  {
    outStream.write(INIT_HOT_START.getBytes());
    outStream.write(13);
    outStream.write(10);
  }

  // ----------------------------------------------------------------------
  /**
   * Stopps the data processing. The Data Processor disconnects from the GPSDevice.
   *
   * @exception if an error occured on disconnecting.
   */
  public void close() throws GPSException
  {
    if (gps_device_ == null)
      throw new GPSException("no GPSDevice set!");
    open_ = false;
    gps_device_.close();
  }

  // ----------------------------------------------------------------------
  /**
   * Returns information about the gps connected (name of device, type of connection, etc.) This
   * information is for display to the user, not for further processing (may change without notice).
   *
   * @return information about the gps connected.
   */
  public String[] getGPSInfo()
  {
    String[] info = null;
    if(gps_infos_ == null)
    {
      // wait a little, as the device info is sent after initialization
      try
      {
        Thread.sleep(NMEA_DEVICE_INFO_WAIT_MS);
      } catch (InterruptedException ignore)
      {
      }
      if(gps_infos_ == null)
      {
        info = new String[] {"Unknown NMEA GPS"};
      }
    }

    if(info == null)
    {
      info = (String[]) gps_infos_.toArray(new String[gps_infos_.size()]);
    }
    return (info);
  }

  /**
   * Reads messages
   * @see java.lang.Runnable#run()
   */
  public void run()
  {
    readMessages();
  }

  // ----------------------------------------------------------------------
  /**
   * Requests the gps device to send the current position/heading/etc. periodically. This
   * implementation ignores the period set as all known NMEA devices send one sentence set every
   * second.
   *
   * @param period time in milliseconds between periodically sending position/heading/etc. This
   *          value may be changed by the gps device, so do not rely on the value given!
   * @return the period chosen by the gps device or 0 if the gps device is unable to send
   *         periodically. This implementation always return 1000.
   * @throws GPSException if the operation threw an exception (e.g. communication problem).
   */
  public long startSendPositionPeriodically(long period) throws GPSException
  {
    return (1000);
  }

  // ----------------------------------------------------------------------
  /**
   * Requests the gps device to stop to send the current position/heading/etc. periodically. This
   * implementation does nothing, as most NMEA devices cannot be stopped.
   *
   * @throws GPSException if the operation threw an exception (e.g. communication problem).
   */
  public void stopSendPositionPeriodically() throws GPSException
  {
    // do nothing, cannot stop!
  }

  // ----------------------------------------------------------------------
  /**
   * Reads and parses the NMEA sentences from the inputstream and fires events depending on the nmea
   * sentence read.
   */
  protected void readMessages()
  {
    if (logger_.isDebugEnabled())
      logger_.debug("start reading from GPSDevice...");

    char[] buffer;
    int count;
    int data;
    NMEA0183Sentence message;
    try
    {

      if (logger_.isDebugEnabled())
        logger_.debug("inputstream: " + in_stream_);

      if (!readGarbage()) // try to (re)sync with nmea stream
        return;

      int loopcount = 0;
      while (true)
      {
        loopcount++;
        count = 0;
        buffer = new char[MAX_NMEA_MESSAGE_LENGTH];

        while ((data = getNextByte()) != (char) 13) // read data until CR
        {
          if (count >= MAX_NMEA_MESSAGE_LENGTH - 1)
          {
            System.err.println("ERROR: max. message length exceeded! (" + count + "):" + new String(buffer));
            if (!readGarbage()) // try to (re)sync with nmea stream
              return;
            loopcount++;
            count = 0;
            buffer = new char[MAX_NMEA_MESSAGE_LENGTH];
          } else
          {
            if (data != (char) 10) // ignore LF
            {
              buffer[count] = (char) data; // add data to the buffer
              count++;
            }
          }
        } // end of while (read until end of line)

        if (buffer[0] != '$') // no valid nmea sentence
        {
          if (!readGarbage()) // try to (re)sync with nmea stream
            return;
        } else
        { // valid sentence, no garbage
          try
          {
            message = new NMEA0183Sentence(buffer, 0, count);

            buffer[count] = 13; // add CR from NMEA message
            buffer[count + 1] = 10; // add LF from NMEA message
            fireRawDataReceived(buffer, 0, count + 2);
            if (logger_message_.isDebugEnabled())
            {
              logger_message_.debug("message: '" + message + "'");
              logger_message_.debug("sentenceId: '" + message.getSentenceId() + "'");
            }

            if (!message.isValid() && ignore_invalid_checksum_ && print_ignore_warning_)
            {
              logger_.error("ERORR: invalid checksum in NMEA message: " + message);
              logger_.error("checksum of sentence: " + message.getChecksum() + ", calculated checksum: " + message.getCalculatedChecksum());
              logger_.warn("WARNING: As you chose to ingore invalid messages, this message is only printed once!");
              print_ignore_warning_ = false;
            }

            if (ignore_invalid_checksum_ || message.isValid() || message.getSentenceId().equals("RFTXT"))
            {
              try
              {
                processNmeaSentence(message);
              }
              catch (Exception e)
              {
                logger_.error("ERROR: Exception thrown on processing of NMEA sentences:");
                logger_.error(message);
                e.printStackTrace();
              }
            } else
            {
              logger_.error("ERORR: invalid checksum in NMEA message: " + message);
              logger_.error("checksum of sentence: " + message.getChecksum() + ", calculated checksum: " + message.getCalculatedChecksum());
            }
          }
          catch (Exception e)
          {
            logger_.error("ERROR: Exception thrown on creation or processing of NMEA sentences:");
            logger_.error(new String(buffer));
            e.printStackTrace();
          }
        }

        if (delay_time_ > 0)
        {
          try
          {
            Thread.sleep(delay_time_);
          }
          catch (InterruptedException ie)
          {
          }
        }
      }
    }
    catch (IOException ioe)
    {
      if (open_) // otherwise, this is the reason for the exception!
        ioe.printStackTrace();
    }
  }

  /**
   * Fetches the next byte from the input stream and returns it.
   *
   * @return the next byte from the input stream.
   * @throws IOException if an error occurs.
   */
  private int getNextByte() throws IOException
  {
    int data = -1;
    // rxtx 2.1.7 on windows has the problem (?) that even if the stream is not ended
    // it returns -1 here (happens in between nmea sentences)
    // so we just wait a little and then retry to read
    while ((data = in_stream_.read()) < 0)
    {
      try
      {
        Thread.sleep(EOFREACHED_WAIT_MS);
      } catch (InterruptedException ignore)
      {
      }
    }
    return data;
  }

  // ----------------------------------------------------------------------
  /**
   * Reads garbage from the input stream and tries to find the beginning of the next valid nmea
   * sentence (by searching the next CR/LF pair).
   *
   * @return true if everything was fine, false otherwise (e.g. if the end of the input stream was
   *         reached!)
   */
  protected boolean readGarbage()
  {
    int data;
    try
    {
      while (true)
      {
        // read until CR/LF>
        while ((data = getNextByte()) != (char) 13) // read data until CR
        {
        }
        data = getNextByte(); // char after CR
        if (data == (char) 10) // linefeed
        {
          return (true);
        }
        if (logger_message_.isDebugEnabled())
          logger_message_.debug("reading garbage...");
      }
    }
    catch (IOException ioe)
    {
      System.err.println("GPS Nmea Reading: IOException on beginning of reading, try once more: " + ioe.getMessage());
      return (false);
    }
  }

  // ----------------------------------------------------------------------
  /**
   * Processes the different nmea sentences.
   *
   * @param sentence a NMEA sentence.
   */
  protected void processNmeaSentence(NMEA0183Sentence sentence)
  {
    String id = sentence.getSentenceId().toUpperCase();
    if (id.equals("GLL"))
    {
      processGLL(sentence);
      return;
    }
    if (id.equals("RMC"))
    {
      processRMC(sentence);
      return;
    }
    if (id.equals("HDG"))
    {
      processHDG(sentence);
      return;
    }
    if (id.equals("GGA"))
    {
      processGGA(sentence);
      return;
    }
    if (id.equals("GSV"))
    {
      processGSV(sentence);
      return;
    }
    if (id.equals("DBT"))
    {
      processDBT(sentence);
      return;
    }

    if (id.equals("VTG"))
    {
      processVTG(sentence);
      return;
    }

    if (id.equals("HDT"))
    {
      processHDT(sentence);
      return;
    }

    if (id.equals("RME"))
    {
      processRME(sentence);
      return;
    }

    if (id.equals("RFTXT"))
    {
      processRFTXT(sentence);
      return;
    }
    // PHILIPPE START
    if (id.equals("GSA"))
    {
    	processGSA(sentence);
    	return;
    }
    // PHILIPPE STOP
  }

  // ----------------------------------------------------------------------
  /**
   * Processes a GLL nmea sentences and fires the specific events about the information contained in
   * this sentence (property name GPSDataProcessor.LOCATION).
   *
   * @param sentence a NMEA sentence.
   */
  protected void processGLL(NMEA0183Sentence sentence)
  {
    if (logger_nmea_.isDebugEnabled())
      logger_nmea_.debug("GLL detected: " + sentence);
    List data_fields = sentence.getDataFields();

    String valid = (String) data_fields.get(5);
    if (valid.equals("V")) // invalid
      return;

    String latitude = (String) data_fields.get(0);
    String north_south = (String) data_fields.get(1);
    String longitude = (String) data_fields.get(2);
    String east_west = (String) data_fields.get(3);
    String utc_time = (String) data_fields.get(4);

    // check for empty messages:
    if (latitude.length() == 0)
      return;

    double wgs84_lat = nmeaLatOrLongToWGS84(latitude);
    double wgs84_long = nmeaLatOrLongToWGS84(longitude);

    GPSPosition pos = new GPSPosition(wgs84_lat, north_south, wgs84_long, east_west);
    changeGPSData(LOCATION, pos);
  }

  // ----------------------------------------------------------------------
  /**
   * Processes a DBT (Depth below Transducer) nmea sentences and fires the specific events about the
   * information contained in this sentence (property name GPSDataProcessor.DEPTH) (depth in
   * meters!)
   *
   * @param sentence a NMEA sentence.
   */
  protected void processDBT(NMEA0183Sentence sentence)
  {
    if (logger_nmea_.isDebugEnabled())
      logger_nmea_.debug("DBT detected: " + sentence);
    List data_fields = sentence.getDataFields();
    String depth_str = (String) sentence.getDataFields().get(2);
    Float depth = null;
    try
    {
      depth = new Float(depth_str);
      changeGPSData(DEPTH, depth);
    }
    catch (NumberFormatException nfe)
    {
      if (logger_nmea_.isDebugEnabled())
        nfe.printStackTrace();
    }
  }

  // ----------------------------------------------------------------------
  /**
   * Processes a GGA nmea sentences and fires the specific events about the information contained in
   * this sentence (property name GPSDataProcessor.LOCATION, GPSDataProcessor.ALTITUDE,
   * GPSDataProcessor.NUMBER_SATELLITES).
   *
   * @param sentence a NMEA sentence.
   */
  protected void processGGA(NMEA0183Sentence sentence)
  {
    if (logger_nmea_.isDebugEnabled())
      logger_nmea_.debug("GGA detected: " + sentence);
    List data_fields = sentence.getDataFields();
    String latitude = (String) data_fields.get(1);
    String north_south = (String) data_fields.get(2);
    String longitude = (String) data_fields.get(3);
    String east_west = (String) data_fields.get(4);
    int valid_fix = Integer.parseInt((String) data_fields.get(5));

    if (valid_fix == 0)
      return;

    // check for empty messages:
    if (latitude.length() == 0)
      return;

    double wgs84_lat = nmeaLatOrLongToWGS84(latitude);
    double wgs84_long = nmeaLatOrLongToWGS84(longitude);

    GPSPosition pos = new GPSPosition(wgs84_lat, north_south, wgs84_long, east_west);
    changeGPSData(LOCATION, pos);

    String num_sat = (String) data_fields.get(6);
    changeGPSData(NUMBER_SATELLITES, new Integer(num_sat));

    // PHILIPPE START
    String hdop = (String) data_fields.get(7);
    changeGPSData(HDOP, new Float(hdop));
    // PHILIPPE END
    try
    {
      String altitude = (String) data_fields.get(8);
      changeGPSData(ALTITUDE, new Float(altitude));
    }
    catch (NumberFormatException nfe)
    {
      if (logger_nmea_.isDebugEnabled())
        nfe.printStackTrace();
    }
  }

  // PHILIPPE START
  // ----------------------------------------------------------------------
  /**
   * Processes a GSA nmea sentences and fires the specific events about the information contained in
   * this sentence (property name {@link GPSDataProcessor#PDOP}, {@link GPSDataProcessor#HDOP}
   * {@link GPSDataProcessor#VDOP} and {@link GPSDataProcessor#IDS_SATELLITES}).
   *
   * @param sentence a NMEA sentence.
   */
  protected void processGSA(NMEA0183Sentence sentence)
  {
    if (logger_nmea_.isDebugEnabled())
      logger_nmea_.debug("GSA detected: " + sentence);
    List data_fields = sentence.getDataFields();
    Integer[] satellites_ids = new Integer[12];
    String pdop = (String) data_fields.get(14);
    String hdop = (String) data_fields.get(15);
    String vdop = (String) data_fields.get(16);
    int valid_fix = Integer.parseInt((String) data_fields.get(1));

    for (int i=0; i < 12; i++) {
    	String id = (String) data_fields.get(i+2);
    	if (id != null && id.length()>0) {
    		satellites_ids[i] = new Integer(Integer.parseInt(id));
    	}
    }

    changeGPSData(PDOP,new Float(pdop));

    changeGPSData(HDOP,new Float(hdop));
    
    changeGPSData(VDOP, new Float(vdop));
    
    changeGPSData(IDS_SATELLITES, satellites_ids);

  }
  // PHILIPPE END
  
  // ----------------------------------------------------------------------
  /**
   * Processes a RMC nmea sentences and fires the specific events about the information contained in
   * this sentence (property name GPSDataProcessor.LOCATION, GPSDataProcessor.SPEED).
   *
   * @param sentence a NMEA sentence.
   */
  protected void processRMC(NMEA0183Sentence sentence)
  {
    if (logger_nmea_.isDebugEnabled())
      logger_nmea_.debug("RMC detected: " + sentence);
    List data_fields = sentence.getDataFields();
    String latitude = (String) data_fields.get(2);
    String north_south = (String) data_fields.get(3);
    String longitude = (String) data_fields.get(4);
    String east_west = (String) data_fields.get(5);

    // check for empty messages:
    if (latitude.length() == 0)
      return;

    double wgs84_lat = nmeaLatOrLongToWGS84(latitude);
    double wgs84_long = nmeaLatOrLongToWGS84(longitude);

    GPSPosition pos = new GPSPosition(wgs84_lat, north_south, wgs84_long, east_west);
    changeGPSData(LOCATION, pos);

    String speed_knots = (String) data_fields.get(6);
    try
    {
      float speed = Float.parseFloat(speed_knots);
      speed = speed / KM2NAUTIC;

      changeGPSData(SPEED, new Float(speed));
    }
    catch (NumberFormatException nfe)
    {
      if (logger_nmea_.isDebugEnabled())
        nfe.printStackTrace();
    }
  }

  // ----------------------------------------------------------------------
  /**
   * Processes a GSV nmea sentences and fires the specific events about the information contained in
   * this sentence (property name GPSDataProcessor.SATELLITE_INFO). This event is not fired for each
   * RMC sentence as there might more than one GSV sentences belonging to one GSV block. So the
   * event is fired only on occurrence of the last message of the block. Missing messages of a block
   * are detected and the whole block is thrown away in the case of incorrect occurrence of
   * messages.
   *
   * @param sentence a NMEA sentence.
   */
  protected void processGSV(NMEA0183Sentence sentence)
  {
    if (logger_nmea_.isDebugEnabled())
      logger_nmea_.debug("GSV detected: " + sentence);

    try
    {
      List data_fields = sentence.getDataFields();
      int total_number_messages = Integer.parseInt((String) data_fields.get(0));
      int message_number = Integer.parseInt((String) data_fields.get(1));
      int number_satellites = Integer.parseInt((String) data_fields.get(2));

      // plausability check for gsv sentences:
      if ((message_number != last_gsv_message_number_ + 1) || ((message_number > 1) && (number_satellites != satellite_infos_.length)))
      {
        if (logger_nmea_.isDebugEnabled())
          logger_nmea_.debug("GSV-message in wrong order, ignoring it!");
        // System.err.println("WARNING: NMEA message: GSV-message part in wrong order!");
        last_gsv_message_number_ = 0; // reset, so ready for next gsv message
        return;
      }
      last_gsv_message_number_ = message_number;

      if (message_number == 1)
      {
        satellite_infos_ = new SatelliteInfo[number_satellites];
        satellite_info_count_ = 0;
      } else
      {
        if (satellite_infos_ == null)
          return;
      }

      int sat_count = 0;
      while ((sat_count < 4) && ((message_number - 1) * 4 + sat_count < number_satellites))
      {
        int prn = Integer.parseInt((String) data_fields.get(3 + 4 * sat_count));
        float elevation = Float.parseFloat((String) data_fields.get(4 + 4 * sat_count));
        float azimuth = Float.parseFloat((String) data_fields.get(5 + 4 * sat_count));
        String srn_string = (String) data_fields.get(6 + 4 * sat_count);
        int srn;
        if (srn_string.length() > 0)
          srn = Integer.parseInt((String) data_fields.get(6 + 4 * sat_count));
        else
          srn = 0;
        satellite_infos_[satellite_info_count_++] = new SatelliteInfo(prn, elevation, azimuth, srn);
        sat_count++;
      }

      if (message_number == total_number_messages) // last message
      {
        changeGPSData(SATELLITE_INFO, satellite_infos_);
        last_gsv_message_number_ = 0;
      }
    }
    catch (NumberFormatException nfe)
    {
      if (logger_nmea_.isDebugEnabled())
        nfe.printStackTrace();
    }
    catch (ArrayIndexOutOfBoundsException aiobe)
    {
      System.err.println("WARNING: ArrayIndexOutOfBoundsException in NMEA Sentence: " + sentence + ": " + aiobe.getMessage());
    }
  }

  // ----------------------------------------------------------------------
  /**
   * Processes a HDG nmea sentences and fires the specific events about the information contained in
   * this sentence (property name GPSDataProcessor.HEADING).
   *
   * @param sentence a NMEA sentence.
   */
  protected void processHDG(NMEA0183Sentence sentence)
  {
    if (logger_nmea_.isDebugEnabled())
      logger_nmea_.debug("HDG detected: " + sentence);
    String heading_str = (String) sentence.getDataFields().get(0);
    Float heading = null;
    try
    {
      heading = new Float(heading_str);
      changeGPSData(HEADING, heading);
    }
    catch (NumberFormatException nfe)
    {
      if (logger_nmea_.isDebugEnabled())
        nfe.printStackTrace();
    }
  }

  // ----------------------------------------------------------------------
  /**
   * Processes a RME nmea sentences (garmin specific) and fires the specific events about the
   * information contained in this sentence (property name GPSDataProcessor.EPE).
   *
   * @param sentence a NMEA sentence.
   */
  protected void processRME(NMEA0183Sentence sentence)
  {
    if (logger_nmea_.isDebugEnabled())
      logger_nmea_.debug("RME detected: " + sentence);
    String horizontal_str = (String) sentence.getDataFields().get(0);
    String vertical_str = (String) sentence.getDataFields().get(2);
    String spherical_str = (String) sentence.getDataFields().get(2);
    Double horizontal_error = null;
    Double vertical_error = null;
    Double spherical_error = null;
    try
    {
      horizontal_error = new Double(horizontal_str);
      vertical_error = new Double(vertical_str);
      spherical_error = new Double(spherical_str);
      changeGPSData(EPE, new GPSPositionError(spherical_error.doubleValue(), horizontal_error.doubleValue(), vertical_error.doubleValue()));
    }
    catch (NumberFormatException nfe)
    {
      if (logger_nmea_.isDebugEnabled())
        nfe.printStackTrace();
    }
  }

  // ----------------------------------------------------------------------
  /**
   * Processes a VTG nmea sentences and fires the specific events about the information contained in
   * this sentence (property name GPSDataProcessor.SPEED).
   *
   * @param sentence a NMEA sentence.
   *
   * @link http://home.mira.net/~gnb/gps/nmea.html#gpvtg
   */

  protected void processVTG(NMEA0183Sentence sentence)
  {
    if (logger_nmea_.isDebugEnabled())
      logger_nmea_.debug("VTG detected: " + sentence);
    List data_fields = sentence.getDataFields();
    String trueCourse = (String) data_fields.get(0); // True course made good over ground,
                                                            // degrees
    // String magneticCourse = (String)data_fields.get(2); // Magnetic course made good over
    // ground, degrees
    // String groundSpeedKnots = (String)data_fields.get(4); // Ground speed, N=Knots
    String groundSpeedKms = (String) data_fields.get(6); // Ground speed, K=Kilometers per
                                                                // hour

    Float heading = null;
    try
    {
      heading = new Float(trueCourse);
      changeGPSData(HEADING, heading);
    }
    catch (NumberFormatException nfe)
    {
      if (logger_nmea_.isDebugEnabled())
        nfe.printStackTrace();
    }

    try
    {
      float speed = Float.parseFloat(groundSpeedKms);
      // speed = speed / KM2NAUTIC;
      changeGPSData(SPEED, new Float(speed));
    }
    catch (NumberFormatException nfe)
    {
      if (logger_nmea_.isDebugEnabled())
        nfe.printStackTrace();
    }
  }

  // ----------------------------------------------------------------------
  /**
   * Processes a HDT nmea sentences and fires the specific events about the information contained in
   * this sentence (property name GPSDataProcessor.HEADING).
   *
   * @param sentence a NMEA sentence.
   *
   * @link http://home.mira.net/~gnb/gps/nmea.html#gphdt
   */
  protected void processHDT(NMEA0183Sentence sentence)
  {
    if (logger_nmea_.isDebugEnabled())
      logger_nmea_.debug("HDT detected: " + sentence);
    String heading_str = (String) sentence.getDataFields().get(0);
    Float heading = null;
    try
    {
      heading = new Float(heading_str);
      changeGPSData(HEADING, heading);
    }
    catch (NumberFormatException nfe)
    {
      if (logger_nmea_.isDebugEnabled())
        nfe.printStackTrace();
    }
  }

  protected void processRFTXT(NMEA0183Sentence sentence)
  {
    if (logger_nmea_.isDebugEnabled())
      logger_nmea_.debug("RFTXT detected: " + sentence);
    String data = (String) sentence.getDataFields().get(0);
    if(gps_infos_ == null || data.startsWith("Version:"))
    {
      gps_infos_ = new ArrayList();
    }
    gps_infos_.add(data);
  }

  // protected void changeGPSData(String key, Object value)
  // throws IllegalArgumentException
  // {
  // // System.out.println("GPSNmeaDataProc: changeGPSData with key: "+key+" called");
  // if(key.equals(NMEA_LOCATION))
  // {
  // super.changeGPSData(GPSDataProcessor.LOCATION,
  // getGPSPosition((NMEA0183Sentence)value));
  // return;
  // }
  // if(key.equals(NMEA_HEADING))
  // {
  // super.changeGPSData(GPSDataProcessor.HEADING,
  // new Double(getHeading((NMEA0183Sentence)value)));
  // return;
  // }
  // // don't know what to do, maybe someone else knows what to do
  // // with a NMEA Sentence:
  // super.changeGPSData(key,value);
  // }

  // ----------------------------------------------------------------------
  /**
   * Returns the last received position from the GPSDevice or <code>null</code> if no position was
   * retrieved until now.
   *
   * @return the position from the GPSDevice.
   */

  public GPSPosition getGPSPosition()
  {
    return ((GPSPosition) getGPSData(LOCATION));
  }

  // ----------------------------------------------------------------------
  /**
   * Returns the last received position from the GPSDevice or <code>null</code> if no position was
   * retrieved until now.
   *
   * @return the position from the GPSDevice.
   */

  protected GPSPosition getGPSPosition(NMEA0183Sentence sentence)
  {
    if (logger_.isDebugEnabled())
      logger_.debug("position sentence = " + sentence);

    return (nmeaGllToPos(sentence));
  }

  protected static GPSPosition nmeaGllToPos(NMEA0183Sentence sentence)
  {
    if (sentence == null)
      return (null);

    List data_fields = sentence.getDataFields();
    String latitude = (String) data_fields.get(0);
    String north_south = (String) data_fields.get(1);
    String longitude = (String) data_fields.get(2);
    String east_west = (String) data_fields.get(3);

    double wgs84_lat = nmeaLatOrLongToWGS84(latitude);
    double wgs84_long = nmeaLatOrLongToWGS84(longitude);

    GPSPosition pos = new GPSPosition(wgs84_lat, north_south, wgs84_long, east_west);

    return (pos);
  }

  // ----------------------------------------------------------------------
  /**
   * Converts the String format used in NMEA messages to represent longitude or latitude to a double
   * value (degrees). NMEA uses e.g. 4916.45 to represent 49 degrees, 16.45 minutes (latitude) or
   * 02311.12 for 23 degrees 11.12 minutes (longitude). So this method converts e.g. 4916.45 to the
   * WGS84 system representation (degrees only) which is 49.27416 degrees.
   *
   * @param nmea_pos the NMEA string representation for global postition.
   * @exception NumberFormatException if the String could not be converted.
   */

  protected static double nmeaLatOrLongToWGS84(String nmea_pos) throws NumberFormatException
  {
    int comma_pos = nmea_pos.indexOf('.');
    if ((comma_pos != 4) && (comma_pos != 5))
      throw new NumberFormatException("unknown NMEA position format: '" + nmea_pos + "'");

    String wgs84_deg = nmea_pos.substring(0, comma_pos - 2);
    String wgs84_min = nmea_pos.substring(comma_pos - 2);
    double wgs84_pos = Double.parseDouble(wgs84_deg) + Double.parseDouble(wgs84_min) / 60.0;
    return (wgs84_pos);
  }

  // ----------------------------------------------------------------------
  /**
   * Returns the last received heading (direction) from the GPSDevice or <code>-1.0</code> if no
   * heading was retrieved until now.
   *
   * @return the heading from the GPSDevice.
   */
  public float getHeading()
  {
    // NMEA0183Sentence sentence = (NMEA0183Sentence)getGPSData(NMEA_HEADING);
    // if(sentence == null)
    // return(-1.0);

    Float heading = (Float) getGPSData(HEADING);
    if (heading != null)
      return (heading.floatValue());
    else
      return (-1);
  }

  // ----------------------------------------------------------------------
  /**
   * Returns the last received heading (direction) from the GPSDevice or <code>-1.0</code> if no
   * heading was retrieved until now.
   *
   * @return the heading from the GPSDevice.
   */
  protected float getHeading(NMEA0183Sentence sentence)
  {
    if (sentence == null)
      return (-1.0f);

    String heading_str = (String) sentence.getDataFields().get(0);
    try
    {
      return (Float.parseFloat(heading_str));
    }
    catch (NumberFormatException nfe)
    {
      nfe.printStackTrace();
    }
    return (-1.0f);
  }
}
