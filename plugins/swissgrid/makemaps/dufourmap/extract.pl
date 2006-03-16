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


$file="/cdrom/data/map/dufour_map/dufour.blb";
#$file="x100.blb";

$numbersfile="dufour-numbers";
#$numbersfile="x100-numbers";

&extract;
&patch;

system("\\rm -rf images/");
unlink("montagepatches");




##########################################################

sub extract
{

system("mkdir images");

open(POS,"xxd $file | grep -e '0000 1f8b 0800 0000 0000 000b' | ");

$number=1;

while(<POS>){

if($_ =~ /^(.......):......(..)(..).*/){
	$offset=hex($1)+6;
	$length=hex(sprintf("%s%s",$3,$2));
	print "extracting $number: offset=$offset length=$length\n";
	if($length > 10201){
		system("dd if=$file of=image-$number.gz bs=1 skip=$offset count=10202");
		$rest=$length-10202;
		$offset2=$offset+10202+32;
		system("dd if=$file of=tmp-$number.gz bs=1 skip=$offset2 count=$rest");
		system("cat tmp-$number.gz >> image-$number.gz");
		system("cat image-$number.gz | gunzip > images/image-$number");
		$number++;
	}else{
		system("dd if=$file bs=1 skip=$offset count=$length | gunzip > images/image-$number");
		$number++;
	}
}

}

close POS;
system("find . -name \"*.gz\" -exec \\rm -f {} \\;");

}

sub patch
{

$lastmap="";
@pages;

open(PO, " <  $numbersfile") || die("can't open patch file file: $!");
while(<PO>) {
	($patchorder,$y,$x,$size,$map) = split(" ");
	if(!($map eq $lastmap)){
		$lastmap=$map;
		push(@pages,$map);
	}
}
close PO;

while ($page = shift @pages){

	open(PO, " <  $numbersfile") || die("can't open patch file file: $!");
	open(MF, " > montagepatches") || die("can't open patch file file: $!");

	$xmax=0;
	$xmin=1000000;
	$ymax=0;
	$ymin=1000000;

	while(<PO>) {
		($patchorder,$y,$x,$size,$map) = split(" ");
		if($map == $page){
			print MF  "images/image-$patchorder\n";
			if($y > $ymax){
				$ymax=$y;
			}
			if($y < $ymin){
				$ymin=$y;
			}
			if($x > $xmax){
				$xmax=$x;
			}
			if($x < $xmin){
				$xmin=$x;
			}			
		}
	}

	$ytile=(($xmax-$xmin)/2000)+1;
	$xtile=(($ymax-$ymin)/2000)+1;

	print "$page SW: $xmin $ymin  NO: $xmax $ymax\n";
	
	close PO;
	close MF;


	$mapname =  "KOMB$page.png";
	$execstr = sprintf("montage -geometry 200x200+0+0 -tile %ix%i \@montagepatches %s",$xtile,$ytile,$mapname);
	print "$execstr\n";
	system($execstr) == 0 
			or die("canot exec $execstr: $!");
}
}
