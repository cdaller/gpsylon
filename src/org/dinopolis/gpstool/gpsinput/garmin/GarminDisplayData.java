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
	int bit_per_pixel_;
	int pixel_per_byte_;
	int bit_mask_;
  BufferedImage image_;
  Graphics graphics_;
  int rotate_image_degrees_;
  Color[] colors_;

  
//----------------------------------------------------------------------
/**
 * Default Construtor.
 */
  public GarminDisplayData()
  {
  }

//----------------------------------------------------------------------
/**
 * Create a new Garmin Display Data object by the use of the header package.
 * @param garmin_package the header package to init the display data
 */

  public GarminDisplayData(GarminPackage garmin_package)
  {
    if(Debug.DEBUG && Debug.isEnabled("garmin_display_header"))
      Debug.println("garmin_display_header","first display data package: "+garmin_package);
    width_ = (int)garmin_package.getLong(16);
    height_ = (int)garmin_package.getLong(20);

		rotate_image_degrees_ = guessOrientation(garmin_package);

    if((rotate_image_degrees_ != 0) && (rotate_image_degrees_ != 180))
      image_ = new BufferedImage(height_,width_,BufferedImage.TYPE_INT_RGB);
    else
      image_ = new BufferedImage(width_,height_,BufferedImage.TYPE_INT_RGB);

        // colors: grey levels are byte 24,25,26,27 in display data:
    colors_  = new Color[4];
    int value;
    int grey_value;
		int num_colors = 0;
    for(int color_index = 0; color_index < 4; color_index++)
    {
      value = garmin_package.getByte(color_index + 24);

					// garmin indicates "no color" by value 255 
					// (e.g. black/white displays have 255 as
					// 3rd and 4th color)
			if (value < 255)
			{
				grey_value = value * 16;
				colors_[color_index] = new Color(grey_value,grey_value,grey_value);
				num_colors++;
			}
			else
				colors_[color_index] = new Color(0,0,0);
    }
    
    graphics_ = image_.createGraphics();
		bit_per_pixel_ = (int)(Math.log(num_colors)/Math.log(2));
		pixel_per_byte_ = 8/bit_per_pixel_;
		bit_mask_ = (int)Math.pow(2,bit_per_pixel_) - 1;

// 		System.out.println("bit mask"+bit_mask_);
// 		System.out.println("bit per pixel"+bit_per_pixel_);
// 		System.out.println("pixel per byte"+pixel_per_byte_);

    if(Debug.DEBUG && Debug.isEnabled("garmin_display_header"))
      Debug.println("garmin_display_header","first display data package: "+this);
  }

//----------------------------------------------------------------------
/**
 * Add a line to the display data using the given garmin package.
 * @param garmin_package the data package holding the next line.
 */
  public void addLine(GarminPackage garmin_package)
  {
    if(Debug.DEBUG && Debug.isEnabled("garmin_display_data"))
      Debug.println("garmin_display_data","next display data package: "+garmin_package);
    garmin_package.getNextAsLong(); // ignore this one (always 1??)
    long pixel_number = garmin_package.getNextAsLong();

				// determine line (could use variable line_, but this seems safer!):
    int y = ((int)pixel_number/(width_/pixel_per_byte_));  
//		System.out.println("drawing in line: "+y);
    int x = 0;
    int value;

    // first long is ignored, second is pixel number
		int data_bytes_available = garmin_package.getPackageSize()-8; 
		int max_byte_index = width_/pixel_per_byte_;
		if(data_bytes_available < max_byte_index)
		{
			System.err.println("WARNING: not enough package data available for image line\n"
												 +"some details may be missing:");
			System.err.println("  bytes available = "+data_bytes_available);
			System.err.println("  desired bytes = "+max_byte_index);
		}

		int pixel_value;
		for(int index_bytes = 0; index_bytes < max_byte_index; index_bytes++)
		{
      value = garmin_package.getNextAsByte();
			for(int pixel_per_byte_count = 0; pixel_per_byte_count < pixel_per_byte_; pixel_per_byte_count++)
			{
				pixel_value = (value >> (pixel_per_byte_count * bit_per_pixel_)) & bit_mask_;
				drawPixel(x,y,pixel_value);
//				System.out.print(x+"/"+pixel_value+", ");
				x++;
			}
//       drawPixel(x++,y,value & 0x03);
//       drawPixel(x++,y,(value >> 2) & 0x03);
//       drawPixel(x++,y,(value >> 4) & 0x03);
//       drawPixel(x++,y,(value >> 6) & 0x03);
		}
    line_++;
  }


