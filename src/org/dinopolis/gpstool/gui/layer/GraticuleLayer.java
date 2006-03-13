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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.dinopolis.gpstool.Gpsylon;
import org.dinopolis.gpstool.GpsylonKeyConstants;
import org.dinopolis.gpstool.util.GeoExtent;
import org.dinopolis.util.Debug;
import org.dinopolis.util.Resources;
import org.dinopolis.util.gui.ActionStore;
import org.dinopolis.util.gui.MenuFactory;
import org.dinopolis.util.gui.SwingWorker;

import com.bbn.openmap.Layer;
import com.bbn.openmap.event.LayerStatusEvent;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMPoly;
import com.bbn.openmap.omGraphics.OMText;
import com.bbn.openmap.proj.Projection;


//----------------------------------------------------------------------
/**
 * A layer that is able to display graticule lines.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GraticuleLayer extends Layer
  implements GpsylonKeyConstants, PropertyChangeListener
{
  boolean layer_active_ = false;

  Resources resources_;
  
  ActionStore action_store_;

  OMGraphic graphic_;
  Object graphic_lock_ = new Object();

  SwingWorker swing_worker_;

  Color ten_degree_color_ = Color.black;
  Color five_degree_color_ = Color.blue;
  Color one_degree_color_ = Color.green;
  Color text_color_ = Color.black;

  boolean draw_text_;

  Font font_ = new Font("Helvetica", java.awt.Font.PLAIN, 10);
//   boolean show_ruler_ = true;

  int lines_threshold_ = 2;
  
//  Properties openmap_properties_;
  
//----------------------------------------------------------------------
/**
 * 
 */
  public GraticuleLayer()
  {
    super();
  }

//----------------------------------------------------------------------
/**
 * Initializes this layer with the given resources.
 */
  public void initialize(Resources resources)
  {
    resources_ = resources;

    ten_degree_color_ = resources_.getColor(KEY_GRATICULE_COLOR_TEN_LINES);
    five_degree_color_ = resources_.getColor(KEY_GRATICULE_COLOR_FIVE_LINES);
    one_degree_color_ = resources_.getColor(KEY_GRATICULE_COLOR_ONE_LINES);

    lines_threshold_ = resources_.getInt(KEY_GRATICULE_LINES_THRESHOLD);

    draw_text_ = resources_.getBoolean(KEY_GRATICULE_DRAW_TEXT);

    layer_active_ = resources_.getBoolean(KEY_GRATICULE_LAYER_ACTIVE);
    
    action_store_ = ActionStore.getStore(Gpsylon.ACTION_STORE_ID);
      /** the Actions */
    Action[] actions_ = { new GraticuleLayerActivateAction()};
    action_store_.addActions(actions_);
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
    if(event.getPropertyName().equals(KEY_GRATICULE_DRAW_TEXT))
    {
      draw_text_ = resources_.getBoolean(KEY_GRATICULE_DRAW_TEXT);
      recalculateGraticule();
    }
  }
  
