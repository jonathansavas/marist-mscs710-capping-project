@echo off

title Metrics Collector

set "SCRIPT_PATH=%~dp0"
cd "%SCRIPT_PATH%"
cd ..

set "APP_HOME=%CD%"

java -cp "%APP_HOME%;%APP_HOME%/config;%APP_HOME%/libs/*" ${mainClass}
