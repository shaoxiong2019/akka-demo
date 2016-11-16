package com.xiaomi.be.example.wordcount;

public class Result {
    private String word;
    private long count;

    public Result(String word, long count) {
        this.word = word;
        this.count = count;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
