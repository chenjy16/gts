package com.wf.gts.common.serizlize;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;


/**
 *  继承netty的decoder,自定义序列化
 */
public  abstract class MessageDecoder extends ByteToMessageDecoder {

    final public static int MESSAGE_LENGTH = MessageCodecService.MESSAGE_LENGTH;
    private MessageCodecService util = null;

    public MessageDecoder(final MessageCodecService service) {
        this.util = service;
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < MessageDecoder.MESSAGE_LENGTH) {
            return;
        }
        in.markReaderIndex();
        int messageLength = in.readInt();
        if (messageLength < 0) {
            ctx.close();
        }
        if (in.readableBytes() < messageLength) {
            in.resetReaderIndex();
            return;
        } else {
            byte[] messageBody = new byte[messageLength];
            in.readBytes(messageBody);

            try {
                Object obj = util.decode(messageBody);
                out.add(obj);
            } catch (IOException ex) {
                Logger.getLogger(MessageDecoder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

