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

import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;
import java.util.TreeMap;

import org.dinopolis.gpstool.util.geoscreen.GeoScreenPoint;
import java.io.File;
import java.io.FileWriter;

//----------------------------------------------------------------------
/**
 * This class is used to write all data of the graph into an xml file
 *
 * @author Wolfgang Auer
 * @version $Revision$
 */

public class GraphFileWriter
{
	FileWriter file_writer_;
	File file_name_;
	Graph graph_;

	public GraphFileWriter(File file_name,Graph graph)
 	{
		file_name_ = file_name;
		graph_ = graph;
	}

	public void write() throws IOException
	{
		file_writer_ = new FileWriter(file_name_);

		String first_line = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>\n";
		String nl = "\n";
		String tab ="\t";
		String graph_tag_o = "<graph>";
		String graph_tag_c = "</graph>";

		file_writer_.write(first_line + nl + graph_tag_o + nl + tab);

		TreeMap nodes_treemap = graph_.getNodes();
		Collection nodes = nodes_treemap.values();
		Iterator nodes_iterator = nodes.iterator();

    String node_data = "";

		while(nodes_iterator.hasNext())
		{
		 	Node node = (Node) nodes_iterator.next();
			String node_id = new Integer(node.getID()).toString();
			GeoScreenPoint node_pos = node.getPosition();
			String node_lat = new Float(node_pos.getLatitude()).toString();
			String node_lon = new Float(node_pos.getLongitude()).toString();

		 	node_data = node_data + "<node id=\"" + node_id + "\" lat=\"" + node_lat + "\" lon=\"" + node_lon + "\"/>" + nl + tab;
		}

		file_writer_.write(node_data + nl);

		TreeMap edges_treemap = graph_.getEdges();
		Collection edges = edges_treemap.values();
		Iterator edges_iterator = edges.iterator();

		while(edges_iterator.hasNext())
		{
		 	Edge edge = (Edge) edges_iterator.next();
			String edge_id = new Integer(edge.getID()).toString();
			String edge_source = new Integer(edge.getStartNode()).toString();
			String edge_target = new Integer(edge.getEndNode()).toString();
			String edge_name = edge.getName();
			String edge_oneway;
			String edge_tag_c = "</edge>";
			if(edge.getOneway())
				edge_oneway = "true";
			else
				edge_oneway = "false";
			String edge_type = new Integer(edge.getType()).toString();
			String edge_points_str = "";

			List edge_points = edge.getEdgePoints();
			Iterator edge_point_iterator = edge_points.iterator();

			while(edge_point_iterator.hasNext())
			{
			 	GeoScreenPoint point_gsp = (GeoScreenPoint) edge_point_iterator.next();
			 	String point_lat = new Float(point_gsp.getLatitude()).toString();
			 	String point_lon = new Float(point_gsp.getLongitude()).toString();

				String point = tab + tab + "<point lat=\"" + point_lat + "\" lon=\"" + point_lon + "\"/>" + nl;
				edge_points_str = edge_points_str + point;
			}

		 	String edge_data = "<edge id=\"" + edge_id + "\" source=\"" + edge_source + "\" target=\"" + edge_target + "\" name=\"" + edge_name + "\" oneway=\"" + edge_oneway + "\" type=\"" + edge_type +"\">";
			edge_data = tab + edge_data + nl + edge_points_str + tab + edge_tag_c + nl;
			file_writer_.write(edge_data);

		}

		file_writer_.write(graph_tag_c);
		file_writer_.close();
	}
 }