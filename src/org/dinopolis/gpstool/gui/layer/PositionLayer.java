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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.dinopolis.gpstool.GPSMap;
import org.dinopolis.gpstool.GPSMapKeyConstants;
import org.dinopolis.gpstool.MapNavigationHook;
import org.dinopolis.util.Debug;
import org.dinopolis.util.Resources;
import org.dinopolis.util.gui.ActionStore;
import org.dinopolis.util.gui.MenuFactory;

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.Layer;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.proj.Projection;


//----------------------------------------------------------------------
/**
 * A layer that is able to display positions (from GPS). CTRL-Mouse1
 * sets the position instead of listening to the gps device.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class PositionLayer extends Layer
  implements PropertyChangeListener, GPSMapKeyConstants,
             MouseListener, MouseMotionListener
{

  LatLonPoint current_position_;

  float current_heading_;
  LatLonPoint tmp_point_;

  Point current_position_xy_ = new Point();
  
  Image position_image_;
  int position_x_;
  int position_y_;
  int position_image_width_;
  int position_image_height_;
  int position_image_width_2_;
  int position_image_height_2_;

  AffineTransform old_transformation_;

  boolean position_use_icon_;
  Color position_circle_color_;
  int position_circle_radius_;
  int position_circle_radius_x2_;
  int position_circle_absolute_radius_;
  int position_circle_line_stroke_;

  boolean layer_active_ = true;
  
      /** the hook responsible for map navigation */
  MapNavigationHook map_navigation_hook_;

  
      /** if true, the current position is followed on the screen, if
          false, the screen may show other areas */
  boolean follow_me_mode_;
      /** used to determine, if in follow me mode the screen has to be
       * recentered because the current position is to close to the
       * border */
  float relative_border_distance_;
  
  Resources resources_;
  PropertyChangeSupport property_change_support_;

  ActionStore action_store_;

  BasicStroke position_line_stroke_;
  BasicStroke position_line_outline_stroke_;

//----------------------------------------------------------------------
/**
 * Construct a default route layer.  Initializes omgraphics to
 * a new OMGraphicList, and invokes createGraphics to create
 * the canned list of routes.
 */
  public PositionLayer(Resources resources)
  {
    resources_ = resources;
    current_position_ = new LatLonPoint(0,0);
    position_use_icon_ = resources_.getBoolean(KEY_POSITION_USE_ICON);
    relative_border_distance_ =
      (float)resources_.getDouble(KEY_POSITION_FOLLOW_ME_PITCH_VALUE_PREFIX
                                  +"."+resources_.getString(KEY_POSITION_FOLLOW_ME_PITCH));
//     relative_border_distance_ = Math.min(0.5f,
//                                (float)resources_.getDouble(KEY_POSITION_FOLLOW_ME_RELATIVE_BORDER));
    
        // unset old key/value (used till 0.4.15pre5):
    resources_.unset("position.follow_me.relative_border");
    
    setupPositionSymbol();

        // actions:
    follow_me_mode_ = resources_.getBoolean(KEY_POSITION_FOLLOW_ME_MODE);  // fixes part of bug #626309

    Action[] actions_ = { new FollowMeModeAction(),
                          new PositionLayerActivateAction()};
    action_store_ = ActionStore.getStore(GPSMap.ACTION_STORE_ID);
    action_store_.addActions(actions_);
    
    setDoubleBuffered(true);
  }

