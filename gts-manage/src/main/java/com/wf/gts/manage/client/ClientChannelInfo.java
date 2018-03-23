package com.wf.gts.manage.client;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientChannelInfo {
    private final Channel channel;
    private final String clientId;
    private volatile long lastUpdateTimestamp = System.currentTimeMillis();

    public ClientChannelInfo(Channel channel) {
        this(channel, null);
    }

    public ClientChannelInfo(Channel channel, String clientId) {
        this.channel = channel;
        this.clientId = clientId;
    }


 
}
