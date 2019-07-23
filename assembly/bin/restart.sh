#!/bin/sh
##
##

#set JAVA_HOME

#check JAVA_HOME & java
noJavaHome=false
if [ -z "$JAVA_HOME" ] ; then
    noJavaHome=true
fi
if [ ! -e "$JAVA_HOME/bin/java" ] ; then
    noJavaHome=true
fi
if $noJavaHome ; then
    echo
    echo "Error: JAVA_HOME environment variable is not set."
    echo
    exit 1
fi
#==============================================================================

#stop Server
$JAVA_HOME/bin/jps |grep CobarStartup|awk -F ' ' '{print $1}'|while read line
do
  eval "kill -9 $line"
done
#==============================================================================

#sleep sometime
sleep 1

#set COBAR_HOME
CURR_DIR=`pwd`
cd `dirname "$0"`/..
COBAR_HOME=`pwd`
COBAR_HOME=/root/apps/cobar/
cd $CURR_DIR
if [ -z "$COBAR_HOME" ] ; then
    echo
    echo "Error: COBAR_HOME environment variable is not defined correctly."
    echo
    exit 1
fi
#==============================================================================

#startup Server
. $COBAR_HOME/bin/startup.sh
#==============================================================================