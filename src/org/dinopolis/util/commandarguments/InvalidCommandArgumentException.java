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
 * CommandInvalidArgumentException is used by CommandArguments to
 * indicate that a given argument is not valid.
 *
 * @author Christof Dallermassl <cdaller@iicm.edu>
 * @version $Id$
 * */
public class InvalidCommandArgumentException extends CommandArgumentException
{

//---------------------------------------------------------------------
/**
 * Empty standard constructor;
 */
  public InvalidCommandArgumentException()
  {
    super();
  }


//---------------------------------------------------------------------
/**
 * Standard constructor with a message as argument.
 *
 * @param message the message of this exception.
 */
  public InvalidCommandArgumentException(String message)
  {
    super(message);
  }


//---------------------------------------------------------------------
/**
 * Constructor with a message and the invalid argument's position as
 * the arguments. 
 *
 * @param message the message of this exception.
 * @param position the position of the invalid argument.
 */
  public InvalidCommandArgumentException(String message, int position)
  {
    super(message,position);
  }
}


