package top.ticho.tool.intranet.server.entity;

import io.netty.channel.Channel;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 通道信息
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Data
public class ClientInfo implements Serializable {
    private static final long serialVersionUID = 2788298692197340477L;

    /** 客户端秘钥 */
    private String accessKey;

    /** 客户端名称 */
    private String name;

    /** 连接时间 */
    private LocalDateTime connectTime;

    /** 连接的通道信息 */
    private transient Channel channel;

    /** 端口信息(端口号, 端口对象信息) */
    private Map<Integer, PortInfo> portMap;

}
