/***********************************************************************
 * @(#)$RCSfile$   $Revision$ $Date$
 *
 * Copyright (c) 2000 IICM, Graz University of Technology
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


package org.dinopolis.util.debug;

import java.util.Date;

//---------------------------------------------------------------------
//---------------------------------------------------------------------
/**
 * Timer is used to measure time differences. It is mainly used by
 * the Debug class.  
 *
 * @author Christof Dallermassl <cdaller@iicm.edu>
 * @version $Id$
 *
 */
public class Timer 
{

  /** label of this Timer */
  protected String label_;
  /** debug-level of this Timer */
//  protected String level_;
  /** start-time of this Timer */
  protected Date start_time_;
  /** end-time of this Timer */
  protected Date end_time_;

//----------------------------------------------------------------------
/**
 * creates new Timer with the specified debug group and level
 *
 * @param label the label of the Timer
 */
  public Timer (String label) 
  {
    this.label_ = label;
  }

//----------------------------------------------------------------------
/**
 * starts the timer
 */
  public void startTimer() 
  {
    start_time_ = new Date (System.currentTimeMillis()); // actual time
  }

//----------------------------------------------------------------------
/**
 * stops the timer
 */
  public void stopTimer() 
  {
    end_time_ = new Date (System.currentTimeMillis());
  }

//----------------------------------------------------------------------
/**
 * @return time passed since Timer was started (in milliseconds)
 */
  public long getTime () 
  {
    stopTimer();
    return getTimeDifference();
  }

//----------------------------------------------------------------------
/**
 * @return the difference between start_time_ and end_time_ (in milliseconds)
 */
  long getTimeDifference () 
  {
    return (end_time_.getTime() - start_time_.getTime());
  }

//----------------------------------------------------------------------
/**
 * @return label of the timer
 */
  public String getLabel () 
  {
    return label_;
  }

//    //----------------------------------------------------------------------
//    /**
//     * @return level of the timer
//     */
//    public String getLevel () 
//    {
//      return level_;
//    }

  /**
   * @return String-representation with label, difference, start- and end-time
   */
  public String toString() 
  {
    return "TIMER " + label_ + " diff=" + getTimeDifference() + "ms started at "
                    + start_time_ + " ended at " + end_time_;
  }
}





