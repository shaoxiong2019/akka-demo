package com.xiaomi.be.route;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.routing.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 请参考： http://udn.yyuap.com/doc/akka-doc-cn/2.3.6/scala/book/chapter3/06_routing.html
 */
public final class RouteSpace {
    static class Task implements Serializable {
        private static final long serialVersionUID = 1L;
        public final String payload;

        public Task(String payload) {
            this.payload = payload;
        }
    }

    class Worker extends UntypedActor {
        @Override
        public void onReceive(Object message) throws Exception {
            // do something
        }
    }


    class Master extends UntypedActor {
        Router router;

        {
            List<Routee> list = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Props props = Props.create(Worker.class, "worker_" + i);
                ActorRef work = getContext().actorOf(props);
                list.add(new ActorRefRoutee(work));
            }
            router = new Router(new RoundRobinRoutingLogic(), list);
        }

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof Task) {
                router.route(message, getSender());
            } else if (message instanceof Terminated) {
                router.removeRoutee(((Terminated) message).getActor());
                ActorRef r = getContext().actorOf(Props.create(Worker.class));
                getContext().watch(r);
                router = router.addRoutee(new ActorRefRoutee(r));
            }
        }
    }


}


