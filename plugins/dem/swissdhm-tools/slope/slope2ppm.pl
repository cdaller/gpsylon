#!/usr/bin/perl
#
#  Slope Angle -> PGM 
#


# PGM Dateiformat: Magic Cookie, Dimensionen und hoechster Grauwert
print "P3\n";
print "#.\n";
print "701 481\n";
print "255\n";

$line=1;

while (<>) {

    $wert=$_;
    $wert=~s/\n//g;


    
    # color RGB
    if($wert >= 45 ){
    	$normalized = sprintf("212 212 212", $wert);
    }
    elsif($wert >= 40){
    	$normalized = sprintf("206 154 255", $wert);
   }
     elsif($wert >= 35){
    	$normalized = sprintf("255 151 152", $wert);
   }  
      elsif($wert >= 30){
    	$normalized = sprintf("255 255 103", $wert);
   }
  #    elsif($wert > 5){
  #  	$normalized = sprintf("119 236 232", $wert);
  # }   
    else{
    	$normalized = sprintf("255 255 255", $wert);  
    }

    if ($line > 699){
	print "\n";
   	$line=0;
    }else{
    	$line++;
    }
    
    print "$normalized ";
 
}
