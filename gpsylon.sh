#!/bin/sh

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

#echo "PRGDIR is $PRGDIR"
$JAVA -jar $PRGDIR/gpsylon-0.5.2pre1.jar
