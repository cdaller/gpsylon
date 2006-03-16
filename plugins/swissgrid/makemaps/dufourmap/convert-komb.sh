#!/bin/sh

mkdir dufour

mv KOMB28.png KOMB31.png KOMB32.png KOMB33.png KOMB36.png KOMB37.png KOMB38.png dufour/
mv KOMB40.png KOMB41.png KOMB42.png KOMB43.png KOMB44.png KOMB48.png dufour/

echo "Make 39"
convert -page +0+0 KOMB39.png -page +7000+0 KOMB136.png -mosaic dufour/KOMB39.png
\rm -f KOMB39.png KOMB136.png

convert -size 7000x4800 xc:white leer.png

echo "Make 29"
composite -gravity SouthWest -compose Multiply KOMB29.png leer.png dufour/KOMB29.png
\rm -f KOMB29.png

echo "Make 26"
composite -gravity SouthWest -compose Multiply KOMB26.png leer.png dufour/KOMB26.png
\rm -f KOMB26.png

echo "Make 45"
composite -compose Multiply KOMB45.png leer.png dufour/KOMB45.png
\rm -f KOMB45.png

echo "Make 46"
composite -compose Multiply KOMB46.png leer.png dufour/KOMB46.png
\rm -f KOMB46.png

echo "Make 30"
composite -gravity SouthEast -compose Multiply KOMB30.png leer.png dufour/KOMB30.png
\rm -f KOMB30.png

echo "Make 47"
composite -compose Multiply KOMB215.png leer.png tmp.png
composite -gravity SouthEast -compose Multiply KOMB216.png tmp.png dufour/KOMB47.png
\rm -f KOMB216.png KOMB215.png tmp.png

echo "Make 35"
composite -gravity SouthWest -compose Multiply KOMB95.png leer.png tmp.png
composite -gravity SouthEast -compose Multiply KOMB96.png tmp.png dufour/KOMB35.png
\rm -f KOMB95.png KOMB96.png tmp.png

echo "Make 34"
composite -gravity SouthWest -compose Multiply KOMB85.png leer.png tmp.png
composite -gravity SouthEast -compose Multiply KOMB86.png tmp.png dufour/KOMB34.png
\rm -f KOMB85.png KOMB86.png tmp.png

echo "Make 27"
composite -gravity SouthWest -compose Multiply KOMB16.png leer.png tmp.png
composite -gravity NorthEast -compose Multiply KOMB15.png tmp.png dufour/KOMB27.png
\rm -f KOMB15.png KOMB16.png tmp.png leer.png
