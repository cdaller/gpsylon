/***********************************************************************
 * @(#)$RCSfile$   $Revision$ $Date$
 *
 * Copyright (c) 2001 IICM, Graz University of Technology
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


package org.dinopolis.util.resource;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.WeakHashMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.dinopolis.util.Debug;

//----------------------------------------------------------------------
/**
 * This class provides the functionality to read resources from a
 * system or user resource file. The way that this is done is: It
 * looks for a resource file within the users home directory and if a
 * proper file is found, it looks for the specific proporty. If no
 * file or the specific property was not found, the system resource
 * file is being asked for the property.<p>
 *
 * Stored resources can not only be requested as <code>Strings</code>,
 * but also in many different other types, like <code>integers</code>,
 * <code>StringArrays</code>, <code>booleans</code>, <code>Colors</code>,
 * <code>Icons</code> and others.<p>
 *
 * Resource files contain key/value pairs. The keys uniquely
 * identify a locale-specific object in the bundle. Here's an
 * example resource file that contains two key/value pairs:
 * <blockquote>
 * <pre>
 * my_app.dimension.width=300
 * my_app.dimension.height=200
 * </pre>
 * </blockquote>
 *
 * Keys are always <code>String</code>s.
 * In this example, the keys are <code>my_app.dimension.width</code>
 * and <code>my_app.dimension.height</code>.
 *
 * In the above example, the values
 * are also <code>String</code>s--<code>200</code> and <code>300</code>--but
 * they may also be interpreted as integer values using the method
 * {@link #getInt}.<p>
 *
 * The single argumented getter methods all require the key as an
 * argument and return the object if found. If the object is not
 * found, the getter method throws a
 * <code>MissingResourceException</code>. To avoid this behavior, it
 * is also possible to use the corresponding methods that take two
 * arguments, the key and the default value. If no property was bound
 * under the given key, the default value will be returned instead of
 * throwing a <code>MissingResourceException</code>.
 *
 * @author Dieter Freismuth
 * @version $Revision$
 */

public class FileResources extends AbstractResources
{
  /** the property filename extension */
  private final static String PROPERTY_EXTENSION = ".properties";

  /** the title suffix */
  private final static String TITLE_SUFFIX = ".title";

  /** the description suffix */
  private final static String DESCRIPTION_SUFFIX = ".description";

  /** the possible values suffix */
  private final static String POSSIBLE_VALUES_SUFFIX = ".possible_values";

  /** the type suffix */
  private final static String TYPE_SUFFIX = ".type";

  /** key - value seperators */
  private static final String KEY_VALUE_SEPARATORS = "=: \t\r\n\f";

  /** whitespace charakters */
  private final static String WHITE_SPACE_CHARS = " \t\r\n\f";

  /** the resource bundle */
  private ResourceBundle system_bundle_;

  /** the users private resource bundle */
  private Properties user_properties_;

  /** the file that holds the users resources */
  private File user_resource_file_;

  /** the system resource base dir */
  private String user_resource_base_name_;

  /** the user resource base dir */
  private String system_resource_base_name_;

  /** the mapping holding all requested FileResources */
  private static WeakHashMap mapping_ = new WeakHashMap();

  //----------------------------------------------------------------------
  /**
   * Creates a new FileResources class.
   *
   * @param user_bundle the user resource bundle.
   * @param user_resource_base_name_ the file name of the user_bundles
   * directory.
   * @param system_bundle the system resource bundle.
   * @param system_resource_base_name_ the file name of the
   * system_bundles directory.
   */

  private FileResources(File user_resource_file,
                        Properties user_bundle, 
                        String user_resource_base_name,
                        ResourceBundle system_bundle,
                        String system_resource_base_name)
  {
    user_resource_file_ = user_resource_file;
    user_properties_ = user_bundle;
    if (user_properties_ == null)
      user_properties_ = new Properties();
    user_resource_base_name_ = user_resource_base_name;
    system_bundle_ = system_bundle;
    system_resource_base_name_ = system_resource_base_name;
  }

