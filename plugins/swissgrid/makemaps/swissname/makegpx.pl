#!/usr/bin/perl -w
#
#
BEGIN {
@INC = ("$ENV{'SWISSMAP'}/bin", @INC);
}


#use GarminSymbol;
use SwissToPix;

%syms = ("Siedlung; Gebäude",'1',"Region",'2',"Fluss",'3',"See",'4',"Gletscher",'5',"Berg; Grat",'6',"Pass",'7',"Andere",'8',"Gasthof; Hütte",'9',"Verkehrsnetz",'10',"Infrastruktur",'11',"Historisches Objekt",'12',"Wald",'13',"Flurnamen",'14');



print "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n";
print "<gpx\n";
print " version=\"1.0\"\n";
print " creator=\"makegpx.pl (perl)\"\n";
print " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n";
print " xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd\">\n";

print "<name>SwissNames</name>\n";
print "<author>SwissMap100 / SwissTopo</author>\n";
print "<copyright>Bundesamt für Landestopographie, 3084 Wabern</copyright>\n";
print "<link>http://www.swisstopo.ch</link>\n";

while (defined($line = <STDIN>)) {

	($name,$lat,$lon,$comune,$type)=split(/,/,$line);
	($lat,$lon)=&wgs84($lat,$lon);
	$type =~ s/\n//;
	$sym=$syms{$type};
	
		print "<wpt lat=\"$lat\" lon=\"$lon\"><name>$name</name><desc>$comune</desc><type>$type</type><sym>$sym</sym></wpt>\n";
}

print "</gpx>\n";
