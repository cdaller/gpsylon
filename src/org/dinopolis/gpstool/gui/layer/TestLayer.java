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


//----------------------------------------------------------------------
/**
 * A layer for testing purposes - usually does nothing.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class TestLayer extends Layer
  implements GPSMapKeyConstants
{

  boolean layer_active_ = false;
  
  Resources resources_;

  ActionStore action_store_;
      /** the Actions */
  private Action[] actions_ = { new TestLayerActivateAction()};

  BasicStroke line_stroke_ = new BasicStroke(3.0f);

  int new_projection_count_;
  
//----------------------------------------------------------------------
/**
 * Construct a test layer.
 */
  public TestLayer(Resources resources)
  {
    resources_ = resources;
    action_store_ = ActionStore.getStore(GPSMap.ACTION_STORE_ID);
    action_store_.addActions(actions_);
    setDoubleBuffered(true);
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
      if(layer_active_)
        System.out.println((new_projection_count_++) +". new Projection: "+proj);
      if(Debug.DEBUG)
        Debug.println("TestLayer_projection","new projection: "+proj);
      
      setProjection(proj.makeClone());
      repaint(); // TODO only repaint area of interest!
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

  class TestLayerActivateAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public TestLayerActivateAction()
    {
      super(GPSMap.ACTION_TEST_LAYER_ACTIVATE);
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
      Action action = action_store_.getAction(GPSMap.ACTION_TEST_LAYER_ACTIVATE);
      if(action != null)
        action.putValue(MenuFactory.SELECTED, new Boolean(layer_active_));
      repaint();
    }
  }


}