  //----------------------------------------------------------------------
  /**
   * Get the appropriate FileResources for the given base_name.
   *
   * @param base_name the base name of the resource bundle.
   * @return the Resource.
   * @exception MissingResourceException if the system resource file
   * could not be located. 
   * @see #getResources(java.lang.Class,java.lang.String,java.lang.String,java.util.Locale)
   */
 
  public static FileResources getResources(String base_name)
    throws MissingResourceException
  {
    return(getResources(null, base_name, base_name, null));
  }

  //----------------------------------------------------------------------
  /**
   * Get the appropriate FileResources for the given base_name and
   * searches the system bundle in the callers package name.
   * See also
   * {@link #getResources(java.lang.Class,java.lang.String,java.lang.String,java.util.Locale)} 
   *
   * @param caller the caller, to search the system bundle for.
   * @param base_name the base name of the resource bundle.
   * @return the Resource.
   * @exception MissingResourceException if the system resource file
   * @see #getResources(java.lang.Class,java.lang.String,java.lang.String,java.util.Locale)
   */
 
  public static FileResources getResources(Class caller, 
                                           String base_name)
    throws MissingResourceException
  {
    return(getResources(caller, base_name, base_name, null));
  }

  //----------------------------------------------------------------------
  /**
   * Get the appropriate FileResources for the given base_name and the
   * given locale.
   *
   * @param base_name the base name of the resource bundle.
   * @param locale the locale.
   * @return the Resource.
   * @exception MissingResourceException if the system resource file
   * could not be located. 
   * @see #getResources(java.lang.Class,java.lang.String,java.lang.String,java.util.Locale)
   */

  public static FileResources getResources(String base_name,
                                           Locale locale)
    throws MissingResourceException
  {
    return(getResources(null, base_name, base_name, locale));
  }

  //----------------------------------------------------------------------
  /**
   * Get the appropriate FileResources for the given base_name and locale
   * and searches the system bundle in the callers package name. See
   * also {@link
   * #getResources(java.lang.Class,java.lang.String,java.lang.String,java.util.Locale)}  
   *
   * @param caller the caller, to search the system bundle for.
   * @param base_name the base name of the resource bundle.
   * @param locale the locale.
   * @return the Resource.
   * @exception MissingResourceException if the system resource file
   * could not be located. 
   * @see #getResources(java.lang.Class,java.lang.String,java.lang.String,java.util.Locale)
   */

  public static FileResources getResources(Class caller, 
                                           String base_name,
                                           Locale locale)
    throws MissingResourceException
  {
    return(getResources(caller, base_name, base_name, locale));
  }


  //----------------------------------------------------------------------
  /**
   * Get the appropriate FileResources for the given base_name and the
   * given dir_name. The dir_name specifies the directory name of the
   * users resource file, relative to the users home directory.
   *
   * @param base_name the base name of the resource bundle.
   * @param dir_name the name of the directory within the users homedir
   * to look for a property file.
   * @return the Resource.
   * @exception MissingResourceException if the system resource file
   * could not be located. 
   * @see #getResources(java.lang.Class,java.lang.String,java.lang.String,java.util.Locale)
 */
 
  public static FileResources getResources(String base_name, 
                                           String dir_name)
    throws MissingResourceException
  {
    return(getResources(null, base_name, dir_name, null));
  }

  //----------------------------------------------------------------------
  /**
   * Get the appropriate FileResources for the given base_name and the
   * given dir_name and searches the system bundle in the callers
   * package name. The dir_name specifies the directory name of the
   * users resource file, relative to the users home directory.
   *
   * @param caller the caller, to search the system bundle for.
   * @param base_name the base name of the resource bundle.
   * @return the Resource.
   * @param dir_name the name of the directory within the users homedir
   * to look for a property file.
   * @exception MissingResourceException if the system resource file
   * could not be located. 
   * @see #getResources(java.lang.Class,java.lang.String,java.lang.String,java.util.Locale)
   */
 
