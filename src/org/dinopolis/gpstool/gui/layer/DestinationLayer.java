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

package org.dinopolis.gpstool.gui.layer;

import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.Image;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.event.ActionEvent;
import java.awt.geom.Line2D;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.awt.geom.AffineTransform;

import javax.swing.ImageIcon;
import javax.swing.Action;
import javax.swing.AbstractAction;

import java.beans.PropertyChangeListener; 
import java.beans.PropertyChangeEvent; 
import java.beans.PropertyChangeSupport;

import com.bbn.openmap.Layer;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.event.MapMouseAdapter;
import com.bbn.openmap.event.SelectMouseMode;
import com.bbn.openmap.LatLonPoint;

import org.dinopolis.util.Debug;
import org.dinopolis.util.Resources;
import org.dinopolis.util.gui.ActionStore;
import org.dinopolis.util.gui.MenuFactory;

import org.dinopolis.gpstool.GPSMap;
import org.dinopolis.gpstool.MapInfo;
import org.dinopolis.gpstool.MapNavigationHook;
import org.dinopolis.gpstool.GPSMapKeyConstants;
import javax.swing.Icon;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


//----------------------------------------------------------------------
/**
 * A layer that is able to display a destination. It calculate the
 * distance from current position to the destination and paints a
 * marker at the destination location. It handles shift-mousebutton1
 * to set the destination. All data is received and passed using events.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class DestinationLayer extends Layer
  implements PropertyChangeListener, GPSMapKeyConstants,
             MouseListener, MouseMotionListener
{

  LatLonPoint current_position_;

  LatLonPoint destination_position_ = null;
  Point destination_xy_ = new Point();
  LatLonPoint tmp_point_;

  Color paint_color_ = new Color(0,0,255);
  Color destination_color_ = new Color(255,0,255);
  
  Resources resources_;
  PropertyChangeSupport property_change_support_;

  final static BasicStroke line_stroke_ = new BasicStroke(3.0f);

//----------------------------------------------------------------------
/**
 * Construct a default destination layer.
 */
  public DestinationLayer(Resources resources)
  {
    resources_ = resources;
    current_position_ = new LatLonPoint(0,0);
  }

//----------------------------------------------------------------------
/**
 * ReCalculates the distance from the local position to the
 * destination. Fires the event for the new distance.
 */
  
  protected void reCalculateDistance()
  {
    if(destination_position_ == null)
      return;
    float distance = GPSMap.calculateDistance(current_position_,destination_position_);
    if(property_change_support_ != null)
      property_change_support_.firePropertyChange(GPSMap.PROPERTY_KEY_ROUTE_DESTINATION_DISTANCE,
                                                  null,
                                                  new Float(distance));
  }

//----------------------------------------------------------------------
/**
 * Calculates the screen position.
 */
  
  protected void calculateDestinationPosition()
  {
    if(destination_position_ == null)
      return;
    destination_xy_ = getProjection().forward(destination_position_,destination_xy_);
  }

  
//----------------------------------------------------------------------
/**
 * Callback method for property change events (ResourceBundle,
 * GPSDevice, etc.). Performes updates according to the values
 * of the PropertyChangeEvent.
 * 
 * @param event the property change event.
 */

  public void propertyChange(PropertyChangeEvent event)
  {
    if(event.getPropertyName().equals(GPSMap.PROPERTY_KEY_GPS_LOCATION))
    {
      LatLonPoint tmp_point = (LatLonPoint)event.getNewValue();
      if(tmp_point != null)
      {
        if(Debug.DEBUG)
          Debug.println("GPSMap_DestinationLayer","new postion in destinationlayer: "+tmp_point);
        current_position_ = tmp_point;
        reCalculateDistance();
      }
      return;
    }
    
    if(event.getPropertyName().equals(GPSMap.PROPERTY_KEY_ROUTE_DESTINATION))
    {
      LatLonPoint tmp_point = (LatLonPoint)event.getNewValue();
      if(tmp_point != null)
      {
        if(Debug.DEBUG)
          Debug.println("GPSMap_DestinationLayer","new destination in destinationlayer: "+tmp_point);
        destination_position_ = new LatLonPoint(tmp_point);
        reCalculateDistance();
      }
      return;
    }
    
}

//----------------------------------------------------------------------
// MouseListener Adapter
//----------------------------------------------------------------------

  public void mouseClicked(MouseEvent event)
  {
    if(event.getButton() == MouseEvent.BUTTON1)
    {
      if(event.isShiftDown())
      {
        destination_xy_ = event.getPoint();
        LatLonPoint old_destination = null;
        if(destination_position_ != null)
          old_destination = new LatLonPoint(destination_position_);
        else
          destination_position_ = new LatLonPoint();

        destination_position_ = getProjection().inverse(destination_xy_,destination_position_);
        calculateDestinationPosition();

        if(Debug.DEBUG)
          Debug.println("GPSMap_DestinationLayer","clicked at"
                        +destination_xy_+" new: "+destination_position_
                        +" old: " + old_destination);

        if(property_change_support_ != null)
          property_change_support_.firePropertyChange(GPSMap.PROPERTY_KEY_ROUTE_DESTINATION,
                                                      old_destination,
                                                      destination_position_);
            // repaint my drawing area
//         repaint((int)destination_xy_.getX() -10,
//                 (int)destination_xy_.getY() -10,
//                 20,20);
        repaint();
      }
    }
  }

  public void mouseEntered(MouseEvent event)
  {
//    System.out.println("mouseEntered: "+event.getSource());
  }

  public void mouseExited(MouseEvent event)
  {
//    System.out.println("mouseExited: "+event.getSource());
  }

  public void mousePressed(MouseEvent event)
  {
//    System.out.println("mousePressed: "+event.getSource());
  }

  public void mouseReleased(MouseEvent event)
  {
//    System.out.println("mouseReleased: "+event.getSource());
  }


