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

import java.util.Locale;
import java.util.MissingResourceException;

import org.dinopolis.util.resource.FileResources;

//----------------------------------------------------------------------
/**
 * This class can be asked to create a ResourceManager instance using one of
 * the <code>getResourceManager</code> methods.
 *
 * @author Dieter Freismuth
 * @version $Revision$
 */

public class ResourceManager
{
  //----------------------------------------------------------------------
  /**
   * Get the appropriate ResourceBundle for the given base_name.
   *
   * @param base_name the base name of the resource bundle.
   * @return the Resource.
   * @exception MissingResourceException if the system resource file
   * could not be located. 
   * @see #getResources(java.lang.Class,java.lang.String,java.lang.String,java.util.Locale)
   */
 
  public static Resources getResources(String base_name)
    throws MissingResourceException
  {
    return(FileResources.getResources(base_name));
  }

  //----------------------------------------------------------------------
  /**
   * Get the appropriate ResourceBundle for the given base_name and searches the
   * system bundle in the callers package name. See also {@link #getResources
   * (java. lang.Class,java.lang.String,java.lang.String,java.util.Locale)}
   *
   * @param caller the caller, to search the system bundle for.
   * @param base_name the base name of the resource bundle.
   * @return the Resource.
   * @exception MissingResourceException if the system resource file
   * @see #getResources(java.lang.Class,java.lang.String,java.lang.String,java.util.Locale)
   */
 
  public static Resources getResources(Class caller, String base_name)
    throws MissingResourceException
  {
    return(FileResources.getResources(caller, base_name));
  }

  //----------------------------------------------------------------------
  /**
   * Get the appropriate ResourceBundle for the given base_name and the given
   * locale.
   *
   * @param base_name the base name of the resource bundle.
   * @param locale the locale.
   * @return the Resource.
   * @exception MissingResourceException if the system resource file
   * could not be located. 
   * @see #getResources(java.lang.Class,java.lang.String,java.lang.String,java.util.Locale)
   */

  public static Resources getResources(String base_name,
                                       Locale locale)
    throws MissingResourceException
  {
    return(FileResources.getResources(base_name, locale));
  }

  //----------------------------------------------------------------------
  /**
   * Get the appropriate ResourceBundle for the given base_name and locale and
   * searches the system bundle in the callers package name. See also {@link
   * #getResources(java.lang.Class,java.lang.String,java.lang.String,java.util.
   * Locale)}
   *
   * @param caller the caller, to search the system bundle for.
   * @param base_name the base name of the resource bundle.
   * @param locale the locale.
   * @return the Resource.
   * @exception MissingResourceException if the system resource file
   * could not be located. 
   */

  public static Resources getResources(Class caller, String base_name,
                                       Locale locale)
    throws MissingResourceException
  {
    return(FileResources.getResources(caller, base_name, locale));
  }

  //----------------------------------------------------------------------
  /**
   * Get the appropriate ResourceBundle for the given base_name and the given
   * dir_name. The dir_name specifies the directory name of the users resource
   * file, relative to the users home directory.
   *
   * @param base_name the base name of the resource bundle.
   * @param dir_name the name of the directory within the users homedir
   * to look for a property file.
   * @return the Resource.
   * @exception MissingResourceException if the system resource file
   * could not be located. 
   */
 
  public static Resources getResources(String base_name, String dir_name)
    throws MissingResourceException
  {
    return(FileResources.getResources(base_name, dir_name));
  }

  //----------------------------------------------------------------------
  /**
   * Get the appropriate ResourceBundle for the given base_name and the given
   * dir_name and searches the system bundle in the callers package name. The
   * dir_name specifies the directory name of the users resource file, relative
   * to the users home directory.
   *
   * @param caller the caller, to search the system bundle for.
   * @param base_name the base name of the resource bundle.
   * @return the Resource.
   * @param dir_name the name of the directory within the users homedir
   * to look for a property file.
   * @exception MissingResourceException if the system resource file
   * could not be located. 
   */
 
  public static Resources getResources(Class caller, String base_name,
                                       String dir_name)
    throws MissingResourceException
  {
    return(FileResources.getResources(caller, base_name, dir_name));
  }

  //----------------------------------------------------------------------
  /**
   * Get the appropriate ResourceManager for the given base_name, the given
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
   */

  public static Resources getResources(String base_name, String dir_name,
                                       Locale locale)
    throws MissingResourceException
  {
    return(FileResources.getResources(base_name, dir_name, locale));
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
   * caller: <code>org.dinopolis.util.ResourceManager</code><br>
   * base_name: <code>TestResourceManager</code><br>
   * dir_name: <code>test_resources</code><br>
   * locale: <code>Locale.getDefault()</code><br>
   * users home dir: ~dfreis<p>
   *
   * then, the users bundle is suspected to be:<br>
   * "~dfreis/test_resources/TestResourceManager.properties", or if not found:<br>
   * "~dfreis/.test_resources/TestResourceManager.properties"
   *
   * the system resources are expected to be in:<br>
   * "org.dinopolis.util.ResourceManager.TestResourceManager.properties"
   * within your classpath!<p>
   * 
   * The sample code of the caller for the given example will look
   * like this:
   * <xmp>
   * ResourceManager resources = ResourceManager.getResources(getClass(), "TestResourceManager", 
   *                                           "test_resources");
   * </xmp>
   *
   * @param caller the caller, to search the system bundle for.
   * @param base_name the base name of the resource bundle.
   * @param locale the locale.
   * @param dir_name the name of the directory within the users homedir
   * to look for a property file.
   * @return the Resource.
   * @exception MissingResourceException if the system resource file
   * could not be located.
   */

  public static Resources getResources(Class caller, String base_name,
                                       String dir_name, Locale locale)
    throws MissingResourceException
  {
    return(FileResources.getResources(caller, base_name, dir_name, locale));
  }
 
  //----------------------------------------------------------------------
  /**
   * Get the appropriate ResourceBundle for the given base_name, the given
   * dir_name and the given locale. The dir_name specifies the directory name of
   * the users resource file, relative to the users home directory.
   *
   * @param caller the caller, to search the system bundle for.
   * @param base_name the base name of the resource bundle.
   * @param locale the locale.
   * @param dir_name the name of the directory within the users homedir
   * to look for a property file.
   * @param loader the classloader to use to load the resources.
   * @return the Resource.
   * @exception MissingResourceException if the system resource file
   * could not be located. 
   * @see #getResources(java.lang.Class,java.lang.String,java.lang.String,java.util.Locale)
   */
  public static Resources getResources(Class caller, String base_name,
                                       String dir_name,
                                       Locale locale,
                                       ClassLoader loader)
	throws MissingResourceException
  {
    return(FileResources.getResources(caller, base_name, dir_name, locale, loader));
  }
 
 
 
}











