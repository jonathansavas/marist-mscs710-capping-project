@echo off

title Metrics Collector

set "METRICS_COLLECTOR_HOME=%~dp0\.."

cd "%METRICS_COLLECTOR_HOME%"

java -cp "%METRICS_COLLECTOR_HOME%;%METRICS_COLLECTOR_HOME%/config;%METRICS_COLLECTOR_HOME%/libs/*" ${mainClass}
