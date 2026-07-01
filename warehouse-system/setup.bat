@echo off
chcp 65001 >nul
title Warehouse System Setup

echo ============================================
echo   WAREHOUSE MANAGEMENT SYSTEM - SETUP
echo ============================================
echo.
echo This script will help you prepare the system
echo in a pure English path for better compatibility.
echo.
echo Steps:
echo 1. Select a destination folder (English path only)
echo 2. Copy all project files
echo 3. Create run shortcuts
echo.
echo Press any key to continue...
pause >nul

echo.
echo ============================================
echo   CURRENT PROJECT LOCATION:
echo   %cd%
echo ============================================
echo.

:: Get current directory
set CURRENT_DIR=%cd%

:: Ask for destination
set /p DEST_DIR="Enter destination folder (English path only, e.g. C:\projects\warehouse): "
if "%DEST_DIR%"=="" goto :end

:: Check if destination is valid
if not exist "%DEST_DIR%" (
    echo Creating directory: %DEST_DIR%
    mkdir "%DEST_DIR%"
)

:: Copy project files
echo Copying project files...
echo From: %CURRENT_DIR%
echo To: %DEST_DIR%
echo.

xcopy "%CURRENT_DIR%\*" "%DEST_DIR%\" /E /I /Y

:: Create shortcuts batch file
echo @echo off > "%DEST_DIR%\start.bat"
echo echo Warehouse Management System >> "%DEST_DIR%\start.bat"
echo echo. >> "%DEST_DIR%\start.bat"
echo echo 1. First run compile_updated.bat to compile the program >> "%DEST_DIR%\start.bat"
echo echo 2. Then run run_updated.bat to start the system >> "%DEST_DIR%\start.bat"
echo echo. >> "%DEST_DIR%\start.bat"
echo echo Or double-click compile_updated.bat and run_updated.bat directly. >> "%DEST_DIR%\start.bat"
echo pause >> "%DEST_DIR%\start.bat"

echo.
echo ============================================
echo   SETUP COMPLETE!
echo ============================================
echo.
echo Project copied to: %DEST_DIR%
echo.
echo Instructions:
echo 1. Open the folder: %DEST_DIR%
echo 2. Double-click compile_updated.bat (compile)
echo 3. Double-click run_updated.bat (run)
echo.
echo Or open start.bat for detailed instructions.
echo.
echo Made simple for you! :)
echo.

:end
pause
exit