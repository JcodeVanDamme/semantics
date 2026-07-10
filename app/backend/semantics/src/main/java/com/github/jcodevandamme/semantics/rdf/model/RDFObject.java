package com.github.jcodevandamme.semantics.rdf.model;

public record RDFObject(Object value, boolean isLiteral) {

    public static RDFObject uri(Object value) {
        return new RDFObject(value, false);
    }

    public static RDFObject literal(Object value) {
        return new RDFObject(value, true);
    }
}
