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

import com.bbn.openmap.Layer;
import java.awt.Graphics;
import org.dinopolis.util.gui.SwingWorker;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.event.LayerStatusEvent;


//----------------------------------------------------------------------
/**
 * LayerPlugins provide a layer in the application to display
 * geographical data. This means they are informed about any changes
 * of the projection (zoom in, out, move, etc.) and have to react
 * accordingly. Usually this reaction is to recalculate the data
 * (calculate the screen coordinates from the geographical (WGS84)
 * coordinates). This task is done by using the
 * <code>forward(...)</code> methods of the projection passed in the
 * <code>projectionChanged()</code> method. This calculation should be
 * done in a separate task, so the {@link #paintCompontent()} method
 * terminates as fast as possible. Use a {@link
 * org.dinopolis.util.gui.SwingWorker} for this purpose.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public abstract class BasicLayerPlugin extends LayerPlugin
{
  protected SwingWorker swing_worker_;
  protected boolean layer_active_ = true;

//----------------------------------------------------------------------
/**
 * This method is called from a background thread to recalulate the
 * screen coordinates of any geographical objects. This method must
 * store its objects and paint them in the paintComponent() method.
 */
  protected void doCalculation()
  {
  }

//----------------------------------------------------------------------
/**
 * This method is called whenever the projection changed or the layer
 * was activated. It starts a SwingWorker and calls the {@link
 * #doCalculation()} method in another thread. After this method
 * finished, a repaint() is initialted.
 */
  protected void recalculateCoordinates()
  {
    if(!layer_active_)
      return;
        // stop old thread
    if(swing_worker_ != null)
      swing_worker_.interrupt();

    swing_worker_ = new SwingWorker()
      {
        
        public Object construct()
        {
          fireStatusUpdate(LayerStatusEvent.START_WORKING);
          doCalculation();
          return(null);
        }

        public void finished()
        {
          fireStatusUpdate(LayerStatusEvent.FINISH_WORKING);
          repaint();
        }
      };
    swing_worker_.start();
  }

  public void paintCompontent(Graphics g)
  {
  }

//----------------------------------------------------------------------
/**
 * Called by the application to switch the layer on or off. If the
 * layer is switched off, it must not paint anything and should not
 * consume any calculational power.
 *
 * @param active if <code>true</code> the layer is switched on and
 * should react on changes of the projection and draw anything in the
 * paintComponent method.
 */
  public void setActive(boolean active)
  {
    layer_active_ = active;
    if(layer_active_)
      recalculateCoordinates();
    repaint();
  }

  
//----------------------------------------------------------------------
// ProjectionListener interface implementation
//----------------------------------------------------------------------

//----------------------------------------------------------------------
/**
 * Handler for <code>ProjectionEvent</code>s.  This function is
 * invoked when the <code>MapBean</code> projection changes.  The
 * graphics are reprojected and then the Layer is repainted.
 * <p>
 * @param event the projection event
 */
  public void projectionChanged(ProjectionEvent event)
  {
    Projection proj = event.getProjection();

    if(proj == null)
      return;

    setProjection(proj.makeClone());
      
    recalculateCoordinates();
  }


}


