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
 * @author Sandra Brueckler, Stefan Feitl
 * @version $Revision$
 */

public class GarminPVT
{
  protected float alt_;
  protected float epe_;
  protected float eph_;
  protected float epv_;
  protected int fix_;
  protected double tow_;
  protected double lat_;
  protected double lon_;
  protected float east_;
  protected float north_;
  protected float up_;
  protected float msl_height_;
  protected int leap_seconds_;
  protected int wn_days_;

  public GarminPVT()
  {
  }

//----------------------------------------------------------------------
/**
 * Get the value for type of position fix (0:unusable, 1:invalid,
 * 2:2D, 3:3D, 4:2D-differential, 5:3D-differential).
 *
 * @return the fix value.
 */
  public int getFix()
  {
    return(fix_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the value for type of position fix.
 *
 * @param comment the value representing the type of position fix.
 */
  protected void setFix(int fix)
  {
    fix_ = fix;
  }

//----------------------------------------------------------------------
/**
 * Print PVT data in human readable form.
 */
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("GarminPVT[");
    buffer.append("alt=").append(alt_).append(", ");
    buffer.append("epe=").append(epe_).append(", ");
    buffer.append("eph=").append(eph_).append(", ");
    buffer.append("epv=").append(epv_).append(", ");
    buffer.append("fix=").append(fix_).append(", ");
    buffer.append("tow=").append(tow_).append(", ");
    buffer.append("lat=").append(lat_).append(", ");
    buffer.append("lon=").append(lon_).append(", ");
    buffer.append("east=").append(east_).append(", ");
    buffer.append("north=").append(north_).append(", ");
    buffer.append("up=").append(up_).append(", ");
    buffer.append("msl_height=").append(msl_height_).append(", ");
    buffer.append("leap_seconds=").append(leap_seconds_).append(", ");
    buffer.append("wn_days=").append(wn_days_);
    buffer.append("]");

    return(buffer.toString());
  }
	/**
	 * Returns the altitude above wgs84-ellipsoid [m].
	 * @return float
	 */
	public float getAlt()
	{
		return alt_;
	}

	/**
	 * Returns the movement speed to the east [m/s].
	 * @return float
	 */
	public float getEast()
	{
		return east_;
	}

	/**
	 * Returns the estimated position error 2sigma [m].
	 * @return float
	 */
	public float getEpe()
	{
		return epe_;
	}

	/**
	 * Returns the estimated position error horizontal only [m].
	 * @return float
	 */
	public float getEph()
	{
		return eph_;
	}

	/**
	 * Returns the estimated position error vertical only [m].
	 * @return float
	 */
	public float getEpv()
	{
		return epv_;
	}

	/**
	 * Returns the latitude [degrees]: reported in rad, has to be converted to degrees.
	 * @return double
	 */
	public double getLat()
	{
		return lat_;
	}

	/**
	 * Returns the difference between GPS and UTC [s].
	 * @return int
	 */
	public int getLeapSeconds()
	{
		return leap_seconds_;
	}

	/**
	 * Returns the longitude [degreees]: reported in rad, has to be converted to degrees.
	 * @return double
	 */
	public double getLon()
	{
		return lon_;
	}

	/**
	 * Returns the height of the wgs84-ellipsoid above/below MSL [m].
	 * @return float
	 */
	public float getMslHeight()
	{
		return msl_height_;
	}

	/**
	 * Returns the movement speed to the north [m/s], movements to the south are
   * reported by negative numbers.
	 * @return float
	 */
	public float getNorth()
	{
		return north_;
	}

	/**
	 * Returns the time of week [s]: number of seconds since the
	 * beginning of the current week.
	 * @return double
	 */
	public double getTow()
	{
		return tow_;
	}

	/**
	 * Returns the movement speed upwards [m/s], movement downwards is
   * reported by negative numbers.
	 * @return float
	 */
	public float getUp()
	{
		return up_;
	}

	/**
	 * Returns the number of days since UTC Dec 31, 1989 to the
	 * beginning of the current week.
	 * @return int
	 */
	public int getWnDays()
	{
		return wn_days_;
	}

}
