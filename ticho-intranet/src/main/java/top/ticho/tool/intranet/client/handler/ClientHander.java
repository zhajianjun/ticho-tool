package top.ticho.tool.intranet.client.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import top.ticho.tool.intranet.common.IdleChecker;
import top.ticho.tool.intranet.common.MessageDecoder;
import top.ticho.tool.intranet.common.MessageEncoder;
import top.ticho.tool.intranet.common.SslHandler;
import top.ticho.tool.intranet.constant.CommConst;
import top.ticho.tool.intranet.prop.ClientProperty;
import top.ticho.tool.intranet.util.CommonUtil;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class ClientHander {

    /** 协调客户端，用于和服务端信息交互 */
    @Getter
    private Bootstrap bootstrap;

    /**
     * 就绪状态服务通道队列
     * 当和服务端交互不活跃的情况下会暂时不不活跃的通道放在队列里，重新交互时优先去队列的通道去进行交互，为空时讲重新连接服务端产生新通道进行交互
     */
    private final ConcurrentLinkedQueue<Channel> readyServerChannels = new ConcurrentLinkedQueue<>();

    /** 此通道为连接服务端时通过权限校验的通道 */
    @Getter
    @Setter
    private volatile Channel authServerChannel;

    @Setter
    private long sleepTime = CommConst.ONE_SECOND;

    private final NioEventLoopGroup workerGroup;

    private final AppHandler appHandler;

    private final ClientProperty clientProperty;

    public ClientHander(ClientProperty clientProperty) {
        this.clientProperty = clientProperty;
        // 工作线程初始化
        this.workerGroup = new NioEventLoopGroup(clientProperty.getWorkerThreads());
        this.appHandler = new AppHandler(workerGroup);
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(workerGroup);
        this.bootstrap.channel(NioSocketChannel.class);
        this.bootstrap.handler(new ClientListenHandlerInit(this, appHandler, clientProperty));
    }

    public void connect(String host, Integer port, GenericFutureListener<? extends Future<? super Void>> listener) {
        bootstrap.connect(host, port).addListener(listener);
    }

    public void start() {
        String host = clientProperty.getServerHost();
        int port = Optional.ofNullable(clientProperty.getServerPort()).orElse(CommConst.SERVER_PORT_DEFAULT);
        // 连接远程服务器，并添加监听器发送accessKey验证权限，服务端验证失败会关闭连接
        this.bootstrap.connect(host, port).addListener(new ServerAuthAfterHander(this, clientProperty));
    }

    public void restart() {
        this.waitMoment();
        this.start();
    }

    public void stop(byte status) {
        this.workerGroup.shutdownGracefully();
        appHandler.clearRequestChannels();
        System.exit(status);
    }

    public void waitMoment() {
        // 超过一分钟则重置为一秒
        if (this.sleepTime > CommConst.ONE_MINUTE) {
            this.sleepTime = CommConst.ONE_SECOND;
        }
        // 时间翻倍
        this.sleepTime = this.sleepTime * 2;
        // 线程睡眠
        CommonUtil.sleep(this.sleepTime);
    }

    /**
     * 添加就绪状态的服务通道
     */
    public void saveReadyServerChannel(Channel channel) {
        if (readyServerChannels.size() > clientProperty.getMaxPoolSize()) {
            channel.close();
            return;
        }
        channel.config().setOption(ChannelOption.AUTO_READ, true);
        channel.attr(CommConst.CHANNEL).set(null);
        // 添加一个元素并返回true
        readyServerChannels.offer(channel);
    }

    /**
     * 获取就绪状态的服务通道
     */
    public Channel getReadyServerChannel() {
        return readyServerChannels.poll();
    }

    public void removeReadyServerChannel(Channel channel) {
        readyServerChannels.remove(channel);
    }


    @AllArgsConstructor
    public static class ClientListenHandlerInit extends ChannelInitializer<SocketChannel> {

        private final ClientHander clientHander;

        private final AppHandler appHandler;

        private final ClientProperty clientProperty;

        @Override
        protected void initChannel(SocketChannel socketChannel) {
            // @formatter:off
        if (Boolean.TRUE.equals(clientProperty.getSslEnable())) {
            SslHandler sslHandler = new SslHandler(clientProperty.getSslPath(), clientProperty.getSslPassword());
            SSLContext sslContext = sslHandler.getSslContext();
            SSLEngine engine = sslContext.createSSLEngine();
            engine.setUseClientMode(true);
            socketChannel.pipeline().addLast(new io.netty.handler.ssl.SslHandler(engine));
        }
        socketChannel.pipeline().addLast(new MessageDecoder(CommConst.MAX_FRAME_LEN, CommConst.FIELD_OFFSET, CommConst.FIELD_LEN, CommConst.ADJUSTMENT, CommConst.INIT_BYTES_TO_STRIP));
        socketChannel.pipeline().addLast(new MessageEncoder());
        socketChannel.pipeline().addLast(new IdleChecker(CommConst.READ_IDLE_TIME, CommConst.WRITE_IDLE_TIME - 10, 0));
        socketChannel.pipeline().addLast(new ServerListenHandler(clientHander, appHandler, clientProperty));
        // @formatter:on
        }

    }


}
