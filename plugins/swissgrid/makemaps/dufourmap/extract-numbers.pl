#!/usr/bin/perl
#
#  script to extract maps from SwissTopo DufourMap CD
#
#  Original Project: SwissToPix <http://buchli.org/jonas/swisstopix/swisstopix.html>
#    Copyright (c) 2004 Jonas Buchli <jonas@buchli.org>
#  Maps: Copyright (c) SwissTopo <http://www.swisstopo.ch/en/about/copyright.htm>
#
#  Copyright (c) 2004 Samuel Benz <benz@switch.ch>
#
#   This program is free software; you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation; either version 2 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program; if not, write to the Free Software
#    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA



## xml db dump from dufour.vdb (generated with Valentina Tools http://www.valentina-db.de/)
$file="dufour.xml";
#$file="x100.xml";

open(DB,$file);

while(<DB>){

#<f n="RecID">15</f>
#<f n="m_ID">60337</f>
#<f n="m_Y">578000</f>
#<f n="m_X">278000</f>
#<f n="m_csize">6358</f>
#<f n="m_PID">1</f>
#<f n="m_mapID">26</f>

if($_ =~ /.*RecID.*>(.*)<.*/){
	$number = $1;
	print "$number ";
}elsif($_ =~ /.*m_Y.*>(.*)<.*/){
	$Y = $1;
	print "$Y ";
}elsif($_ =~ /.*m_X.*>(.*)<.*/){
	$X = $1;
	print "$X ";}
elsif($_ =~ /.*m_csize.*>(.*)<.*/){
	$size = $1;
	print "$size ";
}elsif($_ =~ /.*mapID.*>(.*)<.*/){
	$map = $1;
	print "$map \n";
}

}

close DB;
