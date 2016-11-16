package com.xiaomi.be.route;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.FromConfig;

public class GroupRouter extends UntypedActor {
    Props props = Props.create(RouteSpace.Worker.class);
    // 配置文件创建
    ActorRef router1 = getContext().actorOf(FromConfig.getInstance().props(props), "rr-group");

    @Override
    public void preStart() throws Exception {
        for (int i = 0; i < 3; i++) {
            getContext().actorOf(props, "worker_" + i);
        }
    }

    @Override
    public void onReceive(Object message) throws Exception {

    }
}
