/***********************************************************************
 * @(#)$RCSfile$   $Revision$ $Date$
 *
 * Copyright (c) 2001 IICM, Graz University of Technology
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
 * Holds information about a position (latitude/longitude/altitude).
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GPSPosition  
{
  double longitude_;
  double latitude_;
  double altitude_;
  String name_;
  
//----------------------------------------------------------------------
/**
 * Default Constructor
 */
  public GPSPosition()
  {
  }

//----------------------------------------------------------------------
/**
 * Constructing a GPSPosition.
 *
 * @param latitude_wgs84 the latitude in wgs84 format (degree).
 * @param north_south 
 * @param longitude_wgs84 the longitude in wgs84 format (degree).
 * @param east_west
 * @exception IllegalArgumentException if north_south or east_west are
 * neither "N","S" resp. "E", "W" or the latitude/longitude are
 * incorrect.
 */

  public GPSPosition(double latitude_wgs84,String north_south,
                     double longitude_wgs84, String east_west)
    throws IllegalArgumentException
  {
    this(latitude_wgs84,north_south,longitude_wgs84,east_west,Double.NaN,"");
  }

//----------------------------------------------------------------------
/**
 * Constructing a GPSPosition.
 *
 * @param latitude_wgs84 the latitude in wgs84 format (degree).
 * @param north_south 
 * @param longitude_wgs84 the longitude in wgs84 format (degree).
 * @param east_west
 * @param altitude
 * @exception IllegalArgumentException if north_south or east_west are
 * neither "N","S" resp. "E", "W" or the latitude/longitude are
 * incorrect.
 * @deprecated Altitude is not sent from all gps devices (or just in
 * some messages (e.g. NMEA), so do not use it. Better use the
 * <code>ALTITUDE</code> event sent from the {@link GPSDataProcessor}.
 */

  public GPSPosition(double latitude_wgs84,String north_south,
                     double longitude_wgs84, String east_west,
                     double altitude)
    throws IllegalArgumentException
  {
    this(latitude_wgs84,north_south,longitude_wgs84,east_west,altitude,"");
  }  

//----------------------------------------------------------------------
/**
 * Constructing a GPSPosition.
 *
 * @param latitude_wgs84 the latitude in wgs84 format (degree).
 * @param north_south 
 * @param longitude_wgs84 the longitude in wgs84 format (degree).
 * @param east_west
 * @param name the name of the position
 * @exception IllegalArgumentException if north_south or east_west are
 * neither "N","S" resp. "E", "W" or the latitude/longitude are
 * incorrect.
 */

  public GPSPosition(double latitude_wgs84,String north_south,
                     double longitude_wgs84, String east_west,
                     String name)
    throws IllegalArgumentException
  {
    this(latitude_wgs84,north_south,longitude_wgs84,east_west,Double.NaN,name);
  }
  
//----------------------------------------------------------------------
/**
 * Constructing a GPSPosition.
 *
 * @param latitude_wgs84 the latitude in wgs84 format (degree).
 * @param north_south 
 * @param longitude_wgs84 the longitude in wgs84 format (degree).
 * @param east_west
 * @param altitude
 * @param name the name of the position
 * @exception IllegalArgumentException if north_south or east_west are
 * neither "N","S" resp. "E", "W" or the latitude/longitude are
 * incorrect.
 * @deprecated Altitude is not sent from all gps devices (or just in
 * some messages (e.g. NMEA), so do not use it. Better use the
 * <code>ALTITUDE</code> event sent from the {@link GPSDataProcessor}.
 */

  public GPSPosition(double latitude_wgs84,String north_south,
                     double longitude_wgs84, String east_west,
                     double altitude, String name)
    throws IllegalArgumentException
  {
    north_south = north_south.toUpperCase();
    east_west = east_west.toUpperCase();
    if (!(north_south.equals("N") || north_south.equals("S")
          || east_west.equals("E") || east_west.equals("W")))
      throw new IllegalArgumentException("invalid gps position: " +
        latitude_wgs84 + north_south + " " + longitude_wgs84 + east_west);

    if(north_south.equals("N"))
      setLatitude(latitude_wgs84);
    else
      setLatitude(-latitude_wgs84);
    if(east_west.equals("E"))
      setLongitude(longitude_wgs84);
    else
      setLongitude(-longitude_wgs84);

    altitude_ = altitude;
    name_ = name;
  }  

//----------------------------------------------------------------------
/**
 * Constructing a GPSPosition.
 *
 * @param latitude the latitude (pos for north, negativ for south)
 * @param longitude the longitude (pos for east, negativ for west).
 */

  public GPSPosition(double latitude,
                     double longitude)
  {
    this(latitude,longitude,Double.NaN,"");
  }

