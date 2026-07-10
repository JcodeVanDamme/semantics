package com.github.jcodevandamme.semantics.rdf.model;

public record Triple(RDFObject s, RDFObject p, RDFObject o) {

    public Triple(String s, String p, String o, boolean isLiteral) {
        this(
                RDFObject.uri(s),
                RDFObject.uri(p),
                isLiteral ? RDFObject.literal(o) : RDFObject.uri(o)
        );
    }
    public Triple(String s, String p, String o) {
        this(
                RDFObject.uri(s),
                RDFObject.uri(p),
                RDFObject.uri(o)
        );
    }

    @Override
    public String toString() {
        return "(" + s.value() + ", " + p.value() + ", " + o.value() + " | oLiteral: " + o.isLiteral() + ")";
    }
}