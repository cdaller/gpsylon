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


package org.dinopolis.gpstool.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JComponent;
import org.dinopolis.gpstool.GPSMap;
import org.dinopolis.gpstool.gpsinput.GPSPositionError;
import org.dinopolis.gpstool.gpsinput.SatelliteInfo;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.util.Debug;

//----------------------------------------------------------------------
/**
 * A component that shows the activity of satellites (from a gps
 * device). The satellite levels are situated in columns and rows.  If
 * no satellite infos are set for a certain period of time, the loss
 * of the gps signal is signaled. This is not the same as if satellite
 * infos are set that indicate no valid signal could be found (the
 * latter is the case if the gps device cannot see any satellites, the
 * former is the case if the gps device is switched of and therefore
 * no information at all is sent.)
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class SatelliteActivity extends JComponent
{

  Color active_color_;
  Color inactive_color_;
  Color active_num_color_ = new Color(255,255,0,220);  // transparent yellow

  Font active_num_font_;

  FontMetrics font_metrics_;
  String text_;
  int text_width_;
  int text_height_;
  
  int rows_;
  int columns_;
  int column_width_ = 5;
  int row_height_ = 15;
  int bar_width_ = column_width_ - 2;
  int bar_height_ = row_height_ - 2;
  SatelliteInfo[] satellite_infos_;
  GPSPositionError position_error_;
  Object satellite_infos_lock_ = new Object();
  boolean lost_signal_ = true;
  Timer watchdog_;
  int watchdog_delay_ = 10000; // start in ten seconds
  int watchdog_period_ = 5000; // at least all 5 seconds, we need a valid signal!

  PluginSupport plugin_support_;
  

//----------------------------------------------------------------------
/**
 * Constructor
 */
  public SatelliteActivity(int columns, int rows)
  {
    super();
    active_color_ = Color.green; //new Color(0,0,0);
    inactive_color_ = Color.black; //new Color(200,200,200);
    inactive_color_ = new Color(160,160,160);
    columns_ = columns;
    rows_ = rows;
    setPreferredSize(new Dimension(columns * column_width_,rows * row_height_));
    setMinimumSize(new Dimension(columns * column_width_,rows * row_height_));
    setMaximumSize(new Dimension(columns * column_width_,rows * row_height_));
        // set watchdog that checks regular satellite infos!
    watchdog_ = new Timer(true);
    watchdog_.schedule(new TimerTask()
      {
        public void run()
        {
          if(!lost_signal_)
          {
            lost_signal_ = true;
//            System.out.println("watchdog: lost signal set to true");
            try
            {
              Thread.sleep(watchdog_period_);
            }
            catch(InterruptedException ie)
            {
            }
            if(lost_signal_) // still lost!
            {
//              System.out.println("watchdog: repaint()");
              repaint();
            }
          }
        }
      }, watchdog_delay_, watchdog_period_);
    
  }

  public void initialize(PluginSupport plugin_support)
  {
    plugin_support_ = plugin_support;
  }
  
  
// //----------------------------------------------------------------------
// /**
//  * Get the number of columns.
//  *
//  * @return the number of columns.
//  */
//   public int getColumns()
//   {
//     return (columns_);
//   }
  
// //----------------------------------------------------------------------
// /**
//  * Set the number of columns.
//  *
//  * @param columns the number of columns.
//  */
//   public void setColumns(int columns)
//   {
//     columns_ = columns;
//     setPreferredSize(new Dimension(columns_,rows_));
//   }
  
  
// //----------------------------------------------------------------------
// /**
//  * Get the rows.
//  *
//  * @return the rows.
//  */
//   public int getRows()
//   {
//     return (rows_);
//   }
  
