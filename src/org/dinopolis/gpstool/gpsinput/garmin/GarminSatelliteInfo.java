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

import java.util.List;
import java.util.Vector;
import org.dinopolis.gpstool.gpsinput.SatelliteInfo;



//----------------------------------------------------------------------
/**
 * Represents the undocumented package with id 0x1a (26decimal) that
 * holds information about satellites. Does not really work with
 * garmin etrex summit model. So the usage is at your own risk!!!
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GarminSatelliteInfo
{

  protected SatelliteInfo[] satellite_infos_;
  
//----------------------------------------------------------------------
/**
 * Constructor using an int array of garmin data.
 * @param buffer the garmin package as int[].
 * @deprecated use the constructor with the GarminPackage instead
 */
  public GarminSatelliteInfo(int[] buffer)
  {

  }

//----------------------------------------------------------------------
/**
 * Constructor using an garmin package.
 * @param garmin_package the package from the gps device
 */
  public GarminSatelliteInfo(GarminPackage garmin_package)
  {
    List infos = new Vector();
    int prn;
    float elevation;
    float azimuth;
    int snr;
    int phase;
    for(int count = 0; count < 12; count++) // 12 channels
    {
      SatelliteInfo info;

          // valid for GPS12:
//       prn = garmin_package.getNextAsByte();
//       if(prn != 255)   // 255 -> no satellite on this channel
//       {
//         elevation = (float)garmin_package.getNextAsByte();
//         phase = garmin_package.getNextAsLongWord(); // fractional phase not used here
//         snr = garmin_package.getNextAsLongWord();
//         info = new SatelliteInfo(prn,elevation,0,snr);
//         System.out.println("SatelliteInfo: "+info);
//         infos.add(info);
//       }
          // valid for etrex:
      phase = (int)garmin_package.getNextAsLongWord(); // fractional phase not
																											 // used here // 2 bytes
																											 // or 4???
      snr = (int)garmin_package.getNextAsLongWord(); // 2 bytes or 4???
      elevation = (float)garmin_package.getNextAsByte();
      prn = garmin_package.getNextAsByte();
//       if(prn != 255)   // 255 -> no satellite on this channel
//       {
        info = new SatelliteInfo(prn,elevation,phase,snr);
        System.out.println("SatelliteInfo: "+info);
        infos.add(info);
//       }
    }
    satellite_infos_ = new SatelliteInfo[infos.size()];
    satellite_infos_ = (SatelliteInfo[])infos.toArray(satellite_infos_);
  }


//----------------------------------------------------------------------
/**
 * Returns a SatelliteInfo array providing information about available
 * satellites.
 * @return a SatelliteInfo array.
 */
  public SatelliteInfo[] getSatelliteInfos()
  {
    return(satellite_infos_);
  }

}


