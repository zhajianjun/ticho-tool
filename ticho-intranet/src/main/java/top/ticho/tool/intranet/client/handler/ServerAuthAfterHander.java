package top.ticho.tool.intranet.client.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import top.ticho.tool.intranet.constant.CommConst;
import top.ticho.tool.intranet.entity.Message;
import top.ticho.tool.intranet.prop.ClientProperty;

import java.util.Optional;

/**
 * 客户端身份验证
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ServerAuthAfterHander implements ChannelFutureListener {

    private final ClientHander clientHander;
    private final ClientProperty clientProperty;

    public ServerAuthAfterHander(ClientHander clientHander, ClientProperty clientProperty) {
        this.clientHander = clientHander;
        this.clientProperty = clientProperty;
    }

    @Override
    public void operationComplete(ChannelFuture future) {
        String host = clientProperty.getServerHost();
        int port = Optional.ofNullable(clientProperty.getServerPort()).orElse(CommConst.SERVER_PORT_DEFAULT);
        // future.isSuccess() = false则表示连接服务端失败，尝试重连
        if (!future.isSuccess()) {
            log.warn("连接服务端[{}:{}]失败, error：{}", host, port, future.cause().getMessage());
            // 尝试重连
            clientHander.restart();
            return;
        }
        // 连接成功处理
        Channel channel = future.channel();
        // 连接服务端的通道添加到 通道工厂中
        clientHander.setAuthServerChannel(channel);
        // 通道传输权限信息给服务端进行校验，由服务端校验是否关闭还是正常连接
        Message msg = new Message();
        msg.setType(Message.AUTH);
        msg.setUri(clientProperty.getAccessKey());
        channel.writeAndFlush(msg);
        // 重连后初始化sleepTime
        clientHander.setSleepTime(CommConst.ONE_SECOND);
        // log.warn("[1]连接服务端成功：{}", channel);
        log.info("连接服务端[{}:{}]成功", host, port);
    }

}
