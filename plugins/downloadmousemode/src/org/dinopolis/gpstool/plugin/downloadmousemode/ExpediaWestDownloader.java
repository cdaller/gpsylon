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

import java.net.URLConnection;




//----------------------------------------------------------------------
/**
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class ExpediaWestDownloader extends SimpleUrlDownloader
{

  public String getPluginName()
  {
    return("Expedia West Downloader");
  }
  
  protected String getMapServerName()
  {
    return("expedia_west");
  }


//----------------------------------------------------------------------
/**
 * Set some request properties for the url connection. This method may
 * be overwritten to set some additional http header fields. This
 * implementation sets the "Cookie: jscript=1" header field. (Thanks
 * to Chris Sutton and Fritz Ganter).
 *
 * @param connection the connection that is used to request the url
 * for the map.
 * @return the same (or new connection) with some request properties
 * set.
 */
  protected URLConnection setRequestProperties(URLConnection connection)
  {
    connection.setDoOutput(true);
    connection.setDoInput(true);
    connection.setUseCaches(false);
    connection.setRequestProperty("Cookie","jscript=1");
    return(connection);
  }

}
