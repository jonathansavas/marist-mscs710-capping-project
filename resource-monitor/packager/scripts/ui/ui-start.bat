@echo off

set "UI_HOME=%~dp0"

start /d %UI_HOME% /min python app.py
timeout /t 1 /nobreak >nul 2>&1

cd "%UI_HOME%\web"
call start http://localhost:5001
python -m http.server 5001 --bind 127.0.0.1
