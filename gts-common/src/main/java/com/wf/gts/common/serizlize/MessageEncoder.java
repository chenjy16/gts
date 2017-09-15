package com.wf.gts.common.serizlize;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 继承netty的MessageToByteEncoder,自定义反序列化
 */
public abstract class MessageEncoder extends MessageToByteEncoder<Object> {

    private MessageCodecService util = null;

    public MessageEncoder(final MessageCodecService util) {
        this.util = util;
    }

    protected void encode(final ChannelHandlerContext ctx, final Object msg, final ByteBuf out) throws Exception {
        util.encode(out, msg);
    }
}

