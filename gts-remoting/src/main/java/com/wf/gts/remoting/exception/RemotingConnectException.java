package com.wf.gts.remoting.exception;



public class RemotingConnectException extends RemotingException {
    private static final long serialVersionUID = -5565366231695911316L;

    public RemotingConnectException(String addr) {
        this(addr, null);
    }

    public RemotingConnectException(String addr, Throwable cause) {
        super("connect to <" + addr + "> failed", cause);
    }
}
