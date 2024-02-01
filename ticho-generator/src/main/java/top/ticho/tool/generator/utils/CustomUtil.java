package top.ticho.tool.generator.utils;

import top.ticho.tool.generator.factory.SortedProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * 自定义工具类
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class CustomUtil {
    private static final Logger log = LoggerFactory.getLogger(CustomUtil.class);

    public static Properties createStringAdaptingProperties() {
        return new SortedProperties(false) {
            @Override
            public String getProperty(String key) {
                Object value = get(key);
                return (value != null ? value.toString() : null);
            }
        };
    }

    public static void putToProperties(Object object, Properties properties, String prefixKey) {
        // @formatter:off
        if (!(object instanceof Map)) {
            return;
        }
        Map<?, ?> map = (Map<?, ?>) object;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            String prefix = "".equals(prefixKey) ? "" : prefixKey + ".";
            if (value instanceof Map) {
                putToProperties(value, properties,prefix + key.toString());
            }
            if (key instanceof CharSequence) {
                properties.setProperty(prefix + key.toString(), Optional.ofNullable(value).map(Object::toString).orElse(""));
            }
        }
        // @formatter:on
    }

    /**
     * 获取yaml配置
     *
     * @param file 文件
     * @return Properties
     */
    public static Properties getYml(File file) {
        // @formatter:off
        if (!file.exists()) {
            return null;
        }
        Yaml yaml = new Yaml();
        try (
            InputStream inputStream = Files.newInputStream(file.toPath());
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8
        )) {
            Properties properties = createStringAdaptingProperties();
            Map<?, ?> objects = yaml.loadAs(inputStreamReader, Map.class);
            putToProperties(objects, properties,  "");
            return properties;
        } catch (IOException e) {
            log.error("{}加载失败", file.getAbsolutePath(), e);
            return null;
        }
        // @formatter:on
    }

    /**
     * 获取yaml配置
     *
     * @param pathname 文件路径
     * @return Properties
     */
    public static Properties getYml(String pathname) {
        File file = new File(pathname);
        return getYml(file);
    }

    /**
     * 获取Properties配置
     *
     * @param file 文件
     * @return Properties
     */
    public static Properties getProperties(File file) {
        // @formatter:off
        if (!file.exists()) {
            return null;
        }
        Properties properties = createStringAdaptingProperties();
        try (
            InputStream inputStream = Files.newInputStream(file.toPath());
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)
        ) {
            properties.load(inputStreamReader);
        } catch (IOException e) {
            log.error("{}加载失败", file.getAbsolutePath(), e);
            return null;
        }
        return properties;
        // @formatter:on
    }

    /**
     * 获取Properties配置
     *
     * @param pathname 文件路径
     * @return Properties
     */
    public static Properties getProperties(String pathname) {
        // @formatter:off
        File file = new File(pathname);
        return getProperties(file);
        // @formatter:on
    }
}
