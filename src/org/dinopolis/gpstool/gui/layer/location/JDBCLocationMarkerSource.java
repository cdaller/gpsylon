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


package org.dinopolis.gpstool.gui.layer.location;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.dinopolis.gpstool.gui.layer.location.LocationMarker;

import org.dinopolis.gpstool.util.geoscreen.GeoScreenList;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import org.dinopolis.util.Debug;
import java.sql.Types;
import org.dinopolis.util.Resources;

//----------------------------------------------------------------------
/**
 * This class reads creates Location Marker object from databases via jdbc.
 * <p> After having created this LocationMarkerSource, call
 * initialize() which will create all internal datastructures and
 * calls readLocationMarkers().
 *
 * @author Christof Dallermassl
 * @version $Revision$ */

public class JDBCLocationMarkerSource implements LocationMarkerSource
{

  Connection connection_;
  String driver_name_;
  String jdbc_url_;
  String username_;
  String password_;
  Resources resources_;

//----------------------------------------------------------------------
/**
 * Constructs a location marker source that reads from a jdbc
 * connection.
 *
 * @param driver_name the name of the jdbc driver to use.
 * @param jdbc_url the url to use.
 * @param username the username to use.
 * @param password the password to use.
 */
  public JDBCLocationMarkerSource(String driver_name, String jdbc_url,
                                  String username, String password, Resources resources)
  {
    driver_name_ = driver_name;
    jdbc_url_ = jdbc_url;
    username_ = username;
    password_ = password;
    resources_ = resources;
  }

//----------------------------------------------------------------------
/**
 * Opens the connection to the jdbc database.
 *
 * @exception LocationMarkerSourceException if an error occured during
 * this operation.
 */
  public void open()
    throws LocationMarkerSourceException
  {
    try
    {
      if(Debug.DEBUG)
        Debug.println("JDBC_Location","connecting to jdbc database using driver: '"
                      +driver_name_+"' url: '"+jdbc_url_+"' username: '"+username_+"'");
      Class.forName(driver_name_);
      connection_ = DriverManager.getConnection(jdbc_url_,username_,password_);
    }
    catch(Exception e)
    {
      throw new LocationMarkerSourceException(e);
    }
  }
  
//----------------------------------------------------------------------
/**
 * Closes the connection to the jdbc database.
 *
 * @exception LocationMarkerSourceException if an error occured during
 * this operation.
 */
  public void close()
    throws LocationMarkerSourceException
  {
    try
    {
      connection_.close();
    }
    catch(SQLException e)
    {
      throw new LocationMarkerSourceException(e);
    }
  }
  
//----------------------------------------------------------------------
/**
 * Returns a list of LocationMarker objects in a GeoScreenList that
 * match the given parameters.
 *
 * @param north Northern limit for the locations.
 * @param south Southern limit for the locations to retrieve.
 * @param west Western limit for locations to retrieve.
 * @param east Eastern limit for locations to retrieve.
 * @return a list of LocationMarker objects.
 * @exception LocationMarkerSourceException if the location marker
 * source throws an exception on making the location marker
 * persistent, it is wrapped into a LocationMarkerSourceException
 */
  public GeoScreenList getLocationMarkers(float north,float south,
                                          float west,float east)
    throws LocationMarkerSourceException
  {
    return(getLocationMarkers(north,south,west,east,(GeoScreenList)null));
  }

//----------------------------------------------------------------------
/**
 * Returns a list of LocationMarker objects in a GeoScreenList that
 * match the given parameters.
 *
 * @param north Northern limit for the locations.
 * @param south Southern limit for the locations to retrieve.
 * @param west Western limit for locations to retrieve.
 * @param east Eastern limit for locations to retrieve.
 * @param location_list List to add the new locationmarkers to. If null, the
 *   LocationMarkerSource creates a new GeoScreenList object.
 * @return a list of LocationMarker objects.
 * @exception LocationMarkerSourceException if the location marker
 * source throws an exception on making the location marker
 * persistent, it is wrapped into a LocationMarkerSourceException
 */
  public GeoScreenList getLocationMarkers(float north,float south,
                                          float west,float east,
                                          GeoScreenList location_list)
    throws LocationMarkerSourceException
  {
    if(location_list == null)
      location_list = new GeoScreenList();

    try
    {
      PreparedStatement prst = connection_.prepareStatement("SELECT name,latitude,longitude,category_id FROM markers WHERE latitude < ? AND latitude > ? AND longitude > ? AND longitude < ? ORDER BY name");
      prst.setFloat(1,north);
      prst.setFloat(2,south);
      prst.setFloat(3,west);
      prst.setFloat(4,east);

      ResultSet rs = prst.executeQuery();
      if(rs != null)
      {
        while(rs.next())
        {
          location_list.add(createLocationMarker(rs));
        }
      }
    }
    catch(SQLException e)
    {
      throw new LocationMarkerSourceException(e);
    }
    
    return(location_list);
  }

//----------------------------------------------------------------------
/**
 * Returns a list of LocationMarker objects in a GeoScreenList that
 * match the given parameters and filter.
 *
 * @param north Northern limit for the locations.
 * @param south Southern limit for the locations to retrieve.
 * @param west Western limit for locations to retrieve.
 * @param east Eastern limit for locations to retrieve.
 * @param filter the filter to use to retrieve the location markers.
 * @param location_list List to add the new locationmarkers to. If null, the
 *   LocationMarkerSource creates a new GeoScreenList object.
 * @return a list of LocationMarker objects.
 * @exception LocationMarkerSourceException if the location marker
 * source throws an exception on making the location marker
 * persistent, it is wrapped into a LocationMarkerSourceException
 * @exception UnsupportedOperationException if the location marker
 * source is read only.
 */
  public GeoScreenList getLocationMarkers(float north,float south,
                                          float west,float east,
                                          LocationMarkerFilter filter,
                                          GeoScreenList location_list)
    throws LocationMarkerSourceException, UnsupportedOperationException
  {
    if(location_list == null)
      location_list = new GeoScreenList();

    try
    {
      JDBCPreparedStatementPart part = new JDBCPreparedStatementPart();
      part.appendQuery("SELECT name,latitude,longitude,category_id FROM markers "
                       +"WHERE latitude < ? AND latitude > ? AND longitude > ? AND longitude < ? AND ");
      part.addValue(new Float(north),Types.FLOAT);
      part.addValue(new Float(south),Types.FLOAT);
      part.addValue(new Float(west),Types.FLOAT);
      part.addValue(new Float(east),Types.FLOAT);
      part = LocationMarkerFilter.addToPreparedStatementPart(part,filter);
      part.appendQuery(" ORDER BY name");

      PreparedStatement prst = part.getPreparedStatement(connection_);
      
      ResultSet rs = prst.executeQuery();
      if(rs != null)
      {
        while(rs.next())
        {
          location_list.add(createLocationMarker(rs));
        }
      }
    }
    catch(SQLException e)
    {
      throw new LocationMarkerSourceException(e);
    }
    
    return(location_list);
  }

//----------------------------------------------------------------------
/**
 * Returns a list of LocationMarker objects in a GeoScreenList that
 * match the given parameters and filter.
 *
 * @param north Northern limit for the locations.
 * @param south Southern limit for the locations to retrieve.
 * @param west Western limit for locations to retrieve.
 * @param east Eastern limit for locations to retrieve.
 * @param filter the filter to use to retrieve the location markers.
 * @return a list of LocationMarker objects.
 * @exception LocationMarkerSourceException if the location marker
 * source throws an exception on making the location marker
 * persistent, it is wrapped into a LocationMarkerSourceException
 * @exception UnsupportedOperationException if the location marker
 * source is read only.
 */
  public GeoScreenList getLocationMarkers(float north,float south,
                                          float west,float east,
                                          LocationMarkerFilter filter)
    throws LocationMarkerSourceException, UnsupportedOperationException
  {
    return(getLocationMarkers(north,south,west,east,filter,null));
  }


//----------------------------------------------------------------------
/**
 * Adds a location marker to the source. The source is responsible to
 * make the location marker persistent.
 *
 * @param marker the new location marker to ge added.
 * @exception UnsupportedOperationException if the location marker
 * source is read only.
 * @exception LocationMarkerSourceException if the location marker
 * source throws an exception on making the location marker
 * persistent, it is wrapped into a LocationMarkerSourceException
 */
  public void putLocationMarker(LocationMarker marker)
    throws UnsupportedOperationException, LocationMarkerSourceException
  {
    try
    {
      // the first column is the autoinc id:
      PreparedStatement prst = connection_.prepareStatement("INSERT INTO markers (name, latitude, longitude, category_id) VALUES (?,?,?,?)");
          // TODO: escape single quote in all text fields!!!
      prst.setString(1,marker.getName());
      prst.setFloat(2,marker.getLatitude());
      prst.setFloat(3,marker.getLongitude());
      prst.setString(4,marker.getCategory().getId());

      int result = prst.executeUpdate();
      if(result == 0)
        throw new LocationMarkerSourceException("adding location marker was not successfull");
    }
    catch(SQLException e)
    {
      throw new LocationMarkerSourceException(e);
    }
  }


//----------------------------------------------------------------------
/**
 * If the source supports filters in requests this method must return
 * true, false otherwise. This implementation does support
 * filters, so it always returns true.
 *
 * @return true, if the source supports filters in requests.
 */
  public boolean supportsFilters()
  {
    return(true);
  }



//----------------------------------------------------------------------
/**
 * Creates a location marker from the given result set. The columns
 * must be: name, lat, long, category_id.
 *
 * @return the location marker.
 * @exception SQLException if a database error occurs.
 */
  protected LocationMarker createLocationMarker(ResultSet rs)
    throws SQLException
  {
    String name;
    float latitude;
    float longitude;
    String category_id;
    LocationMarkerCategory category;
    name = rs.getString(1);
    latitude = rs.getFloat(2);
    longitude = rs.getFloat(3);
    category_id = rs.getString(4);
    category = LocationMarkerCategory.getCategory(category_id,resources_);
    return(new LocationMarker (name,latitude,longitude,category));
  }
  
