package com.xiaomi.be.introduction;

import akka.actor.*;
import akka.actor.dsl.Creators;
import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;

import java.io.Serializable;
import java.text.MessageFormat;
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

    static class Greeting implements Serializable {
        String message;

        public Greeting(String greeting) {
            this.message = greeting;
        }
    }

    static class GreeterActor extends UntypedActor {
        String greeting = "";

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof WhoToGreet) {
                greeting = "Hello " + ((WhoToGreet) message).who;
            } else if (message instanceof Greet) {
                getSender().tell(new Greeting(greeting), getSelf());
            } else {
                unhandled(message);
            }
        }

        @Override
        public void aroundReceive(PartialFunction<Object, BoxedUnit> receive, Object msg) {
            super.aroundReceive(receive, msg);
        }
    }

    static class GreeterPrinter extends UntypedActor {

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof Greeting)
                System.out.println(((Greeting) message).message);
        }
    }

    public static void main(String[] args) throws Exception {
        ActorSystem system = ActorSystem.create("hello-akka-system");
        final ActorRef greeter = system.actorOf(Props.create(GreeterActor.class).withMailbox("bounded-mailbox"), "greeter");

        final Inbox inbox = Inbox.create(system);

        greeter.tell(new WhoToGreet("David"), ActorRef.noSender());

        // 如果不需要actor返回,直接tell就好; inbox的行为类似一个没有receive方法的actor； 这里使用actor也可以完成
        inbox.send(greeter, new Greet());
//        greeter.tell(new Greet(), ActorRef.noSender());

        Greeting greeting = (Greeting) inbox.receive(Duration.create(5, TimeUnit.SECONDS));
        System.out.println("Greeting:" + greeting.message);

        greeter.tell(new WhoToGreet("typesafe"), ActorRef.noSender());
        inbox.send(greeter, new Greet());

        Greeting greeting2 = (Greeting) inbox.receive(Duration.create(5, TimeUnit.SECONDS));
        System.out.println("Greeting:" + greeting2.message);

        ActorRef greetPrinter = system.actorOf(Props.create(GreeterPrinter.class), "greeter-printer");
        system.scheduler().schedule(Duration.Zero(), Duration.create(1, TimeUnit.SECONDS), greeter, new Greet(), system.dispatcher(), greetPrinter);

        Thread.sleep(300 * 2000);
        system.shutdown();
//        greeter.tell(new WhoToGreet("Jack"), ActorRef.noSender());
//        greeter.tell(new Greet(), ActorRef.noSender());
    }
}
