@echo off
REM Copyright 1999-2012 Alibaba Group.                                      
REM                                                                         
REM Licensed under the Apache License, Version 2.0 (the "License");         
REM you may not use this file except in compliance with the License.        
REM You may obtain a copy of the License at                                 
REM                                                                         
REM      http://www.apache.org/licenses/LICENSE-2.0                         
REM                                                                         
REM Unless required by applicable law or agreed to in writing, software     
REM distributed under the License is distributed on an "AS IS" BASIS,       
REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM See the License for the specific language governing permissions and     
REM limitations under the License.

REM check JAVA_HOME & java
if "%JAVA_HOME%" == "" goto noJavaHome
if exist "%JAVA_HOME%\bin\java.exe" goto mainEntry
:noJavaHome
echo ---------------------------------------------------
echo Error: JAVA_HOME environment variable is not set. 
echo ---------------------------------------------------
goto end

:mainEntry
REM set JAVA_OPTS
set "JAVA_OPTS=-server -Xms1024m -Xmx1024m -Xmn256m -Xss256k"
REM performance Options
REM set "JAVA_OPTS=%JAVA_OPTS% -XX:+AggressiveOpts"  #�ò�������Ҫ����,�ò�������֮���Ӱ�쵽TreeSet������,�ᵼ��order by ��ѯ��ʱ����ֿ���״��
set "JAVA_OPTS=%JAVA_OPTS% -XX:+UseBiasedLocking"
set "JAVA_OPTS=%JAVA_OPTS% -XX:+UseFastAccessorMethods"
set "JAVA_OPTS=%JAVA_OPTS% -XX:+DisableExplicitGC"
set "JAVA_OPTS=%JAVA_OPTS% -XX:+UseParNewGC"
set "JAVA_OPTS=%JAVA_OPTS% -XX:+UseConcMarkSweepGC"
set "JAVA_OPTS=%JAVA_OPTS% -XX:+CMSParallelRemarkEnabled"
set "JAVA_OPTS=%JAVA_OPTS% -XX:+UseCMSCompactAtFullCollection"
set "JAVA_OPTS=%JAVA_OPTS% -XX:+UseCMSInitiatingOccupancyOnly"
set "JAVA_OPTS=%JAVA_OPTS% -XX:CMSInitiatingOccupancyFraction=75"
REM GC Log Options
REM set "JAVA_OPTS=%JAVA_OPTS% -XX:+PrintGCApplicationStoppedTime"
REM set "JAVA_OPTS=%JAVA_OPTS% -XX:+PrintGCTimeStamps"
REM set "JAVA_OPTS=%JAVA_OPTS% -XX:+PrintGCDetails"
REM debug Options
REM set "JAVA_OPTS=%JAVA_OPTS% -Xdebug -Xrunjdwp:transport=dt_socket,address=8065,server=y,suspend=n"

REM set HOME_DIR
set "CURR_DIR=%cd%"
cd ..
set "COBAR_HOME=%cd%"
cd %CURR_DIR%
if exist "%COBAR_HOME%\bin\startup.bat" goto okHome
echo ---------------------------------------------------
echo Error: COBAR_HOME environment variable is not defined correctly.
echo ---------------------------------------------------
goto end

:okHome
set "APP_VERSION=1.0-SNAPSHOT"

REM set COBAR_CLASSPATH
echo %COBAR_HOME%
set "COBAR_CLASSPATH=%COBAR_HOME%\conf;%COBAR_HOME%\lib\classes"
set "COBAR_CLASSPATH=%COBAR_CLASSPATH%;%COBAR_HOME%\lib\J8crawler-core-%APP_VERSION%.jar"
set "COBAR_CLASSPATH=%COBAR_CLASSPATH%;%COBAR_HOME%\lib\log4j-1.2.16.jar"
set "COBAR_CLASSPATH=%COBAR_CLASSPATH%;%COBAR_HOME%\lib\mysql-connector-java-5.1.24-bin.jar"
 FOR %%I IN ("%COBAR_HOME%\lib\*.jar") DO (
   set "COBAR_CLASSPATH=%COBAR_CLASSPATH%;%%I"
 )
 REM  echo   %COBAR_CLASSPATH%

REM startup Server
set "RUN_CMD="%JAVA_HOME%\bin\java.exe""
set "RUN_CMD=%RUN_CMD% -Dcobar.home="%COBAR_HOME%"" 
set "RUN_CMD=%RUN_CMD% -classpath "%COBAR_CLASSPATH%""
set "RUN_CMD=%RUN_CMD% %JAVA_OPTS%"
set "RUN_CMD=%RUN_CMD% com.j8crawler.core.J8crawlerManager"
call %RUN_CMD%

:end
