package com.xiaomi.be.persistence;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.typesafe.config.ConfigFactory;
import com.xiaomi.be.persistence.ProcessorActor.Cmd;


public class PersistenceSystem {

    static final ActorSystem system = ActorSystem.create("actor-server", ConfigFactory.load("application.conf").getConfig("basic-config"));

    static LoggingAdapter LOG = Logging.getLogger(system, "SYSTEM");

    public static void main(String... args) throws Exception {
        final ActorRef handler = system.actorOf(Props.create(EventHandler.class));
        // 订阅
        system.eventStream().subscribe(handler, Event.class);

        final ActorRef actorRef = system.actorOf(Props.create(ProcessorActor.class), "eventsourcing-processor");

        actorRef.tell(new Cmd("CMD 1"), ActorRef.noSender());
        actorRef.tell(new Cmd("CMD 2"), ActorRef.noSender());
        actorRef.tell(new Cmd("CMD 3"), ActorRef.noSender());
        actorRef.tell("snapshot", ActorRef.noSender());//发送保存快照命令
        actorRef.tell(new Cmd("CMD 4"), ActorRef.noSender());
        actorRef.tell(new Cmd("CMD 5"), ActorRef.noSender());
        actorRef.tell("printstate", ActorRef.noSender());

        Thread.sleep(5000);

        LOG.info("Actor System Shutdown Starting...");

        system.shutdown();
    }

    static class EventHandler extends UntypedActor {
        LoggingAdapter log = Logging.getLogger(getContext().system(), this);

        @Override
        public void onReceive(Object message) throws Exception {
            log.info("Handled Event: " + message);
        }
    }
}
