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


package org.dinopolis.gpstool.projection;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.proj.Orthographic;
import com.bbn.openmap.proj.ProjMath;
import com.bbn.openmap.proj.Projection;

//----------------------------------------------------------------------
/**
 * Implements the Flat projection for use of mapblast and expedia
 * maps. The algorithms are mostly copied from Fritz Ganter's gpsdrive
 * application. (http://gpsdrive.kraftvoll.at). As this class extends
 * an openmap base projection, the usage might not be clear. The main
 * methods are <code>forward</code> and <code>inverse</code>.
 * <p>
 * The forward method forwards
 * latitude/longitude to screen coordinates, the inverse method the
 * other way round. Usage:
 * <p>
 * java.awt.Point forward(float latitude, float longitude)<br>
 * java.awt.Point forward(float latitude, float longitude, java.awt.point reuse)<br>
 * java.awt.Point forward(com.bbn.openmap.LatLonPoint point)<br>
 * java.awt.Point forward(com.bbn.openmap.LatLonPoint point, java.awt.Point reuse)<br>
 * com.bbn.openmap.LatLonPoint inverse(int x, int y)<br>
 * com.bbn.openmap.LatLonPoint inverse(int x, int y, com.bbn.openmap.LatLonPoint reuse)<br>
 * com.bbn.openmap.LatLonPoint inverse(java.awt.Point screen_point, com.bbn.openmap.LatLonPoint reuse)
 *
 * @see com.bbn.openmap.proj.Projection
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class FlatProjection extends Orthographic
{

      /**
       * The name.
       */
  public final static transient String FlatProjectionName = "Flat";

      /**
       * The type of projection.
       */
  public final static transient int FlatProjectionType = 12345;

  LatLonPoint center_;
  float scale_;
  int width_;
  int height_;

  static double[] earth_radius = new double[201];

      /** scale / PIXELFACT gives meters per pixel (for mapblast)<br>
          d_screen [pixel] = d_map [m] * r_map [dpi] * (1/0.0254) in/m<br>
          d_map = d_real / scale<br>
          PIXELFACT = 72 dpi * 1/0.0254 in/m = 2834.65 pixel/m,<br>
      */
      // old value (from gpsdrive) public static final double PIXELFACT = 2817.947378;<br>
  public static final double PIXELFACT = 2834.65;

  public static final double RADIUS_EQUATOR = 6378137;  // equatorial radius 
  public static final double RADIUS_POLAR = 6356752.3;  // polar radius 
  public static final double EARTH_FLATTENING = (RADIUS_EQUATOR - RADIUS_POLAR) / RADIUS_EQUATOR;

  static final public double DIFF_LATITUDE_FACT = 2.00;


//----------------------------------------------------------------------
/**
 * Calculates the earth radii for all latitudes. Taken from gpsdrive by
 * Fritz Ganter (http://gpsdrive.kraftvoll.at)
 *
 */
  static
  {
        /*  Build array for earth radii */
    for (int latitude = -100; latitude <= 100; latitude++)
      earth_radius[latitude + 100] = calcEarthRadius(latitude);

  }
      /**
       * Construct a LLXY projection.
       *
       * @param center LatLonPoint center of projection
       * @param scale float scale of projection
       * @param width width of screen
       * @param height height of screen
       */
  public FlatProjection (LatLonPoint center, float scale, 
                         int width, int height) {
    super(center, scale, width, height, FlatProjectionType);
  }

  public FlatProjection (LatLonPoint center, float scale, 
                         int width, int height, int type) {
    super(center, scale, width, height, type);
  }

