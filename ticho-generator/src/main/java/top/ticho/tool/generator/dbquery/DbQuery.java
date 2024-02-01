package top.ticho.tool.generator.dbquery;

/**
 * sql执行
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public interface DbQuery {

    /**
     * 查询表sql
     * @return String
     */
    String tablesSql();

    /**
     * 查询表字段sql
     * @return String
     */
    String tableFieldsSql();

    /**
     * 查询表sql
     * @return String
     */
    String tableNameKey();

    /**
     * 表描叙key
     * @return String
     */
    String tableCommentKey();

    /**
     * 表字段key
     * @return String
     */
    String fieldNameKey();

    /**
     * 表字段类型key
     * @return String
     */
    String fieldTypeKey();

    /**
     * 表字段描叙key
     * @return String
     */
    String fieldCommentKey();

    /**
     * 索引key
     * @return String
     */
    String indexKey();

    /**
     * 主键索引名称
     *
     * @return String
     */
    String priKeyName();
}
