#!/usr/bin/perl
# 
#  script to extract map graphics from axf files as can be found on 
#  swissmap cds
#
#  Copyright (c) 2004 Jonas Buchli <jonas@buchli.org>
#
#  May be copied or modified under the terms of the GNU General Public
#  License.  See COPYING for more information.

#
#  history
#  30.5.2004 - first working version
#  7.2004    - modify to write bzip addr Samuel Benz
#

use POSIX qw(ceil floor);


$axfroot = $ARGV[0] ||  die("missing arguments! \nusage $0 <path>\n");

$ARGV[1] =~ /(\d.*)/ || die("missing arguments! \nusage $1 <axs file>\n");
$sheetnr = $1;


open(AXS,"< $axfroot/$ARGV[1].axs");

print "$axfroot/$ARGV[1].axs\n";
while(<AXS>) {

	if($_ =~ /Height = (\d.*)/){
		if($1 < 500){
			$xytif=$1;
		}else{
			$resy=$1;
		}
	}
	elsif($_ =~ /Width  = (\d.*)/){
		if($1 < 500){
			$xytif=$1;
		}else{
			$resx=$1;
		}
	}

}

$montagex=ceil($resx/$xytif);
$montagey=ceil($resy/$xytif);

printf("%i %i %i %i %i\n",$xytif,$resx,$resy,$montagex,$montagey);

if ($ARGV[1] eq "cn100"){
	$axf = "${axfroot}/D-$ARGV[1].axf";
	$addr = "${axfroot}/A-$ARGV[1].axf";
} else {
	$axf = "${axfroot}/D-$ARGV[1]-0.axf";
	$addr = "${axfroot}/A-$ARGV[1]-0.axf";
}

if ($ARGV[1] eq "cn200" or $ARGV[1] eq "cn100"){
	&extract;
	&patchorder;
	if($ARGV[1] eq "cn100"){
		&patch;
		unlink "patchorder.txt";
	}
}else{
	&extract;
	&patchorder;
	&patch;
	unlink "montagepatches";
	unlink "patchorder.txt";
}

exit;

####################################################3

sub extract
{

# mini tiff extrahieren ....
open(IN, "  xxd $axf  | grep BZh91AY | ") || die("can't open axf file: $!");


while(<IN>) {

	s/(([[:xdigit:]]))(:.*)/$1/;
	chomp;
	push(@index,$_);
	if($#index >= 1) {
		push(@lengths, hex($index[$#index])- hex($index[$#index-1]));
	}
}


$cnt = 0;
$bziperror = 0;
#$indexc = 0;
#foreach(@lengths) {
for ($cnt = 0; $cnt+$bziperror <=  $#index; $cnt++) {
	### print "indexc = $indexc\n";
	$indexc = $cnt+$bziperror;
	$_ = $lengths[$indexc];
	$i =  hex($index[$indexc]);
	$stelle = $indexc-2;

	$filenroot = sprintf("sm50-D-$sheetnr-%d",$i);
	print "exctracting $cnt at index $i with length  $_\n";
	if( $indexc <  $#index) {
		$execstr = "dd if=$axf of=$filenroot.rgb.bz2 bs=1 skip=$i count=$_";
	} else {
		$execstr = "dd if=$axf of=$filenroot.rgb.bz2 bs=1 skip=$i";
	}
	
	print "$execstr\n";
	system($execstr) == 0 
		or die("canot exec $execstr: $!");
	#$execstr = "bunzip2 $filenroot.rgb.bz2;  convert -depth 8 -size 360x360 $filenroot.rgb $filenroot.tif";
	$execstr = "bunzip2 $filenroot.rgb.bz2;"; 
	if(system($execstr) == 0) {
			$execstr="rawtoppm -bgr $xytif $xytif $filenroot.rgb  > $filenroot.ppm; convert $filenroot.ppm -units PixelsPerCentimeter -density 50  $filenroot.tif;  ";
			print "$execstr\n";
			system($execstr) == 0
				or die("canot exec $execstr: $!");
			
			system("rm  $filenroot.rgb  $filenroot.ppm") == 0 
				or die("canot remove: $!");
	} else {
		# the bz2 was corrupted so skip it and reset the image counter
		system("rm  $filenroot.rgb.bz2") == 0 
				or die("canot remove: $!");
		$bziperror++;
		$cnt--;
	}

	
}
}


sub patchorder
{
# patchorder generieren ....
open(IN, "  xxd $addr  |  ") || die("can't open axf file: $!");
open(PF, " > patchorder.txt") || die("can't open patch file file: $!");

$count =0;

while(<IN>) {

	if ($_ =~ /.+:\s(....)\s(....).+/){
	
	# big endian
	$big=sprintf("%i",hex(sprintf("%s%s",$1,$2)));
	# little endian
	$little=unpack("N",pack("V",$big));
	
	# remove header ...
	if ($little > 0 and $count > 5){
	 print  PF "$little\n";
	 }
	 $count++;
	}
}
}


sub patch
{

# now we generate the files to consturuct the maps
$filenroot = sprintf("sm50-D-$sheetnr-");

#open(PO, " <  patchorder.txt") || die("can't open patch file file: $!");
$patchorder = sprintf("head -n %i patchorder.txt | ",$montagex * $montagey);

open(PO, $patchorder) || die("can't open patch file file: $!");
open(MF, " > montagepatches") || die("can't open patch file file: $!");

foreach(<PO>) {
	chomp;
	if($_) {
		print MF  "$filenroot$_.tif\n";
	}
}

if(!($ARGV[1] eq "cn100")){
$mapname =  "KOMB${sheetnr}.png";
$execstr = sprintf("montage -geometry %ix%i+0+0 -tile %ix%i \@montagepatches tmp.tif && convert -crop %ix%i+0+0 tmp.tif $mapname && rm -f *.tif",$xytif,$xytif,$montagex,$montagey,$resx,$resy);
print "$execstr\n";
system($execstr) == 0 
		or die("canot exec $execstr: $!");
}

}

