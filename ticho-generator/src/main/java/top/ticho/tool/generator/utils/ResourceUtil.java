package top.ticho.tool.generator.utils;

import org.beetl.core.ResourceLoader;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.core.resource.StringTemplateResourceLoader;

/**
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class ResourceUtil {

    private ResourceUtil() {

    }

    public static ResourceLoader<String> getClassPathResource() {
        return SingletonClassInstance.CLASSPATH_INSTANCE;
    }

    public static ResourceLoader<String> getStringTemplateResource() {
        return SingletonClassInstance.STRING_TEMPLATE_INSTANCE;
    }

    private static class SingletonClassInstance {
        public static final ResourceLoader<String> CLASSPATH_INSTANCE = new ClasspathResourceLoader();
        public static final ResourceLoader<String> STRING_TEMPLATE_INSTANCE = new StringTemplateResourceLoader();
     }

}
