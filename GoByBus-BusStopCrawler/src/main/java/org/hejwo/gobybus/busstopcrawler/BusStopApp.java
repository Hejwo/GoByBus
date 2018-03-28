package org.hejwo.gobybus.busstopcrawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class BusStopApp {

    public static void main(String[] args) {
        SpringApplication.run(BusStopApp.class, args);
    }

    // TODO: 11.12.17, phejwowski, 3 - Think about jobs execution time
}
