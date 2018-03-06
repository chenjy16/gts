package com.wf.gts.nameserver.header;
import com.wf.gts.remoting.CommandCustomHeader;
import com.wf.gts.remoting.annotation.CFNullable;
import com.wf.gts.remoting.exception.RemotingCommandException;

public class GetKVConfigResponseHeader implements CommandCustomHeader {
    @CFNullable
    private String value;

    @Override
    public void checkFields() throws RemotingCommandException {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
