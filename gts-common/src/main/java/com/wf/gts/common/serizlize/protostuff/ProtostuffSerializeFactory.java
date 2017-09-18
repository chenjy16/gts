package com.wf.gts.common.serizlize.protostuff;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;


public class ProtostuffSerializeFactory extends BasePooledObjectFactory<ProtostuffSerialize> {

    public ProtostuffSerialize create() throws Exception {
        return createProtostuff();
    }

    public PooledObject<ProtostuffSerialize> wrap(ProtostuffSerialize protostuffSerialize) {
        return new DefaultPooledObject<>(protostuffSerialize);
    }

    private ProtostuffSerialize createProtostuff() {
        return new ProtostuffSerialize();
    }
}
