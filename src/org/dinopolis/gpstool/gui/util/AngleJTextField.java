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

import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JTextField;

import org.dinopolis.gpstool.util.angle.Angle;
import org.dinopolis.gpstool.util.angle.AngleFormat;
import org.dinopolis.gpstool.util.angle.Latitude;
import org.dinopolis.gpstool.util.angle.Longitude;

//----------------------------------------------------------------------
/**
 * AngleJTextField is a JTextField that uses an AngleFormat to
 * validate the input. It may use different format strings to validate
 * the input of the user. In case the decimal separator of the used
 * locale is a comma, the validity is also checked with the dot
 * replaced by the comma. So in case of comma as a decimal separator,
 * "123.34" would also be accepted. The default angle formats are
 * "DD°MM'SS", DD°MM'SS, DD.ddddd°, DD°MM.mmmm'.
 * 
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class AngleJTextField extends JTextField
{
  Vector angle_formatters_ = new Vector();
  Vector valid_formats_ = new Vector();
  AngleFormat print_format_ = new AngleFormat("DD°MM'SS\"");

  protected String[] default_angle_formats_ = {"DD°MM'SS\"","DD°MM'SS\"","DD.ddddd°","DD°MM.mmmm'"};

  boolean decimal_separator_is_comma = false;
  
//----------------------------------------------------------------------
/**
 * Constructs a new <code>TextField</code>.  A default model is created,
 * the initial string is <code>null</code>,
 * and the number of columns is set to 0.
 */
  public AngleJTextField()
  {
    this(null);
  }

//----------------------------------------------------------------------
/**
 * Constructs a new <code>TextField</code> initialized with the
 * specified text. A default model is created and the number of
 * columns is 0.
 *
 * @param text the text to be displayed, or <code>null</code>
 */
  public AngleJTextField(String text)
  {
    super(text);
    decimal_separator_is_comma = (new DecimalFormatSymbols().getDecimalSeparator() == ',');
    for(int count = 0; count < default_angle_formats_.length; count++)
    {
      addValidAngleFormat(default_angle_formats_[count]);
    }
  }

//----------------------------------------------------------------------
/**
 * Sets the format to print angles.
 *
 * @param format the angle format to use to print the angles.
 * @throws IllegalArgumentException if the format is not correct.
 */
  public void setPrintFormat(String format)
  {
    print_format_ = new AngleFormat(format);
  }

//----------------------------------------------------------------------
/**
 * Adds a valid angle pattern to use to validate the input.
 *
 * @param format the angle pattern to use to validate the input.
 */
  public void addValidAngleFormat(String format)
  {
    synchronized(angle_formatters_)
    {
      angle_formatters_.add(new AngleFormat(format));
      valid_formats_.add(format);
    }
  }

//----------------------------------------------------------------------
/**
 * Adds valid angle patterns to use to validate the input.
 *
 * @param formats the angle patterns to use to validate the input.
 */
  public void addValidAngleFormats(String[] formats)
  {
    for(int count = 0; count < formats.length; count++)
    {
      addValidAngleFormat(formats[count]);
    }
  }

//----------------------------------------------------------------------
/**
 * Removes all angle formats used to check the validity.
 */
  public void clearValidAngleFormats()
  {
    synchronized(angle_formatters_)
    {
      angle_formatters_.clear();
      valid_formats_.clear();
    }
  }

//----------------------------------------------------------------------
/**
 * Returns all valid formats used to validate the input text.
 *
 * @return all valid formats used to validate the input text.
 */
  public String[] getValidAngleFormats()
  {
    return((String[])valid_formats_.toArray());
  }

// //----------------------------------------------------------------------
// /**
//  * Handles the actionevents of this textfield. It takes care, that the
//  * input is in valid angle format.
//  *
//  * @param event the actionevent
//  */
//   public void actionPerformed(ActionEvent event)
//   {
//     AngleJTextField input = (AngleJTextField)event.getSource();
//     String angle = input.getText();
    
//     if (isCorrectAngle(angle))
//     {
//       System.out.println("Success! You typed a correct angle.");
//     } else {
//       System.out.println("Invalid angle. Try again.");
//     }
//   }

//----------------------------------------------------------------------
/**
 * Checks the text that was input in the textfield for validity by the
 * use of all given angle formats. If no angle format parses the given
 * text correctly, <code>false</code> is returned, <code>true</code>
 * otherwise.
 *
 * @return true if the text is a valid angle, false otherwise.
 */
  public boolean isCorrectAngle()
  {
    return(getAngle() != null);
  }

//----------------------------------------------------------------------
/**
 * Returns an angle object, if the text input could be validated
 * against one of the given angle formats, <code>null</code>
 * otherwise. The text that was input can be obained in either way by
 * the use of the getText() method.
 *
 * @return an angle object, if the text input could be validated
 * against one of the given angle formats, <code>null</code>
 * otherwise. 
 */
  public Angle getAngle()
  {
    String angle_text = getText();
    if(angle_text == null)
      return(null);
    
    synchronized(angle_formatters_)
    {
      Iterator iterator = angle_formatters_.iterator();
      AngleFormat format;
      Angle angle;
      while(iterator.hasNext())
      {
        format = (AngleFormat)iterator.next();
        try
        {
          angle = format.parse(angle_text);
//          System.out.println("angle detected: "+angle);
          return(angle);
        }
        catch(ParseException pe)
        {
//          System.err.println("check failed: "+pe.getMessage());
        }
            // if the decimal separator is the comma, try to replace a
            // dot with the comma, so both variants are accepted:
        if(decimal_separator_is_comma)
        {
          try
          {
            angle = format.parse(angle_text.replace('.',','));
            return(angle);
          }
          catch(ParseException pe)
          {
//            System.err.println("check failed: "+pe.getMessage());
          }
        }
      }
    }
    return(null);
  }


//----------------------------------------------------------------------
/**
 * Sets the value as angle. The print formatter is used to format the
 * angle.
 *
 * @param angle the angle to set.
 */
  public void setAngle(double angle)
  {
    setText(print_format_.format(new Angle(angle)));
  }

//----------------------------------------------------------------------
/**
 * Sets the value as latitude. The print formatter is used to format the
 * angle.
 *
 * @param latitutde the latitude to set.
 */
  public void setAngleAsLatitude(double latitude)
  {
    setText(print_format_.format(new Latitude(latitude)));
  }

//----------------------------------------------------------------------
/**
 * Sets the value as longitude. The print formatter is used to format the
 * angle.
 *
 * @param longitude the longitude to set.
 */
  public void setAngleAsLongitude(double longitude)
  {
    setText(print_format_.format(new Longitude(longitude)));
  }
}