  public static FileResources getResources(Class caller, 
                                           String base_name,
                                           String dir_name)
    throws MissingResourceException
  {
    return(getResources(caller, base_name, dir_name, null));
  }

  //----------------------------------------------------------------------
  /**
   * Get the appropriate FileResources for the given base_name, the given
   * dir_name and the given locale. The dir_name specifies the
   * directory name of the users resource file, relative to the users
   * home directory.
   *
   * @param base_name the base name of the resource bundle.
   * @param locale the locale.
   * @param dir_name the name of the directory within the users homedir
   * to look for a property file.
   * @return the Resource.
   * @exception MissingResourceException if the system resource file
   * could not be located. 
   * @see #getResources(java.lang.Class,java.lang.String,java.lang.String,java.util.Locale)
   */

  public static FileResources getResources(String base_name, 
                                           String dir_name,
                                           Locale locale)
    throws MissingResourceException
  {
    return(getResources(null, base_name, dir_name, locale));
  }

  //----------------------------------------------------------------------
  /**
   * Get the appropriate ResourceBundle. The ResourceBundle consists
   * of two parts, the system resources (for default settings) and
   * user specific settings. Any given properties in the users
   * resources will overwrite the system resources!  The users
   * resources are searched in the following order:
   * $user_home/<code>dir_name</code>/<code>base_name</code>.properties,
   * $user_home/.<code>dir_name</code>/<code>base_name</code>.properties,
   * $user_home/<code>dir_name</code>.toLowerCase()/<code>base_name</code>.properties,
   * $user_home/.<code>dir_name</code>.toLowerCase()/<code>base_name</code>.properties<p>
   * 
   * The system resources are expected to be in the same package than
   * the caller!<p>
   *
   * Example:<br>
   * caller: <code>org.dinopolis.util.FileResources</code><br>
   * base_name: <code>TestFileResources</code><br>
   * dir_name: <code>test_resources</code><br>
   * locale: <code>Locale.getDefault()</code><br>
   * users home dir: ~dfreis<p>
   *
   * then, the users bundle is suspected to be:<br>
   * "~dfreis/test_resources/TestFileResources.properties", or if not found:<br>
   * "~dfreis/.test_resources/TestFileResources.properties"
   *
   * the system resources are expected to be in:<br>
   * "org.dinopolis.util.FileResources.TestFileResources.properties"
   * within your classpath!<p>
   * 
   * The sample code of the caller for the given example will look
   * like this:
   * <xmp>
   * FileResources resources = FileResources.getResources(getClass(), "TestFileResources", 
   *                                           "test_resources");
   * </xmp>
   *
   * @param caller the caller, to search the system bundle for.
   * @param base_name the base name of the resource bundle.
   * @param dir_name the name of the directory within the users homedir
   * to look for a property file.
   * @param locale the locale.
   * @return the Resource.
   * @exception MissingResourceException if the system resource file
   * could not be located.
   */

  public static FileResources getResources(Class caller, 
                                           String base_name,
                                           String dir_name,
                                           Locale locale)
    throws MissingResourceException
  {
    String system_resource_base_name =
      getSystemResourceBaseName(caller);
    String user_resource_base_name = getUserResourceBaseName(dir_name);
    File user_resource_file = getUsersResourceFile(base_name, user_resource_base_name);
    
    String key = locale+":"+base_name+":"+system_resource_base_name+ 
      ":"+user_resource_base_name; 
    FileResources bound = (FileResources)mapping_.get(key);
    if (bound == null)
    {
      bound = new FileResources(user_resource_file,
                                getUsersResourceBundle(user_resource_file),
                                user_resource_base_name,
                                getSystemResourceBundle(system_resource_base_name, base_name, locale),
                                system_resource_base_name);
      mapping_.put(key, bound);
    }
    return(bound);
  }

