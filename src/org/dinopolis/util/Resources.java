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


package org.dinopolis.util;

import java.awt.Color;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.MissingResourceException;
import javax.swing.Icon;

//----------------------------------------------------------------------
/**
 * This Interface is used to access any Resources. Resources may be
 * given in a file, database, memory or wherever. Resources may also
 * be locale-specific. (see {@link java.util.ResourceBundle} for
 * further details.)<p> 
 *
 * Stored resources can not only be requested as <code>Strings</code>,
 * but also in many different other types, like <code>integers</code>,
 * <code>StringArrays</code>, <code>booleans</code>, <code>Colors</code>,
 * <code>Icons</code> and others.<p>
 *
 * When your program needs a specific object, it loads the
 * <code>ResourceManager</code> class using one of the
 * <code>getResources</code> methods: 
 *
 * <blockquote><pre> Resources my_resources = ResourceManager.getResources(this,
 * "MyResources");</pre></blockquote> 
 *
 * If a lookup fails, <code>getBundle()</code> throws a
 * <code>MissingResourceException</code>.<p>
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

public interface Resources
{

  /** the delimiter used in resources for string arrays */
  public final static String KEY_DELIMITER = ".";

  /** the delimiter used in resources for string arrays */
  public final static String RESOURCE_STRING_ARRAY_DELIMITER = ",";
  
  /** the delimiter used for variable replacement. So '$key$' will be
   * replace by the value of 'key' */
  public final static String VAR_IDENTIFIER = "$";
  
  /** the length of VAR_IDENTIFIER */
  public final static int VAR_LENGTH = VAR_IDENTIFIER.length();

  /** the delimiter used for swallow replacs. eg.: '$%key%$' is
   * equivalent to 'key', but '$%k*%$' will be expanded to all keys of
   * the resource that start with the letter 'k'. '$k$' will be
   * expanded to all values of keys that start with the letter 'k'. */
  public final static String SWALLOW_IDENTIFIER = "%";
  
  /** the length of SWALLOW_IDENTIFIER */
  public final static int SWALLOW_LENGTH = SWALLOW_IDENTIFIER.length();

  /** the key of the 'resource.groups' property */
  public final static String GROUPS = "resource.groups";

  //----------------------------------------------------------------------
  /**
   * Removes the bound value for the given key, if no value was bound
   * under the given key, this method does nothing.
   *
   * @param key the key of the resource to delete.
   * @exception UnsupportedOperationException if the resources is not
   * capable of deleting values, or in particular the given key.
   */

  public void unset(String key)
    throws UnsupportedOperationException;

  //----------------------------------------------------------------------
  /**
   * Resets the bound value to its default value (if supported).
   *
   * @param key the key of the resource to reset.
   * @exception UnsupportedOperationException if the resources is not
   * capable of resetting values, or in particular the given key.
   */

  public void reset(String key)
    throws UnsupportedOperationException;

  //----------------------------------------------------------------------
  /**
   * Returns true, if this resources is capable of storing and
   * deleting resources, false otherwise.
   *
   * @return true, if this resources is capable of storing and
   * deleting resources, false otherwise.
   */

  public boolean isModificationSupported();

  //----------------------------------------------------------------------
  /**
   * Call this method to make all changes performed by unset and
   * setter methods persistent.
   *
   * @exception IOException in case of an IOError.
   * @exception UnsupportedOperationException if the resources is not
   * capable of persistently storing the resources.
   */

  public void store()
    throws IOException, UnsupportedOperationException;

  //----------------------------------------------------------------------
  /**
   * Returns the title for the given key. If no title for this key is
   * available, <code>null</code> is returned.
   *
   * @param key the key to get the title for.
   * @return the title for the given key.
   */

  public String getTitle(String key);

  //----------------------------------------------------------------------
  /**
   * Sets the title for the given key.
   * Deleted the title if title is 'null'.
   *
   * @param key the key to set the title for.
   * @param title the title to set.
   * @exception UnsupportedOperationException if setTitle operations
   * are not supported.
   * @exception IllegalArgumentException if key is 'null'.
   */

  public void setTitle(String key, String title)
    throws UnsupportedOperationException;

  //----------------------------------------------------------------------
  /**
   * Returns a string that describes the key and its possible values.
   * If no description for this key is available, <code>null</code> is
   * returned. 
   *
   * @param key the key to get the description for.
   * @return the description of the given key and its possible values.
   */

  public String getDescription(String key);

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
    throws UnsupportedOperationException;

  //----------------------------------------------------------------------
  /**
   * Returns the type of the value bound under the given key. Note
   * that the returned "Class" object may describe a built-in Java
   * type such as "int" (Integer.TYPE). For arrays, this may be e.g.:
   * <code>int[].class</code>. If no type for this key is available,
   * <code>null</code> is returned.
   *
   * @param key the key to get the type for.
   * @return the type of the value bound under the given key.
   */

  public Class getType(String key);

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
    throws UnsupportedOperationException;

  //----------------------------------------------------------------------
  /**
   * Returns an array of all values that are valid. This is usefull if
   * a value may be choosen out of a predifined set of possible
   * values.
   * If no set of valid values exists <code>null</code> is returned.
   *
   * @param key the key to get the type for.
   * @return the possible values.
   */

  public String[] getPossibleValues(String key);

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
    throws UnsupportedOperationException;

  //----------------------------------------------------------------------
  /**
   * Returns the native value loaded from the resource bundle. If variables
   * are given in the value, they will be replaced
   * recursively. Variables are Strings that start and end with a 
   * '$'. To escape the $-sign use $$. In case of a deadlock (a
   * contains variable b, and b contains variable a), the variable
   * will not be replaced. Variables that are not given within the
   * resources are also not replaced. The return value will be of type
   * String, if the Resources are not type save, which means, that the
   * getType() call with the given key returns <code>null</code>. If
   * the resource is invalid to its type (e.g.: "x" as Integer),
   * an IllegalArgumentException will be thrown.
   * 
   * @param key the key of the resource property to look for.
   * @return the native object loaded from the resource bundle.
   * @exception MissingResourceException if the given key is not defined
   * within the resources.
   * @exception IllegalArgumentException if key is 'null', or the
   * value is not of the registerd type.
   */
  
  public Object get(String key)
    throws MissingResourceException, IllegalArgumentException;

  //----------------------------------------------------------------------
  /**
   * Returns the string loaded from the resource bundle. If variables
   * are given in the value, they will be replaced
   * recursively. Variables are Strings that start and end with a 
   * '$'. To escape the $-sign use $$. In case of a deadlock (a
   * contains variable b, and b contains variable a), the variable
   * will not be replaced. Variables that are not given within the
   * resources are also not replaced. 
   * 
   * @param key the key of the resource property to look for.
   * @return the replaced string loaded from the resource bundle.
   * @exception MissingResourceException if the given key is not defined
   * within the resources.
   * @exception IllegalArgumentException if key is 'null'.
   */
  
  public String getString(String key)
    throws MissingResourceException;

  //----------------------------------------------------------------------
  /**
   * Returns the string loaded from the resource bundle. If the key
   * was not found within the resources, <code>default_value</code>
   * will be returned. Any variables found within the properties value
   * will be replaced.
   * 
   * @param key the key of the resource property to look for.
   * @param default_value the default value that will be returned if no
   * resource of the given key was found.
   * @return the string loaded from the resource bundle.
   * @see #getString(java.lang.String)
   */
  
  public String getString(String key, String default_value);

  //----------------------------------------------------------------------
  /**
   * Registers the given value under the given key.
   * 
   * @param key the key of the resource property to set.
   * @param value the value of the resource property to set.
   * @exception UnsupportedOperationException if set operations are
   * not supported.
   * @exception IllegalArgumentException if key or value is 'null'.
   */

  public void setString(String key, String value)
    throws UnsupportedOperationException;

  //----------------------------------------------------------------------
  /**
   * Returns the string array, loaded from the resource
   * bundle. Returns an empty array instead of <code>null</code>. Any
   * variables found within the properties value will be replaced.
   * 
   * @param key the key of the resource property to look for.
   * @return the string array, loaded from the resource bundle. Returns
   * an empty array instead of <code>null</code>.
   * @exception MissingResourceException if the given key is not defined
   * within the resources.
   */

  public String[] getStringArray(String key)
    throws MissingResourceException;

  //----------------------------------------------------------------------
  /**
   * Returns the string array, loaded from the resource
   * bundle. Returns an empty array instead of <code>null</code>. Any
   * variables found within the properties value will be replaced.
   * 
   * @param key the key of the resource property to look for.
   * @return the string array, loaded from the resource bundle. Returns
   * an empty array instead of <code>null</code>.
   * @exception MissingResourceException if the given key is not defined
   * within the resources.
   */

  public String[] getStringArray(String key, String delimiter)
    throws MissingResourceException;

  //----------------------------------------------------------------------
  /**
   * Returns the string array loaded from the resource bundle. If the
   * key was not found within the resources,
   * <code>default_values</code> will be returned. Any variables found
   * within the properties value will be replaced.
   * 
   * @param key the key of the resource property to look for.
   * @param default_value the default value that will be returned if no
   * resource of the given key was found.
   * @return the string array loaded from the resource bundle.
   */

  public String[] getStringArray(String key, String[] default_values);

  //----------------------------------------------------------------------
  /**
   * Registers the given values under the given key.
   * 
   * @param key the key of the resource property to set.
   * @param values the values of the resource property to set.
   * @exception UnsupportedOperationException if set operations are
   * not supported.
   * @exception IllegalArgumentException if key or values is 'null'.
   */

  public void setStringArray(String key, String[] values)
    throws UnsupportedOperationException;

  //----------------------------------------------------------------------
  /**
   * Registers the given values under the given key.
   * 
   * @param key the key of the resource property to set.
   * @param values the values of the resource property to set.
   * @param delimiter the delimiter to be used to seperate single
   * values. 
   * @exception UnsupportedOperationException if set operations are
   * not supported.
   * @exception IllegalArgumentException if key or values is 'null'.
   */

  public void setStringArray(String key, String[] values, String delimiter)
    throws UnsupportedOperationException;

  //----------------------------------------------------------------------
  /**
   * Returns the int loaded from the resource bundle. Any variables
   * found within the properties value will be replaced. 
   *
   * @param key the key of the resource property to look for.
   * @return the int loaded from the resource bundle.
   * @exception MissingResourceException if the given key is not defined
   * within the resources.
   * @exception NumberFormatException if the stored value does not
   * represent an int.
   */

  public int getInt(String key)
    throws MissingResourceException, NumberFormatException;

  //----------------------------------------------------------------------
  /**
   * Returns the int loaded from the resource bundle. If the key was
   * not found within the resources, <code>default_value</code> will
   * be returned. Any variables found within the properties value will
   * be replaced.
   *
   * @param key the key of the resource property to look for.
   * @param default_value the default value that will be returned if no
   * resource of the given key was found, or the found value is not of
   * type int.
   * @return the int loaded from the resource bundle.
   * @exception MissingResourceException if the given key is not defined
   * within the resources.
   * @exception NumberFormatException if the stored value does not
   * represent an int.
   */

  public int getInt(String key, int default_value);

  //----------------------------------------------------------------------
  /**
   * Registers the given value under the given key.
   * 
   * @param key the key of the resource property to set.
   * @param value the value of the resource property to set.
   * @exception UnsupportedOperationException if set operations are
   * not supported.
   * @exception IllegalArgumentException if key is 'null'.
   */

  public void setInt(String key, int value)
    throws UnsupportedOperationException;

  //----------------------------------------------------------------------
  /**
   * Returns the int array loaded from the resource bundle. Any
   * variables found within the properties value will be replaced.
   *
   * @param key the key of the resource property to look for.
   * @return the int array loaded from the resource bundle.
   * @exception MissingResourceException if the given key is not defined
   * within the resources.
   * @exception NumberFormatException if a stored value does not
   * represent an int.
   */

  public int[] getIntArray(String key)
    throws MissingResourceException, NumberFormatException;

  //----------------------------------------------------------------------
  /**
   * Returns the int array loaded from the resource bundle. If the key
   * was not found within the resources, <code>default_values</code>
   * will be returned. Any variables found within the properties value
   * will be replaced. 
   *
   * @param key the key of the resource property to look for.
   * @param default_values the default value that will be returned if no
   * resource of the given key was found, or a found value is not of
   * type int.
   * @return the int array loaded from the resource bundle.
   * @exception MissingResourceException if the given key is not defined
   * within the resources.
   * @exception NumberFormatException if a stored value does not
   * represent an int.
   */

  public int[] getIntArray(String key, int[] default_values);

  //----------------------------------------------------------------------
  /**
   * Registers the given values under the given key.
   * 
   * @param key the key of the resource property to set.
   * @param values the values of the resource property to set.
   * @exception UnsupportedOperationException if set operations are
   * not supported.
   * @exception IllegalArgumentException if key or values is 'null'.
   */

  public void setIntArray(String key, int[] values)
    throws UnsupportedOperationException;

  //----------------------------------------------------------------------
  /**
   * Returns the double loaded from the resource bundle. Any variables
   * found within the properties value will be replaced. 
   *
   * @param key the key of the resource property to look for.
   * @return the double loaded from the resource bundle.
   * @exception MissingResourceException if the given key is not defined
   * within the resources.
   * @exception NumberFormatException if the stored value does not
   * represent an double.
   */

  public double getDouble(String key)
    throws MissingResourceException, NumberFormatException;

  //----------------------------------------------------------------------
  /**
   * Returns the double loaded from the resource bundle. If the key was
   * not found within the resources, <code>default_value</code> will
   * be returned. Any variables found within the properties value will
   * be replaced.
   *
   * @param key the key of the resource property to look for.
   * @param default_value the default value that will be returned if no
   * resource of the given key was found, or the found value is not of
   * type double.
   * @return the double loaded from the resource bundle.
   * @exception MissingResourceException if the given key is not defined
   * within the resources.
   * @exception NumberFormatException if the stored value does not
   * represent an double.
   */

  public double getDouble(String key, double default_value);

  //----------------------------------------------------------------------
  /**
   * Registers the given value under the given key.
   * 
   * @param key the key of the resource property to set.
   * @param value the value of the resource property to set.
   * @exception UnsupportedOperationException if set operations are
   * not supported.
   * @exception IllegalArgumentException if key is 'null'.
   */

  public void setDouble(String key, double value)
    throws UnsupportedOperationException;

  //----------------------------------------------------------------------
  /**
   * Returns the double array loaded from the resource bundle. Any
   * variables found within the properties value will be replaced.
   *
   * @param key the key of the resource property to look for.
   * @return the double array loaded from the resource bundle.
   * @exception MissingResourceException if the given key is not defined
   * within the resources.
   * @exception NumberFormatException if a stored value does not
   * represent an double.
   */

  public double[] getDoubleArray(String key)
    throws MissingResourceException, NumberFormatException;

  //----------------------------------------------------------------------
  /**
   * Returns the double array loaded from the resource bundle. If the key
   * was not found within the resources, <code>default_values</code>
   * will be returned. Any variables found within the properties value
   * will be replaced. 
   *
   * @param key the key of the resource property to look for.
   * @param default_values the default value that will be returned if no
   * resource of the given key was found, or a found value is not of
   * type double.
   * @return the double array loaded from the resource bundle.
   * @exception MissingResourceException if the given key is not defined
   * within the resources.
   * @exception NumberFormatException if a stored value does not
   * represent an double.
   */

  public double[] getDoubleArray(String key, double[] default_values);

  //----------------------------------------------------------------------
  /**
   * Registers the given values under the given key.
   * 
   * @param key the key of the resource property to set.
   * @param values the values of the resource property to set.
   * @exception UnsupportedOperationException if set operations are
   * not supported.
   * @exception IllegalArgumentException if key or values is 'null'.
   */

  public void setDoubleArray(String key, double[] values)
    throws UnsupportedOperationException;



  //----------------------------------------------------------------------
  /**
   * Returns the boolean loaded from the resource bundle. Boolean value
   * that are interpreted as <code>true</code> within the resource file
   * are: "true", "True", "yes", "Yes" and "1". All other values will be
   * interpreted to be <code>false</code>. Any variables found within
   * the properties value will be replaced. 
   *
   * @param key the key of the resource property to look for.
   * @return the boolean loaded from the resource bundle.
   * @exception MissingResourceException if the given key is not defined
   * within the resources.
   */

  public boolean getBoolean(String key)
    throws MissingResourceException;

  //----------------------------------------------------------------------
  /**
   * Returns the boolean loaded from the resource bundle. Boolean
   * value that are interpreted as <code>true</code> within the
   * resource file are: "true", "True", "yes", "Yes" and "1". All
   * other values will be interpreted to be <code>false</code>. If the
   * key was not found within the resources,
   * <code>default_value</code> will be returned. Any variables found
   * within the properties value will be replaced.
   *
   * @param key the key of the resource property to look for.
   * @param default_value the default value that will be returned if no
   * resource of the given key was found.
   * @return the boolean loaded from the resource bundle.
   */

  public boolean getBoolean(String key, boolean default_value);

  //----------------------------------------------------------------------
  /**
   * Registers the given value under the given key.
   * 
   * @param key the key of the resource property to set.
   * @param value the value of the resource property to set.
   * @exception UnsupportedOperationException if set operations are
   * not supported.
   * @exception IllegalArgumentException if key is 'null'.
   */

  public void setBoolean(String key, boolean value)
    throws UnsupportedOperationException;

  //----------------------------------------------------------------------
  /**
   * Returns the icon loaded from the resource bundle. The filename of
   * the icon in the resource file has to be relative to the resource
   * file. Any variables found within the properties value will be
   * replaced.
   *
   * @param key the key of the resource property to look for.
   * @return the icon loaded from the resource bundle.
   * @exception MissingResourceException if the given key is not defined
   * within the resources.
   */

  public Icon getIcon(String key)
    throws MissingResourceException;

  //----------------------------------------------------------------------
  /**
   * Returns the icon loaded from the resource bundle. Icon value
   * that are interpreted as <code>true</code> within the resource file
   * are: "true", "True", "yes", "Yes" and "1". All other values will be
   * interpreted to be <code>false</code>. If the key was not found
   * within the resources, <code>default_value</code> will be
   * returned. Any variables found within the properties value will be
   * replaced.
   *
   * @param key the key of the resource property to look for.
   * @param default_value the default value that will be returned if no
   * resource of the given key was found.
   * @return the icon loaded from the resource bundle.
   */

  public Icon getIcon(String key, Icon default_value);

  //----------------------------------------------------------------------
  /**
   * Returns the color loaded from the resource bundle. Any variables
   * found within the properties value will be replaced. Possible
   * values for colors are: "white", "blue",... and "r,g,b" values
   * like "255,96,96". 
   *
   * @param key the key of the resource property to look for.
   * @return the color loaded from the resource bundle.
   * @exception MissingResourceException if the given key is not defined
   * within the resources.
   */

  public Color getColor(String key)
    throws MissingResourceException;

  //----------------------------------------------------------------------
  /**
   * Returns the color loaded from the resource bundle. Any variables
   * found within the properties value will be replaced. Possible
   * values for colors are: "white", "blue",... and "r,g,b" values
   * like 255,96,96. If the key was not found within the resources,
   * <code>default_value</code> will be returned.
   *
   * @param key the key of the resource property to look for.
   * @param default_value the default value that will be returned if no
   * resource of the given key was found.
   * @return the color loaded from the resource bundle.
   */
  
  public Color getColor(String key, Color default_value);

  //----------------------------------------------------------------------
  /**
   * Registers the given value under the given key.
   * 
   * @param key the key of the resource property to set.
   * @param value the value of the resource property to set.
   * @exception UnsupportedOperationException if set operations are
   * not supported.
   * @exception IllegalArgumentException if key or value is 'null'.
   */

  public void setColor(String key, Color value)
    throws UnsupportedOperationException;

  //----------------------------------------------------------------------
  /**
   * Returns the file loaded from the resource bundle. Any variables
   * found within the properties value will be replaced. 
   *
   * @param key the key of the resource property to look for.
   * @return the file that is loaded from the resource bundle.
   */

  public File getFile(String key);

  //----------------------------------------------------------------------
  /**
   * Returns the file loaded from the resource bundle. Any variables
   * found within the properties value will be replaced. If the key
   * was not found within the resources, <code>default_value</code>
   * will be returned. 
   *
   * @param key the key of the resource property to look for.
   * @param default_value the default value that will be returned if no
   * resource of the given key was found.
   * @return the file that is loaded from the resource bundle.
   */

  public File getFile(String key, File default_value);

  //----------------------------------------------------------------------
  /**
   * Returns the Url loaded from the resource bundle. Any variables
   * found within the properties value will be replaced. 
   *
   * @param key the key of the resource property to look for.
   * @return the Url that is loaded from the resource bundle.
   * @exception MissingResourceException if the given key is not defined
   * within the resources, or is not a valid Url.
   */

  public URL getURL(String key)
    throws MissingResourceException;

  //----------------------------------------------------------------------
  /**
   * Returns the Url loaded from the resource bundle. Any variables
   * found within the properties value will be replaced. If the key
   * was not found within the resources, <code>default_value</code>
   * will be returned. 
   *
   * @param key the key of the resource property to look for.
   * @param default_value the default value that will be returned if no
   * resource of the given key was found.
   * @return the Url that is loaded from the resource bundle.
   */

  public URL getURL(String key, URL default_value);

  //----------------------------------------------------------------------
  /**
   * Returns an Enumeration containing all keys of all resources.
   *
   * @return an Enumeration containing all keys of all resources.
   */

  public Enumeration getKeys();

  //----------------------------------------------------------------------
  /**
   * Add a PropertyChangeListener to the listener list.
   * The listener is registered for all properties.
   *
   * @param listener The PropertyChangeListener to be added.
   */
  
  public void addPropertyChangeListener(PropertyChangeListener
                                        listener);

  //----------------------------------------------------------------------
  /**
   * Remove a PropertyChangeListener from the listener list.
   * This removes a PropertyChangeListener that was registered
   * for all properties.
   *
   * @param listener The PropertyChangeListener to be removed
   */

  public void removePropertyChangeListener(PropertyChangeListener
                                           listener);

  //----------------------------------------------------------------------
  /**
   * Add a PropertyChangeListener for a specific property. The listener
   * will be invoked only when a call on firePropertyChange names that
   * specific property.
   *
   * @param property_name The name of the property to listen on.
   * @param listener The PropertyChangeListener to be added
   */

  public void addPropertyChangeListener(String property_name,
                                        PropertyChangeListener
                                        listener);

  //----------------------------------------------------------------------
  /**
   * Remove a PropertyChangeListener for a specific property.
   *
   * @param property_name The name of the property that was listened on.
   * @param listener The PropertyChangeListener to be removed
   */

  public void removePropertyChangeListener(String property_name,
                                           PropertyChangeListener
                                           listener);
}











