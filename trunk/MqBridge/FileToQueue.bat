@echo off
setlocal EnableDelayedExpansion
for /f %%i in ("%0") do set curdir=%%~dpi

set CPATH=%curdir%bin\JQuerySQL.jar
set CPATH=%curdir%res;%CPATH%
for /R %curdir%lib %%a in (*.jar) do (
   set CPATH=!CPATH!;%%a
)
 
%JAVA_HOME%\bin\java -classpath %CPATH% uk.co.marcoratto.mqbridge.FileToQueue  %*
endlocal