  //----------------------------------------------------------------------
  /**
   * Returs the base name of the system resource bundle. The base name
   * corresponds to the callers package name.
   *
   * @param caller the caller.
   * @return the base name of the resource bundle.
   */

  protected static String getSystemResourceBaseName(Class caller)
  {
    if (caller == null)
      return("");
    String caller_package = caller.getName();
    int las_dot = caller_package.lastIndexOf('.');
    if (las_dot > 0) 
      caller_package = caller_package.substring(0, las_dot);
    else
      caller_package = "";
    return(caller_package.replace('.','/'));
  }

  //----------------------------------------------------------------------
  /**
   * Returns the system resource bundle.
   *
   * @param system_resource_base_dir he base directory of the system
   * resource bundle.
   * @param base_name he base name of the resource file.
   * @param locale the locale.
   * @return the system resource bundle if found.
   * @exception MissingResourceException if the system resource file
   * could not be located. 
   */

  protected static ResourceBundle getSystemResourceBundle(String system_resource_base_dir,
                                                          String base_name,
                                                          Locale locale)
    throws MissingResourceException
  {
    String resource = system_resource_base_dir;
    if (resource.length() > 0)
      resource += ".";
    resource += base_name;
    if (locale != null)
      return(ResourceBundle.getBundle(resource, locale));
    return(ResourceBundle.getBundle(resource));
  }

  //----------------------------------------------------------------------
  /**
   * Returns the path of the users resource bundle.
   * The Resource bundle is searched in the following order:
   * $user_home/<code>dir_name</code>,
   * $user_home/.<code>dir_name</code>,
   * $user_home/<code>dir_name</code>.toLowerCase(),
   * $user_home/.<code>dir_name</code>.toLowerCase().
   * 
   * @param dir_name the name of the directory within the users homedir
   * to look for a property file.
   * @return the base name of the resource bundle, or null if no dir was
   * found.
   */

  protected static String getUserResourceBaseName(String dir_name)
  {
    File home_dir = new File(System.getProperty("user.home"));
    File resource_dir = new File(home_dir, dir_name);
    if (!resource_dir.isDirectory())
      resource_dir = new File(home_dir, "."+dir_name); // add a dot in front
    if (!resource_dir.isDirectory())
      resource_dir = new File(home_dir, dir_name.toLowerCase()); // try it lowercased
    if (!resource_dir.isDirectory())
      resource_dir = new File(home_dir, "."+dir_name.toLowerCase()); // try it lowercased and added dot
    
    if (!resource_dir.isDirectory())
      // directory not found! -> use standard way
      resource_dir = new File(home_dir, dir_name.toLowerCase());
    return(resource_dir.getPath());
  }

  //----------------------------------------------------------------------
  /**
   * Returns the user resource file. located in
   * <code>user_resource_dir</code> with the base name
   * <code>base_name</code>.
   *
   * @param base_name he base name of the users resource file.
   * @param user_resource_dir the name of the directory within the users
   * homedir to look for a property file.
   * @return the resource file found in the users home dir.
   */

  protected static File getUsersResourceFile(String base_name, 
                                             String user_resource_dir)
  {
    File resource_dir;
    if (user_resource_dir == null)
      resource_dir = new File(System.getProperty("user.home"));
    else
      resource_dir = new File(user_resource_dir);
    return(new File(resource_dir, base_name+PROPERTY_EXTENSION));
  }

  //----------------------------------------------------------------------
  /**
   * Returns the user resource bundle.
   *
   * @param user_resource_file the users resource file
   * @return the resource bundle found in the users home dir, or null if
   * no resource file exists.
   */

