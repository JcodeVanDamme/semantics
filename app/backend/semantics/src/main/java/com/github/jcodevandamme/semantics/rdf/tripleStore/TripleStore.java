package com.github.jcodevandamme.semantics.rdf.tripleStore;

import com.github.jcodevandamme.semantics.rdf.dictionary.TripleDecoder;
import com.github.jcodevandamme.semantics.rdf.dictionary.TripleDictionary;
import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.rdf.bmatrix.BMatrix;
import com.github.jcodevandamme.semantics.rdf.bmatrix.BMatrixBuilder;
import com.github.jcodevandamme.semantics.rdf.provider.TripleProvider;
import com.github.jcodevandamme.semantics.rdf.query.QueryFactory;
import com.github.jcodevandamme.semantics.rdf.query.QueryProcessor;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.DK2Configuration;

import java.util.List;

public class TripleStore {

    // Tree Subdivision Factor
    private final int K = 2;
    // Predicate Sampling Rate
    private final int D = 10;
    // Merge / Unsorted Threshold
    private final int T = 10;

    // DYNAMIC

    private final int CHUNK_SIZE = 4;
    private final int LEAF_MIN_CAPACITY = 1;
    private final int INTERNAL_MIN_CAPACITY = 1;
    private final int INTERNAL_MAX_CAPACITY = 3;

    private TripleDictionary dict;
    private BMatrix bMatrix;
    private QueryFactory factory;
    private QueryProcessor processor;
    private TripleDecoder decoder;

    public TripleDictionary dict() { return dict; }
    public BMatrix bMatrix() { return bMatrix; }

    public void initStatic(TripleProvider tripleProvider) {
        dict = new TripleDictionary();
        bMatrix = new BMatrixBuilder().buildStatic(K, D, T, dict, tripleProvider);

        factory = new QueryFactory(dict);
        processor = new QueryProcessor(bMatrix);
        decoder = new TripleDecoder(dict);
    }
    public void initDynamic(TripleProvider tripleProvider) {
        dict = new TripleDictionary();

        DK2Configuration config = new DK2Configuration(
                CHUNK_SIZE,
                LEAF_MIN_CAPACITY,
                INTERNAL_MIN_CAPACITY,
                INTERNAL_MAX_CAPACITY
        );

        bMatrix = new BMatrixBuilder().buildDynamic(K, D, T, config, dict, tripleProvider);

        factory = new QueryFactory(dict);
        processor = new QueryProcessor(bMatrix);
        decoder = new TripleDecoder(dict);
    }

    public List<Triple> query(String s, String p, String o) {
        return decoder.decode(processor.process(factory.fromTriple(s, p, o)));
    }
    public List<Triple> query(String query) {
        return decoder.decode(processor.process(factory.fromSparql(query)));
    }
    public Boolean create(Triple t) {
        return true;
    }
    public Boolean update(Triple t) {
        return true;
    }
    public Boolean delete(Triple t) {
        return true;
    }

    @Override
    public String toString() {
        return bMatrix.toString() + dict.toString() +
                "-----------------------------------------------------------";
    }
}

