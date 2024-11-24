package top.ticho.tool.generator.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import top.ticho.tool.generator.exception.GenerateException;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtil {


    public static void checkFile(File file) {
        try {
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                // 能创建多级目录
                parentFile.mkdirs();
            }
            if (!file.exists()) {
                // 有路径才能创建文件
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new GenerateException(e);
        }
    }

    public static String extName(String fileName) {
        if (fileName == null) {
            return null;
        }
        int index = fileName.lastIndexOf(".");
        if (index == -1) {
            return "";
        } else {
            return fileName.substring(index + 1);
        }
    }

    public static String readString(File file) {
        long len = file.length();
        byte[] bytes = new byte[(int) len];
        FileInputStream in = null;
        int readLength;
        try {
            in = new FileInputStream(file);
            readLength = in.read(bytes);
            if (readLength < len) {
                throw new GenerateException(String.format("File length is [%s] but read [%s]!", len, readLength));
            }
        } catch (Exception e) {
            throw new GenerateException(e);
        } finally {
            close(in);
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 关闭
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
