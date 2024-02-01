chcp 65001
@echo off
@REM 设置窗口标题
title ticho-intranet-client

@REM 批处理设计了变量延迟。简单来说，在读取了一条完整的语句之后，不立即对该行的变量赋值，而会在某个单条语句执行之前再进行赋值，也就是说“延迟”了对变量的赋值。
setlocal enabledelayedexpansion
set errorlevel=

@REM 设置JAVA_BIN变量为java命令的路径
set JAVA_BIN=java

@REM 设置应用程序的状态码
set APP_OK=0
set APP_ERR=1

@REM 获取当前脚本所在的目录
set APP_CURDIR=%~dp0

@REM 切换到上级目录
cd /d %APP_CURDIR%\..

@REM 获取应用程序的根目录
set APP_HOME=%cd%

@REM 设置应用程序的路径和日志文件路径
set APP=!APP_HOME!\ticho-intranet-client.jar
set APP_LOG=!APP_HOME!\logs

@REM 分割线
set APP_LINE=------------------------------------------

@REM 设置应用程序的配置文件路径
set APP_CONF=!APP_HOME!\conf\init.properties

@REM 检查Java是否安装并设置正确
"!JAVA_BIN!" -version 1>nul 2>nul
if !errorlevel! neq 0 (
    @echo.
    @echo 请安装Java 1.8或更高版本，并确保Java设置正确。
    @echo.
    @echo 您可以执行命令 [ !JAVA_BIN! -version ] 来检查Java是否安装并设置正确。
    @echo.
    pause
    goto:eof
)

@REM 启动客户端应用程序
@echo !APP_LINE!
@echo 正在启动客户端...

start /b !JAVA_BIN!w -jar -Dfile.encoding=UTF-8 "!APP!"
timeout /T 3 /NOBREAK

@REM 检查客户端应用程序是否启动成功
@echo !APP_LINE!
tasklist | findstr !JAVA_BIN!w

if !errorlevel! equ 0 (
    @echo !APP_LINE!
    @echo 客户端已启动。
    @echo.
    @echo 客户端正在运行。
    @echo !APP_LINE!
) else (
    @echo 客户端已停止。
    @echo 请查看日志文件以获取详细信息 [ !APP_LOG! ]
    @echo !APP_LINE!
)

pause
goto:eof