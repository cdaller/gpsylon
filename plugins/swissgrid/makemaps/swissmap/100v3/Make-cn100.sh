#!/bin/sh

../xtrctmap.pl /cdrom/LEGEND L100 && mv KOMB100.png Legend-100.png

../xtrctmap.pl /cdrom/DATA cn100  

##18x110
##19x137
##19x165
##19x165
##19x110

mkdir reihen
mkdir spalten

echo "Reihen r1 - r18 28000x256"
for (( I=1 ; $I<19 ; I++ ))
do 
echo  $I 
 head -n $(($I*110)) montagepatches | tail -n 110 > montage
 montage -geometry 256x256+0+0 -tile 110x1 @montage tmp.tif
 convert -crop 28000x256+88+0 tmp.tif reihen/r$I.tif
 for (( Y=2 ; $Y<6 ; Y++ ))
 do
  convert -crop 7000x256+$((($Y-2)*7000))+0 reihen/r$I.tif spalten/r$I-$Y.tif
 done
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
 for (( Y=1 ; $Y<6 ; Y++ ))
 do
  convert -crop 7000x256+$((($Y-1)*7000))+0 reihen/r$(($I+18)).tif spalten/r$(($I+18))-$Y.tif
 done
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
 for (( Y=1 ; $Y<7 ; Y++ ))
 do
  convert -crop 7000x256+$((($Y-1)*7000))+0 reihen/r$(($I+18+19)).tif spalten/r$(($I+18+19))-$Y.tif
 done
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
 for (( Y=1 ; $Y<5 ; Y++ ))
 do
  convert -crop 7000x256+$((($Y-1)*7000))+0 reihen/r$(($I+18+19+19+19)).tif spalten/r$(($I+18+19+19+19))-$Y.tif
 done
done

find . -name "sm50*.tif" -exec \rm -rf {} \;
\rm -f tmp.tif montage* patchorder.txt
\rm -rf reihen

cd spalten

echo "KOMB26"
montage -geometry 7000x256+0+0 -tile 1x19 r1-2.tif r2-2.tif r3-2.tif r4-2.tif r5-2.tif r6-2.tif r7-2.tif r8-2.tif r9-2.tif r10-2.tif r11-2.tif r12-2.tif r13-2.tif r14-2.tif r15-2.tif r16-2.tif r17-2.tif r18-2.tif r19-2.tif ../tmp.tif
convert -crop 7000x4800+0+0 ../tmp.tif ../KOMB26.png

echo "KOMB27"
montage -geometry 7000x256+0+0 -tile 1x19 r1-3.tif r2-3.tif r3-3.tif r4-3.tif r5-3.tif r6-3.tif r7-3.tif r8-3.tif r9-3.tif r10-3.tif r11-3.tif r12-3.tif r13-3.tif r14-3.tif r15-3.tif r16-3.tif r17-3.tif r18-3.tif r19-3.tif ../tmp.tif
convert -crop 7000x4800+0+0 ../tmp.tif ../KOMB27.png

echo "KOMB28"
montage -geometry 7000x256+0+0 -tile 1x19 r1-4.tif r2-4.tif r3-4.tif r4-4.tif r5-4.tif r6-4.tif r7-4.tif r8-4.tif r9-4.tif r10-4.tif r11-4.tif r12-4.tif r13-4.tif r14-4.tif r15-4.tif r16-4.tif r17-4.tif r18-4.tif r19-4.tif ../tmp.tif
convert -crop 7000x4800+0+0 ../tmp.tif ../KOMB28.png

echo "KOMB28bis"
montage -geometry 7000x256+0+0 -tile 1x19 r1-5.tif r2-5.tif r3-5.tif r4-5.tif r5-5.tif r6-5.tif r7-5.tif r8-5.tif r9-5.tif r10-5.tif r11-5.tif r12-5.tif r13-5.tif r14-5.tif r15-5.tif r16-5.tif r17-5.tif r18-5.tif r19-5.tif ../tmp.tif
convert -crop 7000x4800+0+0 ../tmp.tif ../KOMB28bis.png

echo "KOMB30"
montage -geometry 7000x256+0+0 -tile 1x20 r19-1.tif r20-1.tif r21-1.tif r22-1.tif r23-1.tif r24-1.tif r25-1.tif r26-1.tif r27-1.tif r28-1.tif r29-1.tif r30-1.tif r31-1.tif r32-1.tif r33-1.tif r34-1.tif r35-1.tif r36-1.tif r37-1.tif r38-1.tif ../tmp.tif
convert -crop 7000x4800+0+192 ../tmp.tif ../KOMB30.png

echo "KOMB31"
montage -geometry 7000x256+0+0 -tile 1x20 r19-2.tif r20-2.tif r21-2.tif r22-2.tif r23-2.tif r24-2.tif r25-2.tif r26-2.tif r27-2.tif r28-2.tif r29-2.tif r30-2.tif r31-2.tif r32-2.tif r33-2.tif r34-2.tif r35-2.tif r36-2.tif r37-2.tif r38-2.tif ../tmp.tif
convert -crop 7000x4800+0+192 ../tmp.tif ../KOMB31.png

