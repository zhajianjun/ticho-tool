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
public class DataCollector {
    private static final Map<Integer, DataCollector> collectors = new ConcurrentHashMap<>();


    private Integer port;
    private final AtomicLong readBytes = new AtomicLong();
    private final AtomicLong wroteBytes = new AtomicLong();
    private final AtomicLong readMsgs = new AtomicLong();
    private final AtomicLong wroteMsgs = new AtomicLong();
    private final AtomicInteger channels = new AtomicInteger();

    private DataCollector() {
    }

    public static DataCollector getCollector(Integer port) {
        DataCollector collector = collectors.get(port);
        if (null == collector) {
            synchronized (collectors) {
                collector = collectors.get(port);
                if (null == collector) {
                    collector = new DataCollector();
                    collector.setPort(port);
                    collectors.put(port, collector);
                }
            }
        }
        return collector;
    }

    public static List<ClientDataSummary> getAllData() {
        return collectors.values().stream().map(DataCollector::getData).collect(Collectors.toList());
    }

    public ClientDataSummary getData() {
        ClientDataSummary data = new ClientDataSummary();
        data.setChannels(this.channels.get());
        data.setPort(this.port);
        data.setReadBytes(CommonUtil.divide(this.readBytes.get(), CommConst.ONE_KB));
        data.setWroteBytes(CommonUtil.divide(this.wroteBytes.get(), CommConst.ONE_KB));
        data.setTimestamp(System.currentTimeMillis());
        data.setReadMsgs(this.readMsgs.get());
        data.setWroteMsgs(this.wroteMsgs.get());
        return data;
    }

    public void incrementReadBytes(long bytes) {
        this.readBytes.addAndGet(bytes);
    }

    public void incrementWroteBytes(long bytes) {
        this.wroteBytes.addAndGet(bytes);
    }

    public void incrementReadMsgs(long msgs) {
        this.readMsgs.addAndGet(msgs);
    }

    public void incrementWroteMsgs(long msgs) {
        this.wroteMsgs.addAndGet(msgs);
    }

}
