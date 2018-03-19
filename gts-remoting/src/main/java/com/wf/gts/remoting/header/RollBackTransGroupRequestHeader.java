package com.wf.gts.remoting.header;
import com.wf.gts.remoting.CommandCustomHeader;
import com.wf.gts.remoting.annotation.CFNotNull;
import com.wf.gts.remoting.exception.RemotingCommandException;

public class RollBackTransGroupRequestHeader implements CommandCustomHeader {
    @CFNotNull
    private String txGroupId;

    @Override
    public void checkFields() throws RemotingCommandException {
    }

    public String getTxGroupId() {
      return txGroupId;
    }

    public void setTxGroupId(String txGroupId) {
      this.txGroupId = txGroupId;
    }

    
}
