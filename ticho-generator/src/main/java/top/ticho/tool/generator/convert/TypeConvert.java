package top.ticho.tool.generator.convert;

import top.ticho.tool.generator.enums.DateType;
import top.ticho.tool.generator.enums.JavaType;

/**
 * Java类型DB转换
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public interface TypeConvert {

    /**
     * Java类型
     *
     * @param dateType 日期类型
     * @param dbType db类型
     * @return Java类型
     */
    JavaType typeConvert(DateType dateType, String dbType);
}
