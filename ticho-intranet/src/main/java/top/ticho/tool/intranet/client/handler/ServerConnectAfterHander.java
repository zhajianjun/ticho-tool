package top.ticho.tool.intranet.client.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.ticho.tool.intranet.constant.CommConst;
import top.ticho.tool.intranet.entity.Message;
import top.ticho.tool.intranet.prop.ClientProperty;

/**
 * 客户端连接监听器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
@AllArgsConstructor
public class ServerConnectAfterHander implements ChannelFutureListener {

    private final AppHandler appHandler;

    private final ClientProperty clientProperty;

    private final Channel serverChannel;

    private final Channel requestChannel;

    private final String requestId;

    @Override
    public void operationComplete(ChannelFuture future) {
        if (!future.isSuccess()) {
            Message msg = new Message();
            msg.setType(Message.DISCONNECT);
            msg.setUri(requestId);
            this.serverChannel.writeAndFlush(msg);
            log.error("连接服务端失败:{}", future.cause().getMessage());
            return;
        }

        Channel clientChannel = future.channel();

        clientChannel.attr(CommConst.CHANNEL).set(this.requestChannel);
        this.requestChannel.attr(CommConst.CHANNEL).set(clientChannel);

        Message msg = new Message();
        msg.setType(Message.CONNECT);
        msg.setUri(requestId + "@" + clientProperty.getAccessKey());
        clientChannel.writeAndFlush(msg);

        this.requestChannel.config().setOption(ChannelOption.AUTO_READ, true);
        appHandler.saveRequestChannel(requestId, this.requestChannel);
        this.requestChannel.attr(CommConst.URI).set(requestId);
        // log.warn("[5][客户端]连接信息回传服务端，回传通道{}，携带通道{}，消息{}", clientChannel, requestChannel, msg);
    }

}
