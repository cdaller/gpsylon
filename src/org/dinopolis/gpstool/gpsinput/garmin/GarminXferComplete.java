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
 * Represents a Xfer_Complete packet from a garmin device.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GarminXferComplete
{
//   public static final int ALMANAC_COMPLETE = 1;
//   public static final int PROXIMITY_WAYPOINTS__COMPLETE = 3;
//   public static final int ROUTES_COMPLETE = 4;
//   public static final int TRACKS_COMPLETE = 6;
//   public static final int WAYPOINTS_COMPLETE = 7;

  protected int num_packets_;
  
//----------------------------------------------------------------------
/**
 * Constructor using an int array of garmin data.
 * @param buffer the garmin packet as int[].
 * @deprecated use the constructor with the GarminPacket instead
 */
  public GarminXferComplete(int[] buffer)
  {
    num_packets_ = buffer[3];
  }

//----------------------------------------------------------------------
/**
 * Constructor using an garmin packet.
 * @param garmin_packet the packet from the gps device
 */
  public GarminXferComplete(GarminPacket garmin_packet)
  {
    num_packets_ = garmin_packet.get();
  }

  

//----------------------------------------------------------------------
/**
 * Returns the number of packets transfered.
 * @return the number of packets transfered.
 */
  public int getNumberPackets()
  {
    return(num_packets_);
  }

// //----------------------------------------------------------------------
// /**
//  * Returns the type of transfer complete.
//  * @return the type of transfer complete (Route, track, ...).
//  */
//   public int getCompleteType()
//   {
//     return(complete_type_);
//   }
}
