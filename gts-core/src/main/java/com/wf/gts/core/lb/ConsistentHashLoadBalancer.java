package com.wf.gts.core.lb;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import com.wf.gts.remoting.protocol.LiveManageInfo;


/**
 * 一致性hash算法，同样的请求（第一参数）会打到同样的节点
 */
public class ConsistentHashLoadBalancer  {


    private ConcurrentHashMap<String, Selector> selectorCache = new ConcurrentHashMap<String, Selector>();

    public LiveManageInfo doSelect(String clientIp,List<LiveManageInfo> liveManageInfos) {
        int hashcode = liveManageInfos.hashCode(); // 判断是否同样的服务列表
        Selector selector = selectorCache.get(clientIp);
        if (selector == null // 原来没有
            ||
            selector.getHashCode() != hashcode) { // 或者服务列表已经变化
          
            selector = new Selector(liveManageInfos, hashcode);
            
            selectorCache.put(clientIp, selector);
        }
        return selector.select(clientIp);
    }
       
}
