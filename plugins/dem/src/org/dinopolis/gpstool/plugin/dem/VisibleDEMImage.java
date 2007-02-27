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


package org.dinopolis.gpstool.plugin.dem;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.dinopolis.gpstool.gui.util.ImageInfo;


//----------------------------------------------------------------------
/**
 * Copy of VisibleImage Class
 * 
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class VisibleDEMImage
{
  protected static String debug_indent = "";
  public static final boolean DEBUG = false;

//----------------------------------------------------------------------
/**
 * Finds the parts of images that are visible in a given viewport.
 *
 * @param viewport_xmin the left coordinate of the viewport
 * @param viewport_xmax the right coordinate of the viewport
 * @param viewport_ymin the top coordinate of the viewport
 * @param viewport_ymax the bottom coordinate of the viewport
 * @param available all available images sorted from the smallest scale
 * to the largest.
 * @param result the vector used to add the resulting images that are
 * visible
 * @param empty_rect a vector holding Rectangle objects for the areas
 * of the viewport that are not covered by an image.
 * @return a Vector with images that are visible (using the clipping
 * rectangles).
 */

  public static Vector findVisibleImages(int viewport_xmin, int viewport_xmax,
                                         int viewport_ymin, int viewport_ymax,
                                         Collection available, Vector result, Vector empty_rect)
  {
//     System.out.println("findRectangles:" + viewport_xmin +"," +viewport_xmax +" / "
//                        + viewport_ymin + "," + viewport_ymax);
    if(viewport_xmin > viewport_xmax)
      throw new IllegalArgumentException("xmin > xmax not allowed!");
    if(viewport_ymin > viewport_ymax)
      throw new IllegalArgumentException("ymin > ymax not allowed!");

    int image_xmin, image_xmax, image_ymin, image_ymax;
    int delta_x, delta_y;

    int viewport_width = (viewport_xmax-viewport_xmin);
    int viewport_height= (viewport_ymax-viewport_ymin);

    Iterator iterator = available.iterator();
    boolean image_found = false;
    while(iterator.hasNext() && !image_found)
    {
      ImageInfo info = (ImageInfo)iterator.next();
//      System.out.println("checking image: "+info);
      image_xmin = info.getX();
      image_xmax = image_xmin + info.getWidth();
      image_ymin = info.getY();
      image_ymax = image_ymin + info.getHeight();


          // check, if image is visible at all:

      delta_x = Math.abs((viewport_xmin + viewport_width/2)  - (image_xmin + (image_xmax-image_xmin)/2));
      delta_y = Math.abs((viewport_ymin + viewport_height/2) - (image_ymin + (image_ymax-image_ymin)/2));

          // visible in the given viewport area: if the distance
          // between the center of the image and the center of the
          // viewport is greater than (image.width + viewport.width)/2
          // (same with height), the image is not visible

      if((delta_x > (info.getWidth() + viewport_width)/2)
         && (delta_y > (info.getHeight()  + viewport_height)/2))
      {
//        System.out.println("image not visible: "+info);
      }
      else
// Figure 01:
      if(image_xmin>viewport_xmin & image_xmax<viewport_xmax
         & image_ymin>viewport_ymin & image_ymax<viewport_ymax)
      {
        if(DEBUG){System.out.println(debug_indent+"figure 01 detected"); debug_indent=debug_indent+"  ";}
        image_found = true;
        DEMImageInfo new_info = new DEMImageInfo(info);
        new_info.setVisibleRectangle(image_xmin, image_ymin,
                                     image_xmax-image_xmin, image_ymax-image_ymin);
        result.add(new_info);
            // area 1:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 1...");}
        result = findVisibleImages(viewport_xmin,image_xmin,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 1...");}
            // area 2:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 2...");}
        result = findVisibleImages(image_xmax,viewport_xmax,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 2...");}
            // area 3:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 3...");}
        result = findVisibleImages(image_xmin,image_xmax,
                                viewport_ymin,image_ymin,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 3...");}
            // area 4:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 4...");}
        result = findVisibleImages(image_xmin,image_xmax,
                                image_ymax,viewport_ymax,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 4...");}
        if(DEBUG){debug_indent = debug_indent.substring(2);}
      }
      else
// Figure 02:
      if(image_xmin<viewport_xmin & image_xmax>viewport_xmin & image_xmax<viewport_xmax
         & image_ymin>viewport_ymin & image_ymax<viewport_ymax)
      {
        if(DEBUG){System.out.println(debug_indent+"figure 02 detected"); debug_indent=debug_indent+"  ";}
        image_found = true;
        DEMImageInfo new_info = new DEMImageInfo(info);
        new_info.setVisibleRectangle(viewport_xmin, image_ymin,
                                     image_xmax-viewport_xmin, image_ymax-image_ymin);
        result.add(new_info);
            // area 1:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 1...");}
        result = findVisibleImages(image_xmax,viewport_xmax,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 1...");}
            // area 2:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 2...");}
        result = findVisibleImages(viewport_xmin,image_xmax,
                                viewport_ymin,image_ymin,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 2...");}
            // area 3:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 3...");}
        result = findVisibleImages(viewport_xmin,image_xmax,
                                image_ymax,viewport_ymax,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 3...");}
        if(DEBUG){debug_indent = debug_indent.substring(2);}
      }
      else
// Figure 03:
      if(image_xmin>viewport_xmin & image_xmin<viewport_xmax  & image_xmax>viewport_xmax
          & image_ymin>viewport_ymin & image_ymax<viewport_ymax)
      {
        if(DEBUG){System.out.println(debug_indent+"figure 03 detected"); debug_indent=debug_indent+"  ";}
        image_found = true;
        DEMImageInfo new_info = new DEMImageInfo(info);
        new_info.setVisibleRectangle(image_xmin, image_ymin,
                                     viewport_xmax-image_xmin, image_ymax-image_ymin);
        result.add(new_info);
            // area 1:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 1...");}
        result = findVisibleImages(viewport_xmin,image_xmin,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 1...");}
            // area 2:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 2...");}
        result = findVisibleImages(image_xmin,viewport_xmax,
                                viewport_ymin,image_ymin,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 2...");}
            // area 3:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 3...");}
        result = findVisibleImages(image_xmin,viewport_xmax,
                                image_ymax,viewport_ymax,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 3...");}
        if(DEBUG){debug_indent = debug_indent.substring(2);}
      }
      else
// Figure 04:
      if(image_xmin>viewport_xmin & image_xmax<viewport_xmax
          & image_ymin<viewport_ymin & image_ymax>viewport_ymin & image_ymax<viewport_ymax)
      {
        if(DEBUG){System.out.println(debug_indent+"figure 04 detected"); debug_indent=debug_indent+"  ";}
        image_found = true;
        DEMImageInfo new_info = new DEMImageInfo(info);
        new_info.setVisibleRectangle(image_xmin, viewport_ymin,
                                     image_xmax-image_xmin, image_ymax-viewport_ymin);
        result.add(new_info);
            // area 1:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 1...");}
        result = findVisibleImages(viewport_xmin,image_xmin,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 1...");}
            // area 2:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 2...");}
        result = findVisibleImages(image_xmin,image_xmax,
                                image_ymax,viewport_ymax,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 2...");}
            // area 3:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 3...");}
        result = findVisibleImages(image_xmax,viewport_xmax,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 3...");}
        if(DEBUG){debug_indent = debug_indent.substring(2);}
      }
      else
// Figure 05:
      if(image_xmin>viewport_xmin & image_xmax<viewport_xmax
          & image_ymin>viewport_ymin & image_ymin<viewport_ymax & image_ymax>viewport_ymax)
      {
        if(DEBUG){System.out.println(debug_indent+"figure 05 detected"); debug_indent=debug_indent+"  ";}
        image_found = true;
        DEMImageInfo new_info = new DEMImageInfo(info);
        new_info.setVisibleRectangle(image_xmin, image_ymin,
                                     image_xmax-image_xmin, viewport_ymax-image_ymin);
        result.add(new_info);
            // area 1:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 1...");}
        result = findVisibleImages(viewport_xmin,image_xmin,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 1...");}
            // area 2:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 2...");}
        result = findVisibleImages(image_xmin,image_xmax,
                                viewport_ymin,image_ymin,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 2...");}
            // area 3:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 3...");}
        result = findVisibleImages(image_xmax,viewport_xmax,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 3...");}
        if(DEBUG){debug_indent = debug_indent.substring(2);}
      }
      else
// Figure 06:
      if(image_xmin<=viewport_xmin & image_xmax>viewport_xmin & image_xmax<=viewport_xmax
         & image_ymin<=viewport_ymin & image_ymax>=viewport_ymax)
      {
        if(DEBUG){System.out.println(debug_indent+"figure 06 detected"); debug_indent=debug_indent+"  ";}
        image_found = true;
        DEMImageInfo new_info = new DEMImageInfo(info);
        new_info.setVisibleRectangle(viewport_xmin, viewport_ymin,
                                     image_xmax-viewport_xmin, viewport_ymax-viewport_ymin);
        result.add(new_info);
            // area 1:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 1...");}
        result = findVisibleImages(image_xmax,viewport_xmax,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 1...");}
        if(DEBUG){debug_indent = debug_indent.substring(2);}
      }
      else
// Figure 07:
      if(image_xmin>=viewport_xmin & image_xmin<viewport_xmax  & image_xmax>=viewport_xmax
         & image_ymin<=viewport_ymin & image_ymax>=viewport_ymax)
      {
        if(DEBUG){System.out.println(debug_indent+"figure 07 detected"); debug_indent=debug_indent+"  ";}
        image_found = true;
        DEMImageInfo new_info = new DEMImageInfo(info);
        new_info.setVisibleRectangle(image_xmin, viewport_ymin,
                                     viewport_xmax-image_xmin, viewport_ymax-viewport_ymin);
        result.add(new_info);
            // area 1:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 1...");}
        result = findVisibleImages(viewport_xmin,image_xmin,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 1...");}
        if(DEBUG){debug_indent = debug_indent.substring(2);}
      }
      else
// Figure 08:
      if(image_xmin<=viewport_xmin & image_xmax>=viewport_xmax
         & image_ymin<=viewport_ymin & image_ymax>viewport_ymin & image_ymax<=viewport_ymax)
      {
        if(DEBUG){System.out.println(debug_indent+"figure 08 detected"); debug_indent=debug_indent+"  ";}
        image_found = true;
        DEMImageInfo new_info = new DEMImageInfo(info);
        new_info.setVisibleRectangle(viewport_xmin, viewport_ymin,
                                     viewport_xmax-viewport_xmin, image_ymax-viewport_ymin);
        result.add(new_info);
            // area 1:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 1...");}
        result = findVisibleImages(viewport_xmin,viewport_xmax,
                                image_ymax,viewport_ymax,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 1...");}
        if(DEBUG){debug_indent = debug_indent.substring(2);}
      }
      else
// Figure 09:
      if(image_xmin<=viewport_xmin & image_xmax>=viewport_xmax
         & image_ymin>=viewport_ymin & image_ymin<viewport_ymax & image_ymax>=viewport_ymax)
      {
        if(DEBUG){System.out.println(debug_indent+"figure 09 detected"); debug_indent=debug_indent+"  ";}
        image_found = true;
        DEMImageInfo new_info = new DEMImageInfo(info);
        new_info.setVisibleRectangle(viewport_xmin, image_ymin,
                                     viewport_xmax-viewport_xmin, viewport_ymax-image_ymin);
        result.add(new_info);
            // area 1:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 1...");}
        result = findVisibleImages(viewport_xmin,viewport_xmax,
                                viewport_ymin,image_ymin,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 1...");}
        if(DEBUG){debug_indent = debug_indent.substring(2);}
      }
      else
// Figure 10:
      if(image_xmin<=viewport_xmin & image_xmax>=viewport_xmax
         & image_ymin<=viewport_ymin & image_ymax>=viewport_ymax)
      {
        if(DEBUG){System.out.println(debug_indent+"figure 10 detected"); debug_indent=debug_indent+"  ";}
        image_found = true;
        DEMImageInfo new_info = new DEMImageInfo(info);
        new_info.setVisibleRectangle(viewport_xmin, viewport_ymin,
                                     viewport_xmax-viewport_xmin,
                                     viewport_ymax-viewport_ymin);
        result.add(new_info);
        if(DEBUG){debug_indent = debug_indent.substring(2);}
        return(result);
      }
      else
// Figure 11:
      if(image_xmin<viewport_xmin & image_xmax>viewport_xmin & image_xmax<viewport_xmax
         & image_ymin>viewport_ymin & image_ymin<viewport_ymax & image_ymax>viewport_ymax)
      {
        if(DEBUG){System.out.println(debug_indent+"figure 11 detected"); debug_indent=debug_indent+"  ";}
        image_found = true;
        DEMImageInfo new_info = new DEMImageInfo(info);
        new_info.setVisibleRectangle(viewport_xmin, image_ymin,
                                     image_xmax-viewport_xmin, viewport_ymax-image_ymin);
        result.add(new_info);
            // area 1:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 1...");}
        result = findVisibleImages(viewport_xmin,image_xmax,
                                viewport_ymin,image_ymin,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 1...");}
            // area 2:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 2...");}
        result = findVisibleImages(image_xmax,viewport_xmax,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 2...");}
        if(DEBUG){debug_indent = debug_indent.substring(2);}
      }
      else
// Figure 12:
      if(image_xmin>viewport_xmin & image_xmin<viewport_xmax  & image_xmax>viewport_xmax
         & image_ymin>viewport_ymin & image_ymin<viewport_ymax & image_ymax>viewport_ymax)
      {
        if(DEBUG){System.out.println(debug_indent+"figure 12 detected"); debug_indent=debug_indent+"  ";}
        image_found = true;
        DEMImageInfo new_info = new DEMImageInfo(info);
        new_info.setVisibleRectangle(image_xmin, image_ymin,
                                     viewport_xmax-image_xmin, viewport_ymax-image_ymin);
        result.add(new_info);
            // area 1:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 1...");}
        result = findVisibleImages(image_xmin,viewport_xmax,
                                viewport_ymin,image_ymin,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 1...");}
            // area 2:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 2...");}
        result = findVisibleImages(viewport_xmin,image_xmin,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 2...");}
        if(DEBUG){debug_indent = debug_indent.substring(2);}
      }
      else
// Figure 13:
      if(image_xmin<viewport_xmin & image_xmax>viewport_xmin & image_xmax<viewport_xmax
         & image_ymin<viewport_ymin & image_ymax>viewport_ymin & image_ymax<viewport_ymax)
      {
        if(DEBUG){System.out.println(debug_indent+"figure 13 detected"); debug_indent=debug_indent+"  ";}
        image_found = true;
        DEMImageInfo new_info = new DEMImageInfo(info);
        new_info.setVisibleRectangle(viewport_xmin, viewport_ymin,
                                     image_xmax-viewport_xmin, image_ymax-viewport_ymin);
        result.add(new_info);
            // area 1:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 1...");}
        result = findVisibleImages(image_xmax,viewport_xmax,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 1...");}
            // area 2:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 2...");}
        if(DEBUG){System.out.println("available: "+available);}
        if(DEBUG){System.out.println("viewport_xmin: "+viewport_xmin);}
        if(DEBUG){System.out.println("image_xmax: "+image_xmax);}
        if(DEBUG){System.out.println("image_ymax: "+image_ymax);}
        if(DEBUG){System.out.println("viewport_ymax: "+viewport_ymax);}
        result = findVisibleImages(viewport_xmin,image_xmax,
                                image_ymax,viewport_ymax,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 2...");}
        if(DEBUG){debug_indent = debug_indent.substring(2);}
      }
      else
// Figure 14:
      if(image_xmin>viewport_xmin & image_xmin<viewport_xmax  & image_xmax>viewport_xmax
         & image_ymin<viewport_ymin & image_ymax>viewport_ymin & image_ymax<viewport_ymax)
      {
        if(DEBUG){System.out.println(debug_indent+"figure 14 detected"); debug_indent=debug_indent+"  ";}
        image_found = true;
        DEMImageInfo new_info = new DEMImageInfo(info);
        new_info.setVisibleRectangle(image_xmin, viewport_ymin,
                                     viewport_xmax-image_xmin, image_ymax-viewport_ymin);
        result.add(new_info);
            // area 1:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 1...");}
        result = findVisibleImages(image_xmin,viewport_xmax,
                                image_ymax,viewport_ymax,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 1...");}
            // area 2:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 2...");}
        result = findVisibleImages(viewport_xmin,image_xmin,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 2...");}
        if(DEBUG){debug_indent = debug_indent.substring(2);}
      }
      else
// Figure 15:
      if(image_xmin>viewport_xmin & image_xmin <viewport_xmax  & image_xmax>viewport_xmin
         & image_xmax<viewport_xmax & image_ymin<viewport_ymin & image_ymax>viewport_ymax)
      {
        if(DEBUG){System.out.println(debug_indent+"figure 15 detected"); debug_indent=debug_indent+"  ";}
        image_found = true;
        DEMImageInfo new_info = new DEMImageInfo(info);
        new_info.setVisibleRectangle(image_xmin, viewport_ymin,
                                     image_xmax-image_xmin, viewport_ymax-viewport_ymin);
        result.add(new_info);
            // area 1:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 1...");}
        result = findVisibleImages(viewport_xmin,image_xmin,
                                   viewport_ymin,viewport_ymax,
                                   available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 1...");}
            // area 2:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 2...");}
        result = findVisibleImages(image_xmax,viewport_xmax,
                                   viewport_ymin,viewport_ymax,
                                   available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 2...");}
        if(DEBUG){debug_indent = debug_indent.substring(2);}
      }
      else
// Figure 16:
      if(image_xmin<viewport_xmin & image_xmax>viewport_xmax & image_ymin>viewport_ymin
         & image_ymin<viewport_ymax & image_ymax>viewport_ymin & image_ymax<viewport_ymax)
      {
        if(DEBUG){System.out.println(debug_indent+"figure 15 detected"); debug_indent=debug_indent+"  ";}
        image_found = true;
        DEMImageInfo new_info = new DEMImageInfo(info);
        new_info.setVisibleRectangle(viewport_xmin,image_ymin,
                                     viewport_xmax-viewport_xmin, image_ymax-image_ymin);
        result.add(new_info);
            // area 1:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 1...");}
        result = findVisibleImages(viewport_xmin,viewport_xmax,
                                   viewport_ymin,image_ymin,
                                   available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 1...");}
            // area 2:
        if(DEBUG){System.out.println(debug_indent+"finding rectangles for area 2...");}
        result = findVisibleImages(viewport_xmin,viewport_xmax,
                                   image_ymax,viewport_ymax,
                                   available,result,empty_rect);
        if(DEBUG){System.out.println(debug_indent+"returned from finding rectangles for area 2...");}
        if(DEBUG){debug_indent = debug_indent.substring(2);}
      }
    }
    if(!image_found)
    {
      empty_rect.add(new Rectangle(viewport_xmin,viewport_ymin,
                                   viewport_xmax-viewport_xmin,viewport_ymax-viewport_ymin));
      if(DEBUG){System.out.println(debug_indent+"no image for rectangle found");}
    }
    return(result);
  }

}

