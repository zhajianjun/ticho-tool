package top.ticho.tool.generator;

import top.ticho.tool.generator.constant.CommConst;
import top.ticho.tool.generator.handler.ContextHandler;

/**
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class Application {
    static {
        // logback.xml放在config文件夹里，其它地方调用不会生效config下的logback.xml配置了
        System.setProperty(CommConst.CONFIG_FILE_PROPERTY, CommConst.CONFIG_LOGBACK_XML);
    }


    public static void main(String[] args) {
        ContextHandler contextHandler = new ContextHandler();
        contextHandler.handle();
    }


}
