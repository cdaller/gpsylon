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

//______________________________________________________________________
//______________________________________________________________________
/**
 * This class is responsible for creating the parts of the message format.
 * 
 */
public class DebugMessageFormatFactory
{

      // the getPackage() method does not work in the linux jdk1.2, so 
      // the packagename is hard coded in the meantime.
  // static final String CLASS_PACKAGE = DebugMessageFormatFactory.class.getPackage().getName();
  static final String CLASS_PACKAGE = "org.dinopolis.util.debug";
  static final String CLASS_PREFIX = CLASS_PACKAGE + ".DebugMessage";
 
//______________________________________________________________________
/**
 * Returns a new object of the specified DebugMessage. The class name
 * is created by appending the given name in uppercase to the prefix
 * "DebugMessage".
 *
 * @param name the name of the keyword of the debug message.
 * @return a new object of the specified DebugMessage.   
 */
  public static DebugMessageFormatObject getMessageFormatObject(String name)
    throws ClassNotFoundException, IllegalAccessException, InstantiationException
  {
    String class_name = CLASS_PREFIX + name.toUpperCase();
//    System.out.println("trying to load class: " + class_name);
    DebugMessageFormatObject message_obj = (DebugMessageFormatObject)
      Class.forName(class_name).newInstance();
    return(message_obj);
  }
}










