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

//----------------------------------------------------------------------
/**
 * This class simulates a gps device. It uses a given start point and
 * simulates the walk to a given destination point at some random
 * speed. The events it fires only containt location and heading. No
 * satellite information or similar is provided!
 *
 * @author Christof Dallermassl
 * @version $Revision$ */

public class GPSSimulationDataProcessor extends GPSGeneralDataProcessor implements Runnable
{

/** the reader thread */
  Thread simulation_thread_;

  GPSPosition destination_;
  GPSPosition current_position_;
  float current_heading_;
  float speed_ = 1.0f;
  boolean stopped_ = false;

  public static final int SLEEP_TIME = 2; // every SLEEP_TIME seconds one step
  public static final int SECONDS_TO_DESTINATION = 60;
  
//----------------------------------------------------------------------
/**
 * Default constructor.
 */
  public GPSSimulationDataProcessor()
  {
  }

//----------------------------------------------------------------------
/**
 * Sets the starting position for the simulation.
 *
 * @param start the position to start with.
 */
  public void setStartPosition(GPSPosition start)
  {
    current_position_ = new GPSPosition(start);
  }
  
//----------------------------------------------------------------------
/**
 * Sets the destination position for the simulation.
 *
 * @param start the position to start with.
 */
  public void setDestinationPosition(GPSPosition destination)
  {
    destination_ = destination;
  }

//----------------------------------------------------------------------
/**
 * Sets the average speed the simulation should use.
 *
 * @param speed the speed the simulation should use. The speed is a
 * given in degrees/hour, so on the equator this would mean 111km/hour.
 */
  public void setDestinationPosition(float speed)
  {
    speed_ = speed;
  }


  
//----------------------------------------------------------------------
/**
 * Starts the simulation thread. Set the start and destination position first!
 * An closed simulation thread may be started again by calling this method!
 */
  public void open()
  {
//    System.out.println("GPSSimulationDataProcessor.open called");
    if(!stopped_) // still running
      close();
    stopped_ = false;
    simulation_thread_ = new Thread(this,"GPSSimulationDataProcessor");
    simulation_thread_.setDaemon(true); // so thread is finished after exit of application
    simulation_thread_.start();
  }
  
//----------------------------------------------------------------------
/**
 * Stopps the simulation thread.
 */
  public void close()
  {
    stopped_ = true;
    if(simulation_thread_ != null)
      simulation_thread_.interrupt();
  }

  
  public void run()
  {
    double diff_lat;
    double diff_long;
    double distance;
    double degree_per_step;

    double random;
    double step_lat, step_long;
    double speed_lat, speed_long;

    double last_latitude = current_position_.getLatitude();
    double last_longitude = current_position_.getLongitude();

    changeGPSData(GPSDataProcessor.LOCATION,current_position_);
    changeGPSData(GPSDataProcessor.HEADING,new Float(current_heading_));

    while(!stopped_)
    {
      diff_lat = destination_.getLatitude() - current_position_.getLatitude();
      diff_long = destination_.getLongitude() - current_position_.getLongitude();
      distance = Math.sqrt(diff_lat * diff_lat + diff_long * diff_long);

      step_lat = diff_lat / ((double)SECONDS_TO_DESTINATION / (double)SLEEP_TIME);
      step_long = diff_long /((double)SECONDS_TO_DESTINATION / (double)SLEEP_TIME);

      step_lat = step_lat * ((Math.random() * 0.4) + 0.8); // random factor 0.8-1.2
      step_long = step_long * ((Math.random() * 0.4) + 0.8); // random factor 0.8-1.2
      
      if(Debug.DEBUG)
        Debug.println("GPSSimulation","simulation thread diff_lat="+diff_lat
                      +" diff_long="+diff_long
                      +" current step (lat/long)="
                      +step_lat+"/"+step_long);
      
      current_heading_ = (float)Math.toDegrees(Math.atan2(step_lat,step_long));
          // conversion from mathematical model to geographical (0 is North, 90 is East)
      current_heading_ = 90.0f - current_heading_;
      if(current_heading_ < 0f)
        current_heading_ = 360.0f + current_heading_;
        
      current_position_ = new GPSPosition();
      current_position_.setLatitude(last_latitude + step_lat);
      current_position_.setLongitude(last_longitude + step_long);
      
      last_latitude = current_position_.getLatitude();
      last_longitude = current_position_.getLongitude();
      
      changeGPSData(GPSDataProcessor.LOCATION,current_position_);
      changeGPSData(GPSDataProcessor.HEADING,new Float(current_heading_));

      if(Debug.DEBUG)
        Debug.println("GPSSimulation","GPSSimulationDataProcessor: new location"+current_position_);

      try
      {
        Thread.sleep(SLEEP_TIME*1000);
      }
      catch(InterruptedException ie)
      {
        if(Debug.DEBUG)
          Debug.println("GPSSimulation","GPSSimulationDataProcessor: simulation thread stopped.");
        return;
      }

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
    return(current_position_);
  }

//----------------------------------------------------------------------
/**
 * Returns the last received heading (direction) from the GPSDevice or
 * <code>-1.0</code> if no heading was retrieved until now.
 * @return the heading from the GPSDevice.
 */
  public float getHeading()
  {
    return(current_heading_);
  }

}
