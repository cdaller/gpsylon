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


package org.dinopolis.gpstool.plugin.graph;

import java.io.File;
import java.util.*;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.dinopolis.gpstool.util.geoscreen.GeoScreenPoint;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

//----------------------------------------------------------------------
/**
 * This class is used to calculate the shortest path between a start-node
 * and a target node
 *
 * @author Wolfgang Auer
 * @version $Revision$
 */


public class GraphFileReader
{
	protected File file_name_;
	protected Graph graph_;
	protected SAXParser sax_parser_;


	public GraphFileReader()
	{

		try
		{
			SAXParserFactory sax_parser_factory = SAXParserFactory.newInstance();
			sax_parser_ = sax_parser_factory.newSAXParser();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public Graph parseFile(File file_name)
	{

		DefaultHandler my_default_handler = new MyHandler();

		try
		{
			sax_parser_.parse(file_name,my_default_handler);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return graph_;
	}

	class MyHandler extends DefaultHandler
	{

		protected Node node_;
		protected Edge edge_;
		protected List edge_points_ = new ArrayList();

		public void startDocument()
		{
			//System.out.println("Start of Document");
			graph_ = new Graph();
		}

		public void endDocument()
		{
			//System.out.println("End of Document");
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes)
		{
    	if(qName == "node")
    	{
    		node_ = new Node();
    		int id = new Integer(attributes.getValue("id")).intValue();
    		float lat = new Float(attributes.getValue("lat")).floatValue();
    		float lon = new Float(attributes.getValue("lon")).floatValue();
    		GeoScreenPoint position = new GeoScreenPoint(lat,lon);
				node_.setID(id);
    		node_.setPosition(position);
    	}

			if(qName == "edge")
    	{
    		edge_ = new Edge();
    		int id = new Integer(attributes.getValue("id")).intValue();
				int source = new Integer(attributes.getValue("source")).intValue();
				int target = new Integer(attributes.getValue("target")).intValue();
			  String name = attributes.getValue("name");
			  boolean oneway;
			  if(attributes.getValue("target") == "true")
			  	oneway = true;
			  else
			  	oneway = false;
			  int type = new Integer(attributes.getValue("type")).intValue();

				edge_.setID(id);
				edge_.setStartNode(source);
				edge_.setEndNode(target);
				edge_.setName(name);
				edge_.setOneway(oneway);
				edge_.setType(type);
			}

			if(qName == "point")
			{
				float lat = new Float(attributes.getValue("lat")).floatValue();
    		float lon = new Float(attributes.getValue("lon")).floatValue();
    		GeoScreenPoint position = new GeoScreenPoint(lat,lon);
				edge_points_.add(position);
			}

		}

		public void endElement(String uri,String localName, String qName)

		{
    	if(qName == "node")
    	{
				graph_.addNodeFromFile(node_);
			}

			if(qName == "edge")
			{
				Iterator edge_point_iterator = edge_points_.iterator();
				List edge_points = new ArrayList();
				while(edge_point_iterator.hasNext())
				{
					GeoScreenPoint point = new GeoScreenPoint();
					point = (GeoScreenPoint)edge_point_iterator.next();
					edge_points.add(point);
				}

				edge_.setEdgePoints(edge_points);
				graph_.addEdge(edge_);
				edge_points_.clear();
			}


		}

	}
}