//----------------------------------------------------------------------
/**
 * Constructing a GPSPosition.
 *
 * @param latitude the latitude (pos for north, negativ for south)
 * @param longitude the longitude (pos for east, negativ for west).
 * @param altitude
 * @deprecated Altitude is not sent from all gps devices (or just in
 * some messages (e.g. NMEA), so do not use it. Better use the
 * <code>ALTITUDE</code> event sent from the {@link GPSDataProcessor}.
 */

  public GPSPosition(double latitude,
                     double longitude,
                     double altitude)
  {
    this(latitude,longitude,altitude,"");
  }

//----------------------------------------------------------------------
/**
 * Constructing a GPSPosition.
 *
 * @param latitude the latitude (pos for north, negativ for south)
 * @param longitude the longitude (pos for east, negativ for west).
 * @param altitude
 * @deprecated Altitude is not sent from all gps devices (or just in
 * some messages (e.g. NMEA), so do not use it. Better use the
 * <code>ALTITUDE</code> event sent from the {@link GPSDataProcessor}.
 */

  public GPSPosition(double latitude,double longitude,
		     double altitude,String name)
  {
    altitude_ = altitude;
    name_ = name;
    setLatitude(latitude);
    setLongitude(longitude);
  }

//----------------------------------------------------------------------
/**
 * Copy Constructor
 *
 * @param gpsposition 
 */

  public GPSPosition(GPSPosition gpsposition)
  {
    setLatitude(gpsposition.latitude_);
    setLongitude(gpsposition.longitude_);
    altitude_ = gpsposition.altitude_;
    name_ = gpsposition.name_;
  }  


//----------------------------------------------------------------------
/**
 * Returns the longitude of the gps position. Returns positive values
 * for longitudes in the eastern hemisphere, negative values for the
 * western hemisphere.
 *
 * @return the longitude of the gps position.
 */
  public double getLongitude()
  {
    return(longitude_);
  }

//----------------------------------------------------------------------
/**
 * Sets the longitude of the gps position. Positive values
 * for longitudes are in the eastern hemisphere, negative values in the
 * western hemisphere.
 *
 * @param longitude the longitude of the gps position.
 */
  public void setLongitude(double longitude)
  {
    longitude_ = longitude;
  }

//----------------------------------------------------------------------
/**
 * Returns the latitude of the gps position. Returns positive values
 * for latitudes in the northern hemisphere, negative values for the
 * southern hemisphere.
 *
 * @return the latitude of the gps position.
 */
  public double getLatitude()
  {
    return(latitude_);
  }

//----------------------------------------------------------------------
/**
 * Sets the latitude of the gps position. Positive values
 * for latitudes are in the eastern hemisphere, negative values in the
 * western hemisphere.
 *
 * @param latitude the latitude of the gps position.
 */
  public void setLatitude(double latitude)
  {
    latitude_ = latitude;
  }

//----------------------------------------------------------------------
/**
 * Returns the altitude of the gps position or <code>Double.NaN</code> if
 * the altitude is unknown (not set).
 *
 * @return the altitude of the gps position or <code>Double.NaN</code> if
 * the altitude is unknown (not set).
 * @deprecated Altitude is not sent from all gps devices (or just in
 * some messages (e.g. NMEA), so do not use it. Better use the
 * <code>ALTITUDE</code> event sent from the {@link GPSDataProcessor}.
 */
  public double getAltitude()
  {
    return(altitude_);
  }

//----------------------------------------------------------------------
/**
 * Sets the altitude of the gps position.
 *
 * @param altitude the altitude of the gps position.
 * @deprecated Altitude is not sent from all gps devices (or just in
 * some messages (e.g. NMEA), so do not use it. Better use the
 * <code>ALTITUDE</code> event sent from the {@link GPSDataProcessor}.
 */
  public void setAltitude(double altitude)
  {
    altitude_ = altitude;
  }

//----------------------------------------------------------------------

  public String toString()
  {
    StringBuffer tostring = new StringBuffer("GPSPosition[");
    if (name_.length() > 0)
      tostring.append("name=").append(name_).append(", ");

    tostring.append("lat: "+ latitude_ + ", long:" + longitude_ );

//     if (altitude_ != Double.NaN)
//       tostring.append(", alt:" + altitude_);
    tostring.append("]");
    return(tostring.toString());
  }

//----------------------------------------------------------------------

  public boolean equals(Object pos)
  {
    if(!(pos instanceof GPSPosition))
      return(false);
    return((latitude_ == ((GPSPosition)pos).latitude_)
	   && (longitude_ == ((GPSPosition)pos).longitude_));
  }
  
}


