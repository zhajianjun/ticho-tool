package top.ticho.tool.intranet.server.filter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

/**
 * 应用监听过滤器处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class AppListenRootFilter extends ChannelDuplexHandler {

    private final AppListenFilter appDataListen;

    public AppListenRootFilter(AppListenFilter appDataListen) {
        this.appDataListen = appDataListen;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        appDataListen.channelActive(ctx);
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        appDataListen.channelRead(ctx, (ByteBuf) msg);
        ctx.fireChannelRead(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        appDataListen.write(ctx, (ByteBuf) msg, promise);
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        appDataListen.channelInactive(ctx);
        super.channelInactive(ctx);
    }

}
