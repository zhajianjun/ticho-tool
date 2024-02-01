chcp 65001
@echo off
setlocal enabledelayedexpansion

set errorlevel=
set JAVA_BIN=javaw.exe
set LINE=------------------------------------------

@REM 查找正在运行的名为javaw.exe的进程
@echo !LINE!
tasklist | findstr !JAVA_BIN!

if !errorlevel! equ 0 (
    @echo !LINE!
    @echo.
@REM 强制结束所有名为javaw.exe的进程及其子进程
    taskkill /f /t /im !JAVA_BIN!

    @echo.
    @echo 已停止客户端。
    @echo.
) else (
   @echo.
   @echo 客户端已停止。
   @echo.
)

@echo !LINE!
pause
goto:eof