package top.ticho.tool.intranet.server.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.ticho.tool.intranet.constant.CommConst;
import top.ticho.tool.intranet.entity.Message;
import top.ticho.tool.intranet.util.IntranetUtil;

/**
 * 客户端信息传输消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ClientTransferMessageHandler extends AbstractClientMessageHandler {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) {
        Channel channel = ctx.channel();
        Channel requestChannel = channel.attr(CommConst.CHANNEL).get();
        if (!IntranetUtil.isActive(requestChannel)) {
            return;
        }
        ByteBuf data = ctx.alloc().buffer(msg.getData().length);
        data.writeBytes(msg.getData());
        requestChannel.writeAndFlush(data);
        // log.warn("[10][服务端]响应信息接收，接收通道：{}；写入通道：{}, 消息{}", channel, requestChannel, msg);
    }

}
