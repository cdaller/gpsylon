/***********************************************************************
 * @(#)$RCSfile$   $Revision$ $Date$
 *
 * Copyright (c) 2001 IICM, Graz University of Technology
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



import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JFrame;
import org.dinopolis.gpstool.gpsinput.garmin.GPSGarminDataProcessor;
import org.dinopolis.gpstool.gpsinput.garmin.GarminPVT;
import org.dinopolis.gpstool.gpsinput.nmea.GPSNmeaDataProcessor;
import org.dinopolis.util.commandarguments.CommandArgumentException;
import org.dinopolis.util.commandarguments.CommandArguments;

//----------------------------------------------------------------------
/**
 * Demo application to show the usage of this package (read and
 * interprete gps data from various devices (serial, file, ...).
 *
 * @author Sandra Brueckler, Christof Dallermassl, Stefan Feitl
 * @version $Revision$
 */

public class GPSTool implements PropertyChangeListener
{
  protected boolean gui_ = true;
  protected GPSDataProcessor gps_processor_;
  
  public GPSTool()
  {
  }
  
  public void open(GPSDevice gps_device, GPSDataProcessor gps_processor) throws GPSException
  {
    gps_processor.setGPSDevice(gps_device);
    gps_processor.open();
    gps_processor_ = gps_processor;
  }

  public void close() throws GPSException
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
    System.out.println("GPSTool 0.7.0 - Communication between GPS-Devices and Computers via serial port");
    System.out.println("(c) 2000-2003 Sandra Brueckler, Christof Dallermassl, Stefan Feitl\n");
    System.out.println("Usage: java org.dinopolis.gpstool.GPSTool [options]\n");
    System.out.println("Options:");
    System.out.println("--device, -d <device>, e.g. -d /dev/ttyS0 or COM1 (defaults depending on OS).");
    System.out.println("--speed,  -s <speed>, e.g. -s 4800 (default for nmea, 9600 for garmin).");
    System.out.println("--file,   -f <filename>, the gps data is read from the given file.");
    System.out.println("--nmea,   -n, the gps data is interpreted as NMEA data (default).");
    System.out.println("--garmin, -g, the gps data is interpreted as garmin data.");
    System.out.println("--nogui, no frame is opened.\n");
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
        // If no arguments are specified, help page is displayed by default
    if (Array.getLength(arguments) == 0) {arguments = new String[] {"-h"};}

    String[] valid_args =
      new String[] {"device*","d*","help","h","speed#","s#","file*","f*",
                    "nmea","n","garmin","g","test","nogui","printtrack",
                    "printwaypoint","printroute","printdeviceinfo","printpvt"};

        // Check command arguments
        // Throw exception if arguments are invalid
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

        // Set default values
    String filename = null;
    String serial_port_name = null;
    int serial_port_speed = -1;
    GPSDataProcessor gps_data_processor;

        // Handle given command arguments
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
    {
      gps_data_processor = new GPSNmeaDataProcessor();
      serial_port_speed = 4800;
    }
    
        // Define device to read data from
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

        // Initialize GPS-Device and GPS-Processor
    try
    {
      gps_device.init(environment);
      GPSTool gps_tool = new GPSTool();
      gps_tool.gui_ = !args.isSet("nogui");
    
      gps_tool.open(gps_device,gps_data_processor);

      boolean work_done = false;
      if(args.isSet("printwaypoint"))
      {
        List waypoints = ((GPSGarminDataProcessor)gps_data_processor).getWaypoints(0L);
        System.out.println(waypoints);
        work_done = true;
      }
      else
        if(args.isSet("printtrack"))
        {
          List tracks = ((GPSGarminDataProcessor)gps_data_processor).getTracks(0L);
          System.out.println(tracks);
          work_done = true;
        }
        else
          if(args.isSet("printroute"))
          {
            List routes = ((GPSGarminDataProcessor)gps_data_processor).getRoutes(0L);
            System.out.println(routes);
            work_done = true;
          }
          else
            if(args.isSet("printpvt"))
            {
              GarminPVT pvt = ((GPSGarminDataProcessor)gps_data_processor).getPVT(0L);
              System.out.println(pvt);
              work_done = true;
            }
            else
              if(args.isSet("printdeviceinfo"))
              {
                System.out.println(((GPSGarminDataProcessor)gps_data_processor).getProductInfo());
                System.out.println(((GPSGarminDataProcessor)gps_data_processor).getCapabilities());
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
    catch(IOException ioe)
    {
      ioe.printStackTrace();
    }
  } // end of main ()
}
