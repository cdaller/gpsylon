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

import java.util.Iterator;
import java.util.List;

import org.dinopolis.gpstool.util.GeoMath;
import org.dinopolis.gpstool.util.geoscreen.GeoScreenPoint;

//----------------------------------------------------------------------
/**
 * This class is used to store information about edges (ID,Name,start Node, end Node,
 * all needed points to draw the path)
 *
 * @author Wolfgang Auer
 * @version $Revision$
 */

public class Edge implements Cloneable
{
	protected int id_;
  protected String name_;
  protected int type_;
  protected boolean oneway_;
  protected double length_;
  protected List edge_points_;
  protected int start_node_;
  protected int end_node_;


	//----------------------------------------------------------------------
	/**
	 * Constructor for Edge.
	 */
  public Edge()
 	{
  	name_ = "";
  	length_ = 0.0;
	}

	//----------------------------------------------------------------------
  /**
	 * Constructor for Edge.
	 */
  public Edge(Edge edge)
 	{
  	id_ = edge.getID();
    name_ = edge.getName();
    type_ = edge.getType();
    oneway_ = edge.getOneway();
    length_ = edge.getLength();
    edge_points_ = edge.getEdgePoints();
    start_node_ = edge.getStartNode();
    end_node_ = edge.getEndNode();
	}

	//----------------------------------------------------------------------
  /**
	 * Method to set the ID number.
	 * @param int id.
	 */
	public void setID(int id)
	{
		id_ = id;
	}

	//----------------------------------------------------------------------
	/**
	 * Method to get the ID number.
	 * @return int id.
	 */
	public int getID()
	{
		return(id_);
	}

	//----------------------------------------------------------------------
	/**
	 * Method to set the name of the edge.
	 * @param String name.
	 */
	public void setName(String name)
	{
		name_ = name;
	}

	//----------------------------------------------------------------------
	/**
	 * Method to get the name of the edge.
	 * @return String name.
	 */
	public String getName()
	{
		return(name_);
	}

	//----------------------------------------------------------------------
	/**
	 * Method to set the type of the edge.
	 * @param int type.
	 */
	public void setType(int type)
	{
		type_ = type;
	}

 	//----------------------------------------------------------------------
	/**
	 * Method to get the type of the edge.
	 * @return int type.
	 */
	public int getType()
	{
		return(type_);
	}

	//----------------------------------------------------------------------
	/**
	 * Method to set if the edge is oneway.
	 * @param boolean oneway.
	 */
	public void setOneway(boolean oneway)
	{
		oneway_ = oneway;
	}

	//----------------------------------------------------------------------
	/**
	 * Method to get the "oneway" of the edge.
	 * @return boolean oneway.
	 */
	public boolean getOneway()
	{
		return(oneway_);
	}

	//----------------------------------------------------------------------
	/**
	 * Method to set the pointlist of the edge.
	 * @param List edge_points.
	 */
  public void setEdgePoints(List edge_points)
	{

		GeoScreenPoint a = null;
		length_ = 0.0;
		edge_points_ = edge_points;
	  Iterator edge_points_iterator = edge_points_.iterator();
    if(edge_points_iterator.hasNext())
			 a = (GeoScreenPoint)edge_points_iterator.next();

		while(edge_points_iterator.hasNext())
		{
			GeoScreenPoint b = (GeoScreenPoint) edge_points_iterator.next();
			length_ = length_ + GeoMath.distance(a.getLatitude(),a.getLongitude(),b.getLatitude(),b.getLongitude());
			a = b;
			//System.out.println("id: " + id_+" length: " + length_);
		}
	}

	//----------------------------------------------------------------------
  /**
	 * Method to get the pointlist of the edge.
	 * @return List edge_point.
	 */
	public List getEdgePoints()
	{
		return(edge_points_);
	}

	//----------------------------------------------------------------------
	/**
	 * Method to set the start node of the edge.
	 * @param Node start_node.
	 */
  public void setStartNode(int start_node)
	{
		start_node_ = start_node;
	}

	//----------------------------------------------------------------------
  /**
	 * Method to get the start node of the edge.
	 * @return Node start_node.
	 */
	public int getStartNode()
	{
		return(start_node_);
	}

 	//----------------------------------------------------------------------
	/**
	 * Method to set the end node of the edge.
	 * @param Node end_node.
	 */
  public void setEndNode(int end_node)
	{
		end_node_ = end_node;
	}

	//----------------------------------------------------------------------
  /**
	 * Method to get the end node of the edge.
	 * @return Node end_node.
	 */
	public int getEndNode()
	{
		return(end_node_);
	}

	//----------------------------------------------------------------------
	/**
	 * Method to get the end node of the edge.
	 * @return Node end_node.
	 */
	public double getLength()
	{
		return(length_);
	}



}