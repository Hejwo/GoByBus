package org.hejwo.gobybus.busstopcrawler.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    @Bean
    public String warsawApiKey(@Value("${openData.warsawApi.key}") String warsawApiKey) {
        return warsawApiKey;
    }

}
