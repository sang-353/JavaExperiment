@echo off
setlocal
set "JAVA_HOME=E:\environment\Java\jdk-17"
set "PATH=%JAVA_HOME%\bin;%PATH%"
cd /d "%~dp0"
call mvn -q -DskipTests package
start "" "%JAVA_HOME%\bin\java.exe" -jar "target\JavaExperiment-0.0.1-SNAPSHOT.jar"
endlocal

