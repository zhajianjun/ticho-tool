package top.ticho.tool.intranet.client.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.ticho.tool.intranet.constant.CommConst;
import top.ticho.tool.intranet.entity.Message;

/**
 * 服务端数据传输消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ServerMessageTransferHandler extends AbstractServerMessageHandler {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) {
        Channel clientChannel = ctx.channel();
        ByteBufAllocator alloc = ctx.alloc();
        Channel requestChannel = clientChannel.attr(CommConst.CHANNEL).get();
        if (requestChannel == null) {
            return;
        }
        ByteBuf buf = alloc.buffer(msg.getData().length);
        buf.writeBytes(msg.getData());
        requestChannel.writeAndFlush(buf);
        // log.warn("[8][客户端]接收到到客户端请求信息，接收通道{}，写入通道{}，消息{}", clientChannel, requestChannel, msg);
    }

}
