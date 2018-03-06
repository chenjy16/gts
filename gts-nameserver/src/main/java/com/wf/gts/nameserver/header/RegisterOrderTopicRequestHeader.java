package com.wf.gts.nameserver.header;
import com.wf.gts.remoting.CommandCustomHeader;
import com.wf.gts.remoting.annotation.CFNotNull;
import com.wf.gts.remoting.exception.RemotingCommandException;



public class RegisterOrderTopicRequestHeader implements CommandCustomHeader {
    @CFNotNull
    private String topic;
    @CFNotNull
    private String orderTopicString;

    @Override
    public void checkFields() throws RemotingCommandException {

    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getOrderTopicString() {
        return orderTopicString;
    }

    public void setOrderTopicString(String orderTopicString) {
        this.orderTopicString = orderTopicString;
    }
}
