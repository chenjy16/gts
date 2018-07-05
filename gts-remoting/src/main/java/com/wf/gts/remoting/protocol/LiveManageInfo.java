package com.wf.gts.remoting.protocol;

import java.util.List;

public class LiveManageInfo extends RemotingSerializable {
  
    List<GtsManageLiveAddr> gtsManageLiveAddrs;

    public List<GtsManageLiveAddr> getGtsManageLiveAddrs() {
      return gtsManageLiveAddrs;
    }

    public void setGtsManageLiveAddrs(List<GtsManageLiveAddr> gtsManageLiveAddrs) {
      this.gtsManageLiveAddrs = gtsManageLiveAddrs;
    }

    
}
