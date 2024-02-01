package top.ticho.tool.intranet.entity;

import lombok.Data;
import top.ticho.tool.intranet.enums.MsgType;


/**
 * 消息体
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Data
public class Message {

    /** 1-验证消息以检查accessKey是否正确 */
    public static final byte AUTH = MsgType.AUTH.code();
    /** 2-无效的访问密钥 */
    public static final byte DISABLED_ACCESS_KEY = MsgType.DISABLED_ACCESS_KEY.code();
    /** 3-客户端通道连接 */
    public static final byte CONNECT = MsgType.CONNECT.code();
    /** 4-客户端断开通道连接 */
    public static final byte DISCONNECT = MsgType.DISCONNECT.code();
    /** 5-数据传输 */
    public static final byte TRANSFER = MsgType.TRANSFER.code();
    /** 6-客户端心跳 */
    public static final byte HEARTBEAT = MsgType.HEARTBEAT.code();

    /** 类型 */
    private byte type;

    /** 序列号 */
    private long serial;

    /** uri */
    private String uri;

    /** 数据 */
    private byte[] data;

    @Override
    public String toString() {
        return "{" + "type=" + type + ", serial=" + serial + ", uri=" + uri + "}";
    }

}
