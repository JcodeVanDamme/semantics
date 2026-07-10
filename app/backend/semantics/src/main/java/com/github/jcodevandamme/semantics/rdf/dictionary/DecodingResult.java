package com.github.jcodevandamme.semantics.rdf.dictionary;

import com.github.jcodevandamme.semantics.rdf.model.Triple;

public record DecodingResult(Triple t, boolean isLiteral) {
}
