/***********************************************************************
 * @(#)$RCSfile$   $Revision$$Date$
 *
 * Copyright (c) 2003 IICM, Graz University of Technology
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


package org.dinopolis.util.servicediscovery;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.StringWriter;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;
import java.util.List;

//----------------------------------------------------------------------
/**
 * ClassLoader that allows to add all jar files in one or more
 * repositories (directories). Additionally, a specific directory or a
 * jar file can be added. This behavior allows to search in
 * directories for jars even if they are not in the classpath
 * (e.g. for plugin functionality).
 * <p>

 * Usage: create an instance of this classloader, add the repositories
 * and urls the classloader should look for and call the
 * <code>loadClass</code> method:
 * <pre>
 * RepositoryClassLoader loader = new RepositoryClassLoader();
 * loader.addRepository("/home/cdaller/plugins");
 * SuperPlugin plugin = (SuperPlugin)loader.loadClass("org.dinopolis.plugin.SuperPlugin").newInstance();
 * </pre>
 *
 */
public class RepositoryClassLoader extends URLClassLoader
{
      /*** Simple empty URL[0] array. */
  public static final URL[] EMPTY_URL_ARRAY = new URL[0];

  protected Vector repositories_ = new Vector();

//----------------------------------------------------------------------
/**
 * Empty Constructor
 */
  public RepositoryClassLoader()
  {
    super(EMPTY_URL_ARRAY);
  }

//----------------------------------------------------------------------
/**
 * Constructor given a parent.
 *
 *  @param parent The parent loader.
 */
  public RepositoryClassLoader(ClassLoader parent)
  {
    super(EMPTY_URL_ARRAY, parent);
  }

//----------------------------------------------------------------------
/**
 * Append a URL to this loader's search path.
 *
 *  @param url The URL to append.
 */
  public void addURL(URL url)
  {
    super.addURL(url);
  }

//----------------------------------------------------------------------
/**
 * Produce output suitable for debugging.
 *
 *  @return Output suitable for debugging.
 */
  public String toString()
  {
    return "[RepositoryClassLoader]";
  }

// //----------------------------------------------------------------------
// /**
//  * Find the resource with the given name, and return a String.
//  * The search order is as described for <code>getResource()</code>,
//  * after checking to see if the resource data has been previously cached.
//  * If the resource cannot be found, return <code>null</code>.
//  *
//  * @param name Name of the resource to return a String for.
//  */
//   public String getResourceAsString( String name )
//   {
//     StringWriter sw = null;
//     BufferedReader reader = null;

//     try
//     {
//       sw = new StringWriter();
//       InputStream is = getResourceAsStream( name );
//       reader = new BufferedReader( new InputStreamReader( is ) );

//       char buf[] = new char[1024];
//       int len = 0;

//       while ((len = reader.read(buf, 0, 1024)) != -1)
//       {
//         sw.write( buf, 0, len );
//       }

//       return sw.toString();
//     }
//     catch (IOException ioe)
//     {
//       return null;
//     }
//     finally
//     {
//       try
//       {
//         sw.close();
//         reader.close();
//       }
//       catch (Exception ignored)
//       {
//       }
//     }
//   }

//----------------------------------------------------------------------
/**
 * Add a new repository to the set of places this ClassLoader can look for
 * classes to be loaded.
 *
 * @param repository Name of a source of classes to be loaded, such as a
 *      directory pathname, a JAR file pathname, or a ZIP file pathname. The
 *      parameter must be in the form of an URL.
 * @exception IllegalArgumentException if the specified repository is
 *      invalid or does not exist
 */
  public void addRepository(String repository)
  {
    addRepository(new File(repository));
  }

//----------------------------------------------------------------------
/**
 * Add a new repository to the set of places this ClassLoader can look
 * for classes to be loaded. The directory is parsed for ".jar" files
 * and these jar files are added to the search path. This means that
 * jars added to the directory after this method was called, are not
 * used!
 *
 * @param repository Name of a source of classes to be loaded, such as a
 *      directory pathname, a JAR file pathname, or a ZIP file pathname. The
 *      parameter must be in the form of an URL.
 * @exception IllegalArgumentException if the specified repository is
 *      invalid.
 */
  public void addRepository(File repository)
  {
    if (repository.exists() && repository.isDirectory())
    {
      repositories_.add(repository.toString());
      File[] jars = repository.listFiles();

      for (int j = 0; j < jars.length; j++)
      {
        if (jars[j].getAbsolutePath().endsWith(".jar"))
        {
          try
          {
            URL url = jars[j].toURL();
            super.addURL(url);               
          }
          catch (MalformedURLException e)
          {
            throw new IllegalArgumentException(e.toString());
          }
        }
      }
    }
  }

//----------------------------------------------------------------------
/**
 * Returns the repository names (in a list of Strings).
 *
 * @return a list of strings that indicate the repositories that were
 * added to search for classes/resources.
 */
  
  public List getRepositories()
  {
    return(repositories_);
  }
}
