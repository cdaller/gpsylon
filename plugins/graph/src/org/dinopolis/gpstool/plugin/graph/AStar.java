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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.dinopolis.gpstool.util.geoscreen.GeoScreenPoint;
import java.util.Iterator;




//----------------------------------------------------------------------
/**
 * This class is used to calculate the shortest path between a start-node
 * and a target node in a Graph. The algorithm used is A* (A star).
 *
 * @author Wolfgang Auer
 * @version $Revision$
 */

public class AStar
{
	Double infinite_;
	Graph graph_;
	Node start_node_;
	Node target_node_;
	List search_ = new ArrayList();
	List done_ = new ArrayList();
	List all_nodes_ = new ArrayList();
	List distance_ = new ArrayList();
	List compute_distance_ = new ArrayList();
	List parent_ = new ArrayList();
	List distance_sum_ = new ArrayList();
	List shortest_path_ = new ArrayList();

	//----------------------------------------------------------------------
	/**
	 * Default constructor
	 * @param graph the graph
	 * @param Node the start node
	 * @param Node the traget node
	 */
	public AStar(Graph graph,Node start_node,Node target_node)
	{
		infinite_ = new Double(Double.POSITIVE_INFINITY);
		graph_ = graph;
		start_node_ = start_node;
		target_node_ = target_node;
	}

	//----------------------------------------------------------------------
	/**
	 * Initialise the class whith all it needs.
	 */
	public void initialise()
	{
		search_.add(start_node_);
    Collection all_nodes = graph_.getNodes().values();
		GeoScreenPoint a = start_node_.getPosition();
		Iterator node_iterator = all_nodes.iterator();

		while (node_iterator.hasNext())
		{
			 Node node = (Node) node_iterator.next();
			 all_nodes_.add(node);
			 GeoScreenPoint b = node.getPosition();
			 distance_.add(infinite_);
		   //Double dist = new Double(GeoMath.distance(a.getLatitude(),a.getLongitude(),b.getLatitude(),b.getLongitude()));
			 Double dist = new Double(0);
			 compute_distance_.add(dist);
			 parent_.add(null);
			 distance_sum_.add(new Double(0));
		}

		distance_.set(all_nodes_.indexOf(start_node_),new Double(0));
		compute_distance_.set(all_nodes_.indexOf(target_node_),new Double(0));
		Double sum = new Double(((Double) distance_.get(all_nodes_.indexOf(start_node_))).doubleValue() + ((Double) compute_distance_.get(all_nodes_.indexOf(start_node_))).doubleValue());
		distance_sum_.set(all_nodes_.indexOf(start_node_),sum);
 	}

	//----------------------------------------------------------------------
	/**
	 * Computes the shortest path
	 */
 	public void computeShortestPath()
 	{
 		while(!search_.isEmpty())
 		{
 			Iterator search_iterator = search_.iterator();

 			double dist = 0;
 			boolean first = true;
			Node shortest_dist_node = null;

			while(search_iterator.hasNext())
 			{
 				Node node = (Node)search_iterator.next();

 				int index_of_node = all_nodes_.indexOf(node);

 				if(first)
 				{
 					dist =  ((Double)distance_sum_.get(index_of_node)).doubleValue();
 					shortest_dist_node = node;
 					first = false;
 				}

				if(dist > ((Double)distance_sum_.get(index_of_node)).doubleValue())
 					shortest_dist_node = node;
 			}

		 	search_.remove(shortest_dist_node);

		 	if(shortest_dist_node.equals(target_node_))
		 	{
				pathFound();
				return;
			}

		 	List neighbours_of_node = shortest_dist_node.getNeighbourNodes();

		 	Iterator neighbour_iterator = neighbours_of_node.iterator();

		 	while(neighbour_iterator.hasNext())
		 	{
		 		Integer node_id = (Integer) neighbour_iterator.next();
		 		Node node = graph_.getNode(node_id.intValue());
		 		int node_index_1 = all_nodes_.indexOf(shortest_dist_node);
		 		int node_index_2 = all_nodes_.indexOf(node);
		 		double distance_neighbour1 = ((Double)distance_.get(node_index_1)).doubleValue();
		 		double distance_neighbour2 = ((Double)distance_.get(node_index_2)).doubleValue();
		 		double distance_between_1_2 = graph_.getEdge(shortest_dist_node.getID(),node.getID()).getLength();

				if((distance_neighbour2 <= (distance_neighbour1 + distance_between_1_2)) && (done_.contains(node) || search_.contains(node)))
				{
					continue;
				}

		 		double compute_distance = ((Double) compute_distance_.get(node_index_2)).doubleValue();
		 	 	distance_.set(node_index_2,new Double(distance_neighbour1 + distance_between_1_2));
		 	 	distance_sum_.set(node_index_2,new Double(distance_neighbour2 + compute_distance));
		 	 	parent_.set(node_index_2,shortest_dist_node);

      	if(done_.contains(node))
      		done_.remove(node);
      	if(!search_.contains(node))
      		search_.add(node);
			}

			done_.add(shortest_dist_node);
		}

		System.out.println("NO WAY FOUND!");
 	}

	//----------------------------------------------------------------------
	/**
	 * Is called when a shortest path was found
	 */
 	public void pathFound()
 	{
  	Node node1 = target_node_;
		Node node2 = null;

		while(node1 != null)
		{
			int node_index = all_nodes_.indexOf(node1);
			node2 = (Node)parent_.get(node_index);
			if(node1 != null && node2 != null)
				shortest_path_.add(graph_.getEdge(node1.getID(),node2.getID()));
			node1 = node2;
		}
 	}

	//----------------------------------------------------------------------
	/**
	 * Returns the shortes path as an edge-list
	 * @return shortest path as an edge-list
	 */
 	public List getShortestPath()
 	{
 		return shortest_path_;
 	}
}
