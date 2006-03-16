#!/usr/bin/perl
#
# Dieses Script generiert ein maps.txt File für Gpsylon mit den Kartenmittelpunkt in WGS84
#
# Zusätzlich schreibt es informatinen für gen-subimage.pl, falls man die Karten zerhacken möchte.

BEGIN {
@INC = ("$ENV{'SWISSMAP'}/lib", @INC);
}

use SwissToPix;


&gen_origin_maps;


open (MAPS, "> swissmap.txt");
open (ORIGIN, "< maps-origin.txt");

# 2Mio origin: 62400,480000 center: 182200,665000 (200m/pixel)
print MAPS "swissmap/pk2000/cc2mio.png 46.78779 8.28991 2000000 1851 1199\n";

# 1Mio origin: -252975,155025 center:  (falsch:178075),760025 (100m/pixel)
print MAPS "#swissmap/pk1000/lk1000.png 46.73465 9.53255 1000000 12100 8361\n";
	  $north=-252975;
	  $east=155025;
	  $nlen=836100;
	  $elen=1210000;
	  $nlenvar=19; # nach 4 7--2
	  $elenvar=1; # alle 4 immer ++2
	  $offsetX=0; # 0 ++ 1750 bis 5250 alle 4
	  $offsetY=0; # nach 4 ++1200
	  
	  for($i=0;$i<10;$i++){
	  	for($j=0;$j<10;$j++){
		($lat,$lon)=wgs84($north+$nlen/20*$nlenvar,$east+$elen/20*$elenvar);
	 	printf(MAPS "swissmap/pk1000/lk1000.png|%ix%i %f %f 1000000 1210 836 \n",$offsetX,$offsetY,$lat,$lon);
		$elenvar=$elenvar+2;
		$offsetX=$offsetX+1210;
		}
		$elenvar=1; 
		$offsetX=0;
		$nlenvar=$nlenvar-2;
		$offsetY=$offsetY+836;
	   }


# 500k origin: 62000,480000 center: 182000,672500 (50m/pixel)
print MAPS "#swissmap/pk500/cn500.png 46.78522 8.38809 500000 7700 4800\n";
	  $north=62000;
	  $east=480000;
	  $nlen=240000;
	  $elen=385000;
	  $nlenvar=7; # nach 4 7--2
	  $elenvar=1; # alle 4 immer ++2
	  $offsetX=0; # 0 ++ 1750 bis 5250 alle 4
	  $offsetY=0; # nach 4 ++1200
	  
	  for($i=0;$i<4;$i++){
	  	for($j=0;$j<4;$j++){
		($lat,$lon)=wgs84($north+$nlen/8*$nlenvar,$east+$elen/8*$elenvar);
	 	printf(MAPS "swissmap/pk500/cn500.png|%ix%i %f %f 500000 1925 1200 \n",$offsetX,$offsetY,$lat,$lon);
		$elenvar=$elenvar+2;
		$offsetX=$offsetX+1925;
		}
		$elenvar=1; 
		$offsetX=0;
		$nlenvar=$nlenvar-2;
		$offsetY=$offsetY+1200;
	   }



# 200k origin: 44100,463000 center: 182000,655000 (20m/pixel)
print MAPS "#swissmap/pk200/KOMB1.png 47.40809 6.89544 200000 9600 6895\n"; # center: 250950,559000
	  $north=182000;
	  $east=463000;
	  $nlen=137900;
	  $elen=192000;
	  $nlenvar=9; # nach 4 7--2
	  $elenvar=1; # alle 4 immer ++2
	  $offsetX=0; # 0 ++ 1750 bis 5250 alle 4
	  $offsetY=0; # nach 4 ++1200
	  
	  for($i=0;$i<5;$i++){
	  	for($j=0;$j<5;$j++){
		($lat,$lon)=wgs84($north+$nlen/10*$nlenvar,$east+$elen/10*$elenvar);
	 	printf(MAPS "swissmap/pk200/KOMB1.png|%ix%i %f %f 200000 1920 1379 \n",$offsetX,$offsetY,$lat,$lon);
		$elenvar=$elenvar+2;
		$offsetX=$offsetX+1920;
		}
		$elenvar=1; 
		$offsetX=0;
		$nlenvar=$nlenvar-2;
		$offsetY=$offsetY+1379;
	   }


