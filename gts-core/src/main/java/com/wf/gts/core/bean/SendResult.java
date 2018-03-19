package com.wf.gts.core.bean;

public class SendResult {
  
  private SendStatus sendStatus;
  
  

  public SendResult(SendStatus sendStatus) {
    this.sendStatus = sendStatus;
  }

  public SendStatus getSendStatus() {
    return sendStatus;
  }

  public void setSendStatus(SendStatus sendStatus) {
    this.sendStatus = sendStatus;
  }
  
}
