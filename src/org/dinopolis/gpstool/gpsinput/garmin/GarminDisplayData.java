/***********************************************************************
 * @(#)$RCSfile$   $Revision$$Date$
 *
 * Copyright (c) 2001-2003 Sandra Brueckler, Stefan Feitl
 * Written during an XPG-Project at the IICM of the TU-Graz, Austria
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

package org.dinopolis.gpstool.gpsinput.garmin;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

//----------------------------------------------------------------------
/**
 * This class describes the display data and creates a BufferedImage that may be saved or
 * displayed. There are (up to now) three different data packets:
 * <ul>
 * <li>Header</li>
 * <li>Color definitions (only on color devices)</li>
 * <li>Image data </li>
 * </ul>
 * The first packet (the header) holds the following information:
 * <pre>
 * example eTrex Legend:
 * 
 * 0: ??? (long): 0 0 0 0
 * 4: ??? (long): 1 0 1 1
 * 8: bytes per line (long): 76 0 0 0
 * 12: color depth in bit (byte): 2
 * 13: ??? (byte): 0
 * 14: ??? (byte): 0
 * 15: ??? (byte): 0
 * 16: width (long): 48 1 0 0 = 304 (should be 288!)
 * 20: height (long): 160 0 0 0
 * 24: grayvalue color 1? (byte): 15
 * 25: grayvalue color 2? (byte): 7
 * 26: grayvalue color 3? (byte): 8
 * 27: grayvalue color 4? (byte): 0
 * 28: ??? (long): 0 1 0 0  // same as eMap
 * 32: ??? (long): 200 13 1 0
 * 36: ??? (long): 1 0 0 0  // same as eMap
 * </pre>
 * On color devices (e.g. Streetpilot III) 16 color information packets follow:
 * <pre>
 * id=69,size=11,data=[2 0 0 0 0 0 0 0 128 128 128]
 * id=69,size=11,data=[2 0 0 0 3 0 0 0 0 0 0]
 * id=69,size=11,data=[2 0 0 0 6 0 0 0 0 0 160]
 * id=69,size=11,data=[2 0 0 0 9 0 0 0 0 0 240]
 * id=69,size=11,data=[2 0 0 0 12 0 0 0 64 128 32]
 * id=69,size=11,data=[2 0 0 0 15 0 0 0 0 240 0]
 * id=69,size=11,data=[2 0 0 0 18 0 0 0 0 128 240]
 * id=69,size=11,data=[2 0 0 0 21 0 0 0 0 240 240]
 * id=69,size=11,data=[2 0 0 0 24 0 0 0 128 0 0]
 * id=69,size=11,data=[2 0 0 0 27 0 0 0 240 0 0]
 * id=69,size=11,data=[2 0 0 0 30 0 0 0 128 0 112]
 * id=69,size=11,data=[2 0 0 0 33 0 0 0 240 0 240]
 * id=69,size=11,data=[2 0 0 0 36 0 0 0 160 160 0]
 * id=69,size=11,data=[2 0 0 0 39 0 0 0 240 192 0]
 * id=69,size=11,data=[2 0 0 0 42 0 0 0 240 240 240]
 * id=69,size=11,data=[2 0 0 0 45 0 0 0 160 160 160]
 * 
 * those define the colormap:
 * 
 * 0: data type (long): 2 0 0 0 = color
 * 4: color index * 3 (long): x 0 0 0
 * 8: blue (byte): 128
 * 9: green (byte): 128
 * 10: red (byte): 128
 * </pre>
 *
 * Right after (the color packets or the header, depending on the device), the image data follows: 
 * <pre>
 * data from Streetpilot III:
 *
 * first line, first part:
 * id=69,size=136,data=[1 0 0 0 0 0 0 0 119 119 119 119 71 119 119 119
 * 119 119 119 119 23 23 119 119 119 81 119 119 119 119 68 119 119 119
 * 119 119 119 119 119 119 119 119 119 119 71 119 119 119 119 119 119 119
 * 119 119 119 71 119 119 119 119 116 119 119 119 119 119 119 119 119 119
 * 119 119 119 119 119 119 119 119 119 119 119 119 119 23 113 113 23 113
 * 119 119 119 119 119 119 119 119 23 119 119 119 119 119 116 119 119 119
 * 119 119 119 119 119 119 119 119 71 119 119 119 119 119 119 119 23 17
 * 17 119 113 119 119 21 17 85 119 119 119 119 ]
 * 
 * first line, second part (start at byte 128 which is x=256):
 * id=69,size=36,data=[1 0 0 0 128 0 0 0 119 119 119 119 119 119 119 119
 * 119 119 119 119 119 119 119 119 119 119 119 119 119 119 23 119 23 17
 * 17 17 ]
 * 
 * second line, first part:
 * id=69,size=136,data=[1 0 0 0 156 0 0 0 119 119 119 119 71 119 119 119
 * 119 119 119 119 23 119 17 23 17 119 116 119 119 68 119 119 119 119 119
 * 119 119 119 119 119 119 119 119 119 119 116 119 119 119 119 119 119
 * 119 119 119 116 119 119 119 119 71 119 119 119 119 119 119 119 119 119
 * 119 119 119 119 119 119 119 119 119 119 119 119 119 113 23 23 113 87
 * 21 119 119 119 119 119 119 119 119 113 119 119 119 119 116 119 119 119
 * 119 119 119 119 119 119 119 119 119 119 119 119 119 119 119 119 23 119
 * 119 119 17 17 17 117 81 87 119 119 119 119 ]
 * 
 * 0: data type (long): 1 0 0 0 = image data
 * 4: byte offset (long): 128 0 0 0
 * 8: image data (4 bit per pixel giving the index in the color table)
 * 
 * the third packet has 156 as byte offset, the fourth 284.
 * to calculate the start coordinates of a packet:
 * x = (byte_offset / bytes_per_line) * pixel_per_byte // / is an integer division!
 * y = (byte_offset % bytes_per_line)                  // % is modulo
 *	(pixel_per_byte_ = 8/bit_per_pixel_)
 * </pre>
 */

