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

package org.dinopolis.gpstool.util;

import java.util.Comparator;

import org.dinopolis.gpstool.MapInfo;

//----------------------------------------------------------------------
/**
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class MapInfoScaleComparator  implements Comparator
{

  static public final double COORDINATE_EPSILON = 1e-8;

//   public MapInfoScaleComparator()
//   {
//   }
  
//----------------------------------------------------------------------
/**
 * Compares two MapInfos. The first criteria used is the scale. The
 * comparison results in a ASCENDING order of scales (small values of
 * scale first (100,10000,1000000))!  If scales are equal, the
 * filename is used in the comparision.
 */
  
  public int compare(Object one, Object two)
  {
    MapInfo info1 = (MapInfo)one;
    MapInfo info2 = (MapInfo)two;

//     if(Debug.DEBUG)
//       Debug.println("MapInfoScaleComparator","comparing "+info1 +" with " + info2);

    if(info1.getScale() < info2.getScale())
      return(-1);

    if(info1.getScale() > info2.getScale())
      return(1);

//     if(Debug.DEBUG)
//       Debug.println("MapInfoScaleComparator","scales are equal");
    
        // use center (sort from upper left to lower right)
    double latitude1 = info1.getLatitude();
    double longitude1 = info1.getLongitude();
    double latitude2 = info2.getLatitude();
    double longitude2 = info2.getLongitude();

    if((Math.abs(latitude1 - latitude2) < COORDINATE_EPSILON)
       && (Math.abs(longitude1 - longitude2) < COORDINATE_EPSILON))
    { 
//       if(Debug.DEBUG)
//         Debug.println("MapInfoScaleComparator","centers are equal");
    // center is equal, so use the filename
      int filename_comparison = info1.getFilename().compareTo(info2.getFilename());
      if (filename_comparison != 0)
        return(filename_comparison);
    }
        // use distance from top/left corner (therefore the -latitude
        // (should be 90-latitude, but as the 90 is on t=both sides of
        // the unequation...) as criteria:
    return(Double.compare( -latitude1 + longitude1,-latitude2 + longitude2));
  }

  public boolean equals(Object other)
  {
    return(other instanceof MapInfoScaleComparator);
  }
}




