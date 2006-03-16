#!/usr/bin/perl
#
#

$db = $ARGV[0] ||  die("missing arguments!\n");

open(IN, "xxd -c 127 -g 1 $db  |  ") || die("can't open file: $!");

#CatLt.dat:
#1,"localité, bâtiment","Siedlung, Gebäude","località, casa"
#2,"région","Region","regione"
#3,"rivière","Fluss","fiume"
#4,"lac","See","lago"
#5,"glacier","Gletscher","ghiacciaio"
#6,"montagne, arête","Berg, Grat","monte, spigolo"
#7,"col","Pass","passo"
#8,"autres","Andere","altri"
#9,"auberge, cabane","Gasthof, Hütte","albergo, capanna"
#10,"réseau routier","Verkehrsnetz","rete stradale"
#11,"infrastructure","Infrastruktur","impianti collettivi"
#12,"objet historique","Historisches Objekt","oggetto storico"
#13,"forêt","Wald","bosco"
#14,"lieu-dit","Flurnamen","nome locale"


%cats = ('1',"Siedlung; Gebäude",'2',"Region",'3',"Fluss",'4',"See",'5',"Gletscher",'6',"Berg; Grat",'7',"Pass",'8',"Andere",'9',"Gasthof; Hütte",'10',"Verkehrsnetz",'11',"Infrastruktur",'12',"Historisches Objekt",'13',"Wald",'14',"Flurnamen");

while(<IN>) {


	@line= split(/\s/);
	
	#name
	$name="";
	for ($nc=26;hex($line[$nc]);$nc++){
	   $name = sprintf("%s%c",$name,hex($line[$nc]));
	}
	
	#lon
	$lonbig=sprintf("%i",hex(sprintf("%s%s%s%s",$line[16],$line[17],$line[18],$line[19])));
	$lon=unpack("N",pack("V",$lonbig));
	
	#lat
	$latbig=sprintf("%i",hex(sprintf("%s%s%s%s",$line[21],$line[22],$line[23],$line[24])));
	$lat=unpack("N",pack("V",$latbig));
	
	# comune
	$comune="";
	for ($nc=78;hex($line[$nc]);$nc++){
	   $comune = sprintf("%s%c",$comune,hex($line[$nc]));
	}
	
	# category
	$cat=sprintf("%i",hex(sprintf("%s",$line[120])));
	
	
	printf("%s,%i,%i,%s,%s\n",$name,$lat,$lon,$comune,$cats{$cat});
		
}