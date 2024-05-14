package top.ticho.tool.intranet.server.entity;

import lombok.Data;
import top.ticho.tool.intranet.constant.CommConst;
import top.ticho.tool.intranet.util.CommonUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 数据收集器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Data
public class AppDataCollector {
    private static final Map<Integer, AppDataCollector> collectors = new ConcurrentHashMap<>();

    /** 端口 */
    private Integer port;
    /** 读取流量大小(字节) */
    private final AtomicLong readBytes = new AtomicLong();
    /** 写入流量大小(字节) */
    private final AtomicLong writeBytes = new AtomicLong();
    /** 读取消息数 */
    private final AtomicLong readMsgs = new AtomicLong();
    /** 写入消息数 */
    private final AtomicLong writeMsgs = new AtomicLong();
    /** 访问通道数 */
    private final AtomicInteger channels = new AtomicInteger();

    private AppDataCollector() {
    }

    public static AppDataCollector getCollector(Integer port) {
        AppDataCollector collector = collectors.get(port);
        if (null == collector) {
            synchronized (collectors) {
                collector = collectors.get(port);
                if (null == collector) {
                    collector = new AppDataCollector();
                    collector.setPort(port);
                    collectors.put(port, collector);
                }
            }
        }
        return collector;
    }

    public static List<ClientDataSummary> getAllData() {
        return collectors.values().stream().map(AppDataCollector::getData).collect(Collectors.toList());
    }

    public ClientDataSummary getData() {
        ClientDataSummary data = new ClientDataSummary();
        data.setChannels(this.channels.get());
        data.setPort(this.port);
        data.setReadBytes(CommonUtil.divide(this.readBytes.get(), CommConst.ONE_KB));
        data.setWroteBytes(CommonUtil.divide(this.writeBytes.get(), CommConst.ONE_KB));
        data.setTimestamp(System.currentTimeMillis());
        data.setReadMsgs(this.readMsgs.get());
        data.setWroteMsgs(this.writeMsgs.get());
        return data;
    }

    public void incrementReadBytes(long bytes) {
        this.readBytes.addAndGet(bytes);
    }

    public void incrementWriteBytes(long bytes) {
        this.writeBytes.addAndGet(bytes);
    }

    public void incrementReadMsgs(long msgs) {
        this.readMsgs.addAndGet(msgs);
    }

    public void incrementWriteMsgs(long msgs) {
        this.writeMsgs.addAndGet(msgs);
    }

}
