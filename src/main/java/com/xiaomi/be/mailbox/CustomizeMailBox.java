package com.xiaomi.be.mailbox;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.dispatch.*;
import scala.Option;

public class CustomizeMailBox implements MailboxType,ProducesMessageQueue<CustomizeMailBox.MyMessageQueue> {

    @Override
    public MessageQueue create(Option<ActorRef> owner, Option<ActorSystem> system) {
        return null;
    }

    static class MyMessageQueue implements  MessageQueue,MyUnboundedJMessageQueueSemantics {
        @Override
        public void enqueue(ActorRef receiver, Envelope handle) {

        }
        @Override
        public Envelope dequeue() {
            return null;
        }
        @Override
        public int numberOfMessages() {
            return 0;
        }
        @Override
        public boolean hasMessages() {
            return false;
        }
        @Override
        public void cleanUp(ActorRef owner, MessageQueue deadLetters) {

        }
    }
}

interface MyUnboundedJMessageQueueSemantics{

}
