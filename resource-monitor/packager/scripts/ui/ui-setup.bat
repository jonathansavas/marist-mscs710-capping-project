@echo off

set "UI_HOME=%~dp0"

where pip >nul 2>&1

if "%ERRORLEVEL%" == "0" (
	echo Running "pip install -r requirements.txt" ...
	echo.
    pip install -r "%UI_HOME%\requirements.txt"
) else (
    echo Could not find "pip", please run "pip install %UI_HOME%requirements.txt"
    pause
	echo.
)

where python >nul 2>&1

if NOT "%ERRORLEVEL%" == "0" (
    echo Could not find "python", please add "python" to your PATH
    echo Python 3 is required
    pause
    goto :eof
)

python -V | findstr ython.3 >nul 2>&1

if NOT "%ERRORLEVEL%" == "0" (
    echo Python 3 not detected
    echo Make sure "python" command references a Python 3 installation
    pause
    goto :eof
)
