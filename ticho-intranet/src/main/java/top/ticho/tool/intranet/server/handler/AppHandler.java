package top.ticho.tool.intranet.server.handler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import top.ticho.tool.intranet.prop.ServerProperty;
import top.ticho.tool.intranet.server.entity.PortInfo;
import top.ticho.tool.intranet.server.filter.AppListenFilter;
import top.ticho.tool.intranet.server.filter.AppListenRootFilter;
import top.ticho.tool.intranet.util.IntranetUtil;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class AppHandler {

    private final ServerBootstrap serverBootstrap;

    /** 请求id */
    private final AtomicLong requestId = new AtomicLong(0L);

    /** 与绑定端口的通道 */
    @Getter
    private final Map<Integer, Channel> bindPortChannelMap = new ConcurrentHashMap<>();

    private final ServerProperty serverProperty;

    public AppHandler(ServerProperty serverProperty, ServerHandler serverHandler, NioEventLoopGroup serverBoss, NioEventLoopGroup serverWorker, AppListenFilter appListenFilter) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ServerBootstrap group = serverBootstrap.group(serverBoss, serverWorker);
        ServerBootstrap channel = group.channel(NioServerSocketChannel.class);
        AppListenHandlerInit childHandler = new AppListenHandlerInit(this, serverProperty, serverHandler, appListenFilter);
        channel.childHandler(childHandler);
        this.serverBootstrap = serverBootstrap;
        this.serverProperty = serverProperty;
    }

    public boolean exists(Integer portNum) {
        if (Objects.isNull(portNum)) {
            return false;
        }
        return bindPortChannelMap.containsKey(portNum);
    }

    public void createApp(PortInfo portInfo) {
        Integer port;
        if (Objects.isNull(portInfo) || Objects.isNull(port = portInfo.getPort())) {
            return;
        }
        if (bindPortChannelMap.containsKey(port)) {
            log.warn("创建应用失败，端口：{}已被创建", port);
            return;
        }
        Long maxBindPorts = serverProperty.getMaxBindPorts();
        if (bindPortChannelMap.size() >= maxBindPorts) {
            log.warn("创建应用失败，端口：{} 超出最大绑定端口数{}", port, maxBindPorts);
            return;
        }
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(port);
            channelFuture.get();
            bindPortChannelMap.put(port, channelFuture.channel());
        } catch (InterruptedException | ExecutionException e) {
            log.error("创建应用失败，端口：{}，错误信息：{}", port, e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
        log.info("创建应用成功，端口：{}", port);
    }

    public void deleteApp(Integer port) {
        if (null == port) {
            return;
        }
        Channel channel = bindPortChannelMap.get(port);
        if (channel == null) {
            return;
        }
        IntranetUtil.close(channel);
        bindPortChannelMap.remove(port);
        log.info("删除应用成功，端口：{}", port);
    }

    public String getRequestId() {
        return String.valueOf(requestId.incrementAndGet());
    }

    /**
     * 应用总数
     */
    public int size() {
        return bindPortChannelMap.size();
    }

    @AllArgsConstructor
    public static class AppListenHandlerInit extends ChannelInitializer<SocketChannel> {

        private final AppHandler appHandler;

        private final ServerProperty serverProperty;

        private final ServerHandler serverHandler;

        private final AppListenFilter appListenFilter;

        protected void initChannel(SocketChannel socketChannel) {
            socketChannel.pipeline().addFirst(new AppListenRootFilter(appListenFilter));
            socketChannel.pipeline().addLast(new AppListenHandler(serverProperty, serverHandler, appHandler));
        }

    }

}
