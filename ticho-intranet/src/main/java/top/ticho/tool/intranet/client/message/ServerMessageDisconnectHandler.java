package top.ticho.tool.intranet.client.message;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import top.ticho.tool.intranet.constant.CommConst;
import top.ticho.tool.intranet.entity.Message;

/**
 * 服务端断开通道连接消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class ServerMessageDisconnectHandler extends AbstractServerMessageHandler {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) {
        Channel clientChannel = ctx.channel();
        Channel requestCHannel = clientChannel.attr(CommConst.CHANNEL).get();
        if (null == requestCHannel) {
            return;
        }
        clientChannel.attr(CommConst.CHANNEL).set(null);
        clientHander.saveReadyServerChannel(clientChannel);
        requestCHannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

}
