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

import org.dinopolis.util.Debug;
import org.dinopolis.util.commandarguments.CommandArguments;
import org.dinopolis.util.commandarguments.CommandArgumentException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import org.dinopolis.gpstool.gpsinput.nmea.GPSNmeaDataProcessor;

//----------------------------------------------------------------------
/**
 * @author Christof Dallermassl
 * @version $Revision$
 *
 * TODO: points file: name may contain spaces!
 *       support points of interest not only N and E!
 */


public class GPSOrient implements PropertyChangeListener
{

  public Vector points_of_interest_;
  public double view_angle_ = 5.0;

  public GPSOrient(String[] arguments)
  {
    String[] valid_args =
      new String[] {"device*","d*","help","h","speed#","s#","gpsfile*","f*","nmea","n",
                    "pointfile*","p*","test","angle%","a%","datalogfile*","l*"};

    CommandArguments args = null;
    try
    {
      args = new CommandArguments(arguments,valid_args);
    }
    catch(CommandArgumentException cae) 
    {
      cae.printStackTrace();
    }

    String gps_data_filename = null;
    String data_log_filename = null;
    String point_filename = null;
    String serial_port_name = null;
    int serial_port_speed = -1;
    GPSDataProcessor gps_data_processor = new GPSNmeaDataProcessor();


    if (args != null)
    {
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
        serial_port_name = args.getStringValue("device");
      }
      else 
      if (args.isSet("d"))
      {
        serial_port_name = args.getStringValue("d");
      }

      if (args.isSet("speed"))
      {
        serial_port_speed = args.getIntegerValue("speed").intValue();
      }
      else 
      if (args.isSet("s"))
      {
        serial_port_speed = args.getIntegerValue("s").intValue();
      }

      if (args.isSet("datalogfile"))
      {
        data_log_filename = args.getStringValue("datalogfile");
      }
      else 
      if (args.isSet("l"))
      {
        data_log_filename = args.getStringValue("f");
      }

      if (args.isSet("gpsfile"))
      {
        gps_data_filename = args.getStringValue("gpsfile");
      }
      else 
      if (args.isSet("f"))
      {
        gps_data_filename = args.getStringValue("f");
      }

      if (args.isSet("pointfile"))
      {
        point_filename = args.getStringValue("pointfile");
      }
      else 
      if (args.isSet("p"))
      {
        point_filename = args.getStringValue("p");
      }


      if (args.isSet("angle"))
      {
        view_angle_ = args.getDoubleValue("angle").doubleValue();
      }
      else 
      if (args.isSet("a"))
      {
        view_angle_ = args.getDoubleValue("a").doubleValue();
      }


      if ((args.isSet("nmea")) || args.isSet("n"))
      {
        gps_data_processor = new GPSNmeaDataProcessor();
      }
    }

    GPSDevice gps_device;
    Hashtable environment = new Hashtable();
    if (gps_data_filename != null)
    {
      environment.put(GPSFileDevice.PATH_NAME_KEY,gps_data_filename);
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
    }
    catch(GPSException e)
    {
      e.printStackTrace();
    }

    try
    {
      if (point_filename == null)
      {
        System.err.println("ERROR: No points-of-interest file given!");
        printHelp();
        System.exit(2);
      }
      points_of_interest_ = readPointsOfInterestFile(point_filename);
    }
    catch(IOException ioe)
    {
      ioe.printStackTrace();
      System.exit(1);
    }

    if(data_log_filename != null)
    {
      gps_data_processor.addGPSRawDataListener(new GPSRawDataFileLogger(data_log_filename));
    }

