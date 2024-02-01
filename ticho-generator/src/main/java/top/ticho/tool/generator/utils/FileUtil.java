package top.ticho.tool.generator.utils;

import top.ticho.tool.generator.exception.GeException;

import java.io.File;
import java.io.IOException;

/**
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class FileUtil {
    private FileUtil() {
    }

    public static boolean exists(String filePath) {
        if (StrUtil.isBlank(filePath)) {
            throw new GeException("文件路径不能为空");
        }
        File file = new File(filePath);
        return file.exists();
    }


    public static File checkFile(String filePath) {
        try {
            File file = new File(filePath);
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                // 能创建多级目录
                parentFile.mkdirs();
            }
            if (!file.exists()) {
                // 有路径才能创建文件
                file.createNewFile();
            }
            return file;
        } catch (IOException e) {
            throw new GeException(e);
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

}
