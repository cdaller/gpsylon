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
 * Free Software Foundation, Inc., a
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 ***********************************************************************/

package org.dinopolis.gpstool.plugin.imageserver;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;


//----------------------------------------------------------------------
/**
 * This class is a very simple (but multithreaded) webserver. The only content
 * it provides is a default page and a screenshot of a Component.
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class WebServer extends Thread
{
  protected int port_;
  public static final int DEFAULT_PORT = 10080;
  public static final int SERVER_SOCKET_TIMEOUT = 60000;  // in milliseconds (1min)
  protected ServerSocket server_socket_;
  protected boolean thread_run_ = true;
  protected Component component_;
  protected String default_page_;
  protected SimpleDateFormat date_format_ = new SimpleDateFormat("EEE, dd-MMM-yy HH:mm:ss z");
  protected String screenshot_url_;

//----------------------------------------------------------------------
/**
 * Initializes and starts the listener threads.
 *
 * @param port the port to listen to.
 * @param default_page the default html page to return on request to
 * "/" or "index.html".
 * @param screenshot_filename the filename that provides the
 * screenshot.
 */
  public WebServer(int port, String default_page, String screenshot_filename)
  {
    default_page_ = default_page;
    screenshot_url_ = screenshot_filename;
    try
    {
      System.err.println("starting webserver on port "+port);
      server_socket_ = new ServerSocket (port);
      start();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }


//----------------------------------------------------------------------
/**
 * Stops the listener threads.
 */
  public void stopServer()
  {
    thread_run_ = false;
  }

//----------------------------------------------------------------------
/**
 * Set the component that is used to create the screeshot.
 *
 * @param component the component to produce a screenshot from.
 */
  public void setComponent(Component component)
  {
    component_ = component;
  }



  public void run()
  {
    try
    {
      server_socket_.setSoTimeout(SERVER_SOCKET_TIMEOUT);
      while (thread_run_)
      {
        try
        {
          new WebServerClientConnection(server_socket_.accept() );
        }
        catch (InterruptedIOException iioe)
        {
        }
      }
    }
    catch (IOException ioe)
    {
    }
    try
    {
      server_socket_.close();
    }
    catch (IOException ioe)
    {
    }
  }


//----------------------------------------------------------------------
/**
 * Returns an image of the component.
 *
 * @return an image of the component.
 */
  public static BufferedImage getComponentScreenShot(Component component)
  {
    BufferedImage image = (BufferedImage)component.createImage(component.getWidth(),component.getHeight());
    Graphics2D g2 = image.createGraphics();
    component.paint(g2);
    return (image);
  }


//----------------------------------------------------------------------
/**
 * Writes the data to the given output stream.
 *
 * @param component the component to write.
 * @param out the outputstream to write the data to.
 * @throws IOException if an error occurs during writing.
 */
  public void writeComponent(Component component, OutputStream out)
    throws IOException
  {
    BufferedImage image = getComponentScreenShot(component);
    ImageIO.write(image,"PNG",out);
  }

  

  public static void main(String[] args)
  {
    WebServer server = new WebServer(DEFAULT_PORT,"no default page","gpsylon.png");
  }
  
//----------------------------------------------------------------------
// inner class
/**
 */
  class WebServerClientConnection extends Thread
  {
    protected Socket socket_;
    protected PrintStream output_;
    protected BufferedReader input_;
  
    WebServerClientConnection (Socket socket)
    {
      socket_ = socket;
      start();
    }
  
  
    public void run()
    {
      try
      {
        output_ = new PrintStream (socket_.getOutputStream());
        InputStream input_stream = socket_.getInputStream ();
        input_ = new BufferedReader(new InputStreamReader(input_stream));

        String request = input_.readLine();
        System.err.println("original requesting "+request);
        String dummy = input_.readLine(); 
        while ( (dummy!=null) && (!dummy.trim().equals("")) )
        { 
          dummy = input_.readLine();
          System.err.println ("rest of request:"+dummy);
        }


        StringTokenizer st = new StringTokenizer( request );
        if ( (st.countTokens() >= 2) && st.nextToken().equals("GET"))
        {
          if ( (request = st.nextToken()).startsWith("/") )
            request = request.substring( 1 );

          if (request.equals(""))
            request = "/";
          
          System.err.println("requesting '"+request+"'");

          if(request.equals("index.html") || request.equals("/"))
          {
            StringBuffer html_page = new StringBuffer();
            html_page.append("<HTML><HEAD><TITLE>index.html</title></HEAD>");
            html_page.append("<body>this is the body</body>");
            html_page.append("</HTML>");
            
            output ("HTTP/1.0 200 Document Follows");
            output ("Server: Simple HTTP Server");
            output ("Date: "+date_format_.format(new Date()));
            
            String mime_type = "text/html";
            output ("Content-Type: "+mime_type);
            output ("Content-Length: " + default_page_.length());
            output ("Last-Modified: Sunday, 18-May-97 23:59:59 GMT");
            output ("");
            output (default_page_);
          }
          else if (request.equals(screenshot_url_))
          {
            System.out.println("return screenshot of component "+component_);
            if(component_ != null)
            {
              ByteArrayOutputStream out = new ByteArrayOutputStream();
              writeComponent(component_,out);
              
              output ("HTTP/1.0 200 Document Follows");
              output ("Server: Simple GPSylon HTTP Server");
              output ("Date: "+date_format_.format(new Date()));
              output ("Content-Type: image/png");
              output ("Content-Length: "+out.size());
              output ("");
              output_.write(out.toByteArray());
            }
            else
            {
              output( "HTTP/1.0 404 Object not found.");
              output("");
            }
            
          }
          else
          {
            output( "HTTP/1.0 404 Object not found.");
            output("");
          }
        }

        output_.flush ();
        socket_.close();
      }
      catch ( Exception e )
      {
        e.printStackTrace();
      }
    }
  
    protected void output (String s)
      throws IOException
    {
        output_.print (s + (char)13 + (char)10);
        output_.flush();
        System.out.println("Return: '" + s +"'\n");
    }
  }

}
