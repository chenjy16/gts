package com.wf.gts.core.exception;

public class SocketTimeoutException extends Exception{
  
  private static final long serialVersionUID = -7802390753013728320L;

  public SocketTimeoutException(String message) {
    super(message);
  }
  
  public SocketTimeoutException(String message, Throwable cause) {
      super(message, cause);
  }

}
