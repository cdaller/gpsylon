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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.util.*;

import javax.swing.*;

import org.dinopolis.gpstool.MapNavigationHook;
import org.dinopolis.gpstool.TrackManager;
import org.dinopolis.gpstool.gui.MouseMode;
import org.dinopolis.gpstool.plugin.GuiPlugin;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.track.*;
import org.dinopolis.util.*;
import org.dinopolis.util.gui.ActionStore;
import org.dinopolis.util.gui.MenuFactory;

import com.bbn.openmap.Layer;

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

public class ModifyTracksPlugin implements GuiPlugin//,ListSelectionListener
{
	/** the track manager to retrieve the information about maps from */
	protected TrackManager track_manager_;
	/** the layer to draw */
	protected ModifyTracksLayer layer_;
	protected JMenuItem frame_menu_item_;
	protected JMenu frame_menu_;
	/** the resources of the track plugin */
	protected Resources resources_;
	/** the resources of the GPSMap application */
	protected Resources application_resources_;

//	protected TrackChooser track_chooser_;

  protected PluginSupport plugin_support_;

  protected MapNavigationHook map_navigation_hook_;

	protected ActionStore action_store_;
	protected Action delete_trackpoints_;
	protected Action make_new_track_;
	protected Action add_new_trackpoint_;
	protected Action insert_new_trackpoint_;
	protected Frame main_frame_;

	/** the mouse modes */
	protected ModifyTracksMouseMode modifyer_mouse_mode_;
//	protected TrackChooserMouseMode chooser_mouse_mode_;


	protected Trackpoint rect_start_point_;
	protected Trackpoint rect_end_point_;

	protected boolean track_chooser_active_;
	protected boolean make_new_track_active_;
	protected boolean modify_trackpoints_active_;
	protected boolean insert_new_trackpoints_active_;
	protected boolean add_new_trackpoints_active_;
 	protected boolean remove_trackpoints_active_;

	protected Track active_track_;
	protected Track new_track_;



	// keys for resources:
	public static final String KEY_MODIFYTRACKS_PLUGIN_IDENTIFIER =
		"modifytracks.plugin.identifier";
	public static final String KEY_MODIFYTRACKS_PLUGIN_VERSION =
		"modifytracks.plugin.version";
	public static final String KEY_MODIFYTRACKS_PLUGIN_NAME =
		"modifytracks.plugin.name";
	public static final String KEY_MODIFYTRACKS_PLUGIN_DESCRIPTION =
		"modifytracks.plugin.description";
	public static final String KEY_MODIFYTRACKS_MSG_ENTER_TRACKNAME =
		"modifytracks.msg.enter_trackname";
	public static final String KEY_MODIFYTRACKS_MSG_DEFAULT_TRACKNAME =
		"modifytracks.msg.default_trackname";
	public static final String KEY_MODIFYTRACKS_MSG_CHOOS_TRACK =
		"modifytracks.msg.choos_track";
	public static final String KEY_MODIFYTRACKS_MSG_NO_ACTIVE_TRACK =
		"modifytracks.msg.no_active_track";
	public static final String KEY_MODIFYTRACKS_MSG_TRACK_INPUT_DIALOG =
		"modifytracks.msg.track_input_dialog";
	public static final String KEY_MODIFYTRACKS_MSG_ANOTHER_NAME =
		"modifytracks.msg.another_name";
	public static final String KEY_MODIFYTRACKS_MSG_IS_USED =
		"modifytracks.msg.is_used";
	public static final String KEY_MODIFYTRACKS_MSG_DELETE_TRACKPOINTS =
		"modifytracks.msg.delete.trackpoints";
	public static final String KEY_MODIFYTRACKS_MSG_DELETE_TRACKPOINTS_TITLE =
		"modifytracks.msg.delete.trackpoints.title";

	// menu actions
//  public final static String ACTION_TRACKCHOOSER_CHOOSE_TRACK_FRAME = "choose_track";
  public final static String ACTION_MAKE_NEW_TRACK = "make_new_track";
  public final static String ACTION_MODIFY_TRACKPOINTS = "modify_trackpoints";
  public final static String ACTION_INSERT_TRACKPOINTS = "insert_trackpoints";
  public final static String ACTION_ADD_TRACKPOINTS = "add_trackpoints";
  public final static String ACTION_DELETE_TRACKPOINTS = "delete_trackpoints";




	/** the name of the menu */
	public static final String KEY_MODIFYTRACKS_MENU_NAME = "modifytracks";

