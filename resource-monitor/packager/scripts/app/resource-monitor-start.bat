@echo off
setlocal enableDelayedExpansion

echo Starting resource monitor ...

set "KAFKA_TITLE=resource-monitor-kafka-server"
set "ZOOKEEPER_TITLE=resource-monitor-zookeeper-server"

set "RESOURCE_MONITOR_HOME=%~dp0"

set "KAFKA_HOME=%RESOURCE_MONITOR_HOME%\kafka\kafka_2.12-2.4.0"

if exist "%KAFKA_HOME%" (
    start "%ZOOKEEPER_TITLE%" /d %KAFKA_HOME% /min %KAFKA_HOME%\bin\windows\zookeeper-server-start.bat %KAFKA_HOME%\config\zookeeper.properties
    timeout /t 2 /nobreak >nul 2>&1
    start "%KAFKA_TITLE%" /d %KAFKA_HOME% /min %KAFKA_HOME%\bin\windows\kafka-server-start.bat %KAFKA_HOME%\config\server.properties
    timeout /t 2 /nobreak >nul 2>&1
)

start /d %RESOURCE_MONITOR_HOME%\metrics-collector /min %RESOURCE_MONITOR_HOME%\metrics-collector\bin\metrics-collector-start.bat
start /d %RESOURCE_MONITOR_HOME%\metrics-persistence /min %RESOURCE_MONITOR_HOME%\metrics-persistence\bin\metrics-persistence-start.bat

for /l %%i in (1, 1, 12) do (
    timeout /t 5 /nobreak >nul 2>&1
    tasklist /fi "WINDOWTITLE eq %KAFKA_TITLE%" | findstr [0-9] >nul 2>&1
    if "!ERRORLEVEL!" == "0" (
		taskkill /fi "WINDOWTITLE eq %KAFKA_TITLE%" >nul 2>&1
        start "%KAFKA_TITLE%" /d %KAFKA_HOME% /min %KAFKA_HOME%\bin\windows\kafka-server-start.bat %KAFKA_HOME%\config\server.properties
    )
)

exit
