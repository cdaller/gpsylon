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

package org.dinopolis.gpstool.alarm;

import java.beans.PropertyChangeEvent;
import org.dinopolis.gpstool.util.GeoMath;
import org.dinopolis.gpstool.gpsinput.GPSPosition;
import org.dinopolis.gpstool.gpsinput.GPSDataProcessor;

//----------------------------------------------------------------------
/**
 * A PositionAlarmCondition is informed about any changes of the
 * current position by the use of a property change event or by
 * calling the setPosition method. If the condition is true (a
 * specific position resp. area is entered, it calls the listener and
 * passes the action that was previously set.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class PositionAlarmCondition implements AlarmCondition
{
  protected AlarmAction action_;
  protected AlarmListener listener_;

  protected double latitude_rad_;
  protected double longitude_rad_;
  protected double distance_;

  protected GPSPosition tmp_pos_;

  protected boolean in_alarm_area_ = false;
  
  protected double hysteresis_distance_;  // TODO
  
//----------------------------------------------------------------------
/**
 * Constructs an alarm condition that triggers an action if the gps
 * position is near the position iven here.
 *
 * @param latitude
 * @param longitude
 * @param distance in meters
 */
  public PositionAlarmCondition(double latitude, double longitude, double distance)
  {
    latitude_rad_ = Math.toRadians(latitude);
    longitude_rad_ = Math.toRadians(longitude);
    distance_ = distance;
  }
  
//----------------------------------------------------------------------
/**
 * Callback method for property change events (GPSDataProcessor). This
 * class forwards <code>GPSDataProcessor.LOCATION</code> events to the
 * <code>setPosition</code> method.
 * 
 * @param event the property change event.
 */

  public void propertyChange(PropertyChangeEvent event)
  {
    if(event.getPropertyName().equals(GPSDataProcessor.LOCATION))
    {
      tmp_pos_ = (GPSPosition)event.getNewValue();
      if(tmp_pos_ == null)
        return;
      setPosition(tmp_pos_.getLatitude(),tmp_pos_.getLongitude());
    }
  }

//----------------------------------------------------------------------
/**
 * Sets the current position. This method performs the check, if the
 * alarm condition is true and calls the alarm listener in that
 * case. If no listener was set, the <code>isAlarm</code> method
 * returns, if the position triggered an alarm.
 * 
 * @param latitude the latitude to calculate the condition for.
 * @param longitude the latitude to calculate the condition for.
 */
  public void setPosition(double latitude, double longitude)
  {
      double pos_latitude = Math.toRadians(latitude);
      double pos_longitude = Math.toRadians(longitude);

      double distance = GeoMath.distanceDegrees(latitude_rad_,longitude_rad_,
                                                pos_latitude,pos_longitude)
                        * GeoMath.M_PER_RADIAN;

      if(isAlarmDistance(distance))
      {
        if(!in_alarm_area_)
        {
          listener_.gpsAlarm(new AlarmEvent(this));
          in_alarm_area_ = true;
        }
      }
      else
      {
        if(in_alarm_area_)
        {
          in_alarm_area_ = false; // reset alarm state
        }
      }
  }

//----------------------------------------------------------------------
/**
 * Returns true, if the given distance is less than the alarm
 * distance.  This method may be overriden by subclasses that triggers
 * on a distance.
 *
 * @param distance the distance to the current gps position.
 */
  public boolean isAlarmDistance(double distance)
  {
    return(distance < distance_);
  }
  
//----------------------------------------------------------------------
/**
 * Sets the action to pass to the alarm listener in case of an alarm.
 *
 * @param action the action to pass to the listener, if the condition
 * becomes true.
 */
  public void setAlarmAction(AlarmAction action)
  {
    action_ = action;
  }

//----------------------------------------------------------------------
/**
 * Returns the action to pass to the alarm listener in case of an alarm.
 *
 * @return the action to pass to the listener, if the condition
 * becomes true.
 */
  public AlarmAction getAlarmAction()
  {
    return(action_);
  }

//----------------------------------------------------------------------
/**
 * Sets the alarm listener to be informed about an alarm to occur..
 *
 * @param listener the listener to call, if the condition becomes true.
 */
  public void setAlarmListener(AlarmListener listener)
  {
    listener_ = listener;
  }

//----------------------------------------------------------------------
/**
 * Returns the alarm listener to be informed about an alarm to occur..
 *
 * @return the listener to call, if the condition becomes true.
 */
  public AlarmListener getAlarmListener()
  {
    return(listener_);
  }

//----------------------------------------------------------------------
/**
 * Returns true if the condition of the alarm is true.
 *
 * @return true if the condition is the alarm true.
 */
  public boolean isAlarm()
  {
    return(in_alarm_area_);
  }
}



