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

package org.dinopolis.gpstool.gui.layer;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;

import org.dinopolis.gpstool.Gpsylon;
import org.dinopolis.gpstool.GpsylonKeyConstants;
import org.dinopolis.gpstool.util.ExtensionFileFilter;
import org.dinopolis.util.Resources;
import org.dinopolis.util.gui.ActionStore;
import org.dinopolis.util.gui.MenuFactory;


//----------------------------------------------------------------------
/**
 * A layer that is able to display shape files.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class ShapeLayer extends com.bbn.openmap.layer.shape.MultiShapeLayer
  implements GpsylonKeyConstants
{
  boolean layer_active_ = true;

  Resources resources_;
  
  JFileChooser file_chooser_;

  Properties openmap_properties_;
  int shape_number_ = 1;
  String shape_list_;
  ActionStore action_store_;
      /** the Actions */
  private Action[] actions_ = { new ShapeLayerActivateAction(),
				new LoadShapeAction()};

//----------------------------------------------------------------------
/**
 * 
 */
  public ShapeLayer(Resources resources)
  {
    super();
    
    
    resources_ = resources;
    action_store_ = ActionStore.getStore(Gpsylon.ACTION_STORE_ID);
    action_store_.addActions(actions_);
    setDoubleBuffered(true);
  }


  public void addShapeFile(File file)
  {
    if(openmap_properties_ == null)
      openmap_properties_ = new Properties();

    if(shape_list_ == null)
      shape_list_ = "shape"+shape_number_;
    else
      shape_list_ = shape_list_ + " shape"+shape_number_;

    // set shape list:
    openmap_properties_.setProperty("shapeLayer."+ShapeFileListProperty, shape_list_);
    // add property for last shape file
    openmap_properties_.setProperty("shapeLayer.shape"+shape_number_+"."+shapeFileProperty,
				    file.getAbsolutePath());
    setProperties("shapeLayer",openmap_properties_);
    shape_number_++;
  }
  
//----------------------------------------------------------------------
/**
 * If this layer is enabled, calls paint from its superclass.
 */
  public void paint(Graphics g)
  {
    if(!layer_active_)
      return;

    super.paint(g);
  }



// ----------------------------------------------------------------------
// inner classes
// ----------------------------------------------------------------------

// ----------------------------------------------------------------------
// action classes

//----------------------------------------------------------------------
/**
 * The Action that triggers the de-/activation of this layer.
 */

  class ShapeLayerActivateAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public ShapeLayerActivateAction()
    {
      super(Gpsylon.ACTION_SHAPE_LAYER_ACTIVATE);
      putValue(MenuFactory.SELECTED, new Boolean(layer_active_));
    }

        //----------------------------------------------------------------------
        /**
         * Stores bounds and locations if this option was enabled and
         * exits.
         * 
         * @param event the action event
         */

    public void actionPerformed(ActionEvent event)
    {
      layer_active_ = !layer_active_;
      Action action = action_store_.getAction(Gpsylon.ACTION_SHAPE_LAYER_ACTIVATE);
      if(action != null)
        action.putValue(MenuFactory.SELECTED, new Boolean(layer_active_));
      repaint();
    }
  }

//----------------------------------------------------------------------
/**
 * The Action that triggers load shape mode.
 */

  class LoadShapeAction extends AbstractAction 
  {

        //----------------------------------------------------------------------
        /**
         * The Default Constructor.
         */

    public LoadShapeAction()
    {
      super(Gpsylon.ACTION_LOAD_SHAPE);
    }

        //----------------------------------------------------------------------
        /**
         * Load a shape
         * 
         * @param event the action event
         */

    public void actionPerformed(ActionEvent event)
    {
      File[] chosen_files = null;
      if(file_chooser_ == null)
      {
        file_chooser_ = new JFileChooser();
        file_chooser_.setDialogTitle(resources_.getString(KEY_LOCALIZE_LOAD_SHAPE_DIALOG_TITLE));
        ExtensionFileFilter filter;
        
            // add filter for shape files:
        filter = new ExtensionFileFilter();
        filter.addExtension(resources_.getString(KEY_SHAPE_FILE_EXTENSION));
        filter.setDescription(resources_.getString(KEY_SHAPE_FILE_DESCRIPTIVE_NAME));
        file_chooser_.addChoosableFileFilter(filter);
        
//        file_chooser_.setAcceptAllFileFilterUsed(true);
        
        file_chooser_.setMultiSelectionEnabled(true);
        file_chooser_.setFileHidingEnabled(false);
//          String tracks_dirname = FileUtil.getAbsolutePath(resources_.getString(KEY_FILE_MAINDIR),
//                                                           resources_.getString(KEY_FILE_TRACK_DIR));
//          file_chooser_.setCurrentDirectory(new File(tracks_dirname));
      }
      
      int result = file_chooser_.showOpenDialog(ShapeLayer.this);
      if(result == JFileChooser.APPROVE_OPTION)
      {
        chosen_files = file_chooser_.getSelectedFiles();
        for(int count = 0; count < chosen_files.length; count++)
        {
          addShapeFile(chosen_files[count]);
        }
	doPrepare();
      }

    }
  }

}
