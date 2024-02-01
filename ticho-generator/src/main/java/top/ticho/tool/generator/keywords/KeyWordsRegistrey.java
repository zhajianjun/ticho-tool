package top.ticho.tool.generator.keywords;

import top.ticho.tool.generator.enums.DbType;

import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class KeyWordsRegistrey {
    private final Map<DbType, KeyWordsHandler> key_words_enum_map = new EnumMap<>(DbType.class);

    public KeyWordsRegistrey() {
        this.key_words_enum_map.put(DbType.MYSQL, new MySqlKeyWordsHandler());
    }

    public KeyWordsRegistrey(DbType dbType, KeyWordsHandler keyWordsHandler) {
        this.key_words_enum_map.putIfAbsent(dbType, keyWordsHandler);
    }

    public KeyWordsHandler getKeyWordsHandler(DbType dbType) {
        return this.key_words_enum_map.get(dbType);
    }
}