// //----------------------------------------------------------------------
// /**
//  * Set the rows.
//  *
//  * @param rows the rows.
//  */
//   public void setRows(int rows)
//   {
//     rows_ = rows;
//     setPreferredSize(new Dimension(columns_,rows_));
//   }

  
//----------------------------------------------------------------------
/**
 * Get the color for active pixels.
 *
 * @return the color for active pixels.
 */
  public Color getActiveColor()
  {
    return (active_color_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the color for active pixels.
 *
 * @param color the color for active pixels.
 */
  public void setActiveColor(Color color)
  {
    active_color_ = color;
    repaint();
  }
  
//----------------------------------------------------------------------
/**
 * Get the color for inactive pixels.
 *
 * @return the color for inactive pixels.
 */
  public Color getInactiveColor()
  {
    return (inactive_color_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the color for inactive pixels.
 *
 * @param color the color for inactive pixels.
 */
  public void setInactiveColor(Color color)
  {
    inactive_color_ = color;
    repaint();
  }

// //----------------------------------------------------------------------
// /**
//  * Set the the given row/column active (true) or inactive (false);
//  *
//  * @param column the column of the pixel
//  * @param row the row of the pixel
//  * @param value between 0 and 100;
//  */
//   public void setActivity(int column, int row, byte value)
//   {
//     active_array_[column][row] = value;
//   }


// //----------------------------------------------------------------------
// /**
//  * Get the state of the given pixel.
//  *
//  * @return true, if active.
//  */
//   public byte getActivity(int column, int row)
//   {
//     return (active_array_[column][row]);
//   }
  

//----------------------------------------------------------------------
/**
 * Sets the infos about the satellites.
 *
 * @param infos an array of satelliteinfos.
 */
  public void setSatelliteInfos(SatelliteInfo[] infos)
  {
    // copy satellite info:
    synchronized(satellite_infos_lock_)
    {
      satellite_infos_ = new SatelliteInfo[infos.length];
      System.arraycopy(infos,0,satellite_infos_,0,satellite_infos_.length);
      if(Debug.DEBUG && Debug.isEnabled("satellite_infos"))
        Debug.println("satellite_infos","setSatelliteInfos: "+Debug.objectToString(satellite_infos_));
    }
    lost_signal_ = false;
    repaint();
  }

//----------------------------------------------------------------------
/**
 * Sets the estimated position error.
 *
 * @param pos_error the estimated position error.
 */
  public void setPositionError(GPSPositionError pos_error)
  {
    position_error_ = pos_error;
    lost_signal_ = false;
    repaint();
  }

//----------------------------------------------------------------------
/**
 * Paint the component
 *
 */
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g); // draw background

    int active_height;
    int sat_index;
//     int column_width = g.getWidth()/columns_;
//     int row_height = g.getHeight()/rows_;

    int num_sat_active = 0;
    
    if(satellite_infos_ != null)
    {
          // copy satellite info, so changes during painting do not disturb:
      SatelliteInfo[] sat_infos;
      synchronized(satellite_infos_lock_)
      {
        sat_infos = new SatelliteInfo[satellite_infos_.length];
        System.arraycopy(satellite_infos_,0,sat_infos,0,satellite_infos_.length);
        if(Debug.DEBUG && Debug.isEnabled("satellite_infos"))
          Debug.println("satellite_infos","paintInfos: "+Debug.objectToString(sat_infos));
      }

          // paint them
      for(int row_count = 0; row_count < rows_; row_count++)
      {
        for(int column_count = 0; column_count < columns_; column_count++)
        {
          g.setColor(inactive_color_);
          g.fillRect(column_count * column_width_,
                     row_count * row_height_,
                     bar_width_,bar_height_);
          
          g.setColor(active_color_);
          sat_index = row_count*(columns_) + column_count;
          if(sat_index < sat_infos.length)
          {
//             System.out.println("print sat index: "+sat_index);
//             System.out.println("paintInfo: "+sat_infos[sat_index]);
            active_height = (int)(bar_height_ * sat_infos[sat_index].getSNR()/100.0);
            
            if (active_height > 0)
              num_sat_active++;
            
            g.fillRect(column_count * column_width_,
                       row_count * row_height_ + (bar_height_ - active_height),
                       bar_width_,active_height);
          }
        }
      }
    }
//        draw the estimated position error over the bars:
    if(position_error_ != null)
    {
      if(active_num_font_ == null)
      {
        font_metrics_ = g.getFontMetrics();
        active_num_font_ = font_metrics_.getFont().deriveFont((float)(getHeight()-4));
      }
      g.setFont(active_num_font_);
      font_metrics_ = g.getFontMetrics();
      
          // give error in correct unit (feet or meters):

      int pos_error = (int)Math.round(plugin_support_.getUnitHelper().
                                      getAltitude((float)position_error_.getSphericalError()));
      text_ = String.valueOf(pos_error+plugin_support_.getUnitHelper().getAltitudeUnit());
      text_width_ = font_metrics_.stringWidth(text_);
      text_height_ = font_metrics_.getAscent();

          // check if text is wider than width of component:
      while(text_width_ > getWidth())
      {
//        System.out.println("text does not match, decrease font size: "+text_height_);
        active_num_font_ = font_metrics_.getFont().deriveFont((float)(text_height_-2));
        g.setFont(active_num_font_);
        font_metrics_ = g.getFontMetrics();
        text_width_ = font_metrics_.stringWidth(text_);
        text_height_ = font_metrics_.getAscent();
      }
        
//      System.out.println("textheight="+text_height_+" height="+getHeight());
      g.setColor(active_num_color_);
      g.drawString(text_,(getWidth()-text_width_)/2,(getHeight() + text_height_)/2 );
//      System.out.println("drawstring"+text_+","+((getWidth()-text_width_)/2)
//                         +","+((getHeight() + text_height_)/2));
    }
    
    if(lost_signal_)
    {
      g.setColor(new Color(255,0,0,200)); // transparent red
      g.fillRect(0,0,getWidth(),getHeight());
    }
//     g.setXORMode(Color.yellow);
//     String num = String.valueOf(num_sat_active);
//     g.drawString(num,15,10);
  }

  
  
}


