package com.wf.gts.common.serizlize.kryo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.esotericsoftware.kryo.pool.KryoPool;
import com.wf.gts.common.serizlize.MessageCodecService;

import io.netty.buffer.ByteBuf;

/**
 * kryo 序列化实现
 */
public class KryoCodecService implements MessageCodecService {

    private KryoPool pool;

    public KryoCodecService(KryoPool pool) {
        this.pool = pool;
    }

    @Override
    public void encode(ByteBuf out, Object message) throws IOException {
        try( ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
          
            KryoSerialize kryoSerialization = new KryoSerialize(pool);
            kryoSerialization.serialize(byteArrayOutputStream, message);
            byte[] body = byteArrayOutputStream.toByteArray();
            int dataLength = body.length;
            out.writeInt(dataLength);
            out.writeBytes(body);
        } 
    }

    @Override
    public Object decode(byte[] body) throws IOException {
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body)) {
            KryoSerialize kryoSerialization = new KryoSerialize(pool);
            return kryoSerialization.deserialize(byteArrayInputStream);
        }
    }
}
