@echo off
rem Starts the server in backend\ on http://localhost:8080 (PORT changes it).
rem CN1_PROFILE picks its settings; the default, dev, needs nothing installed.
setlocal
cd /d "%~dp0"
if "%CN1_PROFILE%"=="" set CN1_PROFILE=dev
call mvnw.cmd -B -pl backend -am -Dcodename1.platform=backend compile cn1:backend %*
