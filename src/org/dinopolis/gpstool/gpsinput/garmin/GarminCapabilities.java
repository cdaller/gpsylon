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


package org.dinopolis.gpstool.gpsinput.garmin;

import java.util.HashSet;
import java.util.Vector;

//----------------------------------------------------------------------
/**
 * This class holds information concerning the capabilities of the
 * Garmin device connected to the serial port.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GarminCapabilities
{
    Vector product_capabilities_ = new Vector();
    HashSet capabilities_ = new HashSet();

  public GarminCapabilities(int[] buffer)
  {
    String full_name;
    String letter;
    int number;
    for (int i=0; i < (buffer[1]/3); i++)
    {
      // Add received capability to global capability variable
      letter = GarminDataConverter.getGarminString(buffer,2+3*i,1);
      number = GarminDataConverter.getGarminWord(buffer,3+3*i);
      full_name = letter + number;
//      String cap = new String(buffer,2+3*i,1) +(int)(buffer[3+3*i]+256*buffer[4+3*i]);
      capabilities_.add(full_name);
      product_capabilities_.add(full_name);
    }
  }
  
  public GarminCapabilities(GarminPackage pack)
  {
    String full_name;
    String letter;
    int number;
    for (int i=0; i < (pack.getPackageSize()/3); i++)
    {
      // Add received capability to global capability variable
      letter = pack.getNextAsString(1);
      number = pack.getNextAsWord();
      full_name = letter + number;
//      String cap = new String(buffer,2+3*i,1) +(int)(buffer[3+3*i]+256*buffer[4+3*i]);
      capabilities_.add(full_name);
      product_capabilities_.add(full_name);
    }
  }

  public String toString()
  {
    return(product_capabilities_.toString());
  }
  
//----------------------------------------------------------------------
/**
 * Get the identification.
 *
 * @return the identification.
 */
  public Vector getProductCapabilities() 
  {
    return (product_capabilities_);
  }
  

    public boolean hasCapability(String name)
    {
	return(capabilities_.contains(name));
    }
    
}
