package top.ticho.tool.generator.config;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import top.ticho.tool.generator.enums.DateType;

import java.util.List;
import java.util.Map;

/**
 * 全局配置
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Data
@Getter
@Setter
@NoArgsConstructor
public class ProjectConfig {

    /** 是否弹出文件夹 */
    private Boolean open;
    /** 是否文件追加模式 */
    private Boolean fileAppend;
    /** 是否覆盖文件 */
    private Boolean fileOverride;
    /** 模板路径 */
    private String templatePath;
    /** 根包位置 */
    private String parentPackage;
    /** Java时间类型 */
    private DateType dateType;
    /** 输出文件路径 */
    private String outPutDir;
    /** 主键名称，如id,如果实际表中有主键id，这个属性会被替换 */
    private String keyName;
    /** 表集合 */
    private List<String> tables;
    /** 表前缀 */
    private List<String> tablePrefixs;
    /** 自定义变量 */
    private Map<String, Object> customParams;

}
