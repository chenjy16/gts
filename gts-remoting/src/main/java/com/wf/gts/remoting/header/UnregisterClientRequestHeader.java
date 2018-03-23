package com.wf.gts.remoting.header;
import com.wf.gts.remoting.CommandCustomHeader;
import com.wf.gts.remoting.annotation.CFNotNull;
import com.wf.gts.remoting.annotation.CFNullable;
import com.wf.gts.remoting.exception.RemotingCommandException;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnregisterClientRequestHeader implements CommandCustomHeader {
    @CFNotNull
    private String clientID;

    @CFNullable
    private String producerGroup;
    @CFNullable
    private String consumerGroup;

   

    @Override
    public void checkFields() throws RemotingCommandException {

    }
}
