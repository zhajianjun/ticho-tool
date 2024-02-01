package top.ticho.tool.generator.engine;

import top.ticho.tool.generator.config.DataConfig;
import top.ticho.tool.generator.config.GlobalConfig;
import top.ticho.tool.generator.config.Summary;
import top.ticho.tool.generator.config.Table;
import top.ticho.tool.generator.config.TableField;
import top.ticho.tool.generator.config.TemplateConfig;
import top.ticho.tool.generator.convert.TypeConvert;
import top.ticho.tool.generator.dbquery.DbQuery;
import top.ticho.tool.generator.enums.DateType;
import top.ticho.tool.generator.enums.JavaType;
import top.ticho.tool.generator.exception.GeException;
import top.ticho.tool.generator.keywords.KeyWordsHandler;
import top.ticho.tool.generator.utils.BeetlUtil;
import top.ticho.tool.generator.utils.IoUtil;
import top.ticho.tool.generator.utils.ObjectUtil;
import top.ticho.tool.generator.utils.StrUtil;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.exception.BeetlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 执行引擎抽象类
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public abstract class AbstractExecuteEngine implements ExecuteEngine {
    private static final Logger log = LoggerFactory.getLogger(AbstractExecuteEngine.class);
    public static final String PREFIX = "/";
    public static final String TABLE = "table";
    public static final String COMMA = ", ";
    public static final String POINT = ".";
    public static final String PACKAGE = "package";
    public static final String CLASS_NAME = "className";
    public static final String KEY_NAME = "keyName";
    public static final String KEY_NAME_UF = "keyNameUF";
    public static final String KEY_NAME_LF = "keyNameLF";
    public static final String DEFAULT_PARENT_PACKAGE = "top.ticho";
    public static final String WINDOWS = "Windows";
    public static final String MAC = "Mac";
    public static final String AUTHOR = "author";
    public static final String DATE = "date";
    public static final String ENABLE_LOMBOK = "enableLombok";
    public static final String ENABLE_SWAGGER = "enableSwagger";
    public static final String ENABLE_MYBATIS_CACHE = "enableMybatisCache";
    public static final String ENABLE_MYBATIS_PLUS = "enableMybatisPlus";
    public static final String DEFAULT_KEY_NAME = "id";
    public static final String JAVA = "java";

    @Override
    public void startUp(Summary summary) {
        try {
            prepare(summary);
            execute(summary);
            complete(summary);
        } catch (Exception e) {
            log.error("执行引擎异常,{}", e.getMessage(), e);
            throw new GeException("执行引擎异常");
        }
    }

    /**
     * 进行预处理
     *
     * <p>
     * 参数检查和填充
     * </p>
     *
     * @param summary 汇总参数对象
     */
    public void prepare(Summary summary) {
        Map<String, Object> commonTemplateParams = summary.getCommonTemplateParams();
        GlobalConfig globalConfig = summary.getGlobalConfig();

        // 1."模板参数"如果是为null,注入空map
        if (commonTemplateParams == null) {
            commonTemplateParams = new HashMap<>(16);
            summary.setCommonTemplateParams(commonTemplateParams);
        }

        // 2.填充基本模板参数，没有值，则默认值
        setDefaultTemplateParams(commonTemplateParams);

        // 3.全局配置的"根包位置"为null设置默认值
        String parentPkg = globalConfig.getParentPkg();
        if (StrUtil.isBlank(parentPkg)) {
            parentPkg = DEFAULT_PARENT_PACKAGE;
            globalConfig.setParentPkg(parentPkg);
        }

        // 4.全局配置的"主键名称"设置默认值，后续如果查询到主键也会被覆盖掉
        String keyName = globalConfig.getKeyName();
        if (StrUtil.isEmpty(keyName)) {
            keyName = DEFAULT_KEY_NAME;
            globalConfig.setKeyName(keyName);
        }

        // 5.1模板配置校验
        Collection<TemplateConfig> templateConfigs = summary.getTemplateConfigs();
        if (templateConfigs == null || templateConfigs.isEmpty()) {
            throw new GeException("模版未配置");
        }
        // 5.2模板配置校验填充
        Set<String> templateNames = new HashSet<>();
        for (TemplateConfig templateConfig : templateConfigs) {
            String templateConfigName = templateConfig.getName();
            // ① 模板名称不为空
            if (StrUtil.isBlank(templateConfigName)) {
                throw new GeException("模版参数名称未填写");
            }
            // ② 模板名称重复则跳过
            if (templateNames.contains(templateConfigName)) {
                log.warn("模版参数名称为{}重复", templateConfigName);
                continue;
            } else {
                templateNames.add(templateConfigName);
            }

            // ③ 相对路径如果为空，默认为java包相对路径
            if (StrUtil.isBlank(templateConfig.getRelativePkgOrPath())) {
                log.warn("{}的relativePkgOrPath相对包路径或者文件未填写", templateConfig.getName());
                templateConfig.setNotPkg(false);
            }
            // ④ 如果是java包相对路径,则组装根包和相对包路径
            if (!templateConfig.isNotPkg()) {
                String relativePackagePath = templateConfig.getRelativePkgOrPath();
                String pkg = parentPkg + POINT + relativePackagePath;
                templateConfig.setRelativePkgOrPath(pkg);
            }
        }
    }

    // @formatter:off
    protected void execute(Summary summary) {

        List<Table> tables = getTables(summary, false, true);
        Map<String, Object> commonTemplateParams = summary.getCommonTemplateParams();
        GlobalConfig globalConfig = summary.getGlobalConfig();
        Collection<TemplateConfig> templateConfigList = summary.getTemplateConfigs();
        Map<String,Object> allTableTemplateParams = summary.getAllTableTemplateParams();
        boolean closeWriter = globalConfig.isCloseWriter();
        // 关闭输出流
        if (closeWriter) {
            log.warn("注意：已关闭输出流");
            globalConfig.setOpen(false);
        }
        for (Table table : tables) {
            Map<String, Object> classNameMap = new HashMap<>(templateConfigList.size());
            Map<String, Object> pkgMap = new HashMap<>(templateConfigList.size());
            for (TemplateConfig templateConfig : templateConfigList) {
                if (!templateConfig.isNotPkg()) {
                    String tempFileName = templateConfig.getTempFileName();
                    // %sServiceImpl.java -> %sServiceImpl
                    String tempClassName = tempFileName.substring(0, tempFileName.lastIndexOf(POINT));
                    // %sServiceImpl.java -> UserServiceImpl
                    String className = String.format(tempClassName, table.getEntityName());

                    classNameMap.put(templateConfig.getName(), className);
                    pkgMap.put(templateConfig.getName(), templateConfig.getRelativePkgOrPath());
                }
            }
            Map<String, Object> templateParams = new HashMap<>(commonTemplateParams);
            templateParams.put(PACKAGE, pkgMap);
            templateParams.put(CLASS_NAME, classNameMap);
            templateParams.put(TABLE, table);
            setDefaultKeyName(table.getKeyName(), globalConfig, templateParams);
            allTableTemplateParams.put(table.getEntityName(), templateParams);
            // 不写入文件流
            if (closeWriter) {
                continue;
            }
            for (TemplateConfig templateConfig : templateConfigList) {
                boolean isTemplate = templateConfig.isFromFile();
                GroupTemplate groupTemplate = BeetlUtil.getGroupTemplate(isTemplate);
                Template template = groupTemplate.getTemplate(templateConfig.getTemplateContext());
                // 验证模板是否存在
                BeetlException validate = template.validate();
                if (validate != null) {
                    // 一般是模板路径不对，不存在才会导致的问题。
                    String message = templateConfig.getName() + "模板加载异常, 路径" + templateConfig.getTemplateContext() + "," + validate.detailCode;
                    log.warn(message);
                }
                template.binding(templateParams);
                OutputStream out = getOutputStream(table, templateConfig, globalConfig);
                try {
                    if (out == null) {
                        continue;
                    }
                    template.renderTo(out);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    IoUtil.close(out);
                }
            }
        }
    }

    // @formatter:on

    /**
     * 任务结束处理
     *
     * @param summary 汇总参数对象
     */
    protected void complete(Summary summary) {

    }

    @Override
    public List<Table> getAllTables(Summary summary, boolean isGetTableField) {
        return getTables(summary, true, isGetTableField);
    }

    /**
     * 获取表信息
     * 执行sql获取表结构信息
     *
     * @param summary         汇总参数对象
     * @param isGetAllTables  是否获取所有表信息
     * @param isGetTableField 是否获取表属性信息
     * @return 表信息列表
     */
    public List<Table> getTables(Summary summary, boolean isGetAllTables, boolean isGetTableField) {
        DataConfig dataConfig = summary.getDataConfig();
        GlobalConfig globalConfig = summary.getGlobalConfig();
        List<String> tables = globalConfig.getTables();
        if (!isGetAllTables && ObjectUtil.isEmpty(tables)) {
            throw new GeException("全局配置中未配置表名信息");
        }
        DbQuery dbQuery = dataConfig.getDbQuery();
        List<Table> tableList = new ArrayList<>();
        try (Connection connection = dataConfig.getConnection(); PreparedStatement tableStatement = connection.prepareStatement(dbQuery.tablesSql());
             ResultSet tableResult = tableStatement.executeQuery()) {
            while (tableResult.next()) {
                String tableName = tableResult.getString(dbQuery.tableNameKey());
                if (!isGetAllTables && !tables.contains(tableName)) {
                    continue;
                }
                String entityName = getEntityName(globalConfig.getTablePrefixs(), tableName);
                String tableComment = tableResult.getString(dbQuery.tableCommentKey());

                Table table = new Table();
                tableList.add(table);
                table.setName(tableName);
                table.setComment(tableComment);
                table.setEntityName(entityName);
                if (!isGetTableField) {
                    continue;
                }
                // 填充 实体类字段属性
                setTableField(summary, table, connection);
            }
            return tableList;
        } catch (Exception e) {
            String message = "获取表信息失败！";
            log.error(message, e);
            throw new GeException(message);
        }
    }

    private String getEntityName(List<String> tablePrefixs, String tableName) {
        // 前缀
        String prefix = null;
        if (ObjectUtil.isNotEmpty(tablePrefixs)) {
            prefix = tablePrefixs.stream().filter(tableName::startsWith).findFirst().orElse(null);
        }
        if (Objects.nonNull(prefix)) {
            return StrUtil.underscoreToCamel(tableName.substring(prefix.length()));
        }
        return StrUtil.underscoreToCamel(tableName);
    }

    /**
     * 实体类字段属性填充
     *
     * @param summary    汇总参数对象
     * @param table      表结构信息
     * @param connection Connection
     * @throws SQLException sql异常
     */
    private void setTableField(Summary summary, Table table, Connection connection) throws SQLException {
        DataConfig dataConfig = summary.getDataConfig();
        GlobalConfig globalConfig = summary.getGlobalConfig();
        // 获取Java时间类型
        DateType dateType = globalConfig.getDateType();
        // 获取sql
        DbQuery dbQuery = dataConfig.getDbQuery();
        // 获取JavaDb转换器
        TypeConvert convert = dataConfig.getTypeConvert();
        // 获取关键字处理器
        KeyWordsHandler keyWordsHandler = dataConfig.getKeyWordsHandler();

        String tableName = table.getName();

        List<String> fieldNames = new ArrayList<>();
        List<TableField> fields = new ArrayList<>();
        Set<String> pkgs = new HashSet<>();
        String tableFieldsSql = String.format(dbQuery.tableFieldsSql(), tableName);
        try (PreparedStatement tableFieldStatement = connection.prepareStatement(tableFieldsSql);
             ResultSet tableFieldResult = tableFieldStatement.executeQuery()) {
            while (tableFieldResult.next()) {
                TableField tableField = new TableField();
                String fieldName = tableFieldResult.getString(dbQuery.fieldNameKey());
                String fieldType = tableFieldResult.getString(dbQuery.fieldTypeKey());
                String propertyUpperName = StrUtil.underscoreToCamel(fieldName);
                String propertyLowerName = StrUtil.underscoreToCamelLF(fieldName);
                String fieldComment = tableFieldResult.getString(dbQuery.fieldCommentKey());
                String index = tableFieldResult.getString(dbQuery.indexKey());
                boolean isPriKey = dbQuery.priKeyName().equals(index);
                JavaType javaType = convert.typeConvert(dateType, fieldType);
                String pkg = javaType.getPkg();

                if (isPriKey) {
                    table.setKeyName(fieldName);
                }

                if (!ObjectUtil.isEmpty(pkg)) {
                    pkgs.add(pkg);
                }
                String simpleName = fieldName;
                if (keyWordsHandler.isKeyWords(fieldName)) {
                    log.warn("当前表【{}】存在字段【{}】为数据库关键字或保留字!", tableName, fieldName);
                    fieldName = keyWordsHandler.formatColumn(fieldName);
                }
                tableField.setName(fieldName);
                tableField.setSimpleName(simpleName);
                tableField.setType(fieldType);
                tableField.setPropertyLowerName(propertyLowerName);
                tableField.setPropertyUpperName(propertyUpperName);
                tableField.setPropertyType(javaType.getType());
                tableField.setComment(fieldComment);
                tableField.setIndex(index);
                tableField.setPriKey(isPriKey);
                // sql server中可能会重复
                if (fieldNames.contains(fieldName)) {
                    continue;
                }
                fieldNames.add(fieldName);
                fields.add(tableField);
            }
        }

        table.setPkgs(pkgs);
        table.setFields(fields);
        table.setFieldNames(String.join(COMMA, fieldNames));
    }

    /**
     * 打开目录文件夹
     *
     * @param outPutDir 输出的目录文件夹
     */
    protected void open(String outPutDir) {
        try {
            String osName = System.getProperty("os.name");
            if (osName != null) {
                if (osName.contains(MAC)) {
                    Runtime.getRuntime().exec("open " + outPutDir);
                } else if (osName.contains(WINDOWS)) {
                    Runtime.getRuntime().exec("cmd /c start " + outPutDir);
                } else {
                    log.debug("文件输出目录: {}", outPutDir);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 主键信息填充到模板参数集合
     *
     * @param templateParams 模板参数
     */
    private void setDefaultKeyName(String keyName, GlobalConfig globalConfig, Map<String, Object> templateParams) {
        keyName = StrUtil.isNotBlank(keyName) ? keyName : globalConfig.getKeyName();
        keyName = StrUtil.isNotBlank(keyName) ? keyName : DEFAULT_KEY_NAME;
        // 转驼峰
        String keyNameUf = StrUtil.underscoreToCamel(keyName);
        String keyNameLf = StrUtil.underscoreToCamelLF(keyName);
        // sys_user_role
        templateParams.put(KEY_NAME, keyName);
        // SysUserRole
        templateParams.put(KEY_NAME_UF, keyNameUf);
        // sysUserRole
        templateParams.put(KEY_NAME_LF, keyNameLf);
    }

    /**
     * 填充基本模板参数
     *
     * @param templateParams 模板参数map集合
     */
    private void setDefaultTemplateParams(Map<String, Object> templateParams) {
        templateParams.putIfAbsent(AUTHOR, "");
        templateParams.putIfAbsent(DATE, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        templateParams.putIfAbsent(ENABLE_LOMBOK, false);
        templateParams.putIfAbsent(ENABLE_SWAGGER, false);
        templateParams.putIfAbsent(ENABLE_MYBATIS_CACHE, false);
        templateParams.putIfAbsent(ENABLE_MYBATIS_PLUS, false);
    }
}
