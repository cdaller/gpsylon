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


package org.dinopolis.util;

import java.io.Writer;

//---------------------------------------------------------------------
//---------------------------------------------------------------------
/**
 * Debug helps the programmer to print various debug messages. This
 * class is a frontend to the {@link org.dinopolis.util.debug.Debug
 * org.dinopolis.util.debug.Debug} class. Its only purpose is to ease
 * the use of the mentioned class. All methods in this class are
 * static so the usage simply is
 * <code>Debug.methodName(arguments)</code>. No instance must be
 * handed from one class to the other.
 * <p>
 * 
 * <h3>Debug Levels</h3>
 * <a name="sd:DebugDebugLevels">
 * 
 * The Debug class provides the possibility to print debug messages
 * depending on the debug level. There is a status for a given debug
 * level that indicates if messages for this level should be put out or
 * not. This status can be requested from the debug class. A shortcut
 * in the method that prints the debug message will be provided.
 * 
 * <h3>Change Settings</h3>
 * <a name="sd:DebugChangeSettings">
 * 
 * The settings of the debug class (e.g. enable/disable debug messages
 * for a debug-level, ...) can be set using different ways:
 * 
 * <ul>
 * 
 * <li> Hardcoded in the application that should be debugged: The debug
 * methods that change the settings can be invoked within the code of the
 * application.
 * 
 * <li> In the configuration file of the debug class: All settings can
 * be written into a configuration file. This file is read by the debug
 * class when the application uses a debug method for the first time. If
 * wanted, the debug class rereads this file periodically, so any changes
 * made in this file are applied directly.
 * 
 * <li> The debug class provides a way to set debug settings first and
 * then directly start a given application, so the settings stay valid
 * for the application.
 * 
 * </ul>
 * 
 * 
 * <h3>Redirect Output</h3>
 * <a name="sd:DebugRedirectOutput">
 * 
 * The debug class is able to write the debug messages to any stream. By
 * default this stream is set to <code>System.err</code>, but can easily be
 * set to a file or to a network stream. 
 * 
 * <h3>Format of Debug Message</h3>
 * <a name="sd:DebugFormatOfDebugMessage">
 * 
 * The format of the debug message is freely configurable. The debug
 * message format definition is parsed for keywords (Keywords are
 * delimitered by a special character.). A factory creates objects by
 * using the keyword as a part of the classname. On request, these
 * objects return a String-value which replaces the keyword in the debug
 * message format definition. E.g. for the keyword <code>%date%</code>,
 * an object of the class {@link
 * org.dinopolis.util.debug.DebugMessageDATE} is created.
 * <p>
 * For performance reasons these objects are crated only when the format
 * definiation changes and are then reused for every debug message. This
 * implies that these objects are stateless.
 * 
 * <h3>Timer</h3>
 * <a name="sd:DebugTimer">
 * 
 * A simple timer functionality is provided. The timer objects are named
 * and the name is then used to retrieve the time or to stop the
 * timer. The timer object is never handed to the user; it is only
 * possible to obtain a string representation that holds the current time
 * and the time spent since the timer was started. 
 * <p>
 * The timer does not subtract the time spent inside the debug class. So
 * timings may vary depending on the debug levels enabled/disabled.
 * 
 * <h3>StackTrace</h3>
 * <a name="sd:DebugStacktrace">
 * 
 * The debug class provides methods to obtain a string representation of
 * the stack trace, so any calling hierarchies can be made visible
 * easily. Alternatively only one line of the stack trace can be
 * requested. This line indicates the class and linenumber (if available)
 * of the caller.
 * <p>
 * As Java offers only stacktraces of Exceptions/Throwables, a new
 * Throwable is created and its stacktrace is used. Any traces from the
 * Debug class itself are removed.
 * 
 * <h3>Print Various Objects Types</h3>
 * <a name="sd:DebugPrintVariousObjectsTypes">
 * 
 * A static method is provided that returns the string representation of
 * arrays of objects as well as of objects. In the case of objects, the
 * <code>toString()<code> method is called. In the case of arrays, for each
 * element in the array the static method is recursivly called. Using
 * this way, string representations of arrays that hold arrays can be
 * returned as well.
 * 
 * <h3>Remove Debug Code</h3>
 * <a name="sd:DebugRemoveDebugCode">
 * 
 * The Debug class holds a <code>static final boolean</code> variable. If this
 * variable is used in a conditional statement, the Java compiler is able
 * to decide that the code is written into the class file or not during
 * compilation. For better understanding, an example is given:
 * <pre>
 * if (Debug.DEBUG)
 * {
 *   // this code will only be in the class file, 
 *   // if Debug.DEBUG is set to true!
 * }
 * </pre>
 * 
 * <h3>Static and non-static class</h3>
 * <a name="sd:DebugStaticAndNon-StaticClass">
 *
 * There are two different ways to use the Debug class. This class
 * with static methods is for comfortable use by applications. This is
 * the class normal users will use. Some situations may ask for
 * independent Debug objects, so an instance of {@link
 * org.dinopolis.util.debug.Debug org.dinopolis.util.debug.Debug} may
 * be used for these purposes. The {@linkplain
 * org.dinopolis.util.Debug 'static' Debug class} uses one instance of
 * the 'instance' Debug class.
 * 
 * <h3>Message Format Objects</h3>
 * 
 * Message Format Objects are created by {@link
 * org.dinopolis.util.debug.DebugMessageFormatFactory} and are used for
 * formatting the debug message depending on the debug message format
 * definition. All Format Objects must implement the interface {@link
 * org.dinopolis.util.debug.DebugMessageFormatObject}
 * 
 * <h3>Usage</h3>
 * 
 * The static methods of Debug makes them easy to use.
 * <p>
 * The easiest way to to print a messages:
 * <pre>
 * Debug.println("this is a debug message");
 * </pre>
 * 
 * Generally this does not really make sense, as no differentiation can
 * be made. The only exception is when debug messages should not be
 * printed on the console but to a file or a network connection. All
 * debug messages can be printed to any {@link java.io.Writer} (see
 * method {@link #setWriter(java.io.Writer)}). For simplicity, a special
 * method is provided that faciliates the output to files: 
 * <pre>
 * Debug.setWriterToFile("/log/debug.log")
 * Debug.println("this is a debug message sent to file '/log/debug.log'");
 * </pre>
 * 
 * Different debug levels let the user decide what type of debug messages
 * should be printed, and which shouldn't:
 * <pre>
 * Debug.println("test-level",
 *                "this is a debug message for debug level 'test-level'");
 * </pre>
 * 
 * In this example the debug message is only printed when the given debug
 * level was enabled before:
 * <pre>
 * Debug.addLevelsToPrint("test-level");
 * </pre>
 * 
 * Of course it is possible to enable more than one level at a
 * time. Debug levels can be separated by space, comma, newlines or tabs.
 * <pre>
 * Debug.addLevelsToPrint("test-level, another-level a_third_level");
 * </pre>
 * 
 * One way to use these levels in the println method was shown
 * above. Another is to use it in a conditional statement. This is very
 * handy in the case when more than one debug message should be printed.
 * <pre>
 * if (Debug.isEnabled("test-level"))
 * {
 *   Debug.println("this a debug message, printed only when "
 *                 +"the debug level 'test-level' is enabled.");
 *       // do something else
 *   Debug.println("this another debug message, printed only when "
 *                 +"the debug level 'test-level' is enabled.");
 * }
 * </pre>
 * 
 * This method is slightly more to type, but peforms better, if the debug
 * message has to be created first:
 * <pre>
 * Object object = new Integer(2);
 * Debug.println("test-level","variable object="+object.toString());
 * </pre>
 * In the statement above the debug message has to be created from the
 * given string and from the result of <code>object.toString()</code>. If
 * the debug-level 'test-level' is not enabled, the creation was done for
 * nothing and performance is wasted.
 * <p>
 * Another way to increase performance is the following: Debug has a
 * final static variable named {@link org.dinopolis.util.Debug#DEBUG}. If set to false, any
 * conditional statements using this are evaluated to false already at
 * compile time!  When creating a version of an application that should
 * not have any debug information, simply set {@link org.dinopolis.util.Debug#DEBUG}
 * to false (in the classfile) and recompile Debug and the whole
 * application.  For best usage always use the Debug methods like this:
 * <pre>
 * if (Debug.DEBUG)
 * {
 *   Debug.println("test-level",
 *                  "this part of source code will not make it into "
 *                  +"the class file when Debug.DEBUG is set to false!");
 * }
 * </pre>
 * 
 * There are other helpful methods in the Debug class. How often is it
 * necessary to print the content of an array for debugging reasons. The
 * java method <code>toString()</code> does not work in this
 * case. {@link org.dinopolis.util.Debug#objectToString(Object)} handles nearly all
 * objects and returns a human readable output:
 * <pre>
 * Object[] obj_array = new Object[]{"one", new Integer(2), "three"};
 * if (Debug.DEBUG && Debug.isEnabled("test-level"))
 * {
 *   Debug.println("content of string_array = "
 *           +Debug.objectToString(obj_array));
 * }
 * </pre>
 * 
 * 
 * Sometimes it is interesting to measure how long a method takes to
 * execute. Debug offers a simple way to measure time:
 * <pre>
 * Debug.startTimer("timer_name");
 *     // ----------------
 *     // execute method 1
 *     // ----------------
 * Debug.println("test-level",Debug.getTimer("timer_name"));
 *     // ----------------
 *     // execute method 2
 *     // ----------------
 * Debug.println("test-level",Debug.stopTimer("timer_name"));
 * </pre>
 * 
 * {@link #getTimer(String)} returns a String but does not stop (and
 * remove) the timer, whereas {@link #stopTimer(String)} returns the
 * String and removes the timer from memory.
 * <p>
 * Sometimes it is usefull to know exactly where a debug output is
 * created. The StackTrace-methods of Debug help here:
 * {@link #getStackTrace()} returns a complete StackTrace, so the
 * calling hierarchy is visible, whereas {@link #getStackTraceLine()}
 * returns only the line, from which the command was invoked:
 * <pre>
 * if (Debug.isEnabled("test_level"))
 * {
 *   Debug.println("complete stacktrace: "
 *                  +Debug.getStackTrace());
 *   Debug.println("only a line of the stacktrace: "
 *                  +Debug.getStackTraceLine());
 * }
 * </pre>
 * 
 * The format of the debug message is widely configurable. A message
 * format string consists of strings and keywords. The command below sets
 * the format of all debug messages to the given format. All keywords
 * (words between '<code>%</code>') are replaced by their value, the rest is
 * left unchanged:
 * <pre>
 * Debug.setMessageFormat("DEBUG: %date% \"%message%\" (Thread: %thread%)");
 * Debug.println("this message is in a different format now.");
 * </pre>
 * 
 * Available keywords are:
 * <ul>
 * <li> <code>%date%</code>: actual date
 * <li> <code>%milliseconds%</code>: the actual milliseconds
 * <li> <code>%diffmilliseconds%</code>: the time difference since the
 * last debug message was printed (in milliseconds)
 * <li> <code>%level%</code>: the levelname of the debugmessage
 * <li> <code>%stacktrace%</code>: a stacktrace to ever msg
 * <li> <code>%stacktraceline%</code>: last line in stacktrace (caller)
 * <li> <code>%thread%</code>: the string rep of the thread of the caller
 * <li> <code>%threadname%</code>: the name of the thread of the caller
 * <li> <code>%threadgroup%</code>: the name of the threadgroup of the caller
 * <li> <code>%message%</code>: the debug message itself
 * </ul>
 * 
 * You can also insert '<code>\n</code>' (and others) e.g. to force a new line
 * or '<code>\t</code>' to divide the output in columns.  The key words are
 * replaced by their value, the rest is left unchanged.
 * 
 * <h3>debug.properties file</h3>
 * All settings of the Debug-class can be set through method calls from
 * the application or by editing the config-file named 'debug.properties'
 * in the directory returned by a call to
 * <code>System.getProperties().get("user.home")</code>. Using java in a
 * Unix environment this is normally the home directory of the user
 * (under Windows, I don't know, sorry!).
 * <p>
 * The second way to set debug options has the
 * advantage that the settings can be changed without restarting the
 * application. This makes it possible to change e.g. the debug-level or
 * the debug-message-format when necessary.
 * 
 * An example of such a properties file is given here (everything behind
 * a '<code>#</code>' is taken as a comment):
 * <pre>
 * # ---------- debug.properties start --------------
 * # enable debug messages
 * Debug.enabled=true
 * 
 * # debug levels to print
 * Debug.printLevels=debug_test, debug_start_application
 * 
 * # set to true, if you want to see all level output
 * #Debug.printAllLevels=true
 * 
 * # set this, if you want the output logged in a file:
 * #Debug.fileName=/home/cdaller/tmp/debug.log
 * 
 * # reload this file every xxx milliseconds 
 * # (if xxx == 0, no thread is started.)
 * Debug.refreshPropertyTime=5000
 * 
 * # Set the format of the debug messages.
 * Debug.messageFormat=DEBUG: "%message%" (L:%level%)
 * # ---------- debug.properties end --------------
 * </pre>
 * 
 * Especially the 'Debug.refreshPropertyTime' is worth taking a closer
 * look. It determines that Debug rereads the property file every 5
 * seconds, so any changes in the file become applied at least 5
 * seconds after saving it. Therefore the user may change the debug
 * levels to be printed without the need to restart the application!
 * <p>
 * Another comfortable way to set debug options without hardcoding the
 * calls to debug methods into the application is the following: The
 * {@link org.dinopolis.util.Debug#main(String[])} method can be used to set
 * Debug options and then start a given application. 
 * <p>
 * To let an applicaton be started by Debug, {@link
 * org.dinopolis.util.Debug#main(String[])} accepts some debug-specific
 * options and the classname (and its arguments) of the application.
 * <p>
 * Debug specific options are:
 * <ul>
 * 
 * <li> <code>--debuglevels <level(s)></code>: adds one or more
 * debug levels (if there are any levels set in the property file, they
 * are added).
 * 
 * <li> <code>--debugmessage <messageformat></code>: sets the format of the
 * debug messages. Please see method
 * {@link #setMessageFormat(String)} for details.
 * 
 * <li> <code>--debugfilename <filename></code>: sets the name of the file the
 * debug messages are written to.
 * 
 * <li> <code>--debugrefresh <milliseconds></code>: sets the refresh time for
 * the thread that rereads the properties-file.
 * 
 * <li> <code>--debugpropertiesfile <filename></code>: sets the name of the
 * properties-file.
 * 
 * </ul>
 * 
 * An example that sets two debug levels and the messageformat and then
 * starts the class <code>test.TestApplication</code>:
 * <pre>
 * java org.dinopolis.util.Debug --debuglevels "level1, level2" \
 *   --debugmessage "DEBUG: %message% (Thread: %thread%)" \ 
 *   "test.Application argument1 argument2 --option value"
 * </pre>
 * 
 * <bold>Note:<bold> The class that should be started and its arguments are
 * put inside double quotes, otherwise its arguments are taken as
 * arguments for <code>Debug<code>. The backslash at the end of the
 * lines indicates that the line is continued in the next one.
 * 
 * Another example shows how to redirect the debug messages to a file and
 * how to set the properties-file:
 * <pre>
 * java org.dinopolis.util.Debug  --debugfile my_debug_output.txt \ 
 *   --debugproperiesfile "my_debug.properties" \
 *   "test.Application argument1 argument2 --option value"
 * </pre>
 *  *
 * @author Christof Dallermassl <cdaller@iicm.edu>
 * @version $Id$
 * @see org.dinopolis.util.debug.Debug
 *
 */
