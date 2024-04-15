package top.ticho.tool.intranet.server.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import top.ticho.tool.intranet.common.IdleChecker;
import top.ticho.tool.intranet.common.MessageDecoder;
import top.ticho.tool.intranet.common.MessageEncoder;
import top.ticho.tool.intranet.common.SslHandler;
import top.ticho.tool.intranet.constant.CommConst;
import top.ticho.tool.intranet.prop.ServerProperty;
import top.ticho.tool.intranet.server.entity.ClientInfo;
import top.ticho.tool.intranet.server.entity.PortInfo;
import top.ticho.tool.intranet.util.CommonUtil;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * 内网映射服务端处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Getter
@Slf4j
public class ServerHandler {

    /** 客户端与服务端的通道 */
    private final Map<String, ClientInfo> clientMap = new ConcurrentHashMap<>();

    private final AppHandler appHandler;

    private final ServerProperty serverProperty;

    /**
     * 接收客户端的连接。
     * serverBoss的线程数量可以设置为CPU核心数的一半。这是因为serverBoss主要负责接收客户端的连接，进行TCP握手等操作，不需要太多的处理能力，因此一般可以设置为较小的值。另外，serverBoss的线程数不宜过大，以免占用过多的系统资源。
     */
    private final NioEventLoopGroup serverBoss;
    /**
     * 处理客户端的实际业务逻辑。
     * serverWorker的线程数量可以设置为CPU核心数或者更多。serverWorker负责处理客户端的实际业务逻辑，因此需要更多的处理能力。通常情况下，可以设置为CPU核心数或者CPU核心数的倍数，以充分利用机器的多核处理能力。
     */
    private final NioEventLoopGroup serverWorker;

