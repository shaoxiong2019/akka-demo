package com.xiaomi.be.remote;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.typesafe.config.ConfigFactory;

public class ServerSystem {
    static ActorSystem system = ActorSystem.create("server-system", ConfigFactory.load("./remote.conf").getConfig("ServerSystem"));
    static LoggingAdapter LOG = Logging.getLogger(system, "MAIN");

    public static void main(String[] args) throws InterruptedException {
        ActorRef ref = system.actorOf(Props.create(PrintActor.class), "printActor");
        while (true) {
            LOG.info("server started....");
            Thread.sleep(300 * 1000);
        }
    }
}
