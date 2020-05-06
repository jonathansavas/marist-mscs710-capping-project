@echo off

title Metrics Persistence

set "METRICS_PERSISTENCE_HOME=%~dp0\.."

cd "%METRICS_PERSISTENCE_HOME%"

java -cp "%METRICS_PERSISTENCE_HOME%;%METRICS_PERSISTENCE_HOME%/config;%METRICS_PERSISTENCE_HOME%/libs/*" ${mainClass}
