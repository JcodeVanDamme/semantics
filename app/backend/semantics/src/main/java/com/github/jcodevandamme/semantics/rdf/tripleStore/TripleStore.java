package com.github.jcodevandamme.semantics.rdf.tripleStore;

import com.github.jcodevandamme.semantics.rdf.bmatrix.BMatrix;
import com.github.jcodevandamme.semantics.rdf.bmatrix.TripleAlreadyExistsException;
import com.github.jcodevandamme.semantics.rdf.bmatrix.TripleNotFoundException;
import com.github.jcodevandamme.semantics.rdf.dictionary.TripleCodingException;
import com.github.jcodevandamme.semantics.rdf.dictionary.TripleDecoder;
import com.github.jcodevandamme.semantics.rdf.dictionary.TripleDictionary;
import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.rdf.query.Query;
import com.github.jcodevandamme.semantics.rdf.query.QueryFactory;
import com.github.jcodevandamme.semantics.rdf.query.QueryProcessor;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.DynamicBitVectorConfiguration;

import java.util.Collections;
import java.util.List;

public class TripleStore {

    private static final int DEFAULT_K = 2;
    private static final int DEFAULT_T = 10;
    private static final int DEFAULT_BITVECTOR_CHUNKSIZE = 4;
    private static final int DEFAULT_BITVECTOR_LEAF_MAX = 1;
    private static final int DEFAULT_BITVECTOR_INT_MIN = 1;
    private static final int DEFAULT_BITVECTOR_INT_MAX = 4;

    private final TripleDictionary dict;
    private final BMatrix bMatrix;
    private final QueryFactory factory;
    private final QueryProcessor processor;

    public TripleStore() {
        dict = new TripleDictionary();
        bMatrix = new BMatrix(
                DEFAULT_K,
                DEFAULT_T,
                new DynamicBitVectorConfiguration(
                        DEFAULT_BITVECTOR_CHUNKSIZE,
                        DEFAULT_BITVECTOR_LEAF_MAX,
                        DEFAULT_BITVECTOR_INT_MIN,
                        DEFAULT_BITVECTOR_INT_MAX
                ));
        factory = new QueryFactory(dict);
        processor = new QueryProcessor(bMatrix);
    }
    public TripleStore(int k, int t, DynamicBitVectorConfiguration config) {
        dict = new TripleDictionary();
        bMatrix = new BMatrix(k, t, config);
        factory = new QueryFactory(dict);
        processor = new QueryProcessor(bMatrix);
    }

    public List<Triple> query(String s, String p, String o) {
        try {
            Query tripleQuery = factory.fromTriple(s, p, o);
            List<Triple> queryResults = processor.process(tripleQuery);
            return TripleDecoder.decode(queryResults, dict);

        } catch (TripleCodingException ex) {
            return Collections.emptyList();
        }
    }

    public List<Triple> query(String query) {
        try {
            Query sparqlQuery = factory.fromSparql(query);
            List<Triple> queryResults = processor.process(sparqlQuery);
            return TripleDecoder.decode(queryResults, dict);

        } catch (TripleCodingException ex) {
            return Collections.emptyList();
        }
    }

    public boolean create(Triple t) throws TripleAlreadyExistsException {
        try {
            register(t);
            Triple encoded = encode(t);
            bMatrix.add(
                    (int) encoded.s(),
                    (int) encoded.p(),
                    (int) encoded.o()
            );
            return true;

        } catch (Exception ex) {
            unregister(t);
            throw ex;
        }
    }

    public Boolean delete(Triple t) throws TripleNotFoundException {
        Triple encoded = encode(t);
        bMatrix.delete(
                (int) encoded.s(),
                (int) encoded.p(),
                (int) encoded.o()
        );
        unregister(t);
        return true;
    }

    public Boolean update(Triple oldT, Triple newT) throws  TripleNotFoundException, TripleAlreadyExistsException {
        try {
            Triple encodedOld = encode(oldT);
            register(newT);
            Triple encodedNew = encode(newT);
            bMatrix.update(
                    (int) encodedOld.s(),
                    (int) encodedOld.p(),
                    (int) encodedOld.o(),
                    (int) encodedNew.s(),
                    (int) encodedNew.p(),
                    (int) encodedNew.o()
            );
            unregister(oldT);
            return true;

        } catch (Exception ex) {
            unregister(newT);
            throw ex;
        }
    }

    private Triple encode(Triple t) throws TripleCodingException {
        return new Triple(
                dict.encodeSO((String) t.s()),
                dict.encodeP((String) t.p()),
                dict.encodeSO((String) t.o())
        );
    }

    private void register(Triple t) {
        dict.registerSO((String) t.s(), false);
        dict.registerP((String) t.p());
        dict.registerSO((String) t.o(), checkObjectType((String) t.o()));
    }

    private void unregister(Triple t) {
        dict.unregisterSO((String) t.s());
        dict.unregisterP((String) t.p());
        dict.unregisterSO((String) t.o());
    }

    private boolean checkObjectType(String o) {
        return true;
    }

    @Override
    public String toString() {
        return bMatrix.toString() + dict.toString() +
                "-----------------------------------------------------------";
    }
}

