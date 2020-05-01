@echo off

set "METRICS_COLLECTOR_HOME=%~dp0\.."

del "%METRICS_COLLECTOR_HOME%\runfile.tmp" >nul 2>&1
