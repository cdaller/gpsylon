#!/bin/sh

echo "xtrctmap.pl /cdrom/Data cn200"
../xtrctmap.pl /cdrom/Data cn200

echo "make200-patch.pl"
./make200-patch.pl

## bild 1
head -n 540 montagepatches1 > montagepatches
echo "montage -geometry 360x360+0+0 -tile 27x20 @montagepatches tmp.tif"
montage -geometry 360x360+0+0 -tile 27x20 @montagepatches tmp.tif
echo "convert -crop 9600x6895+0+0 tmp.tif KOMB1.png"
convert -crop 9600x6895+0+0 tmp.tif KOMB1.png

## bild 3
head -n 1053 montagepatches1 | tail -n 540 > montagepatches
echo "montage -geometry 360x360+0+0 -tile 27x20 @montagepatches tmp.tif"
montage -geometry 360x360+0+0 -tile 27x20 @montagepatches tmp.tif
echo "convert -crop 9600x6895+0+55 tmp.tif KOMB3.png"
convert -crop 9600x6895+0+55 tmp.tif KOMB3.png

## bild 2
head -n 560 montagepatches2 > montagepatches
echo "montage -geometry 360x360+0+0 -tile 28x20 @montagepatches tmp.tif"
montage -geometry 360x360+0+0 -tile 28x20 @montagepatches tmp.tif
echo "convert -crop 9600x6895+240+0 tmp.tif KOMB2.png"
convert -crop 9600x6895+240+0 tmp.tif KOMB2.png

## bild 4
head -n 1092 montagepatches2 | tail -n 560 > montagepatches
echo "montage -geometry 360x360+0+0 -tile 28x20 @montagepatches tmp.tif"
montage -geometry 360x360+0+0 -tile 28x20 @montagepatches tmp.tif
echo "convert -crop 9600x6895+240+55 tmp.tif KOMB4.png"
convert -crop 9600x6895+240+55 tmp.tif KOMB4.png

\rm -f patchorder.txt montagepatches*
\rm -f *.tif

../xtrctmap.pl /cdrom/Legend L200 && mv KOMB200.png Legend-200.png 
