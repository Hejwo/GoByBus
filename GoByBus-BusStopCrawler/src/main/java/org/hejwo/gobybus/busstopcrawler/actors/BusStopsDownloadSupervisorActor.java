package org.hejwo.gobybus.busstopcrawler.actors;

import akka.actor.AbstractLoggingActor;
import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;
import akka.japi.pf.ReceiveBuilder;
import org.hejwo.gobybus.busstopcrawler.configuration.AkkaContextHolder;
import org.hejwo.gobybus.busstopcrawler.configuration.actorprops.BusStopsDownloadProperties;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import scala.concurrent.duration.Duration;

import static org.hejwo.gobybus.busstopcrawler.utils.ClassUtils.getLowerCasedName;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BusStopsDownloadSupervisorActor extends AbstractLoggingActor {

    private final BusStopsDownloadProperties busStopsDownloadProperties;

    public BusStopsDownloadSupervisorActor(AkkaContextHolder akkaContext) {
        this.busStopsDownloadProperties = akkaContext.getBusStopsDownloadProperties();
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .matchAny(this::onForward)
                .build();
    }

    protected void onForward(Object anyMsg) {
        log().info("Invoked: '{}', msg: '{}'.", "BusStopsDownloadSupervisorActor", anyMsg);
        busStopsDownloadProperties.getRef().forward(anyMsg, getContext());
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        // new AllForOneStrategy()
        // when one child fails apply our decision to all childs
        // new OneForOneStrategy()
        // treads ever child individually
        Integer maxRetries = busStopsDownloadProperties.getMaxRetries();
        Duration retriesWindow = busStopsDownloadProperties.getRetriesWindow();

        return new OneForOneStrategy(
                maxRetries, retriesWindow,
                DeciderBuilder
                        .match(Throwable.class, ex -> SupervisorStrategy.restart())
                        .build());
    }

    public static String getBeanName() {
        return getLowerCasedName(BusStopsDownloadSupervisorActor.class);
    }

}
