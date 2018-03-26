package org.hejwo.gobybus.locationcrawler.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.hejwo.gobybus.locationcrawler.jobs.LocationDataImportJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

@Configuration
@Slf4j
class ApplicationConfig {

    @Bean
    @Profile(value = "native")
    public KafkaProducer<String, String> kafkaProducer(KafkaLocationProducerConfig kafkaLocationProducerConfig) {
        return new KafkaProducer<>(kafkaLocationProducerConfig.asProperties());
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(3);
        return taskScheduler;
    }

    @Autowired
    @ConfigurationProperties()
    public void scheduleReadPortionJob(TaskScheduler taskScheduler,
                                       LocationDataImportJob locationDataImportJob,
                                       @Value("${openData.warsawApi.location.crawler.cron}") String cronExpression) {
        taskScheduler.schedule(locationDataImportJob, newCronTrigger(cronExpression));
    }

    private Trigger newCronTrigger(String cronExpression) {
        return new CronTrigger(cronExpression);
    }

}
