# script to create the necessary database tables/indices in postgresql:
#
# thanks to Thomas Mueller for postgresql support
#
#   create the database for the location markers in postgres:
#   cannot do it, because the jdbc url already holds the db:
# 
#   create the user 'gpsmap':
#   createuser --no-adduser --no-createdb --pwprompt gpsmap;
#   you will have to enter the password
#   
#   create the database gpsmap:
#   createdb gpsmap
#   
#   Normally this has to be done as a user the database knowns.
#   Debian has the user postgres you have to do:
#   su postgres -c "SQL ORDER"
# 
#   Ensure the machine running gpsmap is allowed to access the
#   database using TCP/IP.

# create markers table:
CREATE TABLE markers ( marker_id SERIAL NOT NULL UNIQUE PRIMARY KEY,
                       name VARCHAR(255), 
                       latitude FLOAT NOT NULL, 
                       longitude FLOAT NOT NULL,
                       category_id VARCHAR(64) NULL,
                       level_of_detail INTEGER DEFAULT '1'
);

# create indices:
CREATE INDEX markers_name ON markers ( name );
CREATE INDEX markers_cat ON markers ( category_id );
CREATE INDEX markers_coordinates ON markers (latitude, longitude);