//----------------------------------------------------------------------
/**
 * Initializes the icon or the circle used for indiating the current
 * position.
 */
  protected void setupPositionSymbol()
  {
    if(position_use_icon_)
    {
      Icon icon = resources_.getIcon(KEY_POSITION_ICON);
      if(icon instanceof ImageIcon)
        position_image_ = ((ImageIcon)icon).getImage();
      position_image_width_ = icon.getIconWidth();
      position_image_height_ = icon.getIconHeight();
      position_image_width_2_ = position_image_width_/2;
      position_image_height_2_ = position_image_height_/2;
    }
    else
    {
      position_circle_radius_ = resources_.getInt(KEY_POSITION_CIRCLE_RADIUS);
      position_circle_radius_x2_ = 2*position_circle_radius_;
      position_circle_color_ = resources_.getColor(KEY_POSITION_CIRCLE_COLOR);
      float line_width = (float)resources_.getDouble(KEY_POSITION_CIRCLE_LINE_WIDTH);
      position_line_stroke_ = new BasicStroke(line_width - 1.0f);
      position_line_outline_stroke_ = new BasicStroke(line_width);

          // absolute radius is the size of the resulting circle in pixels:
      position_circle_absolute_radius_ = (int)(position_circle_radius_ + line_width/2)+3;
    }
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
    if(!layer_active_)
      return;
    if(event.getPropertyName().equals(GPSMap.PROPERTY_KEY_GPS_LOCATION))
    {
      LatLonPoint tmp_point = (LatLonPoint)event.getNewValue();
      if(tmp_point != null)
      {
        if(Debug.DEBUG)
          Debug.println("GPSMap_PositionLayer","new postion in positionlayer: "+tmp_point);
        setNewCurrentPosition(tmp_point);
      }
      return;
    }
    
    if(event.getPropertyName().equals(GPSMap.PROPERTY_KEY_CURRENT_HEADING))
    {
      Float tmp_heading = (Float)event.getNewValue();
      if(tmp_heading != null)
      {
        current_heading_ =  tmp_heading.floatValue();
        if(Debug.DEBUG)
          Debug.println("GPSMap_PositionLayer","new heading in positionlayer: "+current_heading_);
        doRepaintPosition();
      }
      return;
    }
    if(event.getPropertyName().equals(KEY_POSITION_FOLLOW_ME_PITCH))
    {
      relative_border_distance_ =
        (float)resources_.getDouble(KEY_POSITION_FOLLOW_ME_PITCH_VALUE_PREFIX
                                    +"."+resources_.getString(KEY_POSITION_FOLLOW_ME_PITCH));
      return;
    }

//         // fixes part of bug #626309:
//     if(event.getPropertyName().equals(KEY_POSITION_FOLLOW_ME_RELATIVE_BORDER))
//     {
//       relative_border_distance_ = Math.min(0.5f,
//                                            (float)resources_.getDouble(KEY_POSITION_FOLLOW_ME_RELATIVE_BORDER));
//       return;
//     }
    if(event.getPropertyName().equals(KEY_POSITION_USE_ICON))
    {
      position_use_icon_ = resources_.getBoolean(KEY_POSITION_USE_ICON);
      setupPositionSymbol();
      int width = Math.max(position_image_width_2_,position_circle_radius_);
      int height = Math.max(position_image_height_2_,position_circle_radius_);
      repaint((int)current_position_xy_.getX() - width,
              (int)current_position_xy_.getY() - height,
              2*width, 2*height);
      return;
    }
  }


//----------------------------------------------------------------------
/**
 * Initiates a repaint for the concerned area (just around the
 * position icon/circle).
 */
  protected void doRepaintPosition()
  {
    if(position_use_icon_)
    {
      repaint((int)(current_position_xy_.getX()-position_image_width_2_),
              (int)(current_position_xy_.getY()-position_image_height_2_),
              position_image_width_,
              position_image_height_);
    }
    else
    {
      repaint((int)(current_position_xy_.getX()-position_circle_radius_),
              (int)(current_position_xy_.getY()-position_circle_radius_),
              position_circle_radius_x2_,position_circle_radius_x2_);
    }
  }
  
//----------------------------------------------------------------------
/**
 * Calls repaint with the area of the old (now erased) location and
 * repaint with the new location. It finds the rectangle that includes
 * both the old and the new position (including the icon/circle and calls
 * repaint for this rectangle.
 *
 * @param old_pos
 * @param new_pos
 */
  protected void repaintNewLocation(Point old_pos, Point new_pos)
  {
    int x_min;
    int y_min;
    int x_max;
    int y_max;
    
    x_min = (int)Math.min(old_pos.getX(),new_pos.getX());
    x_max = (int)Math.max(old_pos.getX(),new_pos.getX());
    y_min = (int)Math.min(old_pos.getY(),new_pos.getY());
    y_max = (int)Math.max(old_pos.getY(),new_pos.getY());

    if(position_use_icon_)
    {
      x_min -= position_image_width_2_;
      y_min -= position_image_height_2_;
      x_max += position_image_width_2_;
      y_max += position_image_height_2_;
    }
    else
    { // add/subtract size of circle
      x_min -= position_circle_absolute_radius_;
      y_min -= position_circle_absolute_radius_;
      x_max += position_circle_absolute_radius_;
      y_max += position_circle_absolute_radius_;
    }
    repaint(x_min,y_min, x_max-x_min, y_max-y_min);
  }

  
