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


import java.util.*;

import org.dinopolis.gpstool.util.geoscreen.GeoScreenPoint;

//----------------------------------------------------------------------
/**
 * This class is used to store all information about the graph (Name,Nodes,Edges)
 *
 * @author Wolfgang Auer
 * @version $Revision$
 */

public class Graph
{
	protected String name_;
	protected int node_id_;
	protected int edge_id_;
	protected TreeMap nodes_;
	protected TreeMap edges_;
	protected	Node start_node_;
	protected	Node end_node_;


	//----------------------------------------------------------------------
	/**
	 * Constructor for the graph.
	 */
	public Graph()
	{
		nodes_ = new TreeMap();
		edges_ = new TreeMap();
	}

	//----------------------------------------------------------------------
	/**
	 * Method to set the name of the grah.
	 * @param String name.
	 */
	public void setName(String name)
	{
		name_ = name;
	}

	//----------------------------------------------------------------------
	/**
	 * Method to get the name of the graph.
	 * @return String name.
	 */
	public String getName()
	{
		return(name_);
	}

	//----------------------------------------------------------------------
	/**
	 * Method to add a node to the graph.
	 * @param Node node.
	 */
	public void addNode(Node node)
	{
		node_id_++;
		node.setID(node_id_);
		Integer id = new Integer(node_id_);
		nodes_.put(id,node);
	}

	//----------------------------------------------------------------------
  /**
	 * Method to add a node to the graph.
	 * @param String name.
	 * @param GeoScreenPoint position.
	 */
	public void addNode(GeoScreenPoint position)
	{
		node_id_++;
		Integer id = new Integer(node_id_);
		Node node = new Node();
		node.setID(node_id_);
		node.setName("Node"+node_id_);
		node.setPosition(position);
		nodes_.put(id,node);
	}

	//----------------------------------------------------------------------
	/**
	 * Method to add a node form a file to the graph.
	 * @param Node node.
	 */
	public void addNodeFromFile(Node node)
	{
		Integer id = new Integer(node.getID());
		nodes_.put(id,node);
	}

	//----------------------------------------------------------------------
	/**
	 * Method to get a node from the graph.
	 * @param int node_id.
	 * @return Node node.
	 */
	public Node getNode(int int_id)
	{
		Integer id = new Integer(int_id);
		return((Node)nodes_.get(id));
	}

	//----------------------------------------------------------------------
	/**
	 * Method to get all nodes from the graph.
	 * @return treemap of all nodes.
	 */
	public TreeMap getNodes()
	{
		return(nodes_);
	}

	//----------------------------------------------------------------------
	/**
	 * Method to add a edge to the graph.
	 * @param Edge edge.
	 */
	public void addEdge(Edge edge)
	{
		edge_id_++;
		Integer id = new Integer(edge_id_);
		edge.setID(edge_id_);
		edges_.put(id,edge);
		Integer start_node_id = new Integer(edge.getStartNode());
		Integer end_node_id = new Integer(edge.getEndNode());
		start_node_ = (Node) nodes_.get(start_node_id);
		end_node_ = (Node) nodes_.get(end_node_id);
		start_node_.addNeighbourNode(edge.getEndNode());
		end_node_.addNeighbourNode(edge.getStartNode());
		nodes_.put(start_node_id,start_node_);
		nodes_.put(end_node_id,end_node_);
	}

	//----------------------------------------------------------------------
	/**
	 * Method to add a edge to the graph.
	 * @param int start_node
	 * @param int end_node
	 * @param List edge_points.
	 */
	public void addEdge(int start_node, int end_node, List edge_points)
	{
		edge_id_++;
		Integer id = new Integer(edge_id_);
		Edge edge = new Edge();
		edge.setID(edge_id_);
		edge.setStartNode(start_node);
		edge.setEndNode(end_node);
		edge.setEdgePoints(edge_points);
		edges_.put(id,edge);
		Integer start_node_id = new Integer(start_node);
		Integer end_node_id = new Integer(end_node);
		start_node_ = (Node) nodes_.get(start_node_id);
		end_node_ = (Node) nodes_.get(end_node_id);
		start_node_.addNeighbourNode(edge.getEndNode());
		end_node_.addNeighbourNode(edge.getStartNode());
		nodes_.put(start_node_id,start_node_);
		nodes_.put(end_node_id,end_node_);
	}

