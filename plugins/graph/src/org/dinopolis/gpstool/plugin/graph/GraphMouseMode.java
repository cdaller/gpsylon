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

import java.awt.event.MouseEvent;

import javax.swing.Icon;

import org.dinopolis.gpstool.gui.MouseMode;
import org.dinopolis.gpstool.track.Track;
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

public class GraphMouseMode implements MouseMode
{
	boolean mode_active_ = false;
	Resources resources_;
	GraphLayer layer_;
	GraphPlugin plugin_;
	Track active_track_;
  boolean draw_track_;
	boolean modifying_trackpoint_active_ = false;
	boolean once1_ = true;
	boolean once2_ = true;

	public static final String KEY_GRAPH_MOUSEMODE_NAME =
		"graph.mousemode.name";
	public static final String KEY_GRAPH_MOUSEMODE_DESCRIPTION =
		"graph.mousemode.description";
	public static final String KEY_GRAPH_MOUSEMODE_MNEMONIC =
		"graph.mousemode.mnemonic";
	public static final String KEY_GRAPH_MOUSEMODE_ACCELERATOR_KEY =
		"graph.mousemode.accelerator_key";
	public static final String KEY_GRAPH_MOUSEMODE_ICON =
		"graph.mousemode.icon";

	//----------------------------------------------------------------------
	/**
	 * Constructor for MapManagerMouseMode.
	 */
	public GraphMouseMode()
	{
	}

	/**
	 * Method initialize.
	 * @param plugin_resources the resources of the map manager plugin.
	 * @param layer the layer to draw to
	 */
	public void initialize(
		Resources plugin_resources,
		GraphLayer layer,
		GraphPlugin plugin)
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
		boolean clear = true;
		plugin_.enablePlugin(active);
		if(active && once1_)
		{
			//System.out.println("set");
			layer_.setVisibleTracks();
			layer_.calcCrossPoints();
			once1_ = false;
			clear = false;
		}
		else
		{
			once1_ = true;
		}

		if(!active && clear)
			{
				//System.out.println("clear");
				layer_.clearVisibleTracks();
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
		return (resources_.getString(KEY_GRAPH_MOUSEMODE_NAME));
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
		return (resources_.getString(KEY_GRAPH_MOUSEMODE_DESCRIPTION));
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
		return (resources_.getString(KEY_GRAPH_MOUSEMODE_MNEMONIC).charAt(0));
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
		return (resources_.getString(KEY_GRAPH_MOUSEMODE_ACCELERATOR_KEY));
	}


//----------------------------------------------------------------------
// MouseListener Adapter
//----------------------------------------------------------------------

  public void mouseClicked(MouseEvent event)
  {
    if(!mode_active_)
      return;

		if(event.getButton() == MouseEvent.BUTTON1)
    {
				if(plugin_.isAddNodesActive())
        {
					layer_.makeNearestPointToNewNode(event.getPoint());
					layer_.repaint();
				}
				if(plugin_.isSetEdgesActive() && plugin_.getGraph() != null)
				{
				 	layer_.setEdge(event.getPoint());
				}


				if(!plugin_.isAddNodesActive() && !plugin_.isSetEdgesActive() && !plugin_.isCalcShortestPathActive() && plugin_.getGraph() != null)
				{
					if(event.isControlDown())
						layer_.selectNodes(event.getPoint(),event.isShiftDown());
					else
						layer_.selectEdges(event.getPoint(),event.isShiftDown());
				}

				if(plugin_.isCalcShortestPathActive())
				{
					layer_.getNodesForCalculation(event.getPoint());
				}


    } // end of if(Button1)

    if(event.getButton() == MouseEvent.BUTTON3)
    {
      if(plugin_.isAddNodesActive())
      {
				layer_.addNewNodesToGraph();
				plugin_.setAddNodesActive(false);
			}

			if(plugin_.isSetEdgesActive())
      {
				layer_.addNewEdgesToGraph();
				plugin_.setSetEdgesActive(false);
			}
    	layer_.repaint();
  	}

	}

  public void mouseEntered(MouseEvent event)
  {
  }

  public void mouseExited(MouseEvent event)
  {
  }

  public void mousePressed(MouseEvent event)
  {
    if(!layer_.isActive() || !mode_active_)
      return;

    if(event.getButton() == MouseEvent.BUTTON1)
    {


          // no modifiers pressed:
      if(!event.isAltDown() && !event.isShiftDown() && !event.isControlDown())
      {

			}
    } // end of if(Button1)




 }
  public void mouseReleased(MouseEvent event)
  {
	 }



//----------------------------------------------------------------------
// MouseMotionListener Adapter
//----------------------------------------------------------------------

  public void mouseDragged(MouseEvent event)
  {
      if(!layer_.isActive() || !mode_active_)
      return;

				//if(plugin_.isRemoveTrackpointsActive())
      	//{
					layer_.setMouseDragEnd(event.getPoint());
  				layer_.doCalculation();
				//}

	}

  public void mouseMoved(MouseEvent event)
  {
  	if(!mode_active_)
    return;
    //if(plugin_.isAddNewTrackpointsActive() || plugin_.isMakeNewTrackActive())
    //{
    	layer_.setMouseDragEnd(event.getPoint());

    //}
    //if(plugin_.isInsertNewTrackpointsActive() || plugin_.isModifyTrackpointsActive())
    //{
    	layer_.setMouseCurrent(event.getPoint());
      //System.out.println(event.getPoint());
    //}

	}

}