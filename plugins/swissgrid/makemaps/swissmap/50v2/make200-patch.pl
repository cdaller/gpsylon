#!/usr/bin/perl
#
#
#

$horz=1;
$vert=1;

open(PO, " <  patchorder.txt") || die("can't open patch file file: $!");
open(OUT1," > montagepatches1") || die("can't open patch file file: $!");
open(OUT2," > montagepatches2") || die("can't open patch file file: $!");

foreach(<PO>) {
        chomp;
	if($horz < 28){
      		$pic = $_;
		$pic =~ s/\n//g;
		print OUT1 "sm50-D-200-$pic.tif\n";
		if($horz == 27){
			$pic = $_;
			print $horiz;
			$pic =~ s/\n//g;
			print OUT2 "sm50-D-200-$pic.tif\n";				
		}
		$horz++;
	}elsif($horz < 54){
     		$pic = $_;
		$pic =~ s/\n//g;
		print OUT2 "sm50-D-200-$pic.tif\n";		
	   $horz++;
	}else{
		$pic = $_;
		$pic =~ s/\n//g;
		print OUT2 "sm50-D-200-$pic.tif\n";		
	 $horz=1;
	 $vert++;
	}		
}
