package com.wf.gts.remoting;
import com.wf.gts.remoting.netty.ResponseFuture;

public interface InvokeCallback {
    void operationComplete(final ResponseFuture responseFuture);
}
