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

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;

import javax.swing.*;

import org.dinopolis.gpstool.MapNavigationHook;
import org.dinopolis.gpstool.TrackManager;
import org.dinopolis.gpstool.gui.MouseMode;
import org.dinopolis.gpstool.plugin.GuiPlugin;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.track.Trackpoint;
import org.dinopolis.util.*;
import org.dinopolis.util.gui.ActionStore;
import org.dinopolis.util.gui.MenuFactory;

import com.bbn.openmap.Layer;

//----------------------------------------------------------------------
/**
 * This plugin allows the user to define graphs, made of nodes and edges.
 * Further the user is able to name the nodes and edges to use this information
 * for routing. It is also possible to store the graph in a graphml format
 * on the harddisk to use this information on other systems.
 *
 * @author Wolfgang Auer
 * @version $Revision$
 */

public class GraphPlugin implements GuiPlugin
{
	/** the object in which the graph is stored */
	protected Graph graph_;

	/** the track manager to retrieve the information about maps from */
	protected TrackManager track_manager_;
	/** the layer to draw */
	protected GraphLayer layer_;
	protected JMenuItem frame_menu_item_;
	protected JMenu frame_menu_;
	/** the resources of the graph plugin */
	protected Resources resources_;
	/** the resources of the GPSMap application */
	protected Resources application_resources_;

//	protected TrackChooser track_chooser_;

  protected PluginSupport plugin_support_;

  protected MapNavigationHook map_navigation_hook_;

	protected ActionStore action_store_;

	/** the mouse modes */
	protected GraphMouseMode graph_mouse_mode_;

	protected Trackpoint rect_start_point_;
	protected Trackpoint rect_end_point_;

	protected JFileChooser file_chooser_;

	protected boolean set_edges_active_;
	protected boolean add_nodes_active_;
	protected boolean modify_trackpoints_active_;
	protected boolean insert_new_trackpoints_active_;
	protected boolean add_new_trackpoints_active_;
 	protected boolean calc_shortest_path_active_;

	protected Edge current_edge_;
  protected JFrame main_frame_;

 	protected LoadGraphAction load_graph_action = new LoadGraphAction();
	protected SaveGraphAction save_graph_action = new SaveGraphAction();
  protected AddNodesAction add_nodes_action = new AddNodesAction();
	protected	SetEdgesAction	set_edges_action = new SetEdgesAction();
	protected	DeleteEdgesAction delete_edges_action = new DeleteEdgesAction();
	protected CalculateShortestPathAction calculate_shortest_path_action = new CalculateShortestPathAction();

	// keys for resources:
	public static final String KEY_GRAPH_PLUGIN_IDENTIFIER =
		"graph.plugin.identifier";
	public static final String KEY_GRAPH_PLUGIN_VERSION =
		"graph.plugin.version";
	public static final String KEY_GRAPH_PLUGIN_NAME =
		"graph.plugin.name";
	public static final String KEY_GRAPH_PLUGIN_DESCRIPTION =
		"graph.plugin.description";
	public static final String KEY_GRAPH_MSG_ENTER_GRAPHNAME =
		"graph.msg.enter_graphname";
	public static final String KEY_GRAPH_MSG_ENTER_NODENAME =
		"graph.msg.enter_nodename";
	public static final String KEY_GRAPH_MSG_ENTER_EDGENAME =
		"graph.msg.enter_edgename";
	public static final String KEY_GRAPH_MSG_DEFAULT_GRAPHNAME =
		"graph.msg.default_graphname";
	public static final String KEY_GRAPH_MSG_DEFAULT_NODENAME =
		"graph.msg.default_nodename";
	public static final String KEY_GRAPH_MSG_DEFAULT_EDGENAME =
		"graph.msg.default_edgename";
	public static final String KEY_GRAPH_MSG_ACCEPT_EDGEPOINTS =
		"graph.msg.accept_edgepoints";
	public static final String KEY_GRAPH_MSG_ACCEPT_EDGEPOINTS_TITLE =
		"graph.msg.accept_edgepoints_title";
	public static final String KEY_GRAPH_MSG_GRAPH_INPUT_DIALOG =
		"graph.msg.graph_input_dialog";
	public static final String KEY_GRAPH_MSG_NODE_INPUT_DIALOG =
		"graph.msg.node_input_dialog";
	public static final String KEY_GRAPH_MSG_EDGE_INPUT_DIALOG =
		"graph.msg.edge_input_dialog";
	public static final String KEY_GRAPH_MSG_SELECTED_START_NODE_OK =
		"graph.msg.selectet_start_node_ok";
	public static final String KEY_GRAPH_MSG_SELECTED_END_NODE_OK =
		"graph.msg.selectet_end_node_ok";
	public static final String KEY_GRAPH_MSG_SELECTED_NODE_OK_TITLE =
		"graph.msg.selected_node_ok_title";
	public static final String KEY_GRAPH_MSG_NODES_NOT_ON_SAME_TRACK =
		"graph.msg.nodes_not_on_same_track";
	public static final String KEY_GRAPH_MSG_NODES_NOT_ON_SAME_TRACK_TITLE =
		"graph.msg.nodes_not_on_same_track_title";
  public static final String KEY_GRAPH_MSG_NO_GRAPH =
		"graph.msg.no_graph";
  public static final String KEY_GRAPH_MSG_NO_GRAPH_TITLE =
		"graph.msg.no_graph_title";

