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
 * This class contains information on all possible waypoint types due to
 * Garmin specification.
 *
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
    name_map_.put(new Long(0),"anchor"); /* white anchor symbol */
    id_map_.put("anchor",new Long(0)); /* white anchor symbol */
    name_map_.put(new Long(1),"bell"); /* white bell symbol */
    id_map_.put("bell",new Long(1)); /* white bell symbol */
    name_map_.put(new Long(2),"diamond_grn"); /* green diamond symbol */
    id_map_.put("diamond_grn",new Long(2)); /* green diamond symbol */
    name_map_.put(new Long(3),"diamond_red"); /* red diamond symbol */
    id_map_.put("diamond_red",new Long(3)); /* red diamond symbol */
    name_map_.put(new Long(4),"dive1"); /* diver down flag 1 */
    id_map_.put("dive1",new Long(4)); /* diver down flag 1 */
    name_map_.put(new Long(5),"dive2"); /* diver down flag 2 */
    id_map_.put("dive2",new Long(5)); /* diver down flag 2 */
    name_map_.put(new Long(6),"dollar"); /* white dollar symbol */
    id_map_.put("dollar",new Long(6)); /* white dollar symbol */
    name_map_.put(new Long(7),"fish"); /* white fish symbol */
    id_map_.put("fish",new Long(7)); /* white fish symbol */
    name_map_.put(new Long(8),"fuel"); /* white fuel symbol */
    id_map_.put("fuel",new Long(8)); /* white fuel symbol */
    name_map_.put(new Long(9),"horn"); /* white horn symbol */
    id_map_.put("horn",new Long(9)); /* white horn symbol */
    name_map_.put(new Long(10),"house"); /* white house symbol */
    id_map_.put("house",new Long(10)); /* white house symbol */
    name_map_.put(new Long(11),"knife"); /* white knife & fork symbol */
    id_map_.put("knife",new Long(11)); /* white knife & fork symbol */
    name_map_.put(new Long(12),"light"); /* white light symbol */
    id_map_.put("light",new Long(12)); /* white light symbol */
    name_map_.put(new Long(13),"mug"); /* white mug symbol */
    id_map_.put("mug",new Long(13)); /* white mug symbol */
    name_map_.put(new Long(14),"skull"); /* white skull and crossbones symbol*/
    id_map_.put("skull",new Long(14)); /* white skull and crossbones symbol*/
    name_map_.put(new Long(15),"square_grn"); /* green square symbol */
    id_map_.put("square_grn",new Long(15)); /* green square symbol */
    name_map_.put(new Long(16),"square_red"); /* red square symbol */
    id_map_.put("square_red",new Long(16)); /* red square symbol */
    name_map_.put(new Long(17),"wbuoy"); /* white buoy waypoint symbol */
    id_map_.put("wbuoy",new Long(17)); /* white buoy waypoint symbol */
    name_map_.put(new Long(18),"wpt_dot"); /* waypoint dot */
    id_map_.put("wpt_dot",new Long(18)); /* waypoint dot */
    name_map_.put(new Long(19),"wreck"); /* white wreck symbol */
    id_map_.put("wreck",new Long(19)); /* white wreck symbol */
    name_map_.put(new Long(20),"null"); /* null symbol (transparent) */
    id_map_.put("null",new Long(20)); /* null symbol (transparent) */
    name_map_.put(new Long(21),"mob"); /* man overboard symbol */
    id_map_.put("mob",new Long(21)); /* man overboard symbol */
