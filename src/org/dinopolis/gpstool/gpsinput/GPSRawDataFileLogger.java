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
import java.io.Writer;
import java.io.FileWriter;

import org.dinopolis.util.Debug;


//----------------------------------------------------------------------
/**
 * This class logs gps raw data to a file.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GPSRawDataFileLogger implements GPSRawDataListener
{
  protected Writer out_; 
  protected String log_filename_;
    /** if set to true, the file is reopened everytime a log message
        is written, and closed afterwards again. If set to false, the
        file is left opened. */
  protected boolean always_close_ = false;
    /** if an i/o error occured this flag is set to prevent further
        tries to write to the log file */
    protected boolean io_error_occured_ = false;

  public GPSRawDataFileLogger(String log_filename)
  {
    log_filename_ = log_filename;
  }

//----------------------------------------------------------------------
/**
 * Informs a GPSRawDataListener about received raw data from a
 * gps device.
 *
 * @param raw_data the raw data received from the gps device.
 */
  public void gpsRawDataReceived(char[] raw_data, int offset, int length)
  {
    if(io_error_occured_)
      return;
    try
    {
      if(always_close_ || out_ == null)
        out_ = new FileWriter(log_filename_,true); //append data to file
      out_.write(raw_data,offset,length);
      out_.flush();
      if(always_close_)
        out_.close();
    }
    catch(IOException ioe)
    {
      System.err.println("Cannot open/write to logfile '"+log_filename_
			 +"' - stop logging!");
      ioe.printStackTrace();
      io_error_occured_ = true;
    }
  }


}




