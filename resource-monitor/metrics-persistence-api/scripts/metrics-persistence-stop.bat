@echo off

set "METRICS_PERSISTENCE_HOME=%~dp0\.."

del "%METRICS_PERSISTENCE_HOME%\runfile.tmp" >nul 2>&1
