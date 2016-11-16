package com.xiaomi.be.example.factorial;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class FactorialBackendMain {

    public static void main(String[] args) {
        String port = args.length > 0 ? args[0] : "0";

        Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port)
                .withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]"))
                .withFallback(ConfigFactory.load("ex-factorial"));

        ActorSystem system = ActorSystem.create("ClusterSystem", config.getConfig("ClusterSystem"));

        system.actorOf(Props.create(FactorialBackend.class), "factorialBackend");
    }
}
