package com.wf.gts.remoting;



public interface RemotingService {
    void start();

    void shutdown();

    void registerRPCHook(RPCHook rpcHook);
}
