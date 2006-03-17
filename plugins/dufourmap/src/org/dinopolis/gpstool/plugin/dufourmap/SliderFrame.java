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

package org.dinopolis.gpstool.plugin.dufourmap;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

//----------------------------------------------------------------------
/**
 * SliderFrame for setAlphaChannel on Dufourmaps
 * 
 * @author Samuel Benz
 * @version $Revision$
 */

public class SliderFrame extends JPanel implements ActionListener,WindowListener,ChangeListener {
                                 
	
	private static final long serialVersionUID = -6302133054563154581L;
	
	DufourmapLayer maplayer;
	
	
    public SliderFrame(DufourmapLayer layer_) {
    	
    	maplayer = layer_;
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        //Create the label.
        JLabel sliderLabel = new JLabel("Map opaqueness", JLabel.CENTER);
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Create the slider.
        float initSlide = maplayer.getAlphaValue()*100;
        JSlider alphaChannel = new JSlider(JSlider.HORIZONTAL,0,100,(int)initSlide);
                                        
        alphaChannel.addChangeListener(this);

        //Turn on labels at major tick marks.
        alphaChannel.setMajorTickSpacing(50);
        alphaChannel.setMinorTickSpacing(5);
        alphaChannel.setPaintTicks(true);
        alphaChannel.setPaintLabels(true);
        alphaChannel.setBorder(
        BorderFactory.createEmptyBorder(0,0,10,0));

        //Put everything together.
        add(sliderLabel);
        add(alphaChannel);
    }

    /** Add a listener for window events. */
    void addWindowListener(Window w) {
        w.addWindowListener(this);
    }

    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}
    public void windowClosing(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    public void actionPerformed(ActionEvent e) {}
    
    /** Listen to the slider. */
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
            float alphaValue = (float)source.getValue();
            maplayer.setAlphaValue(alphaValue/100);
            //System.out.println(alphaValue/100);
        }
    }
}