	//----------------------------------------------------------------------
	/**
	 * Method to add a edge to the graph.
	 * @param Edge edge.
	 */
	public void addFromFileEdge(Edge edge)
	{
		Integer id = new Integer(edge.getID());
		edges_.put(id,edge);
		Integer start_node_id = new Integer(edge.getStartNode());
		Integer end_node_id = new Integer(edge.getEndNode());
		start_node_ = (Node) nodes_.get(start_node_id);
		end_node_ = (Node) nodes_.get(end_node_id);
		start_node_.addNeighbourNode(edge.getEndNode());
		end_node_.addNeighbourNode(edge.getStartNode());
		nodes_.put(start_node_id,start_node_);
		nodes_.put(end_node_id,end_node_);
	}


	//----------------------------------------------------------------------
  /**
	 * Method to get a edge from the graph.
	 * @param int edge_id.
	 * @return Edge edge
	 */
	public Edge getEdge(int int_id)
	{
		Integer id = new Integer(int_id);
		return((Edge) edges_.get(id));
	}

	//----------------------------------------------------------------------
	/**
	 * Method to get a edge from the graph.
	 * @param node1_id node2_id.
	 * @return Edge edge
	 */
	public Edge getEdge(int node_1,int node_2)
	{
		Collection edges = edges_.values();
	  Iterator edge_iterator = edges.iterator();

	  while(edge_iterator.hasNext())
	  {
	  	Edge edge = (Edge) edge_iterator.next();
			int start_node_id = edge.getStartNode();
			int end_node_id = edge.getEndNode();
			if(start_node_id == node_1 && end_node_id == node_2)
				return edge;
			if(start_node_id == node_2 && end_node_id == node_1)
				return edge;
	  }
		return null;
	}

	//----------------------------------------------------------------------
	/**
	 * Method to get a edge from the graph.
	 * @param node_id
	 * @return Edge edge
	 */
	public List getEdges(int node)
	{
		Collection edges_col = edges_.values();
	  Iterator edge_iterator = edges_col.iterator();
		List edges = new ArrayList();

	  while(edge_iterator.hasNext())
	  {
	  	Edge edge = (Edge) edge_iterator.next();
			int start_node_id = edge.getStartNode();
			int end_node_id = edge.getEndNode();
			if(start_node_id == node || end_node_id == node)
				edges.add(edge);
	  }
		return edges;
	}

	//----------------------------------------------------------------------
	/**
	 * Method to remove an edge from the graph.
	 * @param edge_id.
	 */
	public void removeEdge(int edge_id)
	{
		Edge edge = (Edge)edges_.get(new Integer(edge_id));
		Node node1 = (Node)nodes_.get(new Integer(edge.getStartNode()));
		Node node2 = (Node)nodes_.get(new Integer(edge.getEndNode()));
  	node1.removeNeighbour(node2.getID());
  	node2.removeNeighbour(node1.getID());
  	nodes_.put(new Integer(node1.getID()),node1);
  	nodes_.put(new Integer(node2.getID()),node2);
  	edges_.remove(new Integer(edge_id));
	}

	//----------------------------------------------------------------------
	/**
	 * Method to remove a node from the graph.
	 * @param node_id.
	 */
	public void removeNode(int node_id)
	{
		List edges = getEdges(node_id);

		Iterator edge_iterator = edges.iterator();

		while(edge_iterator.hasNext())
		{
			Edge edge = (Edge) edge_iterator.next();
			System.out.println("HIER1");
			removeEdge(edge.getID());
		}

		nodes_.remove(new Integer(node_id));
	}

	//----------------------------------------------------------------------
	/**
	 * Method to get all edges from the graph.
	 * @return list of all edges.
	 */
	public TreeMap getEdges()
	{
		return(edges_);
	}
}