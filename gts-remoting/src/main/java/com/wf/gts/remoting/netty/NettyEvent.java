package com.wf.gts.remoting.netty;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NettyEvent {
    private  NettyEventType type;
    private  String remoteAddr;
    private  Channel channel;

    public NettyEvent(NettyEventType type, String remoteAddr, Channel channel) {
        this.type = type;
        this.remoteAddr = remoteAddr;
        this.channel = channel;
    }


    @Override
    public String toString() {
        return "NettyEvent [type=" + type + ", remoteAddr=" + remoteAddr + ", channel=" + channel + "]";
    }
}
