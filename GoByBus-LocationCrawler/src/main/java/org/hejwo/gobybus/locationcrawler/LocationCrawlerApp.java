package org.hejwo.gobybus.locationcrawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class LocationCrawlerApp {

    public static void main(String[] args) {
        SpringApplication.run(LocationCrawlerApp.class, args);
    }

}