echo "KOMB32"
montage -geometry 7000x256+0+0 -tile 1x20 r19-3.tif r20-3.tif r21-3.tif r22-3.tif r23-3.tif r24-3.tif r25-3.tif r26-3.tif r27-3.tif r28-3.tif r29-3.tif r30-3.tif r31-3.tif r32-3.tif r33-3.tif r34-3.tif r35-3.tif r36-3.tif r37-3.tif r38-3.tif ../tmp.tif
convert -crop 7000x4800+0+192 ../tmp.tif ../KOMB32.png

echo "KOMB33"
montage -geometry 7000x256+0+0 -tile 1x20 r19-4.tif r20-4.tif r21-4.tif r22-4.tif r23-4.tif r24-4.tif r25-4.tif r26-4.tif r27-4.tif r28-4.tif r29-4.tif r30-4.tif r31-4.tif r32-4.tif r33-4.tif r34-4.tif r35-4.tif r36-4.tif r37-4.tif r38-4.tif ../tmp.tif
convert -crop 7000x4800+0+192 ../tmp.tif ../KOMB33.png

echo "KOMB34"
montage -geometry 7000x256+0+0 -tile 1x20 r19-5.tif r20-5.tif r21-5.tif r22-5.tif r23-5.tif r24-5.tif r25-5.tif r26-5.tif r27-5.tif r28-5.tif r29-5.tif r30-5.tif r31-5.tif r32-5.tif r33-5.tif r34-5.tif r35-5.tif r36-5.tif r37-5.tif r38-5.tif ../tmp.tif
convert -crop 7000x4800+0+192 ../tmp.tif ../KOMB34.png

echo "KOMB35"
montage -geometry 7000x256+0+0 -tile 1x20 r38-1.tif r39-1.tif r40-1.tif r41-1.tif r42-1.tif r43-1.tif r44-1.tif r45-1.tif r46-1.tif r47-1.tif r48-1.tif r49-1.tif r50-1.tif r51-1.tif r52-1.tif r53-1.tif r54-1.tif r55-1.tif r56-1.tif r57-1.tif ../tmp.tif
convert -crop 7000x4800+0+128 ../tmp.tif ../KOMB35.png

echo "KOMB36"
montage -geometry 7000x256+0+0 -tile 1x20 r38-2.tif r39-2.tif r40-2.tif r41-2.tif r42-2.tif r43-2.tif r44-2.tif r45-2.tif r46-2.tif r47-2.tif r48-2.tif r49-2.tif r50-2.tif r51-2.tif r52-2.tif r53-2.tif r54-2.tif r55-2.tif r56-2.tif r57-2.tif ../tmp.tif
convert -crop 7000x4800+0+128 ../tmp.tif ../KOMB36.png

echo "KOMB37"
montage -geometry 7000x256+0+0 -tile 1x20 r38-3.tif r39-3.tif r40-3.tif r41-3.tif r42-3.tif r43-3.tif r44-3.tif r45-3.tif r46-3.tif r47-3.tif r48-3.tif r49-3.tif r50-3.tif r51-3.tif r52-3.tif r53-3.tif r54-3.tif r55-3.tif r56-3.tif r57-3.tif ../tmp.tif
convert -crop 7000x4800+0+128 ../tmp.tif ../KOMB37.png

echo "KOMB38"
montage -geometry 7000x256+0+0 -tile 1x20 r38-4.tif r39-4.tif r40-4.tif r41-4.tif r42-4.tif r43-4.tif r44-4.tif r45-4.tif r46-4.tif r47-4.tif r48-4.tif r49-4.tif r50-4.tif r51-4.tif r52-4.tif r53-4.tif r54-4.tif r55-4.tif r56-4.tif r57-4.tif ../tmp.tif
convert -crop 7000x4800+0+128 ../tmp.tif ../KOMB38.png

echo "KOMB39"
montage -geometry 7000x256+0+0 -tile 1x20 r38-5.tif r39-5.tif r40-5.tif r41-5.tif r42-5.tif r43-5.tif r44-5.tif r45-5.tif r46-5.tif r47-5.tif r48-5.tif r49-5.tif r50-5.tif r51-5.tif r52-5.tif r53-5.tif r54-5.tif r55-5.tif r56-5.tif r57-5.tif ../tmp.tif
convert -crop 7000x4800+0+128 ../tmp.tif ../KOMB39.png

echo "KOMB39bis"
montage -geometry 7000x256+0+0 -tile 1x20 r38-6.tif r39-6.tif r40-6.tif r41-6.tif r42-6.tif r43-6.tif r44-6.tif r45-6.tif r46-6.tif r47-6.tif r48-6.tif r49-6.tif r50-6.tif r51-6.tif r52-6.tif r53-6.tif r54-6.tif r55-6.tif r56-6.tif r57-6.tif ../tmp.tif
convert -crop 7000x4800+0+128 ../tmp.tif ../KOMB39bis.png

