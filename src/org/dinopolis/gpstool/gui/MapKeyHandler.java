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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import org.dinopolis.gpstool.MapNavigationHook;

//----------------------------------------------------------------------
/**
 * this module is responsible for key press events
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class MapKeyHandler extends KeyAdapter
{

      /** the hook responsible for map navigation */
  MapNavigationHook map_navigation_hook_;

      /** center of screen multiplied with -/+ this factor is used
       * when up/down/left/right is pressed The new center is
       * getWidth()/2 * PAN_FACTOR. To speed up calculation, the
       * division by 2.0 is included in this factor.
       */
  static final float PAN_FACTOR = 0.2f;
  static final float PAN_FACTOR_SHIFT = 0.8f;
  
  protected boolean shift_pressed = false;

//----------------------------------------------------------------------
/**
 * Construct a default route layer.  Initializes omgraphics to
 * a new OMGraphicList, and invokes createGraphics to create
 * the canned list of routes.
 */
  public MapKeyHandler(MapNavigationHook map_navigation_hook)
  {
    map_navigation_hook_ = map_navigation_hook;
//    addKeyListener(new GPSMapKeyListener());
  }

//----------------------------------------------------------------------
// The MapNavigation Hookups
//----------------------------------------------------------------------

// //----------------------------------------------------------------------
// /**
//  * Sets the new center of the map.
//  *
//  * @param latitude The latitude of the new center of the map
//  * @param longitude The longitude of the new center of the map
//  */

//   public void setMapCenter(LatLonPoint center)
//   {
//     setMapCenter(center.getLatitude(),center.getLongitude());
//   }
  
// //----------------------------------------------------------------------
// /**
//  * Sets the new center of the map.
//  *
//  * @param latitude The latitude of the new center of the map
//  * @param longitude The longitude of the new center of the map
//  */

//   public void setMapCenter(double latitude, double longitude)
//   {
//     if(map_navigation_hook_ != null)
//       map_navigation_hook_.setMapCenter(latitude,longitude);
//   }


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
  

//----------------------------------------------------------------------
/**
 * Sets a new center for the map. A negative factor moves the center
 * up or left, a postive factor down or right. A factor of 1.0
 * translates the center a complete height/width down or right.
 *
 * @param factor_x the horizontal factor to recenter the map.
 * @param factor_y the vertical factor to recenter the map.
 */

  public void translateMapCenterRelative(float factor_x, float factor_y)
  {
    if(map_navigation_hook_ != null)
      map_navigation_hook_.translateMapCenterRelative(factor_x,factor_y);
  }

//----------------------------------------------------------------------
// The KeyAdapter Methods
//----------------------------------------------------------------------

//----------------------------------------------------------------------
/**
 * Callback for a key press
 *
 * @param event the KeyEvent
 */

  public void keyPressed(KeyEvent event)
  {
//    System.out.println("Key pressed: "+KeyEvent.getKeyText(event.getKeyCode()));

    switch(event.getKeyCode())
    {
    case KeyEvent.VK_LEFT:
    case KeyEvent.VK_KP_LEFT:
      if(shift_pressed)
        translateMapCenterRelative(-PAN_FACTOR_SHIFT,0f);
      else
        translateMapCenterRelative(-PAN_FACTOR,0f);
      break;
    case KeyEvent.VK_RIGHT:
    case KeyEvent.VK_KP_RIGHT:
      if(shift_pressed)
        translateMapCenterRelative(PAN_FACTOR_SHIFT,0f);
      else
        translateMapCenterRelative(PAN_FACTOR,0f);
      break;
    case KeyEvent.VK_UP:
    case KeyEvent.VK_KP_UP:
      if(shift_pressed)
        translateMapCenterRelative(0f,-PAN_FACTOR_SHIFT);
      else
        translateMapCenterRelative(0f, -PAN_FACTOR);
      break;
    case KeyEvent.VK_DOWN:
    case KeyEvent.VK_KP_DOWN:
      if(shift_pressed)
        translateMapCenterRelative(0f, PAN_FACTOR_SHIFT);
      else
        translateMapCenterRelative(0f, PAN_FACTOR);
      break;
    case KeyEvent.VK_SHIFT:
      shift_pressed = true;
      break;
//    case KeyEvent.VK_LESS:
    case KeyEvent.VK_PLUS:
    case KeyEvent.VK_PAGE_DOWN:
      reScale(0.5f);
      break;
//    case KeyEvent.VK_GREATER:
    case KeyEvent.VK_MINUS:
    case KeyEvent.VK_PAGE_UP:
      reScale(2.0f);
      break;
    default:
      break;
    }
  }


  public void keyReleased(KeyEvent event)
  {
    switch(event.getKeyCode())
    {
    case KeyEvent.VK_SHIFT:
      shift_pressed = false;
      break;
    default:
      break;
    }
  }
}


