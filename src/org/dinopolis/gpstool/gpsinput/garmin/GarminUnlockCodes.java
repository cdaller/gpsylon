/***********************************************************************
 * @(#)$RCSfile$   $Revision$$Date$
 *
 * Copyright (c) 2001-2003 Sandra Brueckler, Stefan Feitl
 * Written during an XPG-Project at the IICM of the TU-Graz, Austria
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

//----------------------------------------------------------------------
/**
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GarminUnlockCodes
{
  GarminPackage data_;
  
  public GarminUnlockCodes()
  {
//    data_ = new GarminPackage(0x6c);
  }

  public void addCode(String code)
  {
//     GarminPackage code = new GarminPackage(code.length()+1);
//     code.setNextAsString(code);
//     data_.appendData(code);
  }


  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    if(data_.getPackageSize() > 1024)
      buffer.append("data (limited to 1024 bytes)=").append(data_.toString().substring(0,1024));
    else
      buffer.append("data=").append(data_);
    buffer.append("]");
    return(buffer.toString());
  }
}
