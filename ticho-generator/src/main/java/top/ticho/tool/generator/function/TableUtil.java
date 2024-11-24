package top.ticho.tool.generator.function;

import top.ticho.tool.generator.constant.CommConst;
import top.ticho.tool.generator.entity.Table;
import top.ticho.tool.generator.entity.TableField;
import top.ticho.tool.generator.util.StrUtil;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Beetl 自定义函数，不可随意变更
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class TableUtil {
    public String getFields(Table table) {
        if (Objects.isNull(table) || Objects.isNull(table.getFields())) {
            return CommConst.EMPTY;
        }
        return table.getFields()
            .stream()
            .map(TableField::getPropertyLowerName)
            .collect(Collectors.joining("},#{item.", "#{item.", "}"));
    }

    public String getFields(Table table, String delimiter, String prefix, String suffix) {
        if (Objects.isNull(table) || Objects.isNull(table.getFields())) {
            return CommConst.EMPTY;
        }
        prefix = StrUtil.emptyDefault(prefix, CommConst.EMPTY);
        suffix = StrUtil.emptyDefault(suffix, CommConst.EMPTY);
        return table.getFields()
            .stream()
            .map(TableField::getPropertyLowerName)
            .collect(Collectors.joining(delimiter, prefix, suffix));
    }

    /**
     * 是否被整除
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @return boolean
     */
    public boolean remainder(Integer dividend, Integer divisor) {
        if (Objects.nonNull(divisor) && Objects.nonNull(dividend)) {
            return dividend % divisor == 0;
        }
        return false;
    }

}
