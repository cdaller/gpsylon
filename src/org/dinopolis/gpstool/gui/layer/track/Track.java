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


package org.dinopolis.gpstool.gui.layer.track;

import org.dinopolis.gpstool.util.geoscreen.GeoScreenList;
import java.util.Collection;

//----------------------------------------------------------------------
/**
 * This class represents a Track (list of {@link
 * org.dinopolis.gpstool.gui.layer.track.TrackPoint} objects and some
 * additional information). As it extends {@link
 * org.dinopolis.gpstool.util.geoscreen.GeoScreenList}, it uses the
 * well known {@link java.util.List} interface to add/remove
 * elements. If objects other than TrackPoint are added, an error may
 * occur.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class Track extends GeoScreenList
{
  /** the name of the track */
  String name_;
  
//----------------------------------------------------------------------
/**
 * Constructs an empty track.
 */
  public Track()
  {
    super();
  }

//----------------------------------------------------------------------
/**
 * Constructs an empty track with the given name.
 *
 * @param name the name of the track
 */
  public Track(String name)
  {
    this();
    name_ = name;
  }


//----------------------------------------------------------------------
/**
 * Constructs a list containing the elements of the specified
 * collection, in the order they are returned by the collection's
 * iterator. The elements in the collections must be TrackPoint
 * objects!
 */
  public Track(Collection collection)
  {
    super(collection);
  }

//----------------------------------------------------------------------
/**
 * Get the name of the track.
 *
 * @return the name.
 */
  public String getName() 
    {
      return (name_);
    }
  
//----------------------------------------------------------------------
/**
 * Set the name of the track.
 *
 * @param name the name.
 */
  public void setName(String name) 
    {
      name_ = name;
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
    return(new Track((GeoScreenList)super.clone()));
  }
  
}




