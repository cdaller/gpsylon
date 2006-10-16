/***********************************************************************
 * @(#)$RCSfile$   $Revision$ $Date$
 *
 * Copyright (c) 2000 IICM, Graz University of Technology
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


package org.dinopolis.util.commandarguments;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


//---------------------------------------------------------------------
//---------------------------------------------------------------------
/**
 * CommandArguments takes care of command line arguments and options.
 * It checks, if arguments are valid and simplifies checking, if an
 * argument is set or not.
 * <P>
 * There are two different kinds of arguments: those starting with one
 * or two minus(es) ('-', '--') (called options here) and those
 * without. The first type is used to set options, those without are
 * 'real' arguments.
 * <P>
 * There are short and long options. Short options are only one
 * character long and need to be prefixed by a single minus
 * (<code>'-'</code>). When they are parsed, it makes no difference,
 * if they are written separately or all in one:
 * e.g. <code>-dfl</code> is equal to <code>-d -f -l</code>).
 * <P>
 * Long options (more than one letter) must be prefixed by two minuses
 * (<code>'--'</code>): e.g. <code>--recursive</code> or
 * <code>--long</code>.
 * <P>
 * There are options that stand just per se. Other options require to be
 * followed by a value (a number or a string). E.g. <code>--filename
 * /home/username/log.txt</code> or <code>--port 1234</code>.
 *
 * @author Christof Dallermassl <cdaller@iicm.edu>
 * @version $Id$
 *  */
public class CommandArguments
{

