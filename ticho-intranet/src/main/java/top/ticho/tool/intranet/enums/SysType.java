package top.ticho.tool.intranet.enums;

/**
 * 系统类型
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public enum SysType {

    /**  */
    WIN("win"),
    LINUX("linux");

    private final String code;

    SysType(String code) {
        this.code = code;
    }

    public String code() {
        return this.code;
    }

}
