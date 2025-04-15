package com.niyas.offshore_proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OffshoreProxyApplication {


    public static void main(String[] args) {
        SpringApplication.run(OffshoreProxyApplication.class, args);
        new OffshoreProxyServer().start();


    }

}
