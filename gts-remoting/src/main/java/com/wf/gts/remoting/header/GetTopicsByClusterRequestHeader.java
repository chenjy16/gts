package com.wf.gts.remoting.header;
import com.wf.gts.remoting.CommandCustomHeader;
import com.wf.gts.remoting.annotation.CFNotNull;
import com.wf.gts.remoting.exception.RemotingCommandException;



public class GetTopicsByClusterRequestHeader implements CommandCustomHeader {
  
  
  @CFNotNull
  private String cluster;

  @Override
  public void checkFields() throws RemotingCommandException {
  }

  public String getCluster() {
      return cluster;
  }

  public void setCluster(String cluster) {
      this.cluster = cluster;
  }

}
