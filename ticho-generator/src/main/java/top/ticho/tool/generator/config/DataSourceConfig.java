package top.ticho.tool.generator.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据源配置
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Data
@Slf4j
public class DataSourceConfig {

    /** 地址 */
    private String url;
    /** 驱动 */
    private String driverName;
    /** 用户名 */
    private String username;
    /** 密码 */
    private String password;

}
