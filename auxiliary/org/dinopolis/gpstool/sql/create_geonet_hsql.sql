# create the database for the geonet data in hsqldb:

# markers table:
CREATE CACHED TABLE geonet_data ( 
  geonet_id INTEGER IDENTITY PRIMARY KEY, 
  RC TINYINT,
  UFI VARCHAR(15),
  UNI VARCHAR(15),
  DD_LAT DOUBLE,
  DD_LONG DOUBLE,
  DMS_LAT VARCHAR(8),
  DMS_LONG VARCHAR(8),
  UTM VARCHAR(10),
  JOG VARCHAR(20),
  FC CHAR(1),
  DSG VARCHAR(6),
  PC INTEGER,
  CC1 CHAR(2),
  ADM1 CHAR(2),
  ADM2 CHAR(2),
  DIM VARCHAR(20),
  CC2 CHAR(2),
  NT CHAR(1),
  LC CHAR(2),
  SHORT_FORM VARCHAR(60),
  GENERIC VARCHAR(60),
  SORT_NAME VARCHAR(60),
  FULL_NAME VARCHAR(60),
  FULL_NAME_ND VARCHAR(60),
  MODIFY_DATE DATE);

CREATE INDEX geonet_full_name ON geonet_data (FULL_NAME);

# if table should be a csv file (change table def as well (identity is
# not supported!) 
# SET TABLE markers SOURCE "hsql_markers.csv";

CHECKPOINT;





