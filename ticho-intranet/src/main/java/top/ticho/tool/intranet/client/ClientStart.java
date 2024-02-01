package top.ticho.tool.intranet.client;

import ch.qos.logback.classic.util.ContextInitializer;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.yaml.YamlUtil;
import lombok.extern.slf4j.Slf4j;
import top.ticho.tool.intranet.client.handler.ClientHander;
import top.ticho.tool.intranet.prop.ClientProperty;

import java.io.File;
import java.util.Objects;


/**
 * 客户端启动器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ClientStart {

    public static void main(String[] args) {
        // logback.xml放在config文件夹里，其它地方调用不会生效config下的logback.xml配置了
        System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, "conf/logback.xml");
        String projectPath = System.getProperty("user.dir");
        String filePath = projectPath + File.separator + "/conf/client.yaml";
        ClientProperty clientProperty;
        try {
            clientProperty = YamlUtil.loadByPath(filePath, ClientProperty.class);
        } catch (Exception e) {
            log.error("配置文件获取失败，{}", e.getMessage(), e);
            return;
        }
        if (Objects.isNull(clientProperty)) {
            log.error("配置文件不存在");
            return;
        }
        log.info("配置信息：{}", JSONUtil.toJsonStr(clientProperty));
        ClientHander clientHander = new ClientHander(clientProperty);
        clientHander.start();
    }

}