/*------------------------------------------------------
  marine navaid symbols
  ------------------------------------------------------*/
    name_map_.put(new Long(22),"buoy_ambr"); /* amber map buoy symbol */
    id_map_.put("buoy_ambr",new Long(22)); /* amber map buoy symbol */
    name_map_.put(new Long(23),"buoy_blck"); /* black map buoy symbol */
    id_map_.put("buoy_blck",new Long(23)); /* black map buoy symbol */
    name_map_.put(new Long(24),"buoy_blue"); /* blue map buoy symbol */
    id_map_.put("buoy_blue",new Long(24)); /* blue map buoy symbol */
    name_map_.put(new Long(25),"buoy_grn"); /* green map buoy symbol */
    id_map_.put("buoy_grn",new Long(25)); /* green map buoy symbol */
    name_map_.put(new Long(26),"buoy_grn_red"); /* green/red map buoy symbol */
    id_map_.put("buoy_grn_red",new Long(26)); /* green/red map buoy symbol */
    name_map_.put(new Long(27),"buoy_grn_wht"); /* green/white map buoy symbol */
    id_map_.put("buoy_grn_wht",new Long(27)); /* green/white map buoy symbol */
    name_map_.put(new Long(28),"buoy_orng"); /* orange map buoy symbol */
    id_map_.put("buoy_orng",new Long(28)); /* orange map buoy symbol */
    name_map_.put(new Long(29),"buoy_red"); /* red map buoy symbol */
    id_map_.put("buoy_red",new Long(29)); /* red map buoy symbol */
    name_map_.put(new Long(30),"buoy_red_grn"); /* red/green map buoy symbol */
    id_map_.put("buoy_red_grn",new Long(30)); /* red/green map buoy symbol */
    name_map_.put(new Long(31),"buoy_red_wht"); /* red/white map buoy symbol */
    id_map_.put("buoy_red_wht",new Long(31)); /* red/white map buoy symbol */
    name_map_.put(new Long(32),"buoy_violet"); /* violet map buoy symbol */
    id_map_.put("buoy_violet",new Long(32)); /* violet map buoy symbol */
    name_map_.put(new Long(33),"buoy_wht"); /* white map buoy symbol */
    id_map_.put("buoy_wht",new Long(33)); /* white map buoy symbol */
    name_map_.put(new Long(34),"buoy_wht_grn"); /* white/green map buoy symbol */
    id_map_.put("buoy_wht_grn",new Long(34)); /* white/green map buoy symbol */
    name_map_.put(new Long(35),"buoy_wht_red"); /* white/red map buoy symbol */
    id_map_.put("buoy_wht_red",new Long(35)); /* white/red map buoy symbol */
    name_map_.put(new Long(36),"dot"); /* white dot symbol */
    id_map_.put("dot",new Long(36)); /* white dot symbol */
    name_map_.put(new Long(37),"rbcn"); /* radio beacon symbol */
    id_map_.put("rbcn",new Long(37)); /* radio beacon symbol */
