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

//----------------------------------------------------------------------
/**
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GarminRouteLinkTypeD210 
{
  int class_;
  String class_name_;
  byte[] subclass_;
  String identification_;

  public static final String[] CLASS_NAME = new String[] {"line","link","net","direct"
                                                          ,"snap"};
  
  public GarminRouteLinkTypeD210(char[] buffer)
  {
    class_ = GarminDataConverter.getGarminWord(buffer,2);
    int class_index = class_;
    if(class_ == -1)
      class_index = CLASS_NAME.length;
    if(class_index < CLASS_NAME.length)
      class_name_ = CLASS_NAME[class_index];
    else
      class_name_ = "unknown";
    subclass_ = GarminDataConverter.getGarminByteArray(buffer,4,18);
    identification_ = GarminDataConverter.getGarminString(buffer,22,(int)buffer[1]-21);
  }


  public String toString()
  {
    return("GarminRouteLinkTypeD210[class="+class_+", class_name="+class_name_
           +", identification="+identification_+"]");
  }
}
