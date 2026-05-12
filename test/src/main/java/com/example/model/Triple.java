package com.example.model;

public class Triple {

    private final int subject;
    private final int predicate;
    private final int object;

    public Triple(int subject, int predicate, int object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public int getSubject() {
        return subject;
    }

    public int getPredicate() {
        return predicate;
    }

    public int getObject() {
        return object;
    }

    @Override
    public String toString() {
        return "(" + subject + ", " + predicate + ", " + object + ")";
    }
}