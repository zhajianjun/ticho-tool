package top.ticho.tool.generator.enums;

import java.util.stream.Stream;

/**
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public enum DateType {
    /** java.util.Date */
    ONLY_DATE,
    /** java.sql.Date、java.sql.Time、java.sql.Timestamp */
    SQL_PACK,
    /** java.time.LocalDate、java.time.LocalTime */
    TIME_PACK;

    public static DateType get(String name) {
        return Stream.of(values()).filter(x -> x.name().equalsIgnoreCase(name)).findAny().orElse(DateType.TIME_PACK);
    }

}
