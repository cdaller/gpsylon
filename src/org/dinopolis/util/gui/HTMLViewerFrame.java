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


package org.dinopolis.util.gui;

import java.net.URL;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.io.IOException;
import java.awt.Dimension;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLDocument;



//----------------------------------------------------------------------
/**
 * This frame can be used to display html pages from an url or from a
 * String. It supports http proxies and follows links.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class HTMLViewerFrame extends JFrame implements HyperlinkListener
{
  protected JEditorPane editor_pane_;
//   protected String authentication_username_;
//   protected String authentication_password_;
  
//----------------------------------------------------------------------
/**
 * Constructor creating a (invisible) frame.
 */
  public HTMLViewerFrame()
  {
    super();
    editor_pane_ = new JEditorPane();
    editor_pane_.setEditable(false);
    JScrollPane editor_scroll_pane = new JScrollPane(editor_pane_);
//     editor_scroll_pane.setVerticalScrollBarPolicy(
//       JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    editor_scroll_pane.setPreferredSize(new Dimension(640,480));
    getContentPane().add(editor_scroll_pane);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    editor_pane_.addHyperlinkListener(this);
  }

//----------------------------------------------------------------------
/**
 * Constructor creating a (invisible) frame with the given url.
 *
 * @param url the url to display.
 */
  public HTMLViewerFrame(URL url)
    throws IOException
  {
    this();
    setPage(url);
  }

//----------------------------------------------------------------------
/**
 * Set the page to display. If a http proxy was set before, it is used.
 *
 * @param url the url to display.
 */
  public void setPage(URL url)
    throws IOException
  {
    editor_pane_.setPage(url);
    setTitle(url.toString());
  }

//----------------------------------------------------------------------
/**
 * Set the page to display.
 *
 * @param text the (html) text to display
 */
  public void setText(String text)
  {
    editor_pane_.setText(text);
  }

//----------------------------------------------------------------------
/**
 * Set the http proxy to use.
 *
 * @param proxy_host the host to use as http proxy.
 */
  public void setProxyHost(String proxy_host)
  {
    System.getProperties().put("http.proxyHost", proxy_host);
  }

//----------------------------------------------------------------------
/**
 * Set the http proxy port to use.
 *
 * @param proxy_host the port of the proxy to use as.
 */
  public void setProxyPort(int proxy_port)
  {
    System.getProperties().put("http.proxyPort", Integer.toString(proxy_port));
  }

//   public void setAuthentication(String username, String password)
//   {
//     authentication_username_ = username;
//     authentication_password_ = password;
//   }

//----------------------------------------------------------------------
/**
 * Called whenever the user clicks on a link.
 *
 * @param event
 */
  public void hyperlinkUpdate(HyperlinkEvent event)
  {
    if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
    {
      JEditorPane pane = (JEditorPane) event.getSource();
      if (event instanceof HTMLFrameHyperlinkEvent)
      {
        HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent)event;
        HTMLDocument doc = (HTMLDocument)pane.getDocument();
        doc.processHTMLFrameHyperlinkEvent(evt);
      }
      else
      {
        try
        {
          pane.setPage(event.getURL());
        }
        catch (Throwable t)
        {
          t.printStackTrace();
        }
      }
    }
  }

  
//----------------------------------------------------------------------
/**
 * A simple main method to test. Call without params to display
 * usage. The params are: ([proxy_host proxy_port] url);
 */
  public static void main(String[] args)
  {
    if(args.length == 0)
    {
      System.out.println("Usage:");
      System.out.println(HTMLViewerFrame.class.getName()+" [<proxyhost> <proxyport>] <url>");
    }
    String url;
    String proxy_host;
    String proxy_port;
    HTMLViewerFrame viewer = new HTMLViewerFrame();
    if(args.length == 3)
    {
      viewer.setProxyHost(args[0]);
      viewer.setProxyPort(Integer.parseInt(args[1]));
    }
    try
    {
      viewer.setPage(new URL(args[args.length-1]));
      viewer.setVisible(true);
    }
    catch(IOException ioe)
    {
      ioe.printStackTrace();
    }
  }
}


