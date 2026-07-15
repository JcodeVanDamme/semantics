package com.github.jcodevandamme.semantics.rdf.tripleStore;

import com.github.jcodevandamme.semantics.rdf.bmatrix.BMatrix;
import com.github.jcodevandamme.semantics.rdf.bmatrix.TripleAlreadyExistsException;
import com.github.jcodevandamme.semantics.rdf.bmatrix.TripleNotFoundException;
import com.github.jcodevandamme.semantics.rdf.dictionary.TripleCodingException;
import com.github.jcodevandamme.semantics.rdf.dictionary.TripleDecoder;
import com.github.jcodevandamme.semantics.rdf.dictionary.TripleDictionary;
import com.github.jcodevandamme.semantics.rdf.model.EncodedTriple;
import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.rdf.query.Query;
import com.github.jcodevandamme.semantics.rdf.query.QueryFactory;
import com.github.jcodevandamme.semantics.rdf.query.TripleQuery;
import com.github.jcodevandamme.semantics.rdf.query.TripleQueryProcessor;
import com.github.jcodevandamme.semantics.rdf.query.sparql.*;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector.DynamicBitVectorConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TripleStore {

    private static final int DEFAULT_K = 12;
    private static final int DEFAULT_T = 10;
    private static final int DEFAULT_BITVECTOR_CHUNKSIZE = 144;
    private static final int DEFAULT_BITVECTOR_LEAF_MAX = 20;
    private static final int DEFAULT_BITVECTOR_INT_MIN = 1;
    private static final int DEFAULT_BITVECTOR_INT_MAX = 2000;

    private final TripleDictionary dict;
    private final BMatrix bMatrix;
    private final TripleQueryProcessor tripleProcessor;
    //private final SparqlProcessor sparqlProcessor;

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
        tripleProcessor = new TripleQueryProcessor(bMatrix);
        //sparqlProcessor = new SparqlProcessor(tripleProcessor);
    }
    public TripleStore(int k, int t, DynamicBitVectorConfiguration config) {
        dict = new TripleDictionary();
        bMatrix = new BMatrix(k, t, config);
        tripleProcessor = new TripleQueryProcessor(bMatrix);
        //sparqlProcessor = new SparqlProcessor(tripleProcessor);
    }

    public List<Triple> query(String s, String p, String o) {
        try {
            TripleQuery query = QueryFactory.fromTriple(s, p, o, dict);
            List<EncodedTriple> queryResults = tripleProcessor.process(query);

            List<EncodedTriple> filteredResults = queryResults.stream()
                    .filter(Objects::nonNull)
                    .toList();

            return TripleDecoder.decode(filteredResults, dict);

        } catch (TripleCodingException ex) {
            return Collections.emptyList();
        }
    }

    // Sparql-Slop
    //
    /*public Object query(String queryString) {
        try {
            SparqlQuery sparqlQuery = SparqlParser.parseSparql(queryString, this.dict);
            SparqlResult result = sparqlProcessor.execute(sparqlQuery);

            if (result.isResultSet()) {
                // HIER findet die Dekodierung für SELECT statt!
                return TripleDecoder.decodeSelectResults(result.getResultSet(), this.dict);
            } else {
                // HIER findet die Dekodierung für CONSTRUCT/DESCRIBE statt (dein bisheriger Weg)
                return TripleDecoder.decode(result.getTripleResult(), this.dict);
            }

        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }*/

    public boolean create(Triple t) {
        register(t);
        EncodedTriple encoded = encode(t);
        boolean added =  bMatrix.add(
                (int) encoded.s(),
                (int) encoded.p(),
                (int) encoded.o()
        );
        if (added) {
            return true;
        } else {
            unregister(t);
            return false;
        }
    }

    public Boolean delete(Triple t) {
        EncodedTriple encoded = encode(t);
        boolean deleted = bMatrix.delete(
                (int) encoded.s(),
                (int) encoded.p(),
                (int) encoded.o()
        );
        if (deleted) {
            unregister(t);
            return true;
        } else {
            return false;
        }
    }

    public Boolean update(Triple oldT, Triple newT) throws  TripleNotFoundException, TripleAlreadyExistsException {
        try {
            EncodedTriple encodedOld = encode(oldT);
            register(newT);
            EncodedTriple encodedNew = encode(newT);
            boolean updated = bMatrix.update(
                    (int) encodedOld.s(),
                    (int) encodedOld.p(),
                    (int) encodedOld.o(),
                    (int) encodedNew.s(),
                    (int) encodedNew.p(),
                    (int) encodedNew.o()
            );
            if (updated) {
                unregister(oldT);
                return true;
            } else {
                unregister(newT);
                return false;
            }
        } catch (Exception ex) {
            unregister(newT);
            throw ex;
        }
    }

    private EncodedTriple encode(Triple t) throws TripleCodingException {
        return new EncodedTriple(
                dict.encodeSO((String) t.s().value()),
                dict.encodeP((String) t.p().value()),
                dict.encodeSO((String) t.o().value())
        );
    }

    private void register(Triple t) {
        dict.registerSO((String) t.s().value(), false);
        dict.registerP((String) t.p().value());
        dict.registerSO((String) t.o().value(), t.o().isLiteral());
    }

    private void unregister(Triple t) {
        dict.unregisterSO((String) t.s().value());
        dict.unregisterP((String) t.p().value());
        dict.unregisterSO((String) t.o().value());
    }

    @Override
    public String toString() {
        return bMatrix.toString() + dict.toString() +
                "-----------------------------------------------------------";
    }
}

