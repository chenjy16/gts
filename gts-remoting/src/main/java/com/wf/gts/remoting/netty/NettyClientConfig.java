package com.wf.gts.remoting.netty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NettyClientConfig {
  
    /**
     * Worker thread number
     */
    private int clientWorkerThreads = 4;
    private int clientCallbackExecutorThreads = Runtime.getRuntime().availableProcessors();
    private int clientOnewaySemaphoreValue = 65535;
    private int clientAsyncSemaphoreValue = 65535;
    private int connectTimeoutMillis = 3000;
    private long channelNotActiveInterval = 1000 * 60;

    private int clientChannelMaxIdleTimeSeconds = 120;
    
    private int clientSocketSndBufSize = 65535; //64K
    private int clientSocketRcvBufSize = 65535; //64K
    
    private boolean clientPooledByteBufAllocatorEnable = false;
    private boolean clientCloseSocketIfTimeout = false;
    private boolean useTLS;

    
}
