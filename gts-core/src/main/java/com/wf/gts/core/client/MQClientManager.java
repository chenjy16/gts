package com.wf.gts.core.client;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wf.gts.core.config.ClientConfig;
import com.wf.gts.remoting.RPCHook;


public class MQClientManager {
  
    private final static Logger log = LoggerFactory.getLogger(MQClientManager.class);
    private static MQClientManager instance = new MQClientManager();
    private AtomicInteger factoryIndexGenerator = new AtomicInteger();
    private ConcurrentMap<String/* clientId */, MQClientInstance> factoryTable =new ConcurrentHashMap<String, MQClientInstance>();
    
    private MQClientManager() {
    }
    
    public static MQClientManager getInstance() {
        return instance;
    }
    public MQClientInstance getAndCreateMQClientInstance(final ClientConfig clientConfig) {
        return getAndCreateMQClientInstance(clientConfig, null);
    }

    public MQClientInstance getAndCreateMQClientInstance(final ClientConfig clientConfig, RPCHook rpcHook) {
        //生成规则需要进一步完善
        String clientId = clientConfig.buildMQClientId();
        MQClientInstance instance = this.factoryTable.get(clientId);
        if (null == instance) {
            instance =new MQClientInstance(clientConfig.cloneClientConfig(), rpcHook);
            MQClientInstance prev = this.factoryTable.putIfAbsent(clientId, instance);
            if (prev != null) {
                instance = prev;
                log.warn("Returned Previous MQClientInstance for clientId:[{}]", clientId);
            } else {
                log.info("Created new MQClientInstance for clientId:[{}]", clientId);
            }
        }
        return instance;
    }

    public void removeClientFactory(final String clientId) {
        this.factoryTable.remove(clientId);
    }
}
