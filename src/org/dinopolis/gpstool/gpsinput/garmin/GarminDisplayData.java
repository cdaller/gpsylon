package org.dinopolis.gpstool.gpsinput.garmin;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import org.dinopolis.util.Debug;

public class GarminDisplayData
{
  int height_;
  int width_;
  int line_;
  BufferedImage image_;
  Graphics graphics_;
  int rotate_image_degrees_ = -90; // garmin eTrex data is wrong orientation, rotate degree clockwise
  Color[] colors_;

  
  public GarminDisplayData()
  {
  }

  public GarminDisplayData(GarminPackage garmin_package)
  {
    width_ = (int)garmin_package.getLong(16);
    height_ = (int)garmin_package.getLong(20);
    if(rotate_image_degrees_ != 0)
      image_ = new BufferedImage(height_,width_,BufferedImage.TYPE_INT_RGB);
    else
      image_ = new BufferedImage(width_,height_,BufferedImage.TYPE_INT_RGB);

        // colors: grey levels are byte 24,25,26,27 in display data:
    colors_  = new Color[4];
    int value;
    int grey_value;
    for(int color_index = 0; color_index < 4; color_index++)
    {
      value = garmin_package.getByte(color_index + 24);
      grey_value = value * 16;
      colors_[color_index] = new Color(grey_value,grey_value,grey_value);
    }
    
    graphics_ = image_.createGraphics();
    if(Debug.DEBUG && Debug.isEnabled("garmin_display_header"))
      Debug.println("garmin_display_header","first display data package: "+garmin_package+", info"+this);
  }

  public void addLine(GarminPackage garmin_package)
  {
    if(Debug.DEBUG && Debug.isEnabled("garmin_display_data"))
      Debug.println("garmin_display_data","next display data package: "+garmin_package);
    garmin_package.getNextAsLong(); // ignore this one (always 1??)
    long pixel_number = garmin_package.getNextAsLong();
//     System.out.println("pixel number "+ pixel_number);
//     System.out.println("pixel number/width "+ (pixel_number*4/width_));
    int y = ((int)pixel_number*4/width_);
    
    int x = 0;
    int value;

        // every byte (8 bit) contains 4 pixels (2bit each)
    for(int index_bytes = 0; index_bytes < width_/4; index_bytes++)
    {
      value = garmin_package.getNextAsByte();
      drawPixel(x,y,value & 0x03);
      drawPixel(x+1,y,(value >> 2) & 0x03);
      drawPixel(x+2,y,(value >> 4) & 0x03);
      drawPixel(x+3,y,(value >> 6) & 0x03);
      x += 4;
    }
    line_++;
  }


  protected void drawPixel(int x, int y, int value)
  {
    graphics_.setColor(colors_[value]);
    if(rotate_image_degrees_ == -90)
      graphics_.drawLine(y, width_-x-1, y, width_-x-1);
    else
      if(rotate_image_degrees_ == 90)
        graphics_.drawLine(height_-y-1, x, height_-y-1, x);
      else
        graphics_.drawLine(x,y,x,y);
  }


  public int getHeight()
  {
    return(height_);
  }

  public int getWidth()
  {
    return(width_);
  }

  public BufferedImage getImage()
  {
    return(image_);
  }

  
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("GarminDisplayData[");
    buffer.append("width=").append(width_);
    buffer.append(",height=").append(height_);
    buffer.append("]");
    return(buffer.toString());
  }
}
