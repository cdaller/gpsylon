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


import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import org.dinopolis.gpstool.TrackManager;
import org.dinopolis.gpstool.gui.util.BasicLayer;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.track.*;
import org.dinopolis.gpstool.util.GeoMath;
import org.dinopolis.gpstool.util.geoscreen.GeoScreenPoint;
import org.dinopolis.util.Resources;

import com.bbn.openmap.util.quadtree.QuadTree;


//----------------------------------------------------------------------
/**
 * This class is responsible to draw all parts of a graph on the screen.
 *
 * @author Wolfgang Auer
 * @version $Revision$
 */

public class GraphLayer extends BasicLayer
  implements ActionListener, FocusListener
{

  GeoScreenPoint mouse_drag_start_;
  GeoScreenPoint mouse_drag_end_;
  Trackpoint mouse_current_;
  Trackpoint cross_point_;
  Resources resources_;
  Resources application_resources_;
  TrackManager track_manager_;
  GraphPlugin plugin_;
  List positions_of_new_nodes_ = new ArrayList();
  List new_edges_ = new ArrayList();
  List cross_points_ = new ArrayList();
	List shortest_path_ = new ArrayList();
	Graph graph_;
  Edge current_edge_;
  Node nearest_node_;
  Node shortest_path_start_node_;
  Node shortest_path_end_node_;
  List visible_tracks_ = new ArrayList();
	List selected_edges_ = new ArrayList();
	List selected_nodes_ = new ArrayList();

	int count_ = 0;
	EdgesPropertyWindow window_;

  int nearest_point_index_;

	// keys for resources:
	public static final String KEY_GRAPH_NODE_COLOR =
	"graph.node.color";
	public static final String KEY_GRAPH_NODE_STROKE =
	"graph.node.stroke";
	public static final String KEY_GRAPH_EDGE_COLOR =
	"graph.edge.color";
	public static final String KEY_GRAPH_EDGE_STROKE =
	"graph.edge.stroke";
	public static final String KEY_GRAPH_SELECTED_EDGE_NODE_COLOR =
	"graph.selected_edge_node.color";
	public static final String KEY_GRAPH_SHORTESTPATH_COLOR =
	"graph.shortestpath.color";


//----------------------------------------------------------------------
/**
 * Default Constructor
 */
  public GraphLayer()
  {
  }

//----------------------------------------------------------------------
/**
 * Initialize with all it needs.
 *
 * @param support a plugin support object
 * @param plugin the plugin itself
 */
  public void initializePlugin(PluginSupport support, GraphPlugin plugin)
  {
    track_manager_ = support.getTrackManager();
    application_resources_ = support.getResources();
    plugin_ = plugin;
    resources_ = plugin_.getResources();
    graph_ = plugin_.getGraph();
  }

//----------------------------------------------------------------------


//----------------------------------------------------------------------
// BasicLayer methods
// ----------------------------------------------------------------------

//----------------------------------------------------------------------
/**
 * This method is called from a background thread to recalulate the
 * screen coordinates of any geographical objects. This method must
 * store its objects and paint them in the paintComponent() method.
 */

  protected void doCalculation()
  {

    repaint();
  }


/**
 * Paint layer objects.
 */
  public void paintComponent(Graphics g)
  {
    graph_ = plugin_.getGraph();
		Graphics2D g2 = (Graphics2D) g;
    //g2.setColor(resources_.getColor(KEY_GRAPH_NODE_COLOR));
		g2.setStroke(new BasicStroke(resources_.getInt(KEY_GRAPH_NODE_STROKE)));
    //System.out.println("Tracks size: "+visible_tracks_.size());
    if(!isActive())
    	return;


		//paint tracks !!!

		/*
   	Iterator track_iterator = visible_tracks_.iterator();
		//System.out.println("Tracks size: "+visible_tracks_.size());
		while(track_iterator.hasNext())
		{

			Track visible_track = (Track) track_iterator.next();
			List trackpoints = visible_track.getWaypoints();
			Trackpoint trackpoint;
			int start_x,start_y,end_x,end_y;
		  if(!trackpoints.isEmpty())
		  {
				Iterator point_iterator = trackpoints.iterator();
		  	trackpoint = (Trackpoint)point_iterator.next();
				trackpoint.forward(getProjection());

		  	start_x = trackpoint.getX();
	 	    start_y = trackpoint.getY();
        g2.setStroke(new BasicStroke(resources_.getInt(KEY_GRAPH_NODE_STROKE)));
        g2.drawRect(start_x-1,start_y-1,3,3);


	 	    while(point_iterator.hasNext())
	  	  {

					trackpoint = (Trackpoint)point_iterator.next();
          trackpoint.forward(getProjection());
					end_x = trackpoint.getX();
	        end_y = trackpoint.getY();

	        // only draw line, if the coordinates are different:
	        if((Math.abs(end_x - start_x) > 0.1)
	          	 || (Math.abs(end_y - start_y) > 0.1))
		      {
	        	// finally draw the line:
	          g2.setStroke(new BasicStroke(1));
						g2.drawLine(start_x,start_y,end_x,end_y);
					}

					start_x = end_x;
					start_y = end_y;
					g2.setStroke(new BasicStroke(resources_.getInt(KEY_GRAPH_NODE_STROKE)));
					g2.drawRect(start_x-1,start_y-1,3,3);
				}
			}
			}*/

		if(graph_ != null)
		{
			// paint the new nodes (nodes which are not in the graph jet)

			if(!positions_of_new_nodes_.isEmpty())
			{
				 g2.setColor(resources_.getColor(KEY_GRAPH_SELECTED_EDGE_NODE_COLOR));
				 Iterator positions_of_new_nodes_iterator = positions_of_new_nodes_.iterator();

				 while(positions_of_new_nodes_iterator.hasNext())
				 {
				 	 GeoScreenPoint new_node = (GeoScreenPoint) positions_of_new_nodes_iterator.next();
					 new_node.forward(getProjection());
					 g2.drawOval(new_node.getX()-3,new_node.getY()-3,6,6);
				 }
			}

		  /*

		  //paint all crosspoints

			if(!cross_points_.isEmpty())
			{
				 g2.setColor(Color.red);
				 Iterator cross_points_iterator = cross_points_.iterator();

				 while(cross_points_iterator.hasNext())
				 {
				 	 GeoScreenPoint new_node = (GeoScreenPoint) cross_points_iterator.next();
					 new_node.forward(getProjection());
					 g2.drawOval(new_node.getX()-3,new_node.getY()-3,6,6);
				 }
			}
      */



			// paint all nodes:

      TreeMap nodes_treemap = graph_.getNodes();
			Collection nodes = nodes_treemap.values();
			Iterator nodes_iterator = nodes.iterator();
			int current_start_node_id = -1;
			int current_end_node_id = -1;

     	if(current_edge_ != null)
     	{
     		current_start_node_id = current_edge_.getStartNode();
     		current_end_node_id = current_edge_.getEndNode();
     	}

			while(nodes_iterator.hasNext())
			{
				Node node = (Node)nodes_iterator.next();
				GeoScreenPoint node_position = node.getPosition();

				node_position.forward(getProjection());

				// set the color for the current edge-nodes
				if((node.getID() == current_start_node_id || node.getID() == current_end_node_id) && plugin_.isSetEdgesActive())
					g2.setColor(resources_.getColor(KEY_GRAPH_SELECTED_EDGE_NODE_COLOR));
				// set the color for the start-node of the shortest-path calculation
				else if(shortest_path_start_node_ != null && node.getID() == shortest_path_start_node_.getID())
					g2.setColor(resources_.getColor(KEY_GRAPH_SHORTESTPATH_COLOR));
				// set the color for the end-node of the shortest-path calculation
				else if(shortest_path_end_node_ != null && node.getID() == shortest_path_end_node_.getID())
					g2.setColor(resources_.getColor(KEY_GRAPH_SHORTESTPATH_COLOR));
				// set the color for a normal node in the graph
				else
					g2.setColor(resources_.getColor(KEY_GRAPH_NODE_COLOR));

				boolean foung = false;
				Iterator selected_node_iterator = selected_nodes_.iterator();

				while(selected_node_iterator.hasNext())
				{
					Integer selected_node_id = (Integer) selected_node_iterator.next();
					if(node.getID() == selected_node_id.intValue())
					{
						g2.setColor(resources_.getColor(KEY_GRAPH_SELECTED_EDGE_NODE_COLOR));
					}
				}

				g2.drawOval(node_position.getX()-3,node_position.getY()-3,6,6);

			}


	    g2.setColor(resources_.getColor(KEY_GRAPH_EDGE_COLOR));
			g2.setStroke(new BasicStroke(resources_.getInt(KEY_GRAPH_EDGE_STROKE)));

			// paint the edges of the graph

			boolean found = false;
			TreeMap edges_treemap = graph_.getEdges();
			Collection edges = edges_treemap.values();
			Iterator edges_iterator = edges.iterator();

			while(edges_iterator.hasNext())
			{
				Edge edge = (Edge)edges_iterator.next();
        found = false;

				// set the color of the edges for the shortest path
				if(!shortest_path_.isEmpty())
				{
					Iterator shortest_path_iterator = shortest_path_.iterator();

					while(shortest_path_iterator.hasNext())
					{
						Edge shortest_path_edge = (Edge) shortest_path_iterator.next();

						if(shortest_path_edge.getID() == edge.getID())
						{
							g2.setColor(resources_.getColor(KEY_GRAPH_SHORTESTPATH_COLOR));
					  	found = true;
						}
						if(!found)
							g2.setColor(resources_.getColor(KEY_GRAPH_EDGE_COLOR));
					}
				}

				found = false;

				// set the color for the marked edges
				if(!selected_edges_.isEmpty())
				{
					Iterator selected_edge_iterator = selected_edges_.iterator();

					while(selected_edge_iterator.hasNext())
					{
						Edge selected_edge = (Edge) selected_edge_iterator.next();

						if(selected_edge.getID() == edge.getID())
						{
							g2.setColor(resources_.getColor(KEY_GRAPH_SELECTED_EDGE_NODE_COLOR));
					  	found = true;
						}
						if(!found && g2.getColor() != Color.green)
							g2.setColor(resources_.getColor(KEY_GRAPH_EDGE_COLOR));
					}
				}


				List edge_points = edge.getEdgePoints();
				if(edge_points != null)
				{
					Iterator point_iterator = edge_points.iterator();
					GeoScreenPoint help = null;
					while(point_iterator.hasNext())
					{
						GeoScreenPoint point = (GeoScreenPoint) point_iterator.next();
						point.forward(getProjection());
						if(help != null)
				  	{
				  		g2.drawLine(help.getX(),help.getY(),point.getX(),point.getY());
				  	}
						help = point;
					}
				}
			}


			// paint the new edges

			g2.setColor(resources_.getColor(KEY_GRAPH_SELECTED_EDGE_NODE_COLOR));

			if(!new_edges_.isEmpty())
			{
				Iterator edge_iterator = new_edges_.iterator();
				GeoScreenPoint first_point = new GeoScreenPoint();

				while(edge_iterator.hasNext())
				{
					Edge current_edge = (Edge) edge_iterator.next();

					List current_edge_points = current_edge.getEdgePoints();
					if(current_edge_points != null)
					{
						Iterator point_iterator = current_edge_points.iterator();

						if(point_iterator.hasNext())
						{
							first_point = (GeoScreenPoint) point_iterator.next();
							first_point.forward(getProjection());
						}

						while(point_iterator.hasNext())
						{
							GeoScreenPoint second_point = (GeoScreenPoint) point_iterator.next();
							second_point.forward(getProjection());
							g2.drawLine(first_point.getX(),first_point.getY(),second_point.getX(),second_point.getY());
					  	first_point = second_point;
						}
					}
			  }
			}
  	}
  }


//----------------------------------------------------------------------
/**
 * Focus Listener Method
 *
 * @param event the action event
 */

  public void focusGained(FocusEvent event)
  {
  }


//----------------------------------------------------------------------
/**
 * Focus Listener Method for focus lost. This method checks the
 * validity of the latitude/longitude input fields for valid inputs
 *
 * @param event the action event
 */

  public void focusLost(FocusEvent event)
  {
  }
//----------------------------------------------------------------------
/**
 * Sets the drag point of the mouse.
 *
 * @param start the start of the mouse drag.
 */
  protected void setMouseDragStart(Point start)
  {
    if(start == null)
    {
      mouse_drag_start_ = null;
      return;
    }
		mouse_drag_start_ = new GeoScreenPoint(start);
    mouse_drag_start_.inverse(getProjection());

  }

//----------------------------------------------------------------------
/**
 * Sets the drag points of the mouse.
 *
 * @param end the start of the mouse drag. If end is null, it is
 * assumed that no drag, but only a click was performed.
 */
  protected void setMouseDragEnd(Point end)
  {
    if(end == null)
    {
      mouse_drag_end_ = null;
      return;
    }
    mouse_drag_end_ = new GeoScreenPoint(end);
    mouse_drag_end_.inverse(getProjection());

  }

//----------------------------------------------------------------------
/**
 * Sets the current point of the mouse.
 *
 * @param current the current point of the mouse.
 */
  protected void setMouseCurrent(Point current)
  {
    mouse_current_ = new TrackpointImpl();
    mouse_current_.setX((int)current.getX());
    mouse_current_.setY((int)current.getY());
    mouse_current_.inverse(getProjection());

  }


//----------------------------------------------------------------------
/**
 * Returns the drag start point of the mouse.
 *
 * @return return_start the start of the mouse drag.
 */
  protected Trackpoint getMouseDragStart()
  {
		Trackpoint return_start = new TrackpointImpl();
		return_start.setX(mouse_drag_start_.getX());
		return_start.setY(mouse_drag_start_.getY());
		return_start.setLatitude(mouse_drag_start_.getLatitude());
		return_start.setLongitude(mouse_drag_start_.getLongitude());

		return(return_start);
  }

//----------------------------------------------------------------------
/**
 * Returns the drag end point of the mouse.
 *
 * @return return_end the end of the mouse drag.
 */
  protected Trackpoint getMouseDragEnd()
  {
		Trackpoint return_end = new TrackpointImpl();
		return_end.setX(mouse_drag_end_.getX());
		return_end.setY(mouse_drag_end_.getY());
		return_end.setLatitude(mouse_drag_end_.getLatitude());
		return_end.setLongitude(mouse_drag_end_.getLongitude());

		return(return_end);
  }


//----------------------------------------------------------------------
/**
 * Calculates the nearest trackpoint and add's it to the new_node list
 *
 * @param current_point is the current mousepoint
 */

	public void	makeNearestPointToNewNode(Point current_point)
	{
		Iterator track_iterator = visible_tracks_.iterator();
		Trackpoint nearest_point = new TrackpointImpl();

		boolean first_point = true;
		double d, help_distance = 0;

		//calculate the nearest point to the current mouse-point

		while(track_iterator.hasNext())
		{
			Track track = (Track)track_iterator.next();
			List track_points = track.getWaypoints();
			Iterator point_iterator = track_points.iterator();

			while(point_iterator.hasNext())
			{
				Trackpoint trackpoint = (Trackpoint) point_iterator.next();
				trackpoint.forward(getProjection());

				d = Math.sqrt(Math.pow((trackpoint.getX() - current_point.getX()),2) + 	Math.pow((trackpoint.getY() - current_point.getY()),2));
				if(first_point)
				{
			  	help_distance = d;
					first_point = false;
					nearest_point = new TrackpointImpl(trackpoint);
				}
				if(d < help_distance)
				{
					help_distance = d;
					nearest_point = new TrackpointImpl(trackpoint);
				}
			}
		}

		GeoScreenPoint new_nearest_point = new GeoScreenPoint();
		new_nearest_point.setLatitude((float)nearest_point.getLatitude());
		new_nearest_point.setLongitude((float)nearest_point.getLongitude());

		graph_ = plugin_.getGraph();

  	new_nearest_point.forward(getProjection());


		// check if nearest new node is already in the new nodes list
		boolean new_point_ok = true;

		Iterator positions_of_new_nodes_iterator = positions_of_new_nodes_.iterator();

		while(positions_of_new_nodes_iterator.hasNext())
		{
			GeoScreenPoint node = (GeoScreenPoint) positions_of_new_nodes_iterator.next();
			if(node.equals(new_nearest_point))
				new_point_ok = false;
		}

		// add new node to the new node list
		if(checkPointIsNode(new_nearest_point) < 0 && new_point_ok)
		{
			positions_of_new_nodes_.add(new_nearest_point);
		}
		repaint();
	}

//----------------------------------------------------------------------
/**
 * Calculates the nearest node in the graph and set's the nodes for the edge
 *
 * @param current_point is the current mousepoint
 */

	public void	setEdge(Point point)
	{
  	TreeMap nodes_tmap = graph_.getNodes();
  	Collection nodes = nodes_tmap.values();
  	Iterator nodes_iterator = nodes.iterator();
  	QuadTree nodes_quadtree = new QuadTree();
		int node_id = -1;

		// calculate the nearest node

		while(nodes_iterator.hasNext())
  	{
  		Node node = (Node)nodes_iterator.next();
  	  GeoScreenPoint node_pos = node.getPosition();
			nodes_quadtree.put(node_pos.getLatitude(),node_pos.getLongitude(),node);
  	}

		GeoScreenPoint current_point = new GeoScreenPoint(point);
		current_point.inverse(getProjection());

		nearest_node_ = new Node();
		nearest_node_ = (Node)nodes_quadtree.get(current_point.getLatitude(),current_point.getLongitude());

		//System.out.println("Node ID: " + nearest_node_.getID());

		// set the nodes of the current edge

		if(nearest_node_ != null)
		{
			if(current_edge_ == null)
			{
				current_edge_ = new Edge();
			  node_id = nearest_node_.getID();
				current_edge_.setStartNode(node_id);
			}
			else
			{
				node_id = nearest_node_.getID();
				current_edge_.setEndNode(node_id);

				if(!edgeExists(current_edge_.getStartNode(),current_edge_.getEndNode()))
				{
					if(!checkNodesOnSameTrack())
					{
						plugin_.showNodesNotOnSameTrackMessage();
						current_edge_ = null;
					}
					else
					{
						if(current_edge_ != null)
							current_edge_.setStartNode(current_edge_.getEndNode());
					}
				}
				if(current_edge_ != null)
					current_edge_.setStartNode(current_edge_.getEndNode());
			}
		}
 	 	//System.out.println("start: " + current_edge_startnode_id_);
	 	// System.out.println("end: " + current_edge_endnode_id_);
		repaint();
	}

//----------------------------------------------------------------------
/**
 * Check if the <Code>Edge</Code> already exists
 *
 * @param the id of the start node
 * @param the id of the end node
 * @return <Code>true</Code> if edge exists else <Code>false</Code>
 */

	public boolean	edgeExists(int start_node_id,int end_node_id)
	{
		Iterator edges_iterator = new_edges_.iterator();

		if(start_node_id == end_node_id)
			return true;

		while(edges_iterator.hasNext())
		{
			Edge edge = (Edge)edges_iterator.next();

			if(edge.getStartNode() == start_node_id && edge.getEndNode() == end_node_id)
				return true;
			if(edge.getEndNode() == start_node_id && edge.getStartNode() == end_node_id)
				return true;
		}

		if(graph_.getEdge(start_node_id,end_node_id) != null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

//----------------------------------------------------------------------
/**
 * Check if the two <Code>Edge</Code> <Code>Node</Code>'s are on the same track
 *
 * @return <Code>true</Code> if Nodes are on the same track else <Code>false</Code>
 */

	public boolean checkNodesOnSameTrack()
	{
		int start_point_index_1 = -1;
		int start_point_index_2 = -1;
		int end_point_index_1 = -1;
		int end_point_index_2 = -1;

		Iterator track_iterator = visible_tracks_.iterator();

		GeoScreenPoint start_node_pos = graph_.getNode(current_edge_.getStartNode()).getPosition();
		GeoScreenPoint end_node_pos = graph_.getNode(current_edge_.getEndNode()).getPosition();


		//System.out.println("Start Node ID: " + current_edge_.getStartNode());
		//System.out.println("End Node ID: " + current_edge_.getEndNode());

		String startnode_track_id_1 = null;
		String startnode_track_id_2 = null;
		String endnode_track_id_1 = null;
		String endnode_track_id_2 = null;

		boolean start_first = true;
		boolean end_first = true;

		//System.out.println("Track size: " + visible_tracks_.size());

		// check if the nodes are on the same track
		while(track_iterator.hasNext())
		{
			Track track = (Track)track_iterator.next();

			List track_points = track.getWaypoints();
			int size = track_points.size();

   		for(int i = 0; i < size; i++)
			{
				GeoScreenPoint geopoint = new GeoScreenPoint();
				Trackpoint trackpoint = (Trackpoint)track_points.get(i);

        // find trackpoint on two tracks (first is my own, second is the second track):
        
// cdaller start:        
// 				if(trackpoint.getLatitude() == start_node_pos.getLatitude()
//            && trackpoint.getLongitude() == start_node_pos.getLongitude())
				if(GeoMath.approximatelyEqual(trackpoint.getLatitude(),start_node_pos.getLatitude())
           && GeoMath.approximatelyEqual(trackpoint.getLongitude(),start_node_pos.getLongitude()))
// cdaller end:          
				{
    			if(start_first)
    			{
						//System.out.println("1.1");
						startnode_track_id_1 = track.getIdentification();
    				start_point_index_1 = i;
					 	//System.out.println("Index start:" + start_point_index_1);
						start_first = false;
					}
					else
					{
						//System.out.println("1.2");
						startnode_track_id_2 = track.getIdentification();
    				start_point_index_2 = i;
					 	//System.out.println("Index start:" + start_point_index_2);
					}
				}
// cdaller start:
// 				if(trackpoint.getLatitude() == end_node_pos.getLatitude()
//            && trackpoint.getLongitude() == end_node_pos.getLongitude())
				if(GeoMath.approximatelyEqual(trackpoint.getLatitude(),end_node_pos.getLatitude())
           && GeoMath.approximatelyEqual(trackpoint.getLongitude(),end_node_pos.getLongitude()))
// cdaller end:          
			  {
			  	if(end_first)
			  	{
						//System.out.println("2.1");
						endnode_track_id_1 = track.getIdentification();
  			  	end_point_index_1 = i;
					 	//System.out.println("Index start:" + end_point_index_1);
  			  	end_first = false;
					}
					else
			  	{
						//System.out.println("2.2");
						endnode_track_id_2 = track.getIdentification();
  			  	end_point_index_2 = i;
					 	//System.out.println("Index start:" + end_point_index_2);
  			 	}
				}
			}
		}

		boolean found = false;

		if(startnode_track_id_1 != null && endnode_track_id_1 != null
       && startnode_track_id_1.equals(endnode_track_id_1))
		{
			if(!checkOnNodeBetweenPoints(start_point_index_1,end_point_index_1,endnode_track_id_1))
				//calculate all points between the two edge-nodes
				calcEdgePoints(start_point_index_1,end_point_index_1,endnode_track_id_1);
			found = true;
	  }

		if(startnode_track_id_1 != null && endnode_track_id_2 != null
       && startnode_track_id_1.equals(endnode_track_id_2))
		{
			if(!checkOnNodeBetweenPoints(start_point_index_1,end_point_index_2,endnode_track_id_2))
				//calculate all points between the two edge-nodes
				calcEdgePoints(start_point_index_1,end_point_index_2,endnode_track_id_2);
			found = true;
	  }

	  if(startnode_track_id_2 != null && endnode_track_id_1 != null
       && startnode_track_id_2.equals(endnode_track_id_1))
		{
			if(!checkOnNodeBetweenPoints(start_point_index_2,end_point_index_1,endnode_track_id_1))
				//calculate all points between the two edge-nodes
				calcEdgePoints(start_point_index_2,end_point_index_1,endnode_track_id_1);
			found = true;
	  }

		if(startnode_track_id_2 != null && endnode_track_id_2 != null
       && startnode_track_id_2.equals(endnode_track_id_2))
		{
			if(!checkOnNodeBetweenPoints(start_point_index_2,end_point_index_2,endnode_track_id_2))
				//calculate all points between the two edge-nodes
				calcEdgePoints(start_point_index_2,end_point_index_2,endnode_track_id_2);
			found = true;
	  }

	  if(found)
	  	return true;
		else
			return false;

	}

//----------------------------------------------------------------------
/**
 * Check if between the start and end-node of the edge is already a node
 *
 * @param start_point_index, is the index of the start point on the track
 * @param end_point_index, is the index of the end point on the track
 * @param track_id, is identification of the track on which the two points are
 * @return <Code>true</Code> if a node is between the start and end-node of the
 * 					edge, else <Code>false</Code>
 */

	public boolean	checkOnNodeBetweenPoints(int start_point_index,int end_point_index,String track_id)
	{
  	List current_edge_points = new ArrayList();
  	Iterator track_iterator = visible_tracks_.iterator();
  	Track track = null;
		while(track_iterator.hasNext())
  	{
  	  track = (Track) track_iterator.next();
			if(track_id == track.getIdentification())
				break;
  	}

  	List trackpoints = track.getWaypoints();
  	boolean between_points = false;

  	// go through all points an check if they are nodes
		for(int i = 0; i < trackpoints.size(); i++)
  	{

			Trackpoint trackpoint = (Trackpoint)trackpoints.get(i);

			if(between_points && start_point_index != i && end_point_index != i)
  		{
  			if(0<=checkPointIsNode(changeTrackpointToGeoScreenPoint(trackpoint)))
					return true;
   		}

			if(start_point_index == i)
			{
				if(!between_points)
					between_points = true;
				else
					between_points = false;
			}

			if(end_point_index == i)
			{
				if(!between_points)
					between_points = true;
				else
					between_points = false;
			}

		}
		return false;
  }


//----------------------------------------------------------------------
/**
 * Check if the choosen point is already a <Code>Node</Code>
 *
 * @return <Code>true</Code> if Point is Node else <Code>false</Code>
 */

	public int	checkPointIsNode(GeoScreenPoint current_point)
	{

		double current_lat = current_point.getLatitude();
	 	double current_long = current_point.getLongitude();

		TreeMap nodes_treemap = graph_.getNodes();
		Collection nodes = nodes_treemap.values();
		Iterator nodes_iterator = nodes.iterator();

		while(nodes_iterator.hasNext())
		{
				Node node = (Node)nodes_iterator.next();
// cdaller start:
// 				double node_lat = (node.getPosition()).getLatitude();
// 				double node_long = (node.getPosition()).getLongitude();
// 				if((node_lat == current_lat) && (node_long == current_long))
// cdaller end:
        if(node.getPosition().getGeoLocation().equals(current_point.getGeoLocation()))
					return node.getID();

		}
		return -1;
	}


//----------------------------------------------------------------------
/**
 * Adds the new nodes to the graph
 */
	public void addNewNodesToGraph()
	{

		 Iterator positions_of_new_nodes_iterator = positions_of_new_nodes_.iterator();

		 while(positions_of_new_nodes_iterator.hasNext())
		 {
		 	 GeoScreenPoint new_node = (GeoScreenPoint) positions_of_new_nodes_iterator.next();
			 graph_.addNode(new_node);
		 }
		 positions_of_new_nodes_.clear();
	}


//----------------------------------------------------------------------
/**
 * Adds the new edges to the graph
 */
	public void addNewEdgesToGraph()
	{

		 Iterator new_edges_iterator = new_edges_.iterator();

		 while(new_edges_iterator.hasNext())
		 {
		 	 Edge new_edge = (Edge) new_edges_iterator.next();
			 graph_.addEdge(new_edge);
		 }
		 new_edges_.clear();
	}

//----------------------------------------------------------------------
/**
 * Calculates the crosspoints between all tracks
 */
	public void calcCrossPoints()
	{
		 int track_size = visible_tracks_.size();
		 int i,ii,iii,iiii;
		 count_++;
		 //System.out.println("COUNT: " +count_);
		 for(i = 0; i < track_size; i++)
		 {
		 	Track track = (Track) visible_tracks_.get(i);
		 	List trackpoints = track.getWaypoints();
			int trackpoint_size = trackpoints.size();

			for(ii = 0; ii < trackpoint_size; ii++)
			{
				Trackpoint trackpoint_a = (Trackpoint) trackpoints.get(ii);
				Trackpoint trackpoint_b;
				if(ii+1 < trackpoint_size)
					trackpoint_b = (Trackpoint)trackpoints.get(ii+1);
				else
					 continue;
				double A_x,A_y,B_x,B_y,AB_x,AB_y;
				//trackpoint_a.forward(getProjection());
				//trackpoint_b.forward(getProjection());
				A_x = trackpoint_a.getLatitude();
				A_y = trackpoint_a.getLongitude();
				B_x = trackpoint_b.getLatitude();
				B_y = trackpoint_b.getLongitude();

				AB_x = B_x - A_x;
				AB_y = B_y - A_y;

				for(iii = 0; iii < track_size; iii++)
        {
				 	Track track2 = (Track) visible_tracks_.get(iii);
				 	List trackpoints2 = track2.getWaypoints();
					int trackpoint_size2 = trackpoints2.size();

					for(iiii = 0; iiii < trackpoint_size2; iiii++)
					{
						Trackpoint trackpoint_2a = (Trackpoint) trackpoints2.get(iiii);
						Trackpoint trackpoint_2b;
						if(iiii+1 < trackpoint_size2)
							trackpoint_2b = (Trackpoint)trackpoints2.get(iiii+1);
            else
            	continue;
						double A2_x,A2_y,B2_x,B2_y,AB2_x,AB2_y;
						//trackpoint_2a.forward(getProjection());
						//trackpoint_2b.forward(getProjection());
						A2_x = trackpoint_2a.getLatitude();
						A2_y = trackpoint_2a.getLongitude();
						B2_x = trackpoint_2b.getLatitude();
						B2_y = trackpoint_2b.getLongitude();

						AB2_x = B2_x - A2_x;
						AB2_y = B2_y - A2_y;

						//System.out.println("AB2_x: " + AB2_x + " AB2_y: " + AB2_y + " A2_x: " + A2_x + " A2_y: " + A2_y + " B2_x: " + B2_x + " B2_y: " + B2_y);
						//System.out.println("AB_x: " + AB_x + " AB_y: " + AB_y + " A_x: " + A_x + " A_y: " + A_y + " B_x: " + B_x + " B_y: " + B_y);

		      	// calculate the factor with witch the crosspoint can be calculated
    				double s = (double)(AB_y*(A_x - A2_x) + AB_x*(A2_y - A_y)) / (AB2_x * AB_y - AB2_y * AB_x);

    				// calculate the crosspoint
						double cross_x = A2_x + s * AB2_x;
						double cross_y = A2_y + s * AB2_y;

						if((((A_x < B_x) && (cross_x >= A_x) && (cross_x <= B_x)) ||
						    ((A_x > B_x) && (cross_x <= A_x) && (cross_x >= B_x)) ||
								((A_y < B_y) && (cross_y >= A_y) && (cross_y <= B_y)) ||
								((A_y > B_y) && (cross_y <= A_y) && (cross_y >= B_y))) &&
						   (((A2_x < B2_x) && (cross_x >= A2_x) && (cross_x <= B2_x)) ||
							  ((A2_x > B2_x) && (cross_x <= A2_x) && (cross_x >= B2_x)) ||
								((A2_y < B2_y) && (cross_y >= A2_y) && (cross_y <= B2_y)) ||
								((A2_y > B2_y) && (cross_y <= A2_y) && (cross_y >= B2_y))))
						{
							GeoScreenPoint cross_point = new GeoScreenPoint();
							//int round_x = (int)Math.round(cross_x);
							//int round_y = (int)Math.round(cross_y);
							cross_point.setLatitude((float)cross_x);
							cross_point.setLongitude((float)cross_y);
							//cross_point.inverse(getProjection());

							boolean equal = false;
							if(A_x == A2_x && A_y == A2_y)
								equal = true;
							else
								equal = false;

              if(A_x == B2_x && A_y == B2_y)
								equal = true || equal;
							else
								equal = false || equal;

							if(B_x == A2_x && B_y == A2_y)
								equal = true || equal;
							else
								equal = false || equal;

							if(B_x == B2_x && B_y == B2_y)
								equal = true || equal;
							else
								equal = false || equal;

						 //System.out.println("a: "+A_x+" "+A_y + " b: "+ B_x+" "+B_y +" 2a: "+ A2_x+" "+A2_y+" 2b: "+B2_x+" "+B2_y+"EQUAL: "+equal);

							if(!checkCrossPointInList(cross_point) && !equal)
							{
								//System.out.println("COUNT in IF: " +count_);
								Trackpoint track_cross_point = new TrackpointImpl();
								track_cross_point.setLatitude(cross_point.getLatitude());
								track_cross_point.setLongitude(cross_point.getLongitude());
								//track_cross_point.inverse(getProjection());
								//System.out.println("lat: "+track_cross_point.getLatitude() + " lon: " + track_cross_point.getLongitude());
								visible_tracks_.remove(i);
								if(i != iii)
									visible_tracks_.remove(iii-1);
								//System.out.println("Visible track size after remove: "+visible_tracks_.size());
								trackpoints.add(ii+1,track_cross_point);
								if(i != iii)
									trackpoints2.add(iiii+1,track_cross_point);
								else
								{
									if(ii<iiii)
										trackpoints.add(iiii+2,track_cross_point);
									else
										trackpoints.add(iiii+1,track_cross_point);
								}
								track.setWaypoints(trackpoints);
								if(i != iii)
									track2.setWaypoints(trackpoints2);

								visible_tracks_.add(track);
								if(i != iii)
									visible_tracks_.add(track2);
								//System.out.println("Visible track size after add: "+visible_tracks_.size());
								cross_points_.add(cross_point);
 		            //cross_points_track_id1_.add(track.getIdentification());
								//cross_points_track_id2_.add(track2.getIdentification());
								//cross_points_trackpoint_id_before1_.add(new Integer(ii));
								//cross_points_trackpoint_id_before2_.add(new Integer(iiii));
								//System.out.println(trackpoints.size());
								calcCrossPoints();
								return;
							}
						}
					}
        }
		  }
  	}
  }

//----------------------------------------------------------------------
/**
 * Checks if the crosspoint is already in the crosspoint-list
 * @param cross point to check
 */
	public boolean checkCrossPointInList(GeoScreenPoint cross_point)
	{
	 	Iterator cross_point_iterator = cross_points_.iterator();

		while(cross_point_iterator.hasNext())
	 	{

			GeoScreenPoint point = (GeoScreenPoint) cross_point_iterator.next();
	 	 	//System.out.println("Aus Liste: "+point);
	 	 	//System.out.println("Berechnet: "+cross_point);
// cdaller start:      
// 			if(point.getLatitude() == cross_point.getLatitude()
//          && point.getLongitude() == cross_point.getLongitude() )
			if(point.getGeoLocation().equals(cross_point.getGeoLocation()))
// cdaller end:
        return true;
	 	}
	 	return false;
	}
//----------------------------------------------------------------------
/**
 * Calculates the points of the edge between the start and end-node
 * @param index of the start point
 * @param index of the end point
 * @param identification of the track
 */
	public void calcEdgePoints(int index_startpoint, int index_endpoint,String track_id)
	{
  	List current_edge_points = new ArrayList();
  	Iterator track_iterator = visible_tracks_.iterator();
  	Track track = null;
		while(track_iterator.hasNext())
  	{
  	  track = (Track) track_iterator.next();
			if(track_id == track.getIdentification())
				break;
  	}

		List trackpoints = track.getWaypoints();
		boolean add_points = false;
		int size = trackpoints.size();



		for(int i=0; i < size; i++)
		{
			Trackpoint trackpoint = (Trackpoint) trackpoints.get(i);
			if(index_startpoint == i)
			{
				current_edge_points.add(changeTrackpointToGeoScreenPoint(trackpoint));
				if(add_points)
					add_points = false;
				else
				{
				  add_points = true;
					continue;
				}
			}
			if(index_endpoint == i)
			{
				current_edge_points.add(changeTrackpointToGeoScreenPoint(trackpoint));
				if(add_points)
					add_points = false;
				else
				{
				  add_points = true;
					continue;
				}
			}

			if(add_points)
				current_edge_points.add(changeTrackpointToGeoScreenPoint(trackpoint));
		}


    current_edge_.setEdgePoints(current_edge_points);
    new_edges_.add(new Edge(current_edge_));
		//System.out.println(new_edges_.size());
		//System.out.println(current_edge_.getEdgePoints());


	}

	//----------------------------------------------------------------------
	/**
	 * Converts a trackpoint to a geoscreenpoint
	 * @param the trackpoint
	 * @return the geoscreenpoint
	 */
	public GeoScreenPoint changeTrackpointToGeoScreenPoint(Trackpoint trackpoint)
	{
	 	GeoScreenPoint geopoint = new GeoScreenPoint();
	 	geopoint.setLatitude((float)trackpoint.getLatitude());
	 	geopoint.setLongitude((float)trackpoint.getLongitude());
	 	geopoint.setX(trackpoint.getX());
	 	geopoint.setY(trackpoint.getY());
	 	return geopoint;
	}

	//----------------------------------------------------------------------
	/**
	 * Selects the nearest edge and open the edge-property-window
	 * @param current mouse point
	 * @param ctrl_down, ture if ctrl is pressed else false
	 */
	public void selectEdges(Point current_point,boolean ctrl_down)
	{
		TreeMap edges_treemap = graph_.getEdges();
		Collection edges = edges_treemap.values();
		Iterator edge_iterator = edges.iterator();
		Edge nearest_edge = null;

		if(!ctrl_down)
			selected_edges_.clear();

		boolean first_point = true;
		double d, help_distance = 0;

		while(edge_iterator.hasNext())
		{
			Edge edge = (Edge)edge_iterator.next();
			List edge_points = edge.getEdgePoints();
			Iterator point_iterator = edge_points.iterator();


			while(point_iterator.hasNext())
			{
				GeoScreenPoint edgepoint = (GeoScreenPoint) point_iterator.next();
				d = Math.sqrt(Math.pow((edgepoint.getX() - current_point.getX()),2) + 	Math.pow((edgepoint.getY() - current_point.getY()),2));
				if(first_point)
				{
			  	help_distance = d;
					first_point = false;
					nearest_edge = edge;
				}
				if(d < help_distance)
				{
					help_distance = d;
					nearest_edge = edge;
				}
			}
		}

		if(nearest_edge != null)
		{
			selected_edges_.add(new Edge(nearest_edge));

			if(window_ == null)
			{
				window_ = new EdgesPropertyWindow(this,nearest_edge.getID(),nearest_edge.getName(),nearest_edge.getLength(),nearest_edge.getOneway(),nearest_edge.getType());
				window_.setSize(270,300);
				window_.setVisible(true);
				window_.requestFocus();
			}
			else
			{
				window_.show();
				if(selected_edges_.size() > 1)
				{
					double distance = 0;
					Iterator selected_edge_iterator = selected_edges_.iterator();
					while(selected_edge_iterator.hasNext())
					{
					 	Edge selected_edge = (Edge) selected_edge_iterator.next();
					 	distance = distance + selected_edge.getLength();
					}

					window_.update(selected_edges_.size(),nearest_edge.getName(),distance,nearest_edge.getOneway(),nearest_edge.getType(),false);
				}
				else
					window_.update(nearest_edge.getID(),nearest_edge.getName(),nearest_edge.getLength(),nearest_edge.getOneway(),nearest_edge.getType(),true);
				window_.requestFocus();
			}
		}
		repaint();
	}

	//----------------------------------------------------------------------
	/**
	 * Closes the window
	 */
	public void closeWindow()
	{
		window_.setVisible(false);
	}

	//----------------------------------------------------------------------
	/**
	 * Set's the values of all selected edge to new values
	 * @param name of the edge
	 * @param oneway (ture) or not (false)
	 * @param type of the edge
	 */
	public void changeEdgePorperties(String name,boolean oneway,int type)
	{
		TreeMap edges_treemap = graph_.getEdges();
		Collection edges = edges_treemap.values();
		Iterator edge_iterator = edges.iterator();

    while(edge_iterator.hasNext())
		{
			Edge edge = (Edge)edge_iterator.next();

			Iterator selected_edge_iterator = selected_edges_.iterator();
			while(selected_edge_iterator.hasNext())
			{
			  Edge selected_edge = (Edge) selected_edge_iterator.next();
        //System.out.println("Edge id:"+edge.getID()+" marked Edge id: "+selected_edge.getID());
			  if(edge.getID() == selected_edge.getID())
			  {
					edge.setName(name);
					edge.setOneway(oneway);
					edge.setType(type);
				}
			}
		}
	}

	//----------------------------------------------------------------------
	/**
	 * Calculates the start and end-point for the A* algorithm
	 * @param current mouse point
	 */
	public void getNodesForCalculation(Point point)
	{
  	TreeMap nodes_tmap = graph_.getNodes();
  	Collection nodes = nodes_tmap.values();
  	Iterator nodes_iterator = nodes.iterator();
  	QuadTree nodes_quadtree = new QuadTree();



		while(nodes_iterator.hasNext())
  	{
  		Node node = (Node)nodes_iterator.next();
  	  GeoScreenPoint node_pos = node.getPosition();
			nodes_quadtree.put(node_pos.getLatitude(),node_pos.getLongitude(),node);
  	}

		GeoScreenPoint current_point = new GeoScreenPoint(point);
		current_point.inverse(getProjection());

		if(shortest_path_start_node_ == null)
		{
			shortest_path_start_node_ = (Node)nodes_quadtree.get(current_point.getLatitude(),current_point.getLongitude());
			repaint();
		}
		else
		{
			shortest_path_end_node_ = (Node)nodes_quadtree.get(current_point.getLatitude(),current_point.getLongitude());
			AStar a_star = new AStar(plugin_.getGraph(),shortest_path_start_node_,shortest_path_end_node_);
			a_star.initialise();
			a_star.computeShortestPath();
			shortest_path_ = a_star.getShortestPath();
			repaint();
    	plugin_.setCalcShortestPathActive(false);
    	shortest_path_start_node_ = null;
		}


	}

	//----------------------------------------------------------------------
  /**
	 * Resets all values
	 */
	public void reset()
	{
		positions_of_new_nodes_.clear();
		new_edges_.clear();
		selected_edges_.clear();
		shortest_path_.clear();
		shortest_path_start_node_ = null;
		shortest_path_end_node_ = null;
		current_edge_ = null;
		selected_nodes_.clear();
		cross_points_.clear();
	}


	//----------------------------------------------------------------------
  /**
	 * Select the nodes which are the nearest to the current mouse point
	 * @param point this is the current mouse point
	 * @param true if shift is down else false
	 */
	public void selectNodes(Point point,boolean shift_down)
	{
	 	TreeMap nodes_tmap = graph_.getNodes();
	 	Collection nodes = nodes_tmap.values();
  	Iterator nodes_iterator = nodes.iterator();
  	QuadTree nodes_quadtree = new QuadTree();

		if(!shift_down)
		{
			selected_nodes_.clear();
		}

		while(nodes_iterator.hasNext())
  	{
  		Node node = (Node)nodes_iterator.next();
  	  GeoScreenPoint node_pos = node.getPosition();
			nodes_quadtree.put(node_pos.getLatitude(),node_pos.getLongitude(),node);
  	}

		GeoScreenPoint current_point = new GeoScreenPoint(point);
		current_point.inverse(getProjection());

		Node selected_node = (Node)nodes_quadtree.get(current_point.getLatitude(),current_point.getLongitude());
		if(selected_node != null)
		{
			selected_nodes_.add(new Integer(selected_node.getID()));
		}
		repaint();
	}

	//----------------------------------------------------------------------
  /**
	 * Removes the selected edges and nodes from the graph
	 */
	public void deleteSelectedParts()
	{
		if(!selected_edges_.isEmpty())
		{

			Iterator selected_edge_iterator = selected_edges_.iterator();

			while(selected_edge_iterator.hasNext())
			{
				Edge selected_edge = (Edge) selected_edge_iterator.next();
				graph_.removeEdge(selected_edge.getID());
			}
		}

		if(!positions_of_new_nodes_.isEmpty())
		{
		 	positions_of_new_nodes_.remove(positions_of_new_nodes_.size() - 1);
		}

		if(!selected_nodes_.isEmpty())
		{

			Iterator selected_node_iterator = selected_nodes_.iterator();

			while(selected_node_iterator.hasNext())
			{
				Integer selected_node_id = (Integer) selected_node_iterator.next();
				graph_.removeNode(selected_node_id.intValue());
			}
		}
		repaint();
	}

	//----------------------------------------------------------------------
  /**
	 * Get the visible tracks from the trackmanager
	 */
	public void setVisibleTracks()
	{
    try
    {
      List tracks = (track_manager_.getVisibleProjectedTracks(getProjection()));

      Iterator track_iterator = tracks.iterator();

      while(track_iterator.hasNext())
      {
        Track track = new TrackImpl((Track) track_iterator.next());
        visible_tracks_.add(track);
      }
    }
    catch(InterruptedException ignore) {}
 	}

 	//----------------------------------------------------------------------
  /**
	 * Clear the visible tracks
	 */
	public void clearVisibleTracks()
	{
  	visible_tracks_.clear();
	}
}
