package com.semantics.rdf.tripleStore;

import com.semantics.rdf.bmatrix.BMatrix;
import com.semantics.rdf.bmatrix.BMatrixBuilder;
import com.semantics.rdf.dictionary.TripleDecoder;
import com.semantics.rdf.dictionary.TripleDictionary;
import com.semantics.rdf.model.Triple;
import com.semantics.rdf.provider.TripleProvider;
import com.semantics.rdf.query.QueryFactory;
import com.semantics.rdf.query.QueryProcessor;

import java.util.ArrayList;
import java.util.List;

public class TripleStore {

    // Tree Subdivision Factor
    private final int K = 2;
    // Predicate Sampling Rate
    private final int D = 10;
    // Merge / Unsorted Threshold
    private final int T = 10;

    private TripleDictionary dict;
    private BMatrix bMatrix;
    private QueryFactory factory;
    private QueryProcessor processor;
    private TripleDecoder decoder;

    public TripleDictionary dict() { return dict; }
    public BMatrix bMatrix() { return bMatrix; }

    public void init(TripleProvider tripleProvider) {
        dict = new TripleDictionary();
        bMatrix = new BMatrixBuilder().build(K, D, T, dict, tripleProvider);

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

