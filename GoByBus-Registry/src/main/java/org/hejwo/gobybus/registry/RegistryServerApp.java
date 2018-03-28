package org.hejwo.gobybus.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class RegistryServerApp {

    public static void main(String[] args) {
        SpringApplication.run(RegistryServerApp.class, args);
    }

}
