@echo off

set "SCRIPT_PATH=%~dp0"
cd "%SCRIPT_PATH%"

set "APP_HOME=%CD%"

set "KAFKA_APP_HOME=%APP_HOME%\kafka\kafka_${kafka-scala.version}-${kafka.version}"

"APP_HOME\metrics-persistence\bin\metrics-persistence-stop.bat"
"APP_HOME\metrics-collector\bin\metrics-collector-stop.bat"

if exist "%KAFKA_APP_HOME%" (
    "%KAFKA_APP_HOME%\bin\windows\kafka-server-stop.bat"
    "%KAFKA_APP_HOME%\bin\windows\zookeeper-server-stop.bat"
)
