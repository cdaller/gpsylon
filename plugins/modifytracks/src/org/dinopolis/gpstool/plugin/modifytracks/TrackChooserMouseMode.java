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

import java.awt.event.MouseEvent;

import javax.swing.Icon;

import org.dinopolis.gpstool.TrackManager;
import org.dinopolis.gpstool.gui.MouseMode;
import org.dinopolis.gpstool.track.Trackpoint;
import org.dinopolis.util.Resources;

//----------------------------------------------------------------------
/**
 * The mouse mode for the map manager plugin allows to interactively
 * select available maps on the map component by clicking and dragging
 * the mouse.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class TrackChooserMouseMode implements MouseMode
{
	boolean mode_active_ = false;
	Resources resources_;
	ModifyTracksLayer layer_;
	TrackManager track_manager_;
	ModifyTracksPlugin plugin_;
	Trackpoint rect_start_point_;
	Trackpoint rect_end_point_;
	int rect_start_x_;
	int rect_start_y_;

	public static final String KEY_TRACKCHOOSER_MOUSEMODE_NAME =
		"trackchooser.mousemode.name";
	public static final String KEY_TRACKCHOOSER_MOUSEMODE_DESCRIPTION =
		"trackchooser.mousemode.description";
	public static final String KEY_TRACKCHOOSER_MOUSEMODE_MNEMONIC =
		"trackchooser.mousemode.mnemonic";
	public static final String KEY_TRACKCHOOSER_MOUSEMODE_ACCELERATOR_KEY =
		"trackchooser.mousemode.accelerator_key";
	public static final String KEY_TRACKCHOOSER_MOUSEMODE_ICON =
		"trackchooser.mousemode.icon";

	//----------------------------------------------------------------------
	/**
	 * Constructor for MapManagerMouseMode.
	 */
	public TrackChooserMouseMode()
	{
	}

	/**
	 * Method initialize.
	 * @param plugin_resources the resources of the map manager plugin.
	 * @param layer the layer to draw to
	 */
	public void initialize(
		Resources plugin_resources,
		ModifyTracksLayer layer,
		ModifyTracksPlugin plugin)
		//TrackManager track_manager)
	{
		resources_ = plugin_resources;
		layer_ = layer;
		plugin_ = plugin;
		//track_manager_ = track_manager;
	}

	//----------------------------------------------------------------------
	/**
	 * Called by the application to switch the mouse mode on or off. If
	 * the mouse mode is switched off, it must not react on mouse events
	 * (although it might register them). This method may be used to
	 * change the mouse cursor, ...
	 *
	 * @param active if <code>true</code> the mouse mode is switched on
	 * and should react on mouse events.
	 */

	public void setActive(boolean active)
	{
		mode_active_ = active;
		if(active)
		 layer_.setActive(active);
	}

	//----------------------------------------------------------------------
	/**
	 * Returns if the mouse mode is active or not.
	 *
	 * @return <code>true</code> if the mouse mode is active and reacts on
	 * mouse events.
	 */
	public boolean isActive()
	{
		return (mode_active_);
	}

	//	----------------------------------------------------------------------
	/**
	 * The name returned here is used in the menu and/or the toolbar of
	 * the application to switch the mouse mode on or off. It should be
	 * localized.
	 *
	 * @return the name of the mouse mode.
	 */
	public String getMouseModeName()
	{
		return (resources_.getString(KEY_TRACKCHOOSER_MOUSEMODE_NAME));
	}

	//	----------------------------------------------------------------------
	/**
	 * The description returned here is used in the menu and/or the toolbar of
	 * the application to switch the mouse mode on or off.
	 *
	 * @return the description of the mouse mode.
	 */
	public String getMouseModeDescription()
	{
		return (resources_.getString(KEY_TRACKCHOOSER_MOUSEMODE_DESCRIPTION));
	}

	//	----------------------------------------------------------------------
	/**
	 * The icon returned here is used in the menu and/or the toolbar of
	 * the application to switch the mouse mode on or off.
	 *
	 * @return the icon of the mouse mode.
	 */
	public Icon getMouseModeIcon()
	{
		//		return(resources_.getIcon(KEY_MAPAMANGER_MOUSEMODE_ICON));
		return (null);
	}

	//	----------------------------------------------------------------------
	/**
	 * Returns the mnemonic character that is used for manual (keyboard)
	 * selection in a menu. If possible, it should be the first letter of
	 * the name (default).
	 *
	 * @return a string describing the mnemonic character for this mouse
	 * mode when used in a menu.
	 */
	public char getMouseModeMnemonic()
	{
		return (resources_.getString(KEY_TRACKCHOOSER_MOUSEMODE_MNEMONIC).charAt(0));
	}

	//	----------------------------------------------------------------------
	/**
	 * Returns the accelerator key that is used for the mouse mode in the
	 * menu or toolbar. The format of the key strings is described in
	 * {@link javax.swing.KeyStroke#getKeyStroke(java.lang.String)}. Some
	 * examples are given: <code>INSERT</code>,<code>controle
	 * DELETE</code>,<code>alt shift X</code>,<code>shift
	 * F</code>.
	 *
	 * @return a string describing the accelerator key.
	 */
	public String getMouseModeAcceleratorKey()
	{
		return (resources_.getString(KEY_TRACKCHOOSER_MOUSEMODE_ACCELERATOR_KEY));
	}

	//	----------------------------------------------------------------------
	/**
	 * Invoked when the mouse has been clicked on a component. Selects the maps
	 * from the map manager. Shift selects all maps (from smalles to largest
	 * scale), control adds the maps to previous selections.
	 *
	 * @param event the mouse event.
	 */
	public void mouseClicked(MouseEvent event)
	{
    if(!mode_active_)
      return;


	}

	//	----------------------------------------------------------------------
	/**
	 * Invoked when a mouse button has been pressed on a component.
	 *
	 * @param event the mouse event.
	 */
	public void mousePressed(MouseEvent event)
	{
		if(!mode_active_)
      return;
		layer_.setMouseDragStart(event.getPoint());
	}

	//	----------------------------------------------------------------------
	/**
	 * Invoked when a mouse button has been released on a component.
	 *
	 * @param event the mouse event.
	 */
	public void mouseReleased(MouseEvent event)
	{
		if(!mode_active_)
      return;
		layer_.doCalculation();

	}

	//	----------------------------------------------------------------------
	/**
	 * Invoked when the mouse enters a component.
	 *
	 * @param event the mouse event.
	 */
	public void mouseEntered(MouseEvent event)
	{
	}

	//	----------------------------------------------------------------------
	/**
	 * Invoked when the mouse exits a component.
	 *
	 * @param event the mouse event.
	 */
	public void mouseExited(MouseEvent event)
	{
	}

	//	----------------------------------------------------------------------
	/**
	 * Invoked when a mouse button is pressed on a component and then
	 * dragged.  Mouse drag events will continue to be delivered to
	 * the component where the first originated until the mouse button is
	 * released (regardless of whether the mouse position is within the
	 * bounds of the component).
	 *
	 * @param event the mouse event.
	 */
	public void mouseDragged(MouseEvent event)
	{
  	if(!mode_active_)
      return;

		layer_.setMouseDragEnd(event.getPoint());
		layer_.doCalculation();

	}

	//	----------------------------------------------------------------------
	/**
	 * Invoked when the mouse button has been moved on a component
	 * (with no buttons no down).
	 *
	 * @param event the mouse event.
	 */
	public void mouseMoved(MouseEvent event)
	{
	}
}