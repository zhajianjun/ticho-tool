package top.ticho.tool.generator.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据库类型
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Getter
@AllArgsConstructor
public enum DbType {
    /** MYSQL */
    MYSQL("mysql", "MySql数据库"),
    /** ORACLE */
    ORACLE("oracle", "Oracle11g及以下数据库(高版本推荐使用ORACLE_NEW)");

    /** 数据库名称 */
    private final String db;
    /** 描述 */
    private final String desc;

    public static DbType getDbType(String driverName) {
        if (driverName.contains("oracle")) {
            return DbType.ORACLE;
        }
        if (driverName.contains("mysql")) {
            return DbType.MYSQL;
        }
        return null;
    }

}