//      protected void finalize() {
//  	Debug.message("gc", "LLXY finalized");
//      }

      /**
       * Return stringified description of this projection.
       * 
       * @return String
       * @see com.bbn.openmap.proj.Projection#getProjectionID
       */
  public String toString() {
    return "FlatProjection[" + super.toString() + "]";
  }

      /**
       * Called when some fundamental parameters change.
       * 
       * Each projection will decide how to respond to this change.
       * For instance, they may need to recalculate "constant" paramters
       * used in the forward() and inverse() calls.
       */
  protected void computeParameters() {
    super.computeParameters();
  }

      /**
       * Sets radian latitude to something sane.  
       *
       * @param lat float latitude in radians
       * @return float latitude (-PI/2 &lt;= y &lt;= PI/2)
       * @see com.bbn.openmap.LatLonPoint#normalize_latitude(float)
       */
  public float normalize_latitude(float lat)
  {
    if (lat > NORTH_POLE)
    {
	    return NORTH_POLE;
    }
    else
      if (lat < SOUTH_POLE)
      {
	    return SOUTH_POLE;
      }
    return lat;
  }

      /**
       * Sets radian latitude to something sane.  
       *
       * @param lat float latitude in degrees
       * @return float latitude (-90 &lt;= y &lt;= 90)
       * @see com.bbn.openmap.LatLonPoint#normalize_latitude(float)
       */
  public static double normalize_latitude_degree(double lat)
  {
    if (lat > 90.0f)
    {
      return 90.0f;
    }
    else
      if (lat < -90.0f)
      {
	return -90.0f;
      }
    return lat;
  }

      /**
       * Checks if a LatLonPoint is plot-able.
       * 
       * A point is always plot-able in the LLXY projection.
       * 
       * @param lat float latitude in decimal degrees
       * @param lon float longitude in decimal degrees
       * @return boolean
       */
  public boolean isPlotable(float lat, float lon)
  {
    return true;
  }

      /**
       * Projects a point from Lat/Lon space to X/Y space.
       * 
       * @param pt LatLonPoint
       * @param p Point retval
       * @return Point p
       */
//   public Point forward(LatLonPoint pt, Point p)
//   {
//     return forward(pt.getLatitude(), pt.getLongitude(), p, false);
//   }

      /**
       * Forward projects a lat,lon coordinates.
       * 
       * @param lat raw latitude in decimal degrees
       * @param lon raw longitude in decimal degrees
       * @param p Resulting XY Point
       * @return Point p
       */
//   public Point forward(float lat, float lon, Point p)
//   {
//     return forward(lat, lon, p, false);
//   }

      /**
       * Forward projects lat,lon into XY space and returns a Point.
       * 
       * @param lat float latitude in radians
       * @param lon float longitude in radians
       * @param p Resulting XY Point
       * @param isRadian bogus argument indicating that lat,lon
       * arguments are in radians
       * @return Point p
       */
//   public Point forward(float lat, float lon, 
//                        Point p, boolean isRadian) 
//   {
//     if (isRadian) 
//     {
//       lat = ProjMath.radToDeg(normalize_latitude(lat));
//       lon = ProjMath.radToDeg(lon);
//     }
//     Point2D result = forward(getCenter().getLatitude(),
//                              getCenter().getLongitude(),
//                              lat,lon,getScale());
//     result.setLocation(result.getX() + getWidth()/2,
//                        -result.getY() + getHeight()/2); // - because of origin in lower left instead of upper right
//     p.setLocation((int)result.getX(),(int)result.getY());
//     return p;
//   }

      /**
       * Forward projects lat,lon into XY space and returns a Point.
       * 
       * @param lat float latitude in radians
       * @param lon float longitude in radians
       * @param p Resulting XY Point
       * @param az_var AzimuthVar or null
       * @return Point p
       */
  protected Point _forward(float lat, float lon,
                           Point p, AzimuthVar az_var)
  {
    Point2D result = forward(getCenter().getLatitude(),
                             getCenter().getLongitude(),
                             ProjMath.radToDeg(lat),ProjMath.radToDeg(lon),getScale());
    result.setLocation(result.getX() + getWidth()/2,
                       -result.getY() + getHeight()/2);
    p.setLocation((int)result.getX(),(int)result.getY());
    return(p);
  }

      /**
       * Inverse project a Point.
       * 
       * @param point x,y Point
       * @param llp resulting LatLonPoint
       * @return LatLonPoint llp
       */
  public LatLonPoint inverse(Point pt, LatLonPoint llp) {
    return inverse(pt.x, pt.y, llp);
  }

      /**
       * Inverse project x,y coordinates into a LatLonPoint.
       * 
       * @param x integer x coordinate
       * @param y integer y coordinate
       * @param llp LatLonPoint
       * @return LatLonPoint llp
       * @see com.bbn.openmap.proj.Proj#inverse(Point)
       */
  public LatLonPoint inverse(int x, int y, LatLonPoint llp) 
  {
    int delta_x = getWidth()/2 -x ;
    int delta_y = -1 *(getHeight()/2 - y) ;
    LatLonPoint result = inverse(getCenter().getLatitude(),
                                 getCenter().getLongitude(),
                                 delta_x,delta_y,getScale());
//  	System.out.println("xxx"+result);
//  	System.out.println(getCenter().getLatitude());
//  	System.out.println(getCenter().getLongitude());
    llp.setLatLon((float)result.getLatitude(),
                  (float)result.getLongitude());
    return llp;
  }

      /**
       * Get the name string of the projection.
       */
  public String getName() {
    return FlatProjectionName;
  }

