package top.ticho.tool.generator.convert;

import top.ticho.tool.generator.enums.DbType;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class TypeConvertRegistry {

    private final Map<DbType, TypeConverter> type_convert_enum_map = new EnumMap<>(DbType.class);

    public TypeConvertRegistry() {
        this.type_convert_enum_map.put(DbType.MYSQL, new MySqlTypeConverter());
    }

    public TypeConverter getTypeConvert(DbType dbType) {
        return this.type_convert_enum_map.get(dbType);
    }

}
