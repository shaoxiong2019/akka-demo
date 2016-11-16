package com.xiaomi.be.route;

import akka.actor.*;
import akka.remote.routing.RemoteRouterConfig;
import akka.routing.FromConfig;
import akka.routing.Pool;
import akka.routing.RandomPool;
import akka.routing.RoundRobinPool;
import scala.concurrent.duration.Duration;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class PoolRouter extends UntypedActor {
    // 从配置文件读取
    ActorRef router1 = getContext().actorOf(FromConfig.getInstance().props(Props.create(RouteSpace.Worker.class)), "router1");
    // 代码创建
    ActorRef router2 = getContext().actorOf(new RoundRobinPool(5).props(Props.create(RouteSpace.Worker.class)), "router2");
    // 随机访问actor池
    ActorRef router3 = getContext().actorOf(new RandomPool(5).props(Props.create(RouteSpace.Worker.class)), "router3");

    ActorRef deployRoutee() {
        Address[] addresses = {new Address("akka.tcp", "linux", "localhost", 10101),
                AddressFromURIString.parse("akka.tcp://linux@localhost:10101")};

        SupervisorStrategy strategy = new OneForOneStrategy(5, Duration.create(1, TimeUnit.MINUTES),
                Collections.<Class<? extends Throwable>>singletonList(Exception.class));

        Pool pool = new RoundRobinPool(5).withSupervisorStrategy(strategy);

        return getContext().actorOf(new RemoteRouterConfig(pool, addresses)
                        .props(Props.create(RouteSpace.Worker.class)));
    }

    void supervision() {

    }

    @Override
    public void onReceive(Object message) throws Exception {

    }
}