public class Debug
{

  static final public boolean DEBUG = org.dinopolis.util.debug.Debug.DEBUG;

  static private org.dinopolis.util.debug.Debug debug_instance_ = 
             org.dinopolis.util.debug.Debug.getInstance();


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
 * You can also insert '\n' (and others) e.g. to force a new line or '\t' to
 * divide the output in columns.
 * The key words are replaced by their value, the rest is left unchanged.
 *
 * Example: <BR>
 * "DEBUG L=%level%: %message%" prints "DEBUG L=defaultlevel: debug message"
 * @param format_string the format string of the debug-messages.
 */
  public static void setMessageFormat(String format_string)
  {
    debug_instance_.setMessageFormat(format_string);
  }

//----------------------------------------------------------------------
/**
 * Should all debug messages be printed, ignoring the level?  
 * @param print_all if <code>true</code> all debug messages are
 * printed (debug levels are not checked anymore), if
 * <code>false</code> the debug levels are checked and depending on
 * this check, the messages are printed or not.  
 */
  public static void printAllLevels(boolean print_all)
  {
    debug_instance_.printAllLevels(print_all);
  }


//----------------------------------------------------------------------
/**
 * adds one or more levels, for which messages should be printed
 * (levels can be separated by space, tab, newline or comma).
 * @param levels the debug-levels to be added
 */
  public static void addLevelsToPrint(String levels)
  {
    debug_instance_.addLevelsToPrint(levels);
  }

//----------------------------------------------------------------------
/**
 * removes one or more levels, for which messages should be printed
 * (levels can be separated by space, tab, newline or comma).
 * @param levels the debug-levels to be removed
 */
  public static void removeLevelsToPrint(String levels)
  {
    debug_instance_.removeLevelsToPrint(levels);
  }

//----------------------------------------------------------------------
/**
 * removes all levels, for which messages should be printed
 */
  public static void removeAllLevelsToPrint()
  {
    debug_instance_.removeAllLevelsToPrint();
  }


//----------------------------------------------------------------------
/**
 * Creates a new Timer and names it. The timer starts at zero.
 * @param name name of the timer(case insensitive).
 */
  public static void startTimer(String name)
  {
    debug_instance_.startTimer(name);
  }


//----------------------------------------------------------------------
/**
 * Stops the named timer and returns the string representation of it.
 * @param name name of the timer(case insensitive)
 * @return the String representation of the timer.
 */
  public static String stopTimer(String name)
  {
    return(debug_instance_.stopTimer(name));
  }

//----------------------------------------------------------------------
/**
 * Returns the string representation of the named timer, but does not 
 * stop it.
 * @param name name of the timer (case insensitive)
 * @return the String representation of the timer.
 */
  public static String getTimer(String name)
  {
    return(debug_instance_.getTimer(name));
  }


//----------------------------------------------------------------------
/**
 * Denable/disable debug output.
 * @param flag <code>true</code> enables, <code>false</code> disables 
 *   debug output.
 */
  public static void enable(boolean flag)
  {
    debug_instance_.enable(flag);
  }


//----------------------------------------------------------------------
/**
 * Returns <code>true</code>, if debug output is enabled.
 * @return <code>true</code>, if debug output is enabled.
 */
  public static boolean isEnabled()
  {
    return(debug_instance_.isEnabled());
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
  public static boolean isEnabled(String level)
  {
    return(debug_instance_.isEnabled(level));
  }


//----------------------------------------------------------------------
/**
 * Prints an object (its string-representation).
 * There is no newline added to the debug message.
 * @param obj the object to be printed.
 */
  public static void print(Object obj)
  {
    debug_instance_.print(obj);
  }


//----------------------------------------------------------------------
/**
 * Prints an object (its string-representation) using the given
 * debug-level. There is no newline added to the debug message. 
 * @param level the debug-level for this object.
 * @param obj the object to be printed.
 */
  public static void print(String level, Object obj)
  {
    debug_instance_.print(level,obj);
  }



//----------------------------------------------------------------------
/**
 * Prints an object (its string-representation).
 * There is a newline added to the debug message.
 * @param obj the object to be printed.
 */
  public static void println(Object obj)
  {
    debug_instance_.println(obj);
  }

//----------------------------------------------------------------------
/**
 * Prints an object (its string-representation) using the given
 * debug-level. There is a newline added to the debug message. 
 * @param level the debug-level for this object.
 * @param obj the object to be printed.
 */
  public static void println(String level, Object obj)
  {
    debug_instance_.println(level,obj);
  }


//----------------------------------------------------------------------
/**
 * Returns a stacktrace. It returned does not include the
 * trace inside the Debug class.
 *
 * @return the stacktrace.
 */
  public static String getStackTrace()
  {
    Throwable throwable = new Throwable();
    return(debug_instance_.getStackTrace(throwable, 4, 10000));
  }


//----------------------------------------------------------------------
/**
 * Returns the stacktrace of throwable.
 *
 * @param throwable the throwable to be printed.
 * @return the stacktrace of throwable.
 */
  public static String getStackTrace(Throwable throwable)
  {
    return(debug_instance_.getStackTrace(throwable,0,10000));
  }


//----------------------------------------------------------------------
/**
 * Returns the line of the source code from which this method
 * was called.
 * @return the line of the source code from which this method
 * was called.
 */
  public static String getStackTraceLine()
  {
    return(debug_instance_.getStackTrace(5,1));
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
    return(org.dinopolis.util.debug.Debug.objectToString(obj));
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
	org.dinopolis.util.debug.Debug.waitEnterPressed();
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
  public static void startRefreshPropertiesThread(long refresh_time)
  {
    debug_instance_.startRefreshPropertiesThread(refresh_time);
  }


//----------------------------------------------------------------------
/**
 * The thread that rereads the debug property file is stopped, so any
 * changes in the file are ignored.
 */
  public static void stopRefreshPropertiesThread()
  {
    debug_instance_.stopRefreshPropertiesThread();
  }



//----------------------------------------------------------------------
/**
 * Writes all messages to a file.
 * @param filename the file to write to.
 */
  public static void setWriterToFile(String filename)
  {
    debug_instance_.setWriterToFile(filename);
  }


//----------------------------------------------------------------------
/**
 * Sets the writer the debug messages are written to. The default is
 * System.err.  
 * @param out the java.io.Writer (e.g. System.out, System.err, or a
 *   network connection), where all the messages are written to
 */
  public static void setWriter(Writer out)
  {
    debug_instance_.setWriter(out);
  }


//----------------------------------------------------------------------
/**
 * Returns the Writer, where the debug messages are written to.
 * @return the Writer, where the debug messages are written to.
 */
  public static Writer getWriter()
  {
    return(debug_instance_.getWriter());
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
 * E.g. <code>javac org.dinopolis.util.Debug --debuglevels test-level
 * --debugrefresh 5000 --debugfilename debug.log
 * "org.testpackage.testclass argument1 argument2 'argument with
 * space in single quote' --option1"</code>
 * 
 * @param args any debug arguments to set debug options and the
 * commandline to start the application (in quotes).
 */
  public static void main(String[] args)
  {
    org.dinopolis.util.debug.Debug.main(args);
  }

}




