/***********************************************************************
 *
 * Copyright (c) 2001 IICM, Graz University of Technology
 * Inffeldgasse 16c, A-8010 Graz, Austria.
 *
 * Copyright (c) 2003 Sven Boeckelmann
 * Langendreerstrasse 30, 44892 Bochum, Germany
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

package org.dinopolis.gpstool.plugin.kismetimport;

import org.dinopolis.gpstool.gui.layer.LocationLayer;
import org.dinopolis.gpstool.gui.layer.location.LocationMarkerSource;
import org.dinopolis.util.Resources;

/**
 * This one is only needed to get 
 * the DefaultLocationMarkerSource.
 *
 * @author sven@boeckelmann.org
 *
 */
 
public class KismetLocationLayer extends LocationLayer {
    
    /** Creates a new instance of KismetLocationLayer */
    public KismetLocationLayer(Resources resources) {
        initialize(resources, null, null, null);
    }
  
    /** public method for accessing the DefaultLocationMarkerSource
     *  @return a good source
     */
    public LocationMarkerSource getLocationMarkerSource()   {
        return getDefaultLocationMarkerSource();
    }
}
