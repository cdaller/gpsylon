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


package org.dinopolis.gpstool.gpsinput.garmin;
import java.util.Map;
import java.util.TreeMap;

//----------------------------------------------------------------------
/**
 * @author Christof Dallermassl
 * @version $Revision$
 */

public class GarminWaypointSymbols  
{
  public static final String UNKNOWN_NAME = "unknown";
  protected static Map name_map_;

//----------------------------------------------------------------------
/**
 * Initializes the id to name map. Names and ids are taken from the
 * garmin protcool specification.
 */
  protected static void initMap()
  {
    name_map_ = new TreeMap();
    
  /*---------------------------------------------------------------
    Symbols for marine (group 0...0-8191...bits 15-13=000).
    ---------------------------------------------------------------*/
    name_map_.put(new Integer(0),"anchor"); /* white anchor symbol */
    name_map_.put(new Integer(1),"bell"); /* white bell symbol */
    name_map_.put(new Integer(2),"diamond_grn"); /* green diamond symbol */
    name_map_.put(new Integer(3),"diamond_red"); /* red diamond symbol */
    name_map_.put(new Integer(4),"dive1"); /* diver down flag 1 */
    name_map_.put(new Integer(5),"dive2"); /* diver down flag 2 */
    name_map_.put(new Integer(6),"dollar"); /* white dollar symbol */
    name_map_.put(new Integer(7),"fish"); /* white fish symbol */
    name_map_.put(new Integer(8),"fuel"); /* white fuel symbol */
    name_map_.put(new Integer(9),"horn"); /* white horn symbol */
    name_map_.put(new Integer(10),"house"); /* white house symbol */
    name_map_.put(new Integer(11),"knife"); /* white knife & fork symbol */
    name_map_.put(new Integer(12),"light"); /* white light symbol */
    name_map_.put(new Integer(13),"mug"); /* white mug symbol */
    name_map_.put(new Integer(14),"skull"); /* white skull and crossbones symbol*/
    name_map_.put(new Integer(15),"square_grn"); /* green square symbol */
    name_map_.put(new Integer(16),"square_red"); /* red square symbol */
    name_map_.put(new Integer(17),"wbuoy"); /* white buoy waypoint symbol */
    name_map_.put(new Integer(18),"wpt_dot"); /* waypoint dot */
    name_map_.put(new Integer(19),"wreck"); /* white wreck symbol */
    name_map_.put(new Integer(20),"null"); /* null symbol (transparent) */
    name_map_.put(new Integer(21),"mob"); /* man overboard symbol */
/*------------------------------------------------------
  marine navaid symbols
  ------------------------------------------------------*/
    name_map_.put(new Integer(22),"buoy_ambr"); /* amber map buoy symbol */
    name_map_.put(new Integer(23),"buoy_blck"); /* black map buoy symbol */
    name_map_.put(new Integer(24),"buoy_blue"); /* blue map buoy symbol */
    name_map_.put(new Integer(25),"buoy_grn"); /* green map buoy symbol */
    name_map_.put(new Integer(26),"buoy_grn_red"); /* green/red map buoy symbol */
    name_map_.put(new Integer(27),"buoy_grn_wht"); /* green/white map buoy symbol */
    name_map_.put(new Integer(28),"buoy_orng"); /* orange map buoy symbol */
    name_map_.put(new Integer(29),"buoy_red"); /* red map buoy symbol */
    name_map_.put(new Integer(30),"buoy_red_grn"); /* red/green map buoy symbol */
    name_map_.put(new Integer(31),"buoy_red_wht"); /* red/white map buoy symbol */
    name_map_.put(new Integer(32),"buoy_violet"); /* violet map buoy symbol */
    name_map_.put(new Integer(33),"buoy_wht"); /* white map buoy symbol */
    name_map_.put(new Integer(34),"buoy_wht_grn"); /* white/green map buoy symbol */
    name_map_.put(new Integer(35),"buoy_wht_red"); /* white/red map buoy symbol */
    name_map_.put(new Integer(36),"dot"); /* white dot symbol */
    name_map_.put(new Integer(37),"rbcn"); /* radio beacon symbol */
/*------------------------------------------------------
  leave space for more navaids (up to 128 total)
  ------------------------------------------------------*/
    name_map_.put(new Integer(150),"boat_ramp"); /* boat ramp symbol */
    name_map_.put(new Integer(151),"camp"); /* campground symbol */
    name_map_.put(new Integer(152),"restrooms"); /* restrooms symbol */
    name_map_.put(new Integer(153),"showers"); /* shower symbol */
    name_map_.put(new Integer(154),"drinking_wtr"); /* drinking water symbol */
    name_map_.put(new Integer(155),"phone"); /* telephone symbol */
    name_map_.put(new Integer(156),"1st_aid"); /* first aid symbol */
    name_map_.put(new Integer(157),"info"); /* information symbol */
    name_map_.put(new Integer(158),"parking"); /* parking symbol */
    name_map_.put(new Integer(159),"park"); /* park symbol */
    name_map_.put(new Integer(160),"picnic"); /* picnic symbol */
    name_map_.put(new Integer(161),"scenic"); /* scenic area symbol */
    name_map_.put(new Integer(162),"skiing"); /* skiing symbol */
    name_map_.put(new Integer(163),"swimming"); /* swimming symbol */
    name_map_.put(new Integer(164),"dam"); /* dam symbol */
    name_map_.put(new Integer(165),"controlled"); /* controlled area symbol */
    name_map_.put(new Integer(166),"danger"); /* danger symbol */
    name_map_.put(new Integer(167),"restricted"); /* restricted area symbol */
    name_map_.put(new Integer(168),"null_2"); /* null symbol */
    name_map_.put(new Integer(169),"ball"); /* ball symbol */

    name_map_.put(new Integer(170),"car"); /* car symbol */
    name_map_.put(new Integer(171),"deer"); /* deer symbol */
    name_map_.put(new Integer(172),"shpng_cart"); /* shopping cart symbol */
    name_map_.put(new Integer(173),"lodging"); /* lodging symbol */
    name_map_.put(new Integer(174),"mine"); /* mine symbol */
    name_map_.put(new Integer(175),"trail_head"); /* trail head symbol */
    name_map_.put(new Integer(176),"truck_stop"); /* truck stop symbol */
    name_map_.put(new Integer(177),"user_exit"); /* user exit symbol */
    name_map_.put(new Integer(178),"flag"); /* flag symbol */
    name_map_.put(new Integer(179),"circle_x"); /* circle with x in the center */
/*---------------------------------------------------------------
  Symbols for land (group 1...8192-16383...bits 15-13=001).
  ---------------------------------------------------------------*/

    name_map_.put(new Integer(8192),"is_hwy"); /* interstate hwy symbol */
    name_map_.put(new Integer(8193),"us_hwy"); /* us hwy symbol */
    name_map_.put(new Integer(8194),"st_hwy"); /* state hwy symbol */
    name_map_.put(new Integer(8195),"mi_mrkr"); /* mile marker symbol */
    name_map_.put(new Integer(8196),"trcbck"); /* TracBack (feet) symbol */
    name_map_.put(new Integer(8197),"golf"); /* golf symbol */
    name_map_.put(new Integer(8198),"sml_cty"); /* small city symbol */
    name_map_.put(new Integer(8199),"med_cty"); /* medium city symbol */
    name_map_.put(new Integer(8200),"lrg_cty"); /* large city symbol */
    name_map_.put(new Integer(8201),"freeway"); /* intl freeway hwy symbol */
    name_map_.put(new Integer(8202),"ntl_hwy"); /* intl national hwy symbol */
    name_map_.put(new Integer(8203),"cap_cty"); /* capitol city symbol (star) */
    name_map_.put(new Integer(8204),"amuse_pk"); /* amusement park symbol */
    name_map_.put(new Integer(8205),"bowling"); /* bowling symbol */
    name_map_.put(new Integer(8206),"car_rental"); /* car rental symbol */
    name_map_.put(new Integer(8207),"car_repair"); /* car repair symbol */
    name_map_.put(new Integer(8208),"fastfood"); /* fast food symbol */
    name_map_.put(new Integer(8209),"fitness"); /* fitness symbol */
    name_map_.put(new Integer(8210),"movie"); /* movie symbol */
    name_map_.put(new Integer(8211),"museum"); /* museum symbol */
    name_map_.put(new Integer(8212),"pharmacy"); /* pharmacy symbol */
    name_map_.put(new Integer(8213),"pizza"); /* pizza symbol */
    name_map_.put(new Integer(8214),"post_ofc"); /* post office symbol */
    name_map_.put(new Integer(8215),"rv_park"); /* RV park symbol */
    name_map_.put(new Integer(8216),"school"); /* school symbol */
    name_map_.put(new Integer(8217),"stadium"); /* stadium symbol */
    name_map_.put(new Integer(8218),"store"); /* dept. store symbol */
    name_map_.put(new Integer(8219),"zoo"); /* zoo symbol */
    name_map_.put(new Integer(8220),"gas_plus"); /* convenience store symbol */
    name_map_.put(new Integer(8221),"faces"); /* live theater symbol */
    name_map_.put(new Integer(8222),"ramp_int"); /* ramp intersection symbol */
    name_map_.put(new Integer(8223),"st_int"); /* street intersection symbol */
    name_map_.put(new Integer(8226),"weigh_sttn"); /* inspection/weigh station symbol */
    name_map_.put(new Integer(8227),"toll_booth"); /* toll booth symbol */
    name_map_.put(new Integer(8228),"elev_pt"); /* elevation point symbol */
    name_map_.put(new Integer(8229),"ex_no_srvc"); /* exit without services symbol */
    name_map_.put(new Integer(8230),"geo_place_mm"); /* Geographic place name, man-made */
    name_map_.put(new Integer(8231),"geo_place_wtr"); /* Geographic place name, water */
    name_map_.put(new Integer(8232),"geo_place_lnd"); /* Geographic place name, land */
    name_map_.put(new Integer(8233),"bridge"); /* bridge symbol */
    name_map_.put(new Integer(8234),"building"); /* building symbol */
    name_map_.put(new Integer(8235),"cemetery"); /* cemetery symbol */
    name_map_.put(new Integer(8236),"church"); /* church symbol */
    name_map_.put(new Integer(8237),"civil"); /* civil location symbol */
    name_map_.put(new Integer(8238),"crossing"); /* crossing symbol */
    name_map_.put(new Integer(8239),"hist_town"); /* historical town symbol */
    name_map_.put(new Integer(8240),"levee"); /* levee symbol */
    name_map_.put(new Integer(8241),"military"); /* military location symbol */
    name_map_.put(new Integer(8242),"oil_field"); /* oil field symbol */
    name_map_.put(new Integer(8243),"tunnel"); /* tunnel symbol */
    name_map_.put(new Integer(8244),"beach"); /* beach symbol */
    name_map_.put(new Integer(8245),"forest"); /* forest symbol */
    name_map_.put(new Integer(8246),"summit"); /* summit symbol */
    name_map_.put(new Integer(8247),"lrg_ramp_int"); /* large ramp intersection symbol */
    name_map_.put(new Integer(8248),"lrg_ex_no_srvc"); /* large exit without services smbl */
    name_map_.put(new Integer(8249),"badge"); /* police/official badge symbol */
    name_map_.put(new Integer(8250),"cards"); /* gambling/casino symbol */
    name_map_.put(new Integer(8251),"snowski"); /* snow skiing symbol */
    name_map_.put(new Integer(8252),"iceskate"); /* ice skating symbol */
    name_map_.put(new Integer(8253),"wrecker"); /* tow truck (wrecker) symbol */
    name_map_.put(new Integer(8254),"border"); /* border crossing (port of entry) */
/*---------------------------------------------------------------
  Symbols for aviation (group 2...16383-24575...bits 15-13=010).
  ---------------------------------------------------------------*/

    name_map_.put(new Integer(16384),"airport"); /* airport symbol */
    name_map_.put(new Integer(16385),"int"); /* intersection symbol */
    name_map_.put(new Integer(16386),"ndb"); /* non-directional beacon symbol */
    name_map_.put(new Integer(16387),"vor"); /* VHF omni-range symbol */
    name_map_.put(new Integer(16388),"heliport"); /* heliport symbol */
    name_map_.put(new Integer(16389),"private"); /* private field symbol */
    name_map_.put(new Integer(16390),"soft_fld"); /* soft field symbol */
    name_map_.put(new Integer(16391),"tall_tower"); /* tall tower symbol */
    name_map_.put(new Integer(16392),"short_tower"); /* short tower symbol */
    name_map_.put(new Integer(16393),"glider"); /* glider symbol */
    name_map_.put(new Integer(16394),"ultralight"); /* ultralight symbol */
    name_map_.put(new Integer(16395),"parachute"); /* parachute symbol */
    name_map_.put(new Integer(16396),"vortac"); /* VOR/TACAN symbol */
    name_map_.put(new Integer(16397),"vordme"); /* VOR-DME symbol */
    name_map_.put(new Integer(16398),"faf"); /* first approach fix */
    name_map_.put(new Integer(16399),"lom"); /* localizer outer marker */
    name_map_.put(new Integer(16400),"map"); /* missed approach point */
    name_map_.put(new Integer(16401),"tacan"); /* TACAN symbol */
    name_map_.put(new Integer(16402),"seaplane"); /* Seaplane Base */
  }

//----------------------------------------------------------------------
/**
 * Returns the garmin symbol name for the given symbol code.
 *
 * @param symbol_type the number of the symbol (as defined in the
 * garmin protocol specification, page 33).
 * @return the name of the symbol.
 */
  public static String getSymbolName(int symbol_type)
  {
    if(name_map_ == null)
      initMap();
    String name = (String)name_map_.get(new Integer(symbol_type));
    if(name == null)
      return(UNKNOWN_NAME);
    return(name);
  }
}


