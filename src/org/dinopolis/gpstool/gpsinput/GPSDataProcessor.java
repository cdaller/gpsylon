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

import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.List;

//----------------------------------------------------------------------
/**
 * All classes implementing this interface are interpreting data from
 * a GPSDevice (serial gps-receivier, file containing gps data, ...)
 * and provide this information in a uniform way. So an NMEA-processor
 * interprets NMEA sentences, while a Garmin-Processor understands the
 * garmin protocol.
 * <p>
 * Other classes may register as a GPSDataListener and receive gps
 * events. The following events are supported (if provided by the gps device):
 * <ul>
 * <li><code>LOCATION</code>: the value is a {@link
 * org.dinopolis.gpstool.gpsinput.GPSPosition} object</li>
 * <li><code>HEADING</code>: the value is a Float</li>
 * <li><code>SPEED</code>: the value is a Float and is in kilometers per hour</li>
 * <li><code>NUMBER_SATELLITES</code>: the value is a Integer</li>
 * <li><code>ALTITUDE</code>: the value is a Float and is in meters</li>
 * <li><code>SATELLITE_INFO</code>: the value is a {@link org.dinopolis.gpstool.gpsinput.SatelliteInfo} object.</li>
 * <li><code>DEPTH</code>: the value is a Float and is in meters.</li>
 * <ul>
 *
 * @author Christof Dallermassl
 * @version $Revision$ */

public interface GPSDataProcessor
{

  public final static String LOCATION = "location";
  public final static String HEADING = "heading";
  public final static String SPEED = "speed";
  public final static String NUMBER_SATELLITES = "number_satellites";
  public final static String ALTITUDE = "altitude";
  public final static String SATELLITE_INFO = "satellite_info";
  public final static String DEPTH = "depth";
  
  public final static float KM2NAUTIC = 0.54f;
  
//----------------------------------------------------------------------
/**
 * Starts the data processing. The Data Processor connects to the
 * GPSDevice and starts retrieving information.
 *
 * @exception if an error occured on connecting.
 */
  public void open()
    throws GPSException;
  
//----------------------------------------------------------------------
/**
 * Stopps the data processing. The Data Processor disconnects from the
 * GPSDevice.
 *
 * @exception if an error occured on disconnecting.
 */
  public void close()
    throws GPSException;
  
//----------------------------------------------------------------------
/**
 * Sets the GPSDevice where the data will be retrieved from.
 *
 * @param gps_device the GPSDevice to retrieve data from.
 */
  public void setGPSDevice(GPSDevice gps_device);

//----------------------------------------------------------------------
/**
 * Returns the GPSDevice where the data will be retrieved from.
 *
 * @return the GPSDevice where the data will be retrieved from.
 */
  public GPSDevice getGPSDevice();

//----------------------------------------------------------------------
/**
 * Returns the last received position from the GPSDevice or
 * <code>null</code> if no position was retrieved until now.
 * @return the position from the GPSDevice.
 */

