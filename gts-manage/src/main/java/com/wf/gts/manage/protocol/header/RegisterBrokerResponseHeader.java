package com.wf.gts.manage.protocol.header;
import com.wf.gts.remoting.CommandCustomHeader;
import com.wf.gts.remoting.annotation.CFNullable;
import com.wf.gts.remoting.exception.RemotingCommandException;

public class RegisterBrokerResponseHeader implements CommandCustomHeader {
    @CFNullable
    private String haServerAddr;
    @CFNullable
    private String masterAddr;

    @Override
    public void checkFields() throws RemotingCommandException {
    }

    public String getHaServerAddr() {
        return haServerAddr;
    }

    public void setHaServerAddr(String haServerAddr) {
        this.haServerAddr = haServerAddr;
    }

    public String getMasterAddr() {
        return masterAddr;
    }

    public void setMasterAddr(String masterAddr) {
        this.masterAddr = masterAddr;
    }
}
