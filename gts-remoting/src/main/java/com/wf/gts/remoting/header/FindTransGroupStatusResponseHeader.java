package com.wf.gts.remoting.header;

import com.wf.gts.remoting.CommandCustomHeader;
import com.wf.gts.remoting.annotation.CFNullable;
import com.wf.gts.remoting.exception.RemotingCommandException;

public class FindTransGroupStatusResponseHeader implements CommandCustomHeader{
  
      @CFNullable
      private Integer status;
    
      @Override
      public void checkFields() throws RemotingCommandException {
      }
    
      public Integer getStatus() {
        return status;
      }
    
      public void setStatus(Integer status) {
        this.status = status;
      }
}
