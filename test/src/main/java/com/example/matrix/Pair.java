package com.example.matrix;

public class Pair {

    private final int subject;
    private final int object;

    public Pair(int subject, int object) {
        this.subject = subject;
        this.object = object;
    }

    public int getSubject() {
        return subject;
    }

    public int getObject() {
        return object;
    }
}