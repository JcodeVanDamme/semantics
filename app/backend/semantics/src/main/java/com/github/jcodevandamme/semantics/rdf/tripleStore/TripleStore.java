package com.github.jcodevandamme.semantics.rdf.tripleStore;

import com.github.jcodevandamme.semantics.rdf.bmatrix.BMatrixBuilder;
import com.github.jcodevandamme.semantics.rdf.bmatrix.BMatrix;
import com.github.jcodevandamme.semantics.rdf.bmatrix.TripleAlreadyExistsException;
import com.github.jcodevandamme.semantics.rdf.bmatrix.TripleNotFoundException;
import com.github.jcodevandamme.semantics.rdf.dictionary.TripleDecoder;
import com.github.jcodevandamme.semantics.rdf.dictionary.TripleDictionary;
import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.rdf.provider.TripleProvider;
import com.github.jcodevandamme.semantics.rdf.query.Query;
import com.github.jcodevandamme.semantics.rdf.query.QueryFactory;
import com.github.jcodevandamme.semantics.rdf.query.QueryProcessor;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.DynamicBitVectorConfiguration;

import java.util.List;

public class TripleStore {

    // Tree Subdivision Factor
    private final int K = 2;

    // Merge / Unsorted Threshold
    private final int T = 10;

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

    public void init(TripleProvider tripleProvider) {
        dict = new TripleDictionary();

        DynamicBitVectorConfiguration config = new DynamicBitVectorConfiguration(
                CHUNK_SIZE,
                LEAF_MIN_CAPACITY,
                INTERNAL_MIN_CAPACITY,
                INTERNAL_MAX_CAPACITY
        );

        bMatrix = new BMatrixBuilder().buildFromStatic(K, T, config, dict, tripleProvider);
        factory = new QueryFactory(dict);
        processor = new QueryProcessor(bMatrix);
        decoder = new TripleDecoder(dict);
    }

    public void initEmpty() {
        dict = new TripleDictionary();

        DynamicBitVectorConfiguration config = new DynamicBitVectorConfiguration(
                CHUNK_SIZE,
                LEAF_MIN_CAPACITY,
                INTERNAL_MIN_CAPACITY,
                INTERNAL_MAX_CAPACITY
        );

        bMatrix = new BMatrixBuilder().buildEmpty(K, T, config);
        factory = new QueryFactory(dict);
        processor = new QueryProcessor(bMatrix);
        decoder = new TripleDecoder(dict);
    }

    public List<Triple> query(String s, String p, String o) {
        Query tripleQuery = factory.fromTriple(s, p, o);
        List<Triple> queryResults = processor.process(tripleQuery);
        return decoder.decode(queryResults);
    }

    public List<Triple> query(String query) {
        Query sparqlQuery = factory.fromSparql(query);
        List<Triple> queryResults = processor.process(sparqlQuery);
        return decoder.decode(queryResults);
    }

    public boolean create(Triple t) throws TripleAlreadyExistsException {
        try {
            dict.registerSO((String) t.s());
            dict.registerP((String) t.p());
            dict.registerSO((String) t.o());

            Triple encoded = new Triple(
                    dict.encodeSO((String) t.s()),
                    dict.encodeP((String) t.p()),
                    dict.encodeSO((String) t.o())
            );

            bMatrix.add(
                    (int) encoded.s(),
                    (int) encoded.p(),
                    (int) encoded.o()
            );

            return true;

        } catch (Exception ex) {
            dict.unregisterSO((String) t.s());
            dict.unregisterP((String) t.p());
            dict.unregisterSO((String) t.o());
            throw ex;
        }
    }

    public Boolean delete(Triple t) throws TripleNotFoundException {
        Triple encoded = new Triple(
                dict.encodeSO((String) t.s()),
                dict.encodeP((String) t.p()),
                dict.encodeSO((String) t.o())
        );

        bMatrix.delete(
                (int) encoded.s(),
                (int) encoded.p(),
                (int) encoded.o()
        );

        dict.unregisterSO((String) t.s());
        dict.unregisterP((String) t.p());
        dict.unregisterSO((String) t.o());

        return true;
    }

    public Boolean update(Triple oldT, Triple newT) {
        try {
            Triple encodedOld = new Triple(
                    dict.encodeSO((String) oldT.s()),
                    dict.encodeP((String) oldT.p()),
                    dict.encodeSO((String) oldT.o())
            );

            dict.registerSO((String) newT.s());
            dict.registerP((String) newT.p());
            dict.registerSO((String) newT.o());

            Triple encodedNew = new Triple(
                    dict.encodeSO((String) newT.s()),
                    dict.encodeP((String) newT.p()),
                    dict.encodeSO((String) newT.o())
            );

            bMatrix.update(
                    (int) encodedOld.s(),
                    (int) encodedOld.p(),
                    (int) encodedOld.o(),
                    (int) encodedNew.s(),
                    (int) encodedNew.p(),
                    (int) encodedNew.o()
            );

            dict.unregisterSO((String) oldT.s());
            dict.unregisterP((String) oldT.p());
            dict.unregisterSO((String) oldT.o());

            return true;

        } catch (Exception ex) {
            dict.unregisterSO((String) newT.s());
            dict.unregisterP((String) newT.p());
            dict.unregisterSO((String) newT.o());
            throw ex;
        }
    }

    @Override
    public String toString() {
        return bMatrix.toString() + dict.toString() +
                "-----------------------------------------------------------";
    }
}

