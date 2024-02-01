package top.ticho.tool.intranet.server.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 模板
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TemplateInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 模板编码 */
    private String code;

    /** 模板名称 */
    private String name;

    /** 模板内容 */
    private String content;

    /** Json常量;缺省时，用于渲染模板 */
    private String jsonConstant;


}