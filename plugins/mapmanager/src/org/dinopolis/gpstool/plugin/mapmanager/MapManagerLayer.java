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


package org.dinopolis.gpstool.plugin.mapmanager;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.dinopolis.gpstool.MapInfo;
import org.dinopolis.gpstool.MapManagerHook;
import org.dinopolis.gpstool.event.MapsChangedEvent;
import org.dinopolis.gpstool.event.MapsChangedListener;
import org.dinopolis.gpstool.gui.util.BasicLayer;
import org.dinopolis.gpstool.gui.util.ImageInfo;




//----------------------------------------------------------------------
/**
 * This layer displays the information used in the map manager plugin (e.g. the
 * frames for all available maps).
 * 
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class MapManagerLayer extends BasicLayer
  implements MapsChangedListener
{
  MapManagerHook map_manager_;
  Collection visible_images_;
  Object visible_images_lock_ = new Object();
  Set selected_maps_;
  Object selected_maps_lock_ = new Object();
  Color unselected_color_ = new Color(0,0,0);
  Color selected_color_ = new Color(255,0,0);
  Color selected_fill_color_ = new Color(255,0,0,128);
  
//----------------------------------------------------------------------
/**
 * Constructor
 *
 * @param map_manager the map manager to ask for map infos.
 */
  public MapManagerLayer(MapManagerHook map_manager)
  {
    super();
    map_manager_ = map_manager;
    map_manager_.addMapsChangedListener(this);
//    map_infos_ = new Vector(map_manager_.getMapInfos());
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

    List visible_images;
    synchronized(visible_images_lock_)
    {
      if(visible_images_ != null)
        visible_images = new Vector(visible_images_); // make copy of image infos
      else
        return; // nothing to do, return
    }

//    System.out.println("selected: "+selected_maps_);

        // create projected position of all mapimages:
    Iterator iterator = visible_images.iterator();
    ImageInfo image;
    MapInfo map_info;
    while(iterator.hasNext())
    {
      image = (ImageInfo)iterator.next();
      map_info = image.getMapInfo();

      if((selected_maps_ == null) || !selected_maps_.contains(map_info.getFilename()))
      {
        g.setColor(unselected_color_);
        g.drawRect(image.getX(),image.getY(),image.getWidth(),image.getHeight());
//        g.drawString(image.getMapInfo().getFilename(),image.getX()+10,image.getY()+10);
      }
      else
      {
        //System.out.println("drawing selected map");
        g.setColor(selected_fill_color_);
        g.fillRect(image.getX(),image.getY(),image.getWidth(),image.getHeight());
        g.setColor(selected_color_);
        g.drawRect(image.getX(),image.getY(),image.getWidth(),image.getHeight());
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
    setVisibleImages(map_manager_.getAllVisibleImages(getProjection()));
  }

//----------------------------------------------------------------------
/**
 * Sets the visible images in a synchronized way.
 *
 * @param visible_images a list of ImageInfo objects
 */
  protected void setVisibleImages(Collection visible_images)
  {
    synchronized(visible_images_lock_)
    {
      visible_images_ = visible_images;
    }
  }
  
//----------------------------------------------------------------------
/**
 * Sets the selected maps in a synchronized way.
 *
 * @param selected_maps a set containing the filenames of the map info
 * objects.
 */
  protected void setSelectedMaps(Set selected_maps)
  {
    synchronized(selected_maps_lock_)
    {
      selected_maps_ = selected_maps;
    }
  }
  
  
//----------------------------------------------------------------------
/**
 * Called when a map is added or removed.
 *
 * @param event the event
 */
  public void mapsChanged(MapsChangedEvent event)
  {
    recalculateCoordinates();
  }


}


