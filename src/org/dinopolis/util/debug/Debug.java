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


package org.dinopolis.util.debug;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;


import java.util.Vector;
import java.util.Properties;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.StringTokenizer;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import org.dinopolis.util.commandarguments.CommandArguments;
import org.dinopolis.util.commandarguments.CommandArgumentException;
import org.dinopolis.util.commandarguments.InvalidCommandArgumentException;

//---------------------------------------------------------------------
//---------------------------------------------------------------------
/**
 * Debug helps the programmer to print various debug messages.  <P> If
 * this class is only used by the {@link #getInstance() getInstance}
 * method, it can be used as a singleton. So the instance of this
 * class must not known everywhere in the source code. An easier way
 * to use it, is the usage of the class {@link
 * org.dinopolis.util.Debug org.dinopolis.util.Debug } which is a
 * static frontend for this class.  <P> For a detailed description of
 * the Debug class see {@linkplain org.dinopolis.util.Debug the
 * javadoc of org.dinopolis.util.Debug}.
 *
 * @author Christof Dallermassl <cdaller@iicm.edu>
 * @version $Id$
 * @see org.dinopolis.util.Debug
 *
 */
public class Debug
{
  /** an object of this class (see {@link #getInstance() getInstance} */
  static Debug debug_instance_ = null;
  /** where to write the messages to (default is <code>System.err</code>*/
  static Writer out_ = new PrintWriter (System.err);  // default
  /** Set with all levels chosen to display */
  protected TreeSet levels_to_print_ = new TreeSet();
//  /** if methods are called without level-name, this one is taken */
//  protected String default_level_ = "default";
  /** if <code>false</code>, no debugging messages will be printed */
  private boolean enabled_ = true;
  private boolean print_all_levels_ = false;
  /** the time the last debug message was printed */
  protected long last_message_time_ = System.currentTimeMillis();  
  /** message counter */
//  protected int count_ = 0;
  /** Where to store timer-information */
  private TreeMap timers_ =  new TreeMap();
  
  private File user_property_file_ = null;
  private Object user_property_file_lock_ = new Object();

    /** name of property file in home directory of user*/
  protected String user_property_file_name_ = "debug.properties";
  protected long property_file_last_modified_ = 0;
      /** Thread used to refresh properties from user_property_file_ */
  protected RefreshPropertiesThread refresh_properties_thread_;
      /** priority of the thread that refreshes the properties */
  static final int REFRESH_PROPERTIES_THREAD_PRIORITY = 3; 
      /** Delimiter for format keywords */
  static final String KEYWORD_DELIMITER = "%";
      /** Vector holding the objects creating the message string */
  protected Vector message_format_;
      /** lock to synchronize on the message_format_ */
  protected Object message_format_lock_ = new Object();
  /** <code>true</code>, if output is written to a file (must be
   *  closed after each access)
   */
  protected boolean write_to_file_ = false;
  /** in case the output is written to a file, that's the name of it */
  protected String filename_ = "";
  /** carriage return on this system */
  static final protected String CR = System.getProperty("line.separator");

      /**
       * if false, any Debug-calls should be removed from class-code
       * when compiling them and "if (Debug.DEBUG) stands before
       * every call to Debug.print...  This variable should not be
       * used for temporarily switching on/off debugging!
       */
  static final public boolean DEBUG = true;

      /** 
       * Only if true, debug messages for the debug class are printed.
       * Additionaly its debug levels must be enabled (as normally). 
       * This is not done using 'normal' debug levels, as users normally
       * do not want to see these messages, even if 'printAllLevels' is set!
       */ 
  static final public boolean DEBUG_DEBUG = false; 


