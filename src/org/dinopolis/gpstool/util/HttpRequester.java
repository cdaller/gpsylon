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

package org.dinopolis.gpstool.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Vector;

//----------------------------------------------------------------------
/**
 * The HttpRequester is able perform a HTTP get request and set any
 * HTTP-Headers wanted. The response headers and the content is read
 * and stored in a map that can be used afterwards. This class is not
 * so powerful as the {@link java.net.URLConnection} but does not hide
 * anything from the programmer. It respects the proxy settings in the
 * system properties. This class is especially usefull if you want to
 * request more than one url from the same server. Do not forget to
 * set "Keep-Alive" in the http header!
 *
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class HttpRequester
{
      /** the buffer size to use when reading from the socket */
  public static final int BUFFER_SIZE = 16384;

  protected String host_;
  protected int port_;
  protected String proxy_host_;
  protected int proxy_port_;
  protected boolean use_proxy_ = false;
  
  protected Map web_request_;
  protected Map web_response_;
  protected Socket socket_;
  protected BufferedReader socket_reader_;
  protected Writer socket_writer_;
  
//----------------------------------------------------------------------
/**
 * Constructor connection to the given hostname and the default port
 * for http (80).
 *
 * @param hostname the host to connect to.
 */
  public HttpRequester(String hostname)
  {
    this(hostname,80);
  }

//----------------------------------------------------------------------
/**
 * Constructor
 *
 * @param hostname the host to connect to.
 * @param port the port to connect to.
 */
  public HttpRequester(String hostname, int port)
  {
    host_ = hostname;
    port_ = port;
    initProxy();
  }

//----------------------------------------------------------------------
/**
 * Reads the proxy settings from the system's properties.
 */
  
  protected void initProxy()
  {
    Properties props = System.getProperties();
    proxy_host_ = (String)props.get("http.proxyHost");
    String proxy_port = (String)props.get("http.proxyPort");
    if(proxy_port != null)
      proxy_port_ = Integer.parseInt(proxy_port);
    use_proxy_ = (proxy_host_ != null);
  }

//----------------------------------------------------------------------
/**
 * Opens the connection to the host or to the proxy.
 */
  protected void openConnection()
    throws UnknownHostException, IOException
  {
    String host;
    int port;
    if(use_proxy_)
    {
      host = proxy_host_;
      port = proxy_port_;
    }
    else
    {
      host = host_;
      port = port_;
    }
    socket_ = new Socket(host,port);
    socket_reader_ = new BufferedReader(new InputStreamReader(socket_.getInputStream()));
    socket_writer_ = new OutputStreamWriter(socket_.getOutputStream());
  }

//----------------------------------------------------------------------
/**
 * Requests the given url from the host or the proxy and uses the
 * given headers (and no other headers!).
 *
 * @param url the url to request from the host (or proxy) set in the
 * constructor.
 * @param http_headers a map holding the http header fields (keys,
 * values) to use. The key and the value of the headers must be
 * Strings!
 * @return a map holding the response. The headers can be retrieved by
 * their key, the content by the special key "Content". An extra entry
 * is generated that holds the number of bytes really read from the
 * server (key "Content-Length-Read"). Another special entry holds the
 * HTTP response (key "HTTP-Response").
 */
  public Map requestUrl(URL url, Map http_headers)
    throws IOException
  {
    if(socket_ == null)
      openConnection();

//    System.out.println("requesting url: "+url);
    writeToSocket("GET "+url+" HTTP/1.1\r\n");
    Iterator iterator = http_headers.keySet().iterator();
    while(iterator.hasNext())
    {
      String key = (String)iterator.next();
      String value = (String)http_headers.get(key);
      writeToSocket(key+": "+value+"\r\n");
    }
      writeToSocket("\r\n");
      socket_writer_.flush();
          // read answer:
      Map response_headers = new TreeMap();
      String http_answer = socket_reader_.readLine();
      response_headers.put("HTTP-Response",http_answer);
//      System.out.println("READ: "+http_answer);
      String header_line;
      int pos;
      String key;
      String value;
      header_line = socket_reader_.readLine();
//      System.out.println("READ: "+header_line);
      while((header_line != null) && (header_line.length() > 0))
      {
        pos = header_line.indexOf(':');
        if(pos > 0)
        {
          key = header_line.substring(0,pos);
          value = header_line.substring(pos+2);
              // if the key is already contained in the map, check if
              // value is already a list (then append value to list),
              // otherwise create a list and add the old and the new
              // value:
          if(response_headers.containsKey(key))
          {
            Object old_value = response_headers.get(key);
            if(old_value instanceof List)
              ((List)old_value).add(value);
            else
            {
              Vector values = new Vector();
              values.add(old_value);
              values.add(value);
              response_headers.put(key,values);
            }
          }
          else
          {
                // new key:
            response_headers.put(key,value);
          }
        }
        header_line = socket_reader_.readLine(); // read next line
//        System.out.println("READ: "+header_line);
      }
          // content follows:
//      printMap(response_headers);
      int content_length = Integer.parseInt((String)response_headers.get("Content-Length"));
      int left_to_read = content_length;
      StringBuffer content = new StringBuffer(4069);
      char[] buffer = new char[BUFFER_SIZE];
      int sum_bytes = 0;
      int num_bytes = 0;
          // Read till end of content
      while (((num_bytes = socket_reader_.read(buffer,0,Math.min(buffer.length,left_to_read))) != -1)
              && (left_to_read > 0))
      {
        content.append(new String(buffer, 0, num_bytes));
        sum_bytes += num_bytes;
        left_to_read -= num_bytes;
      }
//      System.out.println("READ: <content>");

      response_headers.put("Content-Length-Read",Integer.toString(sum_bytes)); // save content length read as well
      response_headers.put("Content",content.toString()); // save content as well

      return(response_headers);
  }

  protected void writeToSocket(String string)
    throws IOException
  {
//    System.out.print("WRITE: "+string);
    socket_writer_.write(string);
  }


  protected void printMap(Map map)
  {
    Iterator iterator = map.keySet().iterator();
    while(iterator.hasNext())
    {
      String key = (String)iterator.next();
      Object value = map.get(key);
      System.out.println(key+": '"+value+"'");
    }
  }


//   public static void main(String[] args)
//     throws Exception
//   {
//     WebRequest web = new WebRequest();
//     web.initProxy();
// //    mpd.getMap(48.0,13.0,7,640,480);
//     URL url = new URL(args[0]);
//     web.initConnection(url);

//     Map request_header = new TreeMap();
//     request_header.put("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 4.0)");
//     request_header.put("Keep-Alive","300");
//     request_header.put("Connection","keep-alive");
//     request_header.put("Host",url.getHost());

//     Map response = web.requestUrl(url,request_header);
//     Iterator iterator = response.keySet().iterator();
//     while(iterator.hasNext())
//     {
//       String key = (String)iterator.next();
//       String value = (String)response.get(key);
//       System.out.println(key+": '"+value+"'");
//     }
//   }
}