public class GarminDisplayData
{
  int height_;
  int width_;
  int bytes_per_line_;
  int bit_per_pixel_;
  int pixel_per_byte_;
  int bit_mask_;
  BufferedImage image_;
  Graphics graphics_;
  int rotate_image_degrees_;
  Color[] colors_;
  private static Logger logger_ = Logger.getLogger(GarminDisplayData.class);
  
//----------------------------------------------------------------------
/**
 * Default Construtor.
 */
  public GarminDisplayData()
  {
  }

//----------------------------------------------------------------------
/**
 * Create a new Garmin Display Data object by the use of the header packet.
 * @param garmin_packet the header packet to init the display data
 */

  public GarminDisplayData(GarminPacket garmin_packet) 
  {
    if (logger_.isDebugEnabled())
      logger_.debug("first display data packet: " + garmin_packet);
    bytes_per_line_ = (int) garmin_packet.getLongWord(8);
    bit_per_pixel_ = garmin_packet.getByte(12);
    width_ = (int) garmin_packet.getLongWord(16);
    height_ = (int) garmin_packet.getLongWord(20);

    rotate_image_degrees_ = guessOrientation(garmin_packet);

    if ((rotate_image_degrees_ != 0) && (rotate_image_degrees_ != 180))
      image_ = new BufferedImage(height_, width_, BufferedImage.TYPE_INT_RGB);
    else
      image_ = new BufferedImage(width_, height_, BufferedImage.TYPE_INT_RGB);

    // colors: grey levels are byte 24,25,26,27 in display data:
    int num_colors = (int) Math.pow(2, bit_per_pixel_);
    colors_ = new Color[num_colors];
    int value;
    int grey_value;
    for (int color_index = 0; color_index < num_colors; color_index++) 
    {
      if (num_colors == 16) 
      {
        // color are defined in data packets (first long is 2)
      } else {
        value = garmin_packet.getByte(color_index + 24);
        grey_value = value * 16;
        colors_[color_index] = new Color(grey_value, grey_value, grey_value);
      }
    }

    graphics_ = image_.createGraphics();
    // bit_per_pixel_ = (int)(Math.log(num_colors)/Math.log(2));
    pixel_per_byte_ = 8 / bit_per_pixel_;
    bit_mask_ = (int) Math.pow(2, bit_per_pixel_) - 1;

    // System.out.println("bit mask: "+bit_mask_);
    // System.out.println("bit per pixel: "+bit_per_pixel_);
    // System.out.println("pixel per byte: "+pixel_per_byte_);

    if(logger_.isDebugEnabled())
      logger_.debug("first display data packet: " + this);
  }

  // ----------------------------------------------------------------------
  /**
   * Add a line to the display data using the given garmin packet.
   * 
   * @param garmin_packet the data packet holding the next line.
   */
  public void addData(GarminPacket garmin_packet) {
    if(logger_.isDebugEnabled())
      logger_.debug("next display data packet: " + garmin_packet);

    long data_type = garmin_packet.getNextAsLongWord();

    if (data_type == 1) // image data
    {
      long byte_offset = garmin_packet.getNextAsLongWord();

      // determine start x/y:
      int x = (int) (byte_offset % bytes_per_line_) * pixel_per_byte_;
      int y = (int) (byte_offset / bytes_per_line_);
      // System.out.println("starting drawing at x="+x+",y="+y);
      int value;

      // first long is ignored, second is pixel number
      int data_bytes_available = garmin_packet.getPacketSize() - 8;
      int pixel_value;
      for (int index_bytes = 0; index_bytes < data_bytes_available; index_bytes++) {
        value = garmin_packet.getNextAsByte();
        for (int pixel_per_byte_count = 0; pixel_per_byte_count < pixel_per_byte_; pixel_per_byte_count++) {
          pixel_value = (value >> (pixel_per_byte_count * bit_per_pixel_)) & bit_mask_;
          drawPixel(x, y, pixel_value);
          // System.out.print(x+"/"+y+":"+pixel_value+", ");
          x++;
        }
      }
    } else if (data_type == 2) // color info
    {
      long color_index = garmin_packet.getNextAsLongWord() / 3;
      int blue = garmin_packet.getNextAsByte();
      int green = garmin_packet.getNextAsByte();
      int red = garmin_packet.getNextAsByte();
      colors_[(int) color_index] = new Color(red, green, blue);
      // System.out.println("color["+color_index+"]="+colors_[(int)color_index]);
    }

  }

