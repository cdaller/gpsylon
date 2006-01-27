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
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

//----------------------------------------------------------------------
/**
 * This class is used to visualize a simple splash screen icon for a
 * definable amount of time. Additionally, a progress bar and a status
 * line can be displayed.
 *
 * @author Dieter Freismuth
 * @version $Revision$
 */

public class SplashScreen extends JWindow 
{

  JProgressBar progress_bar_;
  JLabel status_line_;
  
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
   * Constructor taking an Icon that is to be displayed and the time
   * to display this icon. Additionally, the min/max values for the
   * progress bar can be set. If both are equal to zero, no progress
   * bar and status line are displayed.
   *
   * @param banner the icon to display.
   * @param display_millis the amount of milliseconds to display the
   * banner.
   * @param progress_min_value the min value for the progress bar.
   * @param progress_max_value the max value for the progress bar.
   */

  public SplashScreen(Icon banner, int display_millis,
                      int progress_min_value, int progress_max_value)
  {
    this(new JLabel(banner), display_millis, progress_min_value, progress_max_value);
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
   * Constructor taking a String that is to be displayed and the time
   * to display it. Additionally, the min/max values for the progress
   * bar can be set. If both are equal to zero, no progress bar and
   * status line are displayed.
   *
   * @param text the text to display.
   * @param display_millis the amount of milliseconds to display the
   * banner.
   * @param progress_min_value the min value for the progress bar.
   * @param progress_max_value the max value for the progress bar.
   */

  public SplashScreen(String text, int display_millis,
                      int progress_min_value, int progress_max_value)
  {
    this(new JLabel(text), display_millis, progress_min_value, progress_max_value);
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
    this(label,display_millis,0,0);
  }

  
  //----------------------------------------------------------------------
  /**
   * Constructor taking a label that is to be displayed and the time
   * to display it. Additionally, the min/max values for the progress
   * bar can be set. If both are equal to zero, no progress bar and
   * status line are displayed.
   *
   * @param label the label to display.
   * @param display_millis the amount of milliseconds to display the
   * banner, if negative, the splash screen is not deactivated 
   * automatically.
   * @param progress_min_value the min value for the progress bar.
   * @param progress_max_value the max value for the progress bar.
   */

  public SplashScreen(JLabel label, int display_millis,
                      int progress_min_value, int progress_max_value)
  {
    getContentPane().add(label, BorderLayout.CENTER);
    if((progress_min_value != 0) || (progress_max_value != 0))
    {
      JPanel status_panel = new JPanel(new BorderLayout());
      status_panel.add(progress_bar_ = new JProgressBar(progress_min_value,progress_max_value),
                       BorderLayout.CENTER);
      status_panel.add(status_line_ = new JLabel(),BorderLayout.SOUTH);
      getContentPane().add(status_panel,BorderLayout.SOUTH);
    }
    pack();
    Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension label_size = getContentPane().getPreferredSize();
    setLocation(screen_size.width/2 - (label_size.width/2),
                screen_size.height/2 - (label_size.height/2));
    addMouseListener(new MouseAdapter() 
      {
        public void mousePressed(MouseEvent e)
        {
          close();
        }
      });

    final Runnable closer = new Runnable()
      {
        public void run()
        {
          close();
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
      splash.setDaemon(true);
      splash.start();
    }
  }

  //----------------------------------------------------------------------
  /**
   * Set the status and the current progress for the splash screen.
   *
   * @param text the text to be displayed in the status line.
   * @param progress_value the percentage of the progress.
   */
  public void setStatus(String text, int progress_value)
  {
    setStatus(text);
    setProgress(progress_value);
  }

  //----------------------------------------------------------------------
  /**
   * Set the status for the splash screen.
   *
   * @param text the text to be displayed in the status line.
   */
  public void setStatus(String text)
  {
    if(status_line_ != null)
      status_line_.setText(text);
  }

  //----------------------------------------------------------------------
  /**
   * Set the current progress for the splash screen.
   *
   * @param progress_value the percentage of the progress.
   */
  public void setProgress(int progress_value)
  {
    if(progress_bar_ != null)
      progress_bar_.setValue(progress_value);
  }


  //----------------------------------------------------------------------
  /**
   * Close the splash screen and frees all resources.
   */
  public void close()
  {
    setVisible(false);
    dispose();
  }

  //----------------------------------------------------------------------
  /**
   * @param arguments the command line args, will be ignored
   */

  public static void main(String[] arguments)
  {
    SplashScreen splash = new SplashScreen(new ImageIcon("/filer/cdaller/texte/pic/cdaller.jpg"), 5000,0,100);
    splash.setStatus("loading plugins...",10);
    try
    {
      Thread.sleep(500);
    }
    catch(Exception ignore) {}
    splash.setStatus("initialize plugins...",20);
    try
    {
      Thread.sleep(500);
    }
    catch(Exception ignore) {}
    splash.setStatus("start plugin 1...",30);
    try
    {
      Thread.sleep(500);
    }
    catch(Exception ignore) {}
    splash.setStatus("start another plugin ...",50);
    try
    {
      Thread.sleep(500);
    }
    catch(Exception ignore) {}
    splash.setStatus("finish first plugin...",70);
    try
    {
      Thread.sleep(500);
    }
    catch(Exception ignore) {}
    splash.setStatus("open window...",100);
    
  }

}

