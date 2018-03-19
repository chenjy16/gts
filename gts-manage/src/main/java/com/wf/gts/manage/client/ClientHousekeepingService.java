package com.wf.gts.manage.client;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wf.gts.remoting.ChannelEventListener;
import com.wf.gts.remoting.core.ThreadFactoryImpl;

import io.netty.channel.Channel;

public class ClientHousekeepingService implements ChannelEventListener {
  
    private static final Logger log = LoggerFactory.getLogger(ClientHousekeepingService.class);
    
    private final ProducerManager producerManager;

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl("ClientHousekeepingScheduledThread"));

    public ClientHousekeepingService(final ProducerManager producerManager) {
        this.producerManager = producerManager;
    }

    public void start() {

        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    ClientHousekeepingService.this.scanExceptionChannel();
                } catch (Throwable e) {
                    log.error("Error occurred when scan not active client channels.", e);
                }
            }
        }, 1000 * 10, 1000 * 10, TimeUnit.MILLISECONDS);
    }

    private void scanExceptionChannel() {
        this.producerManager.scanNotActiveChannel();
    }

    public void shutdown() {
        this.scheduledExecutorService.shutdown();
    }

    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {

    }

    @Override
    public void onChannelClose(String remoteAddr, Channel channel) {
        this.producerManager.doChannelCloseEvent(remoteAddr, channel);
    }

    @Override
    public void onChannelException(String remoteAddr, Channel channel) {
        this.producerManager.doChannelCloseEvent(remoteAddr, channel);
    }

    @Override
    public void onChannelIdle(String remoteAddr, Channel channel) {
        this.producerManager.doChannelCloseEvent(remoteAddr, channel);
    }
}
