
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

package org.dinopolis.util.servicediscovery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;


//----------------------------------------------------------------------
/***
 * <p>Implement the JDK1.3 'Service Provider' specification.
 * ( http://java.sun.com/j2se/1.3/docs/guide/jar/jar.html )
 * </p>
 *
 * The caller will first configure the discoverer by adding (in the
 * desired order) all the places to look for the
 * META-INF/services. Currently we support loaders. The directory
 * META-INF/services contains the following information: for every
 * service a file exists that is named like the service
 * interface/class/abstract class. This files contains names of
 * classes that implement this interface or extend this class/abstract
 * class.
 * <p>
 * The findServices() method will check every class loader for the
 * META-INF/services directory.
 * <p>
 * This class was inspired by the
 * org.apache.commons.discovery.ServiceDiscovery classes.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */
public class ServiceDiscovery
{
  protected static final String SERVICE_HOME = "META-INF/services/";
  
  protected Vector class_loaders_ = new Vector();
  
//----------------------------------------------------------------------
/**
 * Construct a new service discoverer
 */
  public ServiceDiscovery()
  {
    this(true);
  }

//----------------------------------------------------------------------
/**
 * Construct a new service discoverer.
 *
 * @param use_system_classloader if set to true, the system
 * classloader is added, if false, no default class loader is set.
 */
  public ServiceDiscovery(boolean use_system_classloader)
  {
    if(use_system_classloader)
      addClassLoader(ClassLoader.getSystemClassLoader());
  }


//----------------------------------------------------------------------
/*** Specify a new class loader to be used in searching.
 *   The order of loaders determines the order of the result.
 *  It is recommended to add the most specific loaders first.
 */
  public void addClassLoader(ClassLoader loader)
  {
    class_loaders_.addElement( loader );
  }

//----------------------------------------------------------------------
/**
 * Convenience method to find the thread class loader.
 * Usefull in jdk1.1, to avoid other introspection hacks.
 */
  public ClassLoader getThreadClassLoader()
  {
    return(Thread.currentThread().getContextClassLoader());
  }

//----------------------------------------------------------------------
/**
 * Returns information about services found for the given
 * class/interface name.
 *
 * @param name
 * @return information about services found for the given
 * class/interface name.
 */
  public ServiceInfo[] findServices(String name)
  {
        // use each loader to find if META-INF/services.
        // find all resources, etc.
    
    Vector results = new Vector();
    
    String service_file = ServiceDiscovery.SERVICE_HOME + name;

        // For each loader
    for(int loader_count=0; loader_count<class_loaders_.size() ; loader_count++ )
    {
      ClassLoader loader=(ClassLoader)class_loaders_.elementAt(loader_count);

      Enumeration enum=null;
      try
      {
        enum=loader.getResources( service_file );
      }
      catch( IOException ex )
      {
        ex.printStackTrace();
      }
      if( enum==null ) continue;

      while( enum.hasMoreElements() )
      {
        try
        {
          URL url=(URL)enum.nextElement();

//           URL base_url=new URL( url, "../../.." );
//           System.out.println("XXX BaseURL " + base_url + " url: " + url );
                   
          InputStream is = url.openStream();
                   
          if( is != null )
          {
            try
            {
                  // This code is needed by EBCDIC and other strange systems.
                  // It's a fix for bugs reported in xerces
              BufferedReader rd;
              try
              {
                rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
              }
              catch (java.io.UnsupportedEncodingException e)
              {
                rd = new BufferedReader(new InputStreamReader(is));
              }
                           
              try
              {
                String service_class_name;
                while( (service_class_name = rd.readLine()) != null)
                {
                  service_class_name.trim();
                  if("".equals(service_class_name))
                    continue;
                  if(service_class_name.startsWith( "#" ))
                    continue;
                  ServiceInfo sinfo=new ServiceInfo();
                  sinfo.setClassName(service_class_name);
                  sinfo.setLoader(loader);
                  sinfo.setURL(url);
                  results.add(sinfo);
//                                    System.out.println("XXX " + sinfo.toString());
                }
              }
              finally
              {
                rd.close();
              }
            }
            finally
            {
              is.close();
            }
          }
        }
        catch (MalformedURLException ex)
        {
          ex.printStackTrace();
        }
        catch (IOException ioe)
        {
          ; // ignore
        }
      }
    }

    ServiceInfo result_array[]=new ServiceInfo[results.size()];
    results.copyInto(result_array);
    return(result_array);
  }


//----------------------------------------------------------------------
/**
 * Returns services found for the given class/interface name. If the
 * given class name is not a valid class, an empty array is returned.
 *
 * @param class_name the class to search.
 * @return the services that implement the given class and are found
 * by the {@link #findServices(String)} method.
 */
  public Object[] getServices(String class_name)
  {
    try
    {
      return(getServices(Class.forName(class_name)));
    }
    catch(ClassNotFoundException cnfe)
    {
      return(new Object[0]);
    }
  }
  
//----------------------------------------------------------------------
/**
 * Returns instances of services found for the given class/interface name.
 *
 * @param clazz the class to search.
 * @return the services that implement the given class and are found
 * by the {@link #findServices(String)} method.
 */
  public Object[] getServices(Class clazz)
  {
    ServiceInfo[] service_infos = findServices(clazz.getName());
    Vector results = new Vector();
    Object service;
    ClassLoader loader;
    for(int count = 0; count < service_infos.length; count++)
    {
      try
      {
        loader = service_infos[count].getLoader();

        service = loader.loadClass(service_infos[count].getClassName()).newInstance();
//         if(Debug.DEBUG)
//           Debug.println("service","adding service "+service);
        
            // check, if the service is really an implementation of the given class:
        if(clazz.isInstance(service))
          results.add(service);
        else
          System.err.println("ServiceDiscovery: Service '"+service_infos[count].getClassName()
                             +"' is not an instance of class "+clazz.getName());
      }
      catch(ClassNotFoundException cnfe)
      {
        cnfe.printStackTrace();
      }
      catch(InstantiationException ie)
      {
        ie.printStackTrace();
      }
      catch(IllegalAccessException iae)
      {
        iae.printStackTrace();
      }
    }
    
    Object result_array[] = new Object[results.size()];
    results.copyInto(result_array);
    return(result_array);
  }
}