//----------------------------------------------------------------------
/**
 * Sets the pixel to the given coordinates. This method respects the 
 * rotation given in the header. 
 * @param x: the x coordinate (before the rotation)
 * @param y: the y coordinate (before the rotation)
 * @param value: the color value for the given pixel
 */
  protected void drawPixel(int x, int y, int value)
  {
    graphics_.setColor(colors_[value]);
    if(rotate_image_degrees_ == -90)
      graphics_.drawLine(y, width_-x-1, y, width_-x-1);
    else if(rotate_image_degrees_ == 90)
			graphics_.drawLine(height_-y-1, x, height_-y-1, x);
		else
			graphics_.drawLine(x,y,x,y);
  }


//----------------------------------------------------------------------
/**
 * Guess the orientation depending on some values in the package. This is more or less a guess, as the
 * detailed information about the garmin protocol is unknown!!!
 * @param garmin_package: the header package
 * @return the angle the image should be rotated (0,-90 (counter clockwise),
 * 90 (clockwise),180).
 */
  protected int guessOrientation(GarminPackage garmin_package)
  {
				// some special treatment to set the orientation (no general concept found!): This is a bad
				// hack!!! I need more data to find out about different values in different garmin devices.
		int byte5 = (int)garmin_package.getByte(5);
		int byte8 = (int)garmin_package.getByte(8);

		if((byte5 == 0) && (byte8 == 76))  // eTrex legend
		{
			System.err.println("INFO: probably eTrex Legend detected, if orientation of image is wrong, please contact the authors!");
			return(-90);
		}
		else if((byte5 == 0) && (byte8 == 40))  // eMap
		{
			System.err.println("INFO: probably eMap legend detected, if orientation of image is wrong, please contact the authors!");
			return(-90);
		}
		else if((byte5 == 1) && (byte8 == 32))  // eTrex summit
		{
			System.err.println("INFO: probably eTrex Summit detected, if orientation of image is wrong, please contact the authors!");
			return(90);
		}
		else if((byte5 == 0) && (byte8 == 8))  // geko201
		{
			System.err.println("INFO: probably Geko detected, if orientation of image is wrong, please contact the authors!");
			return(0);
		}
		else
		{
			rotate_image_degrees_ = -90;  // default (no reason why, just a feeling :-(
			System.err.println("INFO: no idea which device, please contact the author to help to find out more about the garmin protocol!");
			return(-90);
		}
  }


//----------------------------------------------------------------------
/**
 * Returns the height of the image.
 * @return the height of the image.
 */
  public int getHeight()
  {
    return(height_);
  }

//----------------------------------------------------------------------
/**
 * Returns the width of the image.
 * @return the width of the image.
 */
  public int getWidth()
  {
    return(width_);
  }

//----------------------------------------------------------------------
/**
 * Returns the image.
 * @return the image.
 */
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
    buffer.append(",bit per pixel=").append(bit_per_pixel_);
    buffer.append(",rotate=").append(rotate_image_degrees_);
    buffer.append("]");
    return(buffer.toString());
  }

	public static void main(String[] args)
	{
		try
		{
			if(args.length < 1)
			{
				System.out.println("need to give a filename to read package data from!");
				return;
			}
			
			org.dinopolis.util.io.Tokenizer tokenizer = 
				new org.dinopolis.util.io.Tokenizer(new java.io.FileInputStream(args[0]));
			tokenizer.setDelimiters(" ");
			java.util.List tokens;
			tokens = tokenizer.nextLine();
			GarminPackage header = new GarminPackage(69,tokens.size());
			for(int index = 0; index < tokens.size(); index++)
			{
				header.put(Integer.parseInt((String)tokens.get(index)));
			}
			GarminDisplayData display_data = new GarminDisplayData(header);
			while(tokenizer.hasNextLine())
			{
				tokens = tokenizer.nextLine();
				GarminPackage data = new GarminPackage(69,tokens.size());
				for(int index = 0; index < tokens.size(); index++)
				{
					data.put(Integer.parseInt((String)tokens.get(index)));
				}
				display_data.addLine(data);
			}
			BufferedImage image = display_data.getImage();
			java.io.FileOutputStream out = new java.io.FileOutputStream("image.png");
			javax.imageio.ImageIO.write(image,"PNG",out);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
