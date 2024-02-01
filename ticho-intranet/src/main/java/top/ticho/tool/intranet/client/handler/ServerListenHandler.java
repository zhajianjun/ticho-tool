package top.ticho.tool.intranet.client.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.ticho.tool.intranet.client.message.ServerMessageDisconnectHandler;
import top.ticho.tool.intranet.client.message.AbstractServerMessageHandler;
import top.ticho.tool.intranet.client.message.ServerMessageCloseHandler;
import top.ticho.tool.intranet.client.message.ServerMessageConnectHandler;
import top.ticho.tool.intranet.client.message.ServerMessageTransferHandler;
import top.ticho.tool.intranet.client.message.ServerMessageUnknownHandler;
import top.ticho.tool.intranet.constant.CommConst;
import top.ticho.tool.intranet.entity.Message;
import top.ticho.tool.intranet.prop.ClientProperty;
import top.ticho.tool.intranet.util.CommonUtil;

import java.util.HashMap;
import java.util.Map;


/**
 * 服务端监听处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ServerListenHandler extends SimpleChannelInboundHandler<Message> {

    private final ClientHander clientHander;

    private final AppHandler appHandler;

    public final Map<Byte, AbstractServerMessageHandler> MAP = new HashMap<>();

    public final AbstractServerMessageHandler UNKNOWN = new ServerMessageUnknownHandler();

    public ServerListenHandler(ClientHander clientHander, AppHandler appHandler, ClientProperty clientProperty) {
        this.clientHander = clientHander;
        this.appHandler = appHandler;
        ServerMessageConnectHandler clientConnectHandle = new ServerMessageConnectHandler();
        ServerMessageDisconnectHandler clientDisconnectHandle = new ServerMessageDisconnectHandler();
        ServerMessageTransferHandler clientTransferHandle = new ServerMessageTransferHandler();
        ServerMessageCloseHandler clientCloseHandle = new ServerMessageCloseHandler();
        // MAP.put(Message.AUTH, null);
        MAP.put(Message.DISABLED_ACCESS_KEY, clientCloseHandle);
        MAP.put(Message.CONNECT, clientConnectHandle);
        MAP.put(Message.DISCONNECT, clientDisconnectHandle);
        MAP.put(Message.TRANSFER, clientTransferHandle);
        MAP.values().forEach(item -> {
            item.setClientHander(clientHander);
            item.setAppHandler(appHandler);
            item.setClientProperty(clientProperty);
        });
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        byte type = msg.getType();
        AbstractServerMessageHandler clientHandle = MAP.getOrDefault(type, UNKNOWN);
        clientHandle.channelRead0(ctx, msg);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        Channel clientChannel = ctx.channel();
        Channel requestCHannel = clientChannel.attr(CommConst.CHANNEL).get();
        if (null != requestCHannel) {
            requestCHannel.config().setOption(ChannelOption.AUTO_READ, clientChannel.isWritable());
        }
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel clientChannel = ctx.channel();
        if (clientHander.getAuthServerChannel() == clientChannel) {
            clientHander.setAuthServerChannel(null);
            appHandler.clearRequestChannels();
            clientHander.restart();
        } else {
            Channel requestCHannel = clientChannel.attr(CommConst.CHANNEL).get();
            CommonUtil.close(requestCHannel);
        }
        clientHander.removeReadyServerChannel(clientChannel);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端异常 {} {}", ctx.channel(), cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }

}
