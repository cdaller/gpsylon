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

import org.dinopolis.gpstool.gpsinput.GPSTrackpoint;


//----------------------------------------------------------------------
/**
 * @author Sandra Brueckler, Stefan Feitl
 * @version $Revision$
 */

public class GarminTrackpointD301 implements GarminTrackpoint
{
  public int display_;
  public double latitude_;
  public double longitude_;
  public long time_;
  public float altitude_;
  public float depth_;
  public boolean new_track_;

  protected final static int TRACKPOINT_TYPE = 301;
  
  public GarminTrackpointD301()
  {
  }

  public GarminTrackpointD301(int[] buffer)
  {
    latitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer,2);
    longitude_ = GarminDataConverter.getGarminSemicircleDegrees(buffer,6);
    time_ = GarminDataConverter.getGarminLong(buffer,10);
    altitude_ = GarminDataConverter.getGarminFloat(buffer,14);
    depth_ = GarminDataConverter.getGarminFloat(buffer,18);
    new_track_ = GarminDataConverter.getGarminBoolean(buffer,22);
  }

  public GarminTrackpointD301(GarminPackage pack)
  {
    latitude_ = pack.getNextAsSemicircleDegrees();
    longitude_ = pack.getNextAsSemicircleDegrees();
    time_ = pack.getNextAsLong();
    altitude_ = pack.getNextAsFloat();
    depth_ = pack.getNextAsFloat();
    new_track_ = pack.getNextAsBoolean();
  }

  public GarminTrackpointD301(GPSTrackpoint trackpoint)
  {
    latitude_ = trackpoint.getLatitude();  
    longitude_ = trackpoint.getLongitude();
    time_ = GarminDataConverter.convertDateToGarminTime(trackpoint.getDate());

    if (trackpoint.hasValidAltitude())
      altitude_ = (float)trackpoint.getAltitude();
    else
      altitude_ = 1.0E25f;

    depth_ = 1.0E25f;
    new_track_ = trackpoint.isNewTrack();
  }

//----------------------------------------------------------------------
/**
 * Convert data type to {@link GarminPackage}
 * @return GarminPackage representing content of data type.
 */
  public GarminPackage toGarminPackage(int package_id)
  {
    int data_length = 4 + 4 + 4 + 4 + 4 + 1;
    GarminPackage pack = new GarminPackage(package_id,data_length);
    pack.setNextAsSemicircleDegrees(latitude_);
    pack.setNextAsSemicircleDegrees(longitude_);
    pack.setNextAsLong(time_);
    pack.setNextAsFloat(altitude_);
    pack.setNextAsFloat(depth_);
    pack.setNextAsBoolean(new_track_);
    return (pack);
  }
//----------------------------------------------------------------------
/**
 * Get the Trackpoint Type
 *
 * @return Trackpoint Type
 * @throws GarminUnsupportedMethodException
 */
  public int getType() throws GarminUnsupportedMethodException
  {
    return(TRACKPOINT_TYPE);
  }

//----------------------------------------------------------------------
/**
 * Get the Latitude (degrees)
 *
 * @return Latitude (degrees)
 * @throws GarminUnsupportedMethodException
 */
  public double getLatitude() throws GarminUnsupportedMethodException
  {
    return(latitude_);
  }

//----------------------------------------------------------------------
/**
 * Set the Latitude (degrees)
 *
 * @throws GarminUnsupportedMethodException
 */
  public void setLatitude(double latitude) throws GarminUnsupportedMethodException
  {
    latitude_ = latitude;
  }

//----------------------------------------------------------------------
/**
 * Get the Longitude (degrees)
 *
 * @return Longitude (degrees)
 * @throws GarminUnsupportedMethodException
 */
  public double getLongitude() throws GarminUnsupportedMethodException
  {
    return(longitude_);
  }

//----------------------------------------------------------------------
/**
 * Set the Longitude (degrees)
 *
 * @throws GarminUnsupportedMethodException
 */
  public void setLongitude(double longitude) throws GarminUnsupportedMethodException
  {
    longitude_ = longitude;
  }

//----------------------------------------------------------------------
/**
 * Get the time when the point was recorded. Is expressed as number of
 * seconds since UTC Dec 31, 1989 - 12:00 AM.
 *
 * @return Time (seconds)
 * @throws GarminUnsupportedMethodException
 */
 public long getTime() throws GarminUnsupportedMethodException
 {
   return(time_);
 }

//----------------------------------------------------------------------
/**
 * Set the time when the point was recorded. Is expressed as number of
 * seconds since UTC Dec 31, 1989 - 12:00 AM.
 *
 * @throws GarminUnsupportedMethodException
 */
 public void setTime(long time) throws GarminUnsupportedMethodException
 {
   time_ = time;
 }

//----------------------------------------------------------------------
/**
 * Get the Altitude (meters).  A value of 1.0e25 means the parameter is
 * unsupported or unknown.
 *
 * @return Altitude (meters)
 * @throws GarminUnsupportedMethodException
 */
  public float getAltitude() throws GarminUnsupportedMethodException
  {
    return(altitude_);
  }

//----------------------------------------------------------------------
/**
 * Set the Altitude (meters).  A value of 1.0e25 means the parameter is
 * unsupported or unknown.
 *
 * @throws GarminUnsupportedMethodException
 */
  public void setAltitude(float altitude) throws GarminUnsupportedMethodException
  {
    altitude_ = altitude;
  }

//----------------------------------------------------------------------
/**
 * Get the Depth (meters). A value of 1.0e25 means the parameter is
 * unsupported or unknown.
 *
 * @return Depth (meters)
 * @throws GarminUnsupportedMethodException
 */
  public float getDepth() throws GarminUnsupportedMethodException
  {
    return(depth_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the Depth (meters). A value of 1.0e25 means the parameter is
 * unsupported or unknown.
 *
 * @throws GarminUnsupportedMethodException
 */
  public void setDepth(float depth) throws GarminUnsupportedMethodException
  {
    depth_ = depth;
  }
  
//----------------------------------------------------------------------
/**
 * Is this the beginning of a new track segment? If true, this point
 * marks the beginning of a new track segment.
 *
 * @return Beginning of new track segment (boolean)
 * @throws GarminUnsupportedMethodException
 */
  public boolean isNewTrack() throws GarminUnsupportedMethodException
  {
    return(new_track_);
  }

//----------------------------------------------------------------------
/**
 * Is this the beginning of a new track segment? If true, this point
 * marks the beginning of a new track segment.
 *
 * @throws GarminUnsupportedMethodException
 */
  public void setNewTrack(boolean new_track) throws GarminUnsupportedMethodException
  {
    new_track_ = new_track;
  }

  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("GarminTrackpoint[");
    buffer.append("lat=").append(latitude_).append(", ");
    buffer.append("lon=").append(longitude_).append(", ");
    buffer.append("time=").append(time_).append(", ");
    buffer.append("alt=").append(altitude_).append(", ");
    buffer.append("depth=").append(depth_).append(", ");
    buffer.append("new_track=").append(new_track_);
    buffer.append("]");
    return(buffer.toString());
  }
}