	// menu actions
	public final static String ACTION_LOAD_GRAPH = "load_graph";
  public final static String ACTION_SAVE_GRAPH = "save_graph";
  public final static String ACTION_ADD_NODES = "add_nodes";
  public final static String ACTION_SET_EDGES = "set_edges";
  public final static String ACTION_DELETE_EDGES = "delete_edges";
	public final static String ACTION_CALC_SHORTEST_PATH = "clac_shortest_path";



	/** the name of the menu */
	public static final String KEY_GRAPH_MENU_NAME = "graph";

	/** the name of the resource file */
	private final static String RESOURCE_BUNDLE_NAME = "GraphPlugin";

	/** the name of the directory containing the resources */
	private final static String USER_RESOURCE_DIR_NAME = ".graphplugin";

	public static final String GRAPH_ACTION_STORE_ID = RESOURCE_BUNDLE_NAME;

	// ----------------------------------------------------------------------
	// Implementation of org.dinopolis.gpstool.plugin.Plugin
	// ----------------------------------------------------------------------

	//----------------------------------------------------------------------
	/**
	 * Initialize the plugin and pass a PluginSupport that provides
	 * objects, the plugin may use.
	 *
	 * @param support the PluginSupport object
	 */
	public void initializePlugin(PluginSupport support)
	{
		plugin_support_ = support;
		track_manager_ = support.getTrackManager();
		map_navigation_hook_ = support.getMapNavigationHook();
		application_resources_ = support.getResources();

    // load modify tracks resources:
		if (Debug.DEBUG)
			Debug.println("graphplugin_init", "loading resources");
		loadResources();

		// prepare the actionstore for the menu:

    action_store_ = ActionStore.getStore(GRAPH_ACTION_STORE_ID);
    action_store_.addActions(new Action[] {
																						load_graph_action,
																						save_graph_action,
		                                       	add_nodes_action,
																					  set_edges_action,
																					  delete_edges_action,
																					  calculate_shortest_path_action
		                                       //	new InsertNewTrackpointsAction(),
		                                       // new AddNewTrackpointsAction(),
		                                       //	new RemoveTrackpointsAction()
																					});
	}


	//----------------------------------------------------------------------
	/**
	 * This methode is called to enabel or disable the menue
	 *
	 * @param boolean active
	 */

	public void enablePlugin(boolean active)
	{
		load_graph_action.setEnabled(active);
		save_graph_action.setEnabled(active);
  	add_nodes_action.setEnabled(active);
	 	set_edges_action.setEnabled(active);
	 	delete_edges_action.setEnabled(active);
	 	calculate_shortest_path_action.setEnabled(active);

		if(!active)
			layer_.reset();
 	}

