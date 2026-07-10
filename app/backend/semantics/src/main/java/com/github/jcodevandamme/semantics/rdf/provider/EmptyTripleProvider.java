package com.github.jcodevandamme.semantics.rdf.provider;

import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.rdf.tripleStore.TripleStore;

import java.util.Collections;
import java.util.List;

public class EmptyTripleProvider implements TripleProvider {

    @Override
    public void initTriples(TripleStore tripleStore) {
    }
}
