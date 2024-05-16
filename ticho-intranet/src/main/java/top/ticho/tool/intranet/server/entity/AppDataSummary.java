package top.ticho.tool.intranet.server.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 数据收集汇总
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Data
public class AppDataSummary implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 端口号 */
    private int port;
    /** 读取流量 */
    private long readBytes;
    /** 写入流量 */
    private long writeBytes;
    /** 读取消息数 */
    private long readMsgs;
    /** 写入消息数 */
    private long writeMsgs;
    /** 通道连接数 */
    private int channels;
    /** 统计时间 */
    private long timestamp;

}
