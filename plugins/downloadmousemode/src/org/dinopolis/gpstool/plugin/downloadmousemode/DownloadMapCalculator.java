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


package org.dinopolis.gpstool.plugin.downloadmousemode;

import java.util.Vector;


//----------------------------------------------------------------------
/**
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class DownloadMapCalculator  
{

      /** in scale 1.0, mapblast images have 2817 pixels per meter */
  public static float MAPBLAST_METERS_PER_PIXEL = 1.0f/2817.947378f;
  public static final float EARTH_EQUATORIAL_RADIUS_M = 6378137f;
  public static final float EARTH_POLAR_RADIUS_M = 6356752.3f;
  public static final float VERTICAL_METER_PER_DEGREE = (float)(EARTH_POLAR_RADIUS_M * 2 * Math.PI / 360.0);

      /** the scale of the image (mapblast scales are used here) */
  protected double scale_;
      /** how many meters is one pixel, default is the value of mapblast */
  protected double meters_per_pixel_ = MAPBLAST_METERS_PER_PIXEL;
      /** latitude of center (not used in area mode) */
  protected double center_latitude_;
      /** longitude of center (not used in area mode) */
  protected double center_longitude_;
      /** if true, more than one image is used */
  protected boolean area_mode_;

      /** area coordinates (latitude or longitude) (used in area mode) */
  protected double north_,south_,west_,east_;

  protected int image_width_;
  protected int image_height_;
  
//----------------------------------------------------------------------
/**
 * Default Constructor
 */
  public DownloadMapCalculator()
  {
  }

//----------------------------------------------------------------------
/**
 * 
 * 
 */
  public void setDownloadCenter(float latitude, float longitude)
  {
    area_mode_ = false;
    center_longitude_ = longitude;
    center_latitude_ = latitude;
  }

//----------------------------------------------------------------------
/**
 * 
 * 
 */
  public void setDownloadArea(float north, float west, float south, float east)
  {
    area_mode_ = true;
    north_ = north;
    south_ = south;
    east_ = east;
    west_ = west;
    center_latitude_ = (north_ + south_) / 2.0f;
    center_longitude_ = (west_ + east_) / 2.0f;
  }

//----------------------------------------------------------------------
/**
 * Set the image dimension wanted.
 *
 * @param width the width of the image (in pixels).
 * @param height the height of the image (in pixels).
 * 
 */
  public void setImageDimension(int width, int height)
  {
    image_width_ = width;
    image_height_ = height;
  }

//----------------------------------------------------------------------
/**
 * Set the scale of the image wanted (mapblast scales used).
 *
 * @param scale the scale.
 */
  public void setImageScale(float scale)
  {
    scale_ = scale;
  }
 
//----------------------------------------------------------------------
/**
 * Sets how many meters is equal to one pixel for scale 1.0.
 *
 * @param meters_per_pixel
 * 
 */
  public void setMetersPerPixel(float meters_per_pixel)
  {
    meters_per_pixel_ = meters_per_pixel;
  }


//----------------------------------------------------------------------
/**
 * After all parameters are set, this method calculates and returns
 * information about the maps to download.
 *
 * @return info about the maps to download.
 * 
 */
  public MapRectangle[] calculateMapRectangles()
  {
    if(!area_mode_)
    {
      return(new MapRectangle[] {new MapRectangle((float)center_latitude_,
                                                  (float)center_longitude_,
                                                  image_width_,
                                                  image_height_,
                                                  (float)scale_)});
    }
        // calculate circumference of small circle at latitude:
    double horiz_meter_per_degree = Math.cos(Math.toRadians(center_latitude_))
                                    *EARTH_EQUATORIAL_RADIUS_M*2*Math.PI / 360.0;
    
    double image_width_degree = image_width_ * scale_ * meters_per_pixel_ / horiz_meter_per_degree;
    double image_height_degree = image_height_ * scale_ * meters_per_pixel_ / VERTICAL_METER_PER_DEGREE;

    double lat_offset = image_height_degree / 2; 
    double long_offset = image_width_degree / 2;

    double start_lat;
    double start_long;
    double end_lat;
    double end_long;

        // bottom left (latitude origin is in bottom) of rectangle:
    start_lat = south_;
    start_long = west_;
    
        // top right of rectangle:
    end_lat = north_;
    end_long = east_;

    System.out.println("Calculation: from lat "+ start_lat +" to "+ end_lat
                       + " from long "+start_long+" to "+ end_long
      +" step lat: "+image_height_degree +" long: "+image_width_degree);

        // center of image are not at corners of rectangle:
    start_lat += image_height_degree/2;
    start_long += image_width_degree/2;

        // end is reached if the complete image would be outside the
        // rectangle.  therefore we stop if the coordinates are one
        // image size beyond the rectangle:
    end_lat += image_height_degree/2;
    end_long += image_width_degree/2;

    double current_lat = start_lat;
    double current_long = start_long;

    Vector maps = new Vector();
    while(current_lat < end_lat)
    {
      while(current_long < end_long)
      {
        maps.add(new MapRectangle((float)current_lat, (float)current_long,
                                  image_width_, image_height_, (float)scale_));
        current_long += image_width_degree;
      }
      current_long = start_long;
      current_lat += image_height_degree;
    }
    MapRectangle[] rectangles = new MapRectangle[maps.size()];
    rectangles = (MapRectangle[])maps.toArray(rectangles);
    return(rectangles);
  }

  public static void main(String[] args)
  {
    DownloadMapCalculator calc = new DownloadMapCalculator();
    calc.setDownloadArea(47f,15f,46f,16f);
//    calc.setDownloadCenter(47f,15f);
    calc.setImageScale(50000f);
    calc.setImageDimension(1280,1024);
    MapRectangle[] rectangles = calc.calculateMapRectangles();
    for(int index = 0; index < rectangles.length; index++)
      System.out.println(rectangles[index]);
    System.out.println("END");
  }
}


