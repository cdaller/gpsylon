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

import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.dinopolis.util.Debug;

//----------------------------------------------------------------------
/**
 * All classes extending this class are interpreting data from
 * a GPSDevice (serial gps-receivier, file containing gps data, ...)
 * and provide this information in a uniform way. So an NMEA-processor
 * interprets NMEA sentences, while a Garmin-Processor understands the
 * garmin protocol.
 * <P>
 * This abstract class adds some basic functionality all
 * GSPDataProcessors might use.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public abstract class GPSGeneralDataProcessor implements GPSDataProcessor
{
/** the GPSDevice */
  protected GPSDevice gps_device_ = null;
/** the map the gps data is stored in */
  protected Map gps_data_ = new HashMap();
/** the lock object for the gps data */
  protected Object gps_data_lock_ = new Object();
/** the gps data change listeners */
  protected PropertyChangeSupport property_change_support_;
/** the raw data listener */
  protected Vector raw_data_listener_;  

//----------------------------------------------------------------------
/**
 * Starts the data processing. The Data Processor connects to the
 * GPSDevice and starts retrieving information.
 *
 * @exception if an error occured on connecting.
 */
  public abstract void open()
    throws GPSException;
  
//----------------------------------------------------------------------
/**
 * Stopps the data processing. The Data Processor disconnects from the
 * GPSDevice.
 *
 * @exception if an error occured on disconnecting.
 */
  public abstract void close()
    throws GPSException;
  
//----------------------------------------------------------------------
/**
 * Sets the GPSDevice where the data will be retrieved from.
 *
 * @param gps_device the GPSDevice to retrieve data from.
 */
  public void setGPSDevice(GPSDevice gps_device)
  {
    gps_device_ = gps_device;
  }

//----------------------------------------------------------------------
/**
 * Returns the GPSDevice where the data will be retrieved from.
 *
 * @return the GPSDevice where the data will be retrieved from.
 */
  public GPSDevice getGPSDevice()
  {
    return(gps_device_);
  }

//----------------------------------------------------------------------
/**
 * Returns the last received position from the GPSDevice or
 * <code>null</code> if no position was retrieved until now.
 * @return the position from the GPSDevice.
 */

  public abstract GPSPosition getGPSPosition();


//----------------------------------------------------------------------
/**
 * Returns the last received heading (direction) from the GPSDevice or
 * <code>-1.0</code> if no heading was retrieved until now.
 * @return the heading from the GPSDevice.
 */
  public abstract float getHeading();


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
    throws IllegalArgumentException
  {
    if (key == null)
      throw new IllegalArgumentException("The key must not be <null>.");
    synchronized(gps_data_lock_)
    {
      return(gps_data_.get(key));
    }
  }

//----------------------------------------------------------------------
/**
 * Returns a map containing the last received data from the GPSDevice
 * or <code>null</code>, if no data was retrieved until now. The
 * naming scheme for the keys is taken from the NMEA standard
 * (e.g. GLL for location, HDG for heading, ...)
 *
 * @return a map containing all key-value pairs of GPS data.  */
  public Map getGPSData()
  {
    synchronized(gps_data_lock_)
    {
      return((Map)((HashMap)gps_data_).clone());
    }
  }

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
    throws IllegalArgumentException
  {
    if (key == null)
      throw new IllegalArgumentException("The key must not be <null>.");
    if (listener == null)
      throw new IllegalArgumentException("The listener must not be <null>.");

    if (property_change_support_ == null)
      property_change_support_ = new PropertyChangeSupport(this);
    property_change_support_.addPropertyChangeListener(key,listener);
  }

  
//----------------------------------------------------------------------
/**
 * Adds a listener for GPS data change events.
 *
 * @param listener the listener to be added.
 * @exception IllegalArgumentException if <code>listener</code> is
 * <code>null</code>.  
 */
  public void addGPSDataChangeListener(PropertyChangeListener listener)
    throws IllegalArgumentException
  {
    if (listener == null)
      throw new IllegalArgumentException("The listener must not be <null>.");

    if (property_change_support_ == null)
      property_change_support_ = new PropertyChangeSupport(this);
    property_change_support_.addPropertyChangeListener(listener);
  }
  
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
    throws IllegalArgumentException
  {
    if (key == null)
      throw new IllegalArgumentException("The key must not be <null>.");
    if (listener == null)
      throw new IllegalArgumentException("The listener must not be <null>.");

    if (property_change_support_ != null)
      property_change_support_.removePropertyChangeListener(key,listener);
  }


