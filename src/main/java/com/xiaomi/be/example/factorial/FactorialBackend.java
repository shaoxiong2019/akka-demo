package com.xiaomi.be.example.factorial;

import akka.actor.UntypedActor;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.pattern.Patterns;
import scala.concurrent.Future;

import java.math.BigInteger;
import java.util.concurrent.Callable;

public class FactorialBackend extends UntypedActor {

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Integer) {
            final Integer n = (Integer) message;
            Future<BigInteger> f = Futures.future(new Callable<BigInteger>() {
                @Override
                public BigInteger call() throws Exception {
                    return factorial(n);
                }
            }, getContext().dispatcher());

            Future<FactorialResult> result = f.map(new Mapper<BigInteger, FactorialResult>() {
                @Override
                public FactorialResult apply(BigInteger parameter) {
                    return new FactorialResult(n, parameter);
                }
            }, getContext().dispatcher());
            Patterns.pipe(result, getContext().dispatcher()).to(getSender());
        }
    }

    BigInteger factorial(int n) {
        BigInteger acc = BigInteger.ONE;
        for (int i = 1; i <= n; ++i) {
            acc = acc.multiply(BigInteger.valueOf(i));
        }
        return acc;
    }
}
