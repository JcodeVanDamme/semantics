package com.github.jcodevandamme.semantics.rdf.provider;

import com.github.jcodevandamme.semantics.rdf.serialization.TripleParser;
import com.github.jcodevandamme.semantics.rdf.tripleStore.TripleStore;

public class ParserTripleProvider implements TripleProvider {

    private final String filePath;

    public ParserTripleProvider(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void initTriples(TripleStore tripleStore) {
        TripleParser.loadRdfFile(filePath, tripleStore);
    }
}