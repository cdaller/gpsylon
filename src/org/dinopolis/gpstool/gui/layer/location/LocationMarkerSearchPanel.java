/***********************************************************************
 * @(#)$RCSfile$   $Revision$$Date$
 *
 * Copyright (c) 2002 IICM, Graz University of Technology
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

package org.dinopolis.gpstool.gui.layer.location;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.dinopolis.gpstool.GPSMapKeyConstants;
import org.dinopolis.gpstool.util.geoscreen.GeoScreenList;
import org.dinopolis.util.Debug;
import org.dinopolis.util.gui.SwingWorker;


//----------------------------------------------------------------------
/**
 * This panel has a textfield to enter a name of a location marker to
 * search for and a list that displayes the result of the search. The
 * entered name is searched in the location marker sources.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class LocationMarkerSearchPanel extends JPanel
  implements ActionListener, GPSMapKeyConstants  //, MouseListener
{

  JTextField enter_field_;
  JList result_list_;

  Vector sources_ = new Vector();

  SwingWorker swing_worker_;

  Object result_lock_ = new Object();

  JProgressBar progress_bar_;

//----------------------------------------------------------------------
/**
 * Default Constructor.
 */

  public LocationMarkerSearchPanel()
  {
    super(new BorderLayout());
    
    add(enter_field_ = new JTextField(), BorderLayout.NORTH);

    result_list_ = new JList(new DefaultListModel());
    result_list_.setCellRenderer(new LocationMarkerCellRenderer());
    result_list_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//    result_list_.addMouseListener(this);

    
    JScrollPane scroll_pane = new JScrollPane(result_list_);
    add(scroll_pane,BorderLayout.CENTER);

    progress_bar_ = new JProgressBar();
    progress_bar_.setString("");
    progress_bar_.setStringPainted(true);
    add(progress_bar_,BorderLayout.SOUTH);
    
    setSize(600,(int)getSize().getHeight());

    enter_field_.addActionListener(this);

//    updatePreviewRectangle();
    
//     addWindowListener(new WindowAdapter()
//       {
//         public void windowClosing(WindowEvent e)
//         {
// //          preview_hook_.hidePreviewMaps();
//           setVisible(false);
//         }
//       });
  }

//----------------------------------------------------------------------
/**
 * Returns the selected location marker or null, if no entry in the
 * list is selected.
 * 
 * @return the selected location marker or null, if no entry in the
 * list is selected.
 */
  public LocationMarker getSelectedLocationMarker()
  {
    return((LocationMarker)result_list_.getSelectedValue());
  }
  
//----------------------------------------------------------------------
/**
 * Set the location markers to be displayed in the list.
 * 
 * @param markers the location markers to display in the result list.
 */
  protected void setResult(GeoScreenList markers)
  {
    synchronized(result_lock_)
    {
      DefaultListModel list_model = (DefaultListModel)result_list_.getModel();
      list_model.clear();
      Iterator result_iterator = markers.iterator();
      while(result_iterator.hasNext())
      {
        LocationMarker marker = (LocationMarker)result_iterator.next();
        list_model.addElement(marker);
      }
    }
    result_list_.ensureIndexIsVisible(0);
    progress_bar_.setString(markers.size()+" markers found!");
//       progress_bar_.setString(resources_.getString(KEY_LOCALIZE_NOTHING_FOUND));
  }

//----------------------------------------------------------------------
/**
 * Add a location marker source to search.
 * 
 * @param source the source to add.
 */
  public void addLocationMarkerSource(LocationMarkerSource source)
  {
    sources_.add(source);
  }

