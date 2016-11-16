package com.xiaomi.be.cluster;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * http://doc.akka.io/docs/akka/snapshot/java/cluster-usage.html
 */
public class SImpleClusterApp {
    public static void main(String[] args) {
        if (args.length == 0)
            /*启动三个节点*/
            startup(new String[]{"2551", "2552", "0"});
        else
            startup(args);
    }

    public static void startup(String[] ports) {
        for (int i = 0; i < ports.length; i++) {
            String port = ports[i];
            // 重写配置中的远程端口
            Config config = ConfigFactory.parseString(
                    "akka.remote.netty.tcp.port=" + port).withFallback(
                    ConfigFactory.load("./cluster.conf").getConfig("ClusterSystem"));

            // 创建ActorSystem,名称需要和conf配置文件中的相同
            ActorSystem system = ActorSystem.create("ClusterSystem" , config);

            // 创建集群中的Actor,并监听事件
            system.actorOf(Props.create(ClusterListener.class),
                    "clusterListener");

        }
    }
}
