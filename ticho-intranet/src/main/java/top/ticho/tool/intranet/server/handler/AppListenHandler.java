package top.ticho.tool.intranet.server.handler;

import cn.hutool.core.map.MapUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.ticho.tool.intranet.constant.CommConst;
import top.ticho.tool.intranet.entity.Message;
import top.ticho.tool.intranet.prop.ServerProperty;
import top.ticho.tool.intranet.server.entity.ClientInfo;
import top.ticho.tool.intranet.server.entity.PortInfo;
import top.ticho.tool.intranet.util.IntranetUtil;

import java.util.Map;
import java.util.Objects;

/**
 * 客户端根处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
@AllArgsConstructor
public class AppListenHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final ServerProperty serverProperty;

    private final ServerHandler serverHandler;

    private final AppHandler appHandler;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel requestChannel = ctx.channel();
        // log.debug("[服务端]请求通道激活, {}", requestChannel);
        // 查询请求通道的端口号
        Integer portNum = IntranetUtil.getPortByChannel(requestChannel);
        // 查询客户端信息
        ClientInfo clientInfo = serverHandler.getClientByPort(portNum);
        // 如果客户端连接异常，则关闭请求通道
        if (Objects.isNull(clientInfo) || Objects.isNull(clientInfo.getChannel()) || !IntranetUtil.isActive(clientInfo.getChannel())) {
            requestChannel.close();
            super.channelActive(ctx);
            return;
        }
        Long maxRequests = serverProperty.getMaxRequests();
        Channel clientChannel = clientInfo.getChannel();
        // 查询请求连接通道总数，超出最大值，则关闭第一个请求通道requestChannel
        Map<String, Channel> requestChannels = clientChannel.attr(CommConst.REQUEST_ID_ATTR_MAP).get();
        if (MapUtil.isNotEmpty(requestChannels) && requestChannels.size() >= maxRequests) {
            String firstKey = requestChannels.keySet().stream().findFirst().orElse(null);
            Channel oldRequestChannel = requestChannels.remove(firstKey);
            log.warn("超过最大连接数，关闭请求通道 {}", oldRequestChannel);
            IntranetUtil.close(oldRequestChannel);
        }
        String requestId = appHandler.getRequestId();
        // 请求通道自动读设置为false
        requestChannel.config().setOption(ChannelOption.AUTO_READ, false);
        // 请求通道添加请求连接id
        requestChannel.attr(CommConst.URI).set(requestId);
        requestChannels.put(requestId, requestChannel);
        // 获取端口信息
        PortInfo port = clientInfo.getPortMap().get(portNum);
        Message msg = new Message();
        msg.setType(Message.CONNECT);
        msg.setUri(requestId);
        msg.setData(port.getEndpoint().getBytes());
        clientChannel.writeAndFlush(msg);
        super.channelActive(ctx);
        // log.warn("[3][服务端]通道激活, 连接客户端{}, 消息{}", clientChannel, msg);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) {
        Channel requestChannel = ctx.channel();
        // log.debug("[服务端]通道数据请求, 请求通道{}", requestChannel);
        Channel clientChannel = requestChannel.attr(CommConst.CHANNEL).get();
        if (!IntranetUtil.isActive(clientChannel)) {
            requestChannel.close();
            return;
        }
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        Message msg = new Message();
        msg.setType(Message.TRANSFER);
        msg.setUri(requestChannel.attr(CommConst.URI).get());
        msg.setData(data);
        // log.warn("[7][服务端]请求传输到客户端，请求通道{}；客户端通道{}, 消息{}", requestChannel, clientChannel, msg);
        clientChannel.writeAndFlush(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel requestChannel = ctx.channel();
        Integer portNum = IntranetUtil.getPortByChannel(requestChannel);
        ClientInfo clientInfo = serverHandler.getClientByPort(portNum);
        if (Objects.isNull(clientInfo) || Objects.isNull(clientInfo.getChannel()) || !IntranetUtil.isActive(clientInfo.getChannel())) {
            requestChannel.close();
            super.channelInactive(ctx);
            return;
        }
        Channel clientChannelGet = clientInfo.getChannel();
        String requestId = requestChannel.attr(CommConst.URI).get();
        serverHandler.removeRequestChannel(clientChannelGet, requestId);
        Channel clientChannel = requestChannel.attr(CommConst.CHANNEL).get();
        if (!IntranetUtil.isActive(clientChannel)) {
            requestChannel.close();
            super.channelInactive(ctx);
            return;
        }
        IntranetUtil.close(clientChannel.attr(CommConst.CHANNEL).get());
        clientChannel.attr(CommConst.URI).set(null);
        clientChannel.attr(CommConst.KEY).set(null);
        clientChannel.attr(CommConst.CHANNEL).set(null);
        clientChannel.config().setOption(ChannelOption.AUTO_READ, true);
        Message msg = new Message();
        msg.setType(Message.DISCONNECT);
        msg.setUri(requestId);
        clientChannel.writeAndFlush(msg);
        super.channelInactive(ctx);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        Channel requestChannel = ctx.channel();
        Integer portNum = IntranetUtil.getPortByChannel(requestChannel);
        Channel clientChannel = serverHandler.getClientChannelByPort(portNum);
        if (!IntranetUtil.isActive(clientChannel)) {
            requestChannel.close();
        } else {
            Channel channel = requestChannel.attr(CommConst.CHANNEL).get();
            if (Objects.nonNull(channel)) {
                channel.config().setOption(ChannelOption.AUTO_READ, requestChannel.isWritable());
            }
        }
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("客户端异常 {} {}", ctx.channel(), cause.getMessage());
        IntranetUtil.close(ctx);
    }

}
