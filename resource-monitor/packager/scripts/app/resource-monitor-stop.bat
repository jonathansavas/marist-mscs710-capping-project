@echo off

echo Stopping resource monitor ...

set "KAFKA_TITLE=resource-monitor-kafka-server"
set "ZOOKEEPER_TITLE=resource-monitor-zookeeper-server"
set "PERSISTENCE_TITLE=Metrics Persistence"
set "COLLECTOR_TITLE=Metrics Collector"

set "RESOURCE_MONITOR_HOME=%~dp0"

set "KAFKA_HOME=%RESOURCE_MONITOR_HOME%\kafka\kafka_2.12-2.4.0

call %RESOURCE_MONITOR_HOME%\metrics-persistence\bin\metrics-persistence-stop.bat
call %RESOURCE_MONITOR_HOME%\metrics-collector\bin\metrics-collector-stop.bat

timeout /t 7 /nobreak >nul 2>&1

if exist "%KAFKA_HOME%" (
    call %KAFKA_HOME%\bin\windows\kafka-server-stop.bat >nul 2>&1
    timeout /t 2 /nobreak >nul 2>&1
    call %KAFKA_HOME%\bin\windows\zookeeper-server-stop.bat >nul 2>&1
    timeout /t 2 /nobreak >nul 2>&1
)

taskkill /fi "WINDOWTITLE eq %KAFKA_TITLE%" >nul 2>&1
taskkill /fi "WINDOWTITLE eq %ZOOKEEPER_TITLE%" >nul 2>&1
taskkill /fi "WINDOWTITLE eq %PERSISTENCE_TITLE%" >nul 2>&1
taskkill /fi "WINDOWTITLE eq %COLLECTOR_TITLE%" >nul 2>&1

exit