//----------------------------------------------------------------------
/**
 * Recalculate the graticule. This is done in a separate thread using
 * a SwingWorker.
 */
  protected void recalculateGraticule()
  {
    if(!layer_active_)
      return;
        // stop old thread
    if(swing_worker_ != null)
      swing_worker_.interrupt();

    swing_worker_ = new SwingWorker()
      {
        OMGraphic worker_list_;
        
        public Object construct()
        {
              // set the name of the swingworker for debuggin reasons
//          name_ = "worker "+System.currentTimeMillis();
//          System.out.println("worker "+name_ +" construct()");
        
          fireStatusUpdate(LayerStatusEvent.START_WORKING);
//           LatLonPoint point = getProjection().getUpperLeft();
// //          System.out.println("upperleft: " +point);
//           float end_lat = point.getLatitude();
//           float start_lon = point.getLongitude();

//           point = getProjection().getLowerRight();
// //          System.out.println("lowerright: " +point);
//           float start_lat = point.getLatitude();
//           float end_lon = point.getLongitude();
          
          GeoExtent extent = new GeoExtent(getProjection());
          float step = findGraticuleStep(extent.getNorth(),extent.getSouth(),
                                         extent.getWest(),extent.getEast());

          if(Debug.DEBUG)
            Debug.println("GPSMap_GraticuleLayer","calculate graticule lat for extent: "
                          +extent+" step: "+step);

          worker_list_ = createGraticuleLines(extent.getNorth(),extent.getSouth(),
                                              extent.getWest(),extent.getEast(),
                                              step);
          if(Thread.interrupted())
          {
//            System.out.println("worker "+name_ +" interrupted()");
            worker_list_ = null;
            return(null);
          }

          worker_list_.generate(getProjection());

          if(Thread.interrupted())
          {
//            System.out.println("worker "+name_ +" interrupted()");
            worker_list_ = null;
            return(null);
          }

          return(null);
        }

        public void finished()
        {
          fireStatusUpdate(LayerStatusEvent.FINISH_WORKING);
          if(worker_list_ != null)
          {
//            System.out.println("worker "+name_ +" finished()");
            setGraphic(worker_list_);
            repaint();
          }
        }
      };
    swing_worker_.start();
  }



//----------------------------------------------------------------------
/**
 * Determines the step the graticule lines should be drawn. If the
 * scale is larger, less lines are drawn, if the scale is smaller,
 * more (5 and 1 lines are drawn). If there are less than
 * <code>lines_threshold_</code> lines visible, the next finer lines
 * are used.
 *
 * @param north the norhtern coordinate of the screen
 * @param south the southern coordinate of the screen
 * @param west the western coordinate of the screen
 * @param east the eastern coordinate of the screen
 * @return the step (10, 5, or 1)
 */
  protected float findGraticuleStep(float north, float south,
                                    float west, float east)
  {
        // Find the north - south difference
    float nsdiff = north - south;
        // And the east - west difference
    float ewdiff;
        // Check for straddling the dateline -west is positive while
        // east is negative, or, in a big picture view, the west is
        // positive, east is positive, and western hemisphere is
        // between them.
    if ((west > 0 && east < 0) || (west > east) || (Math.abs(west - east) < 1))
    {
	    ewdiff = (180.0f - west) + (east + 180.0f);
    }
    else
    {
	    ewdiff = east - west;
    }

    float ret = 10.0f;
    
        // And use the lesser of the two.
    float diff = (float)Math.min(nsdiff, ewdiff);
        // number of 10 degree lines
    if ((diff/10) <= (float)lines_threshold_)
      ret = 5.0f;
        // number of five degree lines
    if ((diff/5) <= (float)lines_threshold_)
      ret = 1.0f;

    return(ret);
  }
  
