@echo off

set "SCRIPT_PATH=%~dp0"
cd "%SCRIPT_PATH%"
cd ..

set "APP_HOME=%CD%"

del "%APP_HOME%\runfile.tmp" >nul 2>&1
