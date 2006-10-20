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


package org.dinopolis.gpstool.plugin.export;

import java.net.URL;

import org.dinopolis.gpstool.GpsylonKeyConstants;

//----------------------------------------------------------------------
/**
 * This plugin writes track data to a stream.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class WritePcx5Plugin extends AbstractTemplateExportPlugin implements GpsylonKeyConstants
{



      // resource keys:
  private static final String KEY_WRITEPCX5_PLUGIN_IDENTIFIER = "export.pcx5.plugin.identifier";
  private static final String KEY_WRITEPCX5_PLUGIN_VERSION = "export.pcx5.plugin.version";
  private static final String KEY_WRITEPCX5_PLUGIN_NAME = "export.pcx5.plugin.name";
  private static final String KEY_WRITEPCX5_PLUGIN_DESCRIPTION = "export.pcx5.plugin.description";
  private static final String KEY_WRITEPCX5_FILE_EXTENSION  = "export.pcx5.file.extension";
  private static final String KEY_WRITEPCX5_FILE_DESCRIPTIVE_NAME  = "export.pcx5.file.descriptive_name";

  static final String KEY_WRITEPCX5_TEMPLATE  = "export.pcx5.template";

  /**
   * Default Constructor
   */
  public WritePcx5Plugin()
  {
    super();
  }

  /**
   * Returns the url of the template to use.
   * @return the url of the template to use.
   */
  protected URL getTemplateUrl() {
    String template = getResources().getString(KEY_WRITEPCX5_TEMPLATE);
    URL url = getClass().getClassLoader().getResource(template);
    return url;
  }


//----------------------------------------------------------------------
/**
 * Returns a short description of the track data that may be used e.g. in
 * a file chooser. If possible, the description should be localized.
 *
 * @return The short description of the content.
 */

  public String getContentDescription()
  {
    return(getResources().getString(KEY_WRITEPCX5_FILE_DESCRIPTIVE_NAME));
  }

//----------------------------------------------------------------------
/**
 * Returns possible file extensions the content. This information
 * may be used in a file chooser as a filter (e.g. ["jpg","jpeg"]).
 *
 * @return The file extensions to use for this kind of data.
 */

  public String[] getContentFileExtensions()
  {
    return(getResources().getStringArray(KEY_WRITEPCX5_FILE_EXTENSION));
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
    return(getResources().getString(KEY_WRITEPCX5_PLUGIN_IDENTIFIER));
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
    return((float)getResources().getDouble(KEY_WRITEPCX5_PLUGIN_VERSION));
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
    return(getResources().getString(KEY_WRITEPCX5_PLUGIN_NAME));
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
    return(getResources().getString(KEY_WRITEPCX5_PLUGIN_DESCRIPTION));
  }

}
