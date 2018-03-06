package com.wf.gts.remoting;
import com.wf.gts.remoting.exception.RemotingCommandException;

public interface CommandCustomHeader {
    void checkFields() throws RemotingCommandException;
}
