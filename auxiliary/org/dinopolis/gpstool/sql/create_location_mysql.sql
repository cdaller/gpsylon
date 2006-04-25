# create the database for the location markers in mysql:
CREATE DATABASE IF NOT EXISTS gpsylon;

USE gpsylon;

# create db user:
GRANT all on gpsylon.* to gpsylon@localhost identified by '';

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





