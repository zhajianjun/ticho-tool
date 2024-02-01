package top.ticho.tool.generator.enums;

/**
 * 数据库类型
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public enum DbType {
    /**
     * MYSQL
     */
    MYSQL("mysql", "MySql数据库"),
    /**
     * ORACLE
     */
    ORACLE("oracle", "Oracle11g及以下数据库(高版本推荐使用ORACLE_NEW)");

    /**
     * 数据库名称
     */
    private final String db;
    /**
     * 描述
     */
    private final String desc;

    public String getDb() {
        return db;
    }

    public String getDesc() {
        return desc;
    }

    DbType(String db, String desc) {
        this.db = db;
        this.desc = desc;
    }
}
