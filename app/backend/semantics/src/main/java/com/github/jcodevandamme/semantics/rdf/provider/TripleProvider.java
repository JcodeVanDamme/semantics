package com.github.jcodevandamme.semantics.rdf.provider;

import com.github.jcodevandamme.semantics.rdf.model.Triple;

import java.util.List;

public interface TripleProvider {
    List<Triple> getTriples();
}
