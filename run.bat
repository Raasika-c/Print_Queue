@echo off
setlocal enabledelayedexpansion
title Digital Printing Queue Management System

echo ======================================================
echo  DIGITAL PRINTING QUEUE MANAGEMENT SYSTEM
echo  Windows Production ^& Lab Launcher
echo ======================================================
echo.

:: 1. Check if application is already healthy on Port 8080
powershell -NoProfile -ExecutionPolicy Bypass -Command "try { $r = Invoke-RestMethod -Uri 'http://localhost:8080/actuator/health' -TimeoutSec 2; if ($r.status -eq 'UP') { exit 0 } else { exit 1 } } catch { exit 1 }" >nul 2>&1
if %errorlevel% equ 0 (
    echo [INFO] Digital Print Queue is already running and healthy on Port 8080!
    goto SHOW_URLS
)

:: 2. Check Docker availability
docker info >nul 2>&1
if %errorlevel% equ 0 (
    echo [1/3] Docker engine detected. Starting containers via Docker Compose...
    docker volume create printqueue_uploads >nul 2>&1
    docker-compose up -d
    if %errorlevel% neq 0 (
        echo [ERROR] Failed to start Docker Compose containers.
        pause
        exit /b 1
    )

    echo [2/3] Waiting for Spring Boot and MySQL services to become healthy...
    set RETRIES=0
    :WAIT_LOOP
    set /a RETRIES+=1
    if !RETRIES! gtr 25 (
        echo.
        echo [WARN] Health check timed out after 50 seconds.
        echo Checking container status with docker ps:
        docker ps
        goto SHOW_URLS
    )
    <nul set /p=.
    powershell -NoProfile -ExecutionPolicy Bypass -Command "try { $r = Invoke-RestMethod -Uri 'http://localhost:8080/actuator/health' -TimeoutSec 2; if ($r.status -eq 'UP') { exit 0 } else { exit 1 } } catch { exit 1 }" >nul 2>&1
    if %errorlevel% equ 0 (
        echo.
        echo [3/3] Application Actuator reports status: UP!
        goto SHOW_URLS
    )
    powershell -NoProfile -Command "Start-Sleep -Seconds 2"
    goto WAIT_LOOP
)

:: 3. Fallback to Local Java if Docker is not active
echo [1/3] Docker daemon not detected. Falling back to local Java execution...
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Neither Docker nor Java is available in PATH.
    echo Please start Docker Desktop or install Java 21 to run this project.
    pause
    exit /b 1
)

:: Check for compiled JAR
if not exist "target\digital-print-queue-1.0.0.jar" (
    echo [2/3] JAR file not found. Building with Maven...
    set MVN_CMD=mvn
    where mvn >nul 2>&1
    if %errorlevel% neq 0 (
        if exist "C:\apache-maven-3.9.16\bin\mvn.cmd" (
            set MVN_CMD="C:\apache-maven-3.9.16\bin\mvn.cmd"
        ) else (
            echo [ERROR] Maven not found. Please compile the project first.
            pause
            exit /b 1
        )
    )
    call !MVN_CMD! package -DskipTests
    if %errorlevel% neq 0 (
        echo [ERROR] Maven build failed.
        pause
        exit /b 1
    )
)

echo [3/3] Starting Spring Boot application locally...
start "Digital Print Queue" java -jar target\digital-print-queue-1.0.0.jar

:SHOW_URLS
echo.
echo ======================================================
echo  APPLICATION RUNNING SUCCESSFULLY!
echo ======================================================
echo  Web UI Portal     : http://localhost:8080
echo  Login Page        : http://localhost:8080/login.html
echo  Live Print Queue  : http://localhost:8080/queue.html
echo  Printer Control   : http://localhost:8080/printer.html
echo  Admin Dashboard   : http://localhost:8080/admin-dashboard.html
echo  Actuator Telemetry : http://localhost:8080/actuator/health
echo  Swagger API Docs  : http://localhost:8080/swagger-ui.html
echo.
echo  Demo Admin Login  : admin@digitalprint.com / Admin@123
echo ======================================================
echo.

set /p OPEN_BROWSER="Open Web UI in default browser? (Y/N, default Y): "
if /i "%OPEN_BROWSER%"=="" set OPEN_BROWSER=Y
if /i "%OPEN_BROWSER%"=="Y" (
    start http://localhost:8080
)

echo.
echo Application is running. Close this window when done.
pause
