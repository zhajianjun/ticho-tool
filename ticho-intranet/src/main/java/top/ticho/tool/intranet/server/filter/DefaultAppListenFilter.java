package top.ticho.tool.intranet.server.filter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.Data;
import top.ticho.tool.intranet.server.entity.AppDataCollector;

import java.net.InetSocketAddress;

/**
 * 默认应用监听过滤器
 *
 * @author zhajianjun
 * @date 2024-05-14 17:22
 */
@Data
public class DefaultAppListenFilter implements AppListenFilter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        InetSocketAddress addr = (InetSocketAddress) ctx.channel().localAddress();
        AppDataCollector collector = AppDataCollector.getCollector(addr.getPort());
        collector.getChannels().incrementAndGet();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        InetSocketAddress addr = (InetSocketAddress) ctx.channel().localAddress();
        AppDataCollector collector = AppDataCollector.getCollector(addr.getPort());
        collector.incrementReadBytes(((ByteBuf) msg).readableBytes());
        collector.incrementReadMsgs(1L);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        InetSocketAddress addr = (InetSocketAddress) ctx.channel().localAddress();
        AppDataCollector collector = AppDataCollector.getCollector(addr.getPort());
        ByteBuf byteBuf = (ByteBuf) msg;
        collector.incrementWriteBytes(byteBuf.readableBytes());
        collector.incrementWriteMsgs(1L);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        InetSocketAddress addr = (InetSocketAddress) ctx.channel().localAddress();
        AppDataCollector collector = AppDataCollector.getCollector(addr.getPort());
        collector.getChannels().decrementAndGet();
    }


}
