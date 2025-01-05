package top.ticho.tool.generator.entity;

import lombok.Data;

/**
 * 实体类字段属性
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Data
public class TableField {

    /** DB字段名称 如果是关键字则会被对应内容包裹，比如mysql关键字 `status` 会被 `` 包裹住 */
    private String name;
    /** DB字段简单名称,如果是关键字则不会被对应内容包裹 */
    private String simpleName;
    /** DB字段类型 */
    private String type;
    /** Java字段名称Upper */
    private String propertyUpperName;
    /** Java字段名称Lower */
    private String propertyLowerName;
    /** Java字段类型 */
    private String propertyType;
    /** 字段注释 */
    private String comment;
    /** 是否是主键索引 */
    private Boolean priKey;
    /** 是否包含索引 */
    private String index;
    /** 是否可为空 */
    private Boolean nullable;
    /** 是否可为空 */
    private String nullableValue;
    /** 默认值 */
    private String defaultValue;

}
