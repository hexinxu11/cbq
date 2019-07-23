#!/bin/sh

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

#set JAVA_OPTS
#JAVA_OPTS="-Xss256k"
#==============================================================================

#stop Server
INS=`jps |grep J8crawlerManager|awk -F ' ' '{print $1}'|wc -l `
echo $INS
if [[ $INS -gt 1  ]];then
   if [[ -z $1 ]];then
      echo "Error: Multiple J8crawler instances, please give a pid."
      echo "Usage: shutdown.sh pid"
      exit 1
   else
      `kill -9 $1`
   fi
else
 $JAVA_HOME/bin/jps |grep J8crawlerManager|awk -F ' ' '{print $1}'|while read line
 do
   eval "kill -9 $line"
 done
fi
#==============================================================================

#set HOME
CURR_DIR=`pwd`
cd `dirname "$0"`/..
J8CRAWLER_HOME=`pwd`
cd $CURR_DIR
if [ -z "$J8CRAWLER_HOME" ] ; then
    echo
    echo "Error: J8CRAWLER_HOME environment variable is not defined correctly."
    echo
    exit 1
fi
#==============================================================================

#set CLASSPATH
J8CRAWLER_CLASSPATH="$J8CRAWLER_HOME/conf:$J8CRAWLER_HOME/lib/classes"
for i in "$J8CRAWLER_HOME"/lib/*.jar
do
    J8CRAWLER_CLASSPATH="$J8CRAWLER_CLASSPATH:$i"
done
#==============================================================================

#shutdown Server
RUN_CMD="\"$J8CRAWLER_HOME/bin/java\""
RUN_CMD="$RUN_CMD -Dcobar.home=\"$J8CRAWLER_HOME\""
RUN_CMD="$RUN_CMD -classpath \"$J8CRAWLER_CLASSPATH\""
RUN_CMD="$RUN_CMD $JAVA_OPTS"
RUN_CMD="$RUN_CMD com.j8crawler.core.J8crawlerManager $@"
RUN_CMD="$RUN_CMD >> \"$J8CRAWLER_HOME/logs/console.log\" 2>&1 &"
echo $RUN_CMD
eval $RUN_CMD
#==============================================================================
