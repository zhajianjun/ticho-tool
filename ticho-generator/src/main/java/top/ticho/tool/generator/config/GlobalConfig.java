package top.ticho.tool.generator.config;

import top.ticho.tool.generator.enums.DateType;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
public class GlobalConfig {

    /**
     * 是否弹出文件夹
     */
    private boolean isOpen;

    /**
     * 是否覆盖文件
     */
    private boolean isFileOverride;

    /**
     * 是否关闭输出流 true-开启输出流，false-关闭输出流，主要用途查看参数，又不想生成文件
     */
    private boolean closeWriter;

    /**
     * 根包位置
     */
    private String parentPkg;

    /**
     * 模块名称
     */
    private String module;

    /**
     * Java时间类型
     */
    private DateType dateType;

    /**
     * 输出文件路径
     */
    private String outPutDir;

    /**
     * 主键名称，如id,如果实际表中有主键id，这个属性会被替代
     */
    private String keyName;

    /**
     * 表集合
     */
    private List<String> tables;

    /**
     * 表前缀
     */
    private List<String> tablePrefixs;
}
