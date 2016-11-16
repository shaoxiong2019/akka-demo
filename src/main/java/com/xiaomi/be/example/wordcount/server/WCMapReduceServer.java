package com.xiaomi.be.example.wordcount.server;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RoundRobinPool;
import com.typesafe.config.ConfigFactory;

public class WCMapReduceServer {
    private ActorRef mapRouter;
    private ActorRef reduceRouter;
    private ActorRef aggregateActor;
    private ActorRef wcMapReduceActor;

    public WCMapReduceServer(int no_of_reduce_workers, int no_of_map_workers) {
        /*创建了Actor系统*/
        ActorSystem system = ActorSystem.create("WCMapReduceApp", ConfigFactory.load("application")
                .getConfig("WCMapReduceApp"));

        // 创建聚合Actor
        aggregateActor = system.actorOf(Props.create(AggregateActor.class));

        // 创建多个聚合的Actor
        reduceRouter = system.actorOf(Props.create(ReduceActor.class,aggregateActor).withRouter(new RoundRobinPool(no_of_reduce_workers)));

        // 创建多个Map的Actor
        mapRouter = system.actorOf(Props.create(MapActor.class,reduceRouter).withRouter(new RoundRobinPool(no_of_map_workers)));

        // create the overall WCMapReduce Actor that acts as the remote actor
        // for clients
        Props props = Props.create(WCMapReduceActor.class,aggregateActor,mapRouter).withDispatcher("priorityMailBox-dispatcher");
        wcMapReduceActor = system.actorOf(props, "WCMapReduceActor");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new WCMapReduceServer(50, 50);
    }
}
