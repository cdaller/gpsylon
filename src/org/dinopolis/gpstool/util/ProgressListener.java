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


package org.dinopolis.gpstool.util;

//----------------------------------------------------------------------
/**
 * Interface to be informed about progress (in reading/writing data
 * from gps device, from files, etc.).
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public interface ProgressListener
{
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
  public void actionStart(String action_id, int min_value, int max_value);
  
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
  public void actionProgress(String action_id, int current_value);

//----------------------------------------------------------------------
/**
 * Callback to inform listeners about the end of the action.
 *
 * @param action_id the id of the action that is started. This id may
 * be used to display a message for the user.
 */
  public void actionEnd(String action_id);

}
