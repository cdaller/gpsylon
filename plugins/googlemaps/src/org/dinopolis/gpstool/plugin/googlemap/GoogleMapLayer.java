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

package org.dinopolis.gpstool.plugin.googlemap;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

import org.dinopolis.gpstool.gui.util.BasicLayer;

//----------------------------------------------------------------------
/**
 * This layer displays the CH1903 SwissGrid Layer
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GoogleMapLayer extends BasicLayer
{

  private static final long serialVersionUID = 1863203498655212861L;

  Color graticule_color_ = new Color(0,0,0);
  Color rect_color_ = new Color(255,255,255,175);
  Font font_ = new Font("Helvetica", java.awt.Font.PLAIN, 10);
  FontMetrics font_metrics_;

//----------------------------------------------------------------------
/**
 * Constructor
 *
 */
  public GoogleMapLayer()
  {
    super();
  }

//----------------------------------------------------------------------
/**
 * Paints the objects for this layer.
 *
 * @param g the graphics context.
 */
  public void paintComponent(Graphics g)
  {
    if(!isActive())
      return;

    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    double pixel_per_meter_ = getProjection().getScale() / GoogleMapProjection.PIXELFACT;

    String text_;
    Point pnw,pne,psw,pse;

    //int width_ = getProjection().getWidth();
    int height_ = getProjection().getHeight();
    float scale_ = getProjection().getScale();

    g2.setColor(graticule_color_);
    g2.setFont(font_);

    pnw = getProjection().forward(GoogleMapProjection.lv032ll(300000,480000));
    pne = getProjection().forward(GoogleMapProjection.lv032ll(300000,850000));
    psw = getProjection().forward(GoogleMapProjection.lv032ll(65000,480000));
    pse = getProjection().forward(GoogleMapProjection.lv032ll(65000,850000));

    // horz border
    //g2.drawLine(pnw.x,pnw.y,pne.x,pne.y);
    g2.drawLine(psw.x,psw.y,pse.x,pse.y);
    if(psw.x<0){
		g2.drawString("65000",3,psw.y-3);
	}else{
		g2.drawString("65000",psw.x-40,psw.y-3);
	}

    // vert border
    g2.drawLine(pnw.x,pnw.y,psw.x,psw.y);
    if(pse.y<height_){
		g2.drawString("480000",pnw.x+3,psw.y+13);
	}else{
		g2.drawString("480000",pnw.x+3,height_-3);
	}
    //g2.drawLine(pne.x,pne.y,pse.x,pse.y);

    int n=0;
    int e=0;

    int step_width=50000;

    if(scale_ < 750000){
    	step_width = 10000;
    }
    if(scale_ < 150000){
    	step_width = 5000;
    }
    if(scale_ < 75000){
    	step_width = 1000;
    }

    for(int i=0;i<(250000/step_width);i++){
    	g2.drawLine(pnw.x,pnw.y+n,pne.x,pne.y+n);
    	text_ = ""+(300000-(i*step_width));
    	if(pnw.x<0){
    		g2.setColor(rect_color_);
    		g2.fillRect(3,pnw.y+n-3-9,g2.getFontMetrics().stringWidth(text_),10);
    		g2.setColor(graticule_color_);
    		g2.drawString(text_,3,pnw.y+n-3);
    	}else{
    		g2.drawString(text_,pnw.x-40,pnw.y+n-3);
    	}
    	n=n+(int) (step_width/pixel_per_meter_);
    }

    for(int i=0;i<(400000/step_width);i++){
    	g2.drawLine(pne.x+e,pne.y,pse.x+e,pse.y);
    	text_ = ""+(850000-(i*step_width));
    	if(pse.y<height_){
    		g2.drawString(text_,pne.x+e+3,pse.y+13);
    	}else{
    		g2.setColor(rect_color_);
    		g2.fillRect(pne.x+3+e,height_-3-9,g2.getFontMetrics().stringWidth(text_),10);
    		g2.setColor(graticule_color_);
    		g2.drawString(text_,pne.x+3+e,height_-3);
    	}
    	e=e-(int) (step_width/pixel_per_meter_);
    }

    //Point p1 = getProjection().forward(SwissProjection.lv032ll(200000,600000));
    //Point p1 = getProjection().forward(SwissProjection.lv032ll(95295,488548));
    //g2.drawLine(0,p1.y,width_,p1.y);
	//g2.drawLine(p1.x,0,p1.x,height_);
  }

//----------------------------------------------------------------------
/**
 * This method is called from a background thread to recalulate the
 * screen coordinates of any geographical objects. This method must
 * store its objects and paint them in the paintComponent() method.
 */
  protected void doCalculation()
  {
  }


}


