package top.ticho.tool.generator.config;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 汇总参数对象
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Data
@NoArgsConstructor
public class Summary {

    /**
     * 数据源配置
     */
    private DataConfig dataConfig;

    /**
     * 全局配置
     */
    private GlobalConfig globalConfig;

    /**
     * 模板配置列表
     */
    private List<TemplateConfig> templateConfigs;

    /**
     * 通用模板参数
     */
    private Map<String, Object> commonTemplateParams;

    /**
     * 所有表模版参数信息
     */
    private Map<String, Object> allTableTemplateParams;

    public Map<String, Object> getAllTableTemplateParams() {
        if (allTableTemplateParams == null) {
            allTableTemplateParams = new HashMap<>(16);
        }
        return allTableTemplateParams;
    }

}
