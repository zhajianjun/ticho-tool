package top.ticho.tool.intranet.client;

import ch.qos.logback.classic.util.ContextInitializer;
import top.ticho.tool.intranet.client.handler.ClientHander;
import top.ticho.tool.intranet.prop.ClientProperty;


/**
 * 客户端启动测试
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class ClientStartTest {

    public static void main(String[] args) {
        // logback.xml放在config文件夹里，其它地方调用不会生效config下的logback.xml配置了
        System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, "conf/logback.xml");
        ClientProperty clientProperty = new ClientProperty();
        clientProperty.setAccessKey("68bfe8f0af124ecfa093350ab8d4b44f");
        clientProperty.setServerHost("127.0.0.1");
        clientProperty.setServerPort(5120);
        clientProperty.setSslEnable(false);
        ClientHander clientHander = new ClientHander(clientProperty);
        clientHander.start();
    }

}