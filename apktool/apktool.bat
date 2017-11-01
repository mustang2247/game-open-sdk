@echo off
if "%PATH_BASE%" == "" set PATH_BASE=%PATH%
set PATH=%CD%;%PATH_BASE%;
java -jar -Duser.language=en "%~dp0\apktool_2.2.3.jar" %1 %2 %3 %4 %5 %6 %7 %8 %9
