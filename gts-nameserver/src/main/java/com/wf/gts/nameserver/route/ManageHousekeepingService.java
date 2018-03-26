package com.wf.gts.nameserver.route;
import com.wf.gts.nameserver.NameSrvController;
import com.wf.gts.remoting.ChannelEventListener;

import io.netty.channel.Channel;



public class ManageHousekeepingService implements ChannelEventListener{
    
    private final NameSrvController namesrvController;

    public ManageHousekeepingService(NameSrvController namesrvController) {
        this.namesrvController = namesrvController;
    }

    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {
    }

    @Override
    public void onChannelClose(String remoteAddr, Channel channel) {
        this.namesrvController.getRouteInfoManager().onChannelDestroy(remoteAddr, channel);
    }
    
    @Override
    public void onChannelException(String remoteAddr, Channel channel) {
        this.namesrvController.getRouteInfoManager().onChannelDestroy(remoteAddr, channel);
    }

    @Override
    public void onChannelIdle(String remoteAddr, Channel channel) {
        this.namesrvController.getRouteInfoManager().onChannelDestroy(remoteAddr, channel);
    }
}
