package top.ticho.tool.generator.engine;

import top.ticho.tool.generator.config.GlobalConfig;
import top.ticho.tool.generator.config.Summary;
import top.ticho.tool.generator.config.Table;
import top.ticho.tool.generator.config.TemplateConfig;
import top.ticho.tool.generator.exception.GeException;
import top.ticho.tool.generator.utils.FileUtil;
import top.ticho.tool.generator.utils.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.StringJoiner;

/**
 * 默认执行引擎
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class DefaultExecuteEngine extends AbstractExecuteEngine {
    private static final Logger log = LoggerFactory.getLogger(DefaultExecuteEngine.class);

    @Override
    public OutputStream getOutputStream(Table table, TemplateConfig templateConfig, GlobalConfig globalConfig) {
        File file = this.getFile(table.getEntityName(), globalConfig, templateConfig);
        if (file == null) {
            return null;
        }
        try {
            return new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new GeException("文件未找到", e);
        }
    }

    @Override
    public void complete(Summary summary) {
        GlobalConfig globalConfig = summary.getGlobalConfig();
        if (globalConfig.isOpen()) {
            this.open(globalConfig.getOutPutDir());
        }
    }

    protected File getFile(String entityName, GlobalConfig globalConfig, TemplateConfig templateConfig) {
        StringJoiner joiner = new StringJoiner(File.separator);
        joiner.add(globalConfig.getOutPutDir());
        joiner.add("src");
        joiner.add("main");
        if (!templateConfig.isNotPkg()) {
            joiner.add(JAVA);
        }
        String pkg = templateConfig.getRelativePkgOrPath();
        // 去除"."和文件前缀的"/"
        pkg = pkg.replaceAll("\\.", PREFIX);
        if (pkg.startsWith(PREFIX)) {
            pkg = pkg.replaceFirst(PREFIX, "");
        }
        pkg = pkg.replace(PREFIX, File.separator);
        joiner.add(pkg);
        return getFilePath(entityName, globalConfig.isFileOverride(), null, templateConfig.getTempFileName(), joiner);
    }

    protected File getFilePath(String entityName, boolean fileOverride, String prefix, String tempFileName,
            StringJoiner joiner) {
        String fileName = String.format(tempFileName, entityName);
        if (StrUtil.isNotBlank(prefix)) {
            joiner.add(prefix);
        }
        joiner.add(fileName);
        String filePath = joiner.toString();
        boolean exists = FileUtil.exists(filePath);
        if (exists && !fileOverride) {
            return null;
        }
        log.debug("输出文件：{}", filePath);
        return FileUtil.checkFile(filePath);
    }

}
