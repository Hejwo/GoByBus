package org.hejwo.gobybus.locationviewer.configuration;

import lombok.extern.slf4j.Slf4j;
import org.hejwo.gobybus.locationviewer.jobs.ConsumerJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

@Configuration
@Slf4j
public class QuartzConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        return taskScheduler;
    }

    @Autowired
    public void scheduleReadPortionJob(@Value("${readPortionCron}") String cronExpression,
                                       TaskScheduler taskScheduler,
                                       ConsumerJob consumerJob) {
        CronTrigger cronTrigger = new CronTrigger(cronExpression);
        taskScheduler.schedule(consumerJob, cronTrigger);
    }

}
