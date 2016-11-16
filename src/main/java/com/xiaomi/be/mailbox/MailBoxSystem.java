package com.xiaomi.be.mailbox;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.ConfigFactory;

public class MailBoxSystem {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("mailbox-demo", ConfigFactory.load().getConfig("./mail-box.conf"));

        ActorRef myActor = system.actorOf(Props.create(SingleTypeMessageActor.class,"myActor").withDispatcher("control-aware-dispatcher"));
    }
}
