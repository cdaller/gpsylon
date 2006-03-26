/***********************************************************************
 * @(#)$RCSfile$ $Revision$
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

package org.dinopolis.gpstool.plugin.dem;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.dinopolis.gpstool.hook.MapManagerHook;
import org.dinopolis.gpstool.plugin.dem.mlt.MLT2LandSerf;
import org.dinopolis.gpstool.event.MapsChangedEvent;
import org.dinopolis.gpstool.event.MapsChangedListener;
import org.dinopolis.gpstool.gui.util.BasicLayer;
import org.dinopolis.gpstool.gui.util.ImageInfo;
import org.dinopolis.gpstool.gui.util.VisibleImage;
import org.dinopolis.util.gui.SwingWorker;

import com.bbn.openmap.event.LayerStatusEvent;
import com.bbn.openmap.proj.Projection;

// ----------------------------------------------------------------------
/**
 * This layer display and create the DEM images and is based on MultiMapLayer
 * 
 * The DEM images will be created during the caluculateVisibleImage method with LandSerf
 * 
 * @author Samuel Benz
 * @version $Revision$
 */

public class DEMLayer extends BasicLayer implements MapsChangedListener {

	private static final long serialVersionUID = 8071322972694738002L;

	Vector visible_images_;
	Object visible_images_lock_ = new Object();

	Rectangle old_clip_rect = new Rectangle();

	SwingWorker swing_worker_;
	MapManagerHook dem_manager_;
	
	MLT2LandSerf landserf_;
	String tmpPath = System.getProperty("java.io.tmpdir");
	
	double visible_map_scale_factor_ = 1.0/1.67;
	double max_visible_map_scale = 1000000;

	// ----------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 */
	public DEMLayer() {
		super();
	}
	
	/**
	 * Initializes this layer with the given plugin support.
	 * 
	 * @param dem_manager a special MapManager instance for DHM
	 */
	public void initialize(MapManagerHook dem_manager) {
		dem_manager_ = dem_manager;
		dem_manager_.addMapsChangedListener(this);
		landserf_ = new MLT2LandSerf();
		
	}
	

	/**
	 * Called when a map is added or removed.
	 *
	 * @param event the event
	 */
	  public void mapsChanged(MapsChangedEvent event) {
	    calculateVisibleImages();
	  }

	// ----------------------------------------------------------------------
	/**
	 * Paints the objects for this layer.
	 * 
	 * @param g
	 *            the graphics context.
	 */
	public void paintComponent(Graphics g) {
		if (!isActive())
			return;

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
	    float alphaValue = 1.0f;
	    float oldalphaValue;
	    int compositeRule = AlphaComposite.SRC_OVER;
	    
	    // setting map opaqueness
	    alphaValue = 0.7f;
	    
	    AlphaComposite ac;
	    ac = AlphaComposite.getInstance(compositeRule,alphaValue);
		g2.setComposite(ac);

		old_clip_rect = g2.getClipBounds(old_clip_rect);

		if (visible_images_ != null) {
			Iterator image_iterator = visible_images_.iterator();
			float scale_factor;
			while (image_iterator.hasNext()) {
				ImageInfo visible_image = (ImageInfo) image_iterator.next();
				scale_factor = visible_image.getScaleFactor();
				g2.clipRect(visible_image.getVisibleRectangleX(), visible_image
						.getVisibleRectangleY(), visible_image
						.getVisibleRectangleWidth(), visible_image
						.getVisibleRectangleHeight());
				 //System.out.println("Drawing image: "+visible_image);

				if (scale_factor < 1e-4) { // just print image
					g2.drawImage(visible_image.getImage(), (int) visible_image
							.getX(), (int) visible_image.getY(), this);
				} else // print scaled version of image
				{
					g2.drawImage(visible_image.getImage(), (int) visible_image
							.getX(), (int) visible_image.getY(),
							(int) (visible_image.getWidth()),
							(int) (visible_image.getHeight()), this);
				}
				g2.setClip(old_clip_rect); // reset to previous cliprect
			}
		}

		if ((visible_images_ == null) || (visible_images_.size() == 0)) {
			oldalphaValue = alphaValue;
			alphaValue = 1.0f;
		    ac = AlphaComposite.getInstance(compositeRule,alphaValue);
			g2.setComposite(ac);
			String text_ = "No DEM available for given position and scale";
	   		g2.setColor(Color.white);
    		g2.fillRect(10,40-9,g2.getFontMetrics().stringWidth(text_),10);
    		g2.setColor(Color.black);
    		g2.drawString(text_,10,40);
			alphaValue = oldalphaValue;
		}
	}

	
	/**
	 * Finds all dem maps that are visible at the moment and create the missing one,
	 * sets them via the setVisibleImages method and calls repaint. 
	 * Uses a SwingWorker for thetimeconsuming task.
	 */
	protected void calculateVisibleImages() {
		if (!layer_active_)
			return;

		// stop old thread
		if (swing_worker_ != null)
			swing_worker_.interrupt();

		swing_worker_ = new SwingWorker() {
			Vector worker_visible_images_ = new Vector();

			Vector worker_empty_rectangles_ = new Vector();

			public Object construct() {
				fireStatusUpdate(LayerStatusEvent.START_WORKING);
				Projection projection = getProjection();
				// find out, which images are really visible:
				Collection visible_images;
				double tmp_visible_map_scale_factor = visible_map_scale_factor_;
				// Which maps are available for the given scale (do not
				// show maps with a scale much smaller than the scale of
				// the used projection, as they would be very small and
				// unreadable). Before we do not see any maps at all,
				// display smaller maps as well:
				do {
					visible_images = dem_manager_.getAllVisibleImages(projection, tmp_visible_map_scale_factor);
					tmp_visible_map_scale_factor /= 2.0;
				} while ((visible_images.size() == 0) && (tmp_visible_map_scale_factor > 0.001) && (projection.getScale() <= max_visible_map_scale));

				worker_visible_images_ = VisibleImage.findVisibleImages(0,
						projection.getWidth(), 0, projection.getHeight(),
						visible_images, worker_visible_images_,
						worker_empty_rectangles_);
				// System.out.println("Visible images:
				// "+worker_visible_images_);
				
				if (worker_visible_images_ != null) {
					Iterator image_iterator = worker_visible_images_.iterator();
					while (image_iterator.hasNext()) {
						ImageInfo visible_image = (ImageInfo) image_iterator.next();
						String filename_ = visible_image.getMapInfo().getFilename();
						boolean exists = new File(tmpPath + "/" + MLT2LandSerf.createRasterID(filename_) + ".png").exists();
						if(!exists){
							landserf_.addRaster(landserf_.createRaster(filename_));
							// TODO: set different LandSerf parameter
							landserf_.calculateSlope();
							landserf_.calculateRelief();
			        
							landserf_.writeImage();
						}
					}
				}

				return (null);
			}

			public void finished() {
				fireStatusUpdate(LayerStatusEvent.FINISH_WORKING);
				setVisibleImages(worker_visible_images_);
				repaint();
			}
		};
		swing_worker_.start();
	}

	
	protected void setVisibleImages(Vector images) {
		synchronized (visible_images_lock_) {
			visible_images_ = images;
		}
	}

	
	
	// ----------------------------------------------------------------------
	/**
	 * This method is called from a background thread to recalulate the screen
	 * coordinates of any geographical objects. This method must store its
	 * objects and paint them in the paintComponent() method.
	 */
	protected void doCalculation() {
		calculateVisibleImages();
	}
}
