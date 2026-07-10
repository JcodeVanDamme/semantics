package com.github.jcodevandamme.semantics.rdf.bmatrix;

import com.github.jcodevandamme.semantics.rdf.model.Triple;

public record QueryResult(Triple t, boolean isLiteral) {
}
