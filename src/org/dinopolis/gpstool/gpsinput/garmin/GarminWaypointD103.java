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

import java.awt.Color;
import org.dinopolis.gpstool.gpsinput.GPSWaypoint;

// ----------------------------------------------------------------------
/**
 * This class represents packets in Garmin data format D103.
 * 
 * @author Christof Dallermassl, Stefan Feitl
 * @version $Revision$
 */

public class GarminWaypointD103 extends GarminWaypointBase
{
  protected int display_option_;
  protected int symbol_;
  protected int symbol_type_;

  protected final static byte WAYPOINT_TYPE = 103;

  protected final static String[] DISPLAY_OPTIONS = new String[]{"symbol+name", "symbol", "symbol+comment"};

  // protected final static String[] SYMBOL_NAMES =
  // new String[] {"wpt_dot","house","gas","car","fish","boat","anchor","wreck","exit",
  // "skull","flag","camp","circle","deer","first_aid","back_track"};

  /** mapping from D103 symbol names to GarminWaypointSymbol constants */
  protected final static int[] SYMBOL_TYPE = new int[]{18, 10, 8220, 170, 7, 150, 0, 19, 177, 14, 178, 151, 171, 156, 8196};

  /**
   * Default constructor 
   */
  public GarminWaypointD103()
  {
  }

  /**
   * Constructor from a byte buffer
   * @param buffer the buffer
   */
  public GarminWaypointD103(int[] buffer)
  {
    identification_ = GarminDataConverter.getGarminString(buffer, 2, 6).trim();
    latitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer, 8);
    longitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer, 12);
    // unused = GarminDataConverter.getGarminLongWord(buffer,16);
    comment_ = GarminDataConverter.getGarminString(buffer, 20, 40).trim();
    symbol_ = GarminDataConverter.getGarminByte(buffer, 60);
    symbol_type_ = SYMBOL_TYPE[symbol_];
    symbol_name_ = GarminWaypointSymbols.getSymbolName(symbol_type_);
    //display_ = GarminDataConverter.getGarminByte(buffer, 61);
    display_option_ = GarminDataConverter.getGarminByte(buffer,61);
  }

  /**
   * Constructor from a garmin packet
   * @param pack the packet
   */
  public GarminWaypointD103(GarminPacket pack)
  {
    identification_ = pack.getNextAsString(6).trim();
    latitude_ = pack.getNextAsSemicircleDegrees();
    longitude_ = pack.getNextAsSemicircleDegrees();
    pack.getNextAsLongWord(); // unused
    comment_ = pack.getNextAsString(40).trim();
    symbol_ = pack.getNextAsByte();
    symbol_type_ = SYMBOL_TYPE[symbol_];
    symbol_name_ = GarminWaypointSymbols.getSymbolName(symbol_type_);
    display_option_ = pack.getNextAsByte();
    //display_options_ = DISPLAY_OPTIONS[display_];
  }

  /**
   * Copy Constructor
   * @param waypoint the other waypoint
   */
  public GarminWaypointD103(GPSWaypoint waypoint)
  {
    String tmp;
    int val = -1;

    tmp = waypoint.getIdentification();
    identification_ = tmp == null ? "" : tmp;
    latitude_ = waypoint.getLatitude();
    longitude_ = waypoint.getLongitude();
    tmp = waypoint.getComment();
    comment_ = tmp == null ? "" : tmp;
    symbol_type_ = (short) GarminWaypointSymbols.getSymbolId(waypoint.getSymbolName());
    if (symbol_type_ < 0)
      symbol_type_ = 18; // default symbol (wpt_dot)

    // Convert garmin standard symbol types to symbol type used by D103
    for (int i = 0; i < SYMBOL_TYPE.length; i++)
    {
      if (SYMBOL_TYPE[i] == symbol_type_)
        val = i;
    }
    if (val == -1)
      val = 0;
    symbol_ = val;

    symbol_name_ = GarminWaypointSymbols.getSymbolName(symbol_type_);
    display_option_ = 0;
  }

  // ----------------------------------------------------------------------
  /**
   * Convert data type to {@link org.dinopolis.gpstool.gpsinput.garmin.GarminPacket}
   * @param packet_id the packet id
   * 
   * @return GarminPacket representing content of data type.
   */
  public GarminPacket toGarminPacket(int packet_id)
  {
    int data_length = 6 + 4 + 4 + 4 + 40 + 1 + 1;

    GarminPacket pack = new GarminPacket(packet_id, data_length);
    pack.setNextAsString(identification_, 6, false);
    pack.setNextAsSemicircleDegrees(latitude_);
    pack.setNextAsSemicircleDegrees(longitude_);
    pack.setNextAsLongWord(0); // unused
    pack.setNextAsString(comment_, 40, false);
    pack.setNextAsByte(symbol_);
    pack.setNextAsByte(display_option_);

    return (pack);
  }

  // ----------------------------------------------------------------------
  /**
   * Get the Waypoint Type
   * 
   * @return Waypoint Type
   */
  public byte getType()
  {
    return (WAYPOINT_TYPE);
  }

  // ----------------------------------------------------------------------
  /**
   * Get the Waypoint Display Options
   * 
   * @return Waypoint Display Options
   */
  public String getDisplayOptions()
  {
    return (DISPLAY_OPTIONS[display_option_]);
  }

  // ----------------------------------------------------------------------
  /**
   * @see org.dinopolis.gpstool.gpsinput.garmin.GarminWaypoint#setSymbolName(java.lang.String)
   */
  public void setSymbolName(String name) throws UnsupportedOperationException
  {
    symbol_type_ = (short) GarminWaypointSymbols.getSymbolId(name);
    if (symbol_type_ < 0)
      symbol_type_ = 18; // default symbol (wpt_dot)

    int val = -1;
    // Convert garmin standard symbol types to symbol type used by D103
    for (int i = 0; i < SYMBOL_TYPE.length; i++)
    {
      if (SYMBOL_TYPE[i] == symbol_type_)
        val = i;
    }
    if (val == -1)
      val = 0;
    symbol_ = val;

    symbol_name_ = GarminWaypointSymbols.getSymbolName(symbol_type_);
  }
}
