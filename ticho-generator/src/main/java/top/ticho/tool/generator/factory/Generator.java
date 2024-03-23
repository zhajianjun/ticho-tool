package top.ticho.tool.generator.factory;

import ch.qos.logback.classic.util.ContextInitializer;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import top.ticho.tool.generator.config.DataConfig;
import top.ticho.tool.generator.config.GlobalConfig;
import top.ticho.tool.generator.config.Summary;
import top.ticho.tool.generator.config.Table;
import top.ticho.tool.generator.config.TemplateConfig;
import top.ticho.tool.generator.engine.AbstractExecuteEngine;
import top.ticho.tool.generator.engine.DefaultExecuteEngine;
import top.ticho.tool.generator.engine.JarExecuteEngine;
import top.ticho.tool.generator.enums.DateType;
import top.ticho.tool.generator.exception.GeException;
import top.ticho.tool.generator.utils.CustomUtil;
import top.ticho.tool.generator.utils.FileUtil;
import top.ticho.tool.generator.utils.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class Generator {
    static {
        // logback.xml放在config文件夹里，其它地方调用不会生效config下的logback.xml配置了
        System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, "config/logback.xml");
    }

    private static final Logger log = LoggerFactory.getLogger(Generator.class);
    public static final String DEFAULT = "default";
    public static final String BTL = ".btl";
    public static final String SHOWTABLES = "showtables";
    public static final String COMMON_CREATE_JSON_FILE = "common.createJsonFile";

    // @formatter:off

    public static void main(String[] args) {
        log.warn("启动");
        String projectPath = getProjectPath();
        Properties props = getProperties(projectPath);
        if (props == null) {
            log.error("配置文件不存在");
            return;
        }
        AbstractExecuteEngine engine;
        String dataPath = projectPath + File.separator + "data";
        String outPutDir = nullGetDefaultValue(props, "globalConfig.outPutDir", "");
        if (StrUtil.isBlank(outPutDir)) {
            outPutDir = dataPath;
            engine = new JarExecuteEngine();
        }else{
            outPutDir = outPutDir.replace("\\", File.separator);
            engine = new DefaultExecuteEngine();
        }
        Summary summary = new Summary();
        summary.setDataConfig(getDataConfig(props));
        summary.setGlobalConfig(getGlobalConfig(props, outPutDir));
        summary.setTemplateConfigs(getTemplateConfigs(props));
        summary.setCommonTemplateParams(getCommonTemplateParams(props));
        boolean tableFile = createAllTableFile(args, dataPath, summary, engine);
        if (tableFile) {
            log.warn("没有找到表信息");
            return;
        }
        engine.startUp(summary);
        createJsonFile(summary, dataPath, props );
    }

    public static String getProjectPath() {
        return System.getProperty("user.dir");
    }

    /**
     * 获取所有表信息
     */
    private static boolean createAllTableFile(String[] args, String path, Summary summary, AbstractExecuteEngine engine) {
        if (args.length >= 1 && SHOWTABLES.equals(args[0])) {
            List<Table> allTables = engine.getAllTables(summary, true);
            String context = JSONObject.toJSONString(allTables, SerializerFeature.PrettyFormat);
            String filePath = path + File.separator + "Tables.json";
            writeToFile(context, filePath);
            log.debug("所有表数据文件路径：{}" , filePath);
            return true;
        }
        return false;
    }

    /**
     * 创建模板参数文件
     *
     * @param summary 汇总参数对象
     * @param local 文件路径
     * @param props properties
     */
    private static void createJsonFile(Summary summary, String local, Properties props) {
        if (Boolean.parseBoolean(nullGetDefaultValue(props, COMMON_CREATE_JSON_FILE, Boolean.TRUE.toString()))) {
            Map<String, Object> allCommonMap = summary.getAllTableTemplateParams();
            allCommonMap.forEach((k, v) -> {
                String context = JSONObject.toJSONString(v, SerializerFeature.PrettyFormat);
                String filePath = local + File.separator + "ParamJson" + File.separator + k + "Params.json";
                writeToFile(context, filePath);
                log.info("模板参数文件路径：{}" , filePath);
            });
        }
    }

    public static void writeToFile(String context, String filePath) {
        FileUtil.checkFile(filePath);
        File file = new File(filePath);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(context.getBytes(StandardCharsets.UTF_8));
            fileOutputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    // @formatter:on

    public static List<TemplateConfig> getTemplateConfigs(Properties props) {
        // @formatter:off
        String module = props.getProperty("globalConfig.module");
        if (StrUtil.isBlank(module)) {
            module = DEFAULT;
        } else if (DEFAULT.equals(module)) {
            throw new GeException("与默认配置default冲突！");
        }
        String projectPath = getProjectPath();
        String templateDirPath = projectPath + File.separator + "templates" + File.separator + module;
        File templateDirFile = new File(templateDirPath);
        List<TemplateConfig> templateConfigs = new ArrayList<>();
        if (!templateDirFile.exists() || !templateDirFile.isDirectory()) {
            log.error(  "{}文件夹不存在或者不是文件夹！", templateDirFile);
            return templateConfigs;
        }
        File[] files = templateDirFile.listFiles();
        if (files == null) {
            log.error(  "{}路径下的模版文件不存在！", templateDirFile);
            return templateConfigs;
        }
        List<String> simpleFileNames = new ArrayList<>();
        for (File file : files) {
            String name = file.getName();
            // 忽略文件夹 或者 非模版文件，指非'.btl' 后缀名
            if (file.isDirectory() || !name.endsWith(BTL)) {
                continue;
            }
            // controller.java.btl => controller.java
            String fileName = name.substring(0, name.length() - BTL.length());
            // controller.java => java
            String extName = FileUtil.extName(fileName);
            // ontroller.java => controller
            int indexOf = fileName.indexOf(".");
            String simpleFileName = fileName.substring(0, indexOf);
            if (simpleFileNames.contains(simpleFileName)) {
                simpleFileName = simpleFileName + StrUtil.toUpperFirst(extName);
            }
            simpleFileNames.add(simpleFileName);
            boolean isNotPkg = !AbstractExecuteEngine.JAVA.equals(extName);
            TemplateConfig templateConfig = new TemplateConfig();
            templateConfig.setName(simpleFileName);
            templateConfig.setFromFile(true);
            templateConfig.setTemplateContext("templates/" + module + "/" + name);
            templateConfig.setNotPkg(isNotPkg);
            templateConfig.setRelativePkgOrPath(nullGetDefaultValue(props,"template." + simpleFileName + ".pkgOrPath", ""));
            templateConfig.setTempFileName("%s" + nullGetDefaultValue(props,"template." + simpleFileName + ".suffix", "") + "." + extName);
            templateConfigs.add(templateConfig);
        }
        return templateConfigs;
        // @formatter:on
    }

    /**
     * 获取模版参数
     *
     * @param props Properties配置
     * @return 模版参数
     */
    public static Map<String, Object> getCommonTemplateParams(Properties props) {
        // @formatter:off
        Map<String, Object> common = new HashMap<>(16);
        common.put("author", props.getProperty("common.author"));
        common.put("project", props.getProperty("common.project"));
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        common.put("date", nullGetDefaultValue(props, "common.date", date));
        common.put("enableLombok", Boolean.parseBoolean(nullGetDefaultValue(props,"common.enableLombok", "true")));
        common.put("enableSwagger", !Boolean.parseBoolean(nullGetDefaultValue(props,"common.enableSwagger", "true")));
        common.put("enableMybatisCache", !Boolean.parseBoolean(nullGetDefaultValue(props,"common.enableMybatisCache", "true")));
        common.put("enableMybatisPlus", !Boolean.parseBoolean(nullGetDefaultValue(props,"common.enableMybatisPlus", "true")));
        return common;
        // @formatter:on
    }

    /**
     * 获取数据库配置
     *
     * @param props Properties配置
     * @return 数据库配置
     */
    public static DataConfig getDataConfig(Properties props) {
        DataConfig dataConfig = new DataConfig();
        dataConfig.setUrl(props.getProperty("dataConfig.url"));
        dataConfig.setDriverName(props.getProperty("dataConfig.driverName"));
        dataConfig.setUsername(props.getProperty("dataConfig.username"));
        dataConfig.setPassword(props.getProperty("dataConfig.password"));
        return dataConfig;
    }

    /**
    * 获取全局配置
     *
    * @param props Properties配置
    * @param outPutDir 输出位置
     * @return 全局配置
    */
    private static GlobalConfig getGlobalConfig(Properties props, String outPutDir) {
        // @formatter:off
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setOpen(Boolean.parseBoolean(nullGetDefaultValue(props,"globalConfig.isOpen","true")));
        globalConfig.setFileOverride(Boolean.parseBoolean(nullGetDefaultValue(props,"globalConfig.fileOverride","false")));
        globalConfig.setCloseWriter(Boolean.parseBoolean(nullGetDefaultValue(props,"globalConfig.closeWriter","false")));
        globalConfig.setParentPkg(nullGetDefaultValue(props,"globalConfig.parent","top.ticho"));
        globalConfig.setDateType(DateType.get(nullGetDefaultValue(props,"globalConfig.dateType","TIME_PACK")));
        globalConfig.setOutPutDir(outPutDir);
        globalConfig.setTables(Arrays.asList(nullGetDefaultValue(props,"globalConfig.tables","").split(",")));
        globalConfig.setTablePrefixs(Arrays.asList(nullGetDefaultValue(props,"globalConfig.tablePrefixs","").split(",")));
        globalConfig.setKeyName(props.getProperty("globalConfig.keyName"));
        return globalConfig;
        // @formatter:on
    }


    /**
    * 获取Properties配置
     *
    * @param projectPath 文件根路径
    * @return Properties
    */
    public static Properties getProperties(String projectPath) {
        String prefix = projectPath + File.separator;
        Properties properties = CustomUtil.getProperties(prefix + "config.properties");
        if (properties != null) {
            return properties;
        }
        properties = CustomUtil.getYml(prefix + "config.yml");
        return properties;
    }


    public static String nullGetDefaultValue(Properties props, String key, String defaultValue) {
        String value = props.getProperty(key);
        if (StrUtil.isBlank(value)) {
            return defaultValue;
        }
        return value;
    }

}
