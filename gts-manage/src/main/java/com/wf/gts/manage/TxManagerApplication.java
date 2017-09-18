package com.wf.gts.manage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;


@EnableEurekaServer
@SpringBootApplication
public class TxManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TxManagerApplication.class, args);
    }

}
