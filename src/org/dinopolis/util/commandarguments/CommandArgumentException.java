/***********************************************************************
 * @(#)$RCSfile$   $Revision$ $Date$
 *
 * Copyright (c) 2000 IICM, Graz University of Technology
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


package org.dinopolis.util.commandarguments;

//---------------------------------------------------------------------
//---------------------------------------------------------------------
/**
 * CommandArgumentExcpetion 
 *
 * @author Christof Dallermassl <cdaller@iicm.edu>
 * @version $Id$
 *
 */
public class CommandArgumentException extends Exception
{
  /** the position of the invalid argument*/
  int position_ = -1;

//---------------------------------------------------------------------
/**
 * Empty standard constructor;
 */
  public CommandArgumentException()
  {
    super();
  }

//---------------------------------------------------------------------
/**
 * Standard constructor with a message as argument.
 *
 * @param message the message of this exception.
 */
  public CommandArgumentException(String message)
  {
    super(message);
  }

//---------------------------------------------------------------------
/**
 * Standard constructor with a message as argument.
 *
 * @param message the message of this exception.
 * @param position the position of the invalid argument.
 */
  public CommandArgumentException(String message, int position)
  {
    this(message);
    position_ = position;
  }

//---------------------------------------------------------------------
/**
 * Returns the position of the invalid argument.
 *
 * @return the position of the invalid argument.
 */
  public int getPosition()
  {
    return(position_);
  }
}