  public Debug()
  {
    user_property_file_ = new File(
                         (String)System.getProperties().get("user.home"),
                         user_property_file_name_);
    loadProperties();

  }

//----------------------------------------------------------------------
/**
 * Loads the property file.
 */
  public void loadProperties()
  {
    Properties properties=null;
    File props_file=null;

    try
    {
      long props_file_modified = user_property_file_.lastModified();
      
          // read only, if file newer than last time read
      if (property_file_last_modified_ < props_file_modified)
      {
        if (DEBUG_DEBUG)
          println("debug_properties", "rereading properties file '"
                                      +user_property_file_name_+"'");
        property_file_last_modified_ = props_file_modified;
        properties = new Properties();
        synchronized(user_property_file_lock_)
        {
          properties.load(new BufferedInputStream(
                          new FileInputStream(user_property_file_)));
        }
        enable(new Boolean(
          (String)properties.get("Debug.enabled")).booleanValue());

        removeAllLevelsToPrint();
        String levels = (String)properties.get("Debug.printLevels");
        if (levels != null)
          addLevelsToPrint(levels);

            // show all groups/levels? defaults to false!
        printAllLevels(new Boolean(
          (String)properties.get("Debug.printAllLevels")).booleanValue());

        String msg_format = (String)properties.get("Debug.messageFormat");
        if (msg_format == null)
          msg_format = "%message%";  // default value
        setMessageFormat(msg_format);

        try
        {
          long refresh = new Long((String)properties.get(
                           "Debug.refreshPropertyTime")).longValue();
          startRefreshPropertiesThread(refresh);
        }
        catch(NumberFormatException nfe)
        {
          if (DEBUG_DEBUG)
            println("debug_properties",
               "NumberFormatException at Debug.refreshPropertyTime");
        }

        String file_name = (String)properties.get("Debug.fileName");
        if (file_name != null)
          setWriterToFile(file_name);

        if (DEBUG_DEBUG)
        {
          println("debug_properties","Debug-properties="+properties);
          println("debug_properties","levels="+levels_to_print_);
        }
      }
    }
    catch(IOException ioe)
    {
      System.err.println("users property file '"+user_property_file_
                         +"' couldn't be read");
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
        // the default group and level are always set
//    levels_to_print_.add(default_level_);
  }


//----------------------------------------------------------------------
/**
 * Set the format of the debug messages.
 * Key words are delimited by '%' and are replaced at run-time.
 * Valid key words are:
 * %date%             - prints actual date
 * //%count%            - prints the debug message counter
 * %milliseconds%     - prints the actual milliseconds
 * %diffmilliseconds% - prints the time difference since the last
 *                        debug message was printed (in milliseconds)
 * %level%            - prints the levelname of the debugmessage
 * %stacktrace%       - prints a stacktrace to ever msg
 * %stacktraceline%   - prints last line in stacktrace (caller)
 * %thread%           - prints the string rep of the thread of the caller
 * %threadgroup%      - prints the name of the threadgroup of the caller
 * %threadname%       - prints the name of the thread of the caller
 * %message%          - prints the debug message itself
 *
 * You can also insert '\n' (and others) e.g. to force a new line or
 * '\t' to divide the output in columns.  The key words are replaced
 * by their value, the rest is left unchanged.
 *
 * Example: <BR> 
 * "DEBUG L=%level%: %message%"
 * prints "DEBUG L=defaultlevel: debug message"
 * @param format_string the format string of the debug-messages.  
 */

  public void setMessageFormat(String format_string)
  {
    if (DEBUG_DEBUG)
      println("debug_message","setMessageFormat("+format_string+")");
    StringTokenizer tokenizer =
      new StringTokenizer(format_string,KEYWORD_DELIMITER,true);
    int len = tokenizer.countTokens();   

    if (len == 0)  // no variables in prefix_string
    {
      message_format_ = null;
      return;
    }
    Vector message_format = new Vector();
    boolean in_variable = false;
    String token;
    int count=0;
    DebugMessageFormatObject message_obj;
    while (tokenizer.hasMoreTokens())
    {
      token = tokenizer.nextToken();
//      System.out.println("TOKEN: "+token);
      if (token.equals(KEYWORD_DELIMITER))
      {
        in_variable = !in_variable;
      }
      else
      {
        if (in_variable)
        {
          token = token.toLowerCase();
          try
          {
            message_obj =
              DebugMessageFormatFactory.getMessageFormatObject(token);
            message_format.addElement(message_obj);
          }
          catch(Exception e)
          {
            System.err.println ("ERROR: "+getClass().getName()+" Unknown keyword '"+token+"'!");
//              e.printStackTrace();
          }
        }
        else // if no keyword, add the string itself
        {
          message_format.addElement(token);
        }
      }
    }
    synchronized(message_format_lock_)
    {
      message_format_ = message_format;
    }
//    System.out.println("vector: "+message_format_);
  }


//----------------------------------------------------------------------
/**
 * Should all debug messages be printed, ignoring the level?
 * @param print_all if <code>true</code> all debug messages are
 * printed (debug levels are not checked anymore), if
 * <code>false</code> the debug levels are checked and depending on
 * this check, the messages are printed or not.
 */
  public void printAllLevels(boolean print_all)
  {
    print_all_levels_ = print_all;
  }



//----------------------------------------------------------------------
/**
 * adds one or more levels, for which messages should be printed
 * (levels can be separated by space, tab, newline or comma).
 * @param levels the debug-levels to be added
 */
  public void addLevelsToPrint(String levels)
  {
    String[] tokens = tokenize(levels.toLowerCase());
    synchronized(levels_to_print_)
    {
      for (int i=0; i < tokens.length; i++)
      {
        levels_to_print_.add(tokens[i]);
      }
    }
  }

//----------------------------------------------------------------------
/**
 * removes one or more levels, for which messages should be printed
 * (levels can be separated by space, tab, newline or comma).
 * @param levels the debug-levels to be removed
 */
  public synchronized void removeLevelsToPrint(String levels)
  {
    String[] tokens = tokenize(levels.toLowerCase());
    synchronized(levels_to_print_)
    {
      for (int i=0; i<tokens.length; i++)
      {
        levels_to_print_.remove(tokens[i]);
      }
    }
  }

//----------------------------------------------------------------------
/**
 * removes all levels, for which messages should be printed
 */
  public void removeAllLevelsToPrint()
  {
    synchronized(levels_to_print_)
    {
      levels_to_print_.clear();
    }
  }


//----------------------------------------------------------------------
/**
 * Creates a new Timer and names it. The timer starts at zero.
 * @param name name of the timer(case insensitive).
 */
  public void startTimer(String name)
  {
    if (!enabled_)
      return;
    String my_name = name.toLowerCase();
    Timer mytimer = new Timer(my_name);
    synchronized(timers_)
    {
      timers_.put(my_name,mytimer);
    }
    mytimer.startTimer();
  }


//  //----------------------------------------------------------------------
//  /**
//   * Creates a new Timer and names it. The timer starts at zero.
//   * @param level name of the debug-level
//   * @param name name of the timer(case insensitive).
//   */
//    public void startTimer(String level, String name)
//    {
//      if (!enabled_)
//        return;
//      String my_name = name.toLowerCase();
//      Timer mytimer = new Timer(level.toLowerCase(),my_name);
//      timers_.put(my_name,mytimer);
//      mytimer.startTimer();
//    }


//----------------------------------------------------------------------
/**
 * Stops the named timer and returns the string representation of it.
 * @param name name of the timer(case insensitive)
 * @return the String representation of the timer.
 */
  public synchronized String stopTimer(String name)
  {
    if (!enabled_)
      return "";
    String my_name = name.toLowerCase();
    synchronized(timers_)
    {
      Timer mytimer = (Timer)timers_.get(my_name);
      if (mytimer==null)
        return("Unknown Timer '"+my_name+"' "+getStackTrace(3,1));
      String timer_value = "";
      mytimer.stopTimer();
      timer_value = mytimer.toString();
      timers_.remove (my_name);
      mytimer=null;
      return(timer_value.toString());
    }
  }

//----------------------------------------------------------------------
/**
 * Returns the string representation of the named timer, but does not 
 * stop it.
 * @param name name of the timer (case insensitive)
 * @return the String representation of the timer.
 */
  public String getTimer(String name)
  {
    if (!enabled_)
      return"";
    String my_name = name.toLowerCase();
    Timer mytimer;
    synchronized(timers_)
    {
      mytimer = (Timer)timers_.get(my_name);
    }
    if (mytimer==null)
      return("Unknown Timer '"+my_name+"' "+getStackTrace(3,1));
    mytimer.stopTimer();
    return(mytimer.toString());
  }

//----------------------------------------------------------------------
/**
 * Denable/disable debug output.
 * @param flag <code>true</code> enables, <code>false</code> disables 
 *   debug output.
 */
  public void enable(boolean flag)
  {
    enabled_ = flag;
  }


//----------------------------------------------------------------------
/**
 * Returns <code>true</code>, if debug output is enabled.
 * @return <code>true</code>, if debug output is enabled.
 */
  public boolean isEnabled()
  {
    return(enabled_);
  }


//----------------------------------------------------------------------
/**
 * Return <code>true</code>, if debug output is enabled for the given
 * level. If debug output is disabled in general, this method returns
 * <code>false</code>. 
 * @param level name of the level
 * @return <code>true</code>, if debug output is enabled for the given
 * level. If debug output is disabled in general, this method returns
 * <code>false</code>. 
 */
  public boolean isEnabled(String level)
  {
    return (enabled_ && isLevelEnabled(level));
  }

//----------------------------------------------------------------------
/**
 * Checks, if messages for the given debug level are printed.
 * @param level name of the debug-level
 */
  protected boolean isLevelEnabled (String level)
  {
    synchronized(levels_to_print_)
    {
      return (print_all_levels_
              || levels_to_print_.contains(level.toLowerCase()));
    }
  }


//----------------------------------------------------------------------
/**
 * Prints an object (its string-representation).
 * There is no newline added to the debug message.
 * @param obj the object to be printed.
 */
  public void print(Object obj)
  {
    if (enabled_)
      printMsg("",formatMessage("",objectToString(obj)));
  }

//----------------------------------------------------------------------
/**
 * Prints an object (its string-representation) using the given
 * debug-level. There is no newline added to the debug message. 
 * @param level the debug-level for this object.
 * @param obj the object to be printed.
 */
  public void print(String level, Object obj)
  {
    if (enabled_ && isLevelEnabled(level))
      printMsg(level,formatMessage(level,objectToString(obj))+CR);
  }

//----------------------------------------------------------------------
/**
 * Prints an object (its string-representation).
 * There is a newline added to the debug message.
 * @param obj the object to be printed.
 */
  public void println(Object obj)
  {
    if (enabled_)
      printMsg("",formatMessage("",objectToString(obj))+CR);
        //System.out.println(levels_to_print_);
  }

//----------------------------------------------------------------------
/**
 * Prints an object (its string-representation) using the given
 * debug-level. There is a newline added to the debug message. 
 * @param level the debug-level for this object.
 * @param obj the object to be printed.
 */
  public void println(String level, Object obj)
  {
    if (enabled_ && isLevelEnabled(level))
      printMsg(level,formatMessage(level,objectToString(obj))+CR);
  }

//----------------------------------------------------------------------
/**
 * Returns a stacktrace. It returned does not include the
 * trace inside the Debug class.
 *
 * @return the stacktrace.
 */
  public String getStackTrace()
  {
    return(getStackTrace(new Throwable(), 4, 10000));
  }

//----------------------------------------------------------------------
/**
 * Returns the stacktrace of throwable. It returned does not include the
 * trace inside the Debug class.
 *
 * @param throwable the throwable to be printed.
 * @return the stacktrace of throwable.
 */
  public String getStackTrace(Throwable throwable)
  {
    return(getStackTrace(throwable, 2, 10000));
  }


//----------------------------------------------------------------------
/**
 * Returns the line of the source code from which this method
 * was called.
 * @return the line of the source code from which this method
 * was called.
 */
  public String getStackTraceLine()
  {
    return(getStackTrace(4,1));
  }

//----------------------------------------------------------------------
/**
 * returns StackTrace as a String. For internal use. 
 * @param from_line StackTrace starts at from_line
 * @param num_lines max. number of output
 * @return lines of the StackTrace (separated by "\n")
 */
  public String getStackTrace(int from_line, int num_lines)
  {
    return(getStackTrace(new Throwable(), from_line, num_lines));
  }

//----------------------------------------------------------------------
/**
 * returns StackTrace as a String
 * @param from_line StackTrace starts at from_line
 * @param num_lines max. number of output
 * @return lines of the StackTrace (separated by "\n")
 */
  public String getStackTrace(Throwable throwable,
                              int from_line,
                              int num_lines)
  {
    if (!enabled_)
      return("");

    StringBuffer stack_trace = new StringBuffer ();
    PrintReaderWriter readerwriter = new PrintReaderWriter (4096);

    throwable.printStackTrace(readerwriter);

    // read lines from printStackTrace output:

      // skip 'from_line-1' lines
      // e.g.: first line = "java.lang.Throwable"
      //       second line= "at org.dinopolis.util.debug.Debug...."

    while (from_line-- > 0)
    {
      readerwriter.readln();
    }

        // starts to get interesting (who called this method?):
    while ((num_lines-- > 0) && readerwriter.ready())
    {
      String line = readerwriter.readln();
      stack_trace.append(line);
    }

    try
    {
      stack_trace.setLength(stack_trace.length()-1); // cut off last "\n"
    }
    catch (StringIndexOutOfBoundsException e) {}

    return stack_trace.toString();
  }

//----------------------------------------------------------------------
/**
 * Returns a String representation of the object, this method also
 * handles primitive arrays and object arrays.
 * @param obj a String representation of the object
 * @return a String representation of an object 
 */
  public static String objectToString (Object obj)
  {
    StringBuffer sbuf = new StringBuffer(30);
    if (obj == null)
    {
      sbuf.append("[null]");
      return sbuf.toString();
    }
    Class c = obj.getClass();
    if (c.isArray())
    {
      Class type = c.getComponentType();
      int length = Array.getLength(obj);
          //sbuf.append ("[ARRAY:");
      sbuf.append ("(");
      sbuf.append (type.getName());
      sbuf.append ("[");
      sbuf.append (length);
      sbuf.append ("])");
      for (int count = 0; count < length-1; count++)
      {
        sbuf.append(objectToString(Array.get(obj, count)));
        sbuf.append (",");
      }
      if (length > 0)
        sbuf.append(objectToString(Array.get(obj, length-1)));
    }
    else
    {
          //sbuf.append("[");
          //sbuf.append(c.getName());
          //sbuf.append(":");
      sbuf.append(obj.toString());
          //sbuf.append("]");
    }
    return(sbuf.toString());
  }

//----------------------------------------------------------------------
/**
 * Stops the program and waits for a 'enter' on
 * <code>System.in</code>. Actually, it waits for any character, but
 * <code>System.in</code> is flushed only on 'enter', so any
 * characters typed before might stay in the keyboard buffer.
 */
  public static void waitEnterPressed()
  {
    System.err.print ("Press 'enter' to continue...");
    try
    {
      while (System.in.read() == -1);
    }
    catch (IOException e) {}
  }


//----------------------------------------------------------------------
/**
 * Makes an array of strings out of a string
 * (the tokens can be separated by tab, space, newline  or comma)
 * @param str String with the above delimiters
 * @return array of string
 */
  protected static String[] tokenize(String str)
  {
    StringTokenizer st = new StringTokenizer(str," \t\n\r,");
    int len = st.countTokens();
    String[] tokens = new String[len];
    for (int i = 0; i<len; i++)
    {
      tokens[i] = st.nextToken();
    }
    return tokens;
  }

//----------------------------------------------------------------------
/**
 * The Debug Util class is able to reread its property-file from an *
 * independent thread, so any changes in the file (e.g. additional *
 * debug-levels set) are reflected in the behaviour of the Debug Util
 * class.
 * @param refresh_time the time given in milliseconds to reload the
 * debug-property-file, if <code>0<code>, the file is never reloaded
 * (and no thread is started 
 */
  public void startRefreshPropertiesThread(long refresh_time)
  {
    if (refresh_time > 0)
    {
      if (refresh_properties_thread_ == null)
      {
            // create a new one
        refresh_properties_thread_ = new RefreshPropertiesThread(this);
        refresh_properties_thread_.setRefreshTime(refresh_time);
        refresh_properties_thread_.setName("Debug - Refresh Properties");
        refresh_properties_thread_.setPriority(REFRESH_PROPERTIES_THREAD_PRIORITY);
            // set to daemon, so the VM exits, when this is the only thread left
        refresh_properties_thread_.setDaemon(true); 
        refresh_properties_thread_.startThread();
      }
      else
      {
        refresh_properties_thread_.setRefreshTime(refresh_time);
      }
    }
    else  
    {
      stopRefreshPropertiesThread(); 
    }
  }

//----------------------------------------------------------------------
/**
 * The thread that rereads the debug property file is stopped, so any
 * changes in the file are ignored.
 */
  public void stopRefreshPropertiesThread()
  {
    if ((refresh_properties_thread_!= null)
        && (refresh_properties_thread_.isAlive()))
      refresh_properties_thread_.stopThread();
  }


//----------------------------------------------------------------------
/**
 * Writes all messages to a file.
 * @param filename the file to write to.
 */
  public void setWriterToFile(String filename)
  {
    write_to_file_ = true;
    filename_ = filename;
    try 
    {
      // file is opened and closed, just to erase its contents, but
      // not to delete it (maybe the user is allowed to write to this
      // file, but not to create a new one!)
      FileWriter file = new FileWriter(filename);
      file.close();
    }
    catch (IOException e) {}
  }

//----------------------------------------------------------------------
/**
 * Sets the filename from where the debug properties are read. If the
 * file does not exist, the setting is ignored!
 * @param filename the file to read from.  */
  public void setPropertyFile(String filename)
  {
    File new_prop_file = new File(filename);
    if (!new_prop_file.exists())
    {
      System.err.println("ERROR: Debug property file '"+filename
                         +"' does not exist! Ignoring the setting!");
    }
    else
    {
      synchronized(user_property_file_lock_)
      {
        user_property_file_name_ = filename;
        user_property_file_ = new_prop_file;
        property_file_last_modified_ = 0;
      }
      loadProperties();  // load the given property file
    }
  }


//----------------------------------------------------------------------
/**
 * Sets the writer the debug messages are written to. The default is
 * System.err.  
 * @param out the java.io.Writer (e.g. System.out, System.err, or a
 *   network connection), where all the messages are written to
 */
  public void setWriter(Writer out)
  {
    out_ = out;
    write_to_file_ = false;  // don't close after each access
  }


//----------------------------------------------------------------------
/**
 * Returns the Writer, where the debug messages are written to.
 * @return the Writer, where the debug messages are written to.
 */
  public Writer getWriter()
  {
    return(out_);
  }

//----------------------------------------------------------------------
/**
 * Returns an instance of this class (so Debug can be used as a
 * singleton)
 * @return an instance of this class (so Debug can be used as a
 * singleton) 
 */
  public static Debug getInstance()
  {
    if (debug_instance_ == null)
      debug_instance_ = new Debug();
    return (debug_instance_);
  }


//----------------------------------------------------------------------
/**
 * Parses the given arguments, filters out the debug-util specific ones,
 * applies these (e.g. sets the debug levels, ...) and starts the given
 * applications. The first non-debug-util argument is taken as the
 * classname to be started. The rest of the arguments is handed over
 * to the application.
 *
 * @param arguments the arguments, including the debug arguments, the
 * classname and the arguments for the given application.
 */
  public void startDebugApplication(String[] arguments)
  {
    if (DEBUG_DEBUG)
      println("debug_start_application","startDebugApplication: "
              +objectToString(arguments));


    String[] valid_debug_args =
      new String[] {"debuglevels*","debugmessage*","debugfilename*",
                    "debugrefresh#","debugpropertiesfile*"};
    CommandArguments args = null;

    try
    {
      args = new CommandArguments(arguments,valid_debug_args);
    }
    catch(CommandArgumentException cae) 
    {
      cae.printStackTrace();
    }

          // handle the debug arguments:
    if (args != null)
    {
      if (args.isSet("debuglevels"))
        addLevelsToPrint((String)args.getValue("debuglevels"));

      if (args.isSet("debugmessage"))
        setMessageFormat((String)args.getValue("debugmessage"));

      if (args.isSet("debugfilename"))
        setWriterToFile((String)args.getValue("debugfilename"));

      if (args.isSet("debugpropertiesfile"))
        setPropertyFile((String)args.getValue("debugpropertiesfile"));

      if (args.isSet("debugrefresh"))
        refresh_properties_thread_.setRefreshTime(
          ((Integer)args.getValue("debugrefresh")).longValue()); 
    }


    String application_cmd_line = args.getArgumentAt(0);

    StreamTokenizer tokenizer = new StreamTokenizer(
                   new StringReader(application_cmd_line));
    tokenizer.resetSyntax();
    tokenizer.wordChars(0x0021,0x00ff);
    tokenizer.whitespaceChars('\t','\t');   // separate params
    tokenizer.whitespaceChars(' ',' ');     // separate params
    tokenizer.quoteChar ('\"');             // quote params
    tokenizer.quoteChar ('\'');             // quote params

    Vector tokens = new Vector();
    int code;
    try
    {
  out:
      while(true)
      {
        code = tokenizer.nextToken();
        switch(code)
        {
          case StreamTokenizer.TT_EOF:
            if (DEBUG_DEBUG)
              println("debug_tokenize","got EOF");
            break out;
          case StreamTokenizer.TT_EOL:
            if (DEBUG_DEBUG)
              println("debug_tokenize","got EOL");
            break;
          case StreamTokenizer.TT_WORD:
          case '\"':
          case '\'':
            if (DEBUG_DEBUG)
              println("debug_tokenize","got WORD: '"+tokenizer.sval+"'");
            tokens.addElement(tokenizer.sval);
            break;
          default:
            if (DEBUG_DEBUG)
              println("debug_tokenize","unknown token (code 0x0"
                    +Integer.toHexString(code)+") string="+tokenizer.sval);
            throw new IllegalArgumentException ("unknown token (code 0x0"
                   +Integer.toHexString(code)+") string="+tokenizer.sval);
        }
      }
    }
    catch(Exception e)
    {
      System.err.println("Parse Error: ");
      e.printStackTrace();
    }
    
    if (isEnabled("debug_start_application"))
    {
      println("cmd line = "+application_cmd_line);
      println("tokens="+tokens);
    }

    if (tokens.size() > 0)
    {
      String classname = (String)tokens.elementAt(0);
      String[] application_arguments = new String[tokens.size()-1];
      application_arguments = (String[])tokens.toArray(application_arguments);
      executeMainMethod(classname,application_arguments);
    }
  }


//----------------------------------------------------------------------
/**
 * Calls the main method of the given class.
 * @param classname the classname to be started.
 * @param arguments the arguments for the application.
 */
  protected void executeMainMethod(String classname, String[] arguments)
  {
    if (DEBUG_DEBUG)
      println("debug_start_application","execute "+classname
              +".main(" + objectToString(arguments) + ")");
    try
    {
      Class[] paramtypes = new Class[] { String[].class };
      Class application_class = Class.forName(classname);
      Method main_method =
        application_class.getDeclaredMethod("main",paramtypes);
      try
      {
        main_method.invoke (null,new Object[] { arguments });
      }
      catch (InvocationTargetException ite)
      {
        System.err.println("Invokation of class '"+classname
                           +"' threw an exception:");
        throw(ite.getTargetException());
      }
    }
    catch(Throwable t)
    {
      t.printStackTrace();
    }
  }


//----------------------------------------------------------------------
/**
 * The main method can be used to set Debug options and then start a
 * given application. After execution of the application the thread
 * that rereads the properties is stopped. 
 * <P>
 * Valid options are:
 * <UL>
 * <li><code>--debuglevels level1,level2</code>: sets one or more
 * debug levels.</li>
 * <li><code>--debugmessage "messageformat"</code>: sets the format of
 * the debug messages. Please see method <code>setMessageFormat</code>
 * for details.</li>
 * <li><code>--debugfilename debugfile.log</code>: sets the name of
 * the file the debug messages are written to.</li>
 * <li><code>--debugrefresh milliseconds</code>: sets the refresh time
 * for the thread that rereads the properties-file.</li>
 * <li><code>--debugpropertiesfile filename</code>: sets the name of 
 * the properties-file.</li>
 * </UL>
 * <P>
 * One(!) argument must hold the classname to start and the arguments
 * that should be handed to the main method of the class. To ensure
 * that it is only one argument, put the whole command line in
 * quotes. 
 * <P>
 * E.g. <code>javac org.dinopolis.util.debug.Debug --debuglevels test-level
 * --debugrefresh 5000 --debugfilename debug.log
 * "org.testpackage.testclass argument1 argument2 'argument with
 * space' --option1"</code>
 * 
 * @param args any debug arguments to set debug options and the
 * commandline to start the application (in quotes).
 */
  public static void main(String[] args)
  {
    Debug debug = Debug.getInstance();
    if (DEBUG_DEBUG)
      debug.println("debug_start_application","main :"
                    + objectToString(args));
    debug.startDebugApplication(args);
    debug.stopRefreshPropertiesThread();
  }

//----------------------------------------------------------------------
/**
 * Prints a message to the specified writer (default is
 * System.err). In case of a file, it is closed immediately, so no
 * information is lost in case of a program abortion.
 * 
 * @param msg Message to print
 */
  protected void printRawMsg(String msg)
  {
    if (!enabled_)
      return;
    try
    {
      if (write_to_file_)
      {
            // open file (append all lines):
        out_ = new FileWriter (filename_,true); 
      }
      out_.write (msg);
      out_.flush();
      if (write_to_file_)
      {
            // close it again, so if prg hangs, file is correctly closed:
        out_.close();  
      }
    }
    catch (IOException e)
    {
      System.err.println (e.getMessage());
    }
//    count_++;
    last_message_time_ = System.currentTimeMillis();
  }

//--------------------------------------------------------------------------------
/**
 * Prints a message if the given level is allowed to be printed or if
 * the level is an empty string.
 * @param level the debug-level for this object
 * @param msg Message to print
 */
  protected void printMsg(String level, String msg)
  {
    if (enabled_ && (isLevelEnabled(level) || level.length() == 0))
    {
      printRawMsg(msg);
    }
//      else
//        count_++;  // counter is increased anyway
  }

//--------------------------------------------------------------------------------
/**
 * Formats a message according to the format string set by the method
 * <code>setMessageFormat</code>. 
 *
 * @param level the debug-level for this object
 * @param msg Message to print
 */
  protected synchronized String formatMessage(String level, String msg)
  {
     // default, if no format is given, at least the message is printed:
    if (message_format_ == null)
      return (msg);  

    StringBuffer message = new StringBuffer();
    Object message_obj;
    synchronized(message_format_lock_)
    {
      for (int count = 0; count < message_format_.size(); count++)
      {
        message_obj = message_format_.elementAt(count);
        if (message_obj instanceof String)
        {
          message.append((String)message_obj);
        }
        else
        {
          message.append(
            ((DebugMessageFormatObject)message_obj).getEvaluatedKeyword(level,msg,this));
        }
      }
    }
    return(message.toString());
  }

//--------------------------------------------------------------------------------
/**
 * Returns the time in milliseconds the last debug message was printed.
 *
 * @return the time in milliseconds the last debug message was printed.
 */
  protected long getLastDebugMessageTime()
  {
    return(last_message_time_);
  }

//--------------------------------------------------------------------------------
// Inner classes
//--------------------------------------------------------------------------------
//--------------------------------------------------------------------------------
  class RefreshPropertiesThread extends Thread
  {
    long refresh_time_;
    Debug debug_;
    boolean loop_ = true;
  
    public RefreshPropertiesThread(Debug debug)
    {
      debug_ = debug;
    }
  
    public void setRefreshTime(long refresh_time)
    {
      refresh_time_ = refresh_time;
    }

    public void startThread()
    {
      loop_ = true;
      super.start();
    }

    public void stopThread()
    {
      loop_ = false;
    }
  
    public void run()
    {
      while (loop_)
      {
        debug_.loadProperties();
        synchronized(this)
        {
          try
          {
            wait(refresh_time_);
          }
          catch(InterruptedException e) {}
        }
      }
    }
  } // end of inner class RefreshPropertiesThread
  
//______________________________________________________________________
//______________________________________________________________________
/**
 * this class is a workaround to let Throwable.printStackTrace(PrintWriter)
 * write somewhere, where it can be reread line by line.
 */
  class PrintReaderWriter extends PrintWriter
  {
    char[] buf_;
    int pos_;

    public PrintReaderWriter (int len)
    {
      super (System.err);         // have to, even if I'll never use it!
      buf_ = new char[len];
          /* next empty char pos */
      pos_ = 0;
    }

    public synchronized void println(char[] input)
    {
      System.arraycopy (input,0,buf_,pos_,input.length);
      pos_ += input.length;
      buf_[pos_++] = '\n';
    }

    public synchronized String readln()
    {
      int count = 0;
      while ((buf_[count] != '\n') && (count < pos_))
        count++;

      char[] first_line = new char [count+1];
      System.arraycopy (buf_,0,first_line,0,count+1);

          // remove first line from buffer (copy filled part of buffer
          // to the beginning:
      pos_ -= count+1;
      System.arraycopy (buf_,count+1,buf_,0,pos_);

      return (new String(first_line));
    }

    public boolean ready()
    {
      return (pos_ > 0);
    }
  } // end of inner class PrintReaderWriter
}










