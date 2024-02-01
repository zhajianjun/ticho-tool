package top.ticho.tool.generator.config;

import top.ticho.tool.generator.convert.TypeConvert;
import top.ticho.tool.generator.convert.TypeConvertRegistry;
import top.ticho.tool.generator.dbquery.DbQuery;
import top.ticho.tool.generator.dbquery.DbQueryRegistry;
import top.ticho.tool.generator.enums.DbType;
import top.ticho.tool.generator.exception.GeException;
import top.ticho.tool.generator.keywords.KeyWordsHandler;
import top.ticho.tool.generator.keywords.KeyWordsRegistrey;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 数据源配置
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Data
public class DataConfig {
    public static final Logger log = LoggerFactory.getLogger(DataConfig.class);

    /**
     * 地址
     */
    private String url;
    /**
     * 驱动
     */
    private String driverName;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;

    /**
     * 数据库类型
     */
    private DbType dbType;

    /**
     * sql执行器
     */
    private DbQuery dbQuery;

    /**
     * Java类型
     */
    private TypeConvert typeConvert;

    /**
     * 关键字处理
     */
    private KeyWordsHandler keyWordsHandler;

    public DbQuery getDbQuery() {
        if (null != dbQuery) {
            return dbQuery;
        }
        DbType dbType = getDbType();
        DbQueryRegistry dbQueryRegistry = new DbQueryRegistry();
        DbQuery dbQuery = dbQueryRegistry.getDbQuery(dbType);
        dbQuery = Optional.ofNullable(dbQuery).orElseGet(() -> dbQueryRegistry.getDbQuery(DbType.MYSQL));
        return dbQuery;
    }

    public TypeConvert getTypeConvert() {
        if (null != typeConvert) {
            return typeConvert;
        }
        DbType dbType = getDbType();
        TypeConvertRegistry typeConvertRegistry = new TypeConvertRegistry();
        TypeConvert typeConvert = typeConvertRegistry.getTypeConvert(dbType);
        this.typeConvert = Optional.ofNullable(typeConvert).orElseGet(() -> typeConvertRegistry.getTypeConvert(DbType.MYSQL));
        return this.typeConvert;
    }

    public KeyWordsHandler getKeyWordsHandler() {
        if (null != keyWordsHandler) {
            return keyWordsHandler;
        }
        DbType dbType = getDbType();
        KeyWordsRegistrey keyWordsRegistrey = new KeyWordsRegistrey();
        KeyWordsHandler keyWordsHandler = keyWordsRegistrey.getKeyWordsHandler(dbType);
        keyWordsHandler = Optional.ofNullable(keyWordsHandler).orElseGet(() -> keyWordsRegistrey.getKeyWordsHandler(DbType.MYSQL));
        return keyWordsHandler;
    }

    public Connection getConnection() {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            String message = "加载数据库驱动失败！未找到驱动类";
            log.error(message, e);
            throw new GeException(message);
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Connection result;
        // // 真正的任务在这里执行，这里的返回值类型为String，可
        FutureTask<Connection> future = new FutureTask<>(() -> DriverManager.getConnection(url, username, password));
        executor.execute(future);
        // 在这里可以做别的任何事情
        try {
            // 取得结果，同时设置超时执行时间为5秒。同样可以用future.get()，不设置执行超时时间取得结果
            result = future.get(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            String extracted = extracted(e, future);
            throw new GeException(extracted);
        } catch (ExecutionException | TimeoutException e) {
            String extracted = extracted(e, future);
            throw new GeException(extracted);
        } finally {
            executor.shutdown();
        }
        return result;
    }

    private String extracted(Exception e, FutureTask<Connection> future) {
        future.cancel(true);
        String message = "获取连接失败！";
        if (e instanceof TimeoutException) {
            message = "连接超时。" + message;
        }
        log.error(message, e);
        return message;
    }

    private DbType getDbType() {
        if (dbType != null) {
            return dbType;
        }
        if ((dbType = getDbType(driverName)) != null) {
            return dbType;
        }
        if ((dbType = getDbType(url.toLowerCase())) != null) {
            return dbType;
        }
        throw new GeException("未知数据库类型");
    }

    private DbType getDbType(String driverName) {
        if (driverName.contains("oracle")) {
            return DbType.ORACLE;
        }
        return DbType.MYSQL;
    }
}
