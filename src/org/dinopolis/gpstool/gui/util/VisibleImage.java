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


package org.dinopolis.gpstool.gui.util;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;


//----------------------------------------------------------------------
/**
 * The algorithm implemented in this class searches the smallest map
 * to show, paint it, and find the rectangles on the screen that are
 * not covered by this map. For the remaining empty rectangles, the
 * algorithm is repeated until the screen is filled, or no more maps
 * are available. 
 * <p>
 * The following shows, how a map may be location on a given viewport:
 *<pre>
 *             viewport_xmin       viewport_xmax
 *             |                   |
 *
 *             ..................... -viewport_ymin
 *       	     .                   .
 *             . 	   ########      . -image_ymin
 *             .     ########      .
 *       	     . 	   ########      .
 *             . 	   ########      . -image_ymax
 *             . 	               	 .
 *             ..................... -viewport_ymax
 *
 *                  |   	  |
 *                  |       image_xmax
 *                  image_xmin
 *
 *
 *                 figure 01:                            figure 02:
 *             .....................                 .....................
 *             . 	   |  3	  |    	 .                 . 2 |               .
 *             . 	   ########   	 .             #########               .
 *             . 	1  ########  2	 .             #########               .
 *             . 	   ########   	 .             #########       1       .
 *             . 	   ########      .             #########               .
 *             . 	   |  4	  |    	 .                 . 3 |               .
 *             .....................                 .....................
 *
 *
 *
 *                 figure 03:                        figure 04:
 *             .....................
 *             .               | 2 .                 ########
 *             .               ##########       .....########........
 *             .               ##########       .    ########       .
 *             .      1        ##########       .    ########       .
 *             .               ##########       .    |      |       .
 *             .               | 3 .            .  1 |  2   |   3   .
 *             .....................            .    |      |       .
 *                                              .    |      |       .
 *                                              .....................
 *
 *
 *
 *
 *
 *                 figure 05:                #######  figure 06:
 *             .....................         #######.................
 *             .     |       |     .         #######                .
 *             .     |       |     .         #######                .
 *             .     |       |     .         #######                .
 *             .  1  |   2   |  3  .         #######       1        .
 *             .     |       |     .         #######                .
 *             .     #########     .         #######                .
 *             ......#########......         #######.................
 *                   #########               #######
 *                   #########               #######
 *
 *
 *
 *                 figure 07:    ######             figure 08:
 *             ..................######       ##########################
 *             .                 ######       ##########################
 *             .                 ######       ##########################
 *             .                 ######         .                   .
 *             .         1       ######         .                   .
 *             .                 ######         .         1         .
 *             .                 ######         .                   .
 *             ..................######         .....................
 *                               ######
 *                               ######
 *
 *                 figure 09:                       figure 10:
 *             .....................         ############################
 *             .                   .         ############################
 *             .                   .         ############################
 *             .         1         .         ############################
 *             .                   .         ############################
 *             .                   .         ############################
 *          ############################     ############################
 *          ############################     ############################
 *          ############################     ############################
 *          ############################
 *
 *                   figure 11:                       figure 12:
 *             .....................            .....................
 *             .      |            .            .              |    .
 *             .      |            .            .              |    .
 *             .   1  |            .            .              |  1 .
 *             .      |     2      .            .       2      |    .
 *          ###########            .            .              ##########
 *          ###########            .            .              ##########
 *          ###########.............            ...............##########
 *          ###########                                        ##########
 *          ###########                                        ##########
 *
 *
 *         ###########  figure 13:                   figure 14:##########
 *         ###########..............            ...............##########
 *         ###########             .            .              ##########
 *         ###########             .            .              ##########
 *             .     |             .            .              |    .
 *             .  2  |        1    .            .      2       |    .
 *             .     |             .            .              |  1 .
 *             .     |             .            .              |    .
 *             .....................            .....................
 *
 *
 *                      figure 15:                   figure 16:
 *                  ##########
 *             .....##########......            .....................
 *             .    ##########     .            .         1         .
 *             .    ##########     .          ##########################
 *             .    ##########     .          ##########################
 *             .  1 ##########  2  .          ##########################
 *             .    ##########     .          ##########################
 *             .    ##########     .            .         2         .
 *             .....##########......            .....................
 *                  ##########
 *                  ##########
 *</pre>
 * The following conditions must be met for the specific figures:<br>
 * <p>
 * Figure 01:<br>
 *   image_xmin &gt; viewport_xmin &apm; image_xmax &lt; viewport_xmax  &amp; image_ymin &gt; viewport_ymin &amp; image_ymax &lt; viewport_ymax
 * </p>
 * <p>
 * Figure 02:<br>
 *   image_xmin &lt; viewport_xmin &amp; image_xmax &gt; viewport_xmin &amp; image_xmax &lt; viewport_xmax  &amp; image_ymin &gt; viewport_ymin &amp; image_ymax &lt; viewport_ymax
 * </p>
 * <p>
 * Figure 03:<br>
 *  image_xmin &gt; viewport_xmin &amp; image_xmin &lt; viewport_xmax  &amp; image_xmax &gt; viewport_xmax &amp; image_ymin &gt; viewport_ymin &amp; image_ymax &lt; viewport_ymax
 * </p>
 * <p>
 * Figure 04:<br>
 *  image_xmin &gt; viewport_xmin &amp; image_xmax &lt; viewport_xmax  &amp; image_ymin &lt; viewport_ymin &amp; image_ymax &gt; viewport_ymin &amp; image_ymax &lt; viewport_ymax
 * </p>
 * <p>
 * Figure 05:<br>
 *  image_xmin &gt; viewport_xmin &amp; image_xmax &lt; viewport_xmax  &amp; image_ymin &gt; viewport_ymin &amp; image_ymin &lt; viewport_ymax &amp; image_ymax &gt; viewport_ymax
 * </p>
 * <p>
 * Figure 06:<br>
 *   image_xmin &lt; viewport_xmin &amp; image_xmax &gt; viewport_xmin &amp; image_xmax &lt; viewport_xmax  &amp; image_ymin &lt; viewport_ymin &amp; image_ymax &gt; viewport_ymax
 * </p>
 * <p>
 * Figure 07:<br>
 *   image_xmin &gt; viewport_xmin &amp; image_xmin &lt; viewport_xmax  &amp; image_xmax &gt; viewport_xmax &amp; image_ymin &lt; viewport_ymin &amp; image_ymax &gt; viewport_ymax
 * </p>
 * <p>
 * Figure 08:<br>
 *   image_xmin &lt; viewport_xmin &amp; image_xmax &gt; viewport_xmax &amp; image_ymin &lt; viewport_ymin &amp; image_ymax &gt; viewport_ymin &amp; image_ymax &lt; viewport_ymax
 * </p>
 * <p>
 * Figure 09:<br>
 *   image_xmin &lt; viewport_xmin &amp; image_xmax &gt; viewport_xmax &amp; image_ymin &gt; viewport_ymin &amp; image_ymin &lt; viewport_ymax &amp; image_ymax &gt; viewport_ymax
 * </p>
 * <p>
 * Figure 10:<br>
 *   image_xmin &lt;= viewport_xmin &amp; image_xmax &gt;= viewport_xmax &amp; image_ymin &lt;= viewport_ymin &amp; image_ymax &gt;= viewport_ymax
 * </p>
 * <p>
 * Figure 11:<br>
 *   image_xmin &lt; viewport_xmin &amp; image_xmax &gt; viewport_xmin &amp; image_xmax &lt; viewport_xmax  &amp; image_ymin &gt; viewport_ymin &amp; image_ymin &lt; viewport_ymax &amp; image_ymax &gt; viewport_ymax
 * </p>
 * <p>
 * Figure 12:<br>
 *   image_xmin &gt; viewport_xmin &amp; image_xmin &lt; viewport_xmax  &amp; image_xmax &gt; viewport_xmax &amp; image_ymin &gt; viewport_ymin &amp; image_ymin &lt; viewport_ymax &amp; image_ymax &gt; viewport_ymax
 * </p>
 * <p>
 * Figure 13:<br>
 *   image_xmin &lt; viewport_xmin &amp; image_xmax &gt; viewport_xmin &amp; image_xmax &lt; viewport_xmax  &amp; image_ymin &lt; viewport_ymin &amp; image_ymax &gt; viewport_ymin &amp; image_ymax &lt; viewport_ymax
 * </p>
 * <p>
 * Figure 14:<br>
 *   image_xmin &gt; viewport_xmin &amp; image_xmin &lt; viewport_xmax  &amp; image_xmax &gt; viewport_xmax &amp; image_ymin &lt; viewport_ymin &amp; image_ymax &gt; viewport_ymin &amp; image_ymax &lt; viewport_ymax
 * </p>
 * <p>
 * Figure 15:<br>
 *   image_xmin &gt; viewport_xmin &amp; image_xmin  &lt; viewport_xmax  &amp; image_xmax &gt; viewport_xmin &amp; image_xmax &lt; viewport_xmax &amp; image_ymin &lt; viewport_ymin &amp; image_ymax &gt; viewport_ymax
 * </p>
 * <p>
 * Figure 16:<br>
 *   image_xmin &lt; viewport_xmin &amp; image_xmax &gt; viewport_xmax &amp; image_ymin &gt; viewport_ymin &amp; image_ymin &lt; viewport_ymax &amp; image_ymax &gt; viewport_ymin &amp; image_ymax &lt; viewport_ymax
 * </p>
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class VisibleImage
{

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
 * @param empty_rec a vector holding Rectangle objects for the areas
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
        // System.out.println("figure 01 detected");
        image_found = true;
        ImageInfo new_info = new ImageInfo(info);
        new_info.setVisibleRectangle(image_xmin, image_ymin,
                                     image_xmax-image_xmin, image_ymax-image_ymin);
        result.add(new_info);
            // area 1:
        // System.out.println("  finding rectangles for area 1...");
        result = findVisibleImages(viewport_xmin,image_xmin,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 1...");
            // area 2:
        // System.out.println("  finding rectangles for area 2...");
        result = findVisibleImages(image_xmax,viewport_xmax,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 2...");
            // area 3:
        // System.out.println("  finding rectangles for area 3...");
        result = findVisibleImages(image_xmin,image_xmax,
                                viewport_ymin,image_ymin,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 3...");
            // area 4:
        // System.out.println("  finding rectangles for area 4...");
        result = findVisibleImages(image_xmin,image_xmax,
                                image_ymax,viewport_ymax,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 4...");
      }
      else
// Figure 02:
      if(image_xmin<viewport_xmin & image_xmax>viewport_xmin & image_xmax<viewport_xmax
         & image_ymin>viewport_ymin & image_ymax<viewport_ymax)
      {
        // System.out.println("figure 02 detected");
        image_found = true;
        ImageInfo new_info = new ImageInfo(info);
        new_info.setVisibleRectangle(viewport_xmin, image_ymin,
                                     image_xmax-viewport_xmin, image_ymax-image_ymin);
        result.add(new_info);
            // area 1:
        // System.out.println("  finding rectangles for area 1...");
        result = findVisibleImages(image_xmax,viewport_xmax,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 1...");
            // area 2:
        // System.out.println("  finding rectangles for area 2...");
        result = findVisibleImages(viewport_xmin,image_xmax,
                                viewport_ymin,image_ymin,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 2...");
            // area 3:
        // System.out.println("  finding rectangles for area 3...");
        result = findVisibleImages(viewport_xmin,image_xmax,
                                image_ymax,viewport_ymax,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 3...");
      }
      else
// Figure 03:
      if(image_xmin>viewport_xmin & image_xmin<viewport_xmax  & image_xmax>viewport_xmax
          & image_ymin>viewport_ymin & image_ymax<viewport_ymax)
      {
        // System.out.println("figure 03 detected");
        image_found = true;
        ImageInfo new_info = new ImageInfo(info);
        new_info.setVisibleRectangle(image_xmin, image_ymin,
                                     viewport_xmax-image_xmin, image_ymax-image_ymin);
        result.add(new_info);
            // area 1:
        // System.out.println("  finding rectangles for area 1...");
        result = findVisibleImages(viewport_xmin,image_xmin,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 1...");
            // area 2:
        // System.out.println("  finding rectangles for area 2...");
        result = findVisibleImages(image_xmin,viewport_xmax,
                                viewport_ymin,image_ymin,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 2...");
            // area 3:
        // System.out.println("  finding rectangles for area 3...");
        result = findVisibleImages(image_xmin,viewport_xmax,
                                image_ymax,viewport_ymax,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 3...");
      }
      else
// Figure 04:
      if(image_xmin>viewport_xmin & image_xmax<viewport_xmax
          & image_ymin<viewport_ymin & image_ymax>viewport_ymin & image_ymax<viewport_ymax)
      {
        // System.out.println("figure 04 detected");
        image_found = true;
        ImageInfo new_info = new ImageInfo(info);
        new_info.setVisibleRectangle(image_xmin, viewport_ymin,
                                     image_xmax-image_xmin, image_ymax-viewport_ymin);
        result.add(new_info);
            // area 1:
        // System.out.println("  finding rectangles for area 1...");
        result = findVisibleImages(viewport_xmin,image_xmin,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 1...");
            // area 2:
        // System.out.println("  finding rectangles for area 2...");
        result = findVisibleImages(image_xmin,image_xmax,
                                image_ymax,viewport_ymax,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 2...");
            // area 3:
        // System.out.println("  finding rectangles for area 3...");
        result = findVisibleImages(image_xmax,viewport_xmax,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 3...");
      }
      else
// Figure 05:
      if(image_xmin>viewport_xmin & image_xmax<viewport_xmax
          & image_ymin>viewport_ymin & image_ymin<viewport_ymax & image_ymax>viewport_ymax)
      {
        // System.out.println("figure 05 detected");
        image_found = true;
        ImageInfo new_info = new ImageInfo(info);
        new_info.setVisibleRectangle(image_xmin, image_ymin,
                                     image_xmax-image_xmin, viewport_ymax-image_ymin);
        result.add(new_info);
            // area 1:
        // System.out.println("  finding rectangles for area 1...");
        result = findVisibleImages(viewport_xmin,image_xmin,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 1...");
            // area 2:
        // System.out.println("  finding rectangles for area 2...");
        result = findVisibleImages(image_xmin,image_xmax,
                                viewport_ymin,image_ymin,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 2...");
            // area 3:
        // System.out.println("  finding rectangles for area 3...");
        result = findVisibleImages(image_xmax,viewport_xmax,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 3...");
      }
      else
// Figure 06:
      if(image_xmin<viewport_xmin & image_xmax>viewport_xmin & image_xmax<viewport_xmax
         & image_ymin<viewport_ymin & image_ymax>viewport_ymax)
      {
        // System.out.println("figure 06 detected");
        image_found = true;
        ImageInfo new_info = new ImageInfo(info);
        new_info.setVisibleRectangle(viewport_xmin, viewport_ymin,
                                     image_xmax-viewport_xmin, viewport_ymax-viewport_ymin);
        result.add(new_info);
            // area 1:
        // System.out.println("  finding rectangles for area 1...");
        result = findVisibleImages(image_xmax,viewport_xmax,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 1...");
      }
      else
// Figure 07:
      if(image_xmin>viewport_xmin & image_xmin<viewport_xmax  & image_xmax>viewport_xmax
         & image_ymin<viewport_ymin & image_ymax>viewport_ymax)
      {
        // System.out.println("figure 07 detected");
        image_found = true;
        ImageInfo new_info = new ImageInfo(info);
        new_info.setVisibleRectangle(image_xmin, viewport_ymin,
                                     viewport_xmax-image_xmin, viewport_ymax-viewport_ymin);
        result.add(new_info);
            // area 1:
        // System.out.println("  finding rectangles for area 1...");
        result = findVisibleImages(viewport_xmin,image_xmin,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 1...");
      }
      else
// Figure 08:
      if(image_xmin<viewport_xmin & image_xmax>viewport_xmax
         & image_ymin<viewport_ymin & image_ymax>viewport_ymin & image_ymax<viewport_ymax)
      {
        // System.out.println("figure 08 detected");
        image_found = true;
        ImageInfo new_info = new ImageInfo(info);
        new_info.setVisibleRectangle(viewport_xmin, viewport_ymin,
                                     viewport_xmax-viewport_xmin, image_ymax-viewport_ymin);
        result.add(new_info);
            // area 1:
        // System.out.println("  finding rectangles for area 1...");
        result = findVisibleImages(viewport_xmin,viewport_xmax,
                                image_ymax,viewport_ymax,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 1...");
      }
      else
// Figure 09:
      if(image_xmin<viewport_xmin & image_xmax>viewport_xmax
         & image_ymin>viewport_ymin & image_ymin<viewport_ymax & image_ymax>viewport_ymax)
      {
        // System.out.println("figure 09 detected");
        image_found = true;
        ImageInfo new_info = new ImageInfo(info);
        new_info.setVisibleRectangle(viewport_xmin, image_ymin,
                                     viewport_xmax-viewport_xmin, viewport_ymax-image_ymin);
        result.add(new_info);
            // area 1:
        // System.out.println("  finding rectangles for area 1...");
        result = findVisibleImages(viewport_xmin,viewport_xmax,
                                viewport_ymin,image_ymin,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 1...");
      }
      else
// Figure 10:
      if(image_xmin<=viewport_xmin & image_xmax>=viewport_xmax
         & image_ymin<=viewport_ymin & image_ymax>=viewport_ymax)
      {
        // System.out.println("figure 10 detected");
        image_found = true;
        ImageInfo new_info = new ImageInfo(info);
        new_info.setVisibleRectangle(viewport_xmin, viewport_ymin,
                                     viewport_xmax-viewport_xmin,
                                     viewport_ymax-viewport_ymin);
        result.add(new_info);
        return(result);
      }
      else
// Figure 11:
      if(image_xmin<viewport_xmin & image_xmax>viewport_xmin & image_xmax<viewport_xmax
         & image_ymin>viewport_ymin & image_ymin<viewport_ymax & image_ymax>viewport_ymax)
      {
        // System.out.println("figure 11 detected");
        image_found = true;
        ImageInfo new_info = new ImageInfo(info);
        new_info.setVisibleRectangle(viewport_xmin, image_ymin,
                                     image_xmax-viewport_xmin, viewport_ymax-image_ymin);
        result.add(new_info);
            // area 1:
        // System.out.println("  finding rectangles for area 1...");
        result = findVisibleImages(viewport_xmin,image_xmax,
                                viewport_ymin,image_ymin,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 1...");
            // area 2:
        // System.out.println("  finding rectangles for area 2...");
        result = findVisibleImages(image_xmax,viewport_xmax,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 2...");
      }
      else
// Figure 12:
      if(image_xmin>viewport_xmin & image_xmin<viewport_xmax  & image_xmax>viewport_xmax
         & image_ymin>viewport_ymin & image_ymin<viewport_ymax & image_ymax>viewport_ymax)
      {
        // System.out.println("figure 12 detected");
        image_found = true;
        ImageInfo new_info = new ImageInfo(info);
        new_info.setVisibleRectangle(image_xmin, image_ymin,
                                     viewport_xmax-image_xmin, viewport_ymax-image_ymin);
        result.add(new_info);
            // area 1:
        // System.out.println("  finding rectangles for area 1...");
        result = findVisibleImages(image_xmin,viewport_xmax,
                                viewport_ymin,image_ymin,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 1...");
            // area 2:
        // System.out.println("  finding rectangles for area 2...");
        result = findVisibleImages(viewport_xmin,image_xmin,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 2...");
      }
      else
// Figure 13:
      if(image_xmin<viewport_xmin & image_xmax>viewport_xmin & image_xmax<viewport_xmax
         & image_ymin<viewport_ymin & image_ymax>viewport_ymin & image_ymax<viewport_ymax)
      {
        // System.out.println("figure 13 detected");
        image_found = true;
        ImageInfo new_info = new ImageInfo(info);
        new_info.setVisibleRectangle(viewport_xmin, viewport_ymin,
                                     image_xmax-viewport_xmin, image_ymax-viewport_ymin);
        result.add(new_info);
            // area 1:
        // System.out.println("  finding rectangles for area 1...");
        result = findVisibleImages(viewport_xmin,image_xmax,
                                image_ymax,viewport_ymax,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 1...");
            // area 2:
        // System.out.println("  finding rectangles for area 2...");
        result = findVisibleImages(image_xmax,viewport_xmax,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 2...");

      }
      else
// Figure 14:
      if(image_xmin>viewport_xmin & image_xmin<viewport_xmax  & image_xmax>viewport_xmax
         & image_ymin<viewport_ymin & image_ymax>viewport_ymin & image_ymax<viewport_ymax)
      {
        // System.out.println("figure 14 detected");
        image_found = true;
        ImageInfo new_info = new ImageInfo(info);
        new_info.setVisibleRectangle(image_xmin, viewport_ymin,
                                     viewport_xmax-image_xmin, image_ymax-viewport_ymin);
        result.add(new_info);
            // area 1:
        // System.out.println("  finding rectangles for area 1...");
        result = findVisibleImages(image_xmin,viewport_xmax,
                                image_ymax,viewport_ymax,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 1...");
            // area 2:
        // System.out.println("  finding rectangles for area 2...");
        result = findVisibleImages(viewport_xmin,image_xmin,
                                viewport_ymin,viewport_ymax,
                                available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 2...");
      }
      else
// Figure 15:
      if(image_xmin>viewport_xmin & image_xmin <viewport_xmax  & image_xmax>viewport_xmin
         & image_xmax<viewport_xmax & image_ymin<viewport_ymin & image_ymax>viewport_ymax)
      {
        // System.out.println("figure 15 detected");
        image_found = true;
        ImageInfo new_info = new ImageInfo(info);
        new_info.setVisibleRectangle(image_xmin, viewport_ymin,
                                     image_xmax-image_xmin, viewport_ymax-viewport_ymin);
        result.add(new_info);
            // area 1:
        // System.out.println("  finding rectangles for area 1...");
        result = findVisibleImages(viewport_xmin,image_xmin,
                                   viewport_ymin,viewport_ymax,
                                   available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 1...");
            // area 2:
        // System.out.println("  finding rectangles for area 2...");
        result = findVisibleImages(image_xmax,viewport_xmax,
                                   viewport_ymin,viewport_ymax,
                                   available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 2...");
      }
      else
// Figure 16:
      if(image_xmin<viewport_xmin & image_xmax>viewport_xmax & image_ymin>viewport_ymin
         & image_ymin<viewport_ymax & image_ymax>viewport_ymin & image_ymax<viewport_ymax)
      {
        // System.out.println("figure 15 detected");
        image_found = true;
        ImageInfo new_info = new ImageInfo(info);
        new_info.setVisibleRectangle(viewport_xmin,image_ymin,
                                     viewport_xmax-viewport_xmin, image_ymax-image_ymin);
        result.add(new_info);
            // area 1:
        // System.out.println("  finding rectangles for area 1...");
        result = findVisibleImages(viewport_xmin,viewport_xmax,
                                   viewport_ymin,image_ymin,
                                   available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 1...");
            // area 2:
        // System.out.println("  finding rectangles for area 2...");
        result = findVisibleImages(viewport_xmin,viewport_xmax,
                                   image_ymax,viewport_ymax,
                                   available,result,empty_rect);
        // System.out.println("  returned from finding rectangles for area 2...");
      }
    }
    if(!image_found)
    {
      empty_rect.add(new Rectangle(viewport_xmin,viewport_ymin,
                                   viewport_xmax-viewport_xmin,viewport_ymax-viewport_ymin));
       // System.out.println("no image for rectangle found");
    }
    return(result);
  }


  public static void main(String[] args)
  {
      Vector available = new Vector();
      available.add(new ImageInfo("figure 00",-500,-500,400,300,1.0f));
//      available.add(new ImageInfo("figure 01",100,100,400,300,1.0f));
//      available.add(new ImageInfo("figure 02",-100,100,400,300,1.0f));
//       available.add(new ImageInfo("figure 03",500,100,400,300,1.0f));
//       available.add(new ImageInfo("figure 04",100,-100,300,300,1.0f));
//       available.add(new ImageInfo("figure 05",100,400,400,300,1.0f));
//       available.add(new ImageInfo("figure 06",-100,-100,400,600,1.0f));
//       available.add(new ImageInfo("figure 07",500,-100,400,600,1.0f));
//       available.add(new ImageInfo("figure 08",-100,-100,800,300,1.0f));
//       available.add(new ImageInfo("figure 09",-100,400,800,300,1.0f));
//       available.add(new ImageInfo("figure 10",-100,-100,800,600,1.0f));
      Vector result = new Vector();
      Vector empty_rect = new Vector();
      result = findVisibleImages(0,640,0,480,available,result,empty_rect);

      System.out.println("Images:");
      Iterator iterator = result.iterator();
      while(iterator.hasNext())
      {
        ImageInfo info = (ImageInfo)iterator.next();

        System.out.println(info);
      }

      System.out.println("Empty: ");
      iterator = empty_rect.iterator();
      while(iterator.hasNext())
      {
        Rectangle empty = (Rectangle)iterator.next();

        System.out.println(empty);
      }
  }


}

