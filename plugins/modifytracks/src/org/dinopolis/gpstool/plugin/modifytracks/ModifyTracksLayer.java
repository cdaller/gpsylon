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


import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import org.dinopolis.gpstool.*;
import org.dinopolis.gpstool.gui.util.BasicLayer;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.track.*;
import org.dinopolis.gpstool.util.geoscreen.GeoScreenPoint;
import org.dinopolis.util.Debug;
import org.dinopolis.util.Resources;



//----------------------------------------------------------------------
/**
* This class provides a plugin that allows the user to modify tracks.
*
* @author Wolfgang Auer
* @version $Revision$
*/

public class ModifyTracksLayer extends BasicLayer
  implements ActionListener, FocusListener,GPSMapKeyConstants
{

  GeoScreenPoint mouse_drag_start_;
  GeoScreenPoint mouse_drag_end_;
  Trackpoint mouse_current_ = new TrackpointImpl();
  Trackpoint cross_point_;
  Trackpoint trackpoint_after_crosspoint_;
  Trackpoint nearest_trackpoint_;
  List marked_trackpoints_;
	Resources resources_;
  Track active_track_;
  Track new_track_;
  MapNavigationHook map_nav_hook_;
	BasicStroke active_track_stroke_ = new BasicStroke(2);
  TrackManager track_manager_;
  ModifyTracksPlugin plugin_;
  List choosen_trackpoints_;

	String track_to_modify_;
  int trackpoint_to_modify_;

  int draw_rect_start_x_;
  int draw_rect_start_y_;
	int draw_rect_width_;
  int draw_rect_height_;

  boolean mouse_mode_active_ = false;
  boolean draw_track_ = false;
  boolean draw_rect_ = false;
	boolean modify_tracks_mm_active_ = false;
	boolean track_chooser_mm_active_ = false;
	boolean add_trackpoint_active_ = false;
	boolean draw_preview_ = false;

	BasicStroke visible_tracks_line_stroke_;
	BasicStroke marked_trackpoints_line_stroke_;
	BasicStroke modify_trackpoint_preview_line_stroke_;
	BasicStroke add_trackpoint_preview_line_stroke_;
	BasicStroke cross_line_stroke_;

	Color visible_tracks_color_;
	Color marked_trackpoints_color_;
	Color modify_trackpoint_preview_color_;
	Color add_trackpoint_preview_color_;
	Color cross_color_;

  public static final String KEY_TRACK_VISIBLE_TRACKS_LINE_WIDTH = "track.visible_tracks.line.width";
  public static final String KEY_TRACK_VISIBLE_TRACKS_COLOR = "track.visible_tracks.color";
  public static final String KEY_TRACK_MARKED_TRACKPOINTS_LINE_WIDTH = "track.marked_trackpoints.line.width";
  public static final String KEY_TRACK_MARKED_TRACKPOINTS_COLOR = "track.marked_trackpoints.color";
  public static final String KEY_TRACK_MODIFY_TRACKPOINT_PREVIEW_LINE_WIDTH = "track.modify_trackpoint_preview.line.width";
  public static final String KEY_TRACK_MODIFY_TRACKPOINT_PREVIEW_COLOR = "track.modify_trackpoint_preview.color";
  public static final String KEY_TRACK_ADD_TRACKPOINT_PREVIEW_LINE_WIDTH = "track.add_trackpoint_preview.line.width";
  public static final String KEY_TRACK_ADD_TRACKPOINT_PREVIEW_COLOR = "track.add_trackpoint_preview.color";
  public static final String KEY_CROSS_LINE_WIDTH = "track.cross.line.width";
  public static final String KEY_CROSS_COLOR = "track.cross.color";

//----------------------------------------------------------------------
/**
* Default Constructor
*/
  public ModifyTracksLayer()
  {

	}

//----------------------------------------------------------------------
/**
* Initialize with all it needs.
* TODO: use my own resources
*
* @param support a plugin support object
* @param plugin the plugin itself
*/
  public void initializePlugin(PluginSupport support,Resources resources, ModifyTracksPlugin plugin)
  {
    track_manager_ = support.getTrackManager();
    resources_ = support.getResources();
    map_nav_hook_ = support.getMapNavigationHook();
		plugin_ = plugin;
		marked_trackpoints_ = new ArrayList();
		visible_tracks_color_ = resources.getColor(KEY_TRACK_VISIBLE_TRACKS_COLOR);
    visible_tracks_line_stroke_ = new BasicStroke((float)resources.getDouble(KEY_TRACK_VISIBLE_TRACKS_LINE_WIDTH));
    marked_trackpoints_color_ = resources.getColor(KEY_TRACK_MARKED_TRACKPOINTS_COLOR);
    marked_trackpoints_line_stroke_ = new BasicStroke((float)resources.getDouble(KEY_TRACK_MARKED_TRACKPOINTS_LINE_WIDTH));
    modify_trackpoint_preview_color_ = resources.getColor(KEY_TRACK_MODIFY_TRACKPOINT_PREVIEW_COLOR);
    modify_trackpoint_preview_line_stroke_ = new BasicStroke((float)resources.getDouble(KEY_TRACK_MODIFY_TRACKPOINT_PREVIEW_LINE_WIDTH));
		add_trackpoint_preview_color_ = resources.getColor(KEY_TRACK_ADD_TRACKPOINT_PREVIEW_COLOR);
    add_trackpoint_preview_line_stroke_ = new BasicStroke((float)resources.getDouble(KEY_TRACK_ADD_TRACKPOINT_PREVIEW_LINE_WIDTH));
		cross_color_ = resources.getColor(KEY_CROSS_COLOR);
    cross_line_stroke_ = new BasicStroke((float)resources.getDouble(KEY_CROSS_LINE_WIDTH));
  }

//----------------------------------------------------------------------


//----------------------------------------------------------------------
// BasicLayer methods
// ----------------------------------------------------------------------

//----------------------------------------------------------------------
/**
* This method is called from a background thread to recalulate the
* screen coordinates of any geographical objects. This method must
* store its objects and paint them in the paintComponent() method.
*/

  protected void doCalculation()
  {
  	active_track_ = plugin_.getActiveTrack();
		if(active_track_ != null)
  	{
    	active_track_.forward(getProjection());
    }

		if(plugin_.isInsertNewTrackpointsActive() && mouse_current_ != null)
		{
			calcCrossposition();
		}
		plugin_.setActiveTrack(active_track_);
    repaint();
  }


/**
* Paint layer objects.
*/
  public void paintComponent(Graphics g)
  {
    new_track_ = plugin_.getNewTrack();
		Graphics2D g2 = (Graphics2D) g;
    //g2.setColor(Color.red);

    if(!isActive())
      return;

    g2.setColor(visible_tracks_color_);
   	g2.setStroke(visible_tracks_line_stroke_);

		// if the menue "insert trackpoints" is choosen
		if(plugin_.isInsertNewTrackpointsActive() && mouse_current_ != null && cross_point_ != null)
    {
			g2.setColor(cross_color_);
			g2.setStroke(cross_line_stroke_);
			g2.drawLine(cross_point_.getX()-10,cross_point_.getY(),cross_point_.getX()+10,cross_point_.getY());
		  g2.drawLine(cross_point_.getX(),cross_point_.getY()-10,cross_point_.getX(),cross_point_.getY()+10);
     	g2.setColor(visible_tracks_color_);
     	g2.setStroke(visible_tracks_line_stroke_);
		}


//		if(draw_rect_ && (plugin_.isTrackChooserActive() || plugin_.isRemoveTrackpointsActive()))
		if(draw_rect_ && plugin_.isRemoveTrackpointsActive())
    { 	// draw rectangle of dragged mouse:
    	g2.drawRect(draw_rect_start_x_,draw_rect_start_y_,draw_rect_width_,draw_rect_height_);

		}
		// draw visible tracks

    Trackpoint trackpoint, previous = null, next = null;
    int start_x,start_y, end_x, end_y;

		// draw the new track
		if(new_track_ != null)
    {
    	g2.setColor(marked_trackpoints_color_);
 			g2.setStroke(visible_tracks_line_stroke_);

			List trackpoints = new_track_.getWaypoints();

		  if(!trackpoints.isEmpty())
		  {
				Iterator point_iterator = trackpoints.iterator();
		  	trackpoint = (Trackpoint)point_iterator.next();
				trackpoint.forward(getProjection());
		  	start_x = trackpoint.getX();
	 	    start_y = trackpoint.getY();

        g2.drawRect(start_x-1,start_y-1,3,3);


	 	    while(point_iterator.hasNext())
	  	  {

					trackpoint = (Trackpoint)point_iterator.next();
          trackpoint.forward(getProjection());
					end_x = trackpoint.getX();
	        end_y = trackpoint.getY();

	        // only draw line, if the coordinates are different:
	        if((Math.abs(end_x - start_x) > 0.1)
             || (Math.abs(end_y - start_y) > 0.1))
		      {
	        	// finally draw the line:
	          g2.drawLine(start_x,start_y,end_x,end_y);
					}

					start_x = end_x;
					start_y = end_y;
					g2.drawRect(start_x-1,start_y-1,3,3);
				}

	 	    if((mouse_current_ != null) && (plugin_.isAddNewTrackpointsActive() || plugin_.isMakeNewTrackActive()))
					g2.drawLine(start_x,start_y,mouse_current_.getX(),mouse_current_.getY());

 	 		}
		} // end of draw new track

    // draw all visible tracks

    try
    {
      List tracks = track_manager_.getVisibleProjectedTracks(getProjection());
      Iterator track_iterator = tracks.iterator();

      while(track_iterator.hasNext())
      {

        g2.setColor(visible_tracks_color_);
        g2.setStroke(visible_tracks_line_stroke_);
        Track current_track = (Track) track_iterator.next();

        List trackpoints = current_track.getWaypoints();
        Iterator point_iterator = trackpoints.iterator();

        // draw the lines to modify the trackpoint
        /*
        if((plugin_.isModifyTrackpointsActive()) && (nearest_point_index_ != -1) && (draw_preview_))
        {
				Trackpoint nearest = (Trackpoint)trackpoints.get(nearest_point_index_);
				if(nearest_point_index_ >0)
        previous = (Trackpoint)trackpoints.get(nearest_point_index_-1);
				if((nearest_point_index_ + 1) < trackpoints.size())
        next = (Trackpoint)trackpoints.get(nearest_point_index_+1);

				if((previous != null) && (mouse_current_ != null))
        g2.drawLine(mouse_current_.getX(),mouse_current_.getY(),previous.getX(),previous.getY());

				if((next != null) && (mouse_current_ != null))
        g2.drawLine(mouse_current_.getX(),mouse_current_.getY(),next.getX(),next.getY());
        } */



        // goto first trackpoint:

        if(point_iterator.hasNext())
        {
          trackpoint = (Trackpoint)point_iterator.next();

          start_x = trackpoint.getX();
          start_y = trackpoint.getY();

          if(!plugin_.isMakeNewTrackActive() && (!marked_trackpoints_.isEmpty() || nearest_trackpoint_ != null))
          {
            if(nearest_trackpoint_ != null)
            {
              if(trackpoint == nearest_trackpoint_)
              {
                g2.setColor(marked_trackpoints_color_);
                g2.setStroke(marked_trackpoints_line_stroke_);
              }
            }
            if(!marked_trackpoints_.isEmpty())
            {
              Iterator marked_trackpoints_iterator = marked_trackpoints_.iterator();

              while(marked_trackpoints_iterator.hasNext())
              {
                Trackpoint marked_trackpoint = (Trackpoint) marked_trackpoints_iterator.next();
                if(marked_trackpoint == trackpoint)
                {
                  g2.setColor(marked_trackpoints_color_);
                  g2.setStroke(marked_trackpoints_line_stroke_);
                }
              }
            }
          }

          g2.drawRect(start_x-1,start_y-1,3,3);

          if(!plugin_.isMakeNewTrackActive())
          {
						g2.setColor(visible_tracks_color_);
						g2.setStroke(visible_tracks_line_stroke_);
          }


          // draw the rest:
          while(point_iterator.hasNext())
          {
            trackpoint = (Trackpoint)point_iterator.next();

            end_x = trackpoint.getX();
            end_y = trackpoint.getY();

            // only draw line, if the coordinates are different:
            if((Math.abs(end_x - start_x) > 0.1)
            	 || (Math.abs(end_y - start_y) > 0.1))
            {
              // finally draw the line:

              //g2.setStroke(new BasicStroke());
              //g2.setColor(Color.red);
              g2.drawLine(start_x,start_y,end_x,end_y);
            }

            if(!plugin_.isMakeNewTrackActive() && (!marked_trackpoints_.isEmpty() || nearest_trackpoint_ != null))
            {
              if(nearest_trackpoint_ != null)
              {
                if(trackpoint == nearest_trackpoint_)
                {
                  g2.setColor(marked_trackpoints_color_);
                  g2.setStroke(marked_trackpoints_line_stroke_);
                }
              }
              if(!marked_trackpoints_.isEmpty())
              {
                Iterator marked_trackpoints_iterator = marked_trackpoints_.iterator();

                while(marked_trackpoints_iterator.hasNext())
                {
                  Trackpoint marked_trackpoint = (Trackpoint) marked_trackpoints_iterator.next();
                  if(marked_trackpoint == trackpoint)
                  {
                    g2.setColor(marked_trackpoints_color_);
                    g2.setStroke(marked_trackpoints_line_stroke_);
                  }
                }
              }
            }

            g2.drawRect(end_x-1,end_y-1,3,3);

            if(!plugin_.isMakeNewTrackActive())
            {
              g2.setColor(visible_tracks_color_);
              g2.setStroke(visible_tracks_line_stroke_);
            }

            if(Debug.DEBUG)
            	Debug.println("trackplugin_paint","painting line: from"+start_x+"/"
                            +start_y+" to "+end_x+"/"+end_y);

            // last end is new start
	          start_x = end_x;
  	        start_y = end_y;
          }
        }
			}
			if((mouse_current_ != null) && (plugin_.isAddNewTrackpointsActive() || plugin_.isMakeNewTrackActive()))
			{
        //g2.drawLine(start_x,start_y,mouse_current_.getX(),mouse_current_.getY());
        //System.out.println(plugin_.isMakeNewTrackActive());
			}
		}
    catch(InterruptedException ignored)
    {
    }
  }



//----------------------------------------------------------------------
/**
* Focus Listener Method
*
* @param event the action event
*/

  public void focusGained(FocusEvent event)
  {
  }


//----------------------------------------------------------------------
/**
* Focus Listener Method for focus lost. This method checks the
* validity of the latitude/longitude input fields for valid inputs
*
* @param event the action event
*/

  public void focusLost(FocusEvent event)
  {
  }
//----------------------------------------------------------------------
/**
* Sets the drag point of the mouse.
*
* @param start the start of the mouse drag.
*/
  protected void setMouseDragStart(Point start)
  {
    if(start == null)
    {
      mouse_drag_start_ = null;
      return;
    }
		mouse_drag_start_ = new GeoScreenPoint(start);
    mouse_drag_start_.inverse(getProjection());

  }

//----------------------------------------------------------------------
/**
* Sets the drag points of the mouse.
*
* @param end the start of the mouse drag. If end is null, it is
* assumed that no drag, but only a click was performed.
*/
  protected void setMouseDragEnd(Point end)
  {
    if(end == null)
    {
      mouse_drag_end_ = null;
      return;
    }
    mouse_drag_end_ = new GeoScreenPoint(end);
    mouse_drag_end_.inverse(getProjection());

  }

//----------------------------------------------------------------------
/**
* Sets the current point of the mouse.
*
* @param current the current point of the mouse.
*/
  protected void setMouseCurrent(Point current)
  {
    //mouse_current_ = new TrackpointImpl();
    mouse_current_.setX((int)current.getX());
    mouse_current_.setY((int)current.getY());
    mouse_current_.inverse(getProjection());

  }

//----------------------------------------------------------------------
/**
* Sets the selected track as active track.
*
* @param selected_track the selected track will be the active one
*/
	protected void setSelectedTrackActive(Track selected_track)
	{
		if(active_track_ != null)
		{
			active_track_ = selected_track;
			repaint();
		}
	}

//----------------------------------------------------------------------
/**
* Calculates the position of the cross between to trackpoints. The crossposition
* depends on the position of the trackpoints and on the poistion of the mouse.
*
*/
  protected void calcCrossposition()
  {
    try
    {
      List tracks = track_manager_.getVisibleProjectedTracks(getProjection());
      Iterator track_iterator = tracks.iterator();

      double d = 0,length = 0;
      //** is used to block the change of the current mouse poistion while calculating the crosspoint **//
      Trackpoint mouse_current = (Trackpoint)mouse_current_;
      //** used to initialize the distance
      boolean first_line = true;
      //** initialize the help variables for the calculation **/
      int A_x = 0, A_y = 0, B_x = 0, B_y = 0, store_A_x = 0, store_A_y = 0, store_B_x = 0, store_B_y = 0;


      while(track_iterator.hasNext())
      {

        Track track = (Track)track_iterator.next();
        List track_points = track.getWaypoints();
        Iterator point_iterator = track_points.iterator();

        // get the first point for calculation
        if(point_iterator.hasNext())
        {
          Trackpoint track_point_a = (Trackpoint)point_iterator.next();
          A_x = (track_point_a.getX());
          A_y = (track_point_a.getY());
        }

        // get the second point calculation
        while(point_iterator.hasNext())
        {
          Trackpoint track_point_b = (Trackpoint)point_iterator.next();
          B_x = (track_point_b.getX());
          B_y = (track_point_b.getY());

          // calculate and check the crosspoint
          if(CalcCheckCrosspoint(A_x,A_y,B_x,B_y,mouse_current))
          {
            if(cross_point_ != null)
              // calculate the distance between mousepoint and crosspoint
              d = Math.sqrt(Math.pow((cross_point_.getX() - mouse_current.getX()),2) + Math.pow((cross_point_.getY() - mouse_current.getY()),2));

            // store the shortest distance and the nearest trackpoint after the crosspoint
            if((d < length) || (first_line))
            {
              length = d;
              store_A_x = A_x;
              store_A_y = A_y;
              store_B_x = B_x;
              store_B_y = B_y;
              trackpoint_after_crosspoint_ = track_point_b;

              first_line = false;
            }
          }
          else
          {
            cross_point_ = null;
          }
          A_x = B_x;
          A_y = B_y;
        }
      }

      // final crosspoint calculatin with the right trackpoints
      CalcCheckCrosspoint(store_A_x,store_A_y,store_B_x,store_B_y,mouse_current);
    }
    catch(InterruptedException ignore) {}
  }

//----------------------------------------------------------------------
/**
* Calculates and checks the crosspoint. First the crosspoint between the line of the two trackpoints (A and B)
* with the normal line of it going trough the current mousepoint. Then checks if the crosspoint is valid.
*
* @param A_x, A_y, B_x, B_y integer coordinates of the two trackpoints
* @param mouse_current courrent mousepoint
*
* @return returns true if the crosspoint is on the line between point A and B else returns false
*/

  protected  boolean CalcCheckCrosspoint(int A_x,int A_y,int B_x, int B_y,Trackpoint mouse_current)
  {
		// direction vector of line AB
		int AB_x = B_x - A_x;
		int AB_y = B_y - A_y;
    // normal vector of line AB
		int N_x = AB_y*(-1);
		int N_y = AB_x;

		// calculate the factor with witch the crosspoint can be calculated
    double s = (double)(AB_y*(A_x - mouse_current.getX()) + AB_x*(mouse_current.getY() - A_y)) / (N_x * AB_y - N_y * AB_x);

		// calculate the crosspoint
		double cross_x = mouse_current.getX() + s * N_x;
		double cross_y = mouse_current.getY() + s * N_y;

		cross_point_ = new TrackpointImpl();

		// proper double to int
		if((cross_x - (int)cross_x) >= 0.5)
			cross_x = (int) Math.ceil(cross_x);
		else
			cross_x = (int) Math.floor(cross_x);

		if((cross_y - (int)cross_x) >= 0.5)
			cross_y = (int) Math.ceil(cross_y);
		else
			cross_y = (int) Math.floor(cross_y);

		cross_point_.setX((int)cross_x);
    cross_point_.setY((int)cross_y);
    cross_point_.inverse(getProjection());

		// check if the crosspoint is valid
		if((A_x < B_x) && (cross_x >= A_x) && (cross_x <= B_x))
			return(true);
    if((A_x > B_x) && (cross_x <= A_x) && (cross_x >= B_x))
			return(true);
		if((A_y < B_y) && (cross_y >= A_y) && (cross_y <= B_y))
			return(true);
		if((A_y > B_y) && (cross_y <= A_y) && (cross_y >= B_y))
			return(true);

		return(false);
  }

//----------------------------------------------------------------------
/**
* Inserts a new trackpoint in the active track
*/

  protected void insertNewTrackpoint()
	{
    try
    {
      List visible_tracks = track_manager_.getVisibleProjectedTracks(getProjection());
      Iterator track_iterator = visible_tracks.iterator();

      while(track_iterator.hasNext())
      {
        Track track = (Track) track_iterator.next();
        List trackpoints = track.getWaypoints();
        Iterator point_iterator = trackpoints.iterator();

        while(point_iterator.hasNext())
        {
          Trackpoint trackpoint = (Trackpoint) point_iterator.next();
          if(trackpoint == trackpoint_after_crosspoint_)
          {
            int pos = trackpoints.indexOf(trackpoint);
            track.addWaypoint(pos,cross_point_);
            track_manager_.removeTrack(track.getIdentification());
            track_manager_.addTrack(track);
          }
        }
      }

      repaint();
    }
    catch(InterruptedException ignore) {}
  }

//----------------------------------------------------------------------
/**
* Returns the drag start point of the mouse.
*
* @return return_start the start of the mouse drag.
*/
  protected Trackpoint getMouseDragStart()
  {
		Trackpoint return_start = new TrackpointImpl();
		return_start.setX(mouse_drag_start_.getX());
		return_start.setY(mouse_drag_start_.getY());
		return_start.setLatitude(mouse_drag_start_.getLatitude());
		return_start.setLongitude(mouse_drag_start_.getLongitude());

		return(return_start);
  }

//----------------------------------------------------------------------
/**
* Returns the drag end point of the mouse.
*
* @return return_end the end of the mouse drag.
*/
  protected Trackpoint getMouseDragEnd()
  {
		Trackpoint return_end = new TrackpointImpl();
		return_end.setX(mouse_drag_end_.getX());
		return_end.setY(mouse_drag_end_.getY());
		return_end.setLatitude(mouse_drag_end_.getLatitude());
		return_end.setLongitude(mouse_drag_end_.getLongitude());

		return(return_end);
  }

//----------------------------------------------------------------------
/**
* Sets the identification of the active track
*/
  protected void setTrackID(String id)
  {
		if(id != null)
		{
			if(active_track_ == null)
			{
				active_track_ = new TrackImpl();
			}

			active_track_.setIdentification(id);
		}
	}

//----------------------------------------------------------------------
/**
* Calculates the nearest trackpoint to the current mousepoint and sets it.
*
* @param current_point is the current mousepoint
*/

	public void	getsetNearestPoint(Point current_point,boolean shift_down)
	{

    try
    {
      List visible_tracks = track_manager_.getVisibleProjectedTracks(getProjection());
      Iterator track_iterator = visible_tracks.iterator();

      boolean first_point = true;
      double d, help_distance = 0;

      if(shift_down && nearest_trackpoint_ != null)
      {
        marked_trackpoints_.add(nearest_trackpoint_);
      }

      while(track_iterator.hasNext())
      {
        Track track = (Track) track_iterator.next();
        List trackpoints = track.getWaypoints();
        Iterator point_iterator = trackpoints.iterator();


        while(point_iterator.hasNext())
        {
          Trackpoint trackpoint = (Trackpoint) point_iterator.next();
          d = Math.sqrt(Math.pow((trackpoint.getX() - current_point.getX()),2)
                        +	Math.pow((trackpoint.getY() - current_point.getY()),2));
          if(first_point)
          {
            help_distance = d;
            first_point = false;
            nearest_trackpoint_ = trackpoint;
            trackpoint_to_modify_ = trackpoints.indexOf(nearest_trackpoint_);
            track_to_modify_ = track.getIdentification();
          }
          if(d < help_distance)
          {
            help_distance = d;
            nearest_trackpoint_ = trackpoint;
            trackpoint_to_modify_ = trackpoints.indexOf(nearest_trackpoint_);
            track_to_modify_ = track.getIdentification();
          }
        }

        //System.out.println(track.getIdentification());
      }
      //System.out.println("Index: " + nearest_trackpoint_.getX());
      if(shift_down)
      {
        Iterator trackpoints_iterator = marked_trackpoints_.iterator();

        boolean marked = true;
        int index = -1;
        while(trackpoints_iterator.hasNext())
        {
          Trackpoint trackpoint = (Trackpoint) trackpoints_iterator.next();
          if(nearest_trackpoint_ == trackpoint)
          {
            index = marked_trackpoints_.indexOf(trackpoint);
            marked = false;
          }
        }
        if(index >= 0)
          marked_trackpoints_.remove(index);
        if(marked)
          marked_trackpoints_.add(nearest_trackpoint_);
        nearest_trackpoint_ = null;
      }
      else
        marked_trackpoints_.clear();

      repaint();
    }
    catch(InterruptedException ignore) {}
	}



	//----------------------------------------------------------------------
	/**
	* Sets the preview of the "modifyed trackpoint" mode active so it can be visible on the layer.
	*/
	public void setDrawPreviewActive(boolean active)
	{
		draw_preview_ = active;
	}

 	//----------------------------------------------------------------------
	/**
 	* Searchs for the trackpoints whithin a rectangle and stores the points in an array
 	*/
	public void getSetChoosenTrackpoints()
	{

		active_track_ = plugin_.getActiveTrack();
		List trackpoints = active_track_.getWaypoints();
    Iterator point_iterator = trackpoints.iterator();
    Trackpoint trackpoint;
    choosen_trackpoints_ = new ArrayList();
		GeoScreenPoint rect_start_point = mouse_drag_start_;
    GeoScreenPoint rect_end_point = mouse_drag_end_;

	  if(rect_start_point != null && rect_end_point != null)
		{

      while(point_iterator.hasNext())
      {
        trackpoint = (Trackpoint)point_iterator.next();

				if(rect_start_point.getX() < rect_end_point.getX())
	      {
	      	if(rect_start_point.getY() < rect_end_point.getY())
	      	{
	      	  if((trackpoint.getX() >= rect_start_point.getX()) && (trackpoint.getX() <= rect_end_point.getX()))
	      		{
	      	  	if((trackpoint.getY() >= rect_start_point.getY()) && (trackpoint.getY() <= rect_end_point.getY()))
	      			{
	      				choosen_trackpoints_.add(trackpoint);
							}
						}
	      	}
	      	else
	      	{
            if((trackpoint.getX() >= rect_start_point.getX()) && (trackpoint.getX() <= rect_end_point.getX()))
	      		{
	      	  	if((trackpoint.getY() <= rect_start_point.getY()) && (trackpoint.getY() >= rect_end_point.getY()))
	      			{
	      				choosen_trackpoints_.add(trackpoint);
							}
	      		}
	      	}
	      }
	      else
	    	{
          if(rect_start_point.getY() < rect_end_point.getY())
	      	{
	      	  if((trackpoint.getX() <= rect_start_point.getX()) && (trackpoint.getX() >= rect_end_point.getX()))
	      		{
	      	  	if((trackpoint.getY() >= rect_start_point.getY()) && (trackpoint.getY() <= rect_end_point.getY()))
	      			{
	      				choosen_trackpoints_.add(trackpoint);
							}
						}
	      	}
	      	else
	      	{
            if((trackpoint.getX() <= rect_start_point.getX()) && (trackpoint.getX() >= rect_end_point.getX()))
	      		{
	      	  	if((trackpoint.getY() <= rect_start_point.getY()) && (trackpoint.getY() >= rect_end_point.getY()))
	      			{
	      				choosen_trackpoints_.add(trackpoint);
							}
	      		}
					}
				}
      }
    }
		repaint();
	}

  //----------------------------------------------------------------------
	/**
 	* Delets the choosen trackpoints from the active track
 	*/

	public void deleteTrackpoints()
	{
    try
    {
      List visible_tracks = track_manager_.getVisibleProjectedTracks(getProjection());
      Iterator track_iterator = visible_tracks.iterator();

      while(track_iterator.hasNext())
      {
        Track track = (Track) track_iterator.next();
        List trackpoints = track.getWaypoints();
        Iterator trackpoint_iterator = trackpoints.iterator();

        while(trackpoint_iterator.hasNext())
        {
          Trackpoint trackpoint = (Trackpoint) trackpoint_iterator.next();

          if(nearest_trackpoint_ != null)
          {
            if(trackpoint.equals(nearest_trackpoint_))
            {
              track.removeWaypoint(trackpoints.indexOf(trackpoint));
              track_manager_.removeTrack(track.getIdentification());
              track_manager_.addTrack(track);
            }
          }
          if(!marked_trackpoints_.isEmpty())
          {
            Iterator marked_trackpoints_iterator = marked_trackpoints_.iterator();
            while(marked_trackpoints_iterator.hasNext())
            {
              Trackpoint marked_trackpoint = (Trackpoint) marked_trackpoints_iterator.next();
              if(trackpoint == marked_trackpoint)
              {
                int index = trackpoints.indexOf(trackpoint);
                track.removeWaypoint(index);
                track_manager_.removeTrack(track.getIdentification());
                track_manager_.addTrack(track);
                trackpoints.remove(index);
                trackpoint_iterator = trackpoints.iterator();
              }
            }

          }
        }
      }

      repaint();
    }
    catch(InterruptedException ignore) {}
	}

	//----------------------------------------------------------------------
	/**
 	* changes the current trackpoint
 	*/

	public void changeTrackpoint()
	{
		Track track = track_manager_.getTrack(track_to_modify_);
		track.removeWaypoint(trackpoint_to_modify_);
		track.addWaypoint(trackpoint_to_modify_,mouse_current_);
		track_manager_.removeTrack(track_to_modify_);
		track_manager_.addTrack(track);
		repaint();
 	}

 	//----------------------------------------------------------------------
	/**
 	* pans to the end of the track which is nearest to the mouse pointer
 	*/

	public void panToEndOfTrack()
	{
		Track track = track_manager_.getTrack(track_to_modify_);
		int track_size = track.size();
		Trackpoint trackpoint = (Trackpoint)track.getWaypoint(track_size-1);
		map_nav_hook_.setMapCenter(trackpoint.getLatitude(),trackpoint.getLongitude());
		new_track_ = new TrackImpl();
		new_track_.addWaypoint(trackpoint);
		plugin_.setNewTrack(new_track_);
		nearest_trackpoint_ = null;
		repaint();
 	}


  //----------------------------------------------------------------------
	/**
 	* adds the new trackpoints to the current track
 	*/

	public void addNewTrackpointsToTrack()
	{
		new_track_ = plugin_.getNewTrack();

		Track track = track_manager_.getTrack(track_to_modify_);

		List trackpoints_new = new_track_.getWaypoints();
		Iterator trackpoints_new_iterator = trackpoints_new.iterator();

		// first can be deleted (equals the last from the original track)

		while(trackpoints_new_iterator.hasNext())
		{
			track.addWaypoint((Trackpoint) trackpoints_new_iterator.next());
		}

		track_manager_.removeTrack(track_to_modify_);
		track_manager_.addTrack(track);
		new_track_ = null;
		plugin_.setNewTrack(new_track_);
		repaint();
 	}

 	//----------------------------------------------------------------------
	/**
 	* clears the nearest_trackpoint
 	*/

	public void clearNearestTrackpoint()
	{
		nearest_trackpoint_ = null;
 		repaint();
	}

}