  public GPSPosition getGPSPosition();


//----------------------------------------------------------------------
/**
 * Returns the last received heading (direction) from the GPSDevice or
 * <code>-1.0</code> if no heading was retrieved until now.
 * @return the heading from the GPSDevice.
 */
  public float getHeading();


//----------------------------------------------------------------------
/**
 * Returns the last received data from the GPSDevice that is named by
 * the <code>key</code> or <code>null</code> if no data with the given
 * key was retrieved until now. The naming scheme for the keys is
 * taken from the NMEA standard (e.g. GLL for location, HDG for
 * heading, ...)
 *
 * @param key the name of the data.
 * @return the heading from the GPSDevice.
 * @exception IllegalArgumentException if the <code>key</code> is
 * <code>null</code>.
 */
  public Object getGPSData(String key)
    throws IllegalArgumentException;

//----------------------------------------------------------------------
/**
 * Returns a map containing the last received data from the GPSDevice
 * or <code>null</code>, if no data was retrieved until now. The
 * naming scheme for the keys is taken from the NMEA standard
 * (e.g. GLL for location, HDG for heading, ...)
 *
 * @return a map containing all key-value pairs of GPS data.  */
  public Map getGPSData();


//--------------------------------------------------------------------------------
/**
 * Get a list of waypoints from the gps device.
 * @return a list of <code>GPSWaypoint</code> objects.
 *
 * @throws UnsupportedOperationException if the operation is not
 * supported by the gps device or by the protocol used.
 * @throws GPSException if the operation threw an exception
 * (e.g. communication problem).
 * @see GPSWaypoint
 */
  public List getWaypoints()
    throws UnsupportedOperationException, GPSException;

//--------------------------------------------------------------------------------
/**
 * Write the waypoints to the gps device.
 * @param waypoints The new waypoints.
 *
 * @throws UnsupportedOperationException if the operation is not
 * supported by the gps device or by the protocol used.
 * @throws GPSException if the operation threw an exception
 * (e.g. communication problem).
 * @see GPSWaypoint
 */
  public void setWaypoints(List waypoints)
    throws UnsupportedOperationException, GPSException;

//--------------------------------------------------------------------------------
/**
 * Get a list of routes from the gps device.
 * @return a list of <code>GPSRoute</code> objects.
 *
 * @throws UnsupportedOperationException if the operation is not
 * supported by the gps device or by the protocol used.
 * @throws GPSException if the operation threw an exception
 * (e.g. communication problem).
 * @see GPSRoute
 */
  public List getRoutes()
    throws UnsupportedOperationException, GPSException;

//--------------------------------------------------------------------------------
/**
 * Write the routes to the gps device.
 * @param waypoints The new waypoints.
 *
 * @throws UnsupportedOperationException if the operation is not
 * supported by the gps device or by the protocol used.
 * @throws GPSException if the operation threw an exception
 * (e.g. communication problem).
 * @see GPSWaypoint
 */
  public void setRoutes(List routes)
    throws UnsupportedOperationException, GPSException;

//--------------------------------------------------------------------------------
/**
 * Get a list of tracks from the gps device.
 * @return a list of <code>GPSRoute</code> objects.
 *
 * @throws UnsupportedOperationException if the operation is not
 * supported by the gps device or by the protocol used.
 * @throws GPSException if the operation threw an exception
 * (e.g. communication problem).
 * @see GPSRoute
 */
  public List getTracks()
    throws UnsupportedOperationException, GPSException;

//--------------------------------------------------------------------------------
/**
 * Write the tracks to the gps device.
 * @param waypoints The new waypoints.
 *
 * @throws UnsupportedOperationException if the operation is not
 * supported by the gps device or by the protocol used.
 * @throws GPSException if the operation threw an exception
 * (e.g. communication problem).
 * @see GPSRoute
 */
  public void setTracks(List tracks)
    throws UnsupportedOperationException, GPSException;

//----------------------------------------------------------------------
/**
 * Adds a listener for GPS data change events.
 *
 * @param listener the listener to be added.
 * @param key the key of the GPSdata to be observed.
 * @exception IllegalArgumentException if <code>key</code> or
 * <code>listener</code> is <code>null</code>. 
 */
  public void addGPSDataChangeListener(String key, PropertyChangeListener listener)
    throws IllegalArgumentException;

//----------------------------------------------------------------------
/**
 * Adds a listener for GPS data change events.
 *
 * @param listener the listener to be added.
 * @exception IllegalArgumentException if <code>listener</code> is
 * <code>null</code>.  
 */
  public void addGPSDataChangeListener(PropertyChangeListener listener)
    throws IllegalArgumentException;

  
//----------------------------------------------------------------------
/**
 * Removes a listener for GPS data change events.
 *
 * @param listener the listener to be removed.
 * @param key the key of the GPSdata to be observed.
 * @exception IllegalArgumentException if <code>key<code> or
 * <code>listener</code> is <code>null</code>.  
 */
  public void removeGPSDataChangeListener(String key, PropertyChangeListener listener)
    throws IllegalArgumentException;


//----------------------------------------------------------------------
/**
 * Adds a listener for raw GPS data (for loggin purpose or similar).
 *
 * @param listener the listener to be added.
 * @exception IllegalArgumentException if <code>listener</code> is
 * <code>null</code>.  
 */
  public void addGPSRawDataListener(GPSRawDataListener listener)
    throws IllegalArgumentException;

//----------------------------------------------------------------------
/**
 * Removes a listener for faw GPS data.
 *
 * @param listener the listener to be removed.
 * @exception IllegalArgumentException if <code>listener</code> is
 * <code>null</code>.  
 */
  public void removeGPSRawDataListener(GPSRawDataListener listener)
    throws IllegalArgumentException;

//----------------------------------------------------------------------
/**
 * Removes a listener for GPS data change events.
 *
 * @param listener the listener to be removed.
 * @exception IllegalArgumentException if <code>listener</code> is
 * <code>null</code>.  
 */
  public void removeGPSDataChangeListener(PropertyChangeListener listener)
    throws IllegalArgumentException;



}


