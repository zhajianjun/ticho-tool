package top.ticho.tool.generator.entity;

import lombok.Data;

/**
 * 文件模版
 *
 * @author zhajianjun
 * @date 2024-11-17 11:00
 */
@Data
public class FileTemplate {

    private String key;
    /** 模板文件名称 */
    private String templateFileName;
    /** 是否添加至Java目录;true-放在src/java目录下,false-resources下 */
    private Boolean addToJavaDir;
    /** package路径 */
    private String packagePath;
    /** 文件名后缀 */
    private String suffix;
    /** 文件后缀名 */
    private String extName;
    /** 模板内容 */
    private String content;

    /** 渲染文件路径 */
    private String renderFilePath;
    /** 文件首字母是否小写 */
    private Boolean lowerFirstFileName;

}
