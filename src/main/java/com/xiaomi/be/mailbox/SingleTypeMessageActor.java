package com.xiaomi.be.mailbox;

import akka.actor.UntypedActor;
import akka.dispatch.BoundedMessageQueueSemantics;
import akka.dispatch.RequiresMessageQueue;


/**
 *
 * 通过实现接口 RequiresMessageQueue<T> 指定mailbox类型
 * Reference :
 *      http://doc.akka.io/docs/akka/current/java/mailboxes.html
 *      http://udn.yyuap.com/doc/akka-doc-cn/2.3.6/scala/book/chapter3/05_mailboxes.html
 *
 *  邮箱类型的指定方式 ：
 *      actor 实现接口
 *      给调度器配置类型
 *
 *
 */
public class SingleTypeMessageActor extends UntypedActor implements RequiresMessageQueue<BoundedMessageQueueSemantics>{


    @Override
    public void onReceive(Object message) throws Exception {

    }
}
