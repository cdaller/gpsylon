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
  protected static Map id_map_;

//----------------------------------------------------------------------
/**
 * Initializes the id to name map. Names and ids are taken from the
 * garmin protcool specification.
 */
  protected static void initMap()
  {
    name_map_ = new TreeMap();
    id_map_ = new TreeMap();
    
  /*---------------------------------------------------------------
    Symbols for marine (group 0...0-8191...bits 15-13=000).
    ---------------------------------------------------------------*/
    name_map_.put(new Integer(0),"anchor"); /* white anchor symbol */
    id_map_.put("anchor",new Integer(0)); /* white anchor symbol */
    name_map_.put(new Integer(1),"bell"); /* white bell symbol */
    id_map_.put("bell",new Integer(1)); /* white bell symbol */
    name_map_.put(new Integer(2),"diamond_grn"); /* green diamond symbol */
    id_map_.put("diamond_grn",new Integer(2)); /* green diamond symbol */
    name_map_.put(new Integer(3),"diamond_red"); /* red diamond symbol */
    id_map_.put("diamond_red",new Integer(3)); /* red diamond symbol */
    name_map_.put(new Integer(4),"dive1"); /* diver down flag 1 */
    id_map_.put("dive1",new Integer(4)); /* diver down flag 1 */
    name_map_.put(new Integer(5),"dive2"); /* diver down flag 2 */
    id_map_.put("dive2",new Integer(5)); /* diver down flag 2 */
    name_map_.put(new Integer(6),"dollar"); /* white dollar symbol */
    id_map_.put("dollar",new Integer(6)); /* white dollar symbol */
    name_map_.put(new Integer(7),"fish"); /* white fish symbol */
    id_map_.put("fish",new Integer(7)); /* white fish symbol */
    name_map_.put(new Integer(8),"fuel"); /* white fuel symbol */
    id_map_.put("fuel",new Integer(8)); /* white fuel symbol */
    name_map_.put(new Integer(9),"horn"); /* white horn symbol */
    id_map_.put("horn",new Integer(9)); /* white horn symbol */
    name_map_.put(new Integer(10),"house"); /* white house symbol */
    id_map_.put("house",new Integer(10)); /* white house symbol */
    name_map_.put(new Integer(11),"knife"); /* white knife & fork symbol */
    id_map_.put("knife",new Integer(11)); /* white knife & fork symbol */
    name_map_.put(new Integer(12),"light"); /* white light symbol */
    id_map_.put("light",new Integer(12)); /* white light symbol */
    name_map_.put(new Integer(13),"mug"); /* white mug symbol */
    id_map_.put("mug",new Integer(13)); /* white mug symbol */
    name_map_.put(new Integer(14),"skull"); /* white skull and crossbones symbol*/
    id_map_.put("skull",new Integer(14)); /* white skull and crossbones symbol*/
    name_map_.put(new Integer(15),"square_grn"); /* green square symbol */
    id_map_.put("square_grn",new Integer(15)); /* green square symbol */
    name_map_.put(new Integer(16),"square_red"); /* red square symbol */
    id_map_.put("square_red",new Integer(16)); /* red square symbol */
    name_map_.put(new Integer(17),"wbuoy"); /* white buoy waypoint symbol */
    id_map_.put("wbuoy",new Integer(17)); /* white buoy waypoint symbol */
    name_map_.put(new Integer(18),"wpt_dot"); /* waypoint dot */
    id_map_.put("wpt_dot",new Integer(18)); /* waypoint dot */
    name_map_.put(new Integer(19),"wreck"); /* white wreck symbol */
    id_map_.put("wreck",new Integer(19)); /* white wreck symbol */
    name_map_.put(new Integer(20),"null"); /* null symbol (transparent) */
    id_map_.put("null",new Integer(20)); /* null symbol (transparent) */
    name_map_.put(new Integer(21),"mob"); /* man overboard symbol */
    id_map_.put("mob",new Integer(21)); /* man overboard symbol */
/*------------------------------------------------------
  marine navaid symbols
  ------------------------------------------------------*/
    name_map_.put(new Integer(22),"buoy_ambr"); /* amber map buoy symbol */
    id_map_.put("buoy_ambr",new Integer(22)); /* amber map buoy symbol */
    name_map_.put(new Integer(23),"buoy_blck"); /* black map buoy symbol */
    id_map_.put("buoy_blck",new Integer(23)); /* black map buoy symbol */
    name_map_.put(new Integer(24),"buoy_blue"); /* blue map buoy symbol */
    id_map_.put("buoy_blue",new Integer(24)); /* blue map buoy symbol */
    name_map_.put(new Integer(25),"buoy_grn"); /* green map buoy symbol */
    id_map_.put("buoy_grn",new Integer(25)); /* green map buoy symbol */
    name_map_.put(new Integer(26),"buoy_grn_red"); /* green/red map buoy symbol */
    id_map_.put("buoy_grn_red",new Integer(26)); /* green/red map buoy symbol */
    name_map_.put(new Integer(27),"buoy_grn_wht"); /* green/white map buoy symbol */
    id_map_.put("buoy_grn_wht",new Integer(27)); /* green/white map buoy symbol */
    name_map_.put(new Integer(28),"buoy_orng"); /* orange map buoy symbol */
    id_map_.put("buoy_orng",new Integer(28)); /* orange map buoy symbol */
    name_map_.put(new Integer(29),"buoy_red"); /* red map buoy symbol */
    id_map_.put("buoy_red",new Integer(29)); /* red map buoy symbol */
    name_map_.put(new Integer(30),"buoy_red_grn"); /* red/green map buoy symbol */
    id_map_.put("buoy_red_grn",new Integer(30)); /* red/green map buoy symbol */
    name_map_.put(new Integer(31),"buoy_red_wht"); /* red/white map buoy symbol */
    id_map_.put("buoy_red_wht",new Integer(31)); /* red/white map buoy symbol */
    name_map_.put(new Integer(32),"buoy_violet"); /* violet map buoy symbol */
    id_map_.put("buoy_violet",new Integer(32)); /* violet map buoy symbol */
    name_map_.put(new Integer(33),"buoy_wht"); /* white map buoy symbol */
    id_map_.put("buoy_wht",new Integer(33)); /* white map buoy symbol */
    name_map_.put(new Integer(34),"buoy_wht_grn"); /* white/green map buoy symbol */
    id_map_.put("buoy_wht_grn",new Integer(34)); /* white/green map buoy symbol */
    name_map_.put(new Integer(35),"buoy_wht_red"); /* white/red map buoy symbol */
    id_map_.put("buoy_wht_red",new Integer(35)); /* white/red map buoy symbol */
    name_map_.put(new Integer(36),"dot"); /* white dot symbol */
    id_map_.put("dot",new Integer(36)); /* white dot symbol */
    name_map_.put(new Integer(37),"rbcn"); /* radio beacon symbol */
    id_map_.put("rbcn",new Integer(37)); /* radio beacon symbol */
/*------------------------------------------------------
  leave space for more navaids (up to 128 total)
  ------------------------------------------------------*/
    name_map_.put(new Integer(150),"boat_ramp"); /* boat ramp symbol */
    id_map_.put("boat_ramp",new Integer(150)); /* boat ramp symbol */
    name_map_.put(new Integer(151),"camp"); /* campground symbol */
    id_map_.put("camp",new Integer(151)); /* campground symbol */
    name_map_.put(new Integer(152),"restrooms"); /* restrooms symbol */
    id_map_.put("restrooms",new Integer(152)); /* restrooms symbol */
    name_map_.put(new Integer(153),"showers"); /* shower symbol */
    id_map_.put("showers",new Integer(153)); /* shower symbol */
    name_map_.put(new Integer(154),"drinking_wtr"); /* drinking water symbol */
    id_map_.put("drinking_wtr",new Integer(154)); /* drinking water symbol */
    name_map_.put(new Integer(155),"phone"); /* telephone symbol */
    id_map_.put("phone",new Integer(155)); /* telephone symbol */
    name_map_.put(new Integer(156),"1st_aid"); /* first aid symbol */
    id_map_.put("1st_aid",new Integer(156)); /* first aid symbol */
    name_map_.put(new Integer(157),"info"); /* information symbol */
    id_map_.put("info",new Integer(157)); /* information symbol */
    name_map_.put(new Integer(158),"parking"); /* parking symbol */
    id_map_.put("parking",new Integer(158)); /* parking symbol */
    name_map_.put(new Integer(159),"park"); /* park symbol */
    id_map_.put("park",new Integer(159)); /* park symbol */
    name_map_.put(new Integer(160),"picnic"); /* picnic symbol */
    id_map_.put("picnic",new Integer(160)); /* picnic symbol */
    name_map_.put(new Integer(161),"scenic"); /* scenic area symbol */
    id_map_.put("scenic",new Integer(161)); /* scenic area symbol */
    name_map_.put(new Integer(162),"skiing"); /* skiing symbol */
    id_map_.put("skiing",new Integer(162)); /* skiing symbol */
    name_map_.put(new Integer(163),"swimming"); /* swimming symbol */
    id_map_.put("swimming",new Integer(163)); /* swimming symbol */
    name_map_.put(new Integer(164),"dam"); /* dam symbol */
    id_map_.put("dam",new Integer(164)); /* dam symbol */
    name_map_.put(new Integer(165),"controlled"); /* controlled area symbol */
    id_map_.put("controlled",new Integer(165)); /* controlled area symbol */
    name_map_.put(new Integer(166),"danger"); /* danger symbol */
    id_map_.put("danger",new Integer(166)); /* danger symbol */
    name_map_.put(new Integer(167),"restricted"); /* restricted area symbol */
    id_map_.put("restricted",new Integer(167)); /* restricted area symbol */
    name_map_.put(new Integer(168),"null_2"); /* null symbol */
    id_map_.put("null_2",new Integer(168)); /* null symbol */
    name_map_.put(new Integer(169),"ball"); /* ball symbol */
    id_map_.put("ball",new Integer(169)); /* ball symbol */
    name_map_.put(new Integer(170),"car"); /* car symbol */
    id_map_.put("car",new Integer(170)); /* car symbol */
    name_map_.put(new Integer(171),"deer"); /* deer symbol */
    id_map_.put("deer",new Integer(171)); /* deer symbol */
    name_map_.put(new Integer(172),"shpng_cart"); /* shopping cart symbol */
    id_map_.put("shpng_cart",new Integer(172)); /* shopping cart symbol */
    name_map_.put(new Integer(173),"lodging"); /* lodging symbol */
    id_map_.put("lodging",new Integer(173)); /* lodging symbol */
    name_map_.put(new Integer(174),"mine"); /* mine symbol */
    id_map_.put("mine",new Integer(174)); /* mine symbol */
    name_map_.put(new Integer(175),"trail_head"); /* trail head symbol */
    id_map_.put("trail_head",new Integer(175)); /* trail head symbol */
    name_map_.put(new Integer(176),"truck_stop"); /* truck stop symbol */
    id_map_.put("truck_stop",new Integer(176)); /* truck stop symbol */
    name_map_.put(new Integer(177),"user_exit"); /* user exit symbol */
    id_map_.put("user_exit",new Integer(177)); /* user exit symbol */
    name_map_.put(new Integer(178),"flag"); /* flag symbol */
    id_map_.put("flag",new Integer(178)); /* flag symbol */
    name_map_.put(new Integer(179),"circle_x"); /* circle with x in the center */
    id_map_.put("circle_x",new Integer(179)); /* circle with x in the center */
/*---------------------------------------------------------------
  Symbols for land (group 1...8192-16383...bits 15-13=001).
  ---------------------------------------------------------------*/

    name_map_.put(new Integer(8192),"is_hwy"); /* interstate hwy symbol */
    id_map_.put("is_hwy",new Integer(8192)); /* interstate hwy symbol */
    name_map_.put(new Integer(8193),"us_hwy"); /* us hwy symbol */
    id_map_.put("us_hwy",new Integer(8193)); /* us hwy symbol */
    name_map_.put(new Integer(8194),"st_hwy"); /* state hwy symbol */
    id_map_.put("st_hwy",new Integer(8194)); /* state hwy symbol */
    name_map_.put(new Integer(8195),"mi_mrkr"); /* mile marker symbol */
    id_map_.put("mi_mrkr",new Integer(8195)); /* mile marker symbol */
    name_map_.put(new Integer(8196),"trcbck"); /* TracBack (feet) symbol */
    id_map_.put("trcbck",new Integer(8196)); /* TracBack (feet) symbol */
    name_map_.put(new Integer(8197),"golf"); /* golf symbol */
    id_map_.put("golf",new Integer(8197)); /* golf symbol */
    name_map_.put(new Integer(8198),"sml_cty"); /* small city symbol */
    id_map_.put("sml_cty",new Integer(8198)); /* small city symbol */
    name_map_.put(new Integer(8199),"med_cty"); /* medium city symbol */
    id_map_.put("med_cty",new Integer(8199)); /* medium city symbol */
    name_map_.put(new Integer(8200),"lrg_cty"); /* large city symbol */
    id_map_.put("lrg_cty",new Integer(8200)); /* large city symbol */
    name_map_.put(new Integer(8201),"freeway"); /* intl freeway hwy symbol */
    id_map_.put("freeway",new Integer(8201)); /* intl freeway hwy symbol */
    name_map_.put(new Integer(8202),"ntl_hwy"); /* intl national hwy symbol */
    id_map_.put("ntl_hwy",new Integer(8202)); /* intl national hwy symbol */
    name_map_.put(new Integer(8203),"cap_cty"); /* capitol city symbol (star) */
    id_map_.put("cap_cty",new Integer(8203)); /* capitol city symbol (star) */
    name_map_.put(new Integer(8204),"amuse_pk"); /* amusement park symbol */
    id_map_.put("amuse_pk",new Integer(8204)); /* amusement park symbol */
    name_map_.put(new Integer(8205),"bowling"); /* bowling symbol */
    id_map_.put("bowling",new Integer(8205)); /* bowling symbol */
    name_map_.put(new Integer(8206),"car_rental"); /* car rental symbol */
    id_map_.put("car_rental",new Integer(8206)); /* car rental symbol */
    name_map_.put(new Integer(8207),"car_repair"); /* car repair symbol */
    id_map_.put("car_repair",new Integer(8207)); /* car repair symbol */
    name_map_.put(new Integer(8208),"fastfood"); /* fast food symbol */
    id_map_.put("fastfood",new Integer(8208)); /* fast food symbol */
    name_map_.put(new Integer(8209),"fitness"); /* fitness symbol */
    id_map_.put("fitness",new Integer(8209)); /* fitness symbol */
    name_map_.put(new Integer(8210),"movie"); /* movie symbol */
    id_map_.put("movie",new Integer(8210)); /* movie symbol */
    name_map_.put(new Integer(8211),"museum"); /* museum symbol */
    id_map_.put("museum",new Integer(8211)); /* museum symbol */
    name_map_.put(new Integer(8212),"pharmacy"); /* pharmacy symbol */
    id_map_.put("pharmacy",new Integer(8212)); /* pharmacy symbol */
    name_map_.put(new Integer(8213),"pizza"); /* pizza symbol */
    id_map_.put("pizza",new Integer(8213)); /* pizza symbol */
    name_map_.put(new Integer(8214),"post_ofc"); /* post office symbol */
    id_map_.put("post_ofc",new Integer(8214)); /* post office symbol */
    name_map_.put(new Integer(8215),"rv_park"); /* RV park symbol */
    id_map_.put("rv_park",new Integer(8215)); /* RV park symbol */
    name_map_.put(new Integer(8216),"school"); /* school symbol */
    id_map_.put("school",new Integer(8216)); /* school symbol */
    name_map_.put(new Integer(8217),"stadium"); /* stadium symbol */
    id_map_.put("stadium",new Integer(8217)); /* stadium symbol */
    name_map_.put(new Integer(8218),"store"); /* dept. store symbol */
    id_map_.put("store",new Integer(8218)); /* dept. store symbol */
    name_map_.put(new Integer(8219),"zoo"); /* zoo symbol */
    id_map_.put("zoo",new Integer(8219)); /* zoo symbol */
    name_map_.put(new Integer(8220),"gas_plus"); /* convenience store symbol */
    id_map_.put("gas_plus",new Integer(8220)); /* convenience store symbol */
    name_map_.put(new Integer(8221),"faces"); /* live theater symbol */
    id_map_.put("faces",new Integer(8221)); /* live theater symbol */
    name_map_.put(new Integer(8222),"ramp_int"); /* ramp intersection symbol */
    id_map_.put("ramp_int",new Integer(8222)); /* ramp intersection symbol */
    name_map_.put(new Integer(8223),"st_int"); /* street intersection symbol */
    id_map_.put("st_int",new Integer(8223)); /* street intersection symbol */
    name_map_.put(new Integer(8226),"weigh_sttn"); /* inspection/weigh station symbol */
    id_map_.put("weigh_sttn",new Integer(8226)); /* inspection/weigh station symbol */
    name_map_.put(new Integer(8227),"toll_booth"); /* toll booth symbol */
    id_map_.put("toll_booth",new Integer(8227)); /* toll booth symbol */
    name_map_.put(new Integer(8228),"elev_pt"); /* elevation point symbol */
    id_map_.put("elev_pt",new Integer(8228)); /* elevation point symbol */
    name_map_.put(new Integer(8229),"ex_no_srvc"); /* exit without services symbol */
    id_map_.put("ex_no_srvc",new Integer(8229)); /* exit without services symbol */
    name_map_.put(new Integer(8230),"geo_place_mm"); /* Geographic place name, man-made */
    id_map_.put("geo_place_mm",new Integer(8230)); /* Geographic place name, man-made */
    name_map_.put(new Integer(8231),"geo_place_wtr"); /* Geographic place name, water */
    id_map_.put("geo_place_wtr",new Integer(8231)); /* Geographic place name, water */
    name_map_.put(new Integer(8232),"geo_place_lnd"); /* Geographic place name, land */
    id_map_.put("geo_place_lnd",new Integer(8232)); /* Geographic place name, land */
    name_map_.put(new Integer(8233),"bridge"); /* bridge symbol */
    id_map_.put("bridge",new Integer(8233)); /* bridge symbol */
    name_map_.put(new Integer(8234),"building"); /* building symbol */
    id_map_.put("building",new Integer(8234)); /* building symbol */
    name_map_.put(new Integer(8235),"cemetery"); /* cemetery symbol */
    id_map_.put("cemetery",new Integer(8235)); /* cemetery symbol */
    name_map_.put(new Integer(8236),"church"); /* church symbol */
    id_map_.put("church",new Integer(8236)); /* church symbol */
    name_map_.put(new Integer(8237),"civil"); /* civil location symbol */
    id_map_.put("civil",new Integer(8237)); /* civil location symbol */
    name_map_.put(new Integer(8238),"crossing"); /* crossing symbol */
    id_map_.put("crossing",new Integer(8238)); /* crossing symbol */
    name_map_.put(new Integer(8239),"hist_town"); /* historical town symbol */
    id_map_.put("hist_town",new Integer(8239)); /* historical town symbol */
    name_map_.put(new Integer(8240),"levee"); /* levee symbol */
    id_map_.put("levee",new Integer(8240)); /* levee symbol */
    name_map_.put(new Integer(8241),"military"); /* military location symbol */
    id_map_.put("military",new Integer(8241)); /* military location symbol */
    name_map_.put(new Integer(8242),"oil_field"); /* oil field symbol */
    id_map_.put("oil_field",new Integer(8242)); /* oil field symbol */
    name_map_.put(new Integer(8243),"tunnel"); /* tunnel symbol */
    id_map_.put("tunnel",new Integer(8243)); /* tunnel symbol */
    name_map_.put(new Integer(8244),"beach"); /* beach symbol */
    id_map_.put("beach",new Integer(8244)); /* beach symbol */
    name_map_.put(new Integer(8245),"forest"); /* forest symbol */
    id_map_.put("forest",new Integer(8245)); /* forest symbol */
    name_map_.put(new Integer(8246),"summit"); /* summit symbol */
    id_map_.put("summit",new Integer(8246)); /* summit symbol */
    name_map_.put(new Integer(8247),"lrg_ramp_int"); /* large ramp intersection symbol */
    id_map_.put("lrg_ramp_int",new Integer(8247)); /* large ramp intersection symbol */
    name_map_.put(new Integer(8248),"lrg_ex_no_srvc"); /* large exit without services smbl */
    id_map_.put("lrg_ex_no_srvc",new Integer(8248)); /* large exit without services smbl */
    name_map_.put(new Integer(8249),"badge"); /* police/official badge symbol */
    id_map_.put("badge",new Integer(8249)); /* police/official badge symbol */
    name_map_.put(new Integer(8250),"cards"); /* gambling/casino symbol */
    id_map_.put("cards",new Integer(8250)); /* gambling/casino symbol */
    name_map_.put(new Integer(8251),"snowski"); /* snow skiing symbol */
    id_map_.put("snowski",new Integer(8251)); /* snow skiing symbol */
    name_map_.put(new Integer(8252),"iceskate"); /* ice skating symbol */
    id_map_.put("iceskate",new Integer(8252)); /* ice skating symbol */
    name_map_.put(new Integer(8253),"wrecker"); /* tow truck (wrecker) symbol */
    id_map_.put("wrecker",new Integer(8253)); /* tow truck (wrecker) symbol */
    name_map_.put(new Integer(8254),"border"); /* border crossing (port of entry) */
    id_map_.put("border",new Integer(8254)); /* border crossing (port of entry) */
/*---------------------------------------------------------------
  Symbols for aviation (group 2...16383-24575...bits 15-13=010).
  ---------------------------------------------------------------*/

    name_map_.put(new Integer(16384),"airport"); /* airport symbol */
    id_map_.put("airport",new Integer(16384)); /* airport symbol */
    name_map_.put(new Integer(16385),"int"); /* intersection symbol */
    id_map_.put("int",new Integer(16385)); /* intersection symbol */
    name_map_.put(new Integer(16386),"ndb"); /* non-directional beacon symbol */
    id_map_.put("ndb",new Integer(16386)); /* non-directional beacon symbol */
    name_map_.put(new Integer(16387),"vor"); /* VHF omni-range symbol */
    id_map_.put("vor",new Integer(16387)); /* VHF omni-range symbol */
    name_map_.put(new Integer(16388),"heliport"); /* heliport symbol */
    id_map_.put("heliport",new Integer(16388)); /* heliport symbol */
    name_map_.put(new Integer(16389),"private"); /* private field symbol */
    id_map_.put("private",new Integer(16389)); /* private field symbol */
    name_map_.put(new Integer(16390),"soft_fld"); /* soft field symbol */
    id_map_.put("soft_fld",new Integer(16390)); /* soft field symbol */
    name_map_.put(new Integer(16391),"tall_tower"); /* tall tower symbol */
    id_map_.put("tall_tower",new Integer(16391)); /* tall tower symbol */
    name_map_.put(new Integer(16392),"short_tower"); /* short tower symbol */
    id_map_.put("short_tower",new Integer(16392)); /* short tower symbol */
    name_map_.put(new Integer(16393),"glider"); /* glider symbol */
    id_map_.put("glider",new Integer(16393)); /* glider symbol */
    name_map_.put(new Integer(16394),"ultralight"); /* ultralight symbol */
    id_map_.put("ultralight",new Integer(16394)); /* ultralight symbol */
    name_map_.put(new Integer(16395),"parachute"); /* parachute symbol */
    id_map_.put("parachute",new Integer(16395)); /* parachute symbol */
    name_map_.put(new Integer(16396),"vortac"); /* VOR/TACAN symbol */
    id_map_.put("vortac",new Integer(16396)); /* VOR/TACAN symbol */
    name_map_.put(new Integer(16397),"vordme"); /* VOR-DME symbol */
    id_map_.put("vordme",new Integer(16397)); /* VOR-DME symbol */
    name_map_.put(new Integer(16398),"faf"); /* first approach fix */
    id_map_.put("faf",new Integer(16398)); /* first approach fix */
    name_map_.put(new Integer(16399),"lom"); /* localizer outer marker */
    id_map_.put("lom",new Integer(16399)); /* localizer outer marker */
    name_map_.put(new Integer(16400),"map"); /* missed approach point */
    id_map_.put("map",new Integer(16400)); /* missed approach point */
    name_map_.put(new Integer(16401),"tacan"); /* TACAN symbol */
    id_map_.put("tacan",new Integer(16401)); /* TACAN symbol */
    name_map_.put(new Integer(16402),"seaplane"); /* Seaplane Base */
    id_map_.put("seaplane",new Integer(16402)); /* Seaplane Base */
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

//----------------------------------------------------------------------
/**
 * Returns the garmin symbol id for the given symbol name.
 *
 * @param symbol_name the name of the symbol (as defined in the
 * garmin protocol specification, page 33).
 * @return the id of the symbol or -1, if no symbol could be found.
 */
  public static int getSymbolId(String symbol_name)
  {
    if(id_map_ == null)
      initMap();
    Integer id = (Integer)id_map_.get(symbol_name);
    if(id == null)
      return(-1);
    return(id.intValue());
  }
}


