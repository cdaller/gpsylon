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
import java.util.List;

import org.dinopolis.gpstool.util.geoscreen.GeoScreenPoint;

//----------------------------------------------------------------------
/**
 * This class is used to store information about Nodes (ID,Name,Position)
 *
 * @author Wolfgang Auer
 * @version $Revision$
 */

public class Node
{
	protected int id_;
  protected String name_;
  protected GeoScreenPoint position_;
  protected List neighbour_nodes_ = new ArrayList();


	//----------------------------------------------------------------------
	/**
	 * Constructor for Node.
	 */
  public Node()
 	{
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
	 * Method to set the name of the node.
	 * @param String name.
	 */
	public void setName(String name)
	{
		name_ = name;
	}

	//----------------------------------------------------------------------
	/**
	 * Method to get the name of the node.
	 * @return String name.
	 */
	public String getName()
	{
		return(name_);
	}

	//----------------------------------------------------------------------
	/**
	 * Method to set the position of the node.
	 * @param GeoScreenPoint position.
	 */
  public void setPosition(GeoScreenPoint position)
	{
		position_ = position;
	}

	//----------------------------------------------------------------------
  /**
	 * Method to get the postion of the node.
	 * @return GeoScreenPoint position.
	 */
	public GeoScreenPoint getPosition()
	{
		return(position_);
	}

	//----------------------------------------------------------------------
	/**
	 * Method to add a new neighbour node.
	 * @param node id.
	 */
  public void addNeighbourNode(int id)
	{
 		neighbour_nodes_.add(new Integer(id));
	}

	//----------------------------------------------------------------------
  /**
	 * Method to get the id's of the neighbour nodes.
	 * @return node id's as list.
	 */
	public List getNeighbourNodes()
	{
		return(neighbour_nodes_);
	}

	//----------------------------------------------------------------------
  /**
	 * Method to remove a neighbour from the neighbours list.
	 * @param neighbour node id.
	 */
	public void removeNeighbour(int neighbour_id)
	{
		neighbour_nodes_.remove(new Integer(neighbour_id));
	}
}