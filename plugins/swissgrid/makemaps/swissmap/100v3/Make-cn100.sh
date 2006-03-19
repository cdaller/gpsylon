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

echo "Reihen r1 - r18 28000x256"
for (( I=1 ; $I<19 ; I++ ))
do 
echo  $I 
 head -n $(($I*110)) montagepatches | tail -n 110 > montage
 montage -geometry 256x256+0+0 -tile 110x1 @montage tmp.tif
 convert -crop 28000x256+88+0 tmp.tif reihen/r$I.tif
done

VAR=`wc -l montagepatches | cut -d" " -f1`;
tail -n $(($VAR-(18*110))) montagepatches > montagepatches2

echo "Reihen r19 - r37 35000x256"
for (( I=1 ; $I<20 ; I++ ))
do 
echo  $I 
 head -n $(($I*137)) montagepatches2 | tail -n 137 > montage
 montage -geometry 256x256+0+0 -tile 137x1 @montage tmp.tif
 convert -crop 35000x256+0+0 tmp.tif reihen/r$(($I+18)).tif 
done

VAR=`wc -l montagepatches2 | cut -d" " -f1`;
tail -n $(($VAR-(19*137))) montagepatches2 > montagepatches3

echo "Reihen r38 - r75 42000x256"
for (( I=1 ; $I<39 ; I++ ))
do 
echo  $I 
 head -n $(($I*165)) montagepatches3 | tail -n 165 > montage
 montage -geometry 256x256+0+0 -tile 165x1 @montage tmp.tif
 convert -crop 42000x256+0+0 tmp.tif reihen/r$(($I+18+19)).tif  
done

VAR=`wc -l montagepatches3 | cut -d" " -f1`;
tail -n $(($VAR-(38*165))) montagepatches3 > montagepatches4

echo "Reihen r76 - r94 28000x256"
for (( I=1 ; $I<20 ; I++ ))
do 
echo  $I 
 head -n $(($I*110)) montagepatches4 | tail -n 110 > montage
 montage -geometry 256x256+0+0 -tile 110x1 @montage tmp.tif
 convert -crop 28000x256+0+0 tmp.tif reihen/r$(($I+18+19+19+19)).tif 
done

find . -name "sm50*.tif" -exec \rm -rf {} \;
\rm -f tmp.tif montage* patchorder.txt
