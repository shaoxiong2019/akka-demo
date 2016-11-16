package com.xiaomi.be.example.factorial;

import akka.actor.ActorRef;
import akka.actor.ReceiveTimeout;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.FromConfig;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class FactorialFrontend extends UntypedActor {
    LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    int num;
    boolean repeat;

    public FactorialFrontend(int num, boolean repeat) {
        this.num = num;
        this.repeat = repeat;
    }

    ActorRef backend = getContext().actorOf(FromConfig.getInstance().props(), "factorialBackendRouter");

    @Override
    public void preStart() throws Exception {
        sendJobs();
        getContext().setReceiveTimeout(Duration.create(10, TimeUnit.SECONDS));
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof FactorialResult) {
            FactorialResult result = (FactorialResult) message;
            if (result.n == num) {
                System.out.println("计算结果：" + result.factorial.toString());
                if (repeat) {
                    sendJobs();
                } else {
                    getContext().stop(getSelf());
                }
            }
        } else if (message instanceof ReceiveTimeout) {
            LOG.info("Timeout");
            sendJobs();
        } else {
            unhandled(message);
        }
    }

    void sendJobs() {
        LOG.info("Start job factorials up to [{}]" + num);
        for (int i = 1; i < num; i++) {
            backend.tell(i, getSelf());
        }
    }
}
