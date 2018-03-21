package com.wf.gts.nameserver.route;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import com.wf.gts.remoting.core.MixAll;

public class GtsManageData  {
    private String cluster;
    private String manageName;
    private HashMap<Long/* manageId */, String/* manage address */> manageAddrs;

    private final Random random = new Random();

    public GtsManageData() {

    }

    public GtsManageData(String cluster, String manageName, HashMap<Long, String> manageAddrs) {
        this.cluster = cluster;
        this.manageName = manageName;
        this.manageAddrs = manageAddrs;
    }


    public String selectBrokerAddr() {
        String addr = this.manageAddrs.get(MixAll.MASTER_ID);

        if (addr == null) {
            List<String> addrs = new ArrayList<String>(manageAddrs.values());
            return addrs.get(random.nextInt(addrs.size()));
        }

        return addr;
    }
    
    
    

    public String getCluster() {
      return cluster;
    }

    public void setCluster(String cluster) {
      this.cluster = cluster;
    }

    public String getManageName() {
      return manageName;
    }

    public void setManageName(String manageName) {
      this.manageName = manageName;
    }

    public HashMap<Long, String> getManageAddrs() {
      return manageAddrs;
    }

    public void setManageAddrs(HashMap<Long, String> manageAddrs) {
      this.manageAddrs = manageAddrs;
    }

}
