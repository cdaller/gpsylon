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

import java.awt.Point;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.event.ActionEvent;
import java.awt.geom.Line2D;
import java.awt.geom.AffineTransform;

import javax.swing.Action;
import javax.swing.AbstractAction;

import com.bbn.openmap.Layer;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.LatLonPoint;

import org.dinopolis.util.Debug;
import org.dinopolis.util.Resources;
import org.dinopolis.util.gui.ActionStore;
import org.dinopolis.util.gui.MenuFactory;

import org.dinopolis.gpstool.GPSMap;
import org.dinopolis.gpstool.GPSMapKeyConstants;
import org.dinopolis.gpstool.gui.layer.MultiMapLayer;
import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.FontMetrics;


//----------------------------------------------------------------------
/**
 * A layer that is able to display the current scale and a ruler for
 * the scale in a semi transparent way.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class ScaleLayer extends Layer
  implements GPSMapKeyConstants, PropertyChangeListener
{

  boolean layer_active_ = true;
  
  Resources resources_;

  ActionStore action_store_;

  BasicStroke line_stroke_ = new BasicStroke(2.0f);

      /** the rule length in pixels */
  int rule_length_;
      /** the rule value in meters/miles/etc. */
  float rule_value_;
  
  AffineTransform old_transformation_;

      /** valid scales to be used in the rule on the map */
  int[] valid_scales_ = { 10, 50, 100, 250, 500, 1000, 2500, 5000, 10000, 25000,
                          50000, 100000, 250000, 500000, 1000000,
                          2500000, 5000000, 10000000};

  int width_, height_;
  int rectangle_width_;
  int rectangle_height_ = 30;

  final static int RECT_BORDER = 5; // 5 pixels left/right to rule in rectangle

  int transparency_rect_ = 175;
  int transparency_text_ = 255;
  Color rect_color_ = new Color(255,255,255,transparency_rect_);
  Color text_color_ = new Color(0,0,0,transparency_text_);

  float old_scale_;
      /** this is the length that should be found as close as possible */
  int aimed_length_;

      // helper variables for drawing text centered:
  FontMetrics font_metrics_;
  String text_;
  int text_width_;
  
//----------------------------------------------------------------------
/**
 * Construct a scale layer.
 */
  public ScaleLayer(Resources resources)
  {
    resources_ = resources;
    
    aimed_length_ = resources_.getInt(KEY_SCALE_RULE_AIMED_LENGTH);
    layer_active_ = resources_.getBoolean(KEY_SCALE_LAYER_ACTIVE);

    action_store_ = ActionStore.getStore(GPSMap.ACTION_STORE_ID);
    Action[] actions_ = { new ScaleLayerActivateAction()};
    action_store_.addActions(actions_);
    setDoubleBuffered(true);
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
    if(event.getPropertyName().equals(KEY_UNIT_DISTANCE))
    {
      calculateScaleRule();
      return;
    }
  }


  
//----------------------------------------------------------------------
/**
 * Calls repaint for the part the scale bar could be.
 */
  protected void repaintChangedArea()
  {
    repaint(0, height_ - rectangle_height_ - RECT_BORDER,
            width_, rectangle_height_);
  }
  
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

