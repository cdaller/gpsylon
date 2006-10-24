#!/bin/sh

#GPSYLON_VMARGS="-Xmx512m"

# Setup the JVM
if [ "x$JAVA" = "x" ]; then
  if [ "x$JAVA_HOME" != "x" ]; then
    JAVA="$JAVA_HOME/bin/java"
  else
  JAVA="java"
  fi
fi


# find directory where this file is located (needed to find gpsylon jar file)
PRG="$0"
# resolve links - $0 may be a softlink
#while [ -h "$PRG" ]; do
#  ls=`ls -ld "$PRG"`
#  link=`expr "$ls" : '.*-> \(.*\)$'`
#  if expr "$link" : '.*/.*' > /dev/null; then
#    PRG="$link"
#  else
#    PRG=`dirname "$PRG"`/"$link"
#  fi
#done

# Get relative directory for gpsylon.sh
PRGDIR=`dirname "$PRG"`

# Get the path to the native libraries
LIBPATH=$PRGDIR/lib/native/`uname -s`/`arch`

if [ -d "$LIBPATH" ]; then
 JAVA_LIB=-Djava.library.path=$LIBPATH
else
 JAVA_LIB=""
 echo "Warning: Your Plattform `uname -s`/`uname -m` is not supported by this script!"
 echo "Have a look at ftp://ftp.qbang.org/pub/rxtx/ToyBox to install the missing RXTX Serial Library!"
 echo ""
 exit 1
fi

#echo "PRGDIR is $PRGDIR"
$JAVA $JAVA_LIB $GPSYLON_VMARGS -cp $PRGDIR/gpsinput-0.5.3.jar:$PRGDIR/lib/java/RXTXcomm.jar org.dinopolis.gpstool.gpsinput.SerialPortList
