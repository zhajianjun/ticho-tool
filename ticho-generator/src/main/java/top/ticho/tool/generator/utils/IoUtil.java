package top.ticho.tool.generator.utils;

import java.io.Closeable;

/**
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class IoUtil {

    /**
     * 关闭<br>
     * 关闭失败不会抛出异常
     *
     * @param closeable 被关闭的对象
     */
    public static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }
}
