package com.github.jcodevandamme.semantics.rdf.provider;

import com.github.jcodevandamme.semantics.rdf.tripleStore.TripleStore;

public interface TripleProvider {
    void initTriples(TripleStore tripleStore);
}
