package org.hejwo.gobybus.busstopcrawler.actors;

import akka.actor.AbstractActor;
import akka.actor.AbstractLoggingActor;
import akka.japi.pf.ReceiveBuilder;
import org.hejwo.gobybus.busstopcrawler.configuration.AkkaContextHolder;
import org.hejwo.gobybus.busstopcrawler.integration.WarsawBusStopApi;
import org.hejwo.gobybus.busstopcrawler.repository.BusStopRepository;
import scala.Option;
import scala.concurrent.duration.Duration;

public abstract class AbstractBusStopActor<T> extends AbstractLoggingActor {

    protected final AkkaContextHolder akkaContext;
    protected final Class<T> messageClass;

    protected final String warsawApiKey;
    protected final WarsawBusStopApi warsawBusStopApi;
    protected final BusStopRepository busStopRepository;

    protected AbstractBusStopActor(Class<T> messageClass,
                                   AkkaContextHolder akkaContext,
                                   String warsawApiKey,
                                   WarsawBusStopApi warsawBusStopApi,
                                   BusStopRepository busStopRepository) {
        this.warsawApiKey = warsawApiKey;
        this.akkaContext = akkaContext;
        this.messageClass = messageClass;
        this.warsawBusStopApi = warsawBusStopApi;
        this.busStopRepository = busStopRepository;
    }

    protected static Duration getIncrementedInitialDelay(Duration value, Duration increment) {
        return value.plus(increment);
    }

    @Override
    public void preRestart(Throwable reason, Option<Object> message) {
        akkaContext.scheduleRetry(message, reason);
        log().info("Scheduled retry for message {}, reason: {}", message, reason.getClass().getSimpleName());
    }

    @Override
    public AbstractActor.Receive createReceive() {
        return ReceiveBuilder.create()
                .match(messageClass, this::onDownloadMessage)
                .build();
    }

    protected abstract void onDownloadMessage(T message);

}
