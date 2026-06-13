package com.github.jcodevandamme.semantics.rdf.model;

public record Triple(Object s, Object p, Object o) {
    @Override
    public String toString() {
        return "(" + s + ", " + p + ", " + o + ")";
    }
}