    System.out.println("------------------------------------------------------------"); 
    System.out.println("------------------------------------------------------------");
    System.out.println("------------------------------------------------------------");
    try
    {
      gps_data_processor.setGPSDevice(gps_device);
      gps_data_processor.open();
      gps_data_processor.addGPSDataChangeListener(GPSDataProcessor.LOCATION,this);
      gps_data_processor.addGPSDataChangeListener(GPSDataProcessor.HEADING,this);
      System.in.read();
//        try
//        {
//          Thread.sleep(200000);
//        }
//        catch(InterruptedException ie)
//        {}
      gps_data_processor.close();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  public void propertyChange(PropertyChangeEvent event)
  {
    if (Debug.DEBUG)
      Debug.println("gpsorient_event",
                    "EVENT:"
                    +"\nsource: "+event.getSource()
                    +"\nName  : "+event.getPropertyName()
                    +"\nOld   : "+event.getOldValue()
                    +"\nNew   : "+event.getNewValue());
    
    GPSDataProcessor source = (GPSDataProcessor)event.getSource ();
    GPSPosition current_position = source.getGPSPosition();
    double current_heading = source.getHeading();

    if (Debug.DEBUG)
      Debug.println("gpsorient_position","POS:" + current_position + " HDG:" + current_heading);

    Vector points_in_view = findPoints(current_position,current_heading,points_of_interest_,view_angle_);
    Iterator point_iterator = points_in_view.iterator();
    while(point_iterator.hasNext())
    {
      System.out.print("In direction: ");
      GPSPosition point = (GPSPosition)point_iterator.next();
      System.out.println(point);
    }
//    System.out.print(".");
  }

/**
 * Finds the points of interest that are positioned in a given
 * direction (in an area of <code>angle</code> degrees).
 * @param position the current position
 * @param heading the current heading (direction)
 * @param points_of_interest a vector holding all points of interest
 * @param angle the view angle to use (angel/2 plus/minus the heading
 * is used as the area to search)
 * @return a vector holding the points of interest in the given area.
 */
  public Vector findPoints(GPSPosition position, double heading, 
                           Vector points_of_interest,double angle)
  {
    Vector found_points = new Vector();
    GPSPosition point;

    Iterator point_iterator = points_of_interest.iterator();
    double delta_lat;
    double delta_long;
    double target_heading;
    angle /= 2;

    while(point_iterator.hasNext())
    {
      point = (GPSPosition)point_iterator.next();
      delta_lat = point.getLatitude() - position.getLatitude();
      delta_long = point.getLongitude() - position.getLongitude();
      target_heading = calcHeading(delta_long,delta_lat);
      if (Math.abs(target_heading - heading) < angle)
	found_points.addElement(point);
      if(Debug.DEBUG)
	Debug.println("gpsorient_findpoints_targetangle","heading from "
                      +position+" to "+point+" is "+target_heading);
    }

//      double heading_radiant, a, b, c, d;

//      angle = angle/2.0;  // use half of it to the left, halft to the right.
    
//      heading_radiant = Math.toRadians(heading+angle);
//          // x = long, y = lat

//  //  find out, if a given point is in the triangle (heading plus/minus angle):
//  //      y=k*x+d
//  //      k=tan(alpha)

//  //      k*x - y + d = 0
//  //      insert point (x/y) to find d:
//  //      x .. longitude    
//  //      y .. latitude

//  //      tan(alpha)*longitude - latitude = -d
//  //      d = latitude - tan(alpha)*longitude

//  //      line: a*x + b*y + c = 0
//  //      comparison of components:
//  //      a = k = tan(alpha)
//  //      b = -1
//  //      c = d = latitude - tan(alpha)*longitude

//  //      position d of point(x0/y0) in relation to line g: 
//  //      d = a*x0 + b*y0 + c

//      heading_radiant = Math.toRadians(heading-angle);

//      a = Math.tan(heading_radiant);
//      b = -1;
//      c = (position.getLatitude() - a * position.getLongitude());
	    
//      Iterator point_iterator = points_of_interest.iterator();
//      while(point_iterator.hasNext())
//      {
//        point = (GPSPosition)point_iterator.next();

//        if(Debug.DEBUG)
//  	if(Debug.isEnabled("gpsorient_findpoints_targetangle"))
//  	{
//            double delta_lat = point.getLatitude() - position.getLatitude();
//            double delta_long = point.getLongitude() - position.getLongitude();
//            double target_dir = calcHeading(delta_long,delta_lat);
//            Debug.println("gpsorient_findpoints_targetangle","heading from "
//                          +position+" to "+point+" is "+target_dir);
//  	}

//        d = a*point.getLongitude() + b*point.getLatitude() + c;
//        if (Debug.DEBUG)
//  	  Debug.println("gpsorient_distance","direction="
//                          +(heading+angle)+" d="+d);
//        if (d >= 0.0) // left of line
//        {
//          found_points.addElement(point);
//          if(Debug.DEBUG)
//  	  Debug.println("gpsorient_findpoints","found point left of line:"
//                          +point+" d="+d);
//        }
//      }

//      heading_radiant = Math.toRadians(heading+angle);
//      a = Math.tan(heading_radiant);
//  //            b = -1;
//      c = (position.getLatitude() - a * position.getLongitude());

//      point_iterator = found_points.iterator();
//      while(point_iterator.hasNext())
//      {
//        point = (GPSPosition)point_iterator.next();
//        d = a*point.getLongitude() + b*point.getLatitude() + c;
//        if (Debug.DEBUG)
//  	  Debug.println("gpsorient_distance","direction="
//                          +(heading-angle)+" d="+d);
//        if (d > 0.0) // again left of line (so not between the two lines)
//  	point_iterator.remove();
//        else
//        {
//          if(Debug.DEBUG)
//  	  Debug.println("gpsorient_findpoints","found point inside of line:"
//                          +point+" d="+d);
//        }
//      }

    return(found_points);
  }


/**
 * Reads a file holding points of interest and stores them in a vector
 * @param filename the file to read
 * @return a vector holding the points of interest
 * @exception IOException if any read error occurs
 */
  public Vector readPointsOfInterestFile(String filename)
  throws IOException
	{
    BufferedReader points_reader = null;
    Vector points = new Vector();
    points_reader = new BufferedReader(new FileReader(filename));

    String line;
    String longitude,latitude,altitude,name;
    StringTokenizer tokenizer;
    int linenumber = 0;
    while ((line = points_reader.readLine()) != null)
    {
      linenumber++;

          // check for comments and empty lines:
      if ((!line.startsWith("#")) && (line.length() > 0))
      {
        try
        {
          tokenizer = new StringTokenizer(line);
          latitude = tokenizer.nextToken();
          longitude = tokenizer.nextToken();
          altitude = tokenizer.nextToken();
          name = tokenizer.nextToken();
//           GPSPosition point = new GPSPosition(GPSNmeaDataProcessor.nmeaPosToWGS84(latitude),"N",
//                                               GPSNmeaDataProcessor.nmeaPosToWGS84(longitude),"E",
//                                               new Double(altitude).doubleValue(),name);
          GPSPosition point = new GPSPosition(new Double(latitude).doubleValue(),"N",
                                              new Double(longitude).doubleValue(),"E",
                                              new Double(altitude).doubleValue(),name);
          points.addElement(point);
        }
        catch(NoSuchElementException pe)
        {
          System.err.println("ERROR: Parse Error in line "+linenumber);
          pe.printStackTrace();
        }
      }
    }
    points_reader.close();
    return(points);
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
    System.out.println("--speed, -s <speed>, e.g. --speed 4800 (default)");
    System.out.println("--gpsfile, -f <filename>, the gps data is read from the given file and not via the serial interface.");
    System.out.println("--nmea, -n, the gps data is interpreted as NMEA data (default).");
    System.out.println("--datalogfile, -l <filename>, the file where the raw gps data is logged into.");
    System.out.println("--pointfile, -p <filename>, the file holding the points of interest.");
  }

  private void test()
  {
//     double latitude = GPSNmeaDataProcessor.nmeaPosToWGS84("4703.555");
//     double longitude = GPSNmeaDataProcessor.nmeaPosToWGS84("01527.553");
//     System.out.println(new GPSPosition(latitude,"N",longitude,"E"));

//     GPSPosition position = new GPSPosition(47,"N",12,"E","mypos");
//     GPSPosition point = new GPSPosition(46,"N",12,"E","target");
//     double delta_lat = point.getLatitude() - position.getLatitude();
//     double delta_long = point.getLongitude() - position.getLongitude();
//     double target_dir = Math.toDegrees(Math.atan2(delta_long,delta_lat));
//     if (target_dir < 0.0)
//      target_dir = target_dir + 180;
//     System.out.println("heading from "+position+" to "+point+" is "+target_dir);

//     position = new GPSPosition(46.86215,"N",12.7927467,"E","mypos");
//     point = new GPSPosition(46.8329,"N",12.76605,"E","target");
//     delta_lat = point.getLatitude() - position.getLatitude();
//     delta_long = point.getLongitude() - position.getLongitude();
//     target_dir = calcHeading(delta_lat,delta_long);
//     System.out.println("heading from "+position+" to "+point+" is "+target_dir);
	
//     System.out.println("0,5: "+calcHeading(0,5));
//     System.out.println("5,0: "+calcHeading(5,0));
//     System.out.println("0,-5: "+calcHeading(0,-5));
//     System.out.println("-5,0: "+calcHeading(-5,0));
//     System.out.println("-5,-5: "+calcHeading(-5,-5));
  }

  private double calcHeading(double delta_x, double delta_y)
  {
    double angle = Math.toDegrees(Math.atan2(delta_y,delta_x));
    angle -= 90.0;
    angle *= -1;
    if ((angle < 0.0) && (angle > -180.0))
      angle += 360;
    return(angle);
  }

  public static void main (String[] arguments) 
  {
    new GPSOrient(arguments);
  } // end of main ()

}


