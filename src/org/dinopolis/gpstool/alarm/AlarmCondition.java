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

import java.beans.PropertyChangeListener;

//----------------------------------------------------------------------
/**
 * An AlarmCondition is informed about any changes by the use of a
 * property change event. If the condition is true (e.g. a specific
 * position or speed is reached), it must call the trigger and pass
 * the action that was previously set.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public interface AlarmCondition extends PropertyChangeListener
{

//----------------------------------------------------------------------
/**
 * Sets the action to pass to the alarm trigger in case of an alarm.
 *
 * @param action the action to pass to the trigger, if the condition
 * becomes true.
 */
  public void setAlarmAction(AlarmAction action);

//----------------------------------------------------------------------
/**
 * Returns the action to pass to the alarm listener in case of an alarm.
 *
 * @return the action to pass to the listener, if the condition
 * becomes true.
 */
  public AlarmAction getAlarmAction();

//----------------------------------------------------------------------
/**
 * Sets the alarm trigger to be informed about an alarm to occur..
 *
 * @param trigger the trigger to call, if the condition becomes true.
 */
  public void setAlarmListener(AlarmListener listener);

//----------------------------------------------------------------------
/**
 * Returns the alarm listener to be informed about an alarm to occur..
 *
 * @return the listener to call, if the condition becomes true.
 */
  public AlarmListener getAlarmListener();

//----------------------------------------------------------------------
/**
 * Returns true if the condition of the alarm is true.
 *
 * @return true if the condition is the alarm true.
 */
  public boolean isAlarm();
}



