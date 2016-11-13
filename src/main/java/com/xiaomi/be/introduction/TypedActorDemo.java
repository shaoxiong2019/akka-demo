package com.xiaomi.be.introduction;

import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.dispatch.Futures;
import akka.japi.Option;
import scala.Unit;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


/**
 *  概念 ： Activate Object 的实现， 用于分离方法调用和方法的执行
 *  使用场景： 用于桥接actor代码和非actor代码， 例如向外部暴露的接口
 *  但是在rp调用中仍然不建议使用
 *
 *  方法派发语义：
 *      1. Unit 会以 fire-and-forget语义进行派发，与ActorRef.tell完全一致。
 *      2. akka.dispatch.Future[_] 会以 send-request-reply语义进行派发，与 ActorRef.ask完全一致。
 *      3. scala.Option[_]会以send-request-reply语义派发，但是会阻塞等待应答, 如果在超时时限内没有应答则返回scala.None，
 *          否则返回包含结果的scala.Some[_]。在这个调用中发生的异常将被重新抛出。
 *      4. 任何其它类型的值将以send-request-reply语义进行派发，但会阻塞地等待应答, 如果超时会抛出java.util.concurrent.TimeoutException，如果发生异常则将异常重新抛出。
 */
public class TypedActorDemo {

    public interface Squarer {
        Future<Integer> square(int i) throws ExecutionException, InterruptedException;

        Option<Integer> squareNow(int i);

        Unit squareNoCare(int i);
    }

    public static class SquarerImpl implements Squarer {
        @Override
        public Future<Integer> square(final int i) throws ExecutionException, InterruptedException {
            return Futures.successful(_squarer(i));
        }

        @Override
        public Option<Integer> squareNow(int i) {
            return Option.some(_squarer(i));
        }

        @Override
        public Unit squareNoCare(int i) {
            return null;
        }

        private int _squarer(int i){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("执行方法");
            return i * i;
        }
    }

    public static void main(String[] args) throws Exception {
        ActorSystem system = ActorSystem.create("system");
        Squarer typedActor = TypedActor.get(system).typedActorOf(new TypedProps<>(Squarer.class ,SquarerImpl.class));

        int i = 10;
        System.out.println("同步阻塞方法");
        Option<Integer> result1 = typedActor.squareNow(i);
        System.out.println(result1.get());

        System.out.println("非阻塞方法");
        Future<Integer> result = typedActor.square(i);
        System.out.println("非阻塞方法后");
        System.out.print(Await.result(result, Duration.apply(2, TimeUnit.SECONDS)));

        Thread.sleep(1000 * 2);
        system.shutdown();
    }
}
