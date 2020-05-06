@echo off

set "UI_HOME=%~dp0"

call start http://localhost:5000
start /d %UI_HOME% /min python app.py
exit
