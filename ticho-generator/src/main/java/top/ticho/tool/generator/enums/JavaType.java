package top.ticho.tool.generator.enums;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Java类型枚举类
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public enum JavaType {
    /**
     *
     */
    BASE_BYTE("byte", null),
    BASE_SHORT("short", null),
    BASE_CHAR("char", null),
    BASE_INT("int",null),
    BASE_LONG("long",null),
    BASE_FLOAT("float",null),
    BASE_DOUBLE("double",null),
    BASE_BOOLEAN("boolean",null),
    BYTE("Byte",null),
    SHORT("Short",null),
    CHARACTER("Character",null),
    INTEGER("Integer",null),
    LONG("Long",null),
    FLOAT("Float",null),
    DOUBLE("Double",null),
    BOOLEAN("Boolean",null),
    STRING("String",null),
    DATE_SQL("Date", "java.sql.Date"),
    TIME("Time", "java.sql.Time"),
    TIMESTAMP("Timestamp", "java.sql.Timestamp"),
    BLOB("Blob", "java.sql.Blob"),
    CLOB("Clob", "java.sql.Clob"),
    LOCAL_DATE("LocalDate", "java.time.LocalDate"),
    LOCAL_TIME("LocalTime", "java.time.LocalTime"),
    YEAR("Year", "java.time.Year"),
    YEAR_MONTH("YearMonth", "java.time.YearMonth"),
    LOCAL_DATE_TIME("LocalDateTime", "java.time.LocalDateTime"),
    INSTANT("Instant", "java.time.Instant"),
    BYTE_ARRAY("byte[]",null),
    OBJECT("Object",null),
    DATE("Date", "java.util.Date"),
    BIG_INTEGER("BigInteger", "java.math.BigInteger"),
    BIG_DECIMAL("BigDecimal", "java.math.BigDecimal");

    /**
     * java类型
     */
    private final String type;
    /**
     * java类型全路径
     */
    private final String pkg;

    JavaType(String type, String pkg) {
        this.type = type;
        this.pkg = pkg;
    }

    public String getType() {
        return type;
    }

    public String getPkg() {
        return pkg;
    }

    public Map<String, String> getMap() {
        return Stream.of(values()).collect(Collectors.toMap(JavaType::getType, JavaType::getPkg));
    }
}