  protected static Properties getUsersResourceBundle(File
                                                     user_resource_file)
  {
    if (!user_resource_file.isFile())
      return(null);

    try
    {
      Properties properties = new Properties();
      properties.load(new
        FileInputStream(user_resource_file));
      return(properties);
    }
    catch (FileNotFoundException exc)
    {
          // silently ignored
    }
    catch (IOException exc)
    {
          // silently ignored
    }
    return(null);
  }

  //----------------------------------------------------------------------
  /**
   * Returns true, if this resources is capable of storing and
   * deleting resources, false otherwise.
   *
   * @return true, if this resources is capable of storing and
   * deleting resources, false otherwise.
   */

  public boolean isModificationSupported()
  {
    return(true);
  }

  //----------------------------------------------------------------------
  /**
   * Gets the bound value for the given key.
   *
   * @param key the key of the resource property to look for.
   * @return the string loaded from the resource bundle.
   */
  
  protected synchronized String getValue(String key)
  {
    String value = user_properties_.getProperty(key);
    if (value != null)
      return(value);
    return(system_bundle_.getString(key)); // the system bundle
  }

  //----------------------------------------------------------------------
  /**
   * Returns an Enumeration containing all keys of all resources.
   *
   * @return an Enumeration containing all keys of all resources.
   */

  public Enumeration getKeys()
  {
    return(new FileResourcesEnumeration());
  }

  //----------------------------------------------------------------------
  /**
   * Registers the given value under the given key. Key and value are
   * garanteed to be non-null!
   * Overwrite this method in classes extending AbstractResources if
   * set methods are supported.
   *
   * @param key the key of the resource property to set.
   * @param value the value of the resource property to set.
   * 
   */
  
  protected synchronized void setValue(String key, String value)
  {
    //    System.err.println("setValue() key: "+key+", value: "+value);
    user_properties_.setProperty(key, value);
  }

  //----------------------------------------------------------------------
  /**
   * Removes the bound value for the given key, if no value was bound
   * under the given key, this method does nothing. Key is garanteed
   * to be non-null!
   * Overwrite this method in classes extending AbstractResources, if
   * remove is supported.
   *
   * @param key the key of the resource to delete.
   */

  protected synchronized void unsetValue(String key)
  {
    //   System.err.println("unsetValue() key: "+key);
    if(system_bundle_.getString(key) == null)
      user_properties_.remove(key);
    else
          // mark it with a special key, so it is unset from now on!
      user_properties_.put(key,"$"+UNSET_KEY+"$");
  }

  //----------------------------------------------------------------------
  /**
   * Resets the bound value for the given key to its default value. If
   * no value was bound under the given key, this method does
   * nothing. Key is garanteed to be non-null! Removes the value from
   * the user properties.
   *
   * @param key the key of the resource to reset.
   */

  protected void resetValue(String key)
  {
    user_properties_.remove(key);
  }
  
  //----------------------------------------------------------------------
  /**
   * Call this method to make all changes performed by unset and
   * setter methods persistent.
   * Overwrite this method in classes extending AbstractResources, if
   * modifications are supported. by default, this method throws an
   * UnsupportedOperationException.
   *
   * @exception IOException in case of an IOError.
   * @exception UnsupportedOperationException if the resources is not
   * capable of persistently storing the resources.
   */

  public synchronized void store()
    throws IOException, UnsupportedOperationException
  {
    String tmp_file_name = user_resource_file_.getName()+".tmp";
    File parent = user_resource_file_.getParentFile();
    if (!parent.exists())
      parent.mkdir();
        
    FileOutputStream file_out = new FileOutputStream(user_resource_file_);
    user_properties_.store(file_out, "auto generated file - by FileResources");
    file_out.close();
    return;
  }


  //----------------------------------------------------------------------
  /**
   * Returns the title for the given key. If no title for this key is
   * available, <code>null</code> is returned.
   * Overwrite this method in classes extending AbstractResources, if
   * titles are supported. by default, this method returns null.
   *
   * @param key the key to get the title for.
   * @return the title for the given key.
   */

  public String getTitle(String key)
  {
    return(getString(key+TITLE_SUFFIX, null));
  }

