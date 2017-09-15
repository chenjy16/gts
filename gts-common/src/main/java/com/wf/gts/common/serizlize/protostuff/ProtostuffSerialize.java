package com.wf.gts.common.serizlize.protostuff;
import java.io.InputStream;
import java.io.OutputStream;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.wf.gts.common.beans.HeartBeat;
import com.wf.gts.common.serizlize.NettyTransferSerialize;

/**
 * Protostuff序列化实现
 */
public class ProtostuffSerialize implements NettyTransferSerialize {
    private static SchemaCache cachedSchema = SchemaCache.getInstance();
    private static Objenesis objenesis = new ObjenesisStd(true);
    private static <T> Schema<T> getSchema(Class<T> cls) {
        return (Schema<T>) cachedSchema.get(cls);
    }

    public Object deserialize(InputStream input) {
        try {
            HeartBeat message = objenesis.newInstance(HeartBeat.class);
            Schema<HeartBeat> schema = getSchema(HeartBeat.class);
            ProtostuffIOUtil.mergeFrom(input, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void serialize(OutputStream output, Object object) {
        Class cls = object.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema schema = getSchema(cls);
            ProtostuffIOUtil.writeTo(output, object, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }
}

