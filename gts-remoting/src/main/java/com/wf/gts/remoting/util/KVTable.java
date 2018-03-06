package com.wf.gts.remoting.util;
import java.util.HashMap;
import com.wf.gts.remoting.protocol.RemotingSerializable;



public class KVTable extends RemotingSerializable {
  private HashMap<String, String> table = new HashMap<String, String>();

  public HashMap<String, String> getTable() {
      return table;
  }

  public void setTable(HashMap<String, String> table) {
      this.table = table;
  }
}