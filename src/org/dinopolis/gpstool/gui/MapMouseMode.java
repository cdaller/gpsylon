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

import com.bbn.openmap.MapBean;

import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
//import java.awt.event.MouseWheelEvent;

import org.dinopolis.util.Resources;
import org.dinopolis.util.gui.ActionStore;

import com.bbn.openmap.Layer;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMPoint;

import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.JComboBox;

import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.InputEvent;
import java.awt.Point;

import java.util.Vector;
import java.util.Iterator;

import org.dinopolis.util.Debug;
import java.awt.event.ActionListener;
import javax.swing.JTextField;
import com.bbn.openmap.omGraphics.OMRect;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;

import org.dinopolis.gpstool.gui.DownLoadFrame;
import org.dinopolis.gpstool.GPSMapKeyConstants;
import org.dinopolis.gpstool.PreviewHook;
import org.dinopolis.gpstool.MapNavigationHook;
import org.dinopolis.gpstool.StatusHook;
import org.dinopolis.gpstool.MapManagerHook;
import org.dinopolis.gpstool.GPSMap;
import org.dinopolis.gpstool.util.MapRectangle;
import org.dinopolis.util.gui.SwingWorker;
import com.bbn.openmap.event.LayerStatusEvent;

/**
 * This mousemode fulfills the following:
 *
 * <ul>
 *
 * <li> a single (left) click sets a marker
 *
 * <li> a single (left) click with strg pressed extends or creates a
 * line from the previously set marker. if no marker was previously
 * set, a marker is set. The distance from the marker before and the
 * total distance of the line is displayed in the status bar. If in
 * line mode, a single click somewhere else changes to back to normal
 * mode.
 *
 * <li> a click and drag opens a rectangle. in rectangle mode a click
 * into the rectangle zooms into the given area. a right click opens a
 * context menu that offers the possibility to downlad maps for the
 * given rectangle.
 *
 * <li> a right click opens a context menu that offers download of
 * maps with the center point or to zoom in/out with the given point
 * as the new center.
 *
 * <li> a single click close to the border of the map results in the
 * setting of a new center of the map.
 *
 * <li> pan mode: click and drag????
 *
 * <li> zoom mode: click and shift click zooms in or out????
 *
 * <li>
 *
 * </ul>
 */

//----------------------------------------------------------------------
/**
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class MapMouseMode implements MouseListener, MouseMotionListener,
            PreviewHook, GPSMapKeyConstants
{

      /** the map bean to work with */
  MapBean map_bean_;
  Resources resources_;
  MapNavigationHook map_navigation_;
  StatusHook status_hook_;
  MapManagerHook map_manager_hook_;
  MapMouseLayer mouse_layer_;
  SwingWorker swing_worker_;

      // for drawing segments of a line (including distance)
  Vector segment_points_ = new Vector();
  LatLonPoint last_point_;
  float distance_;
  float total_distance_;

  // for dragging a rectangle:
  int drag_start_x_;
  int drag_start_y_;
  int drag_current_x_;
  int drag_current_y_;
  boolean drag_mode_ = false;

  // for drawing the rectangle to download a map:
  double download_center_latitude_;
  double download_center_longitude_;
  float download_scale_;
  float download_image_width_ = 1280f;  // default
  float download_image_height_ = 1024f;  //default
  boolean draw_download_rectangle_ = false;

  Vector preview_maps_ = new Vector();
  Vector preview_rectangles_ = new Vector();
  
  static final int LINE_TYPE = OMGraphic.LINETYPE_GREATCIRCLE;

  ActionStore action_store_;
      /** the Actions */
  protected Action download_area_action_ = new DownloadMapAreaAction();
  private Action[] actions_ = { new DownloadMapPositionAction(),
				download_area_action_
                              };
  DownLoadFrame download_frame_;
  
//----------------------------------------------------------------------
/**
 * Constructor
 */

  public MapMouseMode(MapBean map_bean, Resources resources,
                      MapNavigationHook map_navigation, StatusHook status_hook,
		      MapManagerHook map_manager)
  {
    map_bean_ = map_bean;
    resources_ = resources;
    map_navigation_ = map_navigation;
    status_hook_ = status_hook;
    map_manager_hook_ = map_manager;

    // add actions for this layer/mouse mode:
    action_store_ = ActionStore.getStore(GPSMap.ACTION_STORE_ID);
    action_store_.addActions(actions_);

    mouse_layer_ = new MapMouseLayer();
    enable();

//     addKeyListener( new KeyAdapter {
//         public void keyPressed(KeyEvent event)
//         {
//           switch(event.getKeyCode())
//           {
//             case: KeyEvent.VK_SHIFT:
//               shift_pressed = true;
//               break;
//             default:
//               break;
//           }
//         }

//         pulic void keyReleased(KeyEvent event)
//         {
//         }
//       });
    
  }


