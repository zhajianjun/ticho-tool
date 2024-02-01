package top.ticho.tool.generator.factory;

import top.ticho.tool.generator.config.DataConfig;
import top.ticho.tool.generator.config.GlobalConfig;
import top.ticho.tool.generator.config.Summary;
import top.ticho.tool.generator.config.TemplateConfig;
import top.ticho.tool.generator.engine.DefaultExecuteEngine;
import top.ticho.tool.generator.engine.ExecuteEngine;
import top.ticho.tool.generator.enums.DateType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class SimpleFogExample {
    public static void handle() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setOpen(false);
        globalConfig.setFileOverride(true);
        globalConfig.setParentPkg("top.ticho.module");
        globalConfig.setDateType(DateType.TIME_PACK);
        globalConfig.setOutPutDir("D:\\workingDirectory\\backend\\fog");
        globalConfig.setTables(Stream.of("sys_dict","sys_dict_type", "sys_menu", "sys_operate_log", "sys_role", "sys_user", "sys_user_role").collect(Collectors.toList()));
        globalConfig.setTablePrefixs(Stream.of("fog","sys").collect(Collectors.toList()));
        globalConfig.setKeyName("id");

        DataConfig dataConfig = new DataConfig();
        dataConfig.setUrl("jdbc:mysql://122.112.164.156:3306/fog_dev?useUnicode=true&characterEncoding=UTF-8&useSSL=true&serverTimezone=GMT%2B8");
        dataConfig.setDriverName("com.mysql.cj.jdbc.Driver");
        dataConfig.setUsername("root");
        dataConfig.setPassword("1511ticho.");
        Map<String, Object> common = new HashMap<>(16);
        common.put("author", "zhajianjun");
        common.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        common.put("enableLombok", true);
        common.put("enableSwagger", true);
        common.put("enableMybatisCache", false);
        common.put("enableMybatisPlus", true);
        Summary summary = new Summary();
        summary.setDataConfig(dataConfig);
        summary.setGlobalConfig(globalConfig);
        summary.setTemplateConfigs(getTemplateConfigs());
        summary.setCommonTemplateParams(common);

        ExecuteEngine defaultExcuteEngine = new DefaultExecuteEngine();
        defaultExcuteEngine.startUp(summary);
    }

    public static List<TemplateConfig> getTemplateConfigs() {
        String module = "fog";
        TemplateConfig entity = new TemplateConfig();
        entity.setName("entity");
        entity.setFromFile(true);
        entity.setTemplateContext("templates/" + module + "/entity.java.btl");
        entity.setRelativePkgOrPath("test.entity");
        entity.setTempFileName("%s.java");
        TemplateConfig mapper = new TemplateConfig();
        mapper.setName("mapper");
        mapper.setFromFile(true);
        mapper.setTemplateContext("templates/" + module + "/mapper.java.btl");
        mapper.setRelativePkgOrPath("test.mapper");
        mapper.setTempFileName("%sMapper.java");
        TemplateConfig service = new TemplateConfig();
        service.setName("service");
        service.setFromFile(true);
        service.setTemplateContext("templates/" + module + "/service.java.btl");
        service.setRelativePkgOrPath("test.service");
        service.setTempFileName("%sService.java");
        TemplateConfig serviceImpl = new TemplateConfig();
        serviceImpl.setName("serviceImpl");
        serviceImpl.setFromFile(true);
        serviceImpl.setTemplateContext("templates/" + module + "/serviceImpl.java.btl");
        serviceImpl.setRelativePkgOrPath("test.service.impl");
        serviceImpl.setTempFileName("%sServiceImpl.java");
        TemplateConfig controller = new TemplateConfig();
        controller.setName("controller");
        controller.setFromFile(true);
        controller.setTemplateContext("templates/" + module + "/controller.java.btl");
        controller.setRelativePkgOrPath("test.controller");
        controller.setTempFileName("%sController.java");
        TemplateConfig xml = new TemplateConfig();
        xml.setName("xml");
        xml.setFromFile(true);
        xml.setNotPkg(true);
        xml.setTemplateContext("templates/" + module + "/mapper.xml.btl");
        xml.setRelativePkgOrPath("resources/mapper/test");
        xml.setTempFileName("%sMapper.xml");
        TemplateConfig query = new TemplateConfig();
        query.setName("query");
        query.setFromFile(true);
        query.setTemplateContext("templates/" + module + "/query.java.btl");
        query.setRelativePkgOrPath("test.query");
        query.setTempFileName("%sQueryDTO.java");
        List<TemplateConfig> templateConfigs = new ArrayList<>();
        templateConfigs.add(entity);
        templateConfigs.add(mapper);
        templateConfigs.add(xml);
        templateConfigs.add(service);
        templateConfigs.add(serviceImpl);
        templateConfigs.add(controller);
        templateConfigs.add(query);
        return templateConfigs;
    }

    public static void main(String[] args) {
        handle();
    }

}
