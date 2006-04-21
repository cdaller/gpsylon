/***********************************************************************
 * @(#)$RCSfile$   $Revision$$Date$
 *
 * Copyright (c) Christof Dallermassl
 * Austria.
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

import org.dinopolis.gpstool.gpsinput.GPSWaypointImpl;

/**
 * Base class for all Garmin Waypoint types.
 * 
 * @author Christof Dallermassl / Marc Rechte
 * @version $Revision$
 */
public class GarminWaypointBase extends GPSWaypointImpl implements GarminWaypoint
{
  protected final static Color[] COLORS = new Color[]{Color.black, // black
      new Color(0x80, 0, 0), // dark red
      new Color(0, 0x80, 0), // dark green
      new Color(0x80, 0x80, 0), // dark yellow
      new Color(0, 0, 0x80), // dark blue
      new Color(0x80, 0x80, 0), // dark magenta
      new Color(0, 0x80, 0x80), // dark cyan
      Color.lightGray, // light gray
      Color.darkGray, // dark gray
      Color.red, // red
      Color.green, // green
      Color.yellow, // yellow
      Color.blue, // blue
      Color.magenta, // magenta
      Color.cyan, // cyan
      Color.white}; // white
  protected final static int DEFAULT_COLOR_INDEX = 15;

  // ----------------------------------------------------------------------
  /**
   * @see org.dinopolis.gpstool.gpsinput.garmin.GarminWaypoint#setSymbolName(java.lang.String)
   */
  public void setSymbolName(String name) throws UnsupportedOperationException
  {
    int symbol = GarminWaypointSymbols.getSymbolId(name);
    if (symbol < 0)
      symbol = 18; // default symbol (wpt_dot)
    symbol_name_ = GarminWaypointSymbols.getSymbolName(symbol);
  }

  // ----------------------------------------------------------------------
  /**
   * Get the Waypoint Type. This method MUST be overridden.
   * 
   * @return Waypoint Type.
   * @throws UnsupportedOperationException
   */
  public byte getType() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  // ----------------------------------------------------------------------
  /**
   * Get the Waypoint Class Type
   * 
   * @return Waypoint Class Type
   * @throws UnsupportedOperationException
   */
  public int getClassType() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  // ----------------------------------------------------------------------
  /**
   * Get the Waypoint Class Name
   * 
   * @return Waypoint Class Name
   * @throws UnsupportedOperationException
   */
  public String getClassName() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  // ----------------------------------------------------------------------
  /**
   * Get the Waypoint Color
   * 
   * @return Waypoint Color
   * @throws UnsupportedOperationException
   */
  public Color getColor() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  // ----------------------------------------------------------------------
  /**
   * Get the Waypoint Display Options
   * 
   * @return Waypoint Display Options
   * @throws UnsupportedOperationException
   */
  public String getDisplayOptions() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  // ----------------------------------------------------------------------
  /**
   * Get the Waypoint Attributes
   * 
   * @return Waypoint Attributes
   * @throws UnsupportedOperationException
   */
  public short getAttributes() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  // ----------------------------------------------------------------------
  /**
   * Get the Waypoint Subclass
   * 
   * @return Waypoint Subclass
   * @throws UnsupportedOperationException
   */
  public byte[] getSubclass() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  // ----------------------------------------------------------------------
  /**
   * Get the Depth (metres). A value of 1.0e25 means the parameter is unsupported or unknown.
   * 
   * @return Depth (metres)
   * @throws UnsupportedOperationException
   */
  public float getDepth() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  // ----------------------------------------------------------------------
  /**
   * Get the Distance (metres). A value of 1.0e25 means the parameter is unsupported or unknown.
   * 
   * @return Distance (metres)
   * @throws UnsupportedOperationException
   */
  public float getDistance() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  // ----------------------------------------------------------------------
  /**
   * Get the State Code
   * 
   * @return State Code
   * @throws UnsupportedOperationException
   */
  public String getStateCode() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  // ----------------------------------------------------------------------
  /**
   * Get the Country Code
   * 
   * @return Country Code
   * @throws UnsupportedOperationException
   */
  public String getCountryCode() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  // ----------------------------------------------------------------------
  /**
   * Get the Estimated Time Enroute
   * 
   * @return ETE
   * @throws UnsupportedOperationException
   */
  public long getEstimatedTimeEnroute() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  // ----------------------------------------------------------------------
  /**
   * Get the Facility String
   * 
   * @return Facility String
   * @throws UnsupportedOperationException
   */
  public String getFacility() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  // ----------------------------------------------------------------------
  /**
   * Get the City String
   * 
   * @return City String
   * @throws UnsupportedOperationException
   */
  public String getCity() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  // ----------------------------------------------------------------------
  /**
   * Get the Address String
   * 
   * @return Address String
   * @throws UnsupportedOperationException
   */
  public String getAddress() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  // ----------------------------------------------------------------------
  /**
   * Get the Crossroad String
   * 
   * @return Crossroad String
   * @throws UnsupportedOperationException
   */
  public String getCrossroad() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  // ----------------------------------------------------------------------
  /**
   * Get the Link Identification String
   * 
   * @return Link Identification String
   * @throws UnsupportedOperationException
   */
  public String getLinkIdentification() throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

      
  /**
   * @see org.dinopolis.gpstool.gpsinput.GPSWaypointImpl#toString()
   */
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    String s;
    buffer.append("GarminWaypoint[");
    try {
      s = Byte.toString(getType());
      buffer.append("type=").append(s).append(", ");      
    } 
    catch (UnsupportedOperationException e) {}
    buffer.append(super.toString());
    try {
      s = getDisplayOptions();
      buffer.append(", disp opt=").append(s);     
    } 
    catch (UnsupportedOperationException e) {}
    try {
      s = getColor().toString();
      buffer.append(", color=").append(s);    
    } 
    catch (UnsupportedOperationException e) {}
    try {
      s= getClassName();
      buffer.append(", class=").append(s);    
    } 
    catch (UnsupportedOperationException e) {}
    try {
      s = Short.toString(getAttributes());
      buffer.append(", attribs=").append(s);      
    } 
    catch (UnsupportedOperationException e) {}
    try {
      s = Float.toString(getDepth());
      buffer.append(", depth=").append(s);    
    } 
    catch (UnsupportedOperationException e) {}
    try {
      s = Float.toString(getDistance());
      buffer.append(", dist=").append(s);     
    } 
    catch (UnsupportedOperationException e) {}
    try {
      s = getStateCode();
      buffer.append(", state=").append(s);    
    } 
    catch (UnsupportedOperationException e) {}
    try {
      s = getCountryCode();
      buffer.append(", country=").append(s);      
    } 
    catch (UnsupportedOperationException e) {}
    try {
      s = Long.toString(getEstimatedTimeEnroute());
      buffer.append(", ete=").append(s);      
    } 
    catch (UnsupportedOperationException e) {}
    try {
      s = getFacility();
      buffer.append(", facility=").append(s);     
    } 
    catch (UnsupportedOperationException e) {}
    try {
      s = getCity();
      buffer.append(", city=").append(s);     
    } 
    catch (UnsupportedOperationException e) {}
    try {
      s = getAddress();
      buffer.append(", address=").append(s);      
    } 
    catch (UnsupportedOperationException e) {}
    try {
      s = getCrossroad();
      buffer.append(", cross road=").append(s);   
    } 
    catch (UnsupportedOperationException e) {}
    buffer.append("]");
    return(buffer.toString());
  }

}
