package top.ticho.tool.intranet.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 协议类型
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public enum ProtocolType {

    /**  */
    HTTP(0, "http"),
    HTTPS(1, "https"),
    SSH(2, "ssh"),
    TELNET(3, "telnet"),
    DATABASE(4, "data base"),
    RDESKTOP(5, "remote desktop"),
    TCP(6, "tcp");

    private final int code;
    private final String type;

    ProtocolType(int code, String type) {
        this.code = code;
        this.type = type;
    }

    public static boolean isValid(String value) {
        ProtocolType[] var1 = values();

        for (ProtocolType type : var1) {
            if (type.type().equals(value)) {
                return true;
            }
        }
        return false;
    }

    public int code() {
        return this.code;
    }

    public String type() {
        return this.type;
    }


    public static ProtocolType getByCode(int code) {
        Optional<ProtocolType> optional = Arrays.stream(values()).filter(x -> code == x.code()).findFirst();
        return optional.orElse(null);
    }


}