//----------------------------------------------------------------------
// MouseMotionListener Adapter
//----------------------------------------------------------------------

  public void mouseDragged(MouseEvent event)
  {
//    System.out.println("mouseDragged: "+event.getSource());
  }

  public void mouseMoved(MouseEvent event)
  {
//    System.out.println("mouseMoved: "+event.getSource());
  }


//----------------------------------------------------------------------
// Layer overrides
//----------------------------------------------------------------------

//----------------------------------------------------------------------
/**
 * Renders the graphics list.  It is important to make this
 * routine as fast as possible since it is called frequently
 * by Swing, and the User Interface blocks while painting is
 * done.
 */
  public void paintComponent(Graphics g)
  {
    Graphics2D g2 = (Graphics2D) g;
//    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // calculate and draw new destination on screen:
    if(destination_position_ != null)
    {
      g2.setColor(destination_color_);
      g2.draw(new Line2D.Double(destination_xy_.getX() - 10, destination_xy_.getY(),
                                destination_xy_.getX() + 10, destination_xy_.getY()));
      g2.draw(new Line2D.Double(destination_xy_.getX(),      destination_xy_.getY() -10,
                                destination_xy_.getX(),      destination_xy_.getY() + 10));
    }
  }


  
  
//----------------------------------------------------------------------
// ProjectionListener interface implementation
//----------------------------------------------------------------------

//----------------------------------------------------------------------
/**
 * Handler for <code>ProjectionEvent</code>s.  This function is
 * invoked when the <code>MapBean</code> projection changes.  The
 * graphics are reprojected and then the Layer is repainted.
 * <p>
 * @param event the projection event
 */
  public void projectionChanged(ProjectionEvent event)
  {
    Projection proj = event.getProjection();

    if(proj == null)
      return;
    
    if (!proj.equals(getProjection()))
    {
      setProjection(proj.makeClone());
      calculateDestinationPosition();
    }
    if(Debug.DEBUG)
      Debug.println("DestinationLayer_projection","new projection: "+proj);

        // TODO: probably I should start a new thread here to
        // calculate all visible images (SwingWorker)
    repaint();
  }


//----------------------------------------------------------------------
/**
 * Adds a listener for GPS data change events.
 *
 * @param listener the listener to be added.
 * @param key the key of the GPSdata to be observed.
 * @exception IllegalArgumentException if <code>key</code> or
 * <code>listener</code> is <code>null</code>. 
 */
  public void addPropertyChangeListener(String key, PropertyChangeListener listener)
    throws IllegalArgumentException
  {
    if (key == null)
      throw new IllegalArgumentException("The key must not be <null>.");
    if (listener == null)
      throw new IllegalArgumentException("The listener must not be <null>.");

    if (property_change_support_ == null)
      property_change_support_ = new PropertyChangeSupport(this);
    property_change_support_.addPropertyChangeListener(key,listener);
//    System.out.println("DestinationLayer: property change listener added for key "+key);
  }

  
//----------------------------------------------------------------------
/**
 * Adds a listener for GPS data change events.
 *
 * @param listener the listener to be added.
 * @exception IllegalArgumentException if <code>listener</code> is
 * <code>null</code>.  
 */
  public void addPropertyChangeListener(PropertyChangeListener listener)
    throws IllegalArgumentException
  {
    if (listener == null)
      throw new IllegalArgumentException("The listener must not be <null>.");

    if (property_change_support_ == null)
      property_change_support_ = new PropertyChangeSupport(this);
    property_change_support_.addPropertyChangeListener(listener);
  }
  
//----------------------------------------------------------------------
/**
 * Removes a listener for GPS data change events.
 *
 * @param listener the listener to be removed.
 * @param key the key of the GPSdata to be observed.
 * @exception IllegalArgumentException if <code>key<code> or
 * <code>listener</code> is <code>null</code>.  
 */
  public void removePropertyChangeListener(String key, PropertyChangeListener listener)
    throws IllegalArgumentException
  {
    if (key == null)
      throw new IllegalArgumentException("The key must not be <null>.");
    if (listener == null)
      throw new IllegalArgumentException("The listener must not be <null>.");

    if (property_change_support_ != null)
      property_change_support_.removePropertyChangeListener(key,listener);
  }


//----------------------------------------------------------------------
/**
 * Removes a listener for GPS data change events.
 *
 * @param listener the listener to be removed.
 * @exception IllegalArgumentException if <code>listener</code> is
 * <code>null</code>.  
 */
  public void removePropertyChangeListener(PropertyChangeListener listener)
    throws IllegalArgumentException
  {
    if (listener == null)
      throw new IllegalArgumentException("The listener must not be <null>.");

    if (property_change_support_ != null)
      property_change_support_.removePropertyChangeListener(listener);
  }



//----------------------------------------------------------------------
/**
 * Returns whether this Component can become the focus owner.
 *
 * @return true if this Component is focusable; false otherwise.
 */

  public boolean isFocusable()
  {
    return(true);
  }

}
