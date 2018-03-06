package com.wf.gts.nameserver.header;
import com.wf.gts.remoting.CommandCustomHeader;
import com.wf.gts.remoting.annotation.CFNotNull;
import com.wf.gts.remoting.exception.RemotingCommandException;

public class WipeWritePermOfBrokerResponseHeader implements CommandCustomHeader {
    @CFNotNull
    private Integer wipeTopicCount;

    @Override
    public void checkFields() throws RemotingCommandException {
    }

    public Integer getWipeTopicCount() {
        return wipeTopicCount;
    }

    public void setWipeTopicCount(Integer wipeTopicCount) {
        this.wipeTopicCount = wipeTopicCount;
    }
}
