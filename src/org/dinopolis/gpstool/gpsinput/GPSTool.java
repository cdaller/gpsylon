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

import org.dinopolis.util.commandarguments.CommandArguments;
import org.dinopolis.util.commandarguments.CommandArgumentException;
import java.util.Hashtable;
import javax.swing.JFrame;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

//----------------------------------------------------------------------
/**
 * Demo application to show the usage of this package (read and
 * interpret gps data from various devices (serial, file, ...).
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GPSTool implements PropertyChangeListener
{
  protected boolean gui_ = true;
  protected GPSDataProcessor gps_processor_;
  
  public GPSTool()
  {
  }
  
  public void open(GPSDevice gps_device, GPSDataProcessor gps_processor)
    throws GPSException
  {
    gps_processor.setGPSDevice(gps_device);
    gps_processor.open();
    gps_processor_ = gps_processor;
  }

  public void close()
    throws GPSException
  {
    gps_processor_.close();
  }

  public void registerEvents()
  {
    System.out.println("------------------------------------------------------------"); 
    System.out.println("------------------------------------------------------------");
    System.out.println("------------------------------------------------------------");
   try
    {
      JFrame frame = null;
      if(gui_)
      {
	GPSInfoPanel panel = new GPSInfoPanel();
	frame = new JFrame("GPS Info");
	frame.getContentPane().add(panel);
	frame.pack();
	frame.setVisible(true);
      
	gps_processor_.addGPSDataChangeListener(GPSDataProcessor.LOCATION,panel);
	gps_processor_.addGPSDataChangeListener(GPSDataProcessor.HEADING,panel);
	gps_processor_.addGPSDataChangeListener(GPSDataProcessor.ALTITUDE,panel);
	gps_processor_.addGPSDataChangeListener(GPSDataProcessor.SPEED,panel);
	gps_processor_.addGPSDataChangeListener(GPSDataProcessor.NUMBER_SATELLITES,panel);
	gps_processor_.addGPSDataChangeListener(GPSDataProcessor.SATELLITE_INFO,panel);
      }
      gps_processor_.addGPSDataChangeListener(GPSDataProcessor.LOCATION,this);
      gps_processor_.addGPSDataChangeListener(GPSDataProcessor.HEADING,this);
      gps_processor_.addGPSDataChangeListener(GPSDataProcessor.ALTITUDE,this);
      gps_processor_.addGPSDataChangeListener(GPSDataProcessor.SPEED,this);
      gps_processor_.addGPSDataChangeListener(GPSDataProcessor.NUMBER_SATELLITES,this);
      gps_processor_.addGPSDataChangeListener(GPSDataProcessor.SATELLITE_INFO,this);

      System.out.println("press enter to quit");
      System.in.read();
      if(gui_)
      {
	frame.setVisible(false);
	frame.dispose();
      }
//        try
//        {
//          Thread.sleep(200000);
//        }
//        catch(InterruptedException ie)
//        {}
//        gps_processor_.close();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
   
  }

  public void propertyChange(PropertyChangeEvent event)
  {
    Object value = event.getNewValue();
    String name = event.getPropertyName();
    if(name.equals(GPSDataProcessor.SATELLITE_INFO))
    {
      SatelliteInfo[] infos = (SatelliteInfo[])value;
      SatelliteInfo info;
      for(int count=0; count < infos.length; count++)
      {
	info = infos[count];
	System.out.println("sat "+info.getPRN()+": elev="+info.getElevation()
			   + " azim="+info.getAzimuth()+" dB="+info.getSNR());
      }
    }
    else
      System.out.println(event.getPropertyName()+": "+event.getNewValue());
//      System.out.println("EVENT:");
//      System.out.println("source: "+event.getSource());
//      System.out.println("Name  : "+event.getPropertyName());
//      System.out.println("Old   : "+event.getOldValue());
//      System.out.println("New   : "+event.getNewValue());
//      GPSDataProcessor source = (GPSDataProcessor)event.getSource ();
//      System.out.println("POS:"+source.getGPSPosition());
//      System.out.println("HDG:"+source.getHeading());
  }

//----------------------------------------------------------------------
/**
 * prints the help messages
 */

  public static void printHelp()
  {
    System.out.println("GPSTool");
    System.out.println("Usage:");
    System.out.println("java org.dinopolis.gpstool.GPSTool [options]");
    System.out.println("options:");
    System.out.println("--device, -d <device>, e.g. --device /dev/ttyS1 or COM1 (both default depending on OS)");
    System.out.println("--speed, -s <speed>, e.g. --speed 4800 (default for nmea, 9600 for garmin)");
    System.out.println("--file, -f <filename>, the gps data is read from the given file.");
    System.out.println("--nmea, -n, the gps data is interpreted as NMEA data (default).");
    System.out.println("--garmin, -g, the gps data is interpreted as garmin data.");
    System.out.println("--nogui, no frame is opened.");
    System.out.println("--printtrack, print tracks.");
    System.out.println("--printwaypoint, print waypoints.");
    System.out.println("--printroute, print route.");
  }

  private static void test()
  {
//     double latitude = GPSNmeaDataProcessor.nmeaPosToWGS84("4703.555");
//     double longitude = GPSNmeaDataProcessor.nmeaPosToWGS84("01527.553");
//     System.out.println(new GPSPosition(latitude,"N",longitude,"E"));
  }

  
  public static void main (String[] arguments) 
  {
    String[] valid_args =
      new String[] {"device*","d*","help","h","speed#","s#","file*","f*",
		    "nmea","n","test","nogui","printtrack","printwaypoint",
		    "printroute","garmin","printdeviceinfo","printpvt"};

    CommandArguments args = null;
    try
    {
      args = new CommandArguments(arguments,valid_args);
    }
    catch(CommandArgumentException cae) 
    {
      cae.printStackTrace();
      return;
    }

    String filename = null;
    String serial_port_name = null;
    int serial_port_speed = -1;
    GPSDataProcessor gps_data_processor;


    if (args.isSet("help") || (args.isSet("h")))
    {
      printHelp();
      return;
    }
    
    if (args.isSet("test"))
    {
      test();
      return;
    }
    
    if (args.isSet("device"))
    {
      serial_port_name = (String)args.getValue("device");
    }
    else 
      if (args.isSet("d"))
      {
        serial_port_name = (String)args.getValue("d");
      }
    
    if (args.isSet("speed"))
    {
      serial_port_speed = ((Integer)args.getValue("speed")).intValue();
    }
    else 
      if (args.isSet("s"))
      {
        serial_port_speed = ((Integer)args.getValue("s")).intValue();
      }
    
    if (args.isSet("file"))
    {
      filename = (String)args.getValue("file");
    }
    else 
      if (args.isSet("f"))
      {
        filename = (String)args.getValue("f");
      }
    
    if (args.isSet("garmin") || args.isSet("g"))
    {
      gps_data_processor = new GPSGarminDataProcessor();
      serial_port_speed = 9600;
    }
    else
//      if (args.isSet("nmea") || args.isSet("n"))
    {
      gps_data_processor = new GPSNmeaDataProcessor();
      serial_port_speed = 4800;
    }
    
    GPSDevice gps_device;
    Hashtable environment = new Hashtable();
    if (filename != null)
    {
      environment.put(GPSFileDevice.PATH_NAME_KEY,filename);
      gps_device = new GPSFileDevice();
    }
    else
    {
      if (serial_port_name != null)
        environment.put(GPSSerialDevice.PORT_NAME_KEY,serial_port_name);
      if (serial_port_speed > -1)
        environment.put(GPSSerialDevice.PORT_SPEED_KEY,new Integer(serial_port_speed));
      gps_device = new GPSSerialDevice();
    }
    try
    {
      gps_device.init(environment);
      GPSTool gps_tool = new GPSTool();
      gps_tool.gui_ = !args.isSet("nogui");
    
      gps_tool.open(gps_device,gps_data_processor);

      boolean work_done = false;
      if(args.isSet("printwaypoint"))
      {
	((GPSGarminDataProcessor)gps_data_processor).printWaypointData();
	work_done = true;
      }
      else
      if(args.isSet("printtrack"))
      {
	((GPSGarminDataProcessor)gps_data_processor).printTrackData();
	work_done = true;
      }
      else
      if(args.isSet("printroute"))
      {
	((GPSGarminDataProcessor)gps_data_processor).printRouteData();
	work_done = true;
      }
      else
      if(args.isSet("printpvt"))
      {
	((GPSGarminDataProcessor)gps_data_processor).printPVTData();
	work_done = true;
      }
      else
      if(args.isSet("printdeviceinfo"))
      {
	System.out.println("product id:"+GPSGarminDataProcessor.PROD_ID);
	System.out.println("product sw:"+GPSGarminDataProcessor.PROD_SW);
	System.out.println("product name:"+GPSGarminDataProcessor.PROD_NAME);
	String[] capabilities = GPSGarminDataProcessor.PROD_CAP;
	for(int count = 0; count < capabilities.length; count++)
	  System.out.println("product cap:"+capabilities[count]);
	work_done = true;
      }
      
      if(!work_done)
	gps_tool.registerEvents();
      gps_tool.close();
    }
    catch(GPSException e)
    {
      e.printStackTrace();
    }
  } // end of main ()

}






