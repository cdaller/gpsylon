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


package org.dinopolis.gpstool;

import java.awt.Component;
import java.awt.Frame;
import java.beans.PropertyChangeSupport;
import org.dinopolis.gpstool.gpsinput.GPSDataProcessor;
import org.dinopolis.gpstool.gui.MouseModeManager;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.util.UnitHelper;
import org.dinopolis.util.Resources;
import org.dinopolis.util.servicediscovery.ServiceDiscovery;

//----------------------------------------------------------------------
/**
 * This class provides all available hooks and components that modules
 * may need to work properly. It is passed in the initializer method
 * of plugins or other layers. The modules may retrieve the hooks they
 * need.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class HookManager implements PluginSupport
{
  MapManagerHook map_manager_hook_;
  MapNavigationHook map_navigation_hook_;
  StatusHook status_hook_;
  
  Frame main_frame_;
  Component component_;
  PropertyChangeSupport property_change_support_;
  Resources resources_;
  GPSDataProcessor gps_data_processor_;
  TrackManager track_manager_;
  ServiceDiscovery service_discovery_;
  UnitHelper unit_helper_;
  MouseModeManager mouse_mode_manager_;
  
  public HookManager()
  {
  }

 
//----------------------------------------------------------------------
/**
 * Get the map_manager_hook.
 *
 * @return the map_manager_hook.
 */
  public MapManagerHook getMapManagerHook() 
  {
    return (map_manager_hook_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the map_manager_hook.
 *
 * @param map_manager_hook the map_manager_hook.
 */
  protected void setMapManagerHook(MapManagerHook map_manager_hook) 
  {
    map_manager_hook_ = map_manager_hook;
  }

//----------------------------------------------------------------------
/**
 * Get the map_navigation_hook_.
 *
 * @return the map_navigation_hook_.
 */
  public MapNavigationHook getMapNavigationHook() 
  {
    return (map_navigation_hook_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the map_navigation_hook_.
 *
 * @param map_navigation_hook_ the map_navigation_hook_.
 */
  protected void setMapNavigationHook(MapNavigationHook map_navigation_hook) 
  {
    map_navigation_hook_ = map_navigation_hook;
  }

//----------------------------------------------------------------------
/**
 * Get the status_hook.
 *
 * @return the status_hook.
 */
  public StatusHook getStatusHook() 
  {
    return (status_hook_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the status_hook.
 *
 * @param status_hook the status_hook.
 */
  protected void setStatusHook(StatusHook status_hook) 
  {
    status_hook_ = status_hook;
  }
  

//----------------------------------------------------------------------
/**
 * Get the main_frame.
 *
 * @return the main_frame.
 */
  public Frame getMainFrame() 
  {
    return (main_frame_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the main_frame.
 *
 * @param main_frame the main_frame.
 */
  protected void setMainFrame(Frame main_frame) 
  {
    main_frame_ = main_frame;
  }

//----------------------------------------------------------------------
/**
 * Get the map component
 *
 * @return the map component.
 */
  public Component getMapComponent()
  {
    return(component_);
  }

//----------------------------------------------------------------------
/**
 * Set the map component
 *
 * @return the map component.
 */
  protected void setMapComponent(Component component)
  {
    component_ = component;
  }

//----------------------------------------------------------------------
/**
 * Get the property_change_support.
 *
 * @return the property_change_support.
 */
  public PropertyChangeSupport getPropertyChangeSupport() 
  {
    return (property_change_support_);
  }
  
//----------------------------------------------------------------------
/**
 * Set the property_change_support.
 *
 * @param property_change_support the property_change_support.
 */
  protected void setPropertyChangeSupport(PropertyChangeSupport property_change_support) 
  {
    property_change_support_ = property_change_support;
  }
  
  
//----------------------------------------------------------------------
/**
 * Get the resources (may be used to store strings, booleans, etc.).
 *
 * @return the resources
 */
  public Resources getResources()
  {
    return(resources_);
  }

//----------------------------------------------------------------------
/**
 * Set the resources (may be used to store strings, booleans, etc.).
 *
 * @param resources the resources
 */
  protected void setResources(Resources resources)
  {
    resources_ = resources;
  }
  
//----------------------------------------------------------------------
/**
 * Get the track manager (may be used to set/get tracks).
 *
 * @return the track manager
 */
  public TrackManager getTrackManager()
  {
    return(track_manager_);
  }

//----------------------------------------------------------------------
/**
 * Set the track manager  (may be used to set/get tracks).
 *
 * @param track_manager the trackmanager
 */
  protected void setTrackManager(TrackManager track_manager)
  {
    track_manager_ = track_manager;
  }


//----------------------------------------------------------------------
/**
 * Get the gps data processor that handles the input/output of the gps
 * data. This class may be used to get/set routes, tracks, waypoints,
 * etc. directly to the gps device. For position, heading, speed,
 * etc. better use the {@link #getPropertyChangeSupport()} method.
 *
 * @return the gps data processor
 */
  public GPSDataProcessor getGPSDataProcessor()
  {
    return(gps_data_processor_);
  }

//----------------------------------------------------------------------
/**
 * Set the gps data processor that handles the input/output of the gps
 * data. This class may be used to get/set routes, tracks, waypoints,
 * etc. directly to the gps device. For position, heading, speed,
 * etc. better use the {@link #getPropertyChangeSupport()} method.
 *
 * @param the gps data processor
 */
  protected void setGPSDataProcessor(GPSDataProcessor processor)
  {
    gps_data_processor_ = processor;
  }

//----------------------------------------------------------------------
/**
 * Get the service discovery that allows to find all kind of plugins.
 *
 * @return the service discovery.
 */
  public ServiceDiscovery getServiceDiscovery()
  {
    return(service_discovery_);
  }

//----------------------------------------------------------------------
/**
 * Set the service discovery that allows to find all kind of plugins.
 *
 * @param service_discovery the service discovery.
 */
  protected void setServiceDiscovery(ServiceDiscovery service_discovery)
  {
    service_discovery_ = service_discovery;
  }

//----------------------------------------------------------------------
/**
 * Set the unit helper.
 *
 * @param the unit helper
 */
  protected void setUnitHelper(UnitHelper unit_helper)
  {
    unit_helper_ = unit_helper;
  }

//----------------------------------------------------------------------
/**
 * Set the unit helper. This class may be used to print speed,
 * latitude, longitude, distances, etc. in the correct format
 * (kilomters, miles, etc.).
 *
 * @return the unit helper.
 */
  public UnitHelper getUnitHelper()
  {
    return(unit_helper_);
  }

//----------------------------------------------------------------------
/**
 * Set the mouse mode manager.
 *
 * @param the mouse mode manager
 */
  protected void setMouseModeManager(MouseModeManager mouse_mode_manager)
  {
    mouse_mode_manager_ = mouse_mode_manager;
  }

//----------------------------------------------------------------------
/**
 * Set the mouse mode manager. This class may be used to change the
 * mouse mode.
 *
 * @return the mouse mode manager.
 */
  public MouseModeManager getMouseModeManager()
  {
    return(mouse_mode_manager_);
  }

    
}


