package top.ticho.tool.generator.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.StringTemplateResourceLoader;
import top.ticho.tool.generator.exception.BeetlErrorHandler;
import top.ticho.tool.generator.exception.GenerateException;

import java.io.IOException;

/**
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeetlUtil {

    /**
     * 获取模板组
     *
     * @return 模板组
     */
    public static GroupTemplate getGroupTemplate() {
        GroupTemplate groupTemplate = new GroupTemplate(new StringTemplateResourceLoader(), getConfiguration());
        groupTemplate.setErrorHandler(new BeetlErrorHandler());
        return groupTemplate;
    }

    /**
     * 获取beetl默认配置对象
     */
    public static Configuration getConfiguration() {
        try {
            return Configuration.defaultConfiguration();
        } catch (IOException e) {
            throw new GenerateException("获取beetl默认配置对象失败！", e);
        }
    }

}