//----------------------------------------------------------------------
/**
 * This method calculates the horizontal and vertical distance of two
 * locations given in latitude and longitude. The result is in pixels
 * on the screen. The closer the two locations are, the more accurate
 * is the result! A kind of flat projection is used. The algorithm is
 * taken from gpsdrive of Fritz Ganter (http://gpsdrive.kraftvoll.at)
 *
 * @param reference_latitude the latitude of the reference location in degrees
 * @param reference_longitude the longitude of the reference location in degrees
 * @param latitude the latitude of the location
 * @param longitude the longitude of the location
 * @param scale the scale to use.
 * @return a point containing of the horizontal and vertical distance in meters. 
 */
  
  static public Point2D forward (float reference_latitude, float reference_longitude,
                                 float latitude, float longitude, float scale)
  {
    Point2D point = forward(reference_latitude,reference_longitude,latitude,longitude);
    double posx = point.getX();
    double posy = point.getY();

        // adapt to given map (scale and meters per pixel):
    posx = posx * PIXELFACT / scale;

        // adapt to given map (scale and meters per pixel):
    posy = posy * PIXELFACT / scale;

    return (new Point2D.Double(posx,posy));
  }

//----------------------------------------------------------------------
/**
 * This method calculates the horizontal and vertical distance of two
 * locations given in latitude and longitude. The closer the two
 * locations are, the more accurate is the result! A kind of flat
 * projection is used. The algorithm is taken from gpsdrive of Fritz
 * Ganter (http://gpsdrive.kraftvoll.at)
 *
 * @param reference_latitude the latitude of the reference location in degrees
 * @param reference_longitude the longitude of the reference location in degrees
 * @param latitude the latitude of the location
 * @param longitude the longitude of the location
 * @return a point containing of the horizontal and vertical distance in meters. 
 */
  
  static public Point2D forward (float reference_latitude, float reference_longitude,
                                 float latitude, float longitude)
  {
    double dif;
    double posx,posy;
    double earth_rad = earth_radius[(int) (100 + normalize_latitude_degree(latitude))]; 

    if(latitude > 100)
      System.out.println("forward for: reflat="+reference_latitude
			 + " reflon="+reference_longitude +" lat="+latitude
			 + " lon = "+longitude);
    
        // calculate horizontal position (meters away from reference point):
    posx = earth_rad
           * Math.cos (Math.PI * latitude / 180.0f)
           * (longitude - reference_longitude) * Math.PI / 180.0f;

        // calculate vertical position (meters away from reference point):
    posy = (earth_rad * Math.PI / 180.0f) * (latitude - reference_latitude);

        // helps for large maps
    dif =  earth_rad
           * (1 - (Math.cos ((Math.PI * (longitude - reference_longitude)) / 180.0f)));
  
    posy = posy + dif / DIFF_LATITUDE_FACT;

    return (new Point2D.Double(posx,posy));
  }

