#!/usr/bin/perl
#
# Um die Ladezeit zu optimieren, können die grossen Karten (7000/4800pixel) 
# mit diesem Script verkleinert werden.
# 

open(FILE, "< swissmap.txt");
open(OUT, "> swissmap-tile.txt");

$tilenr = 0;
$oldfile = "";

while(<FILE>){

$line = $_;

if(!($line =~ /#/) && $line =~ /pk100/ && !($line =~ /pk1000/)){
	($path,$lat,$lon,$scale,$xw,$yw)=split(/ /,$line);
	($file,$pos)=split(/\|/,$path);
	($posx,$posy)=split(/x/,$pos);
	
	if (!($oldfile eq $file)){
		$tilenr=0;
	}
	$oldfile = $file;
	
	##$file =~ s/schweiz/\/opt\/gps\/data\/schweiz/g;
	$file =~ s/swissmap/\/opt\/gps\/data\/swissmap/g;
	##$file =~ s/swissmap/\/opt\/gps\/data\/dufourmap/g;
	
	$newfile = $file;

	##$newfile =~ s/\/opt\/gps\/data\/schweiz\///g;
	$newfile =~ s/\/opt\/gps\/data\/swissmap\///g;
	##$newfile =~ s/\/opt\/gps\/data\/dufourmap\///g;
	
	##$newfile =~ s/.TIF/-$tilenr.png/g;
	$newfile =~ s/.png/-$tilenr.png/g;
	##$newfile =~ s/.png/-$tilenr.png/g;
	
	$convert = sprintf("convert -verbose -crop %sx%s+%s+%s %s %s",$xw,$yw,$posx,$posy,$file,$newfile);
	print "$convert\n";
	system($convert);
	
	$mapfile = sprintf("swissmap/%s %s %s %s %s %s",$newfile,$lat,$lon,$scale,$xw,$yw);
	##$mapfile = sprintf("dufourmap/%s %s %s %s %s %s",$newfile,$lat,$lon,$scale,$xw,$yw);

	print OUT "$mapfile\n";
	$tilenr++;

}
}

close FILE;
close OUT;
