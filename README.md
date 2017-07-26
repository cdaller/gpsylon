# GPSylon / GPSTool

## Beware

GPSylon is a very old project of mine. I transfered the source repository from sourceforge.

## GPSylon

GPSylon is a moving map application that may use data from a gps device
to show the current location. It uses the gpstool framework that
provides some classes for geographical and cartographical programming.

To start the application, you need at least java1.4 or java1.5, 
for gps support you need serial support (the comm api from rxtx.org).

Usually, you can start GPSylon by calling

```
java -jar gpsylon-<version>.jar
```

As the map application uses map tile servers that probably do not exist anymore it probable will not work anymore. Sorry for that - but no spare time at the moment to get it running again.

## GPSTool

The command line tool is called GPSTool and demonstrates how to use
the gps device communication classes. It is able to retrieve the current
position and upload/download routes/tracks/waypoints from/to garmin
gps devices.

```
java -jar gpstool-<version>.jar --help
```

gives some information, how to start the command line tool.

## Documentation

Further details are in the html-documentation in the "doc" directory
where you can also find a software design documentation for your own
extensions to the code.

The latest version may be downloaded at http://gpsmap.sourceforge.net

If you have questions, please contact the author 
Christof Dallermassl (christof@dallermassl.at)

GPSylon uses some features of the open-source (GPL) openmap library.

## Build

```
ant - gives a list of all ant commands (see there)
ant compile - compiles the project
```

## License
This project is licensed under [Apache 2.0](http://opensource.org/licenses/apache2.0)