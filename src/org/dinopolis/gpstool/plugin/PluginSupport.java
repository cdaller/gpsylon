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

import java.awt.Component;
import java.awt.Frame;
import java.beans.PropertyChangeSupport;
import org.dinopolis.gpstool.gpsinput.GPSDataProcessor;
import org.dinopolis.gpstool.gui.MouseModeManager;
import org.dinopolis.gpstool.hook.MapManagerHook;
import org.dinopolis.gpstool.hook.MapNavigationHook;
import org.dinopolis.gpstool.hook.StatusHook;
import org.dinopolis.gpstool.track.TrackManager;
import org.dinopolis.gpstool.util.UnitHelper;
import org.dinopolis.util.Resources;
import org.dinopolis.util.servicediscovery.ServiceDiscovery;

//----------------------------------------------------------------------
/**
 * This class provides all available hooks and components that
 * modules/plugins may need to work properly. It is passed in the
 * initializer method of plugins or other layers. The modules may
 * retrieve the hooks/objects they need.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public interface PluginSupport
{
  
//----------------------------------------------------------------------
/**
 * Get the map_manager_hook.
 *
 * @return the map_manager_hook.
 */
  public MapManagerHook getMapManagerHook();
  
//----------------------------------------------------------------------
/**
 * Get the map_navigation_hook_.
 *
 * @return the map_navigation_hook_.
 */
  public MapNavigationHook getMapNavigationHook();
  
//----------------------------------------------------------------------
/**
 * Get the status_hook.
 *
 * @return the status_hook.
 */
  public StatusHook getStatusHook();
  
//----------------------------------------------------------------------
/**
 * Get the main Frame of the application.
 *
 * @return the main_frame.
 */
  public Frame getMainFrame();

//----------------------------------------------------------------------
/**
 * Get the map component
 *
 * @return the map component.
 */
  public Component getMapComponent();

//----------------------------------------------------------------------
/**
 * Get the property_change_support. This support may be used to
 * register as a PropertyChangeListener, and therefore to receive
 * ProperyChangeEvents. Use the property keys in
 * {@link org.dinopolis.gpstool.Gpsylon} as property names
 * (<code>PROPERTY_KEY_...</code>). 
 *
 * @return the property_change_support.
 */
  public PropertyChangeSupport getPropertyChangeSupport();

//----------------------------------------------------------------------
/**
 * Get the resources (may be used to store strings, booleans, etc.).
 *
 * @return the resources
 */
  public Resources getResources();


//----------------------------------------------------------------------
/**
 * Get the track manager (may be used to set/get tracks).
 *
 * @return the track manager
 */
  public TrackManager getTrackManager();

//----------------------------------------------------------------------
/**
 * Get the gps data processor that handles the input/output of the gps
 * data. This class may be used to get/set routes, tracks, waypoints,
 * etc. directly to the gps device. For position, heading, speed,
 * etc. better use the {@link #getPropertyChangeSupport()}
 * method. Please note, that not all implementations of the
 * GPSDataProcessor interface support all operations!
 *
 * @return the gps data processor
 */
  public GPSDataProcessor getGPSDataProcessor();

//----------------------------------------------------------------------
/**
 * Get the service discovery that allows to find all kind of plugins.
 *
 * @return the service discovery.
 */
  public ServiceDiscovery getServiceDiscovery();

//----------------------------------------------------------------------
/**
 * Set the unit helper. This class may be used to print speed,
 * latitude, longitude, distances, etc. in the correct format
 * (kilomters, miles, etc.).
 *
 * @return the unit helper.
 */
  public UnitHelper getUnitHelper();

//----------------------------------------------------------------------
/**
 * Set the mouse mode manager. This class may be used to change the
 * mouse mode.
 *
 * @return the mouse mode manager.
 */
  public MouseModeManager getMouseModeManager();
}


