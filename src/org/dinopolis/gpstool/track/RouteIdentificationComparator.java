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

package org.dinopolis.gpstool.track;

import java.util.Comparator;
import org.dinopolis.gpstool.gpsinput.GPSRoute;

//----------------------------------------------------------------------
/**
 * Compares two {@link org.dinopolis.gpstool.gpsinput.GPSRoute}
 * objects on its identifications.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class RouteIdentificationComparator implements Comparator
{

//----------------------------------------------------------------------
/**
 * Compares two GPSRoute objects on their identification.
 */
  
  public int compare(Object one, Object two)
  {
    return(((GPSRoute)one).getIdentification().compareTo(((GPSRoute)two).getIdentification()));
  }

//----------------------------------------------------------------------
/**
 * Returns true if the identification of the two routes are equal.
 */
  public boolean equals(Object other)
  {
    return(other instanceof RouteIdentificationComparator);
  }
}




