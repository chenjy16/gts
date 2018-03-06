package com.wf.gts.nameserver.header;
import com.wf.gts.remoting.CommandCustomHeader;
import com.wf.gts.remoting.annotation.CFNotNull;
import com.wf.gts.remoting.exception.RemotingCommandException;

public class GetKVListByNamespaceRequestHeader implements CommandCustomHeader {
    @CFNotNull
    private String namespace;

    @Override
    public void checkFields() throws RemotingCommandException {
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
