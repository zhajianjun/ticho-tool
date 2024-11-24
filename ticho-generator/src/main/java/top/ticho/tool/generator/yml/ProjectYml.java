package top.ticho.tool.generator.yml;

import lombok.Data;
import top.ticho.tool.generator.config.DataSourceConfig;
import top.ticho.tool.generator.config.FileTemplateConfig;
import top.ticho.tool.generator.config.ProjectConfig;

import java.util.Map;

/**
 * @author zhajianjun
 * @date 2024-11-16 23:06
 */
@Data
public class ProjectYml {

    /** 项目配置 */
    private ProjectConfig projectConfig;
    /** 数据源配置 */
    private DataSourceConfig dataSourceConfig;
    /** 模板配置 */
    private Map<String, FileTemplateConfig> fileTemplateConfig;
    /** 自定义变量 */
    private Map<String, Object> customParams;

}
