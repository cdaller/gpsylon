#!/usr/bin/perl
#
#  MLT -> GNU Plot Array
#


while (<>) {
  last if (/ENDHEADER/);
  ($title, $ecke, $nichts, $nord, $west) = split(/\s+/) if (/NORD-WEST/);
  ($title, $ecke, $nichts, $sud, $ost) = split(/\s+/) if (/SUED-OST/);
}

$no=sprintf("%d",$nord);
$we=sprintf("%d",$west);
$su=sprintf("%d",$sud);
$os=sprintf("%d",$ost);

print STDERR "$no $we\n";
print STDERR "$su $os\n";

$i=0;
while (<>) {
  # Spaces (\s) am Anfang und am Ende der Zeile loeschen
  s/^\s+//;
  s/\s+$//;

  # Die Zeile in eine Liste von Punkten aufteilen
  @heights = split(/\s+/);

  # Iteration ueber diese Zeilen Liste
  while ($height = shift @heights) {
    @punkte[$i]=sprintf("%.1f",$height/10);
    $i++;
  }
}

$i=0;
# NS 481 Punkte
for($w=$we;$w>$os-25;$w=$w-25){
	# WE 701 Punkte
##	print "\n";
	for($n=$no;$n<$su+25;$n=$n+25){
	     # jeder x te Wert nehmen
	     #if( !($i % 10)){
		print "$n " . "$w " . $punkte[$i] . "\n";
	#	}
	    $i++;
	}
}

print STDERR "$i Punkte\n";
