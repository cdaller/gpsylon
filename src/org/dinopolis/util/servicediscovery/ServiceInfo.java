
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

import java.net.URL;

//----------------------------------------------------------------------
/***
 * 'Service' located by the service discovery class.
 *
 * This class was inspired by the
 * org.apache.commons.discovery.ServiceDiscovery classes.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */
public class ServiceInfo
{
  String class_name_;
  ClassLoader loader_;
  URL location_;

  public ServiceInfo()
  {
  }

  public ServiceInfo(String class_name, ClassLoader loader, URL location)
  {
    setClassName(class_name);
    setLoader(loader);
    setURL(location);
  }

//----------------------------------------------------------------------
/***
 * Get the location of the service class.
 * @return the location of the service class.
 */
  public URL getURL()
  {
    return location_;
  }
   
//----------------------------------------------------------------------
/***
 * Set the location of the service class.
 * @param location the location of the service class.
 */
  public void setURL(URL location)
  {
    location_ = location;
  }
   

//----------------------------------------------------------------------
/***
 * Get name of the service class.
 * @return the name of the service class.
 */
  public String getClassName()
  {
    return class_name_;
  }
   
//----------------------------------------------------------------------
/***
 * Set the name of the service class.
 * @param class_name the name of the service class.
 */
  public void setClassName(String class_name)
  {
    class_name_ = class_name;
  }
   
 //----------------------------------------------------------------------
 /***
  * Get classloader of the service class.
  * @return the classloader of the service class.
  */
  public ClassLoader getLoader()
  {
    return loader_;
  }
   
//----------------------------------------------------------------------
/***
 * Set the classloader of the service class.
 * @param loader the classloader of the service class.
 */
  public void setLoader(ClassLoader loader)
  {
    loader_ = loader;
  }
   
//----------------------------------------------------------------------
/***
 * A String representation of this object.
 * @return A String representation of this object.
 */
  public String toString()
  {
    return "ServiceInfo[name=" + class_name_ + ", location=" + location_ + ", loader=" + loader_+"]";
  }

}

