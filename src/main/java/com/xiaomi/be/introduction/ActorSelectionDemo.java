package com.xiaomi.be.introduction;

import akka.actor.*;

public class ActorSelectionDemo {

    static class DemoActor extends UntypedActor{

        @Override
        public void onReceive(Object message) throws Exception {
            System.out.println("demo actor receive msg: " + message);
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("selection");
        ActorRef demo1 = system.actorOf(Props.create(DemoActor.class),"demo");

        System.out.println(demo1.path());
        ActorSelection selection = system.actorSelection("/user/demo");
        selection.tell("one message" , ActorRef.noSender());

        system.shutdown();
    }
}
