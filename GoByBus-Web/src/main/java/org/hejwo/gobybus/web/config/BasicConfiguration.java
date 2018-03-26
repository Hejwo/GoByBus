package org.hejwo.gobybus.web.config;

import io.vavr.jackson.datatype.VavrModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class BasicConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilder builderCustomizer() {
        return Jackson2ObjectMapperBuilder.json().modules(new VavrModule());
    }

    @Bean
    public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter(Jackson2ObjectMapperBuilder builder) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(builder.build());

        return converter;
    }

}
