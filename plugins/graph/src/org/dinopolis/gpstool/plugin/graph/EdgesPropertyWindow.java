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


package org.dinopolis.gpstool.plugin.graph;


import java.awt.GridLayout;
import java.awt.event.*;
import java.text.DecimalFormat;

import javax.swing.*;



//----------------------------------------------------------------------
/**
 * This class is used to store information about edges (ID,Name,start Node, end Node,
 * all needed points to draw the path)
 *
 * @author Wolfgang Auer
 * @version $Revision$
 */



public class EdgesPropertyWindow extends JFrame
{
		JLabel label1;
		JLabel label2;
		JLabel label3;
		JLabel label4;
		JLabel label5;
		JLabel label6;
		JTextField text_field1;
		JTextField text_field2;
		JTextField text_field3;
		JTextField text_field4;
		JTextField text_field5;
		JButton apply;
		JButton cancel;
		JComboBox typelist;
		JRadioButton oneway_button;
		DecimalFormat df;
		GraphLayer layer_;

	//----------------------------------------------------------------------
	/**
	 * Constructor for the property window
	 * @param graph layer
	 * @param the value of the id
	 * @param the value of the name
	 * @param the value of the length
	 * @param the value of the oneway
	 * @param the value of the edgetype
	 */
 	public EdgesPropertyWindow(GraphLayer layer,int id,String name,double length,boolean oneway, int edgetype)
	{
		super("Edge Properties");

		KeyActionListener listener = new KeyActionListener();
		this.addKeyListener(listener);
		layer_ = layer;

    df = new DecimalFormat( "0.000" );
		String[] data = {"Highway", "Normal Street", "Fast Street", "Motor Way"};
 		typelist = new JComboBox(data);

 		typelist.setSelectedIndex(edgetype);
 		//dataList.getSelectedValue();


		label1 = new JLabel("Edge ID:");
		label2 = new JLabel("Edge Name:");
		label3 = new JLabel("Edge Length: ");
		label4 = new JLabel("Oneway: ");
		label5 = new JLabel("Edge Type: ");
		Integer i = new Integer(id);
		text_field1 = new JTextField(i.toString());
		text_field1.setEditable(false);
		text_field2 = new JTextField(name);
		text_field3 = new JTextField(df.format( length/1000 )+"km");
		text_field3.setEditable(false);
		oneway_button = new JRadioButton();
    oneway_button.setSelected(oneway);



		Integer ii = new Integer(edgetype);
		text_field5 = new JTextField(ii.toString());
		JPanel pane = new JPanel();
		apply = new JButton("Apply");
		apply.addActionListener(new ButtonActionListener());
		cancel = new JButton("Cancel");
		cancel.addActionListener(new ButtonActionListener());
		GridLayout layout = new GridLayout(10,2);

		pane.setLayout(layout);

		pane.add(new JLabel());
		pane.add(new JLabel());
		pane.add(label1);
		pane.add(text_field1);
		pane.add(label2);
		pane.add(text_field2);
		pane.add(label3);
		pane.add(text_field3);
		pane.add(label4);
		pane.add(oneway_button);
		pane.add(label5);
	  pane.add(typelist);
		pane.add(new JLabel());
		pane.add(new JLabel());
		label6 = new JLabel();
		pane.add(label6);
		pane.add(new JLabel());
		pane.add(new JLabel());
		pane.add(new JLabel());
		pane.add(apply);
		pane.add(cancel);

		setContentPane(pane);
	}

	//----------------------------------------------------------------------
	/**
	 * Updates the data in the proberty window
	 * @param the value of the id
	 * @param the value of the name
	 * @param the value of the length
	 * @param the value of the oneway
	 * @param the value of the edgetype
	 * @param if one edge is selected (true) or more (false)
	 */
	public void update(int id,String name,double length,boolean oneway, int edgetype,boolean one_selected)
	{
	 		Integer i = new Integer(id);
 			Integer ii = new Integer(edgetype);
 			typelist.setSelectedIndex(edgetype);
 			text_field1.setText(i.toString());
 			text_field2.setText(name);
 			text_field3.setText(df.format( length/1000 ) + "km");

			if(one_selected)
			{
				oneway_button.setEnabled(true);
				label1.setText("Edge ID:");
				label2.setText("Edge Name:");
				label5.setText("Edge Type:");
			}
			else
			{
				oneway_button.setEnabled(false);
				label1.setText("Nr. of selected edges:");
				label2.setText("Edge Name (last edge):");
				label5.setText("Edge Type (last edge):");
			}
			oneway_button.setSelected(oneway);
      text_field5.setText(ii.toString());
      label6.setText("");

	}

	//----------------------------------------------------------------------
	/**
	 * Button action listener to save the proberties of the edge or to close
	 * the window
	 */
	class ButtonActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if(e.getSource().equals(apply))
			{
				label6.setText("saved...");
				layer_.changeEdgePorperties(text_field2.getText(),oneway_button.isSelected(),typelist.getSelectedIndex());
			}

			if(e.getSource().equals(cancel))
			{
				layer_.closeWindow();
			}


		}

	}

	//----------------------------------------------------------------------
	/**
	 * Key action listener for the delete key, to delete the selected edge
	 */
	class KeyActionListener implements KeyListener
	{
		public void keyReleased(KeyEvent e)
		{
    	if(e.getKeyCode() == KeyEvent.VK_DELETE)
    		layer_.deleteSelectedParts();
		}

		public void keyTyped(KeyEvent e)
		{
		}

		public void keyPressed(KeyEvent e)
		{
		}

	}
}