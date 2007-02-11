
#
#  Copyright (c) 2004 Jonas Buchli <jonas@buchli.org>
#
#  May be copied or modified under the terms of the GNU General Public
#  License.  See COPYING for more information.


#
#  history
#
#   4.6.2004 - first version
# 14.8.2004 - more map scalings added (Samuel Benz)

package SwissToPix;

use POSIX qw(ceil floor);
use Exporter;

@ISA = qw(Exporter);

@EXPORT = qw{ll2lv03 lv03tosn round swissgrid lv032ll wgs84};


# function to convert wgs84 coordinates to swiss coordinates (lv03) 
# this is an approximation (precision +- 1m)
# see:
# http://www.swisstopo.ch/data/geo/naeherung_d.pdf
#
# $1: latitude
# $2: longitude
sub ll2lv03 
{

        $phi = $_[0];
        $lambda = $_[1];
        #print "phi = $phi lambda = $lambda\n";
        # 1. translate to "sexagesimalseconds"
        # 2. auxilary variables
        $phip = ($phi - 169028.66)/10000;
        $lambdap = ($lambda - 26782.5)/10000;
        #print "phip = $phip lambdap = $lambdap\n";

        # 3.
        $y = ((600072.37)+(211455.93)*$lambdap - (10938.51) * $lambdap * $phip - (0.36) * $lambdap * ($phip ** 2) - (44.54) * ($lambdap ** 3));
        #$y = (600072.37);
        $x = ((200147.07) + (308807.95) * $phip + (3745.25) * $lambdap ** 2 + (76.63) * $phip ** 2 - (194.56) * $lambdap**2 *  $phip + (119.79) * $phip ** 3);
        #print "ll2lv03 x = $x y = $y\n";
        return ($x,$y);

}

sub swissgrid
{

    my $lat=shift;
    my $lon=shift;
    
    ($x,$y)=ll2lv03($lat*3600,$lon*3600);
    return round($x),round($y);

}


sub lv032ll 
{

        $x = $_[0];
        $y = $_[1];
        
        $xp = ($x - 200000)/1000000;
        $yp = ($y - 600000)/1000000;

        $lambdap = 2.6779094 + (4.728982 * $yp) + (0.791484 * $yp * $xp) + (0.1306 * $yp * ($xp ** 2)) - (0.0436 * ($yp ** 3));
        
        $phip = 16.9023892 + (3.238272 * $xp) - (0.270978 * ($yp ** 2)) - (0.002528 * ($xp ** 2)) - (0.0447 * ($yp**2) *  $xp) - (0.0140 * ($xp ** 3));
	
        return ($phip*10000,$lambdap*10000);

}

sub wgs84
{

    my $lat=shift;
    my $lon=shift;
    
    ($x,$y)=lv032ll($lat,$lon);
    
    $y= sprintf("%6f",$y/3600);
    $x= sprintf("%6f",$x/3600);
    
    return ($x,$y);  

}

# converts northing,eastings to the sheet number on which this 
# coord. can be found
sub lv03tosn
{
	$northing = $_[0];
	$easting = $_[1];
	$map = $_[2];
	
	if($map eq ""){
		$map = 50;
	}
	
	my $sheetnumber;
	
	SWITCH: {
	if($map == 100){
		$sheetnumber= 25+5*(4-floor(($northing-62000)/48000)) +  floor(($easting-480000)/70000);
		last SWITCH;
	}
	if($map == 50){
		$sheetnumber= 200+10*(9-floor(($northing-62000)/24000)) +  floor(($easting-480000)/35000);
		last SWITCH;
	}
	if($map == 25){
		$sheetnumber=600+20*(19-floor(($northing-302000)/12000)) +  floor(($easting-480000)/17500);
		last SWITCH;
	}	
	}
	
	return $sheetnumber;
}


sub round
{
	my($number) = shift; 
	return int($number + .5 * ($number <=> 0));
}
