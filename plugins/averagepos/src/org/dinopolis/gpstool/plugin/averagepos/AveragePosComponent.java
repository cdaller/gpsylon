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

package org.dinopolis.gpstool.plugin.averagepos;


import com.bbn.openmap.LatLonPoint;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JComponent;

//----------------------------------------------------------------------
/**
 * This plugin allows to manage the available maps. It shows all
 * available maps on a layer it provides, it provides a table that
 * lists all available maps and allows to edit them, and it provides a
 * mouse mode, that lets the user interact with the map manager by
 * clicking on the map component.
 * 
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class AveragePosComponent extends JComponent
{
  double min_latitude_;
  double max_latitude_;
  double min_longitude_;
  double max_longitude_;
  double avg_latitude_;
  double avg_longitude_;
  Vector positions_ = new Vector();

  public static final int[] circles_radius_ = {1,5,10,25,50,100,250};
  
  public static final float EARTH_EQUATORIAL_RADIUS_M = 6378137f;
  public static final float EARTH_POLAR_RADIUS_M = 6356752.3f;
  public static final float VERTICAL_METER_PER_DEGREE = (float)(EARTH_POLAR_RADIUS_M * 2 * Math.PI / 360.0);

  protected Color background_color_ = Color.white;
  protected Color position_color_ = Color.black;
  protected Color circles_color_ = Color.blue;
  
  public AveragePosComponent()
  {
    super();
    Dimension dim = new Dimension(300,300);
    setPreferredSize(dim);
    setMinimumSize(dim);
    setMaximumSize(dim);
    min_latitude_ = 90.0;
    max_latitude_  = -90.0;
    min_longitude_ = 180.0;
    max_longitude_ = -180.0;
    avg_latitude_  = 0.0;
    avg_longitude_ = 0.0;
  }

  public void addPosition(LatLonPoint position)
  {
    positions_.add(position);
    double latitude = position.getLatitude();
    double longitude = position.getLongitude();
    min_longitude_ = Math.min(min_longitude_,longitude);
    max_longitude_ = Math.max(max_longitude_,longitude);
    min_latitude_ = Math.min(min_latitude_,latitude);
    max_latitude_ = Math.max(max_latitude_,latitude);
    double num = positions_.size();
    avg_longitude_ = ((num-1) * avg_longitude_ + longitude)/num;
    avg_latitude_ = ((num-1) * avg_latitude_ + latitude)/num;
    repaint();
  }

  public LatLonPoint getAveragePosition()
  {
    return(new LatLonPoint(avg_latitude_,avg_longitude_));
  }
    
  
  public void addAltitude(float altitude)
  {
  }

  public void clear()
  {
    positions_.clear();
    min_latitude_ = 90.0;
    max_latitude_  = -90.0;
    min_longitude_ = 180.0;
    max_longitude_ = -180.0;
    avg_latitude_  = 0.0;
    avg_longitude_ = 0.0;
    repaint();
  }

  public void paintComponent(Graphics g)
  {
    g.setColor(background_color_);
    g.fillRect(0,0,getWidth(),getHeight());
    
    Vector positions = null;
    synchronized(positions_)
    {
      positions = new Vector(positions_);
    }

    if(positions == null)
      return;

    int width = getWidth();
    int height = getHeight();
    int width_2 = getWidth()/2;
    int height_2 = getHeight()/2;

          // draw cross on average pos
    g.setColor(circles_color_);
    g.drawLine(0,height_2,width,height_2);
    g.drawLine(width_2,0,width_2,height);


        // draw ellipses for distances:
    double horiz_meter_per_degree = Math.cos(Math.toRadians(avg_latitude_))
                                    *EARTH_EQUATORIAL_RADIUS_M*2*Math.PI / 360.0;
    double lat_range_2 = Math.max(max_latitude_-avg_latitude_,avg_latitude_-min_latitude_);
    double long_range_2 = Math.max(max_longitude_-avg_longitude_,avg_longitude_-min_longitude_);
    double horiz_pixel_per_degree = width_2/long_range_2;
    double vert_pixel_per_degree = width_2/lat_range_2;


    int circle_height_2;
    int circle_width_2;
    g.setColor(circles_color_);
    for(int index=0; index < circles_radius_.length; index++)
    {
      circle_height_2 = (int)((double)circles_radius_[index] * vert_pixel_per_degree/VERTICAL_METER_PER_DEGREE);
      circle_width_2 = (int)((double)circles_radius_[index] * horiz_pixel_per_degree/horiz_meter_per_degree);
      if((circle_width_2 > 10) && (circle_height_2) > 10)
      {
        g.drawOval(width_2-circle_width_2,height_2-circle_height_2,circle_width_2*2,circle_height_2*2);
        g.drawString(circles_radius_[index]+"m",width_2-circle_width_2,height_2);
      }
    }
    

    
//     System.out.println("min latitude="+min_latitude_);
//     System.out.println("max latitude="+max_latitude_);
//     System.out.println("min longitude="+min_longitude_);
//     System.out.println("max longitude="+max_longitude_);
//     System.out.println("lat_range/2="+lat_range_2);
//     System.out.println("long_range/2="+long_range_2);
//     System.out.println("horiz_pixel_per_degree="+horiz_pixel_per_degree);
//     System.out.println("vert_pixel_per_degree="+vert_pixel_per_degree);

    g.setColor(position_color_);
    int x,y;
    LatLonPoint pos;
//    System.out.println("Drawing positions");
    Iterator iterator = positions.iterator();
    while(iterator.hasNext())
    {
      pos = (LatLonPoint)iterator.next();

          // calculate screen coordinates (calc from center = average):
      x = (int)(width_2 + (pos.getLongitude() - avg_longitude_) * horiz_pixel_per_degree);
      y = (int)(height_2 - (pos.getLatitude() - avg_latitude_) * vert_pixel_per_degree);

          // draw cross at pos:
      g.drawLine(x-2,y,x+2,y);
      g.drawLine(x,y-2,x,y+2);
//      System.out.println("Drawing pos: "+pos+" at "+x+"/"+y);
    }

//    g.drawString("Avg: "+avg_latitude_+"/"+avg_longitude_,10,10);
  }


}
