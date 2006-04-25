# create the database for the location markers in derby database:

# nothing to do

# markers table:
CREATE CACHED TABLE markers ( marker_id INTEGER IDENTITY PRIMARY KEY, 
                       name VARCHAR_IGNORECASE(255), 
                       latitude FLOAT NOT NULL, 
                       longitude FLOAT NOT NULL,
                       category_id VARCHAR(64) NULL,
                       level_of_detail INTEGER DEFAULT '1');
CREATE INDEX markers_name ON markers ( name );
CREATE INDEX markers_cat ON markers ( category_id );
CREATE INDEX markers_coordinates ON markers (latitude, longitude);

# if table should be a csv file (change table def as well (identity is
# not supported!) 
# SET TABLE markers SOURCE "hsql_markers.csv";

# set maximum script file size to 3MB:
SET LOGSIZE 3;

# otherwise the data is lost, do not know why!
SET WRITE_DELAY 100 MILLIS

# create users:

CREATE USER gpsylon PASSWORD "" ADMIN;
GRANT ALL ON markers TO PUBLIC;




