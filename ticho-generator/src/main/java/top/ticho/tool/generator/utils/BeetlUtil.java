package top.ticho.tool.generator.utils;

import top.ticho.tool.generator.exception.BeetlErrorHandler;
import top.ticho.tool.generator.exception.GeException;
import lombok.extern.slf4j.Slf4j;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.ResourceLoader;

import java.io.IOException;

/**
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class BeetlUtil {
    private BeetlUtil() {

    }

    /**
     * 获取模板组
     *
     * @param isTemplate 是否从模板文件读取模板信息，默认是true-是 false-否
     * @return 模板组
     */
    public static GroupTemplate getGroupTemplate(boolean isTemplate) {
        GroupTemplate groupTemplate = new GroupTemplate(getResourceLoader(isTemplate), getConfiguration());
        groupTemplate.setErrorHandler(new BeetlErrorHandler());
        return groupTemplate;
    }

    /**
     * 获取 资源加载器
     * 负责根据GroupTemplate提供的Key,来获取Resource，这些Resource可以是文件
     *
     * @param isTemplate 是否从模板文件读取模板信息，默认是true-是 false-否
     * @return 资源加载器
     */
    public static ResourceLoader<String> getResourceLoader(boolean isTemplate) {
        ResourceLoader<String> resourceLoader;
        if (isTemplate) {
            resourceLoader = ResourceUtil.getClassPathResource();
        } else {
            resourceLoader = ResourceUtil.getStringTemplateResource();
        }
        return resourceLoader;
    }

    /**
     * 获取beetl默认配置对象
     * @return beetl默认配置对象
     */
    public static Configuration getConfiguration() {
        try {
            return Configuration.defaultConfiguration();
        } catch (IOException e) {
            String message = "获取beetl默认配置对象失败！";
            log.error(message, e);
            throw new GeException(message);
        }
    }
}
