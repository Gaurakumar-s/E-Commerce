@echo off
echo Installing and verifying dependencies...

REM Check for Node.js
where node >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Node.js is not installed. Frontend cannot run.
    echo Please install Node.js: winget install OpenJS.NodeJS.LTS
    exit /b 1
)

cd shop-frontend
echo Installing frontend dependencies...
call npm install
if %errorlevel% neq 0 (
    echo [ERROR] npm install failed.
    exit /b 1
)

echo Starting frontend...
call npm run dev
