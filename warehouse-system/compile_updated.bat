@echo off
chcp 65001 >nul
echo ============================================
echo   Warehouse Management System - Compile Script
echo ============================================
echo.

:: ==== 配置部分 ====
set SRC_DIR=src
set OUT_DIR=out
set JAVA_VERSION=26

:: ==== 清理旧文件 ====
echo [1/4] Cleaning old class files...
if exist "%OUT_DIR%" (
    rmdir /s /q "%OUT_DIR%"
)
mkdir "%OUT_DIR%"

:: ==== 按顺序编译（解决依赖关系） ====
echo [2/4] Compiling model classes...
javac -encoding UTF-8 -source %JAVA_VERSION% -target %JAVA_VERSION% -d "%OUT_DIR%" "%SRC_DIR%\model\*.java"
if errorlevel 1 goto :error

echo [3/4] Compiling DAO classes...
javac -encoding UTF-8 -source %JAVA_VERSION% -target %JAVA_VERSION% -d "%OUT_DIR%" -cp "%OUT_DIR%" "%SRC_DIR%\dao\*.java"
if errorlevel 1 goto :error

echo [4/4] Compiling service and UI classes...
javac -encoding UTF-8 -source %JAVA_VERSION% -target %JAVA_VERSION% -d "%OUT_DIR%" -cp "%OUT_DIR%" "%SRC_DIR%\service\*.java" "%SRC_DIR%\ui\*.java" "%SRC_DIR%\App.java"
if errorlevel 1 goto :error

:: ==== 成功信息 ====
echo.
echo ============================================
echo   COMPILATION SUCCESSFUL!
echo   All class files are in: "%OUT_DIR%\"
echo   Double-click run.bat to start the system
echo ============================================
pause
exit /b 0

:: ==== 错误处理 ====
:error
echo.
echo ============================================
echo   COMPILATION FAILED!
echo   Please check the error messages above
echo ============================================
pause
exit /b 1