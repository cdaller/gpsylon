GPSylon / GPSTool
=================

GPSylon is a moving map application that may use data from a gps device
to show the current location. It uses the gpstool framework that
provides some classes for geographical and cartographical programming.

To start the application, you need at least java1.4 or java1.5, 
for gps support you need serial support (the comm api from rxtx.org).

Usually, you can start GPSylon by calling

java -jar gpsylon-<version>.jar

The command line tool is called GPSTool and demonstrates how to use
the gpd device communication classes. It is able to retrieve the current
position and upload/download routes/tracks/waypoints from/to garmin
gps devices.

java -jar gpstool-<version>.jar --help

gives some information, how to start the command line tool.

Further details are in the html-documentation in the "doc" directory
where you can also find a software design documentation for your own
extensions to the code.

The latest version may be downloaded at http://gpsmap.sourceforge.net

If you have questions, please contact the author 
Christof Dallermassl (christof@dallermassl.at)

GPSylon uses some features of the open-source (GPL) openmap library.
