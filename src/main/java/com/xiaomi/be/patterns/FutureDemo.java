package com.xiaomi.be.patterns;

import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import scala.Function1;
import scala.PartialFunction;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.Future;
import scala.util.Try;

import java.util.concurrent.Callable;


public class FutureDemo {

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("actor-system");

        final ExecutionContextExecutor executor = system.dispatcher();

        Future<String> f = Futures.future(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(10000);
                return "future return";
            }
        }, executor);

        f.andThen(new PartialFunction<Try<String>, Object>() {
            @Override
            public Object apply(Try<String> v1) {
                return null;
            }

            @Override
            public boolean isDefinedAt(Try<String> x) {
                return false;
            }

            @Override
            public <C> PartialFunction<Try<String>, C> andThen(Function1<Object, C> k) {
                return null;
            }
        }, executor);

        f.onComplete(new OnSuccess<Try<String>>() {
            @Override
            public void onSuccess(Try<String> result) throws Throwable {
                System.out.println(result.get());
            }
        }, executor);

        f.onFailure(new OnFailure() {
            @Override
            public void onFailure(Throwable failure) throws Throwable {
                System.out.println("执行出现错误");
                failure.printStackTrace();
            }
        }, executor);


    }


}
