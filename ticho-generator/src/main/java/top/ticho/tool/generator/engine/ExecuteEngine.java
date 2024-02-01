package top.ticho.tool.generator.engine;

import top.ticho.tool.generator.config.GlobalConfig;
import top.ticho.tool.generator.config.Summary;
import top.ticho.tool.generator.config.Table;
import top.ticho.tool.generator.config.TemplateConfig;

import java.io.OutputStream;
import java.util.List;

/**
 * 引擎接口
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public interface ExecuteEngine {

    /**
     * 启动
     *
     * @param summary 汇总参数对象
     */
    void startUp(Summary summary);

    /**
     * 查询所有表数据
     *
     * @param summary 汇总参数对象
     * @param isGetTableField 是否获取表属性信息
     * @return 所有表信息
     */
    List<Table> getAllTables(Summary summary, boolean isGetTableField);

    /**
     * 获取输出流
     *
     * @param table 表结构信息
     * @param templateConfig 模板配置
     * @param globalConfig 全局配置信息
     * @return 输入流
     */
    OutputStream getOutputStream(Table table, TemplateConfig templateConfig, GlobalConfig globalConfig);

}
