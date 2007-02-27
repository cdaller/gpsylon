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

import java.awt.BorderLayout;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import jwo.landserf.gui.GISFrame;
import jwo.landserf.gui.GUIFrame;
import jwo.landserf.process.DispThread;
import jwo.landserf.process.LSThread;
import jwo.landserf.process.SurfParam;
import jwo.landserf.process.SurfParamThread;
import jwo.landserf.process.io.FileHandler;
import jwo.landserf.process.io.LandSerfIO;
import jwo.landserf.structure.ColourTable;
import jwo.landserf.structure.Footprint;
import jwo.landserf.structure.Header;
import jwo.landserf.structure.RasterMap;

//----------------------------------------------------------------------
/**
 * A kind of a api, for integration LandSerf in gpsylon.
 * Currently for Swiss mlt dhm files
 * 
 * @author Samuel Benz
 * @version $Revision$
 */

public class MLT2LandSerf
{

	GUIFrame  gisFrame;
	RasterMap raster;
	String raster_id;
	MLT mlt;
	
	/* tmp dir for image creation */
	public static String tmpPath = System.getProperty("java.io.tmpdir");
	/* calculate slope aspect */
	final public static int ASPECT = 1;
	/* calculate slope angle */
	final public static int SLOPE = 2;
	/* caculate slope angle with special color */
	final public static int AVALANCHE = 3;
	/* display without relief shading */
	final public static int PLAIN = 4;
	/* display with relief shading */
	final public static int RELIEF = 5;
	
    //------------------- Constructor -------------------
    public MLT2LandSerf()
    {   
        gisFrame = new GUIFrame("MLT2LandSerf");
    }
 
	//----------------------------------------------------------------------
	/**
	 * Add a RasterMap to a LandSerf GUIFrame
	 * 
	 * @param raster_ A RasterMap object for adding to the GUIFrame
	 */    
    public void addRaster(RasterMap raster_){
    	gisFrame.addRaster(raster_,GISFrame.PRIMARY);	
    }
    
	//----------------------------------------------------------------------
	/**
	 * Caculates the slope angle from primary raster with a SurfParamThread from LandSerf
	 * 
	 * see: http://www.soi.city.ac.uk/~jwo/landserf/landserf220/doc/programming/chapter4.html
	 * 
	 */
    public void calculateSlope(){
    	calculateSlope(MLT2LandSerf.SLOPE);
    }
    
	//----------------------------------------------------------------------
	/**
	 * Caculates the slope angle or aspect from primary raster with a SurfParamThread from LandSerf
	 * 
	 * see: http://www.soi.city.ac.uk/~jwo/landserf/landserf220/doc/programming/chapter4.html
	 * 
	 */
    public void calculateSlope(int type){
    	
    	SurfParam param;
    	if(type == MLT2LandSerf.ASPECT){
    		param = new SurfParam(gisFrame,3);
    	}else{
    		param = new SurfParam(gisFrame,2);
    	}
        SurfParamThread slope = new SurfParamThread(gisFrame,param);
        
        slope.setDaemon(true);
        slope.start();
        try
        {
            slope.join();    // Join thread (i.e. wait until it is complete).
        }
        catch (InterruptedException e)
        {
            System.err.println("Error: thread interrupted.");
            return;
        }
        
        if(type == MLT2LandSerf.AVALANCHE){
        	ColourTable col = new ColourTable();
        	col.addContinuousColourRule((float) 0.0,255,255,255);
        	col.addContinuousColourRule((float) 29.9,255,255,255);
        	col.addContinuousColourRule((float) 30.0,255,255,100);
        	col.addContinuousColourRule((float) 34.9,255,255,100);
        	col.addContinuousColourRule((float) 35.0,255,149,155);
        	col.addContinuousColourRule((float) 39.9,255,149,155);
        	col.addContinuousColourRule((float) 40.0,201,154,255);
        	col.addContinuousColourRule((float) 44.9,201,154,255);
        	col.addContinuousColourRule((float) 45.0,210,213,210);
        	col.addContinuousColourRule((float) 90.0,210,213,210);
        	gisFrame.getRaster2().setColourTable(col);
        }else if(type == MLT2LandSerf.ASPECT){
        	gisFrame.getRaster2().setColourTable(ColourTable.getPresetColourTable(ColourTable.ASPECT,0,360));
        }else{
        	gisFrame.getRaster2().setColourTable(ColourTable.getPresetColourTable(ColourTable.SLOPE,0,90));
        }
    }

