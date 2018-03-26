package org.hejwo.gobybus.busstopcrawler;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import akka.testkit.javadsl.TestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class AbstractActorTest {

    protected static ActorSystem system;

    @BeforeClass
    public static void classSetup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void classTeardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    protected <T extends AbstractActor> TestActorRef<T> createTestActorRef(String name, Class<T> actorClass, Object... dependencies) {
        int randomSeed = (int) (Math.random() * 100);
        Props props = Props.create(actorClass, dependencies);
        TestActorRef<T> ref = TestActorRef.create(system, props, name + randomSeed);
        return ref;
    }

    protected void stopActor(TestActorRef<? extends AbstractActor> actorRef) {
        actorRef.tell(PoisonPill.getInstance(), null);
    }

}
