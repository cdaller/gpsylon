#!/bin/sh

karten="1011 1012 1031 1032 1033 1034 1035 1047 1048 1049 1050 1051 1052 1053 1054 1055 1056 1064 1065 1066 1067 1068 1069 1070 1071 1072 1073 1074 1075 1076 1084 1085 1086 1087 1088 1089 1090 1091 1092 1093 1094 1095 1096 1104 1105 1106 1107 1108 1109 1110 1111 1112 1113 1114 1115 1116 1123 1124 1125 1126 1127 1128 1129 1130 1131 1132 1133 1134 1135 1136 1143 1144 1145 1146 1147 1148 1149 1150 1151 1152 1153 1154 1155 1156 1157 1159 1162 1163 1164 1165 1166 1167 1168 1169 1170 1171 1172 1173 1174 1175 1176 1177 1178 1179 1182 1183 1184 1185 1186 1187 1188 1189 1190 1191 1192 1193 1194 1195 1196 1197 1198 1199 1201 1202 1203 1204 1205 1206 1207 1208 1209 1210 1211 1212 1213 1214 1215 1216 1217 1218 1219 1221 1222 1223 1224 1225 1226 1227 1228 1229 1230 1231 1232 1233 1234 1235 1236 1237 1238 1239 1240 1241 1242 1243 1244 1245 1246 1247 1248 1249 1250 1251 1252 1253 1254 1255 1256 1257 1258 1260 1261 1262 1263 1264 1265 1266 1267 1268 1269 1270 1271 1272 1273 1274 1275 1276 1277 1278 1280 1281 1283 1284 1285 1286 1287 1288 1289 1290 1291 1292 1293 1294 1296 1298 1300 1301 1304 1305 1306 1307 1308 1309 1310 1311 1312 1313 1314 1324 1325 1326 1327 1328 1329 1332 1333 1334 1344 1345 1346 1347 1348 1349 1352 1353 1365 1366 1373 1374 2180 2200 2220 2240"

karten="1091"

for f in $karten
do
echo
echo $f
echo
zcat $SWISSMAP/data/schweiz/dhm25/MM$f.MLT.gz | ./mlt2xyz.pl > $f.xyz
done