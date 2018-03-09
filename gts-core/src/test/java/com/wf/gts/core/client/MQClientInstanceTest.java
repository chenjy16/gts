package com.wf.gts.core.client;
import java.io.IOException;

import org.junit.Test;

import com.wf.gts.core.config.TxConfig;


public class MQClientInstanceTest {

    @Test
    public void testTopicRouteData2TopicPublishInfo() throws IOException {
      TxConfig txConfig=new TxConfig();
      MQClientInstance mqClientInstance=new MQClientInstance(txConfig, null);
      mqClientInstance.start();
      System.in.read();
    }

 
}