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


package org.dinopolis.gpstool.gpsinput;
import java.util.List;
import java.util.Vector;

//----------------------------------------------------------------------
/**
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GarminProduct
{
  int product_id_;
  int product_software_;
  String product_name_;

  public GarminProduct(char[] buffer)
  {
        product_id_=(int)(buffer[2]+256*buffer[3]);
        product_software_=(int)(buffer[4]+256*buffer[5]);

        char helper[] = new char[255];

        for (int i=0;i<249;i++)
        {
          helper[i]=buffer[i+6];
        }
        product_name_ = new String(helper);
  }

//----------------------------------------------------------------------
/**
 * Get the product id.
 *
 * @return the product id.
 */
  public int getProductId() 
  {
    return (product_id_);
  }
  
//----------------------------------------------------------------------
/**
 * Get the software version.
 *
 * @return the version
 */
  public int getProductSoftware() 
  {
    return (product_software_);
  }
  
//----------------------------------------------------------------------
/**
 * Get the software version.
 *
 * @return the identification.
 */
  public String getProductName() 
  {
    return (product_name_);
  }

    public String toString()
	{
	    return(getProductName() + " id:" + getProductId() + " sw:" + getProductSoftware()); 
	}
}
