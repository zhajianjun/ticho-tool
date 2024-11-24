package top.ticho.tool.generator.entity;

import lombok.Data;

import java.util.List;

/**
 * 表结构信息
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Data
public class Table {

    /** 表名 */
    private String name;
    /** 表注释 */
    private String comment;
    /** 实体类名称 */
    private String entityName;
    /** 实体类名称,首字母小写 */
    private String entityLowerName;
    /** 主键id名称 */
    private String keyName;
    /** 实体类字段属性 */
    private List<TableField> fields;
    /** 实体类字段名称，逗号隔开 */
    private String fieldNames;
    /** imports */
    private List<String> imports;

}
