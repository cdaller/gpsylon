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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.dinopolis.gpstool.GPSMap;
import org.dinopolis.gpstool.GPSMapKeyConstants;
import org.dinopolis.gpstool.MapManagerHook;
import org.dinopolis.gpstool.event.MapsChangedEvent;
import org.dinopolis.gpstool.event.MapsChangedListener;
import org.dinopolis.gpstool.gui.util.ImageInfo;
import org.dinopolis.gpstool.gui.util.VisibleImage;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.util.MapInfoScaleComparator;
import org.dinopolis.util.Debug;
import org.dinopolis.util.Resources;
import org.dinopolis.util.gui.ActionStore;
import org.dinopolis.util.gui.MenuFactory;
import org.dinopolis.util.gui.SwingWorker;

import com.bbn.openmap.Layer;
import com.bbn.openmap.event.LayerStatusEvent;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.proj.Projection;


//----------------------------------------------------------------------
/**
 * A layer that is able to display maps (e.g. from mapblast).
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class MultiMapLayer extends Layer
  implements GPSMapKeyConstants, MapsChangedListener
{
  protected Set map_infos_;
    
  Vector visible_images_;
  Object visible_images_lock_ = new Object();

  Rectangle old_clip_rect = new Rectangle();
    
  MapManagerHook map_manager_;  

  SwingWorker swing_worker_;

  boolean layer_active_ = true;

  Resources resources_;
  
//       // scale/PIXELFACT = meters/pixel
//   public static final float PIXELFACT = FlatProjection.PIXELFACT;
// //  public static float PIXELFACT = 2817.947378f;

  ActionStore action_store_;

      /** map scale divided by projection scale must be larger than
       * this value to show the map */
  double visible_map_scale_factor_;

  
//----------------------------------------------------------------------
/**
 * Default Constructor
 */
  public MultiMapLayer()
  {
    map_infos_ = Collections.synchronizedSet(new TreeSet(new MapInfoScaleComparator()));
  }

//----------------------------------------------------------------------
/**
 * Initializes this layer with the given resources.
 *
 * @param resources the resources to use.
 */
  public void initializePlugin(PluginSupport support)
  {
    resources_ = support.getResources();
    map_manager_ = support.getMapManagerHook();
    map_manager_.addMapsChangedListener(this);
    layer_active_ = resources_.getBoolean(KEY_MAP_LAYER_ACTIVE);
    visible_map_scale_factor_ = 1.0/resources_.getDouble(KEY_MAP_VISIBLE_MAP_SCALE_FACTOR);
    
        /** the Actions */
    Action[] actions_ = { new MultiMapLayerActivateAction()};
    action_store_ = ActionStore.getStore(GPSMap.ACTION_STORE_ID);
    action_store_.addActions(actions_);

  }

//----------------------------------------------------------------------
/**
 * Called when a map is added or removed.
 *
 * @param event the event
 */
  public void mapsChanged(MapsChangedEvent event)

  {
//  	if(event.getAction() == MapsChangedEvent.MAP_ADDED)
//  	    addMap(event.getMapInfo());
//  	else
//  	    if(event.getAction() == MapsChangedEvent.MAP_REMOVED)
//  		removeMap(event.getMapInfo());
    calculateVisibleImages();
  }
  

//----------------------------------------------------------------------
/**
 * Adds a map to this panel.
 *
 * @param map_info the info describing the map.
 */

//    public void addMap(MapInfo map_info)
//    {
//      map_infos_.add(map_info);
//      calculateVisibleImages();
//    }


//----------------------------------------------------------------------
/**
 * Adds all maps in the vector to this panel.
 *
 * @param map_infos a vector containing MapInfo objects.
 */

//    public void addMaps(List map_infos)
//    {
//      MapInfo map_info;
//      Iterator map_iterator = map_infos.iterator();
//      while(map_iterator.hasNext())
//      {
//        map_info = (MapInfo)map_iterator.next();
//        map_infos_.add(map_info);
//      }
//      calculateVisibleImages();
//    }


//----------------------------------------------------------------------
/**
 * Sets all maps in the vector to this panel. Any previously added
 * maps are removed.
 *
 * @param map_infos a vector containing MapInfo objects.
 */

//    public void setMaps(Vector map_infos)
//    {
//      map_infos_.clear();
//      MapInfo map_info;
//      Iterator map_iterator = map_infos.iterator();
//      while(map_iterator.hasNext())
//      {
//        map_info = (MapInfo)map_iterator.next();
//        map_infos_.add(map_info);
//      }
//      calculateVisibleImages();
//    }


//----------------------------------------------------------------------
/**
 * Removes a map from this panel.
 *
 * @param map_info the description of the map to be removed.
 * @return <code>true</code> if the MapInfo was removed.
 */

//    public boolean removeMap(MapInfo map_info)
//    {
//      boolean result = map_infos_.remove(map_info);
//      if(result)
//        calculateVisibleImages();
//      return(result);
//    }