//----------------------------------------------------------------------
/**
 * This method calculates the longitude and latitude of a point that
 * is located a given amount of meters in horizontal and vertical
 * away. The algorithm is taken from gpsdrive of Fritz Ganter
 * (http://gpsdrive.kraftvoll.at)
 *
 * @param reference_latitude the latitude of the reference location
 * @param reference_longitude the longitude of the reference location
 * @param delta_x the horizontal distance in meters
 * @param delta_y the vertical distance in meters
 * @return a point containing the resulting latitude and longitude.
 */
  public static LatLonPoint inverse(double reference_latitude, 
                                    double reference_longitude,
                                    double delta_x, double delta_y)
  {
    double latitude;
    double longitude;

    double px = delta_x;
    double py = delta_y;

    if(Math.abs(reference_latitude) > 90)
      System.out.println("inverse for: reflat="+reference_latitude
			 + " reflon="+reference_longitude +" delta_x="+delta_x
			 + " delta_y= "+delta_y);
    
    latitude = normalize_latitude_degree(reference_latitude - py / (earth_radius[(int) (100 + normalize_latitude_degree(reference_latitude))] * Math.PI / 180.0f));

    latitude = normalize_latitude_degree(reference_latitude - py / (earth_radius[(int) (100 + latitude)] * Math.PI / 180.0f));
    longitude =
      reference_longitude -
      px / ((earth_radius[(int) (100 + latitude)] * Math.PI / 180.0f) * Math.cos (Math.PI * latitude / 180.0f));
    
    double dif = latitude * (1 - (Math.cos ((Math.PI * Math.abs (longitude - reference_longitude)) / 180.0f)));
    latitude = latitude - dif / DIFF_LATITUDE_FACT;
    longitude =
      reference_longitude -
      px / ((earth_radius[(int) (100 + latitude)] * Math.PI / 180.0f) * Math.cos (Math.PI * latitude / 180.0f));

    return(new LatLonPoint(latitude,longitude));
  }
  
//----------------------------------------------------------------------
/**
 * This method calculates the longitude and latitude of a point that
 * is located at a given numer of pixels in horizontal and vertical
 * away. The algorithm is taken from gpsdrive of Fritz Ganter
 * (http://gpsdrive.kraftvoll.at)
 *
 * @param reference_latitude the latitude of the reference location
 * @param reference_longitude the longitude of the reference location
 * @param delta_x the horizontal distance in pixels
 * @param delta_y the vertical distance in pixels
 * @param scale the scale of the image used.
 * @return a point containing the resulting latitude and longitude.
 */
  public static LatLonPoint inverse(double reference_latitude, 
                                    double reference_longitude,
                                    double delta_x, double delta_y,
                                    double scale)
  {
    double px = delta_x / PIXELFACT * scale;
    double py = delta_y / PIXELFACT * scale;
    return(inverse(reference_latitude,reference_longitude,px,py));
  }

//----------------------------------------------------------------------
/**
 * Draw the background for the projection.
 * @param g Graphics
 */
  public void drawBackground(Graphics g)
  {
    g.setColor(backgroundColor);
    g.fillRect(0, 0, getWidth(), getHeight());
  }
  
//----------------------------------------------------------------------
/**
 * Calculate the earth radius for the given latitude. 
 *
 * @param latitude (in degrees).
 * @return the radius (in meters).
 */
  public static double calcEarthRadius(double latitude)
  {

        // earth radius as found in http://topex.ucsd.edu/geodynamics/14gravity1_2.pdf
//      float radius_equator = 6378137;  // equatorial radius 
//      float radius_polar = 6356752.3;  // polar radius 
//          // flattening on WGS84:
//      float flattening = (radius_equator - radius_polar) / radius_equator;

    double radius = RADIUS_EQUATOR * (1 - EARTH_FLATTENING * Math.pow(Math.sin(Math.toRadians(latitude)),2));
    return(radius);
  }

  public static void main(String[] args)
  {
    LatLonPoint center = new LatLonPoint(47f,15f);
    Projection proj = new FlatProjection(center,500000f,640,480);
    LatLonPoint target_latlon = new LatLonPoint(46f,14f);
    Point target_xy = new Point();
    System.out.println(target_xy);
    proj.forward(target_latlon,target_xy);
    System.out.println(target_xy);
    proj.inverse(target_xy,target_latlon);
    System.out.println(target_latlon);
  }
}








