package top.ticho.tool.generator.util;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import top.ticho.tool.generator.exception.GenerateException;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * 断言工具类
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssertUtil {


    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            cast(message);
        }
    }

    public static void isTrue(boolean condition, Supplier<String> stringSupplier) {
        if (!condition) {
            String errMsg = Optional.ofNullable(stringSupplier)
                .map(Supplier::get)
                .filter(s -> !s.isEmpty())
                .orElse("未知异常");
            cast(errMsg);
        }
    }


    public static void cast(String errMsg) {
        throw new GenerateException(errMsg);
    }


}