/*------------------------------------------------------
  leave space for more navaids (up to 128 total)
  ------------------------------------------------------*/
    name_map_.put(new Long(150),"boat_ramp"); /* boat ramp symbol */
    id_map_.put("boat_ramp",new Long(150)); /* boat ramp symbol */
    name_map_.put(new Long(151),"camp"); /* campground symbol */
    id_map_.put("camp",new Long(151)); /* campground symbol */
    name_map_.put(new Long(152),"restrooms"); /* restrooms symbol */
    id_map_.put("restrooms",new Long(152)); /* restrooms symbol */
    name_map_.put(new Long(153),"showers"); /* shower symbol */
    id_map_.put("showers",new Long(153)); /* shower symbol */
    name_map_.put(new Long(154),"drinking_wtr"); /* drinking water symbol */
    id_map_.put("drinking_wtr",new Long(154)); /* drinking water symbol */
    name_map_.put(new Long(155),"phone"); /* telephone symbol */
    id_map_.put("phone",new Long(155)); /* telephone symbol */
    name_map_.put(new Long(156),"1st_aid"); /* first aid symbol */
    id_map_.put("1st_aid",new Long(156)); /* first aid symbol */
    name_map_.put(new Long(157),"info"); /* information symbol */
    id_map_.put("info",new Long(157)); /* information symbol */
    name_map_.put(new Long(158),"parking"); /* parking symbol */
    id_map_.put("parking",new Long(158)); /* parking symbol */
    name_map_.put(new Long(159),"park"); /* park symbol */
    id_map_.put("park",new Long(159)); /* park symbol */
    name_map_.put(new Long(160),"picnic"); /* picnic symbol */
    id_map_.put("picnic",new Long(160)); /* picnic symbol */
    name_map_.put(new Long(161),"scenic"); /* scenic area symbol */
    id_map_.put("scenic",new Long(161)); /* scenic area symbol */
    name_map_.put(new Long(162),"skiing"); /* skiing symbol */
    id_map_.put("skiing",new Long(162)); /* skiing symbol */
    name_map_.put(new Long(163),"swimming"); /* swimming symbol */
    id_map_.put("swimming",new Long(163)); /* swimming symbol */
    name_map_.put(new Long(164),"dam"); /* dam symbol */
    id_map_.put("dam",new Long(164)); /* dam symbol */
    name_map_.put(new Long(165),"controlled"); /* controlled area symbol */
    id_map_.put("controlled",new Long(165)); /* controlled area symbol */
    name_map_.put(new Long(166),"danger"); /* danger symbol */
    id_map_.put("danger",new Long(166)); /* danger symbol */
    name_map_.put(new Long(167),"restricted"); /* restricted area symbol */
    id_map_.put("restricted",new Long(167)); /* restricted area symbol */
    name_map_.put(new Long(168),"null_2"); /* null symbol */
    id_map_.put("null_2",new Long(168)); /* null symbol */
    name_map_.put(new Long(169),"ball"); /* ball symbol */
    id_map_.put("ball",new Long(169)); /* ball symbol */
    name_map_.put(new Long(170),"car"); /* car symbol */
    id_map_.put("car",new Long(170)); /* car symbol */
    name_map_.put(new Long(171),"deer"); /* deer symbol */
    id_map_.put("deer",new Long(171)); /* deer symbol */
    name_map_.put(new Long(172),"shpng_cart"); /* shopping cart symbol */
    id_map_.put("shpng_cart",new Long(172)); /* shopping cart symbol */
    name_map_.put(new Long(173),"lodging"); /* lodging symbol */
    id_map_.put("lodging",new Long(173)); /* lodging symbol */
    name_map_.put(new Long(174),"mine"); /* mine symbol */
    id_map_.put("mine",new Long(174)); /* mine symbol */
    name_map_.put(new Long(175),"trail_head"); /* trail head symbol */
    id_map_.put("trail_head",new Long(175)); /* trail head symbol */
    name_map_.put(new Long(176),"truck_stop"); /* truck stop symbol */
    id_map_.put("truck_stop",new Long(176)); /* truck stop symbol */
    name_map_.put(new Long(177),"user_exit"); /* user exit symbol */
    id_map_.put("user_exit",new Long(177)); /* user exit symbol */
    name_map_.put(new Long(178),"flag"); /* flag symbol */
    id_map_.put("flag",new Long(178)); /* flag symbol */
    name_map_.put(new Long(179),"circle_x"); /* circle with x in the center */
    id_map_.put("circle_x",new Long(179)); /* circle with x in the center */
