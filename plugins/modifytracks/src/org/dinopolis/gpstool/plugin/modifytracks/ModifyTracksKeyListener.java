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

package org.dinopolis.gpstool.plugin.modifytracks;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

//----------------------------------------------------------------------
/**
 * This plugin allows to manage the available tracks. It shows all
 * available tracks on a layer it provides, it provides methods and a mouse mode
 * which allow to edit them. Foristens to make a new track, to edit trackpoints in
 * a track, to insert new trackpoints in a track and to delete track points
 *
 * @author Wolfgang Auer
 * @version $Revision$
 */

public class ModifyTracksKeyListener extends KeyAdapter
{
  //----------------------------------------------------------------------
	// KeyListener methods
	//----------------------------------------------------------------------

	public void keyTyped(KeyEvent e)
	{
		System.out.println("AAAAAAAAAAAAAAAA");
		if(e.getKeyCode()==KeyEvent.VK_DELETE)
			System.out.println("del");
	}
	public void keyReleased(KeyEvent e)
	{
		System.out.println("AAAAAAAAAAAAAAAA");
		if(e.getKeyCode()==KeyEvent.VK_DELETE)
			System.out.println("del");
	}
	public void keyPressed(KeyEvent e)
	{
		System.out.println("AAAAAAAAAAAAAAAA");
		if(e.getKeyCode()==KeyEvent.VK_DELETE)
			System.out.println("del");
	}

}
