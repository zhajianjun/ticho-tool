package top.ticho.tool.generator.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import top.ticho.tool.generator.function.StringUtil;

/**
 * 字符串工具
 *
 * @author zhajianjun
 * @date 2024-11-09 10:04
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StrUtil {
    private static final StringUtil stringUtil = new StringUtil();

    public static String generateId() {
        return stringUtil.generateId();
    }


    /**
     * 首字符大写
     *
     * @param str String
     * @return String
     */
    public static String toUpperFirst(String str) {
        return stringUtil.toUpperFirst(str);
    }

    /**
     * 首字符小写
     *
     * @param str String
     * @return String
     */
    public static String toLowerFirst(String str) {
        return stringUtil.toLowerFirst(str);
    }

    /**
     * 转下划线
     */
    public static String toUnderline(String str) {
        return stringUtil.toUnderline(str);
    }

    /**
     * 转横线
     */
    public static String toHyphen(String str) {
        return stringUtil.toHyphen(str);
    }

    /**
     * 转驼峰，首字母小写
     */
    public static String toCamelUF(String str) {
        return stringUtil.toCamelUF(str);
    }

    /**
     * 转驼峰，首字母大写
     */
    public static String toCamelLF(String str) {
        return stringUtil.toCamelLF(str);
    }

    public static boolean isBlank(CharSequence cs) {
        return stringUtil.isBlank(cs);
    }

    public static boolean isNotBlank(CharSequence cs) {
        return stringUtil.isNotBlank(cs);
    }

    public static boolean isEmpty(CharSequence cs) {
        return stringUtil.isEmpty(cs);
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return stringUtil.isNotEmpty(cs);
    }

    public static String emptyDefault(String suffix, String defaultValue) {
        return stringUtil.emptyDefault(suffix, defaultValue);
    }

}
