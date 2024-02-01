package top.ticho.tool.generator.config;

import lombok.Data;

/**
 * 模板配置
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Data
public class TemplateConfig {

    /**
     * 模板名称,具有唯一性，不可重复,不为空
     */
    private String name;

    /**
     * 是否从模板文件读取模板信息，默认是true-是 false-否
     */
    private boolean fromFile;

    /**
     * 模板文件或者模板内容
     */
    private String templateContext;

    /**
     * 是否是java包 package
     */
    private boolean isNotPkg;

    /**
     * 相对包路径或者文件 如果isNotPkg = true，pkg就是java包相对路径 false，则是普通文件目录
     */
    private String relativePkgOrPath;

    /**
     * 文件名,格式 %sService.java
     */
    private String tempFileName;

}