  //----------------------------------------------------------------------
  /**
   * Sets the title for the given key.
   * Deleted the title if title is 'null'.
   *
   * @param key the key to set the title for.
   * @param title the title to set.
   * @exception IllegalArgumentException if key is 'null'.
   * @exception UnsupportedOperationException if setTitle operations
   * are not supported.
   */

  public void setTitle(String key, String title)
    throws UnsupportedOperationException
  {
    if (title == null)
      unsetValue(key+TITLE_SUFFIX);
    setValue(key+TITLE_SUFFIX, title);
  }

  //----------------------------------------------------------------------
  /**
   * Returns a string that describes the key and its possible values.
   * If no description for this key is available, <code>null</code> is
   * returned. 
   * Overwrite this method in classes extending AbstractResources, if
   * descriptions are supported. by default, this method returns null.
   *
   * @param key the key to get the description for.
   * @return the description of the given key and its possible values.
   */

  public String getDescription(String key)
  {
    return(getString(key+DESCRIPTION_SUFFIX, null));
  }

  //----------------------------------------------------------------------
  /**
   * Sets the description for the given key.
   * Deleted the description if description is 'null'.
   *
   * @param key the key to set the description for.
   * @param description the description to set.
   * @exception UnsupportedOperationException if setDescription
   * operations are not supported.
   * @exception IllegalArgumentException if key is 'null'.
   */

  public void setDescription(String key, String description)
    throws UnsupportedOperationException
  {
    if (description == null)
      unsetValue(key+DESCRIPTION_SUFFIX);
    setValue(key+DESCRIPTION_SUFFIX, description);
  }

  //----------------------------------------------------------------------
  /**
   * Returns the type of the value bound under the given key. Note
   * that the returned "Class" object may describe a built-in Java
   * type such as "int" (Integer.TYPE). For arrays, this may be e.g.:
   * <code>int[].class</code>. If no type for this key is available,
   * <code>null</code> is returned.
   * Overwrite this method in classes extending AbstractResources, if
   * types are supported. by default, this method returns null.
   *
   * @param key the key to get the type for.
   * @return the type of the value bound under the given key.
   */

  public Class getType(String key)
  {
    String class_name = getString(key+TYPE_SUFFIX, null);
    return(getClassForType(class_name));
  }

  //----------------------------------------------------------------------
  /**
   * Sets the type for the given key.
   * Deleted the kype if type is 'null'.
   *
   * @param key the key to set the type for.
   * @param type the type to set.
   * @exception UnsupportedOperationException if setType operations
   * are not supported.
   * @exception IllegalArgumentException if key is 'null'.
   */

  public void setType(String key, Class type)
    throws UnsupportedOperationException
  {
    String class_name = getTypeForClass(type);
    if (class_name == null)
      unsetValue(key+TYPE_SUFFIX);
    else
      setValue(key+TYPE_SUFFIX, class_name);
  }

  //----------------------------------------------------------------------
  /**
   * Returns an array of all values that are valid. This is usefull if
   * a value may be choosen out of a predifined set of possible
   * values.
   * If no set of valid values exists <code>null</code> is returned.
   * Overwrite this method in classes extending AbstractResources, if
   * types is a set of chooseable values. by default, this method
   * returns null. 
   *
   * @param key the key to get the type for.
   * @return the possible values.
   */

  public String[] getPossibleValues(String key)
  {
    return(getStringArray(key+POSSIBLE_VALUES_SUFFIX,
                          (String[])null)); 
  }

  //----------------------------------------------------------------------
  /**
   * Sets the possible Values for the given key.
   * Deleted the possible Values if possible_values are 'null'.
   *
   * @param key the key to set the possible Values for.
   * @param possible_values the possible Values to set.
   * @exception UnsupportedOperationException if setPossibleValues
   * operations are not supported.
   * @exception IllegalArgumentException if is 'null'. 
   */

