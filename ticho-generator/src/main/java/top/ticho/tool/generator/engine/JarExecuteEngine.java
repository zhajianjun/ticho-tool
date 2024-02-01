package top.ticho.tool.generator.engine;

import top.ticho.tool.generator.config.GlobalConfig;
import top.ticho.tool.generator.config.Summary;
import top.ticho.tool.generator.config.TemplateConfig;

import java.io.File;
import java.util.StringJoiner;

/**
 * Jar执行引擎
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class JarExecuteEngine extends DefaultExecuteEngine {

    @Override
    public void complete(Summary summary) {
        GlobalConfig globalConfig = summary.getGlobalConfig();
        if (globalConfig.isOpen()) {
            this.open(globalConfig.getOutPutDir());
        }
    }

    @Override
    protected File getFile(String entityName, GlobalConfig globalConfig, TemplateConfig templateConfig) {
        StringJoiner joiner = new StringJoiner(File.separator);
        joiner.add(globalConfig.getOutPutDir());
        return getFilePath(entityName, globalConfig.isFileOverride(), templateConfig.getName(),
                templateConfig.getTempFileName(), joiner);
    }
}
