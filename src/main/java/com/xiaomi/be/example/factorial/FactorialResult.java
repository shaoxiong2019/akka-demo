package com.xiaomi.be.example.factorial;

import java.math.BigInteger;

public class FactorialResult {
    public final int n;
    public final BigInteger factorial;

    public FactorialResult(int n, BigInteger factorial) {
        this.n = n;
        this.factorial = factorial;
    }

    @Override
    public String toString() {
        return "FactorialResult{" +
                "n=" + n +
                ", factorial=" + factorial +
                '}';
    }
}
