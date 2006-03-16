#!/bin/sh

###########################################################################
#
# Work in Progress .....
# This Script is not finished!
#
###########################################################################

../xtrctmap.pl /cdrom/LEGEND L100 && mv KOMB100.png Legend-100.png

../xtrctmap.pl /cdrom/DATA cn100  

##18x110
##19x137
##19x165
##19x165
##19x110

mkdir reihen

for (( I=1 ; $I<19 ; I++ ))
do 
echo  $I 
 head -n $(($I*110)) montagepatches | tail -n 110 > montage
 montage -geometry 256x256+0+0 -tile 110x1 @montage tmp.tif
 convert -crop 28000x256+160+0 tmp.tif reihen/r$I.tif
done

tail -n 15498 montagepatches > montagepatches2
for (( I=1 ; $I<20 ; I++ ))
do 
echo  $I 
 head -n $(($I*137)) montagepatches2 | tail -n 137 > montage
 montage -geometry 256x256+0+0 -tile 137x1 @montage tmp.tif
 convert -crop 35000x256+0+0 tmp.tif reihen/r$(($I+18)).tif # falsch
done

tail -n 12895 montagepatches2 > montagepatches3
for (( I=1 ; $I<39 ; I++ ))
do 
echo  $I 
 head -n $(($I*165)) montagepatches3 | tail -n 165 > montage
 montage -geometry 256x256+0+0 -tile 165x1 @montage tmp.tif
 convert -crop 42000x256+0+0 tmp.tif reihen/r$(($I+18+19)).tif # falsch 
done

tail -n 6625 montagepatches3 > montagepatches4
for (( I=1 ; $I<20 ; I++ ))
do 
echo  $I 
 head -n $(($I*110)) montagepatches4 | tail -n 110 > montage
 montage -geometry 256x256+0+0 -tile 110x1 @montage tmp.tif
 convert -crop 28000x256+0+0 tmp.tif reihen/r$(($I+18+19+19+19)).tif # falsch
done

find . -name "sm50*.tif" -exec \rm -rf {} \;
\rm -f tmp.tif montage* patchorder.txt
