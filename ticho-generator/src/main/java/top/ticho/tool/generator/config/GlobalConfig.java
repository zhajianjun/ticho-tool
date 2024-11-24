package top.ticho.tool.generator.config;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 全局变量
 *
 * @author zhajianjun
 * @date 2024-11-16 20:02
 */
@Data
public class GlobalConfig {

    /** 环境 */
    private List<String> envs;
    /** 忽略项目错误执行 */
    private Boolean ignoreError;
    /** 日期时间 */
    private String date;
    /** 日期格式，缺省默认为yyyy-MM-dd HH:mm:ss */
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";
    // 自定义全局变量
    private Map<String, Object> globalParams;

}