	//----------------------------------------------------------------------
	/**
	 * Relief shading with primary spatial objects and optional secondary raster
	 * with a DispThread from LandSerf
	 * 
	 * see: http://www.soi.city.ac.uk/~jwo/landserf/landserf220/doc/programming/chapter4.html
	 *
	 */
    public void calculateRelief(){
    	calculateRelief(MLT2LandSerf.RELIEF);
    }
    
	//----------------------------------------------------------------------
	/**
	 * Relief shading with primary spatial objects and optional secondary raster
	 * with a DispThread from LandSerf
	 * 
	 * see: http://www.soi.city.ac.uk/~jwo/landserf/landserf220/doc/programming/chapter4.html
	 *
	 */
    public void calculateRelief(int type){
        // DispThread int type 2==Raster 3==Relief
        // Thread n�tig da nur exportiert wird was man sieht! addRaster macht diesen aber noch nicht sichtbar! 
    	LSThread pdthread;
    	
    	if(type == MLT2LandSerf.PLAIN){
    		// display only slope raster2
    		gisFrame.setRaster1(gisFrame.getRaster2());
    		pdthread = new  DispThread(gisFrame,2);
    	}else if(type == MLT2LandSerf.RELIEF){
    		// display slope raster2 with relief from raster1
    		pdthread = new  DispThread(gisFrame,3);
    	}else{
    		pdthread = new  DispThread(gisFrame,3);
    	}
    		
        pdthread.setDaemon(true);
        pdthread.start();
        try {
            pdthread.join();    // Join thread (i.e. wait until it is complete).
        } catch (InterruptedException e) {
            System.err.println("Error: thread interrupted.");
            return;
        }
    }
    
	//----------------------------------------------------------------------
	/**
	 * Creating a RasterMap Object from a MLT file and setting a unique ID.
	 *
	 * @param filename Filename from the MLT DEM file
	 * @return a RasterMap object for using in LandSerf
	 */
    public RasterMap createRaster(String filename){
    	
    	mlt = new MLT(filename);
    	raster = new RasterMap(mlt.getHeight(),mlt.getWidth(),new Footprint(0,0,mlt.getResloution(),mlt.getResloution()));
        
        int[] dhm = mlt.getElevations();
        int counter = 0;
        for (int row=0; row<raster.getNumRows();row++){
            for (int col=0; col<raster.getNumCols();col++){
                raster.setAttribute(row,col,(float)dhm[counter]/10);
                counter++;
            }
        }         
                
        // Add some simple metadata.
        Header header = new Header(filename);
        header.setNotes("from MLT2LandSerf.class: " + filename);
        raster.setHeader(header);
        
        // Find range of values and create a colour table between them.
        float min = raster.getMinAttribute();
        float max = raster.getMaxAttribute();
        raster.setColourTable(ColourTable.getPresetColourTable(ColourTable.IMHOF_L3,min,max));
   
        // setting unique ID
        setRasterID(createRasterID(filename));
        
        return raster;
    }
    
	//----------------------------------------------------------------------
	/**
	 * Creating a MD5 hash from filename to identify the created images
	 * 
	 * @param instring filename with path
	 * @return MD5 hash for image creating/loading
	 */
    public static String createRasterID(String instring){
    	StringBuffer ID = new StringBuffer();
    	ID.append("landserf_");
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte digest[] = md.digest(instring.getBytes());
			for (int i=0;i<digest.length;i++)
				ID.append(Integer.toHexString( digest[i]&0xff));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
    	return ID.toString();
    }
    
