# create the database for the location markers in hsqldb:
# cannot do it, because the jdbc url already holds the db:
# CREATE DATABASE IF NOT EXISTS gpsmap;

#USE gpsmap;

GRANT all on gpsmap.* to sa@localhost identified by '';

# markers table:
CREATE TABLE markers ( marker_id INTEGER NOT NULL AUTO_INCREMENT, 
                       name VARCHAR(255), 
                       latitude FLOAT NOT NULL, 
                       longitude FLOAT NOT NULL,
                       category_id VARCHAR(64) NULL,
                       level_of_detail INTEGER DEFAULT '1',
                       PRIMARY KEY (marker_id));
CREATE INDEX markers_name ON markers ( name );
CREATE INDEX markers_cat ON markers ( category_id );
CREATE INDEX markers_coordinates ON markers (latitude, longitude);

# if table should be a csv file (change table def as well (identity is
# not supported!) 
# SET TABLE markers SOURCE "hsql_markers.csv";