/*---------------------------------------------------------------
  Symbols for land (group 1...8192-16383...bits 15-13=001).
  ---------------------------------------------------------------*/

    name_map_.put(new Long(8192),"is_hwy"); /* interstate hwy symbol */
    id_map_.put("is_hwy",new Long(8192)); /* interstate hwy symbol */
    name_map_.put(new Long(8193),"us_hwy"); /* us hwy symbol */
    id_map_.put("us_hwy",new Long(8193)); /* us hwy symbol */
    name_map_.put(new Long(8194),"st_hwy"); /* state hwy symbol */
    id_map_.put("st_hwy",new Long(8194)); /* state hwy symbol */
    name_map_.put(new Long(8195),"mi_mrkr"); /* mile marker symbol */
    id_map_.put("mi_mrkr",new Long(8195)); /* mile marker symbol */
    name_map_.put(new Long(8196),"trcbck"); /* TracBack (feet) symbol */
    id_map_.put("trcbck",new Long(8196)); /* TracBack (feet) symbol */
    name_map_.put(new Long(8197),"golf"); /* golf symbol */
    id_map_.put("golf",new Long(8197)); /* golf symbol */
    name_map_.put(new Long(8198),"sml_cty"); /* small city symbol */
    id_map_.put("sml_cty",new Long(8198)); /* small city symbol */
    name_map_.put(new Long(8199),"med_cty"); /* medium city symbol */
    id_map_.put("med_cty",new Long(8199)); /* medium city symbol */
    name_map_.put(new Long(8200),"lrg_cty"); /* large city symbol */
    id_map_.put("lrg_cty",new Long(8200)); /* large city symbol */
    name_map_.put(new Long(8201),"freeway"); /* intl freeway hwy symbol */
    id_map_.put("freeway",new Long(8201)); /* intl freeway hwy symbol */
    name_map_.put(new Long(8202),"ntl_hwy"); /* intl national hwy symbol */
    id_map_.put("ntl_hwy",new Long(8202)); /* intl national hwy symbol */
    name_map_.put(new Long(8203),"cap_cty"); /* capitol city symbol (star) */
    id_map_.put("cap_cty",new Long(8203)); /* capitol city symbol (star) */
    name_map_.put(new Long(8204),"amuse_pk"); /* amusement park symbol */
    id_map_.put("amuse_pk",new Long(8204)); /* amusement park symbol */
    name_map_.put(new Long(8205),"bowling"); /* bowling symbol */
    id_map_.put("bowling",new Long(8205)); /* bowling symbol */
    name_map_.put(new Long(8206),"car_rental"); /* car rental symbol */
    id_map_.put("car_rental",new Long(8206)); /* car rental symbol */
    name_map_.put(new Long(8207),"car_repair"); /* car repair symbol */
    id_map_.put("car_repair",new Long(8207)); /* car repair symbol */
    name_map_.put(new Long(8208),"fastfood"); /* fast food symbol */
    id_map_.put("fastfood",new Long(8208)); /* fast food symbol */
    name_map_.put(new Long(8209),"fitness"); /* fitness symbol */
    id_map_.put("fitness",new Long(8209)); /* fitness symbol */
    name_map_.put(new Long(8210),"movie"); /* movie symbol */
    id_map_.put("movie",new Long(8210)); /* movie symbol */
    name_map_.put(new Long(8211),"museum"); /* museum symbol */
    id_map_.put("museum",new Long(8211)); /* museum symbol */
    name_map_.put(new Long(8212),"pharmacy"); /* pharmacy symbol */
    id_map_.put("pharmacy",new Long(8212)); /* pharmacy symbol */
    name_map_.put(new Long(8213),"pizza"); /* pizza symbol */
    id_map_.put("pizza",new Long(8213)); /* pizza symbol */
    name_map_.put(new Long(8214),"post_ofc"); /* post office symbol */
    id_map_.put("post_ofc",new Long(8214)); /* post office symbol */
    name_map_.put(new Long(8215),"rv_park"); /* RV park symbol */
    id_map_.put("rv_park",new Long(8215)); /* RV park symbol */
    name_map_.put(new Long(8216),"school"); /* school symbol */
    id_map_.put("school",new Long(8216)); /* school symbol */
    name_map_.put(new Long(8217),"stadium"); /* stadium symbol */
    id_map_.put("stadium",new Long(8217)); /* stadium symbol */
    name_map_.put(new Long(8218),"store"); /* dept. store symbol */
    id_map_.put("store",new Long(8218)); /* dept. store symbol */
    name_map_.put(new Long(8219),"zoo"); /* zoo symbol */
    id_map_.put("zoo",new Long(8219)); /* zoo symbol */
    name_map_.put(new Long(8220),"gas_plus"); /* convenience store symbol */
    id_map_.put("gas_plus",new Long(8220)); /* convenience store symbol */
    name_map_.put(new Long(8221),"faces"); /* live theater symbol */
    id_map_.put("faces",new Long(8221)); /* live theater symbol */
    name_map_.put(new Long(8222),"ramp_int"); /* ramp intersection symbol */
    id_map_.put("ramp_int",new Long(8222)); /* ramp intersection symbol */
    name_map_.put(new Long(8223),"st_int"); /* street intersection symbol */
    id_map_.put("st_int",new Long(8223)); /* street intersection symbol */
    name_map_.put(new Long(8226),"weigh_sttn"); /* inspection/weigh station symbol */
    id_map_.put("weigh_sttn",new Long(8226)); /* inspection/weigh station symbol */
    name_map_.put(new Long(8227),"toll_booth"); /* toll booth symbol */
    id_map_.put("toll_booth",new Long(8227)); /* toll booth symbol */
    name_map_.put(new Long(8228),"elev_pt"); /* elevation point symbol */
    id_map_.put("elev_pt",new Long(8228)); /* elevation point symbol */
    name_map_.put(new Long(8229),"ex_no_srvc"); /* exit without services symbol */
    id_map_.put("ex_no_srvc",new Long(8229)); /* exit without services symbol */
    name_map_.put(new Long(8230),"geo_place_mm"); /* Geographic place name, man-made */
    id_map_.put("geo_place_mm",new Long(8230)); /* Geographic place name, man-made */
    name_map_.put(new Long(8231),"geo_place_wtr"); /* Geographic place name, water */
    id_map_.put("geo_place_wtr",new Long(8231)); /* Geographic place name, water */
    name_map_.put(new Long(8232),"geo_place_lnd"); /* Geographic place name, land */
    id_map_.put("geo_place_lnd",new Long(8232)); /* Geographic place name, land */
    name_map_.put(new Long(8233),"bridge"); /* bridge symbol */
    id_map_.put("bridge",new Long(8233)); /* bridge symbol */
    name_map_.put(new Long(8234),"building"); /* building symbol */
    id_map_.put("building",new Long(8234)); /* building symbol */
    name_map_.put(new Long(8235),"cemetery"); /* cemetery symbol */
    id_map_.put("cemetery",new Long(8235)); /* cemetery symbol */
    name_map_.put(new Long(8236),"church"); /* church symbol */
    id_map_.put("church",new Long(8236)); /* church symbol */
    name_map_.put(new Long(8237),"civil"); /* civil location symbol */
    id_map_.put("civil",new Long(8237)); /* civil location symbol */
    name_map_.put(new Long(8238),"crossing"); /* crossing symbol */
    id_map_.put("crossing",new Long(8238)); /* crossing symbol */
    name_map_.put(new Long(8239),"hist_town"); /* historical town symbol */
    id_map_.put("hist_town",new Long(8239)); /* historical town symbol */
    name_map_.put(new Long(8240),"levee"); /* levee symbol */
    id_map_.put("levee",new Long(8240)); /* levee symbol */
    name_map_.put(new Long(8241),"military"); /* military location symbol */
    id_map_.put("military",new Long(8241)); /* military location symbol */
    name_map_.put(new Long(8242),"oil_field"); /* oil field symbol */
    id_map_.put("oil_field",new Long(8242)); /* oil field symbol */
    name_map_.put(new Long(8243),"tunnel"); /* tunnel symbol */
    id_map_.put("tunnel",new Long(8243)); /* tunnel symbol */
    name_map_.put(new Long(8244),"beach"); /* beach symbol */
    id_map_.put("beach",new Long(8244)); /* beach symbol */
    name_map_.put(new Long(8245),"forest"); /* forest symbol */
    id_map_.put("forest",new Long(8245)); /* forest symbol */
    name_map_.put(new Long(8246),"summit"); /* summit symbol */
    id_map_.put("summit",new Long(8246)); /* summit symbol */
    name_map_.put(new Long(8247),"lrg_ramp_int"); /* large ramp intersection symbol */
    id_map_.put("lrg_ramp_int",new Long(8247)); /* large ramp intersection symbol */
    name_map_.put(new Long(8248),"lrg_ex_no_srvc"); /* large exit without services smbl */
    id_map_.put("lrg_ex_no_srvc",new Long(8248)); /* large exit without services smbl */
    name_map_.put(new Long(8249),"badge"); /* police/official badge symbol */
    id_map_.put("badge",new Long(8249)); /* police/official badge symbol */
    name_map_.put(new Long(8250),"cards"); /* gambling/casino symbol */
    id_map_.put("cards",new Long(8250)); /* gambling/casino symbol */
    name_map_.put(new Long(8251),"snowski"); /* snow skiing symbol */
    id_map_.put("snowski",new Long(8251)); /* snow skiing symbol */
    name_map_.put(new Long(8252),"iceskate"); /* ice skating symbol */
    id_map_.put("iceskate",new Long(8252)); /* ice skating symbol */
    name_map_.put(new Long(8253),"wrecker"); /* tow truck (wrecker) symbol */
    id_map_.put("wrecker",new Long(8253)); /* tow truck (wrecker) symbol */
    name_map_.put(new Long(8254),"border"); /* border crossing (port of entry) */
    id_map_.put("border",new Long(8254)); /* border crossing (port of entry) */
