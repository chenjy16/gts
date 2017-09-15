package com.wf.gts.common.serizlize.kryo;
import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.wf.gts.common.beans.HeartBeat;



public class KryoPoolFactory {

    private static volatile KryoPoolFactory poolFactory = null;

    private KryoFactory factory = () -> {
        Kryo kryo = new Kryo();
        kryo.setReferences(false);
        kryo.register(HeartBeat.class);
        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        return kryo;
    };

    private KryoPool pool = new KryoPool.Builder(factory).build();

    private KryoPoolFactory() {
    }

    public static KryoPool getKryoPoolInstance() {
        if (poolFactory == null) {
            synchronized (KryoPoolFactory.class) {
                if (poolFactory == null) {
                    poolFactory = new KryoPoolFactory();
                }
            }
        }
        return poolFactory.getPool();
    }

    public KryoPool getPool() {
        return pool;
    }
}

