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


package org.dinopolis.gpstool.util.geoscreen;

import com.bbn.openmap.proj.Projection;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Vector;
import java.util.Iterator;

//----------------------------------------------------------------------
/**
 * This class represents a list of GeoScreen Objects.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GeoScreenList extends AbstractList implements GeoScreen, Cloneable
{
  Vector geoscreen_objects_;

//----------------------------------------------------------------------
/**
 * Constructs an empty list.
 */
  public GeoScreenList()
  {
    geoscreen_objects_ = new Vector();
  }


//----------------------------------------------------------------------
/**
 * Constructs a list containing the elements of the specified
 * collection, in the order they are returned by the collection's
 * iterator. The elements in the collections must be GeoScreen
 * objects!
 */
  public GeoScreenList(Collection collection)
  {
    geoscreen_objects_ = new Vector(collection);
  }

//----------------------------------------------------------------------
/**
 * Use the geographical coordinates (have to be set prior to calling
 * this method) and the given projection to calculate the screen
 * coordinates of this GeoScreenPoint.
 *
 * @param projection the projection to use.
 */
  public void forward(Projection projection)
  {
    Iterator iterator = geoscreen_objects_.iterator();
    while(iterator.hasNext())
    {
      ((GeoScreen)iterator.next()).forward(projection);
    }
  }

//----------------------------------------------------------------------
/**
 * Use the screen coordinates (have to be set prior to calling this
 * method) and the given projection to calculate the geographical
 * coordinates of this GeoScreenPoint.
 *
 * @param projection the projection to use.
 */
  public void inverse(Projection projection)
  {
    Iterator iterator = geoscreen_objects_.iterator();
    while(iterator.hasNext())
    {
      ((GeoScreen)iterator.next()).inverse(projection);
    }
  }

//----------------------------------------------------------------------
// the list interface methods:
//----------------------------------------------------------------------

//----------------------------------------------------------------------
/**
 * Returns the element at the specified position in this list.
 *
 * @param index index of element to return.
 * @return the element at the specified position in this list.
 * 
 * @throws IndexOutOfBoundsException if the index is out of range (index
 * 		  &lt; 0 || index &gt;= size()).
 */
  public Object get(int index)
  {
    return(geoscreen_objects_.get(index));
  }


//----------------------------------------------------------------------
/**
 * Returns the number of elements in this list.  If this list contains
 * more than <tt>Integer.MAX_VALUE</tt> elements, returns
 * <tt>Integer.MAX_VALUE</tt>.
 *
 * @return the number of elements in this list.
 */
  public int size()
  {
    return(geoscreen_objects_.size());
  }

//----------------------------------------------------------------------
/**
 * Replaces the element at the specified position in this list with the
 * specified element (optional operation).
 *
 * @param index index of element to replace.
 * @param element element to be stored at the specified position.
 * @return the element previously at the specified position.
 * 
 * @throws    ClassCastException if the class of the specified element
 * 		  prevents it from being added to this list.
 * @throws    NullPointerException if the specified element is null and
 *            this list does not support null elements.
 * @throws    IllegalArgumentException if some aspect of the specified
 *		  element prevents it from being added to this list.
 * @throws    IndexOutOfBoundsException if the index is out of range
 *		  (index &lt; 0 || index &gt;= size()).
 */
  public Object set(int index, Object object)
  {
    return(geoscreen_objects_.set(index,(GeoScreen)object));
  }

    /**
     * Inserts the specified element at the specified position in this list
     * (optional operation).  Shifts the element currently at that position
     * (if any) and any subsequent elements to the right (adds one to their
     * indices).<p>
     *
     * This implementation always throws an UnsupportedOperationException.
     *
     * @param index index at which the specified element is to be inserted.
     * @param element element to be inserted.
     * 
     * @throws ClassCastException if the class of the specified element
     * 		  prevents it from being added to this list.
     * @throws IllegalArgumentException if some aspect of the specified
     *		  element prevents it from being added to this list.
     * @throws IndexOutOfBoundsException index is out of range (<tt>index &lt;
     *		  0 || index &gt; size()</tt>).
     */
  public void add(int index, Object object)
  {
    geoscreen_objects_.add(index,(GeoScreen)object);
  }

//----------------------------------------------------------------------
/**
 * Removes the element at the specified position in this list (optional
 * operation).  Shifts any subsequent elements to the left (subtracts one
 * from their indices).  Returns the element that was removed from the
 * list.
 *
 * @param index the index of the element to removed.
 * @return the element previously at the specified position.
 * 
 * @throws IndexOutOfBoundsException if the index is out of range (index
 *            &lt; 0 || index &gt;= size()).
 */
  public Object remove(int index)
  {
    return(geoscreen_objects_.remove(index));
  }
  
//----------------------------------------------------------------------
/**
 * Returns a clone of this list. The copy will contain a reference to
 * a clone of the internal data vector) not a reference to the
 * original internal data vector.
 *
 */
  public Object clone()
  {
    return(new GeoScreenList((Vector)geoscreen_objects_.clone()));
  }
}


