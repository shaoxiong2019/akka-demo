package com.xiaomi.be.dispatch;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.google.common.collect.Lists;
import com.typesafe.config.ConfigFactory;

import java.util.List;

public class DispatchDemo {

    static class StartCommand {
        int count;

        public StartCommand(int count) {
            this.count = count;
        }
    }

    static class WriteActor extends UntypedActor {
        @Override
        public void onReceive(Object message) throws Exception {
            System.out.println(Thread.currentThread().getName());
        }
    }

    static class ControllerActor extends UntypedActor {
        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof StartCommand) {
                List<ActorRef> actorRefs = createActors(((StartCommand) message).count);
                for (int i = 0; i < actorRefs.size(); i++) {
                    actorRefs.get(i).tell(new Object(), getSelf());
                }
            }
        }

        private List<ActorRef> createActors(int count) {
            List<ActorRef> actorRefs = Lists.newArrayListWithExpectedSize(count);
//            Props props = Props.create(WriteActor.class).withDispatcher("writer-dispatcher");
            Props props = Props.create(WriteActor.class).withDispatcher("io-dispatcher");
            for (int i = 0; i < count; i++) {
                actorRefs.add(getContext().actorOf(props, "writer_" + i));
            }
            return actorRefs;
        }
    }

    public static void main(String[] args) throws InterruptedException {
//        ActorSystem system = ActorSystem.create("DispatcherDemo", ConfigFactory.load("application.conf").getConfig("dispatcherDemo"));
        ActorSystem system = ActorSystem.create("DispatcherDemo", ConfigFactory.load("application.conf").getConfig("dispatcherDemo"));

        ActorRef controller = system.actorOf(Props.create(ControllerActor.class), "controller");
        controller.tell(new StartCommand(100), ActorRef.noSender());

        Thread.sleep(2 * 1000);
        system.shutdown();
    }
}