 	//----------------------------------------------------------------------
	/**
	 * The application calls this method to indicate that the plugin is
	 * activated and will be used from now on. The Plugin should
	 * initialize any needed resources (files, etc.) in this method.
	 *
	 * @throws Exception if an error occurs. If this method throws an
	 * exception, the plugin will not be used by the application.
	 */

	public void startPlugin() throws Exception
	{
	}

	//----------------------------------------------------------------------
	/**
	 * The application calls this method to indicate that the plugin is
	 * deactivated and will not be used any more. The Plugin should
	 * release all resources (close files, etc.) in this method.
	 *
	 * @throws Exception if an error occurs.
	 */

	public void stopPlugin() throws Exception
	{
		boolean store_resources = false;
		// save window locaton and dimensions:

    /*if (resources_.getBoolean(KEY_MAPMANAGER_WINDOW_REMEMBER_SETTINGS)
			&& (main_frame_ != null))
		{
			Point location = main_frame_.getLocationOnScreen();
			Dimension dimension = main_frame_.getSize();
			resources_.setInt(KEY_MAPMANAGER_WINDOW_LOCATION_X, location.x);
			resources_.setInt(KEY_MAPMANAGER_WINDOW_LOCATION_Y, location.y);
			resources_.setInt(KEY_MAPMANAGER_WINDOW_DIMENSION_WIDTH, dimension.width);
			resources_.setInt(
				KEY_MAPMANAGER_WINDOW_DIMENSION_HEIGHT,
				dimension.height);
			store_resources = true;
		} */

		if (store_resources)
			resources_.store();

	}

	//----------------------------------------------------------------------
	/**
	 * Returns the unique id of the plugin. The id is used to identify
	 * the plugin and to distinguish it from other plugins.
	 *
	 * @return The id of the plugin.
	 */

	public String getPluginIdentifier()
	{
		return ("Graph Plugin");
	}

	//----------------------------------------------------------------------
	/**
	 * Returns the version of the plugin. The version may be used to
	 * choose between different version of the same plugin.
	 *
	 * @return The version of the plugin.
	 */

	public float getPluginVersion()
	{
		return ((float) resources_.getDouble(KEY_GRAPH_PLUGIN_VERSION));
	}

	//----------------------------------------------------------------------
	/**
	 * Returns the name of the Plugin. The name should be a human
	 * readable and understandable name like "Save Image as JPEG". It is
	 * prefereable but not necessary that the name is localized.
	 *
	 * @return The name of the plugin.
	 */

	public String getPluginName()
	{
		return (resources_.getString(KEY_GRAPH_PLUGIN_NAME));
	}

	//----------------------------------------------------------------------
	/**
	 * Returns a description of the Plugin. The description should be
	 * human readable and understandable like "This plugin saves the
	 * content of the main window as an image in jpeg format". It is
	 * prefereable but not necessary that the description is localized.
	 *
	 * @return The description of the plugin.
	 */

	public String getPluginDescription()
	{
		return (resources_.getString(KEY_GRAPH_PLUGIN_DESCRIPTION));
	}

	//----------------------------------------------------------------------
	// GuiPlugin methods
	//----------------------------------------------------------------------

	//----------------------------------------------------------------------
	/**
	 * The plugin may return a JMenu object to be used in the main menu of
	 * the application and may (should) contain other menu items. The
	 * menuitems returned should provide an icon, a mnemonic key, and a
	 * localized name (and a accelerator key).
	 *
	 * @return A menu that is used in the main menu in the
	 * application or <code>null</code>, if no main menu is needed.
	 *
	 */

	public JMenu getMainMenu()
	{
		if (frame_menu_ == null)
		{
			frame_menu_ =
				(JMenu) MenuFactory.createMenu(
					MenuFactory.KEY_MENUE_PREFIX,
					KEY_GRAPH_MENU_NAME,
					resources_,
					action_store_);
		}
		return (frame_menu_);

	}

