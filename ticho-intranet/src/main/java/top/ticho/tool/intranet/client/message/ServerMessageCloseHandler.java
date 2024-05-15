package top.ticho.tool.intranet.client.message;

import cn.hutool.core.util.StrUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.ticho.tool.intranet.constant.CommConst;
import top.ticho.tool.intranet.entity.Message;
import top.ticho.tool.intranet.util.IntranetUtil;

import java.nio.charset.StandardCharsets;

/**
 * 服务端关闭消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ServerMessageCloseHandler extends AbstractServerMessageHandler {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) {
        Channel clientChannel = ctx.channel();
        log.warn("客户端{}={}关闭连接, 消息：{}", CommConst.ACCESS_KEY, clientProperty.getAccessKey(), StrUtil.str(msg.getData(), StandardCharsets.UTF_8));
        IntranetUtil.close(clientChannel);
        clientHander.stop(msg.getType());
    }

}
