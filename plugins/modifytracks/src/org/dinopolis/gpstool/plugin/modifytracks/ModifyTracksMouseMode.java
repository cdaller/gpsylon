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

import org.dinopolis.gpstool.gui.MouseMode;
import org.dinopolis.gpstool.track.*;
import org.dinopolis.util.Resources;
//----------------------------------------------------------------------
/**
 * The mouse mode for the modify tracks plugin allows to interactively
 * change tracks as remove points add new points change old points by using
 * the mouse.
 *
 * @author Wolfgang Auer
 * @version $Revision$
 */

public class ModifyTracksMouseMode implements MouseMode
{
	boolean mode_active_ = false;
	Resources resources_;
	ModifyTracksLayer layer_;
	ModifyTracksPlugin plugin_;
	Track new_track_;
  Track active_track_;
	boolean draw_track_;
	boolean dragging_ = false;
	boolean pan_to_end_of_track_ = true;
	boolean modifying_trackpoint_active_ = false;

	public static final String KEY_MODIFYTRACKS_MOUSEMODE_NAME =
		"modifytracks.mousemode.name";
	public static final String KEY_MODIFYTRACKS_MOUSEMODE_DESCRIPTION =
		"modifytracks.mousemode.description";
	public static final String KEY_MODIFYTRACKS_MOUSEMODE_MNEMONIC =
		"modifytracks.mousemode.mnemonic";
	public static final String KEY_MODIFYTRACKS_MOUSEMODE_ACCELERATOR_KEY =
		"modifytracks.mousemode.accelerator_key";
	public static final String KEY_MODIFYTRACKS_MOUSEMODE_ICON =
		"modifytracks.mousemode.icon";

	//----------------------------------------------------------------------
	/**
	 * Constructor for MapManagerMouseMode.
	 */
	public ModifyTracksMouseMode()
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
	{
		resources_ = plugin_resources;
		layer_ = layer;
		plugin_ = plugin;
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
 		active_track_ = plugin_.getActiveTrack();
		plugin_.enablePlugin(active);
		//activate the layer
		if(active)
		{
			layer_.setActive(active);
			//if no track is choosen then activate the mode to draw a new track
			if(active_track_ != null)
				draw_track_ = true;
		}
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
		return (resources_.getString(KEY_MODIFYTRACKS_MOUSEMODE_NAME));
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
		return (resources_.getString(KEY_MODIFYTRACKS_MOUSEMODE_DESCRIPTION));
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
		return (resources_.getString(KEY_MODIFYTRACKS_MOUSEMODE_MNEMONIC).charAt(0));
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
		return (resources_.getString(KEY_MODIFYTRACKS_MOUSEMODE_ACCELERATOR_KEY));
	}


//----------------------------------------------------------------------
// MouseListener Adapter
//----------------------------------------------------------------------

  public void mouseClicked(MouseEvent event)
  {
    if(!mode_active_)
      return;

    new_track_ = plugin_.getNewTrack();
		if(event.getButton() == MouseEvent.BUTTON1)
    {
      // no modifiers pressed:

			if(event.isShiftDown())
				layer_.getsetNearestPoint(event.getPoint(),true);

			if(!event.isAltDown() && !event.isShiftDown() && !event.isControlDown())
      {

				if(!plugin_.isAddNewTrackpointsActive() && !plugin_.isInsertNewTrackpointsActive())
					layer_.getsetNearestPoint(event.getPoint(),false);

				if(plugin_.isInsertNewTrackpointsActive())
				{
					layer_.insertNewTrackpoint();
				}

				if(plugin_.isAddNewTrackpointsActive())
        {
					if(pan_to_end_of_track_)
					{
						layer_.getsetNearestPoint(event.getPoint(),false);
						layer_.panToEndOfTrack();
						pan_to_end_of_track_ = false;
					}
					else
					{
						layer_.setMouseCurrent(event.getPoint());
						if(new_track_ == null)
						{
	          	new_track_ = new TrackImpl();
						}
							Trackpoint point = new TrackpointImpl();
	          	point.setX(event.getX());
	          	point.setY(event.getY());
	          	point.inverse(layer_.getProjection());
							new_track_.addWaypoint(point);
	          	//new_track_.inverse(layer_.getProjection());
	          	plugin_.setNewTrack(new_track_);

					}
				}

				if(plugin_.isMakeNewTrackActive())
        {
        	if(new_track_ != null)
					{
          	Trackpoint point = new TrackpointImpl();
          	point.setX(event.getX());
          	point.setY(event.getY());
          	point.inverse(layer_.getProjection());
          	new_track_.addWaypoint(point);
          	//new_track_.inverse(layer_.getProjection());
          	plugin_.setNewTrack(new_track_);
				  }
				}
      }
    } // end of if(Button1)

    if(event.getButton() == MouseEvent.BUTTON3)
      {

        if(plugin_.isInsertNewTrackpointsActive())
        {
        	plugin_.setInsertNewTrackpointsActive(false);
				}

				if((plugin_.isMakeNewTrackActive()) && (new_track_ != null))
        {
     			layer_.track_manager_.addTrack(new_track_);
					plugin_.clearNewTrack();
					plugin_.setMakeNewTrackActive(false);
				}

				if(plugin_.isAddNewTrackpointsActive())
      	{
      		layer_.addNewTrackpointsToTrack();
      		pan_to_end_of_track_ = true;
      		plugin_.setAddNewTrackpointsActive(false);
      	}
     	}
    layer_.repaint();
  }

  public void mouseEntered(MouseEvent event)
  {
  }

  public void mouseExited(MouseEvent event)
  {
  }

  public void mousePressed(MouseEvent event)
  {
    if(!mode_active_)
      return;

  }

	public void mouseReleased(MouseEvent event)
  {
		if(dragging_)
			dragging_ = false;
  }


//----------------------------------------------------------------------
// MouseMotionListener Adapter
//----------------------------------------------------------------------

  public void mouseDragged(MouseEvent event)
  {
      if(!mode_active_)
      return;
      if(event.isControlDown())
      {
				if(!dragging_)
				{
					layer_.getsetNearestPoint(event.getPoint(),false);
					dragging_ = true;
				}
				layer_.setMouseCurrent(event.getPoint());
  			layer_.changeTrackpoint();
  		}
	}

  public void mouseMoved(MouseEvent event)
  {
  	if(!mode_active_)
    return;
    if(plugin_.isAddNewTrackpointsActive() || plugin_.isMakeNewTrackActive())
    {
    	layer_.setMouseCurrent(event.getPoint());
			layer_.repaint();
    }
    if(plugin_.isInsertNewTrackpointsActive())
    {
    	layer_.setMouseCurrent(event.getPoint());
      //System.out.println(event.getPoint());
			layer_.doCalculation();
    }

	}

}