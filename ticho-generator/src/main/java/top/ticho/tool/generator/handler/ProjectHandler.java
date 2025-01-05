package top.ticho.tool.generator.handler;

import lombok.extern.slf4j.Slf4j;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.exception.BeetlException;
import top.ticho.tool.generator.config.DataSourceConfig;
import top.ticho.tool.generator.config.FileTemplateConfig;
import top.ticho.tool.generator.config.GlobalConfig;
import top.ticho.tool.generator.config.ProjectConfig;
import top.ticho.tool.generator.constant.CommConst;
import top.ticho.tool.generator.convert.TypeConvertRegistry;
import top.ticho.tool.generator.convert.TypeConverter;
import top.ticho.tool.generator.dbquery.DbQuery;
import top.ticho.tool.generator.dbquery.DbQueryRegistry;
import top.ticho.tool.generator.entity.FileTemplate;
import top.ticho.tool.generator.entity.Table;
import top.ticho.tool.generator.entity.TableField;
import top.ticho.tool.generator.enums.DbType;
import top.ticho.tool.generator.enums.JavaType;
import top.ticho.tool.generator.exception.GenerateException;
import top.ticho.tool.generator.keywords.KeyWordsHandler;
import top.ticho.tool.generator.keywords.KeyWordsRegistrey;
import top.ticho.tool.generator.keywords.MySqlKeyWordsHandler;
import top.ticho.tool.generator.util.AssertUtil;
import top.ticho.tool.generator.util.BeetlUtil;
import top.ticho.tool.generator.util.FileUtil;
import top.ticho.tool.generator.util.ObjUtil;
import top.ticho.tool.generator.util.StrUtil;
import top.ticho.tool.json.util.TiJsonUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 数据源配置
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ProjectHandler {

    /** 全局配置 */
    private final GlobalConfig globalConfig;
    /** 数据源配置 */
    private final DataSourceConfig dataSourceConfig;
    /** 项目配置 */
    private final ProjectConfig projectConfig;

    /** env */
    private final String env;
    /** sql执行器 */
    private final DbQuery dbQuery;
    /** Java类型 */
    private final TypeConverter typeConverter;
    /** 关键字处理 */
    private final KeyWordsHandler keyWordsHandler;
    /** 文件模板 */
    private final List<FileTemplate> fileTemplates;
    /**  */
    private final GroupTemplate groupTemplate;

    public ProjectHandler(GlobalConfig globalConfig, ProjectConfig projectConfig, DataSourceConfig dataSourceConfig, Map<String, FileTemplateConfig> fileTemplateConfigMap, String env) {
        AssertUtil.isTrue(Objects.nonNull(globalConfig), "全局配置[globalConfig]不能为空");
        AssertUtil.isTrue(Objects.nonNull(dataSourceConfig), "数据源配置[dataSourceConfig]不能为空");
        AssertUtil.isTrue(Objects.nonNull(projectConfig), "项目配置[projectConfig]不能为空");
        AssertUtil.isTrue(ObjUtil.isNotEmpty(fileTemplateConfigMap), "模板配置[fileTemplateConfigs]不能为空");
        DbType dbType = Optional.ofNullable(DbType.getDbType(dataSourceConfig.getDriverName())).orElse(DbType.MYSQL);
        this.globalConfig = globalConfig;
        this.dataSourceConfig = dataSourceConfig;
        this.projectConfig = projectConfig;
        this.groupTemplate = BeetlUtil.getGroupTemplate();
        this.env = env;
        this.dbQuery = getDbQuery(dbType);
        this.typeConverter = getTypeConverter(dbType);
        this.keyWordsHandler = getKeyWordsHandler(dbType);
        this.fileTemplates = getFileTemplates(fileTemplateConfigMap);
    }

    private List<FileTemplate> getFileTemplates(Map<String, FileTemplateConfig> fileTemplateConfigMap) {
        String templatePath = projectConfig.getTemplatePath();
        AssertUtil.isTrue(StrUtil.isNotBlank(templatePath), "模板路径[templatePath]不能为空");
        String templateDirPath = CommConst.TEMPLATE_PATH + File.separator + templatePath;
        File templateDirFile = new File(templateDirPath);
        if (!templateDirFile.exists() || !templateDirFile.isDirectory()) {
            log.warn("[{}]模板文件目录不存在！", templateDirFile);
            return Collections.emptyList();
        }
        File[] files = templateDirFile.listFiles();
        if (Objects.isNull(files) || files.length == 0) {
            log.error("[{}]模版文件目录为空！", templateDirFile);
            return Collections.emptyList();
        }
        return Arrays.stream(files)
            .filter(file -> fileTemplateConfigMap.containsKey(file.getName()))
            .map(file -> getFileTemplate(file, fileTemplateConfigMap))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private KeyWordsHandler getKeyWordsHandler(DbType dbType) {
        KeyWordsRegistrey keyWordsRegistrey = new KeyWordsRegistrey();
        return Optional.ofNullable(keyWordsRegistrey.getKeyWordsHandler(dbType)).orElseGet(MySqlKeyWordsHandler::new);
    }

    private TypeConverter getTypeConverter(DbType dbType) {
        TypeConvertRegistry typeConvertRegistry = new TypeConvertRegistry();
        TypeConverter typeConverter = typeConvertRegistry.getTypeConvert(dbType);
        return Optional.ofNullable(typeConverter).orElseGet(() -> typeConvertRegistry.getTypeConvert(DbType.MYSQL));
    }

    private DbQuery getDbQuery(DbType dbType) {
        DbQueryRegistry dbQueryRegistry = new DbQueryRegistry();
        DbQuery dbQuery = dbQueryRegistry.getDbQuery(dbType);
        return Optional.ofNullable(dbQuery).orElseGet(() -> dbQueryRegistry.getDbQuery(DbType.MYSQL));
    }

    private FileTemplate getFileTemplate(File file, Map<String, FileTemplateConfig> fileTemplateConfigMap) {
        // entity.java.btl
        String templateFileName = file.getName();
        String[] split = templateFileName.split(Pattern.quote(CommConst.DOT));
        if (split.length != 3 || !Objects.equals(split[2], CommConst.TEMPLATE_FILE_EXT_NAME)) {
            log.warn("模板文件[{}],格式不正确，请检查！正确格式为[*.*.btl]", templateFileName);
            return null;
        }
        FileTemplateConfig fileTemplateConfig = fileTemplateConfigMap.get(templateFileName);
        FileTemplate fileTemplate = new FileTemplate();
        fileTemplate.setTemplateFileName(templateFileName);
        fileTemplate.setKey(split[0]);
        fileTemplate.setAddToJavaDir(Objects.equals(split[1], CommConst.JAVA_FILE_EXT_NAME));
        fileTemplate.setSuffix(StrUtil.emptyDefault(fileTemplateConfig.getSuffix(), CommConst.EMPTY));
        fileTemplate.setContent(FileUtil.readString(file));
        fileTemplate.setLowerFirstFileName(Boolean.TRUE.equals(fileTemplateConfig.getLowerFirstFileName()));
        fileTemplate.setExtName(split[1]);
        fileTemplate.setFileAppend(Boolean.TRUE.equals(projectConfig.getFileAppend()));
        setFilePath(fileTemplateConfig, fileTemplate);
        log.debug("模板文件[{}]解析完成，根路径[{}]", templateFileName, file.getAbsolutePath());
        return fileTemplate;
    }

    protected void setFilePath(FileTemplateConfig fileTemplateConfig, FileTemplate fileTemplate) {
        String relativePath = fileTemplateConfig.getRelativePath();
        String outPutDir = projectConfig.getOutPutDir();
        StringJoiner joiner = new StringJoiner(File.separator);
        boolean fileAppend = fileTemplate.getFileAppend();
        if (StrUtil.isBlank(outPutDir)) {
            joiner.add(CommConst.PROJECT_PATH + File.separator + "data");
            joiner.add(env);
            joiner.add(fileTemplate.getKey());
            if (Boolean.TRUE.equals(fileTemplate.getAddToJavaDir())) {
                fileTemplate.setPackagePath(projectConfig.getParentPackage() + CommConst.DOT + relativePath);
            }
            fileTemplate.setRenderFilePath(joiner + File.separator + "%s" + fileTemplate.getSuffix() + CommConst.DOT + fileTemplate.getExtName());
        } else if (fileAppend) {
            joiner.add(CommConst.PROJECT_PATH + File.separator + "data");
            joiner.add(env);
            if (Boolean.TRUE.equals(fileTemplate.getAddToJavaDir())) {
                fileTemplate.setPackagePath(projectConfig.getParentPackage() + CommConst.DOT + relativePath);
            }
            fileTemplate.setRenderFilePath(joiner + File.separator + fileTemplate.getKey() + CommConst.DOT + fileTemplate.getExtName());
        } else {
            joiner.add(outPutDir.replace("\\", File.separator));
            joiner.add("src");
            joiner.add("main");
            if (Boolean.TRUE.equals(fileTemplate.getAddToJavaDir())) {
                joiner.add("java");
                relativePath = projectConfig.getParentPackage() + CommConst.DOT + relativePath;
                fileTemplate.setPackagePath(relativePath);
            }
            // 去除"."和文件前缀的"/"
            relativePath = relativePath.replaceAll("\\.", "/");
            if (relativePath.startsWith("/")) {
                relativePath = relativePath.replaceFirst("/", CommConst.EMPTY);
            }
            relativePath = relativePath.replace("/", File.separator);
            joiner.add(relativePath);
            fileTemplate.setRenderFilePath(joiner + File.separator + "%s" + fileTemplate.getSuffix() + CommConst.DOT + fileTemplate.getExtName());
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dataSourceConfig.getUrl(), dataSourceConfig.getUsername(), dataSourceConfig.getPassword());
    }

    public Connection getSafeConnection() {
        try {
            Class.forName(dataSourceConfig.getDriverName());
        } catch (ClassNotFoundException e) {
            String message = "加载数据库驱动失败！未找到驱动类";
            log.error(message, e);
            throw new GenerateException(message);
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Connection connection;
        // 真正的任务在这里执行，这里的返回值类型为String，可
        FutureTask<Connection> future = new FutureTask<>(this::getConnection);
        executor.execute(future);
        // 在这里可以做别的任何事情
        try {
            // 取得结果，同时设置超时执行时间为10秒。同样可以用future.get()，不设置执行超时时间取得结果
            connection = future.get(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            String extracted = getErrorMessage(e, future);
            throw new GenerateException(extracted);
        } catch (ExecutionException | TimeoutException e) {
            String errorMessage = getErrorMessage(e, future);
            throw new GenerateException(errorMessage);
        } finally {
            executor.shutdown();
        }
        return connection;
    }

    private String getErrorMessage(Exception e, FutureTask<Connection> future) {
        future.cancel(true);
        String message = "获取连接失败！";
        if (e instanceof TimeoutException) {
            message = "连接超时。" + message;
        }
        return message;
    }

    public List<Table> getTables() {
        List<String> tableNames = projectConfig.getTables();
        List<Table> tables = new ArrayList<>();
        try (
            Connection connection = getSafeConnection();
            // 使用参数化查询
            PreparedStatement tableStatement = connection.prepareStatement(dbQuery.tablesSql());
            ResultSet tableResult = tableStatement.executeQuery()
        ) {
            while (tableResult.next()) {
                String tableName = tableResult.getString(dbQuery.tableNameKey());
                if (!tableNames.contains(tableName)) {
                    continue;
                }
                String tableComment = tableResult.getString(dbQuery.tableCommentKey());
                Table table = new Table();
                tables.add(table);
                table.setName(tableName);
                table.setComment(tableComment);
                table.setEntityName(getEntityName(projectConfig.getTablePrefixs(), tableName));
                table.setEntityLowerName(StrUtil.toUpperFirst(table.getEntityName()));
                // 填充 实体类字段属性
                setTableField(table, connection);
            }
            return tables;
        } catch (Exception e) {
            throw new GenerateException("获取表信息失败！", e);
        }
    }

    private String getEntityName(List<String> tablePrefixs, String tableName) {
        // 前缀
        String prefix = null;
        if (ObjUtil.isNotEmpty(tablePrefixs)) {
            prefix = tablePrefixs.stream().filter(tableName::startsWith).findFirst().orElse(null);
        }
        if (Objects.nonNull(prefix)) {
            return StrUtil.toCamelUF(tableName.substring(prefix.length()));
        }
        return StrUtil.toCamelUF(tableName);
    }

    /**
     * 实体类字段属性填充
     *
     * @param table      表结构信息
     * @param connection Connection
     * @throws SQLException sql异常
     */
    private void setTableField(Table table, Connection connection) throws SQLException {
        String tableName = table.getName();
        List<String> fieldNames = new ArrayList<>();
        List<TableField> fields = new ArrayList<>();
        List<String> imports = new ArrayList<>();
        String tableFieldsSql = String.format(dbQuery.tableFieldsSql(), tableName);
        try (
            PreparedStatement tableFieldStatement = connection.prepareStatement(tableFieldsSql);
            ResultSet tableFieldResult = tableFieldStatement.executeQuery()
        ) {
            while (tableFieldResult.next()) {
                TableField tableField = new TableField();
                String fieldName = tableFieldResult.getString(dbQuery.fieldNameKey());
                String fieldType = tableFieldResult.getString(dbQuery.fieldTypeKey());
                String propertyUpperName = StrUtil.toCamelUF(fieldName);
                String propertyLowerName = StrUtil.toCamelLF(fieldName);
                String fieldComment = tableFieldResult.getString(dbQuery.fieldCommentKey());
                String index = tableFieldResult.getString(dbQuery.indexKey());
                String defaultValue = tableFieldResult.getString(dbQuery.defaultValue());
                String nullable = tableFieldResult.getString(dbQuery.nullable());
                boolean isPriKey = dbQuery.priKeyName().equals(index);
                boolean isNullable = dbQuery.nullableValue().equals(nullable);
                JavaType javaType = typeConverter.typeConvert(projectConfig.getDateType(), fieldType);
                if (isPriKey) {
                    table.setKeyName(fieldName);
                }
                if (!ObjUtil.isEmpty(javaType.getPkg()) && !imports.contains(javaType.getPkg())) {
                    imports.add(javaType.getPkg());
                }
                String simpleName = fieldName;
                if (keyWordsHandler.isKeyWords(fieldName)) {
                    log.debug("表[{}]存在字段[{}]为数据库关键字或保留字!", tableName, fieldName);
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
                tableField.setDefaultValue(defaultValue);
                tableField.setNullable(isNullable);
                tableField.setNullableValue(nullable);
                // sql server中可能会重复
                if (fieldNames.contains(fieldName)) {
                    continue;
                }
                fieldNames.add(fieldName);
                fields.add(tableField);
            }
        }
        table.setImports(imports);
        table.setFields(fields);
        table.setFieldNames(String.join(", ", fieldNames));
    }

    public void handle() {
        if (ObjUtil.isEmpty(fileTemplates)) {
            log.warn("无可用的模板");
            return;
        }
        List<Table> tables = getTables();
        boolean fileOverride = Boolean.TRUE.equals(projectConfig.getFileOverride());
        for (Table table : tables) {
            Map<String, Object> templateParams = new HashMap<>(globalConfig.getGlobalParams());
            templateParams.putAll(projectConfig.getCustomParams());
            Map<String, Object> classNameMap = new HashMap<>(fileTemplates.size());
            Map<String, Object> pkgMap = new HashMap<>(fileTemplates.size());
            for (FileTemplate fileTemplate : fileTemplates) {
                if (fileTemplate.getAddToJavaDir()) {
                    String className = table.getEntityName() + fileTemplate.getSuffix();
                    classNameMap.put(fileTemplate.getKey(), className);
                    pkgMap.put(fileTemplate.getKey(), fileTemplate.getPackagePath());
                }
            }
            templateParams.put(CommConst.PACKAGE, pkgMap);
            templateParams.put(CommConst.CLASS_NAME, classNameMap);
            templateParams.put(CommConst.TABLE, table);
            templateParams.put(CommConst.DATE, globalConfig.getDate());
            setDefaultKeyName(table.getKeyName(), templateParams);
            for (FileTemplate fileTemplate : fileTemplates) {
                String filePath = String.format(fileTemplate.getRenderFilePath(), table.getEntityName());
                boolean fileAppend = fileTemplate.getFileAppend();
                File file = new File(filePath);
                if (file.exists() && !fileOverride && !fileAppend) {
                    log.info("文件已存在，跳过生成：{}", filePath);
                    continue;
                }
                FileUtil.checkFile(file);
                Template template = groupTemplate.getTemplate(fileTemplate.getContent());
                template.binding(templateParams);
                // 验证模板是否存在
                BeetlException validate = template.validate();
                if (validate != null) {
                    // 一般是模板路径不对，不存在才会导致的问题。
                    String message = String.format("模板[%s]加载异常, %s", fileTemplate.getTemplateFileName(), validate.detailCode);
                    log.warn(message);
                }
                try (FileOutputStream out = new FileOutputStream(file, fileAppend)) {
                    template.renderTo(out);
                    log.debug("文件生成成功[{}]", file.getAbsolutePath());
                } catch (Exception e) {
                    String message = String.format("模板[%s]渲染异常。%s", fileTemplate.getTemplateFileName(), e.getMessage());
                    if (globalConfig.getIgnoreError()) {
                        log.warn(message);
                        continue;
                    }
                    throw new GenerateException(message);
                }
            }
            createParamJsonFile(templateParams);
        }
    }

    private void createParamJsonFile(Map<String, Object> templateParams) {
        String paramJsonPath = CommConst.PROJECT_PATH + File.separator + CommConst.DATA_PATH
            + File.separator + env + File.separator + CommConst.JSON_FILE_NAME;
        File file = new File(paramJsonPath);
        FileUtil.checkFile(file);
        try (FileOutputStream out = new FileOutputStream(file)) {
            String json = TiJsonUtil.toJsonStringPretty(templateParams);
            out.write(json.getBytes());
            log.debug("参数文件生成成功[{}]", file.getAbsolutePath());
        } catch (Exception e) {
            String message = String.format("参数文件生成异常。%s", e.getMessage());
            if (globalConfig.getIgnoreError()) {
                log.warn(message);
                return;
            }
            throw new GenerateException(message);
        }
    }

    private void setDefaultKeyName(String keyName, Map<String, Object> templateParams) {
        keyName = StrUtil.isNotBlank(keyName) ? keyName : projectConfig.getKeyName();
        keyName = StrUtil.isNotBlank(keyName) ? keyName : CommConst.DEFAULT_KEY_NAME;
        // 转驼峰
        String keyNameUf = StrUtil.toCamelUF(keyName);
        String keyNameLf = StrUtil.toCamelLF(keyName);
        // sys_user_role
        templateParams.put(CommConst.KEY_NAME, keyName);
        // SysUserRole
        templateParams.put(CommConst.KEY_NAME_UF, keyNameUf);
        // sysUserRole
        templateParams.put(CommConst.KEY_NAME_LF, keyNameLf);
    }

}
