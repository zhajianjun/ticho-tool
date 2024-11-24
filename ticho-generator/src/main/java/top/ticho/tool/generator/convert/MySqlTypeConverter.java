package top.ticho.tool.generator.convert;

import top.ticho.tool.generator.enums.DateType;
import top.ticho.tool.generator.enums.JavaType;

/**
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class MySqlTypeConverter implements TypeConverter {

    /**
     * java类型
     *
     * @param dbType db类型
     * @return java类型
     */
    @Override
    public JavaType typeConvert(DateType dateType, String dbType) {
        String typeLower = dbType.toLowerCase();
        if (typeLower.contains("char")) {
            return JavaType.STRING;
        } else if (typeLower.contains("bigint")) {
            // 此处本来应为Long，但是前段对于超大数字是要丢失精度的，只有改为字符串才可正常显示
            return JavaType.LONG;
            // } else if (typeLower.contains("tinyint(1)")) {
            //     return DbColumnMapping.BOOLEAN;
        } else if (typeLower.contains("tinyint(1)")) {
            return JavaType.INTEGER;
        } else if (typeLower.contains("int")) {
            return JavaType.INTEGER;
        } else if (typeLower.contains("text")) {
            return JavaType.STRING;
        } else if (typeLower.contains("bit")) {
            return JavaType.BOOLEAN;
        } else if (typeLower.contains("decimal")) {
            return JavaType.BIG_DECIMAL;
        } else if (typeLower.contains("clob")) {
            return JavaType.CLOB;
        } else if (typeLower.contains("blob")) {
            return JavaType.BLOB;
        } else if (typeLower.contains("binary")) {
            return JavaType.BYTE_ARRAY;
        } else if (typeLower.contains("float")) {
            return JavaType.FLOAT;
        } else if (typeLower.contains("double")) {
            return JavaType.DOUBLE;
        } else if (!typeLower.contains("json") && !typeLower.contains("enum")) {
            return getDatepropertyType(dateType, typeLower);
        } else {
            return JavaType.STRING;
        }
    }

    private JavaType getDatepropertyType(DateType dateType, String type) {
        String year = "year";
        String time = "time";
        String date = "date";
        if (type.contains(date) || type.contains(time) || type.contains(year)) {
            byte b;
            switch (dateType) {
                case ONLY_DATE:
                    return JavaType.DATE;
                case SQL_PACK:
                    b = getType(type, year, time, date, (byte) -1);
                    switch (b) {
                        case 0:
                        case 2:
                            return JavaType.DATE_SQL;
                        case 1:
                            return JavaType.TIME;
                        default:
                            return JavaType.TIMESTAMP;
                    }
                case TIME_PACK:
                    b = getType(type, year, time, date, (byte) -1);
                    switch (b) {
                        case 0:
                            return JavaType.LOCAL_DATE;
                        case 1:
                            return JavaType.LOCAL_TIME;
                        case 2:
                            return JavaType.YEAR;
                        default:
                            return JavaType.LOCAL_DATE_TIME;
                    }
                default:
            }
        }
        return JavaType.STRING;
    }

    private byte getType(String t, String year, String time, String date, byte var5) {
        switch (t.hashCode()) {
            case 3076014:
                if (date.equals(t)) {
                    var5 = 0;
                }
                break;
            case 3560141:
                if (time.equals(t)) {
                    var5 = 1;
                }
                break;
            case 3704893:
                if (year.equals(t)) {
                    var5 = 2;
                }
            default:
        }
        return var5;
    }
}