//    g2.setStroke(line_stroke_);
    old_transformation_ = (AffineTransform)g2.getTransform().clone();

    width_ = getProjection().getWidth();
    height_ = getProjection().getHeight();

    rectangle_width_ = (int)rule_length_ + 2 * RECT_BORDER;
    
    g2.translate(width_ - rectangle_width_ - RECT_BORDER,
                 height_ - rectangle_height_ - RECT_BORDER);

        // draw semi transparent rectangle:
    g2.setColor(rect_color_);
    g2.fillRect(0,0,rectangle_width_,rectangle_height_);

        // draw rule:
    g2.setColor(text_color_);

    g2.setStroke(line_stroke_);
    g2.drawLine(RECT_BORDER , rectangle_height_ / 2,
                rectangle_width_ - RECT_BORDER , rectangle_height_ / 2);
    g2.drawLine(RECT_BORDER, rectangle_height_ / 2 - 3,
                RECT_BORDER, rectangle_height_ / 2 + 3);
    g2.drawLine(rectangle_width_ - RECT_BORDER, rectangle_height_ / 2 - 3,
                rectangle_width_ - RECT_BORDER, rectangle_height_ / 2 + 3);
    
        // draw text:
    font_metrics_ = g2.getFontMetrics();
    text_ = "1:"+(int)getProjection().getScale();
    text_width_ = font_metrics_.stringWidth(text_);
    g2.drawString(text_,(rectangle_width_ - text_width_)/2,27);
    
    text_ = (rule_value_/1000.0)+GPSMap.getDistanceUnit();
    text_width_ = font_metrics_.stringWidth(text_);
    g2.drawString(text_,(rectangle_width_ - text_width_)/2,13);
    
    // reset transformation to previous transformation:
    g2.setTransform(old_transformation_);
    
//    g2.setColor(position_circle_color_);
          // draw line straight up (rotation is done via the affine transformation):
//    g2.drawLine();
  }


//----------------------------------------------------------------------
/**
 * Sets the parameters needed to paint the scale rule.
 *
 * @param rule_length the length of the rule in pixels
 * @param rule_value the value of the rule (meters, miles, etc.)
 */
  protected void setRule(int rule_length, float rule_value)
  {
    rule_value_ = rule_value;
    rule_length_ = rule_length;
    repaintChangedArea(); 
  }


//----------------------------------------------------------------------
/**
 * Finds the best matching scale rule. The available values are tested
 * to match the aimed length of the rule best. At the moment an O(n)
 * algorithm is implemented (searches the whole list of available
 * scale values).
 */
  protected void calculateScaleRule()
  {
        // find the best scale-rule:
    float scale_rule_length;
    float best_rule = 0.0f;
    float rule;
    float best_rule_length = 0.0f;
    float min_diff = 100*aimed_length_;
    float diff;
    for (int count=0; count < valid_scales_.length; count++)
    {
      rule = valid_scales_[count];
      scale_rule_length = rule / getProjection().getScale()
                          * MultiMapLayer.PIXELFACT / GPSMap.getDistanceOrSpeedFactor();
//      System.out.println("checking scale: "+rule +"for scale:"+getProjection().getScale());
      diff = Math.abs(scale_rule_length - aimed_length_);
      if(diff < min_diff)
      {
        best_rule = rule;
        best_rule_length = scale_rule_length;
        min_diff = diff;
//         System.out.println("best rule: "+best_rule);
//         System.out.println("best rule length: "+best_rule_length);
      }
    }
    setRule((int)best_rule_length, best_rule);
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
        Debug.println("ScaleLayer_projection","new projection: "+proj);
      
      setProjection(proj.makeClone());
      float new_scale = proj.getScale();
      if(Math.abs(old_scale_ - new_scale) > 1)
      {
        old_scale_ = new_scale;
        calculateScaleRule(); 
      }
    }
    
  }




// ----------------------------------------------------------------------
// inner classes
// ----------------------------------------------------------------------

// ----------------------------------------------------------------------
// action classes


//----------------------------------------------------------------------
/**
 * The Action that triggers the de-/activation of this layer.
 */

  class ScaleLayerActivateAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public ScaleLayerActivateAction()
    {
      super(GPSMap.ACTION_SCALE_LAYER_ACTIVATE);
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
      Action action = action_store_.getAction(GPSMap.ACTION_SCALE_LAYER_ACTIVATE);
      if(action != null)
        action.putValue(MenuFactory.SELECTED, new Boolean(layer_active_));
       resources_.setBoolean(KEY_SCALE_LAYER_ACTIVE,layer_active_);
       calculateScaleRule();
    }
  }


}
