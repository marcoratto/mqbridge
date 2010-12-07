#!/bin/sh
#set -x
# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`
LIBDIR=$PRGDIR/lib
RESDIR=$PRGDIR/res

CPATH=$PRGDIR/bin/MQBridge.jar
CPATH=$RESDIR:$CPATH
for FILE_JAR in `find $LIBDIR -iname "*.jar" -type f`
do
     CPATH=$FILE_JAR:$CPATH
done

$JAVA_HOME/bin/java -classpath $CPATH uk.co.marcoratto.mqbridge.QueueToFile "$@"
RET_CODE=$?
exit $RET_CODE
