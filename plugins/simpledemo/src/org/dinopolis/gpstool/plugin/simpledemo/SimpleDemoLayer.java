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


package org.dinopolis.gpstool.plugin.simpledemo;

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.proj.Projection;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Vector;
import org.dinopolis.gpstool.gui.util.BasicLayer;
import org.dinopolis.gpstool.plugin.PluginSupport;




//----------------------------------------------------------------------
/**
 * This layer displays tracks stored in the trackmanager.
 * 
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class SimpleDemoLayer extends BasicLayer 
{
  Object lock_ = new Object();
  Vector geo_positions_;
  Vector screen_positions_;
  
//----------------------------------------------------------------------
/**
 * Constructor
 *
 */
  public SimpleDemoLayer()
  {
    super();
    setActive(true);
  }

//----------------------------------------------------------------------
/**
 * Initialize with all it needs.
 *
 * @param support a plugin support object
 */
  public void initializePlugin(PluginSupport support)
  {
    geo_positions_ = new Vector();
    
        // create lots of geographical points:
    for(int latitude = -85; latitude <= 85 ; latitude++)
      for(int longitude = -180; longitude < 180; longitude++)
        geo_positions_.add(new LatLonPoint(latitude,longitude));

        // whenever the coordintes change, call the method
        // recalculateCoordinates(), thats all!
  }

//----------------------------------------------------------------------
/**
 * Paints the objects for this layer.
 *
 * @param g the graphics context.
 */
  public void paintComponent(Graphics g)
  {
    if(!isActive())
      return;
    
    if(screen_positions_ == null)
      return;

    g.setColor(Color.blue);

    // synchronize, so no change of this object occurs while painting it:
    // better: copy the vector, so the lock can be released sooner!
    synchronized(lock_) 
    {
      Point screen_pos;
      for(int index = 0; index < screen_positions_.size(); index++)
      {
        screen_pos = (Point)screen_positions_.get(index);
            // draw a small cross for every position:
        g.drawLine((int)screen_pos.getX()-3,(int)screen_pos.getY(),
                   (int)screen_pos.getX()+3,(int)screen_pos.getY());
        g.drawLine((int)screen_pos.getX(),(int)screen_pos.getY()-3,
                   (int)screen_pos.getX(),(int)screen_pos.getY()+3);
      }
    }
  }

//----------------------------------------------------------------------
/**
 * This method is called from a background thread to recalulate the
 * screen coordinates of any geographical objects. This method must
 * store its objects and paint them in the paintComponent() method.
 */
  protected void doCalculation()
  {
    Projection projection = getProjection();
    if(projection == null)
      return;

    Vector screen_positions = new Vector();
    LatLonPoint geo_pos;
    Point screen_pos;
        // calculate the screen coordinates for all geo positions
        // (would be faster if all non-visible points would not be
        // calculated!)
    for(int index = 0; index < geo_positions_.size(); index++)
    {
      geo_pos = (LatLonPoint)geo_positions_.get(index);
      screen_pos = projection.forward(geo_pos);
      screen_positions.add(screen_pos);
      if(Thread.interrupted())
      {
            // this happens, if the projection changes before the
            // calculation is finished and the second SwingWorker
            // interrupts the first one (the old results won't be used
            // anyway)
        return;
      }
    }
    
    // synchronize, so no change of this object occurs while painting it:
    synchronized(lock_) 
    {
      screen_positions_ = screen_positions;
    }
    // repaint() is called automatically!
  }

}


