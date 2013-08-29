@echo off
setlocal ENABLEDELAYEDEXPANSION

set CLASSPATH=.

for /R .\lib %%G IN (*.jar) do set CLASSPATH=!CLASSPATH!;%%G

set CLASSPATH=%CLASSPATH%;mt4monitor.jar

java -Xmx1g -classpath %CLASSPATH% com.miteke.mt4.monitor.StartApp &
#java -Xmx1g -classpath %CLASSPATH% -Xdebug -Xnoagent -Djava.compiler=none -Xrunjdwp:server=y,transport=dt_socket,suspend=n,address=35988 com.miteke.mt4.monitor.StartApp &