/*---------------------------------------------------------------
  Symbols for aviation (group 2...16383-24575...bits 15-13=010).
  ---------------------------------------------------------------*/

    name_map_.put(new Long(16384),"airport"); /* airport symbol */
    id_map_.put("airport",new Long(16384)); /* airport symbol */
    name_map_.put(new Long(16385),"int"); /* intersection symbol */
    id_map_.put("int",new Long(16385)); /* intersection symbol */
    name_map_.put(new Long(16386),"ndb"); /* non-directional beacon symbol */
    id_map_.put("ndb",new Long(16386)); /* non-directional beacon symbol */
    name_map_.put(new Long(16387),"vor"); /* VHF omni-range symbol */
    id_map_.put("vor",new Long(16387)); /* VHF omni-range symbol */
    name_map_.put(new Long(16388),"heliport"); /* heliport symbol */
    id_map_.put("heliport",new Long(16388)); /* heliport symbol */
    name_map_.put(new Long(16389),"private"); /* private field symbol */
    id_map_.put("private",new Long(16389)); /* private field symbol */
    name_map_.put(new Long(16390),"soft_fld"); /* soft field symbol */
    id_map_.put("soft_fld",new Long(16390)); /* soft field symbol */
    name_map_.put(new Long(16391),"tall_tower"); /* tall tower symbol */
    id_map_.put("tall_tower",new Long(16391)); /* tall tower symbol */
    name_map_.put(new Long(16392),"short_tower"); /* short tower symbol */
    id_map_.put("short_tower",new Long(16392)); /* short tower symbol */
    name_map_.put(new Long(16393),"glider"); /* glider symbol */
    id_map_.put("glider",new Long(16393)); /* glider symbol */
    name_map_.put(new Long(16394),"ultralight"); /* ultralight symbol */
    id_map_.put("ultralight",new Long(16394)); /* ultralight symbol */
    name_map_.put(new Long(16395),"parachute"); /* parachute symbol */
    id_map_.put("parachute",new Long(16395)); /* parachute symbol */
    name_map_.put(new Long(16396),"vortac"); /* VOR/TACAN symbol */
    id_map_.put("vortac",new Long(16396)); /* VOR/TACAN symbol */
    name_map_.put(new Long(16397),"vordme"); /* VOR-DME symbol */
    id_map_.put("vordme",new Long(16397)); /* VOR-DME symbol */
    name_map_.put(new Long(16398),"faf"); /* first approach fix */
    id_map_.put("faf",new Long(16398)); /* first approach fix */
    name_map_.put(new Long(16399),"lom"); /* localizer outer marker */
    id_map_.put("lom",new Long(16399)); /* localizer outer marker */
    name_map_.put(new Long(16400),"map"); /* missed approach point */
    id_map_.put("map",new Long(16400)); /* missed approach point */
    name_map_.put(new Long(16401),"tacan"); /* TACAN symbol */
    id_map_.put("tacan",new Long(16401)); /* TACAN symbol */
    name_map_.put(new Long(16402),"seaplane"); /* Seaplane Base */
    id_map_.put("seaplane",new Long(16402)); /* Seaplane Base */
  }

//----------------------------------------------------------------------
/**
 * Returns the garmin symbol name for the given symbol code.
 *
 * @param symbol_type the number of the symbol (as defined in the
 * garmin protocol specification, page 33).
 * @return the name of the symbol.
 */
  public static String getSymbolName(long symbol_type)
  {
    if(name_map_ == null)
      initMap();
    String name = (String)name_map_.get(new Long(symbol_type));
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
    Long id = (Long)id_map_.get(symbol_name);
    if(id == null)
      return(-1);
    return(id.intValue());
  }
}


