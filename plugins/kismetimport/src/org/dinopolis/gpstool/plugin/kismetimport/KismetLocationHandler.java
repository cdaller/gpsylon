/***********************************************************************
 *
 * Copyright (c) 2001 IICM, Graz University of Technology
 * Inffeldgasse 16c, A-8010 Graz, Austria.
 *
 * Copyright (c) 2003 Sven Boeckelmann
 * Langendreerstrasse 30, 44892 Bochum, Germany
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

package org.dinopolis.gpstool.plugin.kismetimport;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import java.util.HashMap;
import org.dinopolis.gpstool.gui.layer.location.LocationMarkerSource;
import org.dinopolis.gpstool.gui.layer.location.LocationMarker;
import org.dinopolis.gpstool.gui.layer.location.LocationMarkerCategory;
import org.dinopolis.gpstool.gui.layer.location.LocationMarkerFilter;
import org.dinopolis.gpstool.util.geoscreen.GeoScreenList;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;

/** 
 * SAX Handler for Kismet XML scan files.
 * The handler will generate a KismetWirelessNetwork Object
 * and save the data using a LocationMarkerSource.
 *
 *
 * @author sven@boeckelmann.org
 */
public class KismetLocationHandler extends DefaultHandler {

    /** needed to store the GPS info
     */
    LocationMarkerSource locationMarkerSource = null;
    
    /** network to be stored
     */
    KismetWirelessNetwork network = null;
    
    /** helps to anzalyze gps info
     */
    HashMap location = null;
    
    /** the current element withing gps-info
     */
    String key = null;
    
    /** data within an element
     */
    String CDATA = null;
    
    /** gpstool LocationMarkerCategory for 
     *  unencrypted AccessPoints
     */
    LocationMarkerCategory openAp = null;
    
    /** gpstool LocationMarkerCategory for 
     *  encrypted AccessPoints
     */
    LocationMarkerCategory encryptedAp = null;
    
    /** northern boundary 
     */
    float north = 0;
    
    /** southern boundary 
     */
    float south = 0;
    
    /** western boundary 
     */
    float west = 0;
    
    /** eastern boundary 
     */
    float east = 0;
    
    /** offset for boundaries
     */
    float offset = Float.parseFloat("0.001");
    
    /** create new instance of KismetLocatiionHandler
     * @param locationMarkerSource store networks using this source
     */
    public KismetLocationHandler(LocationMarkerSource locationMarkerSource) {
        this.locationMarkerSource = locationMarkerSource;
        openAp = LocationMarkerCategory.getCategory("open_ap");
        encryptedAp = LocationMarkerCategory.getCategory("encrypted_ap");
    }
    
    /** handle SAX startElement event
     * @param uri URI
     * @param localName local name
     * @param qName element name
     * @param attributes the Attributes
     */    
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        try {

            // create new instance of KismetWirelessNetwork
            if (qName.equals("wireless-network"))    {
                String type = attributes.getValue("type");
                // we are only interested in access points (type = infrastructure
                if (type != null && type.equals("infrastructure"))   {
                    network = new KismetWirelessNetwork();
                    network.setWep(attributes.getValue("wep").equals("true"));
                }
                else
                    network = null;
            }

            // configure gps info for network
            if (qName.equals("gps-info"))    {
                location = new HashMap();
            }

        }
        catch (Exception e) {
            System.out.println("exception at startElement " + qName + ":" + e.getMessage());
        }
    }
    
    /** handle SAX endElement event
     * @param uri URI
     * @param localName local name
     * @param qName element name
     */    
    public void endElement(String uri, String localName, String qName) {        
        try {
            // store network to LocationMarkerSource
            if (network != null && qName.equals("wireless-network")) {
                
                System.out.println("\nfound network \n" + network.getName() + "\nlat=" + network.getLatitude() + ", lon=" + network.getLongtitude());
                
                // check whether ap is already known
                LocationMarkerFilter filter = new LocationMarkerFilter(LocationMarkerFilter.KEY_NAME,
                                                                     new String[]{ "%" + network.getBssid() },
                                                                     LocationMarkerFilter.LIKE_OPERATION,
                                                                     false);

                // define boundaries for filter
                north = network.getMaxLatitude();
                south = network.getMinLatitude();
                west = network.getMinLongtitude();
                east = network.getMaxLongtitude();

                west = west - offset;
                east = east + offset;
                north = north + offset;
                south = south - offset;

                GeoScreenList geoScreenList = locationMarkerSource.getLocationMarkers(north, south, west, east, filter);
                
                // if the network is not known, store it
                if (geoScreenList.size() == 0)  {
                    
                    // use the corresponding category
                    // according to the WEP settings
                    LocationMarkerCategory currentCategory = null;
                    if (network.getWep())
                        currentCategory = encryptedAp;
                    else
                        currentCategory = openAp;
                    
                    // create new location marker and store it
                    // --> make network known to gpstool
                    LocationMarker locationMarker = 
                        new LocationMarker(network.getName(), network.getLatitude(), network.getLongtitude(), currentCategory);
                    locationMarkerSource.putLocationMarker(locationMarker);
                }
                // if the network is already known don't do anything
                else
                    System.out.println("network is already known");

                // reset network to continue with next
                network = null;
            }

            // put CDATA into location map,
            // the location will be set on gps-info endElement
            if (qName.equals("max-lat") ||
                qName.equals("min-lat") ||
                qName.equals("min-lon") ||
                qName.equals("max-lon"))    {
                location.put(qName, CDATA);
            }
            
            // configure the gps-info from location Map
            if (network != null && location != null && qName.equals("gps-info"))    {
                network.setMaxLatitude(location.get("max-lat").toString());
                network.setMinLatitude(location.get("min-lat").toString());
                network.setMaxLongtitude(location.get("max-lon").toString());
                network.setMinLongtitude(location.get("min-lon").toString());
                location = null;
            }

            // set network name
            if (network != null && qName.equals("SSID")) {
                network.setSsid(CDATA);
            }

            // set network mac address
            if (network != null && qName.equals("BSSID")) {
                network.setBssid(CDATA);
            }

        }
        catch (Exception e) {
            System.out.println("exception at endElement " + qName + ":" + e.getMessage());
            
        }

    }
    
    /** process CDATA
     *  @param ch characters
     *  @param start start index
     *  @param length length to read
     */
    public void characters(char[] ch, int start, int length)   {
        CDATA = new String(ch).substring(start, start+length);
    }
    
}