//----------------------------------------------------------------------
/**
 * Finds all maps that are visible at the moment, sets them via the
 * setVisibleImages method and calls repaint. Uses a SwingWorker for
 * the timeconsuming task.
 */
  
  protected void calculateVisibleImages()
  {
    if(!layer_active_)
      return;

        // stop old thread
    if(swing_worker_ != null)
      swing_worker_.interrupt();

    swing_worker_ = new SwingWorker()
      {
        Vector worker_visible_images_ = new Vector();
        Vector worker_empty_rectangles_ = new Vector();

        public Object construct()
        {
          fireStatusUpdate(LayerStatusEvent.START_WORKING);
          Projection projection = getProjection();
              // find out, which images are really visible:
          Collection visible_images = map_manager_.getAllVisibleImages(projection,
                                                                       visible_map_scale_factor_);
//          System.out.println("MultiMap: visible maps are: "+visible_images);
              // find out, which images (and which areas of them) are really visible:
          worker_visible_images_ = VisibleImage.findVisibleImages(0,projection.getWidth(),
                                                                  0,projection.getHeight(),
                                                                  visible_images,
                                                                  worker_visible_images_,
                                                                  worker_empty_rectangles_);
//          System.out.println("Visible images: "+worker_visible_images_);
          return(null);
        }

        public void finished()
        {
          fireStatusUpdate(LayerStatusEvent.FINISH_WORKING);
          setVisibleImages(worker_visible_images_);
          repaint();
        }
      };
    swing_worker_.start();
  }


  protected void setVisibleImages(Vector images)
  {
    synchronized(visible_images_lock_)
    {
      visible_images_ = images;
    }
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
  public void paintComponent(java.awt.Graphics g)
  {
    if(!layer_active_)
      return;
    Graphics2D g2 = (Graphics2D) g;
//    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // draw images:

    if(Debug.DEBUG && Debug.isEnabled("MapLayer_measure"))
    {
      Debug.startTimer("MapLayer_paint");
    }

    old_clip_rect = g2.getClipBounds(old_clip_rect);
    
    if(visible_images_ != null)
    {
      Iterator image_iterator = visible_images_.iterator();
      float scale_factor;
      while(image_iterator.hasNext())
      {
        ImageInfo visible_image = (ImageInfo)image_iterator.next();
        scale_factor = visible_image.getScaleFactor();
        g2.clipRect(visible_image.getVisibleRectangleX(),
                    visible_image.getVisibleRectangleY(),
                    visible_image.getVisibleRectangleWidth(),
                    visible_image.getVisibleRectangleHeight());
//        System.out.println("Drawing image: "+visible_image);

        if(scale_factor < 1e-4)
        { // just print image
          g2.drawImage(visible_image.getImage(),
                       (int)visible_image.getX(),(int)visible_image.getY(),
                       this);
        }
        else // print scaled version of image
        {
          g2.drawImage(visible_image.getImage(),
                       (int)visible_image.getX(),(int)visible_image.getY(),
                       (int)(visible_image.getWidth()),
                       (int)(visible_image.getHeight()),
                       this);
        }
        g2.setClip(old_clip_rect);  // reset to previous cliprect
      }
    }

    if((visible_images_ == null) || (visible_images_.size() == 0))
    { 
      g2.setColor(Color.black);
      g2.drawString("No Maps available for given position and scale",10,40);
    }

//      g2.setColor(Color.red);
//      g2.drawString("1:"+((int)getProjection().getScale()),10,15);

    if(Debug.DEBUG && Debug.isEnabled("MapLayer_measure"))
    {
      System.out.println(Debug.stopTimer("MapLayer_paint"));
    }
  }


//       /** 
//        * Implementing the ProjectionPainter interface.
//        */
//   public void renderDataForProjection(Projection proj, java.awt.Graphics g)
//   {
//     System.out.println("RenderDataForPojection callback");
//     if (proj == null)
//     {
// 	    System.err.println("ERROR: MultiImageLayer.renderDataForProjection: null projection!");
// 	    return;
//     }
//     else
//     {
// 	    setProjection(proj.makeClone());
//       omgraphic_list_.generate(proj);
//     }
//     paint(g);
//   }

  
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
    if (!proj.equals(getProjection()))
    {
      setProjection(proj.makeClone());
    }
    if(Debug.DEBUG)
      Debug.println("MapLayer_projection","new projection: "+proj);

        // TODO: probably I should start a new thread here to
        // calculate all visible images (SwingWorker)
    calculateVisibleImages();
  }


//----------------------------------------------------------------------
// Inner Classes
//----------------------------------------------------------------------

//----------------------------------------------------------------------
/**
 * The Action that triggers the de-/activation of this layer.
 */

  class MultiMapLayerActivateAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public MultiMapLayerActivateAction()
    {
      super(GPSMap.ACTION_MAP_LAYER_ACTIVATE);
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
      putValue(MenuFactory.SELECTED, new Boolean(layer_active_));
      resources_.setBoolean(KEY_MAP_LAYER_ACTIVE,layer_active_);
      if(layer_active_)
        calculateVisibleImages();
      else
        repaint();
    }
  }

  
}




