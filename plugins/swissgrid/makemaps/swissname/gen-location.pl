#!/usr/bin/perl
#
#
# Howto install this the location.sql Script into a derby DB?
#
# java -cp derbytools.jar:derby.jar \
#	-Dij.protocol=jdbc:derby: -Dij.database=/home/<user>/.gpsylon/marker/location_marker_db_derby \
#	org.apache.derby.tools.ij 
# ij> run 'location.sql'
# ij> select count(*) from gpsylon.markers;
# ij> exit;
#

BEGIN {
@INC = ("$ENV{'SWISSMAP'}/lib", @INC);
}

use SwissToPix;

# insert into markers (NAME,LATITUDE,LONGITUDE,CATEGORY_ID,LEVEL_OF_DETAIL) values ('test',54.00,23.00,'city',1);


open (LOCATION, "> location.sql");
open (NAMES, "cat swissnames.csv | ") or die;

while(<NAMES>){

	($name,$north,$east,$info,$type)=split(/,/);
	($lat,$lon)=wgs84($north,$east);
	#print LOCATION "\"$name \[$info\]\",$lat,$lon,city\n";
	$name =~ s/'/''/g;
	$info =~ s/'/''/g;
	$type =~ s/\n//g;
	if ($type eq "Andere"){
		$type="others";
	}elsif ($type eq "Berg; Grat"){
		$type="mountain";
        }elsif ($type eq "Flurnamen"){
		$type="agriculture";
        }elsif ($type eq "Fluss"){
		$type="river";
        }elsif ($type eq "Gasthof; Hütte"){
		$type="hotel";
        }elsif ($type eq "Gletscher"){
		$type="landscape";
        }elsif ($type eq "Historisches Objekt"){
		$type="historical";
        }elsif ($type eq "Infrastruktur"){
		$type="industry";
        }elsif ($type eq "Pass"){
		$type="traffic";
        }elsif ($type eq "Region"){
		$type="admin_division";
        }elsif ($type eq "See"){
		$type="lake";
        }elsif ($type eq "Siedlung; Gebäude"){
		$type="city";
        }elsif ($type eq "Verkehrsnetz"){
		$type="traffic";
        }elsif ($type eq "Wald"){
		$type="agriculture";
	}else{
		$type="others";
	}


	print LOCATION "insert into gpsylon.markers (NAME,LATITUDE,LONGITUDE,CATEGORY_ID,LEVEL_OF_DETAIL) values (\'$name \[$info\]\',$lat,$lon,\'$type\',8)\;\n";
	
}

close LOCATION;
close NAMES;
