package com.wf.gts.remoting.protocol;

import java.util.Collection;

public class ClusterInfo extends RemotingSerializable {
  
    Collection<BrokerLiveInfo> brokerLiveSet;
    public Collection<BrokerLiveInfo> getBrokerLiveSet() {
      return brokerLiveSet;
    }
    public void setBrokerLiveSet(Collection<BrokerLiveInfo> brokerLiveSet) {
      this.brokerLiveSet = brokerLiveSet;
    }
}
