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

//----------------------------------------------------------------------
/**
 * A LeavePositionAlarmCondition is informed about any changes by the use
 * of a property change event. If the condition is true (a
 * specific position resp. area is left), it calls the trigger
 * and passes the action that was previously set.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class LeavePositionAlarmCondition extends PositionAlarmCondition
{
  
//----------------------------------------------------------------------
/**
 * Constructs an alarm condition that triggers an action if the gps
 * position is near the position iven here.
 *
 * @param latitude
 * @param longitude
 * @param distance in meters
 */
  public LeavePositionAlarmCondition(float latitude, float longitude, float distance)
  {
    super(latitude,longitude,distance);
  }
  

//----------------------------------------------------------------------
/**
 * Returns true, if the given distance is less than the alarm distance.
 * This method may be overriden by subclasses that trigger on a distance.
 *
 * @param distance the distance to the current gps position.
 */
  public boolean isAlarmDistance(double distance)
  {
    return(distance >= distance_);
  }
  
}