	//----------------------------------------------------------------------
	/**
	 * The application provides a sub menu for every plugin that may be
	 * used. The JMenuItem (or JMenu) returned is added to a submenu in
	 * the "plugins" menu item.  The menuitems returned should provide an
	 * icon, a mnemonic key, and a localized name (and a accelerator key).
	 *
	 * @return A menuitem (or a JMenu) that are used in a sub menu in the
	 * application or <code>null</code>, if no submenus are needed.
	 *
	 */

	public JMenuItem getSubMenu()
	{
    return (null);
  }

	//----------------------------------------------------------------------
	/**
	 * Every plugin may provide one or more mouse modes. These mouse modes
	 * may react on mouse clicks, drags, etc.
	 *
	 * @return mouse modes that are used by this plugin in the application or
	 * <code>null</code>, if no mouse modes are used.
	 *
	 */
	public MouseMode[] getMouseModes()
	{
		if (graph_mouse_mode_ == null)
		{
			graph_mouse_mode_ = new GraphMouseMode();
			graph_mouse_mode_.initialize(resources_, (GraphLayer)getLayer(),this);
		}
		return (new MouseMode[] { graph_mouse_mode_});
	}

	//----------------------------------------------------------------------
	/**
	 * If the plugin wants to draw anything on the map it may
	 * return a layer here or <code>null</code> if not.
	 *
	 * @return the layer the plugin wants to paint into.
	 * @see com.bbn.openmap.Layer
	 */

	public Layer getLayer()
	{
		if (layer_ == null)
		{
			layer_ = new GraphLayer();
			layer_.initializePlugin(plugin_support_, this);
		}
		return (layer_);
	}

	//----------------------------------------------------------------------
	/**
	 * Called by the application to switch the layer on or off. If the
	 * layer is switched off, it must not paint anything and should not
	 * consume any calculational power.
	 *
	 * @param active if <code>true</code> the layer is switched on and
	 * should react on changes of the projection and draw anything in the
	 * paintComponent method.
	 */
	public void setActive(boolean active)
	{
		layer_.setActive(active);
	}

	//----------------------------------------------------------------------
	/**
	 * Returns if the plugin is active or not.
	 *
	 * @return <code>true</code> if the plugin is active and paints
	 * something.
	 */
	public boolean isActive()
	{
		return (layer_.isActive());
	}

	//----------------------------------------------------------------------
	// other methods
	//----------------------------------------------------------------------

	//----------------------------------------------------------------------
	/**
	 * Loads the resource file, or exits on a MissingResourceException.
	 */

	void loadResources()
	{
		try
		{
			resources_ =
				ResourceManager.getResources(
					GraphPlugin.class,
					RESOURCE_BUNDLE_NAME,
					USER_RESOURCE_DIR_NAME,
					Locale.getDefault());
		}
		catch (MissingResourceException mre)
		{
			if (Debug.DEBUG)
				Debug.println(
					"ModifiyTracksPlugin",
					mre.toString() + '\n' + Debug.getStackTrace(mre));
			System.err.println(
				"ModifyTracksPlugin: resource file '"
					+ RESOURCE_BUNDLE_NAME
					+ "' not found");
			System.err.println(
				"please make sure that this file is within the classpath !");
			System.exit(1);
		}
	}

	//----------------------------------------------------------------------
	/**
	 * Returns the resources of the plugin
	 * @return resources
	 */

	public Resources getResources()
	{
		return resources_;
	}

  	//----------------------------------------------------------------------
	/**
	 * Returns the resources of the plugin
	 * @return resources
	 */

	public Edge getCurrentEdge()
	{
		return current_edge_;
	}
	//----------------------------------------------------------------------
	/**
	 * Used to set the graph.
	 *
	 * @param graph sets the graph_
	 */
	public void setGraph(Graph graph)
	{
			graph_ = graph;
	}

	//----------------------------------------------------------------------
	/**
	 * Used to get the graph
	 *
	 * @return graph_ returns the graph
	 */
	public Graph getGraph()
	{
			return(graph_);
	}



