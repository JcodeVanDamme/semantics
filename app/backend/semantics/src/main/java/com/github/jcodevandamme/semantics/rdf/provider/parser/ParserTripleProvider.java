package com.github.jcodevandamme.semantics.rdf.provider.parser;

import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.rdf.provider.TripleProvider;
import com.github.jcodevandamme.semantics.rdf.tripleStore.TripleStore;

import java.util.ArrayList;
import java.util.List;

public class ParserTripleProvider implements TripleProvider {

    private final String filePath;

    public ParserTripleProvider(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<Triple> getTriples() {
        return TripleParser.loadRdfFile(filePath);
    }
}