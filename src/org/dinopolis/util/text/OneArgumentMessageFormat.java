/**
 *
 */
package org.dinopolis.util.text;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * Message format that is limited to one argument.
 * @author christof.dallermassl
 *
 */
public class OneArgumentMessageFormat extends MessageFormat
{
  public OneArgumentMessageFormat(String pattern)
  {
    super(pattern);
  }

  public OneArgumentMessageFormat(String pattern, Locale locale)
  {
    super(pattern,locale);
  }

  public String format(String argument)
  {
    System.out.println("OneArgumentMessageFormat: "+argument);
    return(format(new Object[] {argument}));
  }

  public String format(String pattern, String argument)
  {
    return(format(pattern, new Object[] {argument}));
  }

//----------------------------------------------------------------------
/**
* Pad a string to a maximal length with spaces at the end.
* @param string the string to pad.
* @param length the length for the final string (if the given string is longer,
* it is shortened to the given length).
*/
  public String pad(String string, int length)
  {
    return(pad(string,length,' ',false));
  }

//----------------------------------------------------------------------
/**
* Pad a string to a maximal length with a given character at the end.
* @param string the string to pad.
* @param length the length for the final string (if the given string is longer,
* it is shortened to the given length).
* @param pad_char the character to pad with.
*/
  public String pad(String string, int length, char pad_char)
  {
    return(pad(string,length,pad_char,false));
  }

//----------------------------------------------------------------------
/**
* Pad a string to a maximal length with a given character on the beginning or
*  on the end.
* @param string the string to pad.
* @param length the length for the final string (if the given string is longer,
* it is shortened to the given length).
* @param pad_char the character to pad with.
* @param pad_begin pad on the begin of the string, not at the end
*/
  public String pad(String string, int length, char pad_char, boolean pad_begin)
  {
    StringBuffer str = new StringBuffer(string);
    if(length > string.length())
    {
      do
      {
        if(pad_begin)
          str.insert(0,pad_char);
        else
          str.append(pad_char);
      }
      while(str.length() < length);
      return(str.toString());
    }
    else
    {
      return(string.substring(0,length));
    }
  }
}
