package com.xiaomi.be.example.factorial;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class FactorialFrontendMain {

    public static void main(String[] args) {
        final int num = 1000;

        final Config config = ConfigFactory.parseString("akka.cluster.roles = [frontend]")
                .withFallback(ConfigFactory.load("ex-factorial"));

        final ActorSystem system = ActorSystem.create("FactorialSystem", config.getConfig("ClusterSystem"));

        system.log().info("Factorials will start when 2 backend members in the cluster.");

        Cluster.get(system).registerOnMemberUp(new Runnable() {
            @Override
            public void run() {
                ActorRef ref = system.actorOf(Props.create(FactorialFrontend.class, false));
                ref.tell(num, ActorRef.noSender());
            }
        });
    }
}
