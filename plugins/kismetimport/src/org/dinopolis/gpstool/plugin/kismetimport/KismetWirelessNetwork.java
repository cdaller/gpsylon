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

/**
 * This is the value object for a Kismet Network.
 * It's representing the wireless-network element 
 * of Kismet XML scan file.
 *
 * @author sven@boeckelmann.org
 *
 */

public class KismetWirelessNetwork implements java.io.Serializable {
    
    /** service name
     */    
    protected String ssid = null;
    
    /** mac address
     */
    protected String bssid = null;
    
    /** maximum latitude network was seen
     */
    protected float maxLatitude = 0;
    
    /** minimum latitude network was seen
     */
    protected float minLatitude = 0;
    
    /** maximum longtitude network was seen
     */
    protected float maxLongtitude = 0;
    
    /** minimum longtitude network was seen
     */
    protected float minLongtitude = 0;
    
    /** is WEP encrypted 
     */
    protected boolean wep = false;

    /** Creates a new instance of KismetEntry */
    public KismetWirelessNetwork() {
    }
    
    /** set ssid (service name)
     * @param ssid name
     */
    public void setSsid(String ssid)    {
        this.ssid = ssid;
    }
    
    /** set bssid (mac address)
     * @param bssid max
     */
    public void setBssid(String bssid)  {
        this.bssid = bssid;
    }
    
    /** set maximum latitude network was seen
     * @param latitude seen
     */
    public void setMaxLatitude(String latitude)    {
        this.maxLatitude = Float.parseFloat(latitude);
    }
    
    /** set minimum latitude network was seen
     * @param latitude seen
     */
    public void setMinLatitude(String latitude)    {
        this.minLatitude = Float.parseFloat(latitude);
    }
    
    /** set minimum longtitude network was seen
     * @param longtitude seen
     */
    public void setMinLongtitude(String longtitude)    {
        this.minLongtitude = Float.parseFloat(longtitude);
    }
    
    /** set maximum longtitude network was seen
     * @param longtitude seen
     */
    public void setMaxLongtitude(String longtitude)    {
        this.maxLongtitude = Float.parseFloat(longtitude);
    }
    
    /** set maximum latitude network was seen
     * @param latitude seen
     */
    public void setMaxLatitude(float latitude)    {
        this.maxLatitude = latitude;
    }
    
    /** set minimum latitude network was seen
     * @param latitude seen
     */
    public void setMinLatitude(float latitude)    {
        this.minLatitude = latitude;
    }
    
    /** set minimum longtitude network was seen
     * @param longtitude seen
     */
    public void setMinLongtitude(float longtitude)    {
        this.minLongtitude = longtitude;
    }
    
    /** set maximum longtitude network was seen
     * @param longtitude seen
     */
    public void setMaxLongtitude(float longtitude)    {
        this.maxLongtitude = longtitude;
    }
    
    /** set to true if this network is WEP encrypted
     * @param wep (most of the time false)
     */
    public void setWep(boolean wep) {
        this.wep = wep;
    }
    
    /** get ssid (service name)
     *  @return name
     */
    public String getSsid() {
        if (ssid != null) return ssid;
        else return "<no ssid>";
    }
    
    /** get bssid (mac address)
     *  @return mac
     */
    public String getBssid() {
        return bssid;
    }
    
    /** get estimated center longtitude of this network
     *  the better you drove around the access point, the more precise is this value!
     *  @return center longtitude
     */
    public float getLongtitude()  {
        return average(minLongtitude, maxLongtitude);
    }
    
    /** get estimated center latitude of this network
     *  the better you drove around the access point, the more precise is this value!
     *  @return center latitude
     */
    public float getLatitude()  {
        return average(minLatitude, maxLatitude);
    }
    
    /** get maximum longtitude this network was seen
     * @return longtitude
     */    
    public float getMaxLongtitude()   {
        return maxLongtitude;
    }
    
    /** get minimum longtitude this network was seen
     * @return longitutude
     */    
    public float getMinLongtitude()   {
        return minLongtitude;
    }
    
    /** get maximum latitude this network was seen
     * @return latitude
     */    
    public float getMaxLatitude()   {
        return maxLatitude;
    }
    
    /** get minimum latitude this network was seen
     * @return latitude
     */    
    public float getMinLatitude()   {
        return minLatitude;
    }
    
    /** get name of this network, for now this ssid and bssid.
     * @return the name
     */    
    public String getName() {
        return getSsid() + " | " + getBssid();
    }
    
    /** check if this network uses WEP encryption.
     * @return true if encrypted
     */    
    public boolean getWep() {
        return wep;
    }
    
    /** average of the two known values 
     *  this is not very precise.
     */
    private float average(float min, float max)   {
        if (min == 0) return max;
        if (max == 0) return min;
        // not great but it's doing the job!
        return min + ((max - min)/2);
    }
}