    public ServerHandler(ServerProperty serverProperty) {
        try {
            log.info("内网映射服务启动中，端口：{}，是否开启ssl：{}, ssl端口：{}", serverProperty.getPort(), serverProperty.getSslEnable(), serverProperty.getSslPort());
            this.serverBoss = new NioEventLoopGroup(serverProperty.getBossThreads());
            this.serverWorker = new NioEventLoopGroup(serverProperty.getWorkerThreads());
            this.serverProperty = serverProperty;
            this.appHandler = new AppHandler(serverProperty, this, serverBoss, serverWorker);
            int servPort = serverProperty.getPort();
            String host = CommConst.LOCALHOST;
            // 创建netty服务端
            ServerBootstrap server = this.newServer(false, serverProperty, serverWorker, serverBoss);
            server.bind(host, servPort).get();
            if (Boolean.TRUE.equals(serverProperty.getSslEnable())) {
                // 创建ssl服务端
                ServerBootstrap sslServer = this.newServer(true, serverProperty, serverWorker, serverBoss);
                Integer sslServerPort = serverProperty.getSslPort();
                ChannelFuture cf = sslServer.bind(host, sslServerPort);
                cf.sync();
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("内网映射服务启动失败");
            throw new RuntimeException(e);
        }
        log.info("内网映射服务启动成功");
    }

    /**
     * 保存客户端
     * 如果有端口信息，则创建应用
     */
    public void saveClient(ClientInfo clientInfo) {
        String accessKey;
        // 如果客户端信息为空或者accessKey为空，则不保存
        if (null == clientInfo || StrUtil.isBlank((accessKey = clientInfo.getAccessKey()))) {
            return;
        }
        // 需要创建应用的端口MAP
        Map<Integer, PortInfo> portMap = clientInfo.getPortMap();
        // 获取内存中的客户端信息
        ClientInfo clientInfoFromMem = clientMap.get(accessKey);
        if (Objects.isNull(clientInfoFromMem)) {
            // 如果不存在客户端信息，则把当前客户端信息保存到内存中，客户端的端口MAP设置为null，创建应用时存入
            clientInfo.setPortMap(null);
            clientMap.put(accessKey, clientInfo);
        }
        if (MapUtil.isEmpty(portMap)) {
            return;
        }
        portMap.values().forEach(this::createApp);
    }

    /**
     * 批量保存客户端
     * 如果有端口信息，则创建应用
     */
    public void saveClientBatch(Collection<ClientInfo> clientInfos) {
        if (CollUtil.isEmpty(clientInfos)) {
            return;
        }
        clientInfos.forEach(this::saveClient);
    }

    /**
     * 刷新客户端
     * 如果客户端的端口MAP为空，则删除应用
     */
    public void flushClient(ClientInfo clientInfo) {
        if (Objects.isNull(clientInfo)) {
            return;
        }
        Map<Integer, PortInfo> portMap = clientInfo.getPortMap();
        saveClient(clientInfo);
        String accessKey = clientInfo.getAccessKey();
        // 如果客户端的端口MAP为空，则删除该客户端所有的应用
        if (CollUtil.isEmpty(portMap)) {
            deleteApp(accessKey);
            return;
        }
        // 获取内存中的客户端信息
        ClientInfo clientInfoFromMem = clientMap.get(accessKey);
        Map<Integer, PortInfo> portMapFromMem = clientInfoFromMem.getPortMap();
        // 如果客户端的端口MAP不为空，则删除内存中不应存在的端口
        portMapFromMem.values().forEach(x-> {
            if (portMap.containsKey(x.getPort())) {
                return;
            }
            deleteApp(accessKey, x.getPort());
        });
    }

    /**
     * 批量刷新客户端
     */
    public void flushClientBatch(Collection<ClientInfo> clientInfos) {
        if (CollUtil.isEmpty(clientInfos)) {
            deleteAllClient();
            return;
        }
        clientInfos.forEach(this::flushClient);
    }

    /**
     * 根据accessKey删除客户端
     */
    public void deleteClient(String accessKey) {
        if (StrUtil.isBlank(accessKey)) {
            return;
        }
        ClientInfo clientInfoGet = clientMap.get(accessKey);
        if (Objects.isNull(clientInfoGet)) {
            return;
        }
        Map<Integer, PortInfo> portMap = clientInfoGet.getPortMap();
        Optional.ofNullable(portMap)
            .filter(MapUtil::isNotEmpty)
            .map(Map::keySet)
            .ifPresent(ports-> {
                ports.forEach(appHandler::deleteApp);
                portMap.clear();
            });
        closeClientAndRequestChannel(clientInfoGet);
        CommonUtil.close(clientInfoGet.getChannel());
        clientMap.remove(accessKey);
    }

    /**
     * 删除所有客户端
     */
    public void deleteAllClient() {
        if (MapUtil.isEmpty(clientMap)) {
            return;
        }
        Set<String> accessKeys = clientMap.keySet();
        accessKeys.forEach(this::deleteClient);
    }

    /**
     *  创建应用
     */
    public void createApp(PortInfo portInfo) {
        if (null == portInfo) {
            return;
        }
        ClientInfo clientInfoFromMem = clientMap.get(portInfo.getAccessKey());
        if (Objects.isNull(clientInfoFromMem)) {
            return;
        }
        Map<Integer, PortInfo> portMap = clientInfoFromMem.getPortMap();
        if (null == portMap) {
            portMap = new LinkedHashMap<>();
            clientInfoFromMem.setPortMap(portMap);
        }
        appHandler.createApp(portInfo);
        portMap.put(portInfo.getPort(), portInfo);
    }

    /**
     * 根据accessKey删除应用
     */
    public void deleteApp(String accessKey) {
        if (StrUtil.isBlank(accessKey)) {
            return;
        }
        ClientInfo clientInfo = clientMap.get(accessKey);
        if (null == clientInfo || MapUtil.isEmpty(clientInfo.getPortMap())) {
            return;
        }
        Map<Integer, PortInfo> portMap = clientInfo.getPortMap();
        portMap.keySet().forEach(portNum-> {
            appHandler.deleteApp(portNum);
            portMap.remove(portNum);
        });
    }

    /**
     * 根据accessKey和端口号删除应用
     */
    public void deleteApp(String accessKey, Integer portNum) {
        if (StrUtil.isBlank(accessKey) || Objects.isNull(portNum)) {
            return;
        }
        ClientInfo clientInfo = clientMap.get(accessKey);
        if (null == clientInfo || MapUtil.isEmpty(clientInfo.getPortMap())) {
            return;
        }
        if (clientInfo.getPortMap().containsKey(portNum)) {
            PortInfo portInfo = clientInfo.getPortMap().get(portNum);
            appHandler.deleteApp(portInfo.getPort());
            clientInfo.getPortMap().remove(portNum);
        }
    }

    public ClientInfo getClientByAccessKey(String accessKey) {
        if (StrUtil.isBlank(accessKey)) {
            return null;
        }
        return clientMap.get(accessKey);
    }

    public ClientInfo getClientByPort(Integer port) {
        // @formatter:off
        return clientMap.values()
            .stream()
            .filter(Objects::nonNull)
            .filter(x-> x.getPortMap().containsKey(port))
            .findFirst()
            .orElse(null);
        // @formatter:off
    }

    public Channel getClientChannelByPort(Integer port) {
        // @formatter:off
        return Optional
            .ofNullable(getClientByPort(port))
            .map(ClientInfo::getChannel)
            .orElse(null);
        // @formatter:on
    }

    public Channel getRequestChannel(Channel channel, String requestId) {
        if (null == channel || StrUtil.isBlank(requestId)) {
            return null;
        }
        Map<String, Channel> requestChannelMap = channel.attr(CommConst.REQUEST_ID_ATTR_MAP).get();
        if (MapUtil.isEmpty(requestChannelMap) && !requestChannelMap.containsKey(requestId)) {
            return null;
        }
        return requestChannelMap.get(requestId);
    }

    public Channel removeRequestChannel(String key, String requestId) {
        ClientInfo hc = clientMap.get(key);
        return removeRequestChannel(hc.getChannel(), requestId);
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public Channel removeRequestChannel(Channel channel, String requestId) {
        if (null == channel) {
            return null;
        }
        Map<String, Channel> requestChannelMap = channel.attr(CommConst.REQUEST_ID_ATTR_MAP).get();
        if (MapUtil.isNotEmpty(requestChannelMap) && requestChannelMap.containsKey(requestId)) {
            synchronized (channel) {
                return requestChannelMap.remove(requestId);
            }
        }
        return null;
    }

    public void closeClientAndRequestChannel(ClientInfo clientInfo) {
        if (Objects.isNull(clientInfo)) {
            return;
        }
        Channel channel = clientInfo.getChannel();
        if (Objects.isNull(channel)) {
            return;
        }
        Map<String, Channel> requestChannelMap = channel.attr(CommConst.REQUEST_ID_ATTR_MAP).get();
        if (MapUtil.isNotEmpty(requestChannelMap)) {
            requestChannelMap.values().forEach(CommonUtil::close);
            requestChannelMap.clear();
        }
        CommonUtil.close(channel);
        clientInfo.setChannel(null);
    }

    private ServerBootstrap newServer(boolean sslEnabled, ServerProperty serverProperty, NioEventLoopGroup serverBoss, NioEventLoopGroup serverWorker) {
        ServerBootstrap strap = new ServerBootstrap();
        ServerBootstrap group = strap.group(serverBoss, serverWorker);
        ServerBootstrap channel = group.channel(NioServerSocketChannel.class);
        ServerListenHandlerInit childHandler = new ServerListenHandlerInit(sslEnabled, this, serverProperty);
        channel.childHandler(childHandler);
        return strap;
    }

    public static class ServerListenHandlerInit extends ChannelInitializer<SocketChannel> {

        private final boolean sslEnabled;

        private final ServerHandler serverHandler;

        private final ServerProperty serverProperty;

        public ServerListenHandlerInit(boolean sslEnabled, ServerHandler serverHandler, ServerProperty serverProperty) {
            this.sslEnabled = sslEnabled;
            this.serverHandler = serverHandler;
            this.serverProperty = serverProperty;
        }

        protected void initChannel(SocketChannel sc) {
            if (Boolean.TRUE.equals(this.sslEnabled)) {
                SslHandler sslHandler = new SslHandler(serverProperty.getSslPath(), serverProperty.getSslPassword());
                SSLContext sslContext = sslHandler.getSslContext();
                SSLEngine engine = sslContext.createSSLEngine();
                engine.setUseClientMode(false);
                engine.setNeedClientAuth(true);
                sc.pipeline().addLast(CommConst.SSL, new io.netty.handler.ssl.SslHandler(engine));
            }
            sc.pipeline().addLast(new MessageDecoder(CommConst.MAX_FRAME_LEN, CommConst.FIELD_OFFSET, CommConst.FIELD_LEN, CommConst.ADJUSTMENT, CommConst.INIT_BYTES_TO_STRIP));
            sc.pipeline().addLast(new MessageEncoder());
            sc.pipeline().addLast(new IdleChecker(CommConst.READ_IDLE_TIME, CommConst.WRITE_IDLE_TIME, 0));
            sc.pipeline().addLast(new ClientListenHandler(serverHandler));
        }

    }

}
