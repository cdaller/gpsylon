/***********************************************************************
 * @(#)$RCSfile$   $Revision$$Date$
*
 * Copyright (c) 2002 IICM, Graz University of Technology
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
 * The AlarmEvent holds information about the source (the trigger) of
 * the alarm.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class AlarmEvent  
{
  AlarmCondition alarmtrigger_;
  
//----------------------------------------------------------------------
/**
 * Constructor
 *
 */
  public AlarmEvent()
  {
  }
  
//----------------------------------------------------------------------
/**
 * Constructor
 *
 */
  public AlarmEvent(AlarmCondition trigger)
  {
    alarmtrigger_ = trigger;
  }


//----------------------------------------------------------------------
/**
 * Get the alarmtrigger.
 *
 * @return the alarmtrigger.
 */
  public AlarmCondition getSource() 
  {
    return (alarmtrigger_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the alarmtrigger.
 *
 * @param alarmtrigger the alarmtrigger.
 */
  public void setSource(AlarmCondition alarmtrigger) 
  {
    alarmtrigger_ = alarmtrigger;
  }
  

//----------------------------------------------------------------------
/**
 * Get the action of the trigger.
 *
 * @return the action.
 */
  public AlarmAction getAction() 
  {
    return (alarmtrigger_.getAlarmAction());
  }
  
// //----------------------------------------------------------------------
// /**
//  * Set the action.
//  *
//  * @param action the action.
//  */
//   public void setAction(AlarmAction action) 
//   {
//     action_ = action;
//   }
  
 public String toString()
 {
   return("AlarmEvent[event trigger="+alarmtrigger_+"]");
 }
  
}












