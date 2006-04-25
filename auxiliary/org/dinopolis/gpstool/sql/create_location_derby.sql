# create the database for the location markers in derby database:

# nothing to do for derby

# create users:

# nothing to do

# markers table:
CREATE TABLE markers ( marker_id INT GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), 
                       name VARCHAR (255), 
                       latitude FLOAT NOT NULL, 
                       longitude FLOAT NOT NULL,
                       category_id VARCHAR(64),
                       level_of_detail INT DEFAULT 1);
CREATE INDEX markers_name ON markers ( name );
CREATE INDEX markers_cat ON markers ( category_id );
CREATE INDEX markers_coordinates ON markers (latitude, longitude);



