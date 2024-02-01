# 切换到脚本所在目录的上级目录
cd "$(dirname "$0")/.."

# 设置JAVA_BIN变量为Java的可执行文件路径
JAVA_BIN="$JAVA_HOME"/bin/java

# 定义程序运行状态的常量
APP_OK=0
APP_ERR=1
APP_PID=""

# 定义变量赋值部分
## 获取当前目录的绝对路径
APP_HOME=$(pwd)
## 拼接Java程序的绝对路径
APP="$APP_HOME/ticho-intranet-client.jar"
## 拼接配置文件的绝对路径
APP_CONF="$APP_HOME/conf/init.properties"

# 定义查询进程PID的函数
pid() 
{
    # 使用ps命令查找包含Java程序路径的进程，并提取出PID
    APP_PID=$(ps -ef | grep -v grep | grep "$APP" | awk '{print $2}')
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

start()
{
    # 调用pid函数获取进程PID
    pid
    # 判断PID是否为空
    if [ -n "$APP_PID" ]; then
        # 不为空时，调用status函数输出程序已运行的提示信息，并返回成功状态码
        status
        return $APP_OK
    fi

    # 判断是否存在Java可执行文件
    if [ ! -x "$JAVA_BIN" ]; then
        # 如果不存在，则尝试使用which命令查找Java可执行文件路径
        JAVA_BIN=$(which java)
        # 如果仍然找不到，则将JAVA_BIN设为默认值java
        if [ ! -x "$JAVA_BIN" ]; then
            JAVA_BIN=java
        fi
    fi

    # 使用nohup命令后台运行Java程序，并将输出重定向到/dev/null
    nohup "$JAVA_BIN" -jar -Dfile.encoding=UTF-8 "$APP" > /dev/null 2>&1 &
    # 调用status函数输出程序运行状态
    status
}
start