print MAPS "#swissmap/pk200/KOMB2.png 47.39206 9.43882 200000 9600 6895\n"; # center: 250950,751000
	  $north=182000;
	  $east=655000;
	  $nlen=137900;
	  $elen=192000;
	  $nlenvar=9; # nach 4 7--2
	  $elenvar=1; # alle 4 immer ++2
	  $offsetX=0; # 0 ++ 1750 bis 5250 alle 4
	  $offsetY=0; # nach 4 ++1200
	  
	  for($i=0;$i<5;$i++){
	  	for($j=0;$j<5;$j++){
		($lat,$lon)=wgs84($north+$nlen/10*$nlenvar,$east+$elen/10*$elenvar);
	 	printf(MAPS "swissmap/pk200/KOMB2.png|%ix%i %f %f 200000 1920 1379 \n",$offsetX,$offsetY,$lat,$lon);
		$elenvar=$elenvar+2;
		$offsetX=$offsetX+1920;
		}
		$elenvar=1; 
		$offsetX=0;
		$nlenvar=$nlenvar-2;
		$offsetY=$offsetY+1379;
	   }


print MAPS "#swissmap/pk200/KOMB3.png 46.16767 6.90779 200000 9600 6895\n"; # center: 113050,559000
	  $north=44100;
	  $east=463000;
	  $nlen=137900;
	  $elen=192000;
	  $nlenvar=9; # nach 4 7--2
	  $elenvar=1; # alle 4 immer ++2
	  $offsetX=0; # 0 ++ 1750 bis 5250 alle 4
	  $offsetY=0; # nach 4 ++1200
	  
	  for($i=0;$i<5;$i++){
	  	for($j=0;$j<5;$j++){
		($lat,$lon)=wgs84($north+$nlen/10*$nlenvar,$east+$elen/10*$elenvar);
	 	printf(MAPS "swissmap/pk200/KOMB3.png|%ix%i %f %f 200000 1920 1379 \n",$offsetX,$offsetY,$lat,$lon);
		$elenvar=$elenvar+2;
		$offsetX=$offsetX+1920;
		}
		$elenvar=1; 
		$offsetX=0;
		$nlenvar=$nlenvar-2;
		$offsetY=$offsetY+1379;
	   }
	   
print MAPS "#swissmap/pk200/KOMB4.png 46.15200 9.39331 200000 9600 6895\n"; # center: 113050,751000
	  $north=44100;
	  $east=655000;
	  $nlen=137900;
	  $elen=192000;
	  $nlenvar=9; # nach 4 7--2
	  $elenvar=1; # alle 4 immer ++2
	  $offsetX=0; # 0 ++ 1750 bis 5250 alle 4
	  $offsetY=0; # nach 4 ++1200
	  
	  for($i=0;$i<5;$i++){
	  	for($j=0;$j<5;$j++){
		($lat,$lon)=wgs84($north+$nlen/10*$nlenvar,$east+$elen/10*$elenvar);
	 	printf(MAPS "swissmap/pk200/KOMB4.png|%ix%i %f %f 200000 1920 1379 \n",$offsetX,$offsetY,$lat,$lon);
		$elenvar=$elenvar+2;
		$offsetX=$offsetX+1920;
		}
		$elenvar=1; 
		$offsetX=0;
		$nlenvar=$nlenvar-2;
		$offsetY=$offsetY+1379;
	   }


