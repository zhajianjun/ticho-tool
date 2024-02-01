package top.ticho.tool.generator.utils;


import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class StrUtil {
    StrUtil(){}

    public static final String UNDERSCORE = "_";
    public static final char UNDERLINE_CHAR = '-';
    public static final char UNDERSCORE_CHAR = '_';

    /**
     * 下划线转驼峰
     * <p>
     *     例：sys_user_role -> SysUserRole
     * </p>
     *
     * @param str String
     * @return String
     */
    public static String underscoreToCamel(String str) {
        // @formatter:off
        String trim = str.replace(UNDERLINE_CHAR, UNDERSCORE_CHAR).trim();
        String[] split = trim.split(UNDERSCORE);
        return Stream.of(split)
            .filter(x-> !x.isEmpty())
            .map(String::toLowerCase)
            .map(StrUtil::toUpperFirst)
            .collect(Collectors.joining());
        // @formatter:on
    }

    /**
     * 下划线转驼峰 首字符小写
     * <p>
     *     例：sys_user_role -> sysUserRole
     * </p>
     *
     * @param str String
     * @return String
     */
    public static String underscoreToCamelLF(String str) {
        return toLowerFirst(underscoreToCamel(str));
    }


    /**
     * 首字符大写
     *
     * @param str String
     * @return String
     */
    public static String toUpperFirst(String str) {
        return str.substring(0, 1).toUpperCase().concat(str.substring(1));
    }

    /**
     * 首字符小写
     *
     * @param str String
     * @return String
     */
    public static String toLowerFirst(String str) {
        return str.substring(0, 1).toLowerCase().concat(str.substring(1));
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

        }
        return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() <= 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }
}