  Map args_;
  Map valid_args_;
  /** here, the arguments, which have no leading "-" or "--" are saved. */
  List real_arguments_;

//---------------------------------------------------------------------
/**
 * Convertes the Vector to a String[] and calls the other constructor.
 * @param args arguments given in a Vector.
 * @param valid_args description of valid arguments.
 * @exception CommandArgumentException thrown, if one of the given
 *   argument is not in valid_args.
 */
  public CommandArguments (List args, String[] valid_args)
    throws CommandArgumentException
  {
    this ((String[])args.toArray(new String[args.size()]),valid_args);
  }

//---------------------------------------------------------------------
/**
 * Format of valid_args:
 * <UL>
 * <li>'x' argument 'x' is valid
 * <li>'x#' argument 'x' is valid, when followed by a number (Integer).
 * <li>'x%' argument 'x' is valid, when followed by a number (Double).
 * <li>'x*' argument 'x' is valid, when followed by a string.
 * </UL>
 * If an argument requires an additional argument (String or number),
 * it must be separated by a whitespace for long and short options or
 * can be appended directly only for short options (starting with
 * '-').
 * @param args tokenized commandline (only the arguments) as String[].
 * @param valid_args description of valid arguments.
 * @exception CommandArgumentException thrown, if one of the given
 *   arguments is not in valid_args.  */
  public CommandArguments (String[] args, String[] valid_args)
    throws CommandArgumentException
  {
    valid_args_ = new TreeMap ();
    args_ = new TreeMap ();
    real_arguments_ = new ArrayList ();

    String valid_arg;
    int last_pos;
        // extract the names of the valid arguments:
    for (int count = 0; count < valid_args.length ; count ++)
    {
      valid_arg = valid_args[count];
      last_pos = valid_arg.length()-1;
      if (last_pos >= 0)
      {
        if ((valid_arg.charAt(last_pos) == '#')
            || (valid_arg.charAt(last_pos) == '%')
            || (valid_arg.charAt(last_pos) == '*'))
          valid_args_.put (valid_arg.substring(0,last_pos),
                           new Integer (valid_arg.charAt(last_pos)));
        else
          valid_args_.put (valid_arg,new Integer (0));
      }
    }
//        if (DDebug.DEBUG)
//          DDebug.println ("shell","args","CommandArgs valid_map="+valid_args_);

    String given_argument;
    String value;
    String argument;
    int token, len;
    for (int arg_count = 0; arg_count < args.length ; arg_count ++)
    {
      given_argument = (String)args[arg_count];
      if (given_argument.startsWith ("--"))
      {
        argument = given_argument.substring(2);
        if (!valid_args_.containsKey(argument))
          throw new InvalidCommandArgumentException ("Argument '"
                                 +argument+"' is not valid.",arg_count);

        token = ((Integer)valid_args_.get(argument)).intValue();
        if (token == 0)   // no additional info necessary
          args_.put (argument,null);
        else
        {
          try
          {
            value = (String)args[++arg_count];
            if (token == '*')  // String
              args_.put (argument,value);
            else
              if (token == '#') // Integer
                args_.put (argument,new Integer(value));
              else
                if (token == '%') // Double
                  args_.put (argument,new Double(value));
          }
          catch (NumberFormatException nfe)
          {
            throw new InvalidCommandArgumentFormatException (
              "Invalid number format given for argument '"+argument
              +"'.",arg_count-1);
          }
          catch (ArrayIndexOutOfBoundsException aiobe)
          {
            throw new InvalidCommandArgumentFormatException (
              "Missing value for argument '"+argument+"'.",arg_count-1);
          }
        }
      }
      else
      if (given_argument.startsWith ("-"))
      {
        len = given_argument.length();
        for (int count_char = 1; count_char < len; count_char ++)
        {
          argument = new String (new char[] {given_argument.charAt(count_char)});
          if (!valid_args_.containsKey(argument))
            throw new InvalidCommandArgumentException (
              "Argument '"+argument+"' is not valid.",arg_count);

          token = ((Integer)valid_args_.get(argument)).intValue();
          if (token == 0)
            args_.put (argument,null);  // no additional info necessary
          else
          {
            try
            {
              // last character in argument -> take next argument as value
              if (count_char == len-1)
                value = args[++arg_count];
//                args_.put (argument,args[++arg_count]);
              else  // take rest of argument as value
              {
                value = given_argument.substring(count_char+1);
//                args_.put (argument,given_argument.substring(count_char+1));
                count_char = len; // do not continue with this argument
              }
              if (token == '*')  // String
                args_.put (argument,value);
              else
                if (token == '#') // Integer
                  args_.put (argument,new Integer(value));
                else
                  if (token == '%') // Double
                    args_.put (argument,new Double(value));
            }
            catch (NumberFormatException nfe)
            {
              throw new InvalidCommandArgumentFormatException (
                "Invalid number format given for argument '"+argument
                +"'.",arg_count-1);
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
              throw new InvalidCommandArgumentFormatException(
                "Missing value for argument '"+argument+"'.",arg_count-1);
            }
          }
        }
      }
      else
        real_arguments_.add(given_argument);
    }
  }

//---------------------------------------------------------------------
/**
 * Returns <code>true</code> when the given option (not argument!) was
 * specified.
 *
 * @param option_name the name of the option to be checked.
 * @return <code>true</code> when the given option (not argument!) was
 * specified.
 */
  public boolean isSet(String option_name)
  {
    return (args_.containsKey(option_name));
  }

//---------------------------------------------------------------------
/**
 * Returns the value (String or Integer) of the given option or
 * <code>null</code> if the option was not set.
 *
 * @param option_name the name of the option to be checked.
 * @return the value (String or Integer) of the given optionor
 * <code>null</code> if the option was not set.
 */
  public Object getValue(String option_name)
  {
    return(args_.get(option_name));
  }

//---------------------------------------------------------------------
/**
 * Returns the value of the given option or
 * <code>null</code> if the option was not set.
 *
 * @param option_name the name of the option to be checked.
 * @return the value (String or Integer) of the given optionor
 * <code>null</code> if the option was not set.
 */
  public String getStringValue(String option_name)
  {
    return((String)args_.get(option_name));
  }

//---------------------------------------------------------------------
/**
 * Returns the value of the given option or
 * <code>null</code> if the option was not set.
 *
 * @param option_name the name of the option to be checked.
 * @return the value (String or Integer) of the given optionor
 * <code>null</code> if the option was not set.
 */
  public Integer getIntegerValue(String option_name)
  {
    return((Integer)args_.get(option_name));
  }

//---------------------------------------------------------------------
/**
 * Returns the value (String or Integer) of the given option or
 * <code>null</code> if the option was not set.
 *
 * @param option_name the name of the option to be checked.
 * @return the value (String or Integer) of the given optionor
 * <code>null</code> if the option was not set.
 */
  public Double getDoubleValue(String option_name)
  {
    return((Double)args_.get(option_name));
  }

//---------------------------------------------------------------------
/**
 * Returns the argument (not option!) at the given position.
 *
 * @param position the position of the argument (not option!).
 * @return the argument at the given position.
 */
  public String getArgumentAt(int position)
  {
    return (String)real_arguments_.get(position);
  }

//---------------------------------------------------------------------
/**
 * Returns all argument (not options!).
 *
 * @return all argument (not options!).
 */
  public List getArguments()
  {
    return new ArrayList(real_arguments_);
  }

//---------------------------------------------------------------------
/**
 * Returns the number of arguments (not options!).
 *
 * @return the number of arguments (not options!).
 */
  public int length()
  {
    return (real_arguments_.size());
  }
}










