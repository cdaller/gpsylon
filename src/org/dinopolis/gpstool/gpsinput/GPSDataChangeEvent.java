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

import java.beans.PropertyChangeEvent;

//----------------------------------------------------------------------
/**
 * A "GPSDataChange" event gets delivered whenever GPS data changes.
 * A GPSDataChangeEvent object is sent as an argument to the
 * GPSDataChangeListener.
 * <P>
 * Normally GPSDataChangeEvents are accompanied by the name and the
 * old and new value of the changed gps data.  If the new value is a
 * primitive type (such as int or boolean) it must be wrapped as the
 * corresponding java.lang.Object type (such as Integer or Boolean).
 * <P>
 * Null values may be provided for the old and the new values if
 * their true values are not known.
 *
 * @author Christof Dallermassl
 * @version $Revision$ */

public class GPSDataChangeEvent extends PropertyChangeEvent 
{
//----------------------------------------------------------------------
/**
 * Constructs a new <code>GPSDataChangeEvent</code>.
 *
 * @param source  The source that fired the event.
 * @param property_name  The programmatic name of the gps data that was changed.
 * @param old_value  The old value of the gps data.
 * @param new_value  The new value of the gps data.
 */
  public GPSDataChangeEvent(Object source, String property_name, Object old_value,
                            Object new_value)
  {
    super(source,property_name, old_value, new_value);
  }
}





