package com.example.demo.bMatrix;

public record Triple(Object s, Object p, Object o) {
    @Override
    public String toString() {
        return "(" + s + ", " + p + ", " + o + ")";
    }
}
