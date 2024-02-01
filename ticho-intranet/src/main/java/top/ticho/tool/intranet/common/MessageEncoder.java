package top.ticho.tool.intranet.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import top.ticho.tool.intranet.constant.CommConst;
import top.ticho.tool.intranet.entity.Message;


/**
 * 消息编码器
 * <p>
 * 将Message对象转换为符合特定协议的字节流，方便在网络中传输和解码。
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class MessageEncoder extends MessageToByteEncoder<Message> {

    /**
     * 首先计算消息体的长度，包括消息类型、序列号和URI的长度。
     * 如果URI字段不为null，则将URI转换为字节数组，并计算URI的长度。
     * 如果数据字段不为null，则计算数据的长度。
     * 接下来，在ByteBuf中写入消息体的总长度（不包含长度字段的长度）。
     * 然后，写入消息类型、序列号和URI的长度。
     * 如果URI字段不为null，则写入URI的长度和字节数组。
     * 如果URI字段为null，则写入0x00表示URI长度为0。
     * 最后，如果数据字段不为null，则写入数据。
     *
     * @param ctx     ctx
     * @param message 消息
     * @param bufOut  bufOut
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Message message, ByteBuf bufOut) {
        // 计算消息体的长度
        int messageLength = CommConst.TYPE_SIZE + CommConst.SERIAL_SIZE + CommConst.URI_LEN_SIZE;
        byte[] uriBytes = null;
        if (message.getUri() != null) {
            // 如果URI不为null，则将URI转换为字节数组，并计算URI的长度
            uriBytes = message.getUri().getBytes();
            messageLength += uriBytes.length;
        }
        if (message.getData() != null) {
            // 如果数据不为null，则计算数据的长度
            messageLength += message.getData().length;
        }
        // 1-写入消息体的总长度（不包含长度字段的长度） [int字节数为4] 4
        bufOut.writeInt(messageLength);
        // 2-写入消息类型 [byte字节数为1] 5
        bufOut.writeByte(message.getType());
        // 3-写入消息序列号 [long字节数为8] 13
        bufOut.writeLong(message.getSerial());
        // 4-写入URL长度 和 数据
        if (uriBytes != null) {
            // 4.1 写入URL长度 [byte字节数为1]
            bufOut.writeByte((byte) uriBytes.length);
            // 4.1 写入URL数据 uriBytes.length
            bufOut.writeBytes(uriBytes);
        } else {
            // 如果URI为null，则写入0x00表示长度为0 [byte字节数为1]
            bufOut.writeByte((byte) 0x00);
        }
        if (message.getData() != null) {
            // 如果数据不为null，则写入数据
            bufOut.writeBytes(message.getData());
        }
    }

}
