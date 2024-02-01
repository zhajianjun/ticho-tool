package top.ticho.tool.intranet.client.message;

import io.netty.channel.ChannelHandlerContext;
import lombok.Setter;
import top.ticho.tool.intranet.client.handler.AppHandler;
import top.ticho.tool.intranet.client.handler.ClientHander;
import top.ticho.tool.intranet.entity.Message;
import top.ticho.tool.intranet.prop.ClientProperty;


/**
 * 服务端消息处理器抽象类
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Setter
public abstract class AbstractServerMessageHandler {

    protected ClientHander clientHander;

    protected AppHandler appHandler;

    protected ClientProperty clientProperty;

    /**
     * 读取服务端信息进行不同的处理
     *
     * @param ctx 通道处理上线文
     * @param msg 服务端传输的信息
     */
    public abstract void channelRead0(ChannelHandlerContext ctx, Message msg);

}
