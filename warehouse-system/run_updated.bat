@echo off
chcp 65001 >nul
echo ============================================
echo   Warehouse Management System v1.0
echo   Starting up...
echo ============================================
echo.

:: Change to the script directory (works with any path)
cd /d "%~dp0"

:: Check if program is compiled
if not exist "out\App.class" (
    echo Error: Program not compiled!
    echo Please run compile.bat or compile_updated.bat first.
    echo.
    pause
    exit /b 1
)

:: Run the program
java -cp "out" App

:: Check if the program ran successfully
if errorlevel 1 (
    echo.
    echo ============================================
    echo   STARTUP FAILED!
    echo   Please check if you have Java installed
    echo   or if the compilation succeeded.
    echo ============================================
    pause
    exit /b 1
)