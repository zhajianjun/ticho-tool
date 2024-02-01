package top.ticho.tool.intranet.server.message;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.ticho.tool.intranet.entity.Message;

import java.nio.charset.StandardCharsets;

/**
 * 客户端心跳消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ClientHeartbeatMessageHandler extends AbstractClientMessageHandler {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) {
        Channel channel = ctx.channel();
        notify(channel, Message.HEARTBEAT, msg.getSerial(), "心跳检测".getBytes(StandardCharsets.UTF_8));
    }

}