//----------------------------------------------------------------------
/**
 * Searches all location marker sources and sets the resulting
 * location markers to be displayed in the list. This is done with a
 * background task.
 * 
 * @param search_word the name to search.
 */
  protected void doNameSearch(final String search_word)
  {
        // stop old thread
    if(swing_worker_ != null)
      swing_worker_.interrupt();

    swing_worker_ = new SwingWorker()
      {
        GeoScreenList worker_markers_ = new GeoScreenList();
        
        public Object construct()
        {
          progress_bar_.setIndeterminate(true);
          progress_bar_.setString("...'"+search_word+"'...");
              // create filter that uses the name, a like operator and ignores the case:
          LocationMarkerFilter filter = new LocationMarkerFilter(LocationMarkerFilter.KEY_NAME,
                                                                 new String[]{search_word + "%"},
                                                                 LocationMarkerFilter.LIKE_OPERATION,
                                                                 true);
          JDBCPreparedStatementPart part = filter.toPreparedStatementPart();
          List sources;
          synchronized(sources_)
          {
            sources = (List)sources_.clone();
          }
          Iterator iterator = sources.iterator();
          while(iterator.hasNext())
          {
            try
            {
              LocationMarkerSource source = (LocationMarkerSource)iterator.next();
//              System.out.println("searching "+source);
              worker_markers_ = source.getLocationMarkers(90f,-90f,-180f,180f,filter,worker_markers_);

            }
            catch(LocationMarkerSourceException lmse)
            {
              System.err.println("ERROR: LocationMarkerSource threw an exception: "
                                 +lmse.getMessage());
              lmse.getCause().printStackTrace();
            }
          }
          if(Thread.interrupted())
          {
            worker_markers_ = null;
            return(null);
          }

          if(Debug.DEBUG)
            Debug.println("LocationMarkerSearch","searching finished");
          return(null);
        }
          
        public void finished()
        {
          if(worker_markers_ != null)
          {
            progress_bar_.setIndeterminate(false);
            setResult(worker_markers_);
          }
        }
      };
    swing_worker_.start();
  }        


//----------------------------------------------------------------------
/**
 * Adds a mouse listener for the result list.
 * 
 * @param listener the listener to add.
 */
  public void addMouseListenerForList(MouseListener listener)
  {
    result_list_.addMouseListener(listener);
  }

//----------------------------------------------------------------------
/**
 * Removes a mouse listener from the result list.
 * 
 * @param listener the listener to remove.
 */
  public void removeMouseListenerForList(MouseListener listener)
  {
    result_list_.removeMouseListener(listener);
  }

//----------------------------------------------------------------------
/**
 * Returns the mouse listeners for the list.
 * 
 * @return the mouse listeners for the list.
 */
  public MouseListener[] getMouseListenersForList()
  {
    return(result_list_.getMouseListeners());
  }

//----------------------------------------------------------------------
/**
 * Action Listener Method
 * 
 * @param event the action event
 */
  public void actionPerformed(ActionEvent event)
  {
    if(event.getSource() == enter_field_)
    {
      String search_word = enter_field_.getText();
      doNameSearch(search_word);
    }
  }

//  //----------------------------------------------------------------------
//  // MouseListener Adapter
//  //----------------------------------------------------------------------

//    public void mouseClicked(MouseEvent event)
//    {
//      if (event.getClickCount() == 2)
//      {
//        int index = result_list_.locationToIndex(event.getPoint());
//        System.out.println("Double clicked on Item " + index);
//        System.out.println("Double clicked on marker " + getSelectedLocationMarker());
//      }
//    }
   
//    public void mouseEntered(MouseEvent event)
//    {
//  //    System.out.println("mouseEntered: "+event.getSource());
//    }

//    public void mouseExited(MouseEvent event)
//    {
//  //    System.out.println("mouseExited: "+event.getSource());
//    }

//    public void mousePressed(MouseEvent event)
//    {
//          // on solaris, mousepressed sets popuptrigger
//      if(event.isPopupTrigger())
//      {
//  //      showPopup(event);
//      }
//  //    System.out.println("mousePressed: "+event.getSource());
//    }

//    public void mouseReleased(MouseEvent event)
//    {
//          // on windows, mousereleased sets popuptrigger
//      if(event.isPopupTrigger())
//      {
//  //      showPopup(event);
//      }
//  //    System.out.println("mouseReleased: "+event.getSource());
//    }

  
//    public static void main(String[] args)
//    {
//      try
//      {
//        Resources resources =
//          ResourceManager.getResources(GPSMap.class,
//                                       "GPSMap",".gpsmap",
//                                       Locale.getDefault());
//        JDBCLocationMarkerSource source =
//          new JDBCLocationMarkerSource("org.hsqldb.jdbcDriver",
//                                       "jdbc:hsqldb:/filer/cdaller/.gpsmap/marker/location_marker_db",
//                                       "sa","",resources);
//        final JFrame frame = new JFrame();
//        LocationMarkerSearchPanel search_panel = new LocationMarkerSearchPanel();
//        source.open();
//        search_panel.addLocationMarkerSource(source);
//        frame.getContentPane().add(search_panel);
//        frame.pack();
//        frame.setVisible(true);
      
//        frame.addWindowListener(new WindowAdapter()
//          {
//            public void windowClosing(WindowEvent e)
//            {
//              frame.setVisible(false);
//              System.exit(1);
//            }
//          });
//      }
//      catch(Exception e)
//      {
//        e.printStackTrace();
//      }
//    }
  
}




