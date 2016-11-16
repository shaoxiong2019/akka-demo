package com.xiaomi.be.remote;


import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.Serializable;

public class PrintActor extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private int msgCount;

    @Override
    public void onReceive(Object message) throws Exception {
        log.info("receive message " + message);
        if (message instanceof PrintMessage) {
            System.out.println(((PrintMessage) message).getData());
        } else {
            unhandled(message);
        }
    }

    public static class PrintMessage implements Serializable {
        private String data;

        public PrintMessage(String data) {
            this.data = data;
        }

        public String getData() {
            return data;
        }

        @Override
        public String toString() {
            return "PrintMessage{" +
                    "data='" + data + '\'' +
                    '}';
        }
    }
}
