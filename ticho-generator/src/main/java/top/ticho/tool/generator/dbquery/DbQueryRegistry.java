package top.ticho.tool.generator.dbquery;

import top.ticho.tool.generator.enums.DbType;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class DbQueryRegistry {
    private final Map<DbType, DbQuery> db_query_enum_map = new EnumMap<>(DbType.class);

    public DbQueryRegistry() {
        this.db_query_enum_map.put(DbType.ORACLE, new OracleDbQuery());
        this.db_query_enum_map.put(DbType.MYSQL, new MysqlDbQuery());
    }

    public DbQuery getDbQuery(DbType dbType) {
        return this.db_query_enum_map.get(dbType);
    }

}
