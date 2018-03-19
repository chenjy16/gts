package com.wf.gts.remoting.protocol;

public class HeartbeatData extends RemotingSerializable{
  
  private String clientID;

  public String getClientID() {
    return clientID;
  }

  public void setClientID(String clientID) {
    this.clientID = clientID;
  }
  

}
