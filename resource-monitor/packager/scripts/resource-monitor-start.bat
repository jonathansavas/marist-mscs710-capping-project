@echo off

set "SCRIPT_PATH=%~dp0"
cd "%SCRIPT_PATH%"

set "APP_HOME=%CD%"

set "KAFKA_APP_HOME=%APP_HOME%\kafka\kafka_${kafka-scala.version}-${kafka.version}"

if exist "%KAFKA_APP_HOME%" (
    start /min "%KAFKA_APP_HOME%\bin\windows\zookeeper-server-start.bat"
    timeout /t 2 /nobreak >nul 2>&1
    start /min "%KAFKA_APP_HOME%\bin\windows\kafka-server-start.bat"
    timeout /t 2 /nobreak >nul 2>&1
)

start /min "APP_HOME\metrics-collector\bin\metrics-collector-start.bat"
start /min "APP_HOME\metrics-persistence\bin\metrics-persistence-start.bat"
