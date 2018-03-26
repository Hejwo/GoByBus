package org.hejwo.gobybus.busstopcrawler.configuration;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.hejwo.gobybus.busstopcrawler.actors.BusStopLinesDownloadActor;
import org.hejwo.gobybus.busstopcrawler.actors.BusStopTimetableDownloadActor;
import org.hejwo.gobybus.busstopcrawler.actors.BusStopsDownloadActor;
import org.hejwo.gobybus.busstopcrawler.actors.BusStopsDownloadSupervisorActor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.hejwo.gobybus.busstopcrawler.actors.ext.SpringExtension.SPRING_EXTENSION_PROVIDER;

@Configuration
public class AkkaConfiguration {

    @Bean
    public ActorSystem actorSystem(ApplicationContext applicationContext) {
        ActorSystem system = ActorSystem.create("bus-stops-crawler-system");
        SPRING_EXTENSION_PROVIDER.get(system).initialize(applicationContext);
        return system;
    }

    @Bean
    public ActorRef busStopsDownloadSupervisorActorRef(ActorSystem system) {
//        QuartzSchedulerExtension.get(system).schedule("EveryHour", busStopsDownloadActorRef, BusStopsDownloadActor.message());
        Props props = SPRING_EXTENSION_PROVIDER.get(system).props(BusStopsDownloadSupervisorActor.getBeanName());
        return system.actorOf(props, "busStopsDownloadSupervisorActor");
    }

    @Bean
    public ActorRef busStopsDownloadActorRef(ActorSystem system) {
        Props props = SPRING_EXTENSION_PROVIDER.get(system).props(BusStopsDownloadActor.getBeanName());
        return system.actorOf(props, "busStopsDownloadActor");
    }

    @Bean
    public ActorRef busStopLinesDownloadActorRef(ActorSystem system) {
        Props props = SPRING_EXTENSION_PROVIDER.get(system).props(BusStopLinesDownloadActor.getBeanName());
        return system.actorOf(props, "busStopLinesDownloadActor");
    }

    @Bean
    public ActorRef busStopTimetableDownloadActorRef(ActorSystem system) {
        Props props = SPRING_EXTENSION_PROVIDER.get(system).props(BusStopTimetableDownloadActor.getBeanName());
        return system.actorOf(props, "busStopTimetableDownloadActor");
    }

}
