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
 * This class represents packages in Garmin data format D210.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GarminRouteLinkD210 
{
  int class_;
  String class_name_;
  byte[] subclass_;
  String identification_ = "";

  public static final String[] CLASS_NAME = new String[] {"line","link","net","direct"
                                                          ,"snap"};

  public GarminRouteLinkD210()
  {
	}
  
  public GarminRouteLinkD210(int[] buffer)
  {
    class_ = GarminDataConverter.getGarminWord(buffer,2);
    subclass_ = GarminDataConverter.getGarminByteArray(buffer,4,18);
    identification_ = GarminDataConverter.getGarminString(buffer,22,(int)buffer[1]-21);
  }

  public GarminRouteLinkD210(GarminPackage pack)
  {
    class_ = pack.getNextAsWord();
    subclass_ = pack.getNextAsByteArray(18);
    identification_ = pack.getNextAsString(51);
  }

//----------------------------------------------------------------------
/**
* Get the class value.
* @return the class value.
*/
	public int getClassId()
	{
		return (class_);
	}

//----------------------------------------------------------------------
/**
* Set the class value.
* @param class The new class value.
*/
	public void setClassId(int class_id)
	{
		class_ = class_id;
	}


//----------------------------------------------------------------------
/**
* Get the ClassName value.
* @return the ClassName value.
*/
	public static String getClassName(int class_id)
	{
    if(class_id == 0xff)
      return(CLASS_NAME[CLASS_NAME.length-1]);
    if(class_id < CLASS_NAME.length-1)
      return(CLASS_NAME[class_id]);
    else
      return("unknown");
	}

//----------------------------------------------------------------------
/**
* Get the subclass value.
* @return the subclass value.
*/
	public byte[] getSubclass()
	{
		return (subclass_);
	}

//----------------------------------------------------------------------
/**
* Set the subclass value (18bytes!).
* @param subclass The new subclass value.
* @throws IllegalArgumentException if subclass does not hold 18 bytes.
*/
	public void setSubclass(byte[] subclass)
	{
		if(subclass.length != 18)
			throw new IllegalArgumentException("Subclass is not byte[18]!");
		subclass_ = subclass;
	}

//----------------------------------------------------------------------
/**
* Get the identification value.
* @return the identification value.
*/
	public String getIdentification()
	{
		return (identification_);
	}

//----------------------------------------------------------------------
/**
* Set the identification value.
* @param identification The new identification value.
*/
	public void setIdentification(String identification)
	{
		identification_ = identification;
	}


//----------------------------------------------------------------------
/**
 * Convert data type to {@link org.dinopolis.gpstool.gpsinput.garmin.GarminPackage}
 * @return GarminPackage representing content of data type.
 */
  public GarminPackage toGarminPackage(int package_id)
  {
    int data_length = 2 + 18 + Math.min(identification_.length()+1,51);
    GarminPackage pack = new GarminPackage(package_id,data_length);
    pack.setNextAsWord(class_);
    pack.setNextAsByteArray(subclass_);
    pack.setNextAsString(identification_,51,true);
    return (pack);
  }

  public String toString()
  {
		StringBuffer subclass = new StringBuffer();
		for(int count = 0; count < subclass_.length-1; count++)
			subclass.append(Integer.toHexString(subclass_[count] & 0xff)).append(",");
		subclass.append(Integer.toHexString(subclass_[subclass_.length-1] & 0xff));
    return("GarminRouteLinkD210[class="+class_+", class_name="+getClassName(class_)
           +", identification="+identification_+",subclass="+subclass.toString()+"]");
  }
}
