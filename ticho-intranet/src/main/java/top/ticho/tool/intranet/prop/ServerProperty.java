package top.ticho.tool.intranet.prop;

import lombok.Data;

/**
 * 服务配置
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Data
public class ServerProperty {

    /** 端口 */
    private Integer port;
    /** 是否开启ssl */
    private Boolean sslEnable;
    /** ssl端口 */
    private Integer sslPort;
    /** ssl证书路径 */
    private String sslPath;
    /** ssl证书密码 */
    private String sslPassword;
    /** 最大请求数 */
    private Long maxRequests = 1024L;
    /** 最大绑定端口数 */
    private Long maxBindPorts = 10000L;
    /** 接收客户端连接处理线程数量。默认为当前机器cpu数的二分一 */
    private int bossThreads = Math.max(1, Runtime.getRuntime().availableProcessors() / 2);
    /** 处理客户端连接线程数量。默认为当前机器cpu数的两倍 */
    private int workerThreads = Runtime.getRuntime().availableProcessors() * 2;

}
