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


package org.dinopolis.gpstool.plugin;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

//----------------------------------------------------------------------
/**
 * This plugin reads track data from a stream (ususally from a file)
 * and provides one or more tracks (lists of {@link
 * org.dinopolis.gpstool.track.Trackpoint} objects).
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public interface WriteTrackPlugin extends Plugin
{

//----------------------------------------------------------------------
/**
 * Returns a short description of the track data that may be used for example
 * in a file chooser. If possible, the description should be localized.
 *
 * @return The short description of the content.
 */

  public String getContentDescription();

//----------------------------------------------------------------------
/**
 * Returns possible file extensions the content. This information
 * may be used in a file chooser as a filter (e.g. ["jpg","jpeg"]).
 *
 * @return The file extensions to use for this kind of data.
 */

  public String[] getContentFileExtensions();

//----------------------------------------------------------------------
/**
 * Parse the given input stream and return tracks. If no tracks could
 * be read, an empty array (length of 0) is returned (not null!).
 *
 * @param in the inputstream to read the data from.
 * @return an array of {@link
 * org.dinopolis.gpstool.track.Track} objects.
 * @throws IOException if an error occurs during reading.
 */

  public void writeTracks(OutputStream out, List tracks)
    throws IOException;
}


