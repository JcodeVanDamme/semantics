package com.github.jcodevandamme.semantics.rdf.provider;

import com.github.jcodevandamme.semantics.rdf.model.Triple;

import java.util.Collections;
import java.util.List;

public class EmptyTripleProvider implements TripleProvider {

    @Override
    public List<Triple> getTriples() {
        return Collections.emptyList();
    }
}
