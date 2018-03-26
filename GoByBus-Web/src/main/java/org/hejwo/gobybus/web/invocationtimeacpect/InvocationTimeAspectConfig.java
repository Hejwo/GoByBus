package org.hejwo.gobybus.web.invocationtimeacpect;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
class InvocationTimeAspectConfig {

    @Bean
    public InvocationTimeAspect invocationTimeAspect() {
        return new InvocationTimeAspect();
    }

}
