package top.ticho.tool.generator.factory;

import top.ticho.tool.generator.utils.CustomUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class Test {

    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
        Properties properties = CustomUtil.getYml("D:\\workingDirectory\\backend\\fog-generator\\generator\\src\\main"
                + "\\resources"
                + "\\config.yml");
        System.out.println(properties);
        System.out.println(properties.getProperty("template.entity.pkgOrPath"));
        System.out.println(properties.getProperty("template.mapper.pkgOrPath"));
    }

}
