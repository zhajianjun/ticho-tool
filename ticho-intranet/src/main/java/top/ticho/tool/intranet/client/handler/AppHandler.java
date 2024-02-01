package top.ticho.tool.intranet.client.handler;

import cn.hutool.core.util.StrUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Getter
public class AppHandler {

    /** 监听客户端，用于监听服务器想要请求的应用地址 */
    private final Bootstrap bootstrap;

    /**
     * 监听app
     */
    private static final Map<String, Channel> requestChannelMap = new ConcurrentHashMap<>();

    public AppHandler(NioEventLoopGroup workerGroup) {
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(workerGroup);
        this.bootstrap.channel(NioSocketChannel.class);
        this.bootstrap.handler(new AppListenHandlerInit(this));
    }

    public void connect(String host, Integer port, GenericFutureListener<? extends Future<? super Void>> listener) {
        bootstrap.connect(host, port).addListener(listener);
    }

    public void saveRequestChannel(String uri, Channel channel) {
        requestChannelMap.put(uri, channel);
    }

    public void removeRequestChannel(String requestId) {
        if (StrUtil.isEmpty(requestId)) {
            return;
        }
        requestChannelMap.remove(requestId);
    }

    public void clearRequestChannels() {
        for (Map.Entry<String, Channel> entry : requestChannelMap.entrySet()) {
            Channel channel = entry.getValue();
            if (channel.isActive()) {
                channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
        }
        requestChannelMap.clear();
    }


    @AllArgsConstructor
    public static class AppListenHandlerInit extends ChannelInitializer<SocketChannel> {

        private final AppHandler appHandler;

        @Override
        protected void initChannel(SocketChannel channel) {
            channel.pipeline().addLast(new AppListenHandler(appHandler));
        }

    }

}
