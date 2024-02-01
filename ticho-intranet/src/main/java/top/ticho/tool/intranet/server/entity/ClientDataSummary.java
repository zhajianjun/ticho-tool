package top.ticho.tool.intranet.server.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 通道数据汇总
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Data
public class ClientDataSummary implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 端口号 */
    private int port;
    /** 读取流量(流入) */
    private long readBytes;
    /** 输出流量(流出) */
    private long wroteBytes;
    private long readMsgs;
    private long wroteMsgs;

    /** 连接数 */
    private int channels;
    private long timestamp;
}
