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

package org.dinopolis.gpstool.gpsinput;

import java.io.InputStream;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.dinopolis.gpstool.track.RouteImpl;
import org.dinopolis.gpstool.track.TrackImpl;
import org.dinopolis.gpstool.track.TrackpointImpl;
import org.dinopolis.gpstool.track.WaypointImpl;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.dinopolis.util.Debug;

//----------------------------------------------------------------------
/**
 * This class is able to read a file in GPX-Format and return the data
 * contained in this file in an appropriate data format for the GPSTool.
 *
 * @author Stefan Feitl
 * @version $Revision 1.5$
 */

public class ReadGPX 
{
  protected Vector routes_ = new Vector();
  protected Vector tracks_ = new Vector();
  protected Vector waypoints_ = new Vector();
  protected SimpleDateFormat date_format_ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
  private ParsePosition dummy_position_ = new ParsePosition(0);

  public ReadGPX()
  {
  }

//----------------------------------------------------------------------
/**
 * Get the list of routes that has been read from the source (file,
 * stream...)
 *
 * @return routes The list of routes read.
 */
  public List getRoutes() 
  {
    return((List)routes_);
  }

//----------------------------------------------------------------------
/**
 * Get the list of tracks that has been read from the source (file,
 * stream...)
 *
 * @return tracks The list of tracks read.
 */
  public List getTracks() 
  {
    return((List)tracks_);
  }

//----------------------------------------------------------------------
/**
 * Get the list of waypoints that has been read from the source (file,
 * stream...)
 *
 * @return waypoints The list of waypoints read.
 */
  public List getWaypoints() 
  {
    return((List)waypoints_);
  }

//----------------------------------------------------------------------
/**
 * Function for parsing a given file in gpx-format.
 */
  public void parseFile(String filename)
  {
    try
    {
      SAXParserFactory spf = SAXParserFactory.newInstance();
      SAXParser parser = spf.newSAXParser();
    
// // Set the schema language if necessary
//     try {
//         parser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
//     } catch (SAXNotRecognizedException x) {
//         // This can happen if the parser does not support JAXP 1.2
//       x.printStackTrace();
//     }
//     parser.setProperty(JAXP_SCHEMA_SOURCE, new File(schemaSource));

      DefaultHandler handler = new MyHandler();
      parser.parse(filename,handler);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

//----------------------------------------------------------------------
/**
 * Function for parsing a given input stream in gpx-format.
 */
  public void parseInputStream(InputStream in_stream)
  {
    try
    {
      SAXParserFactory spf = SAXParserFactory.newInstance();
      SAXParser parser = spf.newSAXParser();
    
// // Set the schema language if necessary
//     try {
//         parser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
//     } catch (SAXNotRecognizedException x) {
//         // This can happen if the parser does not support JAXP 1.2
//       x.printStackTrace();
//     }
//     parser.setProperty(JAXP_SCHEMA_SOURCE, new File(schemaSource));

      DefaultHandler handler = new MyHandler();
      parser.parse(in_stream,handler);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  public static void main(String[] args)
  {
    ReadGPX readgpx = new ReadGPX();
    readgpx.parseFile(args[0]);
  }

//----------------------------------------------------------------------
/**
 * Handler class for start and end of xml tags and contents of tags and
 * attributes.
 */
  class MyHandler extends DefaultHandler
  {
    protected boolean check_gpx_ = true;
    protected StringBuffer characters_;

    protected int route_number_ = 0;
    protected int route_point_index_ = 0;
    protected WaypointImpl actual_rtept_;
    protected boolean is_route_ = false;
    protected boolean is_route_point_ = false;

    protected int track_number_ = 0;
    protected int track_point_index_ = 0;
    protected TrackpointImpl actual_trkpt_;
    protected boolean is_track_ = false;
    protected boolean is_track_point_ = false;
    protected boolean is_track_segment_ = false;

    protected int waypoint_number_ = 0;
    protected WaypointImpl actual_waypt_;
    protected boolean is_waypoint_ = false;

//----------------------------------------------------------------------
/**
 * Receive notification when a new XML tag starts and handle found tag.
 */
    public void startElement(String uri, String localName, String qName, Attributes attributes)
    {
      //      System.out.println("StartElement: uri="+uri+" localName="+localName+" qName="+qName+" attributes="+attributes);

      // ------ GENERAL ------
      // Check if input-file is a GPX file
      if (check_gpx_ && !qName.equals("gpx"))
      {
        System.err.println("ERROR: Source file is no valid GPX file!\nPlease check the filename and try again.");
        System.exit(1);
      }
      else
        check_gpx_ = false;


      // ------ ROUTES ------
      // Beginning of a new route
      if (qName.equals("rte"))
      {
        is_route_ = true;
        route_point_index_ = 0;
        routes_.addElement(new RouteImpl());
      }

      // Beginning of a new route point
      if (qName.equals("rtept") && is_route_)
      {
        is_route_point_ = true;
        actual_rtept_ = new WaypointImpl();

	// Set identification of actual route point to route_point_index_
	// Identification may be given by an extra tag or even not
        actual_rtept_.setIdentification(String.valueOf(route_point_index_));

	// Set latitude of actual route point
        if (attributes.getValue("lat")!=null)
          actual_rtept_.setLatitude(new Double(attributes.getValue("lat")).doubleValue());
        else
          actual_rtept_.setLatitude(Double.NaN);

	// Set longitude of actual route point
        if (attributes.getValue("lon")!=null)
          actual_rtept_.setLongitude(new Double(attributes.getValue("lon")).doubleValue());
        else
          actual_rtept_.setLongitude(Double.NaN);

	// Set altitude of actual route point to NaN
	// Elevation may be given by an extra tag or even not
        actual_rtept_.setAltitude(Double.NaN);

	// Set symbol name of route point to default (wpt_dot)
        actual_rtept_.setSymbolName("wpt_dot");

	// Debug
        if(Debug.DEBUG)
          Debug.println("readgpx"," Added Rtept: lat-"+actual_rtept_.getLatitude()+", lon-"+actual_rtept_.getLongitude());
      }


      // ------ TRACKS ------
      // Beginning of a new track
      if (qName.equals("trk"))
      {
        is_track_ = true;
        track_point_index_ = 0;
        tracks_.addElement(new TrackImpl());
      }

      // Beginning of a new track segment
      if (qName.equals("trkseg") && is_track_)
        is_track_segment_ = true;

      // Beginning of a new track point
      if (qName.equals("trkpt") && is_track_)
      {
        is_track_point_ = true;
        actual_trkpt_ = new TrackpointImpl();

	// Set identification of actual trackpoint
        if (attributes.getValue("")!=null)
          actual_trkpt_.setIdentification(attributes.getValue(""));
        else
          actual_trkpt_.setIdentification(String.valueOf(track_point_index_));

	// Set latitude of actual track point
        if (attributes.getValue("lat")!=null)
          actual_trkpt_.setLatitude(new Double(attributes.getValue("lat")).doubleValue());
        else
          actual_trkpt_.setLatitude(Double.NaN);

	// Set longitude of actual track point
        if (attributes.getValue("lon")!=null)
          actual_trkpt_.setLongitude(new Double(attributes.getValue("lon")).doubleValue());
        else
          actual_trkpt_.setLongitude(Double.NaN);

	// Set indicator for a new track segment
        if (is_track_segment_)
        {
          actual_trkpt_.setNewTrack(true);
          is_track_segment_ = false;
        }
        else
          actual_trkpt_.setNewTrack(false);

	// Set altitude of actual track point to NaN
	// Elevation may be given by an extra tag or even not
        actual_trkpt_.setAltitude(Double.NaN);

	// Set date of track point to null
	// Date may be given by an extra tag or even not
        actual_trkpt_.setDate(null);

	// Debug
        if(Debug.DEBUG)
          Debug.println("readgpx"," Added Trkpt: lat="+actual_trkpt_.getLatitude()+", lon="+actual_trkpt_.getLongitude());
      }

      // ------ WAYPOINTS ------
      // Beginning of a new waypoint
      if (qName.equals("wpt"))
      {
        is_waypoint_ = true;
        actual_waypt_ = new WaypointImpl();

	// Set identification of actual waypoint to null
	// Identification may be given by an extra tag or even not
        actual_waypt_.setIdentification(null);

	// Set comment of actual waypoint to null
	// Comment may be given by an extra tag or even not
        actual_waypt_.setComment(null);

	// Set latitude of actual waypoint
        if (attributes.getValue("lat")!=null)
          actual_waypt_.setLatitude(new Double(attributes.getValue("lat")).doubleValue());
        else
          actual_waypt_.setLatitude(Double.NaN);

	// Set longitude of actual waypoint
        if (attributes.getValue("lon")!=null)
          actual_waypt_.setLongitude(new Double(attributes.getValue("lon")).doubleValue());
        else
          actual_waypt_.setLongitude(Double.NaN);

	// Set altitude of actual waypoint to NaN
	// Elevation may be given by an extra tag or even not
        actual_waypt_.setAltitude(Double.NaN);

	// Set symbol name of waypoint to default (wpt_dot)
	// Specific symbol name may be given by an extra tag or even not
        actual_waypt_.setSymbolName("wpt_dot");

	// Debug
        if(Debug.DEBUG)
          Debug.println("readgpx","Added Waypt: lat-"+actual_waypt_.getLatitude()+", lon-"+actual_waypt_.getLongitude());
      }
    }

//----------------------------------------------------------------------
/**
 * Receive notification when a XML tag ends and handle it.
 */
    public void endElement(String uri, String localName, String qName)
    {
      //      if(Debug.DEBUG)
      //      Debug.println("readgpx","EndElement: uri="+uri+" localName="+localName+" qName="+qName);

      // ------ GENERAL ------
      // Ending of identification of route (point) / track / waypoint
      if (qName.equals("name") && (is_route_ || is_track_ || is_waypoint_))
      {
        String chars = characters_.toString();

        if (chars.startsWith("![CDATA[") && chars.endsWith("]]"))
          chars = chars.substring(8,chars.length()-2);

        if (is_route_ && !is_route_point_)
        {
          RouteImpl actual_rte = (RouteImpl)(routes_.get(route_number_));
          actual_rte.setIdentification(chars);

	  // Debug
          if(Debug.DEBUG)
            Debug.println("readgpx","Rte-Name: "+actual_rte.getIdentification());
        }
        else if (is_route_ && is_route_point_)
        {
          actual_rtept_.setIdentification(chars);

	  // Debug
          if(Debug.DEBUG)
            Debug.println("readgpx","  Rtept-Name: "+actual_rtept_.getIdentification());
        }
        else if (is_track_)
        {
          TrackImpl actual_trk = (TrackImpl)(tracks_.get(track_number_));
          actual_trk.setIdentification(chars);

	  // Debug
          if(Debug.DEBUG)
            Debug.println("readgpx","Trk-Name: "+actual_trk.getIdentification());
        }
        else if (is_waypoint_)
        {
          actual_waypt_.setIdentification(chars);

	  // Debug
          if(Debug.DEBUG)
            Debug.println("readgpx","Wpt-Name: "+actual_waypt_.getIdentification());
        }
      }

      // Ending of description of route (point) / track / waypoint
      if (qName.equals("desc") && (is_route_ || is_track_ || is_waypoint_))
      {
        String chars = characters_.toString();

        if (chars.startsWith("![CDATA[") && chars.endsWith("]]"))
          chars = chars.substring(8,chars.length()-2);

        if (is_route_ && !is_route_point_)
        {
          RouteImpl actual_rte = (RouteImpl)(routes_.get(route_number_));
          actual_rte.setComment(chars);

	  // Debug
          if(Debug.DEBUG)
            Debug.println("readgpx","Rte-Desc: "+actual_rte.getComment());
        }
        else if (is_route_ && is_route_point_)
        {
          actual_rtept_.setComment(chars);

	  // Debug
          if(Debug.DEBUG)
            Debug.println("readgpx","  Rtept-Desc: "+actual_rtept_.getComment());
        }
        else if (is_track_)
        {
          TrackImpl actual_trk = (TrackImpl)(tracks_.get(track_number_));
          actual_trk.setComment(chars);

	  // Debug
          if(Debug.DEBUG)
            Debug.println("readgpx","Trk-Desc: "+actual_trk.getComment());
        }
        else if (is_waypoint_)
        {
          actual_waypt_.setComment(chars);

	  // Debug
          if(Debug.DEBUG)
            Debug.println("readgpx","Wpt-Desc: "+actual_waypt_.getComment());
        }
      }

      // Ending of elevation of route point / track point / waypoint
      if (qName.equals("ele") && ((is_route_ && is_route_point_) || (is_track_ && is_track_point_) || is_waypoint_))
      {
        if (is_route_)
        {
          actual_rtept_.setAltitude(new Double(characters_.toString()).doubleValue());

	  // Debug
          if(Debug.DEBUG)
            Debug.println("readgpx","  Rtept-Ele: "+actual_rtept_.getAltitude());
        }
        else if (is_track_)
        {
          actual_trkpt_.setAltitude(new Double(characters_.toString()).doubleValue());

	  // Debug
          if(Debug.DEBUG)
            Debug.println("readgpx","  Trkpt-Ele: "+actual_trkpt_.getAltitude());
        }
        else if (is_waypoint_)
        {
          actual_waypt_.setAltitude(new Double(characters_.toString()).doubleValue());

	  // Debug
          if(Debug.DEBUG)
            Debug.println("readgpx"," Waypt-Ele: "+actual_waypt_.getAltitude());
        }
      }

      // Ending of time information of track point
      if (qName.equals("time") && (is_track_ && is_track_point_))
      {
        actual_trkpt_.setDate(date_format_.parse(characters_.toString(),dummy_position_));

	// Debug
        if(Debug.DEBUG)
          Debug.println("readgpx","  Trkpt-Time: "+actual_trkpt_.getDate());
      }

      // Ending of symbol name of waypoint
      if (qName.equals("sym") && is_waypoint_)
      {
        actual_waypt_.setSymbolName(characters_.toString());

	// Debug
        if(Debug.DEBUG)
          Debug.println("readgpx"," Waypt-Sym: "+actual_waypt_.getSymbolName());
      }

      // Remove unhandled tag data information
      characters_ = null;


      // ------ ROUTES ------
      // Ending of a route
      if (qName.equals("rte"))
      {
        is_route_ = false;
        route_number_++;
      }

      // Ending of a route point
      if (qName.equals("rtept"))
      {
        ((RouteImpl)routes_.get(route_number_)).addWaypoint(actual_rtept_);
        is_route_point_ = false;
        route_point_index_++;
      }


      // ------ TRACKS ------
      // Ending of a track
      if (qName.equals("trk"))
      {
        is_track_ = false;
        track_number_++;
      }

      // Ending of a track segment
      if (qName.equals("trkseg"))
        is_track_segment_ = false;

      // Ending of a track point
      if (qName.equals("trkpt"))
      {
        ((TrackImpl)tracks_.get(track_number_)).addWaypoint(actual_trkpt_);
        is_track_point_ = false;
        track_point_index_++;
      }

      // ------ WAYPOINTS ------
      // Ending of a waypoint
      if (qName.equals("wpt"))
      {
        waypoints_.addElement(actual_waypt_);
        is_waypoint_ = false;
        waypoint_number_++;
      }
    }

//----------------------------------------------------------------------
/**
 * Read and collect xml tag informations.
 */
    public void characters(char[] ch, int start, int length)
    {
      String chars = new String(ch,start,length).trim();
//       if (chars.length() > 0 && Debug.DEBUG)
//         Debug.println("readgpx","characters: "+chars);

      // Collect parsed values of tags
      if (chars.length() > 0)
      {
        if (characters_ == null)
          characters_ = new StringBuffer(chars);
        else
          characters_.append(chars);
      }
    }
  }
}
