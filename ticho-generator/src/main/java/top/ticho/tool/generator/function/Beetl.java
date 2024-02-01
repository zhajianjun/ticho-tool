package top.ticho.tool.generator.function;

import top.ticho.tool.generator.config.Table;
import top.ticho.tool.generator.config.TableField;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Beetl 自定义函数，不可随意变更
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class Beetl {
    private Beetl() {

    }

    public static String convert(Table tableInfo) {
        if (Objects.nonNull(tableInfo) && Objects.nonNull(tableInfo.getFields())) {
            List<TableField> tableFields = tableInfo.getFields();
            return tableFields.stream().map(x -> "#{item." + x.getPropertyLowerName() + "}")
                    .collect(Collectors.joining(","));
        }
        return null;
    }

    /**
     * 是否被整除
     *
     * @param dividend 被除数
     * @param divisor 除数
     * @return boolean
     */
    public static boolean remainder(Integer dividend, Integer divisor) {
        if (Objects.nonNull(divisor) && Objects.nonNull(dividend)) {
            return dividend % divisor == 0;
        }
        return false;
    }

}
