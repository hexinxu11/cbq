#!/bin/sh
##
##

#set JAVA_HOME
JAVA_HOME=$JAVA_HOME

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
#JAVA_OPTS="-server -Xms1024m -Xmx1024m -Xmn256m -Xss256k"    #在机器有足够可用的内存时打开  测试机只有1G内存 不需要打开
#performance Options

#JAVA_OPTS="$JAVA_OPTS -XX:+AggressiveOpts"  #该参数不需要开启,该参数开启之后会影响到TreeSet的子类
JAVA_OPTS="$JAVA_OPTS -XX:+UseBiasedLocking"
JAVA_OPTS="$JAVA_OPTS -XX:+UseFastAccessorMethods"
JAVA_OPTS="$JAVA_OPTS -XX:+DisableExplicitGC"
JAVA_OPTS="$JAVA_OPTS -XX:+UseParNewGC"
JAVA_OPTS="$JAVA_OPTS -XX:+UseConcMarkSweepGC"
JAVA_OPTS="$JAVA_OPTS -XX:+CMSParallelRemarkEnabled"
JAVA_OPTS="$JAVA_OPTS -XX:+UseCMSCompactAtFullCollection"
JAVA_OPTS="$JAVA_OPTS -XX:+UseCMSInitiatingOccupancyOnly"
JAVA_OPTS="$JAVA_OPTS -XX:CMSInitiatingOccupancyFraction=75"
#GC Log Options
#JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCApplicationStoppedTime"
#JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCTimeStamps"
#JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDetails"
#debug Options
#JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,address=8065,server=y,suspend=n"
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

#startup Server
RUN_CMD="\"$JAVA_HOME/bin/java\""
RUN_CMD="$RUN_CMD -Dcobar.home=\"$J8CRAWLER_HOME\""
RUN_CMD="$RUN_CMD -classpath \"$J8CRAWLER_CLASSPATH\""
RUN_CMD="$RUN_CMD $JAVA_OPTS"
RUN_CMD="$RUN_CMD com.j8crawler.core.J8crawlerManager $@"
RUN_CMD="$RUN_CMD >> \"$J8CRAWLER_HOME/logs/console.log\" 2>&1 &"
echo $RUN_CMD
eval $RUN_CMD
#==============================================================================
