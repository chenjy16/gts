package com.wf.gts.manage.spring;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.wf.gts.manage.domain.Address;


@Component
public class ApplicationStartListener implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {


    @Override
    public void onApplicationEvent(EmbeddedServletContainerInitializedEvent event) {
        int port = event.getEmbeddedServletContainer().getPort();
        final String host = getHost();
        Address.getInstance()
                .setHost(host)
                .setPort(port)
                .setDomain(String.join(":", host, String.valueOf(port)));

    }


    private String getHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "127.0.0.1";
        }
    }
}
