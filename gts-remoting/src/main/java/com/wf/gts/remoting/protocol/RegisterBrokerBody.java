package com.wf.gts.remoting.protocol;

import java.util.ArrayList;
import java.util.List;

public class RegisterBrokerBody extends RemotingSerializable{
  
  private TopicConfigSerializeWrapper topicConfigSerializeWrapper = new TopicConfigSerializeWrapper();
  private List<String> filterServerList = new ArrayList<String>();

  public TopicConfigSerializeWrapper getTopicConfigSerializeWrapper() {
      return topicConfigSerializeWrapper;
  }

  public void setTopicConfigSerializeWrapper(TopicConfigSerializeWrapper topicConfigSerializeWrapper) {
      this.topicConfigSerializeWrapper = topicConfigSerializeWrapper;
  }

  public List<String> getFilterServerList() {
      return filterServerList;
  }

  public void setFilterServerList(List<String> filterServerList) {
      this.filterServerList = filterServerList;
  }

}
