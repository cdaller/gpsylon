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

import gnu.regexp.RE;
import gnu.regexp.REException;
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.dinopolis.util.Resources;
import java.util.Iterator;

//----------------------------------------------------------------------
/**
 * AbstractResources represents an abstract, base class that may be
 * userd by Other <code>Resources</code> implementations.
 *
 * Stored resources can not only be requested as <code>Strings</code>,
 * but also in many different other types, like <code>integers</code>,
 * <code>StringArrays</code>, <code>booleans</code>, <code>Colors</code>,
 * <code>Icons</code> and others.<p>
 *
 * Resource entries contain key/value pairs. The keys uniquely
 * identify a specific object in the bundle. Here's an example
 * resource that contains two key/value pairs:
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
 * Classes that do extend this class, only have to implement the
 * <code>getValue(String)</code> method which reads the
 * resource in form of a String. All other type-save getter methods
 * are handles by this class.
 *
 * Extending classes may also implement the setValue(String key,
 * String value) method if they are capable of persistently storing
 * resources. If this is the case, extending classes also have to
 * extend the <code>isSetSupported()</code> method. By default the
 * setValue method will throw an
 * <code>UnsupportedOperationException</code>. and the isSetSupported
 * method will return false.
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

public abstract class AbstractResources implements Resources
{

  /** the PropertyChangeSupport */
  private PropertyChangeSupport property_change_support_;

  /** deadlock dedection */
  private boolean dead_lock_detect_ = false;

  /** the null key */
  protected final static String NULL_KEY = "null";

  /** the unset key */
  protected final static String UNSET_KEY = "unset";

      /** attached resources */
  protected Vector attached_resources_;

  //----------------------------------------------------------------------
  /**
   * The Default Constructor.
   */

  public AbstractResources()
  {
    property_change_support_ = new PropertyChangeSupport(this);
  }

  //----------------------------------------------------------------------
  /**
   * Gets the bound value for the given key.
   * Overwrite this method in classes extending AbstractResources.
   *
   * @param key the key of the resource property to look for.
   * @return the string loaded from the resource bundle.
   * @exception MissingResourceException if the given key is not defined
   * within the resources.
   */

  protected abstract String getValue(String key)
    throws MissingResourceException;


  //----------------------------------------------------------------------
  /**
   * Gets the bound value for the given key from the resources and
   * from the attached resources.
   *
   * @param key the key of the resource property to look for.
   * @return the string loaded from the resource bundle.
   * @exception MissingResourceException if the given key is not defined
   * within the resources.
   */

  protected String getValueAllAttached(String key)
    throws MissingResourceException
  {
    MissingResourceException missing_exception = null;
    try
    {
      return(getValue(key));
    }
    catch(MissingResourceException mre)
    {
      missing_exception = mre;
    }

    if(attached_resources_ == null)
      throw missing_exception;

        // search in all merged resources for the given key:
    synchronized(attached_resources_)
    {
      Iterator resources_iterator = attached_resources_.iterator();
      while(resources_iterator.hasNext())
      {
        try
        {
          return(((Resources)resources_iterator.next()).getString(key));
        }
        catch(MissingResourceException mre)
        {
          missing_exception = mre;
        }
      }
    }
        // nowhere found, throw exception:
    throw missing_exception;
  }

  //----------------------------------------------------------------------
  /**
   * Returns an Enumeration containing all keys of all resources.
   *
   * @return an Enumeration containing all keys of all resources.
   */

  protected abstract Enumeration doGetKeys();

  //----------------------------------------------------------------------
  /**
   * Returns an Enumeration containing all keys of all resources.
   *
   * @return an Enumeration containing all keys of all resources.
   */

  public Enumeration getKeys()
  {
    Vector all_keys = new Vector();
    HashSet added = new HashSet();
    String key;
    Enumeration enum;
        // collect my keys:
    enum = doGetKeys();
    while(enum.hasMoreElements())
    {
      key = (String)enum.nextElement();
      if(!added.contains(key))
      {
        added.add(key);
        all_keys.add(key);
      }
    }
        // collect keys from my attached resources:
    if(attached_resources_ != null)
    {
      synchronized(attached_resources_)
      {
        Iterator resources_iterator = attached_resources_.iterator();
        while(resources_iterator.hasNext())
        {
          enum = ((Resources)resources_iterator.next()).getKeys();
          while(enum.hasMoreElements())
          {
            key = (String)enum.nextElement();
            if(!added.contains(key))
            {
              added.add(key);
              all_keys.add(key);
            }
          }
        }
      }
    }
    return(all_keys.elements());
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
   * @exception UnsupportedOperationException if the resources is not
   * capable of storing values.
   *
   */

  protected void setValue(String key, String value)
    throws UnsupportedOperationException
  {
    throw(new UnsupportedOperationException("set methods are not "+
                                            "supported by this"+
                                            "Resources class"));
  }

//----------------------------------------------------------------------
/**
 * Registers the given value under the given key on this resources or
 * on any attached resources (depending on which resource knows the
 * given key). If no known resources know the given key, it is set in
 * this resources.
 *
 *
 * @param key the key of the resource property to set.
 * @param value the value of the resource property to set.
 * @exception UnsupportedOperationException if the resources is not
 * capable of storing values.
 */

  protected void setValueAllAttached(String key, String value)
    throws UnsupportedOperationException
  {
        // find out, if key is available in my or attached resources:
    Resources resources = findResourcesForKey(key);
    if((resources == null) || (resources == this))
      setValue(key, value);
    else
      resources.setString(key, value);
  }
  
//----------------------------------------------------------------------
/**
 * Finds the resources that do not throw a MissintResourceException for
 * the given key. Returns null, if none (not myself and no attached
 * resources are found.)
 *
 * @param key the key of the resource to find in the resources.
 * @return the first resource that did not throw a
 * MissingResourceException or null, if none was found.
 */
  protected Resources findResourcesForKey(String key)
  {
    String value;
        // try myself:
    try
    {
      value = getValue(key);
          // yes, I have got the key!
      return(this);
    }
    catch(MissingResourceException ignored) {}

        // try other resources:
    if(attached_resources_ != null)
    {
      synchronized(attached_resources_)
      {
        Resources resources;
        Iterator resources_iterator = attached_resources_.iterator();
        while(resources_iterator.hasNext())
        {
          resources = (Resources)resources_iterator.next();
          try
          {
            value = resources.getString(key);
                // yes, the resources have got the key, return it:
            return(resources);
          }
          catch(MissingResourceException ignored) {}
        }
      }
    }
        // no one has the key:
    return(null);
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
 * @exception UnsupportedOperationException if the resources is not
 * capable of deleting values.
 */

  protected void unsetValue(String key)
    throws UnsupportedOperationException
  {
    throw(new UnsupportedOperationException("remove methods are not "+
                                            "supported by this"+
                                            "Resources class"));
  }

//----------------------------------------------------------------------
/**
 * Removes the bound value for the given key on this resources or on
 * any attached resources (depending on which resource knows the given
 * key). If no known resources know the given key, it is removed in
 * this resources.
 *
 *
 * @param key the key of the resource property to set.
 * @exception UnsupportedOperationException if the resources is not
 * capable of storing values.
 */

  protected void unsetValueAllAttached(String key)
    throws UnsupportedOperationException
  {
        // find out, if key is available in my or attached resources:
    Resources resources = findResourcesForKey(key);
    if((resources == null) || (resources == this))
      unsetValue(key);
    else
      resources.unset(key);

  }

//----------------------------------------------------------------------
/**
 * Resets the bound value for the given key to its default value. If
 * no value was bound under the given key, this method does
 * nothing. Key is garanteed to be non-null!  Overwrite this method
 * in classes extending AbstractResources, if reset is supported.
 *
 * @param key the key of the resource to reset.
 * @exception UnsupportedOperationException if the resources is not
 * capable of resetting values.
 */

  protected void resetValue(String key)
    throws UnsupportedOperationException
  {
    throw(new UnsupportedOperationException("reset methods are not "+
                                            "supported by this"+
                                            "Resources class"));
  }


//----------------------------------------------------------------------
/**
 * Resets the given value under the given key on this resources or
 * on any attached resources (depending on which resource knows the
 * given key). If no known resources know the given key, it is reset in
 * this resources.
 *
 *
 * @param key the key of the resource property to set.
 * @exception UnsupportedOperationException if the resources is not
 * capable of storing values.
 */
  protected void resetValueAllAttached(String key)
    throws UnsupportedOperationException
  {
        // find out, if key is available in my or attached resources:
    Resources resources = findResourcesForKey(key);
    if((resources == null) || (resources == this))
      resetValue(key);
    else
      resources.reset(key);

  }

  //----------------------------------------------------------------------
  /**
   * Overwrite this method in classes extending AbstractResources, if
   * modifications are supported. by default, this method throws an
   * UnsupportedOperationException.
   *
   * @exception IOException in case of an IOError.
   * @exception UnsupportedOperationException if the resources is not
   * capable of persistently storing the resources.
   */
  protected void doStore()
    throws IOException, UnsupportedOperationException
  {
    throw(new UnsupportedOperationException("storing is not "+
                                            "supported by this"+
                                            "Resources class"));
  }

  //----------------------------------------------------------------------
  /**
   * Call this method to make all changes performed by unset and
   * setter methods persistent. Overwrite the {@link #doStore()}
   * method in classes extending AbstractResources, if modifications
   * are supported. Do NOT overwrite this method as otherwise the
   * attached resources are not stored anymore.
   *
   * @exception IOException in case of an IOError.
   * @exception UnsupportedOperationException if the resources is not
   * capable of persistently storing the resources.
   */

  public void store()
    throws IOException, UnsupportedOperationException
  {
    doStore();
    if(attached_resources_ == null)
      return;
    UnsupportedOperationException exception = null;
    synchronized(attached_resources_)
    {
      Iterator resources_iterator = attached_resources_.iterator();
      while(resources_iterator.hasNext())
      {
        try
        {
          ((Resources)resources_iterator.next()).store();
        }
        catch(UnsupportedOperationException e)
        {
          exception = e;
        }
      }
    }
    if(exception != null)
      throw exception;
  }

  //----------------------------------------------------------------------
  /**
   * Attach another set of resources. After they are attached, all
   * getXXX() operations are able to read the values from the
   * resources as well as the attached resources. To set any values
   * (add new or change old values), the methods of the original
   * resources must be used or otherwise the keys/values are contained
   * in this resources (new value) and in the attached resources (old
   * values).
   * <p>
   * As soon as additional resources are attached, the getXXX()
   * methods read from them. If there are duplicate keys in the
   * resources and in the attached resources, the original resources
   * have priority (take care!!).
   * <p>
   * The {@link #store()} method calls <code>store()<code> on all
   * attached resources as well.
   * <p>
   * Attaching resources has the advantage that read access to
   * resources is easy for different resources (e.g. in the resource
   * editor), but the place where the resources are stored can still
   * be held separated.
   *
   * @param resources the resources to attach
   * @exception UnsupportedOperationException if the resources is not
   * capable of attaching other resources.
   */

  public void attachResources(Resources resources)
    throws UnsupportedOperationException
  {
    if(attached_resources_ == null)
      attached_resources_ = new Vector();
    synchronized(attached_resources_)
    {
      attached_resources_.add(resources);
    }
  }

  //----------------------------------------------------------------------
  /**
   * Detach previously attached resources.
   *
   * @param resources the resources to detach.
   * @exception UnsupportedOperationException if the resources is not
   * capable of attaching other resources.
   */

  public void detachResources(Resources resources)
    throws UnsupportedOperationException
  {
    if(attached_resources_ == null)
      return;
    synchronized(attached_resources_)
    {
      attached_resources_.remove(resources);
    }
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
    return(null);
  }

  //----------------------------------------------------------------------
  /**
   * Sets the title for the given key.
   * Overwrite this method in classes extending AbstractResources, if
   * titles are supported. by default, this method throws an
   * UnsupportedOperationException.
   *
   * @param key the key to set the title for.
   * @param title the title to set.
   * @exception UnsupportedOperationException if setTitle operations
   * are not supported.
   * @exception IllegalArgumentException if key or title is 'null'.
   */

  public void setTitle(String key, String title)
    throws UnsupportedOperationException
  {
    throw(new UnsupportedOperationException("setTitle methods are "+
                                            "not supported by this"+
                                            "Resources class"));
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
    return(null);
  }

  //----------------------------------------------------------------------
  /**
   * Sets the description for the given key.
   * Overwrite this method in classes extending AbstractResources, if
   * set of descriptions is supported. by default, this method throws
   * an UnsupportedOperationException.
   *
   * @param key the key to set the description for.
   * @param description the description to set.
   * @exception UnsupportedOperationException if setDescription
   * operations are not supported.
   * @exception IllegalArgumentException if key or description is
   * 'null'.
   */

  public void setDescription(String key, String description)
    throws UnsupportedOperationException
  {
    throw(new UnsupportedOperationException("setDescription methods "+
                                            "are not supported by "+
                                            "this Resources class"));
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
    return(null);
  }

  //----------------------------------------------------------------------
  /**
   * Sets the type for the given key.
   * Overwrite this method in classes extending AbstractResources, if
   * set of types is supported. by default, this method throws an
   * UnsupportedOperationException.
   *
   * @param key the key to set the type for.
   * @param type the type to set.
   * @exception UnsupportedOperationException if setType operations
   * are not supported.
   * @exception IllegalArgumentException if key or type is 'null'.
   */

  public void setType(String key, Class type)
    throws UnsupportedOperationException
  {
    throw(new UnsupportedOperationException("setType methods "+
                                            "are not supported by "+
                                            "this Resources class"));
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
    return(null);
  }

  //----------------------------------------------------------------------
  /**
   * Sets the possible Values for the given key.
   * Overwrite this method in classes extending AbstractResources, if
   * set of possible values is supported. by default, this method
   * throws an UnsupportedOperationException.
   *
   * @param key the key to set the possible Values for.
   * @param possible_values the possible Values to set.
   * @exception UnsupportedOperationException if setPossibleValues
   * operations are not supported.
   * @exception IllegalArgumentException if key or possible_values is
   * 'null'.
   */

  public void setPossibleValues(String key, String[] possible_values)
    throws UnsupportedOperationException
  {
    throw(new UnsupportedOperationException("setPossibleValues "+
                                            "methods are not "+
                                            "supported by this "+
                                            "Resources class"));
  }

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
    throws UnsupportedOperationException
  {
//    System.out.println("Unsetting resource key "+key +" in resources "+this);
    if (key == null)
      throw(new IllegalArgumentException("'key' must not be 'null'"));
    try
    {
      Resources resources = findResourcesForKey(key);
      if((resources == null) || (resources == this))
      {
        String old_value = getValue(key);
        unsetValue(key);
        property_change_support_.firePropertyChange(key, old_value, null);
      }
      else
        resources.unset(key);
    }
    catch (MissingResourceException exc)
    {
      // resource did not exist, so do nothing!
    }
  }

  //----------------------------------------------------------------------
  /**
   * Resets the bound value to its default value (if supported).
   *
   * @param key the key of the resource to reset.
   * @exception UnsupportedOperationException if the resources is not
   * capable of resetting values, or in particular the given key.
   */

  public void reset(String key)
    throws UnsupportedOperationException
  {
    if (key == null)
      throw(new IllegalArgumentException("'key' must not be 'null'"));

    String old_value = null;
    try
    {
      old_value = getValueAllAttached(key);
    }
    catch(MissingResourceException exc)
    {
    }
    String new_value = null;
    try
    {
      new_value = getValueAllAttached(key);
    }
    catch(MissingResourceException exc)
    {
    }
    resetValueAllAttached(key);
    property_change_support_.firePropertyChange(key, old_value, new_value);
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
    return(false);
  }

  //----------------------------------------------------------------------
  /**
   * Replaces all variables with their values recursively. Variables
   * are Strings that start and end with a '$'. To escape the $-sign
   * use $$. In case of a deadlock, the variable will not be
   * replaced.
   *
   * @param to_replace the string that is to be replaced
   * @param map the map that stores all variables to replace.
   * @return the replaced string
   */

  public static String replace(String to_replace,
                               Map map)
  {
    int var_start_pos = to_replace.indexOf(VAR_IDENTIFIER);
    int var_end_pos = 0;
    String variable;
    while (var_start_pos >= 0)
    {
      var_end_pos = to_replace.indexOf(VAR_IDENTIFIER, var_start_pos+VAR_LENGTH);
      if (var_end_pos < var_start_pos) // no more identifiers
        break;
      variable =
        to_replace.substring(var_start_pos+VAR_LENGTH,
                             var_end_pos);
      String replaced = null;
      if (variable.length() == 0)
        if (replaced == null)
          replaced = VAR_IDENTIFIER;
        else
          replaced += RESOURCE_STRING_ARRAY_DELIMITER+VAR_IDENTIFIER;
      else
      {
        Object val = map.get(variable);
        if (val != null)
          replaced = val.toString();
      }
      if (replaced != null)
      {
        to_replace = to_replace.substring(0, var_start_pos)+
          replaced+to_replace.substring(var_end_pos+VAR_LENGTH);
        var_end_pos = var_start_pos-VAR_LENGTH+replaced.length();
      }

      var_start_pos = to_replace.indexOf(VAR_IDENTIFIER, var_end_pos+VAR_LENGTH);
    }
    return(to_replace);
  }

  //----------------------------------------------------------------------
  /**
   * Replaces all variables with their values recursively. Variables
   * are Strings that start and end with a '$'. To escape the $-sign
   * use $$. In case of a deadlock, the variable will not be
   * replaced. Variables that are not given within the resources and
   * are not a system property and are not equal to 'null' are not
   * replaced! Variables in the resource file will be searched prior
   * to variables within the system properties and the 'null' key.
   *
   * Examples: <xmp>
   * version = 1.0.0
   * id = Resouces ($version$) </xmp>
   * getString("id") will return 'Resouces (1.0.0)'
   * <xmp>
   * tmp_home_dir = $user.name$/tmp
   * <xmp>
   * getString("tmp_home_dir") will for example return
   * '/usr/users/root/tmp' if the current user is 'root'.
   *
   * @param to_replace the string that is to be replaced
   * @param set the set that stores all recursively replaced values,
   * needed for deadlock detection.
   * @exception MissingResourceException if the given key is not defined
   * within the resources.
   * @return the replaced string
   */

  private String replaceVariables(String to_replace, Set set)
    throws MissingResourceException
  {
    int var_start_pos = to_replace.indexOf(VAR_IDENTIFIER);
    int var_end_pos = 0;
    String variable;
    String[] variables;
    while (var_start_pos >= 0)
    {
      var_end_pos = to_replace.indexOf(VAR_IDENTIFIER, var_start_pos+VAR_LENGTH);
      if (var_end_pos < var_start_pos) // no more identifiers
        break;
      String var =
        to_replace.substring(var_start_pos+VAR_LENGTH,
                             var_end_pos);
      boolean deep_replace = true;
      if (var.startsWith(SWALLOW_IDENTIFIER) && (var.endsWith(SWALLOW_IDENTIFIER)))
      {
        deep_replace = false;
        var = var.substring(SWALLOW_LENGTH, var.length()-SWALLOW_LENGTH);
      }
      variables = expand(var);
      String replaced = null;
      for (int count = 0; count < variables.length; count++)
      {
        variable = variables[count];
        if (set.add(variable))
        {
          try
          {
            if (variable.length() == 0)
              if (replaced == null)
                replaced = VAR_IDENTIFIER;
              else
                replaced += RESOURCE_STRING_ARRAY_DELIMITER+VAR_IDENTIFIER;
            else
              if (replaced == null)
                if (deep_replace)
                  replaced = replaceVariables(getValueAllAttached(variable),
                                              set);
                else
                  replaced = replaceVariables(variable, set);
              else
                if (deep_replace)
                  replaced += RESOURCE_STRING_ARRAY_DELIMITER+
                    replaceVariables(getValueAllAttached(variable),
                                     set);
                else
                  replaced += RESOURCE_STRING_ARRAY_DELIMITER+
                    replaceVariables(variable, set);
          }
          catch (MissingResourceException exc)
          {
//            exc.printStackTrace();
            if (replaced == null)
              replaced = System.getProperty(variable);
            else
              replaced = RESOURCE_STRING_ARRAY_DELIMITER+
                System.getProperty(variable);
            if (replaced == null)
            {
              if (variable.equals(NULL_KEY))
                return(null);
              if (variable.equals(UNSET_KEY))
              {
                String key = (String)set.iterator().next();
                throw(new MissingResourceException("Can't find resource for "+
                                                   "bundle "+
                                                   getClass().getName()+
                                                   ", key '"+key+"'",
                                                   getClass().getName(), key));
              }
            }
          }
        }
        set.remove(variable);
      }
      if (replaced != null)
      {
        to_replace = to_replace.substring(0, var_start_pos)+
          replaced+to_replace.substring(var_end_pos+VAR_LENGTH);
        var_end_pos = var_start_pos-VAR_LENGTH+replaced.length();
      }

      var_start_pos = to_replace.indexOf(VAR_IDENTIFIER, var_end_pos+VAR_LENGTH);
    }
    return(to_replace);
  }

  //----------------------------------------------------------------------
  /**
   * @param key the key that may contain *
   * @return all expanded values
   */

  protected synchronized String[] expand(String key)
  {
    if (dead_lock_detect_)
      return(new String[]{key});
    if (key.indexOf("*") <= 0)
      return(new String[] {key});
    dead_lock_detect_ = true;
    StringBuffer key_perl5 = new StringBuffer();
    key_perl5.append("^"); // beginning
    StringTokenizer tok = new StringTokenizer(key, "*", true);
    String token;
    while (tok.hasMoreTokens())
    {
      token = tok.nextToken();
      if (token.equals("*"))
        key_perl5.append(".*");
      else
        key_perl5.append(token);
    }
    key_perl5.append("$"); // ending

//    System.out.println("key='"+key+"', Key_perl5: "+key_perl5);
    
    try
    {
      RE regular_expression = new RE(key_perl5.toString());

      Vector hits = new Vector();

      Enumeration enum = getKeys();
      Object hit;
      while (enum.hasMoreElements())
      {
        hit = enum.nextElement();
        if (regular_expression.isMatch(hit))
          hits.add(hit);
      }
      String[] ret = new String[hits.size()];
      hits.toArray(ret);

      dead_lock_detect_ = false;
      return(ret);
    }
    catch (REException exc)
    {
      // wrong syntax -> return key
      dead_lock_detect_ = false;
      return(new String[] {key});
    }
  }

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
    throws MissingResourceException
  {
    if (key == null)
      throw(new IllegalArgumentException("'key' must not be 'null'"));
    String value = getValueAllAttached(key);
    if (value == null)
      //      return(null);
      throw(new MissingResourceException("Can't find resource for "+
                                         "bundle "+
                                         getClass().getName()+
                                         ", key '"+key+"'",
                                         getClass().getName(), key));
    if (value.indexOf(VAR_IDENTIFIER) < 0)
      return(value); // noting to escape

    HashSet set = new HashSet();
    set.add(key);
    return(replaceVariables(value, set));
  }

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
    throws MissingResourceException, IllegalArgumentException
  {
    return(get(key, getType(key)));
  }

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
   * getType() call with the given key returns <code>null</code>.
   *
   * @param key the key of the resource property to look for.
   * @param type the type to be returned, if null, a String will be returned.
   * @return the native object loaded from the resource bundle.
   * @exception MissingResourceException if the given key is not defined
   * within the resources.
   * @exception IllegalArgumentException if key is 'null', of the
   * value is not of the given type.
   */

  public Object get(String key, Class type)
    throws MissingResourceException
  {
    if ((type == null) ||
        (type == String.class))
      return(getString(key));
    if (type == String[].class)
      return(getStringArray(key));
    if (type == Integer.TYPE)
      return(new Integer(getInt(key)));
    if (type == int[].class)
      return(getIntArray(key));
    if (type == Boolean.TYPE)
      return(new Boolean(getBoolean(key)));
    if (type == Boolean.TYPE)
      return(new Boolean(getBoolean(key)));
    if (type == Icon.class)
      return(getIcon(key));
    if (type == Color.class)
      return(getColor(key));
    if (type == File.class)
      return(getFile(key));
    if (type == URL.class)
      return(getURL(key));
//      if (type == Boolean[].class)
//        return(getBooleanArray(key));
//      if (type == Character.TYPE)
//        return(new Character(getChar);
//      if (type == Character[].class)
//        return("char[]");
//      if (type == Byte.TYPE)
//        return("byte");
//      if (type == Byte[].class)
//        return("byte[]");
//      if (type == Short.TYPE)
//        return("short");
//      if (type == Short[].class)
//        return("short[]");
//      if (type == Long.TYPE)
//        return("long");
//      if (type == Long[].class)
//        return("long[]");
//      if (type == Float.TYPE)
//        return("float");
//      if (type == Float[].class)
//        return("float[]");
    if (type == Double.TYPE)
      return(new Double(getDouble(key)));
    if (type == Double[].class)
      return(getDoubleArray(key));
    throw(new IllegalArgumentException("creation of types '"+type+
                                       "' are not supported"));
  }

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

  public String getString(String key, String default_value)
  {
    try
    {
      return(getString(key));
    }
    catch (MissingResourceException exc)
    {
    }
    return(default_value);
  }

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
    throws UnsupportedOperationException
  {
    if (key == null)
      throw(new IllegalArgumentException("'key' must not be 'null'"));
    if (value == null)
      throw(new IllegalArgumentException("'value' must not be 'null'"));

    String old_value = null;
    try
    {
      old_value = getValue(key);
    }
    catch(MissingResourceException exc)
    {
    }
    setValueAllAttached(key, value);
    property_change_support_.firePropertyChange(key, old_value, value);
  }

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
    throws MissingResourceException
  {
    return(getStringArray(key, RESOURCE_STRING_ARRAY_DELIMITER));
  }

  //----------------------------------------------------------------------
  /**
   * Returns the string array, loaded from the resource
   * bundle. Returns an empty array instead of <code>null</code>. Any
   * variables found within the properties value will be replaced.
   *
   * @param key the key of the resource property to look for.
   * @param delimiter the delimiter to be used to seperate single
   * values.
   * @return the string array, loaded from the resource bundle. Returns
   * an empty array instead of <code>null</code>.
   * @exception MissingResourceException if the given key is not defined
   * within the resources.
   */

  public String[] getStringArray(String key, String delimiter)
    throws MissingResourceException
  {
    String string_value = getString(key);
    if (string_value == null)
      return(null);
    StringTokenizer tok = new
      StringTokenizer(string_value, delimiter);
    String[] ret = new String[tok.countTokens()];
    for (int count = 0; count < ret.length; count++)
      ret[count] = tok.nextToken().trim();
    return(ret);
  }

  //----------------------------------------------------------------------
  /**
   * Returns the string array loaded from the resource bundle. If the
   * key was not found within the resources,
   * <code>default_values</code> will be returned. Any variables found
   * within the properties value will be replaced.
   *
   * @param key the key of the resource property to look for.
   * @param default_values the default value that will be returned if no
   * resource of the given key was found.
   * @return the string array loaded from the resource bundle.
   */

  public String[] getStringArray(String key, String[] default_values)
  {
    try
    {
      return(getStringArray(key));
    }
    catch (MissingResourceException exc)
    {
    }
    return(default_values);
  }

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
    throws UnsupportedOperationException
  {
    setStringArray(key, values, RESOURCE_STRING_ARRAY_DELIMITER);
  }

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
    throws UnsupportedOperationException
  {
    if (key == null)
      throw(new IllegalArgumentException("'key' must not be 'null'"));
    if (values == null)
      throw(new IllegalArgumentException("'values' must not be 'null'"));

    StringBuffer buffer = new StringBuffer();
    for (int count = 0; count < values.length; count++)
    {
      buffer.append(values[count]);
      if ((count+1) < values.length)
        buffer.append(delimiter);
    }
    setString(key, buffer.toString());
  }

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
    throws MissingResourceException, NumberFormatException
  {
    String string_value = getString(key);
    if (string_value == null)
      throw(new NumberFormatException("null is not an int"));
    return(Integer.parseInt(string_value));
  }

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

  public int getInt(String key, int default_value)
  {
    try
    {
      return(getInt(key));
    }
    catch (MissingResourceException exc)
    {
    }
    catch (NumberFormatException exc)
    {
    }
    return(default_value);
  }

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
    throws UnsupportedOperationException
  {
    if (key == null)
      throw(new IllegalArgumentException("'key' must not be 'null'"));
    setString(key, Integer.toString(value));
  }

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
    throws MissingResourceException, NumberFormatException
  {
    String[] values = getStringArray(key);
    int[] ret = new int[values.length];
    for (int count = 0; count < ret.length; count++)
      ret[count] = Integer.parseInt(values[count]);
    return(ret);
  }

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

  public int[] getIntArray(String key, int[] default_values)
  {
    try
    {
      return(getIntArray(key));
    }
    catch (MissingResourceException exc)
    {
    }
    catch (NumberFormatException exc)
    {
    }
    return(default_values);
  }

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
    throws UnsupportedOperationException
  {
    if (key == null)
      throw(new IllegalArgumentException("'key' must not be 'null'"));
    if (values == null)
      throw(new IllegalArgumentException("'values' must not be 'null'"));

    StringBuffer buffer = new StringBuffer();
    for (int count = 0; count < values.length; count++)
    {
      buffer.append(Integer.toString(values[count]));
      if ((count+1) < values.length)
        buffer.append(RESOURCE_STRING_ARRAY_DELIMITER);
    }
    setString(key, buffer.toString());
  }

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
    throws MissingResourceException, NumberFormatException
  {
    String string_value = getString(key);
    if (string_value == null)
      throw(new NumberFormatException("null is not an double"));
    return(Double.parseDouble(string_value));
  }

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

  public double getDouble(String key, double default_value)
  {
    try
    {
      return(getDouble(key));
    }
    catch (MissingResourceException exc)
    {
    }
    catch (NumberFormatException exc)
    {
    }
    return(default_value);
  }

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
    throws UnsupportedOperationException
  {
    if (key == null)
      throw(new IllegalArgumentException("'key' must not be 'null'"));
    setString(key, Double.toString(value));
  }

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
    throws MissingResourceException, NumberFormatException
  {
    String[] values = getStringArray(key);
    double[] ret = new double[values.length];
    for (int count = 0; count < ret.length; count++)
      ret[count] = Double.parseDouble(values[count]);
    return(ret);
  }

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

  public double[] getDoubleArray(String key, double[] default_values)
  {
    try
    {
      return(getDoubleArray(key));
    }
    catch (MissingResourceException exc)
    {
    }
    catch (NumberFormatException exc)
    {
    }
    return(default_values);
  }

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
    throws UnsupportedOperationException
  {
    if (key == null)
      throw(new IllegalArgumentException("'key' must not be 'null'"));
    if (values == null)
      throw(new IllegalArgumentException("'values' must not be 'null'"));

    StringBuffer buffer = new StringBuffer();
    for (int count = 0; count < values.length; count++)
    {
      buffer.append(Double.toString(values[count]));
      if ((count+1) < values.length)
        buffer.append(RESOURCE_STRING_ARRAY_DELIMITER);
    }
    setString(key, buffer.toString());
  }

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
    throws MissingResourceException
  {
    String string_value = getString(key);
    if (string_value == null)
      return(false);
    String value = string_value.toLowerCase();
    if (value.equals("true"))
      return(true);
    if (value.equals("yes"))
      return(true);
    if (value.equals("1"))
      return(true);
    return(false);
  }

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

  public boolean getBoolean(String key, boolean default_value)
  {
    try
    {
      return(getBoolean(key));
    }
    catch (MissingResourceException exc)
    {
    }
    catch (NumberFormatException exc)
    {
    }
    return(default_value);
  }

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
    throws UnsupportedOperationException
  {
    if (key == null)
      throw(new IllegalArgumentException("'key' must not be 'null'"));

    setString(key, value ? "1" : "0");
  }

  //----------------------------------------------------------------------
  /**
   * Returns the icon loaded from the resource bundle. The value found
   * will be interpreted as URL from which the Icon will be
   * created. Any variables found within the properties value will be
   * replaced.
   *
   * @param key the key of the resource property to look for.
   * @return the icon loaded from the resource bundle.
   * @throws MissingResourceException if the given key is not defined
   * within the resources.
   */

  public Icon getIcon(String key)
    throws MissingResourceException, UnsupportedOperationException
  {
    return(new ImageIcon(getURL(key)));
  }

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

  public Icon getIcon(String key, Icon default_value)
  {
    try
    {
      return(getIcon(key));
    }
    catch (MissingResourceException exc)
    {
    }
    catch (NumberFormatException exc)
    {
    }
    return(default_value);
  }

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
    throws MissingResourceException
  {
    String value = getString(key);
    if (value == null)
      throw(new MissingResourceException("A 'null' value is not valid for a Color!",
                                         getClass().getName(),
                                         key));
    value = value.toLowerCase();
    if (value.equals("white"))
      return(Color.white);
    if (value.equals("light_gray"))
      return(Color.lightGray);
    if (value.equals("gray"))
      return(Color.gray);
    if (value.equals("dark_gray"))
      return(Color.darkGray);
    if (value.equals("black"))
      return(Color.black);
    if (value.equals("red"))
      return(Color.red);
    if (value.equals("pink"))
      return(Color.pink);
    if (value.equals("orange"))
      return(Color.orange);
    if (value.equals("yellow"))
      return(Color.yellow);
    if (value.equals("green"))
      return(Color.green);
    if (value.equals("magenta"))
      return(Color.magenta);
    if (value.equals("cyan"))
      return(Color.cyan);
    if (value.equals("blue"))
      return(Color.blue);
    int[] color_ints = getIntArray(key);
    if (color_ints.length < 3)
      throw(new MissingResourceException("value '"+value+"' is not valid for a Color!",
                                         getClass().getName(),
                                         key));
    if (color_ints.length >= 4) // with alpha
      return(new Color(color_ints[0], color_ints[1], color_ints[2], color_ints[3]));
    return(new Color(color_ints[0], color_ints[1], color_ints[2]));
  }

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

  public Color getColor(String key, Color default_value)
  {
    try
    {
      return(getColor(key));
    }
    catch (MissingResourceException exc)
    {
    }
    catch (NumberFormatException exc)
    {
    }
    return(default_value);
  }

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
    throws UnsupportedOperationException
  {
    if (key == null)
      throw(new IllegalArgumentException("'key' must not be 'null'"));
    if (value == null)
      throw(new IllegalArgumentException("'value' must not be 'null'"));

    String color = null;
    if (value.equals(Color.white))
    {
      setString(key, "white");
      return;
    }
    if (value.equals(Color.lightGray))
    {
      setString(key, "light_gray");
      return;
    }
    if (value.equals(Color.gray))
    {
      setString(key, "gray");
      return;
    }
    if (value.equals(Color.darkGray))
    {
      setString(key, "dark_gray");
      return;
    }
    if (value.equals(Color.black))
    {
      setString(key, "black");
      return;
    }
    if (value.equals(Color.red))
    {
      setString(key, "red");
      return;
    }
    if (value.equals(Color.pink))
    {
      setString(key, "pink");
      return;
    }
    if (value.equals(Color.orange))
    {
      setString(key, "orange");
      return;
    }
    if (value.equals(Color.yellow))
    {
      setString(key, "yellow");
      return;
    }
    if (value.equals(Color.green))
    {
      setString(key, "green");
      return;
    }
    if (value.equals(Color.magenta))
    {
      setString(key, "magenta");
      return;
    }
    if (value.equals(Color.cyan))
    {
      setString(key, "cyan");
      return;
    }
    if (value.equals(Color.blue))
    {
      setString(key, "blue");
      return;
    }

    int alpha = value.getAlpha();
    if (alpha == 255)
      setIntArray(key, new int[] {value.getRed(), value.getGreen(),
                                    value.getBlue()});
    else
      setIntArray(key, new int[] {value.getRed(), value.getGreen(),
                                    value.getBlue(), alpha});
  }

  //----------------------------------------------------------------------
  /**
   * Returns the file loaded from the resource bundle. Any variables
   * found within the properties value will be replaced.
   *
   * @param key the key of the resource property to look for.
   * @return the file that is loaded from the resource bundle.
   * @exception MissingResourceException if the given key is not defined
   * within the resources.
   */

  public File getFile(String key)
    throws MissingResourceException
  {
    String value = getString(key);
    if (value == null)
      return(null);
    return(new File(value));
  }

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

  public File getFile(String key, File default_value)
  {
    try
    {
      return(getFile(key));
    }
    catch (MissingResourceException exc)
    {
    }
    return(default_value);
  }

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
      throw(new MissingResourceException("malformed URL '"+value+"'",
                                         getClass().getName(), key));
    }
  }

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

  public URL getURL(String key, URL default_value)
  {
    try
    {
      return(getURL(key));
    }
    catch (MissingResourceException exc)
    {
    }
    return(default_value);
  }

  //----------------------------------------------------------------------
  /**
   * Add a PropertyChangeListener to the listener list.
   * The listener is registered for all properties.
   *
   * @param listener The PropertyChangeListener to be added.
   */

  public void addPropertyChangeListener(PropertyChangeListener
                                        listener)
  {
    property_change_support_.addPropertyChangeListener(listener);
  }

  //----------------------------------------------------------------------
  /**
   * Remove a PropertyChangeListener from the listener list.
   * This removes a PropertyChangeListener that was registered
   * for all properties.
   *
   * @param listener The PropertyChangeListener to be removed
   */

  public void removePropertyChangeListener(PropertyChangeListener
                                           listener)
  {
    property_change_support_.removePropertyChangeListener(listener);
  }

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
                                        listener)
  {
    property_change_support_.addPropertyChangeListener(property_name, listener);
  }

  //----------------------------------------------------------------------
  /**
   * Remove a PropertyChangeListener for a specific property.
   *
   * @param property_name The name of the property that was listened on.
   * @param listener The PropertyChangeListener to be removed
   */

  public void removePropertyChangeListener(String property_name,
                                           PropertyChangeListener
                                           listener)
  {
    property_change_support_.removePropertyChangeListener(property_name, listener);
  }


  //----------------------------------------------------------------------
  /**
   * Returns the assign class for the given type.
   *
   * @param type the name of the type to get the class for
   * @return the corresponding class, or null if no class for the given type exists.
   */

  public static Class getClassForType(String type)
  {
    if (type == null)
      return(null);
    if (type.equals("int"))
      return(Integer.TYPE);
    if (type.equals("int[]"))
      return(int[].class);
    if (type.equals("boolean"))
      return(Boolean.TYPE);
    if (type.equals("boolean[]"))
      return(boolean[].class);
    if (type.equals("char"))
      return(Character.TYPE);
    if (type.equals("char[]"))
      return(char[].class);
    if (type.equals("byte"))
      return(Byte.TYPE);
    if (type.equals("byte[]"))
      return(byte[].class);
    if (type.equals("short"))
      return(Short.TYPE);
    if (type.equals("short[]"))
      return(short[].class);
    if (type.equals("long"))
      return(Long.TYPE);
    if (type.equals("long[]"))
      return(long[].class);
    if (type.equals("float"))
      return(Float.TYPE);
    if (type.equals("float[]"))
      return(float[].class);
    if (type.equals("double"))
      return(Double.TYPE);
    if (type.equals("double[]"))
      return(double[].class);
    if (type.equals("String"))
      return(String.class);
    if (type.equals("String[]"))
      return(String[].class);

    if (type.equals("resource.group"))
      return(ResourceGroup.class);

    try
    {
      if (type.endsWith("[]"))
        return(Class.forName("[L"+type.substring(0,type.length()-2)+";"));
      return(Class.forName(type));
    }
    catch(Exception exc)
    {
      exc.printStackTrace();
      return(null);
    }
  }

  //----------------------------------------------------------------------
  /**
   * Returns the assign type for the given class.
   *
   * @param type the class to get the type for.
   * @return the corresponding name, or null if type was null.
   */

  public static String getTypeForClass(Class type)
  {
    if (type == null)
      return(null);
    if (type == Integer.TYPE)
      return("int");
    if (type == int[].class)
      return("int[]");
    if (type == Boolean.TYPE)
      return("boolean");
    if (type == boolean[].class)
      return("boolean[]");
    if (type == Character.TYPE)
      return("char");
    if (type == char[].class)
      return("char[]");
    if (type == Byte.TYPE)
      return("byte");
    if (type == byte[].class)
      return("byte[]");
    if (type == Short.TYPE)
      return("short");
    if (type == short[].class)
      return("short[]");
    if (type == Long.TYPE)
      return("long");
    if (type == long[].class)
      return("long[]");
    if (type == Float.TYPE)
      return("float");
    if (type == float[].class)
      return("float[]");
    if (type == Double.TYPE)
      return("double");
    if (type == double[].class)
      return("double[]");
    if (type == String.class)
      return("String");
    if (type == String[].class)
      return("String[]");

    if (type == ResourceGroup.class)
      return("resource.group");
    if (type.isArray())
      return(type.getComponentType().getName()+"[]");

    return(type.getName());
  }
}












