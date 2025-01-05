package top.ticho.tool.generator.dbquery;

/**
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class MysqlDbQuery implements DbQuery {

    @Override
    public String tablesSql() {
        return "show table status";
    }

    @Override
    public String tableFieldsSql() {
        return "show full fields from `%s`";
    }

    @Override
    public String tableNameKey() {
        return "Name";
    }

    @Override
    public String tableCommentKey() {
        return "Comment";
    }

    @Override
    public String fieldNameKey() {
        return "Field";
    }

    @Override
    public String fieldTypeKey() {
        return "Type";
    }

    @Override
    public String fieldCommentKey() {
        return "Comment";
    }

    @Override
    public String indexKey() {
        return "Key";
    }

    @Override
    public String priKeyName() {
        return "PRI";
    }

    @Override
    public String defaultValue() {
        return "Default";
    }

    @Override
    public String nullable() {
        return "Null";
    }

    @Override
    public String nullableValue() {
        return "YES";
    }

}
