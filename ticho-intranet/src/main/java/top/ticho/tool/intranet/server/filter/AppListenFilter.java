package top.ticho.tool.intranet.server.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * 应用监听过滤器 接口
 *
 * @author zhajianjun
 * @date 2024-05-14 18:18
 */
public interface AppListenFilter {

    void channelActive(ChannelHandlerContext ctx);

    void channelRead(ChannelHandlerContext ctx, Object msg);

    void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise);

    void channelInactive(ChannelHandlerContext ctx);

}
