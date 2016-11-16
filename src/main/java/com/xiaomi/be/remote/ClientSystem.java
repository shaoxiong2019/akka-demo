package com.xiaomi.be.remote;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;

public class ClientSystem {

    public static void main(String[] args) throws InterruptedException {
        ActorSystem system = ActorSystem.create("client-system", ConfigFactory.load("./remote.conf").getConfig("client-system"));

        ActorRef ref = system.actorFor("akka.tcp://server-system@127.0.0.1:2552/user/printActor");

        ref.tell(new PrintActor.PrintMessage("client message"), ActorRef.noSender());

        Thread.sleep(5 * 1000);

        system.shutdown();
    }
}
