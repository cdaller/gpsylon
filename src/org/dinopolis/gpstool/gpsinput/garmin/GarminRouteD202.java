/***********************************************************************
 * @(#)$RCSfile$   $Revision$$Date$
 *
 * Copyright (c) 2003 IICM, Graz University of Technology
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


package org.dinopolis.gpstool.gpsinput.garmin;

import org.dinopolis.gpstool.gpsinput.GPSRoute;



//----------------------------------------------------------------------
/**
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GarminRouteD202 extends GarminRoute  
{
  public GarminRouteD202(int[] buffer)
  {
    setIdentification(GarminDataConverter.getGarminString(buffer,2));
  }

  public GarminRouteD202(GarminPackage pack)
  {
    setIdentification(pack.getNextAsString(Math.min(pack.getPackageSize(),51)));
  }

  public GarminRouteD202(GPSRoute route)
  {
    setIdentification(route.getIdentification());
  }

//----------------------------------------------------------------------
/**
 * Convert data type to {@link GarminPackage}
 * @return GarminPackage representing content of data type.
 */
  public GarminPackage toGarminPackage(int package_id)
  {
    int data_length = Math.min(getIdentification().length()+1,51);
    GarminPackage pack = new GarminPackage(package_id,data_length);
    pack.setNextAsString(getIdentification(),data_length,true);
    return (pack);
  }

}
