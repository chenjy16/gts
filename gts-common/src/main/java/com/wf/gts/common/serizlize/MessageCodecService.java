package com.wf.gts.common.serizlize;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 *  netty数据序列化服务
 */
public interface MessageCodecService {

    int MESSAGE_LENGTH = 4;

    void encode(final ByteBuf out, final Object message) throws IOException;

    Object decode(byte[] body) throws IOException;
}