  public void setPossibleValues(String key, String[] possible_values)
    throws UnsupportedOperationException
  {
    if (possible_values == null)
      unsetValue(key+POSSIBLE_VALUES_SUFFIX);
    else
      setStringArray(key+POSSIBLE_VALUES_SUFFIX, possible_values);
  }

  //----------------------------------------------------------------------
  /**
   * Returns the Url loaded from the resource bundle. The key in the
   * resource file may be relative to the resource file. Any variables
   * found within the properties value will be replaced.
   *
   * @param key the key of the resource property to look for.
   * @return the Url that is loaded from the resource bundle.
   * @exception MissingResourceException if the given key is not defined
   * within the resources, or is not a valid Url.
   */

  public URL getURL(String key)
    throws MissingResourceException
  {
    String value = getString(key);
    if (value == null)
      return(null);
    try
    {
      return(new URL(value));
    }
    catch (MalformedURLException exc)
    {
      // try it from private resource file ...
      File file = new File(user_resource_base_name_, value);
      if (file.exists())
      {
        try
        {
          return(file.toURL());
        }
        catch (MalformedURLException exc2)
        {
          // ignored
        }
      }
      
      // try it with class loader
      URL ret = ClassLoader.getSystemResource(system_resource_base_name_+"/"+value);
      if (ret == null) // try it without base name
        ret = ClassLoader.getSystemResource(value);

      if (ret != null)
        return(ret);
      
      throw(new MissingResourceException("malformed URL '"+value+"'", 
                                         getClass().getName(), key));
    }
  }

  //----------------------------------------------------------------------
  /**
   * The Enumeration for this FileResources.
   *
   * @author Dieter Freismuth
   * @version $Revision$
   */

  class FileResourcesEnumeration implements Enumeration
  {
    Enumeration enum_;

    //----------------------------------------------------------------------
    /**
     */

    FileResourcesEnumeration()
    {
      HashSet added = new HashSet();
      Vector keys = new Vector();
      Enumeration enum = system_bundle_.getKeys();
      String key;
      while (enum.hasMoreElements())
      {
        key = (String)enum.nextElement();
        if ((!added.contains(key)) &&
            (!isSpecial(key)))
        {
          keys.add(key);
          added.add(key);
        }
      }
      enum = user_properties_.propertyNames();
      while (enum.hasMoreElements())
      {
        key = (String)enum.nextElement();
        if ((!added.contains(key)) &&
            (!isSpecial(key)))
        {
          keys.add(key);
          added.add(key);
        }
      }
      enum_ = keys.elements();
    }

    //----------------------------------------------------------------------
    /**
     * @param key the key to check for.
     * @return if the key is an extension to a resource, ending with
     * .type, .description, .title or .possible_values.
     */

    private boolean isSpecial(String key)
    {
      int delimiter = key.lastIndexOf(".");
      if (delimiter <= 0)
        return(false);
      if ((!key.endsWith(TITLE_SUFFIX)) &&
          (!key.endsWith(DESCRIPTION_SUFFIX)) &&
          (!key.endsWith(TYPE_SUFFIX)) &&
          (!key.endsWith(POSSIBLE_VALUES_SUFFIX)))
        return(false);
      return(true);
    }

    //----------------------------------------------------------------------
    /**
     * Tests if this enumeration contains more elements.
     *
     * @return  <code>true</code> if and only if this enumeration object
     *           contains at least one more element to provide;
     *          <code>false</code> otherwise.
     */

    public boolean hasMoreElements()
    {
      return(enum_.hasMoreElements());
    }

    //----------------------------------------------------------------------
    /**
     * Returns the next element of this enumeration if this enumeration
     * object has at least one more element to provide.
     *
     * @return     the next element of this enumeration.
     * @exception  NoSuchElementException  if no more elements exist.
     */
    public Object nextElement()
    {
      return(enum_.nextElement());
    }
  }
}