//----------------------------------------------------------------------
/**
 * Sets a new current position. If in followme mode and the position
 * is close to the border or outside the visible area, the projection
 * is centered to the new position.
 * 
 * @param new_current_position the new current position.
 */
  public void setNewCurrentPosition(LatLonPoint new_current_position)
  {
    if(new_current_position == null)
      return;

    Point old_pos = new Point(current_position_xy_);
    current_position_xy_ = getProjection().forward(new_current_position,current_position_xy_);
    if(follow_me_mode_)
    {
      if((current_position_xy_.getX() < getWidth() * relative_border_distance_) ||
         (current_position_xy_.getX() > getWidth() * (1.0-relative_border_distance_)) ||
         (current_position_xy_.getY() < getHeight() * relative_border_distance_) ||
         (current_position_xy_.getY() > getHeight() * (1.0-relative_border_distance_)))
      {
            // set center to current position:
        current_position_ = new_current_position;
        setMapCenter(current_position_); // does a repaint
        return;
      }
    }
    current_position_ = new_current_position;
    
    repaintNewLocation(old_pos,current_position_xy_);
  }


//----------------------------------------------------------------------
// MouseListener Adapter
//----------------------------------------------------------------------

  public void mouseClicked(MouseEvent event)
  {
    if(!layer_active_)
      return;
    if(event.getButton() == MouseEvent.BUTTON3)
    {
      if(event.isControlDown())
      {
        Point pos = event.getPoint();
        LatLonPoint old_pos = null;
        if(current_position_ != null)
          old_pos = new LatLonPoint(current_position_);
        else
          old_pos = new LatLonPoint();
            
            // calculate lat/long of destination:
        current_position_ = getProjection().inverse(pos,current_position_);
        if(Debug.DEBUG)
          Debug.println("GPSMap_PositionLayer","clicked at"
                        +current_position_xy_+" new: "+current_position_
                        +" old: " + old_pos);

        if(property_change_support_ != null)
          property_change_support_.firePropertyChange(GPSMap.PROPERTY_KEY_GPS_LOCATION,
                                                      old_pos,
                                                      current_position_);
        setNewCurrentPosition(current_position_);
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
    if(!layer_active_)
      return;
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    if(Debug.DEBUG && Debug.isEnabled("PositionLayer_measure"))
    {
      Debug.startTimer("PositionLayer_paint");
    }

    if(Debug.DEBUG)
      Debug.println("GPSMap_PositionLayer_paint","current_pos="+current_position_
                    +" in XY="+current_position_xy_);
//    System.out.println("PositionLayer_paint: clip="+g2.getClipBounds());

    if((position_image_ == null) && (position_use_icon_))
      return;
    
    position_x_ = (int)current_position_xy_.getX();
    position_y_ = (int)current_position_xy_.getY();

    if(!position_use_icon_)
    {
          // draw larger circle in different color behind (simulates outline mode)
      g2.setStroke(position_line_outline_stroke_);
      g2.setColor(Color.black);
      g2.drawOval(position_x_ - position_circle_radius_,
                  position_y_ - position_circle_radius_,
                  position_circle_radius_x2_,
                  position_circle_radius_x2_);

      g2.setStroke(position_line_stroke_);
      g2.setColor(position_circle_color_);
      g2.drawOval(position_x_ - position_circle_radius_,
                  position_y_ - position_circle_radius_,
                  position_circle_radius_x2_,
                  position_circle_radius_x2_);

          // draw line that shows into the current_heading_ direction:
      old_transformation_ = (AffineTransform)g2.getTransform().clone();
          // create rotation transformation with center at current position:
      g2.transform(AffineTransform.getRotateInstance(Math.toRadians(current_heading_),
                                                     position_x_,
                                                     position_y_));
          // draw line straight up (rotation is done via the affine transformation):
      g2.setStroke(position_line_outline_stroke_);
      g2.setColor(Color.black);
      g2.drawLine(position_x_,
                  position_y_ - 2,
                  position_x_,
                  position_y_ - position_circle_radius_ - 2);

      g2.setStroke(position_line_stroke_);
      g2.setColor(position_circle_color_);
      g2.drawLine(position_x_,
                  position_y_ - 2,
                  position_x_,
                  position_y_ - position_circle_radius_ - 2);

          // reset transformation to previous transformation:
      g2.setTransform(old_transformation_);
    }
    else  // use icon:
    {
          // draw icon that is rotated, so the top of the icon shows
          // into the current_heading_ direction:
      old_transformation_ = (AffineTransform)g2.getTransform().clone();
          // create rotation transformation with center at current position:
      g2.transform(AffineTransform.getRotateInstance(Math.toRadians(current_heading_),
                                                     position_x_,
                                                     position_y_));
          // draw image at current position
      g2.drawImage(position_image_,position_x_ - position_image_width_2_,
                   position_y_- position_image_height_2_,this);
          // reset transformation to previous transformation:
      g2.setTransform(old_transformation_);
    }
    if(Debug.DEBUG && Debug.isEnabled("PositionLayer_measure"))
    {
      System.out.println(Debug.stopTimer("PositionLayer_paint"));
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
      if(Debug.DEBUG)
        Debug.println("MapLayer_projection","new projection: "+proj);
      
      setProjection(proj.makeClone());
      Point old_pos = new Point(current_position_xy_);
      current_position_xy_ = getProjection().forward(current_position_,current_position_xy_);
      repaintNewLocation(old_pos,current_position_xy_);
    }
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
//    System.out.println("PositionLayer: property change listener added for key "+key);
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
// The MapNavigation Hookups
//----------------------------------------------------------------------


  public void setMapNavigationHook(MapNavigationHook hook)
  {
    map_navigation_hook_ = hook;
  }

//----------------------------------------------------------------------
/**
 * Sets the new center of the map.
 *
 * @param latitude The latitude of the new center of the map
 * @param longitude The longitude of the new center of the map
 */

  public void setMapCenter(LatLonPoint center)
  {
    setMapCenter(center.getLatitude(),center.getLongitude());
  }
  
//----------------------------------------------------------------------
/**
 * Sets the new center of the map.
 *
 * @param latitude The latitude of the new center of the map
 * @param longitude The longitude of the new center of the map
 */

  public void setMapCenter(double latitude, double longitude)
  {
    if(map_navigation_hook_ != null)
      map_navigation_hook_.setMapCenter(latitude,longitude);
  }


//----------------------------------------------------------------------
/**
 * Rescales the map by a given factor. A factor of 1.0 leaves the map
 * unchanged. A factor greater 1.0 zooms in, a factor less than 1.0
 * zooms out.
 *
 * @param scale_factor the scale factor.
 */

  public void reScale(float scale_factor)
  {
    if(map_navigation_hook_ != null)
      map_navigation_hook_.reScale(scale_factor);
  }
  


// //----------------------------------------------------------------------
// /**
//  * Returns whether this Component can become the focus owner. This
//  * method is deprecated since Java 1.4. But for the sake of backward
//  * compatibility it is used however (and calls the new isFocusable()
//  * method).
//  *
//  * @return true if this Component is focusable; false otherwise.
//  */

//   public boolean isFocusTraversable()
//   {
//     return(isFocusable());
//   }

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

// ----------------------------------------------------------------------
// inner classes
// ----------------------------------------------------------------------

// ----------------------------------------------------------------------
// action classes

//----------------------------------------------------------------------
/**
 * The Action that triggers followme mode.
 */

  class FollowMeModeAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public FollowMeModeAction()
    {
      super(GPSMap.ACTION_FOLLOW_ME_MODE);
      putValue(MenuFactory.SELECTED, new Boolean(follow_me_mode_));
    }

        //----------------------------------------------------------------------
        /**
         * Stores bounds and locations if this option was enabled and
         * exits.
         * 
         * @param event the action event
         */

    public void actionPerformed(ActionEvent event)
    {
      follow_me_mode_ = !follow_me_mode_;
      
      resources_.setBoolean(KEY_POSITION_FOLLOW_ME_MODE,follow_me_mode_); // fixes bug #626309
      
      Action action = action_store_.getAction(GPSMap.ACTION_FOLLOW_ME_MODE);
      if(action != null)
        action.putValue(MenuFactory.SELECTED, new Boolean(follow_me_mode_));
      
      if(follow_me_mode_)
        setNewCurrentPosition(current_position_);

      if(Debug.DEBUG)
        Debug.println("GPSMap_PositionLayer","follow me mode: "+follow_me_mode_);
    }
  }


//----------------------------------------------------------------------
/**
 * The Action that triggers the de-/activation of this layer.
 */

  class PositionLayerActivateAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public PositionLayerActivateAction()
    {
      super(GPSMap.ACTION_POSITION_LAYER_ACTIVATE);
      putValue(MenuFactory.SELECTED, new Boolean(layer_active_));
    }

        //----------------------------------------------------------------------
        /**
         * Stores bounds and locations if this option was enabled and
         * exits.
         * 
         * @param event the action event
         */

    public void actionPerformed(ActionEvent event)
    {
      layer_active_ = !layer_active_;
      Action action = action_store_.getAction(GPSMap.ACTION_POSITION_LAYER_ACTIVATE);
      if(action != null)
        action.putValue(MenuFactory.SELECTED, new Boolean(layer_active_));
      repaint();
    }
  }


}
