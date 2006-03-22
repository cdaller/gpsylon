#!/usr/bin/perl
#
#  MLT -> PGM (Graustufenbild)
#

# Der einzige Parameter ist ev. einen Maximalwert (in dm!)
$globalmax = shift;

# Maximalwert der Graustufen
$range = 255;

# Header reinschluerfen: brauchen $we (Anzahl Punkte West -> Ost), und
# $max (hoechster Punkt) falls $globalmax nicht gesetzt ist.
while (<>) {
  last if (/ENDHEADER/);
  ($title, $direction, $we, $ns, $label1, $total, $label2) = split(/\s+/) if (/MATRIXDIMENSIONEN/);
  ($title, $dm, $min, $max, $rest) = split(/\s+/) if (/HOEHENBEREICH/);
}

# eben, einen Maximalwert aussuchen
$max = $globalmax if ($globalmax > 0);

print STDERR "MAX $max\n";

# und den Faktor berechnen mit dem alle Punkte multipliziert werden um auf 0..$range zu kommen.
$factor = $range * 1.0 / $max;

$j = 1;

# PGM Dateiformat: Magic Cookie, Dimensionen und hoechster Grauwert
print "P2\n";
print "#.\n";
print "$we $ns\n";
print "$range\n";

# Daten reinschluerfen
while (<>) {

  # Spaces (\s) am Anfang und am Ende der Zeile loeschen
  s/^\s+//;
  s/\s+$//;

  # Die Zeile in eine Liste von Punkten aufteilen
  @heights = split(/\s+/);

  # Iteration ueber diese Liste
  while ($height = shift @heights) {
    
    # Mit Faktor multiplizieren
    $normalized = sprintf("%d", $height * $factor);

    # und ausspucken
    print "$normalized ";

    # Falls wir am Anfang einer Reihe sind, eine neue Zeile anfangen
    print "\n" if ($i == 1);
    $i++;
    
    # Wir sind am Ende der Karte und muessen 25m nach Sueden und ganz
    # nach Westen...
    if ($i > $we) {
      $i = 1;
      $j++;
    }
  }
}
