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

package org.dinopolis.gpstool.gpsinput;

//----------------------------------------------------------------------
/**
 * Holds information about gps satellites (like position and signal
 * strength).
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class SatelliteInfo  
{
  int prn_;
  float elevation_;
  float azimuth_;
  int snr_;


//----------------------------------------------------------------------
/**
 * Craeates a new SatelliteInfo
 *
 * @param PRN the number of the satellite
 * @param elevation elevation in degrees (90 maximum)
 * @param azimuth Azimuth, degrees from true north, 000 to 359
 * @param SNR, 00-99 dB (null when not tracking)
 */

  public SatelliteInfo(int PRN, float elevation, float azimuth, int SNR)
  {
    prn_ = PRN;
    elevation_ = elevation;
    azimuth_ = azimuth;
    snr_ = SNR;
  }
  
//----------------------------------------------------------------------
/**
 * Get the prn (number of satellite)
 *
 * @return the prn.
 */
  public int getPRN()
  {
    return (prn_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the prn.
 *
 * @param prn the prn.
 */
  public void setPRN(int prn)
  {
    prn_ = prn;
  }

//----------------------------------------------------------------------
/**
 * Get the elevation.
 *
 * @return the elevation.
 */
  public float getElevation()
  {
    return (elevation_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the elevation.
 *
 * @param elevation the elevation.
 */
  public void setElevation(float elevation)
  {
    elevation_ = elevation;
  }

//----------------------------------------------------------------------
/**
 * Get the azimuth.
 *
 * @return the azimuth.
 */
  public float getAzimuth()
  {
    return (azimuth_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the azimuth.
 *
 * @param azimuth the azimuth.
 */
  public void setAzimuth(float azimuth)
  {
    azimuth_ = azimuth;
  }

  
//----------------------------------------------------------------------
/**
 * Get the snr (strength (00 to 99 dB), 0 when not tracking)
 *
 * @return the snr.
 */
  public int getSNR()
  {
    return (snr_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the snr.
 *
 * @param snr the snr.
 */
  public void setSNR(int snr)
  {
    snr_ = snr;
  }
  

//----------------------------------------------------------------------
/**
 * Returns a string representation of this object.
 *
 * @return a string representation of this object.
 */
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("SatelliteInfo[");
    buffer.append("PRN: ");
    buffer.append(prn_);
    buffer.append(", elev: ");
    buffer.append(elevation_);
    buffer.append(", azimuth: ");
    buffer.append(azimuth_);
    buffer.append(", SNR: ");
    buffer.append(snr_);
    buffer.append("]");
    return(buffer.toString());
  }
    
}


