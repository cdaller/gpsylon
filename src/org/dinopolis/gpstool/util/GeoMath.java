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

//----------------------------------------------------------------------
/**
 * This class provides some calculations for latitude/longitude. The
 * formulas are taken from http://williams.best.vwh.net/avform.html
 * Beware that on the web page, negative longitude indicates west!
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GeoMath  
{
  /** earth radius at equator in meters */
  public static final double RADIUS_EQUATOR = 6378137;  // equatorial radius in m
  /** circumference of earth at equator in meters */
  public static final double CIRCUMFERENCE_EQUATOR = RADIUS_EQUATOR * 2 * Math.PI;  // equatorial circumference in m
  /** meters per degree at equator */
  public static final double M_PER_DEGREE = CIRCUMFERENCE_EQUATOR / 360.0;  
  /** meters per radian at equator */
  public static final double M_PER_RADIAN = CIRCUMFERENCE_EQUATOR / (2 * Math.PI);  

  static final double EPSILON = 1E-20;
  static final double EQUAL_EPSILON = 1E-10;

  
//----------------------------------------------------------------------
/**
 * Calculates the distance between two points.
 *
 * @param latitude1 the latitude of the first point given in degrees.
 * @param longitude1 the longitude of the first point given in degrees.
 * @param latitude2 the latitude of the second point given in degrees.
 * @param longitude2 the longitude of the second point given in degrees.
 * @return the distance in meters.
 * FIXXME: The calculation is wrong (does not respect that a degree close to the pole is 
 * less (in meters) than a degree at the equator!
 */
  public static double distance(double latitude1, double longitude1,
                                double latitude2, double longitude2)
  {
    double distance_deg = distanceDegrees(latitude1,longitude1,
                                          latitude2,longitude2);
    return(distance_deg * M_PER_DEGREE);
  }

//----------------------------------------------------------------------
/**
 * Calculates the distance between two points.
 *
 * @param latitude1 the latitude of the first point given in degrees.
 * @param longitude1 the longitude of the first point given in degrees.
 * @param latitude2 the latitude of the second point given in degrees.
 * @param longitude2 the longitude of the second point given in degrees.
 * @return the distance in degrees.
 */
  public static double distanceDegrees(double latitude1, double longitude1,
                                       double latitude2, double longitude2)
  {
    double distance_rad = distanceRadians(Math.toRadians(latitude1),
                                          Math.toRadians(longitude1),
                                          Math.toRadians(latitude2),
                                          Math.toRadians(longitude2));
    return(Math.toDegrees(distance_rad));
  }

//----------------------------------------------------------------------
/**
 * Calculates the distance between two points.
 *
 * @param latitude1 the latitude of the first point given in radians.
 * @param longitude1 the longitude of the first point given in radians.
 * @param latitude2 the latitude of the second point given in radians.
 * @param longitude2 the longitude of the second point given in radians.
 * @return the distance in radians.
 */
  public static double distanceRadians(double latitude1, double longitude1,
                                       double latitude2, double longitude2)
  {
    double pdiff = Math.sin((latitude2 - latitude1)/2);
    double ldiff = Math.sin((longitude2 - longitude1)/2);
    double rval = Math.sqrt((pdiff*pdiff) +
                            Math.cos(latitude2)*Math.cos(latitude1)*(ldiff*ldiff));
    return(2.0 * Math.asin(rval));
  }


//----------------------------------------------------------------------
/**
 * Calculates the course between two points.
 *
 * @param latitude1 the latitude of the first point given in degrees.
 * @param longitude1 the longitude of the first point given in degrees.
 * @param latitude2 the latitude of the second point given in degrees.
 * @param longitude2 the longitude of the second point given in degrees.
 * @return the course in degrees [0,360].
 */
  public static double courseDegrees(double latitude1, double longitude1,
                                     double latitude2, double longitude2)
  {
    double course = Math.toDegrees(courseRadians(Math.toRadians(latitude1),
                                        Math.toRadians(longitude1),
                                        Math.toRadians(latitude2),
                                        Math.toRadians(longitude2)));
    if(course < 0.0)
      return(360.0 + course);
    else
      return(course);
  }
  
//----------------------------------------------------------------------
/**
 * Calculates the course between two points.
 *
 * @param latitude1 the latitude of the first point given in radians.
 * @param longitude1 the longitude of the first point given in radians.
 * @param latitude2 the latitude of the second point given in radians.
 * @param longitude2 the longitude of the second point given in radians.
 * @return the course in radians (-pi,pi).
 */
  public static double courseRadians(double latitude1, double longitude1,
                                     double latitude2, double longitude2)
  {
    if(Math.abs(Math.cos(latitude1)) < EPSILON)   // EPS a small number ~ machine precision
    {
      if(latitude1 > 0)
        return(Math.PI);        //  starting from N pole
      else
        return(0.0);         //  starting from S pole
    }

//     double deg = Math.atan2(Math.sin(lon1-lon2)*Math.cos(lat2),
//                             Math.cos(lat1)*Math.sin(lat2)-Math.sin(lat1)*Math.cos(lat2)*Math.cos(lon1-lon2));
//         // IEEEremainder is better than modulo operator
//         // (http://www.galileocomputing.de/openbook/javainsel2/java_050001.htm)
//     return(Math.IEEEremainder(deg,2*Math.PI));

    double ldiff = longitude2 - longitude1;
    double cosphi = Math.cos(latitude2);
    
    return Math.atan2(cosphi*Math.sin(ldiff),
                      (Math.cos(latitude1)*Math.sin(latitude2) -
                       Math.sin(latitude1)*cosphi*
                       Math.cos(ldiff)));
    
  }

//----------------------------------------------------------------------
/**
 * Checks if a ~= b.
 * Use this to test equality of floating point numbers.
 *
 * @param a double
 * @param b double
 * @return true if Math.abs(a-b) <= EQUAL_EPSILON, false otherwise.
 */
  public static boolean approximatelyEqual(double a, double b)
  {
    return(approximatelyEqual(a,b,EQUAL_EPSILON));
  }

//----------------------------------------------------------------------
/**
 * Checks if a ~= b.
 * Use this to test equality of floating point numbers.
 *
 * @param a double
 * @param b double
 * @param epsilon the allowable error
 * @return true if Math.abs(a-b) <= epsilon, false otherwise.
 */
  public static boolean approximatelyEqual(double a, double b, double epsilon)
  {
    return(Math.abs(a-b) <= epsilon);
  }

  
  public static void main(String[] args)
  {
    System.out.println("north:");
    System.out.println("deg "+courseDegrees(47.0,16.0,48.0,16.0));
    System.out.println("rad "+courseRadians(47.0,16.0,48.0,16.0));

    System.out.println("south:");
    System.out.println("deg "+courseDegrees(47.0,16.0,46.0,16.0));
    System.out.println("rad "+courseRadians(47.0,16.0,46.0,16.0));
    
    System.out.println("north east:");
    System.out.println("deg "+courseDegrees(47.0,16.0,48.0,17.0));
    System.out.println("rad "+courseRadians(47.0,16.0,48.0,17.0));

    System.out.println("west:");
    System.out.println("deg "+courseDegrees(47.0,16.0,47.0,15.0));
    System.out.println("rad "+courseRadians(47.0,16.0,47.0,15.0));
  }
}


