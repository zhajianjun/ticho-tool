#!/bin/bash

# 获取当前脚本所在目录的父目录，并赋值给APP_HOME
cd `dirname $0`/..
APP_HOME=`pwd`

# 定义常量
APP_OK=0
APP_ERR=1
APP_PID=""

# 设置应用程序路径
APP=$APP_HOME/ticho-intranet-client.jar

# 获取应用程序的PID
pid() 
{
    APP_PID=`ps -ef | grep -v grep | grep "$APP" | awk '{print $2}'`
}

# 定义查询程序运行状态的函数
status()
{
    # 调用pid函数获取进程PID
    pid
    # 判断PID是否为空
    if [ -n "$APP_PID" ]; then
        # 不为空时，输出程序已运行的提示信息
        echo "ticho-intranet-client程序已经运行; PID=$APP_PID"
    else
        # 为空时，输出程序未启动或已终止的提示信息
        echo "ticho-intranet-client程序未启动或已终止"
    fi
}

# 停止应用程序
stop() 
{
    # 获取应用程序的PID
    pid
    # 如果应用程序未运行，则显示状态，并返回成功状态码
    if [ -z "$APP_PID" ]; then
        status
        return $APP_OK
    fi

    # 停止应用程序，并将输出重定向到日志文件
    echo -e "ticho-intranet-client程序正在停止; PID=$APP_PID"
    kill -9 $APP_PID > /dev/null 2>&1
    # 显示应用程序的状态
    status
}

# 调用停止函数
stop