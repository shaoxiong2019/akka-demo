package com.xiaomi.be.mailbox;

import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.dispatch.PriorityGenerator;
import akka.dispatch.UnboundedPriorityMailbox;
import com.typesafe.config.Config;

/**
 * 如何实例化 PriorityMailBox
 */
public class MyPriorityMailBox extends UnboundedPriorityMailbox{

    public MyPriorityMailBox(ActorSystem.Settings settings, Config config) {

        super(new PriorityGenerator() {
            @Override
            public int gen(Object message) {
                if (message instanceof PoisonPill){
                    return 4;
                } else {
                    return 1;
                }
            }
        });
    }
}
