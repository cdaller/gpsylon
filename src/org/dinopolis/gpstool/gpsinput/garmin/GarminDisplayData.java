package org.dinopolis.gpstool.gpsinput.garmin;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import org.dinopolis.util.Debug;

public class GarminDisplayData
{
  int height_;
  int width_;
  int line_;
  BufferedImage image_;
  Graphics graphics_;
  boolean rotate_image_ = true; // garmin eTrex data is wrong orientation
  final static Color COLORS[] = new Color[] {new Color(255,255,255),new Color(170,170,170),
                                             new Color(85,85,85), new Color(0,0,0)};

  
  public GarminDisplayData()
  {
  }

  public GarminDisplayData(GarminPackage garmin_package)
  {
    width_ = (int)garmin_package.getLong(16);
    height_ = (int)garmin_package.getLong(20);
    if(rotate_image_)
      image_ = new BufferedImage(height_,width_,BufferedImage.TYPE_INT_RGB);
    else
      image_ = new BufferedImage(width_,height_,BufferedImage.TYPE_INT_RGB);
    
    graphics_ = image_.createGraphics();
    if(Debug.DEBUG && Debug.isEnabled("garmin_display_data"))
      Debug.println("garmin_display_data","first display data package: "+garmin_package+", info"+this);
  }

  public void addLine(GarminPackage garmin_package)
  {
    if(Debug.DEBUG && Debug.isEnabled("garmin_display_data"))
      Debug.println("garmin_displaydata","next display data package: "+garmin_package);
    garmin_package.getNextAsLong(); // ignore this one (always 1??)
    long pixel_number = garmin_package.getNextAsLong();
    
    int x = 0;
    int value;

        // every byte (8 bit) contains 4 pixels (2bit each)
    for(int index_bytes = 0; index_bytes < width_/4; index_bytes++)
    {
      value = garmin_package.getNextAsByte();
      drawPixel(x,line_,value & 0x03);
      drawPixel(x+1,line_,(value >> 2) & 0x03);
      drawPixel(x+2,line_,(value >> 4) & 0x03);
      drawPixel(x+3,line_,(value >> 6) & 0x03);
      x += 4;
    }
    line_++;
  }


  protected void drawPixel(int x, int y, int value)
  {
    graphics_.setColor(COLORS[value]);
    if(rotate_image_)
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