//----------------------------------------------------------------------
/**
 * Removes a listener for GPS data change events.
 *
 * @param listener the listener to be removed.
 * @exception IllegalArgumentException if <code>listener</code> is
 * <code>null</code>.  
 */
  public void removeGPSDataChangeListener(PropertyChangeListener listener)
    throws IllegalArgumentException
  {
    if (listener == null)
      throw new IllegalArgumentException("The listener must not be <null>.");

    if (property_change_support_ != null)
      property_change_support_.removePropertyChangeListener(listener);
  }

//----------------------------------------------------------------------
/**
 * Changes the gps data with given key. If there exists a gps data
 * with the given key, the new value replaces the old one. If the
 * value is set to <code>null</code> the gps data is deleted. If there
 * did not exist a gps data with the given key, it will be created.
 *
 * @param key the key of the gps data to be changed.
 * @param value the value of the gps data to be changed, or
 * <code>null</code> to delete the entry.
 * @exception IllegalArgumentException if the key is
 * <code>null</code>.  
 */

  protected void changeGPSData(String key, Object value)
    throws IllegalArgumentException
  {
    if (key == null)
    {
      throw new IllegalArgumentException("The key must not be <null>!");
    }
    Object old_value;
    synchronized(gps_data_lock_)
    {
      old_value = gps_data_.get(key);
      if (value == null)
        gps_data_.remove(key);
      else
        gps_data_.put(key,value);
    }
    if(Debug.DEBUG)
      Debug.println("GPSGeneralDataProcessor_changeData","fire event for key "
                    +key+" oldvalue="+old_value+" new="+value);
    if (property_change_support_ != null)
      property_change_support_.firePropertyChange(key,old_value,value);
  }

//----------------------------------------------------------------------
/**
 * Fire the event for raw data that was received (for loggers etc.)
 *
 * @param raw_data the raw_data
 * @param length the number of characters to use from the raw_data array. 
 */

  protected void fireRawDataReceived(char[] raw_data, int offset, int length)
  {
    if (raw_data_listener_ == null)
      return;
    Iterator listeners;
    synchronized(raw_data_listener_)
    {
      listeners = ((Vector)raw_data_listener_.clone()).iterator();
    }
    while(listeners.hasNext())
    {
      ((GPSRawDataListener)listeners.next()).gpsRawDataReceived(raw_data,offset,length);
    }
  }
  
//----------------------------------------------------------------------
/**
 * Adds a listener for raw GPS data (for loggin purpose or similar).
 *
 * @param listener the listener to be added.
 * @exception IllegalArgumentException if <code>listener</code> is
 * <code>null</code>.  
 */
  public void addGPSRawDataListener(GPSRawDataListener listener)
    throws IllegalArgumentException
  {
    if (listener == null)
      throw new IllegalArgumentException("The listener must not be <null>.");
    if (raw_data_listener_ == null)
      raw_data_listener_ = new Vector();
    synchronized(raw_data_listener_)
    {
      raw_data_listener_.addElement(listener);
    }
  }

//----------------------------------------------------------------------
/**
 * Removes a listener for faw GPS data.
 *
 * @param listener the listener to be removed.
 * @exception IllegalArgumentException if <code>listener</code> is
 * <code>null</code>.  
 */
  public void removeGPSRawDataListener(GPSRawDataListener listener)
    throws IllegalArgumentException
  {
    if (listener == null)
      throw new IllegalArgumentException("The listener must not be <null>.");
    if (raw_data_listener_ == null)
      return;

    synchronized(raw_data_listener_)
    {
      raw_data_listener_.remove(listener);
    }

//        Iterator listeners = raw_data_listener_.iterator();
//        while(listeners.hasNext())
//        {
//  	  if(listeners.next() == listener)
//  	  {
//  	      listeners.remove();
//  	      return;
//  	  }
//        }
  }

}
