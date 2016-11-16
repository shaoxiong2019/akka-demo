package com.xiaomi.be.persistence;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.xiaomi.be.persistence.ProcessorActor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SystemDemo {
    public static final Logger log = LoggerFactory.getLogger(System.class);

    public static class EventHandler extends UntypedActor {
        LoggingAdapter log = Logging.getLogger(getContext().system(), this);

        @Override
        public void onReceive(Object message) throws Exception {
            log.info("Handled Event: " + message);
        }
    }

    public static void main(String... args) throws Exception {

        final ActorSystem system = ActorSystem.create("actor-server");

        final ActorRef handler = system.actorOf(Props.create(EventHandler.class));
        // 订阅
        system.eventStream().subscribe(handler, Event.class);

        Thread.sleep(5000);

        final ActorRef actorRef = system.actorOf(Props.create(ProcessorActor.class), "eventsourcing-processor");

        actorRef.tell(new Cmd("CMD 1"), null);
        actorRef.tell(new Cmd("CMD 2"), null);
        actorRef.tell(new Cmd("CMD 3"), null);
        actorRef.tell("snapshot", null);//发送保存快照命令
        actorRef.tell(new Cmd("CMD 4"), null);
        actorRef.tell(new Cmd("CMD 5"), null);
        actorRef.tell("printstate", null);

        Thread.sleep(5000);

        log.debug("Actor System Shutdown Starting...");

        system.shutdown();
    }
}