//----------------------------------------------------------------------
/**
 * Creates the graticule lines for the given parameters.
 *
 * @param north the longitude to end
 * @param south the longitude to start
 * @param west the latitude to start
 * @param east the latitude to end
 * @param step the step between the graticule lines
 * @return a Graphic object describing the graticule lines
 */
  protected OMGraphic createGraticuleLines(float north, float south,
                                           float west, float east,
                                           float step)
  {
    OMGraphicList list = new OMGraphicList();

    float text_south = south;
    float text_west = west;

    Projection projection = getProjection();
    Point tmp_point;
    
        // make good (matching to step) integer values from the given
        // coordinates:
    north = (int)Math.ceil(north/step)*step;
    south = (int)Math.floor(south/step)*step;
    west = (int)Math.floor(west/step)*step;
    east = (int)Math.ceil(east/step)*step;


//     System.out.println("calc graticule for west: "+west+" east: "+east
//                        +" north: "+north+" south: "+south);

        // meridians:
    int count;
    for(float longitude = west; longitude <= east; longitude += step)
    {
      count = 0;
      float[] coordinates = new float[(int)((((north - south)/step)+1)*2)];
//      System.out.println("new meridian line:");
      for(float latitude = south; latitude <= north; latitude += step)
      {
        coordinates[count++] = latitude;
        coordinates[count++] = longitude;
      }
//       if(Debug.DEBUG)
//         Debug.println("GPSMap_GraticuleLayer","adding meridian line for long: "+longitude);

      OMPoly line = new OMPoly(coordinates,OMGraphic.DECIMAL_DEGREES,OMGraphic.LINETYPE_GREATCIRCLE);
      
      Color color;
      
      if((longitude % 10) == 0)
        color = ten_degree_color_;
      else
      if((longitude % 5) == 0)
        color = five_degree_color_;
      else
        color = one_degree_color_;

      line.setLinePaint(color);
      list.add(line);

      if(draw_text_)
      {
        tmp_point = projection.forward(text_south,longitude);
        OMText text = new OMText((int)tmp_point.getX()+3,projection.getHeight()-10,
                                 String.valueOf((int)longitude)+"°",
                                 font_,OMText.JUSTIFY_LEFT);
        text.setLinePaint(color);
        list.add(text);
      }

    }

        // parallel to equator:
    for(float latitude = south; latitude <= north; latitude += step)
    {
//      System.out.println("new meridian line:");
      count = 0;
      float[] coordinates = new float[(int)((((east - west)/step)+1)*2)];
      for(float longitude = west; longitude <= east; longitude += step)
      {
        coordinates[count++] = latitude;
        coordinates[count++] = longitude;
      }
//       if(Debug.DEBUG)
//         Debug.println("GPSMap_GraticuleLayer","adding equatorial line for lat: "+latitude);

      OMPoly line = new OMPoly(coordinates,OMGraphic.DECIMAL_DEGREES,OMGraphic.LINETYPE_GREATCIRCLE);

      Color color;
      
      if((latitude % 10) == 0)
        color = ten_degree_color_;
      else
      if((latitude % 5) == 0)
        color = five_degree_color_;
      else
        color = one_degree_color_;

      line.setLinePaint(color);
      list.add(line);
      
      if(draw_text_)
      {
        tmp_point = projection.forward(latitude,text_west);
        OMText text = new OMText(5,(int)tmp_point.getY(),String.valueOf((int)latitude)+"°",
                                 font_,OMText.JUSTIFY_LEFT);
        text.setLinePaint(color);
        list.add(text);
      }
    }
    return(list);
  }
  

//----------------------------------------------------------------------
/**
 * Set the graphic for this layer. The get/setGraphic methods are
 * thread save for accessing the graphics object!
 *
 * @param graphic the graphic to be rendered.
 */
  protected void setGraphic(OMGraphic graphic)
  {
    synchronized(graphic_lock_)
    {
      graphic_ = graphic;
    }
  }
  
//----------------------------------------------------------------------
/**
 * Get the graphic to be rendered. The get/setGraphic methods are
 * thread save for accessing the graphics object!
 *
 * @return the graphic to be rendered. 
 */
  protected OMGraphic getGraphic()
  {
    synchronized(graphic_lock_)
    {
      return(graphic_);
    }
  }
  
//----------------------------------------------------------------------
/**
 * If this layer is enabled, calls paint from its superclass.
 */
  public void paintComponent(Graphics g)
  {
    if(!layer_active_)
      return;

    Graphics2D g2 = (Graphics2D)g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    if(graphic_ != null)
      graphic_.render(g2);
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
      recalculateGraticule();
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

  class GraticuleLayerActivateAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public GraticuleLayerActivateAction()
    {
      super(Gpsylon.ACTION_GRATICULE_LAYER_ACTIVATE);
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
      Action action = action_store_.getAction(Gpsylon.ACTION_GRATICULE_LAYER_ACTIVATE);
      if(action != null)
        action.putValue(MenuFactory.SELECTED, new Boolean(layer_active_));
      resources_.setBoolean(KEY_GRATICULE_LAYER_ACTIVE,layer_active_);
      if(layer_active_)
        recalculateGraticule();
      else
        repaint();
    }
  }


}