while(<ORIGIN>){
	($map,$north,$east,$elen,$nlen)=split(/\s/);
	if($elen eq "17500"){
		$scale=25;
	}elsif($elen eq "35000"){
		$scale=50;	
	}elsif($elen eq "70000"){
		$scale=100;	
	}
	($lat,$lon)=wgs84($north+$nlen/2,$east+$elen/2);
	$masstab=$scale*1000;
	
	if($scale eq "25"){
	   print MAPS "#schweiz/pk$scale/KOMB$map.TIF $lat $lon $masstab 7000 4800 \n";
	   #########################################
	  $nlenvar=7; # nach 4 7--2
	  $elenvar=1; # alle 4 immer ++2
	  $offsetX=0; # 0 ++ 1750 bis 5250 alle 4
	  $offsetY=0; # nach 4 ++1200
	  
	  for($i=0;$i<4;$i++){
	  	for($j=0;$j<4;$j++){
		($lat,$lon)=wgs84($north+$nlen/8*$nlenvar,$east+$elen/8*$elenvar);
	 	printf(MAPS "schweiz/pk%i/KOMB%i.TIF|%ix%i %f %f %i 1750 1200 \n",$scale,$map,$offsetX,$offsetY,$lat,$lon,$masstab);
		$elenvar=$elenvar+2;
		$offsetX=$offsetX+1750;
		}
		$elenvar=1; 
		$offsetX=0;
		$nlenvar=$nlenvar-2;
		$offsetY=$offsetY+1200;
	   }
	
	}else{ 
	 print MAPS "#swissmap/pk$scale/KOMB$map.png $lat $lon $masstab 7000 4800 \n";
	  #########################################
	  $nlenvar=7; # nach 4 7--2
	  $elenvar=1; # alle 4 immer ++2
	  $offsetX=0; # 0 ++ 1750 bis 5250 alle 4
	  $offsetY=0; # nach 4 ++1200
	  
	  for($i=0;$i<4;$i++){
	  	for($j=0;$j<4;$j++){
		($lat,$lon)=wgs84($north+$nlen/8*$nlenvar,$east+$elen/8*$elenvar);
	 	printf(MAPS "swissmap/pk%i/KOMB%i.png|%ix%i %f %f %i 1750 1200 \n",$scale,$map,$offsetX,$offsetY,$lat,$lon,$masstab);
		$elenvar=$elenvar+2;
		$offsetX=$offsetX+1750;
		}
		$elenvar=1; 
		$offsetX=0;
		$nlenvar=$nlenvar-2;
		$offsetY=$offsetY+1200;
	  }	  

	}
}

close ORIGIN;
close MAPS;

exit ;

