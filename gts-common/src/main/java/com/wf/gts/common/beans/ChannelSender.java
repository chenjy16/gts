package com.wf.gts.common.beans;
import java.util.List;

import io.netty.channel.Channel;

/**
 * 渠道发送命令者
 */
public class ChannelSender {
  

  /**
   * 模块netty 长连接渠道
   */
  private  Channel channel;


  private  String tmDomain;

  private List<TxTransactionItem> itemList;

  public Channel getChannel() {
      return channel;
  }

  public void setChannel(Channel channel) {
      this.channel = channel;
  }

  public String getTmDomain() {
      return tmDomain;
  }

  public void setTmDomain(String tmDomain) {
      this.tmDomain = tmDomain;
  }

  public List<TxTransactionItem> getItemList() {
      return itemList;
  }

  public void setItemList(List<TxTransactionItem> itemList) {
      this.itemList = itemList;
  }

}