echo "KOMB40"
montage -geometry 7000x256+0+0 -tile 1x19 r57-1.tif r58-1.tif r59-1.tif r60-1.tif r61-1.tif r62-1.tif r63-1.tif r64-1.tif r65-1.tif r66-1.tif r67-1.tif r68-1.tif r69-1.tif r70-1.tif r71-1.tif r72-1.tif r73-1.tif r74-1.tif r75-1.tif ../tmp.tif
convert -crop 7000x4800+0+64 ../tmp.tif ../KOMB40.png

echo "KOMB41"
montage -geometry 7000x256+0+0 -tile 1x19 r57-2.tif r58-2.tif r59-2.tif r60-2.tif r61-2.tif r62-2.tif r63-2.tif r64-2.tif r65-2.tif r66-2.tif r67-2.tif r68-2.tif r69-2.tif r70-2.tif r71-2.tif r72-2.tif r73-2.tif r74-2.tif r75-2.tif ../tmp.tif
convert -crop 7000x4800+0+64 ../tmp.tif ../KOMB41.png

echo "KOMB42"
montage -geometry 7000x256+0+0 -tile 1x19 r57-3.tif r58-3.tif r59-3.tif r60-3.tif r61-3.tif r62-3.tif r63-3.tif r64-3.tif r65-3.tif r66-3.tif r67-3.tif r68-3.tif r69-3.tif r70-3.tif r71-3.tif r72-3.tif r73-3.tif r74-3.tif r75-3.tif ../tmp.tif
convert -crop 7000x4800+0+64 ../tmp.tif ../KOMB42.png

echo "KOMB43"
montage -geometry 7000x256+0+0 -tile 1x19 r57-4.tif r58-4.tif r59-4.tif r60-4.tif r61-4.tif r62-4.tif r63-4.tif r64-4.tif r65-4.tif r66-4.tif r67-4.tif r68-4.tif r69-4.tif r70-4.tif r71-4.tif r72-4.tif r73-4.tif r74-4.tif r75-4.tif ../tmp.tif
convert -crop 7000x4800+0+64 ../tmp.tif ../KOMB43.png

echo "KOMB44"
montage -geometry 7000x256+0+0 -tile 1x19 r57-5.tif r58-5.tif r59-5.tif r60-5.tif r61-5.tif r62-5.tif r63-5.tif r64-5.tif r65-5.tif r66-5.tif r67-5.tif r68-5.tif r69-5.tif r70-5.tif r71-5.tif r72-5.tif r73-5.tif r74-5.tif r75-5.tif ../tmp.tif
convert -crop 7000x4800+0+64 ../tmp.tif ../KOMB44.png

echo "KOMB44bis"
montage -geometry 7000x256+0+0 -tile 1x19 r57-6.tif r58-6.tif r59-6.tif r60-6.tif r61-6.tif r62-6.tif r63-6.tif r64-6.tif r65-6.tif r66-6.tif r67-6.tif r68-6.tif r69-6.tif r70-6.tif r71-6.tif r72-6.tif r73-6.tif r74-6.tif r75-6.tif ../tmp.tif
convert -crop 7000x4800+0+64 ../tmp.tif ../KOMB44bis.png

echo "KOMB45"
montage -geometry 7000x256+0+0 -tile 1x19 r76-1.tif r77-1.tif r78-1.tif r79-1.tif r80-1.tif r81-1.tif r82-1.tif r83-1.tif r84-1.tif r85-1.tif r86-1.tif r87-1.tif r88-1.tif r89-1.tif r90-1.tif r91-1.tif r92-1.tif r93-1.tif r94-1.tif ../tmp.tif
convert -crop 7000x4800+0+0 ../tmp.tif ../KOMB45.png

echo "KOMB46"
montage -geometry 7000x256+0+0 -tile 1x19 r76-2.tif r77-2.tif r78-2.tif r79-2.tif r80-2.tif r81-2.tif r82-2.tif r83-2.tif r84-2.tif r85-2.tif r86-2.tif r87-2.tif r88-2.tif r89-2.tif r90-2.tif r91-2.tif r92-2.tif r93-2.tif r94-2.tif ../tmp.tif
convert -crop 7000x4800+0+0 ../tmp.tif ../KOMB46.png

echo "KOMB47"
montage -geometry 7000x256+0+0 -tile 1x19 r76-3.tif r77-3.tif r78-3.tif r79-3.tif r80-3.tif r81-3.tif r82-3.tif r83-3.tif r84-3.tif r85-3.tif r86-3.tif r87-3.tif r88-3.tif r89-3.tif r90-3.tif r91-3.tif r92-3.tif r93-3.tif r94-3.tif ../tmp.tif
convert -crop 7000x4800+0+0 ../tmp.tif ../KOMB47.png

echo "KOMB48"
montage -geometry 7000x256+0+0 -tile 1x19 r76-4.tif r77-4.tif r78-4.tif r79-4.tif r80-4.tif r81-4.tif r82-4.tif r83-4.tif r84-4.tif r85-4.tif r86-4.tif r87-4.tif r88-4.tif r89-4.tif r90-4.tif r91-4.tif r92-4.tif r93-4.tif r94-4.tif ../tmp.tif
convert -crop 7000x4800+0+0 ../tmp.tif ../KOMB48.png

\rm -rf tmp.tif spalten
