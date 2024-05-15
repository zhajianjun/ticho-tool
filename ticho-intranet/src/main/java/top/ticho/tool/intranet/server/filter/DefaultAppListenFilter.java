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
    public void channelRead(ChannelHandlerContext ctx, ByteBuf msgByteBuf) {
        InetSocketAddress addr = (InetSocketAddress) ctx.channel().localAddress();
        AppDataCollector collector = AppDataCollector.getCollector(addr.getPort());
        collector.incrementReadBytes(msgByteBuf.readableBytes());
        collector.incrementReadMsgs(1L);
    }

    @Override
    public void write(ChannelHandlerContext ctx, ByteBuf msgByteBuf, ChannelPromise promise) {
        InetSocketAddress addr = (InetSocketAddress) ctx.channel().localAddress();
        AppDataCollector collector = AppDataCollector.getCollector(addr.getPort());
        collector.incrementWriteBytes(msgByteBuf.readableBytes());
        collector.incrementWriteMsgs(1L);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        InetSocketAddress addr = (InetSocketAddress) ctx.channel().localAddress();
        AppDataCollector collector = AppDataCollector.getCollector(addr.getPort());
        collector.getChannels().decrementAndGet();
    }


}
