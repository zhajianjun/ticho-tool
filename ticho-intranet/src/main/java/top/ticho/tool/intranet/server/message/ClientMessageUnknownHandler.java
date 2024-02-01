package top.ticho.tool.intranet.server.message;

import cn.hutool.core.util.StrUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.ticho.tool.intranet.entity.Message;

import java.nio.charset.StandardCharsets;

/**
 * 客户端未知消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ClientMessageUnknownHandler extends AbstractClientMessageHandler {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) {
        log.debug("接收到未知类型{}的消息,{}", msg.getType(), StrUtil.str(msg.getData(), StandardCharsets.UTF_8));
    }

}