	//----------------------------------------------------------------------
	/**
	 * Save the RasterMap to disc. Useful for testing
	 *
	 * @param filename_ Filename
	 */
    public void writeSrfFile(String filename_){
        LandSerfIO.write(raster,filename_);
        //LandSerfIO.write(gisFrame.getRaster1(),MLT2LandSerf.tmpPath + "/raster1.srf");
        //LandSerfIO.write(gisFrame.getRaster2(),MLT2LandSerf.tmpPath + "/raster2.srf");
    }

	//----------------------------------------------------------------------
	/**
	 * Export the graphics pane from GUIFrame to an png image and save this to disc.
	 * 
	 * TODO: fix LandSerf bug. border pixel are exported white
	 * 
	 * @param filename_ Filename
	 */
    public void writeImage(String filename_){
        // Achtung: Diese Methode ist statisch obschon sie nicht statisch aufgerufen werden kann!!
        RasterMap DummyRaster = new RasterMap();
        DummyRaster.writeFile(filename_, FileHandler.IMAGE, gisFrame);	
    }
    
	//----------------------------------------------------------------------
	/**
	 * Save the image to disc an load them from disc.
	 * LandSerf does not support exporting image objects directly!
	 * 
	 * @return image Load the image from disc
	 * @param filename_ Filename
	 */
    public Image getImage(String filename_){
    	writeImage(filename_);
    	Image image = null;
        try {
            // Read from a file
            image = ImageIO.read(new File(filename_));
        } catch (IOException e) {
        	System.err.println("Error: could not get Image " + filename_);
        }
    	return image;	
    }
    
	//----------------------------------------------------------------------
	/**
	 * Show the generated image in a grpahics pane.
	 * Useful for testing debbuging
	 * 	 
	 * @param filename_ Filename
	 */
    public void showImage(String filename_){
    	Image image = getImage(filename_);    
        // Use a label to display the image
        JFrame frame = new JFrame();
        JLabel label = new JLabel(new ImageIcon(image));
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
    
	//----------------------------------------------------------------------
	/**
	 * Set the rasterID
	 * 
	 * @param raster_id_ the unique raster ID string
	 */
    private void setRasterID(String raster_id_){
    	raster_id = raster_id_;
    }
    
	//----------------------------------------------------------------------
	/**
	 * Get the rasterID
	 * 
	 * @return raster_id the unique raster ID string
	 */
    public String getRasterID(){
    	return raster_id;
    }


	//----------------------------------------------------------------------
	/**
	 * Clear all pre generated dem images
	 * \rm -f java.io.tmpdir + "landserf_*"
	 * 
	 */
    public static void clearDEMCache(){
    	File dir = new File(MLT2LandSerf.tmpPath);
    	
        File[] files = dir.listFiles();
        
        for(int i=0;i<files.length;i++){
        	if(files[i].toString().matches(".*landserf_.*")){
        		//System.out.println(files[i].toString());
        		files[i].delete();
        	}
        }
    }

    
    public static void main(String[] args)
    {

    	MLT2LandSerf.clearDEMCache();
    	
		MLT2LandSerf api = new MLT2LandSerf();
		
		String[] maps = {"/opt/map/data/dhm25/MM1091.MLT","/opt/map/data/dhm25/MM1191.MLT"};
		//String[] maps = {"/opt/map/data/dhm25/ch1000.mlt"};
		
		for(int i=0;i < maps.length;i++){
			String file = MLT2LandSerf.tmpPath + "/" + MLT2LandSerf.createRasterID(maps[i]) + ".png";
			boolean exists = new File(file).exists();
			if(!exists){
				api.addRaster(api.createRaster(maps[i]));
				api.calculateSlope(MLT2LandSerf.AVALANCHE);
				api.calculateRelief(MLT2LandSerf.PLAIN);
        
				api.writeImage(file);
				//api.writeSrfFile("/tmp/reaster.srf");
				//api.showImage(file);
			}
		}
		
        //api.gisFrame.setSize(800,600);
        //api.gisFrame.setVisible(true); 
	
        api.gisFrame.dispose();
        System.exit(0);
		
    }
}