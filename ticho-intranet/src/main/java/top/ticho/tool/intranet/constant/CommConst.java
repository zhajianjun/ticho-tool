package top.ticho.tool.intranet.constant;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.util.Map;


/**
 * 常量
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class CommConst {
    private CommConst() {
    }

    /** 消息头部固定长度 */
    public static final byte HEADER_SIZE = 4;

    public static final int TYPE_SIZE = 1;

    public static final int SERIAL_SIZE = 8;

    public static final int URI_LEN_SIZE = 1;

    /* 包的最大长度 2M */
    public static final int MAX_FRAME_LEN = 2 * 1024 * 1024;
    /** 长度域偏移量，指的是长度域的偏移量，表示跳过指定个数字节之后的才是长度域；例如在长度域字段前边还有head字段len=2时，那么此值设置为2，表示把长度域之前的字段都偏移掉。 */
    public static final int FIELD_OFFSET = 0;
    /** 记录该帧数据长度的字段本身的长度 */
    public static final int FIELD_LEN = 4;
    /** 该字段加长度字段等于数据帧的长度，包体长度调整的大小，长度域的数值表示的长度加上这个修正值表示的就是带header的包 */
    public static final int INIT_BYTES_TO_STRIP = 0;
    /** 从数据帧中跳过的字节数，表示获取完一个完整的数据包之后，忽略前面的指定的位数个字节，应用解码器拿到的就是不带长度域的数据包 */
    public static final int ADJUSTMENT = 0;

    public static final int READ_IDLE_TIME = 60;

    public static final int WRITE_IDLE_TIME = 40;

    public static final int SERVER_PORT_DEFAULT = 5120;

    public static final long ONE_SECOND = 1000L;

    public static final long ONE_MINUTE = 60 * ONE_SECOND;

    public static final String TLS = "TLS";

    public static final String JKS = "JKS";

    public static final String ACCESS_KEY = "ACCESS_KEY";

    /** 请求id key */
    public static final String REQUEST_ID_MAP_KEY = "REQUEST_ID";
    /** 请求id attr key */
    public static final AttributeKey<Map<String, Channel>> REQUEST_ID_ATTR_MAP = AttributeKey.newInstance(REQUEST_ID_MAP_KEY);

    public static final AttributeKey<Channel> CHANNEL = AttributeKey.newInstance("CHANNEL");

    public static final AttributeKey<String> URI = AttributeKey.newInstance("URI");

    public static final AttributeKey<String> KEY = AttributeKey.newInstance("KEY");

    /** 本地地址 */
    public static final String LOCALHOST = "0.0.0.0";

    /** 系统类型 */
    public static final String OS_TYPE = System.getProperty("os.name").toLowerCase();
    /** ssl */
    public static final String SSL = "ssl";

    public static final int MAX_PORT = 65535;

    public static final long ONE_KB = 1024L;

}
