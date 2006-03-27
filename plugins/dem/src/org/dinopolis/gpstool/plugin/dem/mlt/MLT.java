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

package org.dinopolis.gpstool.plugin.dem.mlt;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;

import org.dinopolis.util.Debug;

//----------------------------------------------------------------------
/**
 * Holds and load a MLT file.
 * MLT is a DEM file format from swisstopo
 * see: http://www.swisstopo.ch
 * 
 * @author Samuel Benz
 * @version $Revision$
 */

public class MLT {

	protected String filename_;
	protected int width_;
	protected int height_;
	protected int resolution_;
	protected int[] elevations_;

	public MLT() {

	}
	
	public MLT(String filename) {
		filename_ = filename;
		loadMLT();
	}

	// ----------------------------------------------------------------------
	/**
	 * Get the height.
	 * 
	 * @return the height.
	 */
	public int getHeight() {
		return (height_);
	}

	// ----------------------------------------------------------------------
	/**
	 * Set the height.
	 * 
	 * @param height
	 *            the height.
	 */
	public void setHeight(int height) {
		height_ = height;
	}

	// ----------------------------------------------------------------------
	/**
	 * Get the width.
	 * 
	 * @return the width.
	 */
	public int getWidth() {
		return (width_);
	}

	// ----------------------------------------------------------------------
	/**
	 * Set the width.
	 * 
	 * @param width
	 *            the width.
	 */
	public void setWidth(int width) {
		width_ = width;
	}

	// ----------------------------------------------------------------------
	/**
	 * Get the resolution
	 * 
	 * @return the resolution
	 */
	public int getResloution() {
		return resolution_;
	}

	// ----------------------------------------------------------------------
	/**
	 * Set the resolution.
	 * 
	 * @param resolution
	 *            the resolution.
	 */
	public void setResolution(int resolution) {
		resolution_ = resolution;
	}

	// ----------------------------------------------------------------------
	/**
	 * Get the elevations
	 * 
	 * @return the elevations
	 */
	public int[] getElevations() {
		return elevations_;
	}

	// ----------------------------------------------------------------------
	/**
	 * Set the elevations.
	 * 
	 * @param elevations
	 *            the elevations.
	 */
	public void setElevations(int[] elevations) {
		elevations_ = elevations;
	}

	//	 ----------------------------------------------------------------------
	/**
	 * Loads a MLT File
	 * 
	 */
	private void loadMLT() {	

		// some default values (DHM25)
		int width = 701;
		int height = 481;
		int resolution = 25;
		
		try {
			BufferedReader inStream = new BufferedReader(new FileReader(filename_));
			// Skip Header Information
			while (inStream.ready()) {
				String line = inStream.readLine();
				//System.out.println(line);
				if (line.startsWith("MASCHENWEITE")){
					//System.out.println(line);
				    try {
						RE res_exp = new RE("\\d+");
						REMatch res_match = res_exp.getMatch(line);
						resolution = new Integer(res_match.toString()).intValue();
						//System.out.println(resolution);
					} catch (REException e) {

						e.printStackTrace();
					}
				}
				if (line.startsWith("MATRIXDIMENSIONEN")){
					//System.out.println(line);
				    try {
						RE dim_exp = new RE("\\d+");
						REMatch[] dim_match = dim_exp.getAllMatches(line);
						width = new Integer(dim_match[0].toString()).intValue();
						height = new Integer(dim_match[1].toString()).intValue();
						//System.out.println(width + " " + height);
					} catch (REException e) {

						e.printStackTrace();
					}
				}
				if (line.startsWith("ENDHEADER")) {
					break;
				}
			}
			this.setWidth(width);
			this.setHeight(height);
			this.setResolution(resolution);
			
			//int[] heights = (int[])Array.newInstance(int.class, this.height_*this.width_);
			// some mlt matrix are larger than height*width !!!
			int[] heights = (int[])Array.newInstance(int.class, 800*800);
			
			String[] Elevation = null;
			int counter = 0;
			while (inStream.ready()) {
				String dataline = inStream.readLine().trim();
				Elevation = dataline.split(" +");
				for (int i = 0; i < Elevation.length; i++) {
					//System.out.println(Elevation[i]);
					heights[counter] = new Integer(Elevation[i]).intValue();
					counter++;
				}

			}
			inStream.close();
			this.setElevations(heights);
			
		} catch (FileNotFoundException e) {
			Debug.println("MLT loadMLT(): file " + filename_ + " not found");
		} catch (IOException e) {
			Debug.println("MLT loadMLT(): File IO Error!\n" + e.toString());
		}
	}
}