  //----------------------------------------------------------------------
	/**
	 * Schows that the nodes are not on the same track.
	 *
	 */

  public void showNodesNotOnSameTrackMessage()
  {
	 	JOptionPane optionPane = new JOptionPane();
	 	JFrame dialog_frame = new JFrame();

		JOptionPane.showMessageDialog(
 																	dialog_frame,
																	resources_.getString(KEY_GRAPH_MSG_NODES_NOT_ON_SAME_TRACK),
               									  resources_.getString(KEY_GRAPH_MSG_NODES_NOT_ON_SAME_TRACK_TITLE),
               									  JOptionPane.ERROR_MESSAGE
																);


	}


	//----------------------------------------------------------------------
	/**
	 * Sets the "make new graph mode" active and the other modes inactive.
	 *
	 * @param active is boolean
	 */
	public void setAddNodesActive(boolean active)
	{

			add_nodes_active_ = active;

			// deactivate the other modes

			if(active)
			{
				setSetEdgesActive(false);
			  setCalcShortestPathActive(false);
			}
	}

	//----------------------------------------------------------------------
	/**
	 * Returns, the status of the "make new graph mode" active or not.
	 *
	 * @return make_new_graph_active_ is boolean
	 */
	public boolean isAddNodesActive()
	{
			return(add_nodes_active_);
	}

  //----------------------------------------------------------------------
	/**
	 * Sets the "add edges mode" active and the other modes inactive.
	 *
	 * @param active is boolean
	 */
	public void setSetEdgesActive(boolean active)
	{
			set_edges_active_ = active;

			// deactivate the other modes

			if(active)
			{
				setAddNodesActive(false);
			  setCalcShortestPathActive(false);
			}
	}

	//----------------------------------------------------------------------
	/**
	 * Returns, the status of the "add edges mode" active or not.
	 *
	 * @return add_edges_active_ is boolean
	 */
	public boolean isSetEdgesActive()
	{
			return(set_edges_active_);
	}

  //----------------------------------------------------------------------
	/**
	 * Sets the "add edges mode" active and the other modes inactive.
	 *
	 * @param active is boolean
	 */
	public void setCalcShortestPathActive(boolean active)
	{
			calc_shortest_path_active_ = active;

			// deactivate the other modes

			if(active)
			{
				setAddNodesActive(false);
				setSetEdgesActive(false);
			}
	}

	//----------------------------------------------------------------------
	/**
	 * Returns, the status of the "add edges mode" active or not.
	 *
	 * @return add_edges_active_ is boolean
	 */
	public boolean isCalcShortestPathActive()
	{
			return(calc_shortest_path_active_);
	}


	//----------------------------------------------------------------------
	// inner classes
	//----------------------------------------------------------------------

	//----------------------------------------------------------------------
  /**
	 * Action to load a graph
   */

  class LoadGraphAction extends AbstractAction
  {

		//----------------------------------------------------------------------
    /**
     * The Default Constructor.
     */

		public LoadGraphAction()
    {
      super(ACTION_LOAD_GRAPH);
    }

		//----------------------------------------------------------------------
    /**
     * Opens a dialog to save the graph
     *
     * @param event the action event
     */
    public void actionPerformed(ActionEvent event)
    {
     	file_chooser_ = new JFileChooser();
      file_chooser_.setAcceptAllFileFilterUsed(false);
      file_chooser_.setMultiSelectionEnabled(false);
      file_chooser_.setFileHidingEnabled(false);


      int result = file_chooser_.showSaveDialog(null);
      if(result == JFileChooser.APPROVE_OPTION)
      {

        //ExtensionFileFilter filter = (ExtensionFileFilter)file_chooser_.getFileFilter();
        File file_name = file_chooser_.getSelectedFile();
        GraphFileReader graph_file_reader = new GraphFileReader();
				graph_ = graph_file_reader.parseFile(file_name);
				layer_.reset();
				layer_.repaint();
			}
		}
	}

