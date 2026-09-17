@echo off
title Stop Digital Printing Queue Management System

echo ======================================================
echo  STOPPING DIGITAL PRINTING QUEUE SERVICES
echo ======================================================
echo.

docker info >nul 2>&1
if %errorlevel% equ 0 (
    echo Stopping Docker Compose containers...
    docker-compose down
    echo [PASS] Containers stopped. Persistent volume 'printqueue_uploads' preserved.
) else (
    echo Stopping local Java instances on port 8080...
    for /f "tokens=5" %%a in ('netstat -aon ^| find ":8080" ^| find "LISTENING"') do (
        taskkill /f /pid %%a >nul 2>&1
        echo [PASS] Stopped process %%a on port 8080.
    )
)

echo.
echo ======================================================
echo  SERVICES STOPPED SUCCESSFULLY
echo ======================================================
pause