sub gen_origin_maps {

open (FILE, "> maps-origin.txt");

# find . -name "KOMB*" -exec basename {} \; | sed s/.png/,1,/ | sed s/KOMB//
%validmaps=(
26,1,
27,1,
28,1,
29,1,
30,1,
31,1,
32,1,
33,1,
34,1,
35,1,
36,1,
37,1,
38,1,
39,1,
40,1,
41,1,
42,1,
43,1,
44,1,
45,1,
46,1,
47,1,
48,1,
285,1,
212,1,
205,1,
213,1,
206,1,
207,1,
214,1,
215,1,
216,1,
217,1,
218,1,
222,1,
223,1,
224,1,
225,1,
226,1,
227,1,
228,1,
231,1,
232,1,
233,1,
234,1,
235,1,
236,1,
237,1,
238,1,
239,1,
241,1,
242,1,
243,1,
244,1,
245,1,
246,1,
247,1,
248,1,
249,1,
250,1,
251,1,
252,1,
253,1,
254,1,
255,1,
256,1,
257,1,
258,1,
259,1,
260,1,
261,1,
262,1,
263,1,
264,1,
265,1,
266,1,
267,1,
268,1,
269,1,
270,1,
271,1,
272,1,
273,1,
274,1,
275,1,
276,1,
277,1,
278,1,
279,1,
280,1,
282,1,
283,1,
284,1,
286,1,
287,1,
292,1,
293,1,
294,1,
296,1,
297,1,
350,1,
360,1,
370,1,
1011,1,
1012,1,
1031,1,
1032,1,
1033,1,
1034,1,
1035,1,
1047,1,
1048,1,
1049,1,
1050,1,
1051,1,
1052,1,
1053,1,
1054,1,
1055,1,
1056,1,
1064,1,
1065,1,
1066,1,
1067,1,
1068,1,
1069,1,
1070,1,
1071,1,
1072,1,
1073,1,
1074,1,
1075,1,
1076,1,
1084,1,
1085,1,
1086,1,
1087,1,
1088,1,
1089,1,
1090,1,
1091,1,
1092,1,
1093,1,
1094,1,
1095,1,
1096,1,
1104,1,
1105,1,
1106,1,
1107,1,
1108,1,
1109,1,
1110,1,
1111,1,
1112,1,
1113,1,
1114,1,
1115,1,
1116,1,
1123,1,
1124,1,
1125,1,
1126,1,
1127,1,
1128,1,
1129,1,
1130,1,
1131,1,
1132,1,
1133,1,
1134,1,
1135,1,
1136,1,
1143,1,
1144,1,
1145,1,
1146,1,
1147,1,
1148,1,
1149,1,
1150,1,
1151,1,
1152,1,
1153,1,
1154,1,
1155,1,
1156,1,
1157,1,
1159,1,
1162,1,
1163,1,
1164,1,
1165,1,
1166,1,
1167,1,
1168,1,
1169,1,
1170,1,
1171,1,
1172,1,
1173,1,
1174,1,
1175,1,
1176,1,
1177,1,
1178,1,
1179,1,
1182,1,
1183,1,
1184,1,
1185,1,
1186,1,
1187,1,
1188,1,
1189,1,
1190,1,
1191,1,
1192,1,
1193,1,
1194,1,
1195,1,
1196,1,
1197,1,
1198,1,
1199,1,
1201,1,
1202,1,
1203,1,
1204,1,
1205,1,
1206,1,
1207,1,
1208,1,
1209,1,
1210,1,
1211,1,
1212,1,
1213,1,
1214,1,
1215,1,
1216,1,
1217,1,
1218,1,
1219,1,
1221,1,
1222,1,
1223,1,
1224,1,
1225,1,
1226,1,
1227,1,
1228,1,
1229,1,
1230,1,
1231,1,
1232,1,
1233,1,
1234,1,
1235,1,
1236,1,
1237,1,
1238,1,
1239,1,
1240,1,
1241,1,
1242,1,
1243,1,
1244,1,
1245,1,
1246,1,
1247,1,
1248,1,
1249,1,
1250,1,
1251,1,
1252,1,
1253,1,
1254,1,
1255,1,
1256,1,
1257,1,
1258,1,
1260,1,
1261,1,
1262,1,
1263,1,
1264,1,
1265,1,
1266,1,
1267,1,
1268,1,
1269,1,
1270,1,
1271,1,
1272,1,
1273,1,
1274,1,
1275,1,
1276,1,
1277,1,
1278,1,
1280,1,
1281,1,
1282,1,
1283,1,
1284,1,
1285,1,
1286,1,
1287,1,
1288,1,
1289,1,
1290,1,
1291,1,
1292,1,
1293,1,
1294,1,
1295,1,
1296,1,
1298,1,
1300,1,
1301,1,
1304,1,
1305,1,
1306,1,
1307,1,
1308,1,
1309,1,
1310,1,
1311,1,
1312,1,
1313,1,
1314,1,
1318,1,
1320,1,
1324,1,
1325,1,
1326,1,
1327,1,
1328,1,
1329,1,
1332,1,
1333,1,
1334,1,
1344,1,
1345,1,
1346,1,
1347,1,
1348,1,
1349,1,
1352,1,
1353,1,
1354,1,
1365,1,
1366,1,
1368,1,
1373,1,
1374,1,
2180,1,
2200,1,
2220,1,
2240,1,
2260,1,
);

$elen=17500;
$nlen=12000;
$map=25;

for($i=0;$i<3;$i++){

$res=$map/10;
$scale=$map*1000;

print "# Map $map ############################\n";

for($north=302000-$nlen;$north>=60000;$north-=$nlen){
   for($east=480000;$east<=800000;$east+=$elen){
   	$mapnumber=lv03tosn($north,$east,$map);

	if(exists($validmaps{$mapnumber})){
		
			print FILE "$mapnumber $north $east $elen $nlen \n";
			#($lat,$lon)=wgs84($north+$nlen/2,$east+$elen/2);
			#print MAPS "$lat $lon \n";
			#print MAPS "$scale 1750 1200\n";
		
	}
   }
}

print "\n\n";

$elen=$elen*2;
$nlen=$nlen*2;
$map=$map*2;

}

print FILE "1159 206000 812500 17500 12000\n";
print FILE "1179 194000 812500 17500 12000\n";
print FILE "1199 182000 812500 17500 12000\n";
print FILE "1219 170000 812500 17500 12000\n";
print FILE "1239 158000 812500 17500 12000\n";

print FILE "2180 194000 830000 17500 12000\n"; # 1179bis
print FILE "2200 182000 830000 17500 12000\n"; # 1199bis
print FILE "2220 170000 830000 17500 12000\n"; # 1219bis
print FILE "2240 158000 830000 17500 12000\n"; # 1239bis
print FILE "2260 146000 830000 17500 12000\n"; # 1259bis

print FILE "350 182000 830000 35000 24000 \n"; # 249bis
print FILE "360 158000 830000 35000 24000 \n"; # 259bis
print FILE "370 134000 830000 35000 24000 \n"; # 269bis

close FILE;
}