  //----------------------------------------------------------------------
  /**
	 * Action to save the choosen graph
   */

  class SaveGraphAction extends AbstractAction
  {

		//----------------------------------------------------------------------
    /**
     * The Default Constructor.
     */

		public SaveGraphAction()
    {
      super(ACTION_SAVE_GRAPH);
    }

		//----------------------------------------------------------------------
    /**
     * Opens a dialog to save the graph
     *
     * @param event the action event
     */
    public void actionPerformed(ActionEvent event)
    {
     	file_chooser_ = new JFileChooser();
      file_chooser_.setAcceptAllFileFilterUsed(false);
      file_chooser_.setMultiSelectionEnabled(false);
      file_chooser_.setFileHidingEnabled(false);

			if(graph_ != null)
			{
	      int result = file_chooser_.showSaveDialog(null);
	      if(result == JFileChooser.APPROVE_OPTION)
	      {

	          //ExtensionFileFilter filter = (ExtensionFileFilter)file_chooser_.getFileFilter();
	          File file_name = file_chooser_.getSelectedFile();
	         	//System.out.println(file);
	         	GraphFileWriter graph_writer = new GraphFileWriter(file_name,graph_);
						try
						{
						 graph_writer.write();
						}
						catch(IOException e)
						{
							System.out.println("IO Exception");
						}
				}
			}
		}
	}

  //----------------------------------------------------------------------
  /**
	 * Add new nodes
   */

	class AddNodesAction extends AbstractAction
  {

    //----------------------------------------------------------------------
    /**
     * The Default Constructor.
     */

    public AddNodesAction()
    {
      super(ACTION_ADD_NODES);
    }

		//----------------------------------------------------------------------
    /**
     * Controls the dialogs to enter a new node and sets the add node mode
     * to <Code> true </Code>
     *
     * @param event the action event
     */

		public void actionPerformed(ActionEvent event)
    {
			layer_.reset();
			if(graph_ != null)
				setAddNodesActive(true);
			else
			{
				graph_ = new Graph();
				setAddNodesActive(true);
			}
		}
	}

	//----------------------------------------------------------------------
  /**
	 * Set new edges
   */

	class SetEdgesAction extends AbstractAction
  {

    //----------------------------------------------------------------------
    /**
     * The Default Constructor.
     */

    public SetEdgesAction()
    {
      super(ACTION_SET_EDGES);
    }

		//----------------------------------------------------------------------
    /**
     * Controls the dialogs to enter a new edge and sets the add eges mode
     * to <Code> true </Code>
     *
     * @param event the action event
     */

		public void actionPerformed(ActionEvent event)
    {
			layer_.reset();
			setSetEdgesActive(true);
		}
	}

	//----------------------------------------------------------------------
  /**
	 * Delete edges
   */

	class DeleteEdgesAction extends AbstractAction
  {

    //----------------------------------------------------------------------
    /**
     * The Default Constructor.
     */

    public DeleteEdgesAction()
    {
      super(ACTION_DELETE_EDGES);
    }

		//----------------------------------------------------------------------
    /**
     * Controls the dialogs to enter a new edge and sets the add eges mode
     * to <Code> true </Code>
     *
     * @param event the action event
     */

		public void actionPerformed(ActionEvent event)
    {
			layer_.deleteSelectedParts();
			//System.out.println("DELETE");
		}
	}





	//----------------------------------------------------------------------
  /**
	 * Calculate shortest path
   */

	class CalculateShortestPathAction extends AbstractAction
  {

    //----------------------------------------------------------------------
    /**
     * The Default Constructor.
     */

    public CalculateShortestPathAction()
    {
      super(ACTION_CALC_SHORTEST_PATH);
    }

		//----------------------------------------------------------------------
    /**
     * Controls the dialogs to enter a new edge and sets the add eges mode
     * to <Code> true </Code>
     *
     * @param event the action event
     */

		public void actionPerformed(ActionEvent event)
    {
			layer_.reset();
			setCalcShortestPathActive(true);
		}
	}
}
