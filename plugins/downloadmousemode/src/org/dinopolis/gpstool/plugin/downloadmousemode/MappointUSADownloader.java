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


package org.dinopolis.gpstool.plugin.downloadmousemode;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import org.dinopolis.gpstool.GPSMapKeyConstants;
import org.dinopolis.gpstool.MapInfo;
import org.dinopolis.gpstool.plugin.MapRetrievalPlugin;
import org.dinopolis.gpstool.plugin.PluginSupport;
import org.dinopolis.gpstool.plugin.downloadmousemode.DownloadMouseModeLayer;
import org.dinopolis.gpstool.util.HttpRequester;
import org.dinopolis.util.ProgressListener;
import org.dinopolis.util.Debug;
import org.dinopolis.util.Resources;
import org.dinopolis.util.gui.HTMLViewerFrame;



//----------------------------------------------------------------------
/**
 * A downloader download a map from the mappoint usa server. As the
 * mappoint server does not allow simple url requests, a couple of
 * steps are necessary. Thanks to Alex Koralewski for providing the
 * way to do it!
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class MappointUSADownloader extends MappointDownloader
{

  protected String getLocation()
  {
    return("USA");
  }
}
