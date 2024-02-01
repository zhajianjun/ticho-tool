package top.ticho.tool.intranet.server.collect;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import top.ticho.tool.intranet.server.entity.DataCollector;

import java.net.InetSocketAddress;

/**
 * 数据处理程序
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class DataCollectHandler extends ChannelDuplexHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        InetSocketAddress addr = (InetSocketAddress) ctx.channel().localAddress();
        DataCollector collector = DataCollector.getCollector(addr.getPort());
        collector.incrementReadBytes(((ByteBuf) msg).readableBytes());
        collector.incrementReadMsgs(1L);
        ctx.fireChannelRead(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        InetSocketAddress addr = (InetSocketAddress) ctx.channel().localAddress();
        DataCollector collector = DataCollector.getCollector(addr.getPort());
        ByteBuf byteBuf = (ByteBuf) msg;
        collector.incrementWroteBytes(byteBuf.readableBytes());
        collector.incrementWroteMsgs(1L);
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress addr = (InetSocketAddress) ctx.channel().localAddress();
        DataCollector collector = DataCollector.getCollector(addr.getPort());
        collector.getChannels().incrementAndGet();
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress addr = (InetSocketAddress) ctx.channel().localAddress();
        DataCollector collector = DataCollector.getCollector(addr.getPort());
        collector.getChannels().decrementAndGet();
        super.channelInactive(ctx);
    }
}
