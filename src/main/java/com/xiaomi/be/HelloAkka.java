package com.xiaomi.be;

import akka.actor.*;
import scala.concurrent.duration.Duration;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class HelloAkka {

    static class Greet implements Serializable {
    }

    static class WhoToGreet implements Serializable {
        final String who;

        public WhoToGreet(String who) {
            this.who = who;
        }
    }

    class Greeting implements Serializable {
        String message;

        public Greeting(String greeting) {
            this.message = greeting;
        }
    }

    static class  GreeterActor extends UntypedActor {
        String greeting = "";

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof Greeting) {
                greeting = "Hello " + ((Greeting) message).message;
            } else if (message instanceof WhoToGreet) {
                getSender().tell(greeting, getSelf());
            } else {
                unhandled(message);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ActorSystem system = ActorSystem.create("hello-akka-system");
        final ActorRef greeter = system.actorOf(Props.create(GreeterActor.class), "greeter");

        final Inbox inbox = Inbox.create(system);

        greeter.tell(new WhoToGreet("David"), ActorRef.noSender());
        greeter.tell(new Greet(), ActorRef.noSender());
        Greeting greeting = (Greeting) inbox.receive(Duration.create(5, TimeUnit.SECONDS));
        System.out.println(greeting.message);


        greeter.tell(new WhoToGreet("Jack"), ActorRef.noSender());
        greeter.tell(new Greet(), ActorRef.noSender());
    }
}
