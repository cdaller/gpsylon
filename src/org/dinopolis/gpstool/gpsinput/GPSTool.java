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

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.imageio.ImageIO;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.dinopolis.gpstool.gpsinput.garmin.GPSGarminDataProcessor;
import org.dinopolis.gpstool.gpsinput.nmea.GPSNmeaDataProcessor;
import org.dinopolis.gpstool.gpsinput.sirf.GPSSirfDataProcessor;
import org.dinopolis.util.ProgressListener;
import org.dinopolis.util.commandarguments.CommandArgumentException;
import org.dinopolis.util.commandarguments.CommandArguments;

//----------------------------------------------------------------------
/**
 * Demo application to show the usage of this package (read and
 * interpret gps data from various devices (serial, file, ...).  <p>
 * It uses a velocity (http://jakarta.apache.org/velocity) template to
 * print the downloaded tracks, routes, and waypoints. See the help
 * output for details about the usable variables.
 *
 * @author Christof Dallermassl, Stefan Feitl
 * @version $Revision$
 */

public class GPSTool implements PropertyChangeListener, ProgressListener
{
  protected boolean gui_ = true;
  protected GPSDataProcessor gps_processor_;

  public final static String DEFAULT_TEMPLATE =
	"<?xml version=\"1.0\"?>"
	+"$dateformatter.applyPattern(\"yyyy-MM-dd'T'HH:mm:ss'Z'\")"
	+"$longitudeformatter.applyPattern(\"0.000000\")"
	+"$latitudeformatter.applyPattern(\"0.000000\")"
	+"$altitudeformatter.applyPattern(\"0\")\n"
  +"<gpx"
	+"  version=\"1.0\"\n"
	+"  creator=\"$author\"\n"
	+"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
	+"  xmlns=\"http://www.topografix.com/GPX/1/0\"\n"
	+"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd\">\n"
	+"  <time>$dateformatter.format($creation_date)</time>\n"
	+"  <bounds minlat=\"$min_latitude\" minlon=\"$min_longitude\"\n"
	+"          maxlat=\"$max_latitude\" maxlon=\"$max_longitude\"/>\n"
	+"\n"
	+"## print all waypoints that are available:\n"
	+"#if($printwaypoints)\n"
	+"#foreach( $point in $waypoints )\n"
	+"  <wpt lat=\"$latitudeformatter.format($point.Latitude)\" lon=\"$longitudeformatter.format($point.Longitude)\">\n"
	+"#if($point.hasValidAltitude())\n"
	+"    <ele>$altitudeformatter.format($point.Altitude)</ele>\n"
	+"#end\n"
	+"    <name>$!point.Identification</name>\n"
	+"#if($point.getComment().length() > 0)\n"
	+"    <desc>![CDATA[$!point.Comment]]</desc>\n"
	+"#end\n"
	+"#if($point.getSymbolName())\n"
	+"    <sym>$point.getSymbolName()</sym>\n"
	+"#end\n"
	+"  </wpt>\n"
	+"#end\n"
	+"#end\n"
	+"## print all routes that are available:\n"
	+"#if($printroutes)\n"
	+"#foreach( $route in $routes )\n"
	+"  <rte>\n"
	+"    <name>$!route.Identification</name>\n"
	+"#if($route.getComment().length() > 0)\n"
	+"    <desc>![CDATA[$!route.Comment]]</desc>\n"
	+"#end\n"
	+"    <number>$velocityCount</number>\n"
	+"#set ($points = $route.getWaypoints())\n"
	+"#foreach ($point in $points)\n"
	+"    <rtept lat=\"$latitudeformatter.format($point.Latitude)\" lon=\"$longitudeformatter.format($point.Longitude)\">\n"
	+"#if($point.hasValidAltitude())\n"
	+"        <ele>$altitudeformatter.format($point.Altitude)</ele>\n"
	+"#end\n"
	+"#if($point.getIdentification().length() > 0)\n"
	+"    <name>![CDATA[$!point.Identification]]</name>\n"
	+"#end\n"
	+"#if($point.getComment().length() > 0)\n"
	+"    <desc>![CDATA[$!point.Comment]]</desc>\n"
	+"#end\n"
	+"    </rtept>\n"
	+"#end\n"
	+"  </rte>\n"
	+"#end\n"
	+"#end\n"
	+"## print all tracks that are available:\n"
	+"#if($printtracks)\n"
	+"#foreach( $track in $tracks )\n"
	+"#set($close_segment = false)\n"
	+"  <trk>\n"
	+"    <name>$!track.Identification</name>\n"
	+"#if($point.getComment().length() > 0)\n"
	+"    <desc>![CDATA[$!point.Comment]]</desc>\n"
	+"#end\n"
	+"##      <number>$velocityCount</number>\n"
	+"#set ($points = $track.getWaypoints())##\n"
	+"#foreach ($point in $points)##\n"
	+"#if($point.isNewTrack())\n"
	+"#if($close_segment)## close trkseg, if not the first occurence\n"
	+"    </trkseg>\n"
	+"#end\n"
	+"    <trkseg>\n"
	+"#set($close_segment = true)\n"
	+"#end\n"
	+"      <trkpt lat=\"$latitudeformatter.format($point.Latitude)\" lon=\"$longitudeformatter.format($point.Longitude)\">\n"
	+"#if($point.hasValidAltitude())\n"
	+"        <ele>$altitudeformatter.format($point.Altitude)</ele>\n"
	+"#end\n"
	+"#if($point.getDate())## only if there is a time set! \n"
	+"        <time>$dateformatter.format($point.getDate())</time>\n"
	+"#end\n"
	+"      </trkpt>\n"
	+"#end\n"
	+"    </trkseg>\n"
	+"  </trk>\n"
	+"#end\n"
	+"#end\n"
	+"</gpx>\n";
  
//----------------------------------------------------------------------
/**
 * Default constructor
 */
  public GPSTool()
  {
  }
  
//----------------------------------------------------------------------
/**
 * Initialize the gps device, the gps data processor and handle all
 * command line arguments.
 * @param arguments the command line arguments
 */
  public void init(String[] arguments)
  {
    
    if(arguments.length < 1)
    {
      printHelp();
      return;
    }
    
    String[] valid_args =
      new String[] {"device*","d*","help","h","speed#","s#","file*","f*",
                    "nmea","n","garmin","g","sirf","i","rawdata","downloadtracks",
                    "downloadwaypoints","downloadroutes","deviceinfo","printposonce",
                    "printpos","p","printalt","printspeed","printheading","printsat",
                    "template*","outfile*","screenshot*", "printdefaulttemplate",
                    "helptemplate","nmealogfile*","l","uploadtracks","uploadroutes",
                    "uploadwaypoints","infile*"};

        // Check command arguments
        // Throw exception if arguments are invalid
    CommandArguments args = null;
    try
    {
      args = new CommandArguments(arguments,valid_args);
    }
    catch(CommandArgumentException cae) 
    {
      System.err.println("Invalid arguments: "+cae.getMessage());
      printHelp(); 
      return;
    }

        // Set default values
    String filename = null;
    String serial_port_name = null;
    int serial_port_speed = -1;
    GPSDataProcessor gps_data_processor;
    String nmea_log_file = null;

        // Handle given command arguments
    if (args.isSet("help") || (args.isSet("h")))
    {
      printHelp();
      return;
    }

    if(args.isSet("helptemplate"))
    {
      printHelpTemplate();
    }
    
    if (args.isSet("printdefaulttemplate"))
    {
      System.out.println(DEFAULT_TEMPLATE);
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
      if(filename != null)
      {
        System.err.println("ERROR: Cannot read garmin data from file, only serial port supported!");
        return;
      }
    }
    else
      if (args.isSet("sirf") || args.isSet("i"))
      {
        gps_data_processor = new GPSSirfDataProcessor();
        serial_port_speed = 19200;
        if(filename != null)
        {
          System.err.println("ERROR: Cannot read sirf data from file, only serial port supported!");
          return;
        }
      }
      else
// default:      if (args.isSet("nmea") || args.isSet("n"))
      {
        gps_data_processor = new GPSNmeaDataProcessor();
        serial_port_speed = 4800;
      }
    
    if (args.isSet("nmealogfile") || (args.isSet("l")))
    {
      if(args.isSet("nmealogfile"))
        nmea_log_file = args.getStringValue("nmealogfile");
      else
        nmea_log_file = args.getStringValue("l");
    }
    
      if (args.isSet("rawdata"))
    {
      gps_data_processor.addGPSRawDataListener(
        new GPSRawDataListener()
        {
          public void gpsRawDataReceived(char[] data, int offset, int length)
          {
            System.out.println("RAWLOG: "+new String(data,offset,length));
          }
        });
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

    try
    {
          // set params needed to open device (file,serial, ...):
      gps_device.init(environment);
          // connect device and data processor:
      gps_data_processor.setGPSDevice(gps_device);
      gps_data_processor.open();

            // use progress listener to be informed about the number
            // of packages to download
      gps_data_processor.addProgressListener(this);

          // raw data logger (to file):
      if((nmea_log_file != null) && (nmea_log_file.length() > 0))
      {
        gps_data_processor.addGPSRawDataListener(new GPSRawDataFileLogger(nmea_log_file));
      }
          // check, what to do:
      if(args.isSet("deviceinfo"))
      {
        System.out.println("GPSInfo:");
        String[] infos = gps_data_processor.getGPSInfo();
        for(int index=0; index < infos.length; index++)
        {
          System.out.println(infos[index]);
        }
      }

      if(args.isSet("screenshot"))
      {
        FileOutputStream out = new FileOutputStream((String)args.getValue("screenshot"));
        BufferedImage image = gps_data_processor.getScreenShot();
        ImageIO.write(image,"PNG",out);
        
      }

      boolean print_waypoints = args.isSet("downloadwaypoints");
      boolean print_routes = args.isSet("downloadroutes");
      boolean print_tracks = args.isSet("downloadtracks");

      if(print_waypoints || print_routes || print_tracks)
      {
            // create context for template printing:
        VelocityContext context = new VelocityContext();
        
        if(print_waypoints)
        {
          List waypoints = gps_data_processor.getWaypoints();
          if(waypoints != null)
            context.put("waypoints",waypoints);
          else
            print_waypoints = false;
//        System.out.println(waypoints);
        }
        if(print_tracks)
        {
          List tracks = gps_data_processor.getTracks();
          if(tracks != null)
            context.put("tracks",tracks);
          else
            print_tracks = false;
//        System.out.println(tracks);
        }
        if(print_routes)
        {
          List routes = gps_data_processor.getRoutes();
          if(routes != null)
            context.put("routes",routes);
          else
            print_routes = false;
//        System.out.println(routes);
        }
            // download finished, prepare context:
        context.put("printwaypoints",new Boolean(print_waypoints));
        context.put("printtracks",new Boolean(print_tracks));
        context.put("printroutes",new Boolean(print_routes));

        Writer writer;
        Reader reader;
        if(args.isSet("template"))
        {
          String template_file = (String)args.getValue("template");
          reader = new FileReader(template_file);
        }
        else
        {
          reader = new StringReader(DEFAULT_TEMPLATE);
        }
        if(args.isSet("outfile"))
          writer = new FileWriter((String)args.getValue("outfile"));
        else
          writer = new OutputStreamWriter(System.out);

        addDefaultValuesToContext(context);
        boolean result = printTemplate(context,reader,writer);
        
      }
      boolean read_waypoints = (args.isSet("uploadwaypoints") && args.isSet("infile"));
      boolean read_routes = (args.isSet("uploadroutes") && args.isSet("infile"));
      boolean read_tracks = (args.isSet("uploadtracks") && args.isSet("infile"));

      if(read_waypoints || read_routes || read_tracks)
      {
            // Create GPX file parser
        ReadGPX reader = new ReadGPX();
        String in_file = (String)args.getValue("infile");

            // Parse given input file
        reader.parseFile(in_file);

            // Upload read data to attached gps device
        if (read_waypoints)
          gps_data_processor.setWaypoints(reader.getWaypoints());

        if (read_routes)
          gps_data_processor.setRoutes(reader.getRoutes());

        if (read_tracks)
          gps_data_processor.setTracks(reader.getTracks());
      }

      if(args.isSet("printposonce"))
      {
        GPSPosition pos = gps_data_processor.getGPSPosition();
        System.out.println("Current Position: "+pos);
      }
      
          // register as listener to be informed about the chosen events
      if(args.isSet("printpos") || args.isSet("p"))
      {
        gps_data_processor.addGPSDataChangeListener(GPSDataProcessor.LOCATION,this);
      }
      if(args.isSet("printalt"))
      {
        gps_data_processor.addGPSDataChangeListener(GPSDataProcessor.ALTITUDE,this);
      }
      if(args.isSet("printspeed"))
      {
        gps_data_processor.addGPSDataChangeListener(GPSDataProcessor.SPEED,this);
      }
      if(args.isSet("printheading"))
      {
        gps_data_processor.addGPSDataChangeListener(GPSDataProcessor.HEADING,this);
      }
      if(args.isSet("printsat"))
      {
        gps_data_processor.addGPSDataChangeListener(GPSDataProcessor.NUMBER_SATELLITES,this);
        gps_data_processor.addGPSDataChangeListener(GPSDataProcessor.SATELLITE_INFO,this);
      }
      if(args.isSet("printpos") || args.isSet("p") || args.isSet("printalt")
         || args.isSet("printsat") || args.isSet("printspeed") || args.isSet("printheading"))
      {
            // tell gps processor to send curren position once every second:
        gps_data_processor.startSendPositionPeriodically(1000L);

        try
        {
              // wait for user pressing enter:
          System.in.read();
        }
        catch(IOException ignore) {}
      }
            // close device and processor:
      gps_data_processor.close();
    }
    catch(GPSException e)
    {
      e.printStackTrace();
    }
    catch(FileNotFoundException fnfe)
    {
      System.err.println("ERROR: File not found: "+fnfe.getMessage());
    }
    catch(IOException ioe)
    {
      System.err.println("ERROR: I/O Error: "+ioe.getMessage());
    }
  }

//----------------------------------------------------------------------
/**
 * Adds some important values to the velocity context (e.g. date, ...).
 *
 * @param context the velocity context holding all the data
 */
  public void addDefaultValuesToContext(VelocityContext context)
  {
		DecimalFormat latitude_formatter = (DecimalFormat)NumberFormat.getInstance(Locale.US);
		latitude_formatter.applyPattern("0.0000000");
		DecimalFormat longitude_formatter = (DecimalFormat)NumberFormat.getInstance(Locale.US);
		longitude_formatter.applyPattern("0.0000000");
		DecimalFormat altitude_formatter = (DecimalFormat)NumberFormat.getInstance(Locale.US);
		altitude_formatter.applyPattern("000000");
		OneArgumentMessageFormat string_formatter = new OneArgumentMessageFormat("{0}",Locale.US);
    context.put("dateformatter",new SimpleDateFormat());
		context.put("latitudeformatter", latitude_formatter);
		context.put("longitudeformatter", longitude_formatter);
		context.put("altitudeformatter", altitude_formatter);
		context.put("stringformatter", string_formatter);
        // current time, date
    Calendar now = Calendar.getInstance();
    context.put("creation_date",now.getTime());
    int day = now.get(Calendar.DAY_OF_MONTH);
    int month = now.get(Calendar.MONTH);
    int year = now.get(Calendar.YEAR);
    int hour = now.get(Calendar.HOUR_OF_DAY);
    int minute = now.get(Calendar.MINUTE);
    int second = now.get(Calendar.SECOND);
//     DecimalFormat two_digit_formatter = new DecimalFormat("00");
//     context.put("date_day",two_digit_formatter.format((long)day));
//     context.put("date_month",two_digit_formatter.format((long)month));
//     context.put("date_year",Integer.toString(year));
//     context.put("date_hour",two_digit_formatter.format((long)hour));
//     context.put("date_minute",two_digit_formatter.format((long)minute));
//     context.put("date_second",two_digit_formatter.format((long)second));

        // author
    context.put("author",System.getProperty("user.name"));

        // extent of waypoint, routes and tracks:
    double min_latitude = 90.0;
    double min_longitude = 180.0;
    double max_latitude = -90.0;
    double max_longitude = -180.0;

    List routes = (List)context.get("routes");
    GPSRoute route;
    if(routes != null)
    {
      Iterator route_iterator = routes.iterator();
      while(route_iterator.hasNext())
      {
        route = (GPSRoute)route_iterator.next();
        min_longitude = route.getMinLongitude();
        max_longitude = route.getMaxLongitude();
        min_latitude = route.getMinLatitude();
        max_latitude = route.getMaxLatitude();
      }
    }

    List tracks = (List)context.get("tracks");
    GPSTrack track;
    if(tracks != null)
    {
      Iterator track_iterator = tracks.iterator();
      while(track_iterator.hasNext())
      {
        track = (GPSTrack)track_iterator.next();
        min_longitude = Math.min(min_longitude,track.getMinLongitude());
        max_longitude = Math.max(max_longitude,track.getMaxLongitude());
        min_latitude = Math.min(min_latitude,track.getMinLatitude());
        max_latitude = Math.max(max_latitude,track.getMaxLatitude());
      }
    }
    List waypoints = (List)context.get("waypoints");
    GPSWaypoint waypoint;
    if(waypoints != null)
    {
      Iterator waypoint_iterator = waypoints.iterator();
      while(waypoint_iterator.hasNext())
      {
        waypoint = (GPSWaypoint)waypoint_iterator.next();
        min_longitude = Math.min(min_longitude,waypoint.getLongitude());
        max_longitude = Math.max(max_longitude,waypoint.getLongitude());
        min_latitude = Math.min(min_latitude,waypoint.getLatitude());
        max_latitude = Math.max(max_latitude,waypoint.getLatitude());
      }
    }
    context.put("min_latitude",new Double(min_latitude));
    context.put("min_longitude",new Double(min_longitude));
    context.put("max_latitude",new Double(max_latitude));
    context.put("max_longitude",new Double(max_longitude));
  }


//----------------------------------------------------------------------
/**
 * Prints the given context with the given velocity template to the
 * given output writer.
 *
 * @param context the velocity context holding all the data
 * @param template the reader providing the template to use
 * @param out the writer to write the result to.
 * @return true if successfull, false otherwise (see velocity log for
 * details then).
 * @throws IOException if an error occurs
 */
  public boolean printTemplate(VelocityContext context,Reader template, Writer out)
    throws IOException
  {
    boolean result = false;
    try
    {
      Velocity.init();
      result = Velocity.evaluate(context,out,"gpstool",template);
      out.flush();
      out.close();
    }
    catch(ParseErrorException pee)
    {
      pee.printStackTrace();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    return(result);
  }
  
  
//   public void registerListener()
//   {
//     System.out.println("------------------------------------------------------------"); 
//     System.out.println("------------------------------------------------------------");
//     System.out.println("------------------------------------------------------------");

//     try
//     {
// //       JFrame frame = null;
// //       if(gui_)
// //       {
// //         GPSInfoPanel panel = new GPSInfoPanel();
// //         frame = new JFrame("GPS Info");
// //         frame.getContentPane().add(panel);
// //         frame.pack();
// //         frame.setVisible(true);
      
// //         gps_processor_.addGPSDataChangeListener(GPSDataProcessor.LOCATION,panel);
// //         gps_processor_.addGPSDataChangeListener(GPSDataProcessor.HEADING,panel);
// //         gps_processor_.addGPSDataChangeListener(GPSDataProcessor.ALTITUDE,panel);
// //         gps_processor_.addGPSDataChangeListener(GPSDataProcessor.SPEED,panel);
// //         gps_processor_.addGPSDataChangeListener(GPSDataProcessor.NUMBER_SATELLITES,panel);
// //         gps_processor_.addGPSDataChangeListener(GPSDataProcessor.SATELLITE_INFO,panel);
// //       }

//       System.in.read();
//       if(gui_)
//       {
//         frame.setVisible(false);
//         frame.dispose();
//       }
// //        try
// //        {
// //          Thread.sleep(200000);
// //        }
// //        catch(InterruptedException ie)
// //        {}
// //        gps_processor_.close();
//     }
//     catch(Exception e)
//     {
//       e.printStackTrace();
//     }
   
//   }

//----------------------------------------------------------------------
/**
 * Returns the default template used to print data from the gps device.
 *
 * @return the default template.
 */
  public static String getDefaultTEmplate()
  {
    return(DEFAULT_TEMPLATE);
  }

//----------------------------------------------------------------------
// Callback method for PropertyChangeListener
//----------------------------------------------------------------------
  
//----------------------------------------------------------------------
/**
 * Callback for any changes of the gps data (position, heading, speed,
 * etc.).
 * @param event the event holding the information.
 */
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
  }

//----------------------------------------------------------------------
// Callback methods for ProgressListener
//----------------------------------------------------------------------
  
//----------------------------------------------------------------------
/**
 * Callback to inform listeners about an action to start.
 *
 * @param action_id the id of the action that is started. This id may
 * be used to display a message for the user.
 * @param min_value the minimum value of the progress counter.
 * @param max_value the maximum value of the progress counter. If the
 * max value is unknown, max_value is set to <code>Integer.NaN</code>.
 */
  public void actionStart(String action_id, int min_value, int max_value)
  {
    System.err.println("Starting '"+action_id+"' ("+max_value+" packages): ");
  }
  
//----------------------------------------------------------------------
/**
 * Callback to inform listeners about progress going on. It is not
 * guaranteed that this method is called on every change of current
 * value (e.g. only call this method on every 10th change).
 *
 * @param action_id the id of the action that is started. This id may
 * be used to display a message for the user.
 * @param current_value the current value
 */
  public void actionProgress(String action_id, int current_value)
  {
    System.err.print("\r"+current_value);
  }

//----------------------------------------------------------------------
/**
 * Callback to inform listeners about the end of the action.
 *
 * @param action_id the id of the action that is started. This id may
 * be used to display a message for the user.
 */
  public void actionEnd(String action_id)
  {
    System.err.println("\nfinished");
  }


//----------------------------------------------------------------------
/**
 * Prints the help message for writing templates.
 */
  public static void printHelpTemplate()
  {
    System.out.println("GPSTool is able to write tracks, routes, and waypoints in various");
    System.out.println("formats. It uses a velocity template for this. Please see");
    System.out.println("http://jakarta.apache.org/velocity for details. GPSTool provides");
    System.out.println("the following objects to be used in the template (the type is");
    System.out.println("included in parentheses):");
    System.out.println("  $waypoints (List of GPSWaypoint objects): the waypoints from the gps device");
    System.out.println("  $routes (List of GPSRoute objects): the routes from the gps device");
    System.out.println("  $tracks (List of GPSTrack objects) the tracks from the gps device");
    System.out.println("  $printwaypoints (Boolean): true, if the user decided to download waypoints");
    System.out.println("  $printtracks (Boolean): true, if the user decided to download tracks");
    System.out.println("  $printroutes (Boolean): true, if the user decided to download routes");
    System.out.println("  $creation_date (java.util.Date): the creation date (now)");
    System.out.println("  $author (String): the system property 'user.name'");
    System.out.println("  $min_latitude (Double): the minimum latitude of all downloaded data");
    System.out.println("  $max_latitude (Double): the maximum latitude of all downloaded data");
    System.out.println("  $min_longitude (Double): the minimum longitude of all downloaded data");
    System.out.println("  $min_longitude (Double): the maximum longitude of all downloaded data");
    System.out.println("  $dateformatter (java.text.SimpleDateFormat): helper object to format dates");
    System.out.println("For an example use the commandline switch '--printdefaulttemplate'.");
  }
  
  
//----------------------------------------------------------------------
/**
 * Prints the help messages
 */

  public static void printHelp()
  {
    System.out.println("GPSTool 0.8.0 - Communication between GPS-Devices and Computers via serial port");
    System.out.println("(c) 2000-2003 Christof Dallermassl\n");
    System.out.println("Usage: java org.dinopolis.gpstool.GPSTool [options]\n");
    System.out.println("Options:");
    System.out.println("--device, -d <device>, e.g. -d /dev/ttyS0 or COM1 (defaults depending on OS).");
    System.out.println("--speed,  -s <speed>, e.g. -s 4800 (default for nmea, 9600 for garmin, 19200 for sirf).");
    System.out.println("--file,   -f <filename>, the gps data is read from the given file.");
    System.out.println("--nmea,   -n, the gps data is interpreted as NMEA data (default).");
    System.out.println("--garmin, -g, the gps data is interpreted as garmin data.");
    System.out.println("--sirf, -i, the gps data is interpreted as sirf data.");
    System.out.println("--nmealogfile, -l <filename>, the gps data is logged into this file.");
    System.out.println("--rawdata, the raw (nmea or garmin) gps data is printed to stdout.");
//    System.out.println("--nogui, no frame is opened.\n");
    System.out.println("--printposonce, prints the current position and exits again.");
    System.out.println("--printpos, -p, prints the current position and any changes.");
    System.out.println("                Loops until the user presses 'enter'.");
    System.out.println("--printalt, prints the current altitude and any changes.");
    System.out.println("--printsat, prints the current satellite info altitude and any changes.");
    System.out.println("--printspeed, prints the current speed and any changes.");
    System.out.println("--printheading, prints the current heading and any changes.");
    System.out.println("--deviceinfo, prints some information about the gps device (if available)");
    System.out.println("--screenshot <filename>, saves a screenshot of the gps device in PNG format.");
    System.out.println("--downloadtracks, print tracks stored in the gps device.");
    System.out.println("--downloadwaypoints, print waypoints stored in the gps device.");
    System.out.println("--downloadroutes, print routes stored in the gpsdevice .");
    System.out.println("--outfile <filename>, the file to print the tracks, routes and waypoints to, stdout is default");
    System.out.println("--template <filename>, the velocity template to use for printing routes, tracks and waypoints");
    System.out.println("--printdefaulttemplate, prints the default template used to print routes, waypoints, and tracks.");
    System.out.println("--uploadtracks, reads track information from the file given at the infile\n"
                      +"                parameter and uploads it to the gps device.");
    System.out.println("--uploadroutes, reads route information from the file given at the infile\n"
                      +"                parameter and uploads it to the gps device.");
    System.out.println("--uploadwaypoints, reads waypoint information from the file given at the infile\n"
                      +"                   parameter and uploads it to the gps device.");
    System.out.println("--infile <filename>, the GPX file to read the tracks, routes and waypoints from");
    System.out.println("--helptemplate, prints some more information on how to write a template.");
    System.out.println("--help -h, shows this page");
    
  }

  private static void test()
  {
  }



  
//----------------------------------------------------------------------
/**
 * Main method
 * @param arguments the command line arguments
 */
  public static void main (String[] arguments) 
  {
    new GPSTool().init(arguments);
  }

//----------------------------------------------------------------------
//----------------------------------------------------------------------

	static public class OneArgumentMessageFormat extends MessageFormat
	{
		public OneArgumentMessageFormat(String pattern)
		{
			super(pattern);
		}

		public OneArgumentMessageFormat(String pattern, Locale locale)
		{
			super(pattern,locale);
		}

		public String format(String argument)
		{
			System.out.println("OneArgumentMessageFormat: "+argument);
			return(format(new Object[] {argument}));
		}

		public String format(String pattern, String argument)
		{
			return(format(pattern, new Object[] {argument}));
		}

//----------------------------------------------------------------------
 /**
  * Pad a string to a maximal length with spaces at the end.
  * @param string the string to pad.
  * @param length the length for the final string (if the given string is longer,
  * it is shortened to the given length).
	*/
		public String pad(String string, int length) 
		{
			return(pad(string,length,' ',false));
		}

//----------------------------------------------------------------------
 /**
  * Pad a string to a maximal length with a given character at the end.
  * @param string the string to pad.
  * @param length the length for the final string (if the given string is longer,
  * it is shortened to the given length).
  * @param pad_char the character to pad with.
	*/
		public String pad(String string, int length, char pad_char) 
		{
			return(pad(string,length,pad_char,false));
		}

//----------------------------------------------------------------------
 /**
  * Pad a string to a maximal length with a given character on the beginning or
  *	 on the end.
  * @param string the string to pad.
  * @param length the length for the final string (if the given string is longer,
  * it is shortened to the given length).
  * @param pad_char the character to pad with.
  * @param pad_begin pad on the begin of the string, not at the end
	*/
		public String pad(String string, int length, char pad_char, boolean pad_begin) 
		{
			StringBuffer str = new StringBuffer(string);
			if(length > string.length())
			{
				do
				{
					if(pad_begin) 
						str.insert(0,pad_char);
					else 
						str.append(pad_char);
				}
				while(str.length() < length);
				return(str.toString());
			}
			else
			{
				return(string.substring(0,length));
			}
		}
	}


}
