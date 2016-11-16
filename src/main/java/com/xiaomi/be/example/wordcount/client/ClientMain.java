package com.xiaomi.be.example.wordcount.client;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.ConfigFactory;

public class ClientMain {

    public static void main(String[] args) {
        //文件名
        final String fileName = "Othello.txt";

        /*根据配置,找到System*/
        ActorSystem system = ActorSystem.create("ClientApplication", ConfigFactory.load("example-work-count").getConfig("WCMapReduceClientApp"));

        /*实例化远程Actor*/
        final ActorRef remoteActor = system.actorFor("akka.tcp://WCMapReduceApp@127.0.0.1:2552/user/WCMapReduceActor");

        /*实例化Actor的管道*/
        final ActorRef fileReadActor = system.actorOf(Props.create(FileReaderActor.class));

        /*实例化Client的Actor管道*/
        final ActorRef clientActor = system.actorOf(Props.create(ClientActor.class,remoteActor));

        /*发送文件名给fileReadActor.设置sender或者说回调的Actor为clientActor*/
        fileReadActor.tell(fileName,clientActor);
    }
}
