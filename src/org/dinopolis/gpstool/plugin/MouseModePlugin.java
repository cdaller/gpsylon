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


package org.dinopolis.gpstool.plugin;

import javax.swing.Action;
import org.dinopolis.gpstool.gui.MouseMode;

//----------------------------------------------------------------------
/**
 * This interface is used for all plugins that want to provide any
 * kind of gui-interface for the application. The plugins may provide
 * menu items that are placed into a sub menu (in the "plugins" menu)
 * or if they provide a larger quantity of actions, the plugin may
 * provide actions for a main menu item.
 * <p>
 * The plugins are informed
 * about start and stop, so they may initialize and free their
 * resources on startup and on closing of the application.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public interface MouseModePlugin extends Plugin, MouseMode
{

 
}