  public static void main(String[] args)
  {
//     JDBCLocationMarkerSource source =
//       new JDBCLocationMarkerSource("org.hsqldb.jdbcDriver",
//                                    "jdbc:hsqldb:/filer/cdaller/.gpsmap/marker/testdb","sa","");
//     try
//     {
//       JDBCUtil util = new JDBCUtil("org.hsqldb.jdbcDriver",
//                                    "jdbc:hsqldb:/filer/cdaller/.gpsmap/marker/testdb","sa","");
//       util.createLocationMarkerDatabase();
//       System.out.println("using marker db now");
//       source.open();
//       LocationMarker marker;

      
//       System.out.println("add markers to db...");
//       marker = new LocationMarker("test1",47.0f,14.0f);
//       source.putLocationMarker(marker);
//       marker = new LocationMarker("test2",50.0f,14.0f);
//       source.putLocationMarker(marker);

//       System.out.println("retrieve markers from db...");
//       GeoScreenList markers = source.getLocationMarkers(48f,-90f,-180f,180f);
//       Iterator iterator = markers.iterator();
//       while(iterator.hasNext())
//       {
//         System.out.println(iterator.next());
//       }
//     }
//     catch(Exception e)
//     {
//       e.printStackTrace();
//     }
//     finally
//     {
//       try
//       {
//         source.close();
//       }
//       catch(Exception e)
//       {
//         e.printStackTrace();
//       }
//     }
  }
}



