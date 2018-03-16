package com.wf.gts.remoting.protocol;

public class RequestCode {

  public static final int SAVE_TRANSGROUP = 10;
  public static final int ADD_TRANS = 11;
  public static final int FIND_TRANSGROUP_STATUS = 12;
  public static final int ROLLBACK_TRANSGROUP = 13;
  public static final int PRE_COMMIT_TRANS = 14;
  public static final int COMMIT_TRANS = 15;
  
  public static final int HEART_BEAT = 34;
  public static final int UNREGISTER_CLIENT=35;
  
  

  public static final int PUT_KV_CONFIG = 100;

  public static final int GET_KV_CONFIG = 101;

  public static final int REGISTER_BROKER = 103;

  public static final int UNREGISTER_BROKER = 104;
  
  public static final int GET_ROUTEINTO_BY_TOPIC = 105;

  public static final int GET_BROKER_CLUSTER_INFO = 106;
  


}
