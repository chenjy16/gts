package com.wf.gts.remoting.header;

import com.wf.gts.remoting.CommandCustomHeader;
import com.wf.gts.remoting.annotation.CFNullable;
import com.wf.gts.remoting.exception.RemotingCommandException;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FindTransGroupStatusResponseHeader implements CommandCustomHeader{
  
      @CFNullable
      private Integer status;
    
      @Override
      public void checkFields() throws RemotingCommandException {
      }
    
     
}
