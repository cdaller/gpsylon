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
import org.dinopolis.gpstool.gpsinput.GPSException;

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

//----------------------------------------------------------------------
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
  
//----------------------------------------------------------------------
  public GarminCapabilities(GarminPacket pack)
  {
    String full_name;
    String letter;
    int number;
    for (int i=0; i < (pack.getPacketSize()/3); i++)
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


//----------------------------------------------------------------------
  /**
   * Defaults the capabilities according to the product info.
   * This is used for some old GPS which do not know to list their capabilities.
   * <p>Tested for Garmin 45XL (product id 41).
   * This constructor added by Marc Rechte
   */
  public GarminCapabilities(GarminProduct info) 
		throws GPSException 
	{
    String[] all = { "P0", "A100", "A200" }; // All GPS implement P0 + Wpt + Rte

    String[] lnk1 = { "L1", "A10" }; // Most GPS
    String[] lnk2 = { "L2", "A11" }; // Aviation GPS

    String[] dta1 = { "D100", "D200" }; // way point data, route header
    String[] dta2 = { "D100", "D201" }; 
    String[] dta3 = { "D101", "D201" }; 
    String[] dta4 = { "D102", "D201" }; 
    String[] dta13 = { "D103", "D201" }; 
    String[] dta5 = { "D104", "D201" }; 
    String[] dta6 = { "D150", "D201" }; 
    String[] dta7 = { "D151", "D200" }; 
    String[] dta8 = { "D151", "D201" }; 
    String[] dta9 = { "D152", "D200" }; 
    String[] dta10 = { "D152", "D201" }; 
    String[] dta11 = { "D154", "D201" }; 
    String[] dta12 = { "D155", "D201" }; 

    String[] trk1 = { "A300", "D300" }; // Trk with D300 header

    String[] prx1 = { "A400" }; // Prx with way point data
    String[] prx2 = { "A400", "D400" }; // Prx with D400 data
    String[] prx3 = { "A400", "D450" }; // Prx with D450 data
    String[] prx4 = { "A400", "D403" }; // Prx with D403 data

    String[] alm1 = { "A500", "D500" }; // Alm with D500 data
    String[] alm2 = { "A500", "D501" }; // Alm with D501 data
    String[] alm3 = { "A500", "D550" }; // Alm with D550 data
    String[] alm4 = { "A500", "D551" }; // Alm with D551 data

    String[] lnk, dta, trk, prx, alm;

	 switch (info.getProductId()) 
	 {
      case 7: // GPS 50
        lnk = lnk1; dta = dta1; trk = null; prx = null; alm = alm1; break;
      case 13: // GPS 75
      case 18: // GPS 65
      case 23: // GPS 75
      case 24: // GPS 95
      case 25: // GPS 85
      case 35: // GPS 95
      case 42: // GPS 75
        lnk = lnk1; dta = dta1; trk = trk1; prx = prx2; alm = alm1; break;
      case 14: // GPS 55
        lnk = lnk1; dta = dta1; trk = null; prx = prx2; alm = alm1; break;
      case 15: // GPS 55 AVD
        lnk = lnk1; dta = dta7; trk = null; prx = prx1; alm = alm1; break;
      case 20: // GPS 150
      case 33: // GNC 300
      case 34: // GPS 155, GPS 165
      case 52: // GNC 250
        lnk = lnk2; dta = dta6; trk = null; prx = prx3; alm = alm3; break;

      case 22: // GPS 95 AVD
        lnk = lnk1; dta = dta9; trk = trk1; prx = prx1; alm = alm1; break;
      case 36: // GPS95 AVD
        if (info.getProductSoftware() < 300) { // < v3.0
          lnk = lnk1; dta = dta9; trk = trk1; prx = prx1; alm = alm1; break;
        }
        else { // >= v3.0
          lnk = lnk1; dta = dta9; trk = trk1; prx = null; alm = alm1; break;
        }
      case 45: // GPS 90
        lnk = lnk1; dta = dta10; trk = trk1; prx = null; alm = alm1; break;
      case 29: // GPSMAP 205, GPSMAP 210, GPSMAP 220
        if (info.getProductSoftware() < 400) { // < v4.0
          lnk = lnk1; dta = dta3; trk = trk1; prx = prx1; alm = alm1; break;
        }
        else { // >= v4.0
          lnk = lnk1; dta = dta4; trk = trk1; prx = null; alm = alm1; break;
        }
      case 44: // GPSMAP 205
        lnk = lnk1; dta = dta3; trk = trk1; prx = prx1; alm = alm1; break;
      case 31: // GPS 40, GPS 45
      case 41: // GPS 45 XL, GPS 38, GPS 40
      case 47: // GPS 120
      case 55: // GPS 120 Chinese
      case 56: // GPS 38 Chinese, GPS 40 Chinese, GPS 45 Chinese
      case 59: // GPS II
      case 61: // GPS 120 Sounder
      case 62: // GPS 38 Japanese, GPS 40 Japanese
      case 74: // GPS 120 XL
        lnk = lnk1; dta = dta2; trk = trk1; prx = null; alm = alm1; break;
      case 39: // GPS 89
        lnk = lnk1; dta = dta8; trk = trk1; prx = null; alm = alm1; break;
      case 48: // GPSMAP 195
        lnk = lnk1; dta = dta11; trk = trk1; prx = null; alm = alm2; break;
      case 49: // GPSMAP 130, GPSMAP 135 Sounder, GPSMAP 175, GPSMAP 230, GPSMAP 235 Sounder
      case 76: // GPSMAP 130 Chinese, GPSMAP 230 Chinese
      case 88: // GPSMAP 215, GPSMAP 225
          lnk = lnk1; dta = dta4; trk = trk1; prx = prx1; alm = alm2; break;
      case 50: // GPSCOM 170
      case 53: // GPSCOM 190
      case 112: // GPS 92
          lnk = lnk1; dta = dta10; trk = trk1; prx = null; alm = alm2; break;
      case 64: // GNC 250 XL, GPS 150 XL
      case 98: // GNC 300 XL, GPS 155 XL
          lnk = lnk2; dta = dta6; trk = null; prx = prx3; alm = alm4; break;
      case 71: // GPS III Pilot
        lnk = lnk1; dta = dta12; trk = trk1; prx = null; alm = alm2; break;
      case 72: // GPS III
        lnk = lnk1; dta = dta5; trk = trk1; prx = null; alm = alm2; break;
      case 77: // GPS 12, GPS 12 XL
        if (info.getProductSoftware() < 301) { // < v3.01
          lnk = lnk1; dta = dta2; trk = trk1; prx = prx1; alm = alm2; break;
        }
        else if (info.getProductSoftware() < 350 || info.getProductSoftware() >= 361) { // v3.01 <= version < v3.50 or >=v3.61
          lnk = lnk1; dta = dta13; trk = trk1; prx = prx4; alm = alm2; break;
        }
        else { // v3.50 <= version < v3.61
          lnk = lnk1; dta = dta13; trk = trk1; prx = null; alm = alm2; break;
        }
      case 73: // GPS II Plus
      case 97: // GPS II Plus
        lnk = lnk1; dta = dta13; trk = trk1; prx = null; alm = alm2; break;
      case 87: // GPS 12
      case 95: // GPS 126, GPS 128
      case 96: // GPS 12, GPS 12 XL, GPS 48
      case 100: // GPS 126 Chinese, GPS 128 Chinese
      case 105: // GPS 12 XL Japanese
      case 106: // GPS 12 XL Chinese
        lnk = lnk1; dta = dta13; trk = trk1; prx = prx4; alm = alm2; break;
      default:
        throw new GPSException("Garmin GPS not supported for default capabilities, GPS Id is " 
															 + info.getProductId());
    }
    // set the capabilities
    for (int i = 0; i < all.length; i++) 
		{
			capabilities_.add(all[i]);
      product_capabilities_.add(all[i]);
    }
    for (int i = 0; i < lnk.length; i++) 
		{
			capabilities_.add(lnk[i]);
      product_capabilities_.add(lnk[i]);
    }
    for (int i = 0; i < dta.length; i++) 
		{
			capabilities_.add(dta[i]);
      product_capabilities_.add(dta[i]);
    }
    if (trk != null) { // some GPS have no track capabilities
      for (int i = 0; i < trk.length; i++) 
			{
				capabilities_.add(trk[i]);
        product_capabilities_.add(trk[i]);
      }
    }
    if (prx != null) { // some GPS have no proximity capabilities
      for (int i = 0; i < prx.length; i++) 
			{
				capabilities_.add(prx[i]);
        product_capabilities_.add(prx[i]);
      }
    }
    for (int i = 0; i < alm.length; i++) 
		{
			capabilities_.add(alm[i]);
      product_capabilities_.add(alm[i]);
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