	/** the name of the resource file */
	private final static String RESOURCE_BUNDLE_NAME = "ModifyTracksPlugin";

	/** the name of the directory containing the resources */
	private final static String USER_RESOURCE_DIR_NAME = ".modifytracksplugin";

	public static final String MODIFYTRACKS_ACTION_STORE_ID = RESOURCE_BUNDLE_NAME;

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
		delete_trackpoints_ = new DeleteTrackpointsAction();
		make_new_track_ = new MakeNewTrackAction();
		add_new_trackpoint_ = new AddNewTrackpointsAction();
		insert_new_trackpoint_ = new InsertNewTrackpointsAction();

    // load modify tracks resources:
		if (Debug.DEBUG)
			Debug.println("modifytracksplugin_init", "loading resources");
		loadResources();

		// prepare the actionstore for the menu:

			action_store_ = ActionStore.getStore(MODIFYTRACKS_ACTION_STORE_ID);
    action_store_.addActions(new Action[] { insert_new_trackpoint_,
		                                       	add_new_trackpoint_,
		                                       	make_new_track_,
																						delete_trackpoints_
//																						new TrackChooserFrameAction()
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
	 	if(active)
	 	{
			delete_trackpoints_.setEnabled(true);
			make_new_track_.setEnabled(true);
			add_new_trackpoint_.setEnabled(true);
			insert_new_trackpoint_.setEnabled(true);
		}
		else
		{
			delete_trackpoints_.setEnabled(false);
			make_new_track_.setEnabled(false);
			add_new_trackpoint_.setEnabled(false);
			insert_new_trackpoint_.setEnabled(false);
			layer_.clearNearestTrackpoint();
	  }
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
		return ("Tracks Plugin");
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
		return ((float) resources_.getDouble(KEY_MODIFYTRACKS_PLUGIN_VERSION));
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
		return (resources_.getString(KEY_MODIFYTRACKS_PLUGIN_NAME));
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
		return (resources_.getString(KEY_MODIFYTRACKS_PLUGIN_DESCRIPTION));
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
					KEY_MODIFYTRACKS_MENU_NAME,
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
		if (modifyer_mouse_mode_ == null)
		{
			modifyer_mouse_mode_ = new ModifyTracksMouseMode();
			modifyer_mouse_mode_.initialize(resources_, (ModifyTracksLayer)getLayer(),this);
		}
		return (new MouseMode[] { modifyer_mouse_mode_});
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
			layer_ = new ModifyTracksLayer();
			layer_.initializePlugin(plugin_support_, resources_, this);
			KeyListener key_listener = new ModifyTracksKeyListener();
			layer_.addKeyListener(key_listener);
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
					ModifyTracksPlugin.class,
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


// 	//----------------------------------------------------------------------
// 	/**
// 	 * Selects the track which is choosen by the mouse in the track chooser list.
// 	 */

// 	public void setTrackSelection()
// 	{

// 		// find the choosen track
// 		Track choosen_track = findTrack();

// 		if(choosen_track != null)
// 		{
// 			String choosen_track_info = choosen_track.getIdentification();
// 			TrackInfoTable table = track_chooser_.getTrackInfoTable();
// 			TrackInfoHoldingTable table_model = (TrackInfoHoldingTable)table.getModel();
// 			int row;
// 			table.clearSelection();
// 			row = table_model.getRow(choosen_track_info);
// 			table.addRowSelectionInterval(row,row);
// 		}
// 	}

	//----------------------------------------------------------------------
	/**
	 * Finds a track whithin a rectangle, which is drawn by the use of the mouse,
	 * and checks if this track is in the track manager.
	 *
	 * @return track if the choosen track is in the track manager else return <Code> null </Code>.
	 */

	public Track findTrack()
	{
		rect_start_point_ = new TrackpointImpl();
		rect_end_point_ = new TrackpointImpl();

		// get start and end point of the rectangle
		rect_start_point_ = layer_.getMouseDragStart();
		rect_end_point_ = layer_.getMouseDragEnd();

		List tracks = track_manager_.getTracks();
		Iterator track_iterator = tracks.iterator();

		// check if there are trackpoints of existing tracks in the rectangel and return the track
		while(track_iterator.hasNext())
		{

			Track track = new TrackImpl();
			track = (Track) track_iterator.next();
			List trackpoints = track.getWaypoints();
    	Iterator point_iterator = trackpoints.iterator();
    	Trackpoint trackpoint;

      // goto first trackpoint:
      while(point_iterator.hasNext())
      {
      	trackpoint = (Trackpoint)point_iterator.next();

				if(rect_start_point_.getLatitude() < rect_end_point_.getLatitude())
	      {
	      	if(rect_start_point_.getLongitude() < rect_end_point_.getLongitude())
	      	{
	      	  if((trackpoint.getLatitude() >= rect_start_point_.getLatitude()) && (trackpoint.getLatitude() <= rect_end_point_.getLatitude()))
	      		{
	      	  	if((trackpoint.getLongitude() >= rect_start_point_.getLongitude()) && (trackpoint.getLongitude() <= rect_end_point_.getLongitude()))
	      			{
	      				return(track);
							}
						}
	      	}
	      	else
	      	{
	      	 if((trackpoint.getLatitude() >= rect_start_point_.getLatitude()) && (trackpoint.getLatitude() <= rect_end_point_.getLatitude()))
	      		{
	      	  	if((trackpoint.getLongitude() <= rect_start_point_.getLongitude()) && (trackpoint.getLongitude() >= rect_end_point_.getLongitude()))
	      			{
	      				return(track);
							}
	      		}
	      	}
	      }
	      else
	    	{
	    	if(rect_start_point_.getLongitude() < rect_end_point_.getLongitude())
	      	{
	      	  if((trackpoint.getLatitude() <= rect_start_point_.getLatitude()) && (trackpoint.getLatitude() >= rect_end_point_.getLatitude()))
	      		{
	      	  	if((trackpoint.getLongitude() >= rect_start_point_.getLongitude()) && (trackpoint.getLongitude() <= rect_end_point_.getLongitude()))
	      			{
	      				return(track);
							}
						}
	      	}
	      	else
	      	{
	      	 if((trackpoint.getLatitude() <= rect_start_point_.getLatitude()) && (trackpoint.getLatitude() >= rect_end_point_.getLatitude()))
	      		{
	      	  	if((trackpoint.getLongitude() <= rect_start_point_.getLongitude()) && (trackpoint.getLongitude() >= rect_end_point_.getLongitude()))
	      			{
	      				return(track);
							}
	      		}
					}
				}
      }
		}
		return(null);
 	}

//   //----------------------------------------------------------------------
// 	/**
// 	 * Called when the user selects or deselects a track in the trackchooser table.
// 	 *
// 	 * @param event the event
// 	 */

// 	public void valueChanged(ListSelectionEvent event)
// 	{
// 		if (Debug.DEBUG)
// 			Debug.println("TrackChooser_selection", "selection changed " + event);

// 		if (event.getValueIsAdjusting())
// 			return; // user not finished yet!


// 		TrackInfoTable table = track_chooser_.getTrackInfoTable();
// 		TrackInfoHoldingTable table_model = (TrackInfoHoldingTable)table.getModel();

// 		// find and store the selected track:
// 		int selected_row = table.getSelectedRow();
// 		String selected_track_info = new String();
// 		Track selected_track = new TrackImpl();

// 		if(selected_row != -1)
// 		{
// 			selected_track_info = table_model.getTrackInfo(selected_row);
//       selected_track = track_manager_.getTrack(selected_track_info);
// 			active_track_ = selected_track;
// 	    List trackpoints = selected_track.getWaypoints();
//   	  Iterator point_iterator = trackpoints.iterator();

// 			Trackpoint trackpoint;

// 			trackpoint = (Trackpoint)point_iterator.next();

// 			while(point_iterator.hasNext())
// 			{
// 				trackpoint = (Trackpoint)point_iterator.next();
// 			}

// 			layer_.setSelectedTrackActive(selected_track);

//       // switch the sight to the end point of the choosen track
// 			map_navigation_hook_.setMapCenter(trackpoint.getLatitude(),trackpoint.getLongitude());

// 		}

// 		layer_.doCalculation();
// 	}

	//----------------------------------------------------------------------
	/**
	 * Used to set the active track.
	 *
	 * @param active_track sets the active_track
	 */
	public void setNewTrack(Track new_track)
	{
			new_track_ = new_track;
	}


	//----------------------------------------------------------------------
	/**
	 * Used to set the active track.
	 *
	 * @param active_track sets the active_track
	 */
	public void setActiveTrack(Track active_track)
	{
			active_track_ = active_track;
	}

	//----------------------------------------------------------------------
	/**
	 * Used to get the active track
	 *
	 * @return active_track_ returns the active track
	 */
	public Track getActiveTrack()
	{
			return(active_track_);
	}

	//----------------------------------------------------------------------
	/**
	 * Used to get the active track
	 *
	 * @return active_track_ returns the active track
	 */
	public Track getNewTrack()
	{
			return(new_track_);
	}

  //----------------------------------------------------------------------
	/**
	 * Sets the active track <Code> null </Code>.
	 */
	public void clearNewTrack()
	{
			new_track_ = null;
	}

	//----------------------------------------------------------------------
	/**
	 * Sets the active track <Code> null </Code>.
	 */
	public void clearActiveTrack()
	{
			active_track_ = null;
	}

// 	//----------------------------------------------------------------------
// 	/**
// 	 * Sets the "track chooser mode" active and the other modes inactive.
// 	 *
// 	 * @param active is boolean
// 	 */
// 	public void setTrackChooserActive(boolean active)
// 	{
// 			track_chooser_active_ = active;

// 			// deactivate the other modes
// 			if(active)
// 			{
// 				chooser_mouse_mode_.setActive(true);
// 				modifyer_mouse_mode_.setActive(false);
// 				layer_.setActive(true);
// 				setMakeNewTrackActive(false);
// 				setModifyTrackpointsActive(false);
// 				setInsertNewTrackpointsActive(false);
// 				setAddNewTrackpointsActive(false);
// 				setRemoveTrackpointsActive(false);
// 			}
// 	}

// 	//----------------------------------------------------------------------
// 	/**
// 	 * Returns, if the "track chooser mode" is active or not.
// 	 *
// 	 * @return track_chooser_active_
// 	 */
// 	public boolean isTrackChooserActive()
// 	{
// 			return(track_chooser_active_);
// 	}

	//----------------------------------------------------------------------
	/**
	 * Sets the "make new tack" mode active and the other modes inactive.
	 *
	 * @param active is boolean
	 */
	public void setMakeNewTrackActive(boolean active)
	{
			make_new_track_active_ = active;
			// deactivate the other modes
			if(active)
			{
				modifyer_mouse_mode_.setActive(true);
				layer_.setActive(true);
//				setTrackChooserActive(false);
				setModifyTrackpointsActive(false);
				setInsertNewTrackpointsActive(false);
				setAddNewTrackpointsActive(false);
				setRemoveTrackpointsActive(false);
			}
	}

	//----------------------------------------------------------------------
	/**
	 * Returns, if the "make new track" mode is active or not.
	 *
	 * @return make_new_track_active_
	 */
	public boolean isMakeNewTrackActive()
	{
			return(make_new_track_active_);
	}

	//----------------------------------------------------------------------
	/**
	 * Sets the "modify trackpoints" mode active and the other modes inactive.
	 *
	 * @param active is boolean
	 */
	public void setModifyTrackpointsActive(boolean active)
	{
			modify_trackpoints_active_ = active;
			// deactivate the other menuepoints for the ModifiyTracksLayer
			if(active)
			{
				modifyer_mouse_mode_.setActive(true);
				layer_.setActive(true);
				//layer_.clearPointindex();
//				setTrackChooserActive(false);
				setMakeNewTrackActive(false);
				setInsertNewTrackpointsActive(false);
				setAddNewTrackpointsActive(false);
				setRemoveTrackpointsActive(false);
			}
	}

	//----------------------------------------------------------------------
	/**
	 * Returns, if the "modify trackpoints" mode is active or not.
	 *
	 * @return modify_trackpoints_active_
	 */
	public boolean isModifyTrackpointsActive()
	{
			return(modify_trackpoints_active_);
	}

	//----------------------------------------------------------------------
	/**
	 * Sets the "insert new trackpoints" mode active and the other modes inactive.
	 *
	 * @param active is boolean
	 */
	public void setInsertNewTrackpointsActive(boolean active)
	{
			insert_new_trackpoints_active_ = active;

			if(active)
			{
				modifyer_mouse_mode_.setActive(true);
				layer_.setActive(true);
//				setTrackChooserActive(false);
				setMakeNewTrackActive(false);
				setModifyTrackpointsActive(false);
				setAddNewTrackpointsActive(false);
				setRemoveTrackpointsActive(false);
			}
	}

	//----------------------------------------------------------------------
	/**
	 * Returns, if the "insert new trackpoints" mode is active or not.
	 *
	 * @return insert_new_trackpoints_active_
	 */
	public boolean isInsertNewTrackpointsActive()
	{
			return(insert_new_trackpoints_active_);
	}

	//----------------------------------------------------------------------
	/**
	 * Sets the "add new trackpoints" mode active and the other modes inactive.
	 *
	 * @param active is boolean
	 */
	public void setAddNewTrackpointsActive(boolean active)
	{
			add_new_trackpoints_active_ = active;

				if(active)
			{
				modifyer_mouse_mode_.setActive(true);
				layer_.setActive(true);
//				setTrackChooserActive(false);
				setMakeNewTrackActive(false);
				setModifyTrackpointsActive(false);
				setInsertNewTrackpointsActive(false);
				setRemoveTrackpointsActive(false);
			}

	}

	//----------------------------------------------------------------------
	/**
	 * Returns, if the "add new trackpoints" mode is active or not.
	 *
	 * @return add_new_trackpoints_active_
	 */
	public boolean isAddNewTrackpointsActive()
	{
			return(add_new_trackpoints_active_);
	}

	//----------------------------------------------------------------------
	/**
	 * Sets the "remove trackpoints mode" active and the other modes inactive.
	 *
	 * @param active is boolean
	 */
	public void setRemoveTrackpointsActive(boolean active)
	{
			remove_trackpoints_active_ = active;

				if(active)
			{
				modifyer_mouse_mode_.setActive(true);
				layer_.setActive(true);
//				setTrackChooserActive(false);
				setMakeNewTrackActive(false);
				setModifyTrackpointsActive(false);
				setInsertNewTrackpointsActive(false);
				setAddNewTrackpointsActive(false);
			}

	}

	//----------------------------------------------------------------------
	/**
	 * Returns, if the "add new trackpoints" mode is active or not.
	 *
	 * @return remove_trackpoints_active_
	 */
	public boolean isRemoveTrackpointsActive()
	{
			return(remove_trackpoints_active_);
	}

  //----------------------------------------------------------------------
	/**
	 * Returns, the confirmation to delete the choosen trackpoints.
	 *
	 * @return confirmation if deletion is ok then <Code> true </Code> else <Code> false </Code>
	 */

  public boolean showDeleteConfirmMessage()
  {
	 	JOptionPane optionPane = new JOptionPane();
	 	JFrame dialog_frame = new JFrame();

		int id = (int)JOptionPane.showConfirmDialog(
		 																							dialog_frame,
																									resources_.getString(KEY_MODIFYTRACKS_MSG_DELETE_TRACKPOINTS),
         	                     									  resources_.getString(KEY_MODIFYTRACKS_MSG_DELETE_TRACKPOINTS_TITLE),
         	                     									  JOptionPane.YES_NO_OPTION
																							);
		if(id == 0)
		  return(true);
  	else
			return(false);
	}





	//----------------------------------------------------------------------
	// inner classes
	//----------------------------------------------------------------------

//   //----------------------------------------------------------------------
//   /**
// 	 * Action for the track chooser
//    */

//   class TrackChooserFrameAction extends AbstractAction
//   {

// 		//----------------------------------------------------------------------
//     /**
//      * The Default Constructor.
//      */

// 		public TrackChooserFrameAction()
//     {
//       super(ACTION_TRACKCHOOSER_CHOOSE_TRACK_FRAME);
//     }

// 		//----------------------------------------------------------------------
//     /**
//      * Makes a new track chooser and initialize it
//      *
//      * @param event the action event
//      */
//     public void actionPerformed(ActionEvent event)
//     {
//      	  if(track_chooser_ == null)
// 					track_chooser_ = new TrackChooser();

// 				setTrackChooserActive(true);
// 		 	  track_chooser_.Initialize(track_manager_,resources_, ModifyTracksPlugin.this);

// 		}
// 	}

  //----------------------------------------------------------------------
  /**
	 * Make new track actions
   */

	class MakeNewTrackAction extends AbstractAction
  {

    //----------------------------------------------------------------------
    /**
     * The Default Constructor.
     */

    public MakeNewTrackAction()
    {
      super(ACTION_MAKE_NEW_TRACK);
    }

		//----------------------------------------------------------------------
    /**
     * Makes a new panel to input the new track name and sets the make
		 * new track mode <Code> true </Code>
     *
     * @param event the action event
     */

		public void actionPerformed(ActionEvent event)
    {
		 	JOptionPane optionPane = new JOptionPane();
		 	JFrame dialog_frame = new JFrame();

			int track_number = track_manager_.getTracks().size();

			String todo_msg = new String(resources_.getString(KEY_MODIFYTRACKS_MSG_ENTER_TRACKNAME));
			String default_track_name	= new String(resources_.getString(KEY_MODIFYTRACKS_MSG_DEFAULT_TRACKNAME)+ "_" + track_number);
			String empty_id = "";
			String id = "";

			while(id.equals(empty_id))
			{
         id = (String)JOptionPane.showInputDialog(
		 																							dialog_frame,
																									todo_msg,
		 																							resources_.getString(KEY_MODIFYTRACKS_MSG_TRACK_INPUT_DIALOG),
		 																							JOptionPane.PLAIN_MESSAGE,
		 																							null,
		 																							null,
		 																							default_track_name
         	                     									);

				if(id == null)
					break;

				List tracks = track_manager_.getTracks();
				Iterator track_iterator = tracks.iterator();

				while(track_iterator.hasNext())
				{
					Track track = (Track)track_iterator.next();
						//System.out.println("Track ID: "+track.getIdentification());
						//System.out.println("ID: "+id);
					if(id.equals(track.getIdentification()))
					{
						todo_msg = new String(resources_.getString(KEY_MODIFYTRACKS_MSG_ANOTHER_NAME)+", **"+id+"** "+resources_.getString(KEY_MODIFYTRACKS_MSG_IS_USED));
						default_track_name = new String(id);
						id = empty_id;
					}
				}

			}

			if(id != null)
			{
     		new_track_ = new TrackImpl();
				new_track_.setIdentification(id);
				setMakeNewTrackActive(true);
			}
		}
	}



  //----------------------------------------------------------------------
  /**
	 * Insert new trackpoints actions
   */


	class InsertNewTrackpointsAction extends AbstractAction
  {

		//----------------------------------------------------------------------
    /**
     * The Default Constructor.
     */

    public InsertNewTrackpointsAction()
    {
      super(ACTION_INSERT_TRACKPOINTS);
    }

		//----------------------------------------------------------------------
    /**
     * Makes a new panel to check if a track is choosen and sets the insert
		 * trackpoints mode <Code> true </Code>
     *
     * @param event the action event
     */

		public void actionPerformed(ActionEvent event)
    {


			/*
			if(getActiveTrack() == null)
			{
				JOptionPane optionPane = new JOptionPane();
		 		JFrame dialog_frame = new JFrame();

    		optionPane.showMessageDialog (	dialog_frame,
																				new String(resources_.getString(KEY_MODIFYTRACKS_MSG_CHOOS_TRACK)),
																				resources_.getString(KEY_MODIFYTRACKS_MSG_NO_ACTIVE_TRACK),
																				JOptionPane.INFORMATION_MESSAGE
                     									);

		 	}
			else */
			setInsertNewTrackpointsActive(true);
    }
	}

  //----------------------------------------------------------------------
  /**
	 * Add new trackpoints actions
   */

	class AddNewTrackpointsAction extends AbstractAction
  {

		//----------------------------------------------------------------------
    /**
     * The Default Constructor.
     */
	  public AddNewTrackpointsAction()
    {
      super(ACTION_ADD_TRACKPOINTS);
    }

		//----------------------------------------------------------------------
    /**
     * Makes a new panel to check if a track is choosen and sets the add
		 * trackpoints mode <Code> true </Code>
     *
     * @param event the action event
     */

		public void actionPerformed(ActionEvent event)
    {
				setAddNewTrackpointsActive(true);
				layer_.repaint();
    }
  }

  //----------------------------------------------------------------------
  /**
	 * Remove trackpoints actions
   */

  class DeleteTrackpointsAction extends AbstractAction
  {

		//----------------------------------------------------------------------
    /**
     * The Default Constructor.
     */

    public DeleteTrackpointsAction()
    {
      super(ACTION_DELETE_TRACKPOINTS);
    }

    //----------------------------------------------------------------------
    /**
     * Sets the menue point enabled.
     */

    public void setEnable(boolean enabled)
    {
      this.setEnabled(enabled);
    }

		//----------------------------------------------------------------------
    /**
     * Makes a new panel to check if a track is choosen and sets the remove
		 * trackpoints mode <Code> true </Code>
     *
     * @param event the action event
     */

		public void actionPerformed(ActionEvent event)
    {
		  layer_.deleteTrackpoints();
	  }
	}
}
