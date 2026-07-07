package com.github.jcodevandamme.semantics.rdf.dictionary;

import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.rdf.provider.TripleProvider;

import java.util.*;

public class TripleEncoder {
    public static List<Triple> encode(List<Triple> triples, TripleDictionary dict) {
        registerTriples(triples, dict);
        return encodeTriples(triples, dict);
    }

    private static void registerTriples(List<Triple> triples, TripleDictionary dict) {
        for (Triple t : triples) {
            dict.registerSO((String) t.s());
            dict.registerP((String) t.p());
            dict.registerSO((String) t.o());
        }
    }

    private static List<Triple> encodeTriples(List<Triple> triples, TripleDictionary dict) {
        List<Triple> encodedTriples = new ArrayList<>();
        for (Triple t : triples) {
            encodedTriples.add(
                    new Triple(
                            dict.encodeSO((String) t.s()),
                            dict.encodeP((String) t.p()),
                            dict.encodeSO((String) t.o())
                    )
            );
        }
        return encodedTriples;
    }
}