//----------------------------------------------------------------------
/**
 * enable this mouse mode
 */
  
  public void enable()
  {
//     map_bean_.addMouseListener(this);
//     map_bean_.addMouseMotionListener(this);
//    map_bean_.addMouseWheelListner(this);
    map_bean_.add(mouse_layer_);
  }

//----------------------------------------------------------------------
/**
 * disable this mouse mode
 */
  
  public void disable()
  {
//     map_bean_.removeMouseListener(this);
//     map_bean_.removeMouseMotionListener(this);
//    map_bean_.removeMouseWheelListener(this);
    map_bean_.remove(mouse_layer_);
  }


//----------------------------------------------------------------------
// MouseListener Adapter
//----------------------------------------------------------------------

  public void mouseClicked(MouseEvent event)
  {
//    System.out.println("mouseClicked: "+event.getSource());

    if(event.getButton() == MouseEvent.BUTTON1)
    {
      LatLonPoint point = map_bean_.getProjection().inverse(event.getX(),event.getY());

      if(event.isShiftDown())
      {
      }

      if(event.isControlDown())
      {

        if(Debug.DEBUG)
          Debug.println("GPSMap_mouse","adding new segment point" + point);

        segment_points_.add(point);
        if(last_point_ != null)
        {
          distance_ = GPSMap.calculateDistance(last_point_,point);
          total_distance_ += distance_;
          String unit = GPSMap.getDistanceUnit();
          status_hook_.setStatusInfo(GPSMap.getDistanceOrSpeedString(distance_)
                                     + unit + "/"
                                     + GPSMap.getDistanceOrSpeedString(total_distance_)
                                     + unit);
        }
        last_point_ = point;
        mouse_layer_.repaint();
      }

      if(event.isAltDown())
      {
      }

          // no modifiers pressed:
      if(!event.isAltDown() && !event.isShiftDown() && !event.isControlDown())
      { 
        if(distance_ > 0)  // reset previous segments...
        {
          distance_ = 0.0f;
          total_distance_ = 0.0f;
          segment_points_.clear();
          last_point_ = null;
          status_hook_.setStatusInfo("");
        }
        drag_mode_ = false;
        download_area_action_.setEnabled(drag_mode_);

        if((download_frame_ != null) && download_frame_.isVisible())
        {
          download_frame_.setDownloadCoordinates(point.getLatitude(),point.getLongitude());
        }
        
        mouse_layer_.repaint();
      }
    } // end of if(Button1)
    
    if(event.isPopupTrigger())
    {
//      System.out.println("PopUp");
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
    if(event.getButton() == MouseEvent.BUTTON1)
    {
      drag_start_x_ = event.getX();
      drag_start_y_ = event.getY();
    }

        // on solaris, mousepressed sets popuptrigger
    if(event.isPopupTrigger())
    {
      showPopup(event);
    }
//    System.out.println("mousePressed: "+event.getSource());
  }

  public void mouseReleased(MouseEvent event)
  {
        // on windows, mousereleased sets popuptrigger
    if(event.isPopupTrigger())
    {
      showPopup(event);
    }
//    System.out.println("mouseReleased: "+event.getSource());
  }


//----------------------------------------------------------------------
// MouseMotionListener Adapter
//----------------------------------------------------------------------

  public void mouseDragged(MouseEvent event)
  {
    drag_current_x_ = event.getX();
    drag_current_y_ = event.getY();
    drag_mode_ = true;
    download_area_action_.setEnabled(drag_mode_);
    if((download_frame_ != null) && (download_frame_.isVisible()))
    {
      setDownloadFrameParametersForAreaDownload();
    }
    mouse_layer_.repaint();
//    System.out.println("mouseDragged: "+event.getSource());
  }

  public void mouseMoved(MouseEvent event)
  {
//    System.out.println("mouseMoved: "+event.getSource());
  }

//----------------------------------------------------------------------
// MouseWheelListener
// available only in jdk 1.4, so not used at the moment
//----------------------------------------------------------------------

//    void mouseWheelMoved(MouseWheelEvent event)
//     {
//       System.out.println("mouseWheelMoved: "+event.getSource());
//     }


  protected void showPopup(MouseEvent event)
  {
//    System.out.println("POPUP!");
  }


//----------------------------------------------------------------------
/**
 * Sets the rectangle to indicate the area of the map to download.
 *
 * @param download_latitude the latitude of the center of the map to
 * download.
 * @param download_longitude the longitude of the center of the map to
 * download.
 * @param download_scale the scale of the map to download.
 * @param download_image_width the width of the map to download.
 * @param download_image_height the height of the map to download.
 */
  public void setPreviewMap(double download_latitude,
                            double download_longitude,
                            float download_scale,
                            int download_image_width,
                            int download_image_height)
  {
//     System.out.println("setDownloadRectangle "+download_latitude+"/"+download_longitude
//                        +" 1:"+download_scale);
    preview_maps_.clear();
    preview_maps_.add(new MapRectangle(download_latitude, download_longitude,
                                       download_image_width, download_image_height,
                                       download_scale));
    draw_download_rectangle_ = true;
    calculatePreviewRectangles();
  }

//----------------------------------------------------------------------
/**
 * Hides the preview rectangle.
 *
 */
  public void hidePreviewMaps()
  {
    draw_download_rectangle_ = false;
    preview_maps_.clear();
    preview_rectangles_.clear();
    mouse_layer_.repaint();
  }

//----------------------------------------------------------------------
/**
 * Sets the rectangles to indicate the area of the maps (multiple!) to
 * download. The maps are held in a vector that contains MapRectangle
 * objects which in turn indicate the nort/east corner, width, height,
 * and scale of the map.
 *
 * @param maps a vector containing MapRectangle objects.
 */
  public void setPreviewMaps(Vector maps)
  {
    draw_download_rectangle_ = true;
    preview_maps_ = maps;
    calculatePreviewRectangles();
  }


//----------------------------------------------------------------------
/**
 * Finds all maps that are visible at the moment, sets them via the
 * setVisibleImages method and calls repaint. Uses a SwingWorker for
 * the timeconsuming task.
 */
  
  protected void calculatePreviewRectangles()
  {
        // stop old thread
    if(swing_worker_ != null)
      swing_worker_.interrupt();

    swing_worker_ = new SwingWorker()
      {
        Vector worker_preview_rectangles_ = new Vector();

        public Object construct()
        {
          mouse_layer_.fireStatusUpdate(LayerStatusEvent.START_WORKING);
          Iterator map_iterator = preview_maps_.iterator();
          MapRectangle map;
          Projection proj = mouse_layer_.getProjection();
          float scale_factor;
          while(map_iterator.hasNext())
          {
            map = (MapRectangle)map_iterator.next();
            scale_factor = map.getScale() / proj.getScale();
            
            Point position = mouse_layer_.getProjection().forward(map.getPosition());
            position.translate((int)(-map.getWidth()/2*scale_factor),
                               (int)(-map.getHeight()/2*scale_factor));

            Rectangle rectangle = new Rectangle(position);
            rectangle.setSize((int)(map.getWidth()*scale_factor),
                              (int)(map.getHeight()*scale_factor));
//            System.out.println("Preview rectangle: "+rectangle);
            worker_preview_rectangles_.add(rectangle);
          }
          return(null);
        }

        public void finished()
        {
          mouse_layer_.fireStatusUpdate(LayerStatusEvent.FINISH_WORKING);
          setPreviewRectangles(worker_preview_rectangles_);
          mouse_layer_.repaint();
        }
      };
    swing_worker_.start();
  }

//----------------------------------------------------------------------
/**
 * Sets the preview rectangles in a threadsafe way.
 */
  
  protected void setPreviewRectangles(Vector rectangles)
  {
    synchronized(preview_rectangles_)
    {
      preview_rectangles_ = rectangles;
    }
  }

//----------------------------------------------------------------------
/**
 * Sets the drag rectangle.
 */
  
  protected void setDragRectangle(int x, int y, int x1, int y1)
  {
    drag_start_x_ = x;
    drag_start_y_ = y;
    drag_current_x_ = x1;
    drag_current_y_ = y1;
  }

//----------------------------------------------------------------------
/**
 * Calculates the dragged rectangle coordinates after the projection
 * changed.
 */
  
  protected void calculateDragRectangle(Projection old_projection,
                                        Projection new_projection)
  {
    if((old_projection == null) || (new_projection == null))
      return;

    LatLonPoint latlon = old_projection.inverse(drag_start_x_,drag_start_y_);
    Point start_xy = new_projection.forward(latlon);
    latlon = old_projection.inverse(drag_current_x_, drag_current_y_, latlon);
    Point current_xy = new_projection.forward(latlon);
    setDragRectangle((int)start_xy.getX(),(int)start_xy.getY(),
                     (int)current_xy.getX(),(int)current_xy.getY());
  }



//----------------------------------------------------------------------
/**
 * Calculates and sets the parameters needed for an area download in
 * the download_frame.
 */
  
  protected void setDownloadFrameParametersForAreaDownload()
  {
    float scale = mouse_layer_.getProjection().getScale();
    int width = Math.abs(drag_start_x_ - drag_current_x_);
    int height = Math.abs(drag_start_y_ - drag_current_y_);
    
    int abs_width = (int)(width * scale);
    int abs_height = (int)(height * scale);
    download_frame_.setDownloadAreaSize(abs_width,abs_height);
    
    int center_x = Math.min(drag_start_x_,drag_current_x_) + width / 2;
    int center_y = Math.min(drag_start_y_,drag_current_y_) + height / 2;
    LatLonPoint center = mouse_layer_.getProjection().inverse(center_x,center_y);
    download_frame_.setDownloadCoordinates(center.getLatitude(),
                                           center.getLongitude());
    download_frame_.setDownloadMode(DownLoadFrame.DOWNLOAD_MODE_AREA_MAP);
  }
  
//----------------------------------------------------------------------
// Inner classes
//----------------------------------------------------------------------

//----------------------------------------------------------------------
/**
 * MapMouse Layer is responsible to paint all data concerning the mouse mode
 */

  class MapMouseLayer extends Layer
  {


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

          // draw lines of line segment:
      Iterator segment_iterator = segment_points_.iterator();
      LatLonPoint point;
      LatLonPoint last_point = null;
      
//      g2.setColor(new Color(0,0,255));
//      g2.setXORMode(Color.lightGray);
      g2.setColor(Color.darkGray);

      while(segment_iterator.hasNext())
      {
        point = (LatLonPoint)segment_iterator.next();
        if(Debug.DEBUG)
          Debug.println("GPSMap_mouse","paint new segment point" + point);
        OMPoint ompoint = new OMPoint(point.getLatitude(),point.getLongitude(),2);
        ompoint.generate(getProjection());
        ompoint.render(g2);
        if(last_point != null)
        {
          OMLine omline = new OMLine(last_point.getLatitude(),last_point.getLongitude(),
                                     point.getLatitude(),point.getLongitude(),LINE_TYPE);
          omline.generate(getProjection());
          omline.render(g2);
        }
        last_point = point;
      }

        int x,y,width,height;
          // draw rectangle of dragged mouse:
      if(drag_mode_)
      {
        if (drag_mode_)
        {
          x = Math.min(drag_start_x_,drag_current_x_);
          y = Math.min(drag_start_y_,drag_current_y_);
          width = Math.abs(drag_current_x_ - drag_start_x_);
          height = Math.abs(drag_current_y_ - drag_start_y_);
          
          g2.drawRect(x, y, width, height);
        }
      }

          // draw rectangle for map to download:
      if(draw_download_rectangle_)
      {
        Iterator rectangle_iterator = preview_rectangles_.iterator();
        Rectangle rectangle;
        g2.setColor(Color.red);
        while(rectangle_iterator.hasNext())
        {
          rectangle = (Rectangle)rectangle_iterator.next();
          x = (int)rectangle.getX();
          y = (int)rectangle.getY();
          width = (int)rectangle.getWidth();
          height = (int)rectangle.getHeight();
          g2.drawRect(x, y, width, height);
              // draw cross inside rectangle
          g2.drawLine(x, y, x+width, y+height);
          g2.drawLine(x+width, y, x, y+height);
        }

//         float scale_factor = download_scale_ / getProjection().getScale();
//         int width = (int)(download_image_width_ * scale_factor);
//         int height = (int)(download_image_height_ * scale_factor);
//         Point center_xy = getProjection().forward((float)download_center_latitude_,
//                                                   (float)download_center_longitude_);
//         int center_x = (int)center_xy.getX();
//         int center_y = (int)center_xy.getY();

// //         g2.setColor(Color.RED);
// //         g2.setXORMode(Color.RED);

//         System.out.println("Drawing download rectangle: center = "
//                            + center_xy +" - "
//                            +(center_x - width/22)+","
//                            +(center_y - height/2)+","
//                            +(width) +","
//                            +(height));
////        g2.drawRect(center_x - width/2,center_y - height/2,width, height);


//             // bad hack to visualize the maps to download for an area:
//         Point center = new Point();
//         MapInfo info;
//         for(int count = 0; count < preview_rectangles_.size(); count++)
//         {
//           info = (MapInfo)preview_rectangles_.elementAt(count);
//           System.out.println("painting preview rectangle: "+info);
//           center = mouse_layer_.getProjection().forward((float)info.getLatitude(),
//                                                         (float)info.getLongitude(),center);

//           scale_factor = info.getScale() / getProjection().getScale();
//           width = (int)(info.getWidth() * scale_factor);
//           height = (int)(info.getHeight() * scale_factor);
//           System.out.println("painting preview rectangle width/height: "+width+"/"+height);
//           center_x = (int)center.getX();
//           center_y = (int)center.getY();
//           g2.drawRect(center_x - width/2,center_y - height/2,width, height);
//               // draw a cross inside:
//           g2.drawLine(center_x - width/2,center_y - height/2, center_x + width/2,center_y + height/2);
//           g2.drawLine(center_x - width/2,center_y + height/2, center_x + width/2,center_y - height/2);
//         }
        
//         OMRect omrect = new OMRect((float)download_center_latitude_,
//                                    (float)download_center_longitude_,
//                                    -width_2, -height_2, width_2, height_2);
//         omrect.generate(getProjection());
//         omrect.render(g2);
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

      Projection old_projection = getProjection();
      
      if (!proj.equals(getProjection()))
      {
        setProjection(proj.makeClone());
      }
      if(Debug.DEBUG)
        Debug.println("MouseMapLayer_projection","new projection: "+proj);

      calculateDragRectangle(old_projection,proj);  // calculate the drag rectangle for new proj
      calculatePreviewRectangles(); // calculates in the background and repaints when finished!
    }
  }

      //----------------------------------------------------------------------
      /**
       * The Action that triggers the download of a map for a given position.
       */

  class DownloadMapPositionAction extends AbstractAction 
  {

    float download_scale_ = Float.NaN;
    double download_latitude_ = Double.NaN;
    double download_longitude_ = Double.NaN;

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public DownloadMapPositionAction()
    {
      super(GPSMap.ACTION_DOWNLOAD_MAP_POSITION);
      setEnabled(true);
    }

        //----------------------------------------------------------------------
        /**
         * Sets the scale of the map.
         * 
         * @param event the action event
         */

    public void actionPerformed(ActionEvent event)
    {
      if(download_frame_ == null)
        download_frame_ = new DownLoadFrame(resources_,map_manager_hook_,MapMouseMode.this);
      download_frame_.setInfo("");
      download_frame_.setDownloadMode(DownLoadFrame.DOWNLOAD_MODE_SINGLE_MAP);
      download_frame_.setVisible(true);
    }
  }

      //----------------------------------------------------------------------
      /**
       * The Action that triggers the download of a map for a given area
       */

  class DownloadMapAreaAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public DownloadMapAreaAction()
    {
      super(GPSMap.ACTION_DOWNLOAD_MAP_AREA);
      setEnabled(false);
    }

//----------------------------------------------------------------------
/**
 * Opens a download dialog for downloading an area.
 * 
 * @param event the action event
 */

    public void actionPerformed(ActionEvent event)
    {
      if(event.getActionCommand().equals(GPSMap.ACTION_DOWNLOAD_MAP_AREA))
      {
        if(download_frame_ == null)
          download_frame_ = new DownLoadFrame(resources_,map_manager_hook_,
                                              MapMouseMode.this);

        setDownloadFrameParametersForAreaDownload();
        download_frame_.setVisible(true);
      }
    }
  }
}





