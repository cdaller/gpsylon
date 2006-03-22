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
 * Class for testing LandSerf with SwissTopo MLT DEM files
 * 
 * @author Samuel Benz
 * @version $Revision$
 */

public class MLT2LandSerf
{

	GUIFrame  gisFrame;
	RasterMap raster;
	MLT mlt;
	
    //------------------- Constructor -------------------
    public MLT2LandSerf(String filename)
    {
 
        initRaster(filename);
        
    	/* Das Tool funktioniert bestens; die Bildausgabe jedoch nur mit GUIFrame.
    	   Mit einem SimpleGISFrame, hat keine graphicspane und somit kann auch kein Bild
    	   erzeugt werden. Methoden wie writeFile oder DispThread funktionieren daher nicht!
    	   
    	   Fazit: Bild generierung nur mit aufwendigem GUI möglich -> unbrauchbar für gpsylon?
    	*/
        gisFrame = new GUIFrame("MLT2LandSerf");
        gisFrame.addRaster(raster,GISFrame.PRIMARY);
    }
 
    public void calculateSlope(){
    	SurfParam param = new SurfParam(gisFrame,2);
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
    }

    public void calculateRelief(){
        // DispThread int type 2==Raster 3==Relief
        // Thread nötig da nur exportiert wird was amn sieht! addRaster macht diesen aber noch nicht sichtbar! 
        LSThread pdthread = new  DispThread(gisFrame,3);
  
        pdthread.setDaemon(true);
        pdthread.start();
        try {
            pdthread.join();    // Join thread (i.e. wait until it is complete).
        } catch (InterruptedException e) {
            System.err.println("Error: thread interrupted.");
            return;
        }
    }
    
    private void initRaster(String filename){
    	
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
        Header header = new Header("MLT2LandSerf");
        header.setNotes("from MLT2LandSerf.class: " + filename);
        raster.setHeader(header);
        
        // Find range of values and create a colour table between them.
        float min = raster.getMinAttribute();
        float max = raster.getMaxAttribute();
        raster.setColourTable(ColourTable.getPresetColourTable(ColourTable.IMHOF_L3,min,max));
    }
    
    public void writeSrfFile(){
        LandSerfIO.write(raster,"mlt2lanserf.srf");
        //LandSerfIO.write(gisFrame.getRaster1(),"/home/benz/raster1.srf");
        //LandSerfIO.write(gisFrame.getRaster2(),"/home/benz/slope.srf");
    }

    public void writeImage(){
        // Achtung: Diese Methode ist statisch obschon sie nicht statisch aufgerufen werden kann!!
        RasterMap DummyRaster = new RasterMap();
        DummyRaster.writeFile("/tmp/raster.png", FileHandler.IMAGE, gisFrame);	
    }
    
    public Image getImage(){
    	writeImage();
    	Image image = null;
        try {
            // Read from a file
            File file = new File("/tmp/raster.png");
            image = ImageIO.read(file);
        } catch (IOException e) {
        	System.err.println("Error: could not get Image /tmp/raster.png");
        }
    	return image;	
    }
    
    public void showImage(){
    	Image image = getImage();    
        // Use a label to display the image
        JFrame frame = new JFrame();
        JLabel label = new JLabel(new ImageIcon(image));
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
    
    
    public static void main(String[] args)
    {
    	if (args.length > 0 && args.length < 2) {
    		String filename = args[0];
    		MLT2LandSerf api = new MLT2LandSerf(filename);
            api.calculateSlope();
            api.calculateRelief();
            
            api.writeImage();
    		//api.showImage();
            
            //api.gisFrame.setSize(800,600);
            //api.gisFrame.setVisible(true); 
    	
            api.gisFrame.dispose();
            
            System.exit(0);
    		
    	}else{
    		System.out.println("\nUse MLT2LandSerf.class FileName");
    	}
    }
    
}