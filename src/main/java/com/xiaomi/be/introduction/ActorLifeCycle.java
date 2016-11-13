package com.xiaomi.be.introduction;

import akka.actor.*;
import com.xiaomi.be.introduction.HelloAkka;

public class ActorLifeCycle {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("akka-system");

        final ActorRef greeter = system.actorOf(Props.create(HelloAkka.GreeterActor.class), "greeter");

        ActorPath path = greeter.path();
        System.out.println("greeter path:" + path);

//        system.stop(greeter);
//
//        final ActorRef greeter2 = system.actorOf(Props.create(HelloAkka.GreeterActor.class), "greeter");
//
//        System.out.println("greeter2 path:" + greeter2.path());

        ActorRef ref = null;

//        ActorSelectionDemo.apply(ref, path.toString());

        System.out.println(ref == greeter);
    }
}
