package com.wf.gts.common.serizlize.protostuff;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.wf.gts.common.serizlize.MessageCodecService;

import io.netty.buffer.ByteBuf;

/**
 * Protostuff 实现
 */
public class ProtostuffCodecService implements MessageCodecService {
  
    private ProtostuffSerializePool pool = ProtostuffSerializePool.getProtostuffPoolInstance();
    
    public void encode(final ByteBuf out, final Object message) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
          
            ProtostuffSerialize protostuffSerialization = pool.borrow();
            protostuffSerialization.serialize(byteArrayOutputStream, message);
            byte[] body = byteArrayOutputStream.toByteArray();
            int dataLength = body.length;
            out.writeInt(dataLength);
            out.writeBytes(body);
            pool.restore(protostuffSerialization);
        }
    }
    
    public Object decode(byte[] body) throws IOException {
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body)){
            ProtostuffSerialize protostuffSerialization = pool.borrow();
            Object obj = protostuffSerialization.deserialize(byteArrayInputStream);
            pool.restore(protostuffSerialization);
            return obj;
        }
    }
}

