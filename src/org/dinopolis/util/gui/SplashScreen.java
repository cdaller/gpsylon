/***********************************************************************
 * @(#)$RCSfile$   $Revision$ $Date$
 *
 * Copyright (c) 2001 IICM, Graz University of Technology
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


package org.dinopolis.util.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

//----------------------------------------------------------------------
/**
 * This class is used to visualize a simple splash screen icon for a
 * definable amount of time.
 *
 * @author Dieter Freismuth
 * @version $Revision$
 */

public class SplashScreen extends JWindow 
{
  //----------------------------------------------------------------------
  /**
   * Constructor taking an Icon that is to be displayed and the time
   * to display this icon.
   *
   * @param banner the icon to display.
   * @param display_millis the amount of milliseconds to display the
   * banner.
   */

  public SplashScreen(Icon banner, int display_millis)
  {
    this(new JLabel(banner), display_millis);
  }

  //----------------------------------------------------------------------
  /**
   * Constructor taking a String that is to be displayed and the time
   * to display it.
   *
   * @param text the text to display.
   * @param display_millis the amount of milliseconds to display the
   * banner.
   */

  public SplashScreen(String text, int display_millis)
  {
    this(new JLabel(text), display_millis);
  }

  //----------------------------------------------------------------------
  /**
   * Constructor taking a label that is to be displayed and the time
   * to display it.
   *
   * @param label the label to display.
   * @param display_millis the amount of milliseconds to display the
   * banner.
   */

  public SplashScreen(JLabel label, int display_millis)
  {
    getContentPane().add(label, BorderLayout.CENTER);
    pack();
    Dimension screen_size =
      Toolkit.getDefaultToolkit().getScreenSize();
    Dimension label_size = label.getPreferredSize();
    setLocation(screen_size.width/2 - (label_size.width/2),
                screen_size.height/2 - (label_size.height/2));
    addMouseListener(new MouseAdapter() 
      {
        public void mousePressed(MouseEvent e)
        {
          setVisible(false);
          dispose();
        }
      });

    final Runnable closer = new Runnable()
      {
        public void run()
        {
          setVisible(false);
          dispose();
        }
      };
    final int display_millis_ = display_millis;
    setVisible(true);
    if (display_millis_ >= 0)
    {
      Runnable waiter = new Runnable()
        {
          public void run()
          {
            try
            {
              Thread.sleep(display_millis_);
              SwingUtilities.invokeAndWait(closer);
            }
            catch(Exception e)
            {
              e.printStackTrace();
            }
          }
      };
      Thread splash = new Thread(waiter, "SplashThread");
      splash.start();
    }
  }

  //----------------------------------------------------------------------
  /**
   * @param args the command line args, will be ignored
   */

  public static void main(String[] arguments)
  {
    new SplashScreen("test", -1);
  }

}