  // ----------------------------------------------------------------------
  /**
   * Sets the pixel to the given coordinates. This method respects the rotation given in the header.
   * 
   * @param x the x coordinate (before the rotation)
   * @param y the y coordinate (before the rotation)
   * @param value the color value for the given pixel
   */
  protected void drawPixel(int x, int y, int value) {
    graphics_.setColor(colors_[value]);
    if (rotate_image_degrees_ == -90)
      graphics_.drawLine(y, width_ - x - 1, y, width_ - x - 1);
    else if (rotate_image_degrees_ == 90)
      graphics_.drawLine(height_ - y - 1, x, height_ - y - 1, x);
    else
      graphics_.drawLine(x, y, x, y);
  }

  // ----------------------------------------------------------------------
  /**
   * Guess the orientation depending on some values in the packet. This is more or less a guess, as
   * the detailed information about the garmin protocol is unknown!!!
   * 
   * @param garmin_packet the header packet
   * @return the angle the image should be rotated (0,-90 (counter clockwise), 90 (clockwise),180).
   */
  protected int guessOrientation(GarminPacket garmin_packet) {
    // some special treatment to set the orientation (no general concept found!): This is a bad
    // hack!!! I need more data to find out about different values in different garmin devices.
    int byte5 = (int) garmin_packet.getByte(5);
    int byte8 = (int) garmin_packet.getByte(8);

    if ((byte5 == 0) && (byte8 == 76)) // eTrex legend
    {
      System.err.println("INFO: probably eTrex Legend detected, if orientation of image is wrong, please contact the authors!");
      return (-90);
    } else if ((byte5 == 0) && (byte8 == 40)) // eMap
    {
      System.err.println("INFO: probably eMap legend detected, if orientation of image is wrong, please contact the authors!");
      return (-90);
    } else if ((byte5 == 1) && (byte8 == 32)) // eTrex summit
    {
      System.err.println("INFO: probably eTrex Summit detected, if orientation of image is wrong, please contact the authors!");
      return (90);
    } else if ((byte5 == 0) && (byte8 == 8)) // geko201
    {
      System.err.println("INFO: probably Geko detected, if orientation of image is wrong, please contact the authors!");
      return (0);
    } else {
      System.err.println("INFO: no idea which device, please contact the author to help to find out more about the garmin protocol!");
      return (0); // default (no reason why, just a feeling :-(
    }
  }

  // ----------------------------------------------------------------------
  /**
   * Returns the height of the image.
   * 
   * @return the height of the image.
   */
  public int getHeight() {
    return (height_);
  }

  // ----------------------------------------------------------------------
  /**
   * Returns the width of the image.
   * 
   * @return the width of the image.
   */
  public int getWidth() {
    return (width_);
  }

  // ----------------------------------------------------------------------
  /**
   * Returns the image.
   * 
   * @return the image.
   */
  public BufferedImage getImage() {
    return (image_);
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("GarminDisplayData[");
    buffer.append("width=").append(width_);
    buffer.append(",height=").append(height_);
    buffer.append(",bit per pixel=").append(bit_per_pixel_);
    buffer.append(",rotate=").append(rotate_image_degrees_);
    buffer.append("]");
    return (buffer.toString());
  }

  public static void main(String[] args) {
    try {
      if (args.length < 1) {
        System.out.println("need to give a filename to read packet data from!");
        return;
      }

      org.dinopolis.util.io.Tokenizer tokenizer = new org.dinopolis.util.io.Tokenizer(new java.io.FileInputStream(args[0]));
      tokenizer.setDelimiters(" ");
      java.util.List tokens;
      tokens = tokenizer.nextLine();
      GarminPacket header = new GarminPacket(69, tokens.size());
      for (int index = 0; index < tokens.size(); index++) {
        header.put(Integer.parseInt((String) tokens.get(index)));
      }
      GarminDisplayData display_data = new GarminDisplayData(header);
      while (tokenizer.hasNextLine()) {
        tokens = tokenizer.nextLine();
        GarminPacket data = new GarminPacket(69, tokens.size());
        for (int index = 0; index < tokens.size(); index++) {
          data.put(Integer.parseInt((String) tokens.get(index)));
        }
        display_data.addData(data);
      }
      BufferedImage image = display_data.getImage();
      java.io.FileOutputStream out = new java.io.FileOutputStream("image.png");
      javax.imageio.ImageIO.write(image, "PNG", out);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
