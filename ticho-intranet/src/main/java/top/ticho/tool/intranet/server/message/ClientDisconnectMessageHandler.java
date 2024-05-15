package top.ticho.tool.intranet.server.message;

import cn.hutool.core.util.StrUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import top.ticho.tool.intranet.constant.CommConst;
import top.ticho.tool.intranet.entity.Message;
import top.ticho.tool.intranet.util.IntranetUtil;

/**
 * 客户端断开连接消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class ClientDisconnectMessageHandler extends AbstractClientMessageHandler {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) {
        Channel channel = ctx.channel();
        String requestId = msg.getUri();
        String accessKey = channel.attr(CommConst.KEY).get();
        Channel requestChannel;
        if (StrUtil.isEmpty(accessKey)) {
            requestChannel = serverHandler.removeRequestChannel(channel, requestId);
            if (IntranetUtil.isActive(requestChannel)) {
                requestChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
            return;
        }
        requestChannel = serverHandler.removeRequestChannel(accessKey, requestId);
        if (!IntranetUtil.isActive(requestChannel)) {
            return;
        }
        requestChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        IntranetUtil.close(channel.attr(CommConst.CHANNEL).get());
        channel.attr(CommConst.URI).set(null);
        channel.attr(CommConst.KEY).set(null);
        channel.attr(CommConst.CHANNEL).set(null);
    }

}
